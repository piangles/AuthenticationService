package org.piangles.backbone.services.auth.dao;

import java.sql.Date;

import org.piangles.backbone.services.auth.AuthenticationException;
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
			sp.setString(2, credential.getLoginId());
			sp.setString(3, credential.getPassword());
		});
		return true;
	}

	@Override
	public AuthenticationResponse authenticate(Credential credential, int maxNumOfAttempts) throws DAOException
	{
		AuthenticationResponse response = null;
		response = super.executeSPQuery(IS_CREDENTIAL_VALID_SP, 3, (sp)->{
			sp.setString(1, credential.getLoginId());
			sp.setString(2, credential.getPassword());
			sp.setInt(3, maxNumOfAttempts);
		}, (rs)->{
			AuthenticationResponse dbResponse = null;
			String userId = rs.getString(USER_ID);
			int numOfAttempts = rs.getInt(NUM_ATTEMPTS);
			boolean isToken = rs.getBoolean(IS_TOKEN);
			boolean isActive = rs.getBoolean(IS_ACTIVE);
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
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthenticationResponse persistGeneratedToken(String loginId, String token, Date tokenExpirationTime) throws AuthenticationException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
