package ec.gob.ambiente.control.programasremediacion.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class IdentificarProyectoBean implements Serializable {
        	private static final long serialVersionUID = -3526371287113629686L;


	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade; 

	@Setter
	@Getter
	private ProyectoLicenciamientoAmbiental proyectoActivo;

	@Setter
	@Getter
	private List<ProyectoLicenciamientoAmbiental> proyectos;

	@PostConstruct
	public void init() {

		//proyectos = proyectoFacade.listarProyectosLicenciamientoAmbientalFinalizados();

		proyectoActivo = new ProyectoLicenciamientoAmbiental();
		if (!proyectos.isEmpty()) {
			proyectoActivo = proyectos.get(0);
		}
	}

	public void cambiarProyectoActivo() {
	}

	public void seleccionarProyectoActivo() {

		// JsfUtil.addMessageInfo(proyectoActivo.getNombre());
	}

}
