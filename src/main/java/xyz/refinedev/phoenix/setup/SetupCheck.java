package xyz.refinedev.phoenix.setup;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class SetupCheck {

    private final CheckStatus checkStatus;

    private final String text;

    private final String textString;

    private final List<String> strings;

    private final String text2;

    private SetupCheck(
            CheckStatus checkStatus2,
            String text,
            String textString,
            List<String> strings,
            String text2) {
        this.checkStatus = checkStatus2;
        this.text = text;
        this.textString = textString;
        this.strings = strings;
        this.text2 = text2;
    }

    public static SetupCheck of(String text, String text2) {
        return new SetupCheck(CheckStatus.OK, text, text2, Collections.emptyList(), null);
    }

    public static SetupCheck of(String text, String text2, String text3) {
        return new SetupCheck(CheckStatus.TODO, text, text2, Collections.emptyList(), text3);
    }

    public static SetupCheck ofByWARN(String text, String text2, String text3) {
        return new SetupCheck(CheckStatus.WARN, text, text2, Collections.emptyList(), text3);
    }

    public static SetupCheck ofByFAIL(String text, String text2, String text3) {
        return new SetupCheck(CheckStatus.FAIL, text, text2, Collections.emptyList(), text3);
    }

    public SetupCheck of(String... text3) {
        return new SetupCheck(
                this.checkStatus,
                this.text,
                this.textString,
                Collections.unmodifiableList(Arrays.asList(text3)),
                this.text2);
    }

    public static CheckStatus findCheckStatus(List<SetupCheck> values) {
        CheckStatus checkStatus = CheckStatus.OK;
        Iterator<xyz.refinedev.phoenix.setup.SetupCheck> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            SetupCheck setupCheck;
            if ((setupCheck = (SetupCheck) iterator2.next())
                    .getCheckStatus()
                    .isWorseThan(checkStatus)) {
                checkStatus = setupCheck.getCheckStatus();
            }
        }
        return checkStatus;
    }

    public CheckStatus getCheckStatus() {
        return this.checkStatus;
    }

    public String getText() {
        return this.text;
    }

    public String getTextString() {
        return this.textString;
    }

    public List<String> getStrings() {
        return this.strings;
    }

    public String getText2() {
        return this.text2;
    }
}
