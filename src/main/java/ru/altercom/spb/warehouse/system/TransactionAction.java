package ru.altercom.spb.warehouse.system;

import java.util.function.Supplier;

public interface TransactionAction<T> extends Supplier<T> {
}
