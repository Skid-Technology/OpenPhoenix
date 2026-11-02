package xyz.refinedev.phoenix.utils.nametags.util;

import lombok.Generated;

import java.util.ArrayList;
import java.util.List;

public final class WeightUtil {

    private static List<String> cachedAlphabet;

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

    public static String getLetterByWeight(int value) {
        List<String> alphabetPermutations = getAlphabetPermutations();
        int value2 = value;
        if (value > alphabetPermutations.size() - 1) {
            value2 = alphabetPermutations.size() - 1;
        }
        return (String) alphabetPermutations.get(value2);
    }

    @Generated
    private WeightUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
