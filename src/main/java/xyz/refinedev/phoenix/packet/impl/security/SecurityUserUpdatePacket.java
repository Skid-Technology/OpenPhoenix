package xyz.refinedev.phoenix.packet.impl.security;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.packet.api.EntityUpdatePacket;
import xyz.refinedev.phoenix.profile.security.ISecurityUser;
import xyz.refinedev.phoenix.repository.ISecurityRepository;

public class SecurityUserUpdatePacket extends EntityUpdatePacket<ISecurityUser> {

    public SecurityUserUpdatePacket(ISecurityUser securityUser, boolean matches) {
        super(securityUser, matches);
    }

    protected ISecurityUser find(ISecurityUser securityUser) {
        return repository().getFromCache(securityUser.getUuid()).orElse(null);
    }

    protected void add(ISecurityUser securityUser) {
        repository().addToCache(securityUser.getUuid(), securityUser);
    }

    protected void remove(ISecurityUser securityUser) {
        repository().removeFromCache(securityUser.getUuid());
    }

    private static ISecurityRepository repository() {
        return PlatformGetter.getInstance().getSecurityHandler().getRepository();
    }

    public SecurityUserUpdatePacket() {}
}
