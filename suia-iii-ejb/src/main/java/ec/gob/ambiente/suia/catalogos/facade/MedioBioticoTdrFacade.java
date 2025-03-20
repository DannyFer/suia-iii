package ec.gob.ambiente.suia.catalogos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.catalogos.service.MedioBioticoTdrService;
import ec.gob.ambiente.suia.domain.MedioBioticoTdr;

@Stateless
public class MedioBioticoTdrFacade {

	@EJB
	private MedioBioticoTdrService medioBioticoTdrService;
	

	public MedioBioticoTdr medioBiotico(int id) {
		return medioBioticoTdrService.medioBiotico(id);
	}
	
	public List<MedioBioticoTdr> getMediosBioticosSeleccionados(Integer idTdr) {
		return medioBioticoTdrService.mediosBioticosSeleccionados(idTdr);
	}
	
	public List<MedioBioticoTdr> listarMediosBioticosPorProyecto(Integer idProyecto) {
		return medioBioticoTdrService.listarMediosBioticosPorProyecto(idProyecto);
	}

	public void adicionarMedioBiotico(MedioBioticoTdr MedioBioticoTdr) throws Exception {
		medioBioticoTdrService.adicionarMedioBioticoTdr(MedioBioticoTdr);
	}

	public void eliminarMedioBiotico(MedioBioticoTdr MedioBioticoTdr) throws Exception {
		medioBioticoTdrService.adicionarMedioBioticoTdr(MedioBioticoTdr);
	}
}
