package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.controllers;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.AgrupacionAutorizacionesFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.DocumentosAgrupacionFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.ObservacionesAgrupacionFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AgrupacionPrincipal;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.DetalleAgrupacion;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.DocumentoAgrupacion;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.ObservacionesAgrupacion;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;


@ManagedBean
@ViewScoped
public class RevisarAgrupacionController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RevisarAgrupacionController.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private AgrupacionAutorizacionesFacade agrupacionAutorizacionesFacade;
	
	@EJB
	private DocumentosAgrupacionFacade documentosFacade;
	
	@EJB
    private ObservacionesAgrupacionFacade observacionesAgrupacionFacade;
	
	@Getter
	@Setter
	private AgrupacionPrincipal agrupacionPrincipal;
	
	@Getter
	@Setter
	private List<DetalleAgrupacion> detalleAgrupacion;
	
	@Getter
	@Setter
	private DocumentoAgrupacion documentoRespaldo;
	
	@Getter
	@Setter
	private Integer idAgrupacion;
	

	@PostConstruct
	private void iniciar() {
		try {
			idAgrupacion = 1; //TODOMG recuperar id de bpm
			
			//TODOMG falta enlaces para visualizacion de informacion
			
			agrupacionPrincipal = agrupacionAutorizacionesFacade.getAgrupacionPorId(idAgrupacion);
			if (agrupacionPrincipal != null) {
				detalleAgrupacion = agrupacionAutorizacionesFacade
						.getDetalleAgrupacionPorIdPrincipal(agrupacionPrincipal.getId());
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos RegistroSnap.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"");
	}
	
	public void uploadFile(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoRespaldo = new DocumentoAgrupacion();
		documentoRespaldo.setId(null);
		documentoRespaldo.setContenidoDocumento(contenidoDocumento);
		documentoRespaldo.setNombre(event.getFile().getFileName());
		documentoRespaldo.setMime("application/pdf");
		documentoRespaldo.setExtension(".pdf");
		documentoRespaldo.setIdTabla(agrupacionPrincipal.getId());
		documentoRespaldo.setNombreClaseTable(AgrupacionPrincipal.class.getSimpleName());
		documentoRespaldo.setIdTipoDocumento(TipoDocumentoSistema.RCOA_AGRUPACION_RESPALDO_OPERADOR.getIdTipoDocumento());
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoRespaldo != null && documentoRespaldo.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoRespaldo.getIdAlfresco(), documentoRespaldo.getFechaCreacion());
			} else if (documentoRespaldo.getContenidoDocumento() != null) {
				documentoContent = documentoRespaldo.getContenidoDocumento();
			}
			
			if (documentoRespaldo != null && documentoRespaldo.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoRespaldo.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void validateRevision(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();

		try {
			if(agrupacionPrincipal.getAgrupacionCorrecta() != null && !agrupacionPrincipal.getAgrupacionCorrecta()) {
						
				List<ObservacionesAgrupacion> observacionesTramite = observacionesAgrupacionFacade.listarPorIdClaseNombreClaseNoCorregidas(
						agrupacionPrincipal.getId(), "agrupacionAutorizacionesAmbientales");
				
				if(observacionesTramite.size() == 0)
					errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Las Observaciones son requeridas.", null));
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					JsfUtil.ERROR_GUARDAR_REGISTRO, null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void enviar() {
		try {
			if(!agrupacionPrincipal.getAgrupacionCorrecta()) {
				if (documentoRespaldo != null && documentoRespaldo.getContenidoDocumento() != null) {
					documentoRespaldo = documentosFacade.guardarDocumento(agrupacionPrincipal.getCodigoProyecto(),
							"AGRUPACION_AUTORIZACIONES", documentoRespaldo, 1);
				}
			} else {
				List<ObservacionesAgrupacion> observacionesTramite = observacionesAgrupacionFacade.listarPorIdClaseNombreClaseNoCorregidas(
						agrupacionPrincipal.getId(), "agrupacionAutorizacionesAmbientales");
				if(observacionesTramite.size() > 0) {
					for (ObservacionesAgrupacion observacion : observacionesTramite) {
						observacion.setEstado(false);
					}
					
					observacionesAgrupacionFacade.guardar(observacionesTramite);
				}
			}
			
			agrupacionAutorizacionesFacade.guardarAgrupacion(agrupacionPrincipal);
			
			//TODOMG avanzar tarea crear parametro apruebaOperador
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
}