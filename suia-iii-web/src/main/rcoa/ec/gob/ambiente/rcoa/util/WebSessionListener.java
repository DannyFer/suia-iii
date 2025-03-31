package ec.gob.ambiente.rcoa.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
public class WebSessionListener implements HttpSessionListener{

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		System.out.println("Sesión creada...");
		ServletContext contexto = arg0.getSession().getServletContext();
		synchronized (contexto) {
			Integer usuarioConectados = (Integer) contexto.getAttribute("usuariosConectados");
			if (usuarioConectados == null) {
				usuarioConectados = new Integer(0);
			}
			usuarioConectados += 1;
			contexto.setAttribute("usuariosConectados", usuarioConectados);
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		System.out.println("Sesión destruida...");
		ServletContext contexto = arg0.getSession().getServletContext();
		synchronized (contexto) {
			Integer usuarioConectados = (Integer) contexto.getAttribute("usuariosConectados");
			if (usuarioConectados == null) {
				usuarioConectados = new Integer(0);
			}
			usuarioConectados -= 1;
			contexto.setAttribute("usuariosConectados", usuarioConectados);
		}
	}
}
