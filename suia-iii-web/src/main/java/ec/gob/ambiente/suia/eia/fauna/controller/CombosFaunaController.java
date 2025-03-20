/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.fauna.controller;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import lombok.Getter;

/**
 *
 * @author ishmael
 */
@ManagedBean
@ViewScoped
public class CombosFaunaController implements Serializable {

    private static final long serialVersionUID = -443544566665L;

    @EJB
    private CatalogoGeneralFacade catalogoGeneralFacade;

    @Getter
    private List<SelectItem> listaTipoMuestreo;
    @Getter
    private List<SelectItem> listaPisoZoogeografico;
    @Getter
    private List<SelectItem> listaTipoRegistro;
    @Getter
    private List<SelectItem> listaTipoVegetacion;
    @Getter
    private List<SelectItem> listaCondicionesClimaticas;
    @Getter
    private List<SelectItem> listaDistribucionVerticalEspecie;
    @Getter
    private List<SelectItem> listaComportamientoSocial;
    @Getter
    private List<SelectItem> listaGremioAlimenticio;
    @Getter
    private List<SelectItem> listaPatronActividad;
    @Getter
    private List<SelectItem> listaFaseLunar;
    @Getter
    private List<SelectItem> listaSencibilidad;
    @Getter
    private List<SelectItem> listaEspeciesMigratorias;
    @Getter
    private List<SelectItem> listaEspeciesBioindicadoras;
    @Getter
    private List<SelectItem> listaUso;
    @Getter
    private List<SelectItem> listaUICN;
    @Getter
    private List<SelectItem> listaCities;
    @Getter
    private List<SelectItem> listaLibroRojo;
    @Getter
    private List<SelectItem> listaColectasIncidentales;
    @Getter
    private List<SelectItem> listaCentroTenenciaAutorizado;
    @Getter
    private List<SelectItem> listaModosReproduccion;
    @Getter
    private List<SelectItem> listaSistemasHidrograficos;
    @Getter
    private List<SelectItem> listaDistribucionColumnaAgua;
    @Getter
    private List<SelectItem> listaElementosSensibles;
    @Getter
    private List<SelectItem> listaCoberturaVegetal;
    @Getter
    private List<SelectItem> listaZonasIctiohidrograficas;
    @Getter
    private List<SelectItem> listaEpocaDelAnio;
    @Getter
    private List<SelectItem> listaGruposTroficos;
    @Getter
    private List<SelectItem> listaHabitoAlimenticio;
    @Getter
    private List<SelectItem> listaEspecieBioindicadoraCalidadAgua;
    @Getter
    private Map<String, String> mapaCatalogoGeneral;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(CombosFaunaController.class);
    private static final SelectItem SELECCIONE = new SelectItem(null, "Seleccione");

    /**
     *
     */
    @PostConstruct
    public void inicio() {
        mapaCatalogoGeneral = new HashMap<String, String>();
        cargarListaTipoMuestreo();
        cargarListaTipoVegetacion();
        cargarListaCondicionesClimaticas();
        cargarListaComportamientoSocial();
        cargarListaPatronActividad();
        cargarListaFaseLunar();
        cargarListaSencibilidad();
        cargarListaEspeciesBioindicadoras();
        cargarListaUICN();
        cargarListaCities();
        cargarListaLibroRojo();
        cargarListaColectasIncidentales();
        cargarListaCentroTeneciaAutorizado();
        cargarListaPisoZoogeografico();
        cargarListaElementosSensibles();
        cargarListaEpocaDelAnio();
        cargarListaCoberturaVegetal();
        cargarListaZonasIctiohidrograficas();
    }

