package ec.gob.ambiente.suia.catalogos.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.TipoCatalogo;

@Stateless
public class CatalogoFisicoService {

    Integer idCategoria = TipoCatalogo.CLIMA;
    String codigoClima = TipoCatalogo.CODIGO_CLIMA;
    String codigoTipoSuelo = TipoCatalogo.CODIGO_TIPO_SUELO;

    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public List<CatalogoGeneralFisico> obtenerListaFisicoTipo(String categoria) {

        return crudServiceBean
                .getEntityManager()
                .createQuery(
                        "From CatalogoGeneralFisico c where c.tipoCatalogo.codigo = :catyCode order by c.orden")
                .setParameter("catyCode", categoria).getResultList();

    }

    @SuppressWarnings("unchecked")
    public List<CatalogoGeneralFisico> obtenerListaFisicoTipo(
            List<String> categoria) {

		/*List<CatalogoGeneralFisico> lista = crudServiceBean
                .getEntityManager()
				.createQuery(
						"From CatalogoGeneralFisico c where c.tipoCatalogo.codigo in (:catyCodes) order by c.orden")
				.setParameter("catyCodes", categoria).getResultList();*/

        List<CatalogoGeneralFisico> lista = crudServiceBean
                .getEntityManager()
                .createNativeQuery(
                        "SELECT c.gcph_id,c.gcph_description,c.gcph_help, ct.caty_code, ct.caty_type, ct.caty_id, c.gcph_status, c.gcph_weight, c.gcph_parent_id, c.gcph_from, c.gcph_to, c.sector_id from public.general_catalogs_physical c\n" +
                                "  INNER JOIN public.catalog_types ct on c.caty_id = ct.caty_id where ct.caty_code in( :catyCodes) and c.gcph_status = true ORDER  BY  c.gcph_weight", CatalogoGeneralFisico.class)
                .setParameter("catyCodes", categoria).getResultList();


        List<CatalogoGeneralFisico> listaF = new ArrayList<CatalogoGeneralFisico>();
        for (CatalogoGeneralFisico catalogoGeneralFisico : lista) {
            catalogoGeneralFisico.getTipoCatalogo().getId();
            listaF.add(catalogoGeneralFisico);
        }
        return listaF;

    }

    @SuppressWarnings("unchecked")
    public List<CatalogoGeneralFisico> obtenerListaFisicoIdTipo(Integer codigo) {

        return crudServiceBean
                .getEntityManager()
                .createQuery(
                        "From CatalogoGeneralFisico c where c.tipoCatalogo.id = :catyCode order by c.orden")
                .setParameter("catyCode", codigo).getResultList();

    }

    @SuppressWarnings("unchecked")
    public List<CatalogoGeneralFisico> obtenerListaClima() {

        return obtenerListaFisicoTipo(codigoClima);
    }

    @SuppressWarnings("unchecked")
    public List<CatalogoGeneralFisico> obtenerListaTiposSuelo() {

        return obtenerListaFisicoTipo(codigoTipoSuelo);

    }

    @SuppressWarnings("unchecked")
    public List<CatalogoGeneralFisico> obtenerListaFisicoIds(String ids) {

        return crudServiceBean
                .getEntityManager()
                .createQuery(
                        "From CatalogoGeneralFisico c where c.id IN (" + ids
                                + ")").getResultList();

    }

}
