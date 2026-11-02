package xyz.refinedev.phoenix.packet.impl.server;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.packet.api.Packet;

public class ConfigReloadPacket implements Packet {

    @SerializedName("file")
    private String file;

    @SerializedName("from")
    private String from;

    @Override
    public void onReceive() {
        if (!GlobalConfig.SERVER.NAME.equals(this.from)) {
            Phoenix.getInstance().getConfigSyncHandler().loadFromRedis();
            Phoenix.getInstance()
                    .log(
                            "Re-read configs after "
                                    + this.file
                                    + " was edited on "
                                    + this.from
                                    + ".");
        }
    }

    public ConfigReloadPacket(String file, String from) {
        this.file = file;
        this.from = from;
    }

    public ConfigReloadPacket() {}
}
