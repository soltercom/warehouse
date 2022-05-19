package ru.altercom.spb.warehouse.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.altercom.spb.warehouse.system.SelectItem;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends PagingAndSortingRepository<Warehouse, Long> {

    @Transactional(readOnly = true)
    Optional<WarehouseRef> getWarehouseRefById(Long id);

    @Transactional(readOnly = true)
    Page<WarehouseRef> getWarehouseByNameStartsWith(@Param("name") String name, Pageable page);

    @Transactional(readOnly = true)
    @Query("SELECT id, name FROM warehouses WHERE name LIKE :search ORDER BY name LIMIT 10")
    List<WarehouseRef> getSelectList(String search);

}
