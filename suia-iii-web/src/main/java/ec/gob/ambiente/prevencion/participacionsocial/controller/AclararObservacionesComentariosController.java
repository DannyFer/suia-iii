package ec.gob.ambiente.prevencion.participacionsocial.controller;

import ec.gob.ambiente.prevencion.participacionsocial.bean.AclararObservacionesComentariosBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbientalComentarios;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialAmbientalComentariosFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ManagedBean
@RequestScoped
public class AclararObservacionesComentariosController implements Serializable {


	private static final long serialVersionUID = 3953479226895008318L;
	private static final Logger LOGGER = Logger.getLogger(AclararObservacionesComentariosController.class);
	private static final String COMPLETADA = "Completada";
	
	
	@EJB
	private ParticipacionSocialAmbientalComentariosFacade participacionSocialAmbientalComentariosFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private ParticipacionSocialFacade participacionSocialFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
			
	@Getter
	@Setter
	@ManagedProperty(value = "#{aclararObservacionesComentariosBean}")
	private AclararObservacionesComentariosBean aclararObservacionesComentariosBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@Getter
	@Setter
	private String documentoActivo = "";

	@Getter
	@Setter
	private Map<String, Documento> documentos;

	@PostConstruct
	private void init() {
		
	}	
	
	public void editarComentario(ParticipacionSocialAmbientalComentarios comentario)
	{
		aclararObservacionesComentariosBean.setComentarioPPS(comentario);		
	}
	
	
	public void guardarComentario()
	{
		try {			
			participacionSocialAmbientalComentariosFacade.guardarComentario(aclararObservacionesComentariosBean.getComentarioPPS());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	

	public String finalizar(){
		try {
			
			//Validar que exista un respuesta por cada observacion
			for (ParticipacionSocialAmbientalComentarios respuesta : aclararObservacionesComentariosBean.getComentariosProyectosPPS()) {
				if(respuesta.getAccion()==null||respuesta.getAccion().equals(""))
				{
					JsfUtil.addMessageWarning("No se ha ingresado todas las respuestas a las observaciones");		
					return "";
				}
			}

			
			//Finalizar la tarea
			Map<String, Object> data = new HashMap<>();
			procesoFacade.aprobarTarea(loginBean.getUsuario(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getProcessId(),data);
			JsfUtil.addMessageInfo("Se proceso la tarea correctamente");		
			return "/bandeja/bandejaTareas.jsf";
			
		} catch (Exception e) {
			JsfUtil.addMessageError("No se proceso correctamente");			
			return "";
		}
		
	}
	
    public void validarTareaBpm() {

        String url = "/prevencion/participacionsocial/ingresarInformacionComplementaria.jsf";
        if (!aclararObservacionesComentariosBean.getTipo().isEmpty()) {
            url += "?tipo=" + aclararObservacionesComentariosBean.getTipo();
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }
	
}