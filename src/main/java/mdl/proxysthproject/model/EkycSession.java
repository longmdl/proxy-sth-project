package mdl.proxysthproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EkycSession {
    private String transactionId;
    private String idNumber;
    private String birthDate;
    private String expiration;
    private String channel;
    private String partnerId;
    private String accessToken;
    private String xApiKey;
    private String secretSignature;
    private String algSignature;
    private int nfcAttemptCount;
}
