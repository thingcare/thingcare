package io.thingcare.api;

import java.time.ZonedDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public abstract class AuditingEntity extends Entity {
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
