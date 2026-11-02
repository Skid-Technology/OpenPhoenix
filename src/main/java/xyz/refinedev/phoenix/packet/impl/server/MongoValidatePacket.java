package xyz.refinedev.phoenix.packet.impl.server;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;
import xyz.refinedev.phoenix.packet.api.Packet;

public class MongoValidatePacket implements Packet {

    private static final String PX_VALIDATE_MONGO = "px-validate-mongo";

    @Override
    public void onReceive() {
        String text =
                PlatformGetter.getInstance()
                        .getHashHandler()
                        .hash(
                                GlobalConfig.DATABASE.MONGO.URI_MODE
                                        ? GlobalConfig.DATABASE.MONGO.MONGO_URI
                                                + ":"
                                                + GlobalConfig.DATABASE.MONGO.DATABASE
                                        : GlobalConfig.DATABASE.MONGO.ADDRESS
                                                + ":"
                                                + GlobalConfig.DATABASE.MONGO.PORT
                                                + ":"
                                                + GlobalConfig.DATABASE.MONGO.DATABASE
                                                + ":"
                                                + GlobalConfig.DATABASE.MONGO.AUTHENTICATION
                                                + ":"
                                                + GlobalConfig.DATABASE.MONGO.USERNAME
                                                + ":"
                                                + GlobalConfig.DATABASE.MONGO.PASSWORD);
        IDatabaseHandler databaseHandler = PlatformGetter.getInstance().getDatabaseHandler();
        databaseHandler.runRedisCommand(
                result -> result.hset(PX_VALIDATE_MONGO, GlobalConfig.SERVER.NAME, text));
    }
}
