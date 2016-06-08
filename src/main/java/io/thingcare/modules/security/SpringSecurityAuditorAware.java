package io.thingcare.modules.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import io.thingcare.api.security.user.UserCommonConstants;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

	@Override
	public String getCurrentAuditor() {
		String userName = SecurityUtils.getCurrentUserLogin();
		return (userName != null ? userName : UserCommonConstants.SYSTEM_ACCOUNT);
	}
}
