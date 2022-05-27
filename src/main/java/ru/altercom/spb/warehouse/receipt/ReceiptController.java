package ru.altercom.spb.warehouse.receipt;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.altercom.spb.warehouse.table.TableData;

import javax.validation.Valid;

@RequestMapping("/receipts")
@Controller
public class ReceiptController {

    private static final String LIST = "/receipt/list";
    private static final String REDIRECT_LIST = "redirect:/receipts";
    private static final String FORM = "/receipt/form";

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping
    public String doList() {
        return LIST;
    }

    @GetMapping("/new")
    public String doNewForm(ModelMap model) {
        var receiptForm = receiptService.emptyReceiptForm();
        prepareModel(model, receiptForm);
        return FORM;
    }

    @PostMapping("/new")
    public String processNewForm(@Valid @ModelAttribute("receiptForm") ReceiptForm receiptForm,
                                 BindingResult bindingResult,
                                 ModelMap model) {
        if (bindingResult.hasErrors()) {
            prepareModel(model, receiptForm);
            return FORM;
        } else {
            receiptService.save(receiptForm);
            return REDIRECT_LIST;
        }
    }

    @GetMapping("/{id}")
    public String doForm(@PathVariable("id") Long id, ModelMap model) {
        var receiptForm = receiptService.findById(id);
        prepareModel(model, receiptForm);
        return FORM;
    }

    @PostMapping("/{id}")
    public String processForm(@Valid @ModelAttribute("receiptForm") ReceiptForm receiptForm,
                              BindingResult bindingResult,
                              ModelMap model) {
        if (bindingResult.hasErrors()) {
            prepareModel(model, receiptForm);
            return FORM;
        } else {
            receiptService.save(receiptForm);
            return REDIRECT_LIST;
        }
    }

    @PostMapping(value={"/new", "/{id}"}, params={"add-row"})
    public String addRow(@ModelAttribute("receiptForm") ReceiptForm receiptForm,
                         ModelMap model) {
        receiptForm.getRows().add(receiptService.emptyReceiptRowDao(receiptForm.getId()));
        prepareModel(model, receiptForm);
        return FORM;
    }

    @PostMapping(value={"/new", "/{id}"}, params={"remove-row"})
    public String deleteRow(@RequestParam("remove-row") int index,
                            @ModelAttribute("receiptForm") ReceiptForm receiptForm,
                            ModelMap model) {
        receiptForm.getRows().remove(index);
        prepareModel(model, receiptForm);
        return FORM;
    }

    private void prepareModel(ModelMap model, ReceiptForm receiptForm) {
        model.put("receiptForm", receiptForm);
        model.put("formTitle", receiptService.getFormTitle(receiptForm));
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
