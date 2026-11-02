package xyz.refinedev.phoenix.config.provider;

import xyz.refinedev.lib.config.provider.TypeProvider;
import xyz.refinedev.lib.config.provider.context.LoadContext;
import xyz.refinedev.lib.config.provider.context.SaveContext;
import xyz.refinedev.phoenix.config.ConfigLocation;

import java.util.ArrayList;
import java.util.function.Function;

public class LocationProvider implements TypeProvider<ConfigLocation> {

    private static final String DEFAULT_WORLD = "world";

    public ConfigLocation load(LoadContext loadContext) {
        String[] texts;
        if ((texts = String.valueOf(loadContext.getObject()).split(";")).length >= 3
                && texts.length <= 6) {
            int value = 0;
            String text = "world";
            if (texts.length % 2 == 0) {
                text = texts[value++];
            }
            double value2 = this.parse(texts[value++], Double::parseDouble, "x");
            double value3 = this.parse(texts[value++], Double::parseDouble, "y");
            double value4 = this.parse(texts[value++], Double::parseDouble, "z");
            float value5 = 0.0F;
            float value6 = 0.0F;
            if (texts.length >= 5) {
                value5 = this.parse(texts[value++], Float::parseFloat, "yaw");
                value6 = this.parse(texts[value], Float::parseFloat, "pitch");
            }
            return new ConfigLocation(text, value2, value3, value4, value5, value6);
        } else {
            throw new IllegalArgumentException(
                    "Location must consist of (world);x;y;z;(yaw);(pitch).");
        }
    }

    public Object save(SaveContext<ConfigLocation> saveContext) {
        ArrayList<String> values = new ArrayList<>();
        ConfigLocation configLocation;
        String text;
        if (!(text =
                        (configLocation = (ConfigLocation) saveContext.getObject()).getWorldName()
                                        == null
                                ? "world"
                                : configLocation.getWorldName())
                .equals("world")) {
            values.add(text);
        }
        values.add(this.format(configLocation.getX()));
        values.add(this.format(configLocation.getY()));
        values.add(this.format(configLocation.getZ()));
        if (configLocation.getYaw() != 0.0F || configLocation.getPitch() != 0.0F) {
            values.add(this.format(configLocation.getYaw()));
            values.add(this.format(configLocation.getPitch()));
        }
        return String.join(";", values);
    }

    private String format(double value) {
        return String.valueOf(Math.round(value * 100.0) / 100.0);
    }

    private <T extends Number> T parse(String text, Function<String, T> function, String text2) {
        try {
            return (T) function.apply(text);
        } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException("The " + text2 + " value is not a valid number.");
        }
    }
}
