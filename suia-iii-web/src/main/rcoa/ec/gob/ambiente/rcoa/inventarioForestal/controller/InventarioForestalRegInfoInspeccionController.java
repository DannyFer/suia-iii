/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.context.DefaultRequestContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.CoordenadasInventarioForestalCertificadoFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.CoordenadasInventarioForestalCertificado;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ReporteInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ShapeInventarioForestalCertificado;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import index.ContieneZona_entrada;
import index.ContieneZona_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;
import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author Daniel Porras
 * @version Revision: 1.0
 */
@ViewScoped
@ManagedBean
public class InventarioForestalRegInfoInspeccionController extends DocumentoReporteController implements Serializable{

    private static final long serialVersionUID = 165685472149658047L;
    
    private static final String DOFI_MIME = "application/";

    @EJB
    private ConexionBpms conexionBpms;
    @EJB
   	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
    @EJB
   	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalCoaShapeFacade;
    @EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
    private CoordenadasInventarioForestalCertificadoFacade coordenadasInventarioForestalCertificadoFacade;
    @EJB
    private UsuarioFacade usuarioFacade;
    
    @Getter
    @Setter
    private DocumentoInventarioForestal documentoInventarioForestal;
    
    @Getter
    @Setter
    private UploadedFile fileCaractTipoEstado;
    
    @Getter
    @Setter
    private UploadedFile fileCaractEcosistema;
    
    @Getter
    @Setter
    private UploadedFile fileAreaImplantacion;
    
    @Setter
    @Getter
	private DocumentoInventarioForestal informeInspecionCampo = new DocumentoInventarioForestal();
    @Getter
    @Setter
    private ShapeInventarioForestalCertificado shape = new ShapeInventarioForestalCertificado();
    
    @Setter
    @Getter
    private List<FotografiaBean> fotografiasList, fotografiasEcoList, fotografiasAreaList, fotografiasInformeList;
    
    @Getter
    @Setter
    private FotografiaBean fotografia, fotografiaEco, fotografiaArea, fotografiaInforme;
    
    @Getter
    @Setter
    private DocumentoInventarioForestal imagen, imagenEco, imagenArea, imagenInforme;
    
    @Getter
    @Setter
    private List<DocumentoInventarioForestal> fotografiasEliminadasList, fotografiasEcoEliminadasList, fotografiasAreaEliminadasList, fotografiasInformeEliminadasList;
    
    @Getter
    @Setter
    private List<CoordendasPoligonosCertificado> coordinatesWrappers = new ArrayList<CoordendasPoligonosCertificado>();
    
    @Getter
    private UploadedFile uploadedFile;
    
    private TipoForma poligono=new TipoForma();
    
    @Setter
    @Getter
	private DocumentoInventarioForestal documentoCoordenadas;
    private byte[] plantillaCoordenadas; 
    
    @Getter
	@Setter
	private boolean token, subido;    
    
    public InventarioForestalRegInfoInspeccionController() {
    	super();
    }
    

