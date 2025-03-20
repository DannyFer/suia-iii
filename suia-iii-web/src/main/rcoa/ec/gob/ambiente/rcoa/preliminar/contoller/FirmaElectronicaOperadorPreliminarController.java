package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import org.primefaces.context.DefaultRequestContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class FirmaElectronicaOperadorPreliminarController {
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private DocumentosCoaFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
    private UsuarioFacade usuarioFacade;	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	@EJB
    private OrganizacionFacade organizacionFacade;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private DocumentosFacade documentosGeneralFacade;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{reporteInformacionPreliminarController}")
	private ReporteInformacionPreliminarController reporteInformacionPreliminarController;
	
	@Getter
	@Setter
	private DocumentosCOA documentoInformacion, documentoInformacionManual, documentoMapa, documentoCertificado,documento;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    
    private Map<String, Object> variables;
	
	private String tramite;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private boolean token, documentoDescargado, informacionSubida, mapaDescargado, ciDescargado,acepta, esRegistroLicencia, ambienteProduccion, esEnte;
	
	@Getter
	@Setter
	private String nombreDocumentoFirmado, urlAlfresco;
	
	private CertificadoInterseccionOficioCoa oficioCI;
	
	@Getter
	@Setter
	private Usuario usuario=JsfUtil.getLoggedUser();
	
	@Getter
	@Setter
	private String representante;
	
	@Getter
	@Setter
	private String cedula;
	
	@Getter
    @Setter
    public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	
	@Getter
	@Setter
	private Organizacion organizacion;
	
	@PostConstruct
	public void init(){
		try {
			
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);	
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			 if(proyecto.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_EA)) {
				esEnte = true;
			}else
				esEnte = false;			
			
			oficioCI=certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(tramite);
			
			GeneracionDocumentosController oficioController = (GeneracionDocumentosController) BeanLocator.getInstance(GeneracionDocumentosController.class);
			documento = oficioController.generarCertificadoInterseccion(proyecto,usuarioFacade.buscarUsuario(oficioCI.getUsuarioFirma()));
			
			List<DocumentosCOA> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDoc(oficioCI.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO,"CertificadoInterseccionOficioCoa");
			if (listaDocumentosInt.size() > 0) {
				documentoCertificado = listaDocumentosInt.get(0);
			}
			List<DocumentosCOA> listaDocumentosMap = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA,"ProyectoLicenciaCoa");		
			if (listaDocumentosMap.size() > 0) {
				documentoMapa = listaDocumentosMap.get(0);
			}
			
			
			Usuario usuarioPro = proyecto.getUsuario();
			usuarioPro = usuarioFacade.buscarUsuarioPorIdFull(usuarioPro.getId());
			Persona titular = usuario.getPersona();
			Organizacion org = new Organizacion();
			org = organizacionFacade.buscarPorPersona(titular,proyecto.getUsuario().getNombre());	            
			if (org == null) {
				representante = titular.getNombre();
				cedula = titular.getPin();
			} else {	            	
				representante = org.getPersona().getNombre();	                
				cedula=org.getPersona().getPin();
			}

			token = true;
			if(!ambienteProduccion){
				verificaToken();
			}	
			organizacion = organizacionFacade.buscarPorRuc(usuario.getNombre());
			verificaRenderfirma();
			
