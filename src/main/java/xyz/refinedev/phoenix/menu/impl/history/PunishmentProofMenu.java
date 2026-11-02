package xyz.refinedev.phoenix.menu.impl.history;

import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.packet.impl.punishments.PunishmentsUpdatePacket;
import xyz.refinedev.phoenix.profile.punishment.IProof;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.Proof;
import xyz.refinedev.phoenix.profile.punishment.ProofType;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.GsonExclude;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.reflection.Reflection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PunishmentProofMenu extends PaginatedMenu {

    @GsonExclude public static String text;

    private final IPunishment punishment;

    private static final String title = "Proof";

    @Override
    public String getTitle(Player player) {
        return title;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 3);
        entries.put(5, new PunishmentProofMenu.AddProofButton());
        return entries;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (IProof proof : this.punishment.getProof()) {
            entries.put(entries.size(), new PunishmentProofMenu.EntryButton(proof));
        }
        return entries;
    }

    public void applyProofType(ProofType proofType, Player player) {
        Phoenix phoenix = super.plugin;
        String text = LocaleUtil.tr(player, MessageConfig.PROOF.PROOF);
        ConversationUtil.ask(
                phoenix,
                player,
                text,
                result -> {
                    Tasks.run(
                            () -> {
                                this.punishment
                                        .getProof()
                                        .add(new Proof(proofType, result, player.getUniqueId()));
                                CompletableFuture<Boolean> completableFuture =
                                        super.api
                                                .getPunishmentHandler()
                                                .getRepository()
                                                .save(this.punishment.getUuid(), this.punishment);
                                completableFuture.whenCompleteAsync(
                                        (result2, result3) ->
                                                super.api
                                                        .getPacketHandler()
                                                        .sendSync(
                                                                new PunishmentsUpdatePacket(
                                                                        this.punishment
                                                                                .getTarget())));
                                player.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                        player, MessageConfig.PROOF.PROOF_ADDED)));
                            });
                });
    }

    public PunishmentProofMenu(IPunishment punishment2) {
        this.punishment = punishment2;
    }

    class AddProofButton extends Button {

        private AddProofButton() {}

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.PLAYER_HEAD.get(),
                                    1,
                                    (short) SkullType.PLAYER.ordinal()))
                    .setName("&a&lAdd Proof");
            return Reflection.itemWithBase64(
                    itemBuilder.toItemStack(),
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            Phoenix phoenix = PunishmentProofMenu.super.plugin;
            String text = LocaleUtil.tr(player, MessageConfig.PROOF.TYPE);
            ConversationUtil.ask(
                    phoenix,
                    player,
                    text,
                    result -> {
                        ProofType proofType;
                        if ((proofType = ProofType.getByName(result.toUpperCase())) == null) {
                            player.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(player, MessageConfig.PROOF.CANCELLED)));
                        } else {
                            ProofType proofType2 = proofType;
                            Tasks.run(
                                    () ->
                                            PunishmentProofMenu.this.applyProofType(
                                                    proofType2, player));
                        }
                    });
        }
    }

    class EntryButton extends Button {

        private final IProof proof;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PAPER.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.PROOF_MENU.ITEM_NAME)
                                    .replace("<type>", this.proof.getType().getNiceName()));
            for (String text : LocaleUtil.tr(player, MenuConfig.PROOF_MENU.ITEM_LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<proof>", this.proof.getProof())
                                .replace("<addedBy>", this.proof.getNiceAddedBy()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            switch (this.proof.getType()) {
                case CHAT_SNAPSHOT:
                    player.performCommand("chatsnapshot view " + this.proof.getProof());
                    break;
                case LINK:
                    Clickable clickable;
                    (clickable = new Clickable())
                            .add("&a" + this.proof.getProof(), "", "", this.proof.getProof());
                    clickable.sendToPlayer(player);
            }
        }

        public EntryButton(IProof proof2) {
            this.proof = proof2;
        }
    }
}
