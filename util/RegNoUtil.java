package com.example.hiring.util;

public final class RegNoUtil {
  private RegNoUtil() {}
  public static boolean lastTwoDigitsOdd(String regNo) {
    String digits = regNo.replaceAll("\\D+", "");
    if (digits.length() < 2) return false;
    int lastTwo = Integer.parseInt(digits.substring(digits.length() - 2));
    return (lastTwo % 2) == 1;
  }
}
