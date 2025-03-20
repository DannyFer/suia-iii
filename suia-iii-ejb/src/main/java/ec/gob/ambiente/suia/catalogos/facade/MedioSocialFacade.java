package ec.gob.ambiente.suia.catalogos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.catalogos.service.MedioSocialService;
import ec.gob.ambiente.suia.domain.MedioSocial;

@Stateless
public class MedioSocialFacade {

	@EJB
    private MedioSocialService medioSocialService;
    

    public MedioSocial medioSocial(int id) {
		return medioSocialService.MedioSocial(id);
	}
    
	public List<MedioSocial> getMediosSocialesSeleccionados(Integer idTdr) {
		return medioSocialService.mediosSocialesSeleccionados(idTdr);
	}
	
	public List<MedioSocial> listarMediosSocialesPorProyecto(Integer idProyecto) {
		return medioSocialService.listarMediosSocialesPorProyecto(idProyecto);
	}

	public void adicionarmedioSocial(MedioSocial medioSocial) throws Exception {
		medioSocialService.adicionarMedioSocial(medioSocial);
	}

	public void eliminarmedioSocial(MedioSocial medioSocial) throws Exception {
		medioSocialService.eliminarMedioSocial(medioSocial);
	}
}
