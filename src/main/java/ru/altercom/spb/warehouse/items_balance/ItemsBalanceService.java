package ru.altercom.spb.warehouse.items_balance;

import org.springframework.stereotype.Service;
import ru.altercom.spb.warehouse.purchase.Purchase;
import ru.altercom.spb.warehouse.purchase.PurchaseRow;
import ru.altercom.spb.warehouse.receipt.Receipt;
import ru.altercom.spb.warehouse.receipt.ReceiptRow;
import ru.altercom.spb.warehouse.transfer.Transfer;
import ru.altercom.spb.warehouse.transfer.TransferRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemsBalanceService {

    private final ItemsBalanceRepository itemsBalanceRepo;

    public ItemsBalanceService(ItemsBalanceRepository itemsBalanceRepo) {
        this.itemsBalanceRepo = itemsBalanceRepo;
    }

    public void save(Receipt receipt, List<ReceiptRow> receiptRow) {
        itemsBalanceRepo.deleteByRecorderIdAndRecorderType(receipt.getId(), Receipt.RECORDER_TYPE);

        var list = receiptRow.stream().map(row -> new ItemsBalance(null,
                receipt.getId(), Receipt.RECORDER_TYPE, 1, receipt.getDate(),
                receipt.getWarehouseId(), row.getItemId(),
                row.getQuantity())).toList();
        itemsBalanceRepo.saveAll(list);
    }

    public void save(Purchase purchase, List<PurchaseRow> purchaseRow) {
        itemsBalanceRepo.deleteByRecorderIdAndRecorderType(purchase.getId(), Purchase.RECORDER_TYPE);

        var list = purchaseRow.stream().map(row -> new ItemsBalance(null,
                purchase.getId(), Purchase.RECORDER_TYPE, 2, purchase.getDate(),
                purchase.getWarehouseId(), row.getItemId(),
                row.getQuantity())).toList();
        itemsBalanceRepo.saveAll(list);
    }

    public void save(Transfer transfer, List<TransferRow> transferRow) {
        itemsBalanceRepo.deleteByRecorderIdAndRecorderType(transfer.getId(), Transfer.RECORDER_TYPE);

        var listFrom = transferRow.stream().map(row -> new ItemsBalance(null,
                transfer.getId(), Transfer.RECORDER_TYPE, 2, transfer.getDate(),
                transfer.getWarehouseFromId(), row.getItemId(),
                row.getQuantity())).toList();
        var listTo = transferRow.stream().map(row -> new ItemsBalance(null,
                transfer.getId(), Transfer.RECORDER_TYPE, 1, transfer.getDate(),
                transfer.getWarehouseToId(), row.getItemId(),
                row.getQuantity())).toList();
        var list = new ArrayList<ItemsBalance>();
        list.addAll(listFrom);
        list.addAll(listTo);
        itemsBalanceRepo.saveAll(list);
    }

}
