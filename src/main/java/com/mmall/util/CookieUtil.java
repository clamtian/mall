package com.mmall.util;



import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lucky on 2019/3/15.
 */
@Slf4j
public class CookieUtil {

    public final static String SSO_NAME = PropertiesUtil.getProperty("ssoname");
    public final static String SSO_DOMAIN = "ssodomain";

    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie ck = new Cookie(SSO_NAME,token);
        ck.setDomain(SSO_DOMAIN);
        ck.setPath("/");//代表设置在根目录
        ck.setHttpOnly(true);
        //单位是秒。
        //如果这个maxage不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效。
        ck.setMaxAge(60 * 60 * 24 * 365);//如果是-1，代表永久
        log.info("write cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
        response.addCookie(ck);
    }

    public static String readCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(SSO_NAME)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
