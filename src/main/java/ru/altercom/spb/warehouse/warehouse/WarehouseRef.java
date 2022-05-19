package ru.altercom.spb.warehouse.warehouse;

public interface WarehouseRef {

    Long getId();

    String getName();

    static WarehouseRef empty() {
        return new WarehouseRef() {
            @Override
            public Long getId() {
                return null;
            }

            @Override
            public String getName() {
                return "";
            }
        };
    }

}
