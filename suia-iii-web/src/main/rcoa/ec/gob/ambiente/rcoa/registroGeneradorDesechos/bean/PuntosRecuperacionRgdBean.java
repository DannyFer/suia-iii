package ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.clases.CoordinatesWrapper;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers.InformacionRegistroGeneradorAAAController;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers.InformacionRegistroGeneradorController;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.FormaPuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoGeneracionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class PuntosRecuperacionRgdBean extends RegistroGeneradorBaseRcoaBean {
	
	private static final long serialVersionUID = -5624189408694387300L;

    @Setter
    private List<PuntoRecuperacionRgdRcoa> puntosRecuperacion;

    private PuntoRecuperacionRgdRcoa puntoRecuperacion;
	
    @Getter
    @Setter
    private List<PuntoGeneracionRgdRcoa> listaOrigenGeneracion;
    
    @Getter
    @Setter
    private UbicacionesGeografica parroquia;
    
    @Getter
    @Setter
    private List<String> listaCoordenadas = new ArrayList<String>();
    
    @Setter
    @Getter
    private List<PuntoRecuperacionRgdRcoa> puntosRecuperacionEliminar = new ArrayList<PuntoRecuperacionRgdRcoa>();

    public void aceptar(String tipoRGD) {
    	puntosRecuperacion = new ArrayList<PuntoRecuperacionRgdRcoa>();
    	puntoRecuperacion.setUbicacionesGeografica(parroquia);
    	puntoRecuperacion.setListaUbicacion(new ArrayList<UbicacionesGeografica>());
    	puntoRecuperacion.getListaUbicacion().add(parroquia);
//        puntoRecuperacion.setUbicacionesGeografica(JsfUtil.getBean(AdicionarUbicacionesRcoaBean.class).getUbicacionSeleccionada());    	
    	
        puntoRecuperacion.setFormasPuntoRecuperacionRgdRcoa(getFormasPuntoRecuperacion());
        
        String coordenadasIngresadas = JsfUtil.getBean(CargarCoordenadasRcoaBean.class).getCoordenadasPunto();
        if( parroquia == null){
			JsfUtil.addMessageError("Debe ingresar las coordenadas del punto de generación");
        	return;
        }
        puntoRecuperacion.setCoordenadasIngresadas(coordenadasIngresadas);

        JsfUtil.getBean(AdicionarUbicacionesRcoaBean.class).setUbicacionesSeleccionadas(new ArrayList<UbicacionesGeografica>());
        
        if (!puntosRecuperacion.contains(puntoRecuperacion)){
            puntosRecuperacion.add(puntoRecuperacion);
            buscarCoordenadas();
        }
        /**
         * autor: vear
         * fecha 24/02/2016
         * objetivo: aumentado para persistir el paso 3 del wizard
         */
        try {
        	if(tipoRGD.equals(Constantes.TIPO_RGD_AAA)){
        		JsfUtil.getBean(InformacionRegistroGeneradorAAAController.class).guardarPaso2(puntoRecuperacion);
        		JsfUtil.getBean(CargarCoordenadasRcoaBean.class).guardarCoorGeografica(JsfUtil.getBean(InformacionRegistroGeneradorAAAController.class).getRegistroGeneradorDesechos());
        	}else{
			JsfUtil.getBean(InformacionRegistroGeneradorController.class).guardarPaso2(puntoRecuperacion);
			JsfUtil.getBean(CargarCoordenadasRcoaBean.class).guardarCoorGeografica(JsfUtil.getBean(InformacionRegistroGeneradorController.class).getRegistroGeneradorDesechos());
        	}
            if(tipoRGD.equals(Constantes.TIPO_RGD_AAA)){
            	JsfUtil.getBean(InformacionRegistroGeneradorAAAController.class).cargarPuntosRecuepracion();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cancelar();
        JsfUtil.addCallbackParam("addPunto");

    }

    public void cancelar() {
        puntoRecuperacion = null;
        JsfUtil.getBean(AdicionarUbicacionesRcoaBean.class).resetSelections();
        JsfUtil.getBean(CargarCoordenadasRcoaBean.class).reset();
        JsfUtil.getBean(InformacionRegistroGeneradorAAAController.class).setGuardado(false);
		RequestContext.getCurrentInstance().update("btnSiguiente");
    }
    
	
    public void editar(PuntoRecuperacionRgdRcoa puntoRecuperacion) {
        this.puntoRecuperacion = puntoRecuperacion;
        this.setEditar(true);
        JsfUtil.getBean(AdicionarUbicacionesRcoaBean.class).setUbicacionSeleccionada(
                puntoRecuperacion.getUbicacionesGeografica(), 3);
        setFormasPuntoRecuperacion(puntoRecuperacion.getFormasPuntoRecuperacionRgdRcoa());
        
        JsfUtil.getBean(AdicionarUbicacionesRcoaBean.class).getUbicacionesSeleccionadas().add(puntoRecuperacion.getUbicacionesGeografica());
        setParroquia(puntoRecuperacion.getUbicacionesGeografica());
       
        JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).setModificacionesEliminaciones(true);
        buscarCoordenadas();
        JsfUtil.getBean(InformacionRegistroGeneradorAAAController.class).setGuardado(false);
		RequestContext.getCurrentInstance().update("btnSiguiente");
    }

    public void eliminar(PuntoRecuperacionRgdRcoa puntoRecuperacion) {
        puntosRecuperacion.remove(puntoRecuperacion);
        puntosRecuperacionEliminar.add(puntoRecuperacion);
        buscarCoordenadas();
//        JsfUtil.getBean(JustificacionProponenteBean.class).setSeEliminoPuntoGeneracionODesecho(true);
        JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).setModificacionesEliminaciones(true);
        /**
         * autor: vear
         * fecha: 03/03/2016
         * objetivo: eliminar de la lista y de la bdd tambien
         */
//        JsfUtil.getBean(InformacionRegistroGeneradorController.class).eliminarPuntoRecuperacion(puntoRecuperacion);
    }

    public void validateData(FacesContext context, UIComponent validate, Object value) {       
        
        List<FacesMessage> errorMessages = new ArrayList<>();
        if (JsfUtil.getBean(CargarCoordenadasRcoaBean.class).getCoordinatesWrappers().isEmpty()) {
            errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se han definido las coordenadas de este punto de recuperación/generación.", null));
        }
        if (!errorMessages.isEmpty())
            throw new ValidatorException(errorMessages);
    }

    public void validateDataAAA(FacesContext context, UIComponent validate, Object value) {
        List<FacesMessage> errorMessages = new ArrayList<>();
        if (JsfUtil.getBean(CargarCoordenadasRcoaBean.class).getCoordinatesWrappers().isEmpty()) {
            errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se han definido las coordenadas de este punto de recuperación/generación.", null));
        }
        if(puntoRecuperacion.getPuntoGeneracion() == null){
            errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se han definido el área de generación del Residuo o Desecho.", null));
        }
        if (!errorMessages.isEmpty())
            throw new ValidatorException(errorMessages);
    }

    public void validatePuntosRecuperacion(FacesContext context, UIComponent validate, Object value) {
        if (puntosRecuperacion.isEmpty())
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Debe adicionar, al menos, un punto de recuperación/generación.", null));
    }

    public List<PuntoRecuperacionRgdRcoa> getPuntosRecuperacion() {
        return puntosRecuperacion == null ? puntosRecuperacion = new ArrayList<>() : puntosRecuperacion;
    }

    public PuntoRecuperacionRgdRcoa getPuntoRecuperacion() {
        return puntoRecuperacion == null ? puntoRecuperacion = new PuntoRecuperacionRgdRcoa() : puntoRecuperacion;
    }

    private List<FormaPuntoRecuperacionRgdRcoa> getFormasPuntoRecuperacion() {
        List<FormaPuntoRecuperacionRgdRcoa> result = new ArrayList<FormaPuntoRecuperacionRgdRcoa>();
        Iterator<CoordinatesWrapper> coords = JsfUtil.getBean(CargarCoordenadasRcoaBean.class).getCoordinatesWrappers()
                .iterator();
                
        while (coords.hasNext()) {
            CoordinatesWrapper coordinatesWrapper = coords.next();
            FormaPuntoRecuperacionRgdRcoa formaPuntoRecuperacion = new FormaPuntoRecuperacionRgdRcoa();
            formaPuntoRecuperacion.setTipoForma(coordinatesWrapper.getTipoForma());
            formaPuntoRecuperacion.setCoordenadas(coordinatesWrapper.getCoordenadas());
            
            result.add(formaPuntoRecuperacion);
        }
        return result;
    }

    private void setFormasPuntoRecuperacion(List<FormaPuntoRecuperacionRgdRcoa> formas) {
        List<CoordinatesWrapper> coords = new ArrayList<>();
        for (FormaPuntoRecuperacionRgdRcoa formaPuntoRecuperacion : formas) {
            CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper();
            coordinatesWrapper.setTipoForma(formaPuntoRecuperacion.getTipoForma());
            coordinatesWrapper.setCoordenadas(formaPuntoRecuperacion.getCoordenadas());
            coords.add(coordinatesWrapper);
        }
        JsfUtil.getBean(CargarCoordenadasRcoaBean.class).setCoordinatesWrappers(coords);
    }

    public List<Integer> getIdCantonesPuntosRecuperacion() {
        List<Integer> idsCantones = new ArrayList<Integer>();
        for (PuntoRecuperacionRgdRcoa puntoRecuperacion : getPuntosRecuperacion()) {
            idsCantones.add(puntoRecuperacion.getUbicacionesGeografica().getUbicacionesGeografica().getId());
        }
        return idsCantones;
    }
    
    private void buscarCoordenadas(){
    	listaCoordenadas = new ArrayList<String>();
    	for(PuntoRecuperacionRgdRcoa punto : puntosRecuperacion){
    		listaCoordenadas.add(punto.getCoordenadasIngresadas());
    	}
    }

    public void actualizarGeneracion(){
    	if(puntoRecuperacion != null && puntoRecuperacion.getPuntoGeneracion() != null){
    		puntoRecuperacion.setNombresGeneracion(" "+puntoRecuperacion.getPuntoGeneracion().getClave() + " - "+ puntoRecuperacion.getPuntoGeneracion().getNombre());
    		if(puntoRecuperacion.getPuntoGeneracion().getClave().equals("OT"))
    			puntoRecuperacion.setNombresGeneracion(puntoRecuperacion.getNombresGeneracion() + " - "+ puntoRecuperacion.getGeneracionOtros());
    	}
    }

    public void limpiarOtraGeneracion(){
   			puntoRecuperacion.setGeneracionOtros(null);
    }
}
