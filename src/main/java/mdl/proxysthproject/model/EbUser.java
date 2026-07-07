package mdl.proxysthproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EbUser {
    private String phone;
    private String name;
    private String status; // ACTIVE or INACTIVE
}
