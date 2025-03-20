package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class RegistroGeneradorDesechosRcoaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();
	
	public RegistroGeneradorDesechosRcoa findById(Integer id){
		try {
			RegistroGeneradorDesechosRcoa registroGenerador = (RegistroGeneradorDesechosRcoa) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM RegistroGeneradorDesechosRcoa o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			
			return registroGenerador;			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(RegistroGeneradorDesechosRcoa obj, Usuario usuario){
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

	
	public void saveREP(RegistroGeneradorDesechosRcoa obj, Usuario usuario){
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
	
	private String generarCodigo() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-SOL-RGD"
					+ "-"
					+ secuenciasFacade.getCurrentYear()					
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence("MAAE-SOL-RGD",4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoLicenciaCuaCiuu> buscarActividadesCiu(ProyectoLicenciaCoa proyecto){
		try {
			
			List<ProyectoLicenciaCuaCiuu> lista = (List<ProyectoLicenciaCuaCiuu>) crudServiceBean.getEntityManager()
					.createQuery("SELECT a FROM ProyectoLicenciaCuaCiuu a where proyectoLicenciaCoa = :proyecto and a.primario=false order by orderJerarquia asc")
					.setParameter("proyecto", proyecto).getResultList();
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoLicenciaCuaCiuu> buscarActividadesCiuPrincipal(ProyectoLicenciaCoa proyecto){
		try {
			
			List<ProyectoLicenciaCuaCiuu> lista = (List<ProyectoLicenciaCuaCiuu>) crudServiceBean.getEntityManager()
					.createQuery("SELECT a FROM ProyectoLicenciaCuaCiuu a where a.proyectoLicenciaCoa = :proyecto and a.primario=true")
					.setParameter("proyecto", proyecto).getResultList();
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}		
	
	@SuppressWarnings("unchecked")
	public RegistroGeneradorDesechosRcoa buscarRGDPorProyectoRcoa(Integer id){		
		try {
			
			List<RegistroGeneradorDesechosRcoa> lista = (List<RegistroGeneradorDesechosRcoa>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o.registroGeneradorDesechosRcoa FROM RegistroGeneradorDesechosProyectosRcoa o where o.proyectoLicenciaCoa.id = :id and o.estado = true order by o.id desc")
					.setParameter("id", id).getResultList();
			if(lista != null && lista.size() > 0)
				return lista.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public RegistroGeneradorDesechosRcoa buscarRGDFPorProyectoRcoa(Integer id){		
		try {
			
			List<RegistroGeneradorDesechosRcoa> lista = (List<RegistroGeneradorDesechosRcoa>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o.registroGeneradorDesechosRcoa FROM RegistroGeneradorDesechosProyectosRcoa o where o.idPadreHistorial = :id and o.estado = true order by o.id desc")
					.setParameter("id", id).getResultList();
			if(lista != null && lista.size() > 0)
				return lista.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public RegistroGeneradorDesechosRcoa buscarRGDPorProyectoSuia(Integer id){		
		try {
			
			List<RegistroGeneradorDesechosRcoa> lista = (List<RegistroGeneradorDesechosRcoa>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o.registroGeneradorDesechosRcoa FROM RegistroGeneradorDesechosProyectosRcoa o where o.proyectoId = :id and o.estado = true order by o.id desc")
					.setParameter("id", id).getResultList();
			if(lista != null && lista.size() > 0)
				return lista.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public RegistroGeneradorDesechosRcoa buscarRGDPorCodigo(String codigo){		
		try {
			List<RegistroGeneradorDesechosRcoa> lista = (List<RegistroGeneradorDesechosRcoa>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM RegistroGeneradorDesechosRcoa o where o.codigo = :codigo and o.estado = true order by o.id desc")
					.setParameter("codigo", codigo).getResultList();
			if(lista != null && lista.size() > 0)
				return lista.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public RegistroGeneradorDesechosRcoa buscarRGDPorProyectoDigitalizado(Integer codigoProyectoId){		
		try {
			List<RegistroGeneradorDesechosRcoa> lista = (List<RegistroGeneradorDesechosRcoa>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o.registroGeneradorDesechosRcoa FROM RegistroGeneradorDesechosProyectosRcoa o where o.proyectoDigitalizado.id = :codigoId and o.estado = true order by o.id desc")
					.setParameter("codigoId", codigoProyectoId).getResultList();
			if(lista != null && lista.size() > 0)
				return lista.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public RegistroGeneradorDesechosRcoa RegistroGeneradorActivosResolucionLA(String idDocumento)
	{
		try 
		{
			RegistroGeneradorDesechosRcoa obj = new RegistroGeneradorDesechosRcoa();
			StringBuilder query = new StringBuilder();
			query.append("select t2.* from ");
			query.append("coa_waste_generator_record.waste_generator_record_coa t2 ");
			query.append("INNER JOIN coa_waste_generator_record.waste_generator_record_document t3 ");
			query.append("on t2.ware_id = t3.ware_id and t2.ware_status = true and t3.wgrd_status = true and t3.wgrd_document_number = '" + idDocumento + "' ");
			@SuppressWarnings("unchecked")
			List<RegistroGeneradorDesechosRcoa> lista = (List<RegistroGeneradorDesechosRcoa>) crudServiceBean.findNativeQuery(query.toString(), RegistroGeneradorDesechosRcoa.class);
			if ((lista != null) && (lista.size() > 0)) {
				obj = lista.get(0);
			}
			else
			{
				obj = null;
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Boolean RGDPagado(String proyecto) throws ServiceException {
		Boolean finalized = false;
		try {
			String sql="select * "
				+ "from dblink('"+dblinkBpmsSuiaiii+"','select bt.pk from bamtasksummary bt where processinstanceid in (select processinstanceid from variableinstancelog  where value  = ''"+proyecto+"'' and processid = ''rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales''  order by 1 limit 1)"
				+ "and bt.taskname is not null and (lower(bt.taskname) like ''%pago%'' or lower(bt.taskname) like ''%valor %'') and bt.status in (''Completed'')') as (estado integer)";			
			Query q = crudServiceBean.getEntityManager().createNativeQuery(sql);		
			if(q.getResultList().size()>0) {
				finalized = true;
			}
		} catch (Exception e) {
			return false;
		}
		return finalized;
	}
	
	public Boolean RGDPagadoSinProyectos(String rgd) throws ServiceException {
		Boolean finalized = false;
		try {
			String sql="select * "
				+ "from dblink('"+dblinkBpmsSuiaiii+"','select bt.pk from bamtasksummary bt where processinstanceid in (select processinstanceid from variableinstancelog  where value  = ''"+rgd+"'' and processid in (''rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales'',''mae-procesos.GeneradorDesechos'')  order by 1 limit 1)"
				+ "and bt.taskname is not null and (lower(bt.taskname) like ''%pago%'' or lower(bt.taskname) like ''%valor %'') and bt.status in (''Completed'')') as (estado integer)";			
			Query q = crudServiceBean.getEntityManager().createNativeQuery(sql);		
			if(q.getResultList().size()>0) {
				finalized = true;
			}
		} catch (Exception e) {
			return false;
		}
		return finalized;
	}
}
