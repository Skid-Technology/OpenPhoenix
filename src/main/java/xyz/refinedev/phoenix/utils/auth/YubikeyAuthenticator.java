package xyz.refinedev.phoenix.utils.auth;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.auth.api.Authenticator;
import xyz.refinedev.phoenix.utils.auth.yubikey.api.VerificationResponse;
import xyz.refinedev.phoenix.utils.auth.yubikey.api.YubicoClient;
import xyz.refinedev.phoenix.utils.auth.yubikey.exception.YubicoValidationFailure;
import xyz.refinedev.phoenix.utils.auth.yubikey.exception.YubicoVerificationException;

public class YubikeyAuthenticator implements Authenticator {

    private final CommonPlatform platform;

    private final YubicoClient yubicoClient;

    private static final String name = "YubiKey";

    public YubikeyAuthenticator(CommonPlatform commonPlatform, int value, String text) {
        this.platform = commonPlatform;
        this.yubicoClient = YubicoClient.getClient(value, text);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setup(Player player) {
        IProfile profile;
        (profile = this.platform.getProfileHandler().getCachedProfile(player.getUniqueId()))
                .setAuthType(this.getName());
        profile.saveProfile();
        player.sendMessage(
                Color.translate(LocaleUtil.tr(player, MessageConfig.AUTH.YUBIKEY_SPECIFIC)));
    }

    @Override
    public boolean authenticate(Player player, String text) {
        IProfile cachedProfile =
                this.platform.getProfileHandler().getCachedProfile(player.getUniqueId());
        if (!YubicoClient.isValidOTPFormat(text)) {
            return false;
        }
        String secretKey = cachedProfile.getSecretKey();
        try {
            VerificationResponse verificationResponse;
            if ((verificationResponse = this.yubicoClient.verify(text)).isOk()
                    && (secretKey.isEmpty()
                            || verificationResponse.getPublicId().equals(secretKey))) {
                if (secretKey.isEmpty()) {
                    cachedProfile.setSecretKey(verificationResponse.getPublicId());
                }
                return true;
            } else {
                return false;
            }
        } catch (YubicoValidationFailure | YubicoVerificationException exception) {
            return false;
        }
    }

    @Override
    public boolean canSetup(Player player) {
        if (!this.platform
                .getProfileHandler()
                .getCachedProfile(player.getUniqueId())
                .getSecretKey()
                .equals("")) {
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.AUTH.ALREADY_ENABLED)));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean canAuthenticate(Player player) {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return SettingsConfig.AUTH.YUBIKEY_ENABLED;
    }
}
