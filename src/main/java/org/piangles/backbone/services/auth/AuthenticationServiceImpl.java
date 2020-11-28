package org.piangles.backbone.services.auth;

import java.util.HashMap;
import java.util.Map;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.impl.def.DefaultAuthenticator;
import org.piangles.backbone.services.auth.impl.sso.GoogleAuthenticator;
import org.piangles.backbone.services.config.ConfigService;
import org.piangles.backbone.services.config.Configuration;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.session.SessionManagementService;

public final class AuthenticationServiceImpl implements AuthenticationService
{
	private static final String COMPONENT_ID = "2f07e92e-8edf-4fed-897c-2df2bd2ae72d";
	
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private ConfigService configService = Locator.getInstance().getConfigService();
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();

	private Map<AuthenticationType, Authenticator> authenticatorMap;
	
	public AuthenticationServiceImpl() throws Exception
	{
		Configuration config = configService.getConfiguration(COMPONENT_ID);
		authenticatorMap = new HashMap<AuthenticationType, Authenticator>();
		authenticatorMap.put(AuthenticationType.Default, new DefaultAuthenticator(config));
		authenticatorMap.put(AuthenticationType.Google, new GoogleAuthenticator(config));
	}

	@Override
	public AuthenticationResponse createAuthenticationEntry(AuthenticationType type, String userId, Credential credential) throws AuthenticationException
	{
		return authenticatorMap.get(type).createAuthenticationEntry(userId, credential);
	}

	@Override
	public AuthenticationResponse authenticate(AuthenticationType type, Credential credential) throws AuthenticationException
	{
		return authenticatorMap.get(type).authenticate(credential);
	}

	@Override
	public boolean generateResetToken(String loginId) throws AuthenticationException
	{
		return authenticatorMap.get(AuthenticationType.Default).generateResetToken(loginId);
	}

	@Override
	public AuthenticationResponse validatePasswordStrength(String password) throws AuthenticationException
	{
		return authenticatorMap.get(AuthenticationType.Default).validatePasswordStrength(password);
	}

	/**
	 * This is the only method in the service that needs session validation.
	 */
	@Override
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException
	{
		logger.info("Request to change password for UserId:" + userId);
		//TODO if (sessionMgmtService.isValid(userId, sessionId)) -> need to figure out how to get sessionId

		return authenticatorMap.get(AuthenticationType.Default).changePassword(userId, oldPassword, newPassword);
	}
}