    @PostConstruct
    public void init() throws CmisAlfrescoException {
        updateInforme();
        
        poligono = getTipoForma(TipoForma.TIPO_FORMA_POLIGONO);  
        documentoCoordenadas = new DocumentoInventarioForestal();
        
        plantillaCoordenadas = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(Constantes.PLANTILLA_COORDENADAS, null);
        
        listaDocumentos = new ArrayList<>();
        fotografiasEliminadasList = new ArrayList<DocumentoInventarioForestal>();
        fotografiasList = new ArrayList<FotografiaBean>();
        fotografiasEcoEliminadasList = new ArrayList<DocumentoInventarioForestal>();
        fotografiasEcoList = new ArrayList<FotografiaBean>();
        fotografiasAreaEliminadasList = new ArrayList<DocumentoInventarioForestal>();
        fotografiasInformeEliminadasList = new ArrayList<DocumentoInventarioForestal>();
        fotografiasAreaList = new ArrayList<FotografiaBean>();
        fotografiasInformeList = new ArrayList<FotografiaBean>();
        fotografia = new FotografiaBean(); 
        fotografiaEco = new FotografiaBean(); 
        fotografiaArea = new FotografiaBean();
        fotografiaInforme = new FotografiaBean();
        List<ShapeInventarioForestalCertificado> listShapes = shapeInventarioForestalCertificadoFacade.getByInventarioForestalAmbiental(inventarioForestalAmbiental.getId());
    	for(int i=0;i<=listShapes.size()-1;i++){
    		shape = new ShapeInventarioForestalCertificado();
			shape = listShapes.get(i);
			List<CoordenadasInventarioForestalCertificado> coorImpl= new ArrayList<CoordenadasInventarioForestalCertificado>();
			coorImpl = coordenadasInventarioForestalCertificadoFacade.getByShape(shape.getId());
			CoordendasPoligonosCertificado coordinatesWrapper = new CoordendasPoligonosCertificado(coorImpl, poligono);
			coordinatesWrappers.add(coordinatesWrapper);
		}
        try {
        	if (informeInspeccion.getId() != null ) {
            	List<DocumentoInventarioForestal> imagenesAreaList = documentoInventarioForestalFacade.getDocumentosByInventarioTipoDocumento(inventarioForestalAmbiental.getId(), TipoDocumentoSistema.IMAGEN_AREA_IMPLANTACION);
            	List<DocumentoInventarioForestal> imagenesCoberturaList = documentoInventarioForestalFacade.getDocumentosByInventarioTipoDocumento(inventarioForestalAmbiental.getId(), TipoDocumentoSistema.IMAGEN_COBERTURA_VEGETAL);
            	List<DocumentoInventarioForestal> imagenesEcosistemaList = documentoInventarioForestalFacade.getDocumentosByInventarioTipoDocumento(inventarioForestalAmbiental.getId(), TipoDocumentoSistema.IMAGEN_ECOSISTEMAS_PRESENTES);
            	List<DocumentoInventarioForestal> imagenesInformeList = documentoInventarioForestalFacade.getDocumentosByInventarioTipoDocumento(inventarioForestalAmbiental.getId(), TipoDocumentoSistema.IMAGEN_ANEXO);
            	
            	for(DocumentoInventarioForestal img : imagenesAreaList){
            		FotografiaBean foto = new FotografiaBean();
            		foto.setFotografia(img);
            		foto.setDescripcion(img.getDescripcionTabla());
            		foto.setUrl(getImageContentsAsBase64(img.getContenidoDocumento()));
            		fotografiasAreaList.add(foto);
            	}
            	
            	for(DocumentoInventarioForestal img : imagenesCoberturaList){
            		FotografiaBean foto = new FotografiaBean();
            		foto.setFotografia(img);
            		foto.setDescripcion(img.getDescripcionTabla());
            		foto.setUrl(getImageContentsAsBase64(img.getContenidoDocumento()));
            		fotografiasList.add(foto);
            	}
            	
            	for(DocumentoInventarioForestal img : imagenesEcosistemaList){
            		FotografiaBean foto = new FotografiaBean();
            		foto.setFotografia(img);
            		foto.setDescripcion(img.getDescripcionTabla());
            		foto.setUrl(getImageContentsAsBase64(img.getContenidoDocumento()));
            		fotografiasEcoList.add(foto);
            	}        	
            	
            	for(DocumentoInventarioForestal img : imagenesInformeList){
            		FotografiaBean foto = new FotografiaBean();
            		foto.setFotografia(img);
            		foto.setDescripcion(img.getDescripcionTabla());
            		foto.setUrl(getImageContentsAsBase64(img.getContenidoDocumento()));
            		fotografiasInformeList.add(foto);
            	}
        	}   
        	
        	subido = false;
        	verificaToken();
        	
		} catch (Exception e) {
			e.printStackTrace();
		}    
        
    }
    
