package org.piangles.backbone.services.auth.impl.token;

import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.core.dao.DAOException;

public interface AuthenticationDAO
{
	public boolean createAuthenticationEntry(String userId, Credential credential) throws DAOException;
	public AuthenticationResponse authenticate(Credential credential) throws DAOException;
}
