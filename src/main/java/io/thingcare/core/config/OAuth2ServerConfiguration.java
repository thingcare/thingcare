package io.thingcare.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import io.thingcare.core.config.security.MongoDBTokenStore;
import io.thingcare.modules.security.authority.AuthoritiesConstants;
import io.thingcare.modules.security.authority.Http401UnauthorizedEntryPoint;
import io.thingcare.modules.security.oauth.AjaxLogoutSuccessHandler;
import io.thingcare.modules.security.oauth.OAuth2AccessTokenRepository;
import io.thingcare.modules.security.oauth.OAuth2RefreshTokenRepository;

@Configuration
public class OAuth2ServerConfiguration {

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
		@Autowired
		private Http401UnauthorizedEntryPoint authenticationEntryPoint;
		@Autowired
		private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint)
				.and()
				.logout()
				.logoutUrl("/api/logout")
				.logoutSuccessHandler(ajaxLogoutSuccessHandler)
				.and()
				.csrf()
				.disable()
				.headers()
				.frameOptions()
				.disable()
				.and()
				.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/**")
				.permitAll()
				.antMatchers("/api/authenticate")
				.permitAll()
				.antMatchers("/api/register")
				.permitAll()
				.antMatchers("/api/**")
				.authenticated()
				.antMatchers("/websocket/tracker")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/websocket/**")
				.permitAll()
				.antMatchers("/management/**")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/v2/api-docs/**")
				.permitAll()
				.antMatchers("/configuration/ui")
				.permitAll()
				.antMatchers("/swagger-ui/index.html")
				.hasAuthority(AuthoritiesConstants.ADMIN);
		}
	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

		@Autowired
		private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

		@Autowired
		private ThingcareProperties thingcareProperties;
		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;

		@Bean
		public TokenStore tokenStore() {
			return new MongoDBTokenStore(oAuth2AccessTokenRepository, oAuth2RefreshTokenRepository);
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

			endpoints	.tokenStore(tokenStore())
						.authenticationManager(authenticationManager);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.allowFormAuthenticationForClients();
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients	.inMemory()
					.withClient(thingcareProperties	.getSecurity()
													.getAuthentication()
													.getOauth()
													.getClientid())
					.scopes("read", "write")
					.authorities(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
					.authorizedGrantTypes("password", "refresh_token", "authorization_code", "implicit")
					.secret(thingcareProperties	.getSecurity()
												.getAuthentication()
												.getOauth()
												.getSecret())
					.accessTokenValiditySeconds(thingcareProperties	.getSecurity()
																	.getAuthentication()
																	.getOauth()
																	.getTokenValidityInSeconds());
		}
	}
}
