package ec.gob.ambiente.suia.prevencion.categoria2.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Articulo;
import ec.gob.ambiente.suia.domain.ArticuloCatalogo;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.MarcoLegalPma;

/**
 * 
 * @author karla.carvajal
 * 
 */
@Stateless
public class MarcoLegalPmaServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<MarcoLegalPma> getMarcosLegalesPorFichaId(Integer idFicha) {
		List<MarcoLegalPma> marcosLegales = new ArrayList<MarcoLegalPma>();
		marcosLegales = (List<MarcoLegalPma>) crudServiceBean.getEntityManager()
				.createQuery("From MarcoLegalPma m where m.fichaAmbientalPma.id =:idFicha and m.estado = true")
				.setParameter("idFicha", idFicha).getResultList();

		return marcosLegales;
	}

	public void guardarMarcoLegal(MarcoLegalPma marcoLegalPma) {
		crudServiceBean.saveOrUpdate(marcoLegalPma);
	}

	public void eliminarMarcoLegal(MarcoLegalPma marcoLegalPma) {
		crudServiceBean.delete(marcoLegalPma);
	}

	@SuppressWarnings("unchecked")
	public List<ArticuloCatalogo> getArticulosCatalogo(Integer idCatalogo) {
		List<ArticuloCatalogo> articulos = new ArrayList<ArticuloCatalogo>();
		articulos = (List<ArticuloCatalogo>) crudServiceBean.getEntityManager()
				.createQuery("From ArticuloCatalogo m where m.tipoSubsector.id =:idCatalogo and m.estado = true")
				.setParameter("idCatalogo", idCatalogo).getResultList();
		return articulos;
	}

	public List<CatalogoGeneral> getArticulosCatalogoNativeQuery(Integer subSectorId, String codigoUso,String cacsCode) {
		String query ="select gc.geca_id, gc.geca_description, a.faar_article from suia_iii.fapma_catalogo_article ca inner join suia_iii.fapma_article a on ca.faar_id=a.faar_id and a.faar_status=true and ca.faca_status = true inner join general_catalogs gc on a.geca_id = gc.geca_id and gc.geca_status=true where ca.faca_use_code=:codigoUso and ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigoUso", codigoUso );
		
		if(cacsCode!=null && !cacsCode.isEmpty())
		{
			query += "ca.cacs_codes like :cacsCode order by 2,3";
			parameters.put("cacsCode", "%"+cacsCode+"%");
		}else{
			query += "secl_id = :subSectorId order by 2,3";
			parameters.put("subSectorId", subSectorId);
		}		
		
		
		List<Object> result = crudServiceBean.findByNativeQuery(query, parameters);

		List<CatalogoGeneral> catalogos = new ArrayList<CatalogoGeneral>();

		for (Object object : result) {
			// gc.geca_id, gc.geca_description, a.faar_article
			Object[] array = (Object[]) object;

			CatalogoGeneral cg = new CatalogoGeneral((Integer) array[0]);
			cg.setArticulos(new ArrayList<Articulo>());
			if (catalogos.contains(cg)) {
				cg = catalogos.get(catalogos.indexOf(cg));
			} else {
				catalogos.add(cg);
			}
			cg.setDescripcion((String) array[1]);
			Articulo articulo = new Articulo();
			articulo.setArticulo((String) array[2]);
			cg.getArticulos().add(articulo);

		}
		return catalogos;
	}

	public List<CatalogoGeneral> getArticulosCatalogoCoa() {
		String query ="select gc.geca_id, gc.geca_description, a.faar_article from suia_iii.fapma_article a inner join general_catalogs gc on a.geca_id = gc.geca_id and a.faar_status=true and gc.geca_status=true "
				+ "where gc.caty_id = 27 and gc.geca_description in('Código Orgánico del Ambiente', 'Constitución de la República del Ecuador', 'Reglamento al Código Orgánico del Ambiente') ";
		List<Object> result = crudServiceBean.findByNativeQuery(query, null);

		return crearObjeto(result);
	}
	
	private List<CatalogoGeneral> crearObjeto(List<Object> result ){
		List<CatalogoGeneral> catalogos = new ArrayList<CatalogoGeneral>();

		for (Object object : result) {
			// gc.geca_id, gc.geca_description, a.faar_article
			Object[] array = (Object[]) object;

			CatalogoGeneral cg = new CatalogoGeneral((Integer) array[0]);
			cg.setArticulos(new ArrayList<Articulo>());
			if (catalogos.contains(cg)) {
				cg = catalogos.get(catalogos.indexOf(cg));
			} else {
				catalogos.add(cg);
			}
			cg.setDescripcion((String) array[1]);
			Articulo articulo = new Articulo();
			articulo.setArticulo((String) array[2]);
			cg.getArticulos().add(articulo);

		}
		return catalogos;
	}
}