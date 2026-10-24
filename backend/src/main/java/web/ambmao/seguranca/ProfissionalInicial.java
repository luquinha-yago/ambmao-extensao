package web.ambmao.seguranca;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import web.ambmao.profissional.*;

// Provisionamento administrativo: sem cadastro público e sem senha padrão.
@Component
public class ProfissionalInicial implements ApplicationRunner {
    private final ProfissionalRepository repository;
    private final PasswordEncoder encoder;
    private final String nome, email, senha;
    public ProfissionalInicial(ProfissionalRepository repository, PasswordEncoder encoder,
            @Value("${ambmao.profissional-inicial.nome:}") String nome,
            @Value("${ambmao.profissional-inicial.email:}") String email,
            @Value("${ambmao.profissional-inicial.senha:}") String senha) {
        this.repository = repository; this.encoder = encoder;
        this.nome = nome; this.email = email; this.senha = senha;
    }
    @Override @Transactional
    public void run(ApplicationArguments args) {
        if (nome.isBlank() && email.isBlank() && senha.isBlank()) return;
        if (nome.isBlank() || nome.length() > 160 || email.length() > 254
                || !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")
                || senha.length() < 12 || senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException("Configure nome, e-mail válido e senha de 12 caracteres ou mais (até 72 bytes) para o profissional inicial.");
        }
        String normalizado = Profissional.normalizarEmail(email);
        if (!repository.existsByEmail(normalizado)) {
            repository.save(new Profissional(nome, normalizado, encoder.encode(senha)));
        }
    }
}
