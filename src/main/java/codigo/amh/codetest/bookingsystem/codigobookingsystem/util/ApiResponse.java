package codigo.amh.codetest.bookingsystem.codigobookingsystem.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private Integer responseCode;
    private String responseStatus;
    private T data;
    private Map<String,String> error;
}
