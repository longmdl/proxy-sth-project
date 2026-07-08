package mdl.proxysthproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nfc_payloads")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NfcPayload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String idNumber;
    private String fullName;
    private String dob;
    private String expiry;
    private String portraitHash;
    private boolean forceMatchFail; // Configurable for testing
}
