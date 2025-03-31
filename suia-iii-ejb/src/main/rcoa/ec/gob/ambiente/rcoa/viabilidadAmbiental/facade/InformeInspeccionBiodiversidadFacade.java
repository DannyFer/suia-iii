package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionBiodiversidad;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformeInspeccionBiodiversidadFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private SecuenciasFacade secuenciasFacade;

	@SuppressWarnings("unchecked")
	public InformeInspeccionBiodiversidad getInformePorViabilidad(Integer idViabilidad) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idViabilidad", idViabilidad);

		try {
			List<InformeInspeccionBiodiversidad> lista = (List<InformeInspeccionBiodiversidad>) crudServiceBean
					.findByNamedQuery(InformeInspeccionBiodiversidad.GET_POR_VIABILIDAD, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public InformeInspeccionBiodiversidad guardar(InformeInspeccionBiodiversidad informe, String siglaArea) {
		if (informe.getNumeroInforme() == null)
			informe.setNumeroInforme(generarCodigo(siglaArea));

		return crudServiceBean.saveOrUpdate(informe);
	}

	private String generarCodigo(String siglaArea) {
		String anioActual = secuenciasFacade.getCurrentYear();
		String nombreSecuencia = "INFORME_INSPECCION_SNAP_" + siglaArea + "_" + anioActual;

		try {
			return secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4) + "-" + siglaArea + "-"
					+ anioActual + "-SUIA-" + Constantes.SIGLAS_INSTITUCION;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
