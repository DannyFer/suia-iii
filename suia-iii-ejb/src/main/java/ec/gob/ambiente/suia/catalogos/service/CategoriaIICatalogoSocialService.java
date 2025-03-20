package ec.gob.ambiente.suia.catalogos.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneralSocial;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class CategoriaIICatalogoSocialService {

    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public List<CategoriaIICatalogoGeneralSocial> catalogoSeleccionadosCategoriaIITipo(
            Integer idProyecto, String codigo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("idProyecto", idProyecto);
        parameters.put("catyCode", codigo);
        return (List<CategoriaIICatalogoGeneralSocial>) crudServiceBean
                .findByNamedQuery(
                        CategoriaIICatalogoGeneralSocial.FIND_BY_PROJECT_CATYCODE,
                        parameters);
    }

    @SuppressWarnings("unchecked")
    public List<CategoriaIICatalogoGeneralSocial> catalogoSeleccionadosCategoriaIITipo(
            Integer idProyecto, String codigo, String seccion) {
        return (List<CategoriaIICatalogoGeneralSocial>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        "SELECT c FROM CategoriaIICatalogoGeneralSocial c WHERE c.fichaAmbientalPma.proyectoLicenciamientoAmbiental.id = :idProyecto AND c.catalogo.tipoCatalogo.codigo = :catyCode and c.seccion = :seccion")
                .setParameter("idProyecto", idProyecto)
                .setParameter("catyCode", codigo)
                .setParameter("seccion", seccion).getResultList();

    }

    @SuppressWarnings("unchecked")
    public List<CategoriaIICatalogoGeneralSocial> catalogoSeleccionadosCategoriaIITipo(
            Integer idProyecto, List<String> codigos, String seccion) {
       /* List<CategoriaIICatalogoGeneralSocial> lista = (List<CategoriaIICatalogoGeneralSocial>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        "SELECT c FROM CategoriaIICatalogoGeneralSocial c WHERE c.fichaAmbientalPma.proyectoLicenciamientoAmbiental.id = :idProyecto AND c.catalogo.tipoCatalogo.codigo  in(:catyCode) and c.seccion = :seccion")
                .setParameter("idProyecto", idProyecto)
                .setParameter("catyCode", codigos)
                .setParameter("seccion", seccion).getResultList();*/


        List<Object[]> lista = (List<Object[]>) crudServiceBean
                .getEntityManager()
                .createNativeQuery(
                        "SELECT c.cgcs_id, c.cgcs_status, c.cgcs_value,  ct.caty_code, c.cafa_id, c.gcso_id,c.cgcs_section, ct.caty_type, cat.caty_id, cat.gcso_description , cat.gcso_help, c.cgcs_original_record_id, f.cafa_original_record_id  FROM  suia_iii.catii_general_catalogs_social c INNER JOIN suia_iii.catii_fapma f on c.cafa_id = f.cafa_id " +
                                " INNER JOIN public.general_catalogs_social cat on c.gcso_id= cat.gcso_id " +
                                "  INNER JOIN suia_iii.projects_environmental_licensing p on p.pren_id = f.pren_id INNER JOIN public.catalog_types ct on cat.caty_id = ct.caty_id\n" +
                                "  WHERE  p.pren_id = :idProyecto and  ct.caty_code  in ( :catyCode ) and c.cgcs_section = :seccion AND c.cgcs_status=TRUE ")
                .setParameter("idProyecto", idProyecto)
                .setParameter("catyCode", codigos)
                .setParameter("seccion", seccion).getResultList();
//       return lista;

//
       List<CategoriaIICatalogoGeneralSocial> listaFinal = new ArrayList<CategoriaIICatalogoGeneralSocial>();

        for (Object[] elemento : lista) {
        	if((Integer)elemento[11] == null && (Integer)elemento[12] == null){
                listaFinal.add(
                        new CategoriaIICatalogoGeneralSocial(
                                (Integer) elemento[0],(Boolean)elemento[1],(String)elemento[2],(String)elemento[3],  (Integer)elemento[4],
                                (Integer)elemento[5],(String)elemento[6],(String)elemento[7],(Integer)elemento[8],(String)elemento[9],(String)elemento[10]  ));
        	}
        }
        /*for (CategoriaIICatalogoGeneralSocial categoriaIICatalogoGeneralSocial : lista) {
            categoriaIICatalogoGeneralSocial.getCatalogo().getTipoCatalogo()
                    .getId();
            listaFinal.add(categoriaIICatalogoGeneralSocial);
        }*/
        return listaFinal;

    }

    // -------
    public void guardarCatalogoGeneralSocial(
            List<CategoriaIICatalogoGeneralSocial> catalogos) {
        crudServiceBean.saveOrUpdate(catalogos);
    }

    // --------
    public void eliminarCatalogoaGeneralSocialCategoriaIIPorFichaCodigo(
            FichaAmbientalPma ficha, String codigo) {
        List<CategoriaIICatalogoGeneralSocial> catalogos = catalogoSeleccionadosCategoriaIITipo(
                ficha.getProyectoLicenciamientoAmbiental().getId(), codigo);
        crudServiceBean.delete(catalogos);

    }

    public void eliminarCatalogoaGeneralSocialCategoriaIIPorFichaCodigo(
            FichaAmbientalPma ficha, String codigo, String seccion) {
        List<CategoriaIICatalogoGeneralSocial> catalogos = catalogoSeleccionadosCategoriaIITipo(
                ficha.getProyectoLicenciamientoAmbiental().getId(), codigo,
                seccion);
        crudServiceBean.delete(catalogos);

    }

    public void eliminarCatalogoaGeneralSocialCategoriaIIPorFichaCodigo(
            FichaAmbientalPma ficha, List<String> codigos, String seccion) {
        Query q = crudServiceBean
                .getEntityManager()
                .createQuery(
                        "Update From CategoriaIICatalogoGeneralSocial ca set ca.estado=false WHERE ca.estado=true and ca.id in (Select c.id From CategoriaIICatalogoGeneralSocial c WHERE c.fichaAmbientalPma.id = :idFicha AND c.catalogo.tipoCatalogo.codigo in (:catyCodes) and c.seccion = :seccion)");
        q.setParameter("idFicha", ficha.getId());
        q.setParameter("catyCodes", codigos);
        q.setParameter("seccion", seccion);
        q.executeUpdate();

    }

    // -------
    
    /**
     * Cris F: metodo para buscar en bdd
     */
    
    public List<CategoriaIICatalogoGeneralSocial> obtenerCatalogoGeneralSocialCategoriaIIPorFichaCodigo(FichaAmbientalPma ficha, List<String> codigos, String seccion) {
        try {
        	Query q = crudServiceBean.getEntityManager().createQuery(
                    "Select c From CategoriaIICatalogoGeneralSocial c WHERE c.fichaAmbientalPma.id = :idFicha AND "
                    + "c.estado=true AND c.catalogo.tipoCatalogo.codigo in (:catyCodes) and c.seccion = :seccion) AND ca.idRegistroOriginal = null");
        		q.setParameter("idFicha", ficha.getId());
        		q.setParameter("catyCodes", codigos);
        		q.setParameter("seccion", seccion);
    
        		List<CategoriaIICatalogoGeneralSocial> resultado = q.getResultList();
    
        		if(resultado != null)
        			return resultado;
    
        		return null;    
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public void guardarCatalogoGeneralSocial(CategoriaIICatalogoGeneralSocial catalogo){
    	crudServiceBean.saveOrUpdate(catalogo);
    }
    
    @SuppressWarnings("unchecked")
    public List<CategoriaIICatalogoGeneralSocial> catalogoSeleccionadosCategoriaIITipoHistorico(
            Integer idProyecto, List<String> codigos, String seccion) {

        List<Object[]> lista = (List<Object[]>) crudServiceBean
                .getEntityManager()
                .createNativeQuery(
                        "SELECT c.cgcs_id, c.cgcs_status, c.cgcs_value,  ct.caty_code, c.cafa_id, c.gcso_id,c.cgcs_section, "
                        + "ct.caty_type, cat.caty_id, cat.gcso_description , cat.gcso_help, "
                        + "c.cgcs_original_record_id, f.cafa_original_record_id, c.cgcs_historical_date  "
                        + "FROM  suia_iii.catii_general_catalogs_social c "
                        + "INNER JOIN suia_iii.catii_fapma f on c.cafa_id = f.cafa_id " +
                                " INNER JOIN public.general_catalogs_social cat on c.gcso_id= cat.gcso_id " +
                                "  INNER JOIN suia_iii.projects_environmental_licensing p on p.pren_id = f.pren_id "
                                + "INNER JOIN public.catalog_types ct on cat.caty_id = ct.caty_id\n" +
                                "  WHERE  p.pren_id = :idProyecto and  ct.caty_code  in ( :catyCode ) and c.cgcs_section = :seccion "
                                + "AND c.cgcs_status=TRUE and (c.cgcs_original_record_id is not null or c.cgcs_historical_date is not null)")
                .setParameter("idProyecto", idProyecto)
                .setParameter("catyCode", codigos)
                .setParameter("seccion", seccion).getResultList();

       List<CategoriaIICatalogoGeneralSocial> listaFinal = new ArrayList<CategoriaIICatalogoGeneralSocial>();

        for (Object[] elemento : lista) {
        	if((Integer)elemento[12] == null){
                listaFinal.add(
                        new CategoriaIICatalogoGeneralSocial(
                                (Integer) elemento[0],(Boolean)elemento[1],(String)elemento[2],(String)elemento[3],  (Integer)elemento[4],
                                (Integer)elemento[5],(String)elemento[6],(String)elemento[7],(Integer)elemento[8],
                                (String)elemento[9],(String)elemento[10], (Date)elemento[13], (Integer)elemento[11]  ));
        	}
        }
        return listaFinal;

    }
    
    //Fin historico
}
