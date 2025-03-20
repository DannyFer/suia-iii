package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ConsultorForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.EquipoConsultorForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class EquipoConsultorForestalFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public EquipoConsultorForestal guardar(EquipoConsultorForestal consultor) {			
		return crudServiceBean.saveOrUpdate(consultor);
	}
	
	@SuppressWarnings("unchecked")
	public EquipoConsultorForestal getConsultorPrincipal(Integer idInforme) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInformeFactibilidad", idInforme);

		try {
			List<EquipoConsultorForestal> lista = (List<EquipoConsultorForestal>) crudServiceBean
					.findByNamedQuery(EquipoConsultorForestal.GET_PRINCIPAL_POR_INFORME, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<EquipoConsultorForestal> getListaEquipo(Integer idInforme) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInformeFactibilidad", idInforme);

		try {
			List<EquipoConsultorForestal> lista = (List<EquipoConsultorForestal>) crudServiceBean
					.findByNamedQuery(EquipoConsultorForestal.GET_LISTA_EQUIPO_POR_INFORME, parameters);

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
