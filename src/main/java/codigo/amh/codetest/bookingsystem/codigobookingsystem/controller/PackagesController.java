package codigo.amh.codetest.bookingsystem.codigobookingsystem.controller;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.common.Messages;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.packages.PackagesResponse;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.packages.PurchasePackageRequest;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.exception.UnauthorizedException;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.security.JwtUtil;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.service.PackagesService;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.util.ResponseHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.keyvalue.repository.query.PredicateQueryCreator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/package/v1")
@AllArgsConstructor
@Slf4j
public class PackagesController {

    private final JwtUtil jwtUtil;
    private final PackagesService packagesService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllPackages() {
        return ResponseHelper.success(packagesService.getAllPackages());
    }

    @GetMapping("/all-with-country-code")
    public ResponseEntity<?> getAllPackagesByCountryCode(@RequestParam String countryCode) {
        return ResponseHelper.success(packagesService.findAllByCountryCode(countryCode));
    }

    @GetMapping("/detail-info")
    public ResponseEntity<?> getPackageDetailInformation(@RequestParam int id) {
        return ResponseHelper.success(packagesService.getPackagesById(id));
    }

    @PostMapping("/purchase-package")
    public ResponseEntity<?> purchasePackage(@RequestBody PurchasePackageRequest purchasePackageRequest, @RequestHeader("Authorization") String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            throw new UnauthorizedException(Messages.USER_NOT_FOUND);
        }

        packagesService.buyPackage(purchasePackageRequest, email);
        return ResponseHelper.success(Messages.USER_PURCHASED_PACKAGE_SUCCESS);
    }

    @GetMapping("/get-all-purchased-package")
    public ResponseEntity<?> getPurchasedPackages(@RequestHeader("Authorization") String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            throw new UnauthorizedException(Messages.USER_NOT_FOUND);
        }

        return ResponseHelper.success(packagesService.getAllPurchasedPackages(email));
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
