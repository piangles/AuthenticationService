package org.piangles.backbone.services.auth;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.dao.AuthenticationDAO;
import org.piangles.backbone.services.auth.dao.AuthenticationDAOImpl;
import org.piangles.backbone.services.config.ConfigService;
import org.piangles.backbone.services.crypto.CryptoException;
import org.piangles.backbone.services.crypto.CryptoService;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.core.dao.DAOException;

public final class AuthenticationServiceImpl implements AuthenticationService
{
	private static String cipherAuthorizationId = "";
	
	private static final String COMPONENT_ID = "2f07e92e-8edf-4fed-897c-2df2bd2ae72d";
	
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private ConfigService configService = Locator.getInstance().getConfigService();
	private CryptoService crypto = Locator.getInstance().getCryptoService();
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();

	private int maxNoOfAttempts = 5; //By default it is 5
	private AuthenticationDAO authenticationDAO = null;
	private PasswordManagment passwordManagment = null;
	
	public AuthenticationServiceImpl() throws Exception
	{
		authenticationDAO = new AuthenticationDAOImpl();
		passwordManagment = new PasswordManagment(authenticationDAO);
		//TODO get maxNoOfAttempts from Configuration 
	}

	@Override
	public boolean createAuthenticationEntry(String userId, Credential credential) throws AuthenticationException
	{
		boolean result = false; 
		logger.info("Creating an authentication entry for UserId: " + userId);
		try
		{
			result = authenticationDAO.createAuthenticationEntry(userId, createEncryptedCredential(credential));
		}
		catch (CryptoException | DAOException e)
		{
			String message = "Unable to create authentication entry for User";
			logger.error(message + "Id: " + userId, e);
			throw new AuthenticationException(message, e);
		}
		return result;
	}

	@Override
	public AuthenticationResponse authenticate(Credential credential) throws AuthenticationException
	{
		AuthenticationResponse response = null;
		try
		{
			/**
			 * 1. Encrypt the given password to check against the one in DB.
			 * 2. TODO Audit the attempt.
			 */
			response = authenticationDAO.authenticate(createEncryptedCredential(credential), maxNoOfAttempts);
		}
		catch (CryptoException | DAOException e)
		{
			/**
			 * For authentication service, consume & log the errors and return just the response.
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
		//Check if the oldPassword matches the user
		// TODO Auto-generated method stub
		return null;
	}
	
	private Credential createEncryptedCredential(Credential credential) throws CryptoException
	{
		String encryptedLogin = crypto.encrypt(credential.getLoginId());
		String encryptedPassword = crypto.encrypt(credential.getPassword());
		
		return new Credential(encryptedLogin, encryptedPassword);
	}
}
