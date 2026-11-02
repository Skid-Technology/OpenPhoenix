package xyz.refinedev.phoenix.editor.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AltDetails {

    @SerializedName("alt")
    private final AltSummary alt;

    @SerializedName("signals")
    private final List<AltDetails.Entry> signals;

    @SerializedName("lastEvaluated")
    private final long lastEvaluated;

    public AltSummary getAlt() {
        return this.alt;
    }

    public List<AltDetails.Entry> getSignals() {
        return this.signals;
    }

    public long getLastEvaluated() {
        return this.lastEvaluated;
    }

    public AltDetails(AltSummary altSummary, List<AltDetails.Entry> signals, long lastEvaluated) {
        this.alt = altSummary;
        this.signals = signals;
        this.lastEvaluated = lastEvaluated;
    }

    public static class Entry {

        @SerializedName("name")
        private final String name;

        @SerializedName("points")
        private final double points;

        @SerializedName("detail")
        private final String detail;

        public String getName() {
            return this.name;
        }

        public double getPoints() {
            return this.points;
        }

        public String getDetail() {
            return this.detail;
        }

        public Entry(String name, double points, String detail) {
            this.name = name;
            this.points = points;
            this.detail = detail;
        }
    }
}
