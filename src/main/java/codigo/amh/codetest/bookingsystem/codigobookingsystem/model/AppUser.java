package codigo.amh.codetest.bookingsystem.codigobookingsystem.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String email;
    private String countryCode;
    private String password;
    private String salt;
    private Boolean isEnabled;
    private Boolean isLock;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String otp;
    private Integer otpCount;
    private LocalDateTime otpExpireTime;
    private Boolean isOtpActive;
}
