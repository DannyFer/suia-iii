package ec.gob.ambiente.rcoa.viabilidadTecnica.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformeTecnicoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.OficioPronunciamientoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnicaProyecto;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class ViabilidadTecnicaProyectoFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	@EJB
	private InformeTecnicoEsIAFacade informeTecnicoEsIAFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private OficioPronunciamientoEsIAFacade oficioPronunciamientoEsIAFacade;
	
	
	public void guardar(ViabilidadTecnicaProyecto obj, Usuario usuario){
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
	
	public ViabilidadTecnicaProyecto buscarPorId(Integer id){
		try {
			return (ViabilidadTecnicaProyecto)crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM ViabilidadTecnicaProyecto o where o.id = :id")
					.setParameter("id", id).getSingleResult(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ViabilidadTecnicaProyecto> buscarPorCodigoOficioExiste(String codigo){
		
		List<ViabilidadTecnicaProyecto> lista = new ArrayList<ViabilidadTecnicaProyecto>();
		
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("Select v from ViabilidadTecnicaProyecto v where "
					+ "v.viabilidadTecnica.numeroOficio = :codigo and v.estado = true");
			sql.setParameter("codigo", codigo);
			
			lista = (List<ViabilidadTecnicaProyecto>)sql.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<ViabilidadTecnicaProyecto> buscarPorCodigoOficioProyectoExiste(String codigo, Integer id){
		
		List<ViabilidadTecnicaProyecto> lista = new ArrayList<ViabilidadTecnicaProyecto>();
		
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("Select v from ViabilidadTecnicaProyecto v where "
					+ "v.viabilidadTecnica.numeroOficio = :codigo and v.proyecto.id = :id and v.estado = true ");
			sql.setParameter("codigo", codigo);
			sql.setParameter("id", id);
			
			lista = (List<ViabilidadTecnicaProyecto>)sql.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<ViabilidadTecnicaProyecto> buscarPorProyecto(Integer id){
		
		List<ViabilidadTecnicaProyecto> lista = new ArrayList<ViabilidadTecnicaProyecto>();
		
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("Select v from ViabilidadTecnicaProyecto v where "
					+ "v.proyecto.id = :id and v.estado = true ");			
			sql.setParameter("id", id);
			
			lista = (List<ViabilidadTecnicaProyecto>)sql.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<ViabilidadTecnicaProyecto> buscarPorCodigoOficioProyectoTipoExiste(String codigo, Integer idProyecto, int tipoViabilidad){
		
		List<ViabilidadTecnicaProyecto> lista = new ArrayList<ViabilidadTecnicaProyecto>();
		
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("Select v from ViabilidadTecnicaProyecto v where "
					+ "v.viabilidadTecnica.numeroOficio = :codigo and v.proyecto.id = :id and v.estado = true "
					+ "and v.viabilidadTecnica.tipoViabilidad = :tipoViabilidad");
			sql.setParameter("codigo", codigo);
			sql.setParameter("id", idProyecto);
			sql.setParameter("tipoViabilidad", tipoViabilidad);
			
			lista = (List<ViabilidadTecnicaProyecto>)sql.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<ViabilidadTecnicaProyecto> buscarPorViabilidad(Integer idViabilidad){
		List<ViabilidadTecnicaProyecto> lista = new ArrayList<ViabilidadTecnicaProyecto>();
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("Select v from ViabilidadTecnicaProyecto v where "
					+ "v.estado = true and v.viabilidadTecnica.id = :idViabilidad");
			sql.setParameter("idViabilidad", idViabilidad);
			
			lista = (List<ViabilidadTecnicaProyecto>)sql.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public boolean validarArchivoProyecto(ProyectoLicenciaCoa proyecto){		
		try {
			
			InformacionProyectoEia esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
			
			if(esiaProyecto != null && esiaProyecto.getId() != null){
				InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, InformeTecnicoEsIA.consolidado);
				
				if(informeTecnico != null && informeTecnico.getId() != null && informeTecnico.getTipoPronunciamiento() != null){
					if(informeTecnico.getTipoPronunciamiento() == 3 || informeTecnico.getTipoPronunciamiento() == 4){
						return true; //se archivo el estudio
					}
				}
								
				OficioPronunciamientoEsIA oficioArchivo = oficioPronunciamientoEsIAFacade.getPorEstudioTipoOficio(esiaProyecto.getId(), OficioPronunciamientoEsIA.oficioArchivoAutomatico);
				if(oficioArchivo == null || oficioArchivo.getId() == null){
					return true;
				}		
			}
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	


}
