package ec.gob.ambiente.rcoa.util;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;


public class NotificacionInternaUtil {
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	public static void enviarNotificacionRegistro(ProyectoLicenciaCoa proyectoLicenciaCoa, String resolucion) throws Exception {
		List<Usuario> usuariosNotificacion = recuperarUsuariosNotificacion(proyectoLicenciaCoa, null, "R");
		if(usuariosNotificacion != null && usuariosNotificacion.size() > 0) {
			MensajeNotificacionFacade mensajeNotificacionFacade = BeanLocator.getInstance(MensajeNotificacionFacade.class);
			MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionEmisionResolucionRegistroLicencia");
			
			Object[] parametrosAsunto = new Object[] {"Registro Ambiental"};
			String asunto = String.format(mensajeNotificacion.getAsunto(), parametrosAsunto);
			
			String tipo = "el Registro Ambiental";
			
			for (Usuario userNotificacion : usuariosNotificacion) {
				Object[] parametrosCorreo = new Object[] {userNotificacion.getPersona().getNombre(), tipo, resolucion,
						proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental()};
				
				String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
 				
 				Email.sendEmail(userNotificacion, asunto, notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
 				
 				Thread.sleep(2000);
			}
		}
	}
	
	public static void enviarNotificacionLiencia(ProyectoLicenciaCoa proyectoLicenciaCoa, String resolucion, String tecnicoResponsable) throws Exception {
		List<Usuario> usuariosNotificacion = recuperarUsuariosNotificacion(proyectoLicenciaCoa, tecnicoResponsable, "L");
		if(usuariosNotificacion != null && usuariosNotificacion.size() > 0) {
			MensajeNotificacionFacade mensajeNotificacionFacade = BeanLocator.getInstance(MensajeNotificacionFacade.class);
			MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionEmisionResolucionRegistroLicencia");
			
			Object[] parametrosAsunto = new Object[] {"Licencia Ambiental"};
			String asunto = String.format(mensajeNotificacion.getAsunto(), parametrosAsunto);
			
			String tipo = "la Licencia Ambiental";
			
			for (Usuario userNotificacion : usuariosNotificacion) {
				Object[] parametrosCorreo = new Object[] {userNotificacion.getPersona().getNombre(), tipo, resolucion,
						proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental()};
				
				String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
 				
 				Email.sendEmail(userNotificacion, asunto, notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
 				
 				Thread.sleep(2000);
			}
		}
	}
	
	public static List<Usuario> recuperarUsuariosNotificacion(ProyectoLicenciaCoa proyectoLicenciaCoa, String tecnicoResponsable, String tipo) {
		String rolUsuario = "";
		List<Usuario> usuariosNotificacion = new ArrayList<>();
		List<Usuario> listaUsuario = null;
		
		AreaFacade areaFacade = BeanLocator.getInstance(AreaFacade.class);
		UsuarioFacade usuarioFacade = BeanLocator.getInstance(UsuarioFacade.class);
		ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade = BeanLocator.getInstance(ProyectoLicenciaCuaCiuuFacade.class);
		
		ProyectoLicenciaCuaCiuu ciiuArearesponsable = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
		Integer idSector = ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId();
		Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
		String siglasArea = areaTramite.getTipoArea().getSiglas().toUpperCase();
		
		if(tipo.equals("L")) { //se agrega tecnico para notificacion
			Usuario usuarioTecnico = usuarioFacade.buscarUsuario(tecnicoResponsable);
			if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
				usuariosNotificacion.add(usuarioTecnico);
			}
		}
		
		if (siglasArea.equals(Constantes.SIGLAS_TIPO_AREA_PC.toUpperCase())) {
    		rolUsuario = Constantes.getRoleAreaName("role.esia.pc.autoridad");
    		listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolUsuario, areaTramite);
    		if(listaUsuario != null && listaUsuario.size() > 0) {
    			usuariosNotificacion.add(listaUsuario.get(0));
    		}
    		
    		Area areaControl = areaFacade.getAreaSiglas(Constantes.SIGLAS_DIRECCION_NORMATIVA_CONTROL);
    		rolUsuario = Constantes.getRoleAreaName("role.esia.cz.autoridad");
    		listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolUsuario, areaControl);
    		if(listaUsuario != null && listaUsuario.size() > 0) {
    			usuariosNotificacion.add(listaUsuario.get(0));
    		}
    		
    		if(tipo.equals("R")) { //coordinador para registros
    			rolUsuario = Constantes.getRoleAreaName("role.esia.pc.coordinador.tipoSector." + idSector);
        		listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolUsuario, areaTramite);
        		if(listaUsuario != null && listaUsuario.size() > 0) {
        			usuariosNotificacion.add(listaUsuario.get(0));
        		}
    		}
		} else if (siglasArea.equals(Constantes.SIGLAS_TIPO_AREA_OT.toUpperCase())) {
			rolUsuario = Constantes.getRoleAreaName("role.esia.cz.autoridad");
    		listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolUsuario, areaTramite.getArea());
    		if(listaUsuario != null && listaUsuario.size() > 0) {
    			usuariosNotificacion.add(listaUsuario.get(0));
    		}
    		
    		rolUsuario = Constantes.getRoleAreaName("role.esia.cz.coordinador");
    		listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolUsuario, areaTramite);
    		if(listaUsuario != null && listaUsuario.size() > 0) {
    			usuariosNotificacion.add(listaUsuario.get(0));
    		}
		} else if (siglasArea.equals(Constantes.SIGLAS_TIPO_AREA_EA.toUpperCase())) {
//			rolUsuario = Constantes.getRoleAreaName("role.esia.cz.autoridad");
//    		listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolUsuario, areaTramite);
//    		if(listaUsuario != null && listaUsuario.size() > 0) {
//    			usuariosNotificacion.add(listaUsuario.get(0));
//    		}
			
    		rolUsuario = Constantes.getRoleAreaName("role.esia.gad.coordinador");
    		listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolUsuario, areaTramite);
    		if(listaUsuario != null && listaUsuario.size() > 0) {
    			usuariosNotificacion.add(listaUsuario.get(0));
    		}
		} else if(areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)){
			rolUsuario = Constantes.getRoleAreaName("role.esia.ga.autoridad");
			listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolUsuario, areaTramite);
    		if(listaUsuario != null && listaUsuario.size() > 0) {
    			usuariosNotificacion.add(listaUsuario.get(0));
    		}
    		
    		rolUsuario = Constantes.getRoleAreaName("role.esia.ga.coordinador");
    		listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolUsuario, areaTramite);
    		if(listaUsuario != null && listaUsuario.size() > 0) {
    			usuariosNotificacion.add(listaUsuario.get(0));
    		}
		}
		
		return usuariosNotificacion;
    }
	
	public static void remitirNotificacionesEstudio(ProyectoLicenciaCoa proyectoLicenciaCoa, String asunto, String notificacion, List<String> listaArchivos) throws Exception {
		
		//remitir correo a técnicos
		String tramite = proyectoLicenciaCoa.getCodigoUnicoAmbiental();
		Area areaResponsable = proyectoLicenciaCoa.getAreaResponsable();
		String tipoRol = "role.esia.cz.tecnico.responsable";

		if (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
			ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade = BeanLocator.getInstance(ProyectoLicenciaCuaCiuuFacade.class);
			ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
			CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();

			Integer idSector = actividadPrincipal.getTipoSector().getId();

			tipoRol = "role.esia.pc.tecnico.responsable.tipoSector." + idSector;
		} else if (!areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
			tipoRol = "role.esia.gad.tecnico.responsable";
		}

		String rolTecnico = Constantes.getRoleAreaName(tipoRol);

		// buscar usuarios por rol y area
		UsuarioFacade usuarioFacade = BeanLocator.getInstance(UsuarioFacade.class);
		List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
		if (listaUsuario != null && listaUsuario.size() > 0) {
			for (Usuario tecnico : listaUsuario) {
				if(listaArchivos != null) {
					Email.sendEmailAdjuntos(tecnico, asunto, notificacion, listaArchivos, tramite, JsfUtil.getLoggedUser());
				} else {
					Email.sendEmail(tecnico, asunto, notificacion, tramite, JsfUtil.getLoggedUser());
				}
				
				Thread.sleep(2000);
			}
		}
	}
	
}
