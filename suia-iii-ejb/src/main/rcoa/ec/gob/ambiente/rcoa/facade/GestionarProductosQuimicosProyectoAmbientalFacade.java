package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.BienesServiciosInventarioForestal;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.retce.model.DescargasLiquidas;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class GestionarProductosQuimicosProyectoAmbientalFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(GestionarProductosQuimicosProyectoAmbiental sustancia) {        
        	crudServiceBean.saveOrUpdate(sustancia);        
    }
	
	@SuppressWarnings("unchecked")
	public List<GestionarProductosQuimicosProyectoAmbiental> listaSustanciasQuimicas(ProyectoLicenciaCoa proyecto)
	{
		List<GestionarProductosQuimicosProyectoAmbiental> lista = new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From GestionarProductosQuimicosProyectoAmbiental s where s.proyectoLicenciaCoa=:proyecto and s.gestionaSustancia=:gestionaSustancia and s.sustanciaquimica.id!=:sustanciaquimicaId and s.estado=true");
		sql.setParameter("proyecto", proyecto);
		sql.setParameter("gestionaSustancia", true);
		sql.setParameter("sustanciaquimicaId", Constantes.ID_SUSTANCIA_OTROS);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	public List<GestionarProductosQuimicosProyectoAmbiental> listaSustanciasQuimicasTransporta(ProyectoLicenciaCoa proyecto)
	{
		List<GestionarProductosQuimicosProyectoAmbiental> lista = new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From GestionarProductosQuimicosProyectoAmbiental s where s.proyectoLicenciaCoa=:proyecto and s.transportaSustancia=:transportaSustancia and s.sustanciaquimica.id!=:sustanciaquimicaId and s.estado=true");
		sql.setParameter("proyecto", proyecto);
		sql.setParameter("transportaSustancia", true);
		sql.setParameter("sustanciaquimicaId", Constantes.ID_SUSTANCIA_OTROS);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	public void eliminar(ProyectoLicenciaCoa proyecto){		
		 Query sqlUpdate = crudServiceBean.getEntityManager().createQuery("update GestionarProductosQuimicosProyectoAmbiental g set g.estado=false where g.proyectoLicenciaCoa.id=:id");
		 sqlUpdate.setParameter("id", proyecto.getId());
		 sqlUpdate.executeUpdate();		
	 }
	
	public GestionarProductosQuimicosProyectoAmbiental getSustanciaById (ProyectoLicenciaCoa proyecto, Integer id ) {

		GestionarProductosQuimicosProyectoAmbiental result = new GestionarProductosQuimicosProyectoAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT s FROM GestionarProductosQuimicosProyectoAmbiental s WHERE s.proyectoLicenciaCoa.id=:proyecto and s.sustanciaquimica.id=:id and s.estado=true");
		sql.setParameter("proyecto", proyecto.getId());
		sql.setParameter("id", id);
		if (sql.getResultList().size() > 0)
			result = (GestionarProductosQuimicosProyectoAmbiental) sql.getResultList().get(0);
		return result;		
		
	}
	
	public GestionarProductosQuimicosProyectoAmbiental getSustanciaByIdByDescri(ProyectoLicenciaCoa proyecto, Integer id , String otraSustancia) {
		GestionarProductosQuimicosProyectoAmbiental result = new GestionarProductosQuimicosProyectoAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT s FROM GestionarProductosQuimicosProyectoAmbiental s WHERE s.proyectoLicenciaCoa.id=:proyecto and s.sustanciaquimica.id=:id and s.otraSustancia=:otraSustancia and s.estado=true");
		sql.setParameter("proyecto", proyecto.getId());
		sql.setParameter("id", id);
		sql.setParameter("otraSustancia", otraSustancia);
		if (sql.getResultList().size() > 0) {
			result = (GestionarProductosQuimicosProyectoAmbiental) sql.getResultList().get(0);
		}else {
			result=null;
		}
		return result;		
		
	}
	
	public List<GestionarProductosQuimicosProyectoAmbiental> listaSustanciasQuimicasOtros(ProyectoLicenciaCoa proyecto)
	{
		List<GestionarProductosQuimicosProyectoAmbiental> lista = new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From GestionarProductosQuimicosProyectoAmbiental s where s.proyectoLicenciaCoa=:proyecto and s.gestionaSustancia=true and s.sustanciaquimica.id=:sustanciaquimicaId and s.estado=true");
		sql.setParameter("proyecto", proyecto);		
		sql.setParameter("sustanciaquimicaId", Constantes.ID_SUSTANCIA_OTROS);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	public List<GestionarProductosQuimicosProyectoAmbiental> listaSustanciasQuimicasOtrosTransporta(ProyectoLicenciaCoa proyecto)
	{
		List<GestionarProductosQuimicosProyectoAmbiental> lista = new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From GestionarProductosQuimicosProyectoAmbiental s where s.proyectoLicenciaCoa=:proyecto and transportaSustancia=true and s.sustanciaquimica.id=:sustanciaquimicaId and s.estado=true");
		sql.setParameter("proyecto", proyecto);
		sql.setParameter("sustanciaquimicaId", Constantes.ID_SUSTANCIA_OTROS);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	public List<GestionarProductosQuimicosProyectoAmbiental> listaSustanciasQuimicasControl(ProyectoLicenciaCoa proyecto) 
	{
		List<GestionarProductosQuimicosProyectoAmbiental> lista = new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From GestionarProductosQuimicosProyectoAmbiental s where s.proyectoLicenciaCoa=:proyecto and s.controlSustancia=true and s.sustanciaquimica.id!=:sustanciaquimicaId and s.estado=true");
		sql.setParameter("proyecto", proyecto);
		sql.setParameter("sustanciaquimicaId", Constantes.ID_SUSTANCIA_OTROS);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}

	public GestionarProductosQuimicosProyectoAmbiental buscarPorId(Integer id) 
	{
		List<GestionarProductosQuimicosProyectoAmbiental> lista = new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From GestionarProductosQuimicosProyectoAmbiental s where s.id=:id and s.estado=true");
		sql.setParameter("id", id);

		lista = (ArrayList<GestionarProductosQuimicosProyectoAmbiental>)sql.getResultList();

		if(lista.size()>0){
			return lista.get(0);
		}	
		return null;		
	}
	
	@SuppressWarnings("unchecked")
	public List<GestionarProductosQuimicosProyectoAmbiental> listadoSustanciasQuimicasProyectoRLA(Integer id) 
	{
		List<GestionarProductosQuimicosProyectoAmbiental> lista = new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From GestionarProductosQuimicosProyectoAmbiental s where s.proyectoLicenciaCoa.id=:id and s.gestionaSustancia = true and s.estado=true");
		sql.setParameter("id", id);
		lista = (ArrayList<GestionarProductosQuimicosProyectoAmbiental>)sql.getResultList();
		if((lista != null) && (lista.size()>0)){
			return lista;
		}	
		return null;		
	}
	
	
}
