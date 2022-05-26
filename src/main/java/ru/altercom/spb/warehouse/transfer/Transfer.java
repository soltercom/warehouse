package ru.altercom.spb.warehouse.transfer;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Table("Transfers")
@Getter
public class Transfer {

    public static final String RECORDER_TYPE = "Transfer";

    @Id
    private final Long id;

    @NotNull
    private final LocalDate date;

    @NotNull
    private final Long warehouseFromId;

    @NotNull
    private final Long warehouseToId;

    private final String comment;

    @PersistenceConstructor
    public Transfer(Long id, LocalDate date, Long warehouseFromId, Long warehouseToId, String comment) {
        this.id = id;
        this.date = date;
        this.warehouseFromId = warehouseFromId;
        this.warehouseToId = warehouseToId;
        this.comment = comment;
    }

    public static Transfer empty() {
        return new Transfer(null, LocalDate.now(), null, null,"");
    }

    public static Transfer of(TransferForm transferForm) {
        return new Transfer(transferForm.getId(),
                transferForm.getDate(),
                transferForm.getWarehouseFromId(),
                transferForm.getWarehouseToId(),
                transferForm.getComment());
    }

}
