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

import ec.gob.ambiente.suia.domain.EquipoAccesorioPma;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * 
 * @author karla.carvajal
 * 
 */
@ManagedBean
@ViewScoped
public class EquipoAccesorioPmaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Setter
	@ManagedProperty(value = "#{fichaAmbientalPmaBean}")
	private FichaAmbientalPmaBean fichaAmbientalPmaBean;

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@Getter
	@Setter
	private EquipoAccesorioPma equipo;

	@Getter
	@Setter
	private EquipoAccesorioPma equipoSeleccionado;

	@Getter
	@Setter
	private List<EquipoAccesorioPma> equipos;

	@PostConstruct
	public void init() {
		limpiarEquipoAccesorio();
		llenarEquiposAccesorios();
	}

	public void llenarEquiposAccesorios() {
		equipos = new ArrayList<EquipoAccesorioPma>();
		if (fichaAmbientalPmaBean.getFicha() != null
				&& fichaAmbientalPmaBean.getFicha().getId() != null)
			equipos = fichaAmbientalPmaFacade
					.getEquiposAccesoriosPorFichaId(fichaAmbientalPmaBean
							.getFicha().getId());
	}

	public void limpiarEquipoAccesorio() {
		equipo = new EquipoAccesorioPma();
	}

	public void agregar() {
		if (equipos.contains(this.equipo)) {
			this.equipos.set(this.equipos.lastIndexOf(this.equipo), equipo);
		} else {
			this.equipos.add(this.equipo);
		}
		limpiarEquipoAccesorio();
	}

	public void abrirEditarEquipo() {
		RequestContext.getCurrentInstance().execute(
				"PF('dlgEditarEquipo').show()");
	}

	public void editarEquipo() {
		equipos.set(equipos.indexOf(equipoSeleccionado), equipoSeleccionado);
		equipoSeleccionado = new EquipoAccesorioPma();
		JsfUtil.addMessageInfo("Equipo actualizado correctamente");
		RequestContext.getCurrentInstance().execute(
				"PF('dlgEditarEquipo').hide()");
	}

	public void eliminarEquipo(EquipoAccesorioPma _equipo) {
		equipos.remove(_equipo);
	}
}