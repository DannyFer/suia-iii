package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.prevencion.categoriaii.mineria.bean.CaracterisiticasAreaInfluenciaMineriaBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoFisicoFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.Region;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.CaracteristicasAreaInfluenciaMineriaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class CaracterisiticasAreaInfluenciaMineriaController implements Serializable {

    private static final long serialVersionUID = -6173994612215704307L;
    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    @EJB
    private CatalogoFisicoFacade catalogoFisicoFacade;
    @EJB
    private CaracteristicasAreaInfluenciaMineriaFacade caracteristicasAreaInfluenciaMineriaFacade;
    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;

    private static final int ALTURA = 4000;
    private static final int ALTURA1 = 4500;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(CaracteristicasGeneralesController.class);

    @Getter
    @Setter
    private CaracterisiticasAreaInfluenciaMineriaBean caracterisiticasAreaInfluenciaMineriaBean;

    @PostConstruct
    private void cargarDatos() {
        setCaracterisiticasAreaInfluenciaMineriaBean(new CaracterisiticasAreaInfluenciaMineriaBean());
        getCaracterisiticasAreaInfluenciaMineriaBean().iniciarDatos();
        iniciarDatosExistentes();
        cargarRegiones();
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaUbicacionGeograficaProyecto(ubicacionGeograficaFacade.listarPorProyecto(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental()));
        getCaracterisiticasAreaInfluenciaMineriaBean().procesarRegiones();
        validaRangosAltitud();
        validaRangosClima();
        cargarOcupacionAreaInfluencia();
        cargarPendienteSuelo();
        cargarTipoSuelo();
        cargarCalidadSuelo();
        cargarPermiabilidadSuelo();
        cargarCondicionesDrenaje();
        cargarRecursosHidricos();
        cargarNivelFreatico();
        cargarPrecipitaciones();
        cargarCaracteristicasAgua();
        cargarCaracteristicasAire();
        cargarRecirculacionAire();
        cargarRuido();
        
        //Cris F: metodo para mostrar el historial
        mostrarHistorial();
    }

    private void iniciarDatosExistentes() {
        FichaAmbientalMineria f = (FichaAmbientalMineria) JsfUtil.devolverObjetoSession(Constantes.SESSION_FICHA_AMBIENTAL_MINERA_OBJECT);
        getCaracterisiticasAreaInfluenciaMineriaBean().setFichaAmbientalMineria(fichaAmbientalMineriaFacade.obtenerPorId(f.getId()));
        if (getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getIdRuido() != null) {
            getCaracterisiticasAreaInfluenciaMineriaBean().setIdRuido(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getIdRuido().toString());
            getCaracterisiticasAreaInfluenciaMineriaBean().setIdRecirculacionAire(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getIdRecirculacionAire().toString());
            getCaracterisiticasAreaInfluenciaMineriaBean().setIdCaracteristicasAire(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getIdCaracteristicasAire().toString());
            getCaracterisiticasAreaInfluenciaMineriaBean().setIdCaracteristicasAgua(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getIdCaracteristicasAgua().toString());
            getCaracterisiticasAreaInfluenciaMineriaBean().setIdPrecipitaciones(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getIdPrecipitacionesAgua().toString());
            getCaracterisiticasAreaInfluenciaMineriaBean().setIdNivelFreatico(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getIdNivelFreatico().toString());
            getCaracterisiticasAreaInfluenciaMineriaBean().setOpcionesRecursosHidricos(JsfUtil.devuelveVector(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getRecursosHidricos()));
            getCaracterisiticasAreaInfluenciaMineriaBean().setIdCondicionesDrenaje(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getIdCondicionesDrenaje().toString());
            getCaracterisiticasAreaInfluenciaMineriaBean().setIdPermeabilidadSuelo(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getIdPermeabilidadSuelo().toString());
            getCaracterisiticasAreaInfluenciaMineriaBean().setOpcionesCalidadSuelo(JsfUtil.devuelveVector(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getCalidadSuelo()));
            getCaracterisiticasAreaInfluenciaMineriaBean().setOpcionesTipoSuelo(JsfUtil.devuelveVector(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getTipoSuelo()));
            getCaracterisiticasAreaInfluenciaMineriaBean().setOpcionesPendienteSuelo(JsfUtil.devuelveVector(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getPendienteSuelo()));
            getCaracterisiticasAreaInfluenciaMineriaBean().setOpcionesOcupacionAreaInfluencia(JsfUtil.devuelveVector(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getAreaInfluencia()));
            if (getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getIdClima() != null) {
                getCaracterisiticasAreaInfluenciaMineriaBean().setIdClima(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getIdClima().toString());
            }
        }
    }

    private void cargarRegiones() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaRegiones(new ArrayList<SelectItem>());
        List<Region> lista = ubicacionGeograficaFacade.listaRegionesEcuador();
        for (Region r : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaRegiones().add(new SelectItem(r.getId().toString(), r.getNombre(), r.getNombre()));
        }
    }

    private void validaRangosAltitud() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaAltitud(catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.ALTITUD));
        int altura = getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getAltitud();
        getCaracterisiticasAreaInfluenciaMineriaBean().setCatalogoAltura(validarRangosAltitud(altura));
    }

    private void validaRangosClima() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaClima(catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.CLIMA1));
        getCaracterisiticasAreaInfluenciaMineriaBean().setApareceComboClima(getCaracterisiticasAreaInfluenciaMineriaBean().procesarClima());
        if (getCaracterisiticasAreaInfluenciaMineriaBean().isApareceComboClima()) {
            if (getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getIdClima() == null) {
                getCaracterisiticasAreaInfluenciaMineriaBean().setCatalogoClima(null);
                getCaracterisiticasAreaInfluenciaMineriaBean().setIdClima(null);
            }
        } else {
            int altura = getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getAltitud();
            getCaracterisiticasAreaInfluenciaMineriaBean().setCatalogoClima(validarRangosClima(altura));

        }
    }

    private CatalogoGeneralFisico validarRangosAltitud(int altura) {
        CatalogoGeneralFisico cat = null;
        int tamanioLista = getCaracterisiticasAreaInfluenciaMineriaBean().getListaAltitud().size();
        if (altura > ALTURA) {
            cat = getCaracterisiticasAreaInfluenciaMineriaBean().getListaAltitud().get(tamanioLista - 1);
        } else {
            for (CatalogoGeneralFisico c : getCaracterisiticasAreaInfluenciaMineriaBean().getListaAltitud()) {
                if (altura >= c.getValorDesde().intValue() && altura <= c.getValorHasta().intValue()) {
                    cat = c;
                    break;
                }
            }
        }
        return cat;
    }

    private CatalogoGeneralFisico validarRangosClima(int altura) {
        CatalogoGeneralFisico cat = null;
        int tamanioLista = getCaracterisiticasAreaInfluenciaMineriaBean().getListaClima().size();
        if (altura > ALTURA1) {
            cat = getCaracterisiticasAreaInfluenciaMineriaBean().getListaAltitud().get(tamanioLista - 1);
        } else {
            for (CatalogoGeneralFisico c : getCaracterisiticasAreaInfluenciaMineriaBean().getListaClima()) {
                if (altura >= c.getValorDesde().intValue() && altura <= c.getValorHasta().intValue()) {
                    cat = c;
                    break;
                }
            }
        }
        return cat;
    }

    private void cargarOcupacionAreaInfluencia() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaOcupacionAreaInfluenciaCombo(new ArrayList<SelectItem>());
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.OCUPACION_AREA_INFLUENCIA);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaOcupacionAreaInfluenciaCombo().add(new SelectItem(c.getId().toString(), c.getDescripcion()));
        }
    }

    private void cargarPendienteSuelo() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaPendienteSueloCombo(new ArrayList<SelectItem>());
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.PENDIENTE_SUELO1);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaPendienteSueloCombo().add(new SelectItem(c.getId(), c.getDescripcion()));
        }
    }

    private void cargarTipoSuelo() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaTipoSueloCombo(new ArrayList<SelectItem>());
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.TIPO_SUELO);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaTipoSueloCombo().add(new SelectItem(c.getId(), c.getDescripcion()));
        }
    }

    private void cargarCalidadSuelo() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaCalidadSueloCombo(new ArrayList<SelectItem>());
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.CALIDAD_SUELO);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaCalidadSueloCombo().add(new SelectItem(c.getId().toString(), c.getDescripcion()));
        }
    }

    private void cargarPermiabilidadSuelo() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaPermiabilidadSueloCombo(new ArrayList<SelectItem>());
        getCaracterisiticasAreaInfluenciaMineriaBean().getListaPermiabilidadSueloCombo().add(CaracterisiticasAreaInfluenciaMineriaBean.SELECCIONE);
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.PERMIABILIDAD_SUELO);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaPermiabilidadSueloCombo().add(new SelectItem(c.getId(), c.getDescripcion()));
        }
    }

    private void cargarCondicionesDrenaje() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaCondicionesDrenajeCombo(new ArrayList<SelectItem>());
        getCaracterisiticasAreaInfluenciaMineriaBean().getListaCondicionesDrenajeCombo().add(CaracterisiticasAreaInfluenciaMineriaBean.SELECCIONE);
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.CONDICIONES_DRENAJE);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaCondicionesDrenajeCombo().add(new SelectItem(c.getId(), c.getDescripcion()));
        }
    }

    private void cargarRecursosHidricos() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaRecursosHidricosCombo(new ArrayList<SelectItem>());
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.RECURSOS_HIDRICOS);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaRecursosHidricosCombo().add(new SelectItem(c.getId(), c.getDescripcion()));
        }
    }

    private void cargarNivelFreatico() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaNivelFreaticoCombo(new ArrayList<SelectItem>());
        getCaracterisiticasAreaInfluenciaMineriaBean().getListaNivelFreaticoCombo().add(CaracterisiticasAreaInfluenciaMineriaBean.SELECCIONE);
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.NIVEL_FREATICO);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaNivelFreaticoCombo().add(new SelectItem(c.getId(), c.getDescripcion()));
        }
    }

    private void cargarPrecipitaciones() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaPrecipitacionesCombo(new ArrayList<SelectItem>());
        getCaracterisiticasAreaInfluenciaMineriaBean().getListaPrecipitacionesCombo().add(CaracterisiticasAreaInfluenciaMineriaBean.SELECCIONE);
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.PRECIPITANES);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaPrecipitacionesCombo().add(new SelectItem(c.getId(), c.getDescripcion()));
        }
    }

    private void cargarCaracteristicasAgua() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaCaracteristicasAguaCombo(new ArrayList<SelectItem>());
        getCaracterisiticasAreaInfluenciaMineriaBean().getListaCaracteristicasAguaCombo().add(CaracterisiticasAreaInfluenciaMineriaBean.SELECCIONE);
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.CARACTEREISTICAS_AGUA);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaCaracteristicasAguaCombo().add(new SelectItem(c.getId(), c.getDescripcion()));
        }
    }

    private void cargarCaracteristicasAire() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaCaracteristicasAireCombo(new ArrayList<SelectItem>());
        getCaracterisiticasAreaInfluenciaMineriaBean().getListaCaracteristicasAireCombo().add(CaracterisiticasAreaInfluenciaMineriaBean.SELECCIONE);
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.CARACTEREISTICAS_AIRE);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaCaracteristicasAireCombo().add(new SelectItem(c.getId(), c.getDescripcion()));
        }
    }

    private void cargarRecirculacionAire() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaRecirculacionAireCombo(new ArrayList<SelectItem>());
        getCaracterisiticasAreaInfluenciaMineriaBean().getListaRecirculacionAireCombo().add(CaracterisiticasAreaInfluenciaMineriaBean.SELECCIONE);
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.RECIRCULACION_AIRE);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaRecirculacionAireCombo().add(new SelectItem(c.getId(), c.getDescripcion()));
        }
    }

    private void cargarRuido() {
        getCaracterisiticasAreaInfluenciaMineriaBean().setListaRuidoCombo(new ArrayList<SelectItem>());
        getCaracterisiticasAreaInfluenciaMineriaBean().getListaRuidoCombo().add(CaracterisiticasAreaInfluenciaMineriaBean.SELECCIONE);
        List<CatalogoGeneralFisico> lista = catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.RUIDO);
        for (CatalogoGeneralFisico c : lista) {
            getCaracterisiticasAreaInfluenciaMineriaBean().getListaRuidoCombo().add(new SelectItem(c.getId(), c.getDescripcion()));
        }
    }

    public void guardar() {
        try {
            if (getCaracterisiticasAreaInfluenciaMineriaBean().isApareceComboClima()) {
                getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setClima(new CatalogoGeneralFisico(new Integer(getCaracterisiticasAreaInfluenciaMineriaBean().getIdClima())));
            }
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setAreaInfluencia(JsfUtil.transformaVector(getCaracterisiticasAreaInfluenciaMineriaBean().getOpcionesOcupacionAreaInfluencia()));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setPendienteSuelo(JsfUtil.transformaVector(getCaracterisiticasAreaInfluenciaMineriaBean().getOpcionesPendienteSuelo()));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setTipoSuelo(JsfUtil.transformaVector(getCaracterisiticasAreaInfluenciaMineriaBean().getOpcionesTipoSuelo()));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setCalidadSuelo(JsfUtil.transformaVector(getCaracterisiticasAreaInfluenciaMineriaBean().getOpcionesCalidadSuelo()));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setPermeabilidadSuelo(new CatalogoGeneralFisico(new Integer(getCaracterisiticasAreaInfluenciaMineriaBean().getIdPermeabilidadSuelo())));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setCondicionesDrenaje(new CatalogoGeneralFisico(new Integer(getCaracterisiticasAreaInfluenciaMineriaBean().getIdCondicionesDrenaje())));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setRecursosHidricos(JsfUtil.transformaVector(getCaracterisiticasAreaInfluenciaMineriaBean().getOpcionesRecursosHidricos()));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setNivelFreatico(new CatalogoGeneralFisico(new Integer(getCaracterisiticasAreaInfluenciaMineriaBean().getIdNivelFreatico())));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setPrecipitacionesAgua(new CatalogoGeneralFisico(new Integer(getCaracterisiticasAreaInfluenciaMineriaBean().getIdPrecipitaciones())));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setCaracteristicasAgua(new CatalogoGeneralFisico(new Integer(getCaracterisiticasAreaInfluenciaMineriaBean().getIdCaracteristicasAgua())));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setCaracteristicasAire(new CatalogoGeneralFisico(new Integer(getCaracterisiticasAreaInfluenciaMineriaBean().getIdCaracteristicasAire())));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setRecirculacionAire(new CatalogoGeneralFisico(new Integer(getCaracterisiticasAreaInfluenciaMineriaBean().getIdRecirculacionAire())));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setRuido(new CatalogoGeneralFisico(new Integer(getCaracterisiticasAreaInfluenciaMineriaBean().getIdRuido())));
            getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setValidarCaracteristicasAreaInfluencia(true);
            validarOtros();
            //caracteristicasAreaInfluenciaMineriaFacade.guardar(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria());
            caracteristicasAreaInfluenciaMineriaFacade.guardarHistorial(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria());
            setCaracterisiticasAreaInfluenciaMineriaBean(null);
            cargarDatos();
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
        } catch (ServiceException e) {
            LOG.error("error grabar", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
        } catch (RuntimeException e) {
            LOG.error("error runtime", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
        }
    }

    public void cancelar() {

    }
    
    public boolean muestraOpcionOtro(String[] opcionesSeleccionadas, List<SelectItem> lista){
		if(opcionesSeleccionadas != null && !lista.isEmpty() && lista != null){
			Integer num = lista.size();
			for (String string : opcionesSeleccionadas) {
				if (string.equals(lista.get(num-1).getValue())) {
					return true;
				}
			}
			return false;
		}else {
			return false;
		}
	}
    
    public void validarOtros(){
		if (!muestraOpcionOtro(getCaracterisiticasAreaInfluenciaMineriaBean().getOpcionesOcupacionAreaInfluencia(), getCaracterisiticasAreaInfluenciaMineriaBean().getListaOcupacionAreaInfluenciaCombo())) {
			getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setOtrosAreaInfluencia(null);
		}
		if (!muestraOpcionOtro(getCaracterisiticasAreaInfluenciaMineriaBean().getOpcionesCalidadSuelo(), getCaracterisiticasAreaInfluenciaMineriaBean().getListaCalidadSueloCombo())) {
			getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().setOtrosCalidadSuelo(null);
		}
    }
    
    //Cris F. aumento para historial
    @Getter
    @Setter
    private List<FichaAmbientalMineria> listaClimaHistorial, listaGeologiaGeomorfologiaSuelosHistorial,  listaHidrologiaHistorial, listaAireHistorial;
    

    private void mostrarHistorial(){
    	try {
    		
    		List<FichaAmbientalMineria> listaFichaAmbientalMineraHistorico = 
					fichaAmbientalMineriaFacade.buscarFichaAmbientalMineraHistorial(getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getId());
			
    		listaClimaHistorial = new ArrayList<FichaAmbientalMineria>(); 
    		listaGeologiaGeomorfologiaSuelosHistorial = new ArrayList<FichaAmbientalMineria>();  
    		listaHidrologiaHistorial = new ArrayList<FichaAmbientalMineria>();
    		listaAireHistorial = new ArrayList<FichaAmbientalMineria>();
    		
    		if(listaFichaAmbientalMineraHistorico != null && !listaFichaAmbientalMineraHistorico.isEmpty()){
    			    			
    			FichaAmbientalMineria fichaInicial = null;
    			for(FichaAmbientalMineria fichaBdd : listaFichaAmbientalMineraHistorico){
    				
    				if(fichaInicial == null)
						fichaInicial = getCaracterisiticasAreaInfluenciaMineriaBean().getFichaAmbientalMineria();
    				
    				//Clima    				
    				recuperarClima(fichaBdd, fichaInicial);    				
    				//Geología Geomorfología y Suelos
    				recuperarGeologia(fichaBdd, fichaInicial);    				
    				//Hidrologia 
    				recuperarHidrologia(fichaBdd, fichaInicial);    				    				    				
    				//Aire 
    				recuperarAire(fichaBdd, fichaInicial);    				
    				
    				fichaInicial = fichaBdd;
    			}
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void recuperarClima(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial){
    	try {
    		if(fichaBdd.getClima() != null && !fichaInicial.getClima().equals(fichaBdd.getClima())){
				FichaAmbientalMineria historial = new FichaAmbientalMineria();
				historial.setClima(fichaBdd.getClima());
				historial.setFechaHistorico(fichaBdd.getFechaHistorico());
				listaClimaHistorial.add(0, historial);
			}    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void recuperarGeologia(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial){
    	try {
    		//Area de influencia
			FichaAmbientalMineria historialGeologia = new FichaAmbientalMineria();
			if(fichaBdd.getAreaInfluencia() != null && !fichaInicial.getAreaInfluencia().equals(fichaBdd.getAreaInfluencia())){
				String[] lista = JsfUtil.devuelveVector(fichaBdd.getAreaInfluencia());
				String areaInfluencia = "";
				for (String elemento : lista) {
					for (SelectItem item : caracterisiticasAreaInfluenciaMineriaBean.getListaOcupacionAreaInfluenciaCombo()) {
						if (Integer.valueOf(item.getValue().toString()).equals(Integer.valueOf(elemento))) {
							areaInfluencia += item.getLabel() + "\n";
							break;
						}
					}
				}
				historialGeologia.setAreaInfluencia(fichaBdd.getAreaInfluencia());
				historialGeologia.setAreaInfluenciaMostrar(areaInfluencia);
				historialGeologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//area de influencia otros
			if(fichaBdd.getOtrosAreaInfluencia() != null && fichaInicial.getOtrosAreaInfluencia() != null && 
					!fichaBdd.getOtrosAreaInfluencia().equals(fichaInicial.getOtrosAreaInfluencia())){
					
				historialGeologia.setOtrosAreaInfluencia(fichaBdd.getOtrosAreaInfluencia());
				historialGeologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//pendiente de suelo 
			if(fichaBdd.getPendienteSuelo() != null && !fichaInicial.getPendienteSuelo().equals(fichaBdd.getPendienteSuelo())){
				String[] lista = JsfUtil.devuelveVector(fichaBdd.getPendienteSuelo());
				String pendienteSuelo = "";
				for(String elemento : lista){
					for(SelectItem item: caracterisiticasAreaInfluenciaMineriaBean.getListaPendienteSueloCombo()){
						if(Integer.valueOf(item.getValue().toString()).equals(Integer.valueOf(elemento))){
							pendienteSuelo += item.getLabel() + "\n";
							break;
						}
					}
				}  
				historialGeologia.setPendienteSuelo(fichaBdd.getPendienteSuelo());
				historialGeologia.setPendienteSueloMostrar(pendienteSuelo);
				historialGeologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}	
			
			//tipo de suelo
			if(fichaBdd.getTipoSuelo() != null && !fichaInicial.getTipoSuelo().equals(fichaBdd.getTipoSuelo())){
				String[] lista = JsfUtil.devuelveVector(fichaBdd.getTipoSuelo());
				String tipoSuelo = "";
				for (String elemento : lista) {
					for (SelectItem item : caracterisiticasAreaInfluenciaMineriaBean.getListaTipoSueloCombo()) {
						if (Integer.valueOf(item.getValue().toString()).equals(Integer.valueOf(elemento))) {
							tipoSuelo += item.getLabel()+ "\n";
							break;
						}
					}
				}
				historialGeologia.setTipoSuelo(fichaBdd.getTipoSuelo());
				historialGeologia.setTipoSueloMostrar(tipoSuelo);
				historialGeologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//Calidad del suelo
			if(fichaBdd.getCalidadSuelo() != null && !fichaInicial.getCalidadSuelo().equals(fichaBdd.getCalidadSuelo())){
				String[] lista = JsfUtil.devuelveVector(fichaBdd.getCalidadSuelo());
				String calidadSuelo = "";
				for (String elemento : lista) {
					for (SelectItem item : caracterisiticasAreaInfluenciaMineriaBean.getListaCalidadSueloCombo()) {
						if (Integer.valueOf(item.getValue().toString()).equals(Integer.valueOf(elemento))) {
							calidadSuelo += item.getLabel()+ "\n";
							break;
						}
					}
				}
				historialGeologia.setCalidadSuelo(fichaBdd.getCalidadSuelo());
				historialGeologia.setCalidadSueloMostrar(calidadSuelo);
				historialGeologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//Otros calidad de suelo
			if(fichaBdd.getOtrosCalidadSuelo() != null && fichaInicial.getOtrosCalidadSuelo() != null && 
					!fichaBdd.getOtrosCalidadSuelo().equals(fichaInicial.getOtrosCalidadSuelo())){
				
				historialGeologia.setOtrosCalidadSuelo(fichaBdd.getOtrosCalidadSuelo());
				historialGeologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//Permiabilidad del suelo
			if(fichaBdd.getPermeabilidadSuelo() != null && !fichaInicial.getPermeabilidadSuelo().equals(fichaBdd.getPermeabilidadSuelo())){
				historialGeologia.setPermeabilidadSuelo(fichaBdd.getPermeabilidadSuelo());
				historialGeologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//Condiciones de drenaje
			if(fichaBdd.getCondicionesDrenaje() != null && !fichaInicial.getCondicionesDrenaje().equals(fichaBdd.getCondicionesDrenaje())){
				historialGeologia.setCondicionesDrenaje(fichaBdd.getCondicionesDrenaje());
				historialGeologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			
			//validar todos los campos para que ingresen o no en la lista.
			if((historialGeologia.getAreaInfluenciaMostrar() != null && !historialGeologia.getAreaInfluenciaMostrar().isEmpty()) || 
					(historialGeologia.getPendienteSueloMostrar() != null && !historialGeologia.getPendienteSueloMostrar().isEmpty()) || 
					(historialGeologia.getTipoSueloMostrar() != null && !historialGeologia.getTipoSueloMostrar().isEmpty()) || 
					(historialGeologia.getCalidadSueloMostrar() != null && !historialGeologia.getCalidadSueloMostrar().isEmpty()) ||
					(historialGeologia.getPermeabilidadSuelo() != null) ||
					(historialGeologia.getCondicionesDrenaje() != null) ||
					(historialGeologia.getOtrosAreaInfluencia() != null && !historialGeologia.getOtrosAreaInfluencia().isEmpty()) ||
					(historialGeologia.getOtrosCalidadSuelo() != null && !historialGeologia.getOtrosCalidadSuelo().isEmpty())){
				listaGeologiaGeomorfologiaSuelosHistorial.add(0, historialGeologia);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void recuperarHidrologia(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial){
    	try {
    		//Recursos hidricos
			FichaAmbientalMineria historialHidrologia = new FichaAmbientalMineria();
			if(fichaBdd.getRecursosHidricos() != null && !fichaInicial.getRecursosHidricos().equals(fichaBdd.getRecursosHidricos())){
				String[] lista = JsfUtil.devuelveVector(fichaBdd.getRecursosHidricos());
				String recursoHidrico = "";
				for (String elemento : lista) {
					for (SelectItem item : caracterisiticasAreaInfluenciaMineriaBean.getListaRecursosHidricosCombo()) {
						if (Integer.valueOf(item.getValue().toString()).equals(Integer.valueOf(elemento))) {
							recursoHidrico += item.getLabel()+ "\n";
							break;
						}
					}
				}
				historialHidrologia.setRecursosHidricos(fichaBdd.getRecursosHidricos());
				historialHidrologia.setRecursosHidricosMostrar(recursoHidrico);
				historialHidrologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//Nivel Freatico
			if(fichaBdd.getNivelFreatico() != null && !fichaInicial.getNivelFreatico().equals(fichaBdd.getNivelFreatico())){
				historialHidrologia.setNivelFreatico(fichaBdd.getNivelFreatico());
				historialHidrologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//Precipitaciones
			if(fichaBdd.getPrecipitacionesAgua() != null && !fichaInicial.getPrecipitacionesAgua().equals(fichaBdd.getPrecipitacionesAgua())){
				historialHidrologia.setPrecipitacionesAgua(fichaBdd.getPrecipitacionesAgua());
				historialHidrologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//Caracteristicas del agua
			if(fichaBdd.getCaracteristicasAgua() != null && !fichaInicial.getCaracteristicasAgua().equals(fichaBdd.getCaracteristicasAgua())){
				historialHidrologia.setCaracteristicasAgua(fichaBdd.getCaracteristicasAgua());
				historialHidrologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//Descripción de las principales fuentes de contaminación del agua
			if(fichaBdd.getDescripcionFuentesContaminacionAgua() != null && !fichaInicial.getDescripcionFuentesContaminacionAgua().equals(fichaBdd.getDescripcionFuentesContaminacionAgua())){
				historialHidrologia.setDescripcionFuentesContaminacionAgua(fichaBdd.getDescripcionFuentesContaminacionAgua());
				historialHidrologia.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			if((historialHidrologia.getRecursosHidricosMostrar() != null && !historialHidrologia.getRecursosHidricosMostrar().isEmpty()) ||
					historialHidrologia.getNivelFreatico() != null || historialHidrologia.getPrecipitacionesAgua() != null ||
					historialHidrologia.getCaracteristicasAgua() != null || 
					(historialHidrologia.getDescripcionFuentesContaminacionAgua() != null && !historialHidrologia.getDescripcionFuentesContaminacionAgua().isEmpty())){
				listaHidrologiaHistorial.add(0, historialHidrologia);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void recuperarAire(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial){
    	try {
    		//Características del aire
			FichaAmbientalMineria historialAire = new FichaAmbientalMineria();
			if(fichaBdd.getCaracteristicasAire() != null && !fichaInicial.getCaracteristicasAire().equals(fichaBdd.getCaracteristicasAire())){
				historialAire.setCaracteristicasAire(fichaBdd.getCaracteristicasAire());
				historialAire.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//Recirculación del aire
			if(fichaBdd.getRecirculacionAire() != null && !fichaInicial.getRecirculacionAire().equals(fichaBdd.getRecirculacionAire())){
				historialAire.setRecirculacionAire(fichaBdd.getRecirculacionAire());
				historialAire.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//Ruido
			if(fichaBdd.getRuido() != null && !fichaInicial.getRuido().equals(fichaBdd.getRuido())){
				historialAire.setRuido(fichaBdd.getRuido());
				historialAire.setFechaHistorico(fichaBdd.getFechaHistorico());
			}
			
			//Descripción de las principales fuentes de ruido
			if(fichaBdd.getDescripcionFuentesRuido() != null && !fichaInicial.getDescripcionFuentesRuido().equals(fichaBdd.getDescripcionFuentesRuido())){
				historialAire.setDescripcionFuentesRuido(fichaBdd.getDescripcionFuentesRuido());
				historialAire.setFechaHistorico(fichaBdd.getFechaHistorico());
			}			
			
			if(historialAire.getCaracteristicasAire() != null || historialAire.getRecirculacionAire() != null ||
					historialAire.getRuido() != null || 
					(historialAire.getDescripcionFuentesRuido() != null && !historialAire.getDescripcionFuentesRuido().isEmpty())){
				listaAireHistorial.add(0, historialAire);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
