package io.thingcare.modules.security.authority;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "security.Authority")
public class Authority implements Serializable {
	private static final long serialVersionUID = 1329781259259864522L;

	@NotNull
	@Size(max = 50)
	@Id
	private String name;
}
