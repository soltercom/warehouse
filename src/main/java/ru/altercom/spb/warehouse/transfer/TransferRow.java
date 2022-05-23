package ru.altercom.spb.warehouse.transfer;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Table("TRANSFERS_ROW")
@Getter
public class TransferRow {

    @Id
    private final Long id;

    @NotNull
    private final Long transferId;

    @NotNull
    private final Long itemId;

    @NotNull
    @NumberFormat(pattern = "[0-9]*\\.?[0-9]*")
    private final BigDecimal quantity;

    @PersistenceConstructor
    public TransferRow(Long id, Long transferId, Long itemId, BigDecimal quantity) {
        this.id = id;
        this.transferId = transferId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public static TransferRow empty(Long transferId) {
        return new TransferRow(null, transferId, null, BigDecimal.ZERO);
    }

    public static TransferRow of(Long transferId, TransferRowDao transferRowDao) {
        return new TransferRow(transferRowDao.getId(), transferId, transferRowDao.getItemId(), transferRowDao.getQuantity());
    }

}
