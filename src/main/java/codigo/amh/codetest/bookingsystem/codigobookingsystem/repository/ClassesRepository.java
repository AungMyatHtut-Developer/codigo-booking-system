package codigo.amh.codetest.bookingsystem.codigobookingsystem.repository;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.Classes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ClassesRepository extends JpaRepository<Classes,Long> {
    List<Classes> findAllByCountryCode(String CountryCode);
    List<Classes> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime start, LocalDateTime end);
}
