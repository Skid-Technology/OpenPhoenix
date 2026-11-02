package xyz.refinedev.phoenix.packet.impl.punishments;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.packet.api.EntityUpdatePacket;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.repository.IPunishmentLadderRepository;

public class PunishmentLadderUpdatePacket extends EntityUpdatePacket<IPunishmentLadder> {

    public PunishmentLadderUpdatePacket(IPunishmentLadder punishmentLadder, boolean matches) {
        super(punishmentLadder, matches);
    }

    protected IPunishmentLadder find(IPunishmentLadder punishmentLadder) {
        return repository().getFromCache(punishmentLadder.getId()).orElse(null);
    }

    protected void add(IPunishmentLadder punishmentLadder) {
        repository().addToCache(punishmentLadder);
    }

    protected void remove(IPunishmentLadder punishmentLadder) {
        repository().removeFromCache(punishmentLadder);
    }

    private static IPunishmentLadderRepository repository() {
        return PlatformGetter.getInstance().getPunishmentLadderHandler().getRepository();
    }

    public PunishmentLadderUpdatePacket() {}
}
