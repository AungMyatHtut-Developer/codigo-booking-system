package codigo.amh.codetest.bookingsystem.codigobookingsystem.repository;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WaitListRepository extends JpaRepository<Waitlist, Long> {

    @Query("SELECT w FROM Waitlist w " +
            "WHERE w.waitlistedClass.id = :classId AND w.notified = :notified " +
            "ORDER BY w.joinedAt ASC")
    List<Waitlist> findFirstByWaitlistedClassIdAndNotifiedOrderByJoinedAtAsc(
            @Param("classId") Long classId,
            @Param("notified") Boolean notified);

    List<Waitlist> findByWaitlistedClassId(Long classId);
}
