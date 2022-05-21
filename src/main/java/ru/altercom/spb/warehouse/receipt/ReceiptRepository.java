package ru.altercom.spb.warehouse.receipt;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReceiptRepository extends PagingAndSortingRepository<Receipt, Long> {
}
