package ru.altercom.spb.warehouse.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Table("Warehouses")
@Getter
@AllArgsConstructor
public class Warehouse {

    @Id
    private final Long id;

    @NotBlank(message = "Warehouse name is required")
    private final String name;

    public boolean isNew() {
        return id == null;
    }

    public static Warehouse empty() {
        return new Warehouse(null, "");
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Warehouse warehouse)) return false;
        return getId().equals(warehouse.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
