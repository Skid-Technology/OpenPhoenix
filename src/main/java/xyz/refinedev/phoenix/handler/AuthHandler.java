package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.auth.GoogleCodeAuthenticator;
import xyz.refinedev.phoenix.utils.auth.YubikeyAuthenticator;
import xyz.refinedev.phoenix.utils.auth.api.Authenticator;
import xyz.refinedev.phoenix.utils.auth.code.googleapi.GoogleAuthenticator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthHandler {

    private final List<Authenticator> authenticators = new ArrayList<>();

    private final Map<UUID, Integer> authAttempts = new ConcurrentHashMap<>();

    public AuthHandler(Phoenix phoenix) {
        if (SettingsConfig.AUTH.QRCODE_ENABLED) {
            this.authenticators.add(
                    new GoogleCodeAuthenticator(phoenix, new GoogleAuthenticator()));
        }
        if (SettingsConfig.AUTH.YUBIKEY_ENABLED) {
            this.authenticators.add(
                    new YubikeyAuthenticator(
                            phoenix,
                            SettingsConfig.AUTH.YUBIKEY_ID,
                            SettingsConfig.AUTH.YUBIKEY_SECRET));
        }
    }

    @Nullable
    public Authenticator getAuthenticatorByType(String text) {
        if (text == null) {
            return null;
        }
        Iterator<Authenticator> iterator2 = this.authenticators.iterator();
        while (iterator2.hasNext()) {
            Authenticator authenticator;
            if ((authenticator = (Authenticator) iterator2.next())
                    .getName()
                    .equalsIgnoreCase(text)) {
                return authenticator;
            }
        }
        return null;
    }

    public List<Authenticator> getAuthenticators() {
        return this.authenticators;
    }

    public Map<UUID, Integer> getAuthAttempts() {
        return this.authAttempts;
    }
}
