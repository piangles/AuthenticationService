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
 
 
 
package org.piangles.backbone.services.auth.impl.def;

import org.javatuples.Pair;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationException;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Authenticator;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.backbone.services.config.Configuration;
import org.piangles.backbone.services.crypto.CryptoException;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.core.dao.DAOException;

public class DefaultAuthenticator implements Authenticator 
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	
	private int maxNoOfAttempts = 5; //By default it is 5
	private AuthenticationDAO authenticationDAO = null;
	private PasswordManagement passwordManagment = null;
	
	public DefaultAuthenticator(Configuration config) throws Exception
	{
		authenticationDAO = new AuthenticationDAOImpl();
		passwordManagment = new PasswordManagement(authenticationDAO);
		
		String valueAsStr = config.getValue("MaxNoOfAttemps");
		if (valueAsStr != null)
		{
			maxNoOfAttempts = Integer.valueOf(valueAsStr).intValue();
		}
	}
	
	@Override
	public AuthenticationResponse createAuthenticationEntry(String userId, Credential credential) throws AuthenticationException
	{
		AuthenticationResponse response = null; 
		logger.info("Creating an authentication entry for UserId: " + userId);
		try
		{
			response = passwordManagment.validatePasswordStrength(credential.getPassword());
			if (response.isRequestSuccessful())
			{
				boolean result = authenticationDAO.createAuthenticationEntry(userId, createEncryptedCredential(credential));
				response = new AuthenticationResponse(userId, result);
			}
		}
		catch (CryptoException | DAOException e)
		{
			String message = "Unable to create authentication entry for User";
			logger.error(message + "Id: " + userId, e);
			throw new AuthenticationException(message, e);
		}
		return response;
	}

	
	@Override
	public AuthenticationResponse authenticate(Credential credential) throws AuthenticationException
	{
		AuthenticationResponse response = null;
		try
		{
			logger.info("Request to authenticate user.");
			//TODO Audit the attempt -> Trigger in DB
			response = authenticationDAO.authenticate(createEncryptedCredential(credential), maxNoOfAttempts);
		}
		catch (CryptoException | DAOException e)
		{
			logger.error("Exception authenticating:" + e.getMessage(), e);
			/**
			 * Consume & log the errors and return just the response for security reasons.
			 */
			logger.error("Unable to authenticate user because of: " + e.getMessage(), e);
			response = new AuthenticationResponse(FailureReason.InternalError, null);
		}
		return response;
	}
	
	@Override
	public boolean generateResetToken(String loginId) throws AuthenticationException
	{
		return passwordManagment.generateResetToken(loginId);
	}

	@Override
	public AuthenticationResponse validatePasswordStrength(String password) throws AuthenticationException
	{
		return passwordManagment.validatePasswordStrength(password);
	}

	/**
	 * This is the only method in the service that needs session validation.
	 */
	@Override
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException
	{
		logger.info("Request to change password for UserId:" + userId);
		//TODO if (sessionMgmtService.isValid(userId, sessionId)) -> need to figure out how to get sessionId

		return passwordManagment.changePassword(userId, oldPassword, newPassword);
	}
	
	/**
	 * All credentials are encrypted and saved in the database so need to enrypt
	 * when we query as well.
	 * @param credential
	 * @return
	 * @throws CryptoException
	 */
	private Credential createEncryptedCredential(Credential credential) throws CryptoException
	{
		Pair<String, String> tuple = new CryptoCaller().ecrypt(credential.getId(), credential.getPassword());

		return new Credential(tuple.getValue0(), tuple.getValue1());
	}
}
