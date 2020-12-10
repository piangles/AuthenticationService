package org.piangles.backbone.services.auth.impl.token;

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

	private static final String CREATE_ENTRY_SP = "Backbone.CreateTokenBasedCredentialEntry";
	private static final String IS_CREDENTIAL_VALID_SP = "Backbone.IsTokenBasedCredentialValid";
	
	private static final String IS_ACTIVE = "IsActive";

	public AuthenticationDAOImpl() throws Exception
	{
		super.init(ResourceManager.getInstance().getRDBMSDataStore(new DefaultConfigProvider("AuthenticationService", COMPONENT_ID)));
	}

	@Override
	public boolean createAuthenticationEntry(String userId, Credential credential) throws DAOException
	{
		super.executeSP(CREATE_ENTRY_SP, 2, (sp)->{
			sp.setString(1, userId);
			sp.setString(2, credential.getId());
		});
		return true;
	}

	@Override
	public AuthenticationResponse authenticate(Credential credential) throws DAOException
	{
		AuthenticationResponse response = null;
		response = super.executeSPQuery(IS_CREDENTIAL_VALID_SP, 3, (sp)->{
			sp.setString(1, credential.getId());
			sp.registerOutParameter(2, java.sql.Types.VARCHAR);
		}, (rs, call)->{
			AuthenticationResponse dbResponse = null;
			boolean isActive = call.getBoolean(IS_ACTIVE);
			if (isActive)
			{
				dbResponse = new AuthenticationResponse(credential.getId(), true);
			}
			else
			{
				FailureReason reason = null;
				if (!isActive)
				{
					reason = FailureReason.AccountDisabled;
				}
				else
				{
					reason = FailureReason.AuthenticationFailed;
				}
				dbResponse = new AuthenticationResponse(reason, 3);	
			}
			return dbResponse;
		});
		return response;
	}
}
