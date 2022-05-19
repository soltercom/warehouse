package ru.altercom.spb.warehouse.item;

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
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    private final TransactionManager transactionManager;
    private final ItemRepository itemRepo;

    public ItemService(TransactionManager transactionManager,
                       ItemRepository itemRepo) {
        this.transactionManager = transactionManager;
        this.itemRepo = itemRepo;
    }

    public Long save(Item item) {
        Objects.requireNonNull(item);
        var createdItem = transactionManager.doInTransaction(() -> {
            var savedItem = itemRepo.save(item);
            logger.info("Item is saved: {}", savedItem);
            return savedItem;
        });
        return createdItem.getId();
    }

    public Item findById(Long id) {
        return itemRepo.findById(id)
            .orElseThrow(() -> new ItemNotFoundException(id));
    }

    public ItemRef getItemRefById(Long id) {
        return itemRepo.getItemById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    public TableData getTable(int draw, int page, int size, String search, Sort.Direction dir) {

        var pageRequest = PageRequest.of(page / size, size, dir, "name");

        var data = itemRepo.getItemsByNameStartsWith(search, pageRequest);

        return new TableData(draw, data.getTotalElements(), data.getTotalElements(), data.getContent());

    }

    public List<SelectItem> getSelectList(String term) {
        return itemRepo.getSelectList(term + "%")
                .stream()
                .map(this::toSelectItem)
                .toList();
    }

    private SelectItem toSelectItem(ItemRef itemRef) {
        return new SelectItem(itemRef.getId(), itemRef.getName(), itemRef.getName());
    }

}
