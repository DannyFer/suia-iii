package ec.gob.ambiente.suia.tramiteresolver;

import java.util.ArrayList;
import java.util.List;

import ec.gob.ambiente.control.inspeccionescontrolambiental.bean.VerSolicitudInspeccionControlBean;
import ec.gob.ambiente.suia.control.inspeccionescontrolambiental.facade.InspeccionControlAmbientalFacade;
import ec.gob.ambiente.suia.domain.SolicitudInspeccionControlAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.tramiteresolver.base.TramiteResolverClass;
import ec.gob.ambiente.suia.tramiteresolver.classes.LabelModel;
import ec.gob.ambiente.suia.utils.JsfUtil;

public class SolicitudInspeccionControlAmbientalTramiteResolver
		extends TramiteResolverClass<SolicitudInspeccionControlAmbiental> {

	@Override
	public String getTramiteAsString() {
		return this.tramite.getSolicitud();
	}

	@Override
	public String getNombreVariableResolverTramite() {
		return SolicitudInspeccionControlAmbiental.VARIABLE_NUMERO_SOLICITUD;
	}

	@Override
	public void setTramite(Object valueVariableResolverTramite) throws Exception {
		String solicitud = valueVariableResolverTramite.toString();
		this.tramite = getFachada(InspeccionControlAmbientalFacade.class).get(solicitud);
	}

	@Override
	public Usuario getUsuarioProponente() {
		return tramite.getUsuario();
	}

	@Override
	public List<LabelModel> getCamposMostrarTramite() {
		List<LabelModel> campos = new ArrayList<LabelModel>();
		campos.add(new LabelModel("No. solicitud", tramite.getSolicitud()));
		campos.add(
				new LabelModel("Motivo de la solictud", tramite.getCronogramaAnualInspeccionesControlAmbiental() != null
						? "Actividad del cronograma anual de inspecciones" : "Solicitud de inspección directa"));
		campos.add(new LabelModel("Proyecto", tramite.getProyecto().getCodigo()));
		campos.add(new LabelModel("Área responsable", tramite.getAreaResponsable()));
		return campos;
	}

	@Override
	public String getUrlToShowTramite() {
		return VerSolicitudInspeccionControlBean.URL_VER_SOLICITUD_INSPECCION;
	}

	@Override
	public Object endOperation(Object object) {
		JsfUtil.getBean(VerSolicitudInspeccionControlBean.class).actionVerSolicitudInspeccionControl(tramite.getId());
		return null;
	}

}
