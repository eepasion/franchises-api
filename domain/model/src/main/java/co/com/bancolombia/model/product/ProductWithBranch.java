package co.com.bancolombia.model.product;

import co.com.bancolombia.model.branch.Branch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithBranch {
    private Product product;
    private Branch branch;
}
