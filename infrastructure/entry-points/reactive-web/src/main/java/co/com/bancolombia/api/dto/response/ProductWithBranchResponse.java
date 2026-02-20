package co.com.bancolombia.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithBranchResponse {
    private Long productId;
    private String productName;
    private Integer stock;
    private Long branchId;
    private String branchName;
}
