package web.ambmao.cartilha.dto;

import jakarta.validation.constraints.Size;

public record CartilhaSearchRequest(@Size(max = 160) String nomeDoenca) { }
