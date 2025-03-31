package ec.gob.ambiente.rcoa.participacionCiudadana.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
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
import javax.inject.Inject;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.dto.EntityFirmaResponsabilidadPPC;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.DocumentoPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.FacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.DocumentosPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.FacilitadorPPC;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class FirmarDocumentoResponsabilidadFPController {

	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
	private DocumentoPPCFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaUbicacionFacade;
	@EJB
	private FacilitadorPPCFacade facilitadorFacade;
	
	@Inject
	private UtilPPC utilPPC;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@Getter
    @Setter
    private String tramite = "";
	
	@Getter
	@Setter
	private boolean token,documentoDescargado,informacionSubida;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoaUbicacion ubicacion;
	
	@Getter
	@Setter
    private FacilitadorPPC facilitador = new FacilitadorPPC();
	
	byte[] archivoInforme;
	
	@Getter
	@Setter
	private DocumentosPPC docuentoFirmaResponsabilidad,documentoFirmaManual;
	
	@Getter
	@Setter
	private String nombreDocumentoFirmado;
	
	@Getter
	@Setter
	private Area area; 
	
	@PostConstruct
	public void inicio()
	{
		try {			
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
			tramite = (String) variables.get("tramite");
			facilitador=facilitadorFacade.facilitador(proyectosBean.getProyectoRcoa(), loginBean.getUsuario());
			area=proyectosBean.getProyectoRcoa().getAreaResponsable();
			verificaToken();
		} catch (JbpmException e) {

		}
	}
	
	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}
	
	private String getDateFormat(Date date) {
		SimpleDateFormat formateador = new SimpleDateFormat(
				"dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
		return formateador.format(date);
	}
	
	public void descargarInforme()
	{
		try {
			List<DocumentosPPC> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyectoRcoa().getId(), TipoDocumentoSistema.RCOA_PPC_INFORME_PLANIFICACION, "INFORME PLANIFICACION");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Informe planificación-"+new Date().getTime());
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	
	public StreamedContent descargarInformacion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			generarDocumento();
			byte[] documentoContent = null;
			List<DocumentosPPC> listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(facilitador.getId(), TipoDocumentoSistema.RCOA_PPC_FIRMA_DE_RESPONSABILIDAD,FacilitadorPPC.class.getSimpleName());
			if (listaDocumentos.size() > 0) {
				docuentoFirmaResponsabilidad = listaDocumentos.get(0);
			}
			if (docuentoFirmaResponsabilidad != null && docuentoFirmaResponsabilidad.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(docuentoFirmaResponsabilidad.getIdAlfresco());
			}
			if (docuentoFirmaResponsabilidad != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(docuentoFirmaResponsabilidad.getNombreDocumento());
			}
			documentoDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
			documentoDescargado = false;
		}
		return content;
	}
	
	public void generarDocumento()
	{
		try {
			PlantillaReporte plantillaReporte = this.documentosFacade.obtenerPlantillaReporte(TipoDocumentoSistema.RCOA_PPC_FIRMA_DE_RESPONSABILIDAD.getIdTipoDocumento());
			EntityFirmaResponsabilidadPPC entityInforme = new EntityFirmaResponsabilidadPPC();

			ubicacion=proyectoLicenciaUbicacionFacade.ubicacionPrincipal(proyectosBean.getProyectoRcoa());
			entityInforme.setCiudadInforme(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			entityInforme.setFechaInforme(getDateFormat(new Date()));
			entityInforme.setNombreFacilitador(facilitador.getUsuario().getPersona().getNombre());
			entityInforme.setFacilitador(facilitador.getUsuario().getPersona().getNombre());
			entityInforme.setCedulaFacilitador(facilitador.getUsuario().getNombre());
			entityInforme.setNombreProyecto(proyectosBean.getProyectoRcoa().getNombreProyecto().toUpperCase());
			entityInforme.setCodigoProyecto(proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());

			String nombreReporte = "DocumentoResponsabilidad-"+new Date().getTime()+".pdf";

			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(),nombreReporte, true, entityInforme);


			Path path = Paths.get(informePdf.getAbsolutePath());
			String reporteHtmlfinal = nombreReporte;
			archivoInforme = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(Files.readAllBytes(path));
			file.close();

			DocumentosPPC documento = new DocumentosPPC();
			documento.setNombreDocumento(nombreReporte);
			documento.setExtencionDocumento(".pdf");		
			documento.setTipo("application/pdf");
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombreTabla(FacilitadorPPC.class.getSimpleName());
			documento.setIdTabla(facilitador.getId());
			// documento.setProyectoLicenciaCoa(proyectosBean.getProyectoRcoa());
			documento.setIdProceso(JsfUtil.getCurrentProcessInstanceId());

			documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental(), "PARTICIPACION CIUDADANA", bandejaTareasBean.getTarea().getProcessInstanceId(), documento, TipoDocumentoSistema.RCOA_PPC_FIRMA_DE_RESPONSABILIDAD);
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (CmisAlfrescoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void uploadListenerDocumentoFirmado(FileUploadEvent event) {
		if(documentoDescargado) {
			TipoDocumento tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(TipoDocumentoSistema.RCOA_PPC_FIRMA_DE_RESPONSABILIDAD.getIdTipoDocumento());

			byte[] contenidoDocumento = event.getFile().getContents();
			documentoFirmaManual = new DocumentosPPC();
			documentoFirmaManual.setId(null);
			documentoFirmaManual.setContenidoDocumento(contenidoDocumento);
			documentoFirmaManual.setNombreDocumento(event.getFile().getFileName());
			documentoFirmaManual.setExtencionDocumento(".pdf");		
			documentoFirmaManual.setTipo("application/pdf");
			documentoFirmaManual.setIdTabla(facilitador.getId());
			documentoFirmaManual.setNombreTabla(FacilitadorPPC.class.getSimpleName());
			documentoFirmaManual.setTipoDocumento(tipoDocumento);
			// documentoFirmaManual.setProyectoLicenciaCoa(proyectosBean.getProyectoRcoa());
			documentoFirmaManual.setIdProceso(JsfUtil.getCurrentProcessInstanceId());
			nombreDocumentoFirmado = event.getFile().getFileName();
			informacionSubida = true;
		} 
		else
		{			
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public void firmarInformacion() {
		try {	
			if(documentoDescargado) {
				if(docuentoFirmaResponsabilidad != null) {
					String documento = documentosFacade.direccionDescarga(docuentoFirmaResponsabilidad);
					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("window.location.href='"+DigitalSign.sign(documento, Constantes.getFirmaDePrueba()?"1721076097":JsfUtil.getLoggedUser().getNombre())+"'");
				}
			}
			else
			{
				JsfUtil.addMessageError("No ha descargado el documento para la firma");
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void completarTarea(){
		try {
			
			if(!documentoDescargado)
			{
				JsfUtil.addMessageError("No ha descargado el documento para la firma");
				return;
			}
			if (token) {
				String workspace = docuentoFirmaResponsabilidad.getIdAlfresco();
				if (!documentosFacade.verificarFirmaVersion(workspace)) {
					JsfUtil.addMessageError("El documento no está firmado electrónicamente.");
					return;
				}
			} 
			else 
			{
				if(informacionSubida)
				{				
					docuentoFirmaResponsabilidad = documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental(), "PARTICIPACION CIUDADANA", bandejaTareasBean.getTarea().getProcessInstanceId(), documentoFirmaManual,TipoDocumentoSistema.RCOA_PPC_FIRMA_DE_RESPONSABILIDAD);
				}	
				else
				{
					JsfUtil.addMessageError("Debe adjuntar el documento de responsabilidad.");
					return;
				}				
			}
			Map<String, Object> params = new HashMap<String, Object>();
			
			String tecnicoResponsablePPC = (String) variables.get("tecnicoResponsablePPC");
			Usuario usuarioTecnicoResponsable = null;
			if (tecnicoResponsablePPC == null) {
				usuarioTecnicoResponsable = utilPPC.asignarRol(proyectosBean.getProyectoRcoa(), "tecnicoPPC");
			} else {
				usuarioTecnicoResponsable = usuarioFacade.buscarUsuario(tecnicoResponsablePPC);
				if (usuarioTecnicoResponsable.getEstado() == false) {
					usuarioTecnicoResponsable = utilPPC.asignarRol(proyectosBean.getProyectoRcoa(), "tecnicoPPC");
				}
			}
			
			if(usuarioTecnicoResponsable == null) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return;
			}
			
			params.put("tecnicoResponsablePPC", usuarioTecnicoResponsable.getNombre());
			
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
            
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),  bandejaTareasBean.getProcessId(), null);
            
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (Exception e) {					
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
}
