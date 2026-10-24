package web.ambmao.profissional;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
    Optional<Profissional> findByEmail(String email);
    boolean existsByEmail(String email);
}
