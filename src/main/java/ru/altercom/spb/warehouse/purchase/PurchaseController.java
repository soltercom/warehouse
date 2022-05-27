package ru.altercom.spb.warehouse.purchase;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.altercom.spb.warehouse.table.TableData;

import javax.validation.Valid;

@RequestMapping("/purchases")
@Controller
public class PurchaseController {

    private static final String LIST = "/purchase/list";
    private static final String REDIRECT_LIST = "redirect:/purchases";
    private static final String FORM = "/purchase/form";

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public String doList() {
        return LIST;
    }

    @GetMapping("/new")
    public String doNewForm(ModelMap model) {
        var purchaseForm = purchaseService.emptyPurchaseForm();
        prepareModel(model, purchaseForm);
        return FORM;
    }

    @PostMapping("/new")
    public String processNewForm(@Valid @ModelAttribute("purchaseForm") PurchaseForm purchaseForm,
                                 BindingResult bindingResult,
                                 ModelMap model) {
        if (bindingResult.hasErrors()) {
            prepareModel(model, purchaseForm);
            return FORM;
        } else {
            purchaseService.save(purchaseForm);
            return REDIRECT_LIST;
        }
    }

    @GetMapping("/{id}")
    public String doForm(@PathVariable("id") Long id, ModelMap model) {
        var purchaseForm = purchaseService.findById(id);
        prepareModel(model, purchaseForm);
        return FORM;
    }

    @PostMapping("/{id}")
    public String processForm(@Valid @ModelAttribute("purchaseForm") PurchaseForm purchaseForm,
                              BindingResult bindingResult,
                              ModelMap model) {
        if (bindingResult.hasErrors()) {
            prepareModel(model, purchaseForm);
            return FORM;
        } else {
            purchaseService.save(purchaseForm);
            return REDIRECT_LIST;
        }
    }

    @PostMapping(value={"/new", "/{id}"}, params={"add-row"})
    public String addRow(@ModelAttribute("purchaseForm") PurchaseForm purchaseForm,
                         ModelMap model) {
        purchaseForm.getRows().add(purchaseService.emptyPurchaseRowDao(purchaseForm.getId()));
        prepareModel(model, purchaseForm);
        return FORM;
    }

    @PostMapping(value={"/new", "/{id}"}, params={"remove-row"})
    public String deleteRow(@RequestParam("remove-row") int index,
                            @ModelAttribute("purchaseForm") PurchaseForm purchaseForm,
                            ModelMap model) {
        purchaseForm.getRows().remove(index);
        prepareModel(model, purchaseForm);
        return FORM;
    }

    private void prepareModel(ModelMap model, PurchaseForm purchaseForm) {
        model.put("purchaseForm", purchaseForm);
        model.put("formTitle", purchaseService.getFormTitle(purchaseForm));
    }

    @GetMapping("/table")
    @ResponseBody
    public TableData getTable(@RequestParam int draw,
                              @RequestParam int start,
                              @RequestParam int length,
                              @RequestParam(name = "search[value]", defaultValue = "") String search,
                              @RequestParam(name = "order[0][dir]") Sort.Direction dir) {
        return purchaseService.getTable(draw, start, length, search, dir);
    }

}
