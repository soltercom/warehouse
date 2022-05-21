package ru.altercom.spb.warehouse.purchase;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class PurchaseRowDao {

    private Long id;

    @NotNull
    private Long purchaseId;

    @NotNull
    private Long itemId;

    private String itemName;

    private BigDecimal quantity;

    public PurchaseRowDao() {}

    public PurchaseRowDao(Long id, Long purchaseId, Long itemId, String itemName, BigDecimal quantity) {
        this.id = id;
        this.purchaseId = purchaseId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public static PurchaseRowDao of(PurchaseRow purchaseRow, String itemName) {
        return new PurchaseRowDao(
                purchaseRow.getId(),
                purchaseRow.getPurchaseId(),
                purchaseRow.getItemId(),
                itemName,
                purchaseRow.getQuantity());
    }

    public static PurchaseRowDao empty(Long receiptId) {
        return new PurchaseRowDao(null, receiptId, null, "", BigDecimal.ZERO);
    }

}
