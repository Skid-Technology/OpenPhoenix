package xyz.refinedev.phoenix.alt;

import com.google.gson.annotations.SerializedName;

public class AltSignal implements IAltSignal {

    @SerializedName("name")
    private final String name;

    @SerializedName("points")
    private final double points;

    @SerializedName("detail")
    private final String detail;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public double getPoints() {
        return this.points;
    }

    @Override
    public String getDetail() {
        return this.detail;
    }

    public AltSignal(String name, double points, String detail) {
        this.name = name;
        this.points = points;
        this.detail = detail;
    }
}
