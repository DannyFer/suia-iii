/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.controllers;

import ec.gob.ambiente.prevencion.categoria2.bean.ImpactoAmbientalPmaBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.ActividadFactorImpactoPma;
import ec.gob.ambiente.suia.domain.ActividadProcesoPma;
import ec.gob.ambiente.suia.domain.FactorPma;
import ec.gob.ambiente.suia.domain.ImpactoAmbientalPma;
import ec.gob.ambiente.suia.domain.MatrizFactorImpacto;
import ec.gob.ambiente.suia.domain.MatrizFactorImpactoOtros;
import ec.gob.ambiente.suia.dto.EntityCronograma;
import ec.gob.ambiente.suia.dto.EntityDetalleImpactos;
import ec.gob.ambiente.suia.dto.EntityNodoActividad;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ActividadFactorImpactoFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author ishmael
 */
@ManagedBean
@ViewScoped
public class ImpactoAmbientalPmaController implements Serializable {

    private static final long serialVersionUID = -8439687238609354630L;

    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
    @EJB
    private ActividadFactorImpactoFacade actividadFactorImpactoFacade;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    private ImpactoAmbientalPmaBean impactoAmbientalPmaBean;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ImpactoAmbientalPmaController.class);
    private static final String OTRA_ACTIVIDAD = "Otros";
    private static final String SELECCIONE = "Seleccione";

    @Getter
    @Setter
    private Boolean impactosEliminados, otrosImpactosEliminados, existenImpactos, existenOtrosImpactos; //MarielaG para historial
    
    @PostConstruct
    public void inicio() {
        impactoAmbientalPmaBean = new ImpactoAmbientalPmaBean();
        impactoAmbientalPmaBean.setRoot(new DefaultTreeNode());
        recuperarImpactoAmbientalPma();
    }

    private void recuperarImpactoAmbientalPma() {
        try {
            impactoAmbientalPmaBean.setFicha(fichaAmbientalPmaFacade.getFichaAmbientalPorIdProyecto(proyectosBean.getProyecto().getId()));
            List<ActividadProcesoPma> listaTodos = fichaAmbientalPmaFacade.getActividadesProcesosFichaPorIdFicha(impactoAmbientalPmaBean.getFicha().getId());
            if (listaTodos != null && !listaTodos.isEmpty()) {
            	//MarielaG para historial inicializacion de objetos
            	impactoAmbientalPmaBean.setListaImpactosOriginales(new ArrayList<MatrizFactorImpacto>());
                impactoAmbientalPmaBean.setListaImpactosEliminados(new ArrayList<MatrizFactorImpacto>());
                impactoAmbientalPmaBean.setListaImpactosHistoricos(new ArrayList<MatrizFactorImpacto>());
                impactoAmbientalPmaBean.setListaOtrosImpactosOriginales(new ArrayList<MatrizFactorImpactoOtros>());
                impactoAmbientalPmaBean.setListaOtrosImpactosHistoricos(new ArrayList<MatrizFactorImpactoOtros>());
                impactoAmbientalPmaBean.setListaOtrosImpactosEliminados(new ArrayList<MatrizFactorImpactoOtros>());
                existenImpactos = false;
                existenOtrosImpactos = false;
                
                impactoAmbientalPmaBean.setListaActividades(new ArrayList<ActividadProcesoPma>());
                impactoAmbientalPmaBean.setListaActividadesOtros(new ArrayList<ActividadProcesoPma>());
                for (ActividadProcesoPma a : listaTodos) {
                    if (!OTRA_ACTIVIDAD.equals(a.getActividadComercial().getNombreActividad())) {
                        impactoAmbientalPmaBean.getListaActividades().add(a);
                    } else {
                        impactoAmbientalPmaBean.getListaActividadesOtros().add(a);
                    }
                }
                cargarActividadNodos();
                if (!impactoAmbientalPmaBean.getListaActividadesOtros().isEmpty()) {
                    cargarFactorOtros();
                    cargarImpactoOtrosExistentes();
				}

				// MarielaG para historial
				fillHistorialImpactosEliminados();
				fillHistorialOtrosImpactosEliminados();
            } else {
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('dlgInfo').show();");
            }

        } catch (ServiceException e) {
            LOG.error(e, e);
        } catch (RuntimeException ex) {
            LOG.error(ex, ex);
        }
    }

    private void cargarImpactoOtrosExistentes() throws ServiceException {
    	//MarielaG cambio metodo para manejo historial
    	impactoAmbientalPmaBean.setListaActividadOtros(new ArrayList<MatrizFactorImpactoOtros>());
    	List<MatrizFactorImpactoOtros> listaImpactosOtros = fichaAmbientalPmaFacade.listarTodoMatrizOtrosImpactos(impactoAmbientalPmaBean.getFicha().getId());
    	for(MatrizFactorImpactoOtros o : listaImpactosOtros){
    		existenOtrosImpactos = true;
    		if(o.getIdRegistroOriginal() == null){
    			impactoAmbientalPmaBean.getListaActividadOtros().add(o);
    			
    			MatrizFactorImpactoOtros nuevoImpacto = (MatrizFactorImpactoOtros) SerializationUtils.clone(o);
    			nuevoImpacto.setId(o.getId());
    			impactoAmbientalPmaBean.getListaOtrosImpactosOriginales().add(nuevoImpacto);
    		}else{
    			impactoAmbientalPmaBean.getListaOtrosImpactosHistoricos().add(o);
    		}
    	}
    	
        impactoAmbientalPmaBean.setActividadOtros(new MatrizFactorImpactoOtros());
        reasisgnarIndiceOtros();
        this.setRegistrosHistorialOtroImpacto();
    }

    private void reasisgnarIndiceOtros() {
        int i = 0;
        for (MatrizFactorImpactoOtros ei : impactoAmbientalPmaBean.getListaActividadOtros()) {
            ei.setIndice(i);
            i++;
        }
    }

    private void cargarActividadNodos() {
        try {
            impactoAmbientalPmaBean.setListaActividad(new ArrayList<EntityNodoActividad>());
            List<ImpactoAmbientalPma> listaImpactos = fichaAmbientalPmaFacade.getImpactosAmbientalesPorFichaId(impactoAmbientalPmaBean.getFicha().getId());
            if (listaImpactos != null && !listaImpactos.isEmpty()) {
            	existenImpactos = true;
                cargarActividadesExistentes(listaImpactos);
                cargarArbol();
            } else {
                cargarArbol();
            }
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    private void cargarArbol() {
        impactoAmbientalPmaBean.setRoot(new DefaultTreeNode());
        for (ActividadProcesoPma a : impactoAmbientalPmaBean.getListaActividades()) {
            TreeNode nodoH = new DefaultTreeNode(new EntityNodoActividad(a), impactoAmbientalPmaBean.getRoot());
            nodoH.setExpanded(true);
            if (a.getListaMatrizFactorImpacto() != null && !a.getListaMatrizFactorImpacto().isEmpty()) {
                reasignarIndice(a);
                for (MatrizFactorImpacto mfi : a.getListaMatrizFactorImpacto()) {
                	//MarielaG aumento MatrizFactorImpacto para manejo historial
                    new DefaultTreeNode(new EntityNodoActividad(a, mfi.getFactorPma(), mfi.getImpactoPma(), mfi.getIndice(), true, mfi), nodoH);
                }
            } else {
                a.setListaMatrizFactorImpacto(new ArrayList<MatrizFactorImpacto>());
            }
        }
    }

    private void cargarActividadesExistentes(List<ImpactoAmbientalPma> listaImpactos) throws ServiceException {
        for (ActividadProcesoPma a : impactoAmbientalPmaBean.getListaActividades()) {
            for (ImpactoAmbientalPma i : listaImpactos) {
                if (a.equals(i.getActividadProcesoPma())) {
                	//MarielaG cambio metodo para manejo historial
                	List<MatrizFactorImpacto> listaMatrizActividad = new ArrayList<>(); 
                	List<MatrizFactorImpacto> listaMatriz = actividadFactorImpactoFacade.listarTodoMatriz(i.getId());
                	for (MatrizFactorImpacto m : listaMatriz) {
                		if(m.getIdRegistroOriginal() == null){
                			listaMatrizActividad.add(m);
                			
                			MatrizFactorImpacto nuevoMatrizFactorImpacto = (MatrizFactorImpacto) SerializationUtils.clone(m);
                        	nuevoMatrizFactorImpacto.setId(m.getId());
                        	impactoAmbientalPmaBean.getListaImpactosOriginales().add(nuevoMatrizFactorImpacto);
                		}else{
                			impactoAmbientalPmaBean.getListaImpactosHistoricos().add(m);
                		}
                	}
                    a.setListaMatrizFactorImpacto(listaMatrizActividad);
                    break;
                }
            }
        }
        
        this.setRegistrosHistorial();
    }

    private void reasignarIndice(ActividadProcesoPma actividadProcesoPma) {
        int indice = 0;
        for (MatrizFactorImpacto m : actividadProcesoPma.getListaMatrizFactorImpacto()) {
            m.setIndice(indice);
            indice++;
        }
    }

    public void cargarImpacto() {
        try {
            impactoAmbientalPmaBean.setIdImpacto(null);
            if (impactoAmbientalPmaBean.getEntityNodoActividad().getFactor().getId() != null) {
                impactoAmbientalPmaBean.setListaImpacto(new ArrayList<SelectItem>());
                List<ActividadFactorImpactoPma> lista = actividadFactorImpactoFacade.obtenerPorActividadFactorPma(
                        impactoAmbientalPmaBean.getEntityNodoActividad().getActividadProcesoPma().getActividadComercial(),
                        impactoAmbientalPmaBean.getEntityNodoActividad().getFactor());
                impactoAmbientalPmaBean.getListaImpacto().add(new SelectItem(null, SELECCIONE));
                for (ActividadFactorImpactoPma a : lista) {
                    impactoAmbientalPmaBean.getListaImpacto().add(new SelectItem(a.getImpacto().getId(), a.getImpacto().getNombre()));
                }
            }
        } catch (ServiceException e) {
            JsfUtil.addMessageError("No se pueden cargar los impactos.");
            LOG.error(e, e);
        }
    }

    private boolean validarGuardarImpacto() {
        List<String> msgs = new ArrayList<String>();
        if (impactoAmbientalPmaBean.getIdImpacto() == null || impactoAmbientalPmaBean.getIdImpacto().isEmpty()) {
            msgs.add("Debe ingresar los campos requeridos.");
        }
        if (!msgs.isEmpty()) {
            JsfUtil.addMessageError(msgs);
            return false;
        }
        return true;
    }
    
    private boolean validaRepetidos() {
        Integer idImpacto = new Integer(impactoAmbientalPmaBean.getIdImpacto());
        boolean retorno = true;
        for (MatrizFactorImpacto m : impactoAmbientalPmaBean.getEntityNodoActividad().getActividadProcesoPma().getListaMatrizFactorImpacto()) {
            if (idImpacto.equals(m.getImpactoPma().getId())) {
                retorno = false;
                JsfUtil.addMessageError("El impacto ya esta ingresado para esta actividad.");
                break;
            }
        }
        return retorno;
    }

    public void guardarImpacto() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (validarGuardarImpacto() && validaRepetidos()) {
            for (ActividadProcesoPma a : impactoAmbientalPmaBean.getListaActividades()) {
                if (a.getId().equals(impactoAmbientalPmaBean.getEntityNodoActividad().getActividadProcesoPma().getId())) {
                    if (!impactoAmbientalPmaBean.getEntityNodoActividad().isEditar()) {
                        MatrizFactorImpacto mfi = new MatrizFactorImpacto();
                        mfi.setFactorPma(impactoAmbientalPmaBean.getEntityNodoActividad().getFactor());
                        mfi.setImpactoPma(actividadFactorImpactoFacade.obtenerImpactoPorId(Integer.valueOf(impactoAmbientalPmaBean.getIdImpacto())));
                        a.getListaMatrizFactorImpacto().add(mfi);
                    } else {
                    	//MarielaG cambio para manejo historial
                        MatrizFactorImpacto mfi = a.getListaMatrizFactorImpacto().get(impactoAmbientalPmaBean.getEntityNodoActividad().getIndice());
                        mfi.setFactorPma(impactoAmbientalPmaBean.getEntityNodoActividad().getFactor());
                        mfi.setImpactoPma(actividadFactorImpactoFacade.obtenerImpactoPorId(Integer.valueOf(impactoAmbientalPmaBean.getIdImpacto())));
                        a.getListaMatrizFactorImpacto().set(impactoAmbientalPmaBean.getEntityNodoActividad().getIndice(), mfi);
                    }
                    break;
                }
            }
            cargarArbol();
            context.addCallbackParam("impactoIn", true);
        } else {
            context.addCallbackParam("impactoIn", false);
        }
    }

    public void seleccionarActividad(EntityNodoActividad entityNodoActividad) {
        impactoAmbientalPmaBean.setIdImpacto(null);
        impactoAmbientalPmaBean.setEntityNodoActividad(entityNodoActividad);
        try {
            cargarFactor();
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlgImpacto').show();");
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al cargar el factor");
            LOG.error(e, e);
        }
    }

    public void seleccionarActividadEditar(EntityNodoActividad entityNodoActividad) {
        entityNodoActividad.setEditar(true);
        //MarielaG cambio para manejo historial clona nodo actual
        impactoAmbientalPmaBean.setEntityNodoActividad((EntityNodoActividad) SerializationUtils.clone(entityNodoActividad));
        try {
            cargarFactor();
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlgImpacto').show();");
        } catch (ServiceException e) {
            JsfUtil.addMessageError("Error al cargar el factor");
            LOG.error(e, e);
        }
    }

    private void cargarFactor() throws ServiceException {
        List<ActividadFactorImpactoPma> factores
                = actividadFactorImpactoFacade.obtenerPorActividadPma(
                        impactoAmbientalPmaBean.getEntityNodoActividad().getActividadProcesoPma().getActividadComercial());
        impactoAmbientalPmaBean.setListaFactor(new ArrayList<FactorPma>());
        impactoAmbientalPmaBean.getListaFactor().add(new FactorPma(SELECCIONE));
        for (ActividadFactorImpactoPma a : factores) {
            if (!impactoAmbientalPmaBean.getListaFactor().contains(a.getFactor())) {
                impactoAmbientalPmaBean.getListaFactor().add(a.getFactor());
            }
        }
        if (impactoAmbientalPmaBean.getEntityNodoActividad().isEditar()) {
            cargarImpacto();
            impactoAmbientalPmaBean.setIdImpacto(impactoAmbientalPmaBean.getEntityNodoActividad().getImpacto().getId().toString());
        } else {
            impactoAmbientalPmaBean.getEntityNodoActividad().setFactor(null);
            impactoAmbientalPmaBean.setListaImpacto(new ArrayList<SelectItem>());
        }

    }

    private void cargarFactorOtros() throws ServiceException {
        impactoAmbientalPmaBean.setListaFactorOtros(new ArrayList<FactorPma>());
        impactoAmbientalPmaBean.getListaFactorOtros().add(new FactorPma(SELECCIONE));
        impactoAmbientalPmaBean.getListaFactorOtros().addAll(actividadFactorImpactoFacade.listarFactor());
    }

    public void eliminarImpacto(EntityNodoActividad entityNodoActividad) {
        impactoAmbientalPmaBean.setEntityNodoActividad(entityNodoActividad);
        
        //MarielaG para guardar impactos que se eliminan
        MatrizFactorImpacto impactoSeleccionado = impactoAmbientalPmaBean.getEntityNodoActividad().getActividadProcesoPma().getListaMatrizFactorImpacto().get(impactoAmbientalPmaBean.getEntityNodoActividad().getIndice());
        if(impactoSeleccionado.getId() != null)
        	impactoAmbientalPmaBean.getListaImpactosEliminados().add(impactoSeleccionado);
        
        impactoAmbientalPmaBean.getEntityNodoActividad().getActividadProcesoPma().getListaMatrizFactorImpacto().remove(impactoAmbientalPmaBean.getEntityNodoActividad().getIndice());
        cargarArbol();
    }

    public void guardar() {
        try {
            if (validarGuardar()) {
				// MarielaG aumento metodo para guardar historial
				this.guardarHistorial();
            	
                fichaAmbientalPmaFacade.guardarImpactoAmbiental(impactoAmbientalPmaBean.getListaActividades(), 
                		impactoAmbientalPmaBean.getListaActividadOtros(), impactoAmbientalPmaBean.getFicha(), 
                		impactoAmbientalPmaBean.getListaImpactosEliminados(),
                		impactoAmbientalPmaBean.getListaOtrosImpactosEliminados());
                inicio();
                JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
            }
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    private boolean validarGuardar() {
        List<String> msgs = new ArrayList<String>();
        for (ActividadProcesoPma a : impactoAmbientalPmaBean.getListaActividades()) {
            if (a.getListaMatrizFactorImpacto().isEmpty()) {
                msgs.add("Debe seleccionar el factor e impacto correspondiente a la actividad ingresada: " + a.getActividadComercial().getNombreActividad());
            }
        }
        if (!impactoAmbientalPmaBean.getListaActividadesOtros().isEmpty() && (impactoAmbientalPmaBean.getListaActividadOtros() == null
                || impactoAmbientalPmaBean.getListaActividadOtros().isEmpty())) {
            msgs.add("Debe ingresar el impacto para otras actividades.");
        }
        if (!msgs.isEmpty()) {
            JsfUtil.addMessageError(msgs);
            return false;
        }
        return true;
    }

    public void guardarImpactoActividadOtros() {
        if (impactoAmbientalPmaBean.getActividadOtros().getFactorPma() == null
                || impactoAmbientalPmaBean.getActividadOtros().getActividadProcesoPma() == null
                || impactoAmbientalPmaBean.getActividadOtros().getImpactoOtros().isEmpty()) {
            JsfUtil.addMessageError("Debe ingresar los campos requeridos.");
        } else {
            if (!impactoAmbientalPmaBean.getActividadOtros().isEditar()) {
                impactoAmbientalPmaBean.getActividadOtros().setFichaAmbientalPma(impactoAmbientalPmaBean.getFicha());
                impactoAmbientalPmaBean.getListaActividadOtros().add(impactoAmbientalPmaBean.getActividadOtros());
                reasisgnarIndiceOtros();
            }
            
            impactoAmbientalPmaBean.setActividadOtros(new MatrizFactorImpactoOtros());
        }
    }

    public void seleccionarImpactoOtros(MatrizFactorImpactoOtros impactoOtros) {
        impactoOtros.setEditar(true);
        impactoAmbientalPmaBean.setActividadOtros(impactoOtros);
    }

    public void eliminarImpactoOtros(MatrizFactorImpactoOtros impactoOtros) {
        //MarielaG para almacenar impactos que se eliminan y poder guardar el historial
    	MatrizFactorImpactoOtros impactoSeleccionado = impactoAmbientalPmaBean.getListaActividadOtros().get(impactoOtros.getIndice());
    	if(impactoSeleccionado.getId() != null)
    		impactoAmbientalPmaBean.getListaOtrosImpactosEliminados().add(impactoSeleccionado);
        
        impactoAmbientalPmaBean.getListaActividadOtros().remove(impactoOtros.getIndice());
        reasisgnarIndiceOtros();
    }

    /**
     * Para el impreso
     *
     * @return
     */
    public String generarImpreso() {
        try {
            EntityCronograma obj = cargarHtml();
            return obj.getDetalleCronograma();
        } catch (Exception e) {
            LOG.error(e, e);
            return null;
        }
    }

    private EntityCronograma cargarHtml() throws ServiceException {
        EntityCronograma obj = new EntityCronograma();
        List<EntityDetalleImpactos> listaAux = new ArrayList<EntityDetalleImpactos>();
        String[] columnas = new String[]{"Factor", "Impacto"};
        StringBuilder matriz = new StringBuilder();
        for (ActividadProcesoPma a : impactoAmbientalPmaBean.getListaActividades()) {
            String cabeza = "Fase: " + a.getActividadComercial().getCategoriaFase().getFase().getNombre() + " - Actividad: " + a.getActividadComercial().getNombreActividad();
            for (MatrizFactorImpacto m : a.getListaMatrizFactorImpacto()) {
                EntityDetalleImpactos detalleImpactos = new EntityDetalleImpactos();
                detalleImpactos.setFactor(m.getFactorPma().getNombre());
                detalleImpactos.setImpacto(m.getImpactoPma().getNombre());
                listaAux.add(detalleImpactos);
            }
            matriz.append(UtilFichaMineria.devolverDetalle(cabeza, columnas, listaAux, columnas, null));
            listaAux = new ArrayList<EntityDetalleImpactos>();
        }
        if (impactoAmbientalPmaBean.getListaActividadOtros() != null && !impactoAmbientalPmaBean.getListaActividadOtros().isEmpty()) {                        
            for (MatrizFactorImpactoOtros impactoOtros : impactoAmbientalPmaBean.getListaActividadOtros()) {
            	String cabeza = impactoOtros.getActividadProcesoPma().getDescripcionOtros();
                listaAux = new ArrayList<EntityDetalleImpactos>();
                listaAux.add(new EntityDetalleImpactos(impactoOtros.getFactorPma().getNombre(), impactoOtros.getImpactoOtros()));
                matriz.append(UtilFichaMineria.devolverDetalle("Fase: " + impactoOtros.getActividadProcesoPma().getDescripcionFaseOtros() +" - Otras Actividades: " + cabeza, columnas, listaAux, columnas, null));                
            }
        }
        obj.setDetalleCronograma(matriz.toString());
        return obj;
    }

    public void redirigirPunto4() {
        JsfUtil.redirectTo("/prevencion/categoria2/fichaAmbiental/descripcionProceso.jsf");
    }

    /**
	 * MarielaG guardar el historial de los impactos modificados y eliminados
	 */
    public void guardarHistorial() throws Exception {
    	List<MatrizFactorImpacto> listaImpactosHistoriales = new ArrayList<MatrizFactorImpacto>();

    	//guardar historial de los impactos modificados
    	for (ActividadProcesoPma a : impactoAmbientalPmaBean.getListaActividades()) {
            for (MatrizFactorImpacto mfi : a.getListaMatrizFactorImpacto()) {
            	if(mfi.getId() != null){
	                for(MatrizFactorImpacto mfiOriginal :impactoAmbientalPmaBean.getListaImpactosOriginales()){
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
            			if(mfi.getImpactoAmbientalPma() == null){
            				for (MatrizFactorImpacto mfiAux : impactoAmbientalPmaBean.getListaImpactosEliminados()) {
            					if(mfiAux.getImpactoAmbientalPma().getActividadProcesoPma().getId().equals(a.getId())){
            						mfi.setImpactoAmbientalPma(mfiAux.getImpactoAmbientalPma());
            						break;
            					}
            				}
            			}
            		}
            	}
            }
        }
    	
    	//guardar historial de los impactos eliminados
		for (MatrizFactorImpacto mfiPorEliminar : impactoAmbientalPmaBean.getListaImpactosEliminados()) {
			if (mfiPorEliminar.getFechaHistorico() == null) {
				MatrizFactorImpacto mfiOriginal = (MatrizFactorImpacto) SerializationUtils.clone(mfiPorEliminar);
				mfiOriginal.setId(null);
				mfiOriginal.setIdRegistroOriginal(mfiPorEliminar.getId());
				mfiOriginal.setFechaHistorico(new Date());
				listaImpactosHistoriales.add(mfiOriginal);
			}
		}
    	
    	if(listaImpactosHistoriales.size() > 0)
    		fichaAmbientalPmaFacade.guardarMatrizFactorImpacto(listaImpactosHistoriales);
    	
    	if (!impactoAmbientalPmaBean.getListaActividadesOtros().isEmpty()){
	    	//guardar historial de otros impactos modificados
	    	List<MatrizFactorImpactoOtros> listaOtrosImpactosHistoriales = new ArrayList<MatrizFactorImpactoOtros>();
	    	for (MatrizFactorImpactoOtros mfio : impactoAmbientalPmaBean.getListaActividadOtros()) {
	    		if(mfio.getId() != null){
		            for(MatrizFactorImpactoOtros mfiOriginal :impactoAmbientalPmaBean.getListaOtrosImpactosOriginales()){
		            	if(mfio.getId().equals(mfiOriginal.getId())){
		            		if(!mfio.equalsObject(mfiOriginal)){
		            			mfiOriginal.setId(null);
		            			mfiOriginal.setIdRegistroOriginal(mfio.getId());
		            			mfiOriginal.setFechaHistorico(new Date());
		            			
		            			listaOtrosImpactosHistoriales.add(mfiOriginal);
		            		}
		            		
		            		break;
		            	}
		            }
	    		}else{
	    			if(existenOtrosImpactos)
	    				mfio.setFechaHistorico(new Date());
	        	}
	        }
	    	
	    	//guardar historial de otros impactos eliminados
			for (MatrizFactorImpactoOtros mfiPorEliminar : impactoAmbientalPmaBean
					.getListaOtrosImpactosEliminados()) {
				if (mfiPorEliminar.getFechaHistorico() == null) {
					MatrizFactorImpactoOtros mfiOriginal = (MatrizFactorImpactoOtros) SerializationUtils.clone(mfiPorEliminar);
					mfiOriginal.setId(null);
					mfiOriginal.setIdRegistroOriginal(mfiPorEliminar.getId());
					mfiOriginal.setFechaHistorico(new Date());
					listaOtrosImpactosHistoriales.add(mfiOriginal);
				}
			}
	    	
	    	if(listaOtrosImpactosHistoriales.size() > 0)
	    		fichaAmbientalPmaFacade.guardarMatrizFactorOtroImpacto(listaOtrosImpactosHistoriales);
    	}
	}
    
    /**
	 * MarielaG Establece si el impacto es un historial
	 */
	public void setRegistrosHistorial() {		
		for (ActividadProcesoPma a : impactoAmbientalPmaBean.getListaActividades()) {
			if (a.getListaMatrizFactorImpacto() != null) {
				for (MatrizFactorImpacto m : a.getListaMatrizFactorImpacto()) {
					if (m.getFechaHistorico() != null) {
						m.setNuevoEnModificacion(true);
					}
					for (MatrizFactorImpacto mh : impactoAmbientalPmaBean.getListaImpactosHistoricos()) {
						if (m.getId().equals(mh.getIdRegistroOriginal())) {
							m.setHistorialModificaciones(true);
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * MarielaG Establece si el registro otro impacto es un historial
	 */
	public void setRegistrosHistorialOtroImpacto() {		
		//para otros impactos
		for (MatrizFactorImpactoOtros m : impactoAmbientalPmaBean.getListaActividadOtros()) {
			if (m.getFechaHistorico() != null) {
				m.setNuevoEnModificacion(true);
			}
			for (MatrizFactorImpactoOtros mh : impactoAmbientalPmaBean.getListaOtrosImpactosHistoricos()) {
				if (m.getId().equals(mh.getIdRegistroOriginal())) {
					m.setHistorialModificaciones(true);
					break;
				}
			}
		}
	}
	
	/**
	 * MarielaG Metodos para llenar el objeto con los registros respectivos
	 */
	public void fillHistorialImpactos(EntityNodoActividad nodoSeleccionado) {
		impactoAmbientalPmaBean.setHistorialImpactosSeleccionados(new ArrayList<MatrizFactorImpacto>());
		impactoAmbientalPmaBean.setHistorialOtrosImpactosSeleccionados(new ArrayList<MatrizFactorImpactoOtros>());
		
		for (MatrizFactorImpacto impactoHistorial : impactoAmbientalPmaBean.getListaImpactosHistoricos()) {
			if(nodoSeleccionado.getFactorImpacto().getId().equals(impactoHistorial.getIdRegistroOriginal())){
				impactoAmbientalPmaBean.getHistorialImpactosSeleccionados().add(impactoHistorial);
			}
		}
	}
	
	public void fillHistorialImpactosEliminados() {
		impactoAmbientalPmaBean.setHistorialImpactosSeleccionados(new ArrayList<MatrizFactorImpacto>());
		impactoAmbientalPmaBean.setHistorialOtrosImpactosSeleccionados(new ArrayList<MatrizFactorImpactoOtros>());
		List<MatrizFactorImpacto> impactosTemporal = new ArrayList<MatrizFactorImpacto>();

		for (MatrizFactorImpacto impactoHistorial : impactoAmbientalPmaBean.getListaImpactosHistoricos()) {
			Boolean existeImpacto = false;
			for (MatrizFactorImpacto actual : impactoAmbientalPmaBean.getListaImpactosOriginales()){
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
			impactoAmbientalPmaBean.getHistorialImpactosSeleccionados().addAll(impactosTemporal);
		}
	}
	
	public void fillHistorialOtrosImpactos(MatrizFactorImpactoOtros otroImpactoSeleccionado) {
		impactoAmbientalPmaBean.setHistorialOtrosImpactosSeleccionados(new ArrayList<MatrizFactorImpactoOtros>());
		impactoAmbientalPmaBean.setHistorialImpactosSeleccionados(new ArrayList<MatrizFactorImpacto>());
		
		for (MatrizFactorImpactoOtros impactoHistorial : impactoAmbientalPmaBean.getListaOtrosImpactosHistoricos()) {
			if(otroImpactoSeleccionado.getId().equals(impactoHistorial.getIdRegistroOriginal())){
				impactoAmbientalPmaBean.getHistorialOtrosImpactosSeleccionados().add(impactoHistorial);
			}
		}
	}
	
	public void fillHistorialOtrosImpactosEliminados() {
		impactoAmbientalPmaBean.setHistorialOtrosImpactosSeleccionados(new ArrayList<MatrizFactorImpactoOtros>());
		impactoAmbientalPmaBean.setHistorialImpactosSeleccionados(new ArrayList<MatrizFactorImpacto>());
		List<MatrizFactorImpactoOtros> otrosImpactosTemporal = new ArrayList<MatrizFactorImpactoOtros>(); 

		for (MatrizFactorImpactoOtros impactoHistorial : impactoAmbientalPmaBean.getListaOtrosImpactosHistoricos()) {
			Boolean existeImpacto = false;
			for (MatrizFactorImpactoOtros actual : impactoAmbientalPmaBean.getListaOtrosImpactosOriginales()){
				if(actual.getId().equals(impactoHistorial.getIdRegistroOriginal())){
					existeImpacto = true;
					break;
				}
			}
			
			if(!existeImpacto)
				otrosImpactosTemporal.add(impactoHistorial);
		}
		
		if (otrosImpactosTemporal.size() > 0) {
			otrosImpactosEliminados = true;
			impactoAmbientalPmaBean.getHistorialOtrosImpactosSeleccionados().addAll(otrosImpactosTemporal);
		}
	}
}
