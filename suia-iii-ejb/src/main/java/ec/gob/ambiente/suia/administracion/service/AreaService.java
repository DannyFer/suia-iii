/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ResponsableSustanciaQuimica;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.TipoArea;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class AreaService {

	@EJB
	private CrudServiceBean crudServiceBean;

	public List<Area> listarAreasPadre() throws ServiceException {
		List<Area> lista = null;
		try {
			lista = (List<Area>) crudServiceBean.findByNamedQuery(
					Area.LISTAR_PADRES, null);
			for (Area area : lista) {
				if(area.getTipoArea() != null)
					area.getTipoArea().toString();
			}
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return lista;
	}
	
	public List<Area> listarAreasPadreInstitucional(Area areas) throws ServiceException {
		List<Area> lista = null;
		try {
//			lista = (List<Area>) crudServiceBean.findByNamedQuery(
//					Area.LISTAR_PADRES, null);
			lista = crudServiceBean
					.getEntityManager()
					.createQuery(
							" FROM Area a where a.areaName = :areaName and a.area IS NULL and a.estado = true ORDER BY a.areaName")
					.setParameter("areaName", areas.getAreaName())
					.getResultList();
			
			for (Area area : lista) {
				if(area.getTipoArea() != null)
					area.getTipoArea().toString();
			}
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return lista;
	}
	
	public List<Area> listarAreasPadreInstitucionalTotal(Area areas) throws ServiceException {
		List<Area> lista = null;
		try {
//			lista = (List<Area>) crudServiceBean.findByNamedQuery(
//					Area.LISTAR_PADRES, null);
			lista = crudServiceBean
					.getEntityManager()
					.createQuery(
							" FROM Area a where a.areaName = :areaName and a.estado = true ORDER BY a.areaName")
					.setParameter("areaName", areas.getAreaName())
					.getResultList();
			
			for (Area area : lista) {
				if(area.getTipoArea() != null)
					area.getTipoArea().toString();
			}
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return lista;
	}

	public List<Area> listarAreasHijos(final Area areaPadre)
			throws ServiceException {
		List<Area> lista = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("area", areaPadre);
			lista = (List<Area>) crudServiceBean.findByNamedQuery(
					Area.LISTAR_HIJOS, params);
			for (Area area : lista) {
				if(area.getTipoArea() != null)
					area.getTipoArea().toString();
			}
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return lista;
	}

	public List<Area> getAreas(TipoArea tipo) {
		return crudServiceBean.getEntityManager()
				.createQuery(" FROM Area a where a.tipoArea =:tipo order by a.areaName")
				.setParameter("tipo", tipo).getResultList();

	}

	public List<Area> getAreasAll() {
		return crudServiceBean.getEntityManager()
				.createQuery(" FROM Area a where a.estado = true ORDER BY a.areaName ")
				.getResultList();
	}

	public Area getArea(UbicacionesGeografica ubicacionesGeografica,TipoArea tipo) {
		try {
			return (Area) crudServiceBean
					.getEntityManager()
					.createQuery(
							" FROM Area a where a.estado=true and a.area=null and a.tipoArea =:tipo and a.ubicacionesGeografica=:ubicacion")
					.setParameter("tipo", tipo)
					.setParameter("ubicacion", ubicacionesGeografica)
					.setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException e) {
			System.out.println("ADVERTENCIA: Area ("+tipo.getNombre()+") no encontrada en la ubicacion "+ubicacionesGeografica.getNombre());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
		
	}
	
	/**
	 * Obtener el area responsable
	 * (Coordinacion Zonal para Provincias y Oficina Tecnica para cantones)
	 * @param ubicacionesGeografica(provincia o canton)
	 * @return
	 */
	public Area getAreaCoordinacionZonal(UbicacionesGeografica ubicacionesGeografica) {
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT a FROM UbicacionesGeografica u,Area a WHERE u.estado=true and u.areaCoordinacionZonal.id=a.id and a.estado=true and u.id=:idUbicacion ORDER BY 1");
			query.setParameter("idUbicacion", ubicacionesGeografica.getId());
			query.setMaxResults(1);
			return (Area)query.getSingleResult();			
		} catch (NoResultException e) {
			if(ubicacionesGeografica.getCodificacionInec().length()==2) {
				System.out.println("ADVERTENCIA: Coordinacion Zonal no encontrada en la ubicacion "+ubicacionesGeografica.getNombre());
			}else if(ubicacionesGeografica.getCodificacionInec().length()==4) {
				System.out.println("ADVERTENCIA: Oficina Tecnica no encontrada en la ubicacion "+ubicacionesGeografica.getNombre());
			}else {
				System.out.println("ERROR: Area no encontrada en la ubicacion "+ubicacionesGeografica.getNombre());
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}	
		return null;		
	}

	public Area getArea(Integer id) {
		return (Area) crudServiceBean.getEntityManager()
				.createQuery(" FROM Area a where a.id =:id ")
				.setParameter("id", id).getSingleResult();

	}
	
	public Area getArea(String nombreArea) {
		try {
			return (Area) crudServiceBean.getEntityManager()
					.createQuery(" FROM Area a where a.estado=true and a.areaName =:nombreArea order by 1")
					.setParameter("nombreArea", nombreArea)
					.setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException e) {
			System.out.println("Area no encontrada con el nombre: "+nombreArea);
		}
		return null;
	}

	public Area getAreaSiglas(String sigla) {
		try {
		return (Area) crudServiceBean.getEntityManager()
				.createQuery(" FROM Area a where a.areaAbbreviation =:sigla ")
				.setParameter("sigla", sigla).getSingleResult();
		} catch (NoResultException e) {
			System.out.println("Area Siglas no encontrada");
		}
		return null;
	}

	public Area getAreaTipoAreaUbicacion(TipoArea tipoArea,
			UbicacionesGeografica ubicacionesGeografica) {
		List<Area> areas = crudServiceBean
				.getEntityManager()
				.createQuery(
						" FROM Area a where a.tipoArea = :tipoArea and a.ubicacionesGeografica = :ubicacionesGeografica")
				.setParameter("tipoArea", tipoArea)
				.setParameter("ubicacionesGeografica", ubicacionesGeografica)
				.getResultList();
		if (areas.isEmpty())
			return null;
		else
			return areas.get(0);

	}

	public Area getPlantaCentral() {
		TipoArea tipo = getTipoArea(Constantes
				.getRoleAreaName("area.plantacentral"));
		List<Area> listaArea = new ArrayList<Area>();
		if (tipo != null) {
			listaArea = crudServiceBean
					.getEntityManager()
					.createQuery(
							" FROM Area a where a.tipoArea =:tipo AND areaAbbreviation= :area ")
					.setParameter("tipo", tipo)
					.setParameter(
							"area",
							Constantes
									.getRoleAreaName("area.plantacentral.coordinador"))
					.getResultList();
			if (listaArea.isEmpty())
				return null;
			else
				return listaArea.get(0);
		} else {

			return null;
		}
	}

	public TipoArea getTipoArea(String siglas) {
		return (TipoArea) crudServiceBean.getEntityManager()
				.createQuery(" FROM TipoArea t where t.siglas =:tipo ")
				.setParameter("tipo", siglas).getSingleResult();
	}

	public Area getAreaDireccionProvincialPorUbicacion(Integer idTipo,
			UbicacionesGeografica ubicacionesGeografica) {
		List<Area> listaAreas = crudServiceBean
				.getEntityManager()
				.createQuery(
						" FROM Area a where a.tipoArea.id = :tipoArea and a.ubicacionesGeografica = :ubicacionesGeografica and a.area is null")
				.setParameter("tipoArea", idTipo)
				.setParameter("ubicacionesGeografica", ubicacionesGeografica)
				.getResultList();
		if (listaAreas.isEmpty())
			return null;
		else
			return listaAreas.get(0);

	}

	public Area getAreaEntePorUbicacion(Integer idTipo,
			UbicacionesGeografica ubicacionesGeografica) {
		UbicacionesGeografica parroquia = crudServiceBean.find(
				UbicacionesGeografica.class, ubicacionesGeografica.getId());

		if (parroquia.getEnteAcreditado()!=null)
			parroquia.getEnteAcreditado().getId();

		// List<Area> listaAreas = crudServiceBean
		// .getEntityManager()
		// .createQuery(
		// " FROM Area a where a.tipoArea.id = :tipoArea and a.ubicacionesGeografica = :ubicacionesGeografica")
		// .setParameter("tipoArea",
		// idTipo).setParameter("ubicacionesGeografica", ubicacionesGeografica)
		// .getResultList();
		// // if (listaAreas.isEmpty())
		// return null;
		// else
		// return listaAreas.get(0);
		return parroquia.getEnteAcreditado();
	}

	@SuppressWarnings("unchecked")
	public Area getAreaGadProvincial(Integer idTipo,UbicacionesGeografica ubicacionesGeografica) {
		List<Area> listaAreas = crudServiceBean
				.getEntityManager()
				.createQuery(
						" FROM Area a where a.tipoArea.id = :tipoArea and a.ubicacionesGeografica =:ubicacionesGeografica "
					  + " and a.area.id is null and a.tipoEnteAcreditado='GOBIERNO'")
				.setParameter("tipoArea", idTipo)
				.setParameter("ubicacionesGeografica", ubicacionesGeografica)
				.getResultList();
		if (listaAreas.isEmpty())
			return null;
		else
			return listaAreas.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public Area getAreaEnteAcreditado(Integer idTipo,String userNameLogin) {
		List<Area> listaAreas = crudServiceBean
				.getEntityManager()
				.createQuery(
						" FROM Area a where a.tipoArea.id = :tipoArea and a.identificacionEnte =:identificacionUsuario")
				.setParameter("tipoArea", idTipo)
				.setParameter("identificacionUsuario", userNameLogin)
				.getResultList();
		if (listaAreas.isEmpty())
			return null;
		else
			return listaAreas.get(0);
	}

	@SuppressWarnings("unchecked")
	public Area getAreaPorAreaAbreviacion(String areaAbbreviation) {
		List<Area> listaAreas = crudServiceBean
				.getEntityManager()
				.createQuery(
						" FROM Area a where a.areaAbbreviation =:areaAbbreviation")
				.setParameter("areaAbbreviation", areaAbbreviation).getResultList();
		if (listaAreas.isEmpty())
			return null;
		else
			return listaAreas.get(0);
	}
	

//	public Area getAreaPadrePorTipoEnteAcreditado(Integer idTipo,String tipoEnteAcreditado,UbicacionesGeografica ubicacionesGeografica){
//		List<Area> listaAreas = crudServiceBean.getEntityManager().createQuery(" FROM Area a where"
//				+ " a.tipoArea.id = :tipoArea AND a.tipoEnteAcreditado=:tipoEnteAcre AND a.ubicacionesGeografica = :ubicacionesGeografica "
//				+ "and a.area is null").setParameter("tipoArea", idTipo)
//				.setParameter("ubicacionesGeografica", ubicacionesGeografica).setParameter("tipoEnteAcre", tipoEnteAcreditado).getResultList();
//		if (listaAreas.isEmpty())
//		 return null;
//		 else
//		 return listaAreas.get(0);
//	}

	@SuppressWarnings("unchecked")
	public Area getAreaDireccionProvincialPorRango(Integer idUbicacion) {
	List<Area> listaAreas = crudServiceBean
			.getEntityManager()
			.createQuery(
					" FROM Area a where a.ubicacionesGeografica.id = :ubicacionesGeografica and tipoEnteAcreditado ='GOBIERNO' and a.area is null")
			.setParameter("ubicacionesGeografica", idUbicacion)
			.getResultList();
	if (listaAreas.isEmpty())
		return null;
	else
		return listaAreas.get(0);
}	
	
	@SuppressWarnings("unchecked")
	public Area getAreaDireccionMunicipioPorRango(Integer idUbicacion) {
		List<Area> listaAreas = crudServiceBean
				.getEntityManager()
				.createQuery(
						" FROM Area a where a.ubicacionesGeografica.id = :ubicacionesGeografica and tipoEnteAcreditado ='MUNICIPIO' and a.area is null")
				.setParameter("ubicacionesGeografica", idUbicacion)
				.getResultList();
		if (listaAreas.isEmpty())
			return null;
		else
			return listaAreas.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public String getAreaUsuario(Usuario usuario) {
		String listaAreas =null;
		try {
			listaAreas = (String) crudServiceBean
					.getEntityManager()
					.createNativeQuery(
							"select area_name from areas a, users u where a.area_id=u.area_id and u.user_name=?")
					.setParameter(1, usuario.getNombre())
					.getSingleResult();
//			if (listaAreas.isEmpty())
//				return null;
//			else
				
		} catch (Exception e) {
			// TODO: handle exception
		}
		return listaAreas;
		
	}
	
	
	public Area getAreaEnteMunicipioPorUbicacion(Integer idTipo,
			UbicacionesGeografica ubicacionesGeografica) {
		UbicacionesGeografica parroquia = crudServiceBean.find(
				UbicacionesGeografica.class, ubicacionesGeografica.getId());
		return parroquia.getEnteAcreditadomunicipio();
	}

	public Area getAreasFacil(String nombre) {
		return (Area)crudServiceBean.getEntityManager()
				.createQuery(" FROM Area a where a.areaName =:tipo ")
				.setParameter("tipo", nombre).getSingleResult();

	}	

	public Area getAreasPadre(Integer area) {
		Area areas= new Area();
		try {
			areas= (Area) crudServiceBean.getEntityManager()
					.createQuery(" select a.area from Area a where a.id=:area").setParameter("area", area).getSingleResult();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return areas;

	}	
	
	@SuppressWarnings("unchecked")
	public List<Area> listarAreasPadreOT() throws ServiceException {
		List<Area> lista = null;
		try {
			lista = (List<Area>) crudServiceBean.getEntityManager()
					.createQuery(" select a from Area a where a.tipoArea.siglas = 'ZONALES'").getResultList();
			for (Area area : lista) {
				if(area.getTipoArea() != null)
					area.getTipoArea().toString();
			}
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Area> listarAreasPadreSinOT() throws ServiceException {
		List<Area> lista = null;
		try {
			lista = (List<Area>) crudServiceBean.getEntityManager()
					.createQuery(" select a from Area a where a.tipoArea.siglas is not 'OT'").getResultList();
			for (Area area : lista) {
				if(area.getTipoArea() != null)
					area.getTipoArea().toString();
			}
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public Area getAreaPorId(Integer idArea) {
		try {
			return (Area) crudServiceBean
					.getEntityManager()
					.createNativeQuery(
							"select * from areas where area_id=?", Area.class)
					.setParameter(1, idArea)
					.getSingleResult();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new Area();
	}
	
	@SuppressWarnings("unchecked")
	public List<Area> listarDireccionesZonales() throws ServiceException {
		List<Area> lista = null;
		try {
			lista = (List<Area>) crudServiceBean.getEntityManager()
					.createQuery(" select a from Area a where a.tipoArea.siglas = 'ZONALES' order by a.areaName").getResultList();
			for (Area area : lista) {
				if(area.getTipoArea() != null)
					area.getTipoArea().toString();
			}
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Area> areaGalapagos() throws ServiceException {
		List<Area> lista = null;
		try {
			lista = (List<Area>) crudServiceBean.getEntityManager()
					.createQuery(" select a from Area a where a.id = 272 order by a.areaName").getResultList();
			for (Area area : lista) {
				if(area.getTipoArea() != null)
					area.getTipoArea().toString();
			}
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return lista;
	}
	
}
