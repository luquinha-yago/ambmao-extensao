package web.ambmao.profissional;

import jakarta.persistence.*;
import java.util.Locale;

@Entity
@Table(name = "profissional")
public class Profissional {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long codigo;
    @Column(nullable = false, length = 160) private String nome;
    @Column(nullable = false, unique = true, length = 254) private String email;
    @Column(name = "senha_hash", nullable = false, length = 255) private String senhaHash;
    @Column(nullable = false) private boolean ativo = true;
    protected Profissional() { }
    public Profissional(String nome, String email, String senhaHash) {
        this.nome = nome.strip();
        this.email = normalizarEmail(email);
        this.senhaHash = senhaHash;
    }
    public static String normalizarEmail(String email) { return email.strip().toLowerCase(Locale.ROOT); }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public boolean isAtivo() { return ativo; }
}
