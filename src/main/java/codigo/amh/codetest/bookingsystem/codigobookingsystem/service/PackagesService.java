package codigo.amh.codetest.bookingsystem.codigobookingsystem.service;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.common.Messages;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.packages.PackagesResponse;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.packages.PurchasePackageRequest;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.user_packages.PurchasedPackageResponse;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.exception.BusinessException;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.AppUser;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.Packages;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.UserPackage;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.repository.PackagesRepository;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.repository.UserPackageRepository;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.util.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static codigo.amh.codetest.bookingsystem.codigobookingsystem.util.MaskUtils.mask;

@Service
@Slf4j
public class PackagesService {
    private final UserService userService;
    private final PackagesRepository packagesRepository;
    private final UserPackageRepository userPackageRepository;

    public PackagesService(UserService userService, PackagesRepository packagesRepository, UserPackageRepository userPackageRepository) {
        this.userService = userService;
        this.packagesRepository = packagesRepository;
        this.userPackageRepository = userPackageRepository;
    }

    public PackagesResponse getPackagesById(long id) {
        Optional<Packages> packages = packagesRepository.findById(id);
        if (packages.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.PACKAGES_NOT_FOUND);
        }

        return new PackagesResponse(
                packages.get().getId(),
                packages.get().getName(),
                packages.get().getCountryCode(),
                packages.get().getCredits(),
                packages.get().getPrice(),
                packages.get().getValidDays(),
                DateTimeUtils.format(packages.get().getCreatedDate())
        );
    }

    public List<PackagesResponse> getAllPackages() {
        List<Packages> packagesList = packagesRepository.findAll();
        return packagesList.stream()
                .map(pkg -> new PackagesResponse(
                        pkg.getId(),
                        pkg.getName(),
                        pkg.getCountryCode(),
                        pkg.getCredits(),
                        pkg.getPrice(),
                        pkg.getValidDays(),
                        DateTimeUtils.format(pkg.getCreatedDate())
                ))
                .collect(Collectors.toList());
    }

    public List<PackagesResponse> findAllByCountryCode(String countryCode) {
        List<Packages> packagesList = packagesRepository.findAllByCountryCode(countryCode);
        return packagesList.stream()
                .map(pkg -> new PackagesResponse(
                        pkg.getId(),
                        pkg.getName(),
                        pkg.getCountryCode(),
                        pkg.getCredits(),
                        pkg.getPrice(),
                        pkg.getValidDays(),
                        DateTimeUtils.format(pkg.getCreatedDate())
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void buyPackage(PurchasePackageRequest purchasePackageRequest, String email) {
        Optional<Packages> packages = packagesRepository.findById(Long.valueOf(purchasePackageRequest.packageId()));

        if (packages.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.PACKAGES_NOT_FOUND);
        }

        if (!validateUseCredit(purchasePackageRequest.creditCardNumber())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.CREDIT_CARD_NOT_VALID);
        }

        if (!paymentCharge(purchasePackageRequest.creditCardNumber(), packages.get().getPrice())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    Messages.INSUFFICIENT_BALANCE);
        }

        AppUser user = userService.findUserByEmail(email);

        UserPackage userPackage = new UserPackage();
        userPackage.setUser(user);
        userPackage.setPack(packages.get());
        userPackage.setRemainingCredits(packages.get().getCredits());
        userPackage.setUsedCredits(0);
        userPackage.setPurchaseDate(LocalDateTime.now());
        userPackage.setExpiryDate(LocalDateTime.now().plusDays(packages.get().getValidDays()));
        userPackage.setIsActive(true);

        userPackageRepository.save(userPackage);
        log.info("User {} purchased package {} successfully.", user.getEmail(), packages.get().getName());
    }

    public boolean validateUseCredit(String creditCarNumber) {
        log.info("Check User Balance");
        return true;
    }

    public boolean paymentCharge(String creditCardNumber, BigDecimal amount) {
        log.info("Payment with CreditCardNumber : {}, Amount : {}", mask(creditCardNumber), amount);
        return true;
    }

    public List<PurchasedPackageResponse> getAllPurchasedPackages(String email) {
        AppUser appUser = userService.findUserByEmail(email);
        List<UserPackage> userPackageList = userPackageRepository.findUserPackageByUser(appUser);
        List<PurchasedPackageResponse> purchasedPackageResponseList = userPackageList.stream().map(userPackage -> new PurchasedPackageResponse(
                userPackage.getPack().getId(),
                userPackage.getPack().getName(),
                userPackage.getPack().getCountryCode(),
                userPackage.getPack().getCredits(),
                userPackage.getPack().getPrice(),
                userPackage.getPack().getValidDays(),
                DateTimeUtils.format(userPackage.getPack().getCreatedDate()),
                userPackage.getRemainingCredits(),
                userPackage.getUsedCredits(),
                userPackage.getId(),
                DateTimeUtils.format(userPackage.getPurchaseDate()),
                DateTimeUtils.format(userPackage.getExpiryDate()),
                userPackage.getExpiryDate().isAfter(LocalDateTime.now()) ? "Not Expired" : "Expired",
                userPackage.getIsActive()
        )).collect(Collectors.toList());
        return purchasedPackageResponseList;
    }
}
