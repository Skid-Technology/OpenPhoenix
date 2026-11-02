package xyz.refinedev.phoenix.setup;

import java.util.Collections;
import java.util.List;

public final class SetupSummary {

    private final String text;

    private final List<SetupCheck> setupChecks;

    private final CheckStatus checkStatus;

    SetupSummary(String text, List<SetupCheck> values) {
        this.text = text;
        this.setupChecks = Collections.unmodifiableList(values);
        this.checkStatus = SetupCheck.findCheckStatus(values);
    }

    public boolean isEnabled() {
        return this.checkStatus == CheckStatus.OK;
    }

    public String getText() {
        return this.text;
    }

    public List<SetupCheck> getSetupChecks() {
        return this.setupChecks;
    }

    public CheckStatus getCheckStatus() {
        return this.checkStatus;
    }
}
