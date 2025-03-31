package ec.gob.ambiente.suia.administracion.filter;

import java.util.ArrayList;
import java.util.List;

public class SeguridadFilterExcluder {

	private static SeguridadFilterExcluder instance;

	private List<String> excludeUrls;
	private String contextPath;

	private SeguridadFilterExcluder(String contextPath) {
		this.contextPath = contextPath;
		excludeUrls = new ArrayList<String>();

		/**********************************/
		/**** DESHABILITA LA SEGURIDAD ****/
		excludeUrls.add("/");
		/**********************************/
		
		excludeUrls.add("/resources/");
		excludeUrls.add("/javax.faces.resource/");
		excludeUrls.add("/errors/");
		excludeUrls.add("/index.html");
		excludeUrls.add("/start.jsf");
		excludeUrls.add("/claveRecuperada.jsf");
		excludeUrls.add("/promotor.jsf");
		excludeUrls.add("/recuperarClave.jsf");
		excludeUrls.add("/ResumenFichas.jsf");
		excludeUrls.add("/usuarioRegistrado.jsf");
		excludeUrls.add("/acuerdo/acuerdo.pdf");
		excludeUrls.add("/integracion/contenido.jsf");
	}

	public boolean isExcludeUrl(String url) {
		for (String string : excludeUrls) {
			if (url.startsWith(this.contextPath + string))
				return true;
		}
		return false;
	}

	public static SeguridadFilterExcluder getInstance(String contextPath) {
		return instance == null ? instance = new SeguridadFilterExcluder(contextPath) : instance;
	}
}
