package com.radwan.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public final class ValidationUtil {

    public static boolean isValidName(String name) {
        return !StringUtils.isBlank(name);
    }

    public static boolean isValidTransferAmoount(double amount) {
        if (amount <= 0) {
            return false;
        }

        return true;
    }

    public static boolean isValidAccountId(String accountId) {
        return !StringUtils.isBlank(accountId); // more validations can be added e.g. starts with prefix 'Account-'
    }
}
