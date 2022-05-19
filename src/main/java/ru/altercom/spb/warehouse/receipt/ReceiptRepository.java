package ru.altercom.spb.warehouse.receipt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface ReceiptRepository extends PagingAndSortingRepository<Receipt, Long> {

    @Transactional(readOnly = true)
    Page<ReceiptListForm> findAllBy(Pageable page);

    @Transactional(readOnly = true)
    @Query("""
        SELECT r.id, r.date as date, r.comment, w.id as warehouse_id, w.name as warehouse_name FROM receipts AS r 
        LEFT JOIN warehouses as w ON r.warehouse_id = w.id 
        ORDER BY :order LIMIT :start, :size 
    """)
    List<ReceiptList> getList(@Param("start") long start, @Param("size") long size, @Param("order") String order);

}
