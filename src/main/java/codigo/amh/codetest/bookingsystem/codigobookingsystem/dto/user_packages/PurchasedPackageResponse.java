package codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.user_packages;


import java.math.BigDecimal;

public record PurchasedPackageResponse(
        Long packageId,
        String packageName,
        String countryCode,
        Integer packageCredits,
        BigDecimal packagePrice,
        Integer packageValidDays,
        String packagedCreatedDate,
        Integer packageRemainingCredit,
        Integer usedCredits,
        Long purchasedPackageId,
        String packagePurchasedDate,
        String packageExpireDate,
        String isPackageExpired,
        boolean isPackageActive
) {
}
