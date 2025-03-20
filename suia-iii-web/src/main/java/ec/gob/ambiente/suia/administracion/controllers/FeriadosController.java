/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.controllers;

import ec.gob.ambiente.suia.administracion.bean.RolBean;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.administracion.facade.MenuFacade;
import ec.gob.ambiente.suia.domain.Holiday;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;

/**
 *
 * @author mariela
 */
@ManagedBean
@ViewScoped
public class FeriadosController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1228354609196090636L;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
    @EJB
    private FeriadosFacade feriadosFacade;
    @EJB
    private MenuFacade menuFacade;
    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    
    @Getter
    @Setter
    private RolBean rolBean;
    
    @Getter
    @Setter
    private List<Holiday> listaFeriados;
    
    @Getter
    @Setter
    private List<UbicacionesGeografica> listaProvincia;

    @Getter
    @Setter
    private List<UbicacionesGeografica> listaCanton;
    
    @Getter
    @Setter
    private Boolean mostrarFormNuevo;
    
    @Getter
    @Setter
    private Holiday feriadoSeleccionado;
    
    @Getter
    @Setter
    private String justificacion;    
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(FeriadosController.class);

    @PostConstruct
	private void cargarDatos() {
		try {
			validarPagina();
			listaFeriados = feriadosFacade.listarFeriados();
			mostrarFormNuevo = true;
			cargarProvincias();
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	public void validarPagina() {
		if (!loginBean.isAuthenticated()) {
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}
	}

    public void registrarNuevo() {
    	feriadoSeleccionado = new Holiday();
        mostrarFormNuevo = false;
    }
    
    private void cargarProvincias() {
        try {
            listaProvincia = ubicacionGeograficaFacade.listarProvincia();
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    public void cargarCanton() {
        try {
            if (feriadoSeleccionado.getIdProvincia() != null) {
                listaCanton = ubicacionGeograficaFacade.listarPorPadre(new UbicacionesGeografica(feriadoSeleccionado.getIdProvincia()));
            } else {
                listaCanton = null;
                feriadoSeleccionado.setIdCanton(null);
            }
        } catch (NumberFormatException e) {
            LOG.error(e, e);
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }
    
	public void actualizarFechas() {
		feriadoSeleccionado.setFechaFin(null);
		RequestContext.getCurrentInstance().reset(":frmDatos:fechaFin");
	}
    
    public void cancelar() {
    	cargarDatos();
    }
    
    public void seleccionarEditar(Holiday feriado) {
        try {
        	feriadoSeleccionado = feriado;
        	if(feriado.getIdLocalidad() != null){
        		if(feriado.getLocalidad().getUbicacionesGeografica() != null && feriado.getLocalidad().getUbicacionesGeografica().getCodificacionInec() != null){
        			feriadoSeleccionado.setIdProvincia(feriado.getLocalidad().getUbicacionesGeografica().getId());
        			feriadoSeleccionado.setIdCanton(feriado.getLocalidad().getId());
        			
				} else {
					feriadoSeleccionado.setIdProvincia(feriado.getLocalidad().getId());
				}
				cargarCanton();
			}
        	
        	mostrarFormNuevo = false;
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
        }
    }

    public void guardar() {
        try {
        	if(feriadoSeleccionado.getId() == null){
        		feriadoSeleccionado.setUsuarioCreacion(loginBean.getUsuario().getNombre());
        	}else{
        		feriadoSeleccionado.setUsuarioModificacion(loginBean.getUsuario().getNombre());
        		feriadoSeleccionado.setFechaModificacion(new Date());
        	}
        	
        	if(!feriadoSeleccionado.getEsFeriadoNacional()){
        		if (feriadoSeleccionado.getIdCanton() != null && feriadoSeleccionado.getIdCanton() > 0) {
            		feriadoSeleccionado.setIdLocalidad(feriadoSeleccionado.getIdCanton());
            	}else if (feriadoSeleccionado.getIdProvincia() != null && feriadoSeleccionado.getIdProvincia() > 0) {
            		feriadoSeleccionado.setIdLocalidad(feriadoSeleccionado.getIdProvincia());
            	}
        	}else{
        		feriadoSeleccionado.setIdLocalidad(null);
        	}
        	
        	if(validarFeriado()){
        	     long diffTime = feriadoSeleccionado.getFechaFin().getTime() - feriadoSeleccionado.getFechaInicio().getTime();
        	     long diffDays = diffTime / (1000 * 60 * 60 * 24);
        	     int totalDiasFeriado = (int)diffDays;
        	     
        	     feriadoSeleccionado.setTotalDias(totalDiasFeriado + 1);
        	     
	        	feriadosFacade.guardar(feriadoSeleccionado);
	            cargarDatos();
	            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        	}
        } catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            LOG.error(e, e);
        }
    }
    
	public boolean validarFeriado() throws ServiceException {
		Calendar fechaInicial = Calendar.getInstance();
		Calendar fechaFinal = Calendar.getInstance();
		
		if(!feriadoSeleccionado.getEsFeriadoNacional()){
			if(feriadoSeleccionado.getIdLocalidad() == null){
				JsfUtil.addMessageError("Seleccione la localidad donde aplica el feriado");
				return false;
			}
		}

		fechaInicial.setTime(feriadoSeleccionado.getFechaInicio());
		fechaFinal.setTime(feriadoSeleccionado.getFechaFin());
		int finSemana = 0;
		while (fechaInicial.before(fechaFinal)
				|| fechaInicial.equals(fechaFinal)) {
			if (fechaInicial.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
					|| fechaInicial.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				finSemana++;
			}
			fechaInicial.add(Calendar.DATE, 1);
		}
		
		if(finSemana > 0){
			JsfUtil.addMessageInfo("El rango de fechas no puede incluir fines de semana");
			return false;
		}
		
		//validar si rango ya existe en bdd		
		List<Holiday> listaFeriadosExistentes = new ArrayList<Holiday>();
		if (feriadoSeleccionado.getId() == null) {
			listaFeriadosExistentes = feriadosFacade
					.listarFeriadosPorRango(feriadoSeleccionado.getFechaInicio(),
							feriadoSeleccionado.getFechaFin());
		}else{
			listaFeriadosExistentes = feriadosFacade
					.listarFeriadosDiferentesPorRango(feriadoSeleccionado.getFechaInicio(),
							feriadoSeleccionado.getFechaFin(), feriadoSeleccionado.getId());
		}
		if (listaFeriadosExistentes != null && listaFeriadosExistentes.size() > 0) {
			if (!feriadoSeleccionado.getEsFeriadoNacional()) {
				// es un feriado local CANTONAL
				for (Holiday feriado : listaFeriadosExistentes) {
					if (feriado.getEsFeriadoNacional()) {
						JsfUtil.addMessageInfo("Las fechas ya se encuentran registradas a nivel nacional");
						return false;
					}
					
					if (feriadoSeleccionado.getIdLocalidad().equals(feriado.getLocalidad().getId())) {
						JsfUtil.addMessageInfo("Las fechas ya se encuentran registradas en esa localidad");
						return false;
					}
					if(feriado.getLocalidad().getUbicacionesGeografica() != null && 
							feriado.getLocalidad().getUbicacionesGeografica().getCodificacionInec() != null){
						if (feriadoSeleccionado.getIdLocalidad().equals(feriado.getLocalidad().getUbicacionesGeografica().getId())) {
							JsfUtil.addMessageInfo("Las fechas ya se encuentran registradas en la provincia");
							return false;
						}
					}
					if(feriadoSeleccionado.getIdProvincia() != null && feriadoSeleccionado.getIdProvincia() > 0){
						if (feriadoSeleccionado.getIdProvincia().equals(feriado.getLocalidad().getId())) {
							JsfUtil.addMessageInfo("Las fechas ya se encuentran registradas en la provincia");
							return false;
						}
					}
				}
			}else {
					JsfUtil.addMessageInfo("Las fechas ya se encuentran registradas");
					return false;
			}
		}

		return true;
	}
	
	public void seleccionarEliminar(Holiday feriado) {
        try {
        	feriadoSeleccionado = feriado;
        	
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
        }
    }
	
	public void eliminarFeriado() { 
		RequestContext context = RequestContext.getCurrentInstance();
        try {
        	Holiday feriado = feriadoSeleccionado;
        	if(feriado != null){
        		feriado.setFechaEliminacion(new Date());
        		feriado.setUsuarioEliminacion(loginBean.getUsuario().getNombre());
        		feriadosFacade.eliminar(feriado);
        		cargarDatos();
        		
        		context.addCallbackParam("actividadIn", true);
        		context.update("frmDatos");
	            JsfUtil.addMessageInfo("Registro eliminado satisfactoriamente.");
        	}
        } catch (Exception e) {
        	context.addCallbackParam("actividadIn", false);
            JsfUtil.addMessageError("Ocurrió un error al eliminar el registro.");
        }
    }

}
