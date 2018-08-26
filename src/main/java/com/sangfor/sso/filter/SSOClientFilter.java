package com.sangfor.sso.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.sangfor.sso.util.HttpUtils;
import com.sangfor.sso.util.SSOClientUtil;

import net.sf.json.JSONObject;

public class SSOClientFilter implements Filter{

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		Boolean isLogin = (Boolean)session.getAttribute("isLogin");
		System.out.println(isLogin);
		if (isLogin != null && isLogin) {
			chain.doFilter(req, resp);
			return;
		}
		// 判断地址栏中是否有token
		String token = req.getParameter("token");
		System.out.println("token:"+token);
		if (StringUtils.isNoneBlank(token)) {
			// 判断token是否由谁中心产生
			String url = SSOClientUtil.SERVER_URL_PREFIX + "/sso/verify";
			Map<String, String> params = new HashMap<String, String>();
			params.put("token", token);
			try {
				String isVerify = HttpUtils.sendPostRequest(url, params, null);
				System.out.println(isVerify);
				System.out.println("yes".equals(isVerify) );
				JSONObject fromObject = JSONObject.fromObject(isVerify);
				System.out.println(fromObject.get("status"));
				Integer status = (Integer)fromObject.get("status");
				if (status == 200) {
					System.out.println("进入登陆成功");
					session.setAttribute("isLogin", true);
					chain.doFilter(req, resp);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		SSOClientUtil.redirectToSSOURL(req, resp);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
