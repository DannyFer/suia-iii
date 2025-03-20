package ec.gob.ambiente.control.retce.beans;

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
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.retce.model.DesechoAutogestion;
import ec.gob.ambiente.retce.model.DesechoExportacion;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.DisposicionFueraInstalacion;
import ec.gob.ambiente.retce.model.EliminacionFueraInstalacion;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.IdentificacionDesecho;
import ec.gob.ambiente.retce.model.TransporteEmpresasGestoras;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.DisposicionFueraInstalacionFacade;
import ec.gob.ambiente.retce.services.TransporteFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FaseGestionDesecho;
import ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DisposicionDesechosRetceBean implements Serializable {

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
	private TransporteFacade transporteFacade;
	
	@EJB
	private DisposicionFueraInstalacionFacade disposicionFueraInstalacionFacade;

	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaTipoUnidad;
	
	@Getter
	@Setter
	private List<DisposicionFueraInstalacion> listaDisposicionFueraInstalacion, listaDisposicionFueraInstalacionEliminar, listaDisposicionFueraInstalacionOriginales, listaDesechosEliminados;
	
	@Getter
	@Setter
	private List<TransporteEmpresasGestoras> listaEmpresasGestoras, listaEmpresasGestorasEliminar, listaEmpresasGestorasOriginales, listaEmpresasHistorialEliminadas;
	
	@Getter
	@Setter
	private List<SedePrestadorServiciosDesechos> listaPrestadoresServicio;

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
	private DisposicionFueraInstalacion disposicionFueraInstalacion;
	
	@Getter
	@Setter
	private TransporteEmpresasGestoras transporteEmpresaGestora, transporteEmpresaGestoraOriginal;

	@Getter
	@Setter
	private DetalleCatalogoGeneral tipoActividad;
	
	@Getter
	@Setter
	private boolean editarDesechoDisposicion, editarEmpresa;
	
	@Getter
	private boolean panelDesechoDisposicionVisible, panelEmpresaVisible;

	@Getter
	@Setter
	private boolean habilitarBtnSiguiente, mostrarHistorialDesechos, mostrarHistorialEmpresa;
	
	protected List<Integer> idFasesGestion;
	
	@PostConstruct
	private void initDatos() {
		inicializacionObj();

		listaTipoUnidad = JsfUtil.getBean(IdentificacionDesechosBean.class).getListaTipoUnidad();
		
		idFasesGestion = new ArrayList<Integer>();
		idFasesGestion.add(FaseGestionDesecho.FASE_DISPOSICION_FINAL);

		tipoActividad = detalleCatalogoGeneralFacade.findByCodigo("tipoactividaddesecho.disposicion");
		
		generadorDesechosRetce = declaracionGeneradorRetceBean.getGeneradorDesechosRetce();
		
	}
	
	public void inicializacionObj() {
		desechoSeleccionado = null;
		disposicionFueraInstalacion = new DisposicionFueraInstalacion();
		
		listaDisposicionFueraInstalacion = new ArrayList<>();
		listaEmpresasGestoras = new ArrayList<>();
		listaEmpresasGestorasEliminar = new ArrayList<>();
		listaDisposicionFueraInstalacionEliminar = new ArrayList<>();
		listaDisposicionFueraInstalacionOriginales = new ArrayList<>();
		listaEmpresasGestorasOriginales = new ArrayList<>();
		
		panelDesechoDisposicionVisible = false;
		panelEmpresaVisible = false;
		editarDesechoDisposicion = false;
		habilitarBtnSiguiente= false;
	}
	
	public void cargarDatos(){
		inicializacionObj();
		
		listaPrestadoresServicio = registroGeneradorDesechosFacade.getTodasSedesPrestadorServicios();
		
		if (generadorDesechosRetce != null && generadorDesechosRetce.getId() != null) {
			listaDisposicionFueraInstalacion = disposicionFueraInstalacionFacade.getDisposicionPorRgdRetce(generadorDesechosRetce.getId());
			
			if(listaDisposicionFueraInstalacion != null && listaDisposicionFueraInstalacion.size() > 0)
				habilitarBtnSiguiente= true;
			else
				listaDisposicionFueraInstalacion = new ArrayList<>();
			
			listaEmpresasGestoras = transporteFacade.getEmpresasGestorasPorRgdRetce(generadorDesechosRetce.getId(), tipoActividad.getId());
			if(listaEmpresasGestoras != null && listaEmpresasGestoras.size() > 0)
				habilitarBtnSiguiente = true;
			else
				listaEmpresasGestoras = new ArrayList<>();
			
			if(habilitarBtnSiguiente) {
				List<FacesMessage> errorMessages = validateData();
				if (!errorMessages.isEmpty())
					habilitarBtnSiguiente = false;
			}
		}
	}
	
	public void toggleHandleDesechoDisposicion(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelDesechoDisposicionVisible = false;
			editarDesechoDisposicion = false;
		} else
			panelDesechoDisposicionVisible = true;
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
		
		List<EliminacionFueraInstalacion> eliminacionDesechos = JsfUtil.getBean(EliminacionDesechosRetceBean.class).getListaEliminacionFueraInstalacion();
		if(eliminacionDesechos != null && eliminacionDesechos.size() > 0){
			for (EliminacionFueraInstalacion eliminacionDesecho : eliminacionDesechos) {
				desechosRegistrados.remove(eliminacionDesecho.getDesechoPeligroso());
			}
		}
		
		result.addAll(desechosRegistrados);
		
		if(listaDisposicionFueraInstalacion != null) {
			for (DisposicionFueraInstalacion eliminacion : listaDisposicionFueraInstalacion) {
				if(editarDesechoDisposicion && eliminacion.getDesechoPeligroso().equals(desechoSeleccionado))
					continue;
					
				if(eliminacion.getDesechoPeligroso() != null )
					result.remove(eliminacion.getDesechoPeligroso());
			}
		}
		
		return result;
	}
	
	private DisposicionFueraInstalacion disposicionFueraInstalacionOriginal;
	public void editarDesecho(DisposicionFueraInstalacion eliminacion) {
		disposicionFueraInstalacionOriginal = (DisposicionFueraInstalacion) SerializationUtils.clone(eliminacion);
		disposicionFueraInstalacion = eliminacion;
		desechoSeleccionado = eliminacion.getDesechoPeligroso();
        editarDesechoDisposicion = true;
        
        listaDisposicionFueraInstalacion.remove(eliminacion);
        
        try {
        	JsfUtil.addCallbackParam("addDatosGenerales");
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al cargar la información");
        }
    }

    public void eliminarDesecho(DisposicionFueraInstalacion eliminacion) {
        try {
            if (eliminacion.getId() != null) {
            	if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
            		DisposicionFueraInstalacion disposicionOriginal = (DisposicionFueraInstalacion) SerializationUtils.clone(eliminacion);
            		buscarHistorialDiposicionFueraInstalacion(disposicionOriginal, eliminacion);
				}
            	
            	eliminacion.setEstado(false);
            	listaDisposicionFueraInstalacionEliminar.add(eliminacion);
            }
            listaDisposicionFueraInstalacion.remove(eliminacion);
        } catch (Exception e) {
            
        }
    }
	
	public void aceptarDesechoDisposicion() {
		disposicionFueraInstalacion.setDesechoPeligroso(desechoSeleccionado);
		if(!listaDisposicionFueraInstalacion.contains(disposicionFueraInstalacion))
			listaDisposicionFueraInstalacion.add(disposicionFueraInstalacion);
		
		if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			if(disposicionFueraInstalacion.getId() == null) {
				disposicionFueraInstalacion.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			}
		}
		
		desechoSeleccionado = null;
		disposicionFueraInstalacion = new DisposicionFueraInstalacion();
		disposicionFueraInstalacionOriginal = null;
		
		JsfUtil.addCallbackParam("addDesechoDisposicion");
	}
	
	public void cancelar() {
		if(editarDesechoDisposicion)
			listaDisposicionFueraInstalacion.add(disposicionFueraInstalacionOriginal);
		
		desechoSeleccionado = null;
		disposicionFueraInstalacion = new DisposicionFueraInstalacion();
	}
	
	public void buscarHistorialDiposicionFueraInstalacion(DisposicionFueraInstalacion disposicionFueraOriginal, DisposicionFueraInstalacion disposicionFuera) {
		if(disposicionFuera.getNumeroObservacion() == null || 
				disposicionFuera.getNumeroObservacion() != declaracionGeneradorRetceBean.getNumeroObservacion()) {
			DisposicionFueraInstalacion desechoHistorial = disposicionFueraInstalacionFacade.getDisposicionFueraInstalacionHistorial(disposicionFuera.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(desechoHistorial == null){
				Boolean tieneHistorial = false;
				for (DisposicionFueraInstalacion desecho : listaDisposicionFueraInstalacionOriginales) {
					if(desecho.getIdRegistroOriginal().equals(disposicionFuera.getId())) {
						tieneHistorial = true;
						break;
					}
				}
				
				if(!tieneHistorial){
					disposicionFueraOriginal.setId(null);
					disposicionFueraOriginal.setHistorial(true);
					disposicionFueraOriginal.setIdRegistroOriginal(disposicionFuera.getId());
					disposicionFueraOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
					listaDisposicionFueraInstalacionOriginales.add(disposicionFueraOriginal);
				}
			}
		}
	}
	
	public void editarEmpresaGestora(TransporteEmpresasGestoras empresa) {
		transporteEmpresaGestoraOriginal = (TransporteEmpresasGestoras) SerializationUtils.clone(empresa);
		editarEmpresa = true;
		transporteEmpresaGestora = empresa;
		prestadorServicioSeleccionado = empresa.getEmpresaGestora();
	}
	
	public void eliminarEmpresaGestora(TransporteEmpresasGestoras empresa) {
			if (empresa.getId() != null) {
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
					TransporteEmpresasGestoras desechoOriginal = (TransporteEmpresasGestoras) SerializationUtils.clone(empresa);
					buscarHistorialEmpresaGestora(desechoOriginal, empresa);
				}
				
				empresa.setEstado(false);
				listaEmpresasGestorasEliminar.add(empresa);
			}
			listaEmpresasGestoras.remove(empresa);
	}
	
	public void aceptarEmpresaGestora() {
		transporteEmpresaGestora.setEmpresaGestora(prestadorServicioSeleccionado);
		
		if(!listaEmpresasGestoras.contains(transporteEmpresaGestora))
			listaEmpresasGestoras.add(transporteEmpresaGestora);
		
		if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			if(transporteEmpresaGestora.getId() == null) {
				transporteEmpresaGestora.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			}
		}
		
		cancelarEmpresaGestora();
		JsfUtil.addCallbackParam("addEmpresaDisposicion");
	}
	
	public void cancelarEmpresaGestora() {
		prestadorServicioSeleccionado = null;
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
	
	public void validateDisposicion(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = validateData();

		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public List<FacesMessage> validateData() {
		List<FacesMessage> errorMessages = new ArrayList<>();

		if (listaDisposicionFueraInstalacion.size() == 0 && getDesechosPendientes().size() > 0)
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe ingresar la información de disposición en uno o varios desechos.", null));
		
		if (listaEmpresasGestoras.size() == 0 && listaDisposicionFueraInstalacion.size() > 0)
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe ingresar al menos una empresa gestora.", null));
		
		return errorMessages;
	}
	
	public Boolean guardarDisposicion(){
		try{
			for (DisposicionFueraInstalacion disposicionDesechos : listaDisposicionFueraInstalacion) {
				disposicionDesechos.setIdGeneradorRetce(generadorDesechosRetce.getId());
			}
			disposicionFueraInstalacionFacade.guardarListaDisposicion(listaDisposicionFueraInstalacion);
			
			for (TransporteEmpresasGestoras empresaGestora : listaEmpresasGestoras) {
				empresaGestora.setTipoActividad(tipoActividad);
				empresaGestora.setIdGeneradorRetce(generadorDesechosRetce.getId());
			}

			transporteFacade.guardarListaTransporteEmpresaGestora(listaEmpresasGestoras);
			
			if(listaEmpresasGestorasEliminar.size() > 0)
				transporteFacade.eliminarTransporteEmpresasGestoras(listaEmpresasGestorasEliminar);
			
			if(listaDisposicionFueraInstalacionEliminar.size() > 0)
				disposicionFueraInstalacionFacade.eliminarListaDisposicion(listaDisposicionFueraInstalacionEliminar);
			
			
			if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
				guardarHistorial();
			}
			
			return true;
		} catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            return false;
        }
	}
	
	public void validarCantidadEliminada() {
		if (disposicionFueraInstalacion.getCantidad() != null) {
			Boolean permitir = declaracionGeneradorRetceBean
					.validarCantidadReporteDesecho(
							disposicionFueraInstalacion.getCantidad(),
							desechoSeleccionado.getId(),
							disposicionFueraInstalacion.getTipoUnidad().getId());

			if (!permitir) {
				disposicionFueraInstalacion.setCantidad(null);
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
					
					disposicionFueraInstalacion.setTipoUnidad(desecho.getTipoUnidad());
					disposicionFueraInstalacion.setCantidad(sumaTotal);
				}
			}
			
		}
	}
	
	public void guardarHistorial() {
		if(listaDisposicionFueraInstalacionOriginales.size() > 0)
			disposicionFueraInstalacionFacade.guardarListaDisposicion(listaDisposicionFueraInstalacionOriginales);
		
		if(listaEmpresasGestorasOriginales.size() > 0)
			transporteFacade.guardarListaTransporteEmpresaGestora(listaEmpresasGestorasOriginales);
		
		listaDisposicionFueraInstalacionOriginales = new ArrayList<>();
		listaEmpresasGestorasOriginales = new ArrayList<>();
	}
	
	public void cargarDatosHistorial(Integer numeroObservacion) {
		mostrarHistorialDesechos = false;
		mostrarHistorialEmpresa = false;
		
		if(listaDisposicionFueraInstalacion != null && listaDisposicionFueraInstalacion.size() > 0) {
			for (DisposicionFueraInstalacion disposicion : listaDisposicionFueraInstalacion) {
				if(disposicion.getNumeroObservacion() != null && 
						disposicion.getNumeroObservacion().equals(numeroObservacion)) {
					disposicion.setNuevoEnModificacion(true);
					mostrarHistorialDesechos = true;
				}
			}
		}
		
		listaEmpresasGestoras = transporteFacade.getEmpresasGestorasPorRgdRetce(generadorDesechosRetce.getId(), tipoActividad.getId());
		if(listaEmpresasGestoras != null && listaEmpresasGestoras.size() > 0) {
			for (TransporteEmpresasGestoras empresa : listaEmpresasGestoras) {
				if(empresa.getNumeroObservacion() != null && empresa.getNumeroObservacion().equals(numeroObservacion)){
					empresa.setNuevoEnModificacion(true);
					mostrarHistorialEmpresa = true;
				}
			}
		}
		
		listaDesechosEliminados = disposicionFueraInstalacionFacade.getDesechosEliminadosPorRgdRetce(generadorDesechosRetce.getId());
		
		listaEmpresasHistorialEliminadas = transporteFacade.getEmpresasEliminadasPorRgdRetce(generadorDesechosRetce.getId(), tipoActividad.getId());
	}
}
