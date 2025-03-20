package ec.gob.ambiente.prevencion.categoria2.bean;

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

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.suia.domain.Programa;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.PlanPmaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * 
 * @author karla.carvajal
 * 
 */
@ManagedBean
@ViewScoped
public class ProgramaPmaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Setter
	@ManagedProperty(value = "#{planPmaBean}")
	private PlanPmaBean planPmaBean;

	@EJB
	private PlanPmaFacade planPmaFacade;

	@Getter
	@Setter
	private List<Programa> programas;

	@Getter
	@Setter
	private Programa programa;

	@Getter
	@Setter
	private Programa programaSeleccionado;

	@PostConstruct
	public void init() {
		limpiarPrograma();
		llenarProgramas();
	}

	public void llenarProgramas() {
		programas = new ArrayList<Programa>();
		if (planPmaBean.getPlan().getId() != null)
			programas = planPmaFacade.getProgramasPorPlanId(planPmaBean
					.getPlan().getId());
	}

	public void cleanProgramas() {
		programa = new Programa();
		programas = new ArrayList<Programa>();
	}

	public void agregar() {
		if (programas.contains(this.programa)) {
			this.programas.set(this.programas.lastIndexOf(this.programa),
					programa);
		} else {
			this.programas.add(this.programa);
		}
		limpiarPrograma();
	}

	public void editarPrograma() {
		programas.set(programas.indexOf(programaSeleccionado),
				programaSeleccionado);
		programaSeleccionado = new Programa();
		JsfUtil.addMessageInfo("Programa actualizado correctamente");
		RequestContext.getCurrentInstance().execute(
				"PF('dlgEditarPrograma').hide()");
	}

	public void abrirEditarPrograma() {
		RequestContext.getCurrentInstance().execute(
				"PF('dlgEditarPrograma').show()");
	}

	public void eliminarPrograma(Programa _programa) {
		programas.remove(_programa);
	}

	public void limpiarPrograma() {
		programa = new Programa();
	}
}