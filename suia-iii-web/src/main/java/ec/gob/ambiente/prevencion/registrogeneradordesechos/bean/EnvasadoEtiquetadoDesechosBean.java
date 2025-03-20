package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.controller.RegistroGeneradorDesechoController;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligrosoEtiquetado;
import ec.gob.ambiente.suia.domain.TipoEnvase;
import ec.gob.ambiente.suia.domain.TipoEtiquetado;
import ec.gob.ambiente.suia.domain.UnidadMedida;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class EnvasadoEtiquetadoDesechosBean extends RegistroGeneradorBaseBean {

	private static final long serialVersionUID = -8753923283034063157L;
	
	private static final Logger LOG = Logger.getLogger(VerRegistroGeneradorDesechoBean.class);

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	private GeneradorDesechosDesechoPeligrosoEtiquetado desechoPeligrosoEtiquetado;

	@Setter
	private List<GeneradorDesechosDesechoPeligrosoEtiquetado> desechosPeligrososEtiquetados;

	@Getter
	private List<TipoEtiquetado> tiposEtiquetados;

	@Getter
	private List<TipoEnvase> tiposEnvases;

	@Getter
	private List<UnidadMedida> unidadesMedidas;

	private byte[] plantillaEtiqueta;

	@PostConstruct
	private void initEtiquetadoEnvase() {
		tiposEtiquetados = registroGeneradorDesechosFacade.getTiposEtiquetados();
		tiposEnvases = registroGeneradorDesechosFacade.getTiposEnvases();
		unidadesMedidas = Arrays.asList(registroGeneradorDesechosFacade.getUnidadMedidaTonelada(),
				registroGeneradorDesechosFacade.getUnidadMedidaUnidad());

/*		try {
			plantillaEtiqueta = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(Constantes.PLANTILLA_ETIQUETA, null);
		} catch (Exception e) {
			LOG.error("Error al descargar Plantilla");
		}*/
	}

	public void aceptar() {
		if (!getDesechosPeligrososEtiquetados().contains(desechoPeligrosoEtiquetado))
			getDesechosPeligrososEtiquetados().add(desechoPeligrosoEtiquetado);

		/**
		 * autor:vear
		 * fecha: 25/02/2016
		 * objetivo: persistir solo el paso 6 del wizard, como objeto no como lista
		 */
		try {
			JsfUtil.getBean(RegistroGeneradorDesechoController.class).guardarPaso6(desechoPeligrosoEtiquetado);
		} catch (CmisAlfrescoException e) {
			e.printStackTrace();
		}
		////////////////////////////
		cancelar();
		JsfUtil.addCallbackParam("addEnvasadoEtiquetado");
	}

	public void cancelar() {
		desechoPeligrosoEtiquetado = null;
	}

	public void editar(GeneradorDesechosDesechoPeligrosoEtiquetado desechoEtiquetado) {
		this.desechoPeligrosoEtiquetado = desechoEtiquetado;
		setEditar(true);
		JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);
	}

	public void eliminar(GeneradorDesechosDesechoPeligrosoEtiquetado desechoEtiquetado) {
		getDesechosPeligrososEtiquetados().remove(desechoEtiquetado);
		JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);

		JsfUtil.getBean(RegistroGeneradorDesechoController.class).eliminarDesechoEtiquetado(desechoEtiquetado);

	}

	public void updateDesechoPeligroso() {
		if (!desechoPeligrosoEtiquetado.getDesechoPeligroso().isDesechoES_04()
				&& !desechoPeligrosoEtiquetado.getDesechoPeligroso().isDesechoES_06()) {
			desechoPeligrosoEtiquetado.setUnidadMedida(registroGeneradorDesechosFacade
					.getUnidadMedidaTonelada(unidadesMedidas));
		}
	}

	public void uploadListenerModeloEtiqueta(FileUploadEvent event) {
		Documento documento = super.uploadListener(event, GeneradorDesechosDesechoPeligrosoEtiquetado.class);
		getDesechoPeligrosoEtiquetado().setModeloEtiqueta(documento);
	}

	public List<DesechoPeligroso> getDesechosPeligrosoDisponibles() {
		List<DesechoPeligroso> result = new ArrayList<>();
		List<DesechoPeligroso> selected = JsfUtil.getBean(AdicionarDesechosPeligrososBean.class)
				.getDesechosSeleccionados();
		result.addAll(selected);
		for (GeneradorDesechosDesechoPeligrosoEtiquetado desechoPeligrosoEtiquetado : getDesechosPeligrososEtiquetados()) {
			if (isEditar() && desechoPeligrosoEtiquetado.equals(this.desechoPeligrosoEtiquetado))
				continue;
			if (desechoPeligrosoEtiquetado.getDesechoPeligroso() != null)
				result.remove(desechoPeligrosoEtiquetado.getDesechoPeligroso());
		}
		return result;
	}

	public GeneradorDesechosDesechoPeligrosoEtiquetado getDesechoPeligrosoEtiquetado() {
		return desechoPeligrosoEtiquetado == null ? desechoPeligrosoEtiquetado = new GeneradorDesechosDesechoPeligrosoEtiquetado()
				: desechoPeligrosoEtiquetado;
	}

	public List<GeneradorDesechosDesechoPeligrosoEtiquetado> getDesechosPeligrososEtiquetados() {
		return desechosPeligrososEtiquetados == null ? desechosPeligrososEtiquetados = new ArrayList<>()
				: desechosPeligrososEtiquetados;
	}

	public StreamedContent getPlantillaEtiqueta() throws Exception {
		try {
			plantillaEtiqueta = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(Constantes.PLANTILLA_ETIQUETA, null);
		} catch (Exception e) {
			LOG.error("Error al descargar Plantilla");
		}
		DefaultStreamedContent content = null;
		try {
			if (plantillaEtiqueta != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaEtiqueta));
				content.setName(Constantes.PLANTILLA_ETIQUETA);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public StreamedContent getDocumento() throws Exception {
		try {
			plantillaEtiqueta = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(Constantes.AYUDA_GENERACION_DESECHOS_ESPECIALES_O_PELIGROSOS, null);
		} catch (Exception e) {
			LOG.error("Error al descargar Plantilla");
		}
		DefaultStreamedContent content = null;
		try {
			if (plantillaEtiqueta != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaEtiqueta));
				content.setName(Constantes.AYUDA_GENERACION_DESECHOS_ESPECIALES_O_PELIGROSOS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public void validateData(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (desechoPeligrosoEtiquetado.getModeloEtiqueta() == null)
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe adjuntar el modelo de la etiqueta.",
					null));
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void validateDesechosEtiquetados(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (getDesechosPeligrososEtiquetados().isEmpty()
				|| getDesechosPeligrososEtiquetados().size() < JsfUtil.getBean(AdicionarDesechosPeligrososBean.class)
						.getDesechosSeleccionados().size())
			errorMessages.add(
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe completar, para todos los desechos seleccionados, los datos asociados al envasado y etiquetado.",
							null));
		
		if (getDesechosPeligrososEtiquetados().size() > 0) {
        	for (GeneradorDesechosDesechoPeligrosoEtiquetado desechoEtiquetado : JsfUtil.getBean(EnvasadoEtiquetadoDesechosBean.class).getDesechosPeligrososEtiquetados()) {
				if(desechoEtiquetado.getModeloEtiqueta() == null) {
					errorMessages.add(
	    					new FacesMessage(
	    							FacesMessage.SEVERITY_ERROR,
	    							"Debe adjuntar el modelo de la etiqueta para el desecho " + desechoEtiquetado.getDesechoPeligroso().getClave() + ".",
	    							null));
				}
			}
    	}

		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void validacionesDesechos(DesechoPeligroso desechoPeligroso) {
		GeneradorDesechosDesechoPeligrosoEtiquetado desechoEtiquetadoToDelete = null;
		for (GeneradorDesechosDesechoPeligrosoEtiquetado desechoEtiquetado : getDesechosPeligrososEtiquetados()) {
			if (desechoEtiquetado.getDesechoPeligroso().equals(desechoPeligroso)) {
				desechoEtiquetadoToDelete = desechoEtiquetado;
				break;
			}
		}
		if (desechoEtiquetadoToDelete != null)
			getDesechosPeligrososEtiquetados().remove(desechoEtiquetadoToDelete);
	}
}