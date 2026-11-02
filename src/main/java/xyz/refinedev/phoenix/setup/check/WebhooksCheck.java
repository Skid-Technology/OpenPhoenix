package xyz.refinedev.phoenix.setup.check;

import xyz.refinedev.phoenix.setup.SetupCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class WebhooksCheck {

    public static final String text = "Webhooks";

    private static final String textString = "Set the webhook urls in webhooks.yml.";

    public static List<SetupCheck> evaluate(Map<String, Boolean> entries) {
        if (entries.isEmpty()) {
            return Collections.singletonList(
                    SetupCheck.of("Webhooks", "No webhook feeds were read."));
        }
        ArrayList<String> values = new ArrayList<>();
        int value = 0;
        for (Entry<String, Boolean> entry : entries.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                value++;
            } else {
                values.add((String) entry.getKey());
            }
        }
        ArrayList<SetupCheck> values2 = new ArrayList<>();
        if (value == 0) {
            values2.add(
                    SetupCheck.of(
                                    "Webhooks",
                                    "None of the " + entries.size() + " webhook feeds have a url.",
                                    "Set the webhook urls in webhooks.yml.")
                            .of(
                                    "Nothing reaches Discord. This is the shipped state, not a fault."));
            return values2;
        } else if (values.isEmpty()) {
            values2.add(
                    SetupCheck.of(
                            "Webhooks", "All " + entries.size() + " webhook feeds have a url."));
            return values2;
        } else {
            values2.add(
                    SetupCheck.of(
                                    "Webhooks",
                                    value + " of " + entries.size() + " webhook feeds have a url.")
                            .of(values.toArray(new String[0])));
            return values2;
        }
    }

    private WebhooksCheck() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