    public void updateInforme() {
        try {
            this.visualizarInforme(true);
        } catch (Exception e) {
            LOG.error("Error al cargar el informe tecnico del registro de generador", e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }

    public void guardar() {
    	try {
    		informeInspeccion = reporteInventarioForestalFacade.guardar(informeInspeccion);
    	    		
    		for(FotografiaBean fotoBean: fotografiasList){
				DocumentoInventarioForestal foto = fotoBean.getFotografia();
				if(foto.getContenidoDocumento() != null && foto.getId() == null){
					foto.setInventarioForestalAmbiental(inventarioForestalAmbiental);
					foto.setReporteInventarioForestal(informeInspeccion);
					foto.setEstado(true);
					foto.setIdTabla(informeInspeccion.getId());
					foto.setDescripcionTabla(fotoBean.getDescripcion());
					TipoDocumento tipoDocumento = new TipoDocumento();
					tipoDocumento.setId(TipoDocumentoSistema.IMAGEN_COBERTURA_VEGETAL.getIdTipoDocumento());					
					foto.setTipoDocumento(tipoDocumento);
					foto = documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(
									proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
									"INVENTARIO_FORESTAL",
									0L,
									foto,
									TipoDocumentoSistema.IMAGEN_COBERTURA_VEGETAL);
					fotoBean.setFotografia(foto);
				}					
			}
			
			if(fotografiasEliminadasList != null && !fotografiasEliminadasList.isEmpty()){
				for(DocumentoInventarioForestal fotoEliminada : fotografiasEliminadasList){		
					if(fotoEliminada.getId() != null){
						fotoEliminada.setEstado(false);
						fotoEliminada = documentoInventarioForestalFacade.guardar(fotoEliminada);
					}
				}
			}
			
			for(FotografiaBean fotoBean: fotografiasEcoList){
				DocumentoInventarioForestal foto = fotoBean.getFotografia();
				if(foto.getContenidoDocumento() != null && foto.getId() == null){
					foto.setInventarioForestalAmbiental(inventarioForestalAmbiental);
					foto.setReporteInventarioForestal(informeInspeccion);
					foto.setEstado(true);
					foto.setIdTabla(informeInspeccion.getId());
					foto.setDescripcionTabla(fotoBean.getDescripcion());
					TipoDocumento tipoDocumento = new TipoDocumento();
					tipoDocumento.setId(TipoDocumentoSistema.IMAGEN_ECOSISTEMAS_PRESENTES.getIdTipoDocumento());					
					foto.setTipoDocumento(tipoDocumento);
					foto = documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(
									proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
									"INVENTARIO_FORESTAL",
									0L,
									foto,
									TipoDocumentoSistema.IMAGEN_ECOSISTEMAS_PRESENTES);
					
					fotoBean.setFotografia(foto);
				}					
			}
			
			if(fotografiasEcoEliminadasList != null && !fotografiasEcoEliminadasList.isEmpty()){
				for(DocumentoInventarioForestal fotoEliminada : fotografiasEcoEliminadasList){	
					if(fotoEliminada.getId() != null){
						fotoEliminada.setEstado(false);
						fotoEliminada = documentoInventarioForestalFacade.guardar(fotoEliminada);
					}
				}
			}
			
			//area implantacion
			for(FotografiaBean fotoBean: fotografiasAreaList){
				DocumentoInventarioForestal foto = fotoBean.getFotografia();
				if(foto.getContenidoDocumento() != null && foto.getId() == null){
					foto.setInventarioForestalAmbiental(inventarioForestalAmbiental);
					foto.setReporteInventarioForestal(informeInspeccion);
					foto.setEstado(true);
					foto.setIdTabla(informeInspeccion.getId());
					foto.setDescripcionTabla(fotoBean.getDescripcion());
					TipoDocumento tipoDocumento = new TipoDocumento();
					tipoDocumento.setId(TipoDocumentoSistema.IMAGEN_AREA_IMPLANTACION.getIdTipoDocumento());					
					foto.setTipoDocumento(tipoDocumento);
					foto = documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(
									proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
									"INVENTARIO_FORESTAL",
									0L,
									foto,
									TipoDocumentoSistema.IMAGEN_AREA_IMPLANTACION);
					
					fotoBean.setFotografia(foto);
				}					
			}
			
			if(fotografiasAreaEliminadasList != null && !fotografiasAreaEliminadasList.isEmpty()){
				for(DocumentoInventarioForestal fotoEliminada : fotografiasAreaEliminadasList){	
					if(fotoEliminada.getId() != null){
						fotoEliminada.setEstado(false);
						fotoEliminada = documentoInventarioForestalFacade.guardar(fotoEliminada);
					}
				}
			}
			
			for(FotografiaBean fotoBean: fotografiasInformeList){
				DocumentoInventarioForestal foto = fotoBean.getFotografia();
				if(foto.getContenidoDocumento() != null && foto.getId() == null){
					foto.setInventarioForestalAmbiental(inventarioForestalAmbiental);
					foto.setReporteInventarioForestal(informeInspeccion);
					foto.setEstado(true);
					foto.setIdTabla(informeInspeccion.getId());
					foto.setDescripcionTabla(fotoBean.getDescripcion());
					TipoDocumento tipoDocumento = new TipoDocumento();
					tipoDocumento.setId(TipoDocumentoSistema.IMAGEN_ANEXO.getIdTipoDocumento());
					foto.setTipoDocumento(tipoDocumento);
					foto = documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
							"INVENTARIO_FORESTAL",0L,foto,TipoDocumentoSistema.IMAGEN_ANEXO);
					fotoBean.setFotografia(foto);
				}					
			}
			
			if (! fotografiasInformeEliminadasList.isEmpty()) {
				for(DocumentoInventarioForestal fotoEliminada : fotografiasInformeEliminadasList){	
					if(fotoEliminada.getId() != null){
						fotoEliminada.setEstado(false);
						fotoEliminada = documentoInventarioForestalFacade.guardar(fotoEliminada);
					}
				}
			}
			this.asignaInformeInspeccion();	
			updateInforme();
			
	        JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
    	} catch (Exception e) {
    		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
		}
	    	
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
                "/prevencion/registrogeneradordesechos/tecnicoResponsableInformeTecnico.jsf");
    }
    
