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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.clases.CoordinatesWrapper;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers.InformacionRegistroGeneradorREPController;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.FormaPuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class PuntosRecuperacionRgdREPBean extends RegistroGeneradorBaseRcoaBean {
    
    private static final long serialVersionUID = -5624189408694387300L;

    @Setter
    private List<PuntoRecuperacionRgdRcoa> puntosRecuperacion;

    private PuntoRecuperacionRgdRcoa puntoRecuperacion;
    
    @Getter
    @Setter
    private UbicacionesGeografica parroquia;
    
    @Getter
    @Setter
    private List<String> listaCoordenadas = new ArrayList<String>();
    
    @Setter
    @Getter
    private List<PuntoRecuperacionRgdRcoa> puntosRecuperacionEliminar = new ArrayList<PuntoRecuperacionRgdRcoa>();

    public void aceptar() {
    	puntosRecuperacion = new ArrayList<PuntoRecuperacionRgdRcoa>();
        puntoRecuperacion.setUbicacionesGeografica(parroquia);
        boolean insercion=false;
        
        
//        puntoRecuperacion.setUbicacionesGeografica(JsfUtil.getBean(AdicionarUbicacionesRcoaBean.class).getUbicacionSeleccionada());    
        
        puntoRecuperacion.setFormasPuntoRecuperacionRgdRcoa(getFormasPuntoRecuperacion());

        String coordenadasIngresadas = JsfUtil.getBean(CargarCoordenadasREPRcoaBean.class).getCoordenadasPunto();
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
        	insercion=JsfUtil.getBean(InformacionRegistroGeneradorREPController.class).guardarPaso2(puntoRecuperacion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cancelar();
        if(insercion){
        	JsfUtil.addCallbackParam("addPunto");
        } 
        
        
    }

    public void cancelar() {
        puntoRecuperacion = null;
        JsfUtil.getBean(AdicionarUbicacionesRcoaBean.class).resetSelections();
        JsfUtil.getBean(CargarCoordenadasREPRcoaBean.class).reset();
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
        if (JsfUtil.getBean(CargarCoordenadasREPRcoaBean.class).getCoordinatesWrappers().isEmpty()) {
            errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se han definido las coordenadas de este punto de recuperación/generación.", null));
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
        Iterator<CoordinatesWrapper> coords = JsfUtil.getBean(CargarCoordenadasREPRcoaBean.class).getCoordinatesWrappers()
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
        JsfUtil.getBean(CargarCoordenadasREPRcoaBean.class).setCoordinatesWrappers(coords);
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

}
