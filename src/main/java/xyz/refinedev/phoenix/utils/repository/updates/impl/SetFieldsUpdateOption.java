package xyz.refinedev.phoenix.utils.repository.updates.impl;

import xyz.refinedev.phoenix.utils.repository.updates.api.UpdateOptions;
import xyz.refinedev.phoenix.utils.repository.updates.api.UpdateVisitor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SetFieldsUpdateOption implements UpdateOptions {

    private final Map<String, String> fieldJson;

    private static final String CANNOT_BUILD_AN_UPDATE_WITH_NO_FIELDS_TEXT =
            "Cannot build an update with no fields";

    public SetFieldsUpdateOption(Map<String, String> entries) {
        if (entries != null && !entries.isEmpty()) {
            this.fieldJson = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
        } else {
            throw new IllegalArgumentException(CANNOT_BUILD_AN_UPDATE_WITH_NO_FIELDS_TEXT);
        }
    }

    @Override
    public <R> R accept(UpdateVisitor<R> updateVisitor) {
        return (R) updateVisitor.visitSetFields(this.fieldJson);
    }

    public Map<String, String> getFieldJson() {
        return this.fieldJson;
    }
}
