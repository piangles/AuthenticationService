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

import org.piangles.backbone.services.Locator;
import org.piangles.core.services.remoting.SessionAwareable;
import org.piangles.core.services.remoting.SessionDetails;

public class AuthServiceTest extends Thread implements SessionAwareable
{
	public static void main(String[] args) 
	{
		AuthServiceTest test = new AuthServiceTest();
		test.start();
		try
		{
			test.join();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public void run()
	{
		try
		{
			AuthenticationService authService = Locator.getInstance().getAuthenticationService();

			String loginId = "testuser@piangles.org";
			String password = "Password@1";
			String token = "\"6'Z'm4p";
			Credential credential = new Credential(loginId, password);
			
			AuthenticationResponse response = null;
			
			response = authService.createAuthenticationEntry(AuthenticationType.Default, "08f9c02d", credential);
			System.out.println("Result of createAuthenticationEntry:" + response);

			response = authService.authenticate(AuthenticationType.Default, credential);
			System.out.println(response);
			
//			authService.generateResetToken(loginId);

//			AuthenticationResponse response = authService.validatePasswordStrength(token);
//			System.out.println(response);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
	@Override
	public SessionDetails getSessionDetails()
	{
		return new SessionDetails("LoggingService", "TODOSessionId");
	}
}
