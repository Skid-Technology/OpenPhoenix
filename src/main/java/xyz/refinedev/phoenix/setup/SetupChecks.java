package xyz.refinedev.phoenix.setup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public final class SetupChecks {

    private static final String text = "This is a utility class and cannot be instantiated";

    public static List<SetupSummary> findList(List<SetupCheck> values) {
        LinkedHashMap entries = new LinkedHashMap();
        for (SetupCheck setupCheck : (Iterable<SetupCheck>) ((Iterable) values)) {
            List<xyz.refinedev.phoenix.setup.SetupCheck> values2;
            if ((values2 = (List) entries.get(setupCheck.getText())) == null) {
                values2 = new ArrayList();
                entries.put(setupCheck.getText(), values2);
            }
            values2.add(setupCheck);
        }
        ArrayList<xyz.refinedev.phoenix.setup.SetupSummary> values3 = new ArrayList<>();
        for (Entry entry : (Iterable<Entry>) ((Iterable) entries.entrySet())) {
            values3.add(
                    new SetupSummary((String) entry.getKey(), (List<SetupCheck>) entry.getValue()));
        }
        return Collections.unmodifiableList(values3);
    }

    public static SetupSummary findSetupSummary(List<SetupSummary> values) {
        SetupSummary setupSummary = null;
        Iterator<xyz.refinedev.phoenix.setup.SetupSummary> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            SetupSummary setupSummary2;
            if (!(setupSummary2 = (SetupSummary) iterator2.next()).isEnabled()
                    && (setupSummary == null
                            || setupSummary2
                                    .getCheckStatus()
                                    .isWorseThan(setupSummary.getCheckStatus()))) {
                setupSummary = setupSummary2;
            }
        }
        return setupSummary;
    }

    public static int calculateCount(List<SetupCheck> values, CheckStatus checkStatus) {
        int value = 0;
        Iterator<xyz.refinedev.phoenix.setup.SetupCheck> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            if (((SetupCheck) iterator2.next()).getCheckStatus() == checkStatus) {
                value++;
            }
        }
        return value;
    }

    private SetupChecks() {
        throw new UnsupportedOperationException(text);
    }
}
