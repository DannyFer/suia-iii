package ec.gob.ambiente.suia.catalogocategorias.service;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaAcuerdo;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaPublico;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;
import ec.gob.ambiente.suia.domain.TipoSector;
import org.apache.log4j.Logger;

@Stateless
public class CatalogoCategoriasServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	private static final Logger LOG = Logger.getLogger(CatalogoCategoriasServiceBean.class);

	@SuppressWarnings("unchecked")
	public List<CatalogoCategoriaSistema> listarCatalogoCategorias() {
		return (List<CatalogoCategoriaSistema>) crudServiceBean
				.findAll(CatalogoCategoriaSistema.class);
	}

	public CatalogoCategoriaSistema buscarCatalogoCategoriasPorId(Integer id) {
		return crudServiceBean.find(CatalogoCategoriaSistema.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<CatalogoCategoriaSistema> buscarCatalogoCategoriasPorPadre(
			CatalogoCategoriaSistema padre) {
		List<CatalogoCategoriaSistema> categorias = (List<CatalogoCategoriaSistema>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From CatalogoCategoriaSistema c where c.categoriaSistema =:padre order by c.id")
				.setParameter("padre", padre).getResultList();
		initIsCategoriaFinal(categorias);
		return categorias;
	}

	public CatalogoCategoriaSistema buscarCatalogoCategoriaSistemaPorId(
			Integer idCatalogoCategoriaSistema) {
		CatalogoCategoriaSistema categoria = (CatalogoCategoriaSistema) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From CatalogoCategoriaSistema c where c.id =:idCCS")
				.setParameter("idCCS", idCatalogoCategoriaSistema)
				.getSingleResult();
		return categoria;
	}

	public CatalogoCategoriaAcuerdo buscarCatalogoCategoriaAcuerdoPorId(
			Integer idCatalogoCategoriaAcuerdo) {
		CatalogoCategoriaAcuerdo categoria = (CatalogoCategoriaAcuerdo) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From CatalogoCategoriaAcuerdo c where c.id =:idCCA")
				.setParameter("idCCA", idCatalogoCategoriaAcuerdo)
				.getSingleResult();
		return categoria;
	}

	@SuppressWarnings("unchecked")
	public List<CatalogoCategoriaAcuerdo> buscarCatalogoCategoriaAcuerdoPorNombreOrganizacion(
			String nombreOrganizacion) {
		List<CatalogoCategoriaAcuerdo> categorias = (List<CatalogoCategoriaAcuerdo>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From CatalogoCategoriaAcuerdo c where c.nombreOrganizacion =:nombreOrganizacion")
				.setParameter("nombreOrganizacion", nombreOrganizacion)
				.getResultList();
		return categorias;
	}

	@SuppressWarnings("unchecked")
	public List<CatalogoCategoriaAcuerdo> buscarCatalogoCategoriaAcuerdoPorRucOrganizacion(
			String rucOrganizacion) {
		List<CatalogoCategoriaAcuerdo> categorias = (List<CatalogoCategoriaAcuerdo>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From CatalogoCategoriaAcuerdo c where c.rucOrganizacion =:rucOrganizacion")
				.setParameter("rucOrganizacion", rucOrganizacion)
				.getResultList();
		return categorias;
	}

	@SuppressWarnings("unchecked")
	public List<CatalogoCategoriaSistema> buscarCatalogoCategoriasPadres(
			String filtro, TipoSector tipoSector) {
		List<CatalogoCategoriaSistema> categorias = new ArrayList<CatalogoCategoriaSistema>();
		if (filtro != null && !filtro.isEmpty()) {
			filtro = filtro.toLowerCase();
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"FROM CatalogoCategoriaSistema c WHERE (lower(c.codigo) like :filtro OR lower(c.descripcion) like :filtro ) AND c.catalogoCategoriaPublico.tipoSector.id = :sectorId ORDER BY c.id");
			query.setParameter("filtro", "%" + filtro + "%");
			if (tipoSector != null)
				query.setParameter("sectorId", tipoSector.getId());
			categorias = query.getResultList();
		} else {
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"FROM CatalogoCategoriaSistema c WHERE c.categoriaSistema = null AND c.catalogoCategoriaPublico.tipoSector.id = :sectorId ORDER BY c.id");
			if (tipoSector != null)
				query.setParameter("sectorId", tipoSector.getId());
			categorias = query.getResultList();
		}
		initIsCategoriaFinal(categorias);
		return categorias;
	}

	public List<CatalogoCategoriaSistema> buscarCatalogoCategoriasPadres(
			String filtro) {
		return buscarCatalogoCategoriasPadres(filtro, null);
	}

	@SuppressWarnings("unchecked")
	public List<CatalogoCategoriaSistema> listarCatalogoCategoriasPorCategoriaPublica(
			CatalogoCategoriaPublico catalogoCategoriaPublico) {
		Query query = crudServiceBean
				.getEntityManager()
				.createQuery(
						"FROM CatalogoCategoriaSistema c WHERE c.catalogoCategoriaPublico.id = :catalogoPublicoId and c.estado=true and c.catalogoCategoriaPublico.estado=true ORDER BY c.id");
		query.setParameter("catalogoPublicoId",
				catalogoCategoriaPublico.getId());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<CatalogoCategoriaPublico> listarCatalogoCategoriasPublicas() {
		return (List<CatalogoCategoriaPublico>) crudServiceBean
				.findAll(CatalogoCategoriaPublico.class);
	}

	public CatalogoCategoriaPublico buscarCatalogoCategoriaPublicaPorId(
			Integer id) {
		return crudServiceBean.find(CatalogoCategoriaPublico.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<CatalogoCategoriaPublico> buscarCatalogoCategoriasPublicasPorPadre(
			CatalogoCategoriaPublico padre) {
		List<CatalogoCategoriaPublico> categorias = (List<CatalogoCategoriaPublico>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From CatalogoCategoriaPublico c where c.categoriaPublico =:padre order by c.id")
				.setParameter("padre", padre).getResultList();
		initIsCategoriaPublicaFinal(categorias);
		return categorias;
	}

	@SuppressWarnings("unchecked")
	public List<CatalogoCategoriaPublico> buscarCatalogoCategoriasPublicasPadres(
			String filtro, TipoSector tipoSector) {
		List<CatalogoCategoriaPublico> categorias = new ArrayList<CatalogoCategoriaPublico>();
		if (filtro != null && !filtro.isEmpty()) {
			filtro = filtro.toLowerCase();
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"FROM CatalogoCategoriaPublico c WHERE (lower(c.nombre) like :filtro) AND c.tipoSector.id = :sectorId ORDER BY c.id");
			query.setParameter("filtro", "%" + filtro + "%");
			if (tipoSector != null)
				query.setParameter("sectorId", tipoSector.getId());
			categorias = query.getResultList();
		} else {
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"FROM CatalogoCategoriaPublico c WHERE c.categoriaPublico = null AND c.tipoSector.id = :sectorId ORDER BY c.id");
			if (tipoSector != null)
				query.setParameter("sectorId", tipoSector.getId());
			categorias = query.getResultList();
		}
		initIsCategoriaPublicaFinal(categorias);
		return categorias;
	}

	public List<CatalogoCategoriaPublico> buscarCatalogoCategoriasPublicasPadres(
			String filtro) {
		return buscarCatalogoCategoriasPublicasPadres(filtro, null);
	}

	private void initIsCategoriaFinal(List<CatalogoCategoriaSistema> categorias) {
		if (categorias != null)
			for (CatalogoCategoriaSistema catalogoCategoriaSistema : categorias)
				catalogoCategoriaSistema.isCategoriaFinal();
	}

	private void initIsCategoriaPublicaFinal(
			List<CatalogoCategoriaPublico> categorias) {
		if (categorias != null)
			for (CatalogoCategoriaPublico catalogoCategoriaPublico : categorias)
				catalogoCategoriaPublico.isCategoriaFinal();
	}
	
	@SuppressWarnings("unchecked")
	public List<CatalogoCategoriaSistema> buscarCatalogoCategoriasRequierenViabilidad() {
		List<CatalogoCategoriaSistema> categorias = (List<CatalogoCategoriaSistema>) crudServiceBean.getEntityManager()
				.createQuery("From CatalogoCategoriaSistema c where c.requiereViabilidad = true").getResultList();
		return categorias;
	}

	public List<String> getIdActividadesTipoManejoNativeQuery(final String campoTipoManejo) {
		List<String> result = new LinkedList<String>();
		try {
			StringBuilder sql = new StringBuilder();

			sql.append("SELECT cacs_code");
			sql.append(" FROM suia_iii.categories_catalog_system ca");
			sql.append(" WHERE ca."+campoTipoManejo+" = true and ca.cacs_status=true");

			List<Object> list = crudServiceBean.findByNativeQuery(sql.toString(), null);

			for (Object obj: list){
				result.add((String)obj);
			}

		} catch (Exception e) {
			LOG.error("Error al obtener las recepciones con los desechos.", e);
		}
		return result;
	}

}
