package io.thingcare.modules.asset;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.thingcare.api.Entity;
import lombok.Data;

@Data
@Document(collection = "asset.Asset")
public class Asset extends Entity {
	private static final long serialVersionUID = -8757673461587506353L;

	@NotNull
	@Field("name")
	private String name;

}
