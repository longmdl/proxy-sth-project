package mdl.proxysthproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NfcPayloadDto {
    private Long id;
    private String idNumber;
    private String fullName;
    private String dob;
    private String expiry;
    private String portraitHash;
    private boolean forceMatchFail;
}