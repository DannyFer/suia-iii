package ec.gob.ambiente.retce.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.retce.model.FormaInformacionProyecto;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.InformacionProyectoInterseca;
import ec.gob.ambiente.retce.model.ProyectoInformacionUbicacionGeografica;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformacionProyectoFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;

	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	public InformacionProyecto findById(Integer id){		
		try{
			InformacionProyecto informacionProyecto = (InformacionProyecto) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM InformacionProyecto o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			return informacionProyecto;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}	

	public void save(InformacionProyecto obj,Usuario usuario) {
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
			if(!obj.isHistorico()){
				obj.setCodigoRetce(generarCodigo());	
			}
			obj.setInformacionEnviada(obj.getInformacionEnviada()==null?false:obj.getInformacionEnviada());						
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());
		}
		if(obj.getEsEmisionFisica()){
			obj.setCodigo(generarCodigoProyecto(obj));					
		}				
		crudServiceBean.saveOrUpdate(obj);
	}
	
	public void guardarInfoBasica4Categorias(InformacionProyecto objProyecto, String nombreOperador, List<ProyectoInformacionUbicacionGeografica> ubicacciones, List<InformacionProyectoInterseca> listaBosques, List<InformacionProyectoInterseca> listaAreas){
		SimpleDateFormat formatofecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Integer total =0, licenciaId=0, proyectoRetceId=0;
		String sql ="select * from dblink('"+dblinkSuiaVerde+"',"
				+ "'select count(*), lic_secuencial, proj_id  FROM licencia_ambiental_fisica where proj_id = "+objProyecto.getId()+" or lic_codigo = ''"+objProyecto.getCodigo()+"'' group by lic_secuencial, proj_id ' ) "
				+ "as (total integer, inSecuencial Integer, idProyecto Integer )";
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object[]> result = (List<Object[]>) query.getResultList();
		for (Object object : result) {
			total =  Integer.valueOf(((Object[]) object)[0].toString());
			if(((Object[]) object)[1] != null){
				licenciaId =  Integer.valueOf(((Object[]) object)[1].toString());
			}
			if(((Object[]) object)[2] != null){
				proyectoRetceId =  Integer.valueOf(((Object[]) object)[2].toString());
				break;
			}
		}
		// si no existe inserto el registro, caso contrario actualizo
		if(total == 0){
			sql = "select from dblink_exec('"+dblinkSuiaVerde+"',"
					+ "' INSERT INTO licencia_ambiental_fisica "
					+ " (lic_codigo, lic_nom_proponente, lic_nom_proyecto, lic_num_licencia, lic_fecha_emision, lic_intersecta, "
					+ "lic_create_date, lic_update_date, lic_create_user, lic_update_user, lic_tramite_centralizado, lic_process_name, "
					+ "lic_responsible_area_id, lic_tracking_area_id, lic_identification, proj_id, sety_id  )"
					+ " VALUES(''"+objProyecto.getCodigo()+"'', ''"+nombreOperador+"'', "
					+ "''"+objProyecto.getNombreProyecto()+"'', ''"+objProyecto.getNumeroResolucion()+"'', ''"+formatofecha.format(objProyecto.getFechaEmision())+"'' , "
					+ "false,  "+((objProyecto.getFechaCreacion() != null)?("''"+formatofecha.format(objProyecto.getFechaCreacion())+"''"):"null")+"  , "
					+ ((objProyecto.getFechaModificacion() != null)?("''"+formatofecha.format(objProyecto.getFechaModificacion())+"''"):"null")+"  , "
					+ "''"+objProyecto.getUsuarioCreacion()+"'', ''"+objProyecto.getUsuarioModificacion()+"'', false, "
					+ " ''"+objProyecto.getNombreProceso()+"'', "+objProyecto.getAreaResponsable().getId()+", "+objProyecto.getAreaSeguimiento().getId()+", "
					+ "''"+objProyecto.getUsuarioCreacion()+"'', "+objProyecto.getId()+", "+objProyecto.getTipoSector().getId()+" )' )";
			query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			query.getResultList();
				/// obtengo el id generado
				sql ="select * from dblink('"+dblinkSuiaVerde+"',"
						+ "'select count(*), lic_secuencial  FROM licencia_ambiental_fisica where proj_id = "+objProyecto.getId()+" group by lic_secuencial' ) "
						+ "as (total integer, inSecuencial Integer )";
				query = crudServiceBean.getEntityManager().createNativeQuery(sql);
				query.getResultList();
				for (Object object : result) {
					total =  Integer.valueOf(((Object[]) object)[0].toString());
					if(((Object[]) object)[1] != null){
						licenciaId =  Integer.valueOf(((Object[]) object)[1].toString());
					}
				}
		}else{
			sql = "select from dblink_exec('"+dblinkSuiaVerde+"',"
					+ "' UPDATE licencia_ambiental_fisica "
					+ "SET lic_codigo = ''"+objProyecto.getCodigo()+"'', "
							+ "lic_nom_proponente = ''"+nombreOperador+"'', "
							+ "lic_nom_proyecto = ''"+objProyecto.getNombreProyecto()+"'', "
							+ "lic_num_licencia = ''"+objProyecto.getNumeroResolucion()+"'',"
							+ "lic_fecha_emision = ''"+formatofecha.format(objProyecto.getFechaEmision())+"'', "
							+ "lic_intersecta = false, "
							+ "lic_create_date = ''"+formatofecha.format(objProyecto.getFechaCreacion())+"'',"
							+ "lic_update_date = ''"+formatofecha.format(objProyecto.getFechaModificacion())+"'', "
							+ "lic_create_user = ''"+objProyecto.getUsuarioCreacion()+"'', "
							+ "lic_update_user = ''"+objProyecto.getUsuarioModificacion()+"'', "
							+ "lic_tramite_centralizado = false,"
							+ "lic_process_name = ''"+objProyecto.getNombreProceso()+"'', "
							+ "lic_responsible_area_id ="+objProyecto.getAreaResponsable().getId()+","
							+ "lic_tracking_area_id = "+objProyecto.getAreaSeguimiento().getId()+", "
							+ "lic_identification = ''"+objProyecto.getUsuarioCreacion()+"'', "
							+ "sety_id =  "+objProyecto.getTipoSector().getId()+" "
					+ "WHERE proj_id = "+ objProyecto.getId()+" ' ) " ;
			query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			query.getResultList();
		}
		if(licenciaId != null && licenciaId > 0 && proyectoRetceId > 0 ){
			// desabilito todas las ubicaciones
			sql = "select from dblink_exec('"+dblinkSuiaVerde+"',"
					+ "' UPDATE licencia_provincia "
					+ "SET lprov_status = false "
					+ "WHERE lprov_licencia = "+licenciaId +"') " ;
				query = crudServiceBean.getEntityManager().createNativeQuery(sql);
				query.getResultList();
			/// guardo las ubicaciones 
			for	(ProyectoInformacionUbicacionGeografica objUbicacion : ubicacciones) {
				Integer contador =0;
				//verifico se ya existe
				sql ="select * from dblink('"+dblinkSuiaVerde+"',"
						+ "'select count(*) FROM licencia_provincia where lprov_licencia = "+licenciaId+" and gelo_id = "+objUbicacion.getUbicacionesGeografica().getId()+"'  ) "
						+ "as (total integer )";
				Query query1 = crudServiceBean.getEntityManager().createNativeQuery(sql);
				List<Object[]> resultUbicacion = (List<Object[]>) query1.getResultList();
				for (Object object : resultUbicacion) {
					contador =  (((Integer) object).intValue());
				}
				if(contador == 0){
					sql = "select from dblink_exec('"+dblinkSuiaVerde+"',"
							+ "' INSERT INTO licencia_provincia "
							+ " (lprov_licencia, gelo_id, lprov_status )"
							+ " VALUES("+licenciaId+", "+objUbicacion.getUbicacionesGeografica().getId()+", true  ) ') ";
				}else{
					sql = "select from dblink_exec('"+dblinkSuiaVerde+"',"
							+ "' UPDATE licencia_provincia "
							+ "SET lprov_status = true "
							+ "WHERE lprov_licencia = "+licenciaId +"and gelo_id = "+objUbicacion.getUbicacionesGeografica().getId()+"') " ;
				}
				query = crudServiceBean.getEntityManager().createNativeQuery(sql);
				query.getResultList();	
			}
			if(listaBosques.size() > 0){
				// desabilito todas las ubicaciones
				sql = "select from dblink_exec('"+dblinkSuiaVerde+"',"
						+ "' UPDATE licencia_fisica_interseca "
						+ "SET lifi_estado = false, lifi_update_date = now()  "
						+ "WHERE lic_secuencial = "+licenciaId +"') " ;
					query = crudServiceBean.getEntityManager().createNativeQuery(sql);
					query.getResultList();
				// actualizo para bosques
				for (InformacionProyectoInterseca objInterseca : listaBosques) {
					sql ="select * from dblink_exec('"+dblinkSuiaVerde+"',"
							+ "' UPDATE licencia_fisica_interseca SET lifi_estado = true, lifi_update_date = now()  where lic_secuencial = "+licenciaId+" and lifi_bosque_id = "+objInterseca.getBosqueId()+"  ' ) "
							+ " ";
					query = crudServiceBean.getEntityManager().createNativeQuery(sql);
					String resultado = (String) query.getSingleResult();
					String[] dato = resultado.split(" ");
					if (dato.length > 1){
						if(Integer.valueOf(dato[1].trim()) == 0){
							sql = "select from dblink_exec('"+dblinkSuiaVerde+"',"
									+ "' INSERT INTO licencia_fisica_interseca "
									+ " (lic_secuencial, lifi_bosque_id, lifi_nombre_bosque, lifi_estado,  lifi_create_date)"
									+ " VALUES("+licenciaId+", "+objInterseca.getBosqueId()+", ''"+objInterseca.getBosqueNombre()+"'', true , now() ) ') ";
							query = crudServiceBean.getEntityManager().createNativeQuery(sql);
							query.getResultList();	
						}
					}
				}
				// actualizo para areas protegidas
				for (InformacionProyectoInterseca objInterseca : listaAreas) {
					sql ="select * from dblink_exec('"+dblinkSuiaVerde+"',"
							+ "' UPDATE licencia_fisica_interseca SET lifi_estado = true, lifi_update_date = now()  where lic_secuencial = "+licenciaId+" and lifi_id_area_protegida = "+objInterseca.getAreaProtegidaId()+"  ' ) "
							+ " ";
					query = crudServiceBean.getEntityManager().createNativeQuery(sql);
					String resultado = (String) query.getSingleResult();
					String[] dato = resultado.split(" ");
					if (dato.length > 1){
						if(Integer.valueOf(dato[1].trim()) == 0){
							sql = "select from dblink_exec('"+dblinkSuiaVerde+"',"
									+ "' INSERT INTO licencia_fisica_interseca "
									+ " (lic_secuencial, lifi_id_area_protegida, lifi_nombre_area_protegida, lifi_estado,  lifi_create_date)"
									+ " VALUES("+licenciaId+", "+objInterseca.getAreaProtegidaId()+", ''"+objInterseca.getAreaProtegidaNombre()+"'', true , now() ) ') ";
							query = crudServiceBean.getEntityManager().createNativeQuery(sql);
							query.getResultList();	
						}
					}
				}
			}
		}
	}
	
	public void saveRevision(InformacionProyecto obj,Usuario usuario) {
		obj.setUsuarioInformacionValidada(usuario.getNombre());
		obj.setFechaInformacionValidada(new Date());
		obj.setInformacionEnviada(false);
		crudServiceBean.saveOrUpdate(obj);
	}
	
	private String generarCodigo() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-RETCE-"
					+ secuenciasFacade.getCurrentYear()					
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence("MAAE-RETCE",4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String generarCodigoProyecto(InformacionProyecto obj){
    	if(obj.getEsEmisionFisica()){
    		String codigoSuia="";
    		Calendar c = Calendar.getInstance();
    		c.setTime(obj.getFechaEmision());
    		switch (obj.getAreaResponsable().getTipoArea().getSiglas()) {
			case "DP":
			case "PC":
			 if(obj.getAreaResponsable().toString().contains("MEN-SPA")){
				 codigoSuia = "MEN-SPA-FIS-"+c.get(1)+"-"+obj.getNumeroResolucion();
			}else{
				codigoSuia = Constantes.SIGLAS_INSTITUCION + "-FIS-"+obj.getAreaResponsable().getAreaAbbreviation()+"-"+c.get(1)+"-"+obj.getNumeroResolucion();
			}
				break;
			case "EA":
				if(obj.getAreaResponsable().equals("CONELEC")){
		    		codigoSuia = "CONELEC-FIS-"+c.get(1)+"-"+obj.getNumeroResolucion();
				}else if(obj.getAreaResponsable().toString().contains("SINGEI")){
		    		codigoSuia = "MEN-SPA-FIS-"+c.get(1)+"-"+obj.getNumeroResolucion();
				}else{
					codigoSuia = "GAD"+(obj.getAreaResponsable().getTipoEnteAcreditado().equals("MUNICIPIO")?"M":"P")+
	    				obj.getAreaResponsable().getAreaAbbreviation()+"-FIS-"+c.get(1)+"-"+obj.getNumeroResolucion();
				}
				break;
			default:
				codigoSuia = Constantes.SIGLAS_INSTITUCION + "-FIS-"+obj.getAreaResponsable().getAreaAbbreviation()+"-"+c.get(1)+"-"+obj.getNumeroResolucion();
				break;
			}
    		return codigoSuia;
    	}	
    	return obj.getCodigo();
    }
	
	public void guardarFormasCoordenadas(List<FormaInformacionProyecto> formaCoordenadas) {		
		desactivarFormasCoordenadasInforme(formaCoordenadas.get(0).getInformacionProyecto());	
		
		for (FormaInformacionProyecto formaCoordenada : formaCoordenadas) {
			List<Coordenada> coordenadas=formaCoordenada.getCoordenadas();
			for (Coordenada coordenada : coordenadas) {
				coordenada.setFormaInformacionProyecto(formaCoordenada);
			}
			
			crudServiceBean.saveOrUpdate(formaCoordenada);
			crudServiceBean.saveOrUpdate(coordenadas);			
		}
	}
	
	private void desactivarFormasCoordenadasInforme(InformacionProyecto informacion)
	{
		List<FormaInformacionProyecto> formaCoordenadas=getCoordenadasPorIdInformacionProyecto(informacion.getId());
		if(formaCoordenadas!=null){
			for (FormaInformacionProyecto formaCoordenada : formaCoordenadas) {
				formaCoordenada.setEstado(false);				
			}
			crudServiceBean.saveOrUpdate(formaCoordenadas);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<FormaInformacionProyecto> getCoordenadasPorIdInformacionProyecto(int informacionProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("informacionProyecto", informacionProyecto);

		List<FormaInformacionProyecto> result = (List<FormaInformacionProyecto>) crudServiceBean.findByNamedQuery(FormaInformacionProyecto.FIND_BY_PROJECT,parameters);
		if (result != null && !result.isEmpty()) {
			return result;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<InformacionProyecto> findByUser(Usuario usuario){
		List<InformacionProyecto> informacionProyecto=new ArrayList<InformacionProyecto>();
		try{
			informacionProyecto = (List<InformacionProyecto>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM InformacionProyecto o where o.estado=true and o.usuarioCreacion = :usuario order by 1 desc")
					.setParameter("usuario", usuario.getNombre())					
					.getResultList();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return informacionProyecto;
	}
	
	public InformacionProyecto findByCodigo(String codigo){		
		try{
			InformacionProyecto informacionProyecto = (InformacionProyecto) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM InformacionProyecto o where o.estado=true and o.codigo = :codigo order by 1 desc")
					.setParameter("codigo", codigo)
					.setMaxResults(1)
					.getSingleResult();
			return informacionProyecto;
			
		} catch (NoResultException e) {
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
