package co.com.bancolombia.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductRequest {
    @NotBlank(message = "The name is a required field.")
    private String name;
    @NotNull(message = "The stock is a required field.")
    @Min(value = 0, message = "Stock must be greater than or equal to 0.")
    private Integer stock;
}
