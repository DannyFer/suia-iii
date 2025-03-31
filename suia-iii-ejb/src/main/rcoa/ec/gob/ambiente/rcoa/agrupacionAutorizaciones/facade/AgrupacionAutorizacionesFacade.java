package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AgrupacionPrincipal;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.DetalleAgrupacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class AgrupacionAutorizacionesFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardarAgrupacion(AgrupacionPrincipal agrupacion) {
		crudServiceBean.saveOrUpdate(agrupacion);
	}
	
	public void guardarDetalleAgrupacion(DetalleAgrupacion detalle) {
		crudServiceBean.saveOrUpdate(detalle);
	}

	@SuppressWarnings("unchecked")
	public AgrupacionPrincipal getAgrupacionPorProyectoEstado(String proyecto, Integer estado) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("proyecto", proyecto);
		parameters.put("estado", estado);

		try {
			List<AgrupacionPrincipal> lista = (List<AgrupacionPrincipal>) crudServiceBean
					.findByNamedQuery(AgrupacionPrincipal.GET_POR_PROYECTO_ESTADO, parameters);

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
	public List<DetalleAgrupacion> getDetalleAgrupacionPorIdPrincipal(Integer idPrincipal) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idPrincipal", idPrincipal);

		try {
			List<DetalleAgrupacion> lista = (List<DetalleAgrupacion>) crudServiceBean
					.findByNamedQuery(DetalleAgrupacion.GET_POR_ID_AGRUPACION, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public AgrupacionPrincipal getAgrupacionPorId(Integer idAgrupacion) {
		try {
			AgrupacionPrincipal agrupacion = (AgrupacionPrincipal) crudServiceBean.find(AgrupacionPrincipal.class, idAgrupacion);

			if (agrupacion == null) {
				return null;
			} else {
				return agrupacion;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
