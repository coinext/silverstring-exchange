package io.silverstring.core.util;

import io.silverstring.domain.hibernate.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;


public class KeyGenUtil {
    public static String generateDocFileName() {
        return RandomStringUtils.randomAlphanumeric(64);
    }

    public static String generateApiKey() {
        return RandomStringUtils.randomAlphanumeric(32);
    }

    public static String generateTxId() {
        return RandomStringUtils.randomAlphanumeric(64);
    }

    public static String generateKey(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String generateNumericKey(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    public static String generateEmailConfirmNumericKey() {
        return RandomStringUtils.randomAlphanumeric(20);
    }

    public static String generateHashEmail(Long userId, String pwd, String email) {
        return DigestUtils.md5Hex(RandomStringUtils.randomAlphanumeric(32) + "_" + userId + "_" + pwd + "_" + email + "_" + RandomStringUtils.randomAlphanumeric(32));
    }
}
