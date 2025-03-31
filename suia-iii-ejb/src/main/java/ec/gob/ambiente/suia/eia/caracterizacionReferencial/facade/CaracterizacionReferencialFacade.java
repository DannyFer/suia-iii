package ec.gob.ambiente.suia.eia.caracterizacionReferencial.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.eia.caracterizacionReferencial.service.CaracterizacionReferencialService;

@Stateless
public class CaracterizacionReferencialFacade {

	@EJB
	CaracterizacionReferencialService caracterizacionReferencialService;

	public void guardar(List<CoordenadaGeneral> coordenadasGenerales,
			List<List<Object>> eliminados) throws Exception {
		caracterizacionReferencialService.guardar(coordenadasGenerales,
				eliminados);
	}

	public List<CoordenadaGeneral> obtenerCoordenadas(Integer eiaId,
			String tabla) throws Exception {
		return caracterizacionReferencialService.obtenerCoordenadas(eiaId,
				tabla);
	}
}