 // Metodo para enviar el formulario
    private boolean validarDatos() {
    	boolean validar = true;
    	if (informeInspeccion.getFechaInspeccion() == null) {
    		JsfUtil.addMessageError("Ingrese la fecha de inspección");
			validar = false;
		}
    	if (informeInspeccion.getFechaFin() == null) {
    		JsfUtil.addMessageError("Ingrese la fecha de fin");
			validar = false;
		}
    	if (informeInspeccion.getDelegadosInspecion() == null || informeInspeccion.getDelegadosInspecion() == "") {
    		JsfUtil.addMessageError("Ingrese el delegado del promotor");
			validar = false;
		}
    	if (informeInspeccion.getNombresDelegadoInspeccion() == null || informeInspeccion.getAreaDelegado() == null || informeInspeccion.getCargoDelegado() == null) {
    		JsfUtil.addMessageError("Ingrese el equipo técnico delegado ");
			validar = false;
		}
    	if (informeInspeccion.getAntecedentes() == null || informeInspeccion.getAntecedentes() == "") {
    		JsfUtil.addMessageError("Ingrese el antecedente");
			validar = false;
		}
    	if (informeInspeccion.getMarcoLegal() == null || informeInspeccion.getMarcoLegal() == "") {
    		JsfUtil.addMessageError("Ingrese el marco legal");
			validar = false;
		}
    	if (informeInspeccion.getObjetivo() == null || informeInspeccion.getObjetivo() == "") {
    		JsfUtil.addMessageError("Ingrese el objetivo");
			validar = false;
		}
    	if (informeInspeccion.getCaracterizacionEstadoVegetal() == null || informeInspeccion.getCaracterizacionEstadoVegetal() == "") {
    		JsfUtil.addMessageError("Ingrese la caraterizacion de estado vegetal");
			validar = false;
		}
    	if (informeInspeccion.getCaracterizacionEcosistemas() == null || informeInspeccion.getCaracterizacionEcosistemas() == "") {
    		JsfUtil.addMessageError("Ingrese la caracterizacion de ecosistemas");
			validar = false;
		}
    	if (informeInspeccion.getCaracterizacionAreaImplantacion() == null || informeInspeccion.getCaracterizacionAreaImplantacion() == "") {
    		JsfUtil.addMessageError("Ingrese la caracterizacion de area de implantacion");
			validar = false;
		}
    	if (informeInspeccion.getConclusiones() == null || informeInspeccion.getConclusiones() == "") {
    		JsfUtil.addMessageError("Ingrese las conclusiones ");
			validar = false;
		}
    	if (informeInspeccion.getRecomendaciones() == null || informeInspeccion.getRecomendaciones() == "") {
    		JsfUtil.addMessageError("Ingrese las recomendaciones ");
			validar = false;
		}
    	if (fotografiasInformeList.size() == 0) {
    		JsfUtil.addMessageError("Ingrese los anexos");
			validar = false;
		}
    	return validar;
    }
    
    public void enviar() {
    	if (validarDatos()) {
    		RequestContext.getCurrentInstance().execute("PF('signDialog').show();");
    	}else{
    		return;
    	}
    }
    
    public void uploadFileCaractTipoEstado(FileUploadEvent event) {
    	fileCaractTipoEstado = event.getFile();
    }
    
    public void uploadFileCaractEcosistema(FileUploadEvent event) {
    	fileCaractEcosistema = event.getFile();
    }
    
    public void uploadFileAreaImplantacion(FileUploadEvent event) {
    	fileAreaImplantacion = event.getFile();
    }
    
