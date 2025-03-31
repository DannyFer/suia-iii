package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoBiodiversidad;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class PronunciamientoBiodiversidadFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;

	@SuppressWarnings("unchecked")
	public PronunciamientoBiodiversidad getInformePorViabilidad(Integer idViabilidad) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idViabilidad", idViabilidad);

		try {
			List<PronunciamientoBiodiversidad> lista = (List<PronunciamientoBiodiversidad>) crudServiceBean
					.findByNamedQuery(PronunciamientoBiodiversidad.GET_POR_VIABILIDAD, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public void guardar(PronunciamientoBiodiversidad oficio, Area area, Boolean esFavorable) {
		if(oficio.getNumeroOficio() == null)
			oficio.setNumeroOficio(generarCodigo(area, esFavorable));
			
		crudServiceBean.saveOrUpdate(oficio);
	}
	
	public void guardar(PronunciamientoBiodiversidad oficio) {
		crudServiceBean.saveOrUpdate(oficio);
	}
	
	private String generarCodigo(Area area,  Boolean esFavorable) {
		String anioActual=secuenciasFacade.getCurrentYear();
		String nombreSecuencia="OFICIO_PRONUNCIAMIENTO_SNAP"+anioActual+"_"+area.getAreaAbbreviation();
		
		try {
			return secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4)
					+ "-"
					+ area.getAreaAbbreviation()
					+ "-"
					+ anioActual
					+ "-SUIA-" + Constantes.SIGLAS_INSTITUCION;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
