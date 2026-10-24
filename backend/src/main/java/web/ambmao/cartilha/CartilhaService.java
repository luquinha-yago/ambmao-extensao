package web.ambmao.cartilha;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import web.ambmao.cartilha.dto.*;

@Service
@Transactional(readOnly = true)
public class CartilhaService {
    private final CartilhaRepository repository;
    public CartilhaService(CartilhaRepository repository) { this.repository = repository; }
    public List<CartilhaResponse> pesquisar(CartilhaSearchRequest request) {
        return repository.findAll(CartilhaSpecs.comFiltros(request), Sort.by("nomeBusca", "codigo"))
            .stream().map(this::toResponse).toList();
    }
    public CartilhaForm buscarParaEdicao(Long codigo) {
        Cartilha c = buscar(codigo);
        return new CartilhaForm(c.getNomeDoenca(), c.getOQueE(), c.getSinaisSintomas(),
            c.getOrientacoesGerais(), c.getFonteTitulo(), c.getFonteUrl());
    }
    @Transactional
    public void salvar(CartilhaForm form) { repository.save(new Cartilha(form)); }
    @Transactional
    public void atualizar(Long codigo, CartilhaForm form) {
        Cartilha cartilha = buscar(codigo);
        cartilha.atualizar(form);
        repository.save(cartilha);
    }
    @Transactional
    public void excluir(Long codigo) { repository.delete(buscar(codigo)); }
    private Cartilha buscar(Long codigo) {
        return repository.findById(codigo).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Cartilha não encontrada"));
    }
    private CartilhaResponse toResponse(Cartilha c) {
        return new CartilhaResponse(c.getCodigo(), c.getNomeDoenca(), c.getOQueE(),
            c.getSinaisSintomas(), c.getOrientacoesGerais(), c.getFonteTitulo(), c.getFonteUrl());
    }
}
