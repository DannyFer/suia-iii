package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DelegadoOperadorForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DelegadoTecnicoForestal;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class DelegadosInspeccionForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	public void guardarDelegado(DelegadoOperadorForestal delegado) {
		crudServiceBean.saveOrUpdate(delegado);
	}
	
	public void guardarTecnicoDelegado(DelegadoTecnicoForestal delegado) {
		crudServiceBean.saveOrUpdate(delegado);
	}
	
	@SuppressWarnings("unchecked")
	public List<DelegadoOperadorForestal> getDelegadosOperador(Integer idInforme) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInforme", idInforme);

		try {
			List<DelegadoOperadorForestal> lista = (List<DelegadoOperadorForestal>) crudServiceBean
					.findByNamedQuery(DelegadoOperadorForestal.GET_POR_INFORME, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DelegadoTecnicoForestal> getDelegadosTecnicos(Integer idInforme) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInforme", idInforme);

		try {
			List<DelegadoTecnicoForestal> lista = (List<DelegadoTecnicoForestal>) crudServiceBean
					.findByNamedQuery(DelegadoTecnicoForestal.GET_POR_INFORME, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
}
