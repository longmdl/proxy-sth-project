package mdl.proxysthproject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ekyc_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EkycSession {
    @Id
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
