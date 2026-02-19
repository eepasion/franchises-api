package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.CreateFranchiseRequest;
import co.com.bancolombia.api.dto.response.CreateFranchiseResponse;
import co.com.bancolombia.model.franchise.Franchise;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FranchiseMapper {
    public static Franchise toDomain(CreateFranchiseRequest createFranchiseRequest){
       return Franchise.builder()
               .name(createFranchiseRequest.getName())
               .build();
    }

    public static CreateFranchiseResponse toDto(Franchise franchise){
        return CreateFranchiseResponse.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .build();
    }


}
