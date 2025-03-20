package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbientalComentarios;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.ParticipacionSocialAmbientalComentariosService;

@Stateless
public class ParticipacionSocialAmbientalComentariosFacade {
	
	@EJB
	private ParticipacionSocialAmbientalComentariosService participacionSocialAmbientalComentariosService;
	
	public ParticipacionSocialAmbientalComentarios guardarComentario(ParticipacionSocialAmbientalComentarios participacionSocialAmbientalComentarios) {
		participacionSocialAmbientalComentarios.setFecha(new Date());
		return participacionSocialAmbientalComentariosService.guardar(participacionSocialAmbientalComentarios);
	}
	
	public List<ParticipacionSocialAmbientalComentarios> getCommentsByProjectId(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) {		
		return participacionSocialAmbientalComentariosService.getCommentsByProjectId(proyectoLicenciamientoAmbiental);
	}
	
}
