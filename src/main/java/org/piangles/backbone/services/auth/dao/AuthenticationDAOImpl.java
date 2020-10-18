package org.piangles.backbone.services.auth.dao;

import java.sql.Date;

import org.piangles.backbone.services.auth.AuthenticationException;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.config.DefaultConfigProvider;
import org.piangles.core.dao.DAOException;
import org.piangles.core.dao.rdbms.AbstractDAO;
import org.piangles.core.resources.ResourceManager;

public class AuthenticationDAOImpl extends AbstractDAO implements AuthenticationDAO
{
	private static final String COMPONENT_ID = "2f07e92e-8edf-4fed-897c-2df2bd2ae72d";

	public AuthenticationDAOImpl() throws Exception
	{
		super.init(ResourceManager.getInstance().getRDBMSDataStore(new DefaultConfigProvider("AuthenticationService", COMPONENT_ID)));
	}

	@Override
	public boolean createAuthenticationEntry(Credential credential) throws DAOException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AuthenticationResponse authenticate(Credential credential) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
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
