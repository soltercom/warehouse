package ru.altercom.spb.warehouse.purchase;

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
public class PurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);

    private final TransactionManager transactionManager;
    private final PurchaseRepository purchaseRepo;
    private final PurchaseRowRepository purchaseRowRepo;
    private final WarehouseRepository warehouseRepo;
    private final ItemRepository itemRepo;
    private final ItemsBalanceService itemsBalanceService;

    public PurchaseService(TransactionManager transactionManager,
                           PurchaseRepository purchaseRepo,
                           PurchaseRowRepository purchaseRowRepo,
                           WarehouseRepository warehouseRepo,
                           ItemRepository itemRepo, ItemsBalanceService itemsBalanceService) {
        this.transactionManager = transactionManager;
        this.purchaseRepo = purchaseRepo;
        this.purchaseRowRepo = purchaseRowRepo;
        this.warehouseRepo = warehouseRepo;
        this.itemRepo = itemRepo;
        this.itemsBalanceService = itemsBalanceService;
    }

    public PurchaseForm findById(Long id) {
        var purchase = purchaseRepo.findById(id)
                .orElseThrow(() -> new PurchaseNotFoundException(id));
        var purchaseDao = populate(purchase);

        var purchaseList = purchaseRowRepo.findAllByPurchaseIdOrderByIdAsc(purchase.getId());
        var purchaseDaoList = populateRow(purchaseList);

        return new PurchaseForm(purchaseDao.id(),
                purchaseDao.date(),
                purchaseDao.warehouseId(),
                purchaseDao.warehouseName(),
                purchaseDao.comment(),
                purchaseDaoList);
    }

    public void save(PurchaseForm purchaseForm) {
        Objects.requireNonNull(purchaseForm);

        var purchase = Purchase.of(purchaseForm);
        transactionManager.doInTransaction(() -> {
            var savedPurchase = purchaseRepo.save(purchase);
            logger.info("Purchase is saved: {}", savedPurchase);

            var purchaseRowList = purchaseForm.getRows()
                    .stream()
                    .map(i -> PurchaseRow.of(savedPurchase.getId(), i))
                    .toList();

            purchaseRowRepo.deleteByPurchaseId(savedPurchase.getId());
            purchaseRowRepo.saveAll(purchaseRowList);

            itemsBalanceService.save(savedPurchase, purchaseRowList);
            return true;
        });

    }

    public TableData getTable(int draw, int start, int size, String search, Sort.Direction dir) {
        var pageRequest = PageRequest.of(start / size, size, dir, "date");

        var data = purchaseRepo.findAll(pageRequest);

        return new TableData(draw, data.getTotalElements(), data.getTotalElements(),
                populate(data.getContent()));
    }

    public PurchaseForm emptyPurchaseForm() {
        return PurchaseForm.empty();
    }

    public PurchaseRowDao emptyPurchaseRowDao(Long purchaseId) {
        return PurchaseRowDao.empty(purchaseId);
    }

    public PurchaseDao populate(Purchase purchase) {
        var warehouseRef = warehouseRepo
                .getById(purchase.getWarehouseId())
                .orElse(WarehouseRef.empty());

        return PurchaseDao.of(purchase, warehouseRef.getName());
    }

    public List<PurchaseDao> populate(List<Purchase> purchaseList) {
        var ids = purchaseList.stream()
                .map(Purchase::getWarehouseId).toList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        var list = warehouseRepo.populate(ids);
        var map = new HashMap<Long, String>();
        list.forEach(i -> map.put(i.getId(), i.getName()));
        return purchaseList.stream()
                .map(i -> PurchaseDao.of(i, map.get(i.getWarehouseId())))
                .toList();
    }

    public List<PurchaseRowDao> populateRow(List<PurchaseRow> purchaseRowList) {
        var ids = purchaseRowList.stream()
                .map(PurchaseRow::getItemId).toList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        var list = itemRepo.populate(ids);
        var map = new HashMap<Long, String>();
        list.forEach(i -> map.put(i.getId(), i.getName()));
        return purchaseRowList.stream()
                .map(i -> PurchaseRowDao.of(i, map.get(i.getItemId())))
                .toList();
    }

    public String getFormTitle(PurchaseForm purchaseForm) {
        return purchaseForm.isNew() ? "Purchase (new)" : "Purchase (" + purchaseForm.getId() + ")";
    }
}
