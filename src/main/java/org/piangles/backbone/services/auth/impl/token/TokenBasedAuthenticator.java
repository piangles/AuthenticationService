package org.piangles.backbone.services.auth.impl.token;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationException;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Authenticator;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.backbone.services.config.Configuration;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.core.dao.DAOException;

public class TokenBasedAuthenticator implements Authenticator 
{
	private static final String UNSUPPORTED = "This endpoint is not supported for this Authenticator.";
	
	private LoggingService logger = Locator.getInstance().getLoggingService();
	
	private AuthenticationDAO authenticationDAO = null;
	
	public TokenBasedAuthenticator(Configuration config)
	{
	}
	
	@Override
	public AuthenticationResponse createAuthenticationEntry(String userId, Credential credential) throws AuthenticationException
	{
		AuthenticationResponse response = null; 
		logger.info("Creating an authentication entry for UserId: " + userId);
		try
		{
			boolean result = authenticationDAO.createAuthenticationEntry(userId, credential);
			response = new AuthenticationResponse(userId, result);
		}
		catch (DAOException e)
		{
			String message = "Unable to create authentication entry for User";
			logger.error(message + "Id: " + userId, e);
			throw new AuthenticationException(message, e);
		}
		return response;
	}

	@Override
	public AuthenticationResponse authenticate(Credential credential) throws AuthenticationException
	{
		AuthenticationResponse response = null;
		try
		{
			logger.info("Request to authenticate user.");
			//TODO Audit the attempt -> Trigger in DB
			response = authenticationDAO.authenticate(credential);
		}
		catch (DAOException e)
		{
			logger.error("Exception authenticating:" + e.getMessage(), e);
			/**
			 * Consume & log the errors and return just the response for security reasons.
			 */
			logger.error("Unable to authenticate user because of: " + e.getMessage(), e);
			response = new AuthenticationResponse(FailureReason.InternalError, null);
		}
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
