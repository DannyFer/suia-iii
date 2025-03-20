package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeRevisionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ProyectoTipoViabilidadCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RecibirOficiosViabilidadController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private InformeRevisionForestalFacade informeInspeccionForestalFacade;
	
	@EJB
	private InformeInspeccionBiodiversidadFacade informeInspeccionFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
    private UsuarioFacade usuarioFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoOficio;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoInforme, documentoOficio, documentoInformeManual, documentoOficioManual;
	
	@Getter
	@Setter
	private List<DocumentosViabilidad> listaDocumentos;
	
	@Getter
	@Setter
	private Integer idProyecto, idViabilidad;
	
	@Getter
	@Setter
	private Boolean viabilidadSnapDelegada, viabilidadSnapMae, viabilidadForestal, viabilidadAmbientalFavorable, documentosDescargados, tieneInspeccionForestal;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	private boolean tareaPendiente=false;
	
	@PostConstruct
	private void iniciar() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			
			String idProyectoString=(String)variables.get("idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);
			
			listaDocumentos = new ArrayList<>();
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			List<ViabilidadCoa> viabilidadesProyecto = viabilidadCoaFacade.getViabilidadesPorProyecto(idProyecto);
			
			viabilidadAmbientalFavorable = proyectoLicenciaCoa.getTieneViabilidadFavorable();
			
			tieneInspeccionForestal = false;
			
			for (ViabilidadCoa viabilidadCoa : viabilidadesProyecto) {
				if(viabilidadCoa.getViabilidadCompletada()==null 
						|| !viabilidadCoa.getViabilidadCompletada()) {
					tareaPendiente=true;
					continue;
				}
				
				if(!viabilidadCoa.getEsViabilidadSnap()){ //es forestal
					viabilidadForestal = true;
					
					if(viabilidadCoa.getEsPronunciamientoFavorable()) 
						 tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_FAVORABLE;
					else {
						tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_NO_FAVORABLE;
					}
					
					List<DocumentoViabilidad> documentos = documentosFacade
							.getDocumentoPorTipoTramite(
									viabilidadCoa.getId(), tipoOficio.getIdTipoDocumento());
					if (documentos.size() > 0) {
						DocumentosViabilidad nuevodocumento = new DocumentosViabilidad();
						nuevodocumento.setDocumento(documentos.get(0));
						nuevodocumento.setTipo(7);
						
						listaDocumentos.add(nuevodocumento);
					}
				
					documentos = documentosFacade
							.getDocumentoPorTipoTramite(
									viabilidadCoa.getId(),
									TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_REVISION
											.getIdTipoDocumento());
					if (documentos.size() > 0) {
						DocumentosViabilidad nuevodocumento = new DocumentosViabilidad();
						nuevodocumento.setDocumento(documentos.get(0));
						nuevodocumento.setTipo(8);
						listaDocumentos.add(nuevodocumento);
					}
					
					documentos = documentosFacade
							.getDocumentoPorTipoTramite(
									viabilidadCoa.getId(),
									TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_INSPECCION
											.getIdTipoDocumento());
					if (documentos.size() > 0) {
						DocumentosViabilidad nuevodocumento = new DocumentosViabilidad();
						nuevodocumento.setDocumento(documentos.get(0));
						nuevodocumento.setTipo(9);
						listaDocumentos.add(nuevodocumento);
						
						tieneInspeccionForestal = true;
					}
						
				} else {
					if(viabilidadCoa.getEsAdministracionMae())
						viabilidadSnapMae = true;
					else
						viabilidadSnapDelegada = true;
					
					if(viabilidadCoa.getEsPronunciamientoFavorable())
						 tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_FAVORABLE;
					else {
						tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_NO_FAVORABLE;
					}
					
					List<DocumentoViabilidad> documentos = documentosFacade
							.getDocumentoPorTipoTramite(
									viabilidadCoa.getId(), tipoOficio.getIdTipoDocumento());
					if (documentos.size() > 0) {
						DocumentosViabilidad nuevodocumento = new DocumentosViabilidad();
						nuevodocumento.setDocumento(documentos.get(0));
						nuevodocumento.setTipo(viabilidadCoa.getEsAdministracionMae() ? 4 : 1);
						listaDocumentos.add(nuevodocumento);
					}
				
					documentos = documentosFacade
							.getDocumentoPorTipoTramite(
									viabilidadCoa.getId(),
									TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_INFORME_INSPECCION
											.getIdTipoDocumento());
					if (documentos.size() > 0) {
						DocumentosViabilidad nuevodocumento = new DocumentosViabilidad();
						nuevodocumento.setDocumento(documentos.get(0));
						nuevodocumento.setTipo(viabilidadCoa.getEsAdministracionMae() ? 5 : 2);
						listaDocumentos.add(nuevodocumento);
					}
					
					ProyectoTipoViabilidadCoa proyectoTipoViabilidadCoa = viabilidadCoaFacade.getTipoViabilidadPorProyecto(idProyecto, true);
					documentos = documentosFacade
							.getDocumentoPorTipoViabilidadTramite(
									proyectoTipoViabilidadCoa.getId(),
									TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_FORMULARIO_OPERADOR
											.getIdTipoDocumento());
					if (documentos.size() > 0) {
						DocumentosViabilidad nuevodocumento = new DocumentosViabilidad();
						nuevodocumento.setDocumento(documentos.get(0));
						nuevodocumento.setTipo(viabilidadCoa.getEsAdministracionMae() ? 6 : 3);
						listaDocumentos.add(nuevodocumento);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbiental/recibirOficiosPronunciamiento.jsf");
	}
	
	public StreamedContent descargar(Integer tipoDocumento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			DocumentoViabilidad documento = recuperarDocumento(tipoDocumento);
			
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento.getIdAlfresco(), documento.getFechaCreacion());
			} else if (documento.getContenidoDocumento() != null) {
				documentoContent = documento.getContenidoDocumento();
			}
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public DocumentoViabilidad recuperarDocumento(Integer tipoDocumento) {
		for (DocumentosViabilidad docs : listaDocumentos) {
			if(docs.getTipo().equals(tipoDocumento)) {
				docs.setDescargada(true);
				return docs.getDocumento();
			}
		}
		
		return null;
	}
	
	public void validarDescargaDocumentos() {
		documentosDescargados = true;
		for (DocumentosViabilidad docs : listaDocumentos) {
			if(docs.getTipo().equals(1) && !docs.getDescargada()) {
				documentosDescargados = false;
				break;
			}
			if(docs.getTipo().equals(4) && !docs.getDescargada()) {
				documentosDescargados = false;
				break;
			}
			if(docs.getTipo().equals(7) && !docs.getDescargada()) {
				documentosDescargados = false;
				break;
			}
		}
	}
	
	public void finalizar() {
		try {
			if(tareaPendiente) {
				JsfUtil.addMessageWarning("No se han emitido todos los pronunciamientos. Existe una tarea pendiente.");
				return;
			}
			
			validarDescargaDocumentos();
			if(documentosDescargados == null || !documentosDescargados) {
				JsfUtil.addMessageInfo("Para finalizar debe descargar los oficios de pronunciamiento");
				return;
			}
			
			Map<String, Object> parametros = new HashMap<>();
			
			parametros.put("viabilidadAmbientalFavorable", viabilidadAmbientalFavorable);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			if(!viabilidadAmbientalFavorable) {
				//notificacion viabilidad no favorable
				String nombreOperador = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);
				
				Object[] parametrosCorreoTecnicos = new Object[] {nombreOperador, 
						proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental()};
				String notificacionTecnicos = mensajeNotificacionFacade
						.recuperarValorMensajeNotificacion(
								"bodyNotificacionPronunciamientoViabilidadAmbiental",
								parametrosCorreoTecnicos);
	
				Email.sendEmail(proyectoLicenciaCoa.getUsuario(), "Pronunciamiento Viabilidad Ambiental", notificacionTecnicos, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
}
