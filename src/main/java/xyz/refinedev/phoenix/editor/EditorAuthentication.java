package xyz.refinedev.phoenix.editor;

import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.editor.op.EditorOperationException;
import xyz.refinedev.phoenix.editor.op.EditorPayload;
import xyz.refinedev.phoenix.editor.panel.PanelIdentity;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.auth.code.googleapi.GoogleAuthenticator;
import xyz.refinedev.phoenix.utils.auth.code.googleapi.QrCodeImage;

import java.util.UUID;

public final class EditorAuthentication {

    private static final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    private EditorAuthentication() {}

    public static boolean matchesEnabled(IProfile profile) {
        return profile != null
                && profile.getSecretKey() != null
                && !profile.getSecretKey().isEmpty();
    }

    public static void applyPhoenix(Phoenix phoenix, UUID uUID, JsonObject jsonObject) {
        IProfile profile;
        if (matchesEnabled(profile = phoenix.getProfileHandler().getProfile(uUID))) {
            if (!PanelIdentity.currentTimeMillis(uUID)) {
                String text;
                if ((text = EditorPayload.formatString(jsonObject, "twoFactor", "").trim())
                        .isEmpty()) {
                    throw new EditorOperationException(
                            "TWO_FACTOR_REQUIRED", "this needs your two-factor code");
                }
                int value;
                try {
                    value = Integer.parseInt(text.replace(" ", ""));
                } catch (NumberFormatException numberFormatException) {
                    throw new EditorOperationException(
                            "TWO_FACTOR_REQUIRED", "that code is not right");
                }
                if (!QrCodeImage.authorize(googleAuthenticator, profile.getSecretKey(), value)) {
                    throw new EditorOperationException(
                            "TWO_FACTOR_REQUIRED", "that code is not right");
                }
                PanelIdentity.applyUUID(uUID);
            }
        }
    }
}
