package ru.altercom.spb.warehouse.reports;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.altercom.spb.warehouse.warehouse.WarehouseRef;

import java.time.LocalDate;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Getter
public class ItemsBalanceReportForm {

    private final LocalDate start;

    private final LocalDate end;

    private final Long warehouseId;

    private final String warehouseName;

    private final Long itemId;

    private final String itemName;

    public ItemsBalanceReportForm(LocalDate start, LocalDate end,
                                  Long warehouseId, String warehouseName,
                                  Long itemId, String itemName) {
        this.start = start;
        this.end = end;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public static ItemsBalanceReportForm create() {
        return new ItemsBalanceReportForm(
                LocalDate.now().with(firstDayOfMonth()),
                LocalDate.now().with(lastDayOfMonth()),
                null, "",
                null, "");
    }

}
