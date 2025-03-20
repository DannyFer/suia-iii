package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformacionViabilidadLegalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformacionViabilidadLegal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisarInformacionForestalController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RevisarInformacionForestalController.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
    private DocumentosCoaFacade documentoCoaFacade;
	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	
	@EJB
	private	InformacionViabilidadLegalFacade revisionTecnicoJuridicoFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoJuridico;
	
	@Getter
	@Setter
	private InformacionViabilidadLegal informacionViabilidadLegal;
	
	@Getter
	@Setter
	private Boolean datosGuardados, documentoDescargado;
	
	@Getter
	@Setter
	Integer idProyecto, idViabilidad;
	
	private Map<String, Object> variables;

	@PostConstruct
	public void init() {
		try {
			datosGuardados = false;
			documentoDescargado = false;
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			
			String idProyectoString=(String)variables.get("idProyecto");
			
			Boolean conflictoSolventado = false;
			if(variables.get("aprobadoConflictoForestal") != null)
				conflictoSolventado = !Boolean.parseBoolean(variables.get("aprobadoConflictoForestal").toString()) ;
			
			idProyecto = Integer.valueOf(idProyectoString);
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto);
			
			idViabilidad = viabilidadProyecto.getId();
			
			if(conflictoSolventado) {
				informacionViabilidadLegal= revisionTecnicoJuridicoFacade.getInformacionViabilidadLegalPorId(viabilidadProyecto.getId());
			}else {			
				List<DocumentoViabilidad> documentosJuridico = documentosFacade
						.getDocumentoPorTipoTramite(
								viabilidadProyecto.getId(),
								TipoDocumentoSistema.RCOA_VIABILIDAD_ANALISIS_JURIDICO
										.getIdTipoDocumento());
				if (documentosJuridico.size() > 0) {
					documentoJuridico = documentosJuridico.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos Ingreso Forestal.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbiental/forestal/revisarInformacionForestal.jsf");
	}

	public void actualizarExisteConflicto() {
		if(!viabilidadProyecto.getExisteConflictoLegal()){
			viabilidadProyecto.setDetalleConflictoLegal(null);
			documentoJuridico = null;
		}
	}

	public StreamedContent descargar(Integer tipoDocumento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			Integer idTipoDocumento = 0;
			String idDocAlfresco = null;
			String nombreDocumento = null;
			Date fechaCreacion = new Date(); 
			
			switch (tipoDocumento) {
			case 1:				
				CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
				
				List<DocumentosCOA> certificadoInter = documentoCoaFacade.documentoXTablaIdXIdDoc(oficioCI.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO, "CertificadoInterseccionOficioCoa");
				if (certificadoInter.size() > 0) {
					idDocAlfresco = certificadoInter.get(0).getIdAlfresco();
					fechaCreacion = certificadoInter.get(0).getFechaCreacion();
					nombreDocumento = certificadoInter.get(0).getNombreDocumento();
				}
				
				break;
			case 2:
				List<DocumentosCOA> docsMapas = documentoCoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA, "ProyectoLicenciaCoa");
				if (docsMapas.size() > 0) {
					idDocAlfresco = docsMapas.get(0).getIdAlfresco();
					fechaCreacion = docsMapas.get(0).getFechaCreacion();
					nombreDocumento = docsMapas.get(0).getNombreDocumento();
				}
				break;
			case 3:
				List<DocumentosCOA> docsCoordenadas = documentoCoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.RCOA_COORDENADA_IMPLANTACION, "ProyectoLicenciaCoa");
				if (docsCoordenadas.size() > 0) {
					idDocAlfresco = docsCoordenadas.get(0).getIdAlfresco();
					fechaCreacion = docsCoordenadas.get(0).getFechaCreacion();
					nombreDocumento = docsCoordenadas.get(0).getNombreDocumento();
				}
				break;
			case 4:
				idTipoDocumento = TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_ANEXO.getIdTipoDocumento();
				
				List<DocumentoViabilidad> listaDocumentos = documentosFacade
						.getDocumentoPorTipoTramite(viabilidadProyecto.getId(), idTipoDocumento);
				
				if (listaDocumentos.size() > 0) {
					idDocAlfresco = listaDocumentos.get(0).getIdAlfresco();
					fechaCreacion = listaDocumentos.get(0).getFechaCreacion();
					nombreDocumento = listaDocumentos.get(0).getNombre();
				}	
				break;
			default:
				break;
			}
			
			
			byte[] documentoContent = null;
			if (idDocAlfresco != null) {
				documentoContent = documentosFacade.descargar(idDocAlfresco, fechaCreacion);
			} 

			if (nombreDocumento != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent));
				content.setName(nombreDocumento);
				
				if(idTipoDocumento == TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_ANEXO.getIdTipoDocumento()) {
					documentoDescargado = true;
				}
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadFileJuridico(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoJuridico = new DocumentoViabilidad();
		documentoJuridico.setId(null);
		documentoJuridico.setContenidoDocumento(contenidoDocumento);
		documentoJuridico.setNombre(event.getFile().getFileName());
		documentoJuridico.setMime("application/pdf");
		documentoJuridico.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_ANALISIS_JURIDICO.getIdTipoDocumento());
	}
	
	public StreamedContent descargarJuridico(DocumentoViabilidad documentoDescarga) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoDescarga != null && documentoDescarga.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoDescarga.getIdAlfresco(), documentoDescarga.getFechaCreacion());
			} else if (documentoDescarga.getContenidoDocumento() != null) {
				documentoContent = documentoDescarga.getContenidoDocumento();
			}
			
			if (documentoDescarga != null && documentoDescarga.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoDescarga.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void validateDatosRevision(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (!documentoDescargado)
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe descargar la información proporcionada por el operador.", null));
		if(viabilidadProyecto.getExisteConflictoLegal() != null){
			if (viabilidadProyecto.getExisteConflictoLegal() && (documentoJuridico == null || (documentoJuridico.getId() == null && documentoJuridico.getContenidoDocumento() == null)))
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"El campo 'Adjuntar documentos de análisis para jurídico' es requerido.", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void guardarRevision() {
		try {
			viabilidadCoaFacade.guardar(viabilidadProyecto);
			
			if (documentoJuridico != null && documentoJuridico.getContenidoDocumento() != null) {
				documentoJuridico.setIdViabilidad(viabilidadProyecto.getId());
				documentoJuridico = documentosFacade.guardarDocumento(
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoJuridico, 2);
				
				if(documentoJuridico == null || documentoJuridico.getId() == null) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
					return;
				}
			}
			
			datosGuardados = true;
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
		}
	}

	public void finalizar() {
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("existeConflictoLegalForestal", viabilidadProyecto.getExisteConflictoLegal());
			
			if(viabilidadProyecto.getRequiereInspeccionTecnica() != null)
				parametros.put("requiereInspeccionForestal", viabilidadProyecto.getRequiereInspeccionTecnica());
			
			if(viabilidadProyecto.getExisteConflictoLegal()) {
				Area areaJuridico = proyectoLicenciaCoa.getAreaResponsable().getArea()!=null?proyectoLicenciaCoa.getAreaResponsable().getArea():proyectoLicenciaCoa.getAreaResponsable(); //el juridico se toma de las zonales observacion QA
				if(areaJuridico.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
					areaJuridico = areaFacade.getAreaPorAreaAbreviacion("DNPCA");
				} 
				String rolJuridico = Constantes.getRoleAreaName("role.tecnico.Juridico");
				List<Usuario> usuarioJuridico = usuarioFacade.buscarUsuariosPorRolYAreaEspecifica(rolJuridico, areaJuridico); //busca usuario solo de zonales no de OT
				
				if(usuarioJuridico==null || usuarioJuridico.isEmpty()){
					LOG.error("No se encontro usuario " + rolJuridico + " en " + areaJuridico);
					//JsfUtil.addMessageError("No se encontro usuario " + rolJuridico + " en " + areaJuridico);
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
					return;
				}
				
				String juridicoBpm =  (String) variables.get("abogadoForestal");
				
				Usuario analistaJuridico = usuarioJuridico.get(0); 
				if (juridicoBpm == null || !juridicoBpm.equals(analistaJuridico.getNombre())) {
					parametros.put("abogadoForestal", analistaJuridico.getNombre());
				}
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	//para cuando regreso de la revision legal
	public void validarTareaBpmDespuesLegal() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbiental/forestal/revisarInformacionForestalLegal.jsf");
	}
	
	public void guardarRevisionLegal() {
		try {
			viabilidadCoaFacade.guardar(viabilidadProyecto);
			
			datosGuardados = true;
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
		}
	}

	public void finalizarLegal() {
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("requiereInspeccionForestal", viabilidadProyecto.getRequiereInspeccionTecnica());
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
}
