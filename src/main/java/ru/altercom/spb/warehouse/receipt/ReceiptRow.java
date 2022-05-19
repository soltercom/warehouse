package ru.altercom.spb.warehouse.receipt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table("RECEIPTS_ROW")
@Getter @Setter
public class ReceiptRow {

    @NotNull
    private Long itemId;

    @NotNull
    @NumberFormat(pattern = "[0-9]*\\.?[0-9]*")
    private BigDecimal quantity;

    @PersistenceConstructor
    public ReceiptRow(Long itemId, BigDecimal quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public ReceiptRow() {}

    public static ReceiptRow empty() {
        return new ReceiptRow(null, BigDecimal.ZERO);
    }

}
