package web.ambmao.cartilha;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import web.ambmao.cartilha.dto.CartilhaForm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartilhaIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired CartilhaRepository repository;
    @BeforeEach void limpar() { repository.deleteAll(); }
    private MockHttpServletRequestBuilder campos(MockHttpServletRequestBuilder request) {
        return campos(request, "Síndrome do túnel do carpo");
    }
    private MockHttpServletRequestBuilder campos(MockHttpServletRequestBuilder request, String nome) {
        return request.param("nomeDoenca", nome).param("oQueE", "Explicação educativa")
            .param("sinaisSintomas", "Um sintoma\nOutro sintoma").param("orientacoesGerais", "Orientação de teste")
            .param("fonteTitulo", "Referência").param("fonteUrl", "https://example.org/referencia");
    }
    @Test void consultaPublicaVaziaRenderizaSemDemonstracao() throws Exception {
        mvc.perform(get("/cartilhas")).andExpect(status().isOk())
            .andExpect(content().string(containsString("Nenhuma cartilha encontrada")))
            .andExpect(content().string(not(containsString("cartilhas-dados.js"))))
            .andExpect(content().string(not(containsString("Prévia com três cartilhas"))));
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/login")).andExpect(status().isOk());
    }
    @Test void areaProfissionalExigeLogin() throws Exception {
        mvc.perform(get("/profissional/cartilhas")).andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }
    @Test void escritaExigeCsrfEPerfilProfissional() throws Exception {
        mvc.perform(campos(post("/profissional/cartilhas")).with(user("teste").roles("PROFISSIONAL")))
            .andExpect(status().isForbidden());
        mvc.perform(campos(post("/profissional/cartilhas")).with(csrf()).with(user("teste").roles("PACIENTE")))
            .andExpect(status().isForbidden());
        assertThat(repository.count()).isZero();
    }
    @Test void cadastrarEEditarPersisteEMostraNaLeituraPublica() throws Exception {
        mvc.perform(get("/profissional/cartilhas/nova").with(user("teste").roles("PROFISSIONAL")))
            .andExpect(status().isOk()).andExpect(content().string(containsString("_csrf")));
        mvc.perform(campos(post("/profissional/cartilhas")).with(user("teste").roles("PROFISSIONAL")).with(csrf()))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/profissional/cartilhas"));
        var cartilha = repository.findAll().getFirst();
        mvc.perform(get("/cartilhas").param("nomeDoenca", "TUNEL carpo"))
            .andExpect(status().isOk()).andExpect(content().string(containsString("Síndrome do túnel do carpo")));
        mvc.perform(get("/profissional/cartilhas/{codigo}/editar", cartilha.getCodigo()).with(user("teste").roles("PROFISSIONAL")))
            .andExpect(status().isOk()).andExpect(content().string(containsString("Editar cartilha")));
        mvc.perform(campos(put("/profissional/cartilhas/{codigo}", cartilha.getCodigo()), "Nome atualizado")
                .with(user("teste").roles("PROFISSIONAL")).with(csrf()))
            .andExpect(status().is3xxRedirection());
        assertThat(repository.findById(cartilha.getCodigo()).orElseThrow().getNomeDoenca()).isEqualTo("Nome atualizado");
    }
    @Test void rejeitaBrancosEUrlExecutavelMantendoFormulario() throws Exception {
        mvc.perform(post("/profissional/cartilhas").with(user("teste").roles("PROFISSIONAL")).with(csrf())
                .param("nomeDoenca", "   ").param("oQueE", "Texto").param("sinaisSintomas", "Texto")
                .param("orientacoesGerais", "Texto").param("fonteUrl", "javascript:alert(1)"))
            .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("cartilhaForm", "nomeDoenca", "fonteUrl"))
            .andExpect(content().string(containsString("Confira os campos")));
        assertThat(repository.count()).isZero();
    }
    @Test void edicaoInvalidaPreservaCodigoEValores() throws Exception {
        var c = repository.save(new Cartilha(new CartilhaForm("Nome", "Texto", "Texto", "Texto", "", "")));
        mvc.perform(put("/profissional/cartilhas/{codigo}", c.getCodigo()).with(user("teste").roles("PROFISSIONAL")).with(csrf())
                .param("nomeDoenca", "Novo nome").param("oQueE", "").param("sinaisSintomas", "Texto").param("orientacoesGerais", "Texto"))
            .andExpect(status().isOk()).andExpect(model().attribute("codigo", c.getCodigo()))
            .andExpect(model().attributeHasFieldErrors("cartilhaForm", "oQueE"));
        assertThat(repository.findById(c.getCodigo()).orElseThrow().getNomeDoenca()).isEqualTo("Nome");
    }
    @Test void conteudoEExibidoComoTextoSemExecutarHtml() throws Exception {
        repository.save(new Cartilha(new CartilhaForm("<script>alert(1)</script>", "Texto", "Texto", "Texto", "", "")));
        mvc.perform(get("/cartilhas")).andExpect(status().isOk())
            .andExpect(content().string(containsString("&lt;script&gt;")))
            .andExpect(content().string(not(containsString("<script>alert(1)</script>"))));
    }
    @Test void sintomasSaoTopicosSemLinhasVaziasEComHtmlEscapado() throws Exception {
        repository.save(new Cartilha(new CartilhaForm("Exemplo", "Texto",
            "  Dor na mão  \r\n\r\n <script>alert(1)</script> \n", "Texto", "", "")));
        String html = mvc.perform(get("/cartilhas")).andExpect(status().isOk())
            .andExpect(content().string(not(containsString("<script>alert(1)</script>"))))
            .andReturn().getResponse().getContentAsString();
        var lista = java.util.regex.Pattern.compile("<ul[^>]*data-field=\"sinaisSintomas\"[^>]*>(.*?)</ul>",
            java.util.regex.Pattern.DOTALL).matcher(html);
        assertThat(lista.find()).isTrue();
        assertThat(lista.group(1)).contains("<li>Dor na mão</li>", "<li>&lt;script&gt;alert(1)&lt;/script&gt;</li>");
        assertThat(java.util.regex.Pattern.compile("<li>").matcher(lista.group(1)).results().count()).isEqualTo(2);
    }
    @Test void cartilhaInexistenteRetorna404() throws Exception {
        mvc.perform(get("/profissional/cartilhas/999999/editar").with(user("teste").roles("PROFISSIONAL")))
            .andExpect(status().isNotFound());
    }
    @Test void excluirPedeConfirmacaoERemoveDaConsultaPublica() throws Exception {
        var c = repository.save(new Cartilha(new CartilhaForm("Cartilha para excluir", "Texto", "Texto", "Texto", "", "")));
        mvc.perform(get("/profissional/cartilhas/{codigo}/excluir", c.getCodigo())
                .with(user("teste").roles("PROFISSIONAL")))
            .andExpect(status().isOk()).andExpect(content().string(containsString("Cartilha para excluir")))
            .andExpect(content().string(containsString("Sim, excluir cartilha")))
            .andExpect(content().string(containsString("_csrf")));
        assertThat(repository.existsById(c.getCodigo())).isTrue();
        mvc.perform(post("/profissional/cartilhas/{codigo}", c.getCodigo()).param("_method", "DELETE")
                .with(user("teste").roles("PROFISSIONAL")).with(csrf()))
            .andExpect(redirectedUrl("/profissional/cartilhas"))
            .andExpect(flash().attribute("mensagem", "Cartilha excluída com sucesso."));
        assertThat(repository.existsById(c.getCodigo())).isFalse();
        mvc.perform(get("/cartilhas")).andExpect(content().string(not(containsString("Cartilha para excluir"))));
    }
    @Test void exclusaoExigeAutenticacaoPerfilECsrf() throws Exception {
        var c = repository.save(new Cartilha(new CartilhaForm("Preservar", "Texto", "Texto", "Texto", "", "")));
        mvc.perform(get("/profissional/cartilhas/{codigo}/excluir", c.getCodigo()))
            .andExpect(redirectedUrl("/login"));
        mvc.perform(delete("/profissional/cartilhas/{codigo}", c.getCodigo()).with(csrf()))
            .andExpect(redirectedUrl("/login"));
        mvc.perform(delete("/profissional/cartilhas/{codigo}", c.getCodigo())
                .with(user("teste").roles("PROFISSIONAL"))).andExpect(status().isForbidden());
        mvc.perform(delete("/profissional/cartilhas/{codigo}", c.getCodigo())
                .with(user("teste").roles("PACIENTE")).with(csrf())).andExpect(status().isForbidden());
        assertThat(repository.existsById(c.getCodigo())).isTrue();
    }
    @Test void excluirCartilhaInexistenteRetorna404() throws Exception {
        mvc.perform(get("/profissional/cartilhas/999999/excluir").with(user("teste").roles("PROFISSIONAL")))
            .andExpect(status().isNotFound());
        mvc.perform(delete("/profissional/cartilhas/999999").with(user("teste").roles("PROFISSIONAL")).with(csrf()))
            .andExpect(status().isNotFound());
    }
}
