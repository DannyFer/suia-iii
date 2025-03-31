package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.CatalogoMediosParticipacionSocial;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.CatalogoMediosParticipacionSocialService;

@Stateless
public class IngresoMediosVerificacionFacade {
	
	@EJB
	private CatalogoMediosParticipacionSocialService catalogoMediosParticipacionSocialServicesBean;
	
	public List<CatalogoMediosParticipacionSocial> getCatalogoMediosParticipacionSocial() {
		return catalogoMediosParticipacionSocialServicesBean.buscarCatalogoMediosParticipacionSocialPorId(3);
	}
	
}
