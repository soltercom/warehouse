package ru.altercom.spb.warehouse.reports;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.altercom.spb.warehouse.table.TableData;

import java.time.LocalDate;

@RequestMapping("reports")
@Controller
public class ReportsController {

    private static final String ITEMS_BALANCE_REPORT = "/reports/items_balance/report";
    private static final String ITEMS_BALANCE_REPORT_RECORDS = "/reports/items_balance_records/report";

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping("/items-balance")
    public String doItemsBalanceReport(ModelMap model) {
        model.put("form", reportsService.getItemsBalanceReportForm());
        return ITEMS_BALANCE_REPORT;
    }

    @GetMapping("/items-balance/table")
    @ResponseBody
    public TableData getItemsBalanceReport(@RequestParam("draw") int draw,
                                           @Nullable @RequestParam("start-date") LocalDate startDate,
                                           @Nullable @RequestParam("end-date") LocalDate endDate,
                                           @Nullable @RequestParam("warehouse-id") Long warehouseId,
                                           @Nullable @RequestParam("item-id") Long itemId) {
        return reportsService.getItemsBalanceReport(draw, startDate, endDate, warehouseId, itemId);
    }

    @GetMapping("/items-balance-records")
    @ResponseBody
    public TableData getItemsBalanceRecordsReport(ModelMap model,
                                              @RequestParam("start-date") LocalDate startDate,
                                              @RequestParam("end-date") LocalDate endDate,
                                              @RequestParam("warehouse-id") Long warehouseId,
                                              @RequestParam("item-id") Long itemId) {
        return reportsService.getItemsBalanceRecordsReport(startDate, endDate, warehouseId, itemId);
    }
}
