package ru.altercom.spb.warehouse.warehouse;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
public class WarehouseRefFormatter implements Formatter<WarehouseRef> {

    private final WarehouseService warehouseService;

    public WarehouseRefFormatter(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Override
    public WarehouseRef parse(String id, Locale locale) throws ParseException {
        return warehouseService.getWarehouseRefById(Long.valueOf(id));
    }

    @Override
    public String print(WarehouseRef warehouseRef, Locale locale) {
        return warehouseRef.getId() == null ? "" : warehouseRef.getId().toString();
    }
}
