package com.sangfor.sso.util;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SSOClientUtil {

	private static Properties ssoProperties = new Properties();
	public static String SERVER_URL_PREFIX;
	public static String CLIENT_HOST_URL;
	
	static {
		try {
			ssoProperties.load(SSOClientUtil.class.getClassLoader().getResourceAsStream("sso.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		SERVER_URL_PREFIX = ssoProperties.getProperty("server-url-prefix");
		CLIENT_HOST_URL = ssoProperties.getProperty("client-host-url");
	}

	public static void redirectToSSOURL(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String redirectUrl = getRedirectUrl(req);
		StringBuilder url = new StringBuilder(50)
				.append(SERVER_URL_PREFIX)
				.append("/sso/checkLogin?redirectUrl=")
				.append(redirectUrl);
		resp.sendRedirect(url.toString());
	}

	private static String getRedirectUrl(HttpServletRequest req) {
		return CLIENT_HOST_URL + req.getServletPath();
	}
}
