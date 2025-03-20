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
import java.util.HashMap;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeSnap;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeSnapEspecie;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.InformeInspeccionSnapCAFacade;
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
public class InformeInspeccionSnapCAController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1683511338517439792L;

	private static final Logger LOG = Logger.getLogger(InformeInspeccionSnapCAController.class);

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private InformeInspeccionSnapCAFacade informeSnapFacade;
	
	
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    
    @EJB
    private OrganizacionFacade organizacionFacade;

	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
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
	
	
	@Setter
	private List<CertificadoAmbientalInformeSnapEspecie> especiesLista;
	
	@Getter
	@Setter
	private CertificadoAmbientalInformeSnapEspecie especie;
	
	@Setter
	@Getter
	private UploadedFile fileEstadoVegetacion;
	
	@Setter
	@Getter
	private String nombreFileEstadoVegetacion;
	
	private boolean actualizaDoc=false;
	
	
	@Setter
	@Getter
	private boolean verPdf;
	
    @Getter
	@Setter
	private String urlPdf;
    
    @Getter
	@Setter
	private Usuario usuarioCoordinador,usuarioDirector;
    
	@Getter
	@Setter
	byte[] byteArchivo; 
	
	@Getter
	@Setter
	private File fileArchivo;
	
    @Getter
    @Setter
    private Documento documentoInforme;
    
    @Getter
    private String areaInterseca;
    
    private boolean intersecaEstatal=false;
    
    private String usuario_coordinador;
    
    /**
   	 * FIRMA ELECTRONICA
   	 */
   	@Getter
   	@Setter
   	private boolean token, ambienteProduccion, firmaSoloToken, documentoDescargado, informacionSubida,mostrarBotonFirmar;
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
   	
   	public static final String TIPO_DOCUMENTO = "CERTIFICADO AMBIENTAL ESTADO VEGETACION";


	@PostConstruct
	public void init() {
		try {
			
			informeSnap = informeSnapFacade.getInformePorIdProyecto(proyectosBean.getProyecto().getId());
			if (informeSnap == null) {
				informeSnap = new CertificadoAmbientalInformeSnap();
				informeSnap.setProyecto(proyectosBean.getProyecto());
			}else{
				especiesLista=informeSnapFacade.getEspeciesPorInforme(informeSnap.getId());
			}
			
			if(especiesLista==null)
				especiesLista=new ArrayList<CertificadoAmbientalInformeSnapEspecie>();
			
			areaInterseca= certificadoInterseccionFacade.getDetalleInterseccion(proyectosBean.getProyecto().getCodigo());
			
			Map<String, Object> variables=procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId());			
			intersecaEstatal=Boolean.valueOf((String)variables.get("interseca_subsistemas_estatal"));
			usuario_coordinador=(String)variables.get("usuario_coordinador");
			verPdf=false;
			
			/**
			 * FIRMA ELECTRONICA
			 */
			
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			token = true;

			if (!ambienteProduccion) {
				verificaToken();
				documentoDescargado=true;
			}
			urlAlfresco = "";
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos certificadoAmbientalInformeSnap.");
		}
	}
	
	public void crearEspecie(String tipo)
	{
		especie=new CertificadoAmbientalInformeSnapEspecie();
		especie.setTipo(tipo);
	}
	
	public void agregarEspecie()
	{
		especiesLista.add(especie);
	}
	
	public void editarEspecie(CertificadoAmbientalInformeSnapEspecie especie)
	{
		this.especie=especie;
	}
	
	public void eliminarEspecie(CertificadoAmbientalInformeSnapEspecie especie)
	{
		if(especie.getId()==null)
			especiesLista.remove(especie);
		else 
			especie.setEstado(false);
	}	
	
	public List<CertificadoAmbientalInformeSnapEspecie> especiesPorTipo(String tipo)
	{
		List<CertificadoAmbientalInformeSnapEspecie> especies=new ArrayList<CertificadoAmbientalInformeSnapEspecie>();
		for (CertificadoAmbientalInformeSnapEspecie especie : especiesLista) {
			if(especie.getTipo().contains(tipo) && especie.getEstado())
				especies.add(especie);
		}
		return especies;
	}
	
	
	public void cambiarUnidadListener(String campo)
	{
		switch (campo) {
		case "longitud":
			informeSnap.setLongitud(null);
			break;
		case "superficie":
			informeSnap.setSuperficie(null);
			break;
		case "valorOtraExtension":
			informeSnap.setValorOtraExtension(null);
			break;				

		default:
			break;
		}
	}
	
	public void fileUpload(FileUploadEvent event) {
		fileEstadoVegetacion = event.getFile();
		informeSnap.setEstadoVegetacionDocumento(UtilDocumento.generateDocumentPDFFromUpload(fileEstadoVegetacion.getContents(),fileEstadoVegetacion.getFileName()));
		actualizaDoc=true;
	}
	
	public StreamedContent descargar() {
		try {
            byte[] byteFile = informeSnapFacade.descargarFile(informeSnap.getEstadoVegetacionDocumento());
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                return new DefaultStreamedContent(is, "application/pdf",  informeSnap.getEstadoVegetacionDocumento().getNombre());
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
	
	
	public void editarFormulario() {
		verPdf=false;
	}
	
	private void calificar()
	{		
		Integer p01=informeSnap.getActividadPermitida()?1:0;
		Integer p02=informeSnap.getIntersecaRamsar()?0:1;
		Integer p03=informeSnap.getTerritorioAnsestral()?1:0;
		Integer p04=informeSnap.getEstadoVegetacion().contains("PRIMARIO")?0:1;
		Integer p05=informeSnap.getVegetacionNativa()?0:1;
		Integer p06=informeSnap.getAfectacionValoresConservacion()?0:1;
		Integer p07=informeSnap.getAfectacionEspecies()?0:1;
		Integer p08=informeSnap.getAfectacionFuentesHidricas()?0:1;
		Integer p09=informeSnap.getAfectacionServiciosEcosistemicos()?0:1;
		Integer p10=informeSnap.getExisteProyectosInversion()?0:1;
		
		Integer calificacion=p01+p02+p03+p04+p05+p06+p07+p08+p09+p10;	
		
		informeSnap.setPuntaje(calificacion);
		//Ticket#10253935 Validacion >=9 es favorable
		informeSnap.setPronunciamientoFavorable(calificacion>=9?true:false);
	}
	
	private String getLabelProponente() {
        String label = informeSnap.getProyecto().getUsuario().getPersona().getNombre();
        try {
            Organizacion organizacion = organizacionFacade.buscarPorPersona(informeSnap.getProyecto().getUsuario().getPersona(), informeSnap.getProyecto().getUsuario().getNombre());
            if (organizacion != null) {
                return organizacion.getNombre();
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        return label;
    }

	public void guardar() {
		try {
			
			if((informeSnap.getUnidadLongitud()==null || informeSnap.getUnidadLongitud().isEmpty()) && (informeSnap.getUnidadSuperficie()==null || informeSnap.getUnidadSuperficie().isEmpty()) && informeSnap.getOtraExtension().isEmpty()){
				JsfUtil.addMessageError("Debe seleccionar al menos una medida");
	    		return;
			}
			
			if(informeSnap.getUnidadLongitud()!=null && !informeSnap.getUnidadLongitud().isEmpty() && informeSnap.getLongitud().equals(0.0))
			{
				JsfUtil.addMessageError("Agregar Valor en medida Longitud");
	    		return;
			}
			
			if(informeSnap.getUnidadSuperficie()!=null && !informeSnap.getUnidadSuperficie().isEmpty() && informeSnap.getSuperficie().equals(0.0))
			{
				JsfUtil.addMessageError("Agregar Valor en medida Superficie");
	    		return;
			}
			
			if(!informeSnap.getOtraExtension().isEmpty() && (informeSnap.getUnidadOtraExtension()==null || informeSnap.getUnidadOtraExtension().isEmpty() || informeSnap.getValorOtraExtension().equals(0.0)))
			{
				JsfUtil.addMessageError("Agregar Unidad/Valor en 'Otra Medida'");
	    		return;
			}			

			
			if(informeSnap.getEstadoVegetacionDocumento()==null)
			{
				JsfUtil.addMessageError("Agregar Documento Estado Vegetación");
	    		return;
			}
			
			informeSnap.setCodigo(informeSnap.getCodigo().toUpperCase());
			if(informeSnapFacade.codigoUsado(informeSnap))
			{
				JsfUtil.addMessageError("El código de infome ya existe.");
	    		return;
			}
			
			if(actualizaDoc){
											
				DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		    	documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
		    	informeSnap.getEstadoVegetacionDocumento().setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
		    	informeSnap.getEstadoVegetacionDocumento().setIdTable(informeSnap.getProyecto().getId());
		    	documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
		    	
		    	String fileName=informeSnap.getEstadoVegetacionDocumento().getNombre();
		    	if(!fileName.contains("EstadoVegetacionInformeSnap-"))
		    		informeSnap.getEstadoVegetacionDocumento().setNombre("EstadoVegetacionInformeSnap-"+fileName);
		    	Documento documento=informeSnapFacade.guardarDocumento(informeSnap.getProyecto().getCodigo(), informeSnap.getEstadoVegetacionDocumento(), TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_ESTADO_VEGETACION, documentoTarea);
		    	
		    	if(documento==null)
		    	{
		    		JsfUtil.addMessageError("Error al Guardar Documento Estado Vegetacion");
		    		return;
		    	}
		    	informeSnap.setEstadoVegetacionDocumento(documento);
		    	actualizaDoc=false;
			}
	    	
			if(informeSnap.getOtraExtension().isEmpty())
			{
				informeSnap.setUnidadOtraExtension(null);
				informeSnap.setValorOtraExtension(null);
			}
	    	
			calificar();
			informeSnapFacade.guardarInforme(informeSnap);
			for (CertificadoAmbientalInformeSnapEspecie especie : especiesLista) {
				especie.setInformeSnap(informeSnap);
				informeSnapFacade.guardarInformeEspecies(especie);
			}
			
			if(!intersecaEstatal){			
				usuarioCoordinador=usuarioFacade.buscarUsuario(usuario_coordinador);
				if(usuarioCoordinador==null)			
				{
					JsfUtil.addMessageError("No se encontró el usuario Coordinador en "+informeSnap.getProyecto().getAreaResponsable().getAreaName());
					return;
				}
			}
			
			
			generarDocumentoInforme(true);	
			verPdf=true;
			mostrarBotonFirmar=true;
			JsfUtil.addMessageInfo("Verifique la información antes de enviar.");                       

		} catch (Exception e) {
			LOG.error("error al guardar ", e);
			JsfUtil.addMessageError("Ocurrió un error al guardar certificadoAmbientalInformeSnap");
		}		
	}	
	
	public void generarDocumentoInforme(boolean marcaAgua)
	{			
		FileOutputStream file;
		try {			
			String nombreReporte= "Informe_Inspeccion_Snap";
			
			PlantillaReporte plantillaReporte = this.informeSnapFacade.obtenerPlantillaReporte(TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_SNAP.getIdTipoDocumento());
			File informePdf = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new InformeInspeccionSnapCAHtml(informeSnap,especiesLista,getLabelProponente(),areaInterseca,intersecaEstatal,intersecaEstatal?null:loginBean.getUsuario(),intersecaEstatal?loginBean.getUsuario():usuarioCoordinador),null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
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
	       		documentoInforme.setIdTable(informeSnap.getId());	       		
	       		documentoInforme.setNombreTabla(CertificadoAmbientalInformeSnap.class.getSimpleName());
	       		documentoInforme.setNombre("CertificadoAmbientalInformeSnap"+informeSnap.getProyecto().getCodigo()+".pdf");
	       		documentoInforme.setExtesion(".pdf");
	        }
	          
	        verPdf=true;
	       } catch (Exception e) {
	       	e.printStackTrace();
	       }
	}
	
	public StreamedContent descargarDocumento() {
		try {
			generarDocumentoInforme(false);
            byte[] byteFile = byteArchivo;
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                return new DefaultStreamedContent(is, "application/pdf", documentoInforme.getNombre());
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
	
	public void uploadFileFirmado(FileUploadEvent event) {
		try{
			if(documentoInforme==null)
			{
				JsfUtil.addMessageError("Descargue el documento.");
				return;
			}			
			
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoInforme.setContenidoDocumento(contenidoDocumento);
			documentoInforme.setNombre(event.getFile().getFileName());
			
			DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
	    	documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());    	
	    	documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
	    	
	    	informeSnapFacade.guardarDocumento(informeSnap.getProyecto().getCodigo(), documentoInforme, TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_INFORME_SNAP, documentoTarea);
			
	    	JsfUtil.addMessageInfo("Documento subido exitosamente");
			nombreDocumentoFirmado = event.getFile().getFileName();
			informacionSubida = true;
//			enviar();
		}catch(Exception e)
		{
			JsfUtil.addMessageError("Error al guardar el documento del Informe.");
			e.printStackTrace();
		}
		
	}
	
	public void enviar()
	{
		try {

			List<DocumentosTareasProceso> docs = documentosFacade.obtenerDocumentosProcesos(documentoSubido.getId());
			
			for(DocumentosTareasProceso docP : docs){
				docP.setProcessInstanceId(bandejaTareasBean.getProcessId());
				documentosFacade.guardarDocumentoProceso(docP);
			}
			
			List<Usuario>uList = new ArrayList<Usuario>();
			Area area = new Area();
			if(informeSnap.getProyecto().getAreaResponsable().getArea() != null){
				area = informeSnap.getProyecto().getAreaResponsable().getArea();
			}else{
				area = informeSnap.getProyecto().getAreaResponsable();
			}
			
			String areaNombre = area.getAreaName();
			
			try {				
				uList=usuarioFacade.buscarUsuariosPorRolYArea(Constantes.ROL_DIRECTOR_EA, informeSnap.getProyecto().getAreaResponsable().getArea());
			} catch (Exception e) {
				e.printStackTrace();
				JsfUtil.addMessageError("Ocurrió un error al enviar la información. Por favor comunicarse con mesa de ayuda.");
				System.out.println("No se encontró el usuario Autoridad Ambiental en "+ areaNombre);
				return;
			}
			
			if(uList==null || uList.size()==0)			
			{
				JsfUtil.addMessageError("Ocurrió un error al enviar la información. Por favor comunicarse con mesa de ayuda.");
				System.out.println("No se encontró el usuario Autoridad Ambiental en "+informeSnap.getProyecto().getAreaResponsable().getArea().getAreaName());
				return;
			}else{
				usuarioDirector=uList.get(0);
			}
			
			
			Map<String, Object> params=new HashMap<>();
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
						
			if (documentoSubido != null && documentoSubido.getIdAlfresco() != null) {
				String documento = documentosFacade.direccionDescarga(documentoSubido);
				DigitalSign firmaE = new DigitalSign();
				documentoDescargado = true;
				return firmaE.sign(documento, loginBean.getUsuario().getNombre());

			} else
				return "";
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.====>"+e);
			return "";
		}		
	}
	
	public void guardarDocumento(){
		try {
			generarDocumentoInforme(false);
			
			documentoSubido = documentosFacade.guardarDocumentoAlfresco(informeSnap.getProyecto().getCodigo(),
							"CATEGORIA-UNO",
							null,
							documentoInforme,
							TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_ESTADO_VEGETACION,
							null);	
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('signDialog').show();");
			
		} catch (Exception e) {
			e.printStackTrace();
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



	/**
	 * Sube la informacion firmada En el panel
	 * 
	 * @param event
	 */
	public void uploadListenerInformacionFirmada(FileUploadEvent event) {
		System.out.println("Valor de documentoDescargado===========>" + documentoDescargado);

		if (documentoDescargado) {
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD;

			// if
			// (adjuntarPronunciamientoCertificadoViabilidadBean.getTipoPronunciamiento().equals("Biodiversidad"))
			// {
			tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD;
			auxTipoDocumento = obtenerTipoDocumento(
					TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD.getIdTipoDocumento());

			// } else if
			// (adjuntarPronunciamientoCertificadoViabilidadBean.getTipoPronunciamiento().equals("Forestal"))
			// {
			tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD;
			auxTipoDocumento = obtenerTipoDocumento(
					TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD.getIdTipoDocumento());
			// }
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoInformacionManual = new Documento();
			documentoInformacionManual.setId(null);
			documentoInformacionManual.setContenidoDocumento(contenidoDocumento);
			documentoInformacionManual.setNombre(event.getFile().getFileName());
			documentoInformacionManual.setExtesion(".pdf");
			documentoInformacionManual.setMime("application/pdf");
			documentoInformacionManual.setNombre("Certificado_Viabilidad.pdf");

			documentoInformacionManual.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
			documentoInformacionManual.setTipoDocumento(auxTipoDocumento);

			informacionSubida = true;

			nombreDocumentoFirmado = event.getFile().getFileName();
		} else {
			JsfUtil.addMessageError(
					"No ha descargado el documento para la firmas");
		}
	}

	/**
	 * Descarga la informacion del panel
	 * 
	 * @return
	 * @throws IOException
	 */
	public StreamedContent descargarInformacion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent;
			Documento auxdoc = new Documento();
			Boolean generar = false;
			
			documentoContent = documentosFacade.descargar(auxdoc.getIdAlfresco());
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(auxdoc.getNombre());
				documentoDescargado = true;
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		
		}catch (Exception e) {
			JsfUtil.addMessageError(
					"Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
}