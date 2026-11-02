package xyz.refinedev.phoenix.antivpn.api;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.antivpn.IAntiVPNData;

import java.util.UUID;

public interface IAntiVPNProvider {

    @Nullable
    IAntiVPNData check(UUID uUID, String text);
}
