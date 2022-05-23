package ru.altercom.spb.warehouse.transfer;

public class TransferNotFoundException extends RuntimeException {

    public TransferNotFoundException(Long id) {
        super(String.format("Transfer with id %d not found.", id));
    }

}
