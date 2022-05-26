package ru.altercom.spb.warehouse.transfer;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class TransferRowDao {

    private Long id;

    @NotNull
    private Long transferId;

    @NotNull(message = "Item should be filled")
    private Long itemId;

    private String itemName;

    private BigDecimal quantity;

    public TransferRowDao() {}

    public TransferRowDao(Long id, Long transferId, Long itemId, String itemName, BigDecimal quantity) {
        this.id = id;
        this.transferId = transferId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public static TransferRowDao of(TransferRow transferRow, String itemName) {
        return new TransferRowDao(
                transferRow.getId(),
                transferRow.getTransferId(),
                transferRow.getItemId(),
                itemName,
                transferRow.getQuantity());
    }

    public static TransferRowDao empty(Long transferId) {
        return new TransferRowDao(null, transferId, null, "", BigDecimal.ZERO);
    }

}
