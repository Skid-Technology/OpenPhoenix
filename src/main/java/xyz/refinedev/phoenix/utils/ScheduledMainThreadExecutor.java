package xyz.refinedev.phoenix.utils;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.general.Tasks;

public class ScheduledMainThreadExecutor extends ExecuteOnMainThread {

    @Override
    public void run(Callable callable) {
        if (Phoenix.getInstance().isApiEnabled()) {
            Tasks.run(callable::call);
        }
    }
}
