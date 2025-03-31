package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroOperador;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.Constantes;


@Stateless
public class RegistroSustanciaQuimicaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	private String generarCodigo() {
		try {
			String nombreSecuencia="MAAE-RSQ-"+secuenciasFacade.getCurrentYear();
			return Constantes.SIGLAS_INSTITUCION + "-RSQ-"+secuenciasFacade.getCurrentYear() 			
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia,4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void guardar(RegistroSustanciaQuimica obj, Usuario usuario){			
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
			obj.setCodigo(generarCodigo());
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}	
		crudServiceBean.saveOrUpdate(obj);
	}
	
	public RegistroSustanciaQuimica obtenerRegistroPorId(Integer idRSQ) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM RegistroSustanciaQuimica o WHERE o.estado=true and o.id=:idRSQ");
			query.setParameter("idRSQ", idRSQ);
			query.setMaxResults(1);
			return (RegistroSustanciaQuimica)query.getSingleResult();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return null;
	}
	
	public RegistroSustanciaQuimica obtenerRegistroPorCodigo(String codigo) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM RegistroSustanciaQuimica o WHERE o.estado=true and o.numeroAplicacion=:codigo");
			query.setParameter("codigo", codigo);
			query.setMaxResults(1);
			return (RegistroSustanciaQuimica)query.getSingleResult();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return null;
	}
	
	public RegistroSustanciaQuimica obtenerRegistroPorProyecto(ProyectoLicenciaCoa proyectoLicenciaCoa) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM RegistroSustanciaQuimica o WHERE o.estado=true and o.proyectoLicenciaCoa.codigoUnicoAmbiental=:codigoProyecto ORDER BY 1 desc");
			query.setParameter("codigoProyecto", proyectoLicenciaCoa.getCodigoUnicoAmbiental());
			query.setMaxResults(1);
			return (RegistroSustanciaQuimica)query.getSingleResult();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroSustanciaQuimica> obtenerRegistrosSustanciasPorUsuario(Usuario usuario) {		
		List<RegistroSustanciaQuimica> lista=new ArrayList<RegistroSustanciaQuimica>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM RegistroSustanciaQuimica o WHERE o.estado=true "
					+ "and o.usuario.id=:usuario and o.vigenciaHasta != null and o.vigenciaHasta >= now()  ORDER BY 1 desc");
			query.setParameter("usuario", usuario.getId());
			lista= (List<RegistroSustanciaQuimica>)query.getResultList();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return lista;
	}
	
	public List<RegistroSustanciaQuimica> obtenerRegistrosPorUsuario(Usuario usuario){
		List<RegistroSustanciaQuimica> lista=new ArrayList<RegistroSustanciaQuimica>();
		try {
			
			String queryString = "select s.chsr_id, s.prco_id, s.chsr_identification_rep_legal, s.chsr_name_rep_legal, "
								+ "s.chsr_application_number, s.chsr_valid_since, s.chsr_valid_until, '' as nombre, '' as organizacion, "
								+ "s.chsr_creator_user, p.pest_month, p.pest_year, s.user_id  "
								+ "from coa_chemical_sustances.chemical_sustances_records s "
								+ "inner join coa_chemical_sustances.permission_statement p "
								+ "on s.chsr_id = p.chsr_id "
								+ "where "
								+ "case when s.user_id is not null "
								+ "then s.user_id = " + usuario.getId() 
								+ "else s.chsr_creator_user = '" + usuario.getNombre() + "' "
								+ "end "
								+ "and p.pest_import_license is true "
								+ "and s.chsr_status = true "
								+ "and p.pest_status = true "
								+ "and pest_parent_id is null "
								+ "and chsr_valid_until is not null "
								+ "and chsr_valid_until >= now()";
			
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if (result.size() > 0) {
				lista=new ArrayList<RegistroSustanciaQuimica>();
				for (Object object : result) {
					lista.add(new RegistroSustanciaQuimica((Object[]) object));
				}
				return lista;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	
	public List<RegistroOperador> obtenerUsuariosRegistrosRSQ(Integer anio) {
		List<RegistroOperador> lista = new ArrayList<RegistroOperador>();
		
		try {
			String queryString = "select s.chsr_id, s.chsr_application_number, s.chsr_name_rep_legal, "
					+ "l.peop_name, o.orga_name_organization, "
					+ "s.chsr_creator_user, s.user_id "
					+ "from coa_chemical_sustances.chemical_sustances_records s "
					+ "left join public.users u on "
					+ "case when s.user_id is not null "
					+ "then  u.user_id = s.user_id "
					+ "else u.user_name = s.chsr_creator_user "
					+ "end "
					+ "left join public.people l on l.peop_id = u.peop_id "
					+ "left join public.organizations o on o.peop_id = l.peop_id  and o.orga_ruc = u.user_name "
					+ "where chsr_status = true and chsr_valid_until is not null "
					+ "and EXTRACT(YEAR FROM chsr_valid_until) >= " + anio 
					+ "order by 1 desc ";
			
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if (result.size() > 0) {
				lista=new ArrayList<RegistroOperador>();
				for (Object object : result) {
					lista.add(new RegistroOperador((Object[]) object));
				}
				return lista;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroOperador> obtenerUsuariosRegistrosSustanciasQuimicas() {		
		List<RegistroOperador> result = new ArrayList<RegistroOperador>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM RegistroSustanciaQuimica o WHERE o.estado=true ORDER BY 1 desc");
			List<RegistroSustanciaQuimica> lista= (List<RegistroSustanciaQuimica>)query.getResultList();
			
			for (RegistroSustanciaQuimica registroSustanciaQuimica : lista) {
				RegistroOperador registroOperador = new RegistroOperador();
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("name", registroSustanciaQuimica.getUsuarioCreacion());
				Usuario usuario = (Usuario) crudServiceBean.findByNamedQuerySingleResult(Usuario.FIND_BY_USER, params);
				
				if(usuario != null){
					params = new HashMap<String, Object>();
					params.put("ruc", usuario.getNombre());
					List<Organizacion> orgs = (List<Organizacion>) crudServiceBean.findByNamedQuery(Organizacion.FIND_BY_RUC, params);
					
					if(orgs != null && !orgs.isEmpty()){
						registroOperador.setNombreOperador(orgs.get(0).getNombre());
					}else{
						registroOperador.setNombreOperador(usuario.getPersona().getNombre());
					}			
					
//					if (usuario.getPersona().getOrganizaciones().size() == 0) {
//						registroOperador.setNombreOperador(usuario.getPersona().getNombre());
//					} else {
//						params = new HashMap<String, Object>();
//			            params.put("ruc", usuario.getNombre());
//						List<Organizacion> orgs = (List<Organizacion>) crudServiceBean.findByNamedQuery(Organizacion.FIND_BY_RUC, params);
//						registroOperador.setNombreOperador(orgs.get(0).getNombre());
//					}
					
					registroOperador.setRegistro(registroSustanciaQuimica);
					registroOperador.setUsuario(usuario);

					result.add(registroOperador);
				}else{
					
					registroOperador.setNombreOperador(registroSustanciaQuimica.getNombreRepLegal());
					
					registroOperador.setRegistro(registroSustanciaQuimica);

					result.add(registroOperador);
				}
			}
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroSustanciaQuimica> obtenerRegistrosSustanciasImportacionPorUsuario(Usuario usuario) {		
		List<RegistroSustanciaQuimica> lista=new ArrayList<RegistroSustanciaQuimica>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT o FROM RegistroSustanciaQuimica o WHERE o.estado=true and o.usuarioCreacion=:usuario ORDER BY 1 desc");
			query.setParameter("usuario", usuario.getNombre());
			lista= (List<RegistroSustanciaQuimica>)query.getResultList();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<RegistroSustanciaQuimica> obtenerRegistrosSustanciasDeclaracionPorUsuario(Usuario usuario) {		
		List<RegistroSustanciaQuimica> lista=new ArrayList<RegistroSustanciaQuimica>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM RegistroSustanciaQuimica o WHERE o.estado=true and o.usuarioCreacion=:usuario and o.vigenciaDesde != null ORDER BY 1 desc");
			query.setParameter("usuario", usuario.getNombre());
			lista= (List<RegistroSustanciaQuimica>)query.getResultList();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return lista;
	}	
	
	//nuevos metodos
	@SuppressWarnings("unchecked")
	public List<RegistroSustanciaQuimica>  obtenerRegistrosSustanciasQuimicas() {
		List<RegistroSustanciaQuimica> lista=new ArrayList<RegistroSustanciaQuimica>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM RegistroSustanciaQuimica o WHERE o.estado=true");
			
			lista= (List<RegistroSustanciaQuimica>)query.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return lista;
	}
	
	public List<RegistroSustanciaQuimica> obtenerRegistrosCompletos(){
		List<RegistroSustanciaQuimica> lista=new ArrayList<RegistroSustanciaQuimica>();
		try {
			
			String queryString = "select s.chsr_id, s.prco_id, s.chsr_identification_rep_legal, s.chsr_name_rep_legal, " 
					+ "s.chsr_application_number, s.chsr_valid_since, s.chsr_valid_until, "
					+ "l.peop_name, o.orga_name_organization, "
					+ "s.chsr_creator_user, s.user_id  "
					+ "from coa_chemical_sustances.chemical_sustances_records s "
					+ "left join public.users u on "
					+ "case when s.user_id is not null "
					+ "then  u.user_id = s.user_id "
					+ "else u.user_name = s.chsr_creator_user "
					+ "end "
					+ "left join public.people l on l.peop_id = u.peop_id "
					+ "left join public.organizations o on o.peop_id = l.peop_id  and o.orga_ruc = u.user_name "
					+ "where chsr_status = true and chsr_valid_until is not null order by o.orga_name_organization, l.peop_name ";
			
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if (result.size() > 0) {
				lista=new ArrayList<RegistroSustanciaQuimica>();
				for (Object object : result) {
					lista.add(new RegistroSustanciaQuimica((Object[]) object));
				}
				return lista;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<SustanciaQuimicaPeligrosa> listaSustanciasQuimicas()
	{
		List<SustanciaQuimicaPeligrosa> lista = new ArrayList<SustanciaQuimicaPeligrosa>();
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From SustanciaQuimicaPeligrosa s "
				+ "where s.controlSustancia = true and s.estado=true");
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	public void guardarRSQ(RegistroSustanciaQuimica obj, Usuario usuario){			
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}	
		crudServiceBean.saveOrUpdate(obj);
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroSustanciaQuimica> obtenerListaRegistroPorCodigo(String codigo) {	
		List<RegistroSustanciaQuimica> lista=new ArrayList<RegistroSustanciaQuimica>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM RegistroSustanciaQuimica o WHERE o.estado=true and o.numeroAplicacion=:codigo");
			query.setParameter("codigo", codigo);
			
			lista = (List<RegistroSustanciaQuimica>)query.getResultList();
			
			return lista;
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return lista;
	}
		
	public List<RegistroSustanciaQuimica> obtenerRegistrosPorUsuarioPermisoAnioMes(Integer anio, Integer mes, Usuario usuario){
		List<RegistroSustanciaQuimica> lista=new ArrayList<RegistroSustanciaQuimica>();
		try {
			
			String queryString = "select s.chsr_id, s.prco_id, s.chsr_identification_rep_legal, s.chsr_name_rep_legal, "
								+ "s.chsr_application_number, s.chsr_valid_since, s.chsr_valid_until, '' as nombre, '' as organizacion, "
								+ "s.chsr_creator_user, p.pest_month, p.pest_year, s.user_id  "
								+ "from coa_chemical_sustances.chemical_sustances_records s "
								+ "inner join coa_chemical_sustances.permission_statement p "
								+ "on s.chsr_id = p.chsr_id "
								+ "where "
								+ "case when s.user_id is not null "
								+ "then s.user_id = " + usuario.getId() 
								+ "else s.chsr_creator_user = '" + usuario.getNombre() + "' "
								+ "end "
								+ "and p.pest_monthly_statement_substances is true "
								+ "and s.chsr_status = true "
								+ "and p.pest_status = true "
								+ "and pest_parent_id is null "
								+ "and (p.pest_month = " + mes + " or p.pest_month < " + mes + ") "
								+ "and (p.pest_year = " + anio + " or p.pest_year > " + anio + ")";
			
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if (result.size() > 0) {
				lista=new ArrayList<RegistroSustanciaQuimica>();
				for (Object object : result) {
					lista.add(new RegistroSustanciaQuimica((Object[]) object));
				}
				return lista;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public List<RegistroSustanciaQuimica> obtenerRegistrosPorUsuarioPermisoAnio(Integer anio, Usuario usuario){
		List<RegistroSustanciaQuimica> lista=new ArrayList<RegistroSustanciaQuimica>();
		try {
			
			String queryString = "select s.chsr_id, s.prco_id, s.chsr_identification_rep_legal, s.chsr_name_rep_legal, "
					+ "s.chsr_application_number, s.chsr_valid_since, s.chsr_valid_until, '' as nombre, '' as organizacion, "
					+ "s.chsr_creator_user, p.pest_month, p.pest_year, s.user_id "
					+ "from coa_chemical_sustances.chemical_sustances_records s "
					+ "inner join coa_chemical_sustances.permission_statement p "
					+ "on s.chsr_id = p.chsr_id "
					+ "where "
					+ "case when s.user_id is not null "
					+ "then s.user_id = " + usuario.getId() 
					+ " else s.chsr_creator_user = '" + usuario.getNombre() + "' "
					+ "end "
					+ "and (p.pest_year = " + anio + " or p.pest_year > " + anio 
					+ ") and p.pest_monthly_statement_substances is true "
					+ "and s.chsr_status = true "
					+ "and p.pest_status = true "
					+ "and p.pest_parent_id is null";			

			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if (result.size() > 0) {
				lista=new ArrayList<RegistroSustanciaQuimica>();
				for (Object object : result) {
					lista.add(new RegistroSustanciaQuimica((Object[]) object));
				}
				return lista;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroSustanciaQuimica> obtenerTodosRegistrosPorCodigo(String codigo) {	
		List<RegistroSustanciaQuimica> lista=new ArrayList<RegistroSustanciaQuimica>();
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM RegistroSustanciaQuimica o WHERE UPPER(o.numeroAplicacion)=UPPER(:codigo) ORDER BY 1 DESC");
			query.setParameter("codigo", codigo);
			
			lista = (List<RegistroSustanciaQuimica>)query.getResultList();
			
			return lista;
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return lista;
	}
	
	public RegistroSustanciaQuimica obtenerRegistroPorCodigoDRSQ(String codigo) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o.registroSustanciaQuimica FROM DeclaracionSustanciaQuimica o WHERE o.estado=true and o.tramite=:codigo");
			query.setParameter("codigo", codigo);
			query.setMaxResults(1);
			return (RegistroSustanciaQuimica)query.getSingleResult();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return null;
	}
	
	public List<RegistroOperador> obtenerUsuariosRegistrosRSQProvisionales() {
		List<RegistroOperador> lista = new ArrayList<RegistroOperador>();
		
		try {
			String queryString = "select s.chsr_id, s.chsr_application_number, s.chsr_name_rep_legal, "
					+ "l.peop_name, o.orga_name_organization, "
					+ "s.chsr_creator_user, s.user_id "
					+ "from coa_chemical_sustances.chemical_sustances_records s "
					+ "left join public.users u on "
					+ "case when s.user_id is not null "
					+ "then  u.user_id = s.user_id "
					+ "else u.user_name = s.chsr_creator_user "
					+ "end "
					+ "left join public.people l on l.peop_id = u.peop_id "
					+ "left join public.organizations o on o.peop_id = l.peop_id  and o.orga_ruc = u.user_name "
					+ "where chsr_status = true and chsr_consumption = true "					
					+ "order by 1 desc ";
			
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if (result.size() > 0) {
				lista=new ArrayList<RegistroOperador>();
				for (Object object : result) {
					lista.add(new RegistroOperador((Object[]) object));
				}
				return lista;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}
	
	public RegistroSustanciaQuimica obtenerRegistroPorCodigoConsumo(String codigo) {		
		try {
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM RegistroSustanciaQuimica o WHERE o.estado=true and o.numeroAplicacion=:codigo and o.registroConsumo = true");
			query.setParameter("codigo", codigo);
			query.setMaxResults(1);
			return (RegistroSustanciaQuimica)query.getSingleResult();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return null;
	}
	
	public String generarCodigoRSQ(String secuencia, String tipoPersona) {//SCA-DSRD-0001J-2023
		try {
			String nombreSecuencia=secuencia+"-RSQ-"+secuenciasFacade.getCurrentYear();
			return secuencia +"-" 
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia,4)
					+ tipoPersona
					+ "-"
					+ secuenciasFacade.getCurrentYear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
