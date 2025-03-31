package ec.gob.ambiente.rcoa.viabilidadAmbientalBypass.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ProyectoTipoViabilidadCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class IngresarInfoViabilidadController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(IngresarInfoViabilidadController.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
    private ProcesoFacade procesoFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private DocumentosFacade documentosRcoaFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private ProyectoTipoViabilidadCoa proyectoTipoViabilidadCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoRespuestas;

	@Getter
	@Setter
	private Boolean proyectoRetroceso, esForestal, guiaDescargada;
	
	@Getter
	@Setter
	private String cabeceraPanel, infoPanel;
	
	@Getter
	@Setter
	private Integer idTipoDocumento;
	
	@Getter
	@Setter
	Integer idProyecto;
	
	private Map<String, Object> variables;


	@PostConstruct
	private void iniciar() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

			idProyecto = Integer.valueOf(variables.get("idProyecto").toString()); 
			
			esForestal = false;
			cabeceraPanel = "Obtención de la viabilidad ambiental en Sistema Nacional de Áreas Protegidas";
			String nombreTarea = viabilidadCoaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			if(nombreTarea.equals("ingresarDocForestal")) {
				esForestal = true;
				cabeceraPanel = "Obtención de la viabilidad ambiental en Patrimonio Forestal Nacional";
			}
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			proyectoTipoViabilidadCoa = viabilidadCoaFacade.getTipoViabilidadPorProyecto(idProyecto, !esForestal);
			
			if(proyectoTipoViabilidadCoa == null) {
				proyectoTipoViabilidadCoa = new ProyectoTipoViabilidadCoa();
				proyectoTipoViabilidadCoa.setIdProyectoLicencia(idProyecto);
				proyectoTipoViabilidadCoa.setEsViabilidadSnap(!esForestal);
				viabilidadCoaFacade.guardarProyectoTipoViabilidadCoa(proyectoTipoViabilidadCoa);
			}
			
			idTipoDocumento = (esForestal) ? TipoDocumentoSistema.RCOA_VIABILIDAD_GUIA_OPERADOR_FORESTAL.getIdTipoDocumento() : TipoDocumentoSistema.RCOA_VIABILIDAD_GUIA_OPERADOR_SNAP.getIdTipoDocumento();
	
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoViabilidadTramite(
							proyectoTipoViabilidadCoa.getId(),idTipoDocumento);
			if (documentos.size() > 0) {
				documentoRespuestas = documentos.get(0);
			}
			
			guiaDescargada = false;
			proyectoRetroceso = false;
			
			infoPanel = "Estimado operador, en cumplimiento de la Disposición General Décimo Quinta del Reglamento al Código Orgánico del Ambiente, \"Los procesos, trámites y requerimientos contemplados en el presente reglamento que no se encuentren habilitados en el Sistema Único de Información Ambiental podrán presentarse en formato físico\"; en tal virtud, el trámite para la obtención del Informe de Viabilidad Ambiental será gestionado en formato físico, por lo cual se solicita ingresar la documentación requerida.";
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos RegistroSnap.");
		}
	}
	
	public void validarTareaBpm() {
		String url = (esForestal) ? "/pages/rcoa/viabilidadAmbientalBypass/ingresarForestal.jsf" : "/pages/rcoa/viabilidadAmbientalBypass/ingresarAreasProtegidas.jsf";
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(), url);
	}
	
	public StreamedContent descargarGuia() {
        try {
        	DefaultStreamedContent content = null;
        	
        	String nombreGuia = (esForestal) ? "DF RCOA ANEXO I VIABILIDAD AMBIENTAL BYPASS PFN.pdf" : "DF RCOA ANEXO I VIABILIDAD AMBIENTAL BYPASS SNAP.pdf";
        	String nombreDoc = (esForestal) ? "Anexo 01 – Informe de factibilidad ambiental para obtener la viabilidad ambiental de proyectos, obras o actividades dentro del Patrimonio Forestal Nacional.pdf" : "Anexo 02 - Información complementaria del proponente para el análisis de viabilidad ambiental de un proyecto, obra o actividad en el sistema nacional de áreas protegidas.pdf";
        	
            byte[] documentoGuia = documentosRcoaFacade.descargarDocumentoPorNombre(nombreGuia);
            if (documentoGuia != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documentoGuia));
				content.setName(nombreDoc);
				content.setContentType("application/pdf");
				guiaDescargada = true;
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
	
	public void validateDatosIngreso(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(esForestal && proyectoRetroceso) {
			//boton deshabilitado
		} else if(!guiaDescargada) {
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"La descarga del Formato o guía para obtener el informe de viabilidad ambiental es requerida.", null));
		}
		
		if (documentoRespuestas == null || (documentoRespuestas.getId() == null && documentoRespuestas.getContenidoDocumento() == null))
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Adjuntar documento para obtener el informe de viabilidad ambiental' es requerido.", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void uploadDocumento(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoRespuestas = new DocumentoViabilidad();
		documentoRespuestas.setId(null);
		documentoRespuestas.setContenidoDocumento(contenidoDocumento);
		documentoRespuestas.setNombre(event.getFile().getFileName());
		documentoRespuestas.setMime("application/pdf");
		documentoRespuestas.setIdTipoDocumento(idTipoDocumento);
	}
	
	public StreamedContent descargar(DocumentoViabilidad documentoDescarga) throws IOException {
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

	public void enviar() {
		try {
			
			if (documentoRespuestas.getContenidoDocumento() != null) {
				documentoRespuestas.setIdProyectoViabilidad(proyectoTipoViabilidadCoa.getId());
				documentoRespuestas = documentosFacade.guardarDocumentoProceso(
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoRespuestas, 1, JsfUtil.getCurrentProcessInstanceId());
				
				if(documentoRespuestas == null || documentoRespuestas.getId() == null) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
					return;
				}
			}

			Map<String, Object> parametros = new HashMap<>();
			
			Area areaResponsable = null;
			String tipoRol = null;
			
			if(esForestal) {
				areaResponsable = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES);
				tipoRol = "role.va.bypass.tecnico.forestal";
			} else {
				areaResponsable = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS);
				tipoRol = "role.va.bypass.tecnico.snap";
				
				ProyectoLicenciaCoaUbicacion ubicacionProyecto = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa);
				UbicacionesGeografica ubicacionPrincipal = ubicacionProyecto.getUbicacionesGeografica();
				String codeProvincia = ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec();
				if(codeProvincia.equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
					areaResponsable = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
				}
			}

			String rolTecnico = Constantes.getRoleAreaName(tipoRol);
			
			List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
			if (listaUsuario == null || listaUsuario.size() == 0) {
				System.out.println("No se encontró usuario " + rolTecnico + " en "+ areaResponsable.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return;
			}
			
			Usuario tecnicoResponsable = null;
			List<Usuario> listaTecnicosResponsables = asignarTareaFacade
					.getCargaLaboralPorUsuariosProceso(listaUsuario, Constantes.RCOA_PROCESO_VIABILIDAD_BYPASS);
			tecnicoResponsable = listaTecnicosResponsables.get(0);
			
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadPorTipoProyecto(proyectoTipoViabilidadCoa.getId());
			
			if (viabilidadProyecto == null) {
				viabilidadProyecto = new ViabilidadCoa();
				viabilidadProyecto.setIdProyectoLicencia(idProyecto);
				viabilidadProyecto.setIdProyectoTipoViabilidad(proyectoTipoViabilidadCoa.getId());
				
				viabilidadProyecto.setAreaResponsable(areaResponsable);
				viabilidadProyecto.setViabilidadCompletada(false);
				viabilidadProyecto.setTipoFlujoViabilidad(ViabilidadCoa.flujoBypass);
			} else {
				viabilidadProyecto.setAreaResponsable(areaResponsable);
			}
			
			if(esForestal) {
				parametros.put("tecnicoForestal",tecnicoResponsable.getNombre());
				
				viabilidadProyecto.setEsViabilidadSnap(false);
			} else {
				parametros.put("tecnicoSnap",tecnicoResponsable.getNombre());
				
				viabilidadProyecto.setEsViabilidadSnap(true);
			}
			
			viabilidadCoaFacade.guardar(viabilidadProyecto);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
}
