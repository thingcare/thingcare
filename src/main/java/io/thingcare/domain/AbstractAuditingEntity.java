package io.thingcare.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.ZonedDateTime;


/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@Getter
@Setter
public abstract class AbstractAuditingEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 6044782900770678662L;

    @CreatedBy
    @Field("created_by")
    @JsonIgnore
    private String createdBy;

    @CreatedDate
    @Field("created_date")
    @JsonIgnore
    private ZonedDateTime createdDate = ZonedDateTime.now();

    @LastModifiedBy
    @Field("last_modified_by")
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @Field("last_modified_date  ")
    @JsonIgnore
    private ZonedDateTime lastModifiedDate = ZonedDateTime.now();

}
