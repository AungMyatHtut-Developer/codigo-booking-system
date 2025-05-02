package codigo.amh.codetest.bookingsystem.codigobookingsystem.util;

import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

    public class ResponseHelper {
        public static ResponseEntity<ApiResponse<?>> createResponse(
                HttpStatus status, @Nullable Object data, Map<String, String> errors) {
            return ResponseEntity.status(status).body(new ApiResponse<>(status.value(), status.getReasonPhrase(), data, errors));
        }

        public static ResponseEntity<ApiResponse<?>> success(Object data) {
            return createResponse(HttpStatus.OK, data, null);
        }
    }
