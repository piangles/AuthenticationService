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
 
 
 
package org.piangles.backbone.services.auth;

import java.util.HashMap;
import java.util.Map;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.impl.def.DefaultAuthenticator;
import org.piangles.backbone.services.auth.impl.sso.GoogleAuthenticator;
import org.piangles.backbone.services.auth.impl.token.TokenBasedAuthenticator;
import org.piangles.backbone.services.config.ConfigService;
import org.piangles.backbone.services.config.Configuration;

public final class AuthenticationServiceImpl implements AuthenticationService
{
	private static final String COMPONENT_ID = "2f07e92e-8edf-4fed-897c-2df2bd2ae72d";
	
	private ConfigService configService = Locator.getInstance().getConfigService();

	private Map<AuthenticationType, Authenticator> authenticatorMap;
	
	public AuthenticationServiceImpl() throws Exception
	{
		Configuration config = configService.getConfiguration(COMPONENT_ID);
		authenticatorMap = new HashMap<AuthenticationType, Authenticator>();
		authenticatorMap.put(AuthenticationType.Default, new DefaultAuthenticator(config));
		authenticatorMap.put(AuthenticationType.TokenBased, new TokenBasedAuthenticator(config));
		authenticatorMap.put(AuthenticationType.Google, new GoogleAuthenticator(config));
	}

	@Override
	public AuthenticationResponse createAuthenticationEntry(AuthenticationType type, String userId, Credential credential) throws AuthenticationException
	{
		return authenticatorMap.get(type).createAuthenticationEntry(userId, credential);
	}

	@Override
	public AuthenticationResponse authenticate(AuthenticationType type, Credential credential) throws AuthenticationException
	{
		return authenticatorMap.get(type).authenticate(credential);
	}

	@Override
	public boolean generateResetToken(String loginId) throws AuthenticationException
	{
		return authenticatorMap.get(AuthenticationType.Default).generateResetToken(loginId);
	}

	@Override
	public AuthenticationResponse validatePasswordStrength(String password) throws AuthenticationException
	{
		return authenticatorMap.get(AuthenticationType.Default).validatePasswordStrength(password);
	}

	/**
	 * This is the only method in the service that needs session validation.
	 */
	@Override
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException
	{
		return authenticatorMap.get(AuthenticationType.Default).changePassword(userId, oldPassword, newPassword);
	}
}
