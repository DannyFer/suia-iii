package ec.gob.ambiente.prevencion.participacionsocial.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbientalComentarios;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialAmbientalComentariosFacade;

@ManagedBean
@ViewScoped
public class AclararObservacionesComentariosBean implements Serializable {


	private static final long serialVersionUID = 136812468368839119L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(AclararObservacionesComentariosBean.class);

	@EJB
	private ParticipacionSocialAmbientalComentariosFacade participacionSocialAmbientalComentariosFacade;
	

	@Getter
	@Setter
	private ParticipacionSocialAmbiental proyectoPPS;
	
	@Getter
	@Setter	
	private List<ParticipacionSocialAmbientalComentarios> comentariosProyectosPPS;
	
	
	@Getter
	@Setter	
	private ParticipacionSocialAmbientalComentarios comentarioPPS;
		
	@Getter
	@Setter
	@ManagedProperty(value="#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	private boolean revisar;

	@Getter
	@Setter
	private Boolean informacionCompleta;
	
	@Getter
	@Setter
	private String tipo = "";
	
	
	@PostConstruct
	public void init() {
		
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		if (params.containsKey("tipo")) {
			tipo = params.get("tipo");
			if (params.get("tipo").equals("revisar")) {
				revisar = true;

			}
		}
		
		
		comentariosProyectosPPS=new ArrayList<ParticipacionSocialAmbientalComentarios>() ;	
		
		try {
			List<ParticipacionSocialAmbientalComentarios> observaciones=participacionSocialAmbientalComentariosFacade.getCommentsByProjectId(proyectosBean.getProyecto());
			for (ParticipacionSocialAmbientalComentarios observacion : observaciones) {
				if(observacion.getObservacionComplementaria()!=null&&!observacion.getObservacionComplementaria().equals(""))
				{
					comentariosProyectosPPS.add(observacion);
				}
			}
			//comentariosProyectosPPS=participacionSocialAmbientalComentariosFacade.getCommentsByProjectId(proyectosBean.getProyecto());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}


	public int numObservacion(ParticipacionSocialAmbientalComentarios comentario)
	{
		return 1+comentariosProyectosPPS.indexOf(comentario);
	}

}
