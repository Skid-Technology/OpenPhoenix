package xyz.refinedev.phoenix.alt;

import java.util.List;

public class ScoreResult implements IScoreResult {

    private final int score;

    private final List<IAltSignal> signals;

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public List<IAltSignal> getSignals() {
        return this.signals;
    }

    public ScoreResult(int score, List<IAltSignal> signals) {
        this.score = score;
        this.signals = signals;
    }
}
