package ru.altercom.spb.warehouse.reports;

import org.springframework.stereotype.Service;
import ru.altercom.spb.warehouse.table.TableData;

import java.time.LocalDate;
import java.util.Collections;

@Service
public class ReportsService {

    private final ReportsRepository itemsBalanceReportRepo;

    public ReportsService(ReportsRepository itemsBalanceReportRepo) {
        this.itemsBalanceReportRepo = itemsBalanceReportRepo;
    }

    public ItemsBalanceReportForm getItemsBalanceReportForm() {
        return ItemsBalanceReportForm.create();
    }

    public TableData getItemsBalanceReport(int draw, LocalDate startDate, LocalDate endDate,
                                           Long warehouseId, Long itemId) {
        if (startDate == null || endDate == null) {
            return new TableData(draw, 0, 0, Collections.emptyList());
        }
        var list = itemsBalanceReportRepo.getItemsBalanceReport(startDate, endDate, warehouseId, itemId);
        return new TableData(draw, list.size(), list.size(), list);
    }

    public ItemsBalanceRecordsReportForm getItemsBalanceRecordsReportForm(LocalDate start, LocalDate end,
                                                                          Long warehouseId, Long itemId) {
        return new ItemsBalanceRecordsReportForm(start, end, warehouseId, "", itemId, "");
    }

    public TableData getItemsBalanceRecordsReport(LocalDate startDate, LocalDate endDate,
                                                  Long warehouseId, Long itemId) {
        var list = itemsBalanceReportRepo
                .getItemsBalanceRecordsReport(startDate, endDate, warehouseId, itemId);
        return new TableData(0, list.size(), list.size(), list);
    }

}
