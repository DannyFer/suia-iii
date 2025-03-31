package ec.gob.ambiente.suia.catalogos.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneralBiotico;
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
public class CategoriaIICatalogoBioticoService {

    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public List<CategoriaIICatalogoGeneralBiotico> catalogoSeleccionadosCategoriaIITipo(
            Integer idProyecto, String codigo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("idProyecto", idProyecto);
        parameters.put("catyCode", codigo);
        return (List<CategoriaIICatalogoGeneralBiotico>) crudServiceBean
                .findByNamedQuery(
                        CategoriaIICatalogoGeneralBiotico.FIND_BY_PROJECT_CATYCODE,
                        parameters);
    }

    @SuppressWarnings("unchecked")
    public List<CategoriaIICatalogoGeneralBiotico> catalogoSeleccionadosCategoriaIITipo(
            Integer idProyecto, String codigo, String seccion) {
        return (List<CategoriaIICatalogoGeneralBiotico>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        "SELECT c FROM CategoriaIICatalogoGeneralBiotico c WHERE c.fichaAmbientalPma.proyectoLicenciamientoAmbiental.id = :idProyecto AND c.catalogo.tipoCatalogo.codigo = :catyCode and c.seccion = :seccion")
                .setParameter("idProyecto", idProyecto)
                .setParameter("catyCode", codigo)
                .setParameter("seccion", seccion).getResultList();

    }


    @SuppressWarnings("unchecked")
    public List<CategoriaIICatalogoGeneralBiotico> catalogoSeleccionadosCategoriaIITipo(
            Integer idProyecto, List<String> codigos, String seccion) {
        /*List<CategoriaIICatalogoGeneralBiotico> lista = (List<CategoriaIICatalogoGeneralBiotico>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT c FROM CategoriaIICatalogoGeneralBiotico c WHERE c.fichaAmbientalPma.proyectoLicenciamientoAmbiental.id = :idProyecto AND c.catalogo.tipoCatalogo.codigo  in(:catyCode) and c.seccion = :seccion")
				.setParameter("idProyecto", idProyecto)
				.setParameter("catyCode", codigos)
				.setParameter("seccion", seccion).getResultList();
   */


        List<Object[]> lista = (List<Object[]>) crudServiceBean
                .getEntityManager()
                .createNativeQuery(
                        "SELECT c.cgcb_id, c.cgcb_status, c.cgcb_value, ct.caty_code, c.cafa_id, c.gcbi_id,c.cgcb_section , cat.gcbi_description, c.cgcb_original_record_id, f.cafa_original_record_id   FROM  suia_iii.catii_general_catalogs_biotic c INNER JOIN suia_iii.catii_fapma f on c.cafa_id = f.cafa_id INNER JOIN public.general_catalogs_biotic cat on c.gcbi_id= cat.gcbi_id " +
                                "  INNER JOIN suia_iii.projects_environmental_licensing p on p.pren_id = f.pren_id INNER JOIN public.catalog_types ct on cat.caty_id = ct.caty_id " +
                                "  WHERE  p.pren_id = :idProyecto and  ct.caty_code  in ( :catyCode ) and c.cgcb_section = :seccion AND c.cgcb_status =TRUE ")
                .setParameter("idProyecto", idProyecto)
                .setParameter("catyCode", codigos)
                .setParameter("seccion", seccion).getResultList();

        List<CategoriaIICatalogoGeneralBiotico> listaFinal = new ArrayList<CategoriaIICatalogoGeneralBiotico>();

        for (Object[] elemento : lista) {
        	if((Integer)elemento[8] == null && (Integer)elemento[9] == null){
        		 listaFinal.add(
                         new CategoriaIICatalogoGeneralBiotico(
                                 (Integer) elemento[0],(Boolean)elemento[1],(String)elemento[2],(String)elemento[3],  (Integer)elemento[4],
                                 (Integer)elemento[5],(String)elemento[6] ,(String)elemento[7]));
        	}           
        }
       /* for (CategoriaIICatalogoGeneralBiotico categoriaIICatalogoGeneralBiotico : lista) {
            categoriaIICatalogoGeneralBiotico.getCatalogo().getTipoCatalogo()
                    .getId();
            listaFinal.add(categoriaIICatalogoGeneralBiotico);
        }*/
        return listaFinal;

    }


    // -------
    public void guardarCatalogoGeneralBiotico(
            List<CategoriaIICatalogoGeneralBiotico> catalogos) {
        crudServiceBean.saveOrUpdate(catalogos);
    }

    // --------
    public void eliminarCatalogoaGeneralBioticoCategoriaIIPorFichaCodigo(
            FichaAmbientalPma ficha, String codigo) {
        List<CategoriaIICatalogoGeneralBiotico> catalogos = catalogoSeleccionadosCategoriaIITipo(
                ficha.getProyectoLicenciamientoAmbiental().getId(), codigo);
        crudServiceBean.delete(catalogos);

    }

