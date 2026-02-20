package co.com.bancolombia.model.branch;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Branch {
    private Long id;
    private String name;
    private Long franchiseId;
}
