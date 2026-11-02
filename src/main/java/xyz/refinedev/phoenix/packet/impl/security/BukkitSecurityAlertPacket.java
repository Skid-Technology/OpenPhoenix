package xyz.refinedev.phoenix.packet.impl.security;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PlaceholderReplacer;

public class BukkitSecurityAlertPacket extends SecurityAlertPacket {

    public BukkitSecurityAlertPacket(String text, String text2, String text3, String text4) {
        super(text, text2, text3, text4);
    }

    @Override
    public void onReceive() {
        if (GlobalConfig.SERVER.NAME.equals(super.server)) {
            PlaceholderReplacer.applyString(
                    MessageConfig.SECURITY.ALERT_GRANT,
                    LocaleUtil.replacements(
                            "<server>",
                            Phoenix.getInstance().getNetworkHandler().getDisplayName(),
                            "<actor>",
                            super.actor,
                            "<target>",
                            super.target,
                            "<rank>",
                            super.rank));
        }
    }
}
