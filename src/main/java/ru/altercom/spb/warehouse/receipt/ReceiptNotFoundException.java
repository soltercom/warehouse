package ru.altercom.spb.warehouse.receipt;

public class ReceiptNotFoundException extends RuntimeException {

    public ReceiptNotFoundException(Long id) {
        super(String.format("Receipt with id %d not found.", id));
    }

}
