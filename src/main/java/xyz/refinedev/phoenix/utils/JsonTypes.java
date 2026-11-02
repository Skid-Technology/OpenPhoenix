package xyz.refinedev.phoenix.utils;

import com.google.gson.reflect.TypeToken;

import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.rank.IRank;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class JsonTypes {

    public static final Type type = (new TypeToken<Set<UUID>>() {}).getType();

    public static final Type typeType = (new TypeToken<List<IRank>>() {}).getType();

    public static final Type type2 = (new TypeToken<List<ITag>>() {}).getType();

    public static final Type type3 = (new TypeToken<List<IFilter>>() {}).getType();

    public static final Type type4 = (new TypeToken<List<IPunishmentLadder>>() {}).getType();

    public static final Type type5 = (new TypeToken<Map<String, String>>() {}).getType();
}
