package org.piangles.backbone.services.auth.impl.token;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationException;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Authenticator;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.config.Configuration;
import org.piangles.backbone.services.logging.LoggingService;

public class TokenBasedAuthenticator implements Authenticator 
{
	private static final String UNIMPLEMENTED = "This endpoint is not implemented for this Authenticator.";
	
	private LoggingService logger = Locator.getInstance().getLoggingService();
	
	public TokenBasedAuthenticator(Configuration config)
	{
	}
	
	@Override
	public AuthenticationResponse createAuthenticationEntry(String userId, Credential credential) throws AuthenticationException
	{
		throw new AuthenticationException();
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
		throw new AuthenticationException(UNIMPLEMENTED);
	}

	@Override
	public AuthenticationResponse validatePasswordStrength(String password) throws AuthenticationException
	{
		throw new AuthenticationException(UNIMPLEMENTED);
	}

	@Override
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException
	{
		throw new AuthenticationException(UNIMPLEMENTED);
	}
}
