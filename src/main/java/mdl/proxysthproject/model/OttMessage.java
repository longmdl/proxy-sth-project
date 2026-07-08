package mdl.proxysthproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ott_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OttMessage {
    @Id
    private String id;
    private String recipientPhone;
    private String title;
    private String body;
    private LocalDateTime timestamp;
}
