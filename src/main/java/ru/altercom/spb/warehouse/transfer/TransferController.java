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
    public String doList() {
        return LIST;
    }

    @GetMapping("/new")
    public String doNewForm(ModelMap model) {
        var transferForm = transferService.emptyTransferForm();
        prepareModel(model, transferForm);
        return FORM;
    }

    @PostMapping("/new")
    public String processNewForm(@Valid @ModelAttribute("transferForm") TransferForm transferForm,
                                 BindingResult bindingResult,
                                 ModelMap model) {
        if (bindingResult.hasErrors()) {
            prepareModel(model, transferForm);
            return FORM;
        } else {
            transferService.save(transferForm);
            return REDIRECT_LIST;
        }
    }

    @GetMapping("/{id}")
    public String doForm(@PathVariable("id") Long id, ModelMap model) {
        var transferForm = transferService.findById(id);
        prepareModel(model, transferForm);
        return FORM;
    }

    @PostMapping("/{id}")
    public String processForm(@Valid @ModelAttribute("transferForm") TransferForm transferForm,
                              BindingResult bindingResult,
                              ModelMap model) {
        if (bindingResult.hasErrors()) {
            prepareModel(model, transferForm);
            return FORM;
        } else {
            transferService.save(transferForm);
            return REDIRECT_LIST;
        }
    }

    @PostMapping(value={"/new", "/{id}"}, params={"add-row"})
    public String addRow(@ModelAttribute("transferForm") TransferForm transferForm,
                         ModelMap model) {
        transferForm.getRows().add(transferService.emptyTransferRowDao(transferForm.getId()));
        prepareModel(model, transferForm);
        return FORM;
    }

    @PostMapping(value={"/new", "/{id}"}, params={"remove-row"})
    public String deleteRow(@RequestParam("remove-row") int index,
                            @ModelAttribute("transferForm") TransferForm transferForm,
                            ModelMap model) {
        transferForm.getRows().remove(index);
        prepareModel(model, transferForm);
        return FORM;
    }

    private void prepareModel(ModelMap model, TransferForm transferForm) {
        model.put("transferForm", transferForm);
        model.put("formTitle", transferService.getFormTitle(transferForm));
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
