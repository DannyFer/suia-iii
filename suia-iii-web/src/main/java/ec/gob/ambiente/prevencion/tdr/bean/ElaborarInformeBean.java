package ec.gob.ambiente.prevencion.tdr.bean;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.domain.TdrInformeTecnico;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.tdr.facade.TdrFacade;
import ec.gob.ambiente.suia.prevencion.tdrinformetecnico.facade.TdrInformeTecnicoFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarInformeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9182812806123074886L;

	@Getter
	@Setter
	private boolean cumpleCriterios;

	@Getter
	@Setter
	private TdrInformeTecnico tdrInformeTecnico;

	@EJB
	@Getter
	@Setter
	private TdrInformeTecnicoFacade tdrInformeTecnicoFacade;

	@EJB
	private TdrFacade tdrFacade;

	@Setter
	@Getter
	TdrEiaLicencia tdrEia;

	@Setter
	@Getter
	private ProyectoLicenciamientoAmbiental proyectoActivo;

	@EJB
	CrudServiceBean crudServiceBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@PostConstruct
	public void init() {
		Integer idProyecto = idProyecto();
		if (idProyecto != null) {
			proyectoActivo = proyectoFacade.buscarProyectosLicenciamientoAmbientalPorId(idProyecto);
			tdrEia = tdrFacade.getTdrEiaLicenciaPorIdProyecto(proyectoActivo.getId());

			if (tdrEia != null) {
				this.tdrInformeTecnico = this.tdrInformeTecnicoFacade.getTdrInformeTecnicoPorIdTdr(tdrEia.getId());

				if (this.tdrInformeTecnico == null) {
					tdrInformeTecnico = new TdrInformeTecnico();
					tdrInformeTecnico.setTdrEia(tdrEia);
				}
			} else {
				JsfUtil.addMessageError("Ha ocurrido un error.");//
				JsfUtil.redirectTo("/proyectos/listadoProyectos.jsf");
			}
		} else {

			JsfUtil.addMessageError("Ha ocurrido un error.");//
			JsfUtil.redirectTo("/proyectos/listadoProyectos.jsf");
		}
	}
	
	public void guardarInformeTecnico(TdrInformeTecnico tdrInformeTecnico){
		crudServiceBean.saveOrUpdate(tdrInformeTecnico);
	}

	/**
	 * seleccionar id del proyecto del proceso activo
	 * 
	 * @return idProyecto
	 */
	public Integer idProyecto() {
		Integer idProyecto;
		try {
			Map<String, Object> variables;
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
					bandejaTareasBean.getTarea().getProcessInstanceId());
			idProyecto = Integer.parseInt((String) variables
					.get(Constantes.ID_PROYECTO));
		} catch (JbpmException e) {
			idProyecto = null;
			JsfUtil.addMessageError("Ha ocurrido un error.");
			JsfUtil.redirectTo("/proyectos/listadoProyectos.jsf");
		}
		return idProyecto;
	}
}
