package ec.gob.ambiente.rcoa.util;

import java.io.Serializable;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import ec.gob.ambiente.suia.domain.Usuario;
import lombok.Getter;
import lombok.Setter;

public class WebSessionBindingListener implements HttpSessionBindingListener, Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Usuario usuario;

	public WebSessionBindingListener(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
		ServletContext contexto = arg0.getSession().getServletContext();
		synchronized (contexto) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			HashMap<String, Object> mapa = (HashMap) contexto.getAttribute("listadoUsuarios");
			if (mapa == null) {
				mapa = new HashMap<String, Object>();
			}
			if(usuario.getNumeroSesiones() == 1) {
				mapa.put(usuario.getNombre()+"|1", arg0.getSession().getId()+"|1");
			}else {
				String existe = null;
				for(int i = 1; i <= usuario.getNumeroSesiones(); i++) {
					existe = (String)mapa.get(usuario.getNombre()+"|"+i);
					if(existe == null) {
						mapa.put(usuario.getNombre()+"|"+i, arg0.getSession().getId()+"|"+i);
						i = usuario.getNumeroSesiones();
					}
				}
			}
//			System.out.println("Listado de usuarios (Ingreso) : " + mapa);
			contexto.setAttribute("listadoUsuarios", mapa);
		}
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {

		ServletContext contexto = arg0.getSession().getServletContext();
		synchronized (contexto) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			HashMap<String, Object> mapa = (HashMap) contexto.getAttribute("listadoUsuarios");
			if (mapa == null) {
				mapa = new HashMap<String, Object>();
			}
			
			if(usuario.getNumeroSesiones() == 1) {
				mapa.remove(usuario.getNombre()+"|1");
			}else {
				System.out.println("Id session : " + arg0.getSession().getId());
				String existe = null;
				for(int i = 1; i <= usuario.getNumeroSesiones(); i++) {
					try {
						existe = (String) mapa.get(usuario.getNombre()+"|"+i);
					} catch (Exception e) {
						existe	= null;
					}
					if(existe!=null) {
						mapa.remove(usuario.getNombre()+"|"+i);
						i = usuario.getNumeroSesiones();
					}
				}
			}
//			System.out.println("Listado de usuarios (Remove) : " + mapa);
			contexto.setAttribute("listadoUsuarios", mapa);
		}
	}
}
