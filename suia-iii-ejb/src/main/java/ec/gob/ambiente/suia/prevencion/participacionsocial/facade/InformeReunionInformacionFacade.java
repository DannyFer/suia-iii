package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import ec.gob.ambiente.suia.domain.CatalogoMediosParticipacionSocial;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.CatalogoMediosParticipacionSocialService;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.InformeReunionInformacionService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class InformeReunionInformacionFacade {
	
	@EJB
	private InformeReunionInformacionService informeReunionInformacionService;
	
	@EJB
	private CatalogoMediosParticipacionSocialService catalogoMediosParticipacionSocialService;
	
	public List<RegistroMediosParticipacionSocial> getRecordsByProjectId(ParticipacionSocialAmbiental participacionSocialAmbiental) {
		return informeReunionInformacionService.getRecordsByProjectId(participacionSocialAmbiental);
	}
	
	public RegistroMediosParticipacionSocial guardarMedioVerificacion(RegistroMediosParticipacionSocial registroMediosParticipacionSocial) {
		return informeReunionInformacionService.guardarMedioVerificacion(registroMediosParticipacionSocial);
	}
	
	public RegistroMediosParticipacionSocial eliminarMedioVerificacion(RegistroMediosParticipacionSocial registroMediosParticipacionSocial) {
		return informeReunionInformacionService.eliminarMedioVerificacion(registroMediosParticipacionSocial);
	}
	
	public List<CatalogoMediosParticipacionSocial> getCatalogoMediosParticipacionSocial() {
		return catalogoMediosParticipacionSocialService.buscarCatalogoMediosParticipacionSocialPorId(2);
	}
	
}