    public void guardarImagenes(ReporteInventarioForestal informeInspeccion) {
    	try {
    		if (null != fileCaractTipoEstado) {
    			DocumentoInventarioForestal docCaractTipoEstado = inicializarDocumentoInventarioForestal(
    					fileCaractTipoEstado, INFORME_INSPECCION_IMAGEN1);
	        	docCaractTipoEstado.setReporteInventarioForestal(informeInspeccion);
	        	docCaractTipoEstado.setIdTabla(informeInspeccion.getId());
	        	docCaractTipoEstado = documentoInventarioForestalFacade.
	        			guardarDocumentoAlfrescoInventarioForestal(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), 
	        					"INVENTARIO_FORESTAL", 0L, docCaractTipoEstado, 
	        					TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_INSPECCION);
	        }
	    	
	    	if (null != fileCaractEcosistema) {
	    		DocumentoInventarioForestal docCaractEcosistema = inicializarDocumentoInventarioForestal(fileCaractTipoEstado, INFORME_INSPECCION_IMAGEN2);
	    		docCaractEcosistema = documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),"INVENTARIO_FORESTAL", 0L, docCaractEcosistema,TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_INSPECCION);
	        }
	    	
	    	if (null != fileAreaImplantacion) {
	    		DocumentoInventarioForestal docAreaImplantacion = inicializarDocumentoInventarioForestal(fileCaractTipoEstado, INFORME_INSPECCION_IMAGEN3);
	    		docAreaImplantacion = documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),"INVENTARIO_FORESTAL", 0L, docAreaImplantacion,TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_INSPECCION);
	        	
	        }
    	} catch (ServiceException e) {
    		LOG.error("Error al cargar el documento", e);
		} catch (CmisAlfrescoException e) {
			LOG.error("Error al guardar el documento en el sistema Alfresco", e);
		}	
    }
    
    private DocumentoInventarioForestal inicializarDocumentoInventarioForestal(UploadedFile fileCaractTipoEstado, String descripcionTabla) {
    	String nombreCaractTipoEstado = fileCaractTipoEstado.getFileName();
    	String extCaractTipoEstado = nombreCaractTipoEstado.substring(nombreCaractTipoEstado.lastIndexOf('.'), nombreCaractTipoEstado.length());
    	
    	DocumentoInventarioForestal documento = new DocumentoInventarioForestal();
    	
    	documento.setNombreDocumento(nombreCaractTipoEstado);
    	documento.setMimeDocumento(fileCaractTipoEstado.getContentType());
    	documento.setContenidoDocumento(fileCaractTipoEstado.getContents());
    	documento.setExtencionDocumento(extCaractTipoEstado);
    	documento.setDescripcionTabla(descripcionTabla);
    	documento.setNombreTabla(ReporteInventarioForestal.class.getSimpleName());
    	return documento;
    }
    
    //fotografias cobertura
    public void crearFotografia() {
		fotografia = new FotografiaBean();
		imagen = new DocumentoInventarioForestal();
	}
    
    public void imagenListener(FileUploadEvent event) throws IOException {			
		uploadImagen(event.getFile(), imagen);		
		fotografia.setFotografia(imagen);
		byte[] byteImagen = event.getFile().getContents();
		fotografia.setUrl(getImageContentsAsBase64(byteImagen));		
	}
	
	private void uploadImagen(UploadedFile file, DocumentoInventarioForestal documento){		
		
		documento.setContenidoDocumento(file.getContents());
		documento.setMimeDocumento(file.getContentType().compareTo("image/download")!=0?file.getContentType():"image/jpg");
		documento.setNombreDocumento(file.getFileName());
		
		
		String[] split=documento.getNombreDocumento().split("\\.");
		documento.setExtencionDocumento("."+split[split.length-1]);
	}
	
	public String getImageContentsAsBase64(byte[] byteImagen) {
		return DatatypeConverter.printBase64Binary(byteImagen);	    
	}
			
	public void agregarFotografia(){
		if(fotografia.getFotografia() != null && fotografia.getFotografia().getContenidoDocumento() != null){
			fotografiasList.add(fotografia);
			DefaultRequestContext.getCurrentInstance().execute("PF('fotografiaDiag').hide();");
		}else{
			JsfUtil.addMessageError("Debe adjuntar una fotografía para guardar.");				
		}			
	}
    
    public void  eliminarFotografia(FotografiaBean pictureDelete)
	{
		try {			
			fotografiasList.remove(pictureDelete);
			if(pictureDelete.getFotografia().getId() != null){
				fotografiasEliminadasList.add(pictureDelete.getFotografia());
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    //métodos fotografias ecosistemas
    public void crearFotografiaEco() {
		fotografiaEco = new FotografiaBean();
		imagenEco = new DocumentoInventarioForestal();
	}
    
    public void imagenEcoListener(FileUploadEvent event) throws IOException {			
		uploadImagen(event.getFile(), imagenEco);		
		fotografiaEco.setFotografia(imagenEco);
		byte[] byteImagen = event.getFile().getContents();
		fotografiaEco.setUrl(getImageContentsAsBase64(byteImagen));		
	}
    
    public void agregarFotografiaEco(){
		if(fotografiaEco.getFotografia() != null && fotografiaEco.getFotografia().getContenidoDocumento() != null){
			fotografiasEcoList.add(fotografiaEco);
			DefaultRequestContext.getCurrentInstance().execute("PF('fotografiaEcoDiag').hide();");
		}else{
			JsfUtil.addMessageError("Debe adjuntar una fotografía para guardar.");				
		}			
	}
    
    public void eliminarFotografiaEco(FotografiaBean pictureDelete)
	{
		try {			
			fotografiasEcoList.remove(pictureDelete);
			if(pictureDelete.getFotografia().getId() != null){
				fotografiasEcoEliminadasList.add(pictureDelete.getFotografia());
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
  //métodos fotografias de area de implantación
    public void crearFotografiaArea() {
		fotografiaArea = new FotografiaBean();
		imagenArea = new DocumentoInventarioForestal();
	}
    
    public void imagenAreaListener(FileUploadEvent event) throws IOException {			
		uploadImagen(event.getFile(), imagenArea);		
		fotografiaArea.setFotografia(imagenArea);
		byte[] byteImagen = event.getFile().getContents();
		fotografiaArea.setUrl(getImageContentsAsBase64(byteImagen));		
	}
    
    public void agregarFotografiaArea(){
		if(fotografiaArea.getFotografia() != null && fotografiaArea.getFotografia().getContenidoDocumento() != null){
			fotografiasAreaList.add(fotografiaArea);
			DefaultRequestContext.getCurrentInstance().execute("PF('fotografiaAreaDiag').hide();");
		}else{
			JsfUtil.addMessageError("Debe adjuntar una fotografía para guardar.");				
		}			
	}
    
    public void eliminarFotografiaArea(FotografiaBean pictureDelete)
	{
		try {			
			fotografiasAreaList.remove(pictureDelete);
			if(pictureDelete.getFotografia().getId() != null){
				fotografiasAreaEliminadasList.add(pictureDelete.getFotografia());
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void crearFotografiaInforme() {
		fotografiaInforme = new FotografiaBean();
		imagenInforme = new DocumentoInventarioForestal();
	}
    public void imagenInformeListener(FileUploadEvent event) throws IOException {			
		uploadImagen(event.getFile(), imagenInforme);		
		fotografiaInforme.setFotografia(imagenInforme);
		byte[] byteImagen = event.getFile().getContents();
		fotografiaInforme.setUrl(getImageContentsAsBase64(byteImagen));		
	}
    public void agregarFotografiaInforme(){
		if(fotografiaInforme.getFotografia() != null && fotografiaInforme.getFotografia().getContenidoDocumento() != null){
			fotografiasInformeList.add(fotografiaInforme);
			DefaultRequestContext.getCurrentInstance().execute("PF('fotografiaInformeDiag').hide();");
		}else{
			JsfUtil.addMessageError("Debe adjuntar una fotografía para guardar.");				
		}			
	}
    public void eliminarFotografiaInforme(FotografiaBean pictureDelete) {
		try {			
			fotografiasInformeList.remove(pictureDelete);
			if(pictureDelete.getFotografia().getId() != null){
				fotografiasInformeEliminadasList.add(pictureDelete.getFotografia());
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void handleFileUploadCoordenadas(final FileUploadEvent event) {   
        int rows = 0;
        List<String> listCoordenadasProyecto = new ArrayList<String>();
        List<ProyectoLicenciaAmbientalCoaShape> shapeProyecto = proyectoLicenciaAmbientalCoaShapeFacade.buscarFormaGeograficaPorProyecto(proyectoLicenciaCoa, 1, 0);
        for (ProyectoLicenciaAmbientalCoaShape rowShapeProyecto : shapeProyecto) {
        	List<CoordenadasProyecto> listCoordenadas = coordenadasProyectoCoaFacade.buscarPorForma(rowShapeProyecto);
        	String arrayCoordenadas = "";
        	for (CoordenadasProyecto rowCoordenadas : listCoordenadas) {
        		arrayCoordenadas += (arrayCoordenadas == "") ? rowCoordenadas.getX().toString()+" "+rowCoordenadas.getY().toString() : ","+rowCoordenadas.getX().toString()+" "+rowCoordenadas.getY().toString();
        	}
        	listCoordenadasProyecto.add(arrayCoordenadas);
        }
        coordinatesWrappers = new ArrayList<CoordendasPoligonosCertificado>();
        CoordendasPoligonosCertificado coordinatesWrapper = new CoordendasPoligonosCertificado();
        coordinatesWrappers.add(coordinatesWrapper);
        try {
            uploadedFile = event.getFile();
            Workbook workbook = new HSSFWorkbook(uploadedFile.getInputstream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();                
                boolean isEmptyRow = true;
                for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK
                            && StringUtils.isNotBlank(cell.toString())) {
                        isEmptyRow = false;
                    }
                }
                if (isEmptyRow)
                    break;

                if (rows > 0) {

                    Iterator<Cell> cellIterator = row.cellIterator();
                    CoordenadasInventarioForestalCertificado coordenada = new CoordenadasInventarioForestalCertificado();
                    coordenada.setOrdenCoordenada((int) cellIterator.next().getNumericCellValue());
                    coordenada.setX(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))));
                    coordenada.setY(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))));
                    if (coordinatesWrapper.getTipoForma() == null)
                        coordinatesWrapper.setTipoForma(poligono);                    
                    else if (!coordinatesWrapper.getTipoForma().equals(poligono) || coordinatesWrapper.getTipoForma().getId().intValue() == TipoForma.TIPO_FORMA_PUNTO || coordenada.getOrdenCoordenada() == 1) {
                        coordinatesWrapper = new CoordendasPoligonosCertificado(null, poligono);
                        coordinatesWrappers.add(coordinatesWrapper);
                    }
                    coordinatesWrapper.getCoordenadas().add(coordenada);
                }
                rows++;
            }
            List<String> listCoordenadasInventario = new ArrayList<String>();
            for (int i = 0; i < coordinatesWrappers.size(); i++) {
            	String coorInventario = "";
            	for (int j = 0; j < coordinatesWrappers.get(i).getCoordenadas().size(); j++) {
            		CoordenadasInventarioForestalCertificado coordenada = coordinatesWrappers.get(i).getCoordenadas().get(j);
            		coorInventario += (coorInventario == "") ? coordenada.getX().toString()+" "+coordenada.getY().toString() : ","+coordenada.getX().toString()+" "+coordenada.getY().toString();
            	}
            	listCoordenadasInventario.add(coorInventario);
            }
            // Verificar si las coordenadas estan contenidas en el poligono de implantacion del proyecto
            List<Boolean> listInImpl = new ArrayList<Boolean>();
            for (int i = 0; i < listCoordenadasInventario.size(); i++) {
            	Boolean inImplMuestra = false;
            	for (int j = 0; j < listCoordenadasProyecto.size(); j++) {
            		ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada();
            		verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
            		verificarGeoImpla.setXy1(listCoordenadasProyecto.get(j));
            		verificarGeoImpla.setXy2(listCoordenadasInventario.get(i));
            		SVA_Reproyeccion_IntersecadoPortTypeProxy wsGeoImpl=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
            		ContieneZona_resultado[]intRestGeoImpl;
            		intRestGeoImpl=wsGeoImpl.contieneZona(verificarGeoImpla);
            		if (intRestGeoImpl[0].getContieneCapa().getValor().equals("t")) {
            			inImplMuestra = true;
            			listInImpl.add(inImplMuestra);
            			break;
            		}
            	}
            }
            if (listInImpl.size() != listCoordenadasInventario.size()) {
            	coordinatesWrappers = new ArrayList<CoordendasPoligonosCertificado>();
            	documentoCoordenadas = new DocumentoInventarioForestal();
            	JsfUtil.addMessageError("Las coordenadas están fuera la(s) áreas de implantación(es)");
            } else {
            	String[] split=event.getFile().getContentType().split("/");
            	String extension = "."+split[split.length-1];
            	documentoCoordenadas.setNombreDocumento(event.getFile().getFileName());
            	documentoCoordenadas.setMimeDocumento(event.getFile().getContentType());
            	documentoCoordenadas.setContenidoDocumento(event.getFile().getContents());
            	documentoCoordenadas.setExtencionDocumento(extension);
            }
        } catch (Exception e) {
            LOG.error("Error procesando el excel de coordenadas", e);
        }
    }
    
    public StreamedContent getPlantillaCoordenadas() throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (plantillaCoordenadas != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaCoordenadas));
                content.setName(Constantes.PLANTILLA_COORDENADAS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
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
    
    public String firmarDocumento() {
		try {						
			 if(informeInspecionCampo != null){
				 String documentOffice = documentoInventarioForestalFacade.direccionDescarga(informeInspecionCampo); 
				 return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			 }		
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del oficio por el director", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}
    
    public void crearDocumento() throws Exception {
    		try {
    			this.asignaInformeInspeccion();	
    			this.visualizarInforme(false); 		
    			
    			informeInspecionCampo.setNombreDocumento(informeInspeccion.getNombreFichero());
    			informeInspecionCampo.setReporteInventarioForestal(informeInspeccion);
    			informeInspecionCampo.setIdTabla(informeInspeccion.getId());
    			informeInspecionCampo.setNombreTabla(ReporteInventarioForestal.class.getSimpleName());
    			informeInspecionCampo.setDescripcionTabla("Informe Inspeccion Campo");
    			informeInspecionCampo.setMimeDocumento(DOFI_MIME + "pdf");
    			informeInspecionCampo.setContenidoDocumento(informeInspeccion.getArchivoInforme());
    			informeInspecionCampo.setExtencionDocumento(".pdf");
    			informeInspecionCampo = documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "INVENTARIO_FORESTAL", 0L, informeInspecionCampo, TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_INSPECCION);
    			
			} catch (ServiceException e) {
				e.printStackTrace();
			} catch (CmisAlfrescoException e) {
				JsfUtil.addMessageError("Error al guardar el Informe de Inspección de Campo. Por favor comuníquese con Mesa de Ayuda");
    			LOG.error(e);
			}

    }
    
    public void subirDocumento(){
    	try {
    		if(documentoFirmado.getContenidoDocumento() != null){
    			documentoFirmado.setNombreDocumento(informeInspeccion.getNombreFichero());
    			documentoFirmado.setReporteInventarioForestal(informeInspeccion);
    			documentoFirmado.setIdTabla(informeInspeccion.getId());
    			documentoFirmado.setNombreTabla(ReporteInventarioForestal.class.getSimpleName());
    			documentoFirmado.setDescripcionTabla("Informe Inspeccion Campo");
    			documentoFirmado.setMimeDocumento(DOFI_MIME + "pdf");
    			documentoFirmado.setExtencionDocumento(".pdf");
    			documentoFirmado = documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "INVENTARIO_FORESTAL", 0L, documentoFirmado, TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_INSPECCION);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void finalizar(){
    	try {			
			if (informeInspecionCampo.getId() != null) {								

				String idAlfresco = informeInspecionCampo.getIdAlfresco();

				if (token && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				} else if (!token && !subido) {
					JsfUtil.addMessageError("Debe adjuntar el informe firmado.");
					return;
				}
				
				if(!token){
					subirDocumento();
				}
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}
		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
    }
    
    private DocumentoInventarioForestal documentoFirmado = new DocumentoInventarioForestal();
    public void uploadListenerDocumentos(FileUploadEvent event) {
		if(documentoFirmado != null){
			byte[] contenidoDocumento = event.getFile().getContents();

			documentoFirmado.setContenidoDocumento(contenidoDocumento);
			documentoFirmado.setNombreDocumento(event.getFile().getFileName());			

			subido = true;
		}else{
			JsfUtil.addMessageError("No ha descargado el documento del oficio");
		}		
	}
    
    public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			crearDocumento();
			
			byte[] documentoContent = null;
						
			if (informeInspecionCampo != null && informeInspecionCampo.getIdAlfresco() != null) {
				documentoContent = documentoInventarioForestalFacade.descargar(informeInspecionCampo.getIdAlfresco());
			} else if (informeInspecionCampo.getContenidoDocumento() != null) {
				documentoContent = informeInspecionCampo.getContenidoDocumento();
			}
			
			if (informeInspecionCampo != null && informeInspecionCampo.getNombreDocumento() != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(informeInspecionCampo.getNombreDocumento());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
    
    

}
