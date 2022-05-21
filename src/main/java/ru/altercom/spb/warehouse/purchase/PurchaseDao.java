package ru.altercom.spb.warehouse.purchase;

import java.time.LocalDate;

public record PurchaseDao(Long id, LocalDate date, Long warehouseId, String warehouseName, String comment) {

    public static PurchaseDao of(Purchase purchase, String warehouseName) {
        return new PurchaseDao(purchase.getId(),
                purchase.getDate(),
                purchase.getWarehouseId(),
                warehouseName,
                purchase.getComment());
    }

    public static PurchaseDao empty() {
        return PurchaseDao.of(Purchase.empty(), "");
    }

}
