package ru.altercom.spb.warehouse.table;

import java.util.List;

public record TableData(int draw, long recordsTotal, long recordsFiltered, List<?> data) {
}
