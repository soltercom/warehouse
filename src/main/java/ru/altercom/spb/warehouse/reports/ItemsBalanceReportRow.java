package ru.altercom.spb.warehouse.reports;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public record ItemsBalanceReportRow(
        Long warehouse_id, String warehouse_name,
        Long item_id, String item_name,
        BigDecimal openingBalance, BigDecimal receipt, BigDecimal expense, BigDecimal closingBalance) {

    public static ItemsBalanceReportRow of(ResultSet row, int rowNum) throws SQLException {
        return new ItemsBalanceReportRow(
                row.getLong("warehouse_id"),
                row.getString("warehouse_name"),
                row.getLong("item_id"),
                row.getString("item_name"),
                row.getBigDecimal("opening_balance"),
                row.getBigDecimal("receipt"),
                row.getBigDecimal("expense"),
                row.getBigDecimal("closing_balance"));
    }

}
