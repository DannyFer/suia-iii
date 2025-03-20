package ec.gob.ambiente.suia.tramiteresolver;

import java.util.ArrayList;
import java.util.List;

import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.tramiteresolver.base.TramiteResolverClass;
import ec.gob.ambiente.suia.tramiteresolver.classes.LabelModel;

public class AprobacionRequisitosTecnicosTramiteResolver extends TramiteResolverClass<AprobacionRequisitosTecnicos> {

	public static final String URL_VER_APROBACION_REQUISITOS_TECNICO = "/control/aprobacionRequisitosTecnicos/defaultAnalisisRevision.jsf";

	@Override
	public String getNombreVariableResolverTramite() {
		return AprobacionRequisitosTecnicos.VARIABLE_NUMERO_SOLICITUD;
	}

	@Override
	public String getTramiteAsString() {
		//if (tramite.isIniciadoPorNecesidad()) {
			return this.tramite.getSolicitud();
		//} else {
		//	return this.tramite.getProyecto();
		//}

	}

	@Override
	public void setTramite(Object valueVariableResolverTramite) throws Exception {
		String solicitud = valueVariableResolverTramite.toString();
		this.tramite = getFachada(AprobacionRequisitosTecnicosFacade.class)
				.getAprobacionRequisitosTecnicosPorSolicitud(solicitud);
	}

	@Override
	public Usuario getUsuarioProponente() {
		return tramite.getUsuario();
	}

	@Override
	public List<LabelModel> getCamposMostrarTramite() {
		List<LabelModel> campos = new ArrayList<LabelModel>();
		campos.add(new LabelModel("No. solicitud", tramite.getSolicitud()));
		campos.add(new LabelModel("Área responsable", tramite.getAreaResponsable()));
		campos.add(new LabelModel("Proyecto", tramite.getProyecto()));
		return campos;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ec.gob.ambiente.suia.tramiteresolver.base.TramiteResolverClass#
	 * getUrlToShowTramite()
	 */
	@Override
	public String getUrlToShowTramite() {
		return URL_VER_APROBACION_REQUISITOS_TECNICO;
	}

	@Override
	public Object endOperation(Object object) {
		return null;
	}

}
