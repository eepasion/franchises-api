package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.AddBranchRequest;
import co.com.bancolombia.api.dto.response.BranchResponse;
import co.com.bancolombia.model.branch.Branch;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BranchMapper {

    public static Branch toDomain(AddBranchRequest request) {
        return Branch.builder()
                .name(request.getName())
                .build();
    }

    public static BranchResponse toDto(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .franchiseId(branch.getFranchiseId())
                .build();
    }
}
