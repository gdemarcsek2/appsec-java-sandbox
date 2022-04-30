package com.gdemarcsek.appsec.visibility.demo.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class HashRedactionStrategy implements RedactionStrategy<String> {
      private static HashRedactionStrategy _instance = null;

      private HashRedactionStrategy() {
      }

      public static HashRedactionStrategy getInstance() {
            if (_instance == null) {
                  _instance = new HashRedactionStrategy();
            }

            return _instance;
      }

      private static String bytesToHex(byte[] hash) {
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (int i = 0; i < hash.length; i++) {
                  String hex = Integer.toHexString(0xff & hash[i]);
                  if (hex.length() == 1) {
                        hexString.append('0');
                  }
                  hexString.append(hex);
            }
            return hexString.toString();
      }

      @Override
      public String redact(String v) {
            try {
                  MessageDigest digest = MessageDigest.getInstance("SHA-256");
                  byte[] encodedhash = digest.digest(v.getBytes(StandardCharsets.UTF_8));

                  return "sensitive{" + bytesToHex(encodedhash) + "}";
            } catch (NoSuchAlgorithmException e) {
                  // Safe fallback
                  return "*******";
            }
      }
}
