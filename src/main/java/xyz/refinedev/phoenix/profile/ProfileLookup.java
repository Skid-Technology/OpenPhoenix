package xyz.refinedev.phoenix.profile;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.utils.ExecuteOnMainThread;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ProfileLookup {

    private static final AtomicBoolean enabled = new AtomicBoolean();

    private ProfileLookup() {}

    public static void initialize() {
        CommonPlatform commonPlatform;
        Collection<xyz.refinedev.phoenix.profile.IProfile> values2;
        if (!(values2 =
                        (commonPlatform = PlatformGetter.getInstance())
                                .getProfileHandler()
                                .getRepository()
                                .getOnlineCache()
                                .values())
                .stream().allMatch(result -> result instanceof ConsoleProfile)) {
            if (enabled.compareAndSet(false, true)) {
                ExecuteOnMainThread executeOnMainThread = commonPlatform.getExecuteOnMainThread();
                Collection values3 = values2;
                executeOnMainThread.run(
                        () -> {
                            enabled.set(false);
                            Iterator iterator2 = values3.iterator();
                            while (iterator2.hasNext()) {
                                ((IProfile) iterator2.next()).updatePermissions();
                            }
                        });
            }
        }
    }
}
