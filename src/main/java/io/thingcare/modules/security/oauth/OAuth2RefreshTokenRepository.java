package io.thingcare.modules.security.oauth;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OAuth2RefreshTokenRepository extends MongoRepository<OAuth2AuthenticationRefreshToken, String> {
	OAuth2AuthenticationRefreshToken findByTokenId(String tokenId);
}
