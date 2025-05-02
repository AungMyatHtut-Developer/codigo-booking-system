package codigo.amh.codetest.bookingsystem.codigobookingsystem.repository;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.AppUser;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserPackageRepository extends JpaRepository<UserPackage, Long> {
    List<UserPackage> findUserPackageByUser(AppUser user);

    @Query("SELECT up FROM UserPackage up " +
            "WHERE up.user.id = :userId " +
            "AND up.pack.countryCode = :countryCode " +
            "ORDER BY up.purchaseDate DESC")
    UserPackage findTopByUserIdAndCountryCode(@Param("userId") Long userId,
                                              @Param("countryCode") String countryCode);
}
