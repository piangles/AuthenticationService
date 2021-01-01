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
