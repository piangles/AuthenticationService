package org.piangles.backbone.services.auth.impl.sso;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationException;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Authenticator;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.config.Configuration;
import org.piangles.backbone.services.logging.LoggingService;

public class GoogleAuthenticator implements Authenticator 
{
	private static final String UNSUPPORTED = "This endpoint is not supported for this Authenticator.";

	private LoggingService logger = Locator.getInstance().getLoggingService();
	
	public GoogleAuthenticator(Configuration config)
	{
	}
	
	@Override
	public AuthenticationResponse createAuthenticationEntry(String userId, Credential credential) throws AuthenticationException
	{
		throw new AuthenticationException(UNSUPPORTED);
	}

	@Override
	public AuthenticationResponse authenticate(Credential credential) throws AuthenticationException
	{
		AuthenticationResponse response = null;
		return response;
	}


	@Override
	public boolean generateResetToken(String loginId) throws AuthenticationException
	{
		throw new AuthenticationException(UNSUPPORTED);
	}

	@Override
	public AuthenticationResponse validatePasswordStrength(String password) throws AuthenticationException
	{
		throw new AuthenticationException(UNSUPPORTED);
	}

	@Override
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException
	{
		throw new AuthenticationException(UNSUPPORTED);
	}
}
