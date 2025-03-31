package ec.gob.ambiente.suia.catalogos.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoGeneralBiotico;

@Stateless
public class CatalogoBioticoService { 

	@EJB
	private CrudServiceBean crudServiceBean;
 
	@SuppressWarnings("unchecked")
	public List<CatalogoGeneralBiotico> obtenerListaBioticoTipo(String categoria) {

		return crudServiceBean
				.getEntityManager()
				.createQuery(
						"From CatalogoGeneralBiotico c where c.tipoCatalogo.codigo = :catyCode order by c.orden")
				.setParameter("catyCode", categoria).getResultList();

	}
	@SuppressWarnings("unchecked")
	public List<CatalogoGeneralBiotico> obtenerListaBioticoTipo(
			List<String> categoria) {

		/*List<CatalogoGeneralBiotico> lista = crudServiceBean
				.getEntityManager()
				.createQuery(
						"From CatalogoGeneralBiotico c where c.tipoCatalogo.codigo in (:catyCodes) order by c.orden")
				.setParameter("catyCodes", categoria).getResultList();*/

         List<CatalogoGeneralBiotico> lista = crudServiceBean
                .getEntityManager()
                .createNativeQuery(
                        "SELECT c.gcbi_id,c.gcbi_description,c.gcbi_help, ct.caty_code, ct.caty_type, ct.caty_id, c.gcbi_status, c.gcbi_weight, c.gcbi_parent_id, c.sector_id from public.general_catalogs_biotic c\n" +
                                "  INNER JOIN public.catalog_types ct on c.caty_id = ct.caty_id where ct.caty_code in( :catyCodes) and c.gcbi_status = true ORDER  BY  c.gcbi_weight", CatalogoGeneralBiotico.class)
                .setParameter("catyCodes", categoria).getResultList();

		List<CatalogoGeneralBiotico> listaF = new ArrayList<CatalogoGeneralBiotico>();
		for (CatalogoGeneralBiotico catalogoGeneralBiotico : lista) {
			catalogoGeneralBiotico.getTipoCatalogo().getId();
			listaF.add(catalogoGeneralBiotico);
		}
		return listaF;

	}

        @SuppressWarnings("unchecked")
	public List<CatalogoGeneralBiotico> obtenerListaBioticoIdTipo(Integer codigo) {

		return crudServiceBean
				.getEntityManager()
				.createQuery(
						"From CatalogoGeneralBiotico c where c.tipoCatalogo.id = :catyCode order by c.orden")
				.setParameter("catyCode", codigo).getResultList();

	}
	
	public CatalogoGeneralBiotico obtenerIdOtroPorCategoria(String categoria) {
		return (CatalogoGeneralBiotico)crudServiceBean
				.getEntityManager()
				.createQuery(
						"From CatalogoGeneralBiotico c where c.tipoCatalogo.codigo = :catyCode and c.descripcion = :descripcion")
				.setParameter("catyCode", categoria).setParameter("descripcion", "Otro").getSingleResult();
	}

        @SuppressWarnings("unchecked")
        public List<CatalogoGeneralBiotico> obtenerListaBioticoIds(String ids) {
		return crudServiceBean
				.getEntityManager()
				.createQuery(
						"From CatalogoGeneralBiotico c where c.id IN ("+ids+")").getResultList()
				;
	}
      //Cris F: aumento de metodo
        @SuppressWarnings("unchecked")
    	public List<CatalogoGeneralBiotico> obtenerListaBioticoIdPadre(Integer id) {

    		return crudServiceBean
    				.getEntityManager()
    				.createQuery(
    						"From CatalogoGeneralBiotico c where c.catalogoPadre.id = :id order by c.orden")
    				.setParameter("id", id).getResultList();

    	}
}
