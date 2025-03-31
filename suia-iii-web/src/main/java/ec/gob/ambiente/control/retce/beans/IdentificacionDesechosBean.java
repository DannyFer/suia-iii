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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.retce.model.DesechoAutogestion;
import ec.gob.ambiente.retce.model.DesechoExportacion;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.DetalleManifiestoDesecho;
import ec.gob.ambiente.retce.model.DisposicionFueraInstalacion;
import ec.gob.ambiente.retce.model.EliminacionFueraInstalacion;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.IdentificacionDesecho;
import ec.gob.ambiente.retce.services.DesechoAutogestionFacade;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.IdentificacionDesechosFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class IdentificacionDesechosBean implements Serializable {

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
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	@EJB
	private IdentificacionDesechosFacade identificacionDesechosFacade;
	
	@EJB
	private DesechoAutogestionFacade desechoAutogestionFacade;
	
	@Getter
	@Setter
	private List<UbicacionesGeografica> listaPaisDestino;
	
	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaTipoUnidad;
	
	@Getter
	@Setter
	private List<IdentificacionDesecho> listaIdentificacionDesechos, listaIdentificacionDesechosOriginales, listaIdentificacionDesechosHistoriales;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrososRetce generadorDesechosRetce;
	
	@Getter
	@Setter
	private DesechoPeligroso desechoSeleccionado;
	
	@Getter
	@Setter
	private IdentificacionDesecho identificacionDesecho;
	
	@Getter
	@Setter
	private boolean editar;
	
	@Getter
	private boolean panelAdicionarVisible;
	
	@Getter
	@Setter
	private boolean habilitarBtnSiguiente;
	
	@Getter
	@Setter
	private Boolean existeReporteUnidades;
	
	@Getter
	@Setter
	private List<DesechoAutogestion> listaDesechosAutogestionEliminar = new ArrayList<>();
	@Getter
	@Setter
	private List<DetalleManifiestoDesecho> listaDesechoManifiestosEliminar = new ArrayList<>();
	@Getter
	@Setter
	private List<DesechoExportacion> listaDesechosExportacionEliminar = new ArrayList<>();
	@Getter
	@Setter
	private List<EliminacionFueraInstalacion> listaDesechosEliminacionEliminar = new ArrayList<>();
	@Getter
	@Setter
	private List<DisposicionFueraInstalacion> listaDesechosDisposicionEliminar = new ArrayList<>();
	
	@PostConstruct
	private void initDatos() {
		desechoSeleccionado = null;
		identificacionDesecho = new IdentificacionDesecho();
		
		listaIdentificacionDesechos = new ArrayList<>();
		listaIdentificacionDesechosOriginales = new ArrayList<>();
		
		panelAdicionarVisible = false;
		editar = false;
		habilitarBtnSiguiente = false;
		
		listaTipoUnidad = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("generador.tipo_unidad");
		
		listaPaisDestino = ubicacionGeograficaFacade.getUbicacionPorPadre(null);
		
		generadorDesechosRetce = declaracionGeneradorRetceBean.getGeneradorDesechosRetce();
		if(generadorDesechosRetce != null && generadorDesechosRetce.getId() != null){
			//cargar datos desde base
			listaIdentificacionDesechos = identificacionDesechosFacade.getIdentificacionDesechosPorRgdRetce(generadorDesechosRetce.getId());
			
			if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) 
				listaIdentificacionDesechosHistoriales = identificacionDesechosFacade.getIdentificacionDesechosHistorialPorRgdRetce(generadorDesechosRetce.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			
			if(listaIdentificacionDesechos == null)
				listaIdentificacionDesechos = new ArrayList<>();
			else {
				existeReporteUnidades = false;
				for (IdentificacionDesecho desecho : listaIdentificacionDesechos) {
					if(desecho.getDesechoPeligroso().isDesechoES_04() ||
							desecho.getDesechoPeligroso().isDesechoES_06()){
						existeReporteUnidades = true;
						break;
					}
				}
			}
			
			List<DesechoPeligroso> pendientes = getDesechosPendientes();
			if(pendientes.size() == 0)
				habilitarBtnSiguiente = true;
		}
	}
	
	public void nuevoDesecho() {
		editar = false;
	}
	
	public List<DesechoPeligroso> getDesechosPendientes(){
		List<DesechoPeligroso> result = new ArrayList<>();
		List<DesechoPeligroso> desechosRegistrados = declaracionGeneradorRetceBean.getListaDesechosGenerador();
		
		result.addAll(desechosRegistrados);
		
		if(listaIdentificacionDesechos != null) {
			for (IdentificacionDesecho identificacion : listaIdentificacionDesechos) {
				if(editar && identificacion.getDesechoPeligroso().equals(desechoSeleccionado))
					continue;
					
				if(identificacion.getDesechoPeligroso() != null )
					result.remove(identificacion.getDesechoPeligroso());
			}
		}
		
		return result;
	}
	
	private IdentificacionDesecho identificacionDesechoOriginal;
	public void editarDesecho(IdentificacionDesecho desecho) {
		identificacionDesechoOriginal = (IdentificacionDesecho) SerializationUtils.clone(desecho);
		identificacionDesecho = desecho;
		desechoSeleccionado = desecho.getDesechoPeligroso();
		
		listaIdentificacionDesechos.remove(desecho);
		
        editar = true;
        
    }
	
	public void aceptar() {
		identificacionDesecho.setDesechoPeligroso(desechoSeleccionado);
		
		Double totalGestionado = (identificacionDesecho.getCantidadAnioAnterior() + identificacionDesecho.getCantidadGeneracionAnual()) - identificacionDesecho.getCantidadNoGestionada();
		Double factorKgT = declaracionGeneradorRetceBean.getFactorKgT();
		if (!identificacionDesecho.getTipoUnidad().getId()
				.equals(declaracionGeneradorRetceBean.getTipoUnidadTonelada().getId())) 
			identificacionDesecho.setCantidadTotalToneladas(totalGestionado * factorKgT);
		else
			identificacionDesecho.setCantidadTotalToneladas(totalGestionado);
		
		listaIdentificacionDesechos.add(identificacionDesecho);
		
		existeReporteUnidades = false;
		for (IdentificacionDesecho desecho : listaIdentificacionDesechos) {
			if(desecho.getDesechoPeligroso().isDesechoES_04() ||
					desecho.getDesechoPeligroso().isDesechoES_06()){
				existeReporteUnidades = true;
				break;
			}
		}
		
		verificarCambios();
		
		desechoSeleccionado = null;
		identificacionDesecho = new IdentificacionDesecho();
		identificacionDesechoOriginal = new IdentificacionDesecho();
		
		JsfUtil.addCallbackParam("addDatosGenerales");
	}
	
	public void cancelar() {
		if(editar)
			listaIdentificacionDesechos.add(identificacionDesechoOriginal);
		
		desechoSeleccionado = null;
		identificacionDesecho = new IdentificacionDesecho();
	}
	
	public void	validarCantidadNoGestionada(Integer tipoCantidad) {
		Double sumaAnteriorGestion = 0.0;
		Double valorNoGestionado = 0.0;
		if(tipoCantidad.equals(1)) {
			sumaAnteriorGestion = identificacionDesecho.getCantidadAnioAnterior() + identificacionDesecho.getCantidadGeneracionAnual();
			valorNoGestionado = identificacionDesecho.getCantidadNoGestionada();
		} else if(tipoCantidad.equals(2)) { 
			sumaAnteriorGestion = Double.valueOf(identificacionDesecho.getCantidadAnioAnteriorUnidades() + identificacionDesecho.getCantidadGeneracionAnualUnidades());
			valorNoGestionado = Double.valueOf(identificacionDesecho.getCantidadNoGestionadaUnidades());
		}
		
		if(valorNoGestionado > sumaAnteriorGestion){
			JsfUtil.addMessageError("El valor que no pudo ser gestionado no debe superar la cantidad del año anterior (si lo tuviera) más la cantidad de generación anual");
			if(tipoCantidad.equals(1)) 
				identificacionDesecho.setCantidadNoGestionada(null);
			else if(tipoCantidad.equals(2)) 
				identificacionDesecho.setCantidadNoGestionadaUnidades(null);
		}
	}
	
	public void validateIdentificacionDesechos(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		List<DesechoPeligroso> pendientes = getDesechosPendientes();
		if(pendientes.size() > 0){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe completar la información de todos los desechos peligrosos del RGD.", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public Boolean validateIdentificacionDesechos() {
		List<DesechoPeligroso> pendientes = getDesechosPendientes();
		if(pendientes.size() > 0){
			return false;
		}
		
		return true;
	}
	
	public void guardarIdentificacionDesechos() {
		
		for (IdentificacionDesecho identificacionDesecho : listaIdentificacionDesechos) {
			identificacionDesecho.setIdGeneradorRetce(generadorDesechosRetce.getId());
		}
		
		identificacionDesechosFacade.guardarIdentificacion(listaIdentificacionDesechos);
		
		declaracionGeneradorRetceBean.eliminarDatosAsociados();
		
		if(listaIdentificacionDesechosOriginales.size() > 0)
			identificacionDesechosFacade.guardarIdentificacion(listaIdentificacionDesechosOriginales);
		
		listaIdentificacionDesechosOriginales = new ArrayList<>();
	}
	
	public void limpiarInfoDesecho() {
		identificacionDesecho = new IdentificacionDesecho();
    }
	
	public void verificarCambios() {
		if(identificacionDesechoOriginal != null && identificacionDesecho != null && identificacionDesecho.getId() != null){
			if(!identificacionDesecho.equalsObject(identificacionDesechoOriginal)) {
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
					guardarHistorial();
				}
				
				declaracionGeneradorRetceBean.validarDatosRelacionados(1, identificacionDesecho.getDesechoPeligroso().getId());
			}
		}
	}
	
	public void guardarHistorial() {
		Boolean tieneHistorial = false;
		if(listaIdentificacionDesechosHistoriales != null && listaIdentificacionDesechosHistoriales.size() > 0){
			for (IdentificacionDesecho desecho : listaIdentificacionDesechosHistoriales) {
				if(desecho.getIdRegistroOriginal().equals(identificacionDesecho.getId())) {
					tieneHistorial = true;
					break;
				}
			}
		}
		
		if(!tieneHistorial && listaIdentificacionDesechosOriginales.size() > 0){
			for (IdentificacionDesecho desecho : listaIdentificacionDesechosOriginales) {
				if(desecho.getIdRegistroOriginal().equals(identificacionDesecho.getId())) {
					tieneHistorial = true;
					break;
				}
			}
		}
		
		if(!tieneHistorial) {
			identificacionDesechoOriginal.setId(null);
			identificacionDesechoOriginal.setHistorial(true);
			identificacionDesechoOriginal.setIdRegistroOriginal(identificacionDesecho.getId());
			identificacionDesechoOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			listaIdentificacionDesechosOriginales.add(identificacionDesechoOriginal);
		}
	}
}
