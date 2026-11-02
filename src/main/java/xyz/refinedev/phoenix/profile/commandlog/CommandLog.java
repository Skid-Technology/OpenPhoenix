package xyz.refinedev.phoenix.profile.commandlog;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.UUID;

public class CommandLog implements ICommandLog {

    @SerializedName("_id")
    private final UUID id = UUID.randomUUID();

    @SerializedName("issuedBy")
    private final UUID issuedBy;

    @SerializedName("command")
    private final String command;

    @SerializedName("server")
    private final String server;

    @SerializedName("issuedOn")
    private final long issuedOn = System.currentTimeMillis();

    @NotNull
    @Override
    public String getNiceIssuedBy() {
        IProfile profile;
        return (profile =
                                PlatformGetter.getInstance()
                                        .getProfileHandler()
                                        .getProfile(this.issuedBy))
                        == null
                ? this.issuedBy.toString()
                : profile.getName();
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            CommandLog commandLog = (CommandLog) result;
            return this.id.equals(commandLog.id);
        } else {
            return false;
        }
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public UUID getIssuedBy() {
        return this.issuedBy;
    }

    @Override
    public String getCommand() {
        return this.command;
    }

    @Override
    public String getServer() {
        return this.server;
    }

    @Override
    public long getIssuedOn() {
        return this.issuedOn;
    }

    public CommandLog(UUID issuedBy, String command, String server) {
        this.issuedBy = issuedBy;
        this.command = command;
        this.server = server;
    }
}
