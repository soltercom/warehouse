package ru.altercom.spb.warehouse.receipt;

import java.time.LocalDate;

public record ReceiptDao(Long id, LocalDate date, Long warehouseId, String warehouseName, String comment) {

    public static ReceiptDao of(Receipt receipt, String warehouseName) {
        return new ReceiptDao(receipt.getId(),
                               receipt.getDate(),
                               receipt.getWarehouseId(),
                               warehouseName,
                               receipt.getComment());
    }

    public static ReceiptDao empty() {
        return ReceiptDao.of(Receipt.empty(), "");
    }

}
