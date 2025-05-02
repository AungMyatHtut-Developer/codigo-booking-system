package codigo.amh.codetest.bookingsystem.codigobookingsystem.service;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.common.Messages;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.classes.BookedClassResponse;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.classes.ClassesResponse;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.exception.BusinessException;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.exception.NotFoundException;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.*;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.repository.BookingRepository;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.repository.ClassesRepository;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.repository.UserPackageRepository;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.repository.WaitListRepository;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.util.DateTimeUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassesService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ClassesRepository classesRepository;
    private final UserPackageRepository userPackageRepository;
    private final BookingRepository bookingRepository;
    private final WaitListRepository waitListRepository;
    private final UserService userService;

    public ClassesService(RedisTemplate<String, String> redisTemplate, ClassesRepository classesRepository, UserPackageRepository userPackageRepository, BookingRepository bookingRepository, WaitListRepository waitListRepository, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.classesRepository = classesRepository;
        this.userPackageRepository = userPackageRepository;
        this.bookingRepository = bookingRepository;
        this.waitListRepository = waitListRepository;
        this.userService = userService;
    }

    public List<ClassesResponse> getAllClasses() {
        List<Classes> classesList = classesRepository.findAll();
        return classesList.stream()
                .map(classes -> new ClassesResponse(
                        classes.getId(),
                        classes.getName(),
                        classes.getCountryCode(),
                        DateTimeUtils.format(classes.getStartTime()),
                        DateTimeUtils.format(classes.getEndTime()),
                        classes.getCreditRequired(),
                        classes.getMaxCapacity(),
                        DateTimeUtils.format(classes.getCreatedDate())
                ))
                .collect(Collectors.toList());
    }

    public List<ClassesResponse> findAllClassesByCountryCode(String countryCode) {
        List<Classes> classesList = classesRepository.findAllByCountryCode(countryCode);
        return classesList.stream()
                .map(classes -> new ClassesResponse(
                        classes.getId(),
                        classes.getName(),
                        classes.getCountryCode(),
                        DateTimeUtils.format(classes.getStartTime()),
                        DateTimeUtils.format(classes.getEndTime()),
                        classes.getCreditRequired(),
                        classes.getMaxCapacity(),
                        DateTimeUtils.format(classes.getCreatedDate())
                ))
                .collect(Collectors.toList());
    }

    public List<BookedClassResponse> getBookedClasses(String email) {
        AppUser user = userService.findUserByEmail(email);
        if (user == null) {
            throw new NotFoundException(Messages.USER_NOT_FOUND);
        }

        List<Booking> bookingList = bookingRepository.findAllByUser(user);
        return bookingList.stream()
                .filter(booking -> booking.getCancelled() == null || !booking.getCancelled())
                .map(booking -> new BookedClassResponse(
                                booking.getBookedClass().getId(),
                                booking.getBookedClass().getName(),
                                DateTimeUtils.format(booking.getBookedClass().getStartTime()),
                                DateTimeUtils.format(booking.getBookedClass().getEndTime()),
                                booking.getBookedClass().getCreditRequired(),
                                booking.getBookedClass().getMaxCapacity(),
                                DateTimeUtils.format(booking.getBookedClass().getCreatedDate()),
                                booking.getId(),
                                DateTimeUtils.format(booking.getBookingDate()),
                                booking.getAttended(),
                                false
                        )
                ).collect(Collectors.toList());

    }

    public List<BookedClassResponse> getCancelledClasses(String email) {
        AppUser user = userService.findUserByEmail(email);
        if (user == null) {
            throw new NotFoundException(Messages.USER_NOT_FOUND);
        }

        List<Booking> bookingList = bookingRepository.findAllByUser(user);
        return bookingList.stream()
                .filter(booking -> booking.getCancelled() != null && booking.getCancelled())
                .map(booking -> new BookedClassResponse(
                                booking.getBookedClass().getId(),
                                booking.getBookedClass().getName(),
                                DateTimeUtils.format(booking.getBookedClass().getStartTime()),
                                DateTimeUtils.format(booking.getBookedClass().getEndTime()),
                                booking.getBookedClass().getCreditRequired(),
                                booking.getBookedClass().getMaxCapacity(),
                                DateTimeUtils.format(booking.getBookedClass().getCreatedDate()),
                                booking.getId(),
                                DateTimeUtils.format(booking.getBookingDate()),
                                booking.getAttended(),
                                true
                        )
                ).collect(Collectors.toList());

    }

    @Transactional
    public void bookClass(Long classId, Long userPackageId) {
        Optional<Classes> classes = classesRepository.findById(classId);
        if (classes.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.CLASS_NOT_FOUND);
        }

        Optional<UserPackage> userPackage = userPackageRepository.findById(userPackageId);
        if (userPackage.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.PACKAGES_NOT_FOUND);
        }

        Classes classObj = classes.get();
        UserPackage userPackageObj = userPackage.get();

        //Validate country code
        if (!classObj.getCountryCode().contentEquals(userPackageObj.getPack().getCountryCode())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.DIFFERENT_COUNTRY);
        }

        //Validate UserId and BookdId for duplicate booking
        Boolean isAlreadyBooked = bookingRepository.existsByUserIdAndBookedClassId(
                userPackageObj.getUser().getId(), classObj.getId());
        if (isAlreadyBooked) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.ALREADY_BOOKED);
        }

        // Validate time
        if (classObj.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.CLASS_WAS_ENDED);
        }

        // Use Redis to manage concurrent booking count
        String redisKey = "class:booking:count:" + classObj.getId();
        Long currentCount = redisTemplate.opsForValue().increment(redisKey);

        // Set key expiry if newly created
        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(redisKey, Duration.between(LocalDateTime.now(), classObj.getEndTime()));
        }

        // Check if capacity exceeded
        if (currentCount != null && currentCount > classObj.getMaxCapacity()) {
            // Rollback increment
            redisTemplate.opsForValue().decrement(redisKey);

            // Add to waitlist
            Waitlist waitlist = new Waitlist(null, userPackageObj.getUser(), classObj, LocalDateTime.now(), false);
            waitListRepository.save(waitlist);
            return;
        }


        // Prevent overlap with other bookings
        boolean hasConflict = bookingRepository.existsByUserPackageAppUserIdAndClassTimeOverlap(
                userPackageObj.getId(), classObj.getStartTime(), classObj.getEndTime());

        if (hasConflict) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.TIME_CONFLICT);
        }


        // Check remaining capacity
        long currentBookings = bookingRepository.countByBookedClassId(classId);
        if (currentBookings >= classObj.getMaxCapacity()) {
            // Add to waitlist
            Waitlist waitlist = new Waitlist(null, userPackageObj.getUser(), classObj, LocalDateTime.now(), false);
            waitListRepository.save(waitlist);
            return;
        }

        // Check credit
        if (userPackageObj.getRemainingCredits() < classObj.getCreditRequired()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.INSUFFICIENT_CREDITS);
        }

        // Deduct credit and book
        Booking booking = new Booking();
        booking.setBookedClass(classObj);
        booking.setUser(userPackageObj.getUser());
        booking.setBookingDate(LocalDateTime.now());
        booking.setAttended(false);
        booking.setCancelled(false);

        bookingRepository.save(booking);
        userPackageObj.setRemainingCredits(userPackageObj.getRemainingCredits() - classObj.getCreditRequired());
        userPackageObj.setUsedCredits(userPackageObj.getUsedCredits() + classObj.getCreditRequired());
        userPackageRepository.save(userPackageObj);
    }

    @Transactional
    public void cancelClass(Long classId, String email) {
        AppUser user = userService.findUserByEmail(email);
        if (user == null) {
            throw new NotFoundException(Messages.USER_NOT_FOUND);
        }

        Booking booking = bookingRepository.findByUserIdAndClassId(user.getId(), classId);
        if (booking == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.BOOKING_NOT_FOUND);
        }

        if (booking.getCancelled()) {
            return;
        }

        Classes classObj = booking.getBookedClass();

        // Cancel the booking
        booking.setCancelled(true);
        bookingRepository.save(booking);

        // Refund if cancelled at least 4 hours before class start
        if (LocalDateTime.now().isBefore(classObj.getStartTime().minusHours(4))) {
            UserPackage userPackage = userPackageRepository
                    .findTopByUserIdAndCountryCode(user.getId(), classObj.getCountryCode());

            if (userPackage != null) {
                int refund = classObj.getCreditRequired();
                userPackage.setRemainingCredits(userPackage.getRemainingCredits() + refund);
                userPackage.setUsedCredits(userPackage.getUsedCredits() - refund);
                userPackageRepository.save(userPackage);
            }
        }

        // Promote from waitlist
        List<Waitlist> waitlistUser = waitListRepository
                .findFirstByWaitlistedClassIdAndNotifiedOrderByJoinedAtAsc(classObj.getId(), false);

        if (!waitlistUser.isEmpty()) {
            Waitlist nextWaitlistUser = waitlistUser.get(0);

            // Mark the user as notified before removing from the waitlist
            nextWaitlistUser.setNotified(true);
            waitListRepository.save(nextWaitlistUser);  // Save the change for notified status

            // Promote the user from the waitlist to a booking
            Booking newBooking = new Booking();
            newBooking.setBookedClass(classObj);
            newBooking.setUser(nextWaitlistUser.getUser());
            newBooking.setBookingDate(LocalDateTime.now());
            newBooking.setAttended(false); // Assuming the user hasn't attended yet
            newBooking.setCancelled(false);

            bookingRepository.save(newBooking);

            // Remove the user from the waitlist after promotion
            waitListRepository.delete(nextWaitlistUser);
        }
    }

    @Transactional
    public void checkInToClass(Long classId, String email) {
        AppUser user = userService.findUserByEmail(email);
        if (user == null) {
            throw new NotFoundException(Messages.USER_NOT_FOUND);
        }

        Booking booking = bookingRepository.findByUserIdAndClassId(user.getId(), classId);
        if (booking == null || booking.getCancelled()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.BOOKING_NOT_FOUND);
        }

        Classes classObj = booking.getBookedClass();
        LocalDateTime now = LocalDateTime.now();

        // Class must be in progress to check in
        if (now.isBefore(classObj.getStartTime()) || now.isAfter(classObj.getEndTime())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.CHECK_IN_NOT_AVAILABLE);
        }

        if (booking.isCheckedIn()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Already checked in.");
        }

        // Perform check-in
        booking.setCheckedIn(true);
        booking.setCheckInTime(now);
        bookingRepository.save(booking);
    }
}
