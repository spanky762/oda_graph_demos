package com.paulwithers.bacon;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xworlds.appservers.lifecycle.XWorldsManagedThread;
import org.openntf.xworlds.appservers.webapp.config.XWorldsApplicationConfigurator;

@WebFilter(urlPatterns = "/*", asyncSupported = true, filterName = "XWorldsRequestsFilter")
public class BaconFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {

			XWorldsManagedThread.setupAsDominoThread((HttpServletRequest) request);
			XWorldsApplicationConfigurator configurator = (XWorldsApplicationConfigurator) request.getServletContext()
					.getAttribute(XWorldsApplicationConfigurator.APPCONTEXT_ATTRS_CWAPPCONFIG);
			if (configurator != null) {
				configurator.setupRequest((HttpServletRequest) request, (HttpServletResponse) response);
			}

			chain.doFilter(request, response);

		} finally {
			XWorldsManagedThread.shutdownDominoThread();
		}

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
