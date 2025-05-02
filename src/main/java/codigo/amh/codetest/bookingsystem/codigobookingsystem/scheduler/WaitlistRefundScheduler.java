package codigo.amh.codetest.bookingsystem.codigobookingsystem.scheduler;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.AppUser;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.Classes;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.UserPackage;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.Waitlist;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.repository.ClassesRepository;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.repository.UserPackageRepository;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.repository.WaitListRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class WaitlistRefundScheduler {

    private final ClassesRepository classesRepository;
    private final WaitListRepository waitListRepository;
    private final UserPackageRepository userPackageRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void refundCreditsForStartingClasses() {
        log.info("Scheduler started to refund and remove waitlist users for starting classes.");
        LocalDateTime now = LocalDateTime.now();

        // Get classes that are starting
        List<Classes> startingClasses = classesRepository.findByStartTimeBeforeAndEndTimeAfter(now, now);

        for (Classes classObj : startingClasses) {
            List<Waitlist> waitlistUsers = waitListRepository.findByWaitlistedClassId(classObj.getId());

            for (Waitlist waitlist : waitlistUsers) {
                AppUser user = waitlist.getUser();

                // Refund credits ONLY IF you deducted credits when joining waitlist
                UserPackage userPackage = userPackageRepository
                        .findTopByUserIdAndCountryCode(user.getId(), classObj.getCountryCode());

                if (userPackage != null) {
                    int credits = classObj.getCreditRequired();
                    userPackage.setRemainingCredits(userPackage.getRemainingCredits() + credits);
                    userPackage.setUsedCredits(userPackage.getUsedCredits() - credits);
                    userPackageRepository.save(userPackage);
                }

                // Remove user from waitlist
                waitListRepository.delete(waitlist);
            }
        }
    }
}
