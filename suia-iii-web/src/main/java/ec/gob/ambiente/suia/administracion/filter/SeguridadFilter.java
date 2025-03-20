/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.filter;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.login.bean.LoginBean;

/**
 * 
 * @author christian
 */
@WebFilter(filterName = "SeguridadFilter", urlPatterns = { "/*" }, dispatcherTypes = { DispatcherType.REQUEST,
		DispatcherType.FORWARD })
public class SeguridadFilter implements Filter {

	@Getter
	@Setter
	private FilterConfig config;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		setConfig(filterConfig);

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest req = (HttpServletRequest) request;

		if (SeguridadFilterExcluder.getInstance(req.getContextPath()).isExcludeUrl(req.getRequestURI()) || req.getRequestURI().isEmpty()) {
			chain.doFilter(request, response);
			return;
		}

		LoginBean loginBean = (LoginBean) req.getSession().getAttribute("loginBean");
		@SuppressWarnings("unchecked")
		List<String> listaPermisos = (List<String>) req.getSession().getAttribute("listaPermisos");
		if (loginBean == null) {
			req.getRequestDispatcher("/start.jsf").forward(request, response);
		} else {
			if (loginBean.getUsuario().getId() != null) {
				String pagina = devolverPagina(req.getRequestURL().toString());
				if (validarPagina(pagina, listaPermisos)) {
					chain.doFilter(request, response);
				} else {
					req.getRequestDispatcher("/errors/permisos.jsf").forward(request, response);
				}
			} else {
				req.getRequestDispatcher("/errors/permisos.jsf").forward(request, response);
			}
		}
	}

	private boolean validarPagina(final String pagina, final List<String> listaPermisos) {
		return listaPermisos.contains(pagina);
	}

	@Override
	public void destroy() {
		throw new UnsupportedOperationException();
	}

	private String devolverPagina(final String url) {
		StringTokenizer str = new StringTokenizer(url == null ? "" : url, "/");
		String retorno = null;
		while (str.hasMoreTokens()) {
			retorno = str.nextToken();
		}
		return retorno;
	}
}
