package codigo.amh.codetest.bookingsystem.codigobookingsystem.repository;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    boolean existsAppUserByEmail(String email);

    AppUser findAppUserByEmail(String email);
}