    private void cargarListaTipoMuestreo() {
        listaTipoMuestreo = new ArrayList<SelectItem>();
        listaTipoMuestreo.add(SELECCIONE);
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.TIPO_MUESTREO);
            for (CatalogoGeneral c : listaDatos) {
                listaTipoMuestreo.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaPisoZoogeografico() {
        listaPisoZoogeografico = new ArrayList<SelectItem>();
        listaPisoZoogeografico.add(SELECCIONE);
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.PISO_ZOOGEOGRAFICO);
            for (CatalogoGeneral c : listaDatos) {
                listaPisoZoogeografico.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    /**
     *
     * @param codigo
     */
    public void cargarListaTipoRegistro(final String codigo) {
        listaTipoRegistro = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipoXCodigo(TipoCatalogo.TIPO_REGISTRO, codigo);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaTipoRegistro.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaTipoVegetacion() {
        listaTipoVegetacion = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.TIPO_VEGETACION);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaTipoVegetacion.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaCondicionesClimaticas() {
        listaCondicionesClimaticas = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.CONDICION_CLIMATICA);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaCondicionesClimaticas.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    /**
     *
     * @param codigo
     */
    public void cargarListaDistribucionVerticalEspecie(final String codigo) {
        listaDistribucionVerticalEspecie = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipoXCodigo(TipoCatalogo.DISTRIBUCION_VERTICAL_ESPECIE, codigo);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaDistribucionVerticalEspecie.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaComportamientoSocial() {
        listaComportamientoSocial = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipoXCodigo(TipoCatalogo.COMPORTAMIENTO_SOCIAL, CatalogoGeneral.FAUNA_CODIGO);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaComportamientoSocial.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    /**
     *
     * @param codigo
     */
    public void cargarListaGremioAlimenticio(final String codigo) {
        listaGremioAlimenticio = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipoXCodigo(TipoCatalogo.GREMIO_ALIMENTICIO, codigo);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaGremioAlimenticio.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaPatronActividad() {
        listaPatronActividad = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.CICLO_HORARIO);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaPatronActividad.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaFaseLunar() {
        listaFaseLunar = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.FASE_LUNAR);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaFaseLunar.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaSencibilidad() {
        listaSencibilidad = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.SENSIBILIDAD);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaSencibilidad.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    /**
     *
     * @param codigo
     */
    public void cargarListaEspeciesMigratorias(final String codigo) {
        listaEspeciesMigratorias = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipoXCodigo(TipoCatalogo.ESEPECIES_MIGRATORIAS, codigo);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaEspeciesMigratorias.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaEspeciesBioindicadoras() {
        listaEspeciesBioindicadoras = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.ESPECIES_BIOINDICADORAS);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaEspeciesBioindicadoras.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    /**
     *
     * @param codigo
     */
    public void cargarListaUso(final String codigo) {
        listaUso = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipoXCodigo(TipoCatalogo.USO, codigo);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaUso.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaUICN() {
        listaUICN = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.UICN_INTERNACIONAL);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaUICN.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaCities() {
        listaCities = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.CITIES);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaCities.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaLibroRojo() {
        listaLibroRojo = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.LIBRO_ROJO);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaLibroRojo.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaColectasIncidentales() {
        listaColectasIncidentales = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.COLECTAS_INCIDENTALES);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaColectasIncidentales.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaCentroTeneciaAutorizado() {
        listaCentroTenenciaAutorizado = new ArrayList<SelectItem>();
        listaCentroTenenciaAutorizado.add(new SelectItem("1", "Si"));
        listaCentroTenenciaAutorizado.add(new SelectItem("2", "No"));
    }

    /**
     *
     * @param codigo
     */
    public void cargarListaModosReproduccion(final String codigo) {
        listaModosReproduccion = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipoXCodigo(TipoCatalogo.MODOS_REPRODUCCION, codigo);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaModosReproduccion.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    /**
     *
     * @param codigo
     */
    public void cargarListaSistemasHidrograficos() {
        listaSistemasHidrograficos = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.SISTEMAS_HIDROGRAFICOS);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaSistemasHidrograficos.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    /**
     *
     * @param codigo
     */
    public void cargarListaDistribucionColumnaAgua(final String codigo) {
        listaDistribucionColumnaAgua = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipoXCodigo(TipoCatalogo.DISTRIBUCION_COLUMNA_AGUA, codigo);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaDistribucionColumnaAgua.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaElementosSensibles() {
        listaElementosSensibles = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.ELEMENTOS_SENCIBLES);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaElementosSensibles.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaCoberturaVegetal() {
        listaCoberturaVegetal = new ArrayList<SelectItem>();
        listaCoberturaVegetal.add(SELECCIONE);
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.COBERTURA_VEGETAL);
            for (CatalogoGeneral c : listaDatos) {
                listaCoberturaVegetal.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaZonasIctiohidrograficas() {
        listaZonasIctiohidrograficas = new ArrayList<SelectItem>();
        listaZonasIctiohidrograficas.add(SELECCIONE);
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.ZONAS_ICTIOHIDROGRAFICAS);
            for (CatalogoGeneral c : listaDatos) {
                listaZonasIctiohidrograficas.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaEpocaDelAnio() {
        listaEpocaDelAnio = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.EPOCA_DEL_ANIO);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaEpocaDelAnio.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }
    
    public void cargarListaGruposTroficos() {
        listaGruposTroficos = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.GRUPOS_TROFICOS);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaGruposTroficos.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }
    
    public void cargarListaHabitosAlimenticios() {
        listaHabitoAlimenticio = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.HABITOS_ALIMENTICIOS);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaHabitoAlimenticio.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }
    
    public void cargarListaEspecieBioindicadoraCalidadAgua() {
        listaEspecieBioindicadoraCalidadAgua = new ArrayList<SelectItem>();
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.ESPECIE_BIOINDICADORA_CALIDAD_AGUA);
            for (CatalogoGeneral c : listaDatos) {
                mapaCatalogoGeneral.put(c.getId().toString(), c.getDescripcion());
                listaEspecieBioindicadoraCalidadAgua.add(new SelectItem(c.getId(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

}
