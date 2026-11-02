package xyz.refinedev.phoenix.setup;

import java.util.HashMap;
import java.util.Map;

public final class SetupCatalog {

    public static final String HTTPS_DOCS_REFINEDEV_ORG_PHOENIX_INSTALLATION =
            "https://docs.refinedev.org/Phoenix/Installation/";

    public static final String HTTPS_DOCS_REFINEDEV_ORG_PHOENIX_DATABASE =
            "https://docs.refinedev.org/phoenix/database/";

    private static final Map<String, String> stringsByName = new HashMap<>();

    private SetupCatalog() {}

    public static String formatString(String text) {
        return text == null ? null : stringsByName.get(text);
    }

    static {
        stringsByName.put("Storage", "https://docs.refinedev.org/phoenix/database/");
        stringsByName.put("Ranks", "https://docs.refinedev.org/Phoenix/Features/Ranks/");
        stringsByName.put("Bypasses", "https://docs.refinedev.org/Phoenix/Permissions/");
        stringsByName.put(
                "Config master", "https://docs.refinedev.org/Phoenix/Features/ConfigSync/");
        stringsByName.put("Alts", "https://docs.refinedev.org/Phoenix/Features/Alts/");
        stringsByName.put("Anti-VPN", "https://docs.refinedev.org/Phoenix/Features/Proxy/");
        stringsByName.put("Filters", "https://docs.refinedev.org/Phoenix/Features/Filters/");
        stringsByName.put(
                "Punishment ladders", "https://docs.refinedev.org/Phoenix/Features/Punishments/");
        stringsByName.put("Locales", "https://docs.refinedev.org/Phoenix/Features/Locales/");
        stringsByName.put("Reports", "https://docs.refinedev.org/Phoenix/Features/Reports/");
        stringsByName.put("Staff tools", "https://docs.refinedev.org/Phoenix/Features/Staff/");
        stringsByName.put("Tags", "https://docs.refinedev.org/Phoenix/Features/Tags/");
        stringsByName.put("Two-factor", "https://docs.refinedev.org/Phoenix/Features/2FA/");
    }
}
