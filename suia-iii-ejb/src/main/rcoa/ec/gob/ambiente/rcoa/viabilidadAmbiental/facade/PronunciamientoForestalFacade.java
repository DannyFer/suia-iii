package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoForestal;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class PronunciamientoForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;

	@SuppressWarnings("unchecked")
	public PronunciamientoForestal getInformePorViabilidad(Integer idViabilidad) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idViabilidad", idViabilidad);

		try {
			List<PronunciamientoForestal> lista = (List<PronunciamientoForestal>) crudServiceBean
					.findByNamedQuery(PronunciamientoForestal.GET_POR_VIABILIDAD, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public PronunciamientoForestal guardar(PronunciamientoForestal oficio, Area area, Boolean esFavorable) {
		if(oficio.getNumeroOficio() == null)
			oficio.setNumeroOficio(generarCodigo(area, esFavorable));
			
		return crudServiceBean.saveOrUpdate(oficio);
	}
	
	public PronunciamientoForestal guardar(PronunciamientoForestal oficio) {
		return crudServiceBean.saveOrUpdate(oficio);
	}
	
	private String generarCodigo(Area area,  Boolean esFavorable) {
		String anioActual=secuenciasFacade.getCurrentYear();
		String nombreSecuencia="OFICIO_PRONUNCIAMIENTO_FORESTAL_"+anioActual+"_"+area.getAreaAbbreviation();
		
		try {
			return Constantes.SIGLAS_INSTITUCION + "-SUIA-"
					+ area.getAreaAbbreviation()
					+"-"
					+ anioActual
					+"-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4)
					+ "-O";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public PronunciamientoForestal getInformePorViabilidad(Integer idViabilidad, Integer tipo, Integer numeroRevision) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idViabilidad", idViabilidad);
		parameters.put("tipoOficio", tipo);
		parameters.put("numeroRevision", numeroRevision);

		try {
			List<PronunciamientoForestal> lista = (List<PronunciamientoForestal>) crudServiceBean
					.findByNamedQuery(PronunciamientoForestal.GET_POR_VIABILIDAD_TIPO_OFICIO_REVISION, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public PronunciamientoForestal guardar(PronunciamientoForestal oficio, Area area) {
		if(oficio.getNumeroOficio() == null)
			oficio.setNumeroOficio(generarCodigo(area, false));
			
		return crudServiceBean.saveOrUpdate(oficio);
	}

}
