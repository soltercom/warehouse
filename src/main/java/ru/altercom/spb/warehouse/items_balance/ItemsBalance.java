package ru.altercom.spb.warehouse.items_balance;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Table("ITEMS_BALANCE")
public record ItemsBalance(@Id Long id,
                           @NotNull Long recorderId, @NotEmpty String recorderType, int sign,
                           @NotNull LocalDate date, @NotNull Long warehouseId,
                           @NotNull Long itemId, BigDecimal quantity) {

}
