package xyz.refinedev.phoenix.alt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AltScoreCalculator {

    private static final long timeTime = 3600000L;

    public ScoreResult create(
            AltEvidenceAccumulator altEvidenceAccumulator, AltScoringWeights altScoringWeights) {
        ArrayList<xyz.refinedev.phoenix.alt.IAltSignal> values = new ArrayList<>();
        double value = this.calculateValue(altEvidenceAccumulator, altScoringWeights, values);
        double value2 =
                this.calculateValueByTimeTime(altEvidenceAccumulator, altScoringWeights, values);
        double value3;
        if ((value3 = Math.min(altScoringWeights.getValue9(), value2)) < value2) {
            values.add(
                    new AltSignal(
                            "non-ip-cap",
                            value3 - value2,
                            "capped at "
                                    + (int) altScoringWeights.getValue9()
                                    + " - non-IP evidence alone cannot reach the enforce threshold"));
        }
        value =
                value
                        + value3
                        - this.calculateValueByValue(
                                altEvidenceAccumulator, altScoringWeights, values);
        int value4 = (int) Math.round(Math.max(0.0, Math.min(100.0, value)));
        return new ScoreResult(value4, values);
    }

    public ScoreResult findScoreResult(
            List<AddressDensity> values,
            AddressDensity addressDensity,
            AltScoringWeights altScoringWeights) {
        AltEvidenceAccumulator altEvidenceAccumulator;
        (altEvidenceAccumulator = new AltEvidenceAccumulator()).setAddressDensities(values);
        altEvidenceAccumulator.setAddressDensity(addressDensity);
        return this.create(altEvidenceAccumulator, altScoringWeights);
    }

    private double calculateValue(
            AltEvidenceAccumulator altEvidenceAccumulator,
            AltScoringWeights altScoringWeights,
            List<IAltSignal> values2) {
        List<xyz.refinedev.phoenix.alt.AddressDensity> addressDensities =
                altEvidenceAccumulator.getAddressDensities();
        AddressDensity addressDensity = altEvidenceAccumulator.getAddressDensity();
        double value = 0.0;
        Map<String, List<xyz.refinedev.phoenix.alt.AddressDensity>> entries = new LinkedHashMap();
        for (AddressDensity addressDensity2 : addressDensities) {
            entries.computeIfAbsent(addressDensity2.getTextString(), result -> new ArrayList<>())
                    .add(addressDensity2);
        }
        double value2 = Math.max(0.0, Math.min(1.0, altScoringWeights.getValue2()));
        Iterator iterator2 = entries.values().iterator();
        while (iterator2.hasNext()) {
            List<AddressDensity> values3;
            List values4 = values3 = (List) iterator2.next();
            values4.sort(
                    Comparator.<AddressDensity>comparingDouble(
                                    result2 -> this.calculateValue(result2, altScoringWeights))
                            .reversed());
            double value3 = 1.0;
            double value4 = 0.0;
            for (AddressDensity addressDensity3 : values3) {
                double value5 = this.calculateValue(addressDensity3, altScoringWeights) * value3;
                value4 += value5;
                values2.add(
                        new AltSignal(
                                "shared-address",
                                value5,
                                addressDensity3.getCount()
                                        + " accounts seen there (x"
                                        + String.format(
                                                "%.2f",
                                                this.max(addressDensity3, altScoringWeights))
                                        + ")"
                                        + (value3 < 1.0 ? " (pooled)" : "")));
                value3 *= value2;
            }
            if (value4 > altScoringWeights.getValue()) {
                values2.add(
                        new AltSignal(
                                "subnet-cap",
                                altScoringWeights.getValue() - value4,
                                "one subnet cannot outweigh a single clean address match"));
                value4 = altScoringWeights.getValue();
            }
            value += value4;
        }
        if (!addressDensities.isEmpty()) {
            int value6;
            if ((value6 = entries.size() - 1) > 0) {
                double value7 = altScoringWeights.getValueValue() * value6;
                value += value7;
                values2.add(
                        new AltSignal(
                                "multi-subnet",
                                value7,
                                entries.size() + " independent subnets shared"));
            }
        } else if (addressDensity != null) {
            double value8 =
                    altScoringWeights.getValue3()
                            * this.max(addressDensity, altScoringWeights)
                            * this.maxByValue(addressDensity, altScoringWeights);
            value += value8;
            values2.add(new AltSignal("subnet-only", value8, "same subnet, no shared address"));
        }
        return value;
    }

    private double calculateValue(
            AddressDensity addressDensity, AltScoringWeights altScoringWeights) {
        return altScoringWeights.getValue()
                * this.max(addressDensity, altScoringWeights)
                * this.maxByValue(addressDensity, altScoringWeights)
                * this.calculateValueByValue(addressDensity, altScoringWeights);
    }

    double max(int value, int value2) {
        int value3;
        return (double) (value3 = Math.max(1, value2)) / Math.max(value3, value);
    }

    private double calculateValueByTimeTime(
            AltEvidenceAccumulator altEvidenceAccumulator,
            AltScoringWeights altScoringWeights,
            List<IAltSignal> values) {
        return 0.0
                + this.calculateValue(
                        values,
                        "alternation",
                        this.calculateValue(altEvidenceAccumulator, altScoringWeights),
                        altEvidenceAccumulator.getCount() + " login handoffs")
                + this.calculateValue(
                        values,
                        "ban-appearance",
                        this.max(altEvidenceAccumulator, altScoringWeights),
                        "first login "
                                + altEvidenceAccumulator.getTime() / timeTime
                                + "h after the other account was banned")
                + this.calculateValue(
                        values,
                        "client-fingerprint",
                        this.calculateValueByValue(altEvidenceAccumulator, altScoringWeights),
                        altEvidenceAccumulator.getCount4()
                                + "/"
                                + altEvidenceAccumulator.getCount5()
                                + " client attributes"
                                + (altEvidenceAccumulator.getCount6() == 2147483647
                                        ? ""
                                        : ", shared by "
                                                + altEvidenceAccumulator.getCount6()
                                                + " accounts"))
                + this.calculateValue(
                        values,
                        "name-similarity",
                        this.calculateValueByAltEvidenceAccumulator(
                                altEvidenceAccumulator, altScoringWeights),
                        String.format(
                                "%.0f%% name similarity",
                                altEvidenceAccumulator.getValue() * 100.0))
                + this.calculateValue(
                        values,
                        "playtime-rhythm",
                        this.calculateValueByAltScoringWeights(
                                altEvidenceAccumulator, altScoringWeights),
                        String.format(
                                "%.2f play-hour correlation",
                                altEvidenceAccumulator.getValueValue()))
                + this.calculateValue(
                        values,
                        "skin",
                        this.calculateValue2(altEvidenceAccumulator, altScoringWeights),
                        "same skin, used by " + altEvidenceAccumulator.getCount7() + " accounts")
                + this.calculateValue(
                        values,
                        "latency",
                        this.calculateValue3(altEvidenceAccumulator, altScoringWeights),
                        altEvidenceAccumulator.getTimeTime() + "ms median ping difference")
                + this.calculateValue(
                        values,
                        "logout-location",
                        this.calculateValue4(altEvidenceAccumulator, altScoringWeights),
                        altEvidenceAccumulator.getCount8()
                                + " shared logout areas, used by "
                                + altEvidenceAccumulator.getCount9()
                                + " accounts");
    }

    private double calculateValue(
            List<IAltSignal> values, String text, double value, String text2) {
        if (value <= 0.0) {
            return 0.0;
        }
        if (value >= 0.05) {
            values.add(new AltSignal(text, value, text2));
        }
        return value;
    }

    double calculateValue(
            AltEvidenceAccumulator altEvidenceAccumulator, AltScoringWeights altScoringWeights) {
        if (altEvidenceAccumulator.getCount() <= 0) {
            return 0.0;
        }
        int value = Math.max(1, altScoringWeights.getCount2());
        double value2 =
                altScoringWeights.getValue10()
                        * Math.min(1.0, (double) altEvidenceAccumulator.getCount() / value);
        int value3;
        if ((value3 =
                        Math.min(
                                altEvidenceAccumulator.getCountCount(),
                                altEvidenceAccumulator.getCount2()))
                > 0) {
            double value4 = (double) altEvidenceAccumulator.getCount() / value3;
            value2 *= Math.min(1.0, value4 / Math.max(0.01, altScoringWeights.getValue11()));
        }
        return value2;
    }

    double max(AltEvidenceAccumulator altEvidenceAccumulator, AltScoringWeights altScoringWeights) {
        long value;
        if ((value = altEvidenceAccumulator.getTime()) < 0L) {
            return 0.0;
        }
        long value2 = Math.max(1L, altScoringWeights.getTime4());
        return value >= value2
                ? 0.0
                : altScoringWeights.getValue12() * (1.0 - (double) value / value2);
    }

    double calculateValueByValue(
            AltEvidenceAccumulator altEvidenceAccumulator,
            AltScoringWeights altScoringWeights,
            List<IAltSignal> values) {
        if (altEvidenceAccumulator.getCount3() <= 0) {
            return 0.0;
        } else if (altScoringWeights.isEnabled()
                && altEvidenceAccumulator.getAddressDensities() != null
                && !altEvidenceAccumulator.getAddressDensities().isEmpty()) {
            values.add(
                    new AltSignal(
                            "concurrent-sessions",
                            0.0,
                            altEvidenceAccumulator.getCount3()
                                    + " overlapping sessions - not penalised, both are on a shared address"));
            return 0.0;
        } else {
            int value = Math.max(1, altScoringWeights.getCount3());
            double value2 =
                    altScoringWeights.getValue13()
                            * Math.min(1.0, (double) altEvidenceAccumulator.getCount3() / value);
            values.add(
                    new AltSignal(
                            "concurrent-sessions",
                            -value2,
                            altEvidenceAccumulator.getCount3()
                                    + " overlapping sessions - suggests two people"));
            return value2;
        }
    }

    double calculateValueByValue(
            AltEvidenceAccumulator altEvidenceAccumulator, AltScoringWeights altScoringWeights) {
        if (altEvidenceAccumulator.getCount5() <= 0) {
            return 0.0;
        }
        if (altEvidenceAccumulator.getCount4() <= 0) {
            return 0.0;
        }
        double value =
                (double) altEvidenceAccumulator.getCount4() / altEvidenceAccumulator.getCount5();
        return altScoringWeights.getValue14()
                * value
                * this.max(altEvidenceAccumulator.getCount6(), altScoringWeights.getCount4());
    }

    double calculateValueByAltEvidenceAccumulator(
            AltEvidenceAccumulator altEvidenceAccumulator, AltScoringWeights altScoringWeights) {
        return this.calculateValue(
                        altEvidenceAccumulator.getValue(), altScoringWeights.getValue16())
                * altScoringWeights.getValue15();
    }

    double calculateValueByAltScoringWeights(
            AltEvidenceAccumulator altEvidenceAccumulator, AltScoringWeights altScoringWeights) {
        return this.calculateValue(
                        altEvidenceAccumulator.getValueValue(), altScoringWeights.getValue18())
                * altScoringWeights.getValue17();
    }

    double calculateValue2(
            AltEvidenceAccumulator altEvidenceAccumulator, AltScoringWeights altScoringWeights) {
        return !altEvidenceAccumulator.isEnabled()
                ? 0.0
                : altScoringWeights.getValue19()
                        * this.max(
                                altEvidenceAccumulator.getCount7(), altScoringWeights.getCount5());
    }

    double calculateValue3(
            AltEvidenceAccumulator altEvidenceAccumulator, AltScoringWeights altScoringWeights) {
        if (!altEvidenceAccumulator.isEnabledEnabled()) {
            return 0.0;
        }
        long value = Math.max(1L, altScoringWeights.getTime5());
        double value2 = 1.0 - (double) Math.abs(altEvidenceAccumulator.getTimeTime()) / value;
        return altScoringWeights.getValue20() * Math.max(0.0, value2);
    }

    double calculateValue4(
            AltEvidenceAccumulator altEvidenceAccumulator, AltScoringWeights altScoringWeights) {
        if (altEvidenceAccumulator.getCount8() <= 0) {
            return 0.0;
        }
        int value = Math.max(1, altScoringWeights.getCount7());
        double value2 = Math.min(1.0, (double) altEvidenceAccumulator.getCount8() / value);
        return altScoringWeights.getValue21()
                * value2
                * this.max(altEvidenceAccumulator.getCount9(), altScoringWeights.getCount6());
    }

    private double calculateValue(double value, double value2) {
        if (value <= value2) {
            return 0.0;
        } else {
            return value2 >= 1.0 ? 0.0 : (value - value2) / (1.0 - value2);
        }
    }

    double max(AddressDensity addressDensity, AltScoringWeights altScoringWeights) {
        if (addressDensity.getAddressClassification() == AddressClassification.FORCED_TRUSTED) {
            return 1.0;
        }
        if (addressDensity.getAddressClassification() == AddressClassification.FORCED_SHARED) {
            return altScoringWeights.getValue6();
        }
        int value;
        double value2 =
                (double) (value = Math.max(1, altScoringWeights.getCount()))
                        / Math.max(value, addressDensity.getCount());
        if (addressDensity.isEnabled()) {
            value2 *= altScoringWeights.getValue4();
        }
        if (addressDensity.isEnabledEnabled()) {
            value2 *= altScoringWeights.getValue5();
        }
        return value2;
    }

    double maxByValue(AddressDensity addressDensity, AltScoringWeights altScoringWeights) {
        long value = Math.max(0L, addressDensity.getTime());
        long time = altScoringWeights.getTime();
        long timeTime = altScoringWeights.getTimeTime();
        if (value <= time) {
            return 1.0;
        } else if (value < timeTime && timeTime > time) {
            double value2 = (double) (value - time) / (timeTime - time);
            return 1.0 - value2 * (1.0 - altScoringWeights.getValue7());
        } else {
            return altScoringWeights.getValue7();
        }
    }

    double calculateValueByValue(
            AddressDensity addressDensity, AltScoringWeights altScoringWeights) {
        int value = Math.min(addressDensity.getCountCount(), addressDensity.getCount2());
        int value2 = Math.max(1, altScoringWeights.getCountCount());
        double value3 = Math.min(1.0, (double) value / value2);
        return 1.0 - (1.0 - value3) * (1.0 - this.max(addressDensity, altScoringWeights));
    }
}
