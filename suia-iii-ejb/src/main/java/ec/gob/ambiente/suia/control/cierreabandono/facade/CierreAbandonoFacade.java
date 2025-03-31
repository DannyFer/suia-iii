/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.control.cierreabandono.facade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.control.cierreabandono.PlanCierreAbandonoVariables;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.PlanCierre;
import ec.gob.ambiente.suia.domain.PlanCierreDocumentos;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 06/01/2014]
 *          </p>
 */
@Stateless
public class CierreAbandonoFacade {

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private CrudServiceBean crudServiceBean;

	public PlanCierre cargarRegistro(int id) {
		PlanCierre planCierre = crudServiceBean.find(PlanCierre.class, id);
		for (PlanCierreDocumentos pcd : planCierre.getPlanesCierreDocumentos()) {
			pcd.getDocumento().getId();
		}
		return planCierre;
	}

	public void salvarPlanCierre(Usuario usuario, long processId, PlanCierre planCierre,
			Map<String, List<Documento>> documentos) throws JbpmException {
		Integer idProyecto = Integer.valueOf(procesoFacade.recuperarVariablesProceso(usuario, processId)
				.get(PlanCierreAbandonoVariables.idProyecto.name()).toString());

		ProyectoLicenciamientoAmbiental proyecto = crudServiceBean.find(ProyectoLicenciamientoAmbiental.class,
				idProyecto);
		List<PlanCierreDocumentos> docs = new ArrayList<PlanCierreDocumentos>();
		planCierre.setPlanesCierreDocumentos(docs);
		planCierre.setProyecto(proyecto);
		crudServiceBean.saveOrUpdate(planCierre);
		Iterator<String> it = documentos.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			List<Documento> list = documentos.get(key);
			for (Documento documento : list) {
				PlanCierreDocumentos planCierreDocumentos = new PlanCierreDocumentos();
				planCierreDocumentos.setDocumento(documento);
				planCierreDocumentos.setPlanCierre(planCierre);
				planCierreDocumentos.setTipoSeccion(key);
				docs.add(planCierreDocumentos);

				crudServiceBean.saveOrUpdate(documento);
				crudServiceBean.saveOrUpdate(planCierreDocumentos);
			}
		}

		procesoFacade.modificarVariablesProceso(usuario, processId, null);

		long taskId = procesoFacade.recuperarIdTareaActual(usuario, processId);
		procesoFacade.aprobarTarea(usuario, taskId, processId, null);
	}

	public void saveOrUpdate(PlanCierre planCierre) {
		crudServiceBean.saveOrUpdate(planCierre);
	}

	public void delete(PlanCierre planCierre) {
		crudServiceBean.delete(planCierre);
	}
}
