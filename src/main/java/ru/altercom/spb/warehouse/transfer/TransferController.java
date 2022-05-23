package ru.altercom.spb.warehouse.transfer;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.altercom.spb.warehouse.table.TableData;

import javax.validation.Valid;

@RequestMapping("/transfers")
@Controller
public class TransferController {

    private static final String LIST = "/transfer/list";
    private static final String REDIRECT_LIST = "redirect:/transfers";
    private static final String FORM = "/transfer/form";

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping
    public String doList(ModelMap model) {
        return LIST;
    }

    @GetMapping("/new")
    public String doNewForm(ModelMap model) {
        var transferForm = transferService.emptyTransferForm();
        model.put("transferForm", transferForm);
        model.put("formTitle", transferService.getFormTitle(transferForm));
        return FORM;
    }

    @PostMapping("/new")
    public String processNewForm(@Valid TransferForm transferForm,
                                 BindingResult bindingResult,
                                 ModelMap model) {
        if (bindingResult.hasErrors()) {
            model.put("transferForm", transferForm);
            model.put("formTitle", transferService.getFormTitle(transferForm));
            return FORM;
        }

        transferService.save(transferForm);

        return REDIRECT_LIST;
    }

    @GetMapping("/{id}")
    public String doForm(@PathVariable("id") Long id, ModelMap model) {
        var transferForm = transferService.findById(id);
        model.put("transferForm", transferForm);
        model.put("formTitle", transferService.getFormTitle(transferForm));
        return FORM;
    }

    @PostMapping("/{id}")
    public String processForm(@Valid TransferForm transferForm,
                              BindingResult bindingResult,
                              ModelMap model) {
        if (bindingResult.hasErrors()) {
            model.put("transferForm", transferForm);
            model.put("formTitle", transferService.getFormTitle(transferForm));
            return FORM;
        }

        transferService.save(transferForm);

        return REDIRECT_LIST;
    }

    @PostMapping(value="/{id}", params={"add-row"})
    public String addRow(TransferForm transferForm, ModelMap model) {
        transferForm.getRows().add(transferService.emptyTransferRowDao(transferForm.getId()));
        model.put("transferForm", transferForm);
        model.put("formTitle", transferService.getFormTitle(transferForm));
        return FORM;
    }

    @PostMapping(value="/{id}", params={"remove-row"})
    public String deleteRow(@RequestParam("remove-row") int index,
                            TransferForm transferForm, ModelMap model) {
        transferForm.getRows().remove(index);
        model.put("transferForm", transferForm);
        model.put("formTitle", transferService.getFormTitle(transferForm));
        return FORM;
    }

    @GetMapping("/table")
    @ResponseBody
    public TableData getTable(@RequestParam int draw,
                              @RequestParam int start,
                              @RequestParam int length,
                              @RequestParam(name = "search[value]", defaultValue = "") String search,
                              @RequestParam(name = "order[0][dir]") Sort.Direction dir) {
        return transferService.getTable(draw, start, length, search, dir);
    }

}
