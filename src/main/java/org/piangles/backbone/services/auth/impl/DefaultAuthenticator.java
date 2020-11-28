package org.piangles.backbone.services.auth.impl;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationException;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Authenticator;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.auth.CredentialHelper;
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.backbone.services.auth.dao.AuthenticationDAO;
import org.piangles.backbone.services.config.Configuration;
import org.piangles.backbone.services.crypto.CryptoException;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.core.dao.DAOException;

public class DefaultAuthenticator implements Authenticator 
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private int maxNoOfAttempts = 5; //By default it is 5
	private AuthenticationDAO authenticationDAO = null;
	
	public DefaultAuthenticator(Configuration config, AuthenticationDAO authenticationDAO)
	{
		String valueAsStr = config.getValue("MaxNoOfAttemps");
		if (valueAsStr != null)
		{
			maxNoOfAttempts = Integer.valueOf(valueAsStr).intValue();
		}
		this.authenticationDAO = authenticationDAO;
	}
	
	@Override
	public AuthenticationResponse authenticate(Credential credential) throws AuthenticationException
	{
		AuthenticationResponse response = null;
		try
		{
			logger.info("Request to authenticate user.");
			//TODO Audit the attempt -> Trigger in DB
			response = authenticationDAO.authenticate(new CredentialHelper().createEncryptedCredential(credential), maxNoOfAttempts);
		}
		catch (CryptoException | DAOException e)
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
}
