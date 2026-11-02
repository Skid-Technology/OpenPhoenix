package xyz.refinedev.phoenix.editor;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.editor.op.AuthorizedEditorOperations;
import xyz.refinedev.phoenix.editor.op.EditorOperationRegistry;
import xyz.refinedev.phoenix.editor.protocol.EditorOp;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.logging.Level;

public class EditorHandler {

    private static final long time = TimeUnit.MINUTES.toMillis(30L);

    private static final long timeTime = TimeUnit.MINUTES.toMillis(5L);

    private final CommonPlatform platform;

    private final String text;

    private final Supplier<JsonElement> supplier;

    private final EditorOperationRegistry editorOperationRegistry = new EditorOperationRegistry();

    private final AtomicInteger counter = new AtomicInteger();

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(
                    result -> create(result, "Phoenix Editor Scheduler"));

    private final ExecutorService executor =
            Executors.newCachedThreadPool(result -> create(result, "Phoenix Editor Worker"));

    private volatile EditorSession editorSession;

    private volatile EditorSocketClient editorSocketClient;

    private volatile long time3;

    private ScheduledFuture<?> scheduledFuture;

    private volatile boolean enabled;

    public EditorHandler(
            CommonPlatform commonPlatform, String text, Supplier<JsonElement> supplier2) {
        this.platform = commonPlatform;
        this.text = text;
        this.supplier = supplier2;
        AuthorizedEditorOperations.applyEditorOperationRegistry(
                this.editorOperationRegistry, commonPlatform);
        this.scheduler.scheduleAtFixedRate(
                this::initializeByEditorSocketClient, 5L, 5L, TimeUnit.SECONDS);
    }

    public static long calculateTime(int value) {
        if (value <= 0) {
            return 1000L;
        } else {
            return value >= 4 ? 15000L : 1000L << value;
        }
    }

