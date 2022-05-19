package ru.altercom.spb.warehouse.receipt;

import java.time.LocalDate;

public record ReceiptList(Long id, LocalDate date, Long warehouseId, String warehouseName, String comment) { }
