package xyz.refinedev.phoenix.utils.nametags.adapter;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.rank.scopeable.IScopeable;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.nametags.providers.TeamNameProvider;

import java.util.ArrayList;
import java.util.List;

public class PhoenixTeamNameAdapter implements TeamNameProvider {

    private final CommonPlatform api;

    private static List<String> cachedAlphabet;

    @Override
    public String buildTeamName(Player player, int value) {
        if (player == null) {
            return "xxx";
        } else {
            IProfile profile;
            if ((profile = this.api.getProfileHandler().getCachedProfile(player.getUniqueId()))
                    == null) {
                return "xxx";
            } else {
                IRank highestRank = profile.getHighestRank();
                if (profile.isVanished()) {
                    String text;
                    return (text =
                                                    getLetterByWeight(getWeight(highestRank))
                                                            + "v"
                                                            + highestRank.getColor())
                                            .length()
                                    > 16
                            ? text.substring(0, 16)
                            : text;
                } else if (player.hasMetadata("ModMode")) {
                    String text2;
                    return (text2 =
                                                    getLetterByWeight(getWeight(highestRank))
                                                            + "m"
                                                            + highestRank.getColor())
                                            .length()
                                    > 16
                            ? text2.substring(0, 16)
                            : text2;
                } else {
                    String text3;
                    return (text3 =
                                                    getLetterByWeightNoExtra(getWeight(highestRank))
                                                            + highestRank.getColor())
                                            .length()
                                    > 16
                            ? text3.substring(0, 16)
                            : text3;
                }
            }
        }
    }

    public static List<String> getAlphabetPermutations() {
        if (cachedAlphabet != null) {
            return cachedAlphabet;
        }
        ArrayList<String> values = new ArrayList<>();
        for (char value = 'a'; value <= 'z'; value++) {
            for (char value2 = 'a'; value2 <= 'z'; value2++) {
                values.add("" + value + value2);
            }
        }
        cachedAlphabet = values;
        return values;
    }

    public static int getWeight(IRank rank) {
        ArrayList values;
        int value;
        if ((value =
                        (values =
                                        FastStreams.filterArrayList(
                                                Phoenix.getInstance()
                                                        .getRankHandler()
                                                        .getSortedRanksReversed(),
                                                IScopeable::isScope))
                                .indexOf(rank))
                == -1) {
            value = values.size();
        }
        return value + 1;
    }

    public static String getLetterByWeightNoExtra(int value) {
        List<String> alphabetPermutations = getAlphabetPermutations();
        int value2;
        if ((value2 = value - 1) > alphabetPermutations.size() - 1) {
            value2 = alphabetPermutations.size() - 1;
        }
        return (String) alphabetPermutations.get(value2);
    }

    public static String getLetterByWeight(int value) {
        List<String> alphabetPermutations = getAlphabetPermutations();
        int value2;
        if ((value2 = value - 1) > alphabetPermutations.size() - 1) {
            value2 = alphabetPermutations.size() - 1;
        }
        return (String) alphabetPermutations.get(value2) + "a";
    }

    public PhoenixTeamNameAdapter(CommonPlatform commonPlatform) {
        this.api = commonPlatform;
    }
}
