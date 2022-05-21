package ru.altercom.spb.warehouse.receipt;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class ReceiptRowDao {

    private Long id;

    @NotNull
    private Long receiptId;

    @NotNull
    private Long itemId;

    private String itemName;

    private BigDecimal quantity;

    public ReceiptRowDao() {}

    public ReceiptRowDao(Long id, Long receiptId, Long itemId, String itemName, BigDecimal quantity) {
        this.id = id;
        this.receiptId = receiptId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public static ReceiptRowDao of(ReceiptRow receiptRow, String itemName) {
        return new ReceiptRowDao(
                receiptRow.getId(),
                receiptRow.getReceiptId(),
                receiptRow.getItemId(),
                itemName,
                receiptRow.getQuantity());
    }

    public static ReceiptRowDao empty(Long receiptId) {
        return new ReceiptRowDao(null, receiptId, null, "", BigDecimal.ZERO);
    }

}
