package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.CatalogoMediosParticipacionSocial;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.CatalogoMediosParticipacionSocialService;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.MediosVerificacionService;

@Stateless
public class MediosVerificacionFacade {
	
	@EJB
	private MediosVerificacionService mediosVerificacionService;
	
	@EJB
	private CatalogoMediosParticipacionSocialService catalogoMediosParticipacionSocialService;
	
	public List<RegistroMediosParticipacionSocial> getRegistrosMediosPPS() {
		return mediosVerificacionService.getRegistrosMediosPPS();
	}
	
	public List<RegistroMediosParticipacionSocial> getRecordsByProjectId(ParticipacionSocialAmbiental participacionSocialAmbiental) {
		return mediosVerificacionService.getRecordsByProjectId(participacionSocialAmbiental);
	}
	
	public RegistroMediosParticipacionSocial guardarMedioVerificacion(RegistroMediosParticipacionSocial registroMediosParticipacionSocial) {
		return mediosVerificacionService.guardarMedioVerificacion(registroMediosParticipacionSocial);
	}
	
	public RegistroMediosParticipacionSocial eliminarMedioVerificacion(RegistroMediosParticipacionSocial registroMediosParticipacionSocial) {
		return mediosVerificacionService.eliminarMedioVerificacion(registroMediosParticipacionSocial);
	}
	
	public List<CatalogoMediosParticipacionSocial> getCatalogoMediosParticipacionSocial() {
		return catalogoMediosParticipacionSocialService.buscarCatalogoMediosParticipacionSocialPorId(3);
	}
	
}
