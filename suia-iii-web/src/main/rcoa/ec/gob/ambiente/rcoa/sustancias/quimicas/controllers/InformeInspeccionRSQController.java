package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

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
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.AnalisisTecnicoRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.UbicacionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.AnalisisTecnicoRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.UbicacionSustancia;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class InformeInspeccionRSQController implements Serializable {

	private static final long serialVersionUID = -1L;

	private static final Logger LOG = Logger.getLogger(InformeInspeccionRSQController.class);
	
	//EJBs	
	@EJB
	private AnalisisTecnicoRSQFacade analisisTecnicoRSQFacade;
	
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@EJB
	private DocumentosRSQFacade documentosRSQFacade;

	@EJB
	private InformesOficiosRSQFacade informesOficiosRSQFacade;
	    
    @EJB
    private OrganizacionFacade organizacionFacade;

	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	    
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade; 
	
	@EJB
    private UbicacionSustanciaQuimicaFacade ubicacionSustanciaQuimicaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;

	//BEANs   
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;    
 	
	//LISTs
	@Getter
    private List<AnalisisTecnicoRSQ> analisisTecnicoRSQLista;
    
    @Getter
    private List<CatalogoGeneralCoa> catalogoHallazgosLista;
    
    private List<UbicacionesGeografica> proyectoUbicacionLista;
    
    private List<UbicacionSustancia> ubicacionSustanciaProyectoLista;
	
    //OBJs
    @Getter
    @Setter
    private DocumentosSustanciasQuimicasRcoa documentoInforme;
    
	@Getter
	@Setter
	private InformeOficioRSQ informe;
	
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	private RegistroSustanciaQuimica registroSustanciaQuimica;
	
	//MAPs
    private Map<String, Object> variables;
	
	//STRINGs
    @Getter
	@Setter
	private String urlPdf;
    
    private String varTramite;
    
    //INTEGERs
    private Integer numeroRevision;
	
	//BOOLEANs
    private boolean firmadoFisico;
    
	private boolean informeApoyo;
	
	@Getter
	@Setter
	private boolean token;
	
	@Setter
	@Getter
	private boolean verPdf;
	
	@Getter
	@Setter
	private DocumentosSustanciasQuimicasRcoa documentoEvidencia;
	
	@Getter
	@Setter
	private AnalisisTecnicoRSQ objetoSeleccionado;
	
	@PostConstruct
	public void init() {
		try {
			cargarDatosTarea();
			cargarDatosProyecto();			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos InformeInspeccionRSQController.");
		}
	}
	
	private void cargarDatosTarea() throws ServiceException{

		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			varTramite=(String)variables.get("tramite");
			numeroRevision=variables.containsKey("numero_revision")?(Integer.valueOf((String)variables.get("numero_revision"))):1;
			
			String nombreTarea=bandejaTareasBean.getTarea().getTaskName().toUpperCase();
			if(nombreTarea.contains("APOYO")) {
				informeApoyo=true;				
			}
		} catch (JbpmException e) {
			LOG.error("Error al recuperar variables numero_observacionescargarDatosTarea "+e.getCause()+" "+e.getMessage());
		}
	}
	
	private void cargarDatosProyecto(){		
		proyectoLicenciaCoa=proyectoLicenciaCoaFacade.buscarProyecto(varTramite);
		registroSustanciaQuimica=registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyectoLicenciaCoa);
		proyectoUbicacionLista=proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoLicenciaCoa);
		analisisTecnicoRSQLista=new ArrayList<>();
		
		if(registroSustanciaQuimica!=null) {
			
			Area areaUsuario = new Area();
			
			if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() == 1){
				areaUsuario = JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea();
			}else{
				areaUsuario = this.proyectoLicenciaCoa.getAreaInventarioForestal();
			}
	
			informe = informesOficiosRSQFacade.obtenerPorRSQArea(registroSustanciaQuimica,TipoInformeOficioEnum.INFORME_INSPECCION,numeroRevision,informeApoyo?areaUsuario:registroSustanciaQuimica.getArea());	
			ubicacionSustanciaProyectoLista=ubicacionSustanciaQuimicaFacade.obtenerUbicacionesPorRSQ(registroSustanciaQuimica);		
			
			
			if (informe == null) {
				informe = new InformeOficioRSQ(registroSustanciaQuimica,TipoInformeOficioEnum.INFORME_INSPECCION,numeroRevision,informeApoyo?areaUsuario:registroSustanciaQuimica.getArea());
			}else {
				analisisTecnicoRSQLista=analisisTecnicoRSQFacade.obtenerPorInformeOficio(informe,CatalogoTipoCoaEnum.RSQ_INFORME_HALLAZGOS);
				documentoInforme=documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_INFORME_INSPECCION_APROBADO_OBSERVADO,InformeOficioRSQ.class.getSimpleName(),informe.getId());
			}
			
