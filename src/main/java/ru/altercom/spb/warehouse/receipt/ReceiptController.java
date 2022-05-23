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
    public String doList(ModelMap model) {
        return LIST;
    }

    @GetMapping("/new")
    public String doNewForm(ModelMap model) {
        var receiptForm = receiptService.emptyReceiptForm();
        model.put("receiptForm", receiptForm);
        model.put("formTitle", receiptService.getFormTitle(receiptForm));
        return FORM;
    }

    @PostMapping("/new")
    public String processNewForm(@Valid ReceiptForm receiptForm,
                                 BindingResult bindingResult,
                                 ModelMap model) {
        if (bindingResult.hasErrors()) {
            model.put("receiptForm", receiptForm);
            model.put("formTitle", receiptService.getFormTitle(receiptForm));
            return FORM;
        }

        receiptService.save(receiptForm);

        return REDIRECT_LIST;
    }

    @GetMapping("/{id}")
    public String doForm(@PathVariable("id") Long id, ModelMap model) {
        var receiptForm = receiptService.findById(id);
        model.put("receiptForm", receiptForm);
        model.put("formTitle", receiptService.getFormTitle(receiptForm));
        return FORM;
    }

    @PostMapping("/{id}")
    public String processForm(@Valid ReceiptForm receiptForm,
                              BindingResult bindingResult,
                              ModelMap model) {
        if (bindingResult.hasErrors()) {
            model.put("receiptForm", receiptForm);
            model.put("formTitle", receiptService.getFormTitle(receiptForm));
            return FORM;
        }

        receiptService.save(receiptForm);

        return REDIRECT_LIST;
    }

    @PostMapping(value="/{id}", params={"add-row"})
    public String addRow(ReceiptForm receiptForm, ModelMap model) {
        receiptForm.getRows().add(receiptService.emptyReceiptRowDao(receiptForm.getId()));
        model.put("receiptForm", receiptForm);
        model.put("formTitle", receiptService.getFormTitle(receiptForm));
        return FORM;
    }

    @PostMapping(value="/{id}", params={"remove-row"})
    public String deleteRow(@RequestParam("remove-row") int index,  ReceiptForm receiptForm, ModelMap model) {
        receiptForm.getRows().remove(index);
        model.put("receiptForm", receiptForm);
        model.put("formTitle", receiptService.getFormTitle(receiptForm));
        return FORM;
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
