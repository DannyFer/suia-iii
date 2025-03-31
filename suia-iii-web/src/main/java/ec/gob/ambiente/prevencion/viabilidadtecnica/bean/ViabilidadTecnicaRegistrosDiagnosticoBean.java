package ec.gob.ambiente.prevencion.viabilidadtecnica.bean;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import ec.gob.ambiente.prevencion.viabilidadtecnica.controller.ViabilidadTecnicaRegistrosDiagnosticoController;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.PlanMonitoreoEia;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaParametrosDiagnostico;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaRegistrosDiagnostico;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * 
 * @author Rafael Richards
 * @version Revision: 1.0
 * Fecha: 28/09/2015
 * 
 */
@ViewScoped
@ManagedBean
public class ViabilidadTecnicaRegistrosDiagnosticoBean implements Serializable {


	private static final long serialVersionUID = 2224093339465028043L;

	private static final Logger LOGGER = Logger.getLogger(ViabilidadTecnicaRegistrosDiagnosticoBean.class);

	@EJB	
	ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;	
	
	@EJB	
	ProcesoFacade procesoFacade;	
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@Setter	
	private PlanMonitoreoEia planMonitoreoEia;
	
    @Getter
	private int valorAdjunto;
	
    @Getter
    @Setter
    private File imagenFotografica; 
    
    @Getter
    @Setter
    private boolean esCierre;
    
    @Getter
    @Setter
    private boolean soloLectura;
    
    @EJB	
    ViabilidadTecnicaRegistrosDiagnosticoController viabilidadTecnicaRegistrosDiagnosticoController;
	
	Integer idProyecto;

    @Getter
    @Setter
	ViabilidadTecnicaRegistrosDiagnostico viabilidadTecnicaRegistrosDiagnostico;

    @Getter
    @Setter
    EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico;

    @Getter
    @Setter    
    ViabilidadTecnicaParametrosDiagnostico viabilidadTecnicaParametrosDiagnostico;
    
    @Getter
    @Setter     
    private String densidadBasura;
    
    @Getter
    @Setter     
    private String poblacionServida;
    
    @Getter
    @Setter     
    private String anoInicio;
    
    @Getter
    @Setter     
    private String poblacionProyectada;    

    @Getter
    @Setter     
    private String residuosDiario;
    
    @Getter
    @Setter     
    private String residuosMensual;
    
    @Getter
    @Setter     
    private String residuosAnual;    
    
	@EJB
	private CrudServiceBean crudServiceBean;

