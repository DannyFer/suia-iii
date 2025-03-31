package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.kie.api.task.model.TaskSummary;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.dto.EntityOficioArchivacionEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.CatalogoGeneralEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformeTecnicoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.OficioPronunciamientoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.CatalogoGeneralEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.facade.ProcesosArchivadosFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarPdf;

@ManagedBean
@ViewScoped
public class FirmarArchivacionEstudioController implements Serializable{

	 private static final long serialVersionUID = -875087443147320594L;
	  
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;	
	
	@EJB
	private DocumentosImpactoEstudioFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	
	@EJB
	private OficioPronunciamientoEsIAFacade oficioPronunciamientoEsIAFacade;
	
	@EJB
	private CatalogoGeneralEsIAFacade catalogoGeneralEsIAFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private InformeTecnicoEsIAFacade informeTecncioEsIAFacade;
	
	@EJB
	private ProcesosArchivadosFacade procesosArchivadosFacade;

	@Getter
	@Setter
	private DocumentoEstudioImpacto documento,documentoManual;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private InformacionProyectoEia esiaProyecto;
	
	@Getter
	@Setter
	private OficioPronunciamientoEsIA oficioPronunciamiento;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocumento;
	
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionPrincipal = new UbicacionesGeografica();
	
	@Getter
	@Setter
	private Integer idProyecto;

	@Getter
	@Setter
	private Boolean documentoDescargado = false;
	
	@Getter
	@Setter
	private boolean token, documentoSubido, habilitarSeleccionToken, emitioRespuestaEnTiempo, pagoTerceraRevision, firmaSoloToken;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private String tramite ="", docuTableClass;
	
	@Getter
	@Setter
	private Integer numeroRevision;
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
		
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
		String idProyectoString = (String) variables.get("idProyecto");
		idProyecto = Integer.valueOf(idProyectoString);
		String numRevision = (String) variables.get("numeroRevision");
		numeroRevision = Integer.valueOf(numRevision);
		
		emitioRespuestaEnTiempo =  Boolean.parseBoolean(variables.get("emitioRespuesta").toString());
		if(variables.get("pagoTerceraRevision") != null)
			pagoTerceraRevision =  Boolean.parseBoolean( variables.get("pagoTerceraRevision").toString());
		
		habilitarSeleccionToken = true;
		tipoDocumento = TipoDocumentoSistema.EIA_OFICIO_ARCHIVACION_AUTOMATICA;
		docuTableClass = "OficioPronunciamientoArchivacionEsIA";
		
		verificaToken();
		
		proyecto = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
		
		esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
		
		oficioPronunciamiento = oficioPronunciamientoEsIAFacade.getPorEstudioTipoOficio(esiaProyecto.getId(), OficioPronunciamientoEsIA.oficioArchivoAutomatico);
		if(oficioPronunciamiento == null) {				
			oficioPronunciamiento = new OficioPronunciamientoEsIA();
			oficioPronunciamiento.setInformacionProyecto(esiaProyecto);
			oficioPronunciamiento.setFechaOficio(new Date());
			oficioPronunciamiento.setTipoOficio(OficioPronunciamientoEsIA.oficioArchivoAutomatico);
	
			
//			Area area = JsfUtil.getLoggedUser().getArea();
			Area area = proyecto.getAreaResponsable();
			if (area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
				area = area.getArea();
			
			oficioPronunciamiento.setAreaResponsable(area);
			
			oficioPronunciamientoEsIAFacade.guardarOficioArchivacion(oficioPronunciamiento, area);
		} else {
			oficioPronunciamiento.setFechaOficio(new Date());
			oficioPronunciamientoEsIAFacade.guardar(oficioPronunciamiento);
		}
		
		List<DocumentoEstudioImpacto> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDocLista(esiaProyecto.getId(), docuTableClass, tipoDocumento);
		if (listaDocumentosInt.size() > 0) {
			documento = listaDocumentosInt.get(0);
			
			if(!documento.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre()) 
					|| !JsfUtil.getSimpleDateFormat(documento.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
				documento.setEstado(false);
				documentosFacade.guardar(documento);
				
				generarOficio();
			} else {
				if (documentosFacade.verificarFirmaVersion(documento.getAlfrescoId())) {
					JsfUtil.addMessageInfo("El documento ya está firmado electrónicamente, debe finalizar la tarea.");
					
					token = true;
					habilitarSeleccionToken = false;
				}
			}
		} else {
			generarOficio();
		}		
	}
	
