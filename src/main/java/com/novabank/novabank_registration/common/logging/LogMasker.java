package com.novabank.novabank_registration.common.logging;

public final class LogMasker {

    private LogMasker() {
    }

    public static String maskEmail(String email) {

        if (email == null || email.isBlank()) {
            return "***";
        }

        int atIndex = email.indexOf('@');

        if (atIndex <= 0) {
            return "***";
        }

        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);

        char firstCharacter = localPart.charAt(0);

        return firstCharacter + "***@" + domain;
    }
}
