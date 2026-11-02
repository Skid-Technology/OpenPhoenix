package xyz.refinedev.phoenix.report;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;

import java.util.List;

public interface IReportCategory {

    String getId();

    String getDisplayName();

    void setDisplayName(String text);

    List<String> getDescription();

    void setDescription(List<String> values);

    String getPunishmentLadderId();

    void setPunishmentLadderId(String text);

    String getMaterialName();

    void setMaterialName(String text);

    @Nullable
    IPunishmentLadder getPunishmentLadder();
}
