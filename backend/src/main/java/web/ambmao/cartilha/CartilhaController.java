package web.ambmao.cartilha;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import web.ambmao.cartilha.dto.*;

@Controller
public class CartilhaController {
    private final CartilhaService service;
    public CartilhaController(CartilhaService service) { this.service = service; }
    @GetMapping("/cartilhas")
    public String listar(@Valid @ModelAttribute CartilhaSearchRequest busca, BindingResult resultado, Model model) {
        model.addAttribute("cartilhas", service.pesquisar(resultado.hasErrors() ? new CartilhaSearchRequest("") : busca));
        return "cartilhas/lista";
    }
    @GetMapping("/profissional/cartilhas")
    public String gerenciar(@Valid @ModelAttribute CartilhaSearchRequest busca, BindingResult resultado, Model model) {
        model.addAttribute("cartilhas", service.pesquisar(resultado.hasErrors() ? new CartilhaSearchRequest("") : busca));
        return "profissional/cartilhas/lista";
    }
    @GetMapping("/profissional/cartilhas/nova")
    public String nova(Model model) {
        model.addAttribute("cartilhaForm", CartilhaForm.vazio());
        return "profissional/cartilhas/form";
    }
    @GetMapping("/profissional/cartilhas/{codigo}/editar")
    public String editar(@PathVariable Long codigo, Model model) {
        model.addAttribute("codigo", codigo);
        model.addAttribute("cartilhaForm", service.buscarParaEdicao(codigo));
        return "profissional/cartilhas/form";
    }
    @PostMapping("/profissional/cartilhas")
    public String cadastrar(@Valid @ModelAttribute("cartilhaForm") CartilhaForm form,
            BindingResult resultado, RedirectAttributes redirect) {
        if (resultado.hasErrors()) return "profissional/cartilhas/form";
        service.salvar(form);
        redirect.addFlashAttribute("mensagem", "Cartilha cadastrada com sucesso.");
        return "redirect:/profissional/cartilhas";
    }
    @GetMapping("/profissional/cartilhas/{codigo}/excluir")
    public String confirmarExclusao(@PathVariable Long codigo, Model model) {
        model.addAttribute("codigo", codigo);
        model.addAttribute("cartilha", service.buscarParaEdicao(codigo));
        return "profissional/cartilhas/excluir";
    }
    @DeleteMapping("/profissional/cartilhas/{codigo}")
    public String excluir(@PathVariable Long codigo, RedirectAttributes redirect) {
        service.excluir(codigo);
        redirect.addFlashAttribute("mensagem", "Cartilha excluída com sucesso.");
        return "redirect:/profissional/cartilhas";
    }
    @PutMapping("/profissional/cartilhas/{codigo}")
    public String atualizar(@PathVariable Long codigo, @Valid @ModelAttribute("cartilhaForm") CartilhaForm form,
            BindingResult resultado, Model model, RedirectAttributes redirect) {
        // Verifica a existência inclusive quando há erros de validação.
        service.buscarParaEdicao(codigo);
        model.addAttribute("codigo", codigo);
        if (resultado.hasErrors()) return "profissional/cartilhas/form";
        service.atualizar(codigo, form);
        redirect.addFlashAttribute("mensagem", "Cartilha atualizada com sucesso.");
        return "redirect:/profissional/cartilhas";
    }
}
