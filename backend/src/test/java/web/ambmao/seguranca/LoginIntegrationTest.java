package web.ambmao.seguranca;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import web.ambmao.profissional.ProfissionalRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import org.springframework.mock.web.MockHttpSession;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ProfissionalRepository repository;
    @Test void senhaEArmazenadaComHashELoginRealCriaSessao() throws Exception {
        var profissional = repository.findByEmail("profissional@example.test").orElseThrow();
        assertThat(profissional.getSenhaHash()).startsWith("$2").isNotEqualTo("SenhaApenasDeTeste123!");
        mvc.perform(formLogin().user("PROFISSIONAL@example.test").password("SenhaApenasDeTeste123!"))
            .andExpect(authenticated().withRoles("PROFISSIONAL"))
            .andExpect(redirectedUrl("/profissional/cartilhas"));
    }
    @Test void senhaIncorretaNaoAutentica() throws Exception {
        mvc.perform(formLogin().user("profissional@example.test").password("incorreta"))
            .andExpect(unauthenticated()).andExpect(redirectedUrl("/login?error"));
    }
    @Test void cabecalhoExibeContaDoProfissionalAposLogin() throws Exception {
        var login = mvc.perform(formLogin().user("profissional@example.test").password("SenhaApenasDeTeste123!"))
            .andExpect(authenticated()).andReturn();
        var sessao = (MockHttpSession) login.getRequest().getSession(false);
        mvc.perform(get("/").session(sessao)).andExpect(status().isOk())
            .andExpect(content().string(containsString("Minha conta")))
            .andExpect(content().string(containsString("Profissional de teste")))
            .andExpect(content().string(containsString("profissional@example.test")))
            .andExpect(content().string(not(containsString("Login do profissional"))));
        mvc.perform(get("/")).andExpect(content().string(containsString("Login do profissional")))
            .andExpect(content().string(not(containsString("profissional@example.test"))))
            .andExpect(content().string(not(containsString("Minha conta"))));
    }
    @Test void logoutExigeCsrf() throws Exception {
        mvc.perform(post("/logout").with(user("teste").roles("PROFISSIONAL"))).andExpect(status().isForbidden());
        mvc.perform(post("/logout").with(user("teste").roles("PROFISSIONAL")).with(csrf()))
            .andExpect(redirectedUrl("/login?logout"));
    }
}
