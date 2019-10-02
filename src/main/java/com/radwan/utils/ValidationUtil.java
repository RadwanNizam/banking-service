package com.radwan.utils;

public final class ValidationUtil {
    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        return true;
    }

    public static boolean isValidTransferAmoount(double amount) {
        if (amount <= 0) {
            return false;
        }

        return true;
    }

    public static boolean isValidAccountId(String accountId) {
        if (accountId == null || accountId.isEmpty()) {
            return false;
        }

        return true;
    }
}
