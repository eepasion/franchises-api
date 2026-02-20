package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.AddProductRequest;
import co.com.bancolombia.api.dto.response.ProductResponse;
import co.com.bancolombia.api.dto.response.ProductWithBranchResponse;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.ProductWithBranch;
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
    public static ProductWithBranchResponse toDtoWithBranch(ProductWithBranch productWithBranch){
        return ProductWithBranchResponse.builder()
                .productId(productWithBranch.getProduct().getId())
                .productName(productWithBranch.getProduct().getName())
                .stock(productWithBranch.getProduct().getStock())
                .branchId(productWithBranch.getBranch().getId())
                .branchName(productWithBranch.getBranch().getName())
                .build();
    }
}
