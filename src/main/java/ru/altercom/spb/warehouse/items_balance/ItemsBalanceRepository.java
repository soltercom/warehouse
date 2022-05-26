package ru.altercom.spb.warehouse.items_balance;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ItemsBalanceRepository extends CrudRepository<ItemsBalance, Long> {

    @Modifying
    @Query("DELETE FROM items_balance WHERE recorder_id = :recorderId AND recorder_type = :recorderType")
    void deleteByRecorderIdAndRecorderType(@Param("recorderId") Long recorderId,
                                           @Param("recorderType") String recorderType);

}
