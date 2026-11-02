package xyz.refinedev.phoenix.dialog.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerAbstract;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerPriority;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketReceiveEvent;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCustomClickAction;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.UUID;

public class DialogPacketListener extends PacketListenerAbstract {

    private final DialogHandler dialogHandler;

    private static final String PHOENIX_TEXT = "phoenix";

    public DialogPacketListener(DialogHandler dialogHandler2) {
        super(PacketListenerPriority.NORMAL);
        this.dialogHandler = dialogHandler2;
    }

    public void onPacketReceive(PacketReceiveEvent packetReceiveEvent) {
        if (packetReceiveEvent.getPacketType() == Client.CUSTOM_CLICK_ACTION) {
            WrapperPlayClientCustomClickAction wrapperPlayClientCustomClickAction;
            if ((wrapperPlayClientCustomClickAction =
                                            new WrapperPlayClientCustomClickAction(
                                                    packetReceiveEvent))
                                    .getId()
                            != null
                    && PHOENIX_TEXT.equals(
                            wrapperPlayClientCustomClickAction.getId().getNamespace())) {
                packetReceiveEvent.setCancelled(true);
                UUID uUID;
                if ((uUID =
                                packetReceiveEvent.getUser() == null
                                        ? null
                                        : packetReceiveEvent.getUser().getUUID())
                        != null) {
                    WrapperPlayClientCustomClickAction wrapperPlayClientCustomClickAction2 =
                            wrapperPlayClientCustomClickAction;
                    UUID uUID2 = uUID;
                    Tasks.run(
                            () -> {
                                Player player;
                                if ((player = Bukkit.getPlayer(uUID2)) != null) {
                                    this.dialogHandler.applyPlayer(
                                            player,
                                            wrapperPlayClientCustomClickAction2.getId(),
                                            wrapperPlayClientCustomClickAction2.getPayload());
                                }
                            });
                }
            }
        }
    }
}
