package ru.altercom.spb.warehouse.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {

    @Transactional(readOnly = true)
    Optional<ItemRef> getItemById(Long id);

    @Transactional(readOnly = true)
    Page<ItemRef> getItemsByNameStartsWith(@Param("name") String name, Pageable page);

    @Transactional(readOnly = true)
    @Query("SELECT id, name FROM items WHERE name LIKE :search ORDER BY name LIMIT 10")
    List<ItemRef> getSelectList(String search);

    @Transactional(readOnly = true)
    @Query("SELECT id, name FROM items WHERE id IN (:ids)")
    List<ItemRef> populate(List<Long> ids);
}