//			List<DocumentosCOA> listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.REPORTE_INFORMACION_PRELIMINAR,"ProyectoLicenciaCoa");			
//			if (listaDocumentos.size() > 0) {
//				documentoInformacion = listaDocumentos.get(0);
//			}
			
			mapaDescargado = false;
			ciDescargado = false;
			
			urlAlfresco = "";
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void  verificaRenderfirma() {
		String uCategoria = (String) variables.get("u_categoria");
		
		
		if(null != uCategoria &&  Integer.parseInt(uCategoria) > 1   ) {
			esRegistroLicencia = true;	
		}	
	}
	
	
	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}
	
	public void guardarToken() {		
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"");
	}
	
	public void firmarInformacion() {
		try {
			if (!mapaDescargado || !ciDescargado || !documentoDescargado) {
				if (!mapaDescargado)
					JsfUtil.addMessageError("Debe descargar el Mapa de certificado de intersección.");

				if (!ciDescargado)
					JsfUtil.addMessageError("Debe descargar el Certificado de intersección.");
				
				if (!documentoDescargado)
					JsfUtil.addMessageError("Debe descargar el resumen de la información preliminar.");
				
				return;
			}
			
			Boolean generar = false;
            String usuarioFirma = "";
            if (organizacion != null) {
            	
            	//Cuando el representante legal es otra organizacion
				Organizacion organizacionRep = organizacion;				
				while (organizacionRep.getPersona().getPin().length() == 13) {
					Organizacion orgaAux = organizacionFacade.buscarPorRuc(organizacionRep.getPersona().getPin());
					if (orgaAux != null) {
						organizacionRep = orgaAux;
					} else {
						break;
					}
				}
				usuarioFirma = organizacionRep.getPersona().getPin();			
			} else {
				usuarioFirma = JsfUtil.getLoggedUser().getNombre();				
			}
            
            if(usuarioFirma.length()==13 && usuarioFirma.endsWith("001")) {
            	usuarioFirma=usuarioFirma.substring(0, 10);
            }

			if (documentoInformacion == null || documentoInformacion.getId() == null) {
				generar = true;
			} else {
				if (documentoInformacion.getIdProceso() == null) {
					documentoInformacion.setEstado(false);
					documentosFacade.guardar(documentoInformacion);

					generar = true;
				}
			}

			if(generar) {
				reporteInformacionPreliminarController.generarReporte(proyecto, true, false);
				List<DocumentosCOA> listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(),
						TipoDocumentoSistema.REPORTE_INFORMACION_PRELIMINAR, "ProyectoLicenciaCoa");

				if (listaDocumentos.size() > 0) {
					documentoInformacion = listaDocumentos.get(0);
				}
			}

			if(documentoInformacion != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoInformacion);
				urlAlfresco = DigitalSign.sign(documentOffice, usuarioFirma);
			}
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
		
		RequestContext.getCurrentInstance().update("formDialogs:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");

	}
	
	public StreamedContent descargarInformacion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			Boolean generar = false;
			if(documentoInformacion == null || documentoInformacion.getId() == null){
				generar = true;
			}
			
			if(generar) {
				reporteInformacionPreliminarController.generarReporte(proyecto, false, true);
				List<DocumentosCOA> listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.REPORTE_INFORMACION_PRELIMINAR,"ProyectoLicenciaCoa");
				
				if (listaDocumentos.size() > 0) {
					documentoInformacion = listaDocumentos.get(0);
				}
			}
			
			if (documentoInformacion != null && documentoInformacion.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoInformacion.getIdAlfresco());
			}
			
			if (documentoInformacion != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoInformacion.getNombreDocumento());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			documentoDescargado = true;

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerInformacionFirmada(FileUploadEvent event) {
		if(documentoDescargado) {
			TipoDocumento tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(TipoDocumentoSistema.REPORTE_INFORMACION_PRELIMINAR.getIdTipoDocumento());
			
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoInformacionManual = new DocumentosCOA();
			documentoInformacionManual.setId(null);
			documentoInformacionManual.setContenidoDocumento(contenidoDocumento);
			documentoInformacionManual.setNombreDocumento(event.getFile().getFileName());
			documentoInformacionManual.setExtencionDocumento(".pdf");		
			documentoInformacionManual.setTipo("application/pdf");
			documentoInformacionManual.setIdTabla(proyecto.getId());
			documentoInformacionManual.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
			documentoInformacionManual.setTipoDocumento(tipoDocumento);
			documentoInformacionManual.setProyectoLicenciaCoa(proyecto);
			
	        informacionSubida = true;
	        nombreDocumentoFirmado = event.getFile().getFileName();
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
    }	
	
	public void completarTarea(){
		try {
			
			if (!mapaDescargado || !ciDescargado || !documentoDescargado) {
				if (!mapaDescargado)
					JsfUtil.addMessageError("Debe descargar el Mapa de certificado de intersección.");

				if (!ciDescargado)
					JsfUtil.addMessageError("Debe descargar el Certificado de intersección.");
				
				if (!documentoDescargado)
					JsfUtil.addMessageError("Debe descargar el resumen de la información preliminar.");
				
				return;
			}
			
			if (esRegistroLicencia) {
				if (informacionSubida) {
					documentoInformacionManual.setIdProceso(JsfUtil.getCurrentProcessInstanceId());
					documentoInformacion = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoPma(),
							"INFORMACION_PRELIMINAR", 0L, documentoInformacionManual,
							TipoDocumentoSistema.REPORTE_INFORMACION_PRELIMINAR);
				}
				if (token) {
					String idAlfrescoInforme = documentoInformacion.getIdAlfresco();

					if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
						JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
						return;
					}

				} else {
					if (!informacionSubida) {
						JsfUtil.addMessageError("Debe adjuntar el reporte de información preliminar firmado.");
						return;
					}
				}
			}
					
			try {
				Map<String, Object> parametros = new HashMap<>();
				
				// Recuperar el usuario responsable de la revision del proyecto
				String strProyectoEnEjecucion = (String) variables.get("esProyectoEnEjecucion");		
				Boolean esProyectoEnEjecucion = (strProyectoEnEjecucion != null) ? Boolean.valueOf(strProyectoEnEjecucion) : false;
				
				String strEsCertificadoRegistro = (String) variables.get("esCertificadoRegistro");		
				Boolean esCertificadoRegistro = (strEsCertificadoRegistro != null) ? Boolean.valueOf(strEsCertificadoRegistro) : false;
				
				if(esProyectoEnEjecucion || !esCertificadoRegistro) {
					Area areaResponsable = proyecto.getAreaResponsable();
					String tipoRol = "role.esia.cz.tecnico.responsable";

					if (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
						ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
						CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
						
						Integer idSector = actividadPrincipal.getTipoSector().getId();
						
						tipoRol = "role.esia.pc.tecnico.responsable.tipoSector." + idSector;
					} else if (!areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
						tipoRol = "role.esia.gad.tecnico.responsable";
					}
	
					String rolTecnico = Constantes.getRoleAreaName(tipoRol);
					
					List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
					if (listaUsuario == null || listaUsuario.size() == 0) {
						System.out.println("No se encontró usuario " + rolTecnico + " en "+ areaResponsable.getAreaName());
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						return;
					}
					
					Usuario tecnicoResponsable = null;
					String usrTecnico = (String) variables.get("u_tecnicoResponsable");		
					if(usrTecnico != null) {
						Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
						if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
							if(listaUsuario != null && listaUsuario.size() >= 0 && listaUsuario.contains(usuarioTecnico)) {
								tecnicoResponsable = usuarioTecnico;
							}
						} 
					}
	
					if(tecnicoResponsable == null) {
						List<Usuario> listaTecnicosResponsables = asignarTareaFacade
								.getCargaLaboralPorUsuariosProceso(listaUsuario, Constantes.RCOA_REGISTRO_PRELIMINAR);
						tecnicoResponsable = listaTecnicosResponsables.get(0);
					}
					
					parametros.put("u_tecnicoResponsable",tecnicoResponsable.getNombre());
				}

				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId(), parametros);

				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);				
				
				documentoCertificado.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
				documentosFacade.guardar(documentoCertificado);
				documentoMapa.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
				documentosFacade.guardar(documentoMapa);
				documentoInformacion.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
				documentosFacade.guardar(documentoInformacion);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			} catch (JbpmException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public StreamedContent descargarMapa() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
				
			if (documentoMapa != null && documentoMapa.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoMapa.getIdAlfresco());
			}
			
			if (documentoMapa != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoMapa.getNombreDocumento());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			mapaDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public StreamedContent descargarCertificadoInterseccion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
				
			if (documentoCertificado != null && documentoCertificado.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoCertificado.getIdAlfresco());
			}
			
			if (documentoCertificado != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoCertificado.getNombreDocumento());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			ciDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	public StreamedContent descargarGuiaNoMetalicos() throws IOException {
		DefaultStreamedContent content = null;
		try {
			String nombreFormato = "Guía para la elaboración de estudios de impacto ambiental para proyectos de pequeña minería no metálica_AM122.pdf";
			String nombreDoc = "Guía para la elaboración de estudios de impacto ambiental para proyectos de pequeña minería no metálica";
			
			byte[] documentoGuia = documentosGeneralFacade.descargarDocumentoPorNombre(nombreFormato);
            if (documentoGuia != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documentoGuia));
				content.setName(nombreDoc + ".pdf");
				return content;
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void verificarExistenciaAutoridad(){
		
		List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea("AUTORIDAD AMBIENTAL", proyecto.getAreaResponsable());	
		
		if(usuarios != null && !usuarios.isEmpty()){
			completarTarea();
		}else{
			DefaultRequestContext.getCurrentInstance().execute("PF('dlgBloqueEntes').show();");
		}
	}
		
}
