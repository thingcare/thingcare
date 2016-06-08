package io.thingcare.modules.security.oauth;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import io.thingcare.api.Entity;
import lombok.Data;

@Data
@Document(collection = "security.OAuth2AuthenticationRefreshToken")
public class OAuth2AuthenticationRefreshToken extends Entity {
    private static final long serialVersionUID = -8299627319646310797L;

    private String tokenId;

    private OAuth2RefreshToken oAuth2RefreshToken;

    private OAuth2Authentication authentication;

    public OAuth2AuthenticationRefreshToken(OAuth2RefreshToken oAuth2RefreshToken, OAuth2Authentication authentication) {
        this.id = UUID.randomUUID().toString();
        this.oAuth2RefreshToken = oAuth2RefreshToken;
        this.authentication = authentication;
        this.tokenId = oAuth2RefreshToken.getValue();
    }
}
