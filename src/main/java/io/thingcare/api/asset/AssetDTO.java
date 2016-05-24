package io.thingcare.api.asset;

import javax.validation.constraints.NotNull;

import io.thingcare.api.Dto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssetDto extends Dto {
	private static final long serialVersionUID = -2976279140125635164L;

	private String id;

	@NotNull
	private String name;
}
