package ru.altercom.spb.warehouse.warehouse;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.altercom.spb.warehouse.system.SelectItem;
import ru.altercom.spb.warehouse.table.TableData;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RequestMapping("/warehouses")
@Controller
public class WarehouseController {

    private static final String LIST = "/warehouse/list";
    private static final String REDIRECT_LIST = "redirect:/warehouses";
    private static final String FORM = "/warehouse/form";

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    public String doList() {
        return LIST;
    }

    @GetMapping("/new")
    public String doNewForm(ModelMap model) {
        var warehouse = warehouseService.emptyWarehouse();
        prepareModel(model, warehouse);
        return FORM;
    }

    @PostMapping("/new")
    public String processNewForm(@Valid @ModelAttribute("warehouseForm") Warehouse warehouseForm,
                                 BindingResult bindingResult,
                                 ModelMap model) {
        if (bindingResult.hasErrors()) {
            prepareModel(model, warehouseForm);
            return FORM;
        } else {
            warehouseService.save(warehouseForm);
            return REDIRECT_LIST;
        }
    }

    @GetMapping("/{id}")
    public String doForm(@PathVariable("id") Long id,
                         ModelMap model) {
        var warehouseForm = warehouseService.findById(id);
        prepareModel(model, warehouseForm);
        return FORM;
    }

    @PostMapping("/{id}")
    public String processForm(@Valid @ModelAttribute("warehouseForm") Warehouse warehouseForm,
                              BindingResult bindingResult,
                              ModelMap model) {
        if (bindingResult.hasErrors()) {
            prepareModel(model, warehouseForm);
            return FORM;
        } else {
            warehouseService.save(warehouseForm);
            return REDIRECT_LIST;
        }
    }

    private void prepareModel(ModelMap model, Warehouse warehouseForm) {
        model.put("warehouseForm", warehouseForm);
        model.put("formTitle", warehouseService.getFormTitle(warehouseForm));
    }

    @GetMapping("/table")
    @ResponseBody
    public TableData getTable(@RequestParam int draw,
                              @RequestParam int start,
                              @RequestParam int length,
                              @RequestParam(name = "search[value]", defaultValue = "") String search,
                              @RequestParam(name = "order[0][dir]") Sort.Direction dir) {
        return warehouseService.getTable(draw, start, length, search, dir);
    }

    @GetMapping("/select")
    @ResponseBody
    public List<SelectItem> getSelectList(@RequestParam(name="term", defaultValue = "") String term) {
        return warehouseService.getSelectList(term);
    }

}
