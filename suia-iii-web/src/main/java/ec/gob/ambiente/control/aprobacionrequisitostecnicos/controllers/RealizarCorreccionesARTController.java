package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeOficioArtFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.OficioAproReqTec;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

/***
 * 
 * <b> Controlador para tarea recibir probunciamiento de observaciones. </b>
 * 
 * @author carlos.pupo
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: carlos.pupo $, $Date: 28/10/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class RealizarCorreccionesARTController implements Serializable {

	private static final long serialVersionUID = -2132538671526594943L;

	public static final String OFICIO_OBSERVACIONES = "Oficio_Observaciones_Requisitos";
	public static final String TIPO_PDF = ".pdf";

	private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(RealizarCorreccionesARTController.class);

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private InformeOficioArtFacade informeOficioFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	private byte[] oficioDoc;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@PostConstruct
	public void init() {
		try {
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosFacade
					.recuperarCrearAprobacionRequisitosTecnicos(JsfUtil.getCurrentProcessInstanceId(),
							JsfUtil.getLoggedUser());
			OficioAproReqTec oficioArt = this.informeOficioFacade.obtenerOficioAprobacionPorArt(
					TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART, this.aprobacionRequisitosTecnicos.getId(),
					aprobacionRequisitosTecnicosFacade.getCantidadVecesObservadoInformacion(loginBean.getUsuario(),
							bandejaTareasBean.getProcessId()));
			oficioDoc = documentosFacade.descargar(oficioArt.getDocumentoOficio().getIdAlfresco());
		} catch (Exception e) {
			LOGGER.error("Error al inicializar: OficioAprobacionRtBean: ", e);
			JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
		}
	}

	public String siguiente() {
		return JsfUtil.actionNavigateTo("/control/aprobacionRequisitosTecnicos/default.jsf", "tipo=revisar");
	}

	public StreamedContent getStream(byte[] oficioDoc) throws Exception {
		DefaultStreamedContent content = null;
		if (oficioDoc != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(oficioDoc), "application/pdf");
			content.setName(OFICIO_OBSERVACIONES + TIPO_PDF);
		}

		return content;
	}
}
