package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.packet.impl.punishments.PunishmentLadderUpdatePacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.Proof;
import xyz.refinedev.phoenix.profile.punishment.ProofType;
import xyz.refinedev.phoenix.profile.punishment.Punishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadderType;
import xyz.refinedev.phoenix.profile.punishment.ladder.PunishmentLadder;
import xyz.refinedev.phoenix.repository.IPunishmentLadderRepository;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PunishmentLadderHandler implements IPunishmentLadderHandler {

    private final CommonPlatform platform;

    private final IPunishmentLadderRepository repository;

    public PunishmentLadderHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).punishmentLadders();
        this.repository
                .fetchAll()
                .whenCompleteAsync(
                        (result, result2) ->
                                this.repository
                                        .getCache()
                                        .addAll((Collection<? extends IPunishmentLadder>) result));
    }

    @Override
    public IPunishment createPunishment(
            UUID uUID, IProfile profile, IPunishmentLadder punishmentLadder, boolean matches) {
        List<IPunishment> punishments = profile.getPunishments();
        IPunishmentLadderType punishmentLadderType;
        long value =
                (punishmentLadderType = punishmentLadder.getNextLadder(punishments)).getType()
                                        != PunishmentType.KICK
                                && punishmentLadderType.getType() != PunishmentType.WARN
                        ? punishmentLadderType.getDuration()
                        : -5L;
        Punishment punishment;
        (punishment = new Punishment()).setPunishmentType(punishmentLadderType.getType());
        punishment.setTarget(profile.getUniqueId());
        punishment.setAddress(profile.getCurrentAddress());
        punishment.setSilent(matches);
        punishment.setPermanent(value == -5L);
        punishment.setDuration(value);
        punishment.setIp(
                punishmentLadderType.isIp()
                        || punishmentLadderType.getType() == PunishmentType.BLACKLIST);
        punishment.setShadow(punishmentLadderType.isShadow());
        punishment.setIssuedOn(GlobalConfig.SERVER.NAME);
        punishment.setIssuedAt(System.currentTimeMillis());
        punishment.setReason(punishmentLadder.getReason());
        punishment.setIssuedBy(uUID);
        IChatSnapshot chatSnapshot;
        if (punishmentLadder.isRequireChatSnapshot()
                && (chatSnapshot =
                                this.platform
                                        .getChatSnapshotHandler()
                                        .createSnapshot(uUID, profile.getUniqueId()))
                        != null) {
            punishment
                    .getProof()
                    .add(new Proof(ProofType.CHAT_SNAPSHOT, chatSnapshot.getNiceId(), uUID));
        }
        return punishment;
    }

    @Override
    public void createLadder(String text, String text2, boolean matches) {
        PunishmentLadder punishmentLadder = new PunishmentLadder(text, text2, matches);
        this.repository.save(punishmentLadder);
        this.platform
                .getPacketHandler()
                .send(new PunishmentLadderUpdatePacket(punishmentLadder, false));
    }

    @Nullable
    @Override
    public IPunishmentLadder getByName(String text) {
        Iterator<IPunishmentLadder> iterator2 = this.repository.getCache().iterator();
        while (iterator2.hasNext()) {
            IPunishmentLadder punishmentLadder;
            if ((punishmentLadder = (IPunishmentLadder) iterator2.next())
                    .getId()
                    .equalsIgnoreCase(text)) {
                return punishmentLadder;
            }
        }
        return null;
    }

    @Override
    public List<IPunishmentLadder> getSortedLadders() {
        return this.repository.getCache().stream()
                .sorted(Comparator.comparingInt(IPunishmentLadder::getPriority).reversed())
                .collect(Collectors.toList());
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public IPunishmentLadderRepository getRepository() {
        return this.repository;
    }
}
