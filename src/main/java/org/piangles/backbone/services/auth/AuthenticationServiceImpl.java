package org.piangles.backbone.services.auth;

import java.util.HashMap;
import java.util.Map;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.dao.AuthenticationDAO;
import org.piangles.backbone.services.auth.dao.AuthenticationDAOImpl;
import org.piangles.backbone.services.auth.impl.DefaultAuthenticator;
import org.piangles.backbone.services.auth.impl.GoogleAuthenticator;
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

	private AuthenticationDAO authenticationDAO = null;
	private PasswordManagement passwordManagment = null;
	private Map<AuthenticationType, Authenticator> authenticatorMap;
	
	public AuthenticationServiceImpl() throws Exception
	{
		authenticationDAO = new AuthenticationDAOImpl();
		passwordManagment = new PasswordManagement(authenticationDAO);

		Configuration config = configService.getConfiguration(COMPONENT_ID);
		authenticatorMap = new HashMap<AuthenticationType, Authenticator>();
		authenticatorMap.put(AuthenticationType.Default, new DefaultAuthenticator(config, authenticationDAO));
		authenticatorMap.put(AuthenticationType.Google, new GoogleAuthenticator(config, authenticationDAO));
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
				boolean result = authenticationDAO.createAuthenticationEntry(userId, new CredentialHelper().createEncryptedCredential(credential));
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
	public AuthenticationResponse authenticate(AuthenticationType type, Credential credential) throws AuthenticationException
	{
		return authenticatorMap.get(type).authenticate(credential);
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
		//TODO if (sessionMgmtService.isValid(userId, sessionId)) -> need to figure out how to get sessionId

		return passwordManagment.changePassword(userId, oldPassword, newPassword);
	}
}
