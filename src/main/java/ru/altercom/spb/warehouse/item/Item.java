package ru.altercom.spb.warehouse.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("ITEMS")
@Getter
@AllArgsConstructor
public class Item {

    @Id
    private final Long id;

    private final String name;

    public static Item empty() {
        return new Item (null, "");
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