    public void eliminarCatalogoaGeneralBioticoCategoriaIIPorFichaCodigo(
            FichaAmbientalPma ficha, String codigo, String seccion) {
        List<CategoriaIICatalogoGeneralBiotico> catalogos = catalogoSeleccionadosCategoriaIITipo(
                ficha.getProyectoLicenciamientoAmbiental().getId(), codigo,
                seccion);
        crudServiceBean.delete(catalogos);

    }

    public void eliminarCatalogoaGeneralBioticoCategoriaIIPorFichaCodigo(
            FichaAmbientalPma ficha, List<String> codigos, String seccion) {
        Query q = crudServiceBean
                .getEntityManager()
                .createQuery(
                        "Update From CategoriaIICatalogoGeneralBiotico ca set ca.estado=false WHERE ca.estado=true and ca.id in (Select c.id From CategoriaIICatalogoGeneralBiotico c WHERE c.fichaAmbientalPma.id = :idFicha AND c.catalogo.tipoCatalogo.codigo in (:catyCodes) and c.seccion = :seccion)");
        q.setParameter("idFicha", ficha.getId());
        q.setParameter("catyCodes", codigos);
        q.setParameter("seccion", seccion);
        q.executeUpdate();

    }


    // -------
    
    /**
     * CF: Consulta de Historico
     */
    public List<CategoriaIICatalogoGeneralBiotico> obtenerCatalogoGeneralBioticoCategoriaIIPorFichaCodigo(FichaAmbientalPma ficha, List<String> codigos, String seccion) {
        try {
        	Query q = crudServiceBean.getEntityManager().createQuery(
                    "Select c From CategoriaIICatalogoGeneralBiotico c WHERE c.fichaAmbientalPma.id = :idFicha AND "
                    + "c.estado=true AND c.catalogo.tipoCatalogo.codigo in (:catyCodes) and c.seccion = :seccion) AND ca.idRegistroOriginal = null");
        		q.setParameter("idFicha", ficha.getId());
        		q.setParameter("catyCodes", codigos);
        		q.setParameter("seccion", seccion);
    
        		List<CategoriaIICatalogoGeneralBiotico> resultado = q.getResultList();
    
        		if(resultado != null)
        			return resultado;
    
        		return null;    
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public void guardarCatalogoGeneralBiotico(CategoriaIICatalogoGeneralBiotico catalogo){
    	crudServiceBean.saveOrUpdate(catalogo);
    }
    
    @SuppressWarnings("unchecked")
    public List<CategoriaIICatalogoGeneralBiotico> catalogoSeleccionadosCategoriaIITipoHistorico(
            Integer idProyecto, List<String> codigos, String seccion) {        

        List<Object[]> lista = (List<Object[]>) crudServiceBean
                .getEntityManager()
                .createNativeQuery(
                        "SELECT c.cgcb_id, c.cgcb_status, c.cgcb_value, ct.caty_code, c.cafa_id, c.gcbi_id,c.cgcb_section , cat.gcbi_description, "
                        + "c.cgcb_original_record_id, f.cafa_original_record_id, c.cgcb_historical_date   "
                        + "FROM  suia_iii.catii_general_catalogs_biotic c "
                        + "INNER JOIN suia_iii.catii_fapma f on c.cafa_id = f.cafa_id "
                        + "INNER JOIN public.general_catalogs_biotic cat on c.gcbi_id= cat.gcbi_id " 
                        + "INNER JOIN suia_iii.projects_environmental_licensing p on p.pren_id = f.pren_id "
                        + "INNER JOIN public.catalog_types ct on cat.caty_id = ct.caty_id " 
                        + "WHERE  p.pren_id = :idProyecto and  ct.caty_code  in ( :catyCode ) and "
                        + "c.cgcb_section = :seccion AND c.cgcb_status =TRUE and (c.cgcb_original_record_id is not null or c.cgcb_historical_date is not null)")
                .setParameter("idProyecto", idProyecto)
                .setParameter("catyCode", codigos)
                .setParameter("seccion", seccion).getResultList();

        List<CategoriaIICatalogoGeneralBiotico> listaFinal = new ArrayList<CategoriaIICatalogoGeneralBiotico>();

        for (Object[] elemento : lista) {
        	if((Integer)elemento[9] == null){
        		 listaFinal.add(
                         new CategoriaIICatalogoGeneralBiotico(
                                 (Integer) elemento[0],(Boolean)elemento[1],(String)elemento[2],(String)elemento[3],  (Integer)elemento[4],
                                 (Integer)elemento[5],(String)elemento[6] ,(String)elemento[7], (Integer)elemento[8], (Date)elemento[10]));
        	}           
        }
        return listaFinal;

    }
}
