package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.repository.IPunishmentLadderRepository;

import java.util.List;
import java.util.UUID;

public interface IPunishmentLadderHandler {

    IPunishment createPunishment(
            UUID uUID, IProfile profile, IPunishmentLadder punishmentLadder, boolean matches);

    void createLadder(String text, String text2, boolean matches);

    @Nullable
    IPunishmentLadder getByName(String text);

    List<IPunishmentLadder> getSortedLadders();

    IPunishmentLadderRepository getRepository();
}
