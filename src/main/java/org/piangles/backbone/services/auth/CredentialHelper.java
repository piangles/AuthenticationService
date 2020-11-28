package org.piangles.backbone.services.auth;

import org.javatuples.Pair;
import org.piangles.backbone.services.crypto.CryptoException;

public class CredentialHelper
{
	/**
	 * All credentials are encrypted and saved in the database so need to enrypt
	 * when we query as well.
	 * @param credential
	 * @return
	 * @throws CryptoException
	 */
	public Credential createEncryptedCredential(Credential credential) throws CryptoException
	{
		Pair<String, String> tuple = new CryptoCaller().ecrypt(credential.getId(), credential.getPassword());

		return new Credential(tuple.getValue0(), tuple.getValue1());
	}
}
