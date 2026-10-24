package web.ambmao.cartilha;

import java.text.Normalizer;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;
import web.ambmao.cartilha.dto.CartilhaSearchRequest;

public final class CartilhaSpecs {
    private CartilhaSpecs() { }
    public static String normalizar(String nome) {
        if (nome == null) return "";
        return Normalizer.normalize(nome, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).strip();
    }
    public static Specification<Cartilha> comFiltros(CartilhaSearchRequest request) {
        return (root, query, cb) -> {
            var filtros = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            for (String termo : normalizar(request.nomeDoenca()).split("\\s+")) {
                if (!termo.isEmpty()) {
                    String seguro = termo.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
                    filtros.add(cb.like(root.get("nomeBusca"), "%" + seguro + "%", '\\'));
                }
            }
            return cb.and(filtros.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }
}
