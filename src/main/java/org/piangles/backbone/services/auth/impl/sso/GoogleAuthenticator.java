/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.backbone.services.auth.impl.sso;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationException;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Authenticator;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.logging.LoggingService;

public class GoogleAuthenticator implements Authenticator 
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	
	public GoogleAuthenticator()
	{
	}
	
	@Override
	public AuthenticationResponse createAuthenticationEntry(Credential credential) throws AuthenticationException
	{
		throw new AuthenticationException(UNSUPPORTED);
	}

	@Override
	public AuthenticationResponse authenticate(Credential credential) throws AuthenticationException
	{
		AuthenticationResponse response = null;
		return response;
	}


	@Override
	public AuthenticationResponse generateResetToken(String loginId) throws AuthenticationException
	{
		throw new AuthenticationException(UNSUPPORTED);
	}

	@Override
	public AuthenticationResponse validatePasswordStrength(String password) throws AuthenticationException
	{
		throw new AuthenticationException(UNSUPPORTED);
	}

	@Override
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException
	{
		throw new AuthenticationException(UNSUPPORTED);
	}
}
