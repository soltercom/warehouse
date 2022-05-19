package ru.altercom.spb.warehouse.system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class RootController {

    public static final String REDIRECT_ITEMS = "redirect:/items";

    @GetMapping
    public String index() {
        return REDIRECT_ITEMS;
    }

}
