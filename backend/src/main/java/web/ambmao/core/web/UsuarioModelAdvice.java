package web.ambmao.core.web;

import java.security.Principal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import web.ambmao.profissional.ProfissionalRepository;

@ControllerAdvice
public class UsuarioModelAdvice {
    private final ProfissionalRepository profissionais;
    public UsuarioModelAdvice(ProfissionalRepository profissionais) { this.profissionais = profissionais; }
    public record ContaProfissional(String nome, String email) { }

    @ModelAttribute("contaProfissional")
    public ContaProfissional contaProfissional(Principal principal) {
        if (principal == null) return null;
        return profissionais.findByEmail(principal.getName())
            .map(p -> new ContaProfissional(p.getNome(), p.getEmail()))
            .orElseGet(() -> new ContaProfissional("Profissional", principal.getName()));
    }
    @ModelAttribute("usuarioAutenticado")
    public boolean usuarioAutenticado(Principal principal) { return principal != null; }
}
