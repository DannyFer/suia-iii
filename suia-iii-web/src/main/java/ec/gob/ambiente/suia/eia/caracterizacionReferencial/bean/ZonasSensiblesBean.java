package ec.gob.ambiente.suia.eia.caracterizacionReferencial.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.eia.caracterizacionReferencial.facade.CaracterizacionReferencialFacade;
import ec.gob.ambiente.suia.utils.Archivo;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean(name = "zonaSensible")
@ViewScoped
public class ZonasSensiblesBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5204799283917028713L;

	@EJB
	public CaracterizacionReferencialFacade caracterizacionReferencialFacade;

	@Getter
	@Setter
	private Integer eiaId;

	@Getter
	@Setter
	private boolean aplica;

	@Getter
	@Setter
	private List<CoordenadaGeneral> coordenadas;

	@Getter
	@Setter
	private CoordenadaGeneral coordenada;

	@Getter
	@Setter
	private Archivo archivoDescripcion;

	@Getter
	@Setter
	private Archivo archivoImagen;

	@Getter
	@Setter
	private List<List<Object>> eliminados;

	@Getter
	@Setter
	private int indice = 0;

	@PostConstruct
	public void init() {
		if (coordenadas == null) {
			coordenadas = new ArrayList<CoordenadaGeneral>();
		}
		if (coordenada == null) {
			coordenada = new CoordenadaGeneral();
		}
		if (eliminados == null) {
			eliminados = new ArrayList<List<Object>>();
		}

		try {
			EstudioImpactoAmbiental eia = (EstudioImpactoAmbiental)JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
			setEiaId(eia.getId());

			setCoordenadas(caracterizacionReferencialFacade.obtenerCoordenadas(
					getEiaId(),
					CoordenadaGeneral.PREFIX_EIA_ZONAS_SENSIBLES
							+ EstudioImpactoAmbiental.class.getSimpleName()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleFileUploadImagenes(FileUploadEvent event) {
		Archivo archivo = new Archivo(event.getFile().getContents(), event
				.getFile().getFileName(), event.getFile().getContentType());
		archivoImagen = archivo;
		FacesMessage message = new FacesMessage("Archivo cargado con éxito: ",
				event.getFile().getFileName());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void handleFileUploadDescripciones(FileUploadEvent event) {
		Archivo archivo = new Archivo(event.getFile().getContents(), event
				.getFile().getFileName(), event.getFile().getContentType());
		archivoDescripcion = archivo;
		FacesMessage message = new FacesMessage("Archivo cargado con éxito: ",
				event.getFile().getFileName());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void agregarCoordenada() {
		if (getCoordenada().isEditar()) {
			getCoordenadas().remove(getCoordenada());
		} else {
			getCoordenada().setIndice(indice);
			indice++;
		}

		coordenada.getArchivos().put(CoordenadaGeneral.PREFIX_FILE_DESCRIPCION,
				archivoDescripcion);
		coordenada.getArchivos().put(CoordenadaGeneral.PREFIX_FILE_IMAGE,
				archivoImagen);
		coordenada.setIdTable(getEiaId());
		coordenada.setNombreTabla(CoordenadaGeneral.PREFIX_EIA_ZONAS_SENSIBLES
				+ EstudioImpactoAmbiental.class.getSimpleName());
		getCoordenadas().add(coordenada);
		coordenada = new CoordenadaGeneral();
		archivoDescripcion = null;
		archivoImagen = null;
		RequestContext.getCurrentInstance().addCallbackParam("puntoIn", true);
	}

	public void editarCoordenada(CoordenadaGeneral coordenada) {
		coordenada.setEditar(true);
		archivoDescripcion = coordenada.getArchivos().get(
				CoordenadaGeneral.PREFIX_FILE_DESCRIPCION);
		archivoImagen = coordenada.getArchivos().get(
				CoordenadaGeneral.PREFIX_FILE_IMAGE);
		setCoordenada(coordenada);
	}

	public void eliminarCoordenada(CoordenadaGeneral coordenada) {
		getCoordenadas().remove(coordenada);
		if (coordenada.getId() != null) {
			List<Object> objectos = new ArrayList<Object>();
			objectos.add(CoordenadaGeneral.class);
			objectos.add(coordenada.getId());
			eliminados.add(objectos);
		}

	}

	public String guardar() {
		try {
			caracterizacionReferencialFacade.guardar(getCoordenadas(),
					eliminados);
			JsfUtil.addMessageInfo("Grabado exitoso");
			return JsfUtil.actionNavigateTo("/eia/default.jsf");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al grabar el registro");
			return null;
		}
	}

	public String cancelar() {
		return JsfUtil.actionNavigateTo("/eia/default.jsf");
	}

}
