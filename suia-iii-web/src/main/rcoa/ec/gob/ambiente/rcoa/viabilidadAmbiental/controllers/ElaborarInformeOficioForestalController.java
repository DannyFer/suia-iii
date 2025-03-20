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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeTecnicoForestal;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarInformeOficioForestalController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(ElaborarInformeOficioForestalController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{informeOficioViabilidadForestalBean}")
	@Getter
	@Setter
	private InformeOficioViabilidadForestalBean informeOficioViabilidadForestalBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private DocumentosFacade documentosGeneralFacade;
	
	@Getter
	@Setter
	private InformeTecnicoForestal informeTecnicoForestal;

	@Getter
	@Setter
	private Boolean mostrarInforme, esReporteAprobacion, informeGuardado, oficioGuardado, validarRequeridos, pronunciamientoSeleccionado;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte, nombreProyecto;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		
		mostrarInforme = true;
		informeGuardado = false;
		oficioGuardado = false;
		validarRequeridos = false;
		pronunciamientoSeleccionado = false;
		
		informeOficioViabilidadForestalBean.generarInforme(true);
		
		urlReporte = informeOficioViabilidadForestalBean.getInformeRevision().getInformePath();
		nombreReporte = informeOficioViabilidadForestalBean.getInformeRevision().getNombreReporte();
		archivoReporte = informeOficioViabilidadForestalBean.getInformeRevision().getArchivoInforme();
		
		if(informeOficioViabilidadForestalBean.getInformeRevision().getId() != null 
				&& informeOficioViabilidadForestalBean.getInformeRevision().getEsPronunciamientoFavorable() != null)
			pronunciamientoSeleccionado = true;
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbiental/forestal/elaborarInformeOficioForestal.jsf");
	}
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}
	
	public StreamedContent descargarInforme() throws IOException {
		DefaultStreamedContent content = null;
		try {
			DocumentoViabilidad documentoDescarga = null;
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(informeOficioViabilidadForestalBean.getViabilidadProyecto().getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_INSPECCION.getIdTipoDocumento());
			if (documentos.size() > 0) {
				documentoDescarga = documentos.get(0);
			}	
			
			byte[] documentoContent = null;

			if (documentoDescarga != null && documentoDescarga.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(
						documentoDescarga.getIdAlfresco(),
						documentoDescarga.getFechaCreacion());
			} 

			if (documentoDescarga != null
					&& documentoDescarga.getNombre() != null
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
	
	public void validateDatosIngreso(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(informeOficioViabilidadForestalBean.getInformeRevision().getNombreTecnico() == null || 
				informeOficioViabilidadForestalBean.getInformeRevision().getNombreTecnico().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Nombres y Apellidos' es requerido", null));
		
		if(informeOficioViabilidadForestalBean.getInformeRevision().getCargoTecnico() == null || 
				informeOficioViabilidadForestalBean.getInformeRevision().getCargoTecnico().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Cargo' es requerido", null));
		
		if(informeOficioViabilidadForestalBean.getInformeRevision().getAreaTecnico() == null || 
				informeOficioViabilidadForestalBean.getInformeRevision().getAreaTecnico().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Área' es requerido", null));
		
		if(informeOficioViabilidadForestalBean.getInformeRevision().getAntecedentes() == null || 
				informeOficioViabilidadForestalBean.getInformeRevision().getAntecedentes().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Antecedentes' es requerido", null));
		
		if(informeOficioViabilidadForestalBean.getInformeRevision().getObjetivo() == null || 
				informeOficioViabilidadForestalBean.getInformeRevision().getObjetivo().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Objetivo' es requerido", null));
		
		if(informeOficioViabilidadForestalBean.getInformeRevision().getMarcoLegal()== null || 
				informeOficioViabilidadForestalBean.getInformeRevision().getMarcoLegal().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Marco Legal Aplicable' es requerido", null));
		
		if(informeOficioViabilidadForestalBean.getInformeRevision().getResultadosRevision()== null || 
				informeOficioViabilidadForestalBean.getInformeRevision().getResultadosRevision().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Resultados de Revisión' es requerido", null));
		
		if(informeOficioViabilidadForestalBean.getInformeRevision().getConclusiones() == null || 
				informeOficioViabilidadForestalBean.getInformeRevision().getConclusiones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Conclusiones' es requerido", null));
		
		if(informeOficioViabilidadForestalBean.getInformeRevision().getRecomendaciones()== null || 
				informeOficioViabilidadForestalBean.getInformeRevision().getRecomendaciones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Recomendaciones' es requerido", null));

		if(context.getMessageList().size() > 0 || errorMessages.size() > 0)
			informeGuardado = false;
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void guardarInforme() {
		if(!pronunciamientoSeleccionado)
			informeOficioViabilidadForestalBean.getInformeRevision().setEsPronunciamientoFavorable(null);
		
		informeOficioViabilidadForestalBean.guardarInforme();
				
		informeOficioViabilidadForestalBean.generarInforme(true);
		
		urlReporte = informeOficioViabilidadForestalBean.getInformeRevision().getInformePath();
		nombreReporte = informeOficioViabilidadForestalBean.getInformeRevision().getNombreReporte();
		archivoReporte = informeOficioViabilidadForestalBean.getInformeRevision().getArchivoInforme();
		
		validarRequeridos = true;
		informeGuardado = true;
		
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}
	
	public void aceptarInforme() {
		
		informeOficioViabilidadForestalBean.getInformeRevision().setFechaElaboracion(new Date());
		informeOficioViabilidadForestalBean.guardarInforme();
		
		informeOficioViabilidadForestalBean.generarOficio(true);
		
		urlReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getOficioPath();
		nombreReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getNombreOficio();
		archivoReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getArchivoOficio();
		
		mostrarInforme = false;
	}
	
	public void guardarRegresar() {
		informeOficioViabilidadForestalBean.generarInforme(true);
		
		urlReporte = informeOficioViabilidadForestalBean.getInformeRevision().getInformePath();
		nombreReporte = informeOficioViabilidadForestalBean.getInformeRevision().getNombreReporte();
		archivoReporte = informeOficioViabilidadForestalBean.getInformeRevision().getArchivoInforme();
		
		mostrarInforme = true;
	}
	
	public void validateDatosIngresoOficio(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(informeOficioViabilidadForestalBean.getOficioPronunciamiento().getAntecedentes() == null || 
				informeOficioViabilidadForestalBean.getOficioPronunciamiento().getAntecedentes().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Antecedentes' es requerido", null));
		
		if(informeOficioViabilidadForestalBean.getOficioPronunciamiento().getMarcoLegal()== null || 
				informeOficioViabilidadForestalBean.getOficioPronunciamiento().getMarcoLegal().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Marco Legal Aplicable' es requerido", null));
		
		if(informeOficioViabilidadForestalBean.getOficioPronunciamiento().getConclusiones() == null || 
				informeOficioViabilidadForestalBean.getOficioPronunciamiento().getConclusiones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Conclusiones' es requerido", null));
		
		if(informeOficioViabilidadForestalBean.getOficioPronunciamiento().getRecomendaciones()== null || 
				informeOficioViabilidadForestalBean.getOficioPronunciamiento().getRecomendaciones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Recomendaciones' es requerido", null));
		
		if(informeOficioViabilidadForestalBean.getOficioPronunciamiento().getPronunciamiento()== null || 
				informeOficioViabilidadForestalBean.getOficioPronunciamiento().getPronunciamiento().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Pronunciamiento' es requerido", null));

		if(context.getMessageList().size() > 0 || errorMessages.size() > 0)
			oficioGuardado = false;
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void guardarOficio() {
		informeOficioViabilidadForestalBean.guardarInforme();
		informeOficioViabilidadForestalBean.guardarOficio();

		informeOficioViabilidadForestalBean.generarOficio(true);
		
		urlReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getOficioPath();
		nombreReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getNombreOficio();
		archivoReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getArchivoOficio();
		
		oficioGuardado = true;
		
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}

	public void aceptar() {
		try {
			informeOficioViabilidadForestalBean.guardarOficio();
			
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			
			Map<String, Object> parametros = new HashMap<>();
			
			Area areaResponsable = informeOficioViabilidadForestalBean.getViabilidadProyecto().getAreaResponsable();
			String tipoRol = "role.va.cz.coordinador.forestal";
			
			if(areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC))
				tipoRol = "role.va.pc.coordinador.forestal";
			
			String rolCoordinador = Constantes.getRoleAreaName(tipoRol);
			List<Usuario> usuarioCoordinador = usuarioFacade.buscarUsuariosPorRolYArea(rolCoordinador, areaResponsable);
			
			if(usuarioCoordinador==null || usuarioCoordinador.isEmpty()){
				LOG.error("No se encontro usuario " + rolCoordinador + " en " + areaResponsable);
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			Usuario coordinador = usuarioCoordinador.get(0);
			String coordinadorBpm =  (String) variables.get("coordinadorForestal");
			if (coordinadorBpm == null || !coordinadorBpm.equals(coordinador.getNombre())) {
				parametros.put("coordinadorForestal", coordinador.getNombre());
			}
			
			try {
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
	
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public StreamedContent descargarFormatoOficio() {
        try {
        	String nombreFormato = "Rcoa_formato_pronunciamiento_no_favorable_forestal.doc";
        	
        	if(informeOficioViabilidadForestalBean.getInformeRevision().getEsPronunciamientoFavorable())
        		 nombreFormato = "Rcoa_formato_pronunciamiento_favorable_forestal.doc";
        	

        	DefaultStreamedContent content = null;
            byte[] documentoGuia = documentosGeneralFacade.descargarDocumentoPorNombre(nombreFormato);
            if (documentoGuia != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documentoGuia));
				content.setName("FormatoPronunciamiento.doc");
				return content;
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
        } catch (Exception e) {
            JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
        }
    }
	
	public void seleccionarTipoPronunciamiento() {
		pronunciamientoSeleccionado = true;
	}
	
}
