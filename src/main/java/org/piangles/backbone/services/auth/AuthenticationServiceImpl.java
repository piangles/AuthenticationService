package org.piangles.backbone.services.auth;

import org.javatuples.Pair;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.dao.AuthenticationDAO;
import org.piangles.backbone.services.auth.dao.AuthenticationDAOImpl;
import org.piangles.backbone.services.config.ConfigService;
import org.piangles.backbone.services.config.Configuration;
import org.piangles.backbone.services.crypto.CryptoException;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.core.dao.DAOException;

public final class AuthenticationServiceImpl implements AuthenticationService
{
	private static final String COMPONENT_ID = "2f07e92e-8edf-4fed-897c-2df2bd2ae72d";
	
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private ConfigService configService = Locator.getInstance().getConfigService();
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();

	private int maxNoOfAttempts = 5; //By default it is 5
	private AuthenticationDAO authenticationDAO = null;
	private PasswordManagment passwordManagment = null;
	
	public AuthenticationServiceImpl() throws Exception
	{
		authenticationDAO = new AuthenticationDAOImpl();
		passwordManagment = new PasswordManagment(authenticationDAO);

		Configuration config = configService.getConfiguration(COMPONENT_ID);
		String valueAsStr = config.getValue("MaxNoOfAttemps");
		if (valueAsStr != null)
		{
			maxNoOfAttempts = Integer.valueOf(valueAsStr).intValue();
		}
	}

	@Override
	public AuthenticationResponse createAuthenticationEntry(String userId, Credential credential) throws AuthenticationException
	{
		AuthenticationResponse response = null; 
		logger.info("Creating an authentication entry for UserId: " + userId);
		try
		{
			response = passwordManagment.validatePasswordStrength(credential.getPassword());
			if (response.isRequestSuccessful())
			{
				boolean result = authenticationDAO.createAuthenticationEntry(userId, createEncryptedCredential(credential));
				response = new AuthenticationResponse(userId, result);
			}
		}
		catch (CryptoException | DAOException e)
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
			response = authenticationDAO.authenticate(createEncryptedCredential(credential), maxNoOfAttempts);
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

	/**
	 * This is the only method in the service that needs session validation.
	 */
	@Override
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException
	{
		logger.info("Request to change password for UserId:" + userId);
		//TODO if (sessionMgmtService.isValid(userId, sessionId)) -> nee to figure out how to get sessionId

		return passwordManagment.changePassword(userId, oldPassword, newPassword);
	}
	
	/**
	 * All credentials are encrypted and saved in the database so need to enrypt
	 * when we query as well.
	 * @param credential
	 * @return
	 * @throws CryptoException
	 */
	private Credential createEncryptedCredential(Credential credential) throws CryptoException
	{
		Pair<String, String> tuple = new CryptoCaller().ecrypt(credential.getLoginId(), credential.getPassword());

		return new Credential(tuple.getValue0(), tuple.getValue1());
	}
}
