package ru.altercom.spb.warehouse.transfer;

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

import java.util.*;

@Service
public class TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    private final TransactionManager transactionManager;
    private final TransferRepository transferRepo;
    private final TransferRowRepository transferRowRepo;
    private final WarehouseRepository warehouseRepo;
    private final ItemRepository itemRepo;
    private final ItemsBalanceService itemsBalanceService;

    public TransferService(TransactionManager transactionManager,
                           TransferRepository transferRepo,
                           TransferRowRepository transferRowRepo,
                           WarehouseRepository warehouseRepo,
                           ItemRepository itemRepo, ItemsBalanceService itemsBalanceService) {
        this.transactionManager = transactionManager;
        this.transferRepo = transferRepo;
        this.transferRowRepo = transferRowRepo;
        this.warehouseRepo = warehouseRepo;
        this.itemRepo = itemRepo;
        this.itemsBalanceService = itemsBalanceService;
    }

    public TransferForm findById(Long id) {
        var transfer = transferRepo.findById(id)
                .orElseThrow(() -> new TransferNotFoundException(id));
        var transferDao = populate(transfer);

        var transferList = transferRowRepo.findAllByTransferIdOrderByIdAsc(transfer.getId());
        var transferDaoList = populateRow(transferList);

        return new TransferForm(transferDao.id(),
                transferDao.date(),
                transferDao.warehouseFromId(),
                transferDao.warehouseFromName(),
                transferDao.warehouseToId(),
                transferDao.warehouseToName(),
                transferDao.comment(),
                transferDaoList);
    }

    public void save(TransferForm transferForm) {
        Objects.requireNonNull(transferForm);

        var transfer = Transfer.of(transferForm);
        var createdTransfer = transactionManager.doInTransaction(() -> {
            var savedTransfer = transferRepo.save(transfer);
            logger.info("Transfer is saved: {}", savedTransfer);
            return savedTransfer;
        });

        var transferRowList = transferForm.getRows()
                .stream()
                .map(i -> TransferRow.of(createdTransfer.getId(), i))
                .toList();

        transactionManager.doInTransaction(() -> {
            transferRowRepo.deleteByTransferId(createdTransfer.getId());
            transferRowRepo.saveAll(transferRowList);

            itemsBalanceService.save(createdTransfer, transferRowList);
            return true;
        });

    }

    public TableData getTable(int draw, int start, int size, String search, Sort.Direction dir) {
        var pageRequest = PageRequest.of(start / size, size, dir, "date");

        var data = transferRepo.findAll(pageRequest);

        return new TableData(draw, data.getTotalElements(), data.getTotalElements(),
                populate(data.getContent()));
    }

    public TransferForm emptyTransferForm() {
        return TransferForm.empty();
    }

    public TransferRowDao emptyTransferRowDao(Long transferId) {
        return TransferRowDao.empty(transferId);
    }

    public TransferDao populate(Transfer transfer) {
        var warehouseFromRef = warehouseRepo
                .getById(transfer.getWarehouseFromId())
                .orElse(WarehouseRef.empty());
        var warehouseToRef = warehouseRepo
                .getById(transfer.getWarehouseToId())
                .orElse(WarehouseRef.empty());

        return TransferDao.of(transfer, warehouseFromRef.getName(), warehouseToRef.getName());
    }

    public List<TransferDao> populate(List<Transfer> transferList) {
        var idsFrom = transferList.stream()
                .map(Transfer::getWarehouseFromId).toList();
        var idsTo = transferList.stream()
                .map(Transfer::getWarehouseToId).toList();
        var ids = new ArrayList<>(idsFrom);
        ids.addAll(idsTo);
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        var list = warehouseRepo.populate(ids);
        var map = new HashMap<Long, String>();
        list.forEach(i -> map.put(i.getId(), i.getName()));
        return transferList.stream()
                .map(i -> TransferDao.of(i, map.get(i.getWarehouseFromId()), map.get(i.getWarehouseToId())))
                .toList();
    }

    public List<TransferRowDao> populateRow(List<TransferRow> transferRowList) {
        var ids = transferRowList.stream()
                .map(TransferRow::getItemId).toList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        var list = itemRepo.populate(ids);
        var map = new HashMap<Long, String>();
        list.forEach(i -> map.put(i.getId(), i.getName()));
        return transferRowList.stream()
                .map(i -> TransferRowDao.of(i, map.get(i.getItemId())))
                .toList();
    }

    public String getFormTitle(TransferForm transformForm) {
        return transformForm.isNew() ? "Transfer (new)" : "Transfer (" + transformForm.getId() + ")";
    }
}
