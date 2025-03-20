/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.procesoadministrativo.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 18/03/2015]
 *          </p>
 */
@Stateless
public class ProcesoAdministrativoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private IntegracionFacade integracionFacade;

	/*@SuppressWarnings("unchecked")
	public List<ProcesoAdministrativo> getProcesosAdministrativos() throws Exception {
		return (List<ProcesoAdministrativo>) crudServiceBean.findAll(ProcesoAdministrativo.class);
	}*/
	
	public int cantidadProcesoAdministrativosPorPersona(String usuarioAutenticado) throws Exception {
		return integracionFacade.getAdministrativeProcessByUser(usuarioAutenticado);
	}

	/*@SuppressWarnings("unchecked")
	private List<ProcesoAdministrativo> getProcesoAdministrativosPorProcedimientoAfectacion(int idProcedimientoAfectacion)
			throws Exception {
		Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
		parametros.put("idProcedimientoAfectacion", idProcedimientoAfectacion);
		return (List<ProcesoAdministrativo>) crudServiceBean.findByNamedQuery(
				ProcesoAdministrativo.GET_ENTE_RECEPTOR_PROCESO_ADMINISTRATIVO_POR_TIPO, parametros);
	}

	public int cantidadProcesoAdministrativosPorPersona(Persona usuarioAutenticado) throws Exception {
		int contador = 0;
		List<ProcesoAdministrativo> procesosAdministrativosPersonas = getProcesoAdministrativosPorProcedimientoAfectacion(ProcedimientoAfectacion.PERSONA);
		for (int i = 0; i < procesosAdministrativosPersonas.size(); i++) {
			if (procesosAdministrativosPersonas.get(i).getPersona().equals(usuarioAutenticado)) {
				contador++;
			}
		}
		return contador;
	}*/
}
