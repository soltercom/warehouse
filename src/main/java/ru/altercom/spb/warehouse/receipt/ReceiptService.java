package ru.altercom.spb.warehouse.receipt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.altercom.spb.warehouse.item.ItemRepository;
import ru.altercom.spb.warehouse.system.TransactionManager;
import ru.altercom.spb.warehouse.table.TableData;
import ru.altercom.spb.warehouse.warehouse.WarehouseRef;
import ru.altercom.spb.warehouse.warehouse.WarehouseRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ReceiptService {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptService.class);

    private final TransactionManager transactionManager;
    private final ReceiptRepository receiptRepo;
    private final WarehouseRepository warehouseRepository;
    private final ItemRepository itemRepository;

    public ReceiptService(TransactionManager transactionManager,
                          ReceiptRepository receiptRepo, WarehouseRepository warehouseRepository, ItemRepository itemRepository) {
        this.transactionManager = transactionManager;
        this.receiptRepo = receiptRepo;
        this.warehouseRepository = warehouseRepository;
        this.itemRepository = itemRepository;
    }

    public Receipt findById(Long id) {
        return receiptRepo.findById(id)
            .orElseThrow(() -> new ReceiptNotFoundException(id));
    }

    public Long save(Receipt receipt) {
        Objects.requireNonNull(receipt);
        var createdReceipt = transactionManager.doInTransaction(() -> {
            var savedReceipt = receiptRepo.save(receipt);
            logger.info("Receipt is saved: {}", savedReceipt);
            return savedReceipt;
        });
        return createdReceipt.getId();
    }

    public TableData getTable(int draw, int start, int size, String search, Sort.Direction dir) {

        //var pageRequest = PageRequest.of(start / size, size, dir, "date");

        //var data = receiptRepo.findAllBy(pageRequest);

        var data = receiptRepo.getList(start, size,"w.name DESC");
        var total = receiptRepo.count();

        return new TableData(draw, total, total, data);

    }

    public Map<String, String> populate(Receipt receipt) {
        var data = new HashMap<String, String>();
        if (receipt.getId() == null) {
            return data;
        }

        var warehouseRef = warehouseRepository
                .getWarehouseRefById(receipt.getWarehouseId())
                .orElse(WarehouseRef.empty());
        data.put("warehouse" + warehouseRef.getId(), warehouseRef.getName());

        var ids = receipt.getRows()
                .stream()
                .map(ReceiptRow::getItemId)
                .toList();
        if (!ids.isEmpty()) {
            var list = itemRepository.populate(ids);
            for (var item: list) {
                data.put("item" + item.getId(), item.getName());
            }
        }

        return data;
    }
}
