package org.piangles.backbone.services.auth;

import org.javatuples.Pair;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.crypto.CryptoException;
import org.piangles.backbone.services.crypto.CryptoService;
import org.piangles.core.services.remoting.SessionImpersonator;

public class CryptoCaller
{
	private static String cipherAuthorizationId = "7a948dce-1ebb-4770-b077-f453e60243da";

	private CryptoService crypto = Locator.getInstance().getCryptoService();
	
	public Pair<String, String> ecrypt(String value1, String value2) throws CryptoException
	{
		SessionImpersonator<Pair<String, String>, CryptoException> smt = new SessionImpersonator<Pair<String, String>, CryptoException>(() ->{
			String encryptedLogin = crypto.encrypt(value1);
			String encryptedPassword = crypto.encrypt(value2);
			return Pair.with(encryptedLogin, encryptedPassword);
		});
		
		return smt.execute();
	}
}
