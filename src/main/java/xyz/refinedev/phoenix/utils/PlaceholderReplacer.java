package xyz.refinedev.phoenix.utils;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;

import java.util.Map;

public final class PlaceholderReplacer {

    private PlaceholderReplacer() {}

    public static void applyString(String text, Map<String, String> entries) {
        Phoenix.getInstance()
                .getPacketHandler()
                .send(new SendToStaffPacket(text, entries, true, false, true));
    }
}
