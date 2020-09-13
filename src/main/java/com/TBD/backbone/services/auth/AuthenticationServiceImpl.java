package com.TBD.backbone.services.auth;

import java.util.UUID;

import com.TBD.backbone.services.Tier2ServiceLocator;
import com.TBD.backbone.services.logging.LoggingService;

public final class AuthenticationServiceImpl implements AuthenticationService
{
	private LoggingService logger = Tier2ServiceLocator.getInstance().getLoggingService();

	public AuthenticationServiceImpl()
	{
		
	}

	@Override
	public AuthenticationResponse authenticate(String loginId, String password) throws AuthenticationException
	{
		/**
		 * 1. encrypt the given password to check against the one in DB.
		 * 2. Also remember to Audit the tries.
		 * 3. Need another table for checking the number of failed attempts.
		 * 
		 */
		
		
		String id = UUID.randomUUID().toString();
		String userId = id.substring(0, id.indexOf('-'));
		
		userId = "7014b086";
		logger.info("Randomly generated userId in stubbed code : " + userId + " for loginId : " + loginId);
		
		return new AuthenticationResponse(userId);
	}
}
