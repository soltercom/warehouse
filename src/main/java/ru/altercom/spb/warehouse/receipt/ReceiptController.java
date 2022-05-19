package ru.altercom.spb.warehouse.receipt;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.altercom.spb.warehouse.item.ItemService;
import ru.altercom.spb.warehouse.system.SelectItem;
import ru.altercom.spb.warehouse.table.TableData;
import ru.altercom.spb.warehouse.warehouse.WarehouseRef;
import ru.altercom.spb.warehouse.warehouse.WarehouseService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RequestMapping("/receipts")
@Controller
public class ReceiptController {

    private static final String LIST = "/receipt/list";
    private static final String REDIRECT_LIST = "redirect:/receipts";
    private static final String FORM = "/receipt/form";

    private final ReceiptService receiptService;
    private final WarehouseService warehouseService;
    private final ItemService itemService;

    public ReceiptController(ReceiptService receiptService, WarehouseService warehouseService, ItemService itemService) {
        this.receiptService = receiptService;
        this.warehouseService = warehouseService;
        this.itemService = itemService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new RemoveEmptyRowsValidator(binder.getValidator()));
    }

    @GetMapping
    public String doList(ModelMap model) {
        return LIST;
    }

    @GetMapping("/new")
    public String doNewForm(ModelMap model) {
        var receipt = Receipt.empty();
        model.put("receiptForm", receipt);
        model.put("formTitle", "Receipt (new)");
        model.put("storage", receiptService.populate(receipt));
        return FORM;
    }

    @PostMapping("/new")
    public String processNewForm(@Valid Receipt receipt,
                                 BindingResult bindingResult,
                                 ModelMap model) {
        receipt = receipt.withRows(receipt.getRows());
        if (bindingResult.hasErrors()) {
            model.put("receiptForm", receipt);
            model.put("formTitle", "Receipt (new)");
            model.put("storage", receiptService.populate(receipt));
            return FORM;
        }

        receiptService.save(receipt);

        return REDIRECT_LIST;
    }

    @GetMapping("/{id}")
    public String doForm(@PathVariable("id") Long id, ModelMap model) {
        var receipt = receiptService.findById(id);
        model.put("receiptForm", receipt);
        model.put("formTitle", "Receipt (" + receipt.getId() + ")");
        model.put("storage", receiptService.populate(receipt));
        return FORM;
    }

    @PostMapping("/{id}")
    public String processForm(@Valid Receipt receipt,
                              BindingResult bindingResult,
                              ModelMap model) {
        receipt = receipt.withRows(receipt.getRows());
        if (bindingResult.hasErrors()) {
            model.put("receiptForm", receipt);
            model.put("formTitle", "Receipt (" + receipt.getId() + ")");
            model.put("storage", receiptService.populate(receipt));
            return FORM;
        }

        receiptService.save(receipt);

        return REDIRECT_LIST;
    }

    @PostMapping(value="/{id}", params={"addRow"})
    public String addRow(final Receipt receipt, ModelMap model) {
        receipt.getRows().add(ReceiptRow.empty());
        model.put("receiptForm", receipt);
        model.put("formTitle", "Receipt (" + receipt.getId() + ")");
        model.put("storage", receiptService.populate(receipt));
        return FORM;
    }

    private record RemoveEmptyRowsValidator(Validator validator) implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return validator.supports(clazz);
        }

        @Override
        public void validate(Object target, Errors errors) {
            if (target instanceof Receipt receipt) {
                target = receipt.withRows(receipt.getRows());
            }
            validator.validate(target, errors);
        }
    }

    @GetMapping("/table")
    @ResponseBody
    public TableData getTable(@RequestParam int draw,
                              @RequestParam int start,
                              @RequestParam int length,
                              @RequestParam(name = "search[value]", defaultValue = "") String search,
                              @RequestParam(name = "order[0][dir]") Sort.Direction dir) {
        return receiptService.getTable(draw, start, length, search, dir);
    }
}
