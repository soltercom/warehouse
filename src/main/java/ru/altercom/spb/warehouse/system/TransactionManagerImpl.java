package ru.altercom.spb.warehouse.system;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionManagerImpl implements TransactionManager {

    @Override
    @Transactional
    public <T> T doInTransaction(TransactionAction<T> action) {
        return action.get();
    }

}
