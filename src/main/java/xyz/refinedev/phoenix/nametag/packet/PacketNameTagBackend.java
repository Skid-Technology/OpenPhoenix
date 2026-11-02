package xyz.refinedev.phoenix.nametag.packet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.NameTagsConfig;
import xyz.refinedev.phoenix.internal.CachedSettings;
import xyz.refinedev.phoenix.nametag.NameTagBackend;
import xyz.refinedev.phoenix.nametag.NameTagBackendFactory;
import xyz.refinedev.phoenix.nametag.NameTagExecutor;
import xyz.refinedev.phoenix.nametag.line.ConditionContext;
import xyz.refinedev.phoenix.nametag.line.LineRenderer;
import xyz.refinedev.phoenix.nametag.line.NameTagLine;
import xyz.refinedev.phoenix.nametag.line.PlayerConditionContext;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class PacketNameTagBackend implements NameTagBackend {

    private static final String text = "%rel_";

    private final Map<UUID, Map<UUID, NameTagState>> nameTagStatesById = new HashMap<>();

    private final Map<UUID, AtomicInteger> atomicIntegersById = new HashMap<>();

    private final Map<Integer, UUID> uUIDsByIndex = new ConcurrentHashMap<>();

    private final NameTagViewerState nameTagViewerState = new NameTagViewerState();

    private final CommonPlatform platform;

    private final NameTagBackendFactory nameTagBackendFactory;

    private final NameTagExecutor nameTagExecutor;

    private final NameTagTeam nameTagTeam;

    private final NameTagEntry nameTagEntry;

    private final boolean enabled;

    private Consumer<Player> consumer = result -> {};

    private long time;

    public PacketNameTagBackend(
            CommonPlatform commonPlatform,
            NameTagBackendFactory nameTagBackendFactory2,
            NameTagExecutor nameTagExecutor2) {
        this.platform = commonPlatform;
        this.nameTagBackendFactory = nameTagBackendFactory2;
        this.nameTagExecutor = nameTagExecutor2;
        this.nameTagTeam = new NameTagTeam(NameTagPackets.getNameTagPackets());
        this.nameTagEntry = new NameTagEntry(commonPlatform);
        this.enabled = PlayerConditionContext.isEnabled();
    }

    @Override
    public void refresh(Player player, Player player2) {
        this.applyPlayer(player, player2, null);
    }

    private void applyPlayer(
            Player player, Player player2, Map<UUID, Map<LineRenderer, String>> entries) {
        if (player != null && player2 != null) {
            if (this.nameTagBackendFactory.matchesEnabled(player)) {
                try {
                    PlayerConditionContext playerConditionContext =
                            new PlayerConditionContext(
                                    this.platform, player2, player, player2, this.enabled);
                    PlayerConditionContext playerConditionContext2 =
                            new PlayerConditionContext(
                                    this.platform, player, player, player2, this.enabled);
                    Map<LineRenderer, String> entries2 =
                            entries == null
                                    ? null
                                    : entries.computeIfAbsent(
                                            player2.getUniqueId(), result -> new HashMap());
                    this.applyPlayerByNameTagEntry(
                            player,
                            player2,
                            playerConditionContext,
                            playerConditionContext2,
                            entries2);
                    if (!this.matchesEnabled(player, player2)) {
                        this.remove(player, player2);
                        return;
                    }
                    this.applyPlayer(
                            player,
                            player2,
                            playerConditionContext,
                            playerConditionContext2,
                            entries2);
                } catch (Throwable throwable) {
                    this.applyPlayer(player, throwable);
                }
            }
        }
    }

    @Override
    public void refreshFor(Player player, List<Player> values) {
        this.applyPlayer(player, values, (Map<UUID, Map<LineRenderer, String>>) null);
    }

    private void applyPlayer(
            Player player, List<Player> values, Map<UUID, Map<LineRenderer, String>> entries) {
        for (Player player2 : values) {
            this.applyPlayer(player, player2, entries);
        }
    }

    @Override
    public void refreshTargets(Player player, List<Player> values) {
        this.applyPlayerByVoid(player, values, new HashMap<>());
    }

    private void applyPlayerByVoid(
            Player player, List<Player> values, Map<UUID, Map<LineRenderer, String>> entries) {
        for (Player player2 : values) {
            this.applyPlayer(player2, player, entries);
        }
    }

    public void applyPlayer(Player player, List<Player> values) {
        HashMap entries = new HashMap();
        this.applyPlayer(player, values, entries);
        this.applyPlayerByVoid(player, values, entries);
    }

    public void applyList(List<Player> values) {
        HashMap entries = new HashMap();
        for (Player player : values) {
            this.applyPlayer(player, values, entries);
        }
    }

    @Override
    public void remove(Player player, Player player2) {
        Map<UUID, xyz.refinedev.phoenix.nametag.packet.NameTagState> entries;
        if ((entries = this.nameTagStatesById.get(player.getUniqueId())) != null) {
            NameTagState nameTagState;
            if ((nameTagState = (NameTagState) entries.remove(player2.getUniqueId())) != null) {
                try {
                    if (!nameTagState.isEmpty()) {
                        this.nameTagTeam.applyPlayer(player, nameTagState.getCountArray());
                    }
                    this.nameTagEntry.applyPlayerByVoid(player, player2);
                } catch (Throwable throwable) {
                }
                nameTagState.initialize();
            }
        }
    }

    public void applyPlayer(Player player) {
        this.uUIDsByIndex.put(player.getEntityId(), player.getUniqueId());
    }

    public void applyPlayer(Player player, Player player2) {
        Map<UUID, xyz.refinedev.phoenix.nametag.packet.NameTagState> entries;
        if ((entries = this.nameTagStatesById.get(player.getUniqueId())) != null) {
            NameTagState nameTagState;
            if ((nameTagState = (NameTagState) entries.get(player2.getUniqueId())) != null) {
                nameTagState.setEnabledEnabledByEnabledEnabled();
            }
        }
    }

    public void applyPlayerByNameTagStatesById(Player player) {
        Map<UUID, xyz.refinedev.phoenix.nametag.packet.NameTagState> entries;
        if ((entries = this.nameTagStatesById.get(player.getUniqueId())) != null
                && !entries.isEmpty()) {
            Iterator iterator2 = new ArrayList(entries.keySet()).iterator();
            while (iterator2.hasNext()) {
                UUID uUID;
                Player player2;
                if ((player2 = Bukkit.getPlayer(uUID = (UUID) iterator2.next())) != null) {
                    NameTagState nameTagState;
                    if ((nameTagState = (NameTagState) entries.get(uUID)) != null) {
                        nameTagState.setEnabledEnabledByEnabledEnabled();
                    }
                    this.refresh(player, player2);
                }
            }
        }
    }

    public Player getPlayer(int value) {
        UUID uUID;
        return (uUID = this.uUIDsByIndex.get(value)) == null ? null : Bukkit.getPlayer(uUID);
    }

    public void applyPlayerByNameTagTeam(Player player) {
        Map<UUID, xyz.refinedev.phoenix.nametag.packet.NameTagState> entries;
        if ((entries = this.nameTagStatesById.remove(player.getUniqueId())) != null) {
            Iterator iterator2 = entries.values().iterator();
            while (iterator2.hasNext()) {
                NameTagState nameTagState;
                if (!(nameTagState = (NameTagState) iterator2.next()).isEmpty()) {
                    try {
                        this.nameTagTeam.applyPlayer(player, nameTagState.getCountArray());
                    } catch (Throwable throwable) {
                    }
                }
            }
            this.applyMap(entries);
        }
    }

    public void applyPlayerByUUIDsByIndex(Player player, List<Player> values) {
        UUID uniqueId = player.getUniqueId();
        this.uUIDsByIndex.remove(player.getEntityId());
        Map<UUID, xyz.refinedev.phoenix.nametag.packet.NameTagState> entries;
        if ((entries = this.nameTagStatesById.remove(uniqueId)) != null) {
            this.applyMap(entries);
        }
        this.atomicIntegersById.remove(uniqueId);
        Iterator<Player> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            Player player2;
            Map<UUID, xyz.refinedev.phoenix.nametag.packet.NameTagState> entries2;
            NameTagState nameTagState;
            if (!(player2 = (Player) iterator2.next()).getUniqueId().equals(uniqueId)
                    && (entries2 = this.nameTagStatesById.get(player2.getUniqueId())) != null
                    && (nameTagState = (NameTagState) entries2.remove(uniqueId)) != null) {
                try {
                    if (!nameTagState.isEmpty()) {
                        this.nameTagTeam.applyPlayer(player2, nameTagState.getCountArray());
                    }
                } catch (Throwable throwable) {
                }
                nameTagState.initialize();
            }
        }
    }

    private void applyMap(Map<UUID, NameTagState> entries) {
        Iterator<xyz.refinedev.phoenix.nametag.packet.NameTagState> iterator2 =
                entries.values().iterator();
        while (iterator2.hasNext()) {
            ((NameTagState) iterator2.next()).initialize();
        }
        entries.clear();
    }

    public void applyListByNameTagStatesById(List<Player> values2) {
        Map<UUID, Player> entries = findMap(values2);
        for (Entry<UUID, Map<UUID, xyz.refinedev.phoenix.nametag.packet.NameTagState>> entry :
                this.nameTagStatesById.entrySet()) {
            Player player = (Player) entries.get(entry.getKey());
            for (NameTagState nameTagState :
                    (Iterable<NameTagState>) ((Iterable) ((Map) entry.getValue()).values())) {
                if (player != null && !nameTagState.isEmpty()) {
                    try {
                        this.nameTagTeam.applyPlayer(player, nameTagState.getCountArray());
                    } catch (Throwable throwable) {
                    }
                }
                nameTagState.initialize();
            }
            ((Map) entry.getValue()).clear();
        }
        this.nameTagStatesById.clear();
        this.atomicIntegersById.clear();
    }

    private static Map<UUID, Player> findMap(List<Player> values) {
        HashMap<UUID, Player> entries = new HashMap<>();
        for (Player player : values) {
            entries.put(player.getUniqueId(), player);
        }
        return entries;
    }

    public void updateCount(int value, List<Player> values) {
        long value2 = this.time;
        this.time = value2 + value;
        boolean matches = false;
        LineRenderer[] lineRenderers;
        int value3 = (lineRenderers = NameTagsConfig.lines()).length;
        for (int index = 0; index < value3; index++) {
            if (lineRenderers[index].getConditionalLine().matchesEnabled(value2, this.time)) {
                matches = true;
                break;
            }
        }
        if (matches) {
            HashMap entries = new HashMap();
            Map<UUID, Player> entries2 = findMap(values);
            for (Player player : values) {
                Map<UUID, xyz.refinedev.phoenix.nametag.packet.NameTagState> entries3;
                if (this.nameTagBackendFactory.matchesEnabled(player)
                        && (entries3 = this.nameTagStatesById.get(player.getUniqueId())) != null) {
                    for (UUID uUID :
                            (Iterable<UUID>) ((Iterable) new ArrayList(entries3.keySet()))) {
                        Player player2;
                        if ((player2 = (Player) entries2.get(uUID)) != null) {
                            this.applyPlayer(player, player2, entries);
                        }
                    }
                }
            }
        }
    }

    private boolean matchesEnabled(Player player, Player player2) {
        if (!player.isOnline() || !player2.isOnline()) {
            return false;
        }
        if (!player.isDead() && !player2.isDead()) {
            if (player2.getGameMode() == GameMode.SPECTATOR) {
                return false;
            } else if (!player.canSee(player2)) {
                return false;
            } else {
                return player2.hasPotionEffect(PotionEffectType.INVISIBILITY)
                        ? false
                        : player.getWorld() == player2.getWorld();
            }
        } else {
            return false;
        }
    }

    private void applyPlayer(Player player, NameTagState nameTagState) {
        if (!nameTagState.isEmpty()) {
            int[] countArray = nameTagState.getCountArray();
            for (String text : nameTagState.keySet()) {
                nameTagState.remove(text);
            }
            nameTagState.setEnabledEnabledByEnabledEnabled();
            this.nameTagTeam.applyPlayer(player, countArray);
            if (NameTagsConfig.DISPLAY.DEBUG) {
                Bukkit.getLogger()
                        .info("[PhoenixNT]   destroyed " + countArray.length + " line(s) for self");
            }
        }
    }

    public void applyPlayerByVoid(Player player, List<Player> values) {
        for (Player player2 : values) {
            this.applyPlayer(player2, player);
        }
        this.refreshTargets(player, values);
    }

    private void applyPlayer(
            Player player,
            Player player2,
            ConditionContext conditionContext,
            ConditionContext conditionContext2,
            Map<LineRenderer, String> entries) {
        NameTagState nameTagState;
        if ((nameTagState =
                        this.nameTagStatesById
                                .computeIfAbsent(player.getUniqueId(), result -> new HashMap<>())
                                .computeIfAbsent(
                                        player2.getUniqueId(), result -> new NameTagState()))
                .isEnabled()) {
            this.nameTagEntry.applyPlayer(player, player2);
        }
        if (NameTagsConfig.DISPLAY.DEBUG) {
            Bukkit.getLogger()
                    .info(
                            "[PhoenixNT] reconcile "
                                    + player.getName()
                                    + " -> "
                                    + player2.getName()
                                    + " entry="
                                    + player2.getName()
                                    + " entityId="
                                    + player2.getEntityId()
                                    + " live="
                                    + nameTagState.getCount());
        }
        if (NameTagsConfig.DISPLAY.HIDE_SELF
                && player.getUniqueId().equals(player2.getUniqueId())) {
            this.applyPlayer(player, nameTagState);
        } else {
            ArrayList values = new ArrayList();
            LineRenderer[] lineRenderers;
            int value = (lineRenderers = NameTagsConfig.lines()).length;
            for (int index = 0; index < value; index++) {
                LineRenderer lineRenderer;
                if ((lineRenderer = lineRenderers[index])
                                .getLineCondition()
                                .isEmpty(conditionContext)
                        && lineRenderer
                                .getLineConditionLineCondition()
                                .isEmpty(conditionContext2)) {
                    values.add(lineRenderer);
                }
            }
            ArrayList<String> values2 = new ArrayList<>(values.size());
            for (LineRenderer lineRenderer2 : (Iterable<LineRenderer>) ((Iterable) values)) {
                values2.add(lineRenderer2.getText());
            }
            boolean matches = !values2.equals(nameTagState.keySet());
            for (String text : nameTagState.keySet()) {
                Integer value2;
                if (!values2.contains(text) && (value2 = nameTagState.remove(text)) != null) {
                    this.nameTagTeam.applyPlayer(player, new int[] {value2});
                }
            }
            CachedSettings cachedSettings = NameTagsConfig.displaySettings();
            float[] values3 =
                    NameTagLine.findFloatArray(
                            values.size(),
                            cachedSettings.getFloatValueFloat(),
                            cachedSettings.getFloatValue2());
            HashMap entries2 = new HashMap();
            for (LineRenderer lineRenderer3 : (Iterable<LineRenderer>) ((Iterable) values)) {
                entries2.put(
                        lineRenderer3.getText(),
                        this.formatString(lineRenderer3, player2, conditionContext, entries));
            }
            ArrayList<Integer> values4 = new ArrayList<>();
            for (int index2 = 0; index2 < values.size(); index2++) {
                LineRenderer lineRenderer4 = (LineRenderer) values.get(index2);
                String text2 = (String) entries2.get(lineRenderer4.getText());
                Integer value3;
                if ((value3 = nameTagState.findInteger(lineRenderer4.getText())) == null) {
                    value3 = this.nameTagViewerState.getCount();
                    nameTagState.applyString(lineRenderer4.getText(), value3);
                    nameTagState.matchesEnabled(lineRenderer4.getText(), text2);
                    values4.add(value3);
                    this.nameTagTeam.applyPlayer(player, player2, value3);
                    this.nameTagTeam.applyPlayer(
                            player,
                            value3,
                            Color.translateComponent(text2),
                            values3[index2],
                            lineRenderer4.getFloatValue(),
                            cachedSettings,
                            cachedSettings.isEnabled3()
                                    ? cachedSettings.findByte(0, cachedSettings.getCount2())
                                    : cachedSettings.getByteValue7());
                } else {
                    if (nameTagState.matchesEnabled(lineRenderer4.getText(), text2)) {
                        this.nameTagTeam.applyPlayer(
                                player, value3, Color.translateComponent(text2));
                    }
                    if (matches) {
                        this.nameTagTeam.applyPlayer(player, value3, values3[index2]);
                    }
                }
            }
            if (matches || !nameTagState.isEnabledEnabled()) {
                nameTagState.applyList(values2);
                if (!nameTagState.isEmpty()) {
                    this.nameTagTeam.applyPlayer(player, player2, nameTagState.getCountArray());
                    nameTagState.setEnabledEnabled();
                }
            }
            if (NameTagsConfig.DISPLAY.DEBUG) {
                for (LineRenderer lineRenderer5 : (Iterable<LineRenderer>) ((Iterable) values)) {
                    String text3 = (String) entries2.get(lineRenderer5.getText());
                    Bukkit.getLogger()
                            .info(
                                    "[PhoenixNT]   line "
                                            + lineRenderer5.getText()
                                            + " id="
                                            + nameTagState.findInteger(lineRenderer5.getText())
                                            + " raw="
                                            + text3
                                            + " component="
                                            + Color.translateComponent(text3));
                }
            }
            if (!values4.isEmpty() && cachedSettings.isEnabled3()) {
                this.applyPlayer(player, values4, cachedSettings);
            }
        }
    }

    private void applyPlayer(Player player, List<Integer> values, CachedSettings cachedSettings) {
        int value;
        if ((value = cachedSettings.getCount2()) > 0) {
            this.applyPlayer(player, values, cachedSettings, 1, value);
        } else {
            for (int value2 : values) {
                this.nameTagTeam.applyPlayer(player, value2, cachedSettings.getByteValue7());
            }
        }
    }

    private void applyPlayer(
            Player player,
            List<Integer> values,
            CachedSettings cachedSettings,
            int value,
            int value2) {
        this.nameTagExecutor.applyRunnable(
                () -> {
                    if (player.isOnline()) {
                        byte value3 = cachedSettings.findByte(value, value2);
                        for (int value4 : values) {
                            try {
                                this.nameTagTeam.applyPlayer(player, value4, value3);
                            } catch (Throwable throwable) {
                            }
                        }
                        if (value < value2) {
                            this.applyPlayer(player, values, cachedSettings, value + 1, value2);
                        }
                    }
                },
                1L);
    }

    public String formatString(Player player) {
        PlayerConditionContext playerConditionContext =
                new PlayerConditionContext(this.platform, player, player, player, this.enabled);
        LineRenderer[] lineRenderers;
        int value = (lineRenderers = NameTagsConfig.tabFormats()).length;
        for (int index = 0; index < value; index++) {
            LineRenderer lineRenderer;
            if ((lineRenderer = lineRenderers[index])
                    .getLineCondition()
                    .isEmpty(playerConditionContext)) {
                return Color.translate(
                        this.formatString(lineRenderer, player, playerConditionContext, null));
            }
        }
        return null;
    }

    private void applyPlayerByNameTagEntry(
            Player player,
            Player player2,
            ConditionContext conditionContext,
            ConditionContext conditionContext2,
            Map<LineRenderer, String> entries) {
        LineRenderer[] lineRenderers;
        int value = (lineRenderers = NameTagsConfig.tabFormats()).length;
        for (int index = 0; index < value; index++) {
            LineRenderer lineRenderer;
            if ((lineRenderer = lineRenderers[index]).getLineCondition().isEmpty(conditionContext)
                    && lineRenderer.getLineConditionLineCondition().isEmpty(conditionContext2)) {
                this.nameTagEntry.applyPlayer(
                        player,
                        player2,
                        this.formatString(lineRenderer, player2, conditionContext, entries));
                return;
            }
        }
    }

    private String formatString(
            LineRenderer lineRenderer,
            Player player,
            ConditionContext conditionContext,
            Map<LineRenderer, String> entries) {
        String text;
        if (entries != null && (text = (String) entries.get(lineRenderer)) != null) {
            return text;
        }
        text = lineRenderer.formatString(this.time);
        IProfile profile;
        IRank rank;
        String text2 =
                NameTagEntry.formatString(
                        (rank =
                                                (profile =
                                                                        this.platform
                                                                                .getProfileHandler()
                                                                                .getCachedProfile(
                                                                                        player
                                                                                                .getUniqueId()))
                                                                == null
                                                        ? null
                                                        : profile.getHighestRank())
                                        == null
                                ? null
                                : rank.getPlayerListPrefix());
        String text3 = rank == null ? "" : rank.getSuffix();
        String text4 = rank != null && rank.getColor() != null ? rank.getColor() : "";
        String text5 = profile == null ? player.getName() : profile.getPlayerName();
        String text6 =
                text.replace("<prefix>", text2 == null ? "" : text2)
                        .replace("<suffix>", text3 == null ? "" : text3)
                        .replace("<color>", text4 == null ? "" : text4)
                        .replace("<name>", text5 == null ? player.getName() : text5);
        boolean matches = entries != null && !text6.contains("%rel_");
        String text7 = conditionContext.placeholders(text6);
        if (matches) {
            entries.put(lineRenderer, text7);
        }
        return text7;
    }

    private void applyPlayer(Player player, Throwable throwable) {
        if (this.atomicIntegersById
                        .computeIfAbsent(player.getUniqueId(), result -> new AtomicInteger())
                        .incrementAndGet()
                >= 3) {
            this.nameTagBackendFactory.applyUUIDByNameTagBackendTypesById(player.getUniqueId());
            Map<UUID, xyz.refinedev.phoenix.nametag.packet.NameTagState> entries;
            if ((entries = this.nameTagStatesById.remove(player.getUniqueId())) != null) {
                Iterator iterator2 = entries.entrySet().iterator();
                while (iterator2.hasNext()) {
                    Entry entry;
                    NameTagState nameTagState =
                            (NameTagState) (entry = (Entry) iterator2.next()).getValue();
                    try {
                        if (!nameTagState.isEmpty()) {
                            this.nameTagTeam.applyPlayer(player, nameTagState.getCountArray());
                        }
                        Player player2;
                        if ((player2 = Bukkit.getPlayer((UUID) entry.getKey())) != null) {
                            this.nameTagEntry.applyPlayerByVoid(player, player2);
                        }
                    } catch (Throwable throwable2) {
                    }
                    nameTagState.initialize();
                }
            }
            this.consumer.accept(player);
            Bukkit.getLogger()
                    .warning(
                            "[Phoenix] Display nametags failed for "
                                    + player.getName()
                                    + ", falling back to team nametags until they rejoin: "
                                    + throwable);
        }
    }

    public void setConsumer(Consumer<Player> consumer2) {
        this.consumer = consumer2;
    }
}
