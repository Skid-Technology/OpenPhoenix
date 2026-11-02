package xyz.refinedev.phoenix.editor.dto;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.notes.INote;

import java.util.List;

public class ProfileDetails {

    @SerializedName("summary")
    private final ProfileSummary summary;

    @SerializedName("grants")
    private final JsonArray grants;

    @SerializedName("punishments")
    private final JsonArray punishments;

    @SerializedName("notes")
    private final List<INote> notes;

    @SerializedName("alts")
    private final List<AltSummary> alts;

    @SerializedName("altsTruncated")
    private final boolean altsTruncated;

    @SerializedName("logins")
    private final List<ILogin> logins;

    @SerializedName("firstLogin")
    private final String firstLogin;

    @SerializedName("lastLogin")
    private final String lastLogin;

    public ProfileSummary getSummary() {
        return this.summary;
    }

    public JsonArray getGrants() {
        return this.grants;
    }

    public JsonArray getPunishments() {
        return this.punishments;
    }

    public List<INote> getNotes() {
        return this.notes;
    }

    public List<AltSummary> getAlts() {
        return this.alts;
    }

    public boolean isAltsTruncated() {
        return this.altsTruncated;
    }

    public List<ILogin> getLogins() {
        return this.logins;
    }

    public String getFirstLogin() {
        return this.firstLogin;
    }

    public String getLastLogin() {
        return this.lastLogin;
    }

    public ProfileDetails(
            ProfileSummary profileSummary,
            JsonArray jsonArray,
            JsonArray jsonArray2,
            List<INote> notes,
            List<AltSummary> alts,
            boolean altsTruncated,
            List<ILogin> logins,
            String firstLogin,
            String lastLogin) {
        this.summary = profileSummary;
        this.grants = jsonArray;
        this.punishments = jsonArray2;
        this.notes = notes;
        this.alts = alts;
        this.altsTruncated = altsTruncated;
        this.logins = logins;
        this.firstLogin = firstLogin;
        this.lastLogin = lastLogin;
    }
}
