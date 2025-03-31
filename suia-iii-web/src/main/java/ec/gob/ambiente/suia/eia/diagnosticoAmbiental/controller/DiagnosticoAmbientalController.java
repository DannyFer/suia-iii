package ec.gob.ambiente.suia.eia.diagnosticoAmbiental.controller;

import javax.ejb.EJB;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.domain.DiagnosticoAmbiental;
import ec.gob.ambiente.suia.eia.diagnosticoAmbiental.bean.DiagnosticoAmbientalBean;
import ec.gob.ambiente.suia.eia.diagnosticoAmbiental.facade.DiagnosticoAmbientalFacade;

public class DiagnosticoAmbientalController {

	private static final Logger LOG = Logger.getLogger(DiagnosticoAmbientalController.class);

	@EJB
	DiagnosticoAmbientalFacade diagnosticoAmbientalFacade;
	
	public void guardarDiagnostico(DiagnosticoAmbientalBean diagnosticoAmbientalBean) throws Exception{
		try {
			DiagnosticoAmbiental diagnosticoAmbiental = new DiagnosticoAmbiental();
			diagnosticoAmbiental.setAnexoDiagnostico(diagnosticoAmbientalBean.getAnexoDiagnostico());
			diagnosticoAmbiental.setAnexoDiagnosticoContenType(diagnosticoAmbientalBean.getAnexoDiagnosticoContenType());
			diagnosticoAmbiental.setAnexoDiagnosticoName(diagnosticoAmbientalBean.getAnexoDiagnosticoName());
			diagnosticoAmbiental.setEstado(true);
			diagnosticoAmbiental.setId(diagnosticoAmbientalBean.getId());
			diagnosticoAmbiental.setProblematicaCultura(diagnosticoAmbientalBean.getProblematicaCultural());
			diagnosticoAmbiental.setProblematicaMedioBiotico(diagnosticoAmbientalBean.getProblematicaMedioBiotico());
			diagnosticoAmbiental.setProblematicaMedioFisico(diagnosticoAmbientalBean.getProblematicaMedioFisico());
			diagnosticoAmbiental.setArchivoEditado(diagnosticoAmbientalBean.isArchivoEditado());
			diagnosticoAmbiental.setEiaId(diagnosticoAmbientalBean.getEiaId());
			
			diagnosticoAmbientalFacade.guardarDiagnostico(diagnosticoAmbiental);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e);
		}
	}
	
	public DiagnosticoAmbiental obtenerDiagnostico(Integer id) throws Exception{
		try {
			DiagnosticoAmbiental diagnosticoAmbiental = diagnosticoAmbientalFacade.obtenerDiagnostico(id);
			return diagnosticoAmbiental;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e);
		}
	}
}
