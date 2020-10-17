package org.piangles.backbone.services.auth;

/**
 * AuthManagerHandler (omnistac)
 *
 */
public interface PasswordManagment
{
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException;

	public AuthenticationResponse validatePasswordStrength(String password) throws AuthenticationException;
	
	public String generateResetToken() throws AuthenticationException;
}
