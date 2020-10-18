package org.piangles.backbone.services.auth;

import java.util.UUID;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.dao.AuthenticationDAO;
import org.piangles.backbone.services.auth.dao.AuthenticationDAOImpl;
import org.piangles.backbone.services.logging.LoggingService;

public final class AuthenticationServiceImpl implements AuthenticationService
{
	private LoggingService logger = Locator.getInstance().getLoggingService();

	private AuthenticationDAO authenticationDAO = null;
	private PasswordManagment passwordManagment = null;
	
	public AuthenticationServiceImpl() throws Exception
	{
		authenticationDAO = new AuthenticationDAOImpl();
		passwordManagment = new PasswordManagment(authenticationDAO);
	}

	/**
	 * This is the only method in the service that needs session validation.
	 */
	@Override
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createAuthenticationEntry(Credential credential) throws AuthenticationException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AuthenticationResponse authenticate(Credential credential) throws AuthenticationException
	{
		//USE : 50593880-1854-4c23-8450-07185e3d5b08
		/**
		 * 1. encrypt the given password to check against the one in DB.
		 * 2. Also remember to Audit the tries.
		 * 3. Need another table for checking the number of failed attempts.
		 * 
		 * Id
		 * From 4e830a76-71ed-453a-b31f-b6ab90640c5b extract 4e830a76
		 */
		
		
		String id = UUID.randomUUID().toString();
		String userId = id.substring(0, id.indexOf('-'));
		
		userId = "7014b086";
		logger.info("Randomly generated userId in stubbed code : " + userId + " for loginId : " + credential.getLoginId());
		
		return new AuthenticationResponse(userId);
	}

	@Override
	public boolean generateResetToken(String loginId) throws AuthenticationException
	{
		return passwordManagment.generateResetToken(loginId);
	}

	@Override
	public AuthenticationResponse validatePasswordStrength(String password) throws AuthenticationException
	{
		return passwordManagment.validatePasswordStrength(password);
	}
}
