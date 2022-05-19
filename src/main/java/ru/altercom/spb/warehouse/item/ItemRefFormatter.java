package ru.altercom.spb.warehouse.item;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
public class ItemRefFormatter implements Formatter<ItemRef> {

    private final ItemService itemService;

    public ItemRefFormatter(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public ItemRef parse(String id, Locale locale) throws ParseException {
        return itemService.getItemRefById(Long.valueOf(id));
    }

    @Override
    public String print(ItemRef itemRef, Locale locale) {
        return itemRef.getId() == null ? "" : itemRef.getId().toString();
    }
}
