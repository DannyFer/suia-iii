package ec.gob.ambiente.suia.recaudaciones.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;

@Stateless
public class NumeroUnicoTransaccionalFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardarNUT(NumeroUnicoTransaccional numeroUnicoTransaccional) throws ServiceException {
		crudServiceBean.saveOrUpdate(numeroUnicoTransaccional);
	}
	
	@SuppressWarnings("unchecked")
	public NumeroUnicoTransaccional buscarNUTPorSolicitud(String solicitudCodigo) throws ServiceException {
		 
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("solicitudCodigo", solicitudCodigo);
	    List<NumeroUnicoTransaccional> result = (List<NumeroUnicoTransaccional>) crudServiceBean.findByNamedQuery(NumeroUnicoTransaccional.LISTAR_POR_SOLICITUD,params);
	    if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
	    return  null;
	}
	
	@SuppressWarnings("unchecked")
	public List<NumeroUnicoTransaccional> listBuscaNUTPorSolicitud(String solicitudCodigo) {
		 
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("solicitudCodigo", solicitudCodigo);
	    try {
	    	List<NumeroUnicoTransaccional> result = (List<NumeroUnicoTransaccional>) crudServiceBean.findByNamedQuery(NumeroUnicoTransaccional.LISTAR_POR_SOLICITUD,params);
	    	if (result != null && !result.isEmpty()) {
				return result;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	    
	    return  null;
	}
	
	@SuppressWarnings("unchecked")
	public NumeroUnicoTransaccional buscarNUTPorTramite(String codigoTramite) throws ServiceException {
		 
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("codigoTramite", codigoTramite);
	    List<NumeroUnicoTransaccional> result = (List<NumeroUnicoTransaccional>) crudServiceBean.findByNamedQuery(NumeroUnicoTransaccional.LISTAR_POR_TRAMITE,params);
	    if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
	    return  null;
	}

	@SuppressWarnings("unchecked")
	public List<NumeroUnicoTransaccional> listBuscaNUTPorIdSolicitud(Integer solicitudId) {
		 
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("solicitudId", solicitudId);
	    try {
	    	List<NumeroUnicoTransaccional> result = (List<NumeroUnicoTransaccional>) crudServiceBean.findByNamedQuery(NumeroUnicoTransaccional.LISTAR_POR_SOLICITUDID,params);
	    	if (result != null && !result.isEmpty()) {
				return result;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	    
	    return  null;
	}
	
	@SuppressWarnings("unchecked")
	public NumeroUnicoTransaccional buscarNUTPorNumeroTramite(String tramitNumber) throws ServiceException {
		 
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("tramitNumber", tramitNumber);
	    List<NumeroUnicoTransaccional> result = (List<NumeroUnicoTransaccional>) crudServiceBean.findByNamedQuery(NumeroUnicoTransaccional.LISTAR_POR_NUMBERO_TRAMITE,params);
	    if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
	    return  null;
	}
	
	@SuppressWarnings("unchecked")
	public List<NumeroUnicoTransaccional> listNUTPorTramite(String codigoTramite) throws ServiceException {
		 
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("codigoTramite", codigoTramite);
	    List<NumeroUnicoTransaccional> result = (List<NumeroUnicoTransaccional>) crudServiceBean.findByNamedQuery(NumeroUnicoTransaccional.LISTAR_POR_TRAMITE,params);
	    if (result != null && !result.isEmpty()) {
			return result;
		}
	    return  null;
	}
	
	@SuppressWarnings("unchecked")
	public List<NumeroUnicoTransaccional> listarNUTActivos() {
		 
		Map<String, Object> params = new HashMap<String, Object>();
	    try {
	    	List<NumeroUnicoTransaccional> result = (List<NumeroUnicoTransaccional>) crudServiceBean.findByNamedQuery(NumeroUnicoTransaccional.LISTAR_NUT_HABILITADOS,params);
	    	
	    	if (result != null && !result.isEmpty()) {
				return result;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	    
	    return  null;
	}
	
	@SuppressWarnings("unchecked")
	public NumeroUnicoTransaccional getNUTPorCodigo(String codigo) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codigo", codigo);
	    List<NumeroUnicoTransaccional> listaNuts = (List<NumeroUnicoTransaccional>) crudServiceBean.findByNamedQuery(NumeroUnicoTransaccional.LISTAR_POR_CODIGO_NUT,params);

		if (listaNuts != null && !listaNuts.isEmpty()) {
			return listaNuts.get(0);
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<NumeroUnicoTransaccional> getListaNUTsPorEstado(Integer estado) {
		 
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("estado", estado);
	    List<NumeroUnicoTransaccional> listaNuts = (List<NumeroUnicoTransaccional>) crudServiceBean.findByNamedQuery(NumeroUnicoTransaccional.LISTAR_NUTS_POR_ESTADO,params);

		if (listaNuts != null && !listaNuts.isEmpty()) {
			return listaNuts;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<NumeroUnicoTransaccional> listNUTActivoPorTramite(String codigoTramite) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codigoTramite", codigoTramite);
	    List<NumeroUnicoTransaccional> listaNuts = (List<NumeroUnicoTransaccional>) crudServiceBean.findByNamedQuery(NumeroUnicoTransaccional.LISTAR_ACTIVOS_POR_TRAMITE,params);

		if (listaNuts != null && !listaNuts.isEmpty()) {
			return listaNuts;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<NumeroUnicoTransaccional> listNUTActivoPorTramitePorTarea(String codigoTramite, Long  idProceso) {
		Query sql= crudServiceBean.getEntityManager().createQuery("select n from  NumeroUnicoTransaccional n inner join n.solicitudUsuario s "
				+ " , DocumentoNUT d "
				+ "where s.id = d.solicitudId and n.estado= true and n.nutCodigoProyecto=:codigoTramite "
				+ " and (n.nutUsado is null or n.nutUsado = false) and d.idProceso =:idProceso "
				+ "order by n.id desc " );
		sql.setParameter("codigoTramite", codigoTramite);
		sql.setParameter("idProceso", idProceso);
        
		List<NumeroUnicoTransaccional> listaNuts = (List<NumeroUnicoTransaccional>) sql.getResultList();
		if (listaNuts != null && !listaNuts.isEmpty()) {
			return listaNuts;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<NumeroUnicoTransaccional> listNUTActivoPorTramitePorTareaPorBanco(String codigoTramite, Long  idProceso, Integer bancoId) {
		Query sql= crudServiceBean.getEntityManager().createQuery("select n from  NumeroUnicoTransaccional n inner join n.solicitudUsuario s "
				+ " , DocumentoNUT d "
				+ "where s.id = d.solicitudId and n.estado= true and n.nutCodigoProyecto=:codigoTramite "
				+ " and (n.nutUsado is null or n.nutUsado = false) and d.idProceso =:idProceso "
				+ " and n.estadosNut.id in (2, 5) "
				+ " and s.institucionFinanciera.id = :idBanco "
				+ "order by n.id desc " );
		sql.setParameter("codigoTramite", codigoTramite);
		sql.setParameter("idProceso", idProceso);
		sql.setParameter("idBanco", bancoId);
        
		List<NumeroUnicoTransaccional> listaNuts = (List<NumeroUnicoTransaccional>) sql.getResultList();
		if (listaNuts != null && !listaNuts.isEmpty()) {
			return listaNuts;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<NumeroUnicoTransaccional> listNUTActivoPorTramitePorTareaPorBancoPorCuenta(String codigoTramite, Long  idProceso, Integer bancoId, Integer cuentaId) {
		Query sql= crudServiceBean.getEntityManager().createQuery("select n from  NumeroUnicoTransaccional n inner join n.solicitudUsuario s "
				+ " , DocumentoNUT d "
				+ "where s.id = d.solicitudId and n.estado= true and n.nutCodigoProyecto=:codigoTramite "
				+ " and (n.nutUsado is null or n.nutUsado = false) and d.idProceso =:idProceso "
				+ " and n.estadosNut.id in (2, 5) "
				+ " and n.cuentas.id = :idCuenta "
				+ " and s.institucionFinanciera.id = :idBanco "
				+ "order by n.id desc " );
		sql.setParameter("codigoTramite", codigoTramite);
		sql.setParameter("idProceso", idProceso);
		sql.setParameter("idBanco", bancoId);
		sql.setParameter("idCuenta", cuentaId);
        
		List<NumeroUnicoTransaccional> listaNuts = (List<NumeroUnicoTransaccional>) sql.getResultList();
		if (listaNuts != null && !listaNuts.isEmpty()) {
			return listaNuts;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<NumeroUnicoTransaccional> listNUTPagadoPorTramitePorTarea(String codigoTramite, Long  idProceso) {
		Query sql= crudServiceBean.getEntityManager().createQuery("select n from  NumeroUnicoTransaccional n inner join n.solicitudUsuario s "
				+ " , DocumentoNUT d "
				+ "where s.id = d.solicitudId and n.estado= true and n.nutCodigoProyecto=:codigoTramite "
				+ " and d.idProceso =:idProceso "
				+ " and n.estadosNut.id in (3) "
				+ "order by n.id desc " );
		sql.setParameter("codigoTramite", codigoTramite);
		sql.setParameter("idProceso", idProceso);
        
		List<NumeroUnicoTransaccional> listaNuts = (List<NumeroUnicoTransaccional>) sql.getResultList();
		if (listaNuts != null && !listaNuts.isEmpty()) {
			return listaNuts;
		} else {
			return new ArrayList<NumeroUnicoTransaccional>();
		}
	}

	@SuppressWarnings("unchecked")
	public List<NumeroUnicoTransaccional> listNutPorTramiteSinFiltro(
			String codigoTramite) throws ServiceException {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codigoTramite", codigoTramite);
		List<NumeroUnicoTransaccional> result = (List<NumeroUnicoTransaccional>) crudServiceBean
				.findByNamedQueryWithOutFilters(
						NumeroUnicoTransaccional.LISTAR_POR_TRAMITE_SIN_FILTRO, params);
		if (result != null && !result.isEmpty()) {
			return result;
		}
		return new ArrayList<NumeroUnicoTransaccional>();
	}
	
	public void activarRegistroDePagoManual(Integer idNut, String motivo, String proyecto, Integer idUsuario, String usuario, Long idProceso) {
		String sqlUpdate = "UPDATE payments.unique_transaction_number "
				+ " set nut_status = false, nuts_id = 4, "
				+ " nut_observation_bd = '" + motivo + "', "
				+ " nut_observation_bd_date = now() " + " where nut_id = "
				+ idNut + " ;";

		String sqlInsert = "INSERT INTO payments.projects_not_nut "
				+ " (prnn_project, user_id, prnn_status, prnn_creation_date, prnn_creator_user, "
				+ " prnn_observation_bd, processinstanceid, prnn_status_tramit_number) "
				+ " VALUES('" + proyecto + "', " + idUsuario + ", true, now(), '" + usuario +"', "
				+ " '" + motivo + "', " + idProceso + ", false);";

		if(idNut != null) {
			crudServiceBean.insertUpdateByNativeQuery(sqlUpdate.toString(), null);
		}

		crudServiceBean.insertUpdateByNativeQuery(sqlInsert.toString(), null);

	}
	
	@SuppressWarnings("unchecked")
	public List<NumeroUnicoTransaccional> listNUTActivosPagoOnline(String codigoTramite) {
		Query sql= crudServiceBean.getEntityManager().createQuery("select n from  NumeroUnicoTransaccional n "
				+ "where n.nutCodigoProyecto=:codigoTramite "
				+ " and (n.nutUsado is null or n.nutUsado = false) and n.estado =true "
				+ "order by n.id desc " );
		sql.setParameter("codigoTramite", codigoTramite);    
		List<NumeroUnicoTransaccional> listaNuts = (List<NumeroUnicoTransaccional>) sql.getResultList();
		if (listaNuts != null && !listaNuts.isEmpty()) {
			return listaNuts;
		} else {
			return null;
		}
	}
	

}
