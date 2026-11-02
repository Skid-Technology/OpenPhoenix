package xyz.refinedev.phoenix.dialog.api;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBT;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;

public class DialogResponse {

    private final NBTCompound nBTCompound;

    public DialogResponse(NBT nBT) {
        this.nBTCompound = nBT instanceof NBTCompound ? (NBTCompound) nBT : null;
    }

    public String formatString(String text, String text2, int value) {
        if (this.nBTCompound == null) {
            return text2;
        }
        String text3;
        try {
            text3 = this.nBTCompound.getStringTagValueOrNull(text);
        } catch (Throwable throwable) {
            return text2;
        }
        if (text3 == null) {
            return text2;
        } else if ((text3 = text3.trim()).isEmpty()) {
            return text2;
        } else {
            return text3.length() > value ? text3.substring(0, value) : text3;
        }
    }

    public boolean matchesEnabled(String text, boolean matches) {
        if (this.nBTCompound == null) {
            return matches;
        }
        try {
            String text2;
            if ((text2 = this.nBTCompound.getStringTagValueOrNull(text)) != null) {
                return Boolean.parseBoolean(text2.trim());
            }
        } catch (Throwable throwable) {
        }
        try {
            return this.nBTCompound.getBooleanOr(text, matches);
        } catch (Throwable throwable2) {
            return matches;
        }
    }

    public long calculateTime(String text, long value, long value2, long value3) {
        if (this.nBTCompound == null) {
            return value;
        }
        Number number;
        try {
            number = this.nBTCompound.getNumberTagValueOrNull(text);
        } catch (Throwable throwable) {
            return value;
        }
        if (number == null) {
            String text2;
            if ((text2 = this.formatString(text, null, 32)) == null) {
                return value;
            }
            try {
                return Math.max(value2, Math.min(value3, Long.parseLong(text2)));
            } catch (NumberFormatException numberFormatException) {
                return value;
            }
        } else {
            return Math.max(value2, Math.min(value3, number.longValue()));
        }
    }

    public boolean isEnabled() {
        return this.nBTCompound != null;
    }
}
