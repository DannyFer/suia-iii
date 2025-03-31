/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.prevencion.categoriaii.mineria.bean.MatrizPlanAmbientalBB;
import ec.gob.ambiente.suia.domain.ActividadFactorImpactoPma;
import ec.gob.ambiente.suia.domain.ActividadMinera;
import ec.gob.ambiente.suia.domain.FactorPma;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.MatrizAmbientalMineria;
import ec.gob.ambiente.suia.domain.MatrizFactorImpacto;
import ec.gob.ambiente.suia.dto.EntityNodoActividad;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ActividadFactorImpactoFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.ActividadMineriaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.MatrizAmbientalMineriaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class MatrizAmbientalController implements Serializable {

    private static final long serialVersionUID = -1051071583238522450L;
    @EJB
    private ActividadMineriaFacade actividadMineriaFacade;
    @EJB
    private ActividadFactorImpactoFacade actividadFactorImpactoFacade;
    @EJB
    private MatrizAmbientalMineriaFacade matrizAmbientalMineriaFacade;
    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MatrizAmbientalController.class);
    private static final String SELECCIONE = "Seleccione";

    @Getter
    @Setter
    private MatrizPlanAmbientalBB matrizPlanAmbientalBB;
    
	// MarielaG para historial
    @Getter
    @Setter
    private Boolean impactosEliminados, existenImpactos;

    @PostConstruct
    private void cargarDatos() {
        try {
            setMatrizPlanAmbientalBB(new MatrizPlanAmbientalBB());
            
			// MarielaG para historial inicializacion de objetos
			matrizPlanAmbientalBB.setListaImpactosOriginales(new ArrayList<MatrizFactorImpacto>());
			matrizPlanAmbientalBB.setListaImpactosEliminados(new ArrayList<MatrizFactorImpacto>());
			matrizPlanAmbientalBB.setListaImpactosHistoricos(new ArrayList<MatrizFactorImpacto>());
			existenImpactos = false;
            
            getMatrizPlanAmbientalBB().iniciarDatos();
            FichaAmbientalMineria f = (FichaAmbientalMineria) JsfUtil.devolverObjetoSession(Constantes.SESSION_FICHA_AMBIENTAL_MINERA_OBJECT);
            getMatrizPlanAmbientalBB().setFichaAmbientalMineria(fichaAmbientalMineriaFacade.obtenerPorId(f.getId()));
            getMatrizPlanAmbientalBB().setListaActividadMineria(actividadMineriaFacade.listarPorFichaAmbiental(getMatrizPlanAmbientalBB().getFichaAmbientalMineria()));
            List<MatrizAmbientalMineria> lista = matrizAmbientalMineriaFacade.listarPorFicha(getMatrizPlanAmbientalBB().getFichaAmbientalMineria());
            if (lista != null && !lista.isEmpty()) {
            	existenImpactos = true;
                cargarExistentes(lista);
            }
            iniciarArbol();
            
			// MarielaG para historial
			fillHistorialImpactosEliminados();
        } catch (RuntimeException e) {
            LOG.error(e, e);
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    private void iniciarArbol() {
        getMatrizPlanAmbientalBB().setRoot(new DefaultTreeNode());
        for (ActividadMinera a : getMatrizPlanAmbientalBB().getListaActividadMineria()) {
            EntityNodoActividad obj = new EntityNodoActividad();
            obj.setActividadMinera(a);
            TreeNode nodoH = new DefaultTreeNode(obj, getMatrizPlanAmbientalBB().getRoot());
            nodoH.setExpanded(true);
            if (a.getListaMatrizFactorImpacto() != null && !a.getListaMatrizFactorImpacto().isEmpty()) {
            	reasignarIndice(a);
                cargarHijos(a, nodoH, a.getListaMatrizFactorImpacto());
            } else {
                a.setListaMatrizFactorImpacto(new ArrayList<MatrizFactorImpacto>());
                reasignarIndice(a);
            }
        }
    }

    private void reasignarIndice(ActividadMinera a) {
        int i = 0;
        for (MatrizFactorImpacto m : a.getListaMatrizFactorImpacto()) {
            m.setIndice(i);
            i++;
        }
    }

    private void cargarHijos(ActividadMinera a, TreeNode nodoH, List<MatrizFactorImpacto> listaMatrizFactorImpacto) {
        for (MatrizFactorImpacto m : listaMatrizFactorImpacto) {
        	//MarielaG aumento MatrizFactorImpacto para manejo historial
            TreeNode hijo = new DefaultTreeNode(new EntityNodoActividad(a, m.getFactorPma(), m.getImpactoPma(), true, m.getIndice(), m), nodoH);
        }
    }

    private void cargarExistentes(final List<MatrizAmbientalMineria> lista) throws ServiceException {
        for (ActividadMinera a : getMatrizPlanAmbientalBB().getListaActividadMineria()) {
            for (MatrizAmbientalMineria i : lista) {
                if (a.getId().equals(i.getIdActividadMineria())) {
                	//MarielaG cambio metodo para manejo historial
                	List<MatrizFactorImpacto> listaMatrizActividad = new ArrayList<>(); 
                	List<MatrizFactorImpacto> listaMatriz = actividadFactorImpactoFacade.listarTodoMatrizMineria(i.getId());
                	for (MatrizFactorImpacto m : listaMatriz) {
                		if(m.getIdRegistroOriginal() == null){
                			listaMatrizActividad.add(m);
                			
                			MatrizFactorImpacto nuevoMatrizFactorImpacto = (MatrizFactorImpacto) SerializationUtils.clone(m);
                        	nuevoMatrizFactorImpacto.setId(m.getId());
                        	matrizPlanAmbientalBB.getListaImpactosOriginales().add(nuevoMatrizFactorImpacto);
                		}else{
                			matrizPlanAmbientalBB.getListaImpactosHistoricos().add(m);
                		}
                	}
                    a.setListaMatrizFactorImpacto(listaMatrizActividad);
                    break;
                }
            }
            
            if(a.getListaMatrizFactorImpacto() == null){
            	a.setListaMatrizFactorImpacto(new ArrayList<MatrizFactorImpacto>());
            }
        }
        
        this.setRegistrosHistorial();
    }

    private void cargarActividadesFactores() {
        try {
            getMatrizPlanAmbientalBB().setListaFactores(new ArrayList<SelectItem>());
            getMatrizPlanAmbientalBB().getListaFactores().add(new SelectItem(null, SELECCIONE));
            List<ActividadFactorImpactoPma> lista = actividadFactorImpactoFacade.obtenerPorActividadPma(getMatrizPlanAmbientalBB().getEntityNodoActividad().getActividadMinera().getActividadComercial());
            for (ActividadFactorImpactoPma a : lista) {
                getMatrizPlanAmbientalBB().getListaFactores().add(new SelectItem(a.getFactor().getId(), a.getFactor().getNombre()));
            }
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    public void cargarActividadesImpactos() {
        try {
            getMatrizPlanAmbientalBB().setListaImpactos(new ArrayList<SelectItem>());
            getMatrizPlanAmbientalBB().getListaImpactos().add(new SelectItem(null, SELECCIONE));
            if (getMatrizPlanAmbientalBB().getIdFactor() == null || getMatrizPlanAmbientalBB().getIdFactor().isEmpty()) {
                getMatrizPlanAmbientalBB().setIdImpacto(null);
            } else {
                List<ActividadFactorImpactoPma> lista = actividadFactorImpactoFacade.obtenerPorActividadFactorPma(getMatrizPlanAmbientalBB().getEntityNodoActividad().getActividadMinera().getActividadComercial(), new FactorPma(new Integer(getMatrizPlanAmbientalBB().getIdFactor())));
                for (ActividadFactorImpactoPma a : lista) {
                    getMatrizPlanAmbientalBB().getListaImpactos().add(new SelectItem(a.getImpacto().getId(), a.getImpacto().getNombre()));
                }
            }
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    public void seleccionarActividad(EntityNodoActividad entityNodoActividad) {
        entityNodoActividad.setEditar(true);
        getMatrizPlanAmbientalBB().setEntityNodoActividad(entityNodoActividad);
        if (getMatrizPlanAmbientalBB().getEntityNodoActividad().getFactor() != null) {
            getMatrizPlanAmbientalBB().setIdFactor(getMatrizPlanAmbientalBB().getEntityNodoActividad().getFactor().getId().toString());
            getMatrizPlanAmbientalBB().setIdImpacto(getMatrizPlanAmbientalBB().getEntityNodoActividad().getImpacto().getId().toString());
            cargarActividadesImpactos();
        }else{
        	getMatrizPlanAmbientalBB().setIdFactor(null);
            getMatrizPlanAmbientalBB().setIdImpacto(null);
        }
        cargarActividadesFactores();
    }

    public void guardarImpacto() {
        RequestContext context = RequestContext.getCurrentInstance();
        List<String> mensajes = new ArrayList<String>();
        if (getMatrizPlanAmbientalBB().getIdFactor() == null || getMatrizPlanAmbientalBB().getIdFactor().isEmpty()) {
            mensajes.add("El campo 'Factor' es requerido");
        }
        if (getMatrizPlanAmbientalBB().getIdImpacto() == null || getMatrizPlanAmbientalBB().getIdImpacto().isEmpty()) {
            mensajes.add("El campo 'Impacto' es requerido");
        }
        if (mensajes.isEmpty()) {
            if (validaRepetidos()) {
                if (getMatrizPlanAmbientalBB().getEntityNodoActividad().getFactor() == null) {
                    MatrizFactorImpacto m = new MatrizFactorImpacto();
                    m.setFactorPma(actividadFactorImpactoFacade.obtenerFactorPorId(new Integer(getMatrizPlanAmbientalBB().getIdFactor())));
                    m.setImpactoPma(actividadFactorImpactoFacade.obtenerImpactoPorId(new Integer(getMatrizPlanAmbientalBB().getIdImpacto())));
                    getMatrizPlanAmbientalBB().getEntityNodoActividad().getActividadMinera().getListaMatrizFactorImpacto().add(m);
                    reasignarIndice(getMatrizPlanAmbientalBB().getEntityNodoActividad().getActividadMinera());
                } else {
					// MarielaG cambio para manejo historial
                    MatrizFactorImpacto m = matrizPlanAmbientalBB.getEntityNodoActividad().getActividadMinera().getListaMatrizFactorImpacto().get(matrizPlanAmbientalBB.getEntityNodoActividad().getIndice());
                    m.setFactorPma(actividadFactorImpactoFacade.obtenerFactorPorId(new Integer(getMatrizPlanAmbientalBB().getIdFactor())));
                    m.setImpactoPma(actividadFactorImpactoFacade.obtenerImpactoPorId(new Integer(getMatrizPlanAmbientalBB().getIdImpacto())));
                    getMatrizPlanAmbientalBB().getEntityNodoActividad().getActividadMinera().getListaMatrizFactorImpacto().set(getMatrizPlanAmbientalBB().getEntityNodoActividad().getIndice(), m);
                    reasignarIndice(getMatrizPlanAmbientalBB().getEntityNodoActividad().getActividadMinera());
                }
                iniciarArbol();
                context.addCallbackParam("impactoIn", true);
                getMatrizPlanAmbientalBB().setIdFactor(null);
                getMatrizPlanAmbientalBB().setIdImpacto(null);
            } else {
                JsfUtil.addMessageError("El impacto ya esta ingresado para esta actividad");
            }
        } else {
            JsfUtil.addMessageError(mensajes);
        }
    }

    private boolean validaRepetidos() {
        Integer idImpacto = new Integer(getMatrizPlanAmbientalBB().getIdImpacto());
        boolean retorno = true;
        for (MatrizFactorImpacto m : getMatrizPlanAmbientalBB().getEntityNodoActividad().getActividadMinera().getListaMatrizFactorImpacto()) {
            if (idImpacto.equals(m.getImpactoPma().getId())) {
                retorno = false;
                break;
            }
        }
        return retorno;
    }

    public void eliminarImpacto(EntityNodoActividad entityNodoActividad) {
        getMatrizPlanAmbientalBB().setEntityNodoActividad(entityNodoActividad);
        
		// MarielaG para guardar impactos que se eliminan
		MatrizFactorImpacto impactoSeleccionado = matrizPlanAmbientalBB.getEntityNodoActividad().getActividadMinera().getListaMatrizFactorImpacto()
				.get(matrizPlanAmbientalBB.getEntityNodoActividad().getIndice());
		if (impactoSeleccionado.getId() != null)
			matrizPlanAmbientalBB.getListaImpactosEliminados().add(impactoSeleccionado);
		
        getMatrizPlanAmbientalBB().getEntityNodoActividad().getActividadMinera().getListaMatrizFactorImpacto().remove(getMatrizPlanAmbientalBB().getEntityNodoActividad().getIndice());
        reasignarIndice(getMatrizPlanAmbientalBB().getEntityNodoActividad().getActividadMinera());
        iniciarArbol();
    }

    public void guardar() {
        try {
            List<String> mensajes = new ArrayList<String>();
            List<MatrizAmbientalMineria> lista = new ArrayList<MatrizAmbientalMineria>();
            for (ActividadMinera a : getMatrizPlanAmbientalBB().getListaActividadMineria()) {
                if (a.getListaMatrizFactorImpacto() != null && !a.getListaMatrizFactorImpacto().isEmpty()) {
                    MatrizAmbientalMineria m = new MatrizAmbientalMineria();
                    m.setActividadMinera(a);
                    m.setFichaAmbientalMineria(getMatrizPlanAmbientalBB().getFichaAmbientalMineria());
                    m.setEstado(true);
                    lista.add(m);
                } else {
                    mensajes.add("Debe seleccionar el factor e impacto correspondiente a la actividad ingresada: " + a.getActividadComercial().getNombreActividad());
                }
            }
            guardarPunto(mensajes, lista);
        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO + " " + e.getMessage());
            LOG.error(e, e);
        } catch (RuntimeException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO + " " + e.getMessage());
            LOG.error(e, e);
        }
    }

    private void guardarPunto(List<String> mensajes, List<MatrizAmbientalMineria> lista) throws ServiceException {
        if (mensajes.isEmpty()) {
            getMatrizPlanAmbientalBB().getFichaAmbientalMineria().setValidarMatrizIdentificacionImpactosAmbientales(true);
            
			// MarielaG aumento metodo para guardar historial
			this.guardarHistorial();
         			
			matrizAmbientalMineriaFacade.guardar(lista, getMatrizPlanAmbientalBB().getFichaAmbientalMineria(), 
            		matrizPlanAmbientalBB.getListaImpactosEliminados());
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
            setMatrizPlanAmbientalBB(null);
            cargarDatos();
        } else {
            JsfUtil.addMessageError(mensajes);
        }
    }

    public void cancelar() {

    }
    
    //MarielaG metodos para manejo de historial
    public void guardarHistorial() {
    	List<MatrizFactorImpacto> listaImpactosHistoriales = new ArrayList<MatrizFactorImpacto>();

    	//guardar historial de los impactos modificados
    	for (ActividadMinera a : matrizPlanAmbientalBB.getListaActividadMineria()) {
            for (MatrizFactorImpacto mfi : a.getListaMatrizFactorImpacto()) {
            	if(mfi.getId() != null){
	                for(MatrizFactorImpacto mfiOriginal :matrizPlanAmbientalBB.getListaImpactosOriginales()){
	                	if(mfi.getId().equals(mfiOriginal.getId())){
	                		if(!mfi.equalsObject(mfiOriginal)){
	                			mfiOriginal.setId(null);
	                			mfiOriginal.setIdRegistroOriginal(mfi.getId());
	                			mfiOriginal.setFechaHistorico(new Date());
	                			
	                			listaImpactosHistoriales.add(mfiOriginal);
	                		}
	                		
	                		break;
	                	}
	                }
            	}else{
            		if(existenImpactos){
            			mfi.setFechaHistorico(new Date());
            			//cuando se eliminan todos los impactos existentes de la actividad, se busca el id de la cabecera en los 
            			//impactos eliminados de esa actividad para que no se genere un nuevo registro
            			if(mfi.getMatrizAmbientalMineria() == null){
            				for (MatrizFactorImpacto mfiAux : matrizPlanAmbientalBB.getListaImpactosEliminados()) {
            					if(mfiAux.getMatrizAmbientalMineria().getActividadMinera().getId().equals(a.getId())){
            						mfi.setMatrizAmbientalMineria(mfiAux.getMatrizAmbientalMineria());
            						break;
            					}
            				}
            			}
            		}
            	}
            }
        }
    	
    	//guardar historial de los impactos eliminados
		for (MatrizFactorImpacto mfiPorEliminar : matrizPlanAmbientalBB.getListaImpactosEliminados()) {
			if (mfiPorEliminar.getFechaHistorico() == null) {
				MatrizFactorImpacto mfiOriginal = (MatrizFactorImpacto) SerializationUtils.clone(mfiPorEliminar);
				mfiOriginal.setId(null);
				mfiOriginal.setIdRegistroOriginal(mfiPorEliminar.getId());
				mfiOriginal.setFechaHistorico(new Date());
				listaImpactosHistoriales.add(mfiOriginal);
			}
		}
    	
    	if(listaImpactosHistoriales.size() > 0)
    		matrizAmbientalMineriaFacade.guardarMatrizFactorImpacto(listaImpactosHistoriales);
	}
    
    public void setRegistrosHistorial() {
		for (ActividadMinera a : matrizPlanAmbientalBB.getListaActividadMineria()) {
			for (MatrizFactorImpacto m : a.getListaMatrizFactorImpacto()) {
				if (m.getFechaHistorico() != null) {
					m.setNuevoEnModificacion(true);
				}
				for (MatrizFactorImpacto mh : matrizPlanAmbientalBB.getListaImpactosHistoricos()) {
					if (m.getId().equals(mh.getIdRegistroOriginal())) {
						m.setHistorialModificaciones(true);
						break;
					}
				}
			}
		}
	}
    
    public void fillHistorialImpactos(EntityNodoActividad nodoSeleccionado) {
    	matrizPlanAmbientalBB.setHistorialImpactosSeleccionados(new ArrayList<MatrizFactorImpacto>());
		
		for (MatrizFactorImpacto impactoHistorial : matrizPlanAmbientalBB.getListaImpactosHistoricos()) {
			if(nodoSeleccionado.getFactorImpacto().getId().equals(impactoHistorial.getIdRegistroOriginal())){
				matrizPlanAmbientalBB.getHistorialImpactosSeleccionados().add(impactoHistorial);
			}
		}
	}
    
    public void fillHistorialImpactosEliminados() {
    	matrizPlanAmbientalBB.setHistorialImpactosSeleccionados(new ArrayList<MatrizFactorImpacto>());
		List<MatrizFactorImpacto> impactosTemporal = new ArrayList<MatrizFactorImpacto>();

		for (MatrizFactorImpacto impactoHistorial : matrizPlanAmbientalBB.getListaImpactosHistoricos()) {
			Boolean existeImpacto = false;
			for (MatrizFactorImpacto actual : matrizPlanAmbientalBB.getListaImpactosOriginales()){
				if(actual.getId().equals(impactoHistorial.getIdRegistroOriginal())){
					existeImpacto = true;
					break;
				}
			}
			
			if(!existeImpacto)
				impactosTemporal.add(impactoHistorial);
		}
		
		if (impactosTemporal.size() > 0) {
			impactosEliminados = true;
			matrizPlanAmbientalBB.getHistorialImpactosSeleccionados().addAll(impactosTemporal);
		}
	}
	// Fin metodos historial
    
}
