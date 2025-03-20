package ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PerforacionCoordenadas;
import ec.gob.ambiente.suia.domain.PerforacionCronogramaPma;
import ec.gob.ambiente.suia.domain.PerforacionDesechosNoPeligrosos;
import ec.gob.ambiente.suia.domain.PerforacionDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.PerforacionEquipoMultidisciplinario;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.PerforacionMaquinasEquipos;
import ec.gob.ambiente.suia.domain.PerforacionMaterialInsumo;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class FichaAmbientalMineria020Facade {

    @EJB
    private CrudServiceBean crudServiceBean;
    
    public PerforacionExplorativa guardarPerforacionExplorativa(PerforacionExplorativa perforacionExplorativa) throws ServiceException {
        try {
            return crudServiceBean.saveOrUpdate(perforacionExplorativa);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    public PerforacionCoordenadas guardarPerforacionCoordenada(PerforacionCoordenadas perforacionCoordenada) throws ServiceException {
        try {
            return crudServiceBean.saveOrUpdate(perforacionCoordenada);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    @SuppressWarnings("unchecked")
	public List<PerforacionCoordenadas> guardarPerforacionCoordenada(List<PerforacionCoordenadas> perforacionCoordenada) throws ServiceException {
        try {
            return (List<PerforacionCoordenadas>) crudServiceBean.saveOrUpdate(perforacionCoordenada);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    public PerforacionEquipoMultidisciplinario guardarEquipoMultidisciplinario(PerforacionEquipoMultidisciplinario perforacionEquipoMultidisciplinario) throws ServiceException {
        try {
            return crudServiceBean.saveOrUpdate(perforacionEquipoMultidisciplinario);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    public PerforacionMaquinasEquipos guardarMaquinariaEquipo(PerforacionMaquinasEquipos perforacionMaquinasEquipos) throws ServiceException {
        try {
            return crudServiceBean.saveOrUpdate(perforacionMaquinasEquipos);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    public PerforacionMaterialInsumo guardarMaterialInsumo(PerforacionMaterialInsumo perforacionMaterialInsumo) throws ServiceException {
        try {
            return crudServiceBean.saveOrUpdate(perforacionMaterialInsumo);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    public PerforacionDesechosNoPeligrosos guardarDesechosNoPeligrosos(PerforacionDesechosNoPeligrosos perforacionDesechosNoPeligrosos) throws ServiceException {
        try {
            return crudServiceBean.saveOrUpdate(perforacionDesechosNoPeligrosos);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    public PerforacionDesechosPeligrosos guardarDesechosPeligrosos(PerforacionDesechosPeligrosos perforacionDesechosPeligrosos) throws ServiceException {
        try {
            return crudServiceBean.saveOrUpdate(perforacionDesechosPeligrosos);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    public List<PerforacionCronogramaPma> guardarCronogramaPma(List<PerforacionCronogramaPma> perforacionCronogramaPma) throws ServiceException {
        try {
            @SuppressWarnings("unchecked")
			List<PerforacionCronogramaPma> saveOrUpdate = (List<PerforacionCronogramaPma>) crudServiceBean.saveOrUpdate(perforacionCronogramaPma);
			return saveOrUpdate;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public PerforacionExplorativa cargarPerforacionExplorativa(ProyectoLicenciamientoAmbiental proyecto) throws ServiceException {
    	PerforacionExplorativa perforacionExplorativa= new PerforacionExplorativa();
            String sql = "SELECT p FROM PerforacionExplorativa p WHERE p.proyectoLicenciamientoAmbiental=:proyecto and p.estado=true";
            Query x=crudServiceBean.getEntityManager().createQuery(sql).setParameter("proyecto", proyecto);
            if(x.getResultList().size()>0)
            	perforacionExplorativa=(PerforacionExplorativa) x.getResultList().get(0);
            
			return perforacionExplorativa; 
    }
    
    @SuppressWarnings("unchecked")
    public List<PerforacionCoordenadas> cargarPerforacionCoordendas(PerforacionExplorativa perforacionExplorativa) throws ServiceException {
    	List<PerforacionCoordenadas> listaCoordendas= new ArrayList<PerforacionCoordenadas>();
    	String sql = "SELECT p FROM PerforacionCoordenadas p WHERE p.perforacionExplorativa=:scdr and p.estado=true";
    	Query x=crudServiceBean.getEntityManager().createQuery(sql).setParameter("scdr", perforacionExplorativa);
    	if(x.getResultList().size()>0)
    		listaCoordendas= x.getResultList();

    	return listaCoordendas; 
    }
    @SuppressWarnings("unchecked")
    public List<PerforacionEquipoMultidisciplinario> cargarEquipoMultidisciplinario(PerforacionExplorativa perforacionExplorativa) throws ServiceException {
    	List<PerforacionEquipoMultidisciplinario> listaEquipoMultidis= new ArrayList<PerforacionEquipoMultidisciplinario>();
    	String sql = "SELECT p FROM PerforacionEquipoMultidisciplinario p WHERE p.perforacionExplorativa=:scdr and p.estado=true";
    	Query x=crudServiceBean.getEntityManager().createQuery(sql).setParameter("scdr", perforacionExplorativa.getId());
    	if(x.getResultList().size()>0)
    		listaEquipoMultidis= x.getResultList();

    	return listaEquipoMultidis; 
    }
    @SuppressWarnings("unchecked")
    public List<PerforacionEquipoMultidisciplinario> equipoMultidisciplinarioCompleto(PerforacionExplorativa perforacionExplorativa) throws ServiceException {
    	List<PerforacionEquipoMultidisciplinario> listaEquipoMultidis= new ArrayList<PerforacionEquipoMultidisciplinario>();
    	String sql = "SELECT p FROM PerforacionEquipoMultidisciplinario p WHERE p.perforacionExplorativa=:scdr";
    	Query x=crudServiceBean.getEntityManager().createQuery(sql).setParameter("scdr", perforacionExplorativa.getId());
    	if(x.getResultList().size()>0)
    		listaEquipoMultidis= x.getResultList();

    	return listaEquipoMultidis; 
    }
    @SuppressWarnings("unchecked")
    public List<PerforacionMaquinasEquipos> cargarMaquinariaEquipo(PerforacionExplorativa perforacionExplorativa) throws ServiceException {
    	List<PerforacionMaquinasEquipos> listaMaquinaEquipo= new ArrayList<PerforacionMaquinasEquipos>();
    	String sql = "SELECT p FROM PerforacionMaquinasEquipos p WHERE p.perforacionExplorativa=:scdr and p.estado=true";
    	Query x=crudServiceBean.getEntityManager().createQuery(sql).setParameter("scdr", perforacionExplorativa.getId());
    	if(x.getResultList().size()>0)
    		listaMaquinaEquipo= x.getResultList();

    	return listaMaquinaEquipo; 
    }
    @SuppressWarnings("unchecked")
    public List<PerforacionMaterialInsumo> cargarMaterialInsumo(PerforacionExplorativa perforacionExplorativa) throws ServiceException {
    	List<PerforacionMaterialInsumo> lista= new ArrayList<PerforacionMaterialInsumo>();
    	String sql = "SELECT p FROM PerforacionMaterialInsumo p WHERE p.perforacionExplorativa=:scdr and p.estado=true";
    	Query x=crudServiceBean.getEntityManager().createQuery(sql).setParameter("scdr", perforacionExplorativa.getId());
    	if(x.getResultList().size()>0)
    		lista= x.getResultList();

    	return lista; 
    }
    @SuppressWarnings("unchecked")
    public List<PerforacionDesechosNoPeligrosos> cargarDesechosNoPeligrosos(PerforacionExplorativa perforacionExplorativa) throws ServiceException {
    	List<PerforacionDesechosNoPeligrosos> lista= new ArrayList<PerforacionDesechosNoPeligrosos>();
    	String sql = "SELECT p FROM PerforacionDesechosNoPeligrosos p WHERE p.perforacionExplorativa=:scdr and p.estado=true";
    	Query x=crudServiceBean.getEntityManager().createQuery(sql).setParameter("scdr", perforacionExplorativa.getId());
    	if(x.getResultList().size()>0)
    		lista= x.getResultList();

    	return lista; 
    }
    @SuppressWarnings("unchecked")
    public List<PerforacionDesechosPeligrosos> cargarDesechosPeligrosos(PerforacionExplorativa perforacionExplorativa) throws ServiceException {
    	List<PerforacionDesechosPeligrosos> lista= new ArrayList<PerforacionDesechosPeligrosos>();
    	String sql = "SELECT p FROM PerforacionDesechosPeligrosos p WHERE p.perforacionExplorativa=:scdr and p.estado=true";
    	Query x=crudServiceBean.getEntityManager().createQuery(sql).setParameter("scdr", perforacionExplorativa.getId());
    	if(x.getResultList().size()>0)
    		lista= x.getResultList();

    	return lista; 
    }
    @SuppressWarnings("unchecked")
    public List<PerforacionCronogramaPma> cargarCronogramaPma(PerforacionExplorativa perforacionExplorativa) throws ServiceException {
    	List<PerforacionCronogramaPma> lista= new ArrayList<PerforacionCronogramaPma>();
    	String sql = "SELECT p FROM PerforacionCronogramaPma p WHERE p.perforacionExplorativa=:scdr and p.estado=true order by p.id";
    	Query x=crudServiceBean.getEntityManager().createQuery(sql).setParameter("scdr", perforacionExplorativa.getId());
    	if(x.getResultList().size()>0)
    		lista= x.getResultList();

    	return lista; 
    }
    public Double sumaCalificacionPmaPorProyecto(Integer idProyecto, String tipoPma) {
		String queryString = "select sum(enmp_calification) from suia_iii.scdr_environmental_management_projects where scdr_id =(select scdr_id from "
				+ "suia_iii.scout_drilling where pren_id="+idProyecto+") and enma_parent_id in(select enma_id from suia_iii.scdr_environmental_management where enma_status=true and "
				+ "enma_description LIKE '%"+tipoPma+"%')";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

		Double calificacion=0.0;
		if(result.size()>0)
		for (Object object : result) {
			Object obj=new Object();
			obj=object;
			if(obj!=null)
				calificacion=(Double) obj;
		}

		return calificacion;
	}
    
  //suspendido
    public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
    @SuppressWarnings({ "unchecked", "unused" })
	public void modificarPropietarioTareasRGD(String codigoProyecto, Usuario usuario,boolean suspender,String numeroTramitePago,String ip) {		
		

			String sql="select * from dblink('"+dblinkSuiaVerde+"',"
					+ "'select h.id_online_payment_historical,h.online_payment_id,h.retired_value,h.tramit_number,p.used_value  "
					+ "from online_payment.online_payments_historical h "
					+ "inner join online_payment.online_payments p on (p.id_online_payment=h.online_payment_id)  "
					//+ "where h.project_id=''"+codigoProyecto+"'' "
					+ "where h.tramit_number=''"+numeroTramitePago+"'' "
					+ "order by 1 desc limit 1')"
					+ "as (id_online_payment_historical integer,online_payment_id integer,retired_value text,tramit_number text,used_value text)";
			Query queryPago = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object[]> result = (List<Object[]>) queryPago.getResultList();
	    	if (result.size() > 0) {
	    		for (int i = 0; i < result.size(); i++) {
	    			Object[] pagoHistorico = (Object[]) result.get(i);
	    			Integer idPagoHistorico=(Integer)pagoHistorico[0];
	    			Integer idPago=(Integer)pagoHistorico[1];
	    			String valorS=(String)pagoHistorico[2];
	    			String tramite=(String)pagoHistorico[3];
	    			String valorUsadoS=(String)pagoHistorico[4];
	    			
	    			Double valor=Double.valueOf(valorS);	    			
	    			if(valor>0)
	    			{
	    				String sqlInsert="select dblink_exec('"+dblinkSuiaVerde+"',"
	    						+ "'insert into online_payment.online_payments_historical  "
	    						+ "(id_online_payment_historical, description, project_id, retired_value,sender_ip, tramit_number, update_date, value_updated, online_payment_id) "
	    						+ "VALUES (nextval(''online_payment.seq_id_online_payments_historical''), ''Se reactiva la transacción por desactivación del proyecto'', "
	    						+ "''"+codigoProyecto+"'', ''0.00'',''"+ip+"'', ''"+tramite+"'', clock_timestamp(),  ''"+valorS+"'',"+idPago+")') as result";
	    				crudServiceBean.getEntityManager().createNativeQuery(sqlInsert).getResultList();	    				
	    				
	    				Double valorUsado=Double.valueOf(valorUsadoS)-valor;
	    				valorUsado=valorUsado>=0.0?valorUsado:0.0;
	    				
	    				String sqlUpdate="select dblink_exec('"+dblinkSuiaVerde+"',"
	    						+ "'update online_payment.online_payments  "
	    						+ "SET is_used = false, used_value = ''"+valorUsado+"'' "
	    						+ "WHERE tramit_number = ''"+tramite+"''') as result ";	    						
	    				crudServiceBean.getEntityManager().createNativeQuery(sqlUpdate).getResultList();
	    			}	    			
	    		}
	    	}
				
	}
    
    public int setEstadoProyecto(String codigo,boolean estado) {
		
		String queryString = "update suia_iii.projects_environmental_licensing set pren_status = "+estado+",pren_delete_reason="+(estado?"null":"'SUSPENDIDO'")+" where pren_code = '"+codigo+"'";
		return crudServiceBean.getEntityManager().createNativeQuery(queryString).executeUpdate();
			
	}
    
    /**
     * Método para obtener todos los proyectos con perforación explorativa
     */
    @SuppressWarnings("unchecked")
    public List<ProyectoLicenciamientoAmbiental> cargarProyectosPerforacionExplorativa() throws ServiceException {
    	List<ProyectoLicenciamientoAmbiental> perforacionExplorativa= new ArrayList<>();
            String sql = "SELECT p.proyectoLicenciamientoAmbiental FROM PerforacionExplorativa p "
            		+ "WHERE p.estado=true and p.finalized = true and p.approveTechnical is null ";
            Query x=crudServiceBean.getEntityManager().createQuery(sql);
            if(x.getResultList().size()>0)
            	perforacionExplorativa=(List<ProyectoLicenciamientoAmbiental>) x.getResultList();
            
			return perforacionExplorativa; 
    }
    
    @SuppressWarnings("unchecked")
    public List<PerforacionExplorativa> cargarProyectosPerforacionExplorativa2() throws ServiceException {
    	List<PerforacionExplorativa> perforacionExplorativa= new ArrayList<>();
            String sql = "SELECT p FROM PerforacionExplorativa p "
            		+ "WHERE p.estado=true and p.finalized = true and (p.approveTechnical is null or p.approveTechnical=false)";
            Query x=crudServiceBean.getEntityManager().createQuery(sql);
            if(x.getResultList().size()>0)
            	perforacionExplorativa=(List<PerforacionExplorativa>) x.getResultList();
            
			return perforacionExplorativa; 
    }
    /**
     * Metodo para buscar el pago de un tramite para Perforación Explorativa
     * @param tramite
     * @return
     */
    @SuppressWarnings({ "unchecked", "unused" })
    public boolean consultarPagoPorTramite(String tramite){
    	try{
    		String sql_pago = "select * from dblink('"+Constantes.getDblinkSuiaVerde()+"', "
	            	+ "'select id_online_payment_historical "
	            	+ "from online_payment.online_payments_historical where project_id = "
	            	+ "''" + tramite +"'' "	            	
	            	+ "order by 1') as (id_online_payment_historical text)";
    		
    		Query queryPago = crudServiceBean.getEntityManager().createNativeQuery(sql_pago);
			
    		//Como solo es una variable solo se pone en una lista de String
			//se lo coloca en un hashmap para poder tener acceso a la variable mediante la key processintanceid
			Map<String, Object> variablesPago = new HashMap<String, Object>();
        	List<String> resultListPago = (List<String>) queryPago.getResultList();
    		if (resultListPago.size() > 0) {
    			for (int i = 0; i < resultListPago.size(); i++) {
    				variablesPago.put("idPago", resultListPago.get(i));
    				break;
    			}
    		}	
    		
    		if(variablesPago.isEmpty())
    			return false;
    		else
    			return true;
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return false;
    }
    
    public PerforacionExplorativa cargarPerforacionExplorativaRcoa(Integer idProyecto) throws ServiceException {
    	PerforacionExplorativa perforacionExplorativa= new PerforacionExplorativa();
            String sql = "SELECT p FROM PerforacionExplorativa p WHERE p.proyectoLicenciaCoa.id=:idProyecto and p.estado=true";
            Query x=crudServiceBean.getEntityManager().createQuery(sql).setParameter("idProyecto", idProyecto);
            if(x.getResultList().size()>0)
            	perforacionExplorativa=(PerforacionExplorativa) x.getResultList().get(0);
            
			return perforacionExplorativa; 
    }
    
    public Double sumaCalificacionPmaPorProyectoRcoa(Integer idProyecto, String tipoPma) {
		String queryString = "select sum(enmp_calification) from suia_iii.scdr_environmental_management_projects where scdr_id =(select scdr_id from "
				+ "suia_iii.scout_drilling where prco_id="+idProyecto+") and enma_parent_id in(select enma_id from suia_iii.scdr_environmental_management where enma_status=true and "
				+ "enma_description LIKE '%"+tipoPma+"%')";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

		Double calificacion=0.0;
		if(result.size()>0)
		for (Object object : result) {
			Object obj=new Object();
			obj=object;
			if(obj!=null)
				calificacion=(Double) obj;
		}

		return calificacion;
	}
}