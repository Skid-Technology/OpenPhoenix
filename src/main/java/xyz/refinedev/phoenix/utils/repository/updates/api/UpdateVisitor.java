package xyz.refinedev.phoenix.utils.repository.updates.api;

import java.util.Map;

public interface UpdateVisitor<R> {

    R visitSet(String text, Object result);

    R visitSetFields(Map<String, String> entries);
}
