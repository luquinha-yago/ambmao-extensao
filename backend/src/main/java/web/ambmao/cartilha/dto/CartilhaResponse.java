package web.ambmao.cartilha.dto;

public record CartilhaResponse(Long codigo, String nomeDoenca, String oQueE,
    String sinaisSintomas, String orientacoesGerais, String fonteTitulo, String fonteUrl) {
    public java.util.List<String> getSinaisSintomasTopicos() {
        return sinaisSintomas.lines().map(String::strip).filter(linha -> !linha.isEmpty()).toList();
    }
}
