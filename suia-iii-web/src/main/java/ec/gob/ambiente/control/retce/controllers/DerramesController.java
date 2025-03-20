package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.retce.model.Derrames;
import ec.gob.ambiente.retce.model.DerramesComponenteAfectado;
import ec.gob.ambiente.retce.model.ProyectoComponente;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.services.CatalogoGeneralRetceFacade;
import ec.gob.ambiente.retce.services.DerramesFacade;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.ProyectoComponenteFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.comun.bean.CargarCoordenadasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.SustanciaQuimicaPeligrosaFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DerramesController {
	
	private static final Logger LOG = Logger.getLogger(DerramesController.class);
	
	/*BEANs*/	
    
    /*EJBs*/	
	@EJB
	private CatalogoGeneralRetceFacade catalogoGeneralRetceFacade;
	
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@EJB
    private DerramesFacade derramesFacade;
	
	@EJB
    private DocumentosFacade documentosFacade;
    
    @EJB
    private InformacionProyectoFacade informacionProyectoFacade;
    
    @EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
    
    @EJB
    private OrganizacionFacade organizacionFacade;
    
    @EJB
	private SustanciaQuimicaPeligrosaFacade sustanciaQuimicaPeligrosaFacade;
    
    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    
    @EJB
    private ProyectoComponenteFacade proyectoComponenteFacade;
               
    /*List*/       
    @Getter
   	private List<Derrames> derramesList;
       
    private List<Derrames> derramesEliminadosList;
    
    @Getter
	private List<DerramesComponenteAfectado> componenteAfectadoList;
    
    private List<DerramesComponenteAfectado> componenteAfectadoEliminadosList;
    
    @Getter
	private List<DetalleCatalogoGeneral> listaContaminantes, listaContaminantesAire, catalogoDerrameReporteEmergenciaList,catalogoDerrameCausaList,catalogoDerrameInfraestructuraList,catalogoDerrameProductoList,catalogoDerrameComponenteAfectadoList,catalogoTypoAfectadoList,catalogoDerrameContaminanteList,catalogoDerrameTratamientoSuelosList;
    
	@Getter
	private List<UbicacionesGeografica> provinciasList,cantonesList,parroquiasList;
	
	@Getter
	private List<SustanciaQuimicaPeligrosa> sustanciaQuimicaPeligrosaList;
	@Getter
	private List<ProyectoComponente> listaComponentesProyecto;

		
	/*Object*/	
	private InformacionProyecto informacionProyecto;
	
	@Getter
	private Derrames derrames;
    
    @Getter
	private DerramesComponenteAfectado componenteAfectado;
    
	@Setter
	@Getter
	private Documento adjuntoReporteNotificacion, adjuntoEspeciesAfectadas;
		
	@Setter
	@Getter
	private UploadedFile fileAdjuntoReporteNotificacion;
	
	@Setter
	@Getter
	private UbicacionesGeografica provincia,canton;
		
	/*Boolean*/
	private boolean actualizaReporteNotificacion;
	
    @Getter       
    private boolean habilitarIngreso;
    
    @Getter       
    private boolean verFormulario;

	@Setter
	@Getter
	private boolean componenteNinguno, areaAfectada, especiesAfectadas, procesoTratamiento, volumenContaminados, volumenRemediados, personasAfectadas, tipoAfectacion;

	@Setter
	@Getter
	private String labelProcesoTratamiento, labelVolumenContaminados, labelVolumenRemediados, mensajeResponsabilidad;
	private byte[] plantillaComponenteFauna, plantillaComponenteFlora;
		
	@PostConstruct
	public void init() 
	{
		cargarDatosIniciales();		
		Integer idInformacionBasica =(Integer)(JsfUtil.devolverObjetoSession(InformacionProyecto.class.getSimpleName()));		
		informacionProyecto =informacionProyectoFacade.findById(idInformacionBasica);
		MensajeResponsabilidadRetceController mensajeResponsabilidadRetceController = JsfUtil.getBean(MensajeResponsabilidadRetceController.class);
		mensajeResponsabilidad = mensajeResponsabilidadRetceController.mensajeResponsabilidadRetce(informacionProyecto.getUsuarioCreacion());
		derramesList=derramesFacade.findByInformacionBasica(informacionProyecto);
		if(derramesList.isEmpty()){
			verFormulario=true;
			habilitarIngreso=true;		
		}
		cargarProyectoSesion();

		try {
			plantillaComponenteFauna =  documentosFacade.descargarDocumentoPorNombre("COMPONENTE_AFECTADO_FAUNA.xls");
			plantillaComponenteFlora = documentosFacade.descargarDocumentoPorNombre("COMPONENTE_AFECTADO_FLORA.xls");
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
	private void cargarDatosIniciales(){
		derrames=new Derrames();
		derramesList=new ArrayList<Derrames>();
		derramesEliminadosList=new ArrayList<Derrames>();
		
		componenteAfectado=new DerramesComponenteAfectado();
		componenteAfectadoList=new ArrayList<DerramesComponenteAfectado>();
		componenteAfectadoEliminadosList=new ArrayList<DerramesComponenteAfectado>();
		listaComponentesProyecto = new ArrayList<ProyectoComponente>();
		adjuntoReporteNotificacion=new Documento();
		adjuntoEspeciesAfectadas = new Documento();
		actualizaReporteNotificacion=false;	
		habilitarIngreso=false;
		
		cantonesList=new ArrayList<UbicacionesGeografica>();
		parroquiasList=new ArrayList<UbicacionesGeografica>();
		
		//cargarCatalogos
		provinciasList=ubicacionGeograficaFacade.getProvincias();
		catalogoDerrameReporteEmergenciaList=detalleCatalogoGeneralFacade.findByCatalogoGeneralString("derrames.tipo_reporte_derrame");
		catalogoDerrameCausaList=detalleCatalogoGeneralFacade.findByCatalogoGeneralString("derrames.causas_derrame");		
		catalogoDerrameInfraestructuraList=detalleCatalogoGeneralFacade.findByCatalogoGeneralString("derrames.infraestructura_derrame");
		catalogoDerrameProductoList=detalleCatalogoGeneralFacade.findByCatalogoGeneralString("derrames.productos_derrame");
		catalogoDerrameComponenteAfectadoList=detalleCatalogoGeneralFacade.findByCatalogoGeneralString("derrames.componentes_afectados");
		catalogoTypoAfectadoList=detalleCatalogoGeneralFacade.findByCatalogoGeneralString("derrames.tipo.afectacion");
		listaContaminantes=detalleCatalogoGeneralFacade.findByCatalogoGeneralString("derrames.contaminantes_derrame");
		listaContaminantesAire=detalleCatalogoGeneralFacade.findByCatalogoGeneralString("derrames.contaminantes_derrame");
		catalogoDerrameTratamientoSuelosList=detalleCatalogoGeneralFacade.findByCatalogoGeneralString("derrames.tratamiento_suelos_derrame");
		sustanciaQuimicaPeligrosaList=new ArrayList<SustanciaQuimicaPeligrosa>();
		List<SustanciaQuimicaPeligrosa> sustanciaQuimicaPeligrosatemp=sustanciaQuimicaPeligrosaFacade.getSustanciasQuimicasPeligrosas("%");
		for (SustanciaQuimicaPeligrosa sustancia : sustanciaQuimicaPeligrosatemp) {
			if(sustancia.getSustanciaQuimicaPeligrosa()!=null)
				sustanciaQuimicaPeligrosaList.add(sustancia);
		}
		//quito el item material particulado de la lista de contaminantes
		for (DetalleCatalogoGeneral objContaminante : listaContaminantes) {
			if(objContaminante.getDescripcion().toUpperCase().contains("MATERIAL PARTICULADO")){
				listaContaminantes.remove(objContaminante);
				break;
			}
		}
	}
	
	private void cargarProyectoSesion(){
		Integer idInformacionBasica =(Integer)(JsfUtil.devolverObjetoSession(InformacionProyecto.class.getSimpleName()));
		if(idInformacionBasica!=null && derrames !=null){
			derrames.setInformacionProyecto(informacionProyectoFacade.findById(idInformacionBasica));
		}
	}
	
	public Date getFechaActual(){
		return new Date();
	}
	
	public void fileUploadReporteNotificacion(FileUploadEvent event) {
		fileAdjuntoReporteNotificacion = event.getFile();
		setAdjuntoReporteNotificacion(UtilDocumento.generateDocumentPDFFromUpload(fileAdjuntoReporteNotificacion.getContents(),fileAdjuntoReporteNotificacion.getFileName()));
		actualizaReporteNotificacion=true;
	}

	public StreamedContent getDocumentoReporteNotificacion() {		
		try {
			if(adjuntoReporteNotificacion.getContenidoDocumento()==null && adjuntoReporteNotificacion.getId()!=null)
				adjuntoReporteNotificacion.setContenidoDocumento(documentosFacade.descargar(adjuntoReporteNotificacion.getIdAlfresco()));
		} catch (CmisAlfrescoException e) {
			e.printStackTrace();
		}
		return UtilDocumento.getStreamedContent(adjuntoReporteNotificacion);
	}
	
	public void fileUploadEspeciesAfectadas(FileUploadEvent event) {
		setAdjuntoEspeciesAfectadas(UtilDocumento.generateDocumentXLSFromUpload(event.getFile().getContents(),event.getFile().getFileName()));
		actualizaReporteNotificacion=true;
	}
	
	   public void handleFileUpload(final FileUploadEvent event) {
		   setAdjuntoEspeciesAfectadas(UtilDocumento.generateDocumentXLSFromUpload(event.getFile().getContents(),event.getFile().getFileName()));
			actualizaReporteNotificacion=true;
		    ProyectoComponente objComponenteProyecto = new ProyectoComponente();
		    listaComponentesProyecto = new ArrayList<ProyectoComponente>();
	        boolean formatoCorrecto=true;
	        int rows = 0;
	        
	        try {
	        	UploadedFile uploadedFile = event.getFile();
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
	                	objComponenteProyecto = new ProyectoComponente();
	                	for (int i=1; i< row.getLastCellNum(); i++){

							if(i==1 && row.getCell(i) != null && !row.getCell(i).toString().isEmpty()){
			                    objComponenteProyecto.setNombre((String)row.getCell(i).getStringCellValue());
							}
							if(i==2 && row.getCell(i) != null && !row.getCell(i).toString().isEmpty()){
			                    objComponenteProyecto.setFamilia((String)row.getCell(i).getStringCellValue());
							}
							if(i==3 && row.getCell(i) != null && !row.getCell(i).toString().isEmpty()){
			                    objComponenteProyecto.setEspecies((String)row.getCell(i).getStringCellValue());
							}
							if(i==4 && row.getCell(i) != null && !row.getCell(i).toString().isEmpty()){
			                    objComponenteProyecto.setGenero((String)row.getCell(i).getStringCellValue());
							}
	                	}
	                 /*   Iterator<Cell> cellIterator = row.cellIterator();
	                    cellIterator.next();
	                    objComponenteProyecto.setFamilia(cellIterator.next().getStringCellValue());
	                    objComponenteProyecto.setEspecies(cellIterator.next().getStringCellValue());
	                    if(cellIterator.hasNext())
	                    objComponenteProyecto.setGenero(cellIterator.next().getStringCellValue());*/
                        listaComponentesProyecto.add(objComponenteProyecto);
                        
	                }
	                rows++;
	            }

	        } catch (Exception e) {
	        	String message = "";
	            if (rows == 0)
	                message = "Error procesando el archivo excel de componentes. Por favor revise su estructura e intente nuevamente.";
	            else
	                message = "Error procesando el archivo excel de coordenadas en la fila " + rows
	                        + ". Por favor corrija e intente nuevamente.";
	            JsfUtil.addMessageError(message);
	            return;
	        }
	    }
	   
	public StreamedContent getDocumentoEspeciesAfectadas() {		
		try {
			if(adjuntoEspeciesAfectadas.getContenidoDocumento()==null && adjuntoEspeciesAfectadas.getId()!=null)
				adjuntoEspeciesAfectadas.setContenidoDocumento(documentosFacade.descargar(adjuntoEspeciesAfectadas.getIdAlfresco()));
		} catch (CmisAlfrescoException e) {
			e.printStackTrace();
		}
		return UtilDocumento.getStreamedContent(adjuntoEspeciesAfectadas);
	}
	
	public StreamedContent getStreamedContentDocumento(Documento documento) {
		try {
			if(documento.getContenidoDocumento()==null && documento.getId()!=null)
				documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
			return UtilDocumento.getStreamedContent(documento);
		} catch (CmisAlfrescoException e) {
			LOG.error(e);
			e.printStackTrace();
			return null;
		}		
	}
	
	public void crearDerrame(){		
		derrames=new Derrames();
		componenteAfectadoList=new ArrayList<DerramesComponenteAfectado>();
		componenteAfectadoEliminadosList=new ArrayList<DerramesComponenteAfectado>();
		adjuntoReporteNotificacion=new Documento();
		
		cantonesList=new ArrayList<UbicacionesGeografica>();
		parroquiasList=new ArrayList<UbicacionesGeografica>();		
		provincia=null;
		canton=null;		
		
		verFormulario=true;
		habilitarIngreso=true;
		
		cargarProyectoSesion();
		
	}
	
	public void seleccionarDerrame(Derrames derrames){	
		this.derrames=derrames;
		
		adjuntoReporteNotificacion=documentosFacade.documentoXTablaIdXIdDocUnico(this.derrames.getId(),Derrames.class.getSimpleName(),TipoDocumentoSistema.RETCE_OFICIO_NOTIFICACION_DERRAME);
		if(adjuntoReporteNotificacion==null)
			adjuntoReporteNotificacion=new Documento();
		
		verFormulario=true;
		actualizaReporteNotificacion=false;		
		componenteAfectadoList=derramesFacade.findByDerrames(this.derrames);
		for (DerramesComponenteAfectado componenteAfectado : componenteAfectadoList) {
			if(componenteAfectado.getCatalogoComponenteAfectado().getDescripcion().contains("Flora") 
					|| componenteAfectado.getCatalogoComponenteAfectado().getDescripcion().contains("Fauna") ){
				List<ProyectoComponente> objListaCom = proyectoComponenteFacade.findByProyecto(componenteAfectado, componenteAfectado.getCatalogoComponenteAfectado().getDescripcion());
				if(objListaCom != null && objListaCom.size() > 0){
					componenteAfectado.setListProyectoComponente(objListaCom);
				}
			}
		}
		if(this.derrames.getUbicacionesGeografica()!=null){
			UbicacionesGeografica ubicacionTemp=derrames.getUbicacionesGeografica();
			provincia=ubicacionTemp.getUbicacionesGeografica().getUbicacionesGeografica();
			seleccionarProvincia();
			canton=ubicacionTemp.getUbicacionesGeografica();
			seleccionarCanton();
			derrames.setUbicacionesGeografica(ubicacionTemp);
		}
		habilitarIngreso=!derrames.getEnviado();
		
	}
	
	public void eliminarDerrame(Derrames derrames){
		if(derrames.getId()!=null){				
			derrames.setEstado(false);
			derramesEliminadosList.add(derrames);
		}		
		derramesList.remove(derrames);
	}
	
	public void crearComponenteAfectado(){
		componenteAfectado=new DerramesComponenteAfectado();
		componenteAfectado.setDerrames(derrames);
		listaComponentesProyecto = new ArrayList<ProyectoComponente>();
		mostrarCampos();
	}
	
	public void agregarComponenteAfectado(){
		if(componenteAfectado.getCatalogoComponenteAfectado().getDescripcion().contains("Flora") || componenteAfectado.getCatalogoComponenteAfectado().getDescripcion().contains("Fauna") ){
			if(adjuntoEspeciesAfectadas == null  || (adjuntoEspeciesAfectadas.getContenidoDocumento() == null && adjuntoEspeciesAfectadas.getId() == null )){
				JsfUtil.addMessageError("El documento componente afectado "+componenteAfectado.getCatalogoComponenteAfectado().getDescripcion()+" es obligatorio.");
				FacesContext.getCurrentInstance().validationFailed();
				return;
			}
			if(listaComponentesProyecto == null || listaComponentesProyecto.size() == 0){
				JsfUtil.addMessageError("Las especies afectadas es obligatorio.");
				FacesContext.getCurrentInstance().validationFailed();
				return;
			}
			if(adjuntoEspeciesAfectadas.getContenidoDocumento() != null){
				componenteAfectado.setAdjuntoEspeciesAfectadas(adjuntoEspeciesAfectadas);
			}
			componenteAfectado.setListProyectoComponente(listaComponentesProyecto);
		}
		if(!componenteAfectadoList.contains(componenteAfectado)){
			componenteAfectadoList.add(componenteAfectado);
		}
		listaComponentesProyecto = new ArrayList<ProyectoComponente>();
	}
	
	public void editarComponenteAfectado(DerramesComponenteAfectado componenteAfectado){
		listaComponentesProyecto = new ArrayList<ProyectoComponente>();
		this.componenteAfectado=componenteAfectado;
		listaComponentesProyecto = componenteAfectado.getListProyectoComponente();
		
		mostrarCampos();
	}
	
	public void eliminarComponenteAfectado(DerramesComponenteAfectado componenteAfectado){
		if(componenteAfectado.getId()!=null)
			componenteAfectadoEliminadosList.add(componenteAfectado);
		componenteAfectadoList.remove(componenteAfectado);
	}
	public void refresh() {
	    FacesContext context = FacesContext.getCurrentInstance();
	    Application application = context.getApplication();
	    ViewHandler viewHandler = application.getViewHandler();
	    UIViewRoot viewRoot = viewHandler.createView(context, context
	     .getViewRoot().getViewId());
	    context.setViewRoot(viewRoot);
	    context.renderResponse(); 
	 }

	public void ocultarFormulario(){
		verFormulario=false;		
	}
	
	public void validarFormulario(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();		
		if (derrames.getInformacionProyecto()==null) {			
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe seleccionar el proyecto", null));
		}
		if (componenteAfectadoList.isEmpty()) {			
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe agregar al menos 1 componente afectado", null));
		}	
		//validar coordenada
		if(derrames.getCoordenadaX() != null && derrames.getCoordenadaY() != null) {
			String coordenadaX = derrames.getCoordenadaX().toString().replace(",", ".");
			String coordenadaY = derrames.getCoordenadaY().toString().replace(",", ".");
			
			String mensaje = JsfUtil.validarCoordenadaPunto17S(coordenadaX, coordenadaY, 
					derrames.getInformacionProyecto().getCodigoRetce());
			if(mensaje == null){
				cargarUbicacion(coordenadaX, coordenadaY);
			}
			if(mensaje != null)
				errorMessages.add(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, mensaje, null));
		}
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	

	public void validarCoordenadas() {
		try {		
	
		//validar coordenada
		if(derrames.getCoordenadaX() != null && derrames.getCoordenadaY() != null) {
			String coordenadaX = derrames.getCoordenadaX().toString().replace(",", ".");
			String coordenadaY = derrames.getCoordenadaY().toString().replace(",", ".");
			
			String mensaje = JsfUtil.validarCoordenadaPunto17S(coordenadaX, coordenadaY, 
					derrames.getInformacionProyecto().getCodigoRetce());
			if(mensaje == null){
				cargarUbicacion(coordenadaX, coordenadaY);
			}
			if(mensaje != null)
				JsfUtil.addMessageError(mensaje);
		}

		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	public void seleccionarProvincia()
	{
		cantonesList=new ArrayList<UbicacionesGeografica>();
		parroquiasList=new ArrayList<UbicacionesGeografica>();
		canton=null;
		derrames.setUbicacionesGeografica(null);
		
		if(provincia!=null){
			cantonesList=ubicacionGeograficaFacade.getUbicacionPorPadre(provincia);
		}
	}
	
	public void seleccionarCanton()
	{		
		parroquiasList=new ArrayList<UbicacionesGeografica>();	
		derrames.setUbicacionesGeografica(null);
		
		if(canton!=null){
			parroquiasList=ubicacionGeograficaFacade.getUbicacionPorPadre(canton);
		}
	}
	
	public void cargarUbicacion(String coordenadaX, String coordenadaY){

		CargarCoordenadasBean coodenadasBean = JsfUtil.getBean(CargarCoordenadasBean.class);
		List<String> listParroquias = coodenadasBean.getParroquiaByCoordenadas(coordenadaX, coordenadaY);
		if(listParroquias != null){
			for(int i=0; i< listParroquias.size(); i++){
				if(listParroquias.get(i).length() != 6)
					return;
				UbicacionesGeografica objParroquia = ubicacionGeograficaFacade.buscarUbicacionPorCodigoInec(listParroquias.get(i));
				if(objParroquia != null){
					provincia = objParroquia.getUbicacionesGeografica().getUbicacionesGeografica();
					seleccionarProvincia();
					canton = objParroquia.getUbicacionesGeografica();
					seleccionarCanton();
				}
			}
		}
	}
	
	private boolean validarDatos() {
		boolean validar=true;
		if (derrames.getFechaOcurrenciaDesde()!=null && derrames.getFechaOcurrenciaHasta() !=null 
				&& derrames.getFechaOcurrenciaDesde().after(derrames.getFechaOcurrenciaHasta())) {			
			JsfUtil.addMessageError("La fecha de Ocurrencia 'hasta' no debe ser anterior a la fecha de Ocurrencia 'desde'");
			validar = false;
		}	
		if (derrames.getFechaOcurrenciaDesde()!=null && derrames.getFechaNotificacion() !=null 
				&& derrames.getFechaOcurrenciaDesde().after(derrames.getFechaNotificacion())) {			
			JsfUtil.addMessageError("La fecha de Notificación no debe ser anterior a la fecha de Ocurrencia 'desde'");
			validar= false;
		}
		if (derrames.getVolumenRecuperado() != null && derrames.getVolumenDerramado() !=null
				&& derrames.getVolumenRecuperado()>derrames.getVolumenDerramado()) {			
			JsfUtil.addMessageError("El Volumen Recuperado no puede superar al volumen Derramado");
			validar = false;
		}
		return validar;
	}
	
	public void guardar(){		
		try {
			if(!validarDatos())
				return;
			
			derramesFacade.save(derrames, JsfUtil.getLoggedUser());
			for (DerramesComponenteAfectado componenteAfectado : componenteAfectadoList) {
				derramesFacade.save(componenteAfectado, JsfUtil.getLoggedUser());

				if(componenteAfectado.getCatalogoComponenteAfectado().getDescripcion().contains("Flora") 
						|| componenteAfectado.getCatalogoComponenteAfectado().getDescripcion().contains("Fauna") ){
					TipoDocumentoSistema objTipoDocumento=null;
					if(componenteAfectado.getCatalogoComponenteAfectado().getDescripcion().contains("Flora")){
						objTipoDocumento = TipoDocumentoSistema.RETCE_ESPECIES_AFECTADAS_DERRAME_FLORA;
					}else{
						objTipoDocumento = TipoDocumentoSistema.RETCE_ESPECIES_AFECTADAS_DERRAME_FAUNA;
					}
					
					if(componenteAfectado.getAdjuntoEspeciesAfectadas() != null && componenteAfectado.getAdjuntoEspeciesAfectadas().getContenidoDocumento()!=null && componenteAfectado.getAdjuntoEspeciesAfectadas().getId() == null){
						proyectoComponenteFacade.deshactivarAdjuntoDocumento(componenteAfectado);
						componenteAfectado.getAdjuntoEspeciesAfectadas().setIdTable(componenteAfectado.getId());
						componenteAfectado.getAdjuntoEspeciesAfectadas().setNombreTabla(DerramesComponenteAfectado.class.getSimpleName());
						componenteAfectado.getAdjuntoEspeciesAfectadas().setDescripcion("Especies Afectadas Derrame");
						componenteAfectado.getAdjuntoEspeciesAfectadas().setEstado(true);				
						documentosFacade.guardarDocumentoAlfrescoSinProyecto(derrames.getInformacionProyecto().getCodigoRetce(), "SITUACIONES_EMERGENTES", componenteAfectado.getId().longValue(), componenteAfectado.getAdjuntoEspeciesAfectadas(), objTipoDocumento, null);		
					}
					if(componenteAfectado.getListProyectoComponente() != null && componenteAfectado.getListProyectoComponente().size() > 0 ){
						for (ProyectoComponente objComponente : componenteAfectado.getListProyectoComponente()) {
							if(objComponente.getId() == null){
								proyectoComponenteFacade.deshactivarComponentes(componenteAfectado);
								break;
							}
						}
						for (ProyectoComponente objComponente : componenteAfectado.getListProyectoComponente()) {
							objComponente.setEstado(true);
							objComponente.setDerrameComponenteAfectado(componenteAfectado);
							objComponente.setTipo(componenteAfectado.getCatalogoComponenteAfectado().getDescripcion());
							proyectoComponenteFacade.save(objComponente);
						}
					}else{
						proyectoComponenteFacade.deshactivarComponentes(componenteAfectado);
					}
				}else{
					proyectoComponenteFacade.deshactivarComponentes(componenteAfectado);
					proyectoComponenteFacade.deshactivarAdjuntoDocumento(componenteAfectado);
				}
			}
			for (DerramesComponenteAfectado componenteAfectado : componenteAfectadoEliminadosList) {
				componenteAfectado.setEstado(false);
				derramesFacade.save(componenteAfectado, JsfUtil.getLoggedUser());
			}
			componenteAfectadoEliminadosList.clear();
			
			if(!derramesList.contains(derrames))
				derramesList.add(derrames);
			
			if(actualizaReporteNotificacion && adjuntoReporteNotificacion.getContenidoDocumento()!=null){
				adjuntoReporteNotificacion.setIdTable(derrames.getId());
				adjuntoReporteNotificacion.setNombreTabla(Derrames.class.getSimpleName());
				adjuntoReporteNotificacion.setDescripcion("Oficio Notificacion Derrame");
				adjuntoReporteNotificacion.setEstado(true);				
				documentosFacade.guardarDocumentoAlfrescoSinProyecto(derrames.getInformacionProyecto().getCodigoRetce(), "SITUACIONES_EMERGENTES", derrames.getId().longValue(), adjuntoReporteNotificacion, TipoDocumentoSistema.RETCE_OFICIO_NOTIFICACION_DERRAME, null);
				actualizaReporteNotificacion=false;				
			}
			if(!derrames.getEnviado())
			JsfUtil.addMessageInfo("Se ha guardado la información con éxito");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al guardar Información. Por favor comuníquese con Mesa de Ayuda");
			e.printStackTrace();
			LOG.error(e);
		}		
	}
	
	
	
	public void enviar(){
		if(!validarDatos())
			return;
		derrames.setEnviado(true);
		guardar();
		ocultarFormulario();
		notificacionDeclaracion();
		JsfUtil.addMessageInfo("Se ha enviado la información con éxito");
	}
	
	private void notificacionDeclaracion(){		
		try {
			List<Usuario> usuariosNotifica=JsfUtil.getBean(ProcesoRetceController.class).buscarUsuariosNotificacionDeclaracionRetce(informacionProyecto.getAreaSeguimiento());
			for (Usuario usuario : usuariosNotifica) {
				String nombreOperador=JsfUtil.getNombreOperador(JsfUtil.getLoggedUser(), organizacionFacade.buscarPorRuc(JsfUtil.getLoggedUser().getNombre()));
				String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionIngresoReporteRetce", new Object[]{usuario.getPersona().getNombre(),nombreOperador, derrames.getCodigo(),"situaciones emergentes ambientales/derrames"});
				Email.sendEmail(usuario, "Declaración Situaciones Emergentes, Derrames", mensaje, informacionProyecto.getCodigo(), JsfUtil.getLoggedUser());					
			}
		} catch (Exception e) {
			LOG.error("No se envio la notificacion al usuario. "+e.getCause()+" "+e.getMessage());
		}
		
			
	}
	
	public void mostrarCampos(){
		labelVolumenContaminados ="Volumen de Suelo / Sedimentos / Lastre / Agua contaminados (m3)*";
		labelVolumenRemediados="Volumen de Suelo / Sedimentos / Lastre / Agua remediados (m3) *";
		labelProcesoTratamiento = "Proceso de Tratamiento de Suelo / Sedimentos / Lastre / Agua*";
		tipoAfectacion =false;
		especiesAfectadas =false;
		componenteNinguno = false;
		areaAfectada = true;
		adjuntoEspeciesAfectadas = new Documento();
		catalogoDerrameContaminanteList = listaContaminantes;
		if(componenteAfectado == null || componenteAfectado.getCatalogoComponenteAfectado() == null){

			procesoTratamiento = false;
			volumenContaminados = false;
			volumenRemediados = false;
			personasAfectadas= false;
			areaAfectada = false;
			return;
		}
		switch (componenteAfectado.getCatalogoComponenteAfectado().getDescripcion()) {
		case "Agua":
			labelProcesoTratamiento = "Proceso de Tratamiento de Agua*";
			labelVolumenContaminados ="Volumen de Agua contaminada (m3)*";
			labelVolumenRemediados="Volumen de Agua remediada (m3) *";
			procesoTratamiento = true;
			volumenContaminados = true;
			volumenRemediados = true;
			personasAfectadas= false;
			componenteAfectado.setPersonasAfectadas(null);
			componenteAfectado.setListProyectoComponente(null);
			break;
		case "Aire":
			procesoTratamiento = false;
			volumenContaminados = false;
			volumenRemediados = false;
			personasAfectadas= false;
			areaAfectada = false;
			componenteAfectado.setSuelosSedimentosContaminados(null);
			componenteAfectado.setSuelosSedimentosRemediados(null);
			componenteAfectado.setPersonasAfectadas(null);
			componenteAfectado.setCatalogoTratamientoSuelo(null);
			catalogoDerrameContaminanteList = listaContaminantesAire;
			componenteAfectado.setListProyectoComponente(null);
			break;
		case "Fauna":
		case "Flora":
			procesoTratamiento = false;
			volumenContaminados = false;
			volumenRemediados = false;
			personasAfectadas= false;
			especiesAfectadas =true;
			TipoDocumentoSistema objTipoDocumento=null;
			if(componenteAfectado.getCatalogoComponenteAfectado().getDescripcion().contains("Flora")){
				objTipoDocumento = TipoDocumentoSistema.RETCE_ESPECIES_AFECTADAS_DERRAME_FLORA;
			}else{
				objTipoDocumento = TipoDocumentoSistema.RETCE_ESPECIES_AFECTADAS_DERRAME_FAUNA;
			}
			adjuntoEspeciesAfectadas=documentosFacade.documentoXTablaIdXIdDocUnico(this.componenteAfectado.getId(),DerramesComponenteAfectado.class.getSimpleName(),objTipoDocumento);
			if(adjuntoEspeciesAfectadas==null){
				adjuntoEspeciesAfectadas=new Documento();
			}else{
				componenteAfectado.setAdjuntoEspeciesAfectadas(adjuntoEspeciesAfectadas);
			}

			componenteAfectado.setSuelosSedimentosContaminados(null);
			componenteAfectado.setSuelosSedimentosRemediados(null);
			componenteAfectado.setCatalogoTratamientoSuelo(null);
			List<ProyectoComponente> objListaCom = proyectoComponenteFacade.findByProyecto(componenteAfectado, componenteAfectado.getCatalogoComponenteAfectado().getDescripcion());
			if(objListaCom != null && objListaCom.size() > 0){
				componenteAfectado.setListProyectoComponente(objListaCom);
				listaComponentesProyecto = objListaCom;
			}else{
				componenteAfectado.setListProyectoComponente(null);
				listaComponentesProyecto = new ArrayList<ProyectoComponente>();
			}
			break;
		case "Suelo":
			procesoTratamiento = true;
			volumenContaminados = true;
			volumenRemediados = true;
			personasAfectadas= false;
			labelVolumenContaminados ="Volumen de Tratamiento de Suelo contaminado (m3)*";
			labelVolumenRemediados="Volumen de Tratamiento de Suelo remediado (m3) *";
			labelProcesoTratamiento = "Proceso de Tratamiento de Suelo*";
			componenteAfectado.setPersonasAfectadas(null);
			componenteAfectado.setListProyectoComponente(null);
			break;
		case "Lastre":
			procesoTratamiento = true;
			volumenContaminados = true;
			volumenRemediados = true;
			personasAfectadas= false;
			labelProcesoTratamiento = "Proceso de Tratamiento de lastre*";
			labelVolumenContaminados = "Volumen de Lastre contaminado (m3)*";
			labelVolumenRemediados = "Volumen de Lastre remediado (m3)*";
			componenteAfectado.setListProyectoComponente(null);
			break;
		case "Sedimento":
			procesoTratamiento = true;
			volumenContaminados = true;
			volumenRemediados = true;
			personasAfectadas= false;
			labelProcesoTratamiento = "Proceso de Tratamiento de Sedimento*";
			labelVolumenContaminados = "Volumen de Sedimento contaminado (m3)*";
			labelVolumenRemediados = "Volumen de Sedimento remediado (m3)*";
			componenteAfectado.setPersonasAfectadas(null);
			componenteAfectado.setListProyectoComponente(null);
			break;
		case "Social":
			procesoTratamiento = false;
			volumenContaminados = false;
			volumenRemediados = false;
			personasAfectadas= true;
			tipoAfectacion = true;
			componenteAfectado.setSuelosSedimentosContaminados(null);
			componenteAfectado.setSuelosSedimentosRemediados(null);
			componenteAfectado.setCatalogoTratamientoSuelo(null);
			componenteAfectado.setListProyectoComponente(null);
			break;
		case "Ninguno":
			procesoTratamiento = false;
			volumenContaminados = false;
			volumenRemediados = false;
			personasAfectadas= false;
			areaAfectada = false;
			componenteNinguno =true;
			componenteAfectado.setAreaAfectada(null);
			componenteAfectado.setPersonasAfectadas(null);
			componenteAfectado.setCatalogoContaminante(null);
			componenteAfectado.setConcentracionInicial(null);
			componenteAfectado.setUnidad(null);
			componenteAfectado.setSuelosSedimentosContaminados(null);
			componenteAfectado.setSuelosSedimentosRemediados(null);
			componenteAfectado.setCatalogoTratamientoSuelo(null);
			componenteAfectado.setCatalogoTipoAfectacion(null);
			componenteAfectado.setOtroContaminante(null);
			componenteAfectado.setOtroTipoAfectacion(null);
			componenteAfectado.setListProyectoComponente(null);
			break;

		default:
			procesoTratamiento = false;
			volumenContaminados = false;
			volumenRemediados = false;
			personasAfectadas= false;
			areaAfectada = false;
			break;
		}
		
	}


	public StreamedContent getPlantillaComponente() throws Exception {
		DefaultStreamedContent content = null;
		String nombreComponente = componenteAfectado.getCatalogoComponenteAfectado().getDescripcion();
		try {
			if(nombreComponente.contains("Flora")){
				if (plantillaComponenteFlora != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaComponenteFlora));
					content.setName(Constantes.PLANTILLA_COMPONENTE_FLORA);
				}
			}else if(nombreComponente.contains("Fauna")){
				if (plantillaComponenteFauna != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaComponenteFauna));
					content.setName(Constantes.PLANTILLA_COMPONENTE_FAUNA);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
	
	
}
