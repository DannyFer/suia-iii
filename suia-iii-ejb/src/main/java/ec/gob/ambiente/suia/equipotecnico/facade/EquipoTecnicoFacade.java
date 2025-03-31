/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.equipotecnico.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EquipoTecnico;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author frank torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: frank torres, Fecha: 02/02/2015]
 *          </p>
 */
@Stateless
public class EquipoTecnicoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public EquipoTecnico equipoTecnico(int id) {
		return crudServiceBean.find(EquipoTecnico.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<EquipoTecnico> getEquipoTecnicoPorIdTdr(Integer idTdr) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idTdr", idTdr);
		return (List<EquipoTecnico>) crudServiceBean.findByNamedQuery(
				EquipoTecnico.FIND_BY_TDR, parameters);

	}
	
	public void eliminarEquipoTecnico(EquipoTecnico equipo) throws Exception{
		crudServiceBean.delete(equipo);
	}
	
	public void adicionarEquipoTecnico(EquipoTecnico tecnicoActivo) throws Exception{
		crudServiceBean.saveOrUpdate(tecnicoActivo);
	}
}
