package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.alt.AltLinkStatus;
import xyz.refinedev.phoenix.alt.IAltLink;
import xyz.refinedev.phoenix.alt.IScoreResult;
import xyz.refinedev.phoenix.repository.IAltLinkRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IAltHandler {

    IAltLinkRepository getRepository();

    IScoreResult evaluate(UUID uUID, UUID uUID2);

    List<IAltLink> refreshLinks(UUID uUID);

    List<IAltLink> getLinks(UUID uUID);

    Set<UUID> altsAtOrAbove(UUID uUID, int value);

    IAltLink setStatus(UUID uUID, UUID uUID2, AltLinkStatus altLinkStatus);
}
