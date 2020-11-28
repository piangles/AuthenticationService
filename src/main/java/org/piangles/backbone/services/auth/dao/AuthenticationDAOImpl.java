package org.piangles.backbone.services.auth.dao;

import java.sql.Date;

import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.backbone.services.config.DefaultConfigProvider;
import org.piangles.core.dao.DAOException;
import org.piangles.core.dao.rdbms.AbstractDAO;
import org.piangles.core.resources.ResourceManager;

public class AuthenticationDAOImpl extends AbstractDAO implements AuthenticationDAO
{
	private static final String COMPONENT_ID = "2f07e92e-8edf-4fed-897c-2df2bd2ae72d";

	private static final String CREATE_ENTRY_SP = "Backbone.CreateCredentialEntry";
	private static final String IS_CREDENTIAL_VALID_SP = "Backbone.IsCredentialValid";
	private static final String SET_CREDENTIAL_SP = "Backbone.SetCredential";
	
	private static final String USER_ID = "UserId";
	private static final String NUM_ATTEMPTS = "NoOfAttempts";
	private static final String IS_TOKEN = "IsToken";
	private static final String IS_ACTIVE = "IsActive";

	public AuthenticationDAOImpl() throws Exception
	{
		super.init(ResourceManager.getInstance().getRDBMSDataStore(new DefaultConfigProvider("AuthenticationService", COMPONENT_ID)));
	}

	@Override
	public boolean createAuthenticationEntry(String userId, Credential credential) throws DAOException
	{
		super.executeSP(CREATE_ENTRY_SP, 3, (sp)->{
			sp.setString(1, userId);
			sp.setString(2, credential.getId());
			sp.setString(3, credential.getPassword());
		});
		return true;
	}

	@Override
	public AuthenticationResponse authenticate(Credential credential, int maxNumOfAttempts) throws DAOException
	{
		AuthenticationResponse response = null;
		response = super.executeSPQuery(IS_CREDENTIAL_VALID_SP, 7, (sp)->{
			sp.setString(1, credential.getId());
			sp.setString(2, credential.getPassword());
			sp.setInt(3, maxNumOfAttempts);
			
			sp.registerOutParameter(4, java.sql.Types.VARCHAR);
			sp.registerOutParameter(5, java.sql.Types.INTEGER);
			sp.registerOutParameter(6, java.sql.Types.BOOLEAN);
			sp.registerOutParameter(7, java.sql.Types.BOOLEAN);
		}, (rs, call)->{
			AuthenticationResponse dbResponse = null;
			String userId = call.getString(USER_ID);
			int numOfAttempts = call.getInt(NUM_ATTEMPTS);
			boolean isToken = call.getBoolean(IS_TOKEN);
			boolean isActive = call.getBoolean(IS_ACTIVE);
			if (userId != null)
			{
				dbResponse = new AuthenticationResponse(userId, isToken);
			}
			else
			{
				FailureReason reason = null;
				int noOfAttemptsRemaining = maxNumOfAttempts - numOfAttempts + 1;
				if (noOfAttemptsRemaining == 0)
				{
					reason = FailureReason.TooManyAttempts;
				}
				else if (!isActive)
				{
					reason = FailureReason.AccountDisabled;
					noOfAttemptsRemaining = 0;
				}
				else
				{
					reason = FailureReason.AuthenticationFailed;
				}
				dbResponse = new AuthenticationResponse(reason, noOfAttemptsRemaining);	
			}
			return dbResponse;
		});
		return response;
	}

	@Override
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws DAOException
	{
		super.executeSP(SET_CREDENTIAL_SP, 6, (sp) -> {
			sp.setString(1, userId);
			sp.setString(2, null);
			sp.setString(3, oldPassword);
			sp.setString(4, newPassword);
			sp.setString(5, null);
			sp.setDate(6, null);
		});
		return new AuthenticationResponse(userId, true);
	}

	@Override
	public void persistGeneratedToken(String loginId, String token, Date tokenExpirationTime) throws DAOException
	{
		super.executeSP(SET_CREDENTIAL_SP, 6, (sp) -> {
			sp.setString(1, null);
			sp.setString(2, loginId);
			sp.setString(3, null);
			sp.setString(4, null);
			sp.setString(5, token);
			sp.setDate(6, tokenExpirationTime);
		});
	}
}
