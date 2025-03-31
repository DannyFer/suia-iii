package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.ReporteSustanciasQuimicasPeligrosas;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class ReporteSustanciasQuimicasPeligrosasFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	public ReporteSustanciasQuimicasPeligrosas findById(Integer id){
		try {
			ReporteSustanciasQuimicasPeligrosas sustancia = (ReporteSustanciasQuimicasPeligrosas) crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT o FROM ReporteSustanciasQuimicasPeligrosas o where o.id = id")
					.setParameter("id", id).getSingleResult();
			return sustancia;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ReporteSustanciasQuimicasPeligrosas save(ReporteSustanciasQuimicasPeligrosas obj, Usuario usuario){
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
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public List<ReporteSustanciasQuimicasPeligrosas> findByInformacionProyecto(InformacionProyecto informacion){
		List<ReporteSustanciasQuimicasPeligrosas> lista = new ArrayList<ReporteSustanciasQuimicasPeligrosas>();
		try {			
			lista = (ArrayList<ReporteSustanciasQuimicasPeligrosas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM ReporteSustanciasQuimicasPeligrosas o where o.estado = true and o.informacionProyecto.id = :id order by 1 desc")
					.setParameter("id", informacion.getId()).getResultList();
			
			return lista;			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<ReporteSustanciasQuimicasPeligrosas> findByUser(Usuario usuario){
		List<ReporteSustanciasQuimicasPeligrosas> lista = new ArrayList<ReporteSustanciasQuimicasPeligrosas>();
		try {			
			lista = (ArrayList<ReporteSustanciasQuimicasPeligrosas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM ReporteSustanciasQuimicasPeligrosas o where o.estado = true and o.usuarioCreacion = :usuario order by 1 desc")
					.setParameter("usuario", usuario.getNombre()).getResultList();
			
			return lista;			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	private String generarCodigo() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-RETCE-SQP"
					+ "-"
					+ secuenciasFacade.getCurrentYear()					
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence("MAAE-RETCE-SQP",4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public ReporteSustanciasQuimicasPeligrosas getByInformacionProyectoAnio(Integer idInformacionProyecto, Integer anio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idInformacionProyecto);
		parameters.put("anio", anio);
		try {
			List<ReporteSustanciasQuimicasPeligrosas> lista = (List<ReporteSustanciasQuimicasPeligrosas>) crudServiceBean.findByNamedQuery(ReporteSustanciasQuimicasPeligrosas.GET_POR_ANIO_INFORMACION_PROYECTO,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
}
