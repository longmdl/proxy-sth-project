package mdl.proxysthproject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "eb_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EbUser {
    @Id
    private String phone;
    private String name;
    private String status; // ACTIVE or INACTIVE
}
