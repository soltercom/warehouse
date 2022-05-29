package ru.altercom.spb.warehouse.reports;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public record ItemsBalanceRecordsReportRow(Long id, Long recorderId, String recorderType, LocalDate date,
                                           BigDecimal receipt,
                                           BigDecimal expense) {

    public static ItemsBalanceRecordsReportRow of(ResultSet row, int rowNum) throws SQLException {
        return new ItemsBalanceRecordsReportRow(
                row.getLong("id"),
                row.getLong("recorder_id"),
                row.getString("recorder_type"),
                row.getDate("date").toLocalDate(),
                row.getBigDecimal("receipt"),
                row.getBigDecimal("expense"));
    }
}
