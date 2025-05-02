package codigo.amh.codetest.bookingsystem.codigobookingsystem.repository;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.AppUser;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Count the number of bookings that overlap with the user's requested class time
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.user.id = :userId " +  // Reference to AppUser's ID
            "AND ((b.bookedClass.startTime BETWEEN :startTime AND :endTime) OR " +
            "(b.bookedClass.endTime BETWEEN :startTime AND :endTime))" +
            "AND b.cancelled = false AND b.attended = false "
    )
    boolean existsByUserPackageAppUserIdAndClassTimeOverlap(@Param("userId") Long userId,
                                                            @Param("startTime") LocalDateTime startTime,
                                                            @Param("endTime") LocalDateTime endTime);


    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.user.id = :userId AND b.bookedClass.id = :classId AND b.cancelled = false")
    Boolean existsByUserIdAndBookedClassId(Long userId, Long classId);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.bookedClass.id = :classId " +
            "AND b.attended = false AND b.cancelled = false")
    Booking findByUserIdAndClassId(Long userId, Long classId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookedClass.id = :classId AND (b.cancelled IS NULL OR b.cancelled = false)")
    long countByBookedClassId(Long classId);

    List<Booking> findAllByUser(AppUser user);
}
