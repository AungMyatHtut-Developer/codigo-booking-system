package codigo.amh.codetest.bookingsystem.codigobookingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CodigoBookingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodigoBookingSystemApplication.class, args);
    }

}
