package ec.gob.ambiente.prevencion.viabilidadtecnica.bean;
//object create cls_mba
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaMaterialesDiagnostico;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaTipoMateriales;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.DiagnosticoFactibilidadAnexoVTFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;


@ManagedBean
@ViewScoped
public class DiagnosticoFactibilidadAnexoVTBean implements Serializable {

	private static final long serialVersionUID = 5738803542928606242L;


	private static final Logger LOGGER = Logger.getLogger(DiagnosticoFactibilidadAnexoVTBean.class);
	

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB
	private ProcesoFacade procesoFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@EJB
	private DiagnosticoFactibilidadAnexoVTFacade diagnosticoFactibilidadAnexoVTFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@Setter
	@Getter
	public EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico;
	
	@Setter
	@Getter
	private EstudioViabilidadTecnica estudioViabilidadTecnica;
	
	@Setter
	@Getter
	private ViabilidadTecnicaTipoMateriales viabilidadTecnicaTipoMateriales;
	
	@Setter
	@Getter
	private ViabilidadTecnicaMaterialesDiagnostico viabilidadTecnicaMaterialesDiagnostico;
	
	
	
	@Getter
	@Setter
	private List<EstudioViabilidadTecnicaDiagnostico> llEstudioViabilidadTecnicaDiagnostico;
	
	@Getter
	@Setter
	private List<ViabilidadTecnicaTipoMateriales> llViabilidadTecnicaTipoMateriales;
	
	@Getter
	@Setter
	private List<ViabilidadTecnicaMaterialesDiagnostico> llViabilidadTecnicaMaterialesDiagnostico;
	
	@Getter
	@Setter
	private Integer lnIdEstudioViabilidadTecnica;

	@Getter
	@Setter
	private List<String> llViabilidadTecnicaMaterialesDiagnosticoValor;
	
	
	@Getter
	private Integer valorAdjunto;
	
	@Getter
    @Setter
    private File imagenFotografica;
	
	@PostConstruct
	public void init()
	{	
		setLnIdEstudioViabilidadTecnica(10);//se recupera del proceso
		
//		estudioViabilidadTecnicaDiagnostico = new EstudioViabilidadTecnicaDiagnostico();
		cargaDiagnosticoFactibilidadAnexo();//carga la data por idEstudioViabilidadTecnica
		
		estudioViabilidadTecnica= new EstudioViabilidadTecnica();
		estudioViabilidadTecnica.setId(lnIdEstudioViabilidadTecnica);
		
		datosViabilidadTecnicaTipoMateriales();
		
		
		
	}
	
	public void guardaDiagnosticoFactibilidadAnexo() {
		try {
			System.out.println(estudioViabilidadTecnicaDiagnostico.getAguaSuperficial());
			System.out.println(estudioViabilidadTecnicaDiagnostico.getAlmacenamientoTemporal());
			
			estudioViabilidadTecnicaDiagnostico.setEstudioViabilidadTecnica(estudioViabilidadTecnica);	
			
			diagnosticoFactibilidadAnexoVTFacade.guardaDiagnosticoFactibilidadAnexo(estudioViabilidadTecnicaDiagnostico);
			
			guardaViabilidadTecnicaMaterialesDiagnostico();
			
			estudioViabilidadTecnicaDiagnostico = diagnosticoFactibilidadAnexoVTFacade.cargarDiagnosticoFactibilidadAnexo(getLnIdEstudioViabilidadTecnica());
			
		} catch (Exception e) {
			JsfUtil.addMessageInfo("La información no ha sido salvada correctamente.");
			LOGGER.error("Error al guardar información", e);
		}
	}
	public void cargaDiagnosticoFactibilidadAnexo() {
		try {
			estudioViabilidadTecnicaDiagnostico = diagnosticoFactibilidadAnexoVTFacade.cargarDiagnosticoFactibilidadAnexo(getLnIdEstudioViabilidadTecnica());
		} catch (Exception e) {
			JsfUtil.addMessageInfo("Error al cargar información.");
			LOGGER.error("Error al cargar información", e);
		}
	}
	//carga los tipos de materiales
	public void datosViabilidadTecnicaTipoMateriales() {
		
		
		 llViabilidadTecnicaTipoMateriales = diagnosticoFactibilidadAnexoVTFacade.datosViabilidadTecnicaTipoMateriales();
		 
		 
		 for (ViabilidadTecnicaTipoMateriales forViabilidadTecnicaTipoMateriales : llViabilidadTecnicaTipoMateriales) {
			 
			 ViabilidadTecnicaMaterialesDiagnostico datosviabilidadTecnicaMateriales = new ViabilidadTecnicaMaterialesDiagnostico();  
			 datosviabilidadTecnicaMateriales=diagnosticoFactibilidadAnexoVTFacade.cargaViabilidadTecnicaMaterialesDiagnostico(estudioViabilidadTecnicaDiagnostico.getId(), 
								forViabilidadTecnicaTipoMateriales.getId());
			 
			 forViabilidadTecnicaTipoMateriales.setDetalleNombre(datosviabilidadTecnicaMateriales.getValor()); 
			 
			 
		}
		
	}
	
