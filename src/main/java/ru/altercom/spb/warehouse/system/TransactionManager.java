package ru.altercom.spb.warehouse.system;

public interface TransactionManager {

    <T> T doInTransaction(TransactionAction<T> action);

}
