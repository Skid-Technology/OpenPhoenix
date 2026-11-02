package xyz.refinedev.phoenix.setup;

import xyz.refinedev.phoenix.config.SettingsConfig;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class FeatureToggle {

    private final String text;

    private final String textString;

    private final boolean enabled;

    private final Supplier<List<String>> supplier;

    private final Supplier<Boolean> supplierSupplier;

    private final Consumer<Boolean> consumer;

    public boolean isEnabledBySupplierSupplier() {
        return Boolean.TRUE.equals(this.supplierSupplier.get());
    }

    public List<String> getList() {
        return this.supplier.get();
    }

    public void applyEnabled(boolean matches) {
        this.consumer.accept(matches);
        SettingsConfig.INSTANCE.save();
    }

    public String getText() {
        return this.text;
    }

    public String getTextString() {
        return this.textString;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Supplier<List<String>> getSupplier() {
        return this.supplier;
    }

    public Supplier<Boolean> getSupplierSupplier() {
        return this.supplierSupplier;
    }

    public Consumer<Boolean> getConsumer() {
        return this.consumer;
    }

    public FeatureToggle(
            String text,
            String textString,
            boolean enabled,
            Supplier<List<String>> supplier2,
            Supplier<Boolean> supplier3,
            Consumer<Boolean> consumer2) {
        this.text = text;
        this.textString = textString;
        this.enabled = enabled;
        this.supplier = supplier2;
        this.supplierSupplier = supplier3;
        this.consumer = consumer2;
    }
}
