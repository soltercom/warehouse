package ru.altercom.spb.warehouse.receipt;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReceiptForm {

    private Long id;

    @NotNull
    private LocalDate date;

    @NotNull
    private Long warehouseId;

    private String warehouseName;

    private String comment;

    @Valid
    private List<ReceiptRowDao> rows = new ArrayList<>();

    public boolean isNew() {
        return id == null;
    }

    public ReceiptForm() {}

    public ReceiptForm(Long id, LocalDate date, Long warehouseId, String warehouseName, String comment, List<ReceiptRowDao> rows) {
        this.id = id;
        this.date = date;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.comment = comment;
        this.rows = rows;
    }

    public static ReceiptForm empty() {
        return new ReceiptForm(null, LocalDate.now(), null, "", "" , new ArrayList<>());
    }

}
