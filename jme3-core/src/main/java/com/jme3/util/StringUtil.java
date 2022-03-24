package com.jme3.util;

public class StringUtil {
    /**
     * This can be used to compare Strings.
     * It will perform faster than String.equals when the same strings are compared multiple times. 
     * String.equals might be faster when strings are compared only once.
     * @param s1 First string
     * @param s2 Second string
     * @return true if the strings are equal
     */
    public static boolean fastStringEquals(String s1, String s2) {
        if (s1 == s2) return true; // same object
        if (s1 != s2 && (s1 == null || s2 == null)) return false;// different objects and one is null
        if (s1.length() != s2.length()) return false; // different lengths, strings are different
        if (s1.hashCode() != s2.hashCode()) return false; // different hashcode, strings must be different
        return s1.equals(s2);
    }

    
}
