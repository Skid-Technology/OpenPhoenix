package me.creaxx.api.particle;

import com.google.common.base.Preconditions;

import me.creaxx.api.utils.VersionUtil;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import xyz.refinedev.lib.particlenativeapi.api.ParticleNativeAPI;
import xyz.refinedev.lib.particlenativeapi.api.particle.type.ParticleTypeMotion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public enum ParticleEffect {
    EXPLOSION_NORMAL("explode", 0, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    EXPLOSION_LARGE("largeexplode", 1, -1),
    EXPLOSION_HUGE("hugeexplosion", 2, -1),
    FIREWORKS_SPARK("fireworksSpark", 3, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    WATER_BUBBLE(
            "bubble",
            4,
            -1,
            ParticleEffect.ParticleProperty.DIRECTIONAL,
            ParticleEffect.ParticleProperty.REQUIRES_WATER),
    WATER_SPLASH("splash", 5, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    WATER_WAKE("wake", 6, 7, ParticleEffect.ParticleProperty.DIRECTIONAL),
    SUSPENDED("suspended", 7, -1, ParticleEffect.ParticleProperty.REQUIRES_WATER),
    SUSPENDED_DEPTH("depthSuspend", 8, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    CRIT("crit", 9, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    CRIT_MAGIC("magicCrit", 10, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    SMOKE_NORMAL("smoke", 11, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    SMOKE_LARGE("largesmoke", 12, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    SPELL("spell", 13, -1),
    SPELL_INSTANT("instantSpell", 14, -1),
    SPELL_MOB("mobSpell", 15, -1, ParticleEffect.ParticleProperty.COLORABLE),
    SPELL_MOB_AMBIENT("mobSpellAmbient", 16, -1, ParticleEffect.ParticleProperty.COLORABLE),
    SPELL_WITCH("witchMagic", 17, -1),
    DRIP_WATER("dripWater", 18, -1),
    DRIP_LAVA("dripLava", 19, -1),
    VILLAGER_ANGRY("angryVillager", 20, -1),
    VILLAGER_HAPPY("happyVillager", 21, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    TOWN_AURA("townaura", 22, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    NOTE("note", 23, -1, ParticleEffect.ParticleProperty.COLORABLE),
    PORTAL("portal", 24, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    ENCHANTMENT_TABLE("enchantmenttable", 25, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    FLAME("flame", 26, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    LAVA("lava", 27, -1),
    FOOTSTEP("footstep", 28, -1),
    CLOUD("cloud", 29, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    REDSTONE("reddust", 30, -1, ParticleEffect.ParticleProperty.COLORABLE),
    SNOWBALL("snowballpoof", 31, -1),
    SNOW_SHOVEL("snowshovel", 32, -1, ParticleEffect.ParticleProperty.DIRECTIONAL),
    SLIME("slime", 33, -1),
    HEART("heart", 34, -1),
    BARRIER("barrier", 35, 8),
    ITEM_CRACK(
            "iconcrack",
            36,
            -1,
            ParticleEffect.ParticleProperty.DIRECTIONAL,
            ParticleEffect.ParticleProperty.REQUIRES_DATA),
    BLOCK_CRACK("blockcrack", 37, -1, ParticleEffect.ParticleProperty.REQUIRES_DATA),
    BLOCK_DUST(
            "blockdust",
            38,
            7,
            ParticleEffect.ParticleProperty.DIRECTIONAL,
            ParticleEffect.ParticleProperty.REQUIRES_DATA),
    WATER_DROP("droplet", 39, 8),
    ITEM_TAKE("take", 40, 8),
    MOB_APPEARANCE("mobappearance", 41, 8);

    private static final Map<String, ParticleEffect> NAME_MAP = new HashMap<>();

    private static final Map<Integer, ParticleEffect> ID_MAP = new HashMap<>();

    private final String name;

    private final int id;

    private final int requiredVersion;

    private final List<ParticleEffect.ParticleProperty> properties;

    ParticleEffect(
            String nullxx,
            int nullxxx,
            int nullxxxx,
            ParticleEffect.ParticleProperty... nullxxxxx) {
        this.name = nullxx;
        this.id = nullxxx;
        this.requiredVersion = nullxxxx;
        this.properties = Arrays.asList(nullxxxxx);
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public int getRequiredVersion() {
        return this.requiredVersion;
    }

    public boolean hasProperty(ParticleEffect.ParticleProperty particleProperty) {
        return this.properties.contains(particleProperty);
    }

    public static ParticleEffect fromName(String text) {
        Iterator<Entry<String, me.creaxx.api.particle.ParticleEffect>> iterator2 =
                NAME_MAP.entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            if (((String) (entry = (Entry) iterator2.next()).getKey()).equalsIgnoreCase(text)) {
                return (ParticleEffect) entry.getValue();
            }
        }
        return null;
    }

    public static ParticleEffect fromId(int value) {
        Iterator<Entry<Integer, me.creaxx.api.particle.ParticleEffect>> iterator2 =
                ID_MAP.entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            if ((Integer) (entry = (Entry) iterator2.next()).getKey() == value) {
                return (ParticleEffect) entry.getValue();
            }
        }
        return null;
    }

    private static boolean isWater(Location location) {
        Material material;
        return (material = location.getBlock().getType()) == Material.WATER
                || material.name().contains("STATIONARY_WATER");
    }

    private static boolean isLongDistance(Location location, List<Player> values) {
        String name = location.getWorld().getName();
        Iterator<Player> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            Location location2 = ((Player) iterator2.next()).getLocation();
            if (name.equals(location2.getWorld().getName())
                    && !(location2.distanceSquared(location) < 65536.0)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDataCorrect(
            ParticleEffect particleEffect, ParticleEffect.ParticleData particleData) {
        return (particleEffect == BLOCK_CRACK || particleEffect == BLOCK_DUST)
                        && particleData instanceof ParticleEffect.BlockData
                || particleEffect == ITEM_CRACK && particleData instanceof ParticleEffect.ItemData;
    }

    private static boolean isColorCorrect(
            ParticleEffect particleEffect, ParticleEffect.ParticleColor particleColor) {
        return (particleEffect == SPELL_MOB
                                || particleEffect == SPELL_MOB_AMBIENT
                                || particleEffect == REDSTONE)
                        && particleColor instanceof ParticleEffect.OrdinaryColor
                || particleEffect == NOTE && particleColor instanceof ParticleEffect.NoteColor;
    }

    public ParticleEffect.ParticlePacket display(
            float value,
            float value2,
            float value3,
            float value4,
            int value5,
            Location location,
            double value6)
            throws ParticleEffect.ParticleVersionException,
                    ParticleEffect.ParticleDataException,
                    IllegalArgumentException {
        if (this.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleEffect.ParticleDataException(
                    "This particle effect requires additional data");
        } else if (this.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_WATER)
                && !isWater(location)) {
            throw new IllegalArgumentException("There is no water at the center location");
        } else {
            return new ParticleEffect.ParticlePacket(
                    this, value, value2, value3, value4, value5, value6 > 256.0, null);
        }
    }

    public ParticleEffect.ParticlePacket display(
            float value,
            float value2,
            float value3,
            float value4,
            int value5,
            Location location,
            List<Player> values)
            throws ParticleEffect.ParticleVersionException,
                    ParticleEffect.ParticleDataException,
                    IllegalArgumentException {
        if (this.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleEffect.ParticleDataException(
                    "This particle effect requires additional data");
        } else if (this.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_WATER)
                && !isWater(location)) {
            throw new IllegalArgumentException("There is no water at the center location");
        } else {
            return new ParticleEffect.ParticlePacket(
                    this,
                    value,
                    value2,
                    value3,
                    value4,
                    value5,
                    isLongDistance(location, values),
                    null);
        }
    }

    public ParticleEffect.ParticlePacket display(
            float value,
            float value2,
            float value3,
            float value4,
            int value5,
            Location location,
            Player... player)
            throws ParticleEffect.ParticleVersionException,
                    ParticleEffect.ParticleDataException,
                    IllegalArgumentException {
        return this.display(value, value2, value3, value4, value5, location, Arrays.asList(player));
    }

    public ParticleEffect.ParticlePacket display(
            Vector vector, float value, Location location, double value2)
            throws ParticleEffect.ParticleVersionException,
                    ParticleEffect.ParticleDataException,
                    IllegalArgumentException {
        if (this.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleEffect.ParticleDataException(
                    "This particle effect requires additional data");
        } else if (!this.hasProperty(ParticleEffect.ParticleProperty.DIRECTIONAL)) {
            throw new IllegalArgumentException("This particle effect is not directional");
        } else if (this.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_WATER)
                && !isWater(location)) {
            throw new IllegalArgumentException("There is no water at the center location");
        } else {
            return new ParticleEffect.ParticlePacket(this, vector, value, value2 > 256.0, null);
        }
    }

    public ParticleEffect.ParticlePacket display(
            Vector vector, float value, Location location, List<Player> values)
            throws ParticleEffect.ParticleVersionException,
                    ParticleEffect.ParticleDataException,
                    IllegalArgumentException {
        if (this.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleEffect.ParticleDataException(
                    "This particle effect requires additional data");
        } else if (!this.hasProperty(ParticleEffect.ParticleProperty.DIRECTIONAL)) {
            throw new IllegalArgumentException("This particle effect is not directional");
        } else if (this.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_WATER)
                && !isWater(location)) {
            throw new IllegalArgumentException("There is no water at the center location");
        } else {
            return new ParticleEffect.ParticlePacket(
                    this, vector, value, isLongDistance(location, values), null);
        }
    }

    public ParticleEffect.ParticlePacket display(
            Vector vector, float value, Location location, Player... player)
            throws ParticleEffect.ParticleVersionException,
                    ParticleEffect.ParticleDataException,
                    IllegalArgumentException {
        return this.display(vector, value, location, Arrays.asList(player));
    }

    public ParticleEffect.ParticlePacket display(
            ParticleEffect.ParticleColor particleColor, Location location, double value)
            throws ParticleEffect.ParticleVersionException, ParticleEffect.ParticleColorException {
        if (!this.hasProperty(ParticleEffect.ParticleProperty.COLORABLE)) {
            throw new ParticleEffect.ParticleColorException(
                    "This particle effect is not colorable");
        } else if (!isColorCorrect(this, particleColor)) {
            throw new ParticleEffect.ParticleColorException("The particle color type is incorrect");
        } else {
            return new ParticleEffect.ParticlePacket(this, particleColor, value > 256.0);
        }
    }

    public ParticleEffect.ParticlePacket display(
            ParticleEffect.ParticleColor particleColor, Location location, List<Player> values)
            throws ParticleEffect.ParticleVersionException, ParticleEffect.ParticleColorException {
        if (!this.hasProperty(ParticleEffect.ParticleProperty.COLORABLE)) {
            throw new ParticleEffect.ParticleColorException(
                    "This particle effect is not colorable");
        } else if (!isColorCorrect(this, particleColor)) {
            throw new ParticleEffect.ParticleColorException("The particle color type is incorrect");
        } else {
            return new ParticleEffect.ParticlePacket(
                    this, particleColor, isLongDistance(location, values));
        }
    }

    public ParticleEffect.ParticlePacket display(
            ParticleEffect.ParticleColor particleColor, Location location, Player... player)
            throws ParticleEffect.ParticleVersionException, ParticleEffect.ParticleColorException {
        return this.display(particleColor, location, Arrays.asList(player));
    }

    public ParticleEffect.ParticlePacket display(
            ParticleEffect.ParticleData particleData,
            float value,
            float value2,
            float value3,
            float value4,
            int value5,
            Location location,
            double value6)
            throws ParticleEffect.ParticleVersionException, ParticleEffect.ParticleDataException {
        if (!this.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleEffect.ParticleDataException(
                    "This particle effect does not require additional data");
        } else if (!isDataCorrect(this, particleData)) {
            throw new ParticleEffect.ParticleDataException("The particle data type is incorrect");
        } else {
            return new ParticleEffect.ParticlePacket(
                    this, value, value2, value3, value4, value5, value6 > 256.0, particleData);
        }
    }

    public ParticleEffect.ParticlePacket display(
            ParticleEffect.ParticleData particleData,
            float value,
            float value2,
            float value3,
            float value4,
            int value5,
            Location location,
            List<Player> values)
            throws ParticleEffect.ParticleVersionException, ParticleEffect.ParticleDataException {
        if (!this.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleEffect.ParticleDataException(
                    "This particle effect does not require additional data");
        } else if (!isDataCorrect(this, particleData)) {
            throw new ParticleEffect.ParticleDataException("The particle data type is incorrect");
        } else {
            return new ParticleEffect.ParticlePacket(
                    this,
                    value,
                    value2,
                    value3,
                    value4,
                    value5,
                    isLongDistance(location, values),
                    particleData);
        }
    }

    public ParticleEffect.ParticlePacket display(
            ParticleEffect.ParticleData particleData,
            float value,
            float value2,
            float value3,
            float value4,
            int value5,
            Location location,
            Player... player)
            throws ParticleEffect.ParticleVersionException, ParticleEffect.ParticleDataException {
        return this.display(
                particleData,
                value,
                value2,
                value3,
                value4,
                value5,
                location,
                Arrays.asList(player));
    }

    public ParticleEffect.ParticlePacket display(
            ParticleEffect.ParticleData particleData,
            Vector vector,
            float value,
            Location location,
            double value2)
            throws ParticleEffect.ParticleVersionException, ParticleEffect.ParticleDataException {
        if (!this.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleEffect.ParticleDataException(
                    "This particle effect does not require additional data");
        } else if (!isDataCorrect(this, particleData)) {
            throw new ParticleEffect.ParticleDataException("The particle data type is incorrect");
        } else {
            return new ParticleEffect.ParticlePacket(
                    this, vector, value, value2 > 256.0, particleData);
        }
    }

    public ParticleEffect.ParticlePacket display(
            ParticleEffect.ParticleData particleData,
            Vector vector,
            float value,
            Location location,
            List<Player> values)
            throws ParticleEffect.ParticleVersionException, ParticleEffect.ParticleDataException {
        if (!this.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleEffect.ParticleDataException(
                    "This particle effect does not require additional data");
        } else if (!isDataCorrect(this, particleData)) {
            throw new ParticleEffect.ParticleDataException("The particle data type is incorrect");
        } else {
            return new ParticleEffect.ParticlePacket(
                    this, vector, value, isLongDistance(location, values), particleData);
        }
    }

    public ParticleEffect.ParticlePacket display(
            ParticleEffect.ParticleData particleData,
            Vector vector,
            float value,
            Location location,
            Player... player)
            throws ParticleEffect.ParticleVersionException, ParticleEffect.ParticleDataException {
        return this.display(particleData, vector, value, location, Arrays.asList(player));
    }

    static {
        ParticleEffect[] particleEffects;
        int value = (particleEffects = values()).length;
        for (int index = 0; index < value; index++) {
            ParticleEffect particleEffect = particleEffects[index];
            NAME_MAP.put(particleEffect.name, particleEffect);
            ID_MAP.put(particleEffect.id, particleEffect);
        }
    }

    public static final class BlockData extends ParticleEffect.ParticleData {

        public BlockData(ItemStack itemStack) {
            super(itemStack);
        }
    }

    public static final class ItemData extends ParticleEffect.ParticleData {

        public ItemData(ItemStack itemStack) {
            super(itemStack);
        }
    }

    public static final class NoteColor extends ParticleEffect.ParticleColor {

        private final int note;

        public NoteColor(int note) throws IllegalArgumentException {
            if (note < 0) {
                throw new IllegalArgumentException("The note value is lower than 0");
            }
            if (note > 24) {
                throw new IllegalArgumentException("The note value is higher than 24");
            }
            this.note = note;
        }

        @Override
        public float getValueX() {
            return this.note / 24.0F;
        }

        @Override
        public float getValueY() {
            return 0.0F;
        }

        @Override
        public float getValueZ() {
            return 0.0F;
        }
    }

    public static final class OrdinaryColor extends ParticleEffect.ParticleColor {

        private final int red;

        private final int green;

        private final int blue;

        public OrdinaryColor(int red, int green, int blue) throws IllegalArgumentException {
            if (red < 0) {
                throw new IllegalArgumentException("The red value is lower than 0");
            }
            if (red > 255) {
                throw new IllegalArgumentException("The red value is higher than 255");
            }
            this.red = red;
            if (green < 0) {
                throw new IllegalArgumentException("The green value is lower than 0");
            }
            if (green > 255) {
                throw new IllegalArgumentException("The green value is higher than 255");
            }
            this.green = green;
            if (blue < 0) {
                throw new IllegalArgumentException("The blue value is lower than 0");
            }
            if (blue > 255) {
                throw new IllegalArgumentException("The blue value is higher than 255");
            }
            this.blue = blue;
        }

        public OrdinaryColor(Color color) {
            this(color.getRed(), color.getGreen(), color.getBlue());
        }

        public int getRed() {
            return this.red;
        }

        public int getGreen() {
            return this.green;
        }

        public int getBlue() {
            return this.blue;
        }

        @Override
        public float getValueX() {
            return this.red / 255.0F;
        }

        @Override
        public float getValueY() {
            return this.green / 255.0F;
        }

        @Override
        public float getValueZ() {
            return this.blue / 255.0F;
        }
    }

    public abstract static class ParticleColor {

        public abstract float getValueX();

        public abstract float getValueY();

        public abstract float getValueZ();
    }

    private static final class ParticleColorException extends RuntimeException {

        private static final long serialVersionUID = 3203085387160737484L;

        public ParticleColorException(String text) {
            super(text);
        }
    }

    public abstract static class ParticleData {

        private final Material material;

        private final ItemStack itemStack;

        public ParticleData(ItemStack itemStack2) {
            Preconditions.checkNotNull(itemStack2, "Item cannot be null");
            this.material = itemStack2.getType();
            this.itemStack = itemStack2;
        }

        public Material getMaterial() {
            return this.material;
        }

        public ItemStack getItem() {
            return this.itemStack;
        }
    }

    private static final class ParticleDataException extends RuntimeException {

        private static final long serialVersionUID = 3203085387160737484L;

        public ParticleDataException(String text) {
            super(text);
        }
    }

    public static final class ParticlePacket {

        private final ParticleEffect effect;

        private float offsetX;

        private final float offsetY;

        private final float offsetZ;

        private final float speed;

        private final int amount;

        private final boolean longDistance;

        private final ParticleEffect.ParticleData data;

        private ParticleEffect.ParticleColor color;

        public ParticlePacket(
                ParticleEffect particleEffect,
                float offsetX,
                float offsetY,
                float offsetZ,
                float speed,
                int amount,
                boolean longDistance,
                ParticleEffect.ParticleData particleData)
                throws IllegalArgumentException {
            if (speed < 0.0F) {
                throw new IllegalArgumentException("The speed is lower than 0");
            }
            if (amount < 0) {
                throw new IllegalArgumentException("The amount is lower than 0");
            }
            this.effect = particleEffect;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.speed = speed;
            this.amount = amount;
            this.longDistance = longDistance;
            this.data = particleData;
        }

        public ParticlePacket(
                ParticleEffect particleEffect,
                Vector vector,
                float value,
                boolean matches,
                ParticleEffect.ParticleData particleData)
                throws IllegalArgumentException {
            this(
                    particleEffect,
                    (float) vector.getX(),
                    (float) vector.getY(),
                    (float) vector.getZ(),
                    value,
                    0,
                    matches,
                    particleData);
        }

        public ParticlePacket(
                ParticleEffect particleEffect,
                ParticleEffect.ParticleColor particleColor,
                boolean matches) {
            this(
                    particleEffect,
                    particleColor.getValueX(),
                    particleColor.getValueY(),
                    particleColor.getValueZ(),
                    1.0F,
                    0,
                    matches,
                    null);
            this.color = particleColor;
            if (particleEffect == ParticleEffect.REDSTONE
                    && particleColor instanceof ParticleEffect.OrdinaryColor
                    && ((ParticleEffect.OrdinaryColor) particleColor).getRed() == 0) {
                this.offsetX = Float.MIN_NORMAL;
            }
        }

        public xyz.refinedev.lib.particlenativeapi.api.packet.ParticlePacket toNativePacket(
                ParticleNativeAPI particleNativeAPI, Location location) {
            xyz.refinedev.lib.particlenativeapi.api.particle.type.ParticleType result;
            switch (this.effect) {
                case EXPLOSION_NORMAL:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.EXPLOSION_NORMAL;
                    } else {
                        result = particleNativeAPI.LIST_1_13.EXPLOSION;
                    }
                    break;
                case EXPLOSION_LARGE:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.EXPLOSION_LARGE;
                    } else {
                        result = particleNativeAPI.LIST_1_13.EXPLOSION;
                    }
                    break;
                case EXPLOSION_HUGE:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.EXPLOSION_HUGE;
                    } else {
                        result = particleNativeAPI.LIST_1_13.EXPLOSION_EMITTER;
                    }
                    break;
                case FIREWORKS_SPARK:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.FIREWORKS_SPARK;
                    } else {
                        result = particleNativeAPI.LIST_1_13.FIREWORK;
                    }
                    break;
                case WATER_BUBBLE:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.WATER_BUBBLE;
                    } else {
                        result = particleNativeAPI.LIST_1_13.BUBBLE;
                    }
                    break;
                case WATER_SPLASH:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.WATER_SPLASH;
                    } else {
                        result = particleNativeAPI.LIST_1_13.SPLASH;
                    }
                    break;
                case WATER_WAKE:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.WATER_WAKE;
                    } else {
                        result = particleNativeAPI.LIST_1_13.FISHING;
                    }
                    break;
                case SUSPENDED:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.SUSPENDED;
                    } else {
                        result = particleNativeAPI.LIST_1_13.FALLING_WATER;
                    }
                    break;
                case SUSPENDED_DEPTH:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.SUSPENDED_DEPTH;
                    } else {
                        result = particleNativeAPI.LIST_1_13.UNDERWATER;
                    }
                    break;
                case CRIT:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.CRIT;
                    } else {
                        result = particleNativeAPI.LIST_1_13.CRIT;
                    }
                    break;
                case CRIT_MAGIC:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.CRIT_MAGIC;
                    } else {
                        result = particleNativeAPI.LIST_1_13.ENCHANTED_HIT;
                    }
                    break;
                case SMOKE_NORMAL:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.SMOKE_NORMAL;
                    } else {
                        result = particleNativeAPI.LIST_1_13.SMOKE;
                    }
                    break;
                case SMOKE_LARGE:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.SMOKE_LARGE;
                    } else {
                        result = particleNativeAPI.LIST_1_13.LARGE_SMOKE;
                    }
                    break;
                case SPELL:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.SPELL;
                    } else {
                        result = particleNativeAPI.LIST_1_13.EFFECT;
                    }
                    break;
                case SPELL_INSTANT:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.SPELL_INSTANT;
                    } else {
                        result = particleNativeAPI.LIST_1_13.INSTANT_EFFECT;
                    }
                    break;
                case SPELL_MOB:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.SPELL_MOB;
                    } else {
                        result = particleNativeAPI.LIST_1_13.ENTITY_EFFECT;
                    }
                    break;
                case SPELL_MOB_AMBIENT:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.SPELL_MOB_AMBIENT;
                    } else {
                        result = particleNativeAPI.LIST_1_13.AMBIENT_ENTITY_EFFECT;
                    }
                    break;
                case SPELL_WITCH:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.SPELL_WITCH;
                    } else {
                        result = particleNativeAPI.LIST_1_13.WITCH;
                    }
                    break;
                case DRIP_WATER:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.DRIP_WATER;
                    } else {
                        result = particleNativeAPI.LIST_1_13.DRIPPING_WATER;
                    }
                    break;
                case DRIP_LAVA:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.DRIP_LAVA;
                    } else {
                        result = particleNativeAPI.LIST_1_13.DRIPPING_LAVA;
                    }
                    break;
                case VILLAGER_ANGRY:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.VILLAGER_ANGRY;
                    } else {
                        result = particleNativeAPI.LIST_1_13.ANGRY_VILLAGER;
                    }
                    break;
                case VILLAGER_HAPPY:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.VILLAGER_HAPPY;
                    } else {
                        result = particleNativeAPI.LIST_1_13.HAPPY_VILLAGER;
                    }
                    break;
                case TOWN_AURA:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.TOWN_AURA;
                    } else {
                        result = particleNativeAPI.LIST_1_13.MYCELIUM;
                    }
                    break;
                case NOTE:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.NOTE;
                    } else {
                        result = particleNativeAPI.LIST_1_13.NOTE;
                    }
                    break;
                case PORTAL:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.PORTAL;
                    } else {
                        result = particleNativeAPI.LIST_1_13.PORTAL;
                    }
                    break;
                case ENCHANTMENT_TABLE:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.ENCHANTMENT_TABLE;
                    } else {
                        result = particleNativeAPI.LIST_1_13.ENCHANT;
                    }
                    break;
                case FLAME:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.FLAME;
                    } else {
                        result = particleNativeAPI.LIST_1_13.FLAME;
                    }
                    break;
                case LAVA:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.LAVA;
                    } else {
                        result = particleNativeAPI.LIST_1_13.LAVA;
                    }
                    break;
                case FOOTSTEP:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.FOOTSTEP;
                    } else {
                        result = particleNativeAPI.LIST_1_13.BLOCK.of(Material.DIRT, (byte) 0);
                    }
                    break;
                case CLOUD:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.CLOUD;
                    } else {
                        result = particleNativeAPI.LIST_1_13.CLOUD;
                    }
                    break;
                case REDSTONE:
                    if (this.color != null) {
                        if (VersionUtil.MINOR_VERSION <= 12) {
                            return particleNativeAPI.LIST_1_8.REDSTONE.packetColored(
                                    true,
                                    location,
                                    ((ParticleEffect.OrdinaryColor) this.color).getRed(),
                                    ((ParticleEffect.OrdinaryColor) this.color).getGreen(),
                                    ((ParticleEffect.OrdinaryColor) this.color).getBlue());
                        }
                        result =
                                particleNativeAPI.LIST_1_13.DUST.color(
                                        ((ParticleEffect.OrdinaryColor) this.color).getRed(),
                                        ((ParticleEffect.OrdinaryColor) this.color).getGreen(),
                                        ((ParticleEffect.OrdinaryColor) this.color).getBlue(),
                                        1.0F);
                    } else {
                        if (VersionUtil.MINOR_VERSION <= 12) {
                            return particleNativeAPI.LIST_1_8.REDSTONE.packetColored(
                                    true,
                                    location,
                                    (int) this.offsetX,
                                    (int) this.offsetY,
                                    (int) this.offsetZ);
                        }
                        result =
                                particleNativeAPI.LIST_1_13.DUST.color(
                                        this.offsetX, this.offsetY, this.offsetZ, 1.0F);
                    }
                    break;
                case SNOWBALL:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.SNOWBALL;
                    } else {
                        result = particleNativeAPI.LIST_1_13.ITEM_SNOWBALL;
                    }
                    break;
                case SNOW_SHOVEL:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.SNOW_SHOVEL;
                    } else {
                        result = particleNativeAPI.LIST_1_13.ITEM_SNOWBALL;
                    }
                    break;
                case SLIME:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.SLIME;
                    } else {
                        result = particleNativeAPI.LIST_1_13.ITEM_SLIME;
                    }
                    break;
                case HEART:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.HEART;
                    } else {
                        result = particleNativeAPI.LIST_1_13.HEART;
                    }
                    break;
                case BARRIER:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.BARRIER;
                    } else {
                        result = particleNativeAPI.LIST_1_13.BARRIER;
                    }
                    break;
                case ITEM_CRACK:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        return particleNativeAPI
                                .LIST_1_8
                                .ITEM_CRACK
                                .of(this.data.getMaterial())
                                .packet(
                                        true,
                                        location,
                                        this.offsetX,
                                        this.offsetY,
                                        this.offsetZ,
                                        this.speed,
                                        this.amount);
                    }
                    return particleNativeAPI
                            .LIST_1_13
                            .ITEM
                            .of(this.data.getMaterial())
                            .packet(
                                    true,
                                    location,
                                    this.offsetX,
                                    this.offsetY,
                                    this.offsetZ,
                                    this.speed,
                                    this.amount);
                case BLOCK_CRACK:
                    ParticleTypeMotion particleTypeMotion;
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        particleTypeMotion =
                                this.data.getItem() == null
                                        ? particleNativeAPI.LIST_1_8.BLOCK_CRACK.of(
                                                this.data.getMaterial())
                                        : particleNativeAPI.LIST_1_8.BLOCK_CRACK.of(
                                                this.data.getMaterial(),
                                                this.data.getItem().getData().getData());
                    } else {
                        particleTypeMotion =
                                particleNativeAPI.LIST_1_13.BLOCK.of(this.data.getMaterial());
                    }
                    return particleTypeMotion.packet(
                            true,
                            location,
                            this.offsetX,
                            this.offsetY,
                            this.offsetZ,
                            this.speed,
                            this.amount);
                case BLOCK_DUST:
                    ParticleTypeMotion particleTypeMotion2;
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        particleTypeMotion2 =
                                this.data.getItem() == null
                                        ? particleNativeAPI.LIST_1_8.BLOCK_DUST.of(
                                                this.data.getMaterial())
                                        : particleNativeAPI.LIST_1_8.BLOCK_DUST.of(
                                                this.data.getMaterial(),
                                                this.data.getItem().getData().getData());
                    } else {
                        particleTypeMotion2 =
                                particleNativeAPI.LIST_1_13.BLOCK.of(this.data.getMaterial());
                    }
                    return particleTypeMotion2.packet(
                            true,
                            location,
                            this.offsetX,
                            this.offsetY,
                            this.offsetZ,
                            this.speed,
                            this.amount);
                case WATER_DROP:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.WATER_DROP;
                    } else {
                        result = particleNativeAPI.LIST_1_13.SPLASH;
                    }
                    break;
                case ITEM_TAKE:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.ITEM_TAKE;
                    } else {
                        result = particleNativeAPI.LIST_1_13.ITEM_SLIME;
                    }
                    break;
                case MOB_APPEARANCE:
                    if (VersionUtil.MINOR_VERSION <= 12) {
                        result = particleNativeAPI.LIST_1_8.MOB_APPEARANCE;
                    } else {
                        result = particleNativeAPI.LIST_1_13.ENTITY_EFFECT;
                    }
                    break;
                default:
                    throw new ParticleEffect.ParticleVersionException(
                            "This particle effect is not supported by your server version");
            }
            return result.packet(
                    true,
                    location,
                    this.offsetX,
                    this.offsetY,
                    this.offsetZ,
                    this.speed,
                    this.amount);
        }

        public ParticleEffect getEffect() {
            return this.effect;
        }

        public float getOffsetX() {
            return this.offsetX;
        }

        public float getOffsetY() {
            return this.offsetY;
        }

        public float getOffsetZ() {
            return this.offsetZ;
        }

        public float getSpeed() {
            return this.speed;
        }

        public int getAmount() {
            return this.amount;
        }

        public boolean isLongDistance() {
            return this.longDistance;
        }

        public ParticleEffect.ParticleData getData() {
            return this.data;
        }

        public ParticleEffect.ParticleColor getColor() {
            return this.color;
        }

        public static final class PacketInstantiationException extends RuntimeException {

            private static final long serialVersionUID = 3203085387160737484L;

            public PacketInstantiationException(String text, Throwable throwable) {
                super(text, throwable);
            }
        }

        public static final class PacketSendingException extends RuntimeException {

            private static final long serialVersionUID = 3203085387160737484L;

            public PacketSendingException(String text, Throwable throwable) {
                super(text, throwable);
            }
        }

        public static final class VersionIncompatibleException extends RuntimeException {

            private static final long serialVersionUID = 3203085387160737484L;

            public VersionIncompatibleException(String text, Throwable throwable) {
                super(text, throwable);
            }
        }
    }

    public enum ParticleProperty {
        REQUIRES_WATER,
        REQUIRES_DATA,
        DIRECTIONAL,
        COLORABLE
    }

    private static final class ParticleVersionException extends RuntimeException {

        private static final long serialVersionUID = 3203085387160737484L;

        public ParticleVersionException(String text) {
            super(text);
        }
    }
}
