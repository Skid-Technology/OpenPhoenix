package xyz.refinedev.phoenix.repository.redis;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.disguise.ISkin;
import xyz.refinedev.phoenix.profile.disguise.Skin;
import xyz.refinedev.phoenix.utils.repository.redis.RedisListRepository;

public class DisguiseSkinRepository extends RedisListRepository<String, ISkin> {

    public DisguiseSkinRepository(CommonPlatform commonPlatform, String text) {
        super(Skin.class, text, commonPlatform, result -> result.getName().toLowerCase());
    }
}
