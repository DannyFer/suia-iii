package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.prevencion.categoriaii.mineria.bean.FichaMineriaBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoMaterial;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class FichaMineriaController implements Serializable {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(FichaMineriaController.class);

    private static final long serialVersionUID = -5960789019485377815L;
    @Getter
    @Setter
    private FichaMineriaBean fichaMineriaBean;
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;

    @PostConstruct
    public void cargarDatos() {
        setFichaMineriaBean(new FichaMineriaBean());
        getFichaMineriaBean().iniciarDatos();
        cargarDatosBandeja();
    }

    private void cargarDatosBandeja() {
        ProyectoLicenciamientoAmbiental proyecto = proyectosBean.getProyecto();
        try {
            getFichaMineriaBean().setFichaAmbientalMineria(fichaAmbientalMineriaFacade.obtenerPorProyecto(proyecto));
            if (getFichaMineriaBean().getFichaAmbientalMineria() == null) {
                if (proyectosBean.getProyecto().getId() != null) {
                    getFichaMineriaBean().setFichaAmbientalMineria(new FichaAmbientalMineria());
                    getFichaMineriaBean().getFichaAmbientalMineria().setProyectoLicenciamientoAmbiental(proyecto);
                    getFichaMineriaBean().getFichaAmbientalMineria().setTipoMaterial(new TipoMaterial(proyecto.getIdTipoMaterial()));
                    FichaAmbientalMineria ficha = fichaAmbientalMineriaFacade.guardarFicha(getFichaMineriaBean().getFichaAmbientalMineria());
                    JsfUtil.cargarObjetoSession(Constantes.SESSION_FICHA_AMBIENTAL_MINERA_OBJECT, ficha);
                } else {
                    getFichaMineriaBean().setMensaje("Debes seleccionar un proyecto");
                }
            } else {
                getFichaMineriaBean().getFichaAmbientalMineria().getTipoMaterial();
                getFichaMineriaBean().getFichaAmbientalMineria().setProyectoLicenciamientoAmbiental(proyecto);
                JsfUtil.cargarObjetoSession(Constantes.SESSION_FICHA_AMBIENTAL_MINERA_OBJECT, getFichaMineriaBean().getFichaAmbientalMineria());
            }
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }
    
    public boolean ppc() throws ParseException {
		Date fechaproyecto=null;
		Date fechabloqueo=null;
		Date fechabloqueoObligatorioInicio=null;
		Date fechabloqueoObligatorioFin=null;
		Date fechabloqueoOpcionalInicio=null;
		Date fechabloqueoOpcionalFin=null;
		
		Date fechabloqueoSIN=null;
		boolean bloquear=false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		fechabloqueo = sdf.parse(Constantes.getFechaProyectosSinPccAntes());
		fechabloqueoObligatorioInicio = sdf.parse(Constantes.getFechaProyectosObligatorioPccInicio());
		fechabloqueoObligatorioFin = sdf.parse(Constantes.getFechaProyectosObligatorioPccFin());	
		fechabloqueoOpcionalInicio = sdf.parse(Constantes.getFechaProyectosOpcionalPccInicio());
		fechabloqueoOpcionalFin = sdf.parse(Constantes.getFechaProyectosOpcionalPccFin());	
		
		fechabloqueoSIN = sdf.parse(Constantes.getFechaProyectosSinPpcAdelante());
		
		fechaproyecto=sdf.parse(proyectosBean.getProyecto().getFechaRegistro().toString());
		if (fechaproyecto.before(fechabloqueo)){
			return false;
		}		
		if (fechaproyecto.after(fechabloqueoObligatorioInicio) && fechaproyecto.before(fechabloqueoObligatorioFin)){
			return true;
		}
		
		if (fechaproyecto.after(fechabloqueoObligatorioInicio) && fechaproyecto.before(fechabloqueoObligatorioFin)){
			return true;
		}
		
		if (fechaproyecto.after(fechabloqueoOpcionalInicio) && fechaproyecto.before(fechabloqueoOpcionalFin)){
			return true;
		}
		if (fechaproyecto.after(fechabloqueoSIN)){
			return false;
		}		
		
		return bloquear;
		
	}
    

}
