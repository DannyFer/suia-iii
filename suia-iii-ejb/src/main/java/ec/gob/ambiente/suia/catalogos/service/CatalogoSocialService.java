package ec.gob.ambiente.suia.catalogos.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoGeneralSocial;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CatalogoSocialService {

    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public List<CatalogoGeneralSocial> obtenerListaSocialTipo(String categoria) {

        return crudServiceBean
                .getEntityManager()
                .createQuery(
                        "From CatalogoGeneralSocial c where c.tipoCatalogo.codigo = :catyCode order by c.orden")
                .setParameter("catyCode", categoria).getResultList();

    }

    @SuppressWarnings("unchecked")
    public List<CatalogoGeneralSocial> obtenerListaSocialTipo(
            List<String> categoria) {

	/*	List<CatalogoGeneralSocial> lista = crudServiceBean
                .getEntityManager()
				.createQuery(
						"From CatalogoGeneralSocial c where c.tipoCatalogo.codigo in (:catyCodes) order by c.orden")
				.setParameter("catyCodes", categoria).getResultList();
*/
        List<CatalogoGeneralSocial> lista = crudServiceBean
                .getEntityManager()
                .createNativeQuery(
                        "SELECT c.gcso_id,c.gcso_description,c.gcso_help, ct.caty_code, ct.caty_type, ct.caty_id, c.gcso_status, c.gcso_weight, c.gcso_parent_id, c.sector_id from public.general_catalogs_social c " +
                                "  INNER JOIN public.catalog_types ct on c.caty_id = ct.caty_id where ct.caty_code in( :catyCodes) and c.gcso_status = true ORDER  BY  c.gcso_weight", CatalogoGeneralSocial.class)
                .setParameter("catyCodes", categoria).getResultList();


        List<CatalogoGeneralSocial> listaF = new ArrayList<CatalogoGeneralSocial>();
        for (CatalogoGeneralSocial catalogoGeneralSocial : lista) {
            catalogoGeneralSocial.getTipoCatalogo().getId();
            listaF.add(catalogoGeneralSocial);
        }
        return listaF;

    }

    @SuppressWarnings("unchecked")
    public List<CatalogoGeneralSocial> obtenerListaSocialIdTipo(Integer codigo) {

        return crudServiceBean
                .getEntityManager()
                .createQuery(
                        "From CatalogoGeneralSocial c where c.tipoCatalogo.id = :catyCode order by c.orden")
                .setParameter("catyCode", codigo).getResultList();

    }

    @SuppressWarnings("unchecked")
    public List<CatalogoGeneralSocial> obtenerListaSocialIds(String ids) {

        return crudServiceBean
                .getEntityManager()
                .createQuery(
                        "From CatalogoGeneralSocial c where c.id IN (" + ids
                                + ") order by c.orden").getResultList();

    }

    @SuppressWarnings("unchecked")
    public List<CatalogoGeneralSocial> obtenerListaSocialPorFichaPorTipoCatalogo(
            Integer idFicha, Integer idTipoCatalogo) {

        return crudServiceBean
                .getEntityManager()
                .createQuery(
                        "SELECT cgs From CategoriaIICatalogoGeneralSocial c,CatalogoGeneralSocial cgs where c.fichaAmbientalPma.id IN ("
                                + idFicha
                                + ") AND cgs.tipoCatalogo.id = :catyCode AND c.catalogo.id=cgs.id order by cgs.orden")
                .setParameter("catyCode", idTipoCatalogo).getResultList();

    }
}
