package ru.altercom.spb.warehouse.receipt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table("RECEIPTS_ROW")
@Getter
public class ReceiptRow {

    @Id
    private final Long id;

    @NotNull
    private final Long receiptId;

    @NotNull
    private final Long itemId;

    @NotNull
    @NumberFormat(pattern = "[0-9]*\\.?[0-9]*")
    private final BigDecimal quantity;

    @PersistenceConstructor
    public ReceiptRow(Long id, Long receiptId, Long itemId, BigDecimal quantity) {
        this.id = id;
        this.receiptId = receiptId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public static ReceiptRow empty(Long receiptId) {
        return new ReceiptRow(null, receiptId, null, BigDecimal.ZERO);
    }

    public static ReceiptRow of(Long receiptId, ReceiptRowDao receiptRowDao) {
        return new ReceiptRow(receiptRowDao.getId(), receiptId, receiptRowDao.getItemId(), receiptRowDao.getQuantity());
    }

}
