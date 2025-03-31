package ec.gob.ambiente.control.documentos.bean;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.service.InformeTecnicoService;
import ec.gob.ambiente.suia.domain.InformeTecnico;
import ec.gob.ambiente.suia.login.bean.LoginBean;

@ManagedBean
@SessionScoped
public class InformeTecnicoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1378474059892092143L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(InformeTecnicoBean.class);

	@Getter
	@Setter
	private InformeTecnico informeTecnico;

	@EJB
	private InformeTecnicoService informeTecnicoService;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	private boolean habilitarComentarios;

	@Getter
	@Setter
	private boolean habilitarEdicion;

	public InformeTecnicoBean() {
		informeTecnico = new InformeTecnico();
	}
}
