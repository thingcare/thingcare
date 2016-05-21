package io.thingcare.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Persist AuditEvent managed by the Spring Boot actuator
 * @see org.springframework.boot.actuate.audit.AuditEvent
 */
@Data
@Document(collection = "security.persistent_audit_event")
public class PersistentAuditEvent {

    @Id
    @Field("event_id")
    private String id;

    @NotNull
    private String principal;

    private LocalDateTime auditEventDate;

    @Field("event_type")
    private String auditEventType;

    private Map<String, String> data = new HashMap<>();

}
