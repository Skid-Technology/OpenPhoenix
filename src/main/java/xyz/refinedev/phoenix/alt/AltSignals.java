package xyz.refinedev.phoenix.alt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class AltSignals {

    private AltSignals() {}

    public static List<AddressDensity> findList(
            List<AddressDensity> values, long value, boolean matches, String text, String text2) {
        ArrayList<AddressDensity> values2 = new ArrayList<>();
        if (values != null && !values.isEmpty()) {
            if (!matches || !isEmpty(text) && !isEmpty(text2)) {
                long value2 = Math.max(0L, value);
                Iterator<xyz.refinedev.phoenix.alt.AddressDensity> iterator2 = values.iterator();
                while (iterator2.hasNext()) {
                    AddressDensity addressDensity;
                    if ((addressDensity = (AddressDensity) iterator2.next()).getTime() <= value2
                            && (!matches
                                    || addressDensity.getText().equals(text)
                                            && addressDensity.getText().equals(text2))) {
                        values2.add(addressDensity);
                    }
                }
                return values2;
            } else {
                return values2;
            }
        } else {
            return values2;
        }
    }

    private static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }
}
