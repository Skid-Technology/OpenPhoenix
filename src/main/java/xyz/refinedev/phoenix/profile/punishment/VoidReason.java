package xyz.refinedev.phoenix.profile.punishment;

public enum VoidReason {
    MISTAKE("Mistake", true),
    INSUFFICIENT_EVIDENCE("Insufficient Evidence", true),
    STAFF_ABUSE("Staff Abuse", true),
    APPEAL_ACCEPTED("Appeal Accepted", false),
    COMPROMISED_ACCOUNT("Compromised Account", false),
    OTHER("Other", false);

    private final String niceName;

    private final boolean staffFault;

    public static VoidReason parse(String text) {
        if (text != null && !text.isEmpty()) {
            String text2 = text.split(":", 2)[0].trim();
            VoidReason[] voidReasons;
            int value = (voidReasons = values()).length;
            for (int index = 0; index < value; index++) {
                VoidReason voidReason;
                if ((voidReason = voidReasons[index]).name().equalsIgnoreCase(text2)) {
                    return voidReason;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public String format(String text) {
        return text != null && !text.trim().isEmpty()
                ? this.name() + ": " + text.trim()
                : this.name();
    }

    public static String noteOf(String text) {
        if (text == null) {
            return "";
        }
        String[] texts;
        return (texts = text.split(":", 2)).length < 2 ? "" : texts[1].trim();
    }

    public String getNiceName() {
        return this.niceName;
    }

    public boolean isStaffFault() {
        return this.staffFault;
    }

    VoidReason(String niceName, boolean staffFault) {
        this.niceName = niceName;
        this.staffFault = staffFault;
    }
}
