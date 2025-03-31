package ec.gob.ambiente.prevencion.categoria1.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.Query;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.RegistroProyectoBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.CargarCoordenadasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeForestal;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeForestalFormaCoordenada;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeSnap;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.InformeInspeccionForestalCAFacade;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.InformeInspeccionSnapCAFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class InformeInspeccionForestalCAController implements Serializable {

	private static final long serialVersionUID = -8335883875646842045L;

	private static final Logger LOG = Logger.getLogger(InformeInspeccionSnapCAController.class);

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private InformeInspeccionForestalCAFacade informeForestalFacade;
	
	@EJB
	private CategoriaIIFacade 	categoriaIIFacade; 
	
	
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    
    @EJB
    private OrganizacionFacade organizacionFacade;

	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
    private SecuenciasFacade secuenciasFacade;
    
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
	@ManagedProperty(value = "#{cargarCoordenadasBean}")
	private CargarCoordenadasBean cargarCoordenadasBean;
	
	@Getter
	@Setter
	private CertificadoAmbientalInformeForestal informeForestal;
	
	@Getter
	@Setter
	private CertificadoAmbientalInformeSnap informeSnap;
	
	@Getter
	@Setter
	private List<ProyectoUbicacionGeografica> ubicaciones;
	
	@Setter
	@Getter
	private boolean verPdf;
	
    @Getter
	@Setter
	private String urlPdf;
    
    @Getter
	@Setter
	private Usuario usuarioDirector;
    
	@Getter
	@Setter
	byte[] byteArchivo; 
	
	@Getter
	@Setter
	private File fileArchivo;
	
    @Getter
    @Setter
    private Documento documentoInforme;
	
	Map<String, Object> variables;
	
	boolean remocionCoberturaVegetal;
	
	//Bean Solo para cargar las coordenadas
    @ManagedProperty(value = "#{registroProyectoBean}")
    @Setter
    @Getter
    private RegistroProyectoBean registroProyectoBean;
    
    @Getter
    List<CertificadoAmbientalInformeForestalFormaCoordenada> listaCoordenadas;
    

	@Setter
	@Getter
	private UploadedFile fileAdjunto;
    
    @Setter
	@Getter
	private String nombreFileAdjunto;
	
	private boolean actualizaDoc=false;
	
	/**
	 * FIRMA ELECTRONICA
	 */
	@Getter
	@Setter
	private boolean token, ambienteProduccion, firmaSoloToken, documentoDescargado, informacionSubida;
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

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private InformeInspeccionSnapCAFacade informeSnapFacade;

	public static final String TIPO_DOCUMENTO = "CERTIFICADO AMBIENTAL INFORME FORESTAL ADJUNTO";


	@PostConstruct
	public void init() {
		try {
			//Bean Solo para cargar las coordenadas
			registroProyectoBean.setProyecto(proyectosBean.getProyecto());
			informeSnap = informeSnapFacade.getInformePorIdProyecto(proyectosBean.getProyecto().getId());
			informeForestal = informeForestalFacade.getInformePorIdProyecto(proyectosBean.getProyecto().getId());

			if (informeForestal == null) {
				informeForestal = new CertificadoAmbientalInformeForestal();
				informeForestal.setProyecto(proyectosBean.getProyecto());
				informeForestal.setRemocionCoberturaVegetal(proyectosBean.getProyecto().getRemocionCoberturaVegetal());
				//informeForestal.setCodigo(generarCodigo(informeForestal.getProyecto().getAreaResponsable()));
			}
			
			ubicaciones=informeForestal.getProyecto().getProyectoUbicacionesGeograficas();
			
			if(informeForestal.getId()!=null)
			{
				listaCoordenadas=informeForestalFacade.getCoordenadasPorIdInformeForestal(informeForestal.getId());				
			}
			
			variables=procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
			remocionCoberturaVegetal=Boolean.parseBoolean((String)variables.get("remocion_cobertura_vegetal"));
			
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			
			token = true;
			if (!ambienteProduccion) {
				verificaToken();
				documentoDescargado = true;
				guardar();
			}
			urlAlfresco = "";
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos certificadoAmbientalInformeSnap.");
		}
	}
	
	public void editarFormulario() {
		verPdf=false;
	}
	
	
	private String getLabelProponente() {
        String label = informeForestal.getProyecto().getUsuario().getPersona().getNombre();
        try {
            Organizacion organizacion = organizacionFacade.buscarPorPersona(informeForestal.getProyecto().getUsuario().getPersona(), informeForestal.getProyecto().getUsuario().getNombre());
            if (organizacion != null) {
                return organizacion.getNombre();
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        return label;
    }
	
	public Date getCurrentDate() {
		return new Date();
	}
	
	public void fileUpload(FileUploadEvent event) {
		fileAdjunto = event.getFile();
		informeForestal.setAdjunto(UtilDocumento.generateDocumentPDFFromUpload(fileAdjunto.getContents(),fileAdjunto.getFileName()));
		actualizaDoc=true;
	}
	
	public StreamedContent descargar() {
		try {
            byte[] byteFile = informeForestalFacade.descargarFile(informeForestal.getAdjunto());
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                return new DefaultStreamedContent(is, "application/pdf",  informeForestal.getAdjunto().getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}
	
    private List<CertificadoAmbientalInformeForestalFormaCoordenada> getFormasCoordenadas() {
        List<CertificadoAmbientalInformeForestalFormaCoordenada> result = new ArrayList<CertificadoAmbientalInformeForestalFormaCoordenada>();
        Iterator<CoordinatesWrapper> coords = JsfUtil.getBean(CargarCoordenadasBean.class).getCoordinatesWrappers().iterator();
        while (coords.hasNext()) {
            CoordinatesWrapper coordinatesWrapper = coords.next();
            CertificadoAmbientalInformeForestalFormaCoordenada formaCoordenada = new CertificadoAmbientalInformeForestalFormaCoordenada();
            formaCoordenada.setInformeForestal(informeForestal);
            formaCoordenada.setTipoForma(coordinatesWrapper.getTipoForma());
            formaCoordenada.setCoordenadas(coordinatesWrapper.getCoordenadas());
            result.add(formaCoordenada);
        }
        return result;
    }
    
    

	public void guardar() {
		try {
			if(informeForestal.getAdjunto()==null)
			{
				JsfUtil.addMessageError("Agregar Documento Anexo");
	    		return;
			}
			
			informeForestal.setCodigo(informeForestal.getCodigo().toUpperCase());			
			if(informeForestalFacade.codigoUsado(informeForestal))
			{
				JsfUtil.addMessageError("El código de infome ya existe.");
	    		return;
			}
			
			if(actualizaDoc){
				
				DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		    	documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
		    	informeForestal.getAdjunto().setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
		    	informeForestal.getAdjunto().setIdTable(informeForestal.getProyecto().getId());
		    	documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
		    	
		    	String fileName=informeForestal.getAdjunto().getNombre();
		    	if(!fileName.contains("EstadoVegetacionInformeSnap-"))
		    		informeForestal.getAdjunto().setNombre("AdjuntoInformeForestal-"+fileName);
		    	Documento documento=informeForestalFacade.guardarDocumento(informeForestal.getProyecto().getCodigo(),informeForestal.getAdjunto(), TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_FORESTAL_ADJUNTO, documentoTarea);
		    	
		    	if(documento==null)
		    	{
		    		JsfUtil.addMessageError("Error al Guardar Documento Anexo");
		    		return;
		    	}
		    	informeForestal.setAdjunto(documento);
		    	actualizaDoc=false;
			}
			
			informeForestalFacade.guardarInforme(informeForestal);
			
			List<CertificadoAmbientalInformeForestalFormaCoordenada> formasCoordenadas=getFormasCoordenadas();
			if(formasCoordenadas.size()>0)
			{
				informeForestalFacade.guardarFormasCoordenadas(formasCoordenadas);
			}
						
			listaCoordenadas=informeForestalFacade.getCoordenadasPorIdInformeForestal(informeForestal.getId());
			if(listaCoordenadas==null)
			{
				JsfUtil.addMessageError("Ingrese las coordenadas");
				return;
			}			
			
			Area areaTramite = informeForestal.getProyecto().getAreaResponsable();
			if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
				areaTramite = areaTramite.getArea();
			List<Usuario>uList=usuarioFacade.buscarUsuariosPorRolYArea(Constantes.ROL_DIRECTOR_EA, areaTramite);
			if(uList==null || uList.size()==0)			
			{
				JsfUtil.addMessageError("No se encontró el usuario Director en "+informeForestal.getProyecto().getAreaResponsable().getAreaName());
				return;
			}else{
				usuarioDirector=uList.get(0);
			}
			
			generarDocumentoInforme(true);			
			JsfUtil.addMessageInfo("Verifique la información antes de enviar.");                       

		} catch (Exception e) {
			LOG.error("error al guardar ", e);
			JsfUtil.addMessageError("Ocurrió un error al generar el documento. Comuníquese con mesa de ayuda.");
		}		
	}
	
	/*
	private String generarCodigo(Area area) {
	        try {
	        	String mae="MAE-";
	        	if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
	        		mae= mae.replace("MAE", "GAD");
	        	}
	        	
	        	String siglasArea="Forestal-";	        	
	            
	        	return mae
	        			+siglasArea
	                    + secuenciasFacade.getCurrentYear()
	                    + "-"
	                    + secuenciasFacade.getNextValueDedicateSequence("CERTIFICADO_AMBIENTAL_INFORME_FORESTAL", 5)
	                    + "-Informe";
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return "";
	}*/
	
	public void generarDocumentoInforme(boolean marcaAgua)
	{			
		FileOutputStream file;
		try {			
			String nombreReporte= "Informe_Inspeccion_Snap";
			
			PlantillaReporte plantillaReporte = this.informeForestalFacade.obtenerPlantillaReporte(TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_FORESTAL.getIdTipoDocumento());
			File informePdf = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new InformeInspeccionForestalCAHtml(informeForestal,ubicaciones,listaCoordenadas,getLabelProponente(),loginBean.getUsuario(),usuarioDirector),null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
	        Path path = Paths.get(informePdf.getAbsolutePath());
	        byteArchivo = Files.readAllBytes(path);
	        fileArchivo = new File(JsfUtil.devolverPathReportesHtml(informePdf.getName()));
	        file = new FileOutputStream(fileArchivo);
	        file.write(byteArchivo);
	        file.close();
	        setUrlPdf(JsfUtil.devolverContexto("/reportesHtml/"+ informePdf.getName()));
	        
	        if(!marcaAgua){	        			    	
	        	documentoInforme = new Documento();
	        	JsfUtil.uploadApdoDocument(informePdf, documentoInforme);						
	       		documentoInforme.setMime("application/pdf");
	       		documentoInforme.setIdTable(informeForestal.getId());	       		
	       		documentoInforme.setNombreTabla(CertificadoAmbientalInformeForestal.class.getSimpleName());	       		
	       		documentoInforme.setNombre("CertificadoAmbientalInformeForestal"+informeForestal.getProyecto().getCodigo()+".pdf");
	       		documentoInforme.setExtesion(".pdf");	
	       		
	       		TipoDocumentoSistema tipoDocumento = null;
				if (informeSnap != null) {
					tipoDocumento = TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_SNAP;									
				} else {
					tipoDocumento = TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_FORESTAL;
				}
				
				documentoSubido = documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), Constantes.CARPETA_CATEGORIA_UNO, 
						bandejaTareasBean.getProcessId(), documentoInforme, tipoDocumento, null);
	        }
	          
	        verPdf=true;
	       } catch (Exception e) {
	       	e.printStackTrace();
	       }
	}
	
	public void generar() {
		generarDocumentoInforme(false);		
	}
	
	public StreamedContent descargarDocumento() {
		try {
			if(!ambienteProduccion){
				generarDocumentoInforme(false);
	            byte[] byteFile = byteArchivo;
	            if (byteFile != null) {
	                InputStream is = new ByteArrayInputStream(byteFile);
	                return new DefaultStreamedContent(is, "application/pdf", documentoInforme.getNombre());
	            } else {
	                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
	                return null;
	            }
			}			
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
		return null;
	}
	
	public void uploadFileFirmado(FileUploadEvent event) {
		try{
			if (documentoInforme == null) {
				JsfUtil.addMessageError("Descargue el documento.");
				return;
			}

			byte[] contenidoDocumento = event.getFile().getContents();
			documentoInforme.setContenidoDocumento(contenidoDocumento);
			documentoInforme.setNombre(event.getFile().getFileName());

			DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
			documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
			documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());

			informeForestalFacade.guardarDocumento(informeForestal.getProyecto().getCodigo(), documentoInforme,
					TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_FORESTAL, documentoTarea);
			informacionSubida = true;

			// JsfUtil.addMessageInfo("Documento subido exitosamente");
			// enviar();
			JsfUtil.addMessageInfo("Documento subido exitosamente");
			nombreDocumentoFirmado = event.getFile().getFileName();
			
		}catch(Exception e){
			JsfUtil.addMessageError("Error al guardar el documento del Informe.");
			e.printStackTrace();
		}		
	}	
	
	public void enviar()
	{
		try {
			
			Map<String, Object> params=new HashMap<>();
			
			//Si la variable remocion_cobertura_vegetal cambia, se actualiza en el bpm
			if(remocionCoberturaVegetal!=informeForestal.getRemocionCoberturaVegetal())
			{				
				params.put("remocion_cobertura_vegetal",informeForestal.getRemocionCoberturaVegetal());
			}			
			
			params.put("usuario_director",usuarioDirector.getNombre());
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

	/**
	 * PARA FIRMA CON TOKEN
	 * 
	 * @param event
	 */
	@SuppressWarnings("static-access")
	public String firmarOficio() {
		try {
			TipoDocumentoSistema tipoDocumento = null;

			String documento = null;
			if (informeSnap != null) {
				tipoDocumento = TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_SNAP;
				documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(informeForestal.getId(),
						CertificadoAmbientalInformeSnap.class.getSimpleName(), tipoDocumento);
			} else {
				tipoDocumento = TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_FORESTAL;
				documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(informeForestal.getId(),
						CertificadoAmbientalInformeForestal.class.getSimpleName(), tipoDocumento);
			}
			
			if((documentoSubido != null && !documentoSubido.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre())) 
					|| (documentoSubido != null && !JsfUtil.getSimpleDateFormat(documentoSubido.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date())))) {
				documentoSubido.setEstado(false);
				documentosFacade.actualizarDocumento(documentoSubido);				
				documentoSubido = null;
			}
			
			if(documentoSubido == null){
				generarDocumentoInforme(false);
			}

			if(documentoSubido != null){
				documento = documentosFacade.direccionDescarga(documentoSubido);
				DigitalSign firmaE = new DigitalSign();
				documentoDescargado = true;
				return firmaE.sign(documento, loginBean.getUsuario().getNombre());
			}else{
				return "";
			}			

		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * METODO DE TIPO DOCUMENTO SE USA PARA LA FIRMA ELECTRONICA
	 * 
	 * @param codTipo
	 * @return
	 */
	public TipoDocumento obtenerTipoDocumento(Integer codTipo) {
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM TipoDocumento o WHERE o.estado=true and o.id=:id");
			query.setParameter("id", codTipo);
			return (TipoDocumento) query.getResultList().get(0);
		} catch (Exception e) {
			return null;
		}
	}
}