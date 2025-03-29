package com.example.accountservice.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class ConvertUtil {
    public static String removeDiacritics(String str) {
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    // Tạo email từ fullname
    public static String createEmail(String accountCode) {
        return accountCode + "@gmail.com";
    }
}
