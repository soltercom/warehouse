package ru.altercom.spb.warehouse.purchase;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface PurchaseRepository extends PagingAndSortingRepository<Purchase, Long> {
}
