package xyz.refinedev.phoenix.task;

import xyz.refinedev.phoenix.CommonPlatform;

public class ServerHeartbeatTask implements Runnable {

    private final CommonPlatform platform;

    @Override
    public void run() {
        this.platform.getChatLogHandler().saveLogsToDatabase();
    }

    public ServerHeartbeatTask(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
