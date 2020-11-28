package org.piangles.backbone.services.auth.impl;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationException;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Authenticator;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.auth.dao.AuthenticationDAO;
import org.piangles.backbone.services.config.Configuration;
import org.piangles.backbone.services.logging.LoggingService;

public class GoogleAuthenticator implements Authenticator 
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private AuthenticationDAO authenticationDAO = null;
	
	public GoogleAuthenticator(Configuration config, AuthenticationDAO authenticationDAO)
	{
		this.authenticationDAO = authenticationDAO;
	}
	
	@Override
	public AuthenticationResponse authenticate(Credential credential) throws AuthenticationException
	{
		AuthenticationResponse response = null;
		return response;
	}
}
