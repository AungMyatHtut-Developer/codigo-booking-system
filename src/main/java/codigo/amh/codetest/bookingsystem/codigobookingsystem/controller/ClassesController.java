package codigo.amh.codetest.bookingsystem.codigobookingsystem.controller;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.common.Messages;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.classes.BookClassesRequest;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.classes.CancelBookRequest;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.exception.UnauthorizedException;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.security.JwtUtil;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.service.ClassesService;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.util.ResponseHelper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/classes/v1")
@AllArgsConstructor
@Slf4j
public class ClassesController {

    private final ClassesService classesService;
    private final JwtUtil jwtUtil;

    @GetMapping("/all")
    public ResponseEntity<?> getAllClases() {
        return ResponseHelper.success(classesService.getAllClasses());
    }

    @GetMapping("/all-with-country-code")
    public ResponseEntity<?> getAllPackagesByCountryCode(@RequestParam String countryCode) {
        return ResponseHelper.success(classesService.findAllClassesByCountryCode(countryCode));
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookClass(@Valid @RequestBody BookClassesRequest request) {
        classesService.bookClass(request.classId(), request.userPackageId());
        return ResponseHelper.success(Messages.BOOKING_SUCCESS);
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelClass(@Valid @RequestBody CancelBookRequest request, @RequestHeader("Authorization") String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            throw new UnauthorizedException(Messages.USER_NOT_FOUND);
        }
        classesService.cancelClass(request.classId(), email);
        return ResponseHelper.success(Messages.BOOKING_CANCEL);
    }

    @GetMapping("/booked-classes")
    public ResponseEntity<?> getAllBookedClasses(@RequestHeader("Authorization") String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            throw new UnauthorizedException(Messages.USER_NOT_FOUND);
        }
        return ResponseHelper.success(classesService.getBookedClasses(email));
    }

    @GetMapping("/cancelled-classes")
    public ResponseEntity<?> getAllCancelledClasses(@RequestHeader("Authorization") String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            throw new UnauthorizedException(Messages.USER_NOT_FOUND);
        }
        return ResponseHelper.success(classesService.getCancelledClasses(email));
    }

    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestParam Long classId, @RequestHeader("Authorization") String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            throw new UnauthorizedException(Messages.USER_NOT_FOUND);
        }
        classesService.checkInToClass(classId, email);
        return ResponseHelper.success(Messages.CHECK_IN_SUCCESS);
    }

    private String extractEmailFromToken(String authorizationHeader) {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        if (token != null && jwtUtil.isTokenValid(token)) {
            return jwtUtil.extractUsername(token);
        }

        return null;
    }
}
