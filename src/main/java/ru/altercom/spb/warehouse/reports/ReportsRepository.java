package ru.altercom.spb.warehouse.reports;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Repository
public class ReportsRepository {

    private static final String ITEMS_BALANCE_REPORT_SQL = """
    SELECT warehouse_id, warehouses.name as warehouse_name,
           item_id, items.name as item_name,
           ob as opening_balance, r as receipt, e as expense, cb as closing_balance FROM
    (SELECT warehouse_id, item_id, SUM(ob) as ob, SUM(r) as r, SUM(e) as e, SUM(cb) as cb FROM
    (SELECT
       warehouse_id, item_id,
       SUM(CASE WHEN sign = 1 THEN quantity ELSE -quantity END) as ob,
       SUM(0) as r, SUM(0) as e, SUM(0) as cb
    FROM items_balance WHERE date < :start WAREHOUSE_FILTER
    GROUP BY warehouse_id, item_id
    UNION
    SELECT
       warehouse_id, item_id,
       SUM(0), SUM(0), SUM(0),
       SUM(CASE WHEN sign = 1 THEN quantity ELSE -quantity END)
    FROM items_balance WHERE date <= :end WAREHOUSE_FILTER
    GROUP BY warehouse_id, item_id
    UNION
    SELECT
       warehouse_id, item_id,
       SUM(0),
       SUM(CASE WHEN sign = 1 THEN quantity ELSE 0 END),
       SUM(CASE WHEN sign = 2 THEN quantity ELSE 0 END),
       SUM(0)
    FROM  items_balance WHERE date between :start AND :end WAREHOUSE_FILTER
    GROUP BY warehouse_id, item_id
    ) as balance
    GROUP BY warehouse_id, item_id) as groped_balance
    LEFT JOIN warehouses ON warehouse_id = warehouses.id
    LEFT JOIN items ON item_id = items.id
    ORDER BY warehouse_name, item_name;
    """;

    private static final String ITEMS_BALANCE_RECORDS_REPORT_SQL = """
            SELECT
                id, recorder_id, recorder_type, date,
                CASE WHEN sign = 1 THEN quantity ELSE 0 END as receipt,
                CASE WHEN sign = 2 THEN quantity ELSE 0 END as expense
            FROM
                items_balance
            WHERE
                warehouse_id = :warehouseId AND item_id = :itemId
                AND date between :start AND :end
            ORDER BY
                date;
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ReportsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ItemsBalanceReportRow> getItemsBalanceReport(LocalDate start, LocalDate end, Long warehouseId, Long itemId) {
        var params = new HashMap<String, Object>();
        params.put("start", start);
        params.put("end", end);
        var sql = ITEMS_BALANCE_REPORT_SQL;
        if (warehouseId == null && itemId == null) {
            sql = sql.replaceAll("WAREHOUSE_FILTER", "");
        } else if (warehouseId != null && itemId == null) {
            sql = sql.replaceAll("WAREHOUSE_FILTER", " AND warehouse_id = :warehouseId ");
            params.put("warehouseId", warehouseId);
        } else if (warehouseId == null && itemId != null) {
            sql = sql.replaceAll("WAREHOUSE_FILTER", " AND item_id = :itemId ");
            params.put("itemId", itemId);
        } else {
            sql = sql.replaceAll("WAREHOUSE_FILTER", " AND warehouse_id = :warehouseId AND item_id = :itemId ");
            params.put("warehouseId", warehouseId);
            params.put("itemId", itemId);
        }
        return jdbcTemplate.query(sql, params, ItemsBalanceReportRow::of);
    }

    public List<ItemsBalanceRecordsReportRow> getItemsBalanceRecordsReport(LocalDate start, LocalDate end, Long warehouseId, Long itemId) {
        var params = new HashMap<String, Object>();
        params.put("start", start);
        params.put("end", end);
        params.put("warehouseId", warehouseId);
        params.put("itemId", itemId);
        return jdbcTemplate.query(ITEMS_BALANCE_RECORDS_REPORT_SQL, params, ItemsBalanceRecordsReportRow::of);
    }

}
