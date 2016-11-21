package com.chouti.analyse.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-10.
 *******************************************************************************/
public class UrlUtils {

    private static String regix="[^\\/]+\\/\\/([\\w\\.\\-]+)";
    static Pattern p = Pattern.compile(regix,Pattern.CASE_INSENSITIVE);
    public static String getNewsHost(String url) {
        Matcher matcher = p.matcher(url);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }

    public static void main(String args[]){
        System.out.println(getNewsHost("http://you.1-63.com?from=web_in_wydaohang"));
        System.out.println(getNewsHost("http://jiu.163.com"));
    }
}
