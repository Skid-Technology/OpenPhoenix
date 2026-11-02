package xyz.refinedev.phoenix.setup;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SetupReminder {

    private final Set<UUID> uUIDs = ConcurrentHashMap.newKeySet();

    private volatile int count;

    public void setList(List<SetupCheck> values) {
        int value = 0;
        Iterator<xyz.refinedev.phoenix.setup.SetupCheck> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            if (((SetupCheck) iterator2.next()).getCheckStatus() != CheckStatus.OK) {
                value++;
            }
        }
        this.count = value;
    }

    public int getCount() {
        return this.count;
    }

    public boolean matchesEnabled(UUID uUID) {
        return this.count > 0 && this.uUIDs.add(uUID);
    }
}
