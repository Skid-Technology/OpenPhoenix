package xyz.refinedev.phoenix.config;

import java.util.Arrays;
import java.util.List;

public class Tip {

    private final List<String> lines;

    public static Tip of(String... text) {
        return new Tip(Arrays.asList(text));
    }

    public Tip(List<String> lines) {
        this.lines = lines;
    }

    public List<String> getLines() {
        return this.lines;
    }
}