	//guarda el detalle de los tipos materiales
	public void guardaViabilidadTecnicaMaterialesDiagnostico() {
		
		
		List<ViabilidadTecnicaMaterialesDiagnostico> lviabilidadTecnicaMaterialesDiagnosticos = new ArrayList<ViabilidadTecnicaMaterialesDiagnostico>();		
		ViabilidadTecnicaMaterialesDiagnostico vtmd = null;
		
		for (ViabilidadTecnicaTipoMateriales viabilidadTecnicaTipoMateriales : llViabilidadTecnicaTipoMateriales) {
			
			
			vtmd = new ViabilidadTecnicaMaterialesDiagnostico();
			
			vtmd = diagnosticoFactibilidadAnexoVTFacade.cargaViabilidadTecnicaMaterialesDiagnostico(estudioViabilidadTecnicaDiagnostico.getId(), viabilidadTecnicaTipoMateriales.getId());
			
			vtmd.setValor(viabilidadTecnicaTipoMateriales.getDetalleNombre());
			vtmd.setViabilidadTecnicaTipoMateriales(viabilidadTecnicaTipoMateriales);
			vtmd.setEstudioViabilidadTecnicaDiagnostico(estudioViabilidadTecnicaDiagnostico);
			vtmd.setEstado(true);
			
			try {
				diagnosticoFactibilidadAnexoVTFacade.guardaViabilidadTecnicaMaterialesDiagnostico(vtmd);
			} catch (Exception e) {
				JsfUtil.addMessageInfo("La información no ha sido salvada correctamente.");
				LOGGER.error("Error al guardar información", e);
			}
			
		}

	}
	
	//carga los el detalle de los tipos de materiales

	public void cargaViabilidadTecnicaMaterialesDiagnostico
									(Integer idEstudioViabilidadTecnicaDiagnostico, Integer idMaterialesDiagnostico) {
		
		viabilidadTecnicaMaterialesDiagnostico = 
				diagnosticoFactibilidadAnexoVTFacade.cargaViabilidadTecnicaMaterialesDiagnostico(idEstudioViabilidadTecnicaDiagnostico, idMaterialesDiagnostico);
		
		System.out.println(viabilidadTecnicaMaterialesDiagnostico.getValor());
				
	} 
	
	public void validarTareaBpm() {
		// JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(),
		// "/control/aprobacionPuntosMonitoreoAmbiental/listarPuntosMonitoreo.jsf");
	}
	
	
	
	//////////////////////////////////////////DOCUMENTO/////////////////////////////////////////////////////////
	
