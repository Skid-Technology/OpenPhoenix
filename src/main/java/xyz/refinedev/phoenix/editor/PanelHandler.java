package xyz.refinedev.phoenix.editor;

import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.editor.op.AuthorizedEditorOperations;
import xyz.refinedev.phoenix.editor.op.EditorOpHandler;
import xyz.refinedev.phoenix.editor.op.EditorOperationRegistry;
import xyz.refinedev.phoenix.editor.panel.PanelLinkStatus;
import xyz.refinedev.phoenix.editor.panel.PanelSessions;
import xyz.refinedev.phoenix.editor.protocol.EditorOp;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class PanelHandler {

    private final CommonPlatform platform;

    private final String text;

    private final EditorOperationRegistry editorOperationRegistry = new EditorOperationRegistry();

    private final AtomicInteger counter = new AtomicInteger();

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(
                    result -> create(result, "Phoenix Panel Scheduler"));

    private final ExecutorService executor =
            Executors.newCachedThreadPool(result -> create(result, "Phoenix Panel Worker"));

    private final EditorSession editorSession =
            new EditorSession("panel", "", IProfile.getConsoleUuid(), "Panel");

    private volatile EditorSocketClient editorSocketClient;

    private volatile boolean enabled;

    private volatile boolean enabledEnabled;

    private volatile String textString;

    private volatile PanelLinkStatus.State state = PanelLinkStatus.State.STOPPED;

    private volatile long timeTime = System.currentTimeMillis();

    private volatile String text2;

    private volatile long time2;

    private volatile long time3;

    private ScheduledFuture<?> scheduledFuture;

    private volatile boolean enabled2;

    public PanelHandler(CommonPlatform commonPlatform, String text) {
        this.platform = commonPlatform;
        this.text = text;
        AuthorizedEditorOperations.applyEditorOperationRegistryByPANEL_AUTH(
                this.editorOperationRegistry, commonPlatform);
    }

    public void applyEditorOp(EditorOp editorOp, EditorOpHandler editorOpHandler) {
        AuthorizedEditorOperations.applyEditorOperationRegistry(
                this.editorOperationRegistry, this.platform, editorOp, editorOpHandler);
    }

    public static long calculateTime(int value) {
        if (value <= 0) {
            return 1000L;
        } else {
            return value >= 6 ? 60000L : 1000L << value;
        }
    }

    public void initialize() {
        if (!this.enabled) {
            this.enabled = true;
            PanelSessions.setRevoker(result -> this.findCompletableFuture(result, null));
            this.initializeByEditorSocketClient();
            PanelHandler panelHandler = this;
            this.scheduler.scheduleAtFixedRate(
                    panelHandler::initializeByEnabled, 15L, 15L, TimeUnit.SECONDS);
        }
    }

    public boolean isEnabled() {
        EditorSocketClient editorSocketClient2 = this.editorSocketClient;
        return this.editorSocketClient != null && editorSocketClient2.isEnabled();
    }

    public PanelLinkStatus create() {
        EditorSocketClient editorSocketClient2 = this.editorSocketClient;
        PanelLinkStatus.State state2 = this.enabled ? this.state : PanelLinkStatus.State.STOPPED;
        return new PanelLinkStatus(
                state2,
                this.timeTime,
                this.text2,
                this.time2,
                this.counter.get(),
                this.time3,
                this.textString,
                editorSocketClient2 == null ? 0 : editorSocketClient2.getCount());
    }

    private void setState(PanelLinkStatus.State state2) {
        if (this.state != state2) {
            this.state = state2;
            this.timeTime = System.currentTimeMillis();
        }
    }

    private void setString(String text2) {
        this.text2 = text2;
        this.time2 = System.currentTimeMillis();
    }

    public void initializeBySTOPPED() {
        this.enabled = false;
        this.setState(PanelLinkStatus.State.STOPPED);
        PanelSessions.setRevoker();
        EditorSocketClient editorSocketClient2 = this.editorSocketClient;
        this.editorSocketClient = null;
        if (editorSocketClient2 != null) {
            editorSocketClient2.setString("shutdown");
        }
        this.scheduler.shutdownNow();
        this.executor.shutdownNow();
    }

    private void initializeByEnabled() {
        if (this.enabled) {
            EditorSocketClient editorSocketClient2 = this.editorSocketClient;
            if (this.editorSocketClient != null) {
                editorSocketClient2.initialize();
            }
            if (editorSocketClient2 == null || !editorSocketClient2.isEnabled()) {
                this.initializeByEnabled2();
            }
        }
    }

    private synchronized void initializeByEnabled2() {
        if (this.enabled && !this.enabled2) {
            if (this.scheduledFuture == null || this.scheduledFuture.isDone()) {
                long value;
                long value2 =
                        (value = calculateTime(this.counter.getAndIncrement())) / 2L
                                + ThreadLocalRandom.current().nextLong(value / 2L + 1L);
                this.time3 = System.currentTimeMillis() + value2;
                PanelHandler panelHandler = this;
                this.scheduledFuture =
                        this.scheduler.schedule(
                                panelHandler::initializeByEditorSocketClient,
                                value2,
                                TimeUnit.MILLISECONDS);
            }
        }
    }

    private boolean matchesEnabled(EditorSocketClient editorSocketClient2) {
        return this.enabled && this.editorSocketClient == editorSocketClient2;
    }

    private synchronized boolean matchesEnabledByEditorSocketClient(
            EditorSocketClient editorSocketClient2) {
        if (this.editorSocketClient != editorSocketClient2) {
            return false;
        }
        this.editorSocketClient = null;
        this.enabled2 = false;
        return this.enabled;
    }

    private void initializeByEditorSocketClient() {
        EditorSocketClient editorSocketClient2;
        EditorSocketClient editorSocketClient3;
        synchronized (this) {
            if (!this.enabled) {
                return;
            }
            editorSocketClient2 = this.editorSocketClient;
            editorSocketClient3 =
                    new EditorSocketClient(
                            this.text,
                            EditorJson.getString(),
                            this.editorOperationRegistry,
                            this.platform.getLogger(),
                            this.executor);
            this.editorSocketClient = editorSocketClient3;
            this.enabled2 = true;
            this.time3 = 0L;
            this.setState(PanelLinkStatus.State.CONNECTING);
        }
        if (editorSocketClient2 != null) {
            editorSocketClient2.setString("replaced");
        }
        editorSocketClient3.setEditorSession(this.editorSession);
        try {
            this.applyEditorSocketClient(editorSocketClient3);
        } catch (RuntimeException runtimeException) {
            if (this.matchesEnabledByEditorSocketClient(editorSocketClient3)) {
                this.setString(formatString(runtimeException));
                this.setState(PanelLinkStatus.State.OFFLINE);
            }
            this.platform
                    .getLogger()
                    .log(
                            Level.WARNING,
                            "Panel link could not start a connection to " + this.text,
                            runtimeException);
        }
    }

    private void applyEditorSocketClient(final EditorSocketClient editorSocketClient) {
        editorSocketClient.applyEditorSocketListener(
                new EditorSocketListener() {

                    @Override
                    public void onOpen() {
                        if (PanelHandler.this.matchesEnabled(editorSocketClient)) {
                            PanelHandler.this.enabled2 = false;
                            PanelHandler.this.counter.set(0);
                            PanelHandler.this.enabledEnabled = false;
                            PanelHandler.this.setState(PanelLinkStatus.State.ANNOUNCING);
                            PanelHandler.this.applyEditorSocketClientByNAME(editorSocketClient);
                        }
                    }

                    @Override
                    public void onClosed(String result) {
                        if (PanelHandler.this.matchesEnabledByEditorSocketClient(
                                editorSocketClient)) {
                            if (result != null && !result.startsWith("1000")) {
                                PanelHandler.this.setString(
                                        "The backend closed the link: " + result);
                            }
                            PanelHandler.this.setState(PanelLinkStatus.State.OFFLINE);
                        }
                    }

                    @Override
                    public void onFailure(Throwable result) {
                        this.onFailure(result, null);
                    }

                    @Override
                    public void onFailure(Throwable result, Integer value) {
                        if (PanelHandler.this.matchesEnabledByEditorSocketClient(
                                editorSocketClient)) {
                            String text2 =
                                    PanelLinkStatus.formatString(
                                            PanelHandler.formatString(result), value);
                            PanelHandler.this.setString(text2);
                            PanelHandler.this.setState(PanelLinkStatus.State.OFFLINE);
                            if (PanelHandler.this.platform.isDebugMode()) {
                                PanelHandler.this
                                        .platform
                                        .getLogger()
                                        .log(
                                                Level.WARNING,
                                                "Panel link could not connect to "
                                                        + PanelHandler.this.text,
                                                result);
                            } else if (!PanelHandler.this.enabledEnabled) {
                                PanelHandler.this.enabledEnabled = true;
                                PanelHandler.this
                                        .platform
                                        .getLogger()
                                        .warning(
                                                "Panel link is offline - retrying quietly in the background. Staff cannot sign in until it is up. ("
                                                        + PanelHandler.this.text
                                                        + ": "
                                                        + text2
                                                        + ") Run /panel status to check on it.");
                            }
                        }
                    }
                });
    }

    private static String formatString(Throwable throwable) {
        if (throwable == null) {
            return "unknown";
        }
        String text;
        return (text = throwable.getMessage()) != null && !text.isEmpty()
                ? text
                : throwable.getClass().getSimpleName();
    }

    public CompletableFuture<Boolean> findCompletableFuture(UUID uUID, String text) {
        EditorSocketClient editorSocketClient2 = this.editorSocketClient;
        if (this.editorSocketClient != null && editorSocketClient2.isEnabled()) {
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("target", uUID.toString());
            if (text != null) {
                jsonObject.addProperty("sessionId", text);
            }
            CompletableFuture<com.google.gson.JsonElement> completableFuture =
                    editorSocketClient2.create(EditorOp.PANEL_REVOKE, jsonObject);
            return completableFuture.handle(
                    (result, result2) -> {
                        if (result2 == null) {
                            return true;
                        }
                        this.platform
                                .getLogger()
                                .log(Level.WARNING, "Panel sessions could not be revoked", result2);
                        return false;
                    });
        } else {
            return CompletableFuture.completedFuture(false);
        }
    }

    public String getTextString() {
        return this.textString;
    }

    private void applyEditorSocketClientByNAME(EditorSocketClient editorSocketClient) {
        JsonObject jsonObject;
        (jsonObject = new JsonObject()).addProperty("server", GlobalConfig.SERVER.NAME);
        jsonObject.add("groups", this.platform.getGson().toJsonTree(GlobalConfig.SERVER.GROUPS));
        CompletableFuture<com.google.gson.JsonElement> completableFuture =
                editorSocketClient.create(EditorOp.PANEL_ANNOUNCE, jsonObject);
        completableFuture.whenComplete(
                (result, result2) -> {
                    if (this.matchesEnabled(editorSocketClient)) {
                        if (result2 != null) {
                            if (editorSocketClient.isEnabled()) {
                                this.platform
                                        .getLogger()
                                        .log(
                                                Level.WARNING,
                                                "Panel link connected but was refused",
                                                result2);
                                this.setString(
                                        "The backend refused this server: "
                                                + formatString(result2));
                                this.setState(PanelLinkStatus.State.REFUSED);
                            }
                        } else {
                            JsonObject jsonObject2 =
                                    result != null && result.isJsonObject()
                                            ? result.getAsJsonObject()
                                            : new JsonObject();
                            this.textString =
                                    jsonObject2.has("network")
                                                    && !jsonObject2.get("network").isJsonNull()
                                            ? jsonObject2.get("network").getAsString()
                                            : null;
                            if (this.textString != null) {
                                this.platform
                                        .getLogger()
                                        .info("Panel link is up. Network ID: " + this.textString);
                                this.setState(PanelLinkStatus.State.UP);
                            } else {
                                this.platform
                                        .getLogger()
                                        .warning(
                                                "Panel link is up, but the backend sent no Network ID - it is older than this plugin. Nobody can sign in until the backend is updated.");
                                this.setString(
                                        "The backend sent no Network ID; it is older than this plugin.");
                                this.setState(PanelLinkStatus.State.NO_NETWORK_ID);
                            }
                        }
                    }
                });
    }

    private static Thread create(Runnable runnable, String text) {
        Thread thread;
        (thread = new Thread(runnable, text)).setDaemon(true);
        return thread;
    }
}
