package ec.gob.ambiente.rcoa.facade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProcesosArchivados;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;

@Stateless
public class ProcesosArchivadosFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
    @EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	public void guardar(ProcesosArchivados procesoArchivado) {
		crudServiceBean.saveOrUpdate(procesoArchivado);
	}

	@SuppressWarnings("unchecked")
	public ProcesosArchivados getPorCodigoProyecto(String codigo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigoProyecto", codigo);

		try {
			List<ProcesosArchivados> lista = (List<ProcesosArchivados>) crudServiceBean
					.findByNamedQuery(ProcesosArchivados.GET_POR_PROYECTO,parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public void guardarArchivado(ProyectoLicenciaCoa proyectoLicenciaCoa, Integer tipoArchivo) {
		String razon = null;
		
		switch (tipoArchivo) {
		case 1:
			razon = "Automático por Viabilidad Ambiental NO FAVORABLE";
			break;
		case 2:
			razon = "Automático por no remitir la información del Estudio de Impacto Ambiental en los tiempos establecidos";
			break;
		case 3:
			razon = "Automático por tercera observación del Estudio de Impacto Ambiental";
			break;
		case 4:
			razon = "Automático por observación sustancial en el Estudio de Impacto Ambiental";
			break;
		default:
			razon = "Archivado mediante sistema";
			break;
		}
		
		proyectoLicenciaCoa.setEstado(false);
		proyectoLicenciaCoa.setProyectoFinalizado(true);
		proyectoLicenciaCoa.setProyectoFechaFinalizado(new Date());
		proyectoLicenciaCoa.setFechaDesactivacion(new Date());
		proyectoLicenciaCoa.setRazonEliminacion("ARCHIVADO");
		proyectoLicenciaCoaFacade.guardar(proyectoLicenciaCoa);
		
		ProcesosArchivados procesoArchivo = getPorCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
		if(procesoArchivo == null) {
			procesoArchivo = new ProcesosArchivados();
		}
		
		procesoArchivo.setCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
		procesoArchivo.setDescripcion(razon);
		procesoArchivo.setEsArchivado(true);
		guardar(procesoArchivo);
	}

	public List<ProyectoCustom> listarProyectosArchivadosRcoa (Map<String, Object> filters, String sortField, String sortOrder, Usuario usuario, 
			boolean esOperador, boolean rolAdmin,  boolean rolEnte, boolean rolPatrimonio, boolean delimitado, Integer limite, Integer offset) {
		CriteriaBuilder cb = crudServiceBean.getEntityManager().getCriteriaBuilder();

		Metamodel m = crudServiceBean.getEntityManager().getMetamodel();
		EntityType<ProyectoLicenciaCuaCiuu> consultaProyectoLicencia = m.entity(ProyectoLicenciaCuaCiuu.class);
		CriteriaQuery<ProyectoCustom> cq = cb.createQuery(ProyectoCustom.class);

		Root<ProyectoLicenciaCuaCiuu> rootProyectoCiiu = cq.from(consultaProyectoLicencia);
		Join<ProyectoLicenciaCuaCiuu, ProyectoLicenciaCoa> rootProyectoLicencia = rootProyectoCiiu.join("proyectoLicenciaCoa", JoinType.INNER);
		cq.orderBy(cb.asc(rootProyectoLicencia.get("codigoUnicoAmbiental")));
		Join<ProyectoLicenciaCoa, Area> area = rootProyectoLicencia.join("areaResponsable", JoinType.INNER);
		Join<ProyectoLicenciaCoa, Categoria> categoria = rootProyectoLicencia.join("categoria", JoinType.INNER);
		
		Join<ProyectoLicenciaCuaCiuu, CatalogoCIUU> catalogo = rootProyectoCiiu.join("catalogoCIUU", JoinType.INNER);
		Join<ProyectoLicenciaCuaCiuu, TipoSector> tipoSector = catalogo.join("tipoSector", JoinType.INNER);

		Root<ProcesosArchivados> rootProcesoArchivado = cq.from(ProcesosArchivados.class);
		
		Predicate predicado = cb.conjunction();

		predicado = cb.equal(rootProyectoLicencia.get("codigoUnicoAmbiental"), rootProcesoArchivado.get("codigoProyecto"));
		predicado = cb.and(predicado, cb.equal(rootProcesoArchivado.get("esArchivado"), true));
		predicado = cb.and(predicado, cb.equal(rootProcesoArchivado.get("estado"), true));

		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("primario"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("estado"), false));

		if (filters != null && filters.size() > 0) {
			for (Map.Entry<String, Object> filtro : filters.entrySet()) {
				String tipoFiltro = filtro.getKey();
				String valorFiltro = filtro.getValue().toString().toLowerCase();
				switch (tipoFiltro) {
				case "codigo":
					predicado = cb.and(predicado, cb.like(cb.lower(rootProyectoLicencia.<String> get("codigoUnicoAmbiental")), "%" + valorFiltro + "%"));
					break;
				case "nombre":
					predicado = cb.and(predicado, cb.like(cb.lower(rootProyectoLicencia.<String> get("nombreProyecto")), "%" + valorFiltro + "%"));
					break;
				case "sector":
					predicado = cb.and(predicado, cb.like(cb.lower(tipoSector.<String> get("nombre")), "%" + valorFiltro + "%"));
					break;
				case "responsableSiglas":
					predicado = cb.and(predicado, cb.like(cb.lower(area.<String> get("areaAbbreviation")), "%" + valorFiltro + "%"));
					break;
				case "categoriaNombrePublico":
					predicado = cb.and(predicado, cb.like(cb.lower(categoria.<String> get("nombrePublico")), "%" + valorFiltro + "%"));
					break;
				default:
					break;
				}
			}
		}

		if(esOperador) {
			predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("usuario"), usuario));
		} else if(!rolAdmin) {
			List<Integer> listaAreasUsuario  = new ArrayList<Integer>();
			
			for(AreaUsuario areaUs : usuario.getListaAreaUsuario()){
				listaAreasUsuario.add(areaUs.getArea().getId());
			}
			
			if(!rolEnte && !rolPatrimonio) {
				//Direccion provincial
				Expression<Integer> listaAreas = area.get("id");
				Predicate predicado1 = cb.and(predicado, listaAreas.in(listaAreasUsuario));
				
				Expression<Integer> listaAreasPadres = area.get("area").get("id");
				Predicate predicado2 = cb.or(predicado1, cb.and(predicado, listaAreasPadres.in(listaAreasUsuario)));
				
				predicado = cb.and(predicado, predicado2);
				
			} else {
				if(rolEnte) {
					//no estrategicos
					Expression<Integer> listaAreas = area.get("id");
					predicado = cb.and(predicado, cb.and(predicado, listaAreas.in(listaAreasUsuario)));
					
					Predicate predicado1 = cb.equal(rootProyectoLicencia.get("interecaSnap"), false);
					Predicate predicado2 = cb.or(predicado1, cb.equal(rootProyectoLicencia.get("interecaBosqueProtector"), false));
					Predicate predicado3 = cb.or(predicado2, cb.equal(rootProyectoLicencia.get("interecaPatrimonioForestal"), false));
					
					predicado = cb.and(predicado, predicado3);
				}
				if (rolPatrimonio) {
					Predicate predicado1 = cb.equal(rootProyectoLicencia.get("interecaSnap"), true);
					Predicate predicado2 = cb.or(predicado1, cb.equal(rootProyectoLicencia.get("interecaBosqueProtector"), true));
					Predicate predicado3 = cb.or(predicado2, cb.equal(rootProyectoLicencia.get("interecaPatrimonioForestal"), true));
					
					predicado = cb.and(predicado, predicado3);
				}
			}
		}
		
		cq.multiselect(rootProyectoLicencia.get("id"), rootProyectoLicencia.get("codigoUnicoAmbiental"),
				rootProyectoLicencia.get("nombreProyecto"), rootProyectoLicencia.get("fechaCreacion"),
				tipoSector.get("nombre"), categoria.get("nombrePublico"),
				rootProyectoLicencia.get("razonEliminacion"),
				area.get("areaAbbreviation"), area.get("areaName"), rootProcesoArchivado.get("fechaCreacion"))
				.where(predicado);
		
		if(sortField != null && !sortField.isEmpty()) {
			switch (sortField) {
			case "codigo":
				cq.orderBy(sortOrder.equals("DESCENDING") ? cb.asc(rootProyectoLicencia.get("codigoUnicoAmbiental")) : cb.desc(rootProyectoLicencia.get("codigoUnicoAmbiental")));
				break;
			case "nombre":
				cq.orderBy(sortOrder.equals("DESCENDING") ? cb.asc(rootProyectoLicencia.get("nombreProyecto")) : cb.desc(rootProyectoLicencia.get("nombreProyecto")));
				break;
			case "sector":
				cq.orderBy(sortOrder.equals("DESCENDING") ? cb.asc(tipoSector.get("nombre")) : cb.desc(tipoSector.get("nombre")));
				break;
			case "responsableSiglas":
				cq.orderBy(sortOrder.equals("DESCENDING") ? cb.asc(area.get("areaAbbreviation")) : cb.desc(area.get("areaAbbreviation")));
				break;
			case "categoriaNombrePublico":
				cq.orderBy(sortOrder.equals("DESCENDING") ? cb.asc(categoria.get("nombrePublico")) : cb.desc(categoria.get("nombrePublico")));
				break;
			case "fechaArchivo":
				cq.orderBy(sortOrder.equals("DESCENDING") ? cb.asc(rootProcesoArchivado.get("fechaCreacion")) : cb.desc(rootProcesoArchivado.get("fechaCreacion")));
				break;
			default:
				break;
			}
		}
		
		if(delimitado)
			return this.crudServiceBean.getEntityManager().createQuery(cq) .setFirstResult(offset).setMaxResults(limite).getResultList();
		else
			return this.crudServiceBean.getEntityManager().createQuery(cq).getResultList();
	}
	
	public String getDate(Date date){

	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    return sdf.format(date);
	}

}
