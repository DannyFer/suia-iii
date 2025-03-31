package ec.gob.ambiente.control.denuncias.controllers;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.denuncias.bean.DenunciaBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.bandeja.controllers.BandejaTareasController;
import ec.gob.ambiente.suia.control.denuncia.facade.DenunciaFacade;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.Serializable;

@ViewScoped
@ManagedBean
public class DenunciaController implements Serializable{
    
    private static final long serialVersionUID = -3526571287113629686L;

	@Getter
	@Setter
	@ManagedProperty(value = "#{denunciaBean}")
	private DenunciaBean denunciaBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private DenunciaFacade denunciaFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	private static final Logger LOG = Logger
			.getLogger(BandejaTareasController.class);

	@PostConstruct
	private void init() {
		denunciaBean.setProvincias(denunciaFacade.getProvincias());
		cargarCantones();		

	}

	public void subirArchivo() {
		if (denunciaBean.getFile() != null) {
			// TODO:	Implementar este metodo!		
		}
	}

	private void validarUbicacionSeleccionada() {
		UbicacionesGeografica ubicacion = denunciaBean.getParroquia() == null ? (denunciaBean
				.getCanton() == null ? denunciaBean.getParroquia()
				: denunciaBean.getCanton()) : denunciaBean.getParroquia();

		denunciaBean.getDenuncia().setUbicacion(ubicacion);
	}

	public String aceptarDenuncia() {
		try {
			validarUbicacionSeleccionada();					
			denunciaFacade.aceptarDenuncia(denunciaBean.getDenuncia(),
					denunciaBean.getDenunciante(),
					loginBean.getUsuario());
			JsfUtil.addMessageInfo("Denuncia guarda exitosamente");
			return JsfUtil.actionNavigateTo("../../start.jsf");
		} catch (JbpmException e) {
			LOG.error("Error al iniciar el proceso", e);
			JsfUtil.addMessageError("Ocurrio un error al intentar iniciar el proceso");
			return "#";
		}
	}

	public void rechazarDenuncia() {
		validarUbicacionSeleccionada();
		denunciaFacade.rechazarDenuncia(denunciaBean.getDenuncia());
	}

	public void cargarCantones() {
		if (denunciaBean.getProvincia() != null)
			denunciaBean.setCantones(denunciaFacade
					.getCantonesParroquia(denunciaBean.getProvincia()));
	}

	public void cargarParroquias() {
		denunciaBean.setParroquias(denunciaFacade
				.getCantonesParroquia(denunciaBean.getCanton()));
	}
}
