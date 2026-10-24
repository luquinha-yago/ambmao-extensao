package web.ambmao.cartilha.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import web.ambmao.core.validation.HttpUrl;

// Mesmo formulário para criação e edição; o código vem exclusivamente da rota.
public record CartilhaForm(
    @NotBlank(message = "Informe o nome da doença") @Size(max = 160) String nomeDoenca,
    @NotBlank(message = "Explique o que é a condição") @Size(max = 5000) String oQueE,
    @NotBlank(message = "Informe os sinais e sintomas") @Size(max = 5000) String sinaisSintomas,
    @NotBlank(message = "Informe as orientações gerais") @Size(max = 10000) String orientacoesGerais,
    @Size(max = 200) String fonteTitulo,
    @Size(max = 2000) @HttpUrl String fonteUrl
) {
    public static CartilhaForm vazio() { return new CartilhaForm("", "", "", "", "", ""); }
}
