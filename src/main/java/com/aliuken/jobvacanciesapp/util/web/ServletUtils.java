package com.aliuken.jobvacanciesapp.util.web;

import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.util.ForwardedHeaderUtils;
import org.springframework.web.util.UriComponents;

import jakarta.servlet.http.HttpServletRequest;

public class ServletUtils {
	public static String getUrlFromHttpServletRequest(final HttpServletRequest httpServletRequest) {
		final ServletServerHttpRequest servletServerHttpRequest = new ServletServerHttpRequest(httpServletRequest);
		final UriComponents uriComponents = ForwardedHeaderUtils.adaptFromForwardedHeaders(servletServerHttpRequest.getURI(), servletServerHttpRequest.getHeaders()).build();
		final String url = uriComponents.toUriString();
		return url;
	}
}