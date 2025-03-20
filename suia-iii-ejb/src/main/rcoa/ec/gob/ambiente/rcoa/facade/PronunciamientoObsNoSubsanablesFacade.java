package ec.gob.ambiente.rcoa.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.PronunciamientoObservacionesNoSubsanables;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;


@Stateless
public class PronunciamientoObsNoSubsanablesFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;

	private String generarCodigo(Area area) {
		try {			
			String nombreSecuencia = "OFICIO_OBSERVACIONES_NO_SUBSANABLES_"
					+ area.getAreaAbbreviation() + "_"
					+ secuenciasFacade.getCurrentYear();
			String secuencia = Constantes.SIGLAS_INSTITUCION + "-" + area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
			if(area.getTipoArea().getId()==3)
				secuencia = area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
			
			return secuencia
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public PronunciamientoObservacionesNoSubsanables guardar(PronunciamientoObservacionesNoSubsanables oficio, Area area){
		if(oficio.getCodigo()==null)
			oficio.setCodigo(generarCodigo(area));		
		return crudServiceBean.saveOrUpdate(oficio);
	}

	public PronunciamientoObservacionesNoSubsanables obtenerPorProyecto(Integer idProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);

		try {
			List<PronunciamientoObservacionesNoSubsanables> lista = (List<PronunciamientoObservacionesNoSubsanables>) crudServiceBean
					.findByNamedQuery(PronunciamientoObservacionesNoSubsanables.GET_POR_PROYECTO, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}	
	}

}
