package org.piangles.backbone.services.auth;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.dao.AuthenticationDAO;
import org.piangles.backbone.services.logging.LoggingService;

public class PasswordManagment
{
	private LoggingService logger = Locator.getInstance().getLoggingService();

	private AuthenticationDAO authenticationDAO = null;
	
	public PasswordManagment(AuthenticationDAO authenticationDAO)
	{
		this.authenticationDAO = authenticationDAO;
	}
	
	public AuthenticationResponse validatePasswordStrength(String password) throws AuthenticationException
	{
		return null;
	}
	
	public boolean generateResetToken(String loginId) throws AuthenticationException
	{
		//Generate a token
		//Persist in the DAO
		return false;
	}
}