	//---------------------------------------------------------------------------------------------------------				
    public void setValorAdjunto(int valor)
    {
    	valorAdjunto = valor;
    }	
	//---------------------------------------------------------------------------------------------------------				
    public void adjuntarPDF(FileUploadEvent event)
    {
    	try 
    	{
			if (event != null) 
			{
				imagenFotografica = JsfUtil.upload(event);
				switch(valorAdjunto)
				{
					case 3014: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DCT_MANEJO_ESCORENTIA_SUPERFICIAL);break;
					case 3015: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DCT_MANEJO_EROSION_SEDIMENTACION);break;
					case 3016: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DCT_MANEJO_LIXIVIADOS);break;
					case 3017: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DCT_MANEJO_BIOGAS);break;
					case 3018: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DCT_ESTABILIDAD_CIERRE_TECNICO);break;
					case 3019: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DCT_DISENO_CAPA_COBERTURA);break;
					case 3020: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DCT_OBRAS_COMPLEMENTARIAS);break;
					case 3021: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DCT_USO_FINAL_SUELO);break;
					case 3022: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DCT_DISENO_CIERRE_TECNICO);break;
					case 3023: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_BDCE_BASE_DISENO_CELDA_EMERGENTE);break;
					case 3024: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DDCE_DISENO_DEFINITIVO_CELDA_EMERGENTE);break;
					case 3025: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DDCE_MANEJO_ESCORENTIA_SUPERFICIAL);break;
					case 3026: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DDCE_MANEJO_EROSION_SEDIMENTACION);break;
					case 3027: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DDCE_MANEJO_LIXIVIADOS);break;
					case 3028: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DDCE_MANEJO_BIOGAS);break;
					case 3029: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DDCE_ESTABILIDAD_CELDA_EMERGENTE);break;
					case 3030: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DDCE_DISENO_CAPA_COBERTURA);break;
					case 3031: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DDCE_OBRAS_COMPLEMENTARIAS);break;
					case 3032: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DDCE_DISENO_CELDA_DESECHOS_SANITARIOS);break;
					case 3033: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_PRESUPUESTO_INVERSION);break;
					case 3034: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_PRESUPUESTO_TOTAL);break;
					case 3035: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_MANUAL_OPERACION_MANTENIMIENTO);break;
					case 3036: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ESPECIFICACIONES_TECNICAS);break;
					case 3037: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_CRONOGRAMA_PROYECTO);break;
					case 3038: ingresarPDFAdjunto(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ANEXO_OFICIO_ACEPTACION);break;
				}
			}
		}
    	catch (Exception e)
    	{
			LOGGER.error("Error en el Metodo adjuntarImagen", e);
    		e.printStackTrace();
		}
    }
	//---------------------------------------------------------------------------------------------------------			 
    @PostConstruct
	private void init() 
	{
		esCierre = false;
		
		soloLectura = true;
    	
    	viabilidadTecnicaRegistrosDiagnostico = new ViabilidadTecnicaRegistrosDiagnostico();
		
    	// Esta tendría que obtnerse del proyecto 
    	estudioViabilidadTecnicaDiagnostico = new EstudioViabilidadTecnicaDiagnostico();
		
    	// Loss valores de la pantalla se llenaran aqui
    	viabilidadTecnicaParametrosDiagnostico = new ViabilidadTecnicaParametrosDiagnostico();
		
//		String value = "";
		valorAdjunto = 0;
		try
		{
//			value = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId())
//				     .get("idProyecto").toString();
		}
		catch (Exception e) 
		{
			LOGGER.error("Error en el Metodo Init", e);
			e.printStackTrace();
		}		
	}
	//---------------------------------------------------------------------------------------------------------			
	 public ProyectoLicenciamientoAmbiental cargarProyecto(Integer id) 
	 {
		  try
		  {
			  return proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(id);
		  } 
		  catch (Exception e) 
		  {
			  return null;
		  }
	 }	
	//---------------------------------------------------------------------------------------------------------
	 public void ingresarPDFAdjunto(File file,TipoDocumentoSistema tipoDocumento) throws Exception 
	    {
		 	MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
	    	byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
	    	Documento documento1 = new Documento();
//	    	documento1.setIdTable(proyectoLicenciamientoAmbiental.getId());
	    	documento1.setIdTable(1234);	    	
	    	String ext = getExtension(file.getAbsolutePath());
	    	documento1.setNombre(file.getName());
	    	documento1.setExtesion(ext);
	    	documento1.setNombreTabla(EstudioViabilidadTecnicaDiagnostico.class.getSimpleName());
	    	documento1.setMime(mimeTypesMap.getContentType(file));
	    	documento1.setContenidoDocumento(data);
	    	DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
//	    	documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
	    	documentoTarea.setIdTarea(1234);
//	    	documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
	    	documentoTarea.setProcessInstanceId(1234);
//	    	return documentosFacade.guardarDocumentoAlfresco(proyectoLicenciamientoAmbiental.getCodigo(),"Viabilidad_Tecnica", bandejaTareasBean.getProcessId(), documento1, tipoDocumento,documentoTarea);
		    JsfUtil.addMessageInfo("El archivo " + file.getName() + " fue adjuntado correctamente.");
	    	documentosFacade.guardarDocumentoAlfresco("MAE-SOL-SUBT-2015-000002","Viabilidad_Tecnica", new Long(1234), documento1, tipoDocumento,documentoTarea); 
	    }
		//---------------------------------------------------------------------------------------------------------	    
	    private String getExtension(String fullPath)
	    {
	        String extension = "";
	        int i = fullPath.lastIndexOf('.');
	        if (i > 0) 
	        {
	            extension = fullPath.substring(i + 1);
	        }
	        return extension;
	    } 
		//---------------------------------------------------------------------------------------------------------
	    public void salvarDatosRegistros()
		{
	    	ingresarViabilidadTecnicaRegistrosDiagnostico("26", densidadBasura);
	    	ingresarViabilidadTecnicaRegistrosDiagnostico("46", poblacionServida);
	    	ingresarViabilidadTecnicaRegistrosDiagnostico("28", anoInicio);
	    	ingresarViabilidadTecnicaRegistrosDiagnostico("27", poblacionProyectada);
	    	ingresarViabilidadTecnicaRegistrosDiagnostico("31", residuosDiario);
	    	ingresarViabilidadTecnicaRegistrosDiagnostico("32", residuosMensual);
	    	ingresarViabilidadTecnicaRegistrosDiagnostico("33", residuosAnual);	   
		    JsfUtil.addMessageInfo("Informacion Registrada Satisfactoriamente...");	    	
		}
		//---------------------------------------------------------------------------------------------------------
	    public void obtenerDatosRegistros()
		{
	    	// Es importante resaltar que el código de id de viabilidad esta fijo, hay que traerlo del proyecto
	    	// en este caso de la prueba es el 39
	    	
	    	densidadBasura = viabilidadTecnicaRegistrosDiagnosticoController.buscarRegistrosViabilidadTecnica("39","26");
	    	poblacionServida = viabilidadTecnicaRegistrosDiagnosticoController.buscarRegistrosViabilidadTecnica("39","46");
	    	anoInicio = viabilidadTecnicaRegistrosDiagnosticoController.buscarRegistrosViabilidadTecnica("39","28");
	    	poblacionProyectada = viabilidadTecnicaRegistrosDiagnosticoController.buscarRegistrosViabilidadTecnica("39","27");
	    	residuosDiario =  viabilidadTecnicaRegistrosDiagnosticoController.buscarRegistrosViabilidadTecnica("39","31");
	    	residuosMensual = viabilidadTecnicaRegistrosDiagnosticoController.buscarRegistrosViabilidadTecnica("39","32");
	    	residuosAnual = viabilidadTecnicaRegistrosDiagnosticoController.buscarRegistrosViabilidadTecnica("39","33");   	
		}
		//---------------------------------------------------------------------------------------------------------
	    public void ingresarViabilidadTecnicaRegistrosDiagnostico(String id, String valorParametro)
		{
			  try
			  {
			    	viabilidadTecnicaRegistrosDiagnostico = new ViabilidadTecnicaRegistrosDiagnostico();
			    	viabilidadTecnicaRegistrosDiagnosticoController.ingresarViabilidadTecnicaRegistrosDiagnostico(id, valorParametro, estudioViabilidadTecnicaDiagnostico, viabilidadTecnicaParametrosDiagnostico, viabilidadTecnicaRegistrosDiagnostico);
			  } 
			  catch (Exception e) 
			  {
				    JsfUtil.addMessageInfo("Error al Ingresar los Registros de Diagnostico de Viabilidad Tecnica. "+ e.getMessage());
			  }
		}
		//---------------------------------------------------------------------------------------------------------
}
