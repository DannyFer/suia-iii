package ec.gob.ambiente.suia.catalogos.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.DescripcionActividadMineriaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class CatalogosDescripcionProcesoPmaService {

    @EJB
    private CrudServiceBean crudServiceBean;

    @EJB
    private DescripcionActividadMineriaFacade descripcionActividadMineriaFacade;

    @SuppressWarnings("unchecked")
    public Map<String, List<?>> getCatalogos(List<String> aplicaCatalogoList, List<Integer> catalogoCategoriaFaseIdList, Integer idActividadEspecial, String codigoSubsector) throws Exception {

        Map<String, List<?>> outPut = new HashMap<String, List<?>>();

        //Catalogo Actividades

        if (aplicaCatalogoList.contains("aplicaActividades")) {
            List<CatalogoActividadComercial> cat_actividades;
            //Se buscan las actividades según el sector y las fases seleccionadas
            cat_actividades = (List<CatalogoActividadComercial>) crudServiceBean
                    .getEntityManager()
                    .createQuery("From CatalogoActividadComercial c where c.categoriaFase.id in:CategoriaFaseList and c.estado = true ORDER BY c.nombreActividad")
                    .setParameter("CategoriaFaseList", catalogoCategoriaFaseIdList)
                    .getResultList();

            //Se busca y agrega la categoría de "Otras"
            List<CatalogoActividadComercial> cat_Otras = (List<CatalogoActividadComercial>) crudServiceBean
                    .getEntityManager()
                    .createQuery("From CatalogoActividadComercial c where c.categoriaFase is null and c.estado = true")
                    .getResultList();

            if (!cat_Otras.isEmpty()) {
                cat_actividades.add(cat_Otras.get(0));
            }

            outPut.put("ACTIVITIES_CAT", cat_actividades);

        }

        //Catalogo Herramientas

        if (aplicaCatalogoList.contains("aplicaHerramientas")) {

            List<CatalogoHerramienta> cat_herramientas;

            if (idActividadEspecial != -1) {
                //Se buscan las herramientas específicas para la actividad especial
                cat_herramientas = (List<CatalogoHerramienta>) crudServiceBean
                        .getEntityManager()
                        .createQuery("From CatalogoHerramienta c where c.actividadEspecial.id =:idActividadEspecial and c.estado = true ORDER BY c.nombreHerramienta")
                        .setParameter("idActividadEspecial", idActividadEspecial).getResultList();
            } else {
                //Se buscan las herramientas según el sector y las fases seleccionadas
                cat_herramientas = (List<CatalogoHerramienta>) crudServiceBean
                        .getEntityManager()
                        .createQuery("From CatalogoHerramienta c where c.categoriaFase.id in:CategoriaFaseList and c.estado = true ORDER BY c.nombreHerramienta")
                        .setParameter("CategoriaFaseList", catalogoCategoriaFaseIdList).getResultList();

            }

            //Se busca y agrega la categoría de "Otras"
            List<CatalogoHerramienta> cat_Otras = (List<CatalogoHerramienta>) crudServiceBean
                    .getEntityManager()
                    .createQuery("From CatalogoHerramienta c where c.categoriaFase is null and c.actividadEspecial is null and c.estado = true")
                    .getResultList();

            if (!cat_Otras.isEmpty()) {
                cat_herramientas.add(cat_Otras.get(0));
            }
            for (CatalogoHerramienta cat : cat_herramientas) {
                if(cat.getCategoriaFase()!=null && cat.getCategoriaFase().getFase()!=null) {
                    cat.getCategoriaFase().getFase().getId();
                }
                if(cat.getActividadEspecial()!=null && cat.getActividadEspecial().getTipoSubsector()!=null) {
                    cat.getActividadEspecial().getTipoSubsector().getId();
                }
            }
            outPut.put("TOOLS_CAT", cat_herramientas);
        }

        //Catalogo Insumos

        if (aplicaCatalogoList.contains("aplicaInsumos")) {

            List<CatalogoInsumo> cat_insumos;

            if (idActividadEspecial != -1) {
                //Se buscan los insumos específicos para la actividad especial
                cat_insumos = (List<CatalogoInsumo>) crudServiceBean
                        .getEntityManager()
                        .createQuery("From CatalogoInsumo c where c.actividadEspecial.id =:catalogoCategoriaId and c.estado = true or c.categoriaFase is null and c.actividadEspecial is null and c.estado = true and c.nombreInsumo != 'Otros' ORDER BY c.nombreInsumo")
                        .setParameter("catalogoCategoriaId", idActividadEspecial).getResultList();
            } else {
                //Se buscan los insumos según el sector y las fases seleccionadas
                cat_insumos = (List<CatalogoInsumo>) crudServiceBean
                        .getEntityManager()
                        .createQuery("From CatalogoInsumo c where c.categoriaFase.id in:CategoriaFaseList and c.estado = true or c.categoriaFase is null and c.actividadEspecial is null and c.estado = true and c.nombreInsumo != 'Otros' ORDER BY c.nombreInsumo")
                        .setParameter("CategoriaFaseList", catalogoCategoriaFaseIdList).getResultList();
            }

            //Se busca y agrega la categoría de "Otras"
            List<CatalogoInsumo> cat_Otras = (List<CatalogoInsumo>) crudServiceBean
                    .getEntityManager()
                    .createQuery("From CatalogoInsumo c where c.categoriaFase is null and c.actividadEspecial is null and c.estado = true and c.nombreInsumo = 'Otros' ORDER BY c.nombreInsumo")
                    .getResultList();

            if (!cat_Otras.isEmpty()) {
                cat_insumos.add(cat_Otras.get(0));
            }

            outPut.put("SUPPLIE_CAT", cat_insumos);

            //Unidades de medidas
            List<UnidadMedida> cat_unidades = (List<UnidadMedida>) crudServiceBean
                    .getEntityManager()
                    .createQuery("From UnidadMedida c where c.estado = true ORDER BY c.siglas").getResultList();

            outPut.put("METRICS_CAT", cat_unidades);
        }

        //Catalogo Tecnicas

        if (aplicaCatalogoList.contains("aplicaTecnicas")) {
            List<CatalogoTecnica> cat_tecnicas = descripcionActividadMineriaFacade.getCatalogoTecnicas();
            outPut.put("TECH_CAT", cat_tecnicas);
        }

        //Catalogo Instalaciones

        if (aplicaCatalogoList.contains("aplicaInstalaciones")) {
            List<CatalogoInstalacion> cat_instalaciones = descripcionActividadMineriaFacade.getCatalogoInstalaciones(codigoSubsector);
            outPut.put("INSTALATIONS_CAT", cat_instalaciones);
        }

        //Catalogo para Plaguicidas

        if (aplicaCatalogoList.contains("aplicaPlaguicidas")) {
            List<CatalogoCategoriaToxicologica> cat_categoriaToxicologica = (List<CatalogoCategoriaToxicologica>) crudServiceBean
                    .getEntityManager()
                    .createQuery("From CatalogoCategoriaToxicologica t where t.estado=true").getResultList();

            outPut.put("CATTOXICO_CAT", cat_categoriaToxicologica);
        }

        return outPut;
    }
}