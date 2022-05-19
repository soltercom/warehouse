package ru.altercom.spb.warehouse.item;

import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.altercom.spb.warehouse.system.SelectItem;
import ru.altercom.spb.warehouse.table.TableData;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/items")
@Controller
public class ItemController {

    private static final String LIST = "/item/list";
    private static final String REDIRECT_LIST = "redirect:/items";
    private static final String FORM = "/item/form";

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String doList(ModelMap model) {
        return LIST;
    }

    @GetMapping("/new")
    public String doNewForm(ModelMap model) {
        model.put("itemForm", Item.empty());
        model.put("formTitle", "Item (new)");
        return FORM;
    }

    @PostMapping("/new")
    public String processNewForm(@Valid Item item,
                                  BindingResult bindingResult,
                                  ModelMap model) {
        if (bindingResult.hasErrors()) {
            model.put("itemForm", item);
            model.put("formTitle", "Item (new)");
            return FORM;
        }

        itemService.save(item);

        return REDIRECT_LIST;
    }

    @GetMapping("/{id}")
    public String doForm(@PathVariable("id") Long id,
                         ModelMap model) {
        var item = itemService.findById(id);
        model.put("itemForm", item);
        model.put("formTitle", "Item (" + item.getId() + ")");
        return FORM;
    }

    @PostMapping("/{id}")
    public String processForm(@Valid Item itemForm,
                              BindingResult bindingResult,
                              ModelMap model) {
        if (bindingResult.hasErrors()) {
            model.put("itemForm", itemForm);
            model.put("formTitle", "Item (" + itemForm.getId() + ")");
            return FORM;
        }

        itemService.save(itemForm);

        return REDIRECT_LIST;
    }

    @GetMapping("/table")
    @ResponseBody
    public TableData getTable(@RequestParam int draw,
                              @RequestParam int start,
                              @RequestParam int length,
                              @RequestParam(name = "search[value]", defaultValue = "") String search,
                              @RequestParam(name = "order[0][dir]") Sort.Direction dir) {
        return itemService.getTable(draw, start, length, search, dir);
    }

    @GetMapping("/select")
    @ResponseBody
    public List<SelectItem> getSelectList(@RequestParam(name="term", defaultValue = "") String term) {
        return itemService.getSelectList(term);
    }
}
