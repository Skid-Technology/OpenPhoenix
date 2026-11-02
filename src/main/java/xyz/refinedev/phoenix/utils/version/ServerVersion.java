package xyz.refinedev.phoenix.utils.version;

public enum ServerVersion {
    V_1_7_2(4),
    V_1_7_4(4),
    V_1_7_5(4),
    V_1_7_6(5),
    V_1_7_7(5),
    V_1_7_8(5),
    V_1_7_9(5),
    V_1_7_10(5),
    V_1_8(47),
    V_1_8_3(47),
    V_1_8_8(47),
    V_1_9(107),
    V_1_9_1(108),
    V_1_9_2(109),
    V_1_9_4(110),
    V_1_10(210),
    V_1_10_1(210),
    V_1_10_2(210),
    V_1_11(315),
    V_1_11_2(316),
    V_1_12(335),
    V_1_12_1(338),
    V_1_12_2(340),
    V_1_13(393),
    V_1_13_1(401),
    V_1_13_2(404),
    V_1_14(477),
    V_1_14_1(480),
    V_1_14_2(485),
    V_1_14_3(490),
    V_1_14_4(498),
    V_1_15(573),
    V_1_15_1(575),
    V_1_15_2(578),
    V_1_16(735),
    V_1_16_1(736),
    V_1_16_2(751),
    V_1_16_3(753),
    V_1_16_4(754),
    V_1_16_5(754),
    V_1_17(755),
    V_1_17_1(756),
    V_1_18(757),
    V_1_18_1(757),
    V_1_18_2(758),
    V_1_19(759),
    V_1_19_1(760),
    V_1_19_2(760),
    V_1_19_3(761),
    V_1_19_4(762),
    V_1_20(763),
    V_1_20_1(763),
    V_1_20_2(764),
    V_1_20_3(765),
    V_1_20_4(765),
    V_1_20_5(766),
    V_1_20_6(766),
    V_1_21(767),
    V_1_21_1(767),
    V_1_21_2(768),
    V_1_21_3(768),
    V_1_21_4(769),
    V_1_21_5(770),
    V_1_21_6(771),
    V_1_21_7(772),
    V_1_21_8(772),
    V_1_21_9(773),
    V_1_21_10(773),
    V_1_21_11(774),
    V_26_1(775),
    V_26_1_1(775),
    V_26_1_2(775),
    V_26_2(776),
    V_26_2_1(776),
    V_26_2_2(776),
    ERROR(-1, true);

    private static final ServerVersion[] VALUES = values();

    private static final ServerVersion[] REVERSED_VALUES = values();

    private final int protocolVersion;

    private final String name;

    ServerVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
        this.name = this.name().substring(2).replace("_", ".");
    }

    ServerVersion(int protocolVersion, boolean matches) {
        this.protocolVersion = protocolVersion;
        if (matches) {
            this.name = this.name();
        } else {
            this.name = this.name().substring(2).replace("_", ".");
        }
    }

    public static ServerVersion[] reversedValues() {
        return REVERSED_VALUES;
    }

    public static ServerVersion getLatest() {
        return REVERSED_VALUES[1];
    }

    public static ServerVersion getOldest() {
        return VALUES[0];
    }

    @Deprecated
    public static ServerVersion getById(int value) {
        ServerVersion[] serverVersions = VALUES;
        int value2 = VALUES.length;
        for (int index = 0; index < value2; index++) {
            ServerVersion serverVersion;
            if ((serverVersion = serverVersions[index]).protocolVersion == value) {
                return serverVersion;
            }
        }
        return null;
    }

    public String getReleaseName() {
        return this.name;
    }

    public boolean isNewerThan(ServerVersion serverVersion) {
        return this.ordinal() > serverVersion.ordinal();
    }

    public boolean isOlderThan(ServerVersion serverVersion) {
        return this.ordinal() < serverVersion.ordinal();
    }

    public boolean isNewerThanOrEquals(ServerVersion serverVersion) {
        return this.ordinal() >= serverVersion.ordinal();
    }

    public boolean isOlderThanOrEquals(ServerVersion serverVersion) {
        return this.ordinal() <= serverVersion.ordinal();
    }

    static {
        int value = 0;
        int value2 = REVERSED_VALUES.length - 1;
        while (value2 > value) {
            ServerVersion serverVersion = REVERSED_VALUES[value2];
            REVERSED_VALUES[value2--] = REVERSED_VALUES[value];
            REVERSED_VALUES[value++] = serverVersion;
        }
    }
}
