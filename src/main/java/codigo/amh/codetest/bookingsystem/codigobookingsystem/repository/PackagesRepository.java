package codigo.amh.codetest.bookingsystem.codigobookingsystem.repository;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.Packages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackagesRepository extends JpaRepository<Packages,Long> {
    List<Packages> findAllByCountryCode(String countryCode);
}
