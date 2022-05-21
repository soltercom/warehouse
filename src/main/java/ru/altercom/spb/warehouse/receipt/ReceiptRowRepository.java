package ru.altercom.spb.warehouse.receipt;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReceiptRowRepository extends PagingAndSortingRepository<ReceiptRow, Long> {

    List<ReceiptRow> findAllByReceiptIdOrderByIdAsc(Long receiptId);

    @Modifying
    @Query("DELETE FROM receipts_row WHERE receipt_id = :receiptId")
    void deleteByReceiptId(@Param("receiptId") Long receiptId);
}
