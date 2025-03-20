package ec.gob.ambiente.rcoa.revisionDiagnosticoAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
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

import org.jfree.util.Log;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.dto.EntityIngresoDiagnosticoAmbiental;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class CargarDiagnosticoPlanAccionController implements Serializable{

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
	private ProcesoFacade procesoFacade;	
	
	@EJB
	private DocumentosCoaFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;

	@Getter
	@Setter
	private DocumentosCOA documento;

	@Getter
	@Setter
	private DocumentosCOA documentoCertificacion, documentoManual;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private List<DocumentosCOA> listaDocumentosDiagnostico, listaPlanAccion, listaDocumentosEliminar;
	
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionPrincipal;
	
	@Getter
	@Setter
	private Usuario tecnicoResponsable;
	
	private Map<String, Object> variables;
	
	private List<String> msgError= new ArrayList<String>();
	
	@Getter
	@Setter
	private String tramite, mensaje;
	
	@Getter
	@Setter
	private Boolean tieneObservacionesDiagnostico;
	
	@Getter
	@Setter
	private Boolean documentoDescargado = false, existeNormativaGuias;
	
	@Getter
	@Setter
	private Boolean documentoGuardado;
	
	@Getter
	@Setter
	private String urlAlfresco;
	
	byte[] byteGuias;
	
	private boolean descargado = false;
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
		tramite = (String) variables.get("u_tramite");
		
		String obsDiagnostico = (String) variables.get("observacionesRevisionDiagnostico");		
		tieneObservacionesDiagnostico = (obsDiagnostico != null) ? Boolean.valueOf(obsDiagnostico) : false;
		
		listaDocumentosEliminar = new ArrayList<>();
		listaDocumentosDiagnostico = new ArrayList<>();
		listaPlanAccion = new ArrayList<>();
		
		proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		
		listaDocumentosDiagnostico = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_DIAGNOSTICO_AMBIENTAL,"Diagnostico ambiental");		
		
		listaPlanAccion = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_PLAN_ACCION,"Diagnostico ambiental");
		
		List<DocumentosCOA> listaDoscFirma = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INGRESO_DIAGNOSTICO_AMBIENTAL, "DiagnosticoAmbiental");
		if(listaDoscFirma != null && listaDoscFirma.size() > 0){
			documentoCertificacion = listaDoscFirma.get(0);
			if(!JsfUtil.getSimpleDateFormat(documentoCertificacion.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
				documentoCertificacion.setEstado(false);
				documentosFacade.guardar(documentoCertificacion);
				
				documentoCertificacion = null;
			}
		}
		
		ubicacionPrincipal=proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto).getUbicacionesGeografica();
		
		byteGuias = documentosFacade.descargarDocumentoPorNombre("Directrices_para_elaboracion_de_diagnostico_ambiental.doc");
		
		buscarTecnico();	
		
		existeNormativaGuias = Constantes.getPropertyAsBoolean("rcoa.existe.normativa.guias.diagnostico");
		mensaje = "<i>De conformidad a lo establecido en el Reglamento al Código Orgánico del Ambiente: “Art. 457. – Diagnóstico Ambiental. - Los operadores que se encuentren ejecutando obras, proyectos o actividades sin autorización administrativa, deberán presentar a la Autoridad Ambiental Competente un diagnóstico ambiental y, de ser necesario, su respectivo plan de acción para subsanar los incumplimientos normativos identificados, conforme a la norma técnica expedida para el efecto por la Autoridad Ambiental Nacional”</i>, la guía estará disponible una vez que se emita la norma técnica correspondiente.";
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(), "/pages/rcoa/revisionDiagnosticoAmbiental/cargarDiagnosticoAmbiental.jsf");
	}
	
	public void uploadDiagnostico(FileUploadEvent event) {
		DocumentosCOA documentoLocal = new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoLocal.setContenidoDocumento(contenidoDocumento);
		documentoLocal.setNombreDocumento(event.getFile().getFileName());
		documentoLocal.setExtencionDocumento(".pdf");		
		documentoLocal.setTipo("application/pdf");
		documentoLocal.setNombreTabla("Diagnostico ambiental");
		documentoLocal.setIdTabla(proyecto.getId());
		documentoLocal.setProyectoLicenciaCoa(proyecto);
		
		listaDocumentosDiagnostico.add(documentoLocal);
	
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void uploadPlanAccion(FileUploadEvent event) {
		DocumentosCOA documento = new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");		
		documento.setTipo("application/pdf");
		documento.setNombreTabla("Diagnostico ambiental");
		documento.setIdTabla(proyecto.getId());
		documento.setProyectoLicenciaCoa(proyecto);
		
		listaPlanAccion.add(documento);
	
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void eliminarDocumento(Integer lista, DocumentosCOA documento) {
		try {
			if (documento.getId() != null) {
				documento.setEstado(false);
				listaDocumentosEliminar.add(documento);
			}
			
			if(lista.equals(1))
				listaDocumentosDiagnostico.remove(documento);
			else if(lista.equals(2))
				listaPlanAccion.remove(documento);
		} catch (Exception e) {
			Log.debug(e.toString());
		}
	}
	
	public StreamedContent descargarDocumento(DocumentosCOA documento) throws Exception {
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
		return content;

	}
	
	public void generarDeclaracion(Boolean marcaAgua) {
		try {
			
			PlantillaReporte plantillaReporteOficio = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_CERTIFICADO_INGRESO_DIAGNOSTICO_AMBIENTAL);
			
			
			String nombreDocumento = "IngresoDiagnosticoAmbiental_";
			nombreDocumento = nombreDocumento + UtilViabilidad.getFileNameEscaped(proyecto.getCodigoUnicoAmbiental().replace("/", "-"));
			
			EntityIngresoDiagnosticoAmbiental entityDocumento = new EntityIngresoDiagnosticoAmbiental();
			
			DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
			Usuario usuarioOperador = proyecto.getUsuario();
			
			entityDocumento.setUbicacion(ubicacionPrincipal.getUbicacionesGeografica().getNombre());
			entityDocumento.setFecha(formatoFechaEmision.format(new Date()));
			entityDocumento.setNombreOperador(usuarioOperador.getPersona().getNombre());
			entityDocumento.setNumeroCI(usuarioOperador.getPersona().getPin());
			entityDocumento.setNombreProyecto(proyecto.getNombreProyecto());
			entityDocumento.setCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
			

			File filePdf = UtilGenerarInforme.generarFichero(
					plantillaReporteOficio.getHtmlPlantilla(),
					nombreDocumento, true, entityDocumento);
			
			File file = JsfUtil.fileMarcaAgua(filePdf, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(file.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(file.getName()));
	        FileOutputStream fileOutputStream = new FileOutputStream(fileArchivo);
	        fileOutputStream.write(byteArchivo);
	        fileOutputStream.close();
	        
	        DocumentosCOA documentoLocal = new DocumentosCOA();	        	
	        documentoLocal.setContenidoDocumento(Files.readAllBytes(path));
	        documentoLocal.setExtencionDocumento(".pdf");		
	        documentoLocal.setTipo("application/pdf");
	        documentoLocal.setIdTabla(proyecto.getId());	       		
	        documentoLocal.setNombreTabla("DiagnosticoAmbiental");
	        documentoLocal.setNombreDocumento(nombreDocumento + ".pdf");
        	
        	
        	documentoCertificacion = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "DIAGNOSTICO_AMBIENTAL", 1L, documentoLocal, TipoDocumentoSistema.RCOA_CERTIFICADO_INGRESO_DIAGNOSTICO_AMBIENTAL);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			if (documentoCertificacion != null) {
				if (documentoCertificacion.getContenidoDocumento() == null) {
					documentoCertificacion.setContenidoDocumento(documentosFacade
							.descargar(documentoCertificacion.getIdAlfresco()));
				}
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoCertificacion.getContenidoDocumento()), documentoCertificacion.getExtencionDocumento());
				content.setName(documentoCertificacion.getNombreDocumento());
				
				documentoDescargado = true;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void cancelar() {
		JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
	}
	
	public void generarDocumentoFirma() {
		try {
			
			if(listaDocumentosDiagnostico.size() == 0) {
				JsfUtil.addMessageError("Debe ingresar el documento de Diagnóstico Ambiental.");
				return;
			}
			
			if(tieneObservacionesDiagnostico && listaPlanAccion.size() == 0) {
				JsfUtil.addMessageError("Debe ingresar el documento de Plan de acción.");
				return;
			}
			
			if (documentoCertificacion == null || documentoCertificacion.getId() == null) {
				generarDeclaracion(false);
			}
			
			if (documentoCertificacion != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoCertificacion);
				urlAlfresco = DigitalSign.sign(documentOffice, loginBean.getUsuario().getNombre());
			}
			
			documentoManual = null;
			
			RequestContext.getCurrentInstance().update("formDialogs:");
	        RequestContext context = RequestContext.getCurrentInstance();
	        context.execute("PF('signDialog').show();");

		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public void buscarTecnico() {
		//busqueda de rol
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
		
		//buscar usuarios por rol y area
		List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
		if (listaUsuario == null || listaUsuario.size() == 0) {
			msgError.add(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
			return;
		}

		//recuperar tecnico de bpm y validar si el usuario existe en el listado anterior
		String usrTecnico = (String) variables.get("u_tecnicoResponsable");
		Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
		if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
			if(listaUsuario != null && listaUsuario.size() >= 0 && listaUsuario.contains(usuarioTecnico)) {
				tecnicoResponsable = usuarioTecnico;
			}
		}
		
		//si no se encontró el usuario se realiza la busqueda de uno nuevo y se actualiza en el bpm
		if(tecnicoResponsable == null) {
			List<Usuario> listaTecnicosResponsables = asignarTareaFacade
					.getCargaLaboralPorUsuariosProceso(listaUsuario, Constantes.RCOA_REGISTRO_PRELIMINAR);
			tecnicoResponsable = listaTecnicosResponsables.get(0);
		
		}
	}
	
	public void finalizar() {
		try {
			
			//agrego la validacion
			if(listaDocumentosDiagnostico.size() == 0) {
				JsfUtil.addMessageError("Debe ingresar el documento de Diagnóstico Ambiental.");
				return;
			}
						
			if(existeNormativaGuias && !descargado){
				JsfUtil.addMessageError("Debe descargar la Guía para la elaboración del Diagnóstico Ambiental");
				return;
			}
			//fin validacion
			
			for (DocumentosCOA documento: listaDocumentosDiagnostico) {
				if(documento.getId() == null)
					documento = documentosFacade
							.guardarDocumentoAlfresco(tramite, "DIAGNOSTICO_AMBIENTAL", 0L, documento,TipoDocumentoSistema.RCOA_DOCUMENTO_DIAGNOSTICO_AMBIENTAL);
				
				documento.setIdProceso(bandejaTareasBean.getProcessId());
				documento = documentosFacade.guardar(documento);
			}
			listaDocumentosDiagnostico = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_DIAGNOSTICO_AMBIENTAL,"Diagnostico ambiental");		
			
			if(tieneObservacionesDiagnostico) {
				for (DocumentosCOA documento: listaPlanAccion) {
					if(documento.getId() == null)
						documento = documentosFacade
								.guardarDocumentoAlfresco(tramite, "DIAGNOSTICO_AMBIENTAL", 0L, documento,TipoDocumentoSistema.RCOA_DOCUMENTO_PLAN_ACCION);
					
					documento.setIdProceso(bandejaTareasBean.getProcessId());
					documento = documentosFacade.guardar(documento);
				}
				
				listaPlanAccion = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_PLAN_ACCION,"Diagnostico ambiental");
			}
			
			if(listaDocumentosEliminar.size() > 0) {
				for (DocumentosCOA documento: listaDocumentosEliminar) {
					documento.setEstado(false);
					documentosFacade.guardar(documento);
				}
			}
			
			if(msgError.size()>0) {
				JsfUtil.addMessageError(msgError);
				return;
			}
			
			Map<String, Object> parametros = new HashMap<>();
			
			Integer nroRevision = 0;
			if(variables.get("numeroRevisionDiagnostico") == null)
				nroRevision = 0;
			else {
				nroRevision = Integer.valueOf(variables.get("numeroRevisionDiagnostico").toString());
				parametros.put("cumpleTiempoSubsanacion", true);
			}
			
			parametros.put("numeroRevisionDiagnostico", nroRevision + 1);
			
			String usrTecnico = (String) variables.get("u_tecnicoResponsable");
			if((tecnicoResponsable != null && usrTecnico == null) || 
					(tecnicoResponsable != null && usrTecnico != null && tecnicoResponsable.getNombre().equals(usrTecnico))) {
				parametros.put("u_tecnicoResponsable",tecnicoResponsable.getNombre());
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public StreamedContent getGuias(){
		try {
			if(byteGuias != null){
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(byteGuias), "application/doc", "Guia de elaboración de Diagnostico Ambiental.doc");		
				descargado = true;
				return streamedContent;
			}			
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar la Guia de elaboración de Diagnostico Ambiental.");
			e.printStackTrace();
		}
		JsfUtil.addMessageError("No se pudo descargar la Guia de elaboración de Diagnostico Ambiental.");
		return null;
	}
}