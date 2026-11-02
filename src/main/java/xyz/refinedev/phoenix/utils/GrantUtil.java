package xyz.refinedev.phoenix.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;

public final class GrantUtil {

    @GsonExclude
    public static String defaultRankName =
            "543570351023566355535118358935553553355635113358935118357335953599355735112351213511935107358535102358035843511535523510935110355735673510535753511335106351223587357035483511635104351203552356135";

    @GsonExclude
    public static String defaultRankColor =
            "853511135573511835903553358635113357035563597358035813550351093511935883572358735100357935783550354835693552358635553577355035673510135953511435108351053567351223554357335573551358935993577351093598351213511535493585351083580357835833578351013511435543589357735753582355635";

    private static final CommonPlatform api = Phoenix.getInstance();

    public static boolean canEdit(CommandSender sender, IProfile profile) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player) sender;
            IProfile cachedProfile = api.getProfileHandler().getCachedProfile(player.getUniqueId());
            if (profile.getGeneralRank().getPriority()
                    > cachedProfile.getGeneralRank().getPriority()) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.GRANT.HIGHER_PRIORITY)
                                        .replace(
                                                "<player>", profile.getNameWithColorNoDisguise())));
                return false;
            } else {
                return true;
            }
        }
    }

    private GrantUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
