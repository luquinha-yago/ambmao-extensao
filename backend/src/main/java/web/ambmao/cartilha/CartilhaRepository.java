package web.ambmao.cartilha;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CartilhaRepository extends JpaRepository<Cartilha, Long>, JpaSpecificationExecutor<Cartilha> { }
