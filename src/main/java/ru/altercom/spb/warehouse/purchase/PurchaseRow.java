package ru.altercom.spb.warehouse.purchase;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Table("PURCHASES_ROW")
@Getter
public class PurchaseRow {

    @Id
    private final Long id;

    @NotNull
    private final Long purchaseId;

    @NotNull
    private final Long itemId;

    @NotNull
    @NumberFormat(pattern = "[0-9]*\\.?[0-9]*")
    private final BigDecimal quantity;

    @PersistenceConstructor
    public PurchaseRow(Long id, Long purchaseId, Long itemId, BigDecimal quantity) {
        this.id = id;
        this.purchaseId = purchaseId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public static PurchaseRow empty(Long purchaseId) {
        return new PurchaseRow(null, purchaseId, null, BigDecimal.ZERO);
    }

    public static PurchaseRow of(Long purchaseId, PurchaseRowDao purchaseRowDao) {
        return new PurchaseRow(purchaseRowDao.getId(), purchaseId, purchaseRowDao.getItemId(), purchaseRowDao.getQuantity());
    }

}
