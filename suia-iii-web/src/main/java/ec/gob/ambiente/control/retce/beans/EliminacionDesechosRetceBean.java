package ec.gob.ambiente.control.retce.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.Visibility;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.retce.model.DesechoAutogestion;
import ec.gob.ambiente.retce.model.DesechoExportacion;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.EliminacionFueraInstalacion;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.IdentificacionDesecho;
import ec.gob.ambiente.retce.model.TransporteEmpresasGestoras;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.EliminacionFueraInstalacionFacade;
import ec.gob.ambiente.retce.services.TransporteFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FaseGestionDesecho;
import ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class EliminacionDesechosRetceBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{declaracionGeneradorRetceBean}")
	@Getter
	@Setter
	private DeclaracionGeneradorRetceBean declaracionGeneradorRetceBean;
	
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
	private EliminacionFueraInstalacionFacade eliminacionFueraInstalacionFacade;
	
	@EJB
	private TransporteFacade transporteFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaTipoUnidad;
	
	@Getter
	@Setter
	private List<EliminacionFueraInstalacion> listaEliminacionFueraInstalacion, listaEliminacionFueraInstalacionEliminar, listaEliminacionFueraInstalacionOriginales,  listaDesechosEliminados;
	
	@Getter
	@Setter
	private List<TransporteEmpresasGestoras> listaEmpresasGestoras, listaEmpresasGestorasEliminar, listaEmpresasGestorasOriginales, listaEmpresasHistorial, listaEmpresasHistorialEliminadas;
	
	@Getter
	@Setter
	private List<SedePrestadorServiciosDesechos> listaPrestadoresServicio;
	
	@Getter
	@Setter
	private List<Documento> listaCertificadosOriginales, listaDocumentosHistorial;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrososRetce generadorDesechosRetce;
	
	@Getter
	@Setter
	private SedePrestadorServiciosDesechos prestadorServicioSeleccionado;
	
	@Getter
	@Setter
	private DesechoPeligroso desechoSeleccionado;
	
	@Getter
	@Setter
	private EliminacionFueraInstalacion eliminacionFueraInstalacion;
	
	@Getter
	@Setter
	private TransporteEmpresasGestoras transporteEmpresaGestora, transporteEmpresaGestoraOriginal;
	
	@Getter
	@Setter
	private Documento documentoCertificadoDestruccion, documentoCertificadoDestruccionOriginal;
	
	@Getter
	@Setter
	private DetalleCatalogoGeneral tipoActividad;
	
	@Getter
	@Setter
	private boolean editarDesechoEliminacion, editarEmpresa;
	
	@Getter
	private boolean panelDesechoEliminacionVisible, panelEmpresaVisible;
	
	@Getter
	@Setter
	private boolean habilitarBtnSiguiente, mostrarHistorialDesechos, mostrarHistorialEmpresa;
	
	protected List<Integer> idFasesGestion;
	
	@PostConstruct
	private void initDatos() {
		inicializacionObj();
		
		listaTipoUnidad = JsfUtil.getBean(IdentificacionDesechosBean.class).getListaTipoUnidad();
		
		idFasesGestion = new ArrayList<Integer>();
		idFasesGestion.add(FaseGestionDesecho.FASE_ELIMINACION);
		
		tipoActividad = detalleCatalogoGeneralFacade.findByCodigo("tipoactividaddesecho.eliminacion");
		
		generadorDesechosRetce = declaracionGeneradorRetceBean.getGeneradorDesechosRetce();
		
	}
	
	public void inicializacionObj() {
		desechoSeleccionado = null;
		eliminacionFueraInstalacion = new EliminacionFueraInstalacion();
		documentoCertificadoDestruccion = new Documento();
		
		listaEliminacionFueraInstalacion = new ArrayList<>();
		listaEmpresasGestoras = new ArrayList<>();
		listaEliminacionFueraInstalacionEliminar = new ArrayList<>();
		listaEmpresasGestorasEliminar = new ArrayList<>();
		listaEliminacionFueraInstalacionOriginales = new ArrayList<>();
		listaEmpresasGestorasOriginales = new ArrayList<>();
		listaCertificadosOriginales = new ArrayList<>();
		
		panelDesechoEliminacionVisible = false;
		panelEmpresaVisible = false;
		editarDesechoEliminacion = false;
		habilitarBtnSiguiente= false;
	}
	
	public void cargarDatos(){
		inicializacionObj();
		
		listaPrestadoresServicio = registroGeneradorDesechosFacade.getTodasSedesPrestadorServicios();
		
		if (generadorDesechosRetce != null && generadorDesechosRetce.getId() != null) {
			listaEliminacionFueraInstalacion = eliminacionFueraInstalacionFacade.getEliminacionPorRgdRetce(generadorDesechosRetce.getId());
			
			if(listaEliminacionFueraInstalacion != null && listaEliminacionFueraInstalacion.size() > 0)
				habilitarBtnSiguiente= true;
			else
				listaEliminacionFueraInstalacion = new ArrayList<>();
			
			listaEmpresasGestoras = transporteFacade.getEmpresasGestorasPorRgdRetce(generadorDesechosRetce.getId(), tipoActividad.getId());
			if(listaEmpresasGestoras != null && listaEmpresasGestoras.size() > 0){

				for (TransporteEmpresasGestoras empresaGestora : listaEmpresasGestoras) {
					List<Documento> documentosManifiesto = documentosFacade.documentoXTablaIdXIdDoc(
							empresaGestora.getId(),
							TransporteEmpresasGestoras.class.getSimpleName(),
							TipoDocumentoSistema.DOCUMENTO_CERTIFICADO_DESTRUCCION);
					if (documentosManifiesto.size() > 0)
						empresaGestora.setCertificadoDestruccion(documentosManifiesto.get(0));
				}
				
				habilitarBtnSiguiente = true;
			} else
				listaEmpresasGestoras = new ArrayList<>();
			
			if(habilitarBtnSiguiente) {
				List<FacesMessage> errorMessages = validateData();
				if (!errorMessages.isEmpty())
					habilitarBtnSiguiente = false;
			}
		}
	}
	
	public void toggleHandleDesechoEliminacion(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelDesechoEliminacionVisible = false;
			editarDesechoEliminacion = false;
		} else
			panelDesechoEliminacionVisible = true;
	}
	
	public void toggleHandleEmpresaGestora(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelEmpresaVisible = false;
			editarEmpresa = false;
		} else
			panelEmpresaVisible = true;
		
		if(!editarEmpresa){
			transporteEmpresaGestora = new TransporteEmpresasGestoras();
		}
		
	}
	
	public List<SedePrestadorServiciosDesechos> getPrestadoresDisponibles(){
		List<SedePrestadorServiciosDesechos> result = new ArrayList<>();		
		
		result.addAll(listaPrestadoresServicio);
		
		if(listaEmpresasGestoras != null) {
			for (TransporteEmpresasGestoras transporte : listaEmpresasGestoras) {
				if (editarEmpresa && transporte.getEmpresaGestora().equals(prestadorServicioSeleccionado))
					continue;

				if (transporte.getEmpresaGestora() != null && transporte.getOtraEmpresa() == null)
					result.remove(transporte.getEmpresaGestora());
			}
		}
		
		return result;
	}
	
	public List<DesechoPeligroso> getDesechosPendientes(){
		List<DesechoPeligroso> result = new ArrayList<>();
		List<DesechoPeligroso> desechosRegistrados = JsfUtil.getBean(AutogestionDesechosBean.class).getDesechosPeligrososSinAutogestion();
		
		List<DesechoExportacion> desechosExportacion = JsfUtil.getBean(ExportacionDesechosBean.class).getListaExportacionDesechos();
		if(desechosExportacion != null && desechosExportacion.size() > 0){
			for (DesechoExportacion desechoExportacion : desechosExportacion) {
				desechosRegistrados.remove(desechoExportacion.getDesechoPeligroso());
			}
		}
		
		result.addAll(desechosRegistrados);
		
		if(listaEliminacionFueraInstalacion != null) {
			for (EliminacionFueraInstalacion eliminacion : listaEliminacionFueraInstalacion) {
				if(editarDesechoEliminacion && eliminacion.getDesechoPeligroso().equals(desechoSeleccionado))
					continue;
					
				if(eliminacion.getDesechoPeligroso() != null )
					result.remove(eliminacion.getDesechoPeligroso());
			}
		}
		
		return result;
	}
	
	private EliminacionFueraInstalacion eliminacionFueraInstalacionOriginal;
	public void editarDesecho(EliminacionFueraInstalacion eliminacion) {
		eliminacionFueraInstalacionOriginal = (EliminacionFueraInstalacion) SerializationUtils.clone(eliminacion);
		eliminacionFueraInstalacion = eliminacion;
		desechoSeleccionado = eliminacion.getDesechoPeligroso();
        editarDesechoEliminacion = true;
        
        listaEliminacionFueraInstalacion.remove(eliminacion);
        
        try {
        	JsfUtil.addCallbackParam("addDatosGenerales");
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al cargar la información");
        }
    }

    public void eliminarDesecho(EliminacionFueraInstalacion eliminacion) {
        try {
            if (eliminacion.getId() != null) {
            	if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
            		EliminacionFueraInstalacion eliminacionOriginal = (EliminacionFueraInstalacion) SerializationUtils.clone(eliminacion);
            		buscarHistorialEliminacionFueraInstalacion(eliminacionOriginal, eliminacion);
				}
            	
            	eliminacion.setEstado(false);
            	listaEliminacionFueraInstalacionEliminar.add(eliminacion);
            }
            listaEliminacionFueraInstalacion.remove(eliminacion);
        } catch (Exception e) {
            
        }
    }
	
	public void aceptarDesechoEliminacion() {
		eliminacionFueraInstalacion.setDesechoPeligroso(desechoSeleccionado);
		if(!listaEliminacionFueraInstalacion.contains(eliminacionFueraInstalacion))
			listaEliminacionFueraInstalacion.add(eliminacionFueraInstalacion);
		
		verificarCambios();
		
		if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			if(eliminacionFueraInstalacion.getId() == null) {
				eliminacionFueraInstalacion.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			}
		}
		
		desechoSeleccionado = null;
		eliminacionFueraInstalacion = new EliminacionFueraInstalacion();
		eliminacionFueraInstalacionOriginal = null;
		
		JsfUtil.addCallbackParam("addDesechoEliminacion");
	}
	
	public void cancelar() {
		if(editarDesechoEliminacion)
			listaEliminacionFueraInstalacion.add(eliminacionFueraInstalacionOriginal);
		
		desechoSeleccionado = null;
		eliminacionFueraInstalacion = new EliminacionFueraInstalacion();
	}
	
	public void buscarHistorialEliminacionFueraInstalacion(EliminacionFueraInstalacion eliminacionFueraOriginal, EliminacionFueraInstalacion eliminacionFuera) {
		if(eliminacionFuera.getNumeroObservacion() == null || 
				eliminacionFuera.getNumeroObservacion() != declaracionGeneradorRetceBean.getNumeroObservacion()) {
			EliminacionFueraInstalacion desechoHistorial = eliminacionFueraInstalacionFacade.getEliminacionFueraInstalacionHistorial(eliminacionFuera.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(desechoHistorial == null){
				Boolean tieneHistorial = false;
				for (EliminacionFueraInstalacion desecho : listaEliminacionFueraInstalacionOriginales) {
					if(desecho.getIdRegistroOriginal().equals(eliminacionFuera.getId())) {
						tieneHistorial = true;
						break;
					}
				}
				
				if(!tieneHistorial){
					eliminacionFueraOriginal.setId(null);
					eliminacionFueraOriginal.setHistorial(true);
					eliminacionFueraOriginal.setIdRegistroOriginal(eliminacionFuera.getId());
					eliminacionFueraOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
					listaEliminacionFueraInstalacionOriginales.add(eliminacionFueraOriginal);
				}
			}
		}
	}
	
	public void editarEmpresaGestora(TransporteEmpresasGestoras empresa) {
		transporteEmpresaGestoraOriginal = (TransporteEmpresasGestoras) SerializationUtils.clone(empresa);
		editarEmpresa = true;
		transporteEmpresaGestora = empresa;
		prestadorServicioSeleccionado = empresa.getEmpresaGestora();
		documentoCertificadoDestruccion = empresa.getCertificadoDestruccion();
		documentoCertificadoDestruccionOriginal = (Documento) SerializationUtils.clone(empresa.getCertificadoDestruccion());
	}
	
	public void eliminarEmpresaGestora(TransporteEmpresasGestoras empresa) {
		try {
			if (empresa.getId() != null) {
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
					TransporteEmpresasGestoras desechoOriginal = (TransporteEmpresasGestoras) SerializationUtils.clone(empresa);
					buscarHistorialEmpresaGestora(desechoOriginal, empresa);
				}
				
				empresa.setEstado(false);
				listaEmpresasGestorasEliminar.add(empresa);
			}
			listaEmpresasGestoras.remove(empresa);
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al eliminar empresa gestora");
		}
	}
	
	public void aceptarEmpresaGestora() {
		transporteEmpresaGestora.setEmpresaGestora(prestadorServicioSeleccionado);
		transporteEmpresaGestora.setCertificadoDestruccion(documentoCertificadoDestruccion);
		transporteEmpresaGestora.setIdGeneradorRetce(generadorDesechosRetce.getId());
		if(!listaEmpresasGestoras.contains(transporteEmpresaGestora))
			listaEmpresasGestoras.add(transporteEmpresaGestora);
		
		if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			if(transporteEmpresaGestora.getId() != null) {
				if(!transporteEmpresaGestoraOriginal.equalsObject(transporteEmpresaGestora)) 
					buscarHistorialEmpresaGestora(transporteEmpresaGestoraOriginal, transporteEmpresaGestora);
			}else {
				transporteEmpresaGestora.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			}
			
			if(documentoCertificadoDestruccionOriginal != null && documentoCertificadoDestruccion.getContenidoDocumento()!=null)
				listaCertificadosOriginales.add(documentoCertificadoDestruccionOriginal);
		}
		
		cancelarEmpresaGestora();
		JsfUtil.addCallbackParam("addEmpresaEliminacion");
	}
	
	public void cancelarEmpresaGestora() {
		documentoCertificadoDestruccionOriginal = null;
		prestadorServicioSeleccionado = null;
		documentoCertificadoDestruccion = new Documento();
		transporteEmpresaGestora = new TransporteEmpresasGestoras();
	}
	
	public void buscarHistorialEmpresaGestora(TransporteEmpresasGestoras empresaGestoraOriginal, TransporteEmpresasGestoras empresaGestora) {
		if(empresaGestora.getNumeroObservacion() == null || 
				empresaGestora.getNumeroObservacion() != declaracionGeneradorRetceBean.getNumeroObservacion()) {
			TransporteEmpresasGestoras desechoHistorial = transporteFacade.getTransporteEmpresasGestorasHistorial(empresaGestora.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(desechoHistorial == null){
				Boolean tieneHistorial = false;
				for (TransporteEmpresasGestoras desecho : listaEmpresasGestorasOriginales) {
					if(desecho.getIdRegistroOriginal().equals(empresaGestora.getId())) {
						tieneHistorial = true;
						break;
					}
				}
				
				if(!tieneHistorial){
					empresaGestoraOriginal.setId(null);
					empresaGestoraOriginal.setHistorial(true);
					empresaGestoraOriginal.setIdRegistroOriginal(empresaGestora.getId());
					empresaGestoraOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
					listaEmpresasGestorasOriginales.add(empresaGestoraOriginal);
				}
			}
		}
	}
	
	public void uploadFileCertificado(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoCertificadoDestruccion.setId(null);
		documentoCertificadoDestruccion.setContenidoDocumento(contenidoDocumento);
		documentoCertificadoDestruccion.setNombre(event.getFile().getFileName());
		documentoCertificadoDestruccion.setMime("application/pdf");
		documentoCertificadoDestruccion.setExtesion(".pdf");
				
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void validateEliminacion(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = validateData();

		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public List<FacesMessage> validateData() {
		List<FacesMessage> errorMessages = new ArrayList<>();

		if (listaEliminacionFueraInstalacion.size() == 0 && getDesechosPendientes().size() > 0)
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe ingresar la información de eliminación en uno o varios desechos.", null));
		
		if (listaEmpresasGestoras.size() == 0 && listaEliminacionFueraInstalacion.size() > 0)
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe ingresar al menos una empresa gestora.", null));
		
		return errorMessages;
	}
	
	public void validateEmpresaGestora(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if (documentoCertificadoDestruccion.getId() == null && documentoCertificadoDestruccion.getContenidoDocumento() == null)
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Certificado de destrucción' es requerido.", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public Boolean guardarEliminacion(){
		try{
			for (EliminacionFueraInstalacion eliminacionDesechos : listaEliminacionFueraInstalacion) {
				eliminacionDesechos.setIdGeneradorRetce(generadorDesechosRetce.getId());
			}
			eliminacionFueraInstalacionFacade.guardarEliminacion(listaEliminacionFueraInstalacion);
			
			for (TransporteEmpresasGestoras empresaGestora : listaEmpresasGestoras) {
				empresaGestora.setTipoActividad(tipoActividad);
				transporteFacade.guardarTransporteEmpresaGestora(empresaGestora);
				
				Documento documento = empresaGestora.getCertificadoDestruccion();
				if(documento.getContenidoDocumento()!=null) {
					documento.setIdTable(empresaGestora.getId());
					documento.setNombreTabla(TransporteEmpresasGestoras.class.getSimpleName());
					documento.setDescripcion("Certificado destrucción gestor ambiental");
					documento.setEstado(true);               
					Documento documentoSave = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
									generadorDesechosRetce.getCodigoGenerador(),
									"GENERADOR_DESECHOS",
									generadorDesechosRetce.getId().longValue(),
									documento,
									TipoDocumentoSistema.DOCUMENTO_CERTIFICADO_DESTRUCCION,
									null);
					
					for (Documento documentoOrg : listaCertificadosOriginales) {
						if(documentoOrg.getIdTable().equals(empresaGestora.getId())){
							documentoOrg.setIdHistorico(documentoSave.getId());
							documentoOrg.setNumeroNotificacion(declaracionGeneradorRetceBean.getNumeroObservacion());
							documentosFacade.actualizarDocumento(documentoOrg);
						}
					}
				}
			}
			
			transporteFacade.eliminarTransporteEmpresasGestoras(listaEmpresasGestorasEliminar);
			eliminacionFueraInstalacionFacade.eliminarEliminacionFueraInstalacion(listaEliminacionFueraInstalacionEliminar);
			
			declaracionGeneradorRetceBean.eliminarDatosAsociados();
			
			if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
				guardarHistorial();
			}
			
			return true;
		} catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            return false;
        }
	}
	
	public StreamedContent descargar(Documento documento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
			} else if (documento.getContenidoDocumento() != null) {
				documentoContent = documento.getContenidoDocumento();
			}
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void validarCantidadEliminada() {
		if (eliminacionFueraInstalacion.getCantidad() != null) {
			Boolean permitir = declaracionGeneradorRetceBean
					.validarCantidadReporteDesecho(
							eliminacionFueraInstalacion.getCantidad(),
							desechoSeleccionado.getId(),
							eliminacionFueraInstalacion.getTipoUnidad().getId());

			if (!permitir) {
				eliminacionFueraInstalacion.setCantidad(null);
			}
		}
	}
	
	public void getInfoDesecho() {
		if (desechoSeleccionado != null) {
			List<IdentificacionDesecho> listaIdentificacionDesechos = JsfUtil.getBean(IdentificacionDesechosBean.class).getListaIdentificacionDesechos();
			List<DesechoAutogestion> listaDesechosAutogestion = JsfUtil.getBean(AutogestionDesechosBean.class).getListaDesechosAutogestion();
			
			for (IdentificacionDesecho desecho : listaIdentificacionDesechos) {
				if(desechoSeleccionado.getId().equals(desecho.getDesechoPeligroso().getId())){
					Double sumaTotal = (desecho.getCantidadAnioAnterior() + desecho.getCantidadGeneracionAnual()) - desecho.getCantidadNoGestionada();
					
					Double valorAutogestionado = 0.0;
					for (DesechoAutogestion desechoAutogestion : listaDesechosAutogestion) {
						if(desechoSeleccionado.getId().equals(desechoAutogestion.getDesechoPeligroso().getId())){
							valorAutogestionado = desechoAutogestion.getTotalToneladas();
							if (!desecho.getTipoUnidad().getId().equals(declaracionGeneradorRetceBean.getTipoUnidadTonelada().getId())) 
								valorAutogestionado = valorAutogestionado / declaracionGeneradorRetceBean.getFactorKgT();
							
							sumaTotal = sumaTotal - valorAutogestionado;
							break;
						}
					}
					
					eliminacionFueraInstalacion.setTipoUnidad(desecho.getTipoUnidad());
					eliminacionFueraInstalacion.setCantidad(sumaTotal);
				}
			}
			
		}
	}
	
	public void verificarCambios() {
		if (eliminacionFueraInstalacion != null) {
			declaracionGeneradorRetceBean.validarDatosRelacionados(5, eliminacionFueraInstalacion.getDesechoPeligroso().getId());
		}
	}
	
	public void guardarHistorial() {
		if(listaEliminacionFueraInstalacionOriginales.size() > 0)
			eliminacionFueraInstalacionFacade.guardarEliminacion(listaEliminacionFueraInstalacionOriginales);
		
		if(listaEmpresasGestorasOriginales.size() > 0)
			transporteFacade.guardarListaTransporteEmpresaGestora(listaEmpresasGestorasOriginales);
		
		listaEliminacionFueraInstalacionOriginales = new ArrayList<>();
		listaEmpresasGestorasOriginales = new ArrayList<>();
		listaCertificadosOriginales = new ArrayList<>();
	}
	
	public void cargarDatosHistorial(Integer numeroObservacion) {
		mostrarHistorialDesechos = false;
		mostrarHistorialEmpresa = false;
		
		if(listaEliminacionFueraInstalacion != null && listaEliminacionFueraInstalacion.size() > 0) {
			for (EliminacionFueraInstalacion eliminacion : listaEliminacionFueraInstalacion) {
				if(eliminacion.getNumeroObservacion() != null && eliminacion.getNumeroObservacion().equals(numeroObservacion)) {
					eliminacion.setNuevoEnModificacion(true);
					mostrarHistorialDesechos = true;
				}
			}
		}
		listaDesechosEliminados = eliminacionFueraInstalacionFacade.getDesechosEliminadosPorRgdRetce(generadorDesechosRetce.getId());
		
//		listaEmpresasGestoras = transporteFacade.getEmpresasGestorasPorRgdRetce(generadorDesechosRetce.getId(), tipoActividad.getId()); no se necesitaxq ya tengo el listado de las empresas
		if(listaEmpresasGestoras != null && listaEmpresasGestoras.size() > 0) {
			for (TransporteEmpresasGestoras empresa : listaEmpresasGestoras) {
				if(empresa.getNumeroObservacion() != null && empresa.getNumeroObservacion().equals(numeroObservacion)){
					empresa.setNuevoEnModificacion(true);
					mostrarHistorialEmpresa = true;
				}

				List<Documento> documentosManifiesto = documentosFacade.documentoXTablaIdXIdDoc(
								empresa.getId(),
								TransporteEmpresasGestoras.class.getSimpleName(),
								TipoDocumentoSistema.DOCUMENTO_CERTIFICADO_DESTRUCCION);
				List<Documento> documentosHistorial = new ArrayList<>();
				if (documentosManifiesto.size() > 0) {
					for (Documento documento : documentosManifiesto) {
						if(documento.getIdHistorico() != null) {
							documentosHistorial.add(documento);
							mostrarHistorialEmpresa = true;
						}
					}
				}
				
				empresa.setListaHistorialDocumentos(documentosHistorial);
			}
		}
		
		listaEmpresasHistorialEliminadas = transporteFacade.getEmpresasEliminadasPorRgdRetce(generadorDesechosRetce.getId(), tipoActividad.getId());
		if(listaEmpresasHistorialEliminadas != null) {
			for (TransporteEmpresasGestoras empresa : listaEmpresasHistorialEliminadas) {
				List<Documento> documentosCertificado = documentosFacade.documentoXTablaIdXIdDoc(
						empresa.getIdRegistroOriginal(),
						TransporteEmpresasGestoras.class.getSimpleName(),
						TipoDocumentoSistema.DOCUMENTO_CERTIFICADO_DESTRUCCION);
				if (documentosCertificado.size() > 0) {
					empresa.setCertificadoDestruccion(documentosCertificado.get(0));
				}
			}
		}
	}
	
	public void verHistorialEmpresa(TransporteEmpresasGestoras empresaGestora) {
		listaEmpresasHistorial = empresaGestora.getListaHistorial();
		listaDocumentosHistorial = empresaGestora.getListaHistorialDocumentos();
	}
}
