package xyz.refinedev.phoenix.nametag.line;

public final class LineRenderer {

    private final String text;

    private final String textString;

    private final float floatValue;

    private final LineCondition lineCondition;

    private final LineCondition lineConditionLineCondition;

    private final ConditionalLine conditionalLine;

    public LineRenderer(
            String text,
            String text3,
            float value,
            LineCondition lineCondition2,
            LineCondition lineCondition3,
            ConditionalLine conditionalLine2) {
        this.text = text;
        this.textString = text3 == null ? "" : text3;
        this.floatValue = value <= 0.0F ? 1.0F : value;
        this.lineCondition = lineCondition2 == null ? LineCondition.lineCondition : lineCondition2;
        this.lineConditionLineCondition =
                lineCondition3 == null ? LineCondition.lineCondition : lineCondition3;
        this.conditionalLine =
                conditionalLine2 == null ? ConditionalLine.conditionalLine : conditionalLine2;
    }

    public String formatString(long value) {
        return this.conditionalLine.isEmpty(value, this.textString);
    }

    public String getText() {
        return this.text;
    }

    public String getTextString() {
        return this.textString;
    }

    public float getFloatValue() {
        return this.floatValue;
    }

    public LineCondition getLineCondition() {
        return this.lineCondition;
    }

    public LineCondition getLineConditionLineCondition() {
        return this.lineConditionLineCondition;
    }

    public ConditionalLine getConditionalLine() {
        return this.conditionalLine;
    }
}
