package xyz.refinedev.phoenix.nametag.line;

public final class NameTagLine {

    private NameTagLine() {}

    public static float findFloat(int value, int value2, float value3, float value4) {
        if (value2 <= 0) {
            return value4;
        }
        int value5 = Math.max(0, Math.min(value, value2 - 1));
        return value4 + (value2 - 1 - value5) * value3;
    }

    public static float[] findFloatArray(int value, float value2, float value3) {
        if (value <= 0) {
            return new float[0];
        }
        float[] values = new float[value];
        for (int index = 0; index < value; index++) {
            values[index] = findFloat(index, value, value2, value3);
        }
        return values;
    }
}
