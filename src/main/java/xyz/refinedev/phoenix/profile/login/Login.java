package xyz.refinedev.phoenix.profile.login;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.TrackedEntity;

import java.util.UUID;

public class Login extends TrackedEntity implements ILogin {

    @SerializedName("_id")
    private final UUID id = UUID.randomUUID();

    @SerializedName("target")
    private final UUID target;

    @SerializedName("login")
    private final long login = System.currentTimeMillis();

    @SerializedName("ip")
    private final String ip;

    @SerializedName("subnet")
    private String subnet;

    @SerializedName("logout")
    private long logout = 0L;

    public Login(UUID target, String ip) {
        this.target = target;
        this.ip = ip;
    }

    public Login(UUID target, String ip, String subnet) {
        this.target = target;
        this.ip = ip;
        this.subnet = subnet;
    }

    @Override
    public long getDuration() {
        return !this.hasLoggedOut()
                ? System.currentTimeMillis() - this.login
                : this.logout - this.login;
    }

    @Override
    public boolean hasLoggedOut() {
        return this.logout != 0L;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public UUID getTarget() {
        return this.target;
    }

    @Override
    public long getLogin() {
        return this.login;
    }

    @Override
    public String getIp() {
        return this.ip;
    }

    @Override
    public String getSubnet() {
        return this.subnet;
    }

    @Override
    public long getLogout() {
        return this.logout;
    }

    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }

    @Override
    public void setLogout(long logout) {
        this.logout = logout;
    }
}
