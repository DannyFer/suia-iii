package ec.gob.ambiente.campanias.bajalealplastico.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.suia.campanias.bajalealplastico.domain.Informacion;
import ec.gob.ambiente.suia.campanias.bajalealplastico.domain.Institucion;
import ec.gob.ambiente.suia.campanias.bajalealplastico.domain.TipoEntidad;
import ec.gob.ambiente.suia.campanias.bajalealplastico.facade.DesembotellateFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class ListadoRegistrosController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private DesembotellateFacade desembotellateFacade;

	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	@Setter
	private Informacion informacion;

	@Getter
	@Setter
	private UbicacionesGeografica provincia, canton, parroquia;

	@Getter
	private List<UbicacionesGeografica> provinciasList, cantonesList,
			parroquiasList;

	@Getter
	private List<Institucion> institucionList;

	@Getter
	private List<TipoEntidad> entidadList;

	@Getter
	@Setter
	private List<Informacion> listaRegistros;

	@Getter
	@Setter
	private Institucion institucion;

	@Getter
	@Setter
	private TipoEntidad tipoEntidad;

	@Getter
	@Setter
	private Documento documentoLogo;

	@Getter
	@Setter
	private String id;

	@Getter
	@Setter
	private Integer idEntidad;
	
	@Getter
	@Setter
	private String linkReporte="";

	@PostConstruct
	private void init() {
		JsfUtil.getLoggedUser().getNombre();
		
		loginBean.getUsuario();

		listaRegistros = desembotellateFacade.getListaRegistros();
		
		linkReporte = Constantes.getReporteCampaniaBajaleAlPlastico();
	}

	public void verInformacion(Informacion info) {
		informacion = info;

		institucion = informacion.getInstitucion();
		
		documentoLogo = null;
		List<Documento> listaDocumentos = documentosFacade
				.documentoXTablaIdXIdDoc(informacion.getId(),
						"Campaña bájale al plástico",
						TipoDocumentoSistema.ADJUNTO_BAJALE_PLASTICO);
		if (listaDocumentos.size() > 0) {
			documentoLogo = listaDocumentos.get(0);
		}
	}

	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			
			Documento documento = documentoLogo;

			byte[] documentoContent = null;

			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento
						.getIdAlfresco());
			} else if (documento.getContenidoDocumento() != null) {
				documentoContent = documento.getContenidoDocumento();
			}

			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
}
