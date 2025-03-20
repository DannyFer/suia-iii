package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InterseccionViabilidadCoaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private CapasCoaFacade capasCoaFacade;
	
	public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();
	
	public String dblinkIdeMaae = Constantes.getDblinkIdeMaae();
	
	@SuppressWarnings("unchecked")
	public List<InterseccionProyectoLicenciaAmbiental> getInterseccionesViabilidadPorProyecto(Integer idProyecto) {
		Query sql = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select i from InterseccionProyectoLicenciaAmbiental i where i.proyectoLicenciaCoa.id=:idProyecto and i.estado=true");
		sql.setParameter("idProyecto", idProyecto);
		if (sql.getResultList().size() > 0)
			return sql.getResultList();

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleInterseccionProyectoAmbiental> getDetalleInterseccionSnapPorProyecto(Integer idProyecto) {
		Query sql = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select d from DetalleInterseccionProyectoAmbiental d "
						+ "where d.interseccionProyectoLicenciaAmbiental.proyectoLicenciaCoa.id = :idProyecto "
						+ "and d.interseccionProyectoLicenciaAmbiental.capa.id = :idTipoCapa "
						+ "and d.estado=true");
		sql.setParameter("idProyecto", idProyecto);
		sql.setParameter("idTipoCapa", Constantes.ID_CAPA_SNAP);
		if (sql.getResultList().size() > 0)
			return sql.getResultList();

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleInterseccionProyectoAmbiental> getDetalleInterseccionForestalPorProyecto(Integer idProyecto) {
		List<DetalleInterseccionProyectoAmbiental> resultado = new ArrayList<>();
		
		Query sqlBP = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select d from DetalleInterseccionProyectoAmbiental d "
						+ "where d.interseccionProyectoLicenciaAmbiental.proyectoLicenciaCoa.id = :idProyecto "
						+ "and d.interseccionProyectoLicenciaAmbiental.capa.id = :idTipoCapa "
						+ "and d.estado=true");
		sqlBP.setParameter("idProyecto", idProyecto);
		sqlBP.setParameter("idTipoCapa", Constantes.ID_CAPA_BP);
		if (sqlBP.getResultList().size() > 0)
			resultado.addAll(sqlBP.getResultList());
		
		Query sqlPFE = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select d from DetalleInterseccionProyectoAmbiental d "
						+ "where d.interseccionProyectoLicenciaAmbiental.proyectoLicenciaCoa.id = :idProyecto "
						+ "and d.interseccionProyectoLicenciaAmbiental.capa.id = :idTipoCapa "
						+ "and d.estado=true");
		sqlPFE.setParameter("idProyecto", idProyecto);
		sqlPFE.setParameter("idTipoCapa", Constantes.ID_CAPA_PFE);
		if (sqlPFE.getResultList().size() > 0)
			resultado.addAll(sqlPFE.getResultList());
		
		if(resultado.size() > 0)
			return resultado;

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleInterseccionProyectoAmbiental> getDetallePorInterseccion(Integer idInterseccion) {
		Query sql = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select d from DetalleInterseccionProyectoAmbiental d where d.interseccionProyectoLicenciaAmbiental.id = :idInterseccion and d.estado=true");
		sql.setParameter("idInterseccion", idInterseccion);
		if (sql.getResultList().size() > 0)
			return sql.getResultList();

		return null;
	}
	
	public AreasSnapProvincia getSnapProvinciaPorProyecto(String codigoCapa, Integer idProvincia) {
		Query sql = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select a from AreasSnapProvincia a where a.codigoSnap = :codigoCapa and a.geloIdProvincia = :idProvincia and a.estado=true");
		sql.setParameter("codigoCapa", codigoCapa);
		sql.setParameter("idProvincia", idProvincia);
		if (sql.getResultList().size() > 0)
			return (AreasSnapProvincia) sql.getResultList().get(0);

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<AreasSnapProvincia> getAreasSnapPorCapaProvincia(String codigoCapa, Integer idProvincia) {
		Query sql = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select a from AreasSnapProvincia a where a.codigoSnap = :codigoCapa and a.geloIdProvincia = :idProvincia and a.estado=true");
		sql.setParameter("codigoCapa", codigoCapa);
		sql.setParameter("idProvincia", idProvincia);
		if (sql.getResultList().size() > 0)
			return sql.getResultList();

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<AreasSnapProvincia> getUbicacionSnapPorIdCapa(Integer idCapa) {
		Query query = crudServiceBean
				.getEntityManager()
				.createNativeQuery(
						"SELECT * FROM  dblink('"
								+ dblinkIdeMaae
								+ "',"
								+ "'select csnap from h_demarcacion.fa210_snap_a where gid = "
								+ idCapa + " ')"
								+ " as (codigo character varying)");
		List<String> result = query.getResultList();
		
		if(result.size() == 1) {
			String codigoArea = result.get(0);
			Query sql = crudServiceBean
					.getEntityManager()
					.createQuery(
							"Select a from AreasSnapProvincia a where a.codigoSnap = :codigoArea and a.estado=true");
			sql.setParameter("codigoArea", codigoArea);

			if (sql.getResultList().size() > 0)
				return sql.getResultList();
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<AreasSnapProvincia> getUbicacionSnapPorCodigoCapa(String codigoArea) {
		Query sql = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select a from AreasSnapProvincia a where a.codigoSnap = :codigoArea and a.estado=true");
		sql.setParameter("codigoArea", codigoArea);

		if (sql.getResultList().size() > 0)
			return sql.getResultList();

		return null;
	}
	
	public String getNombresAreasProtegidasForestal(Integer idProyecto, Integer tipoRetorno) {
		String nombreAreaProtegida = "";
		String separador = "";
		
		List<DetalleInterseccionProyectoAmbiental> detalleInterseccion = getDetalleInterseccionForestalPorProyecto(idProyecto);
		
		if(detalleInterseccion != null) {
			if(tipoRetorno.equals(1)) {
				separador = " - ";
			}
			
			if(tipoRetorno.equals(2)) {
				separador = " <br /> ";
			}
			
			for (DetalleInterseccionProyectoAmbiental interseccion : detalleInterseccion) {
				if(interseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getId().equals(Constantes.ID_CAPA_BP)){
					nombreAreaProtegida = (nombreAreaProtegida.equals("")) ? interseccion.getNombreGeometria() : nombreAreaProtegida + separador + interseccion.getNombreGeometria();
				}
				if(interseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getId().equals(Constantes.ID_CAPA_PFE)){
					nombreAreaProtegida = (nombreAreaProtegida.equals("")) ? interseccion.getNombreGeometria() : nombreAreaProtegida + separador + interseccion.getNombreGeometria();
				}
			}
		}
		
		return nombreAreaProtegida;
	}
	
	public String getNombresAreasProtegidasSnap(Integer idProyecto, Integer tipoRetorno) {
		String nombreAreaProtegida = "";
		String separador = "";
		
		List<DetalleInterseccionProyectoAmbiental> detalleInterseccion = getDetalleInterseccionSnapPorProyecto(idProyecto);
		
		if(detalleInterseccion != null) {
			if(tipoRetorno.equals(1)) {
				separador = " - ";
			}
			
			if(tipoRetorno.equals(2)) {
				separador = " <br /> ";
			}
			
			for (DetalleInterseccionProyectoAmbiental interseccion : detalleInterseccion) {
				if(interseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getId().equals(Constantes.ID_CAPA_SNAP)){
					nombreAreaProtegida = (nombreAreaProtegida.equals("")) ? interseccion.getNombreGeometria() : nombreAreaProtegida + separador + interseccion.getNombreGeometria();
				}
			}
		}
		
		return nombreAreaProtegida;
	}
	
	public String getInterseccionesForestal(Integer idProyecto, Integer tipoRetorno) {
		String interseca = "";
		String separador = "";
		
		if(tipoRetorno.equals(1)) {
			separador = " - ";
		}
		
		if(tipoRetorno.equals(2)) {
			separador = " <br /> ";
		}
		
		List<InterseccionProyectoLicenciaAmbiental> intersecciones = getInterseccionesViabilidadPorProyecto(idProyecto);
		if(intersecciones != null) {
			for (InterseccionProyectoLicenciaAmbiental interseccion : intersecciones) {
				if(interseccion.getCapa().getId().equals(Constantes.ID_CAPA_BP) || interseccion.getCapa().getId().equals(Constantes.ID_CAPA_PFE)){
					String nombreCapa = interseccion.getDescripcionCapa(); //interseccion.getCapa().getNombre();
					String nombreDetalles = "";
					
					List<DetalleInterseccionProyectoAmbiental> detalleInterseccion = getDetallePorInterseccion(interseccion.getId());
					for (DetalleInterseccionProyectoAmbiental detalle : detalleInterseccion) {
						String nombreInterseccion = detalle.getNombreGeometria();
						nombreDetalles = (nombreDetalles.equals("")) ? nombreCapa + ": " + nombreInterseccion : nombreDetalles + ", " + nombreInterseccion;
					}
					
					interseca = (interseca.equals("")) ? nombreDetalles : interseca + separador + nombreDetalles;
				}
			}
		}
		
		return interseca;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleInterseccionProyectoAmbiental> getDetalleInterseccionSnap(Integer idProyecto) {
		CapasCoa capa = capasCoaFacade.getCapaByNombre(CapasCoa.NAME_CAPA_SNAP_ZONAS);
		
		Query sql = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select d from DetalleInterseccionProyectoAmbiental d "
						+ "where d.interseccionProyectoLicenciaAmbiental.proyectoLicenciaCoa.id = :idProyecto "
						+ "and d.interseccionProyectoLicenciaAmbiental.capa.id = :idTipoCapa "
						+ "and d.estado=true order by areaInterseccion desc, id asc");
		sql.setParameter("idProyecto", idProyecto);
		sql.setParameter("idTipoCapa", capa.getId());
		if (sql.getResultList().size() > 0) {
			List<DetalleInterseccionProyectoAmbiental> listaDetalle = sql.getResultList();
			List<DetalleInterseccionProyectoAmbiental> listaDetalleAux = new ArrayList<>();
			listaDetalleAux.addAll(listaDetalle);

			List<Integer> listaCapas = new ArrayList<>();

			for(DetalleInterseccionProyectoAmbiental item : listaDetalleAux) {
				if(listaCapas.contains(item.getIdGeometria())) {
					listaDetalle.remove(item);
				} else {
					listaCapas.add(item.getIdGeometria());
				}
			}

			return listaDetalle;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<AreasSnapProvincia> getAreaSnapPorCodigoZona(String codigoArea, String zona) {
		Query sql = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select a from AreasSnapProvincia a where a.codigoSnap = :codigoArea and a.zona = :zona and a.estado=true");
		sql.setParameter("codigoArea", codigoArea);
		sql.setParameter("zona", zona);

		if (sql.getResultList().size() > 0)
			return sql.getResultList();

		return null;
	}
	
	public String getNombresAreasProtegidasZonaSnap(Integer idProyecto, Integer tipoRetorno) {
		String nombreAreaProtegida = "";
		String separador = "";
		
		List<DetalleInterseccionProyectoAmbiental> detalleInterseccion = getDetalleInterseccionSnap(idProyecto);
		
		if(detalleInterseccion != null) {
			if(tipoRetorno.equals(1)) {
				separador = " - ";
			}
			
			if(tipoRetorno.equals(2)) {
				separador = " <br /> ";
			}
			
			for (DetalleInterseccionProyectoAmbiental interseccion : detalleInterseccion) {
				String nombreCompleto = interseccion.getNombreAreaCompleto();
				
				nombreAreaProtegida = (nombreAreaProtegida.equals("")) ? nombreCompleto
						: nombreAreaProtegida + separador + nombreCompleto;
			}
		}
		
		return nombreAreaProtegida;
	}

}
