package ru.altercom.spb.warehouse.receipt;

import java.time.LocalDate;

public interface ReceiptListForm {

        Long getId();

        LocalDate getDate();

        Long getWarehouseId();

        String getComment();

}
