/**	
 * Beacon - Open Source Minecraft Server
 * Copyright (C) 2014  Jan Delius
 * Copyright (C) 2014  Blazecube
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author Jan Delius
 */
 package org.beaconmc.utils;

import org.beaconmc.logging.ErrorStream;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class EncryptionUtils {

    private static SecureRandom secureRandom = new SecureRandom();

    public static KeyPair generateRSAPair(){
        KeyPair keyPair = null;
        try{
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            keyPair = generator.generateKeyPair();
        }catch(NoSuchAlgorithmException e){
            ErrorStream.handle(e);
        }
        return keyPair;
    }

    public static byte[] generateToken(){
        byte[] token = new byte[4];
        secureRandom.nextBytes(token);
        return token;
    }

    public static SecretKey a(PrivateKey privatekey, byte[] abyte) {
        return new SecretKeySpec(b(privatekey, abyte), "AES");
    }

    public static byte[] b(Key key, byte[] abyte) {
        return a(2, key, abyte);
    }
    private static byte[] a(int i, Key key, byte[] abyte) {
        try {
            return a(i, key.getAlgorithm(), key).doFinal(abyte);
        } catch (Exception e) {
            ErrorStream.handle(e);
        }
        return null;
    }

    private static Cipher a(int i, String s, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(s);
            cipher.init(i, key);
            return cipher;
        } catch (Exception e) {
            ErrorStream.handle(e);
        }
        return null;
    }

}
