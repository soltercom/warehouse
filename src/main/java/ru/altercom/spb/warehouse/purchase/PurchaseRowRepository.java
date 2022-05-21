package ru.altercom.spb.warehouse.purchase;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseRowRepository extends PagingAndSortingRepository<PurchaseRow, Long> {

    List<PurchaseRow> findAllByPurchaseIdOrderByIdAsc(Long purchaseId);

    @Modifying
    @Query("DELETE FROM purchases_row WHERE purchase_id = :purchaseId")
    void deleteByPurchaseId(@Param("purchaseId") Long purchaseId);

}
