package org.piangles.backbone.services.auth;

public interface Authenticator
{
	public AuthenticationResponse createAuthenticationEntry(String userId, Credential credential) throws AuthenticationException;
	public AuthenticationResponse authenticate(Credential credential) throws AuthenticationException;
	public boolean generateResetToken(String loginId) throws AuthenticationException;
	public AuthenticationResponse validatePasswordStrength(String password) throws AuthenticationException;
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException;
}
