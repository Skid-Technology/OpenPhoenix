package xyz.refinedev.phoenix.config.provider;

import xyz.refinedev.lib.boostedyaml.block.implementation.Section;
import xyz.refinedev.lib.config.provider.TypeProvider;
import xyz.refinedev.lib.config.provider.context.LoadContext;
import xyz.refinedev.lib.config.provider.context.SaveContext;
import xyz.refinedev.phoenix.nametag.line.ConditionalLine;
import xyz.refinedev.phoenix.nametag.line.LineCondition;
import xyz.refinedev.phoenix.nametag.line.LineRenderer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NameTagLineProvider implements TypeProvider<LineRenderer> {

    public LineRenderer load(LoadContext loadContext) {
        String key = loadContext.getKey();
        Object result;
        return !isBlock(result = loadContext.getObject())
                ? new LineRenderer(
                        key,
                        result == null ? "" : String.valueOf(result),
                        1.0F,
                        LineCondition.lineCondition,
                        LineCondition.lineCondition,
                        ConditionalLine.conditionalLine)
                : new LineRenderer(
                        key,
                        string(get(result, "text"), ""),
                        (float) number(get(result, "scale"), 1.0),
                        LineCondition.of(string(get(result, "condition"), null)),
                        LineCondition.of(string(get(result, "visible-to"), null)),
                        animation(get(result, "animation")));
    }

    public Object save(SaveContext<LineRenderer> saveContext) {
        LineRenderer lineRenderer = (LineRenderer) saveContext.getObject();
        LinkedHashMap entries;
        (entries = new LinkedHashMap()).put("text", lineRenderer.getTextString());
        entries.put("scale", lineRenderer.getFloatValue());
        if (!lineRenderer.getLineCondition().getText().isEmpty()) {
            entries.put("condition", lineRenderer.getLineCondition().getText());
        }
        if (!lineRenderer.getLineConditionLineCondition().getText().isEmpty()) {
            entries.put("visible-to", lineRenderer.getLineConditionLineCondition().getText());
        }
        if (lineRenderer.getConditionalLine().isEnabled()) {
            LinkedHashMap entries2;
            (entries2 = new LinkedHashMap())
                    .put("interval", lineRenderer.getConditionalLine().getCount());
            entries2.put("frames", new ArrayList<>(lineRenderer.getConditionalLine().getStrings()));
            entries.put("animation", entries2);
        }
        return entries;
    }

    private static ConditionalLine animation(Object result) {
        if (!isBlock(result)) {
            return ConditionalLine.conditionalLine;
        }
        ArrayList<String> values = new ArrayList<>();
        Object result2;
        if ((result2 = get(result, "frames")) instanceof List) {
            for (Object result3 : (List) result2) {
                values.add(String.valueOf(result3));
            }
        }
        return values.isEmpty()
                ? ConditionalLine.conditionalLine
                : new ConditionalLine(values, (int) number(get(result, "interval"), 20.0));
    }

    private static boolean isBlock(Object result) {
        return result instanceof Map || result instanceof Section;
    }

    private static Object get(Object result, String text) {
        if (result instanceof Map) {
            return ((Map) result).get(text);
        } else {
            return result instanceof Section ? ((Section) result).get(text) : null;
        }
    }

    private static String string(Object result, String text) {
        return result == null ? text : String.valueOf(result);
    }

    private static double number(Object result, double value) {
        if (result instanceof Number) {
            return ((Number) result).doubleValue();
        }
        if (result == null) {
            return value;
        }
        try {
            return Double.parseDouble(String.valueOf(result));
        } catch (NumberFormatException numberFormatException) {
            return value;
        }
    }
}
