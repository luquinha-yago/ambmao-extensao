package web.ambmao.core.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {
    @GetMapping("/") public String inicio() { return "index"; }
    @GetMapping("/login") public String login() { return "login"; }
}
