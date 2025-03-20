package ec.gob.ambiente.suia.tramiteresolver;

import java.util.ArrayList;
import java.util.List;

import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.VerRegistroGeneradorDesechoBean;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.tramiteresolver.base.TramiteResolverClass;
import ec.gob.ambiente.suia.tramiteresolver.classes.LabelModel;
import ec.gob.ambiente.suia.utils.JsfUtil;

public class RegistroGeneradorTramiteResolver extends TramiteResolverClass<GeneradorDesechosPeligrosos> {

	@Override
	public String getTramiteAsString() {
		return this.tramite.getSolicitud();
	}

	@Override
	public String getNombreVariableResolverTramite() {
		return GeneradorDesechosPeligrosos.VARIABLE_NUMERO_SOLICITUD;
	}

	@Override
	public void setTramite(Object valueVariableResolverTramite) throws Exception {
		String solicitud = valueVariableResolverTramite.toString();
		this.tramite = getFachada(RegistroGeneradorDesechosFacade.class).get(solicitud, JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea().getId());
		this.tramite = getFachada(RegistroGeneradorDesechosFacade.class).getUltimoPorSolicitud(solicitud); //Para visualizar la información se recupera el ultimo RGD no el primero. 
	}

	@Override
	public Usuario getUsuarioProponente() {
		return tramite.getUsuario();
	}

	@Override
	public List<LabelModel> getCamposMostrarTramite() {
		List<LabelModel> campos = new ArrayList<LabelModel>();
		campos.add(new LabelModel("No. solicitud", tramite.getSolicitud()));
		if(tramite.getTipoSector() != null)
		campos.add(new LabelModel("Sector", tramite.getTipoSector() != null ? tramite.getTipoSector() : tramite
				.getProyecto().getTipoSector()));
		campos.add(new LabelModel("Área responsable", tramite.getAreaResponsable()));
		return campos;
	}

	@Override
	public String getUrlToShowTramite() {
		return VerRegistroGeneradorDesechoBean.URL_VER_GENERADOR_DESECHOS;
	}

	@Override
	public Object endOperation(Object object) {
		JsfUtil.getBean(VerRegistroGeneradorDesechoBean.class).actionVerGeneradorDesechosPeligrosos(tramite.getId(),
				true, false, false);
		return null;
	}

}
