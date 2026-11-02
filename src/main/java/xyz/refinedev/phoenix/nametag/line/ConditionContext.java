package xyz.refinedev.phoenix.nametag.line;

public interface ConditionContext {

    boolean permission(String text);

    String rankName();

    int rankPriority();

    int priorityOf(String text);

    boolean hasPrefix();

    boolean vanished();

    boolean modMode();

    boolean self();

    boolean sameTeam();

    String placeholders(String text);
}
