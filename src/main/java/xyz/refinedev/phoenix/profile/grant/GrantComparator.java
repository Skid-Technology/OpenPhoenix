package xyz.refinedev.phoenix.profile.grant;

import java.util.Comparator;

public class GrantComparator implements Comparator<IGrant> {

    public int compare(IGrant grant, IGrant grant2) {
        if (grant.isActiveOnOneScope() == grant2.isActiveOnOneScope()) {
            return grant.getIssuedAt() > grant2.getIssuedAt() ? -1 : 1;
        } else if (grant.isActiveOnOneScope() && !grant2.isActiveOnOneScope()) {
            return -1;
        } else {
            return !grant.isActiveOnOneScope() && grant2.isActiveOnOneScope() ? 1 : 0;
        }
    }
}
