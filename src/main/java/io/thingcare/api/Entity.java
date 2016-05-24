package io.thingcare.api;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class Entity implements Serializable {
    private static final long serialVersionUID = 5483062864964343432L;

    @Id
	protected String id;
	protected String externalId;
	protected String code;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
