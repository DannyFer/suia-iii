package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.dto.EntityProyectoParticipacionSocial;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.PublicarEstudioParticipacionSocialService;
import ec.gob.ambiente.suia.ubicaciongeografica.service.UbicacionGeograficaServiceBean;

@Stateless
public class PublicarEsudioParticipacionSocialFacade {
	
	@EJB
	private PublicarEstudioParticipacionSocialService publicarEstudioParticipacionSocialService;
	
	@EJB
	private UbicacionGeograficaServiceBean ubicacionGeograficaService;
	
	public List<ParticipacionSocialAmbiental> getProyectosParticipacionSocial(){
		return publicarEstudioParticipacionSocialService.getProyectosParticipacionSocial();
	}

	public List<EntityProyectoParticipacionSocial> getProyectosPPS(){
		return publicarEstudioParticipacionSocialService.getProyectosPPSocial();
	}
	
	public List<ProyectoCustom> getProyectosPublicarEstudio(/*Usuario usuario*/){
		return publicarEstudioParticipacionSocialService.getProyectosPublicarEstudio(/*usuario*/);
	}
	
	
	public List<UbicacionesGeografica> getProvincias() {
		return ubicacionGeograficaService.buscarUbicacionGeograficaPorId(1);
	}

	public List<UbicacionesGeografica> getCantonesParroquia(
			UbicacionesGeografica ubicacion) {
		return ubicacionGeograficaService.buscarUbicacionGeograficaPorId(ubicacion.getId());
	}
}
