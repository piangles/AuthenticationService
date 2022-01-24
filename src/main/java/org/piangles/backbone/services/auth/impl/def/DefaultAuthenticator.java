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

import java.util.Calendar;
import java.util.Date;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationException;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.AuthenticationService;
import org.piangles.backbone.services.auth.Authenticator;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.backbone.services.config.ConfigService;
import org.piangles.backbone.services.config.Configuration;
import org.piangles.backbone.services.config.DefaultConfigProvider;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileException;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.core.dao.DAOException;
import org.piangles.core.util.abstractions.ConfigProvider;

public class DefaultAuthenticator implements Authenticator 
{
	private static final String COMPONENT_ID = "2f07e92e-8edf-4fed-897c-2df2bd2ae72d";
	private static final int USER_ID_NOT_FOUND_ATTEMPTS_REMAINING = 1;
	
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private ConfigService configService = Locator.getInstance().getConfigService();
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	
	private int maxNoOfAttempts = 5; //By default it is 5
	private AuthenticationDAO authenticationDAO = null;
	private PasswordManagement passwordManagment = null;
	
	public DefaultAuthenticator() throws Exception
	{
		ConfigProvider cp = new DefaultConfigProvider(AuthenticationService.NAME, COMPONENT_ID);
		Configuration config = configService.getConfiguration(COMPONENT_ID);
		
		authenticationDAO = new AuthenticationDAOImpl(cp);
		passwordManagment = new PasswordManagement();
		
		String valueAsStr = config.getValue("MaxNoOfAttemps");
		if (valueAsStr != null)
		{
			maxNoOfAttempts = Integer.valueOf(valueAsStr).intValue();
		}
	}
	
	@Override
	public boolean doesAuthenticationEntryExist(String userId) throws AuthenticationException
	{
		boolean exists = false;
		logger.info("Request to check for AuthenticationEntryExist for UserId:" + userId);
		//TODO if (sessionMgmtService.isValid(userId, sessionId)) -> need to figure out how to get sessionId
		try
		{
			exists = authenticationDAO.doesAuthenticationEntryExist(userId);
		}
		catch (DAOException e)
		{
			String message = "Unable to doesAuthenticationEntryExist for UserId: " + userId;
			logger.error(message + ". Reason: " + e.getMessage(), e);
			throw new AuthenticationException(message);
		}
		return exists;
	}

	@Override
	public AuthenticationResponse createAuthenticationEntry(Credential credential) throws AuthenticationException
	{
		AuthenticationResponse response = null; 
		
		String userId = credential.getId();
		logger.info("Request to create Authentication Entry for a user.");
		try
		{
			response = passwordManagment.validatePasswordStrength(credential.getPassword());
			if (response.isRequestSuccessful())
			{
				logger.info("Creating an authentication entry for UserId: " + userId);
				
				Credential finalCredential = new Credential(userId, credential.getPassword());
				response = authenticationDAO.createAuthenticationEntry(finalCredential);
			}
		}
		catch (DAOException e)
		{
			String message = "Unable to create authentication entry for UserId: " + userId;
			logger.error(message + ". Reason: " + e.getMessage(), e);
			throw new AuthenticationException(message);
		}
		
		return response;
	}

	
	@Override
	public AuthenticationResponse authenticate(Credential credential) throws AuthenticationException
	{
		AuthenticationResponse response = null;
		String userId = null;
		logger.info("Request to authenticate user.");
		
		try
		{
			userId = profileService.searchProfile(new BasicUserProfile(null, null, credential.getId(), null));
			if (userId == null)
			{
				logger.warn("Cannot find UserId for Id: " + credential.getId());
				response = new AuthenticationResponse(FailureReason.AccountDoesNotExist, USER_ID_NOT_FOUND_ATTEMPTS_REMAINING);
			}
			else
			{
				Credential finalCredential = new Credential(userId, credential.getPassword());
				response = authenticationDAO.authenticate(finalCredential, maxNoOfAttempts);
			}			
		}
		catch (UserProfileException | DAOException e)
		{
			logger.error("Exception authenticating Reason:" + e.getMessage(), e);
			/**
			 * Consume & log the errors and return just the response for security reasons.
			 */
			logger.error("Unable to authenticate user because of: " + e.getMessage(), e);
			response = new AuthenticationResponse(FailureReason.InternalError, 0);
		}
		return response;
	}
	
	@Override
	public AuthenticationResponse generateResetToken(String loginId) throws AuthenticationException
	{
		AuthenticationResponse response = null;
		String userId = null;
		logger.info("Request to generateResetToken for:" + loginId);
		//TODO if (sessionMgmtService.isValid(userId, sessionId)) -> need to figure out how to get sessionId
		try
		{
			userId = profileService.searchProfile(new BasicUserProfile(null, null, loginId, null));
			if (userId == null)
			{
				logger.warn("Cannot find UserId for Id: " + loginId);
				response = new AuthenticationResponse(FailureReason.AccountDoesNotExist, USER_ID_NOT_FOUND_ATTEMPTS_REMAINING);
			}
			else
			{
				String token = passwordManagment.generateResetToken(loginId);
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.DATE, 1);
				
				response = authenticationDAO.persistGeneratedToken(userId, token, new java.sql.Date(c.getTime().toInstant().toEpochMilli()));
			}			
		}
		catch (UserProfileException | DAOException e)
		{
			String message = "Unable to generateResetToken for LoginId: " + loginId;
			logger.error(message + ". Reason: " + e.getMessage(), e);
			throw new AuthenticationException(message);
		}
		
		return response;
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
		AuthenticationResponse response = null;
		logger.info("Request to change password for UserId:" + userId);
		//TODO if (sessionMgmtService.isValid(userId, sessionId)) -> need to figure out how to get sessionId
		response = validatePasswordStrength(newPassword);
		if (response.isRequestSuccessful())
		{
			try
			{
				response = authenticationDAO.changePassword(userId, oldPassword, newPassword);
			}
			catch (DAOException e)
			{
				String message = "Unable to changePassword for UserId: " + userId;
				logger.error(message + ". Reason: " + e.getMessage(), e);
				throw new AuthenticationException(message);
			}
		}
		return response;
	}
}
