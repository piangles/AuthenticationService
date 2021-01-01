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
			String encryptedValue1 = null;
			if (value1 == null)
			{
				encryptedValue1 = "";
			}
			else
			{
				encryptedValue1 = crypto.encrypt(value1);
			}
			String encryptedValue2 = crypto.encrypt(value2);

			return Pair.with(encryptedValue1, encryptedValue2);
		});
		
		return smt.execute();
	}
}
