package ru.altercom.spb.warehouse.transfer;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferRowRepository extends PagingAndSortingRepository<TransferRow, Long> {

    List<TransferRow> findAllByTransferIdOrderByIdAsc(Long transferId);

    @Modifying
    @Query("DELETE FROM transfers_row WHERE transfer_id = :transferId")
    void deleteByTransferId(@Param("transferId") Long transferId);

}
