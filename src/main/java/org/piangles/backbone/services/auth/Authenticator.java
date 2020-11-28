package org.piangles.backbone.services.auth;

public interface Authenticator
{
	public AuthenticationResponse authenticate(Credential credential) throws AuthenticationException;
}
