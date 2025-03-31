package ec.gob.ambiente.suia.catalogos.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.TipoCatalogo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class CategoriaIICatalogoFisicoService {

    private String codigoClima = TipoCatalogo.CODIGO_CLIMA;
    private String codigoTipoSuelo = TipoCatalogo.CODIGO_TIPO_SUELO;
    private String codigoPendienteSuelo = TipoCatalogo.CODIGO_PENDIENTE_SUELO;

    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public List<CategoriaIICatalogoGeneralFisico> catalogoSeleccionadosCategoriaIITipo(
            Integer idProyecto, String codigo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("idProyecto", idProyecto);
        parameters.put("catyCode", codigo);
        return (List<CategoriaIICatalogoGeneralFisico>) crudServiceBean
                .findByNamedQuery(
                        CategoriaIICatalogoGeneralFisico.FIND_BY_PROJECT_CATYCODE,
                        parameters);
    }

    @SuppressWarnings("unchecked")
    public List<CategoriaIICatalogoGeneralFisico> catalogoSeleccionadosCategoriaIITipo(
            Integer idProyecto, String codigo, String seccion) {
        return (List<CategoriaIICatalogoGeneralFisico>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        "SELECT c FROM CategoriaIICatalogoGeneralFisico c WHERE c.fichaAmbientalPma.proyectoLicenciamientoAmbiental.id = :idProyecto AND c.catalogo.tipoCatalogo.codigo = :catyCode and c.seccion = :seccion and c.idRegistroOriginal = null")
                .setParameter("idProyecto", idProyecto)
                .setParameter("catyCode", codigo)
                .setParameter("seccion", seccion).getResultList();

    }

    @SuppressWarnings("unchecked")
    public List<CategoriaIICatalogoGeneralFisico> catalogoSeleccionadosCategoriaIITipo(
            Integer idProyecto, List<String> codigos, String seccion) {

        List<Object[]> lista = (List<Object[]>) crudServiceBean
                .getEntityManager()
                .createNativeQuery(
                        "SELECT c.cgcp_id, c.cgcp_status, c.cgcp_value, ct.caty_code,  c.cafa_id, c.gcph_id,c.cgcp_section, cat.gcph_description, c.cgcp_original_record_id, f.cafa_original_record_id FROM  suia_iii.catii_general_catalogs_physical c INNER JOIN suia_iii.catii_fapma f on c.cafa_id = f.cafa_id INNER JOIN public.general_catalogs_physical cat on c.gcph_id= cat.gcph_id\n" +
                                "  INNER JOIN suia_iii.projects_environmental_licensing p on p.pren_id = f.pren_id INNER JOIN public.catalog_types ct on cat.caty_id = ct.caty_id\n" +
                                "  WHERE  p.pren_id = :idProyecto and  ct.caty_code  in ( :catyCode ) and c.cgcp_section = :seccion AND c.cgcp_status=TRUE ")
                .setParameter("idProyecto", idProyecto)
                .setParameter("catyCode", codigos)
                .setParameter("seccion", seccion).getResultList();


		 List<CategoriaIICatalogoGeneralFisico> listaFinal = new ArrayList<CategoriaIICatalogoGeneralFisico>();
        for (Object[] elemento : lista) {
        	if((Integer)elemento[8] == null){
        		listaFinal.add(
                        new CategoriaIICatalogoGeneralFisico(
                                (Integer) elemento[0],(Boolean)elemento[1],(String)elemento[2],(String)elemento[3],  (Integer)elemento[4],
                                (Integer)elemento[5],(String)elemento[6],(String)elemento[7]));            
        	}

        }
		/*for (CategoriaIICatalogoGeneralFisico categoriaIICatalogoGeneralFisico : lista) {
			categoriaIICatalogoGeneralFisico.getCatalogo().getTipoCatalogo()
					.getId();
			listaFinal.add(categoriaIICatalogoGeneralFisico);
		}*/
		return listaFinal;

    }

    public List<CategoriaIICatalogoGeneralFisico> climasSeleccionadosCategoriaII(
            Integer idProyecto) {
        return catalogoSeleccionadosCategoriaIITipo(idProyecto, codigoClima);
    }

    public List<CategoriaIICatalogoGeneralFisico> tipoSuelosSeleccionadosCategoriaII(
            Integer idProyecto) {
        return catalogoSeleccionadosCategoriaIITipo(idProyecto, codigoTipoSuelo);
    }

    public List<CategoriaIICatalogoGeneralFisico> pendienteSuelosSeleccionadosCategoriaII(
            Integer idProyecto) {
        return catalogoSeleccionadosCategoriaIITipo(idProyecto,
                codigoPendienteSuelo);
    }

    // -------
    public void guardarCatalogoGeneralFisico(
            List<CategoriaIICatalogoGeneralFisico> catalogos) {
        crudServiceBean.saveOrUpdate(catalogos);
    }

    // --------
    public void eliminarCatalogoaGeneralFisicoCategoriaIIPorFichaCodigo(
            FichaAmbientalPma ficha, String codigo) {
        List<CategoriaIICatalogoGeneralFisico> catalogos = catalogoSeleccionadosCategoriaIITipo(
                ficha.getProyectoLicenciamientoAmbiental().getId(), codigo);
        crudServiceBean.delete(catalogos);

    }

    public void eliminarCatalogoaGeneralFisicoCategoriaIIPorFichaCodigo(
            FichaAmbientalPma ficha, String codigo, String seccion) {
        List<CategoriaIICatalogoGeneralFisico> catalogos = catalogoSeleccionadosCategoriaIITipo(
                ficha.getProyectoLicenciamientoAmbiental().getId(), codigo,
                seccion);
        crudServiceBean.delete(catalogos);

    }

    public void eliminarCatalogoaGeneralFisicoCategoriaIIPorFichaCodigo(
            FichaAmbientalPma ficha, List<String> codigos, String seccion) {
        Query q = crudServiceBean
                .getEntityManager()
                .createQuery(
                        "Update From CategoriaIICatalogoGeneralFisico ca set ca.estado=false WHERE ca.estado=true and ca.id in (Select c.id From CategoriaIICatalogoGeneralFisico c WHERE c.fichaAmbientalPma.id = :idFicha AND c.catalogo.tipoCatalogo.codigo in (:catyCodes) and c.seccion = :seccion)");
        q.setParameter("idFicha", ficha.getId());
        q.setParameter("catyCodes", codigos);
        q.setParameter("seccion", seccion);
        q.executeUpdate();

    }

    public void eliminarClimaPorFicha(FichaAmbientalPma ficha) {
        eliminarCatalogoaGeneralFisicoCategoriaIIPorFichaCodigo(ficha,
                codigoClima);
    }

    public void eliminarTipoSueloPorFicha(FichaAmbientalPma ficha) {
        eliminarCatalogoaGeneralFisicoCategoriaIIPorFichaCodigo(ficha,
                codigoTipoSuelo);
    }

    // -------
    
    /**
     * Cris F: aumento método para obtener las categorias General Fisica
     */
    
    public List<CategoriaIICatalogoGeneralFisico> obtenerCatalogoGeneralFisicoCategoriaIIPorFichaCodigo(FichaAmbientalPma ficha, List<String> codigos, String seccion) {
        try {
        	Query q = crudServiceBean.getEntityManager().createQuery(
                    "Select c From CategoriaIICatalogoGeneralFisico c WHERE c.fichaAmbientalPma.id = :idFicha AND "
                    + "c.estado=true AND c.catalogo.tipoCatalogo.codigo in (:catyCodes) and c.seccion = :seccion AND c.idRegistroOriginal = null");
        		q.setParameter("idFicha", ficha.getId());
        		q.setParameter("catyCodes", codigos);
        		q.setParameter("seccion", seccion);
    
        		List<CategoriaIICatalogoGeneralFisico> resultado = q.getResultList();
    
        		if(resultado != null)
        			return resultado;
    
        		return null;    
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public void guardarCatalogoGeneral(CategoriaIICatalogoGeneralFisico catalogo) {
    	crudServiceBean.saveOrUpdate(catalogo);
    }
    
    
    @SuppressWarnings("unchecked")
    public List<CategoriaIICatalogoGeneralFisico> catalogoSeleccionadosCategoriaIITipoHistorico(
            Integer idProyecto, List<String> codigos, String seccion) {

        List<Object[]> lista = (List<Object[]>) crudServiceBean
                .getEntityManager()
                .createNativeQuery(
                        "SELECT c.cgcp_id, c.cgcp_status, c.cgcp_value, ct.caty_code,  c.cafa_id, c.gcph_id,c.cgcp_section, "
                        + "cat.gcph_description, c.cgcp_original_record_id, f.cafa_original_record_id, c.cgcp_historical_date "
                        + "FROM  suia_iii.catii_general_catalogs_physical c "
                        + "INNER JOIN suia_iii.catii_fapma f on c.cafa_id = f.cafa_id "
                        + "INNER JOIN public.general_catalogs_physical cat on c.gcph_id= cat.gcph_id\n" 
                        + "  INNER JOIN suia_iii.projects_environmental_licensing p on p.pren_id = f.pren_id "
                        + "INNER JOIN public.catalog_types ct on cat.caty_id = ct.caty_id\n" 
                        + "  WHERE  p.pren_id = :idProyecto and  ct.caty_code  in ( :catyCode ) and "
                        + "c.cgcp_section = :seccion AND c.cgcp_status=TRUE and (c.cgcp_original_record_id is not null or c.cgcp_historical_date is not null)")
                .setParameter("idProyecto", idProyecto)
                .setParameter("catyCode", codigos)
                .setParameter("seccion", seccion).getResultList();


		 List<CategoriaIICatalogoGeneralFisico> listaFinal = new ArrayList<CategoriaIICatalogoGeneralFisico>();
        for (Object[] elemento : lista) {
        	if((Integer)elemento[9] == null){
        		listaFinal.add(
                        new CategoriaIICatalogoGeneralFisico(
                                (Integer) elemento[0],(Boolean)elemento[1],(String)elemento[2],(String)elemento[3],  (Integer)elemento[4],
                                (Integer)elemento[5],(String)elemento[6],(String)elemento[7], (Integer)elemento[8], (Date)elemento[10]));            
        	}
        }
		return listaFinal;
    }
    
    //Fin codigo historico
}
