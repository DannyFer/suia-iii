package ec.gob.ambiente.prevencion.participacionsocial.bean;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.dto.EntityProyectoParticipacionSocial;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.PublicarEsudioParticipacionSocialFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ManagedBean
@ViewScoped
public class PublicarEstudioBean implements Serializable {

	private static final long serialVersionUID = -712516115528818398L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(PublicarEstudioBean.class);

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private PublicarEsudioParticipacionSocialFacade publicarEsudioParticipacionSocialFacade;
	
	@Getter
	@Setter
	private ParticipacionSocialAmbiental proyectoPPS;
	
	@Getter
	@Setter
	private ProyectoCustom proyectoCustom;

	@Setter
	private List<ProyectoCustom> proyectos;
	
//	@Getter
	@Setter
	private List<ParticipacionSocialAmbiental> proyectosPPS;
//	@Getter
//	@Setter
	private List<EntityProyectoParticipacionSocial> proyectosPS;

	private List<EntityProyectoParticipacionSocial> proyectosPSFilter;

	private EntityProyectoParticipacionSocial proyectoPS;

	@Setter
	private List<ProyectoCustom> proyectosNoFinalizados;

	@Getter
	private List<String> categoriasItems;

	@Getter
	private List<String> sectoresItems;

	@Getter
	@Setter
	private String proponente;

	@Setter
	private List<UbicacionesGeografica> ubicacionProponente;
	
	@Getter
	@Setter
	private ProyectosBean proyectosBean;
	
	
	@Setter
	@Getter
	private List<UbicacionesGeografica> provincias;

	@Setter
	@Getter
	private List<UbicacionesGeografica> cantones;

	@Setter
	@Getter
	private List<UbicacionesGeografica> parroquias;

	@Setter
	@Getter
	private UbicacionesGeografica provincia;

	@Setter
	@Getter
	private UbicacionesGeografica canton;

	@Setter
	@Getter
	private UbicacionesGeografica parroquia;
	

	@Getter
	@Setter
	private ParticipacionSocialAmbientalComentarios comentarioPPS;

	/*public void setProyectoToShow(ProyectoLicenciamientoAmbiental proyecto) throws CmisAlfrescoException {
		this.proyecto = proyecto;
		JsfUtil.getBean(VerProyectoController.class).verProyecto(proyecto.getId());
	}

	public void setProyectoToEdit(ProyectoLicenciamientoAmbiental proyecto) throws CmisAlfrescoException {
		this.proyecto = proyecto;
		JsfUtil.getBean(VerProyectoController.class).verProyecto(proyecto.getId(), true);
	}*/

	@PostConstruct
	public void init() {
		//proyecto = new ProyectoLicenciamientoAmbiental();
		//proyectosPS = new ArrayList<EntityProyectoParticipacionSocial>();
		proyectos = new ArrayList<ProyectoCustom>();
		sectoresItems = new ArrayList<String>();
		categoriasItems = new ArrayList<String>();
		//obtenerProyectos();


		Iterator<Categoria> iteratorCategorias = proyectoLicenciamientoAmbientalFacade.getCategorias().iterator();
		Iterator<TipoSector> iteratorSectores = proyectoLicenciamientoAmbientalFacade.getTiposSectores().iterator();

		while (iteratorSectores.hasNext()) {
			TipoSector tipoSector = iteratorSectores.next();
			sectoresItems.add(tipoSector.toString());
		}

		while (iteratorCategorias.hasNext()) {
			Categoria categoria = iteratorCategorias.next();
			if (!categoriasItems.contains(categoria.getNombrePublico()))
				categoriasItems.add(categoria.getNombrePublico());
		}
		
		this.comentarioPPS=new ParticipacionSocialAmbientalComentarios();
	}

	public List<ProyectoCustom> getProyectos() {
		return proyectos == null ? proyectos = new ArrayList<ProyectoCustom>() : proyectos;
	}
	
	public List<ParticipacionSocialAmbiental> getProyectosPPS() {
		return proyectosPPS == null ? proyectosPPS = new ArrayList<ParticipacionSocialAmbiental>() : proyectosPPS;
	}

	public List<ProyectoCustom> getProyectosNoFinalizados() {
		return proyectosNoFinalizados == null ? proyectosNoFinalizados = new ArrayList<ProyectoCustom>()
				: proyectosNoFinalizados;
	}

	public List<UbicacionesGeografica> getUbicacionProponente() {
		return ubicacionProponente == null ? ubicacionProponente = new ArrayList<UbicacionesGeografica>()
				: ubicacionProponente;
	}

	public List<EntityProyectoParticipacionSocial> getProyectosPS() {
		if (proyectosPS == null) {
			proyectosPS = new ArrayList<EntityProyectoParticipacionSocial>();
		}
		return proyectosPS;
	}

	public void setProyectosPS(List<EntityProyectoParticipacionSocial> proyectosPS) {
		this.proyectosPS = proyectosPS;
	}

	public void obtenerProyectos() {
		try {
			setProyectosPS(publicarEsudioParticipacionSocialFacade.getProyectosPPS());

		} catch (Exception e) {
			LOG.error("No se puede obtener las Fichas Ambientales." , e);
			//JsfUtil.addMessageError("No se puede obtener las Fichas Ambientales.");
		}

	}

	public EntityProyectoParticipacionSocial getProyectoPS() { return proyectoPS; }

	public void setProyectoPS(EntityProyectoParticipacionSocial proyectoPS) { this.proyectoPS = proyectoPS;	}

	public List<EntityProyectoParticipacionSocial> getProyectosPSFilter() {
		return proyectosPSFilter;
	}

	public void setProyectosPSFilter(List<EntityProyectoParticipacionSocial> proyectosPSFilter) {
		this.proyectosPSFilter = proyectosPSFilter;
	}
}