	public void setValorAdjunto(Integer valor) {
		this.valorAdjunto = valor;	  
		System.out.println("valorAdjunto"+valor);
	}

	
	public void adjuntarDocumento(FileUploadEvent event){
    try{   
    	 System.out.println("valorAdjunto try:"+valorAdjunto);
      if (event != null){
    	  System.out.println("evento != null");
		 byte[] x = event.getFile().getContents();
    	  imagenFotografica = JsfUtil.upload(event);
       
    	  System.out.println("valorAdjunto:"+valorAdjunto);
        
    	  switch(valorAdjunto){
    	  	//Estudio de cantidad y calidad de los residuos y desechos sólidos
    	  	case 3067: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ESTUDIO_CALIDAD_DESECHOS);break;
    	  	
    	  	//Topografía
			case 3039: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_TOPOGRAFIA);break;
			//Suelos, geología y geotécnia
			case 3040: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_SUELOS_GEOLOGIA_GEOTECNICA);break;
			//Análisis hidrogeológico y analisis meteorologico
			case 3041: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ANAISIS_HIDROMETEO);break;
			//Aguas superficiales
			case 3042: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_AGUAS_SUPERFICIALES);break;
			
			
			//Area del Proyecto
			case 3068: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ESTUDIO_AREA_PROYECTO);break;
			//Aguas superficiales
			case 3069: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ESTUDIO_PLANTEAMIENTO_ANALISIS);break;
			//Aguas superficiales
			case 3070: ingresarDocumento(imagenFotografica,TipoDocumentoSistema.VIAB_TECN_ESTUDIO_ACEPTACION_ALTERNATIVA_GADM);break;
			   
			default: JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " no fue adjuntado.");break;
    	  }

    //   JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " fue adjuntado correctamente.");
      	}
    	}catch (Exception e){
			LOGGER.error("Error en el Metodo adjuntarImagen", e);
			e.printStackTrace();
		}
	}

	/*public void uploadListenerAprobacion(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = new Documento();
		String accion = JsfUtil.getBean(DocumentoRGBean.class).isEmision() ? "Emision" : "Actualizacion";
		documento.setNombre("Oficio" + accion + "-" + JsfUtil.getBean(DocumentoRGBean.class).getOficio().getNumero()
				+ ".pdf");
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(OficioEmisionRegistroGeneradorDesechos.class.getSimpleName());
		documento.setIdTable(JsfUtil.getBean(DocumentoRGBean.class).getOficio().getId());
		documento.setMime("application/pdf");
		documento.setExtesion(".pdf");
		try {
			if (JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getProyecto() != null) {
				documento = documentosFacade.guardarDocumentoAlfresco(JsfUtil.getBean(DocumentoRGBean.class)
								.getGenerador().getProyecto().getCodigo(), Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS,
						JsfUtil.getCurrentProcessInstanceId(), documento, JsfUtil.getBean(DocumentoRGBean.class)
								.getOficio().getTipoDocumento().getTipoDocumentoSistema(), null);
			} else {
				documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(JsfUtil.getBean(DocumentoRGBean.class)
								.getGenerador().getSolicitud(), Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS,
						JsfUtil.getCurrentProcessInstanceId(), documento, JsfUtil.getBean(DocumentoRGBean.class)
								.getOficio().getTipoDocumento().getTipoDocumentoSistema(), null);
			}
			JsfUtil.getBean(DocumentoRGBean.class).getOficio().setDocumento(documento);
			JsfUtil.getBean(DocumentoRGBean.class).guardarOficio();
			this.archivoAprobacion = documento;
			this.archivoAprobacion.setContenidoDocumento(contenidoDocumento);
			documentoAprobacionAdjuntado = true;
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}*/

	public Documento ingresarDocumento(File file,TipoDocumentoSistema tipoDocumento) throws Exception 
	{
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		Documento documento1 = new Documento();
		//       documento1.setIdTable(proyectoLicenciamientoAmbiental.getId());
		documento1.setIdTable(1234);      
		String ext = getExtension(file.getAbsolutePath());
		documento1.setNombre(file.getName());
		documento1.setExtesion(ext);
		documento1.setNombreTabla(EstudioViabilidadTecnicaDiagnostico.class.getSimpleName());
		documento1.setMime(mimeTypesMap.getContentType(file));
		documento1.setContenidoDocumento(data);
		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		//       documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
		documentoTarea.setIdTarea(1234);
		//       documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
		documentoTarea.setProcessInstanceId(1234);
		//       return documentosFacade.guardarDocumentoAlfresco(proyectoLicenciamientoAmbiental.getCodigo(),"Viabilidad_Tecnica", bandejaTareasBean.getProcessId(), documento1, tipoDocumento,documentoTarea);
		JsfUtil.addMessageInfo("El archivo " + file.getName() + " fue adjuntado correctamente.");

		return documentosFacade.guardarDocumentoAlfresco("MAAE-RA-2015-211963","Viabilidad_Tecnica", new Long(1234), documento1, tipoDocumento,documentoTarea);  
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
	
	
	
	
	
	
	
	
	
	
	

}
