package xyz.refinedev.phoenix.hook;

import java.util.UUID;

public interface ICosmeticsHook {

    int getCoins(UUID uUID);

    String getColor(UUID uUID);

    String getChatColor(UUID uUID);
}
