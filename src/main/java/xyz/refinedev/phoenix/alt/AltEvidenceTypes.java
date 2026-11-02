package xyz.refinedev.phoenix.alt;

public final class AltEvidenceTypes {

    private AltEvidenceTypes() {}

    public static boolean matchesEnabled(int value, AltLinkStatus altLinkStatus, int value2) {
        return altLinkStatus != AltLinkStatus.ACTIVE ? false : value >= value2;
    }
}
