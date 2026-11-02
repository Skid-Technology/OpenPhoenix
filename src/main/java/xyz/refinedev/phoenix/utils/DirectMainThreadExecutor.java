package xyz.refinedev.phoenix.utils;

public class DirectMainThreadExecutor extends ExecuteOnMainThread {

    @Override
    public void run(Callable callable) {
        callable.call();
    }
}
