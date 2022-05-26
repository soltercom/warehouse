package ru.altercom.spb.warehouse.receipt;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Table("RECEIPTS")
@Getter
public class Receipt {

    public static final String RECORDER_TYPE = "Receipt";

    @Id
    private final Long id;

    @NotNull
    private final LocalDate date;

    @NotNull
    private final Long warehouseId;

    private final String comment;

    @PersistenceConstructor
    public Receipt(Long id, LocalDate date, Long warehouseId, String comment) {
        this.id = id;
        this.date = date;
        this.warehouseId = warehouseId;
        this.comment = comment;
    }

    public static Receipt empty() {
        return new Receipt(null, LocalDate.now(), null, "");
    }

    public static Receipt of(ReceiptForm receiptForm) {
        return new Receipt(receiptForm.getId(),
                receiptForm.getDate(),
                receiptForm.getWarehouseId(),
                receiptForm.getComment());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Receipt receipt)) return false;
        return getId().equals(receipt.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }
}
