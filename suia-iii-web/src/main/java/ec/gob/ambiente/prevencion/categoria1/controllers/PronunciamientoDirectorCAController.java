package ec.gob.ambiente.prevencion.categoria1.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
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

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeForestal;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeSnap;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeSnapEspecie;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalPronunciamiento;
import ec.gob.ambiente.suia.domain.DetalleInterseccionProyecto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.InformeTecnicoGeneralLA;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.InventarioForestalPma;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.InformeInspeccionForestalCAFacade;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.InformeInspeccionSnapCAFacade;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.PronunciamientoCAFacade;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class PronunciamientoDirectorCAController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1683511338517439792L;

	private static final Logger LOG = Logger.getLogger(PronunciamientoDirectorCAController.class);

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private InformeInspeccionSnapCAFacade informeSnapFacade;
	
	@EJB
	private InformeInspeccionForestalCAFacade informeForestalFacade;
	
	@EJB
	private PronunciamientoCAFacade pronunciamientoFacade;
	
	
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    
    @EJB
    private OrganizacionFacade organizacionFacade;

	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
    private SecuenciasFacade secuenciasFacade;
	
	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionFacade;
    
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	private CertificadoAmbientalInformeSnap informeSnap;
	
	@Getter
	@Setter
	private CertificadoAmbientalInformeForestal informeForestal;
	
	@Getter
	@Setter
	private CertificadoAmbientalPronunciamiento pronunciamiento;
		
	
	
	@Setter
	@Getter
	private boolean verPdfSnap,verPdfForestal;
	
    @Getter
	@Setter
	private String urlPdf;
    
	@Getter
	@Setter
	byte[] byteArchivo, byteRecuperado, archivo; 
	
	@Getter
	@Setter
	private File fileArchivo;
	
	@Getter
    @Setter
    private Documento documentoInformeSnap;
	
	@Getter
    @Setter
    private Documento documentoInformeForestal;
	
    @Getter
    @Setter
    private Documento documentoPronunciamiento;
    
    @Getter
    @Setter
    private boolean token;
    
    @Getter
    @Setter
    private boolean descargarOficioSnap,descargarOficioForestal;
    
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto; 
    
    private List<DetalleInterseccionProyecto> detalleInterseccion;
    
    private boolean uploadedSnap,uploadedBP;
    
    /**
   	 * FIRMA ELECTRONICA
   	 */
   	@Getter
   	@Setter
   	private boolean ambienteProduccion, firmaSoloToken, documentoDescargado, informacionSubida;
   	@Getter
   	@Setter
   	private String nombreDocumentoFirmado, urlAlfresco;
   	@Getter
   	@Setter
   	public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
   	@Getter
   	@Setter
   	private Documento documentoSubido, documentoInformacionManual;
   	@EJB
   	private CrudServiceBean crudServiceBean;
   	@Getter
   	@Setter
   	private FileUploadEvent lastEvent;
   	@EJB
   	private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;

	@PostConstruct
	public void init() {
		try {
			
			informeSnap = informeSnapFacade.getInformePorIdProyecto(proyectosBean.getProyecto().getId());
			informeForestal = informeForestalFacade.getInformePorIdProyecto(proyectosBean.getProyecto().getId());
						
			if(informeSnap!=null)
			{
				List<Documento> documentos=documentosFacade.documentoXTablaIdXIdDoc(informeSnap.getId(),CertificadoAmbientalInformeSnap.class.getSimpleName(),TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_SNAP);
				documentoInformeSnap=documentos.get(0);
				proyecto=informeSnap.getProyecto();
				
				detalleInterseccion=detalleAreasInterseca();
				
				pronunciamiento=pronunciamientoFacade.getpronunciamientoPorIdProyecto(proyecto.getId());
				if(pronunciamiento==null)
				{
					pronunciamiento=new CertificadoAmbientalPronunciamiento();
					pronunciamiento.setProyecto(proyecto);
					pronunciamiento.setCodigo(generarCodigo(pronunciamiento.getProyecto().getAreaResponsable()));							
				}
				
				if(informeSnap!=null)
					pronunciamiento.setPronunciamientoFavorable(informeSnap.getPronunciamientoFavorable());	
				
				pronunciamientoFacade.guardar(pronunciamiento);
				generarDocumentoPronunciamiento(true);
				verPdfSnap = true;
				descargarSnap();
				generarDocumentoRecuperado();
				firmarSnap();
			}
			
			if(informeForestal!=null)
			{
				List<Documento> documentos=documentosFacade.documentoXTablaIdXIdDoc(informeForestal.getId(),CertificadoAmbientalInformeForestal.class.getSimpleName(),TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_FORESTAL);
				documentoInformeForestal=documentos.get(0);
				proyecto=informeForestal.getProyecto();
				
				verPdfForestal=true;
				
				//Si es solo forestal no guarda pronunciamiento
				if(pronunciamiento==null){
					pronunciamiento=new CertificadoAmbientalPronunciamiento();
					pronunciamiento.setProyecto(proyecto);
				}
				descargarForestal();
				generarDocumentoRecuperado();
				firmarForestal();					
			}			
			
			token=loginBean.getUsuario().getToken()!=null?loginBean.getUsuario().getToken():false;
			
			/**
			 * FIRMA ELECTRONICA
			 */

			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			
			token = true;

			if (!ambienteProduccion) {
				verificaToken();
				documentoDescargado = true;
			}
			
			urlAlfresco = "";
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al generar la documentación. Comuníquese con mesa de ayuda.");
		}
	}
	
	
	public StreamedContent descargarSnap() {
		try {
			
            byte[] byteFile = informeSnapFacade.descargarFile(documentoInformeSnap);
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                return new DefaultStreamedContent(is, "application/pdf",  documentoInformeSnap.getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo Informe Snap. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo Informe Snap. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}
	
	public StreamedContent descargarForestal() {
		try {
			
            byte[] byteFile = informeForestalFacade.descargarFile(documentoInformeForestal);
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                return new DefaultStreamedContent(is, "application/pdf",  documentoInformeForestal.getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo Informe Forestal. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo Informe Forestal. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}

//	public boolean verificaToken (){
//		token = false;
//		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
//			token = true;
//		return token;
//	}
	
	public void generarDocumentoRecuperado() {
		try {
			String nombreReporte = "";
			String nombreProy = proyectosBean.getProyecto().getCodigo();
			String extension = ".pdf";
			String mime = "application/pdf";
			if (informeForestal != null) {
				nombreReporte = "CertificadoAmbientalInformeForestal" + nombreProy + extension;
			} else {
				nombreReporte = "CertificadoAmbientalInformeSnap" + nombreProy + extension;
			}

			archivo = byteRecuperado;
			String reporteHtmlfinal = nombreReporte.replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(archivo);
			file.close();
			urlPdf = archivoFinal.getAbsolutePath();
			urlPdf = (JsfUtil.devolverContexto("/reportesHtml/" + nombreReporte));
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error al cargar el informe tecnico", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public boolean verificaToken() {
		if (firmaSoloToken) {
			token = true;
			return token;
		}

		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}
	
	public void guardarToken(){
		Usuario usuario= JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken ();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getLabelProponente() {
        String label = proyecto.getUsuario().getPersona().getNombre();
        try {
            Organizacion organizacion = organizacionFacade.buscarPorPersona(proyecto.getUsuario().getPersona(), proyecto.getUsuario().getNombre());
            if (organizacion != null) {
                return organizacion.getNombre();
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        return label;
    }
	
	/**
	 * SUBE ARCHIVO SNAP
	 * 
	 * @param event
	 */
	public void uploadFileSigned(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		if (informeSnap != null) {
			documentoPronunciamiento.setContenidoDocumento(contenidoDocumento);
			documentoPronunciamiento.setNombre(event.getFile().getFileName());

			DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
			documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
			documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());

			informeSnapFacade.guardarDocumento(proyecto.getCodigo(), documentoPronunciamiento,
					TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_PRONUNCIAMIENTO, documentoTarea);
			uploadedSnap = true;
			nombreDocumentoFirmado = event.getFile().getFileName();
			informacionSubida = true;
			this.lastEvent = event;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else if (informeForestal != null) {

			documentoInformeForestal.setContenidoDocumento(contenidoDocumento);
			documentoInformeForestal.setNombre(event.getFile().getFileName());

			DocumentosTareasProceso documentosTarea = new DocumentosTareasProceso();
			documentosTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
			documentosTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
			informeSnapFacade.guardarDocumento(proyecto.getCodigo(), documentoInformeForestal,
					TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_FORESTAL, documentosTarea);
			uploadedBP = true;
			informacionSubida = true;
			nombreDocumentoFirmado = event.getFile().getFileName();
			this.lastEvent = event;
			JsfUtil.addMessageInfo("Documento subido exitosamente");

		}
	}
	
	public void uploadFileSnap(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoPronunciamiento.setContenidoDocumento(contenidoDocumento);
		documentoPronunciamiento.setNombre(event.getFile().getFileName());
		
		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
    	documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());    	
    	documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
		informeSnapFacade.guardarDocumento(proyecto.getCodigo(), documentoPronunciamiento, TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_PRONUNCIAMIENTO, documentoTarea);
		uploadedSnap=true;
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void uploadFileForestal(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoInformeForestal.setContenidoDocumento(contenidoDocumento);
		documentoInformeForestal.setNombre(event.getFile().getFileName());
		
		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
    	documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());    	
    	documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
		informeSnapFacade.guardarDocumento(proyecto.getCodigo(), documentoInformeForestal, TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_FORESTAL, documentoTarea);
		uploadedBP=true;
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	

	public void guardar() {
		try {
			if(verPdfSnap)
				generarDocumentoPronunciamiento(false);
			
			verPdfSnap=false;
			verPdfForestal=false;
			
			if(pronunciamiento.getPronunciamientoFavorable() != null){
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('signDialog').show();");
			}	
			
		} catch (Exception e) {
			LOG.error("error al guardar ", e);
			JsfUtil.addMessageError("Ocurrió un error al generar Borrador");
		}		
	}
	
	
	 private String generarCodigo(Area area) {
	        try {
	        	String mae=Constantes.SIGLAS_INSTITUCION + "-";
	        	/*if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
	        		mae= mae.replace("MAE", "GAD");
	        	}*/
	        	
	        	String siglasArea=area.getAreaAbbreviation();	        	
	            
	        	return mae
	        			+siglasArea	                    
	                    + "-"
	                    + secuenciasFacade.getNextValueDedicateSequence("CERTIFICADO_AMBIENTAL_PRONUNCIAMIENTO_BIODIVERSIDAD_"+siglasArea, 5)
	                    + "-"
	                    + secuenciasFacade.getCurrentYear();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return "";
	    }
	 
	 public List<DetalleInterseccionProyecto> detalleAreasInterseca()
	 {
		 List<DetalleInterseccionProyecto> detalle=new ArrayList<DetalleInterseccionProyecto>();
		 
		 List<InterseccionProyecto> interseccionProyecto=certificadoInterseccionFacade.getListaInterseccionProyectoIntersecaCapas(proyecto.getCodigo());
		 
		 for (InterseccionProyecto ip : interseccionProyecto) {
			 List<DetalleInterseccionProyecto> di=certificadoInterseccionFacade.getDetallesInterseccion(ip.getId());
			 detalle.addAll(di);
		}
		 
		 return detalle;
		 
	 }
	 
	public void actualizarPronunciamiento()
	{
		if(pronunciamiento.getCodigo()!=null)
			generarDocumentoPronunciamiento(true);
	}
	
	public void generarDocumentoPronunciamiento(boolean marcaAgua)
	{			
		FileOutputStream file;
		try {			
			String nombreReporte= "CertificadoAmbientalPronunciamiento"+(marcaAgua?"_Borrador":"");
			
			PlantillaReporte plantillaReporte = this.informeSnapFacade.obtenerPlantillaReporte(TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_PRONUNCIAMIENTO.getIdTipoDocumento());
			File informePdf = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new PronunciamientoDirectorHtml(pronunciamiento,detalleInterseccion,getLabelProponente(),loginBean.getUsuario()),null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
	        Path path = Paths.get(informePdf.getAbsolutePath());
	        byteArchivo = Files.readAllBytes(path);
	        fileArchivo = new File(JsfUtil.devolverPathReportesHtml(informePdf.getName()));
	        file = new FileOutputStream(fileArchivo);
	        file.write(byteArchivo);
	        file.close();
	        setUrlPdf(JsfUtil.devolverContexto("/reportesHtml/"+ informePdf.getName()));
	        
	        if(!marcaAgua){
	        	DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		    	documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());		    	
		    	documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
	        	
	        	
	        	documentoPronunciamiento = new Documento();
	        	JsfUtil.uploadApdoDocument(informePdf, documentoPronunciamiento);						
	       		documentoPronunciamiento.setMime("application/pdf");
	       		documentoPronunciamiento.setIdTable(pronunciamiento.getId());
	       		
	       		documentoPronunciamiento.setNombreTabla(CertificadoAmbientalPronunciamiento.class.getSimpleName());
	       		documentoPronunciamiento.setNombre("CertificadoAmbientalPronunciamiento.pdf");
	       		documentoPronunciamiento.setExtesion(".pdf");
	       		Documento doc=informeSnapFacade.guardarDocumento(proyecto.getCodigo(), documentoPronunciamiento, TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_PRONUNCIAMIENTO, documentoTarea);
	           	
	       		pronunciamiento.setDocumento(doc);
				pronunciamientoFacade.guardar(pronunciamiento);
	       		
				
	        }          
	        
	       } catch (Exception e) {
	       	e.printStackTrace();
	       }
	}
	
	
    public StreamedContent getStreamSnap() throws Exception {
    	
        DefaultStreamedContent content = new DefaultStreamedContent(new ByteArrayInputStream(
            		documentoPronunciamiento.getContenidoDocumento()), "application/pdf");
        content.setName(documentoPronunciamiento.getNombre());
      
        return content;

    }
    
	public void descargarFirmarOficio() {
		if (documentoPronunciamiento.getContenidoDocumento() != null) 
			descargarOficioSnap = true;
	}
	
	public void descargarFirmarForestal() {
		if (documentoInformeForestal.getContenidoDocumento() != null) 
			descargarOficioForestal = true;
		else descargarForestal();
	}
	
	/**
	 * FIRMA ELECTRONICA SNAP
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	public String firmarSnap() {
	    try {
	        if (informeSnap == null) {
	            LOG.error("Error: El informe Snap es nulo.");
	            JsfUtil.addMessageError("No se ha encontrado el informe Snap para firmar.");
	            return "";
	        }

	        documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(
	            informeSnap.getId(),
	            CertificadoAmbientalInformeSnap.class.getSimpleName(),
	            TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_SNAP);

	        if (documentoSubido == null) {
	            LOG.error("Error: El documento Snap es nulo.");
	            JsfUtil.addMessageError("No se ha encontrado el documento Snap para firmar.");
	            return "";
	        }

	        String documento = requisitosPreviosFacade.direccionDescarga(documentoSubido);

	        if (documento == null || documento.isEmpty()) {
	            LOG.error("Error: No se pudo obtener la URL de descarga del documento Snap.");
	            JsfUtil.addMessageError("Error al obtener el documento Snap para firmar.");
	            return "";
	        }
	        
	        pronunciamiento.setDocumento(documentoSubido);
	        pronunciamientoFacade.guardar(pronunciamiento);

	        DigitalSign firmaE = new DigitalSign();
	        documentoDescargado = true;
	        setDocumentoInformeSnap(documentoSubido);
	        System.out.println("idDoc; " + documentoInformeSnap.getIdAlfresco());
	        System.out.println(documentoSubido.getIdAlfresco());

	        // Proceder con la firma del documento
	        return firmaE.sign(documento, loginBean.getUsuario().getNombre());

	    } catch (Exception e) {
	        LOG.error("Error en firmarSnap()", e);
	        JsfUtil.addMessageError("Error al realizar la firma electrónica.");
	        return "";
	    }
	}
    
	/**
	 * FIRMA FORESTAL
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	public String firmarForestal() {
	    try {
	        // Verificar si el informe forestal está presente
	        if (informeForestal == null) {
	            LOG.error("Error: El informe forestal es nulo.");
	            JsfUtil.addMessageError("No se ha encontrado el informe forestal para firmar.");
	            return "";
	        }
	        
	        // Verificar si el documento se ha descargado correctamente
	        String documentUrl = documentosFacade.direccionDescarga(
	            CertificadoAmbientalInformeForestal.class.getSimpleName(),
	            informeForestal.getId(),
	            TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_FORESTAL);
	        
	        if (documentUrl == null || documentUrl.isEmpty()) {
	            LOG.error("Error: No se pudo obtener la URL de descarga del documento.");
	            JsfUtil.addMessageError("No se pudo obtener la URL del documento para firmar.");
	            return "";
	        }

	        // Verificar si el pronunciamiento y el documento están correctamente recuperados
	        documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(
	        		informeForestal.getId(),
	            CertificadoAmbientalInformeForestal.class.getSimpleName(),
	            TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_FORESTAL);

	        if (documentoSubido == null) {
	            LOG.error("Error: El documentoSubido es nulo.");
	            JsfUtil.addMessageError("Error al obtener el documento para firmar.");
	            return "";
	        }
	        
	        pronunciamiento.setDocumento(documentoSubido);
	        pronunciamientoFacade.guardar(pronunciamiento);

	        // Si todo está bien, proceder con la firma
	        DigitalSign firmaE = new DigitalSign();
	        documentoDescargado = true;

	        // Realizar la firma
	        String resultadoFirma = firmaE.sign(documentUrl, loginBean.getUsuario().getNombre());
	        
	        if (resultadoFirma == null || resultadoFirma.isEmpty()) {
	            LOG.error("Error: La firma del documento falló.");
	            JsfUtil.addMessageError("Error al firmar el documento.");
	            return "";
	        }

	        return resultadoFirma;

	    } catch (Exception e) {
	        LOG.error("Error en firmarForestal()", e);
	        JsfUtil.addMessageError("Error al realizar la firma electrónica.");
	        return "";
	    }
	}
	
	public void enviar()
	{
		try {
			
			if(informeSnap!=null)
			{
				if((token && !documentosFacade.verificarFirmaVersion(pronunciamiento.getDocumento().getIdAlfresco()))
				  ||(!token && !uploadedSnap)){					
					JsfUtil.addMessageError("El documento no ha sido firmado.");
					return;
				}					
			}
			
			if(informeForestal!=null)
			{
				if((token && !documentosFacade.verificarFirmaVersion(documentoInformeForestal.getIdAlfresco()))
				  ||(!token && !uploadedBP)){
					JsfUtil.addMessageError("El documento no ha sido firmado.");
					return;
				}					
			}
			
			Map<String, Object> params=new HashMap<>();
			params.put("pronunciamiento_favorable",pronunciamiento.getPronunciamientoFavorable());
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}
	
	/**
	 * FIRMA ELECTRONICA
	 */

	/**
	 * Descarga la informacion del panel
	 * 
	 * @return
	 * @throws IOException
	 */
	public void descargarInformacion() {

		if (informeSnap != null) {
			// generarDocumentoPronunciamiento(true);
			// descargarSnap();
			try {
				buscarInformacion();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (informeForestal != null) {

			descargarForestal();
		}

	}

	/**
	 * Descarga la informacion del panel
	 * 
	 * @return
	 * @throws IOException
	 */
	public StreamedContent buscarInformacion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent;
			Documento auxdoc = new Documento();
			Boolean generar = false;

			if (informeSnap != null) {

				auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(pronunciamiento.getId(),
						CertificadoAmbientalPronunciamiento.class.getSimpleName(),
						TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_PRONUNCIAMIENTO);

			} else if (informeForestal != null) {

				auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(informeForestal.getId(),
						CertificadoAmbientalInformeForestal.class.getSimpleName(),
						TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_FORESTAL);
			}

			documentoContent = documentosFacade.descargar(auxdoc.getIdAlfresco());
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(auxdoc.getNombre());
				documentoDescargado = true;
				// nombreDocumentoFirmado = event.getFile().getFileName();
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		}

		catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	public void firmarDocumento() {
		try {
			LOG.info("Iniciando proceso de firma de documento..."); // Log de depuración

			if (token) {
				if (informeSnap != null) {
					// Lógica para firmar el informe Snap
					String resultadoFirma = firmarSnap();
					LOG.info("Resultado de la firma del informe Snap: " + resultadoFirma);
					if (!resultadoFirma.isEmpty()) {
						JsfUtil.addMessageInfo("Informe Snap firmado correctamente.");
					} else {
						JsfUtil.addMessageError("Error al firmar el Informe Snap.");
					}
				} else if (informeForestal != null) {
					// Lógica para firmar el informe Forestal
					String resultadoFirma = firmarForestal();
					LOG.info("Resultado de la firma del informe Forestal: " + resultadoFirma);
					if (!resultadoFirma.isEmpty()) {
						JsfUtil.addMessageInfo("Informe Forestal firmado correctamente.");
					} else {
						JsfUtil.addMessageError("Error al firmar el Informe Forestal.");
					}
				} else {
					JsfUtil.addMessageError("No se ha encontrado un informe para firmar.");
				}
			} else {
				JsfUtil.addMessageError("Debe contar con un token para firmar el documento.");
			}

			LOG.info("Proceso de firma completado."); // Log de depuración
		} catch (Exception e) {
			LOG.error("Error al firmar el documento", e);
			JsfUtil.addMessageError("Error al realizar la operación de firma.");
		}
	}
	
	public String firmarOficio() {

		try {

			if (informeSnap != null) {
				return firmarSnap();
			} else

			if (informeForestal != null) {
				return firmarForestal();
			}	 
		} catch (Throwable e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
		return "";
	}
}