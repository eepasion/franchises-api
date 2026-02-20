package co.com.bancolombia.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Builder;
import lombok.Data;

@Table("franchises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseEntity {
    @Id
    private Long id;
    private String name;
}
