package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class RevisarInformacionViabilidadPfnController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RevisarInformacionViabilidadPfnController.class);
	
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
	private AreaFacade areaFacade;
	@EJB
	private BandejaFacade bandejaFacade;
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionfacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private List<DocumentoViabilidad> listaDocumentosAdjuntos;
	
	@Getter
	@Setter
	private List<Area> listaAutoridadAmbientalMaae;
	
	@Getter
	@Setter
	private ArrayList<Integer> listaDocumentosDescargados;
	
	@Getter
	@Setter
	private Boolean documentoDescargado, variasZonales, esTecnicoApoyo, infoGuardada;
	
	@Getter
	@Setter
	Integer idProyecto, numeroRevision;
	
	private Map<String, Object> variables;

	@PostConstruct
	public void init() {
		try {
			documentoDescargado = false;
			esTecnicoApoyo = false;
			infoGuardada = false;
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			
			String idProyectoString=(String)variables.get("idProyecto");
			
			idProyecto = Integer.valueOf(idProyectoString);
			
			numeroRevision = variables.containsKey("numeroRevisionInformacion") ? (Integer.valueOf((String) variables.get("numeroRevisionInformacion"))) : 0;
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto);
			
			variasZonales = (viabilidadProyecto.getNroZonalesInterseccion() > 1) ? true : false;
			
			if(variasZonales) {
				recuperarOficinasApoyo();
			} else {
				listaAutoridadAmbientalMaae = new ArrayList<Area>();
			}
			
			String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			if (tarea.equals("revisarInformacionApoyoForestal")) {
				esTecnicoApoyo = true;
			}
			
			listaDocumentosDescargados = new ArrayList<Integer>();
			
			listaDocumentosAdjuntos = documentosFacade.getDocumentoPorTipoTramite(viabilidadProyecto.getId(), TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_ADJUNTO_GENERAL_PROYECTO.getIdTipoDocumento());
			List<DocumentoViabilidad> listaDocumentosAnexos = documentosFacade.getDocumentoPorTipoTramite(viabilidadProyecto.getId(), TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_ANEXOS_INFORME_FACTIBILIDAD.getIdTipoDocumento());
			listaDocumentosAdjuntos.addAll(listaDocumentosAnexos);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos Ingreso Forestal.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalForestal/revisarInformacion.jsf");
	}
	
	public void validarTareaApoyoBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalForestal/revisarInformacionApoyo.jsf");
	}
	
	public void recuperarOficinasApoyo() throws ServiceException {
		List<Area> listaAreasMaae = areaFacade.getAutoridadAmbientalMaae();
		
		ArrayList<Integer> listaCantones = new ArrayList<Integer>();
		List<UbicacionesGeografica> ubicacionesProyecto = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoLicenciaCoa);
		for (UbicacionesGeografica ubi : ubicacionesProyecto) {
			listaCantones.add(ubi.getUbicacionesGeografica().getUbicacionesGeografica() == null ? 0 
					: ubi.getUbicacionesGeografica().getId());
		}
		
		Set<Integer> setCantones = new HashSet<Integer>(listaCantones);
		
		ArrayList<Integer> listaIdOficinasTecnicas = new ArrayList<Integer>();
		for (int id : setCantones) {
			UbicacionesGeografica ubicacion = ubicacionfacade.buscarPorId(id);
			if (ubicacion.getAreaCoordinacionZonal() != null)
				listaIdOficinasTecnicas.add(ubicacion.getAreaCoordinacionZonal().getId());
		}

		Set<Integer> setOficinasTecnicas = new HashSet<Integer>(listaIdOficinasTecnicas);
		
		listaAutoridadAmbientalMaae = new ArrayList<Area>();
		
		for (Area area : listaAreasMaae) {
			if(setOficinasTecnicas.contains(area.getId()) 
					&& !viabilidadProyecto.getAreaResponsable().getId().equals(area.getId())) {
				listaAutoridadAmbientalMaae.add(area);
			}
		}
	}

	public StreamedContent descargar(Integer tipoDocumento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			Integer idTipoDocumento = 0;
			String idDocAlfresco = null;
			String nombreDocumento = null;
			
			switch (tipoDocumento) {
			case 1:				
				CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
				
				List<DocumentosCOA> certificadoInter = documentoCoaFacade.documentoXTablaIdXIdDoc(oficioCI.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO, "CertificadoInterseccionOficioCoa");
				if (certificadoInter.size() > 0) {
					idDocAlfresco = certificadoInter.get(0).getIdAlfresco();
					nombreDocumento = certificadoInter.get(0).getNombreDocumento();
				}
				
				break;
			case 2:
				List<DocumentosCOA> docsMapas = documentoCoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA, "ProyectoLicenciaCoa");
				if (docsMapas.size() > 0) {
					idDocAlfresco = docsMapas.get(0).getIdAlfresco();
					nombreDocumento = docsMapas.get(0).getNombreDocumento();
				}
				break;
			case 3:
				List<DocumentosCOA> docsCoordenadas = documentoCoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.RCOA_COORDENADA_IMPLANTACION, "ProyectoLicenciaCoa");
				if (docsCoordenadas.size() > 0) {
					idDocAlfresco = docsCoordenadas.get(0).getIdAlfresco();
					nombreDocumento = docsCoordenadas.get(0).getNombreDocumento();
				}
				break;
			case 4:
				idTipoDocumento = TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_FACTIBILIDAD.getIdTipoDocumento();
				
				List<DocumentoViabilidad> listaDocumentos = documentosFacade
						.getDocumentoPorTipoTramite(viabilidadProyecto.getId(), idTipoDocumento);
				
				if (listaDocumentos.size() > 0) {
					idDocAlfresco = listaDocumentos.get(0).getIdAlfresco();
					nombreDocumento = listaDocumentos.get(0).getNombre();
				}	
				break;
			default:
				break;
			}
			
			
			byte[] documentoContent = null;
			if (idDocAlfresco != null) {
				documentoContent = documentosFacade.descargar(idDocAlfresco);
			} 

			if (nombreDocumento != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent));
				content.setName(nombreDocumento);
				
				listaDocumentosDescargados.add(tipoDocumento);
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public StreamedContent descargarAnexo(DocumentoViabilidad documento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
			} else if (documento.getContenidoDocumento() != null) {
				documentoContent = documento.getContenidoDocumento();
			}
			
			if (documento != null && documento.getNombre() != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
				
				return content;
			} else {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return null;
	}
	
	public void guardarRevision() {
		try {
			viabilidadCoaFacade.guardar(viabilidadProyecto);
			
			infoGuardada = true;
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
			
		} catch (Exception e) {
			infoGuardada = false;
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void finalizar() {
		try {
			
			Set<Integer> setDocumentosDescargados = new HashSet<Integer>(listaDocumentosDescargados);
			
			if(setDocumentosDescargados.size() < 4) {
				JsfUtil.addMessageError("Debe descargar todos los documentos para poder finalizar la tarea.");
				return;
			}
			
			Map<String, Object> parametros = new HashMap<>();
			
			if(variasZonales) {
				if(viabilidadProyecto.getRequiereApoyo() != null) {
					parametros.put("requiereApoyo", viabilidadProyecto.getRequiereApoyo());
					
					if(viabilidadProyecto.getRequiereApoyo()) {
						//buscar tecnico
						String tipoRol = "role.va.pfn.tecnico.forestal.apoyo";
						String rolTecnico = Constantes.getRoleAreaName(tipoRol);

						List<Usuario> listaTecnicos = asignarTareaFacade
								.getCargaLaboralPorUsuariosV2(rolTecnico, viabilidadProyecto.getAreaApoyo().getAreaName());			

						if(listaTecnicos==null || listaTecnicos.isEmpty()){
							LOG.error("No se encontro usuario " + rolTecnico + " en " + viabilidadProyecto.getAreaApoyo().getAreaName());
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
							JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
							return;
						}

						if(!variables.containsKey("tecnicoApoyo")) {
							Usuario tecnicoResponsable = listaTecnicos.get(0);
							parametros.put("tecnicoApoyo", tecnicoResponsable.getNombre());
						} else {
							String usrTecnico = (String) variables.get("tecnicoApoyo");
							Usuario tecnicoResponsable = null;
							Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
							if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
								if (listaTecnicos != null && listaTecnicos.size() >= 0
										&& listaTecnicos.contains(usuarioTecnico)) {
									tecnicoResponsable = usuarioTecnico;
								}
							}
							
							if (tecnicoResponsable == null) {
								tecnicoResponsable = listaTecnicos.get(0);
								parametros.put("tecnicoApoyo", tecnicoResponsable.getNombre()); 
							}
						}
					}
					
				} else {
					viabilidadProyecto.setRequiereApoyo(false);
					parametros.put("requiereApoyo", viabilidadProyecto.getRequiereApoyo());
				}
			} else {
				parametros.put("requiereApoyo", false);
			}
			
			viabilidadCoaFacade.guardar(viabilidadProyecto);
			
			parametros.put("requiereInspeccion", viabilidadProyecto.getRequiereInspeccionTecnica());
			
			parametros.put("numeroRevisionInformacion", numeroRevision + 1);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public void enviarRevisionApoyo() {
		try {
			
			Set<Integer> setDocumentosDescargados = new HashSet<Integer>(listaDocumentosDescargados);
			
			if(setDocumentosDescargados.size() < 4) {
				JsfUtil.addMessageError("Debe descargar todos los documentos para poder finalizar la tarea.");
				return;
			}
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
}
