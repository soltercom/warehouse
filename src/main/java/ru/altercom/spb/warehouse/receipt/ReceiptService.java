package ru.altercom.spb.warehouse.receipt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.altercom.spb.warehouse.item.ItemRepository;
import ru.altercom.spb.warehouse.items_balance.ItemsBalanceService;
import ru.altercom.spb.warehouse.system.TransactionManager;
import ru.altercom.spb.warehouse.table.TableData;
import ru.altercom.spb.warehouse.warehouse.WarehouseRef;
import ru.altercom.spb.warehouse.warehouse.WarehouseRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class ReceiptService {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptService.class);

    private final TransactionManager transactionManager;
    private final ReceiptRepository receiptRepo;
    private final ReceiptRowRepository receiptRowRepo;
    private final WarehouseRepository warehouseRepo;
    private final ItemRepository itemRepo;
    private final ItemsBalanceService itemsBalanceService;

    public ReceiptService(TransactionManager transactionManager,
                          ReceiptRepository receiptRepo,
                          ReceiptRowRepository receiptRowRepo,
                          WarehouseRepository warehouseRepo,
                          ItemRepository itemRepo,
                          ItemsBalanceService itemsBalanceService) {
        this.transactionManager = transactionManager;
        this.receiptRepo = receiptRepo;
        this.receiptRowRepo = receiptRowRepo;
        this.warehouseRepo = warehouseRepo;
        this.itemRepo = itemRepo;
        this.itemsBalanceService = itemsBalanceService;
    }

    public ReceiptForm findById(Long id) {
        var receipt = receiptRepo.findById(id)
            .orElseThrow(() -> new ReceiptNotFoundException(id));
        var receiptDao = populate(receipt);

        var receiptList = receiptRowRepo.findAllByReceiptIdOrderByIdAsc(receipt.getId());
        var receiptDaoList = populateRow(receiptList);

        return new ReceiptForm(receiptDao.id(),
                               receiptDao.date(),
                               receiptDao.warehouseId(),
                               receiptDao.warehouseName(),
                               receiptDao.comment(),
                               receiptDaoList);
    }

    public void save(ReceiptForm receiptForm) {
        Objects.requireNonNull(receiptForm);

        var receipt = Receipt.of(receiptForm);
        transactionManager.doInTransaction(() -> {
            var savedReceipt = receiptRepo.save(receipt);
            logger.info("Receipt is saved: {}", savedReceipt);

            var receiptRowList = receiptForm.getRows()
                    .stream()
                    .map(i -> ReceiptRow.of(savedReceipt.getId(), i))
                    .toList();

            receiptRowRepo.deleteByReceiptId(savedReceipt.getId());
            receiptRowRepo.saveAll(receiptRowList);

            itemsBalanceService.save(savedReceipt, receiptRowList);

            return true;
        });
    }

    public TableData getTable(int draw, int start, int size, String search, Sort.Direction dir) {
        var pageRequest = PageRequest.of(start / size, size, dir, "date");

        var data = receiptRepo.findAll(pageRequest);

        return new TableData(draw, data.getTotalElements(), data.getTotalElements(),
                populate(data.getContent()));
    }

    public ReceiptForm emptyReceiptForm() {
        return ReceiptForm.empty();
    }

    public ReceiptRowDao emptyReceiptRowDao(Long receiptId) {
        return ReceiptRowDao.empty(receiptId);
    }

    public ReceiptDao populate(Receipt receipt) {
        var warehouseRef = warehouseRepo
                .getById(receipt.getWarehouseId())
                .orElse(WarehouseRef.empty());

        return ReceiptDao.of(receipt, warehouseRef.getName());
    }

    public List<ReceiptDao> populate(List<Receipt> receiptList) {
        var ids = receiptList.stream().map(Receipt::getWarehouseId).toList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        var list = warehouseRepo.populate(ids);
        var map = new HashMap<Long, String>();
        list.forEach(i -> map.put(i.getId(), i.getName()));
        return receiptList.stream()
                .map(i -> ReceiptDao.of(i, map.get(i.getWarehouseId())))
                .toList();
    }

    public List<ReceiptRowDao> populateRow(List<ReceiptRow> receiptRowList) {
        var ids = receiptRowList.stream().map(ReceiptRow::getItemId).toList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        var list = itemRepo.populate(ids);
        var map = new HashMap<Long, String>();
        list.forEach(i -> map.put(i.getId(), i.getName()));
        return receiptRowList.stream()
                .map(i -> ReceiptRowDao.of(i, map.get(i.getItemId())))
                .toList();
    }

    public String getFormTitle(ReceiptForm receiptForm) {
        return receiptForm.isNew() ? "Receipt (new)" : "Receipt (" + receiptForm.getId() + ")";
    }
}
