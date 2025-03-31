/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.prevencion.categoriaii.mineria.bean.CaracteristicasGeneralesBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoFisicoFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.CatalogoTipoMaterial;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.HerramientaMinera;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.CaracteristicasGeneralesMineriaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.DescripcionActividadMineriaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class CaracteristicasGeneralesController implements Serializable {

    private static final long serialVersionUID = -5296396183799417114L;
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
    @EJB
    private CaracteristicasGeneralesMineriaFacade caracteristicasGeneralesMineriaFacade;
    @EJB
    private CatalogoFisicoFacade catalogoGeneralFacade;
    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;
    @EJB
    private DescripcionActividadMineriaFacade descripcionActividadMineriaFacade;

    @Getter
    @Setter
    private CaracteristicasGeneralesBean caracteristicasGeneralesBean;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(CaracteristicasGeneralesController.class);

    @PostConstruct
    private void cargarDatos() {
        setCaracteristicasGeneralesBean(new CaracteristicasGeneralesBean());
        getCaracteristicasGeneralesBean().iniciarDatos();
        getCaracteristicasGeneralesBean().setListaTipoMaterial(proyectoLicenciamientoAmbientalFacade.getTiposMateriales());
        getCaracteristicasGeneralesBean().setFichaAmbientalMineria(fichaAmbientalMineriaFacade.obtenerPorId(((FichaAmbientalMineria) JsfUtil.devolverObjetoSession(Constantes.SESSION_FICHA_AMBIENTAL_MINERA_OBJECT)).getId()));
        
        cargarOpcionesTipoMaterial();
        cargarVariables();
        cargarHistorialDatos();
    }

    private void cargarOpcionesTipoMaterial() {
        try {
            List<CatalogoTipoMaterial> lista = caracteristicasGeneralesMineriaFacade.listarPorTipoMaterial(getCaracteristicasGeneralesBean().getFichaAmbientalMineria().getTipoMaterial());
            getCaracteristicasGeneralesBean().setListaOpcionesTipoMaterial(new ArrayList<SelectItem>());
            getCaracteristicasGeneralesBean().setListaPredioActividadMinera(new ArrayList<SelectItem>());
            getCaracteristicasGeneralesBean().setListaEtapaActividadMinera(new ArrayList<SelectItem>());
            for (CatalogoTipoMaterial c : lista) {
                getCaracteristicasGeneralesBean().getListaOpcionesTipoMaterial().add(new SelectItem(c.getId().toString(), c.getNombre()));
            }
            List<CatalogoGeneralFisico> listaCatalogo = catalogoGeneralFacade.obtenerListaFisicoIdTipo(TipoCatalogo.PREDIO_ACTIVIDAD_MINERA);
            for (CatalogoGeneralFisico c : listaCatalogo) {
                getCaracteristicasGeneralesBean().getListaPredioActividadMinera().add(new SelectItem(c.getId(), c.getDescripcion()));
            }
            listaCatalogo = catalogoGeneralFacade.obtenerListaFisicoIdTipo(TipoCatalogo.ETAPA_ACTIVIDAD_MINERA);
            for (CatalogoGeneralFisico c : listaCatalogo) {
                getCaracteristicasGeneralesBean().getListaEtapaActividadMinera().add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    private void cargarVariables() {
        getCaracteristicasGeneralesBean().setIdEtapa(getCaracteristicasGeneralesBean().getFichaAmbientalMineria().getIdEtapa() == null ? null : getCaracteristicasGeneralesBean().getFichaAmbientalMineria().getIdEtapa().toString());
        getCaracteristicasGeneralesBean().setIdPredio(getCaracteristicasGeneralesBean().getFichaAmbientalMineria().getIdPredio() == null ? null : getCaracteristicasGeneralesBean().getFichaAmbientalMineria().getIdPredio().toString());
        getCaracteristicasGeneralesBean().setOtro(getCaracteristicasGeneralesBean().getFichaAmbientalMineria().getOtrosPredios());
        getCaracteristicasGeneralesBean().setOpcionesTipoMaterial(JsfUtil.devuelveVector(getCaracteristicasGeneralesBean().getFichaAmbientalMineria().getCatalogoTipoMaterial()));
    }

    public void validarOtro() {
        getCaracteristicasGeneralesBean().setOtro(null);
    }
    
    public void guardar(){
    	try {
    		List<HerramientaMinera> lista = descripcionActividadMineriaFacade.listarHerramientaPorFichaAmbiental(getCaracteristicasGeneralesBean().getFichaAmbientalMineria());
    		if (lista.isEmpty() || lista == null) {
        		getCaracteristicasGeneralesBean().validarFormulario();
                getCaracteristicasGeneralesBean().getFichaAmbientalMineria().setCatalogoTipoMaterial(JsfUtil.transformaVector(getCaracteristicasGeneralesBean().getOpcionesTipoMaterial()));
                getCaracteristicasGeneralesBean().getFichaAmbientalMineria().setPredio(new CatalogoGeneralFisico(new Integer(getCaracteristicasGeneralesBean().getIdPredio())));
                getCaracteristicasGeneralesBean().getFichaAmbientalMineria().setEtapa(new CatalogoGeneralFisico(new Integer(getCaracteristicasGeneralesBean().getIdEtapa())));
                getCaracteristicasGeneralesBean().getFichaAmbientalMineria().setOtrosPredios(getCaracteristicasGeneralesBean().getOtro());
                getCaracteristicasGeneralesBean().getFichaAmbientalMineria().setValidarCaracteristicasGenerales(true);
                //FichaAmbientalMineria ficha = fichaAmbientalMineriaFacade.guardarFicha(getCaracteristicasGeneralesBean().getFichaAmbientalMineria());
                //Cris F: cambio para guardar historico
                FichaAmbientalMineria ficha = fichaAmbientalMineriaFacade.guardarFichaNuevo(getCaracteristicasGeneralesBean().getFichaAmbientalMineria());
                ficha.getTipoMaterial();
                setCaracteristicasGeneralesBean(null);
                JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
                cargarDatos();                
			}else {
				RequestContext context = RequestContext.getCurrentInstance();
	            context.execute("PF('dlgInfo').show();");
			}
		} catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
            LOG.error(e, e);
        } catch (RuntimeException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
            LOG.error(e, e);
        }
    }

    public void guardarEliminarActividades() {
        try {
            getCaracteristicasGeneralesBean().validarFormulario();
            getCaracteristicasGeneralesBean().getFichaAmbientalMineria().setCatalogoTipoMaterial(JsfUtil.transformaVector(getCaracteristicasGeneralesBean().getOpcionesTipoMaterial()));
            getCaracteristicasGeneralesBean().getFichaAmbientalMineria().setPredio(new CatalogoGeneralFisico(new Integer(getCaracteristicasGeneralesBean().getIdPredio())));
            getCaracteristicasGeneralesBean().getFichaAmbientalMineria().setEtapa(new CatalogoGeneralFisico(new Integer(getCaracteristicasGeneralesBean().getIdEtapa())));
            getCaracteristicasGeneralesBean().getFichaAmbientalMineria().setOtrosPredios(getCaracteristicasGeneralesBean().getOtro());
            getCaracteristicasGeneralesBean().getFichaAmbientalMineria().setValidarCaracteristicasGenerales(true);
            getCaracteristicasGeneralesBean().getFichaAmbientalMineria().setValidarDescripcionActividad(false);
            //FichaAmbientalMineria ficha = fichaAmbientalMineriaFacade.guardarFichaCaracteristicas(getCaracteristicasGeneralesBean().getFichaAmbientalMineria());
            //Cris F: cambio para guardar historial
            FichaAmbientalMineria ficha = fichaAmbientalMineriaFacade.guardarFichaCaracteristicasNuevo(getCaracteristicasGeneralesBean().getFichaAmbientalMineria());
            ficha.getTipoMaterial();
            setCaracteristicasGeneralesBean(null);
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
            cargarDatos();
            JsfUtil.redirectTo("/prevencion/categoria2/v2/fichaMineria/caracteristicas.jsf");
        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
            LOG.error(e, e);
        } catch (RuntimeException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
            LOG.error(e, e);
        }
    }

    public void cancelar() {

    }
    
    //Cris F: metodo cargar historial
    @Getter
    private List<FichaAmbientalMineria> listaFichaAmbientalMineraHisTipoMaterial, listaFichaAmbientalMineraHisPredio, listaFichaAmbientalMineraHisActividad;
   
    private void cargarHistorialDatos(){
    	try {
    		List<FichaAmbientalMineria> listaFichaAmbientalMineraHistorico = fichaAmbientalMineriaFacade.buscarFichaAmbientalMineraHistorial(caracteristicasGeneralesBean.getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getId());
    		
    		if(listaFichaAmbientalMineraHistorico != null && !listaFichaAmbientalMineraHistorico.isEmpty()){
    			listaFichaAmbientalMineraHisTipoMaterial = new ArrayList<FichaAmbientalMineria>();
    			listaFichaAmbientalMineraHisPredio = new ArrayList<FichaAmbientalMineria>();
    			listaFichaAmbientalMineraHisActividad = new ArrayList<FichaAmbientalMineria>();
    			    			
    			FichaAmbientalMineria fichaInicial = null;
    			for(FichaAmbientalMineria fichaBdd : listaFichaAmbientalMineraHistorico){
    				
    				if(fichaInicial == null)
						fichaInicial =caracteristicasGeneralesBean.getFichaAmbientalMineria();
    				
    				if(fichaBdd.getCatalogoTipoMaterial() != null){
    					
						
    					if (!fichaBdd.getCatalogoTipoMaterial().equals(fichaInicial.getCatalogoTipoMaterial())) {
							FichaAmbientalMineria historicoCatalogo = new FichaAmbientalMineria();

							String[] lista = JsfUtil.devuelveVector(fichaBdd.getCatalogoTipoMaterial());
							String tipoMaterial = "";
							for (String elemento : lista) {
								for (SelectItem item : caracteristicasGeneralesBean.getListaOpcionesTipoMaterial()) {
									if (Integer.valueOf(item.getValue().toString()).equals(Integer.valueOf(elemento))) {
										tipoMaterial += item.getLabel()+ "\n";
										break;
									}
								}
							}
							historicoCatalogo.setCatalogoTipoMaterial(fichaBdd.getCatalogoTipoMaterial());
							historicoCatalogo.setCatalogoTipoMaterialMostrar(tipoMaterial);
							historicoCatalogo.setFechaHistorico(fichaBdd.getFechaHistorico());
							
							if(historicoCatalogo.getCatalogoTipoMaterial() != null && !historicoCatalogo.getCatalogoTipoMaterial().isEmpty())
								listaFichaAmbientalMineraHisTipoMaterial.add(0, historicoCatalogo);							
						} 
					}    				
    				
    				//tipo de predio
        			if(fichaBdd.getPredio() != null){
        				if(!fichaBdd.getPredio().equals(fichaInicial.getPredio())){        					
            				listaFichaAmbientalMineraHisPredio.add(0, fichaBdd);
            			}else if(fichaBdd.getPredio() != null && fichaBdd.getPredio().getDescripcion().equals("Otro") && 
            					fichaBdd.getOtrosPredios() != null && fichaInicial.getOtrosPredios() != null &&  
            					!fichaBdd.getOtrosPredios().equals(fichaInicial.getOtrosPredios())){
            				listaFichaAmbientalMineraHisPredio.add(0, fichaBdd);
            			}
        			}
        			
        			//etapa de actividad minera
        			if(fichaBdd.getEtapa() != null){
        				if(!fichaBdd.getEtapa().equals(fichaInicial.getEtapa() )){//            				
            				listaFichaAmbientalMineraHisActividad.add(0,fichaBdd);
            			}        			
        			}
        			
        			fichaInicial = fichaBdd;
    			}
    		}    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
