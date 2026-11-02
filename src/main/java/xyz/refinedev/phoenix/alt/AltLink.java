package xyz.refinedev.phoenix.alt;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.TrackedEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AltLink extends TrackedEntity implements IAltLink {

    @SerializedName("_id")
    private final String id;

    @SerializedName("a")
    private final UUID a;

    @SerializedName("b")
    private final UUID b;

    @SerializedName("score")
    private int score;

    @SerializedName("signals")
    private List<IAltSignal> signals = new ArrayList<>();

    @SerializedName("sharedAddresses")
    private int sharedAddresses;

    @SerializedName("independentSubnets")
    private int independentSubnets;

    @SerializedName("lastSharedLogin")
    private long lastSharedLogin;

    @SerializedName("firstLinked")
    private long firstLinked = System.currentTimeMillis();

    @SerializedName("lastEvaluated")
    private long lastEvaluated = System.currentTimeMillis();

    @SerializedName("status")
    private AltLinkStatus status = AltLinkStatus.ACTIVE;

    public AltLink(UUID uUID, UUID uUID2) {
        boolean matches = uUID.toString().compareTo(uUID2.toString()) <= 0;
        this.a = matches ? uUID : uUID2;
        this.b = matches ? uUID2 : uUID;
        this.id = formatString(uUID, uUID2);
    }

    public static String formatString(UUID uUID, UUID uUID2) {
        String text = uUID.toString();
        String text2 = uUID2.toString();
        return text.compareTo(text2) <= 0 ? text + ":" + text2 : text2 + ":" + text;
    }

    @Override
    public UUID other(UUID uUID) {
        return this.a.equals(uUID) ? this.b : this.a;
    }

    @Override
    public boolean involves(UUID uUID) {
        return this.a.equals(uUID) || this.b.equals(uUID);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public UUID getA() {
        return this.a;
    }

    @Override
    public UUID getB() {
        return this.b;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public List<IAltSignal> getSignals() {
        return this.signals;
    }

    @Override
    public int getSharedAddresses() {
        return this.sharedAddresses;
    }

    @Override
    public int getIndependentSubnets() {
        return this.independentSubnets;
    }

    @Override
    public long getLastSharedLogin() {
        return this.lastSharedLogin;
    }

    @Override
    public long getFirstLinked() {
        return this.firstLinked;
    }

    @Override
    public long getLastEvaluated() {
        return this.lastEvaluated;
    }

    @Override
    public AltLinkStatus getStatus() {
        return this.status;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public void setSignals(List<IAltSignal> signals) {
        this.signals = signals;
    }

    @Override
    public void setSharedAddresses(int sharedAddresses) {
        this.sharedAddresses = sharedAddresses;
    }

    @Override
    public void setIndependentSubnets(int independentSubnets) {
        this.independentSubnets = independentSubnets;
    }

    @Override
    public void setLastSharedLogin(long lastSharedLogin) {
        this.lastSharedLogin = lastSharedLogin;
    }

    public void setFirstLinked(long firstLinked) {
        this.firstLinked = firstLinked;
    }

    @Override
    public void setLastEvaluated(long lastEvaluated) {
        this.lastEvaluated = lastEvaluated;
    }

    @Override
    public void setStatus(AltLinkStatus altLinkStatus) {
        this.status = altLinkStatus;
    }
}
