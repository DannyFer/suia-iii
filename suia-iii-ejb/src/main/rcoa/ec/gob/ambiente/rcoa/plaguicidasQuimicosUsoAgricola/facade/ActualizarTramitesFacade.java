package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class ActualizarTramitesFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();

	public int actualizarInfoProducto() {

		String sql = " UPDATE chemical_pesticides.products_pqa t1  "
				+ " SET geca_id_product_type_final = t2.geca_id_product_type, "
				+ " geca_id_container_type_final = t2.geca_id_container_type, "
				+ " geca_id_toxicological_category_final = t2.geca_id_toxicological_category, "
				+ " geca_id_stripe_color_final = t2.geca_id_stripe_color , prod_observations_bdd = ' Informacion actualizada automaticamente'"
				+ " FROM chemical_pesticides.pesticide_project t2 "
				+ " WHERE t1.prod_id = t2.prod_id";
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public Date actualizarTramite(String codigoProyecto) {

		Date fechaEnvio = null;

		String sqltaskbpmhyd = "select * from dblink('" + dblinkBpmsSuiaiii + "',"
				+ "'select processinstanceid from variableinstancelog where value=''" + codigoProyecto+ "'' "
				+ " and variableid = ''tramite'' "
				+ " and processid = ''rcoa.ActualizacionEtiquetadoPlaguicidasUsoAgricola'' ') as (id integer)";
		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);

		List<Object> resultPro = new ArrayList<Object>();
		resultPro = queryProceso.getResultList();
		if (resultPro != null) {
			for (Object processId : resultPro) {
				String sqlFechasProcesoAnterior = "select * from dblink('" + dblinkBpmsSuiaiii + "',"
						+ "'select b.taskid,b.createddate "
						+ " from bamtasksummary b "
						+ " where b.processinstanceid =" + processId + " order by b.taskid ') as t1 (taskid integer, createddate timestamp)";
				Query queryFecha = crudServiceBean.getEntityManager().createNativeQuery(sqlFechasProcesoAnterior);
				List<Object> resultFechas = queryFecha.getResultList();

				if (resultFechas.size() > 0) {
					Object[] objAux = (Object[]) resultFechas.get(0);
					Timestamp tsFecha = (Timestamp) objAux[1];
					fechaEnvio = new Date(tsFecha.getTime());
					break;
				}
			}
		}

		return fechaEnvio;
	}

	@SuppressWarnings("unchecked")
	public List<Object> recuperarEstadoProceso(String codigoProyecto) {

		List<Object> estado = null;

		String sqltaskbpmhyd = "select * from dblink('" + dblinkBpmsSuiaiii + "',"
				+ "'select processinstanceid from variableinstancelog where value=''" + codigoProyecto+ "'' "
				+ " and variableid = ''tramite'' "
				+ " and processid = ''rcoa.ActualizacionEtiquetadoPlaguicidasUsoAgricola'' ') as (id integer)";
		Query queryVariables = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);

		List<Object> resultPro = new ArrayList<Object>();
		resultPro = queryVariables.getResultList();
		if (resultPro != null) {
			for (Object processId : resultPro) {

				String sqlProceso = "select * from dblink('" + dblinkBpmsSuiaiii + "',"
						+ "'select b.id, b.status, b.end_date "
						+ " from processinstancelog b "
						+ " where b.processinstanceid =" + processId + " order by 1 ') as t1 (processid integer, status integer, enddate timestamp)";
				Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqlProceso);
				estado = queryProceso.getResultList();

				break;
			}
		}

		return estado;
	}

}
