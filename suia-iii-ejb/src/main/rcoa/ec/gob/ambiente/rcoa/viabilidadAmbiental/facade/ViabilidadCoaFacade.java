package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeTecnicoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ProyectoTipoViabilidadCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class ViabilidadCoaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private InformeRevisionForestalFacade informeInspeccionFacade;
	@EJB
	private InformeInspeccionBiodiversidadFacade informeInspeccionSnapFacade;
	@EJB
	private PronunciamientoForestalFacade pronunciamientoForestalFacade;
	@EJB
	private PronunciamientoBiodiversidadFacade pronunciamientoBiodiversidadFacade;

	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();

	private static final String PROYECTO = "proyecto";

	@SuppressWarnings("unchecked")
	public ProyectoTipoViabilidadCoa getTipoViabilidadPorProyecto(Integer proyecto, Boolean tipo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(PROYECTO, proyecto);
		parameters.put("EsSnap", tipo);

		try {
			List<ProyectoTipoViabilidadCoa> lista = (List<ProyectoTipoViabilidadCoa>) crudServiceBean
					.findByNamedQuery(ProyectoTipoViabilidadCoa.GET_POR_PROYECTO_TIPO, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ViabilidadCoa getViabilidadPorTipoProyecto(Integer tipoProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tipoProyecto", tipoProyecto);

		try {
			List<ViabilidadCoa> lista = (List<ViabilidadCoa>) crudServiceBean
					.findByNamedQuery(ViabilidadCoa.GET_POR_TIPO_PROYECTO, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ViabilidadCoa getViabilidadForestalPorProyecto(Integer proyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(PROYECTO, proyecto);

		try {
			List<ViabilidadCoa> lista = (List<ViabilidadCoa>) crudServiceBean
					.findByNamedQuery(ViabilidadCoa.GET_FORESTAL_POR_PROYECTO, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ViabilidadCoa getViabilidadSnapPorProyectoTipo(Integer proyecto, Boolean esAdminMae) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(PROYECTO, proyecto);
		parameters.put("esAdminMae", esAdminMae);

		try {
			List<ViabilidadCoa> lista = (List<ViabilidadCoa>) crudServiceBean
					.findByNamedQuery(ViabilidadCoa.GET_SNAP_POR_PROYECTO_TIPO, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ViabilidadCoa> getViabilidadesPorProyecto(Integer proyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(PROYECTO, proyecto);

		try {
			List<ViabilidadCoa> lista = (List<ViabilidadCoa>) crudServiceBean
					.findByNamedQuery(ViabilidadCoa.GET_POR_PROYECTO, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public ViabilidadCoa getViabilidadPorId(Integer idViabilidad) {
		ViabilidadCoa viabilidad = crudServiceBean.find(ViabilidadCoa.class, idViabilidad);
		return viabilidad;
	}

	public void guardarProyectoTipoViabilidadCoa(ProyectoTipoViabilidadCoa tipoViabilidad) {
		crudServiceBean.saveOrUpdate(tipoViabilidad);
	}

	public void guardar(ViabilidadCoa viabilidad) {
		crudServiceBean.saveOrUpdate(viabilidad);
	}

	public String getNombreTarea(Long idTarea) throws Exception {
		String nombre = null;
		try {

			Query query = crudServiceBean.getEntityManager()
					.createNativeQuery("select * " + "from dblink('" + dblinkBpmsSuiaiii + "',' " + "select t.formname "
							+ "from " + "task t " + "where " + "t.id = " + idTarea + " " + "') as ( "
							+ "nombre character varying(255))");

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = (List<Object[]>) query.getResultList();
			if (resultList.size() > 0) {
				nombre = String.valueOf(resultList.get(0));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return nombre;
	}
	
	public String getDetalleInfoViabilidad(ProyectoLicenciaCoa proyecto) {
    	List<ViabilidadCoa> viabilidadesProyecto = getViabilidadesPorProyecto(proyecto.getId());
    	if(viabilidadesProyecto != null) {
    		String viabilidad = "";
    		
    		for (ViabilidadCoa viabilidadCoa : viabilidadesProyecto) {
				if(!viabilidadCoa.getEsViabilidadSnap()){ //es forestal
					String recomendacion = null;
					if(viabilidadCoa.getTipoFlujoViabilidad() == null || viabilidadCoa.getTipoFlujoViabilidad().equals("1")) {
						InformeTecnicoForestal informeRevision = informeInspeccionFacade.getInformePorViabilidad(viabilidadCoa.getId());
						recomendacion = informeRevision.getRecomendaciones().replace("<p>", "<p style=\"font-size:11px; margin-top: 5px;\">");
					} else {
						PronunciamientoForestal oficioForestal = pronunciamientoForestalFacade.getInformePorViabilidad(viabilidadCoa.getId());
						recomendacion = "<p style=\"font-size:11px; margin-top: 5px;\">" + oficioForestal.getRecomendaciones() + "</p>";
					}
					
					viabilidad += "<span style=\"font-size:11px\"><strong>Viabilidad Forestal </strong></span>";
					viabilidad += recomendacion;
				} else {
					String recomendacion = null;
					if(viabilidadCoa.getTipoFlujoViabilidad() == null || viabilidadCoa.getTipoFlujoViabilidad().equals("1")) {
						if(viabilidadCoa.getEsAdministracionMae())
							viabilidad += "<span style=\"font-size:11px\"><strong>Viabilidad SNAP " + Constantes.SIGLAS_INSTITUCION + "</strong></span>";
						else
							viabilidad += "<span style=\"font-size:11px\"><strong>Viabilidad SNAP DELEGADA </strong></span>";
						
						InformeInspeccionBiodiversidad informeInspeccion = informeInspeccionSnapFacade.getInformePorViabilidad(viabilidadCoa.getId());
						recomendacion = informeInspeccion.getRecomendaciones().replace("<p>", "<p style=\"font-size:11px; margin-top: 5px;\">");
					} else {
						viabilidad += "<span style=\"font-size:11px\"><strong>Viabilidad SNAP </strong></span>";
						
						PronunciamientoBiodiversidad oficioSnap = pronunciamientoBiodiversidadFacade.getInformePorViabilidad(viabilidadCoa.getId());
						recomendacion = "<p style=\"font-size:11px; margin-top: 5px;\">" + oficioSnap.getRecomendaciones() + "</p>";
					}
					
					viabilidad += recomendacion;
				}
    		}
    		
    		return viabilidad;
    	}
		
		return null;
    } 

	@SuppressWarnings("unchecked")
	public ViabilidadCoa getViabilidadSnapPorProyecto(Integer proyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(PROYECTO, proyecto);

		try {
			List<ViabilidadCoa> lista = (List<ViabilidadCoa>) crudServiceBean
					.findByNamedQuery(ViabilidadCoa.GET_SNAP_POR_PROYECTO, parameters);

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