//			documentoInforme = new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_INFORME_INSPECCION_APROBADO_OBSERVADO,InformeOficioRSQ.class.getSimpleName(),informe.getId(),bandejaTareasBean.getTarea().getProcessInstanceId());
			documentoInforme = new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_INFORME_INSPECCION_APROBADO_OBSERVADO,InformeOficioRSQ.class.getSimpleName(),informe.getId(),null);
			
			informe.setInformeApoyo(informeApoyo);
			
			if(analisisTecnicoRSQLista.isEmpty()) {
				catalogoHallazgosLista=catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.RSQ_INFORME_HALLAZGOS);
				
				for (UbicacionSustancia ubicacion : getUbicacionSustanciaProyectoLista()) {
					for (CatalogoGeneralCoa hallazgo : catalogoHallazgosLista) {
						analisisTecnicoRSQLista.add(new AnalisisTecnicoRSQ(informe,CatalogoTipoCoaEnum.RSQ_INFORME_HALLAZGOS,ubicacion,hallazgo));											
					}
				}
			}else{
				/**
				 * buscar documento por hallazgo
				 */
				for(AnalisisTecnicoRSQ item : analisisTecnicoRSQLista){
					if(item.getCatalogoGeneralCoa().getDescripcion().equals("Evidencia fotográfica") && item.getHabilitado()){
						DocumentosSustanciasQuimicasRcoa documento = documentosRSQFacade.obtenerDocumentoPorTipoIdTabla(TipoDocumentoSistema.RCOA_RSQ_EVIDENCIA_FOTOGRAFICA,InformeOficioRSQ.class.getSimpleName(),item.getId());
						item.setHallazgoDescripcion(documento.getNombre());
						item.setDocumento(documento);
					}
				}
			}						
		}	
		
	}
	
	public List<UbicacionSustancia> getUbicacionSustanciaProyectoLista(){
		if(informeApoyo) {
			
//			Area areaUsuario = new Area();
			
			List<Area> areasUsuario = new ArrayList<Area>();
			
			if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() == 1){
				areasUsuario.add(JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea());
			}else{
				for(AreaUsuario areaUs : JsfUtil.getLoggedUser().getListaAreaUsuario()){
					areasUsuario.add(areaUs.getArea());		
				}
						
			}			
			
			List<UbicacionSustancia> lista=new ArrayList<UbicacionSustancia>();
			for (UbicacionSustancia ubicacionSustancia : ubicacionSustanciaProyectoLista) {
				for(Area areaSe : areasUsuario){
					if(ubicacionSustancia.getArea()!=null && ubicacionSustancia.getArea().getId().intValue()==areaSe.getId().intValue()) {
						lista.add(ubicacionSustancia);
						break;
					}
				}				
			}
			return lista;
		}
		return ubicacionSustanciaProyectoLista;
	}
	
	public List<AnalisisTecnicoRSQ> obtenerHallazgos(UbicacionSustancia ubicacion){
		List<AnalisisTecnicoRSQ> lista=new ArrayList<AnalisisTecnicoRSQ>();
		for (AnalisisTecnicoRSQ item : analisisTecnicoRSQLista) {
			if(item.getUbicacionSustancia().getId().intValue()==ubicacion.getId().intValue()) {
				lista.add(item);
			}
		}
		
		return lista;
	}
	
	public void editarFormulario() {
		verPdf=false;
	}
	
	public void cumpleListener(UbicacionSustancia ubicacionSustancia) {
		if(ubicacionSustancia.getCumpleValor()!=null && ubicacionSustancia.getCumpleValor()) {
			ubicacionSustancia.setObservaciones(null);
		}
	}	
	
	private String getLabelProponente() {       
        try {
            Organizacion organizacion = organizacionFacade.buscarPorRuc(informe.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getUsuario().getNombre());
            if (organizacion != null) {
                return organizacion.getNombre();
            } 
            return informe.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getUsuario().getPersona().getNombre();
            
        } catch (ServiceException e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return null;
    }
	
	public Date getCurrentDate() {
		return new Date();
	}
	
	public void limpiarDescripcionHallazgo(AnalisisTecnicoRSQ item){
		if(!item.getHabilitado()){
			item.setHallazgoDescripcion(null);
		}		
	}

	public void guardar() {
		try {		
			
			boolean elementoSeleccionado = false;
			
			List<String> mensajesDoc = new ArrayList<>();
			for (AnalisisTecnicoRSQ item : analisisTecnicoRSQLista) {
				if(item.getHabilitado()){
					elementoSeleccionado = true;
				}
				
				if(item.getCatalogoGeneralCoa().getDescripcion().equals("Evidencia fotográfica") && item.getHabilitado()){
					if(item.getDocumento() == null || item.getDocumento().getContenidoDocumento() == null){
						mensajesDoc.add("Debe adjunta el documento de Evidencia fotográfica de " + item.getUbicacionSustancia().getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion());
					}					
				}
			}					
			
			if(!elementoSeleccionado){
				JsfUtil.addMessageError("Debe seleccionar al menos un requisito");
				return;
			}
			
			if(!mensajesDoc.isEmpty()){
				JsfUtil.addMessageError(mensajesDoc);
				return;
			}
			
			informesOficiosRSQFacade.guardarInforme(informe);
								
			for (AnalisisTecnicoRSQ item : analisisTecnicoRSQLista) {
				analisisTecnicoRSQFacade.guardar(item);
				if(item.getCatalogoGeneralCoa().getDescripcion().equals("Evidencia fotográfica") && item.getHabilitado()){
					documentosRSQFacade.guardarDocumento(varTramite, item.getDocumento(),item.getId());
				}
			}
			
			for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
				ubicacionSustanciaQuimicaFacade.guardar(item,JsfUtil.getLoggedUser());
			}			
			
			generarDocumentoInforme(true);			
			JsfUtil.addMessageInfo("Verifique la información antes de enviar.");                       

		} catch (Exception e) {
			LOG.error("error al guardar ", e);
			JsfUtil.addMessageError("Ocurrió un error al generar el documento. Comuníquese con mesa de ayuda.");
		}		
	}
	
	
	
	public void generarDocumentoInforme(boolean marcaAgua)
	{			
		try {			
			String nombreReporte= "InformeInspeccionRSQ";
			
			PlantillaReporte plantillaReporte = this.informesOficiosRSQFacade.obtenerPlantillaReporte(TipoDocumentoSistema.RCOA_RSQ_INFORME_INSPECCION_APROBADO_OBSERVADO.getIdTipoDocumento());
			File file = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new InformeInspeccionRSQHtml(informe,getUbicacionSustanciaProyectoLista(),analisisTecnicoRSQLista,proyectoUbicacionLista,getLabelProponente(),JsfUtil.getLoggedUser()),null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
	        Path path = Paths.get(file.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(file.getName()));
	        FileOutputStream fileOutputStream = new FileOutputStream(fileArchivo);
	        fileOutputStream.write(byteArchivo);
	        fileOutputStream.close();
	        urlPdf=JsfUtil.devolverContexto("/reportesHtml/"+ file.getName());
	        
	        if(!marcaAgua){
	        	JsfUtil.uploadDocumentoRSQ(file, documentoInforme);
	       	}
	          
	        verPdf=true;
	       } catch (Exception e) {
	       	e.printStackTrace();
	       }
	}	
	
	public StreamedContent descargarDocumento() {
		try {
			generarDocumentoInforme(false);
            byte[] byteFile = documentoInforme.getContenidoDocumento();
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
			documentoInforme.cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());			
	    	documentosRSQFacade.guardarDocumento(varTramite, documentoInforme,informe.getId());
			firmadoFisico=true;
			JsfUtil.addMessageInfo("El informe fue adjuntado con éxito");
		}catch(Exception e)
		{
			JsfUtil.addMessageError("Error al guardar el documento del Informe.");
			e.printStackTrace();
		}		
	}	
	
	public String firmaElectronica() {
        try {
        	generarDocumentoInforme(false);
        	documentosRSQFacade.guardarDocumento(varTramite, documentoInforme,informe.getId());        	
        	return DigitalSign.sign(documentosRSQFacade.direccionDescarga(documentoInforme.getIdAlfresco()), JsfUtil.getLoggedUser().getNombre());
        } catch (Throwable e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
            return "";
        }
    }
	
	public boolean validarFirma(){		
		if((token && documentosRSQFacade.verificarFirmaVersion(documentoInforme.getIdAlfresco()))
		||(!token && firmadoFisico)){
			return true;
		}
		return false;
	}
	
	public void enviar(){
		try {
			
			if(!validarFirma()){
				JsfUtil.addMessageError("Debe firmar el Informe Técnico");
				return;
			}
		
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectToBandeja();
		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}
	
	
	public void fileUploadListener(FileUploadEvent event) {
		
		DocumentosSustanciasQuimicasRcoa documentoEvidencia = objetoSeleccionado.getDocumento();
		if(documentoEvidencia==null) {
			documentoEvidencia=new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_EVIDENCIA_FOTOGRAFICA, InformeOficioRSQ.class.getSimpleName(), registroSustanciaQuimica.getId(), bandejaTareasBean.getTarea().getProcessInstanceId());			
		}
		documentoEvidencia.cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());
		
		objetoSeleccionado.setDocumento(documentoEvidencia);
		objetoSeleccionado.setHallazgoDescripcion(documentoEvidencia.getNombre());
	}
}