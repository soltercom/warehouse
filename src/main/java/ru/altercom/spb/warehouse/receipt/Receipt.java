package ru.altercom.spb.warehouse.receipt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Table("RECEIPTS")
@Getter @Setter
public class Receipt {

    @Id
    private Long id;

    @NotNull
    private LocalDate date;

    @NotNull
    private Long warehouseId;

    private String comment;

    @Valid
    private List<ReceiptRow> rows = new ArrayList<>();

    public Receipt() {}

    @PersistenceConstructor
    public Receipt(Long id, LocalDate date, Long warehouseId, String comment, List<ReceiptRow> rows) {
        this.id = id;
        this.date = date;
        this.warehouseId = warehouseId;
        this.comment = comment;
        this.rows = rows;
    }

    public static Receipt empty() {
        return new Receipt(null, LocalDate.now(), null, "", new ArrayList<>());
    }

    public Receipt withRows(List<ReceiptRow> rowsBefore) {
        var rowsAfter = rowsBefore.stream()
                .filter(row -> row.getItemId() != null && row.getQuantity() != null)
                .toList();
        return new Receipt(this.getId(), this.getDate(), this.getWarehouseId(), this.getComment(), rowsAfter);
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