    public CompletableFuture<EditorSession> create(final UUID uUID, final String text) {
        final CompletableFuture<EditorSession> completableFuture = new CompletableFuture<>();
        final EditorSocketClient editorSocketClient2 = this.create();
        EditorSocketClient editorSocketClient3;
        synchronized (this) {
            editorSocketClient3 = this.editorSocketClient;
            this.editorSocketClient = editorSocketClient2;
            this.enabled = true;
            this.time3 = 0L;
            this.initializeByScheduledFuture();
        }
        if (editorSocketClient3 != null) {
            editorSocketClient3.setString("replaced");
        }
        editorSocketClient2.applyEditorSocketListener(
                new EditorSocketListener() {

                    @Override
                    public void onOpen() {
                        if (EditorHandler.this.editorSocketClient == editorSocketClient2) {
                            EditorHandler.this.enabled = false;
                            EditorHandler.this.counter.set(0);
                            EditorHandler.this.time3 = 0L;
                            JsonObject jsonObject;
                            (jsonObject = new JsonObject())
                                    .addProperty("server", GlobalConfig.SERVER.NAME);
                            jsonObject.addProperty("actorUuid", uUID.toString());
                            jsonObject.addProperty("actorName", text);
                            jsonObject.add(
                                    "snapshot",
                                    EditorHandler.this
                                            .platform
                                            .getGson()
                                            .toJsonTree(EditorHandler.this.createByPlatform()));
                            jsonObject.add("permissions", EditorHandler.this.getJsonElement());
                            CompletableFuture<JsonElement> completableFuture2 =
                                    editorSocketClient2.create(EditorOp.SESSION_OPEN, jsonObject);
                            completableFuture2.whenComplete(
                                    (result, result2) -> {
                                        if (result2 != null) {
                                            completableFuture.completeExceptionally(result2);
                                        } else {
                                            JsonObject asJsonObject = result.getAsJsonObject();
                                            EditorSession editorSession2 =
                                                    new EditorSession(
                                                            asJsonObject
                                                                    .get("editorId")
                                                                    .getAsString(),
                                                            asJsonObject
                                                                    .get("apiKey")
                                                                    .getAsString(),
                                                            uUID,
                                                            text);
                                            EditorHandler.this.editorSession = editorSession2;
                                            editorSocketClient2.setEditorSession(editorSession2);
                                            completableFuture.complete(editorSession2);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onClosed(String result3) {
                        if (EditorHandler.this.matchesEnabled(editorSocketClient2)) {
                            EditorHandler.this.initializeByTime3();
                        }
                        if (!completableFuture.isDone()) {
                            completableFuture.completeExceptionally(
                                    new IllegalStateException("editor socket closed: " + result3));
                        }
                    }

                    @Override
                    public void onFailure(Throwable result3) {
                        EditorHandler.this
                                .platform
                                .getLogger()
                                .log(Level.WARNING, "Editor socket failed", result3);
                        if (EditorHandler.this.matchesEnabled(editorSocketClient2)) {
                            EditorHandler.this.initializeByTime3();
                        }
                        if (!completableFuture.isDone()) {
                            completableFuture.completeExceptionally(result3);
                        }
                    }
                });
        return completableFuture;
    }

    public CompletableFuture<EditorSessionStore> thenApply() {
        EditorSession editorSession2 = this.editorSession;
        EditorSocketClient editorSocketClient2 = this.editorSocketClient;
        if (editorSession2 != null
                && editorSocketClient2 != null
                && editorSocketClient2.isEnabled()) {
            CompletableFuture<JsonElement> completableFuture =
                    editorSocketClient2.create(EditorOp.SESSION_PULL, this.create(editorSession2));
            return completableFuture.thenApply(
                    result -> {
                        JsonObject asJsonObject = result.getAsJsonObject();
                        EditorSessionFactory editorSessionFactory =
                                (EditorSessionFactory)
                                        this.platform
                                                .getGson()
                                                .fromJson(
                                                        asJsonObject.get("snapshot"),
                                                        EditorSessionFactory.class);
                        this.setString("pulled");
                        return new EditorSessionStore(
                                editorSessionFactory, this.findList(asJsonObject));
                    });
        } else {
            return this.create("no editor session is open");
        }
    }

    public CompletableFuture<List<EditorOperations>> thenApplyByEditorSession() {
        EditorSession editorSession2 = this.editorSession;
        EditorSocketClient editorSocketClient2 = this.editorSocketClient;
        if (editorSession2 != null
                && editorSocketClient2 != null
                && editorSocketClient2.isEnabled()) {
            CompletableFuture<JsonElement> completableFuture =
                    editorSocketClient2.create(EditorOp.SESSION_CLOSE, this.create(editorSession2));
            return completableFuture.thenApply(
                    result -> {
                        List values = this.findList(result.getAsJsonObject());
                        this.setString("closed");
                        return values;
                    });
        } else {
            return this.create("no editor session is open");
        }
    }

    public void initialize() {
        this.setString("shutdown");
        this.scheduler.shutdownNow();
        this.executor.shutdownNow();
    }

    private void initializeByEditorSocketClient() {
        EditorSocketClient editorSocketClient2 = this.editorSocketClient;
        if (this.editorSocketClient != null) {
            editorSocketClient2.initialize();
        }
        EditorSession editorSession2 = this.editorSession;
        if (this.editorSession != null) {
            if (editorSession2.matchesEnabled(System.currentTimeMillis(), time)) {
                this.platform
                        .getLogger()
                        .info("Closing the idle editor session " + editorSession2.getText() + ".");
                this.setString("idle");
            } else if (this.time3 != 0L && System.currentTimeMillis() - this.time3 > timeTime) {
                this.platform
                        .getLogger()
                        .warning(
                                "Abandoning editor session "
                                        + editorSession2.getText()
                                        + ", it has been unreachable for five minutes.");
                this.setString("abandoned");
            } else {
                if (this.time3 != 0L
                        && (editorSocketClient2 == null || !editorSocketClient2.isEnabled())) {
                    this.setEditorSession(editorSession2);
                }
            }
        }
    }

    private synchronized void setEditorSession(EditorSession editorSession) {
        if (!this.enabled) {
            if (this.scheduledFuture == null || this.scheduledFuture.isDone()) {
                int andIncrement = this.counter.getAndIncrement();
                this.scheduledFuture =
                        this.scheduler.schedule(
                                () -> this.setEditorSessionByEditorSession(editorSession),
                                calculateTime(andIncrement),
                                TimeUnit.MILLISECONDS);
            }
        }
    }

    private void setEditorSessionByEditorSession(final EditorSession editorSession2) {
        EditorSocketClient editorSocketClient2;
        final EditorSocketClient editorSocketClient3;
        synchronized (this) {
            if (this.editorSession != editorSession2) {
                return;
            }
            editorSocketClient2 = this.editorSocketClient;
            editorSocketClient3 = this.create();
            this.editorSocketClient = editorSocketClient3;
            this.enabled = true;
        }
        if (editorSocketClient2 != null) {
            editorSocketClient2.setString("replaced");
        }
        editorSocketClient3.setEditorSession(editorSession2);
        try {
            editorSocketClient3.applyEditorSocketListener(
                    new EditorSocketListener() {

                        @Override
                        public void onOpen() {
                            if (EditorHandler.this.editorSocketClient == editorSocketClient3) {
                                EditorHandler.this.enabled = false;
                                JsonObject jsonObject;
                                (jsonObject = EditorHandler.this.create(editorSession2))
                                        .addProperty("server", GlobalConfig.SERVER.NAME);
                                CompletableFuture<JsonElement> completableFuture =
                                        editorSocketClient3.create(
                                                EditorOp.SESSION_RESUME, jsonObject);
                                completableFuture.whenComplete(
                                        (result, result2) -> {
                                            if (EditorHandler.this.editorSocketClient
                                                    == editorSocketClient3) {
                                                if (result2 != null) {
                                                    if (editorSocketClient3.isEnabled()) {
                                                        EditorHandler.this
                                                                .platform
                                                                .getLogger()
                                                                .log(
                                                                        Level.WARNING,
                                                                        "Editor session could not resume",
                                                                        result2);
                                                        if (EditorHandler.this.editorSession
                                                                == editorSession2) {
                                                            EditorHandler.this.setString(
                                                                    "resume rejected");
                                                        }
                                                    }
                                                } else {
                                                    EditorHandler.this.counter.set(0);
                                                    EditorHandler.this.time3 = 0L;
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onClosed(String result3) {
                            if (EditorHandler.this.matchesEnabled(editorSocketClient3)) {
                                EditorHandler.this.initializeByTime3();
                            }
                        }

                        @Override
                        public void onFailure(Throwable result3) {
                            if (EditorHandler.this.matchesEnabled(editorSocketClient3)) {
                                EditorHandler.this.initializeByTime3();
                            }
                        }
                    });
        } catch (RuntimeException runtimeException) {
            this.platform
                    .getLogger()
                    .log(
                            Level.WARNING,
                            "Editor session could not start a reconnect",
                            runtimeException);
            if (this.matchesEnabled(editorSocketClient3)) {
                this.initializeByTime3();
            }
        }
    }

    private synchronized boolean matchesEnabled(EditorSocketClient editorSocketClient2) {
        if (this.editorSocketClient != editorSocketClient2) {
            return false;
        }
        this.enabled = false;
        return true;
    }

    private synchronized void initializeByScheduledFuture() {
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(false);
        }
        this.scheduledFuture = null;
    }

    private EditorSocketClient create() {
        return new EditorSocketClient(
                this.text,
                EditorJson.getString(),
                this.editorOperationRegistry,
                this.platform.getLogger(),
                this.executor);
    }

    private List<EditorOperations> findList(JsonObject jsonObject) {
        List values;
        return (values =
                                (List)
                                        this.platform
                                                .getGson()
                                                .fromJson(
                                                        jsonObject.get("activity"),
                                                        (new TypeToken<List<EditorOperations>>() {})
                                                                .getType()))
                        == null
                ? Collections.emptyList()
                : values;
    }

    private JsonObject create(EditorSession editorSession) {
        JsonObject jsonObject;
        (jsonObject = new JsonObject()).addProperty("editorId", editorSession.getText());
        jsonObject.addProperty("apiKey", editorSession.getTextString());
        return jsonObject;
    }

    private <T> CompletableFuture<T> create(String text) {
        CompletableFuture<T> completableFuture;
        (completableFuture = new CompletableFuture())
                .completeExceptionally(new IllegalStateException(text));
        return completableFuture;
    }

    private void initializeByTime3() {
        if (this.time3 == 0L) {
            this.time3 = System.currentTimeMillis();
        }
    }

    private void setString(String text) {
        EditorSocketClient editorSocketClient2;
        synchronized (this) {
            editorSocketClient2 = this.editorSocketClient;
            this.editorSocketClient = null;
            this.editorSession = null;
            this.enabled = false;
            this.initializeByScheduledFuture();
        }
        this.time3 = 0L;
        this.counter.set(0);
        if (editorSocketClient2 != null) {
            editorSocketClient2.setString(text);
        }
    }

    private JsonElement getJsonElement() {
        if (this.supplier == null) {
            return JsonNull.INSTANCE;
        }
        try {
            JsonElement jsonElement;
            return (JsonElement)
                    ((jsonElement = this.supplier.get()) == null ? JsonNull.INSTANCE : jsonElement);
        } catch (Exception exception) {
            this.platform
                    .getLogger()
                    .log(Level.WARNING, "Could not read permissions for the editor", exception);
            return JsonNull.INSTANCE;
        }
    }

    private EditorSessionFactory createByPlatform() {
        return new EditorSessionFactory(
                this.platform.getRankHandler().getSortedRanksReversed(),
                this.platform.getTagHandler().getSortedTagsReversed(),
                this.platform.getFilterHandler().getRepository().getCache(),
                this.platform.getPunishmentLadderHandler().getRepository().getCache());
    }

    private static Thread create(Runnable runnable, String text) {
        Thread thread;
        (thread = new Thread(runnable, text)).setDaemon(true);
        return thread;
    }

    public EditorSession getEditorSession() {
        return this.editorSession;
    }
}
