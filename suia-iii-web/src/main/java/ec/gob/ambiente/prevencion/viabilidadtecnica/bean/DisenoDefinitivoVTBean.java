package ec.gob.ambiente.prevencion.viabilidadtecnica.bean;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.prevencion.viabilidadtecnica.controller.DisenoDefinitivoVTController;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.DisenoDefinitivoVTFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DisenoDefinitivoVTBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8997777877618654329L;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{disenoDefinitivoVTController}")
	private DisenoDefinitivoVTController disenoDefinitivoVTController;

	
	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	//@Setter
	private Integer valorAdjunto;

	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;
	
	@Setter
	private EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico;
	
	@Setter
	private EstudioViabilidadTecnica estudioViabilidadTecnica;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
    @Getter
    @Setter
    private File imagenFotografica; 
    
	@Setter
	@Getter	
	private Documento anexoFotograficoDoc;	
	
	@EJB
	private DisenoDefinitivoVTFacade disenoDefinitivoVTFacade;
	
	@Getter
	 @Setter
	 private Integer lnIdEstudioViabilidadTecnica;
	

	private static final Logger LOGGER = Logger
			.getLogger(DisenoDefinitivoVTBean.class);

	

	public void setValorAdjunto(Integer valor) {
		this.valorAdjunto = valor;
		
		System.out.println("valorAdjunto"+valor);
	}

	
	
	@PostConstruct
	private void init() {
		valorAdjunto =0;
		
		setLnIdEstudioViabilidadTecnica(31);//se recupera del proceso

		 estudioViabilidadTecnicaDiagnostico = new EstudioViabilidadTecnicaDiagnostico();
		  cargaDiagnosticoFactibilidad();//carga la data por idEstudioViabilidadTecnica
		
		proyectoLicenciamientoAmbiental = new ProyectoLicenciamientoAmbiental();
		estudioViabilidadTecnica= new EstudioViabilidadTecnica();
	}

	public EstudioViabilidadTecnicaDiagnostico getEstudioViabilidadTecnicaDiagnostico() {
		return estudioViabilidadTecnicaDiagnostico == null ? estudioViabilidadTecnicaDiagnostico = new EstudioViabilidadTecnicaDiagnostico()
				: estudioViabilidadTecnicaDiagnostico;
	}
	
	
	
	public ProyectoLicenciamientoAmbiental getProyectoLicenciamientoAmbiental() {

		return proyectoLicenciamientoAmbiental == null ? proyectoLicenciamientoAmbiental = new ProyectoLicenciamientoAmbiental()
				: proyectoLicenciamientoAmbiental;
	}

	public EstudioViabilidadTecnica getEstudioViabilidadTecnica() {
		
		return estudioViabilidadTecnica == null ? estudioViabilidadTecnica = new EstudioViabilidadTecnica()
		: estudioViabilidadTecnica;
	}

	public void guardar() {
		System.out.println("guardar bean");
		estudioViabilidadTecnica.setFechaCreacion(new Date());
		System.out.println("Nombre"+JsfUtil.getLoggedUser().getNombre());
		estudioViabilidadTecnica.setUsuarioCreacion(JsfUtil.getLoggedUser().getNombre());	
		System.out.println("Topografía"+estudioViabilidadTecnicaDiagnostico.getTopografia());
		estudioViabilidadTecnicaDiagnostico.setTopografia(estudioViabilidadTecnicaDiagnostico.getTopografia());
		disenoDefinitivoVTController.guardar(estudioViabilidadTecnica,estudioViabilidadTecnicaDiagnostico);

	}

			
	
    public void adjuntarDocumento(FileUploadEvent event)
    {
     try 
     {   
         System.out.println("valorAdjunto try:"+valorAdjunto);
    	 if (event != null) 
    	 {     		 
             System.out.println("evento != null");
    		imagenFotografica = JsfUtil.upload(event);
    		 
         System.out.println("valorAdjunto:"+valorAdjunto);
        
		switch(valorAdjunto)
		{ 
		 //Topografía
		 case 3039: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_TOPOGRAFIA);break;
		 //Suelos, geología y geotécnia
		 case 3040: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_SUELOS_GEOLOGIA_GEOTECNICA);break;
		 //Análisis hidrogeológico y analisis meteorologico
		 case 3041: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ANAISIS_HIDROMETEO);break;
		 //Aguas superficiales
		 case 3042: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_AGUAS_SUPERFICIALES);break;
		 //aprovechamiento del sistema existente
		 case 3043: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_APROV_SISTEMA_EXISTENTE);break;
		 //Almacenamiento Temporal
		  case 3044: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_APROV_TEMPORAL);break;
		 //Diseño del sistema de barrido público
		  case 3045: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DISENO_BARRIDO);break;
		 //diseño del sistema de recolección y transporte
		  case 3046: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DISENO_RECOLECCION_TRANSPORTE);break;
		 //diseño de las estaciones de transferencia
		  case 3047: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DISENO_ESTACIONES_TRANSFERENCIA);break;
		 //Diseño del sistema de aprovechamiento
		  case 3048: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DISENO_SISTEMA_APROVECHAMIENTO);break;
		 //Diseño del drenaje Pluvial
		  case 3049: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DISENO_DRENAJE_PLUVIAL);break;
		 //Diseño del sistema de dispoción final (infraestructura del relleno, relleno Propiamente dicho, costrucciones auxiliares y accesorios
		  case 3050: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DISENO_SISTEMA_DISPOSICION_FINAL);break;
		 //Diseño del sistema de tratamiento de desechos sanitarios
		  case 3051: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DISENO_SISTEMA_TRATAMIENTO);break;
		 // Celda para los desechos sólidos sanitarios
		  case 3052: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_CELDA_DESECHOS_SOLIDOS);break;
		 //Diseño del sistema de tratamiento de lixiviados
		  case 3053: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DISENO_SISTEMA_TRATAMIENTO_LIXIVIADO);break;
		 //Diseño eléctrico
		  case 3054: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DISENO_ELECTRICO);break;
		 //Diseño del modelo de gestión
		  case 3055: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_DISENO_MODELO_GESTION);break;
		 //Propiedad y derecho de uso
		  case 3056: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_PROPIEDAD_DERECHO_USO);break;
		 //PRESUPUESTO DE INVERSIÓN  
		  case 3033: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_PRESUPUESTO_INVERSION);break;
		 //PRESUPUESTO DE LA GESTIÓN INTEGRA  DE RESIDUOS SÓLIDOS INCLUIDO OPERACION Y MANTENIMIENTO
		  case 3057: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_PRESUPUESTO_GESTION_INTEGRA);break;
		 //EVALUACIÓN ECONOMICA Y FINANCIERA DEL PROYECTO 
		  case 3058: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_EVAlUACION_ECONOMICA_FINANCIERA);break;      
		 //ESPECIFICACIONES TÉCNICAS
		  case 3036: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ESPECIFICACIONES_TECNICAS);break;
		 //MANUAL DE OPERACIÓN Y MANTENIMIENTO
		  case 3035: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_MANUAL_OPERACION_MANTENIMIENTO);break;
		 //CRONOGRAMA DE EJECUCIÓN DEL PROYECTO
		  case 3037: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_CRONOGRAMA_PROYECTO);break;
		 // ACEPTACIÓN DE OFICIO
		  case 3038: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ANEXO_OFICIO_ACEPTACION);break;
		 
		  default: JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " no fue adjuntado.");break;
		 }

    //   JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " fue adjuntado correctamente.");
   }
  }
     catch (Exception e)
     {
   LOGGER.error("Error en el Metodo adjuntarImagen", e);
      e.printStackTrace();
  }
    }

	
	  public Documento ingresarDocumento(File file,TipoDocumentoSistema tipoDocumento) throws Exception 
	     {
	    MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
	      byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
	      Documento documento1 = new Documento();
//	      documento1.setIdTable(proyectoLicenciamientoAmbiental.getId());
	      documento1.setIdTable(1234);      
	      String ext = getExtension(file.getAbsolutePath());
	      documento1.setNombre(file.getName());
	      documento1.setExtesion(ext);
	      documento1.setNombreTabla(EstudioViabilidadTecnicaDiagnostico.class.getSimpleName());
	      documento1.setMime(mimeTypesMap.getContentType(file));
	      documento1.setContenidoDocumento(data);
	      DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
//	      documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
	      documentoTarea.setIdTarea(1234);
//	      documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
	      documentoTarea.setProcessInstanceId(1234);
//	      return documentosFacade.guardarDocumentoAlfresco(proyectoLicenciamientoAmbiental.getCodigo(),"Viabilidad_Tecnica", bandejaTareasBean.getProcessId(), documento1, tipoDocumento,documentoTarea);
	      JsfUtil.addMessageInfo("El archivo " + file.getName() + " fue adjuntado correctamente.");
	     
	      return documentosFacade.guardarDocumentoAlfresco("MAE-RA-2015-211963","Viabilidad_Tecnica", new Long(1234), documento1, tipoDocumento,documentoTarea);  
	     }

	// ---------------------------------------------------------------------------------------------------------
	private String getExtension(String fullPath) {
		String extension = "";
		int i = fullPath.lastIndexOf('.');
		if (i > 0) {
			extension = fullPath.substring(i + 1);
		}
		return extension;
	}
	
	
	public void cargaDiagnosticoFactibilidad() {
		  try {
		   estudioViabilidadTecnicaDiagnostico = disenoDefinitivoVTFacade.cargarDiagnosticoFactibilidad(getLnIdEstudioViabilidadTecnica());
		  } catch (Exception e) {
		   JsfUtil.addMessageInfo("Error al cargar información.");
		   LOGGER.error("Error al cargar información", e);
		  }
		 }
	
	


}
