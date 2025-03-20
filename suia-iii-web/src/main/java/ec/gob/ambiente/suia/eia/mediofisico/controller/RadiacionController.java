package ec.gob.ambiente.suia.eia.mediofisico.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.suia.coordenadageneral.facade.CoordenadaGeneralFacade;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.Radiacion;
import ec.gob.ambiente.suia.domain.Ruido;
import ec.gob.ambiente.suia.eia.mediofisico.bean.MedioFisicoBean;
import ec.gob.ambiente.suia.eia.mediofisico.facade.RadiacionFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.util.Map;
@ManagedBean
@ViewScoped
public class RadiacionController implements Serializable {
	
	/**
	 * 
	 */
	private static final Logger LOG= Logger.getLogger(RadiacionController.class);
	private static final long serialVersionUID = 5573996024882222870L;
	@Getter
	@Setter
	@ManagedProperty(value = "#{medioFisicoBean}")
	private MedioFisicoBean medioFisicoBean;
	@EJB
	private RadiacionFacade radiacionFacade;
	@EJB
	private CoordenadaGeneralFacade coordenadaGeneralFacade;
        
	@Getter
	@Setter
	private Integer eiaId;
	
	@PostConstruct
	public void iniciar(){
		try{
		EstudioImpactoAmbiental eia = (EstudioImpactoAmbiental)JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
        setEiaId(eia.getId());
        List<Radiacion> radiaciones= radiacionFacade.radiacionXEiaId(getEiaId());
        if(!radiaciones.isEmpty()){
        	medioFisicoBean.setListaRadiaciones(radiaciones);
        	for (Radiacion radiacion:radiaciones) {
				CoordenadaGeneral coordenadaGeneral= coordenadaGeneralFacade.coordenadasGeneralXTablaId(radiacion.getId(), Radiacion.class.getSimpleName()).get(0);
				radiacion.setCoordenadaGeneral(coordenadaGeneral);
			}
        }
        medioFisicoBean.setRadiacionBorradas(new ArrayList<Radiacion>());
		}catch(Exception ex){
			LOG.error("Error al iniciar radiaciones",ex);
		}
	}


	public void agregarAListaRadiacion() {
		try {
			if (!medioFisicoBean.getRadiacion().isEditar())
				medioFisicoBean.getListaRadiaciones().add(medioFisicoBean.getRadiacion());
			RequestContext.getCurrentInstance().addCallbackParam("radiacionIn",
					true);
		} catch (Exception e) {
			RequestContext.getCurrentInstance().addCallbackParam("radiacionIn",
					false);
		}
	}

	public void agregarRadiacion() {
		Radiacion radiacion= new Radiacion();
		radiacion.setEditar(false);
		radiacion.setEiaId(getEiaId());
		medioFisicoBean.setRadiacion(radiacion);
		medioFisicoBean.getRadiacion().setCoordenadaGeneral(new CoordenadaGeneral());
	}

	public void seleccionarRadiacion(Radiacion radiacion) {
		radiacion.setEditar(true);
		medioFisicoBean.setRadiacion(radiacion);
	}

	public void eliminarRadiacion(Radiacion radiacion) {
		medioFisicoBean.getListaRadiaciones().remove(radiacion);
		if (radiacion.getId() != null) {
			radiacion.setEstado(false);
			medioFisicoBean.getRadiacionBorradas().add(radiacion);
		}
	}

	public void limpiar() {
		medioFisicoBean.setListaRuido(new ArrayList<Ruido>());
	}

	public String cancelar() {
		medioFisicoBean.setListaRuido(new ArrayList<Ruido>());
		return JsfUtil.actionNavigateTo("/pages/eia/lineaBase/radiacion.jsf");
	}

	public String guardar() {
		try {
                    if(!medioFisicoBean.getListaRadiaciones().isEmpty()){
                        EstudioImpactoAmbiental es = new EstudioImpactoAmbiental(getEiaId());
                        Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
                            radiacionFacade.guardarRadiacion(medioFisicoBean.getListaRadiaciones(),medioFisicoBean.getRadiacionBorradas(), es, mapOpciones.get(EiaOpciones.RADIACION_NO_IONIZANTES_HIDRO));
                            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
                            limpiar();
                            return JsfUtil.actionNavigateTo("/pages/eia/default.jsf");
                        }else{
                            JsfUtil.addMessageError("Debe ingresar registros");
                        }
		} catch (Exception ex) {
			LOG.error("Error al guardar radiaciones",ex);
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
		}
		return null;
	}

}
