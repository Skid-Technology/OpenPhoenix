package xyz.refinedev.phoenix.setup.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BypassPermissionCatalog {

    public static final List<String> strings;

    public static final Map<String, List<String>> stringsByName =
            Collections.unmodifiableMap(
                    new LinkedHashMap<String, List<String>>() {

                        {
                            this.put(
                                    "chat-control",
                                    Collections.unmodifiableList(
                                            Arrays.asList(
                                                    "core.mutechat.bypass",
                                                    "core.slowchat.bypass",
                                                    "core.clearchat.bypass")));
                            this.put("filter", Collections.singletonList("core.filter.bypass"));
                            this.put(
                                    "cooldowns",
                                    Collections.singletonList("core.cooldowns.bypass"));
                            this.put("antispam", Collections.singletonList("core.antispam.bypass"));
                        }
                    });

    public static List<String> getList() {
        ArrayList<String> values2 = new ArrayList<>();
        Iterator<List<String>> iterator2 = stringsByName.values().iterator();
        while (iterator2.hasNext()) {
            for (String text : (Iterable<String>) ((Iterable) (List) iterator2.next())) {
                if (!strings.contains(text)) {
                    values2.add(text);
                }
            }
        }
        return values2;
    }

    private BypassPermissionCatalog() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    static {
        String[] texts = new String[10];
        texts[0] = "core.filter.bypass";
        texts[1] = "core.antispam.bypass";
        texts[2] = "core.cooldowns.bypass";
        texts[3] = "core.mutechat.bypass";
        texts[4] = "core.slowchat.bypass";
        texts[5] = "core.clearchat.bypass";
        texts[6] = "core.bypass.tabcomplete";
        texts[7] = "core.immune.bypass";
        texts[8] = "core.punish.duration.bypass";
        texts[9] = "core.joinfull";
        strings = Collections.unmodifiableList(Arrays.asList(texts));
    }
}
