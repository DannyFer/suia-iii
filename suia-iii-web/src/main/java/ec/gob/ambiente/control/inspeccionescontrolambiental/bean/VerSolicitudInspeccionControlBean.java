package ec.gob.ambiente.control.inspeccionescontrolambiental.bean;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.control.inspeccionescontrolambiental.facade.InspeccionControlAmbientalFacade;
import ec.gob.ambiente.suia.domain.SolicitudInspeccionControlAmbiental;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class VerSolicitudInspeccionControlBean implements Serializable {

	private static final long serialVersionUID = -750153308035825981L;

	private static final Logger LOG = Logger.getLogger(VerSolicitudInspeccionControlBean.class);

	public static final String URL_VER_SOLICITUD_INSPECCION = "/control/inspeccionescontrolambiental/solicitudInspeccionControlAmbientalVer.jsf";

	@EJB
	private InspeccionControlAmbientalFacade inspeccionControlAmbientalFacade;

	@Getter
	private SolicitudInspeccionControlAmbiental solicitud;

	public String actionVerSolicitudInspeccionControl(Integer id) {
		try {
			return this
					.actionVerSolicitudInspeccionControl(inspeccionControlAmbientalFacade.cargarSolicitudFullPorId(id));
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			LOG.error("Error al cargar el registro de generador de desechos", e);
			return "";
		}
	}

	public String actionVerSolicitudInspeccionControl(SolicitudInspeccionControlAmbiental solicitud) {
		this.executeView(solicitud, false);
		return JsfUtil.actionNavigateTo(URL_VER_SOLICITUD_INSPECCION);
	}

	public void redirectVerSolicitudInspeccionControl(Integer id, boolean habilitarObservaciones,
			boolean observacionesSoloLectura, boolean mostrarComoTarea) {
		try {
			this.redirectVerSolicitudInspeccionControl(inspeccionControlAmbientalFacade.cargarSolicitudFullPorId(id));
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			LOG.error("Error al cargar la solicitud de inspeccion de control ambiental", e);
		}
	}

	public void redirectVerSolicitudInspeccionControl(SolicitudInspeccionControlAmbiental solicitud) {
		this.executeView(solicitud, true);
	}

	public void executeView(SolicitudInspeccionControlAmbiental solicitud, boolean redirect) {
		this.solicitud = solicitud;
		if (redirect)
			JsfUtil.redirectTo(URL_VER_SOLICITUD_INSPECCION);
	}
}
