package org.piangles.backbone.services.auth.dao;

import java.sql.Date;

import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.core.dao.DAOException;

public interface AuthenticationDAO
{
	public boolean createAuthenticationEntry(String userId, Credential credential) throws DAOException;
	public AuthenticationResponse authenticate(Credential credential, int maxNumberOfAttempts) throws DAOException;
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws DAOException;
	public void persistGeneratedToken(String loginId, String token, Date tokenExpirationTime) throws DAOException;
}
