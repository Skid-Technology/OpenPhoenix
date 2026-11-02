package xyz.refinedev.phoenix.scope;

public class ScopeExpression {

    private final String text;

    private final String textString;

    private final String text2;

    private static final String CUSTOM_TEXT = "CUSTOM";

    public boolean isEnabled() {
        return CUSTOM_TEXT.equals(this.text);
    }

    public String getText() {
        return this.text;
    }

    public String getTextString() {
        return this.textString;
    }

    public String getText2() {
        return this.text2;
    }

    public ScopeExpression(String text, String textString, String text2) {
        this.text = text;
        this.textString = textString;
        this.text2 = text2;
    }
}
