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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.javatuples.Pair;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordGenerator;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationException;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.backbone.services.crypto.CryptoException;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.core.dao.DAOException;

public class PasswordManagement
{
	private static final String ALLOWED_SPL_CHARACTERS = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
	private LoggingService logger = Locator.getInstance().getLoggingService();

	private AuthenticationDAO authenticationDAO = null;
	private List<Rule> passwordRules = null;

	public PasswordManagement(AuthenticationDAO authenticationDAO)
	{
		this.authenticationDAO = authenticationDAO;

		passwordRules = new ArrayList<>();
		passwordRules.add(compileLengthRule());
		passwordRules.addAll(compilePositiveMatchingRules());
		passwordRules.addAll(compileNegativeMatchingRules());
	}

	public AuthenticationResponse validatePasswordStrength(String password) throws AuthenticationException
	{
		AuthenticationResponse response = null;
		PasswordData passwordData = new PasswordData(password);
		PasswordValidator validator = new PasswordValidator(passwordRules);

		RuleResult result = validator.validate(passwordData);
		if (result.isValid())
		{
			logger.info("Specified password meets password strength rules.");
			response = new AuthenticationResponse(true);
		}
		else
		{
			List<String> failureReasons = validator.getMessages(result);
			logger.info("Specified password does not meet password strength rules: " + failureReasons);
			response = new AuthenticationResponse(FailureReason.PasswordDoesNotMeetStrength, failureReasons);
		}

		return response;
	}

	public AuthenticationResponse changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException
	{
		AuthenticationResponse response = null;
		try
		{
			validatePasswordStrength(newPassword);
			Pair<String, String> tuple = new CryptoCaller().ecrypt(oldPassword, newPassword);
			String encryptedOldPassword = tuple.getValue0();
			String encryptedNewPassword = tuple.getValue1();
			response = authenticationDAO.changePassword(userId, encryptedOldPassword, encryptedNewPassword);
		}
		catch (CryptoException | DAOException e)
		{
			logger.error("Exception updating password:" + e.getMessage(), e);
			throw new AuthenticationException(e.getMessage(), e);
		}
		return response;
	}
	
	public boolean generateResetToken(String loginId) throws AuthenticationException
	{
		PasswordGenerator passwordGenerator = new PasswordGenerator();
		// Generate a token
		String token = passwordGenerator.generatePassword(compileLengthRule().getMinimumLength(), compilePositiveMatchingRules());
		// Persist in the DAO
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, 1);
		try
		{
			Pair<String, String> tuple = new CryptoCaller().ecrypt(loginId, token);
			String encryptedLoginId = tuple.getValue0();
			String encryptedToken = tuple.getValue1();
			authenticationDAO.persistGeneratedToken(encryptedLoginId, encryptedToken, new java.sql.Date(c.getTime().toInstant().toEpochMilli()));
		}
		catch (CryptoException | DAOException e)
		{
			logger.error("Exception generating reset token:" + e.getMessage(), e);
			throw new AuthenticationException(e.getMessage(), e);
		}
		
		
		// Send EMail/SMS from the UserProfile using userId
		// This will require first using loginId to lookup userId
		// and using userId to get email
		// Currently the loginId is the emailId : Should we just mandate the
		// loginId is the emailId? Makes life easier for everyone.
		// Then no lookup is required.
		return false;
	}

	/**
	 * Defined all the password rules here. 1. Length of the password. 2.
	 * Postive matching rules used by validator and generator. 3. Negative
	 * matching rules used by validator only.
	 * 
	 * Validator needs negative matching to eliminate while generator will only
	 * use positive matching to generate.
	 */
	private LengthRule compileLengthRule()
	{
		return new LengthRule(8, 12);
	}

	private List<CharacterRule> compilePositiveMatchingRules()
	{
		List<CharacterRule> positiveMatchingRules = new ArrayList<>();

		// Rule 1: At least one Upper-case character
		positiveMatchingRules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));

		// Rule 2: At least one Lower-case character
		positiveMatchingRules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));

		// Rule 3: At least one digit
		positiveMatchingRules.add(new CharacterRule(EnglishCharacterData.Digit, 1));

		// Rule 4: At least one special character 
		CharacterData specialChars = new CharacterData()
		{
			public String getErrorCode()
			{
				return EnglishCharacterData.Special.getErrorCode();
			}

			public String getCharacters()
			{
				return ALLOWED_SPL_CHARACTERS;
			}
		};
		positiveMatchingRules.add(new CharacterRule(specialChars, 1));

		return positiveMatchingRules;
	}

	private List<Rule> compileNegativeMatchingRules()
	{
		List<Rule> negativeMatchingRules = new ArrayList<>();

		// Rule 1 No whitespace allowed
		negativeMatchingRules.add(new WhitespaceRule());

		return negativeMatchingRules;
	}
}
