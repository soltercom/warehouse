package ru.altercom.spb.warehouse.reports;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.altercom.spb.warehouse.table.TableData;

import java.time.LocalDate;
import java.util.Collections;

@RequestMapping("reports")
@Controller
public class ReportsController {

    private static final String ITEMS_BALANCE_REPORT = "/reports/items_balance/report";

    private final ItemsBalanceReportRepository itemsBalanceReportRepo;

    public ReportsController(ItemsBalanceReportRepository itemsBalanceReportRepo) {
        this.itemsBalanceReportRepo = itemsBalanceReportRepo;
    }

    @GetMapping("/items-balance")
    public String doItemsBalanceReport(ModelMap model) {
        model.put("form", ItemsBalanceReportForm.create());
        return ITEMS_BALANCE_REPORT;
    }

    @GetMapping("/items-balance/table")
    @ResponseBody
    public TableData getItemsBalanceReport(@RequestParam("draw") int draw,
                                           @Nullable @RequestParam("start-date") LocalDate startDate,
                                           @Nullable @RequestParam("end-date") LocalDate endDate,
                                           @Nullable @RequestParam("warehouse-id") Long warehouseId,
                                           @Nullable @RequestParam("item-id") Long itemId) {
        if (startDate == null || endDate == null) {
            return new TableData(draw, 0, 0, Collections.emptyList());
        }
        var list = itemsBalanceReportRepo.getItemsBalanceReport(startDate, endDate, warehouseId, itemId);
        return new TableData(draw, list.size(), list.size(), list);
    }


}
