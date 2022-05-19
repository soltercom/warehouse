package ru.altercom.spb.warehouse.warehouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.altercom.spb.warehouse.system.SelectItem;
import ru.altercom.spb.warehouse.system.TransactionManager;
import ru.altercom.spb.warehouse.table.TableData;

import java.util.List;
import java.util.Objects;

@Service
public class WarehouseService {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseService.class);

    private final TransactionManager transactionManager;
    private final WarehouseRepository warehouseRepo;

    public WarehouseService(TransactionManager transactionManager,
                            WarehouseRepository warehouseRepo) {
        this.transactionManager = transactionManager;
        this.warehouseRepo = warehouseRepo;
    }

    public Long save(Warehouse warehouse) {
        Objects.requireNonNull(warehouse);
        var createdWarehouse = transactionManager.doInTransaction(() -> {
            var savedWarehouse = warehouseRepo.save(warehouse);
            logger.info("Warehouse is saved: {}", savedWarehouse);
            return savedWarehouse;
        });
        return createdWarehouse.getId();
    }

    public Warehouse findById(Long id) {
        return warehouseRepo.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));
    }

    public WarehouseRef getWarehouseRefById(Long id) {
        return warehouseRepo.getWarehouseRefById(id)
            .orElseThrow(() -> new WarehouseNotFoundException(id));
    }

    public TableData getTable(int draw, int page, int size, String search, Sort.Direction dir) {

        var pageRequest = PageRequest.of(page / size, size, dir, "name");

        var data = warehouseRepo.getWarehouseByNameStartsWith(search, pageRequest);

        return new TableData(draw, data.getTotalElements(), data.getTotalElements(), data.getContent());

    }

    public List<SelectItem> getSelectList(String term) {
        return warehouseRepo.getSelectList(term + "%")
                .stream()
                .map(this::toSelectItem)
                .toList();
    }

    private SelectItem toSelectItem(WarehouseRef warehouseRef) {
        return new SelectItem(warehouseRef.getId(), warehouseRef.getName(), warehouseRef.getName());
    }
}
