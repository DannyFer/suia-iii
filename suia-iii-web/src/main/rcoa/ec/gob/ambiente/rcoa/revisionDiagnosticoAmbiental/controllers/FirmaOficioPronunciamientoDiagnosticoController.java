package ec.gob.ambiente.rcoa.revisionDiagnosticoAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.dto.EntityInformeOficioDiagnosticoAmbiental;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.OficioPronunciamientoDiagnosticoFacade;
import ec.gob.ambiente.rcoa.facade.PronunciamientoArchivacionProyectoFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.OficioPronunciamientoDiagnostico;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class FirmaOficioPronunciamientoDiagnosticoController implements Serializable{

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
	private DocumentosCoaFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private PronunciamientoArchivacionProyectoFacade pronunciamientoProhibicionActividadFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private OficioPronunciamientoDiagnosticoFacade oficioPronunciamientoFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@Getter
	@Setter
	private OficioPronunciamientoDiagnostico oficioPronunciamiento;

	@Getter
	@Setter
	private DocumentosCOA documento,documentoManual;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocumento;

	@Getter
	@Setter
	private Boolean documentoDescargado = false, firmaSoloToken;
	
	@Getter
	@Setter
	private Integer tipoOficio;
	
	@Getter
	@Setter
	private boolean token, documentoSubido, habilitarSeleccionToken;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private String tramite ="", docuTableClass, diasNormativa;
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
		
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
		tramite = (String) variables.get("u_tramite");
		
		docuTableClass = "OficioDiagnosticoAmbiental";
		proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		
		CatalogoGeneralCoa catalogoNormativa = null;
		tipoDocumento = TipoDocumentoSistema.RCOA_ARCHIVO_OBSERVACIONES_DIAGNOSTICO_AMBIENTAL; //archivacion por no subsanacion de observaciones
		tipoOficio = 3;
		if(variables.containsKey("iniciaProceso")) {
			tipoDocumento = TipoDocumentoSistema.RCOA_ARCHIVO_NO_INICIA_REGULARIZACION; //archivacion por no inicio de regularización
			tipoOficio = 4;
			
			catalogoNormativa = catalogoCoaFacade.obtenerCatalogoPorCodigo("dias.habiles.inicio.regularizacion.ambiental");
		} else
			catalogoNormativa = catalogoCoaFacade.obtenerCatalogoPorCodigo("dias.subsanacion.diagnostico.ambiental");
		
		diasNormativa  = catalogoNormativa.getValor();
		
		oficioPronunciamiento = oficioPronunciamientoFacade.getPorProyectoTipo(proyecto.getId(), tipoOficio);
		
		Area areaTramite = proyecto.getAreaResponsable();
		if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
			areaTramite = areaTramite.getArea();
		
		if(oficioPronunciamiento.getId() == null) {
			oficioPronunciamiento = new OficioPronunciamientoDiagnostico();
			oficioPronunciamiento.setProyectoLicenciaCoa(proyecto);
			oficioPronunciamiento.setIdTarea((int) bandejaTareasBean.getTarea().getTaskId());
			oficioPronunciamiento.setFechaElaboracion(new Date());
			oficioPronunciamiento.setTipoPronunciamiento(tipoOficio);
			
			oficioPronunciamientoFacade.guardar(oficioPronunciamiento, areaTramite);	
		}
		
		List<DocumentosCOA> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDoc(oficioPronunciamiento.getId(), tipoDocumento, docuTableClass);
		if (listaDocumentosInt.size() > 0) {
			documento = listaDocumentosInt.get(0);
			
			if (documentosFacade.verificarFirmaVersion(documento.getIdAlfresco())) {
				if(documento.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre()))
					JsfUtil.addMessageInfo("El documento ya está firmado electrónicamente, debe finalizar la tarea.");
				else
					generarOficio();
			} else 
				generarOficio();
		} else {
			generarOficio();
		}	
	}
	
	
	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		
		if(firmaSoloToken)
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
	
	
	public void generarOficio() {
		try {
			
			oficioPronunciamiento.setFechaElaboracion(new Date());
			oficioPronunciamientoFacade.guardar(oficioPronunciamiento);

			Boolean marcaAgua = false;
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(tipoDocumento);
			
			
			oficioPronunciamiento.setNombreReporte("OficioPronunciamiento_" + oficioPronunciamiento.getCodigo() + ".pdf");
			
			EntityInformeOficioDiagnosticoAmbiental entity = cargarDatosOficio();

			File oficioPdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					oficioPronunciamiento.getNombreReporte(), true, entity);
			
			File oficioPdf = JsfUtil.fileMarcaAgua(oficioPdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(oficioPdf.getAbsolutePath());
			oficioPronunciamiento.setArchivoReporte(Files.readAllBytes(path));
			String reporteHtmlfinal = oficioPronunciamiento.getNombreReporte().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(oficioPronunciamiento.getArchivoReporte());
			file.close();
			
			documento = new DocumentosCOA();
			documento.setNombreDocumento(reporteHtmlfinal);
			documento.setExtencionDocumento(".pdf");		
			documento.setTipo("application/pdf");
			documento.setContenidoDocumento(oficioPronunciamiento.getArchivoReporte());
			documento.setNombreTabla(docuTableClass);
			documento.setIdTabla(oficioPronunciamiento.getId());
			documento.setProyectoLicenciaCoa(proyecto);
			
			documento = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "DIAGNOSTICO_AMBIENTAL", 0L, documento, tipoDocumento);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public EntityInformeOficioDiagnosticoAmbiental cargarDatosOficio() throws Exception {
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-YYYY");
		
		String nombreOperador = "";
		Usuario usuarioOperador = proyecto.getUsuario();
		Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());

		if (organizacion != null) {
			nombreOperador = organizacion.getNombre();
		} else {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		}
		
		String tratamiento = (organizacion == null) ? (usuarioOperador.getPersona().getTipoTratos() == null ? " " : usuarioOperador.getPersona().getTipoTratos().getNombre()) : (organizacion.getPersona().getTipoTratos() == null ? " " : organizacion.getPersona().getTipoTratos().getNombre());
		
		//fecha ingreso coordenadas
		ProyectoLicenciaCoaUbicacion ubicacionProyecto = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto);
		Date fechaCoordenadas = ubicacionProyecto.getFechaCreacion();
		UbicacionesGeografica ubicacionPrincipal = ubicacionProyecto.getUbicacionesGeografica();
		
		//informacion certificado interseccion
		CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
		String resultadoInterseccion = (oficioCI.getInterseccionViabilidad() == null || oficioCI.getInterseccionViabilidad().isEmpty()) ? "NO INTERSECA" : "INTERSECA";
		Date fechaOficioCI = (oficioCI.getFechaOficio() != null) ? oficioCI.getFechaOficio() : oficioCI.getFechaCreacion();
		
		String fechaTarea = JsfUtil.getCurrentTask().getActivationDate();
		Date fechaInicioAnalisis = new SimpleDateFormat("dd/MM/yyyy h:m a").parse(fechaTarea);

		EntityInformeOficioDiagnosticoAmbiental entity = new EntityInformeOficioDiagnosticoAmbiental();
		
		entity.setNumero(oficioPronunciamiento.getCodigo());		
		entity.setCiudad(JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		entity.setFechaEmision(formatoFechaEmision.format(new Date()));
		
		entity.setTratamientoOperador(tratamiento);
		entity.setNombreOperador(nombreOperador);
		entity.setNombreProyecto(proyecto.getNombreProyecto());
		entity.setCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
		entity.setFechaRegistro(formatoFecha.format(proyecto.getFechaCreacion()));
		entity.setFechaCoordenadas(formatoFecha.format(fechaCoordenadas));
		entity.setNroOficioCertificado(oficioCI.getCodigo());
		entity.setFechaOficioCertificado(formatoFecha.format(fechaOficioCI));
		entity.setParroquia(ubicacionPrincipal.getNombre());
		entity.setCanton(ubicacionPrincipal.getUbicacionesGeografica().getNombre());
		entity.setProvincia(ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		entity.setResultadoInterseccion(resultadoInterseccion);
		entity.setFechaIngresoAnalisis(formatoFecha.format(fechaInicioAnalisis));
		entity.setDiasNormativa(diasNormativa);

		entity.setNombreResponsable(JsfUtil.getLoggedUser().getPersona().getNombre());
		
		Area areaTecnicoRes = new Area();
		if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() == 1){
			areaTecnicoRes = JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea();
		}else{
			areaTecnicoRes = proyecto.getAreaResponsable();
		}
		
//		entity.setAreaResponsable(JsfUtil.getLoggedUser().getArea().getAreaName());
		entity.setAreaResponsable(areaTecnicoRes.getAreaName());
		
		if(proyecto.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
			entity.setAreaResponsable(Constantes.CARGO_AUTORIDAD_ZONAL);
		
		
		if(areaTecnicoRes.getTipoArea().getId()==3)
			entity.setSiglasEnte(areaTecnicoRes.getAreaAbbreviation());
		
		return entity;
	}
	
	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentosFacade
						.descargar(documento.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documento.getContenidoDocumento()), documento.getExtencionDocumento());
			content.setName(documento.getNombreDocumento());
		}
		documentoDescargado = true;
		return content;

	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (documentoDescargado == true) {
			documentoManual=new DocumentosCOA();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombreDocumento(event.getFile().getFileName());
			documentoManual.setExtencionDocumento(".pdf");		
			documentoManual.setTipo("application/pdf");
			documentoManual.setNombreTabla(docuTableClass);
			documentoManual.setIdTabla(oficioPronunciamiento.getId());
			documentoManual.setProyectoLicenciaCoa(proyecto);
			documentoSubido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
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
				String idAlfrescoInforme = documento.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El documento no está firmado electrónicamente.");
					return;
				}
			} else {
				if(documentoSubido) {
					documento = documentosFacade
							.guardarDocumentoAlfresco(tramite, "DIAGNOSTICO_AMBIENTAL", 0L, documentoManual, tipoDocumento);
				} else {
					JsfUtil.addMessageError("Debe adjuntar el documento firmado.");
					return;
				}				
			}
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			

		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
}