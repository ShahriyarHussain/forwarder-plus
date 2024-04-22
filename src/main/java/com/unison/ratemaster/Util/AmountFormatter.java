package com.unison.ratemaster.Util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class AmountFormatter {

    static final Logger logger = LogManager.getLogger(AmountFormatter.class.getName());
    private static final String ZERO_AMOUNT = "0.00";
    private static final int DIGIT_INTERVAL_AFTER_FIRST_COMMA = 2;
    private static final int DIGIT_INTERVAL_BEFORE_FIRST_COMMA = 3;
    private static final Map<String, String> symbolMap = new HashMap<>();


//    public static String getFormattedAmount(BigDecimal bigDecimal, String currency) {
//        setSymbolMap();
//        if (!"BDT".equals(currency)) {
//            String formattedAmount = getForeignCurrencyFormatter().format(bigDecimal);
//            if (symbolMap.containsKey(currency)) {
//                return symbolMap.get(currency) + formattedAmount;
//            } else {
//                return formattedAmount;
//            }
//        } else {
//            return symbolMap.get(currency) + getBDTakaFormattedAmount(bigDecimal);
//        }
//    }

    public static String getBDTakaFormattedAmount(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return ZERO_AMOUNT;
        }
        try {
            String basicFormatted = getBasicFormatter().format(bigDecimal);
            String[] decimalSplitArray = basicFormatted.split("\\.");
            return getBDCurrencyFormattedAmount(decimalSplitArray[0]) + "." + decimalSplitArray[1];
        } catch (Exception e) {
            logger.error("ERROR while formatting amount: " + e.getMessage());
            return getForeignCurrencyFormatter().format(bigDecimal); //if exception, return built in formatted value
        }
    }

    private static String getBDCurrencyFormattedAmount(String amount) {
        boolean isNegative = false;
        String unsignedAmount;
        if (amount.startsWith("-")) {
            unsignedAmount = amount.substring(1); //
            isNegative = true;
        } else {
            unsignedAmount = amount;
        }
        if (!isFormatRequired(unsignedAmount)) {
            return amount;
        }
        return (isNegative ? "-" : "") + formatAmount(unsignedAmount);
    }

    private static String formatAmount(String s) {
        int charCount = DIGIT_INTERVAL_BEFORE_FIRST_COMMA;
        Stack<Character> charStack = new Stack<>();

        for (int i = s.length() - 1; i > -1; i--) {
            if (charCount == 0) {
                charStack.push(',');
                charCount = DIGIT_INTERVAL_AFTER_FIRST_COMMA;
            }
            charStack.push(s.charAt(i));
            charCount--;
        }
        return getFormattedAmountFromStack(charStack);
    }

    private static String getFormattedAmountFromStack(Stack<Character> charStack) {
        StringBuilder sb = new StringBuilder();
        while (!charStack.isEmpty()) {
            sb.append(charStack.pop());
        }
        return sb.toString();
    }

    // length > 3 means either greater than 999 or -999
    private static boolean isFormatRequired(String unsignedAmount) {
        return unsignedAmount.length() > 3;
    }


//    private static void setSymbolMap() {
//        symbolMap.put("BDT", "৳");
//        symbolMap.put("USD", "US$");
//        symbolMap.put("YEN", "¥");
//        symbolMap.put("GBP", "£");
//        symbolMap.put("EUR", "€");
//        symbolMap.put("AUD", "AU$");
//        symbolMap.put("CAD", "CA$");
//    }

    public static DecimalFormat getBasicFormatter() {
        return new DecimalFormat("###0.00");
    }

    public static DecimalFormat getForeignCurrencyFormatter() {
        return new DecimalFormat("#,###0.00");
    }
}
