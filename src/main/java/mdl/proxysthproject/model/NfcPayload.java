package mdl.proxysthproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NfcPayload {
    private String idNumber;
    private String fullName;
    private String dob;
    private String expiry;
    private String portraitHash;
    private boolean forceMatchFail; // Configurable for testing
}
