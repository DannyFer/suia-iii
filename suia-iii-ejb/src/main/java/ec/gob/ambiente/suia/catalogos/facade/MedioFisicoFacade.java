package ec.gob.ambiente.suia.catalogos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.catalogos.service.MedioFisicoService;
import ec.gob.ambiente.suia.domain.MedioFisico;

@Stateless
public class MedioFisicoFacade {

	@EJB
    private MedioFisicoService medioFisicoService;
    

    public MedioFisico medioFisico(int id) {
		return medioFisicoService.medioFisico(id);
	}
    
	public List<MedioFisico> getMediosFisicosSeleccionados(Integer idTdr) {
		return medioFisicoService.mediosFisicosSeleccionados(idTdr);
	}
	
	public List<MedioFisico> listarMediosFisicosPorProyecto(Integer idProyecto) {
		return medioFisicoService.listarMediosFisicosPorProyecto(idProyecto);
	}

	public void adicionarMedioFisico(MedioFisico medioFisico) throws Exception {
		medioFisicoService.adicionarMedioFisico(medioFisico);
	}

	public void eliminarMedioFisico(MedioFisico medioFisico) throws Exception {
		medioFisicoService.eliminarMedioFisico(medioFisico);
	}
}
