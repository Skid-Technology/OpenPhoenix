package xyz.refinedev.phoenix.utils;

public final class Preconditions {

    public static void checkArgument(boolean matches) {
        if (!matches) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean matches, Object result) {
        if (!matches) {
            throw new IllegalArgumentException(String.valueOf(result));
        }
    }

    public static void checkArgument(boolean matches, String text, Object... result) {
        if (!matches) {
            throw new IllegalArgumentException(format(text, result));
        }
    }

    public static void checkState(boolean matches) {
        if (!matches) {
            throw new IllegalStateException();
        }
    }

    public static void checkState(boolean matches, Object result) {
        if (!matches) {
            throw new IllegalStateException(String.valueOf(result));
        }
    }

    public static void checkState(boolean matches, String text, Object... result) {
        if (!matches) {
            throw new IllegalStateException(format(text, result));
        }
    }

    public static <T> T checkNotNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        } else {
            return (T) t;
        }
    }

    public static <T> T checkNotNull(T t, Object result) {
        if (t == null) {
            throw new NullPointerException(String.valueOf(result));
        } else {
            return (T) t;
        }
    }

    public static <T> T checkNotNull(T t, String text, Object... result) {
        if (t == null) {
            throw new NullPointerException(format(text, result));
        } else {
            return (T) t;
        }
    }

    public static int checkElementIndex(int value, int value2) {
        return checkElementIndex(value, value2, "index");
    }

    public static int checkElementIndex(int value, int value2, String text) {
        if (value >= 0 && value < value2) {
            return value;
        } else {
            throw new IndexOutOfBoundsException(badElementIndex(value, value2, text));
        }
    }

    private static String badElementIndex(int value, int value2, String text) {
        if (value < 0) {
            return format("%s (%s) must not be negative", text, value);
        } else if (value2 < 0) {
            throw new IllegalArgumentException("negative size: " + value2);
        } else {
            return format("%s (%s) must be less than size (%s)", text, value, value2);
        }
    }

    public static int checkPositionIndex(int value, int value2) {
        return checkPositionIndex(value, value2, "index");
    }

    public static int checkPositionIndex(int value, int value2, String text) {
        if (value >= 0 && value <= value2) {
            return value;
        } else {
            throw new IndexOutOfBoundsException(badPositionIndex(value, value2, text));
        }
    }

    private static String badPositionIndex(int value, int value2, String text) {
        if (value < 0) {
            return format("%s (%s) must not be negative", text, value);
        } else if (value2 < 0) {
            throw new IllegalArgumentException("negative size: " + value2);
        } else {
            return format("%s (%s) must not be greater than size (%s)", text, value, value2);
        }
    }

    public static void checkPositionIndexes(int value, int value2, int value3) {
        if (value < 0 || value2 < value || value2 > value3) {
            throw new IndexOutOfBoundsException(badPositionIndexes(value, value2, value3));
        }
    }

    private static String badPositionIndexes(int value, int value2, int value3) {
        if (value >= 0 && value <= value3) {
            return value2 >= 0 && value2 <= value3
                    ? format("end index (%s) must not be less than start index (%s)", value2, value)
                    : badPositionIndex(value2, value3, "end index");
        } else {
            return badPositionIndex(value, value3, "start index");
        }
    }

    static String format(String text, Object... result) {
        text = String.valueOf(text);
        StringBuilder stringBuilder = new StringBuilder(text.length() + 16 * result.length);
        int value = 0;
        int value2 = 0;
        int value3;
        while (value2 < result.length && (value3 = text.indexOf("%s", value)) != -1) {
            stringBuilder.append(text.substring(value, value3));
            stringBuilder.append(result[value2++]);
            value = value3 + 2;
        }
        stringBuilder.append(text.substring(value));
        if (value2 < result.length) {
            stringBuilder.append(" [");
            stringBuilder.append(result[value2++]);
            while (value2 < result.length) {
                stringBuilder.append(", ");
                stringBuilder.append(result[value2++]);
            }
            stringBuilder.append("]");
        }
        return stringBuilder.toString();
    }

    private Preconditions() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
