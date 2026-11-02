package xyz.refinedev.phoenix.editor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.lib.okhttp3.OkHttpClient;
import xyz.refinedev.lib.okhttp3.OkHttpClient.Builder;
import xyz.refinedev.lib.okhttp3.Request;
import xyz.refinedev.lib.okhttp3.Response;
import xyz.refinedev.lib.okhttp3.WebSocket;
import xyz.refinedev.lib.okhttp3.WebSocketListener;
import xyz.refinedev.phoenix.editor.op.EditorOperationRegistry;
import xyz.refinedev.phoenix.editor.protocol.EditorFrame;
import xyz.refinedev.phoenix.editor.protocol.EditorFrameCodec;
import xyz.refinedev.phoenix.editor.protocol.EditorFrameKind;
import xyz.refinedev.phoenix.editor.protocol.EditorOp;
import xyz.refinedev.phoenix.editor.protocol.EditorProtocolException;
import xyz.refinedev.phoenix.editor.protocol.PendingEditorRequests;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditorSocketClient {

    private final OkHttpClient httpClient;

    private final String text;

    private final String textString;

    private final EditorOperationRegistry editorOperationRegistry;

    private final Logger logger;

    private final Executor executor;

    private final PendingEditorRequests pendingEditorRequests =
            new PendingEditorRequests(10000L, System::currentTimeMillis);

    private volatile WebSocket webSocket;

    private volatile EditorSession editorSession;

    public EditorSocketClient(
            String text,
            String textString,
            EditorOperationRegistry editorOperationRegistry2,
            Logger logger2,
            Executor executor2) {
        this.text = text;
        this.textString = textString;
        this.editorOperationRegistry = editorOperationRegistry2;
        this.logger = logger2;
        this.executor = executor2;
        this.httpClient =
                new Builder()
                        .pingInterval(20L, TimeUnit.SECONDS)
                        .readTimeout(0L, TimeUnit.MILLISECONDS)
                        .build();
    }

    public void setEditorSession(EditorSession editorSession2) {
        this.editorSession = editorSession2;
    }

    public boolean isEnabled() {
        return this.webSocket != null;
    }

    public void applyEditorSocketListener(final EditorSocketListener editorSocketListener) {
        Request request =
                new Request.Builder()
                        .url(this.text)
                        .header("Authorization", this.textString)
                        .build();
        this.httpClient.newWebSocket(
                request,
                new WebSocketListener() {

                    private final AtomicBoolean enabled = new AtomicBoolean();

                    private static final String text = "editor socket closed: ";

                    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response result2) {
                        EditorSocketClient.this.webSocket = webSocket;
                        editorSocketListener.onOpen();
                    }

                    public void onMessage(@NotNull WebSocket result, @NotNull String result2) {
                        EditorSocketClient.this.applyWebSocket(result, result2);
                    }

                    public void onClosing(
                            @NotNull WebSocket result, int result2, @NotNull String result3) {
                        result.close(1000, null);
                        this.applyString(result2 + " " + result3);
                    }

                    public void onClosed(
                            @NotNull WebSocket result, int result2, @NotNull String result3) {
                        this.applyString(result2 + " " + result3);
                    }

                    public void onFailure(
                            @NotNull WebSocket result,
                            @NotNull Throwable result2,
                            @Nullable Response result3) {
                        if (this.enabled.compareAndSet(false, true)) {
                            EditorSocketClient.this.webSocket = null;
                            EditorSocketClient.this.pendingEditorRequests.applyThrowable(result2);
                            editorSocketListener.onFailure(
                                    result2, result3 != null ? result3.code() : null);
                        }
                    }

                    private void applyString(String result) {
                        if (this.enabled.compareAndSet(false, true)) {
                            EditorSocketClient.this.webSocket = null;
                            EditorSocketClient.this.pendingEditorRequests.applyThrowable(
                                    new IllegalStateException(text + result));
                            editorSocketListener.onClosed(result);
                        }
                    }
                });
    }

    public CompletableFuture<JsonElement> create(EditorOp editorOp, JsonObject jsonObject) {
        WebSocket webSocket2 = this.webSocket;
        if (this.webSocket == null) {
            CompletableFuture<JsonElement> completableFuture;
            (completableFuture = new CompletableFuture())
                    .completeExceptionally(
                            new IllegalStateException("editor socket is not connected"));
            return completableFuture;
        } else {
            CompletableFuture<JsonElement> completableFuture2 = new CompletableFuture<>();
            String text = this.pendingEditorRequests.formatString(completableFuture2);
            webSocket2.send(
                    EditorFrameCodec.formatString(EditorFrame.of(text, editorOp, jsonObject)));
            return completableFuture2;
        }
    }

    public int getCount() {
        return this.pendingEditorRequests.getCount();
    }

    public void initialize() {
        this.pendingEditorRequests.initialize();
    }

    public void setString(String text) {
        WebSocket webSocket2 = this.webSocket;
        this.webSocket = null;
        this.pendingEditorRequests.applyThrowable(
                new IllegalStateException("editor socket closed: " + text));
        if (webSocket2 != null) {
            webSocket2.close(1000, text);
        }
    }

    private void applyWebSocket(WebSocket webSocket, String text) {
        EditorFrame editorFrame;
        try {
            editorFrame = EditorFrameCodec.create(text);
        } catch (EditorProtocolException editorProtocolException) {
            this.logger.log(
                    Level.WARNING, "Discarded a malformed editor frame", editorProtocolException);
            return;
        }
        if (editorFrame.getEditorFrameKind() == EditorFrameKind.RESPONSE
                || editorFrame.getEditorFrameKind() == EditorFrameKind.ERROR) {
            this.pendingEditorRequests.matchesEnabled(editorFrame);
        } else if (editorFrame.getEditorFrameKind() == EditorFrameKind.REQUEST) {
            EditorSession editorSession2 = this.editorSession;
            if (this.editorSession == null) {
                webSocket.send(
                        EditorFrameCodec.formatString(
                                EditorFrame.of(
                                        editorFrame.getText(),
                                        "NO_SESSION",
                                        "no editor session is open on this server")));
            } else {
                editorSession2.initialize();
                EditorFrame editorFrame2 = editorFrame;
                this.executor.execute(
                        () ->
                                webSocket.send(
                                        EditorFrameCodec.formatString(
                                                this.editorOperationRegistry.findEditorFrame(
                                                        editorSession2, editorFrame2))));
            }
        }
    }
}
