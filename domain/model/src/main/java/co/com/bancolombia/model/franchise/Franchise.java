package co.com.bancolombia.model.franchise;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Franchise {
    private Long id;
    private String name;
}
