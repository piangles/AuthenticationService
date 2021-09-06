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
 
 
 
package org.piangles.backbone.services.auth.impl.token;

import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.core.dao.DAOException;
import org.piangles.core.dao.rdbms.AbstractDAO;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.util.abstractions.ConfigProvider;

public class AuthenticationDAOImpl extends AbstractDAO implements AuthenticationDAO
{
	private static final String CREATE_ENTRY_SP = "Backbone.CreateTokenBasedCredentialEntry";
	private static final String IS_CREDENTIAL_VALID_SP = "Backbone.IsTokenBasedCredentialValid";
	
	private static final String USER_ID = "UserId";
	private static final String IS_ACTIVE = "IsActive";

	public AuthenticationDAOImpl(ConfigProvider cp) throws Exception
	{
		super.init(ResourceManager.getInstance().getRDBMSDataStore(cp));
	}

	@Override
	public boolean createAuthenticationEntry(String userId, Credential credential) throws DAOException
	{
		super.executeSP(CREATE_ENTRY_SP, 2, (sp)->{
			sp.setString(1, userId);
			sp.setString(2, credential.getId());
		});
		return true;
	}

	@Override
	public AuthenticationResponse authenticate(Credential credential) throws DAOException
	{
		AuthenticationResponse response = null;
		response = super.executeSPQuery(IS_CREDENTIAL_VALID_SP, 3, (sp)->{
			sp.setString(1, credential.getId());
			sp.registerOutParameter(2, java.sql.Types.VARCHAR);
			sp.registerOutParameter(3, java.sql.Types.BOOLEAN);
		}, (rs, call)->{
			AuthenticationResponse dbResponse = null;
			String userId = call.getString(USER_ID);
			boolean isActive = call.getBoolean(IS_ACTIVE);
			if (isActive)
			{
				dbResponse = new AuthenticationResponse(userId, true);
			}
			else
			{
				FailureReason reason = null;
				if (!isActive)
				{
					reason = FailureReason.AccountDisabled;
				}
				else
				{
					reason = FailureReason.AuthenticationFailed;
				}
				dbResponse = new AuthenticationResponse(reason, 1);	
			}
			return dbResponse;
		});
		return response;
	}
}
