package ec.gob.ambiente.prevencion.registro.proyecto.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.AdicionarUbicaciones1Bean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.AdicionarUbicaciones2Bean;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.base.RegistroProyectoController;
import ec.gob.ambiente.suia.domain.Camaroneras;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ProyectoCamaronera;
import ec.gob.ambiente.suia.domain.ProyectoRutaUbicacionGeografica;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RegistroProyectoOtrosSectoresController extends RegistroProyectoController {

	private static final long serialVersionUID = -7754590885987420451L;

	// No existe en el catalogo actual
	public static final String[] CATEGORIAS_ALCANCE = new String[] { "94.1", "94.2", "94.3" };

	// No existe en el catalogo actual
	public static final String[] CATEGORIAS_PESCA_PLANTADOS = new String[] { "11.05" };

	@Setter
	@Getter
	private boolean mostrarCamaronera = false;

	@Setter
	@Getter
	private Camaroneras camaroneras = new Camaroneras();
	
	@Setter
	@Getter
	private Camaroneras camaronerastotal = new Camaroneras();

	@Setter
	@Getter
	private String acuerdoCamaronera;	
	
	@Setter
	@Getter
	private double extensionCamaronera;	
	
	@Getter
	private Boolean viabilidadcamaronera;

	@Setter
	@ManagedProperty(value = "#{adicionarUbicaciones1Bean}")
	private AdicionarUbicaciones1Bean adicionarUbicaciones1Bean;

	@Setter
	@ManagedProperty(value = "#{adicionarUbicaciones2Bean}")
	private AdicionarUbicaciones2Bean adicionarUbicaciones2Bean;
	
	@Getter
	@Setter
	private List<ProyectoCamaronera>listaProyectosCamaroneras= new ArrayList<ProyectoCamaronera>();
	
	private double areaCamaronera=0.00;
	
    @Getter
    private UploadedFile uploadedFile;
    
    private byte[] transformedFile;
    
    private String nombreDocumento="";
    
    @Getter
    @Setter
    private String nombreArchivo="Seleccione documento";
	
	@PostConstruct
	private void initOtrosSectores() {
		registroProyectoBean.getProyecto().setUtilizaPlantados(false);
		mostrarCoordenadas();

		if (registroProyectoBean.isEdicion()) {
			if (isMostrarAlcance()) {
				if(adicionarUbicacionesBean.getListParroquiasGuardar().size()>0){
					//adicionarUbicaciones1Bean.setUbicacionesSeleccionadas(adicionarUbicacionesBean.getListParroquiasGuardar());
					adicionarUbicacionesBean.setUbicacionesSeleccionadas(adicionarUbicacionesBean.getListParroquiasGuardar());
				}
				adicionarUbicaciones1Bean.setUbicacionesSeleccionadas(adicionarUbicacionesBean.getUbicacionesSeleccionadas());
				
				
				Iterator<ProyectoRutaUbicacionGeografica> iteratorPRUG = registroProyectoBean.getProyecto()
						.getProyectoRutaUbicacionesGeograficas().iterator();
				while (iteratorPRUG.hasNext()) {
					ProyectoRutaUbicacionGeografica proyectoRutaUbicacionGeografica = iteratorPRUG.next();
					adicionarUbicaciones2Bean.getUbicacionesSeleccionadas().add(
							proyectoRutaUbicacionGeografica.getUbicacionesGeografica());
				}
			}
			registroProyectoBean.setViabilidad(false);
			/*
			 * if (registroProyectoBean.getProyecto().getResiduosSolidos() !=
			 * null && registroProyectoBean.getProyecto().getResiduosSolidos())
			 * { registroProyectoBean.setViabilidad(true); }
			 */
			if (registroProyectoBean.getProyecto().getOficioViabilidad() != null) {
				registroProyectoBean.setViabilidad(true);
			}
		}
	}

	public void guardarProyecto() throws CmisAlfrescoException {
		List<UbicacionesGeografica> ubicaciones;
		if(adicionarUbicacionesBean.getListParroquiasGuardar().size()>0){
			//ubicaciones = adicionarUbicacionesBean.getListParroquiasGuardar();
			adicionarUbicacionesBean.setUbicacionesSeleccionadas(adicionarUbicacionesBean.getListParroquiasGuardar());
		}
		ubicaciones = adicionarUbicacionesBean.getUbicacionesSeleccionadas();
		
		List<UbicacionesGeografica> ubicacionesRutas = null;
		if (isMostrarAlcance()) {
			ubicaciones = adicionarUbicaciones1Bean.getUbicacionesSeleccionadas();
			registroProyectoBean.getProyecto().setDatosOficinaPrincipal(true);
			ubicacionesRutas = adicionarUbicaciones2Bean.getUbicacionesSeleccionadas();
		}
		if (!isMostrarPescaPlantados()) {
			registroProyectoBean.getProyecto().setUtilizaPlantados(null);
		}

		registroProyectoBean.getProyecto().setTipoSector(proyectoFacade.getTipoSector(TipoSector.TIPO_SECTOR_OTROS));
		//aqui guador las concesiones con todo el proyecto
		if(listaProyectosCamaroneras!=null && listaProyectosCamaroneras.size()>0){
			proyectoFacade.guardarConCamaronera(registroProyectoBean.getProyecto(),
					catalogoActividadesBean.getActividadSistemaSeleccionada(), ubicaciones, ubicacionesRutas, null, null,
					null, cargarCoordenadasBean.getCoordinatesWrappers(),
					cargarCoordenadasBean.generateDocumentFromUpload(),
					registroProyectoBean.getFasesDesechosSeleccionadas(), desechoPeligrosoBean.getDesechosSeleccionados(),
					registroProyectoBean.getSustanciasQuimicasSeleccionadas(),JsfUtil.getLoggedUser().getNombre(),listaProyectosCamaroneras);
					registroProyectoBean.getProyecto().setFormato(cargarCoordenadasBean.getFormato());
		}else{		
		proyectoFacade.guardar(registroProyectoBean.getProyecto(),
				catalogoActividadesBean.getActividadSistemaSeleccionada(), ubicaciones, ubicacionesRutas, null, null,
				null, cargarCoordenadasBean.getCoordinatesWrappers(),
				cargarCoordenadasBean.generateDocumentFromUpload(),
				registroProyectoBean.getFasesDesechosSeleccionadas(), desechoPeligrosoBean.getDesechosSeleccionados(),
				registroProyectoBean.getSustanciasQuimicasSeleccionadas(),JsfUtil.getLoggedUser().getNombre());
				registroProyectoBean.getProyecto().setFormato(cargarCoordenadasBean.getFormato());
		}
	}

//	public void adicionarValidaciones(List<String> errors) {
//	}
	public void setViabilidadcamaronera(Boolean viabilidadcamaronera) {
		this.viabilidadcamaronera = viabilidadcamaronera;
		if (!viabilidadcamaronera)
			JsfUtil.addCallbackParam("showNoViabilidadcamaronera");
		mostrarCamaronera = false;
	}

	public List<String> validar() {
		List<String> messages = new ArrayList<String>();
		if (isMostrarAlcance() && adicionarUbicaciones1Bean.getUbicacionesSeleccionadas().isEmpty()) {
			messages.add("No se han definido ubicaciones geográficas para patio de maniobras / oficina princiapal / almacenamiento.");
		}
		if (isMostrarAlcance() && adicionarUbicaciones2Bean.getUbicacionesSeleccionadas().isEmpty()) {
			messages.add("No se han definido ubicaciones geográficas para el alcance del proyecto.");
		}
		return messages;
	}

	public List<String> validarNegocio() {
		return null;
	}

	public boolean isMostrarAlcance() {
		boolean result = isCatalogoCategoriaCodigoInicia(CATEGORIAS_ALCANCE);
		registroProyectoBean.setMostrarUbicacionGeografica(!result);
		return result;
	}

	public boolean isMostrarPescaPlantados() {
		boolean result = isCatalogoCategoriaCodigoInicia(CATEGORIAS_PESCA_PLANTADOS);
		return result;
	}

	public void mostrarCoordenadas() {
		registroProyectoBean.setOcultarCoordenadas(isMostrarPescaPlantados()
				&& registroProyectoBean.getProyecto().getUtilizaPlantados() != null
				&& registroProyectoBean.getProyecto().getUtilizaPlantados());
	}
	
	public boolean isverPanelCamaroneras() {
		boolean resultado = false;
		try {
			String code = catalogoActividadesBean
					.getActividadSistemaSeleccionada().getCodigo();
			resultado = proyectoFacade.verPanCam(code);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return resultado;
	}
	
	public void verificarOficioViavilidadcamaronera() {
		try {
			Camaroneras camaroneras = new Camaroneras();
			ProyectoCamaronera proyectoCamaronera= new ProyectoCamaronera();
			camaroneras = proyectoFacade.verificarOficioViabilidadcamaronera(registroProyectoBean.getNumeroOficioViabilidadcamaronera());
			
			if (!camaroneras.isUsado()){
				//registroProyectoBean.getProyecto().setOficioCamaronera(camaroneras);
				if (camaroneras!=null || camaroneras.getAcuerdo().equals(null) || registroProyectoBean.getNumeroOficioViabilidadcamaronera().equals(null)) {
					if(listaProyectosCamaroneras.size()==0 && camaroneras.getAcuerdo().equals(null)){
						JsfUtil.addMessageError("El número de oficio de camaroneras ingresado no es válido.");
						mostrarCamaronera = false;
						return;
					}else {
						mostrarCamaronera = true;	
					}					
				} 

				if(listaProyectosCamaroneras.size()>0){
					proyectoCamaronera.setCamaroneras(camaroneras);
					boolean acuerdo=true;
					for (ProyectoCamaronera pCamaronera : listaProyectosCamaroneras) {
						if(pCamaronera.getCamaroneras()!=null && proyectoCamaronera.getCamaroneras().getId().equals(pCamaronera.getCamaroneras().getId())){
							acuerdo=false;
							break;
						}
					}
					if(acuerdo){
						areaCamaronera+=camaroneras.getExtension();
						registroProyectoBean.getProyecto().setArea(areaCamaronera);
						registroProyectoBean.getProyecto().setUnidad("ha");	
						listaProyectosCamaroneras.add(proyectoCamaronera);
						registroProyectoBean.setNumeroOficioViabilidadcamaronera(null);
						FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:area");
						FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:cb_unidad");
					}else {
						JsfUtil.addMessageError("La concesión ya está ingresada.");
					}
				}else{
					proyectoCamaronera.setCamaroneras(camaroneras);
					areaCamaronera+=camaroneras.getExtension();
					registroProyectoBean.getProyecto().setArea(areaCamaronera);
					registroProyectoBean.getProyecto().setUnidad("ha");
					listaProyectosCamaroneras.add(proyectoCamaronera);
					registroProyectoBean.setNumeroOficioViabilidadcamaronera(null);
					FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:area");
					FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:cb_unidad");
				}				
			}else{
				JsfUtil.addMessageError("El número de oficio de camaroneras ya se encuentra usuado");
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("El número de oficio de camaroneras ingresado no es válido.");
			if(listaProyectosCamaroneras.size()==0)
			mostrarCamaronera=false;
			return;
		}
	}

	public void eliminarOficioViabilidadcamaroneras() {
		camaroneras.setAcuerdo(null);
		acuerdoCamaronera = "";	
		extensionCamaronera=0.0;
		registroProyectoBean.setNumeroOficioViabilidadcamaronera(null);
	}	
	
	public void adicionarValidaciones(List<String> errors) {
		if (isverPanelCamaroneras()) {
			if (registroProyectoBean.getViabilidadcamaronera() != null && registroProyectoBean.getViabilidadcamaronera()) {
				Camaroneras camaroneras = new Camaroneras();
				camaroneras = proyectoFacade
						.verificarOficioViabilidadcamaronera(registroProyectoBean
								.getNumeroOficioViabilidadcamaronera());
				if (registroProyectoBean.getProyecto().getArea()> camaroneras.getExtension()){
					errors.add("La extensión del proyecto supera la extensión registrada en el Acuerdo de Concesión para camaroneras.");
				}
//				if (registroProyectoBean.getProyecto().getOficioCamaronera() == null) {
//					errors.add("Debe especificar un oficio de viabilidad de camaronera validado por el sistema.");
//				}
			} else if (registroProyectoBean.getViabilidadcamaronera() != null && !registroProyectoBean.getViabilidadcamaronera()) {
				errors.add("Si su proyecto, obra o actividad no cuenta con una aprobación de camaronera, el registro del proyecto no puede continuar.");
			}
		}
	}
	public void quitarCamaronera(ProyectoCamaronera proyectoCamaronera) {
		BigDecimal extensionCamaronera;
		if(proyectoCamaronera.getCamaroneras()!=null){
			extensionCamaronera=BigDecimal.valueOf(proyectoCamaronera.getCamaroneras().getExtension());
		}else {
			extensionCamaronera=BigDecimal.valueOf(proyectoCamaronera.getExtensionCamaronera());
		}
		BigDecimal total = BigDecimal.valueOf(areaCamaronera).subtract(extensionCamaronera);
		areaCamaronera = total.doubleValue();
		registroProyectoBean.getProyecto().setArea(areaCamaronera);
		getListaProyectosCamaroneras().remove(proyectoCamaronera);
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:area");
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:cb_unidad");
		if(listaProyectosCamaroneras.size()==0){
			reset();
			registroProyectoBean.getProyecto().setUnidad(null);
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:viabilidadcamaroneraContainer");
		}
	}

	public void handleFileUpload(final FileUploadEvent event) {
		uploadedFile = event.getFile();
		transformedFile = null;
		try {
			transformedFile = event.getFile().getContents();
			nombreDocumento=uploadedFile.getFileName();
			nombreArchivo=nombreDocumento;
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:nombreArchivo");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void agregarConcesionCamaronera(){
		ProyectoCamaronera proyectoCamaronera= new ProyectoCamaronera();
		
		proyectoCamaronera.setCodigoCamaronera(acuerdoCamaronera);
		proyectoCamaronera.setExtensionCamaronera(extensionCamaronera);
		
		
		if(transformedFile!=null) {
			Documento documento= new Documento();
			documento.setContenidoDocumento(transformedFile);
			documento.setNombre(nombreDocumento);
			
			proyectoCamaronera.setDocumento(documento);
		} else if(!esGranjaAcuicola()) {
			JsfUtil.addMessageInfo("Debe adjuntar un documento.");
			return;
		}
		
		if(listaProyectosCamaroneras.size()>0){
			boolean acuerdo=true;
			for (ProyectoCamaronera pCamaronera : listaProyectosCamaroneras) {
				if(pCamaronera.getCamaroneras()!=null){
					if(proyectoCamaronera.getCodigoCamaronera().equals(pCamaronera.getCamaroneras().getAcuerdo())){
						acuerdo=false;
						break;
					}
				}else if(proyectoCamaronera.getCodigoCamaronera().equals(pCamaronera.getCodigoCamaronera())){
					acuerdo=false;
					break;
				}
			}
			if(acuerdo){
				areaCamaronera=BigDecimal.valueOf(areaCamaronera).add(BigDecimal.valueOf(extensionCamaronera)).doubleValue();
				registroProyectoBean.getProyecto().setArea(areaCamaronera);
				registroProyectoBean.getProyecto().setUnidad("ha");				
				listaProyectosCamaroneras.add(proyectoCamaronera);
				FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:area");
				FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:cb_unidad");
				mostrarCamaronera = true;
				reset();
			}else {
				JsfUtil.addMessageError("La concesión ya está ingresada.");
				reset();
			}
		}else {
			if(extensionCamaronera>0){
			areaCamaronera=BigDecimal.valueOf(areaCamaronera).add(BigDecimal.valueOf(extensionCamaronera)).doubleValue();
			registroProyectoBean.getProyecto().setArea(areaCamaronera);
			registroProyectoBean.getProyecto().setUnidad("ha");
			listaProyectosCamaroneras.add(proyectoCamaronera);
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:area");
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:cb_unidad");
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:viabilidadcamaroneraContainer");
			mostrarCamaronera = true;
			reset();
			}else {
				JsfUtil.addMessageInfo("La extensión debe ser mayor a 0.");
			}
		}
		
	}
	
	public void reset(){
		acuerdoCamaronera = "";	
		extensionCamaronera=0.0;
		registroProyectoBean.setNumeroOficioViabilidadcamaronera(null);
		nombreArchivo="Seleccione Archivo";
	}
	
	public Documento verDocumento(Documento documento) throws CmisAlfrescoException {        
        try {
        	JsfUtil.descargarPdf(documento.getContenidoDocumento(), documento.getNombre().replace(".pdf", ""));
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        return documento;
    }
	
	public Boolean esGranjaAcuicola() {
		boolean resultado = false;
		try {
			resultado = proyectoFacade
					.esGranjasAcuicolas(catalogoActividadesBean
							.getActividadSistemaSeleccionada().getId());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return resultado;
	}
}
