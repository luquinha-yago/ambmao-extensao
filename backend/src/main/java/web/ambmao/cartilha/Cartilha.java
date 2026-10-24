package web.ambmao.cartilha;

import jakarta.persistence.*;
import java.time.Instant;
import web.ambmao.cartilha.dto.CartilhaForm;

@Entity
@Table(name = "cartilha")
public class Cartilha {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;
    @Column(name = "nome_doenca", nullable = false, length = 160)
    private String nomeDoenca;
    @Column(name = "nome_busca", nullable = false, length = 160)
    private String nomeBusca;
    @Column(name = "o_que_e", nullable = false, columnDefinition = "text")
    private String oQueE;
    @Column(name = "sinais_sintomas", nullable = false, columnDefinition = "text")
    private String sinaisSintomas;
    @Column(name = "orientacoes_gerais", nullable = false, columnDefinition = "text")
    private String orientacoesGerais;
    @Column(name = "fonte_titulo", length = 200)
    private String fonteTitulo;
    @Column(name = "fonte_url", length = 2000)
    private String fonteUrl;
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Cartilha() { }
    public Cartilha(CartilhaForm form) { atualizar(form); }
    public void atualizar(CartilhaForm form) {
        nomeDoenca = form.nomeDoenca().strip();
        nomeBusca = CartilhaSpecs.normalizar(nomeDoenca);
        oQueE = form.oQueE().strip();
        sinaisSintomas = form.sinaisSintomas().strip();
        orientacoesGerais = form.orientacoesGerais().strip();
        fonteUrl = form.fonteUrl() == null ? "" : form.fonteUrl().strip();
        fonteTitulo = form.fonteTitulo() == null ? "" : form.fonteTitulo().strip();
        if (!fonteUrl.isEmpty() && fonteTitulo.isEmpty()) fonteTitulo = "Consultar referência";
    }
    @PrePersist void criar() { criadoEm = atualizadoEm = Instant.now(); }
    @PreUpdate void atualizarData() { atualizadoEm = Instant.now(); }
    public Long getCodigo() { return codigo; }
    public String getNomeDoenca() { return nomeDoenca; }
    public String getOQueE() { return oQueE; }
    public String getSinaisSintomas() { return sinaisSintomas; }
    public String getOrientacoesGerais() { return orientacoesGerais; }
    public String getFonteTitulo() { return fonteTitulo; }
    public String getFonteUrl() { return fonteUrl; }
}
