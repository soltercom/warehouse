package ru.altercom.spb.warehouse.transfer;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TransferForm {

    private Long id;

    @NotNull
    private LocalDate date;

    @NotNull
    private Long warehouseFromId;

    private String warehouseFromName;

    @NotNull
    private Long warehouseToId;

    private String warehouseToName;

    private String comment;

    @Valid
    private List<TransferRowDao> rows = new ArrayList<>();

    public boolean isNew() {
        return id == null;
    }

    public TransferForm() {}

    public TransferForm(Long id, LocalDate date,
                        Long warehouseFromId, String warehouseFromName,
                        Long warehouseToId, String warehouseToName,
                        String comment, List<TransferRowDao> rows) {
        this.id = id;
        this.date = date;
        this.warehouseFromId = warehouseFromId;
        this.warehouseFromName = warehouseFromName;
        this.warehouseToId = warehouseToId;
        this.warehouseToName = warehouseToName;
        this.comment = comment;
        this.rows = rows;
    }

    public static TransferForm empty() {
        return new TransferForm(null, LocalDate.now(),
                null, "",
                null, "",
                "" , new ArrayList<>());
    }

}
