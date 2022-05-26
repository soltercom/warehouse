package ru.altercom.spb.warehouse.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Table("ITEMS")
@Getter
@AllArgsConstructor
public class Item {

    @Id
    private final Long id;

    @NotBlank(message = "Item name is required")
    private final String name;

    public static Item empty() {
        return new Item (null, "");
    }

    public boolean isNew() {
        return id == null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return getId().equals(item.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
