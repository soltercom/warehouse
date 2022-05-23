package ru.altercom.spb.warehouse.transfer;

import java.time.LocalDate;

public record TransferDao(Long id, LocalDate date,
                          Long warehouseFromId, String warehouseFromName,
                          Long warehouseToId, String warehouseToName,
                          String comment) {

    public static TransferDao of(Transfer transfer, String warehouseFromName, String warehouseToName) {
        return new TransferDao(transfer.getId(),
                transfer.getDate(),
                transfer.getWarehouseFromId(),
                warehouseFromName,
                transfer.getWarehouseToId(),
                warehouseToName,
                transfer.getComment());
    }

    public static TransferDao empty() {
        return TransferDao.of(Transfer.empty(), "", "");
    }

}
