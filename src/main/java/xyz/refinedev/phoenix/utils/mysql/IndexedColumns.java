package xyz.refinedev.phoenix.utils.mysql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class IndexedColumns {

    private static final IndexedColumns NONE = new IndexedColumns(Collections.emptyList());

    private static final Map<String, IndexedColumns> BY_TABLE = new HashMap<>();

    private final List<IndexedColumn> columns;

    private final Map<String, String> byField;

    private static void register(String text, IndexedColumn... indexedColumn) {
        BY_TABLE.put(text, new IndexedColumns(Arrays.asList(indexedColumn)));
    }

    private IndexedColumns(List<IndexedColumn> values) {
        this.columns = Collections.unmodifiableList(new ArrayList<>(values));
        HashMap entries = new HashMap();
        for (IndexedColumn indexedColumn : this.columns) {
            entries.put(indexedColumn.getField(), indexedColumn.getColumn());
        }
        this.byField = Collections.unmodifiableMap(entries);
    }

    public static IndexedColumns forTable(String text) {
        IndexedColumns indexedColumns;
        return (indexedColumns = BY_TABLE.get(text)) == null ? NONE : indexedColumns;
    }

    public static IndexedColumns none() {
        return NONE;
    }

    public List<IndexedColumn> all() {
        return this.columns;
    }

    public String columnFor(String text) {
        return this.byField.get(text);
    }

    public boolean isEmpty() {
        return this.columns.isEmpty();
    }

    public IndexedColumns retaining(Set<String> values) {
        ArrayList<xyz.refinedev.phoenix.utils.mysql.IndexedColumn> values2 = new ArrayList<>();
        for (IndexedColumn indexedColumn : this.columns) {
            if (values.contains(indexedColumn.getColumn())) {
                values2.add(indexedColumn);
            }
        }
        return new IndexedColumns(values2);
    }

    static {
        IndexedColumn indexedColumn = IndexedColumn.existing("_id", "id");
        register(
                "pxprofiles",
                indexedColumn,
                IndexedColumn.generated("nameToLowercase", "name_lower"),
                IndexedColumn.generated("name", "name_exact"),
                IndexedColumn.generated("currentAddress", "current_address"),
                IndexedColumn.generated("currentSubnet", "current_subnet"));
        IndexedColumn[] indexedColumns = new IndexedColumn[6];
        indexedColumns[0] = indexedColumn;
        indexedColumns[1] = IndexedColumn.generated("target", "target_uuid");
        indexedColumns[2] = IndexedColumn.generated("issuedBy", "issued_by");
        indexedColumns[3] = IndexedColumn.generated("rankId", "rank_id");
        indexedColumns[4] = IndexedColumn.generated("active", "active_flag");
        indexedColumns[5] = IndexedColumn.generated("permanent", "permanent_flag");
        register("pxgrants", indexedColumns);
        indexedColumns = new IndexedColumn[12];
        indexedColumns[0] = indexedColumn;
        indexedColumns[1] = IndexedColumn.generated("target", "target_uuid");
        indexedColumns[2] = IndexedColumn.generated("issuedBy", "issued_by");
        indexedColumns[3] = IndexedColumn.generated("removedBy", "removed_by");
        indexedColumns[4] = IndexedColumn.generated("punishmentID", "punishment_id");
        indexedColumns[5] = IndexedColumn.generated("punishmentType", "punishment_type");
        indexedColumns[6] = IndexedColumn.generated("address", "address_hash");
        indexedColumns[7] = IndexedColumn.generated("issuedAt", "issued_at");
        indexedColumns[8] = IndexedColumn.generated("issuedOn", "issued_on");
        indexedColumns[9] = IndexedColumn.generated("voided", "voided_flag");
        indexedColumns[10] = IndexedColumn.generated("active", "active_flag");
        indexedColumns[11] = IndexedColumn.generated("permanent", "permanent_flag");
        register("pxpunishments", indexedColumns);
        register(
                "pxlogins",
                indexedColumn,
                IndexedColumn.generated("target", "target_uuid"),
                IndexedColumn.generated("ip", "ip_hash"),
                IndexedColumn.generated("login", "login_time"));
        register("pxserverHops", indexedColumn, IndexedColumn.generated("target", "target_uuid"));
        register(
                "pxchatLogs",
                indexedColumn,
                IndexedColumn.generated("sender", "sender_uuid"),
                IndexedColumn.generated("timestamp", "log_timestamp"));
        register(
                "pxchatSnapshots",
                indexedColumn,
                IndexedColumn.generated("requestedBy", "requested_by"),
                IndexedColumn.generated("niceId", "nice_id"));
        register(
                "pxcommandLogs",
                indexedColumn,
                IndexedColumn.generated("issuedBy", "issued_by"),
                IndexedColumn.generated("server", "server_name"));
        register(
                "pxcustomTags",
                IndexedColumn.generated("creator", "creator_uuid"),
                IndexedColumn.generated("status", "tag_status"));
        register(
                "pxdisguiseHistory",
                indexedColumn,
                IndexedColumn.generated("player", "player_uuid"));
        register("pxranks", indexedColumn);
        register("pxnotifications", indexedColumn);
        register("pxfilters", indexedColumn);
        register("pxpunishmentLadders", indexedColumn);
        register("pxsecurity", indexedColumn);
    }
}
