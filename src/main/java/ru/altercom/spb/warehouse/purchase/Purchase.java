package ru.altercom.spb.warehouse.purchase;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Table("PURCHASES")
@Getter
public class Purchase {

    @Id
    private final Long id;

    @NotNull
    private final LocalDate date;

    @NotNull
    private final Long warehouseId;

    private final String comment;

    @PersistenceConstructor
    public Purchase(Long id, LocalDate date, Long warehouseId, String comment) {
        this.id = id;
        this.date = date;
        this.warehouseId = warehouseId;
        this.comment = comment;
    }

    public static Purchase empty() {
        return new Purchase(null, LocalDate.now(), null, "");
    }

    public static Purchase of(PurchaseForm purchaseForm) {
        return new Purchase(purchaseForm.getId(),
                purchaseForm.getDate(),
                purchaseForm.getWarehouseId(),
                purchaseForm.getComment());
    }

}
