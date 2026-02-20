package co.com.bancolombia.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("branches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchEntity {
    @Id
    private Long id;
    private String name;
    private Long franchiseId;
}

