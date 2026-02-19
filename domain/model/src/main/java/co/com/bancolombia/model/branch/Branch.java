package co.com.bancolombia.model.branch;
import co.com.bancolombia.model.product.Product;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Branch {
    private String id;
    private String name;
    private List<Product> products;
}
