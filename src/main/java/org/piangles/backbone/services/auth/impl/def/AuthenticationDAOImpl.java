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

import java.sql.Date;
import java.sql.Types;

import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.core.dao.DAOException;
import org.piangles.core.dao.rdbms.AbstractDAO;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.util.abstractions.ConfigProvider;

public class AuthenticationDAOImpl extends AbstractDAO implements AuthenticationDAO
{
	private static final String CREATE_ENTRY_SP = "auth.create_credential_entry";
	private static final String IS_CREDENTIAL_VALID_SP = "auth.is_credential_valid";
	private static final String SET_CREDENTIAL_SP = "auth.set_credential";
	
	private static final int NUM_ATTEMPTS_INDEX = 4;
	private static final int IS_TOKEN_INDEX = 5;
	private static final int IS_ACTIVE_INDEX = 6;
	private static final int LAST_LOGGED_IN_TS_INDEX = 7;

	public AuthenticationDAOImpl(ConfigProvider cp) throws Exception
	{
		super.init(ResourceManager.getInstance().getRDBMSDataStore(cp));
	}

	@Override
	public AuthenticationResponse createAuthenticationEntry(Credential credential) throws DAOException
	{
		super.executeSP(CREATE_ENTRY_SP, 2, (sp)->{
			sp.setString(1, credential.getId());
			sp.setString(2, credential.getPassword());
		});
		return new AuthenticationResponse(true);
	}

	@Override
	public AuthenticationResponse authenticate(Credential credential, int maxNumOfAttempts) throws DAOException
	{
		AuthenticationResponse response = null;
		response = super.executeSPQuery(IS_CREDENTIAL_VALID_SP, 7, (sp)->{
			sp.setString(1, credential.getId());
			sp.setString(2, credential.getPassword());
			sp.setInt(3, maxNumOfAttempts);
			
			sp.registerOutParameter(4, Types.INTEGER);
			sp.registerOutParameter(5, Types.BOOLEAN);
			sp.registerOutParameter(6, Types.BOOLEAN);
			sp.registerOutParameter(7, Types.TIMESTAMP);
		}, (rs, call)->{
			AuthenticationResponse dbResponse = null;
			int numOfAttempts = call.getInt(NUM_ATTEMPTS_INDEX);
			boolean isToken = call.getBoolean(IS_TOKEN_INDEX);
			boolean isActive = call.getBoolean(IS_ACTIVE_INDEX);
			long lastLoggedInTimestamp = call.getTimestamp(LAST_LOGGED_IN_TS_INDEX).getTime();
			
			if (numOfAttempts == 1)//=>It was successful attempt
			{
				dbResponse = new AuthenticationResponse(credential.getId(), isToken, lastLoggedInTimestamp);
			}
			else
			{
				int noOfAttemptsRemaining = maxNumOfAttempts - numOfAttempts + 1;
				FailureReason reason = null;
				if (!isActive)
				{
					reason = FailureReason.AccountDisabled;
					noOfAttemptsRemaining = 0;
				}
				else if (noOfAttemptsRemaining == 0)
				{
					reason = FailureReason.TooManyAttempts;
				}
				else
				{
					reason = FailureReason.AuthenticationFailed;
				}
				dbResponse = new AuthenticationResponse(reason, noOfAttemptsRemaining);	
			}
			return dbResponse;
		});
		return response;
	}

	@Override
	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws DAOException
	{
		AuthenticationResponse response = null;
		response = super.executeSPQuery(SET_CREDENTIAL_SP, 5, (sp) -> {
			sp.setString(1, userId);
			sp.setString(2, oldPassword);
			sp.setString(3, newPassword);
			sp.setString(4, null);
			sp.setDate(5, null);
		}, (rs, call)->{
			return new AuthenticationResponse(rs.getBoolean(1));
		});
		return response;
	}

	@Override
	public AuthenticationResponse persistGeneratedToken(String userId, String token, Date tokenExpirationTime) throws DAOException
	{
		AuthenticationResponse response = null;
		response = super.executeSPQuery(SET_CREDENTIAL_SP, 5, (sp) -> {
			sp.setString(1, userId);
			sp.setString(2, null);
			sp.setString(3, null);
			sp.setString(4, token);
			sp.setDate(5, tokenExpirationTime);
		}, (rs, call)->{
			return new AuthenticationResponse(token);
		});
		return response;
	}
}
