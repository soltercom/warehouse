package ru.altercom.spb.warehouse.warehouse;

public class WarehouseNotFoundException extends RuntimeException {
    public WarehouseNotFoundException(Long id) {
        super(String.format("Warehouse with id %d not found.", id));
    }
}