	public boolean verificaToken() {
		if(firmaSoloToken) {
			token = true;
			return token;
		}
		
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentosFacade
						.descargar(documento.getAlfrescoId()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documento.getContenidoDocumento()), documento.getExtesion());
			content.setName(documento.getNombre());
		}
		documentoDescargado = true;
		return content;

	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (documentoDescargado == true) {
			documentoManual = new DocumentoEstudioImpacto();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual = new DocumentoEstudioImpacto();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(docuTableClass);
			documentoManual.setIdTable(esiaProyecto.getId());
			
			documentoSubido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}

	}
	
	public void generarOficio() {
		try{
			DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
//			SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-YYYY");
			
			Usuario usuarioAutoridad = JsfUtil.getLoggedUser();
			
			Area area = oficioPronunciamiento.getAreaResponsable();
			
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(tipoDocumento);
			
			String nombreOperador = usuarioFacade.recuperarNombreOperador(proyecto.getUsuario()).get(0);

			//fecha ingreso coordenadas
			ProyectoLicenciaCoaUbicacion ubicacionProyecto = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto);
			Date fechaCoordenadas = ubicacionProyecto.getFechaCreacion();
			ubicacionPrincipal = ubicacionProyecto.getUbicacionesGeografica();
			
			//informacion certificado interseccion
			CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
			String resultadoInterseccion = (oficioCI.getInterseccionViabilidad() == null || oficioCI.getInterseccionViabilidad().isEmpty()) ? "NO INTERSECA" : "INTERSECA";
			Date fechaOficioCI = (oficioCI.getFechaOficio() != null) ? oficioCI.getFechaOficio() : oficioCI.getFechaCreacion();
			
			List<OficioPronunciamientoEsIA> listaOficios = oficioPronunciamientoEsIAFacade.getListaPorEstudioTipoOficio(esiaProyecto.getId(), OficioPronunciamientoEsIA.oficio);

			EntityOficioArchivacionEsIA entityOficio = new EntityOficioArchivacionEsIA();
			entityOficio.setNumeroOficio(oficioPronunciamiento.getCodigoOficio());
			entityOficio.setUbicacion(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			entityOficio.setFechaEmision(formatoFechaEmision.format(new Date()));
			entityOficio.setNombreOperador(nombreOperador);
			entityOficio.setNombreProyecto(proyecto.getNombreProyecto());
			entityOficio.setCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
			entityOficio.setFechaRegistro(formatoFechaEmision.format(proyecto.getFechaCreacion()));
			entityOficio.setFechaCoordenadas(formatoFechaEmision.format(fechaCoordenadas));
			entityOficio.setNroOficioCertificado(oficioCI.getCodigo());
			entityOficio.setFechaOficioCertificado(formatoFechaEmision.format(fechaOficioCI));
			entityOficio.setParroquia(ubicacionPrincipal.getNombre());
			entityOficio.setCanton(ubicacionPrincipal.getUbicacionesGeografica().getNombre());
			entityOficio.setProvincia(ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			entityOficio.setResultadoInterseccion(resultadoInterseccion);
			entityOficio.setDetalleInterseccion(oficioCI.getInterseccionViabilidad());
			entityOficio.setFechaIngresoEstudio(formatoFechaEmision.format(esiaProyecto.getFechaEnvioEstudio()));						
			
			if(oficioCI.getInterseccionViabilidad() == null || oficioCI.getInterseccionViabilidad().isEmpty())
				entityOficio.setMostrarDetalleInterseccion("display: none");
			else 
				entityOficio.setMostrarDetalleInterseccion("display: inline");
			
			entityOficio.setMostrarArchivoPorTiempoObs2("display: none");
			entityOficio.setMostrarArchivoPorTiempoObs3("display: none");
			
			for(int i= 0 ; i<listaOficios.size(); i++){
				
				InformeTecnicoEsIA informe = informeTecncioEsIAFacade.obtenerPorId(listaOficios.get(i).getIdInforme());
				
				if(informe.getNumeroRevision() == 1){					
					entityOficio.setFechaOficioObs1(formatoFechaEmision.format(listaOficios.get(i).getFechaOficio()));
					entityOficio.setCodigoOficioObs1(listaOficios.get(i).getCodigoOficio());
				}
				if(informe.getNumeroRevision() == 2){
					entityOficio.setFechaOficioObs2(formatoFechaEmision.format(listaOficios.get(i).getFechaOficio()));
					entityOficio.setCodigoOficioObs2(listaOficios.get(i).getCodigoOficio());
					
				}
				if(informe.getNumeroRevision()==3){
					entityOficio.setFechaOficioObs3(formatoFechaEmision.format(listaOficios.get(i).getFechaOficio()));
					entityOficio.setCodigoOficioObs3(listaOficios.get(i).getCodigoOficio());					
				}
			}						
			
			if(numeroRevision == 2){
				entityOficio.setFechaIngresoEstudio2(formatoFechaEmision.format(esiaProyecto.getFechaSegundoEnvio() != null ? esiaProyecto.getFechaSegundoEnvio() : esiaProyecto.getFechaModificacion()));
				entityOficio.setMostrarArchivoPorTiempoObs2("display: inline");
			}else if(numeroRevision == 3){
				entityOficio.setFechaIngresoEstudio2(formatoFechaEmision.format(esiaProyecto.getFechaSegundoEnvio() != null ? esiaProyecto.getFechaSegundoEnvio() : esiaProyecto.getFechaModificacion()));
				entityOficio.setFechaIngresoEstudio3(formatoFechaEmision.format(esiaProyecto.getFechaTercerEnvio() != null ? esiaProyecto.getFechaTercerEnvio() : esiaProyecto.getFechaModificacion()));
				entityOficio.setMostrarArchivoPorTiempoObs2("display: inline");
				entityOficio.setMostrarArchivoPorTiempoObs3("display: inline");
			}
				
			
			entityOficio.setNombreAutoridad(null);
			if(usuarioAutoridad != null)
				entityOficio.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());
			
			entityOficio.setAreaResponsable(area.getAreaName());
			if(area.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES))
				entityOficio.setAreaResponsable(Constantes.CARGO_AUTORIDAD_ZONAL);
			
			if(area.getTipoArea().getId()==3)
				entityOficio.setSiglasEnte(area.getAreaAbbreviation());
			
			entityOficio.setMostrarArchivoPorPago("display: none");
			entityOficio.setMostrarArchivoPorTiempo("display: none");
			if(!emitioRespuestaEnTiempo)
				entityOficio.setMostrarArchivoPorTiempo("display: inline");
			else if(!pagoTerceraRevision)
				entityOficio.setMostrarArchivoPorPago("display: inline");			
			
			String nombreArchivo = "PronunciamientoArchivo_" + proyecto.getCodigoUnicoAmbiental() + ".pdf";
			
			File informePdf = UtilGenerarPdf.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					nombreArchivo, true, entityOficio);
					
	
			Path path = Paths.get(informePdf.getAbsolutePath());
			String reporteHtmlfinal = nombreArchivo;
			byte[] archivoInforme = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(Files.readAllBytes(path));
			file.close();
			
			TipoDocumento tipoDoc = new TipoDocumento();
			tipoDoc.setId(tipoDocumento.getIdTipoDocumento());
			
			documento = new DocumentoEstudioImpacto();
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombre(nombreArchivo);
			documento.setExtesion(".pdf");		
			documento.setMime("application/pdf");
			documento.setNombreTabla(docuTableClass);
			documento.setIdTable(esiaProyecto.getId());
			
			documento = documentosFacade.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(), "ESTUDIO_IMPACTO_AMBIENTAL", documento, tipoDocumento);
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al generar el documento.");
		}
	}
	
	public String firmarDocumento() {
		try {
			String documentOffice = documentosFacade.direccionDescarga(documento);
			return DigitalSign.sign(documentOffice, loginBean.getUsuario().getNombre());
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
	}
	
	public void completarTarea() {
		try {
			
			if (token) {
				String idAlfrescoInforme = documento.getAlfrescoId();

				if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El oficio no está firmado electrónicamente.");
					return;
				}
			} else {
				if(documentoSubido) {
					documento = documentosFacade.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(), "OFICIO_PRONUNCIAMIENTO_ARCHIVACION", documentoManual, tipoDocumento);
				} else {
					JsfUtil.addMessageError("Debe adjuntar el oficio firmado.");
					return;
				}				
			}

			try {
				Map<String, Object> parametros = new HashMap<>();
				parametros.put("tipoPronunciamiento", 3); //3 ARCHIVADO estudio archivo
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				
				notificacionOperador();
				
				//para finalizar la tarea de encuesta y descarga de pronunciamiento
				List<TaskSummary> listaTareas = procesoFacade.getTaskReservedInProgress(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
				if(listaTareas != null && listaTareas.size() > 0) {				
					for (TaskSummary tarea : listaTareas) {
						String nombretarea = bandejaFacade.getNombreTarea(tarea.getId());
						if(nombretarea.equals("descargarPronunciamiento")) {
							Usuario usuarioTarea = usuarioFacade.buscarUsuario(tarea.getActualOwner().getId());

							String fechaAvance = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS").format(new Date());

							Map<String, Object> param = new HashMap<>();
					
							param.put("finalizadoAutomaticoSisEncuestaEsIA", fechaAvance);
							
							procesoFacade.modificarVariablesProceso(usuarioTarea, bandejaTareasBean.getTarea().getProcessInstanceId(), param);

							procesoFacade.aprobarTarea(usuarioTarea, tarea.getId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
							
							documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
							documentosFacade.guardar(getDocumento());
							
							CatalogoGeneralEsIA resultado = catalogoGeneralEsIAFacade.buscarPorCodigo("pronunciamiento_estudio_2"); // 2 archivado automatico
							
							if(resultado != null)
								esiaProyecto.setResultadoFinalEia(resultado.getId());
							esiaProyecto.setFechaFinEstudio(new Date());
							informacionProyectoEIACoaFacade.guardar(esiaProyecto);
									
							break;
						} 
					}
				}
				
				procesosArchivadosFacade.guardarArchivado(proyecto, 2);

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			} catch (JbpmException e) {
				JsfUtil.addMessageError("Error al realizar la operación.");
			}


		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public void notificacionOperador(){
		try {
			String nombreOperador = "";
			Usuario usuarioOperador = esiaProyecto.getProyectoLicenciaCoa().getUsuario();
			
			if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
				nombreOperador = usuarioOperador.getPersona().getNombre();
			} else {
				Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
				nombreOperador = organizacion.getNombre();
				usuarioOperador.getPersona().setContactos(organizacion.getContactos());
			}
									
			OficioPronunciamientoEsIA oficioArchivo = oficioPronunciamientoEsIAFacade.getPorEstudioTipoOficio(esiaProyecto.getId(), OficioPronunciamientoEsIA.oficioArchivoAutomatico);
			
			String fechaOficioString = JsfUtil.devuelveFechaEnLetras(oficioArchivo.getFechaOficio());
			
			ProyectoLicenciaCoaUbicacion proyectoLicenciaCoaUbicacion = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(esiaProyecto.getProyectoLicenciaCoa());
			UbicacionesGeografica ubicacion = proyectoLicenciaCoaUbicacion.getUbicacionesGeografica();
			
			String provincia = ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
			String canton = ubicacion.getUbicacionesGeografica().getNombre();
			String parroquia = ubicacion.getNombre();
			
			Calendar fecha = Calendar.getInstance();
			int anio = fecha.get(Calendar.YEAR);
			
			Object[] parametrosCorreoNuevo = new Object[] {nombreOperador, oficioArchivo.getCodigoOficio(),
					fechaOficioString, esiaProyecto.getProyectoLicenciaCoa().getNombreProyecto(),
					esiaProyecto.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), provincia, canton, parroquia,
					anio};
			
					
			MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionArchivoProyectoPorTiempoEsIA");
			
			String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreoNuevo);
			
			List<DocumentoEstudioImpacto> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDocLista(esiaProyecto.getId(), docuTableClass, tipoDocumento);
			
			List<String> listaArchivos = new ArrayList<>();
			FileOutputStream file;
			String nombreArchivo = "PronunciamientoArchivo_" + proyecto.getCodigoUnicoAmbiental() + ".pdf";
			File fileArchivo = new File(System.getProperty("java.io.tmpdir")+"/"+nombreArchivo);
			
			try {
				file = new FileOutputStream(fileArchivo);
				file.write(documentosFacade.descargar(listaDocumentosInt.get(0).getAlfrescoId()));
				file.close();
				
				listaArchivos.add(nombreArchivo);
			} catch (Exception e) {
				e.printStackTrace();
			}	
			
			
			Email.sendEmailAdjuntos(usuarioOperador, mensajeNotificacion.getAsunto(), notificacion, listaArchivos, esiaProyecto.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
}