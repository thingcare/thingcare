package io.thingcare.modules.security.oauth;

import java.util.UUID;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import io.thingcare.api.Entity;
import lombok.Data;

@Data
@Document(collection = "security.OAuth2AuthenticationAccessToken")
public class OAuth2AuthenticationAccessToken extends Entity {
	private static final long serialVersionUID = 2561059148547756363L;

	private String tokenId;

	private OAuth2AccessToken oAuth2AccessToken;

	private String authenticationId;

	private String userName;

	private String clientId;

	private OAuth2Authentication authentication;

	private String refreshToken;

	@PersistenceConstructor
	public OAuth2AuthenticationAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication,
			String authenticationId) {
		this.id = UUID	.randomUUID()
						.toString();
		this.tokenId = oAuth2AccessToken.getValue();
		this.oAuth2AccessToken = oAuth2AccessToken;
		this.authenticationId = authenticationId;
		this.userName = authentication.getName();
		this.clientId = authentication	.getOAuth2Request()
										.getClientId();
		this.authentication = authentication;
		if (oAuth2AccessToken.getRefreshToken() != null) {
			this.refreshToken = oAuth2AccessToken	.getRefreshToken()
													.getValue();
		}
	}
}
