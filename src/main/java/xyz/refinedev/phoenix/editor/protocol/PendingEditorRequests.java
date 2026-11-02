package xyz.refinedev.phoenix.editor.protocol;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

public class PendingEditorRequests {

    private final Map<String, PendingEditorRequests.Entry> entriesByName =
            new ConcurrentHashMap<>();

    private final AtomicLong counter = new AtomicLong();

    private final long time;

    private final LongSupplier longSupplier;

    public PendingEditorRequests(long time, LongSupplier longSupplier2) {
        this.time = time;
        this.longSupplier = longSupplier2;
    }

    public String formatString(CompletableFuture<JsonElement> completableFuture) {
        long asLong = this.longSupplier.getAsLong();
        String text =
                Long.toHexString(this.counter.incrementAndGet()) + "-" + Long.toHexString(asLong);
        this.entriesByName.put(text, new PendingEditorRequests.Entry(completableFuture, asLong));
        return text;
    }

    public boolean matchesEnabled(EditorFrame editorFrame) {
        if (editorFrame.getText() == null) {
            return false;
        }
        PendingEditorRequests.Entry entry;
        if ((entry = this.entriesByName.remove(editorFrame.getText())) == null) {
            return false;
        }
        if (editorFrame.getEditorFrameKind() == EditorFrameKind.ERROR) {
            PendingEditorRequests.Entry.getCompletableFuture(entry)
                    .completeExceptionally(
                            new EditorProtocolException(
                                    editorFrame.getString()
                                            + ": "
                                            + editorFrame.getStringByString()));
        } else {
            PendingEditorRequests.Entry.getCompletableFuture(entry)
                    .complete(editorFrame.getJsonElement());
        }
        return true;
    }

    public void initialize() {
        long asLong = this.longSupplier.getAsLong();
        ArrayList<xyz.refinedev.phoenix.editor.protocol.PendingEditorRequests.Entry> values =
                new ArrayList<>();
        Iterator iterator2 = this.entriesByName.entrySet().iterator();
        while (iterator2.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator2.next();
            if (asLong
                            - PendingEditorRequests.Entry.getTime(
                                    (PendingEditorRequests.Entry) entry.getValue())
                    >= this.time) {
                values.add((PendingEditorRequests.Entry) entry.getValue());
                iterator2.remove();
            }
        }
        iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            PendingEditorRequests.Entry.getCompletableFuture(
                            (PendingEditorRequests.Entry) iterator2.next())
                    .completeExceptionally(
                            new PendingEditorRequests.RequestTimeoutException(
                                    "editor request timed out after " + this.time + "ms"));
        }
    }

    public void applyThrowable(Throwable throwable) {
        ArrayList values2 = new ArrayList<>(this.entriesByName.values());
        this.entriesByName.clear();
        Iterator iterator2 = values2.iterator();
        while (iterator2.hasNext()) {
            PendingEditorRequests.Entry.getCompletableFuture(
                            (PendingEditorRequests.Entry) iterator2.next())
                    .completeExceptionally(throwable);
        }
    }

    public int getCount() {
        return this.entriesByName.size();
    }

    static final class Entry {

        private final CompletableFuture<JsonElement> completableFuture;

        private final long time;

        private Entry(CompletableFuture<JsonElement> completableFuture2, long time) {
            this.completableFuture = completableFuture2;
            this.time = time;
        }

        static CompletableFuture<JsonElement> getCompletableFuture(
                PendingEditorRequests.Entry entry) {
            return entry.completableFuture;
        }

        static long getTime(PendingEditorRequests.Entry entry) {
            return entry.time;
        }
    }

    public static class RequestTimeoutException extends RuntimeException {

        public RequestTimeoutException(String text) {
            super(text);
        }
    }
}
