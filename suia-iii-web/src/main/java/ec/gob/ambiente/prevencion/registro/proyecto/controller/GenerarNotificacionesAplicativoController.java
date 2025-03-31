/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.registro.proyecto.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * @author frank torres
 */
@ManagedBean
@ViewScoped
public class GenerarNotificacionesAplicativoController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(GenerarNotificacionesAplicativoController.class);

	@Getter
	@Setter
	ProyectoLicenciamientoAmbiental proyecto;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
    private UsuarioFacade usuarioFacade;
	
	@EJB
	ContactoFacade contactoFacade;


    /**
     * fecha:  2019-02-21
     * descripcion: metodo para envio de notificaciones en caso de error por no encontrar firma o usuario responsable
     * @param tipoError
     * @throws ServiceException
     * @throws InterruptedException
     */
	public void enviarNotificacionError(ProyectoLicenciamientoAmbiental proyecto, String tipoError) throws ServiceException, InterruptedException {

		String mensajeMail ="";
		if("firma".equals(tipoError)){
			mensajeMail = "No existe la firma de "+((proyecto.getAreaResponsable().getTipoArea().getId().equals(3))?"AUTORIDAD AMBIENTAL en "+proyecto.getAreaResponsable().getAreaName():"SUBSECRETARIO DE CALIDAD AMBIENTAL.");
		}else if("responsable".equals(tipoError)){
			mensajeMail = "No existe usuario de "+((proyecto.getCatalogoCategoria().getEstrategico()==true)?"SUBSECRETARIO DE CALIDAD AMBIENTAL ":"AUTORIDAD AMBIENTAL en "+proyecto.getAreaResponsable().getAreaName());							
		}else if("subsecretario".equals(tipoError)){
			mensajeMail = "No existe SUBSECRETARIO DE CALIDAD AMBIENTAL, o existe mas de un SUBSECRETARIO DE CALIDAD AMBIENTAL";							
		}else if("firmaCertificadol".equals(tipoError)){
			mensajeMail = "No existe la firma del Director de Prevencion.";							
		}else{
			mensajeMail = tipoError;
		}
		//obtengo los usuarios con el rol de notificacion de errores para el envio de la notificacion
		List<Usuario> usuarios_notificacion= new ArrayList<Usuario>();
		usuarios_notificacion = usuarioFacade.buscarUsuarioPorRol("NOTIFICACIONES APLICATIVOS");
		List<Contacto> listContacto = new ArrayList<Contacto>();
		for (int i = 0; i < usuarios_notificacion.size(); i++) {
			listContacto = contactoFacade.buscarPorPersona(usuarios_notificacion.get(i).getPersona());
			for (int j = 0; j < listContacto.size(); j++) {
				if (listContacto.get(j).getFormasContacto().getId().equals(5)) {
					// envio de correo
					NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
					mail_a.sendEmailNotificacionesError(listContacto.get(j).getValor(), 
							"Registro del proyecto",mensajeMail,
							proyecto.getNombre(),
							proyecto.getCodigo(),
							JsfUtil.getLoggedUser().getPersona().getNombre(),
							proyecto.getCatalogoCategoria().getDescripcion(),
							proyecto.getAreaResponsable().getAreaName(), usuarios_notificacion.get(i), loginBean.getUsuario());
					Thread.sleep(2000);
					break;
				}
			}
		}
	}
	
	public void envioNotificacion(ProyectoLicenciamientoAmbiental proyecto, String rolName, String proponente, String tipo, boolean tecnicoPorArea ) {
		List<Usuario> listaUsuario = new ArrayList<Usuario>();
		List<Contacto> listContacto = new ArrayList<Contacto>();
		//obtengo los usuarios con el rol de notificacion de errores para el envio de la notificacion
		if(tecnicoPorArea){
			listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolName, proyecto.getAreaResponsable());
		} else{
			listaUsuario = usuarioFacade.buscarUsuarioPorRol(rolName);	
		}
		for (int i = 0; i < listaUsuario.size(); i++) {
			try {
				listContacto = contactoFacade.buscarPorPersona(listaUsuario.get(i).getPersona());
				for (int j = 0; j < listContacto.size(); j++) {
					if (listContacto.get(j).getFormasContacto().getId().equals(5)){
						// envio de correo
						NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
						mail_a.sendEmailAutoridadMineria(tipo, 
								listContacto.get(j).getValor(),
								"Registro final del proyecto",
								"Este correo fue enviado usando JavaMail",
								proyecto.getCodigo(), proyecto.getNombre(),
								proponente, listaUsuario.get(i), loginBean.getUsuario());
						Thread.sleep(2000);
					}																					
				}
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void envioNotificacionRcoa(ProyectoLicenciaCoa proyecto, String rolName, String proponente, String tipo, boolean tecnicoPorArea ) {
		List<Usuario> listaUsuario = new ArrayList<Usuario>();
		List<Contacto> listContacto = new ArrayList<Contacto>();
		//obtengo los usuarios con el rol de notificacion de errores para el envio de la notificacion
		if(tecnicoPorArea){
			listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolName, proyecto.getAreaResponsable());
		} else{
			listaUsuario = usuarioFacade.buscarUsuarioPorRol(rolName);	
		}
		for (int i = 0; i < listaUsuario.size(); i++) {
			try {
				listContacto = contactoFacade.buscarPorPersona(listaUsuario.get(i).getPersona());
				for (int j = 0; j < listContacto.size(); j++) {
					if (listContacto.get(j).getFormasContacto().getId().equals(5)){
						// envio de correo
						NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
						mail_a.sendEmailAutoridadMineria(tipo, 
								listContacto.get(j).getValor(),
								"Registro final del proyecto",
								"Este correo fue enviado usando JavaMail",
								proyecto.getCodigoUnicoAmbiental(), proyecto.getNombreProyecto(),
								proponente, listaUsuario.get(i), loginBean.getUsuario());
						Thread.sleep(2000);
					}																					
				}
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
