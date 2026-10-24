package web.ambmao.seguranca;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.ambmao.profissional.*;

@Service
public class ProfissionalUserDetailsService implements UserDetailsService {
    private final ProfissionalRepository repository;
    public ProfissionalUserDetailsService(ProfissionalRepository repository) { this.repository = repository; }
    @Override @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Profissional profissional = repository.findByEmail(Profissional.normalizarEmail(email))
            .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas"));
        return User.withUsername(profissional.getEmail()).password(profissional.getSenhaHash())
            .roles("PROFISSIONAL").disabled(!profissional.isAtivo()).build();
    }
}
