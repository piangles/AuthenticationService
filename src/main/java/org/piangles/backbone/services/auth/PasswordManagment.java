package org.piangles.backbone.services.auth;

import java.util.ArrayList;
import java.util.List;

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
import org.piangles.backbone.services.auth.dao.AuthenticationDAO;
import org.piangles.backbone.services.logging.LoggingService;

public class PasswordManagment
{
	private LoggingService logger = Locator.getInstance().getLoggingService();

	private AuthenticationDAO authenticationDAO = null;
	private List<Rule> passwordRules = null;

	public PasswordManagment(AuthenticationDAO authenticationDAO)
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
			response = new AuthenticationResponse(FailureReason.TooManyAttempts, failureReasons);
		}

		return response;
	}

	public boolean generateResetToken(String loginId) throws AuthenticationException
	{
		PasswordGenerator passwordGenerator = new PasswordGenerator();
		//Generate a token
		String generatedPassword = passwordGenerator.generatePassword(compileLengthRule().getMinimumLength(), compilePositiveMatchingRules());
		//Persist in the DAO
		
		//Send EMail from the UserProfile using userId 
		//This will require first using loginId to lookup userId
		//and using userId to get email
		//Currently the loginId is the emailId : Should we just mandate the loginId is the emailId? Makes life easier for everyone.
		//Then no lookup is required.
		return false;
	}

	
	/**
	 * Defined all the password rules here.
	 * 1. Length of the password.
	 * 2. Postive matching rules used by validator and generator.
	 * 3. Negative matching rules used by validator only.
	 * 
	 * Validator needs negative matching to eliminate while
	 * generator will only use positive matching to generate.
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
		positiveMatchingRules.add(new CharacterRule(EnglishCharacterData.Special, 1));
		
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
