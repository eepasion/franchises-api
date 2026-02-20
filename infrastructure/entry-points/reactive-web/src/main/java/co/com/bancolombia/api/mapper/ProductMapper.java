package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.AddProductRequest;
import co.com.bancolombia.api.dto.response.ProductResponse;
import co.com.bancolombia.model.product.Product;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductMapper {

    public static Product toDomain(AddProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .stock(request.getStock())
                .build();
    }

    public static ProductResponse toDto(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .branchId(product.getBranchId())
                .build();
    }
}
