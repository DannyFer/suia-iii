package ec.gob.ambiente.prevencion.licenciamiento.requistosprevios.bean.OLD;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;

@ManagedBean(name = "categorizarProyecto")
@ViewScoped
public class CategorizarProyectoBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5425251122652880220L;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamiento;
	@Getter
	@Setter
	private String tipoProyecto;
	@Getter
	@Setter
	private String sectorProyecto;
	@Getter
	@Setter
	private String categoriaProyecto;
	@Getter
	@Setter
	private String tipoRequerimiento;
	@Getter
	@Setter
	private String modalidad;
	@Getter
	@Setter
	private List<String> modalidades;

	@PostConstruct
	public void init() {
		// DATOS DEL PROYECTO
		if (getProyectoLicenciamiento() != null) {
			setTipoProyecto(proyectoLicenciamiento.getNombre());
			setSectorProyecto(proyectoLicenciamiento.getNombre());
			setCategoriaProyecto(proyectoLicenciamiento.getNombre());
		}

		// LISTA DE MODALIDADES
		List<String> initModalidades = new ArrayList<String>();
		initModalidades.add("A");
		initModalidades.add("B");
		initModalidades.add("C");
		initModalidades.add("D");
		initModalidades.add("E");
		initModalidades.add("F");
		initModalidades.add("Transporte");

		setModalidades(initModalidades);
	}
}
