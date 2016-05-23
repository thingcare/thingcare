package io.thingcare.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;
import java.util.UUID;

@Data
@Document(collection = "OAUTH_AUTHENTICATION_REFRESH_TOKEN")
public class OAuth2AuthenticationRefreshToken implements Serializable {

    private static final long serialVersionUID = -8299627319646310797L;

    @Id
    private String id;

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
