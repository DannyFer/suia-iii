package ec.gob.ambiente.suia.eia.ficha.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ConsultorNoCalificado;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;

/**
 * 
 * @author lili
 *
 */

@Stateless
public class ConsultorNoCalificadoService implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1822934317185435634L;
	
	@EJB
	private CrudServiceBean crudServiceBean;

	
	/**
	 * guardar consultor no calificado
	 * @param consultorNoCalificado
	 * @throws Exception
	 */
	public void guardarConsultorNoCalificado(ConsultorNoCalificado consultorNoCalificado) throws Exception {
		crudServiceBean.saveOrUpdate(consultorNoCalificado);
	}

	/**
	 * eliminar consultor no calificado
	 * @param consultorNoCalificado
	 * @throws Exception
	 */
	public void eliminarConsultorNoCalificado(ConsultorNoCalificado consultorNoCalificado) throws Exception {
		crudServiceBean.delete(consultorNoCalificado);
	}
	
	/**
	 * obtener consulor no calificado por id
	 * @param id
	 * @return
	 */
	public ConsultorNoCalificado consultorNoCalificado(int id) throws Exception{
		return crudServiceBean.find(ConsultorNoCalificado.class, id);
	}
	
	/**
	 * Lista los consultores no calificados por eia
	 * @param estudio
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ConsultorNoCalificado> consultoresNoCalificadosPorEia(EstudioImpactoAmbiental estudio) throws Exception{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", estudio.getId());
		return (List<ConsultorNoCalificado>) crudServiceBean.findByNamedQuery(ConsultorNoCalificado.FIND_BY_ESTUDIO, parameters);
	}
	
}
