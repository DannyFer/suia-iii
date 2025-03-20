package ec.gob.ambiente.prevencion.categoria2.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogosDescripcionProcesoPmaFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.ActividadProcesoPma;
import ec.gob.ambiente.suia.domain.CatalogoActividadComercial;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaToxicologica;
import ec.gob.ambiente.suia.domain.CatalogoHerramienta;
import ec.gob.ambiente.suia.domain.CatalogoInstalacion;
import ec.gob.ambiente.suia.domain.CatalogoInsumo;
import ec.gob.ambiente.suia.domain.CatalogoTecnica;
import ec.gob.ambiente.suia.domain.DesechoPeligrosoProcesoPma;
import ec.gob.ambiente.suia.domain.DesechoSanitarioProcesoPma;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Fase;
import ec.gob.ambiente.suia.domain.FertilizanteProcesoPma;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.HerramientaProcesoPma;
import ec.gob.ambiente.suia.domain.Instalacion;
import ec.gob.ambiente.suia.domain.InsumoProcesoPma;
import ec.gob.ambiente.suia.domain.PlaguicidaProcesoPma;
import ec.gob.ambiente.suia.domain.TecnicaProcesoPma;
import ec.gob.ambiente.suia.domain.TipoEstudio;
import ec.gob.ambiente.suia.domain.TipoSubsector;
import ec.gob.ambiente.suia.domain.UnidadMedida;
import ec.gob.ambiente.suia.domain.VehiculoDesechoSanitarioProcesoPma;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FaseFichaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DescripcionProcesoPmaBean implements Serializable {

    private static final long serialVersionUID = -3526371284133629686L;

    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @Setter
    @ManagedProperty(value = "#{desechoPeligrosoProcesoBean}")
    private DesechoPeligrosoProcesoBean desechoPeligrosoProcesoBean;

    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

    @EJB
    private FaseFichaAmbientalFacade faseFichaAmbientalFacade;

    @EJB
    private CatalogosDescripcionProcesoPmaFacade catalogosDescripcionProcesoFacade;
    
    //Cris F: aumento de facade de documentos
    @EJB
    private DocumentosFacade documentosFacade;

    @Getter
    @Setter
    private FichaAmbientalPma fichaAmbiental;

    @Setter
    private List<CatalogoActividadComercial> todasActividadesComerciales;

    public List<CatalogoActividadComercial> getTodasActividadesComerciales() {
        return getActividadesFase();
    }

    @Setter
    private List<CatalogoCategoriaFase> categoriaFaseList;

    public List<CatalogoCategoriaFase> getCategoriaFaseList() {
        return categoriaFaseList == null ? new ArrayList<CatalogoCategoriaFase>() : categoriaFaseList;
    }

    @Setter
    private List<Fase> fases;

    public List<Fase> getFases() {
        return fases == null ? new ArrayList<Fase>() : fases;
    }

    @Setter
    @Getter
    private Fase faseSeleccionada;

    @Getter
    @Setter
    private String nombreFases = "";

    @Setter
    @Getter
    private ActividadProcesoPma actividadProceso;

    @Setter
    private List<ActividadProcesoPma> actividadesProceso;

    public List<ActividadProcesoPma> getActividadesProceso() {
        return actividadesProceso == null ? new ArrayList<ActividadProcesoPma>() : actividadesProceso;
    }

    @Setter
    private List<CatalogoHerramienta> herramientasCatalogo;

    public List<CatalogoHerramienta> getHerramientasCatalogo() {
        return herramientasCatalogo == null ? new ArrayList<CatalogoHerramienta>() : herramientasCatalogo;
    }

    @Setter
    @Getter
    private HerramientaProcesoPma herramientaProceso;

    @Setter
    private List<HerramientaProcesoPma> herramientasProceso;

    public List<HerramientaProcesoPma> getHerramientasProceso() {
        return herramientasProceso == null ? new ArrayList<HerramientaProcesoPma>() : herramientasProceso;
    }

    @Setter
    private List<CatalogoInsumo> insumosCatalogo;

    public List<CatalogoInsumo> getInsumosCatalogo() {
        return insumosCatalogo == null ? new ArrayList<CatalogoInsumo>() : insumosCatalogo;
    }

    @Setter
    @Getter
    private InsumoProcesoPma insumoProceso;

    @Setter
    private List<InsumoProcesoPma> insumosProceso;

    public List<InsumoProcesoPma> getInsumosProceso() {
        return insumosProceso == null ? new ArrayList<InsumoProcesoPma>() : insumosProceso;
    }

    @Setter
    private List<UnidadMedida> unidadesMedidaCatalogo;

    public List<UnidadMedida> getUnidadesMedidaCatalogo() {
        return unidadesMedidaCatalogo == null ? new ArrayList<UnidadMedida>() : unidadesMedidaCatalogo;
    }

    @Setter
    private List<CatalogoCategoriaToxicologica> categoriaToxicologicaCatalogo;

    public List<CatalogoCategoriaToxicologica> getCategoriaToxicologicaCatalogo() {
        return categoriaToxicologicaCatalogo == null ? new ArrayList<CatalogoCategoriaToxicologica>() : categoriaToxicologicaCatalogo;
    }

    @Setter
    @Getter
    private PlaguicidaProcesoPma plaguicidaProceso;

    @Setter
    private List<PlaguicidaProcesoPma> plaguicidasProceso;

    public List<PlaguicidaProcesoPma> getPlaguicidasProceso() {
        return plaguicidasProceso == null ? new ArrayList<PlaguicidaProcesoPma>() : plaguicidasProceso;
    }

    @Setter
    @Getter
    private FertilizanteProcesoPma fertilizanteProceso;

    @Setter
    private List<FertilizanteProcesoPma> fertilizantesProceso;

    public List<FertilizanteProcesoPma> getFertilizantesProceso() {
        return fertilizantesProceso == null ? new ArrayList<FertilizanteProcesoPma>() : fertilizantesProceso;
    }

    @Setter
    @Getter
    private DesechoSanitarioProcesoPma desechoSanitarioProceso;

    @Setter
    @Getter
    private VehiculoDesechoSanitarioProcesoPma vehiculoDesechoSanitarioProceso;

    @Setter
    private List<VehiculoDesechoSanitarioProcesoPma> vehiculosDesechoSanitarioProceso;

    public List<VehiculoDesechoSanitarioProcesoPma> getVehiculosDesechoSanitarioProceso() {
       
        if(vehiculosDesechoSanitarioProceso==null){
            vehiculosDesechoSanitarioProceso= new ArrayList<VehiculoDesechoSanitarioProcesoPma>();
        }
        return vehiculosDesechoSanitarioProceso;
        //return vehiculosDesechoSanitarioProceso == null ? new ArrayList<VehiculoDesechoSanitarioProcesoPma>() : vehiculosDesechoSanitarioProceso;
       
    }

    @Setter
    @Getter
    private DesechoPeligrosoProcesoPma desechoPeligrosoProceso;

    @Setter
    private List<DesechoPeligrosoProcesoPma> desechosPeligrosoProceso;

    public List<DesechoPeligrosoProcesoPma> getDesechosPeligrosoProceso() {
       
        if(desechosPeligrosoProceso==null){
            desechosPeligrosoProceso= new ArrayList<DesechoPeligrosoProcesoPma>();
        }
        return desechosPeligrosoProceso;
        //return desechosPeligrosoProceso == null ? new ArrayList<DesechoPeligrosoProcesoPma>() : desechosPeligrosoProceso;
       
    }

    @Setter
    private List<CatalogoInstalacion> instalacionesCatalogo;

    public List<CatalogoInstalacion> getInstalacionesCatalogo() {
        return instalacionesCatalogo == null ? new ArrayList<CatalogoInstalacion>() : instalacionesCatalogo;
    }

    @Setter
    private List<CatalogoTecnica> tecnicasCatalogo;

    public List<CatalogoTecnica> getTecnicasCatalogo() {
        return tecnicasCatalogo == null ? new ArrayList<CatalogoTecnica>() : tecnicasCatalogo;
    }

    @Getter
    @Setter
    private Boolean aplicaHerramientas = true;

    @Getter
    @Setter
    private Boolean aplicaInsumos = true;

    @Getter
    @Setter
    private Boolean aplicaDisposicionFinal = false;

    @Getter
    @Setter
    private Boolean aplicaTransporteDesechosExpost = false;

    @Getter
    @Setter
    private Boolean aplicaTransporteDesechosExantes = false;

    @Getter
    @Setter
    private Boolean aplicaPlaguicidas = false;

    @Getter
    @Setter
    private Boolean incluyeDesechosSanitarios = false;

    @Getter
    @Setter
    private Boolean aplicaTecnicas = false;

    @Getter
    @Setter
    private Boolean aplicaInstalaciones = false;

    @Getter
    @Setter
    private String buttonMode = "Adicionar";
    
   //Cris F: VARIABLES PARA OBTENER EL HISTORICO
    @Getter
    @Setter
    private List<ActividadProcesoPma> listaActividadesEliminadas, listaActividadesModificadas;
        
    @Getter
    @Setter
    private List<HerramientaProcesoPma> listaHerramientasEliminadas, listaHerramientasModificadas;
        
    @Getter
    @Setter
    private List<InsumoProcesoPma> listaInsumosEliminados, listaInsumosModificados;
    
    @Getter
    @Setter
    private List<Instalacion> listaInstalacionHistorico;
    
    @Getter
    @Setter
    private List<TecnicaProcesoPma> listaTecnicasProcesoHistorico;
    
    @Setter
    @Getter
    private List<PlaguicidaProcesoPma> listaPlaguicidasEliminados, listaPlaguicidasModificados, listaPlaguicidasHistorico;
    
    @Getter
    @Setter
    private List<FertilizanteProcesoPma> listaFertilizantesEliminados, listaFertilizantesModificados, listaFertilizantesHistorico;
    
    @Getter
    @Setter
    private List<DesechoSanitarioProcesoPma> listaDesechosSanitariosHistorico;
    
    @Getter
    @Setter
    private List<DesechoPeligrosoProcesoPma> listaDesechosPeligrososEliminados, listaDesechosPeligrososModificados, listaDesechosPeligrososHistorico;
    
    @Getter
    @Setter
    private List<VehiculoDesechoSanitarioProcesoPma> listaVehiculoDesechoSanitarioEliminados, listaVehiculoDesechoSanitarioModificados, listaVehiculoDesechoSanitarioHistorico;

    //variable para guardar historial
    @Getter
    @Setter
    private boolean guardarHistorial = false;
    @PostConstruct
    public void init() throws Exception {
        
        fichaAmbiental = fichaAmbientalPmaFacade.getFichaAmbientalPorIdProyecto(proyectosBean.getProyecto().getId());
        vehiculoDesechoSanitarioProceso= new VehiculoDesechoSanitarioProcesoPma();
        //Se buscan las relaciones subsector-fases
        categoriaFaseList = faseFichaAmbientalFacade.obtenerCatalogoCategoriaFasesPorFicha(fichaAmbiental.getId());
        List<Integer> categoriaFaseIdList = new ArrayList<>();
        TipoSubsector Subsector = proyectosBean.getProyecto().getCatalogoCategoria().getTipoSubsector();

//        System.out.println("--------------------------------------------------");
//        System.out.println("Tipo subsector: " + Subsector.getNombre() + " (id: " + Subsector.getId() + ")" + " (code: " + Subsector.getCodigo() + ")");

        fases = new ArrayList<>();
        desechoPeligrosoProceso = new DesechoPeligrosoProcesoPma();
        if(desechosPeligrosoProceso==null){
        desechosPeligrosoProceso = new ArrayList<DesechoPeligrosoProcesoPma>();
        }
        for (CatalogoCategoriaFase catp : categoriaFaseList) {
            categoriaFaseIdList.add(catp.getId());
            nombreFases += catp.getFase().getNombre() + ", ";
            fases.add(catp.getFase()); //Se agregan todas las fases
        }

        if (categoriaFaseList.isEmpty()) {
            RequestContext.getCurrentInstance().execute("PF('dlgRequerimiento').show();");
            actividadProceso = new ActividadProcesoPma();
            herramientaProceso = new HerramientaProcesoPma();
            insumoProceso = new InsumoProcesoPma();
        } else {
            //Nombre de las fases seleccionadas
            nombreFases = nombreFases.substring(0, nombreFases.length() - 2);
            nombreFases += ".";
            nombreFases = nombreFases.toUpperCase();

            //Se verifican los elementos que aplican
            String codigoSubsector = proyectosBean.getProyecto().getCatalogoCategoria().getTipoSubsector().getCodigo();
            Boolean requiereViabilidad = proyectosBean.getProyecto().getCatalogoCategoria().getRequiereViabilidad();
            //System.out.println("Codigo actividad: " + requiereViabilidad);

            List<String> aplicaCatalogList = new ArrayList<>();

            aplicaCatalogList.add("aplicaActividades");
            aplicaCatalogList.add("aplicaInsumos");
            aplicaCatalogList.add("aplicaHerramientas");

            // En caso de ser saneamiento - RESIDUOS SÓLIDOS
            if (codigoSubsector.equals("0015")) {
                aplicaHerramientas = false;
                aplicaInsumos = false;
                aplicaCatalogList.remove("aplicaInsumos");
                aplicaCatalogList.remove("aplicaHerramientas");

                aplicaDisposicionFinal = true;
                cargarDesechoSanitarioProceso();
               
                // En caso de tipo de estudio Exantes
                if (fichaAmbiental.getProyectoLicenciamientoAmbiental().getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EX_ANTE) && incluyeDesechosSanitarios) {
                    aplicaTransporteDesechosExantes = true;
                }

                // En caso de tipo de estudio Expost
                if (fichaAmbiental.getProyectoLicenciamientoAmbiental().getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EX_POST) && incluyeDesechosSanitarios) {
                    aplicaTransporteDesechosExpost = true;
                }
            }

            // En caso de ser saneamiento - PLANTA POTABILIZADORA DE AGUA, PLANTA DE TRATAMIENTO AGUAS RESIDUALES
            if (codigoSubsector.equals("0016") || codigoSubsector.equals("0017")) {
                aplicaHerramientas = false;
                aplicaInsumos = false;
                aplicaCatalogList.remove("aplicaInsumos");
                aplicaCatalogList.remove("aplicaHerramientas");
            }

            //Algunos que Manejan viavilidad
            if (requiereViabilidad) {
                // En caso de tipo de estudio Exantes
                if (fichaAmbiental.getProyectoLicenciamientoAmbiental().getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EX_ANTE)) {
                    aplicaTransporteDesechosExantes = true;
                }

                // En caso de tipo de estudio Expost
                if (fichaAmbiental.getProyectoLicenciamientoAmbiental().getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EX_POST)) {
                    aplicaTransporteDesechosExpost = true;
                }
            }
            //En caso de ser Minería Explotación Inicial
            if (codigoSubsector.equals("0004")) {
                aplicaTecnicas = true;
                aplicaInstalaciones = true;
                aplicaCatalogList.add("aplicaTecnicas");
                aplicaCatalogList.add("aplicaInstalaciones");
            }

            //En caso de ser Minería Libre Aprovechamientos
            if (codigoSubsector.equals("0005")) {
                aplicaInstalaciones = true;
                aplicaCatalogList.add("aplicaInstalaciones");
            }

            //En caso de ser Agrícola, Pecuario o Agroindustria se verifica el uso de plaguicidas
            if (codigoSubsector.equals("0001") || codigoSubsector.equals("0002") || codigoSubsector.equals("0003")) {
                //if(true){
                if (proyectosBean.getProyecto().getUtilizaSustaciasQuimicas() != null && proyectosBean.getProyecto().getUtilizaSustaciasQuimicas()) {
                    //if(true){
                    aplicaPlaguicidas = true;
                    aplicaCatalogList.add("aplicaPlaguicidas");
                }
            }

            //Se cargan los catálogos de los elementos que aplican------------------
            CargarCatalogos(categoriaFaseIdList, Subsector, aplicaCatalogList);

            //se cargan los datos ya guardados--------------------------------------

            cargarActividadesProceso();

            if (aplicaHerramientas) {
                cargarHerramientasProceso();
            }

            if (aplicaInsumos) {
                cargarInsumosProceso();
            }

            if (aplicaInstalaciones) {
                cargarInstalacionesProceso();
            }

            if (aplicaTecnicas) {
                cargarTecnicasProceso();
            }

            if (aplicaPlaguicidas) {
                cargarPlaguicidasProceso();
                cargarFertilizantesProceso();
            }

            if (aplicaDisposicionFinal) {
                cargarDesechoSanitarioProceso();
            }
           
            if (aplicaTransporteDesechosExantes) {
                cargarDesechosPeligrososProceso(false);
            }

            if (aplicaTransporteDesechosExpost) {
                cargarVehiculosDesechoSanitarioProceso();
                cargarDesechosPeligrososProceso(true);
            }                      
        }
        
    }
    public void inclusionDesechoSanitario() {
        if (fichaAmbiental.getProyectoLicenciamientoAmbiental()
                .getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EX_ANTE)) {
            if (incluyeDesechosSanitarios) {
                aplicaTransporteDesechosExantes = true;
            } else {
                aplicaTransporteDesechosExantes = false;
            }
        }
        if (fichaAmbiental.getProyectoLicenciamientoAmbiental()
                .getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EX_POST)) {
            if (incluyeDesechosSanitarios) {
                aplicaTransporteDesechosExpost = true;
            } else {
                aplicaTransporteDesechosExpost = false;
            }
        }
        RequestContext.getCurrentInstance().update("form");        
    }   
    public Date getExpostOrCurrentDate() {
        //Si es expost
        if (fichaAmbiental.getProyectoLicenciamientoAmbiental().getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EX_POST)) {
            return fichaAmbiental.getProyectoLicenciamientoAmbiental().getFechaInicioOperaciones();
        }
        return new Date();
    }

    public Date getCurrentDate() {
        return new Date();
    }

    //Datos a cargar...

    private void CargarCatalogos(List<Integer> categoriaFaseIdList, TipoSubsector Subsector, List<String> aplicaCatalogList) throws Exception {
        Integer idActividadEspecial = faseFichaAmbientalFacade.getActividadEspecial(Subsector.getId());
        Map<String, List<?>> catalogos = catalogosDescripcionProcesoFacade.getCatalogos(aplicaCatalogList, categoriaFaseIdList, idActividadEspecial, Subsector.getCodigo());
        Set<String> keys = catalogos.keySet();

        //Catalogo actividades
        if (keys.contains("ACTIVITIES_CAT")) {
            todasActividadesComerciales = (List<CatalogoActividadComercial>) catalogos.get("ACTIVITIES_CAT");
        }

        //Catalogo herramientas
        if (keys.contains("TOOLS_CAT")) {
            herramientasCatalogo = (List<CatalogoHerramienta>) catalogos.get("TOOLS_CAT");
        }

        //Catalogo insumos
        if (keys.contains("SUPPLIE_CAT")) {
            insumosCatalogo = (List<CatalogoInsumo>) catalogos.get("SUPPLIE_CAT");
            unidadesMedidaCatalogo = (List<UnidadMedida>) catalogos.get("METRICS_CAT");
        }

        //Catalogo tecnicas
        if (keys.contains("TECH_CAT")) {
            tecnicasCatalogo = (List<CatalogoTecnica>) catalogos.get("TECH_CAT");
        }

        //Catalogo instalaciones
        if (keys.contains("INSTALATIONS_CAT")) {
            instalacionesCatalogo = (List<CatalogoInstalacion>) catalogos.get("INSTALATIONS_CAT");
        }

        //Catalogo para plaguicidas
        if (keys.contains("CATTOXICO_CAT")) {
            categoriaToxicologicaCatalogo = (List<CatalogoCategoriaToxicologica>) catalogos.get("CATTOXICO_CAT");
        }
    }

    private List<CatalogoActividadComercial> getActividadesFase() {
        List<CatalogoActividadComercial> ordenada = new ArrayList<>();
        if (faseSeleccionada != null) {
            for (CatalogoActividadComercial act : todasActividadesComerciales) {
                if (act.getCategoriaFase() != null && act.getCategoriaFase().getFase().equals(faseSeleccionada)) {
                    ordenada.add(act);
                }
            }
            if (!(proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.02.01")) && 
            		!(proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.06"))){
            	ordenada.add(getActividadOtros(todasActividadesComerciales)); //Se agrega otros
            }
        }
        return ordenada;
    }

    private CatalogoActividadComercial getActividadOtros(List<CatalogoActividadComercial> lista) {
        for (CatalogoActividadComercial actividad : lista) {
            if (actividad.getNombreActividad().equals("Otros")) {
                return actividad;
            }
        }
        return null;
    }

    private void cargarActividadesProceso() {
        //Se cargan las actividades del proceso ya guardadas
        if (fichaAmbiental.getId() != null) {
            actividadesProceso = fichaAmbientalPmaFacade.getActividadesProcesosFichaPorIdFicha(fichaAmbiental.getId());
            
            List<ActividadProcesoPma> listaActividadesHistorico = fichaAmbientalPmaFacade.getActividadesProcesosFichaHistoricoPorIdFicha(fichaAmbiental.getId());
            if(listaActividadesHistorico != null && !listaActividadesHistorico.isEmpty())
            	obtenerHistoricoActividades(actividadesProceso, listaActividadesHistorico);
        }
        
        //Se eliminan del catálogo las actividades ya registradas
        for (ActividadProcesoPma actProceso : actividadesProceso) {
            if (!actProceso.getActividadComercial().getNombreActividad().equals("Otros"))
                todasActividadesComerciales.remove(actProceso.getActividadComercial());
            
            if(actProceso.getFechaHistorico() != null){
            	actProceso.setNuevoEnModificacion("NUEVO");
            }
            
            if(listaActividadesModificadas != null && !listaActividadesModificadas.isEmpty()){
            	if(obtenerActividadModificada(actProceso)){
                	actProceso.setNuevoEnModificacion("MODIFICADO");
                }            
            }
            
        }
        actividadProceso = new ActividadProcesoPma();        
        
    }
    //metodo para obtener historicos
    
    private void obtenerHistoricoActividades(List<ActividadProcesoPma> listaActividadesActual, List<ActividadProcesoPma> listaActividadesHistorico){    	
		listaActividadesEliminadas = new ArrayList<ActividadProcesoPma>();
		listaActividadesModificadas = new ArrayList<ActividadProcesoPma>();
		for(ActividadProcesoPma actividadHistorico : listaActividadesHistorico){
			Comparator<ActividadProcesoPma> c = new Comparator<ActividadProcesoPma>() {
				
				@Override
				public int compare(ActividadProcesoPma o1, ActividadProcesoPma o2) {							
					return o1.getId().compareTo(o2.getId());
				}
			};			
			
			Collections.sort(listaActividadesActual, c);
			
			int index = Collections.binarySearch(listaActividadesActual, new ActividadProcesoPma(actividadHistorico.getIdRegistroOriginal()), c);
			
			if(index >= 0){
				listaActividadesModificadas.add(actividadHistorico);									
			}else{
				listaActividadesEliminadas.add(actividadHistorico);
			}
		}    	
    }
    
    public boolean obtenerActividadModificada(ActividadProcesoPma actividadModificada){   
    	boolean existeModificacion = false;
    	for(ActividadProcesoPma actividad : listaActividadesModificadas){
    		if(actividad.getIdRegistroOriginal() != null){
    			if(actividad.getIdRegistroOriginal().equals(actividadModificada.getId())){
    				existeModificacion = true;
    				break;
    			}
    		}
    	}
    	
    	return existeModificacion;    	
    }
    
    @Getter
    @Setter
    private List<ActividadProcesoPma> listaActividadModificada;
    public void obtenerHistoricoActividad(ActividadProcesoPma actividadModificada){   
    	listaActividadModificada = new ArrayList<ActividadProcesoPma>();
    	
    	for(ActividadProcesoPma actividadMod : listaActividadesModificadas){
    		if(actividadModificada.getId().equals(actividadMod.getIdRegistroOriginal())){
    			listaActividadModificada.add(actividadMod);
    		}
    	}
    	RequestContext.getCurrentInstance().update("tbl_actividadesModificadas");
		RequestContext.getCurrentInstance().execute("PF('dlgActividadesModificadas').show()");
    }

    private void cargarHerramientasProceso() {
        //Se cargan las herramientas del proceso ya guardadas
        if (fichaAmbiental.getId() != null) {
            herramientasProceso = fichaAmbientalPmaFacade.getHerramientasProcesosFichaPorIdFicha(fichaAmbiental.getId());
            
            List<HerramientaProcesoPma> listaHerramientaHistorico = fichaAmbientalPmaFacade.getHerramientasProcesosFichaHistoricoPorIdFicha(fichaAmbiental.getId());
            if(listaHerramientaHistorico != null && !listaHerramientaHistorico.isEmpty())
            	obtenerHistoricoHerramientas(herramientasProceso, listaHerramientaHistorico);
        }
        
        
        //Se eliminan del catálogo las herramietnas ya registradas
        for (HerramientaProcesoPma herrProceso : herramientasProceso) {
            if (!herrProceso.getHerramienta().getNombreHerramienta().equals("Otras"))
                herramientasCatalogo.remove(herrProceso.getHerramienta());
            
            if(herrProceso.getFechaHistorico() != null){
            	herrProceso.setNuevoEnModificacion("NUEVO");
            }
            
            if(listaHerramientasModificadas != null && !listaHerramientasModificadas.isEmpty()){
            	 if(obtenerHerramientaModificada(herrProceso)){
                 	herrProceso.setNuevoEnModificacion("MODIFICADO");
                 } 
            }           
        }
        herramientaProceso = new HerramientaProcesoPma();
    }
    
    /**
     * CF: inicio historico herramientas
     */
    
    @Getter
    @Setter
    private List<HerramientaProcesoPma> listaHerramientasHistorico;
    private void obtenerHistoricoHerramientas(List<HerramientaProcesoPma> listaHerramientasActual, List<HerramientaProcesoPma> listaHerramientasHistorico){    	
		listaHerramientasEliminadas = new ArrayList<HerramientaProcesoPma>();
		listaHerramientasModificadas = new ArrayList<HerramientaProcesoPma>();
		for(HerramientaProcesoPma herramientaHistorico : listaHerramientasHistorico){
			Comparator<HerramientaProcesoPma> c = new Comparator<HerramientaProcesoPma>() {
				
				@Override
				public int compare(HerramientaProcesoPma o1, HerramientaProcesoPma o2) {							
					return o1.getId().compareTo(o2.getId());
				}
			};			
			
			Collections.sort(listaHerramientasActual, c);
			
			int index = Collections.binarySearch(listaHerramientasActual, new HerramientaProcesoPma(herramientaHistorico.getIdRegistroOriginal()), c);
			
			if(index >= 0){
				listaHerramientasModificadas.add(herramientaHistorico);									
			}else{
				listaHerramientasEliminadas.add(herramientaHistorico);
			}
		}    	
    }
    
    public boolean obtenerHerramientaModificada(HerramientaProcesoPma herramientaModificada){   
    	boolean existeModificacion = false;
    	for(HerramientaProcesoPma herramienta : listaHerramientasModificadas){
    		if(herramienta.getIdRegistroOriginal() != null){
    			if(herramienta.getIdRegistroOriginal().equals(herramientaModificada.getId())){
    				existeModificacion = true;
    				break;
    			}
    		}
    	}
    	
    	return existeModificacion;    	
    }
    
    public void obtenerListaModificacion(){    	
		listaHerramientasHistorico = listaHerramientasEliminadas;
		RequestContext.getCurrentInstance().update("tbl_equiposHistorico");
		RequestContext.getCurrentInstance().execute("PF('dlgHerramientas').show()");   	
    }
    
    public void obtenerHistoricoHerramienta(HerramientaProcesoPma herramientaModificada){   
    	
    	listaHerramientasHistorico = new ArrayList<HerramientaProcesoPma>();
    	for(HerramientaProcesoPma herramientaMod : listaHerramientasModificadas){
    		if(herramientaModificada.getId().equals(herramientaMod.getIdRegistroOriginal())){
    			listaHerramientasHistorico.add(herramientaMod);
    		}
    	}
    	RequestContext.getCurrentInstance().update("tbl_equiposHistorico");
		RequestContext.getCurrentInstance().execute("PF('dlgHerramientas').show()");  
    }
    
    /**
     * Fin historico
     */

    private void cargarInsumosProceso() {
        //Se cargan los insumos del proceso ya guardados
        if (fichaAmbiental.getId() != null) {
            insumosProceso = fichaAmbientalPmaFacade.getInsumosProcesosFichaPorIdFicha(fichaAmbiental.getId());
            
            listaInsumosModificados = fichaAmbientalPmaFacade.getInsumosProcesosFichaHistoricoPorIdFicha(fichaAmbiental.getId());
            if(listaInsumosModificados != null && !listaInsumosModificados.isEmpty())
            	obtenerHistoricoInsumos(insumosProceso, listaInsumosModificados);
        }
        //Se eliminan del catálogo los insumos ya registrados
        for (InsumoProcesoPma insProceso : insumosProceso) {
            if (!insProceso.getCatalogoInsumo().getNombreInsumo().equals("Otros"))
                insumosCatalogo.remove(insProceso.getCatalogoInsumo());
            
            if(insProceso.getFechaHistorico() != null){
            	insProceso.setNuevoEnModificacion("NUEVO");
            }
            
            if(listaInsumosModificados != null && !listaInsumosModificados.isEmpty()){
            	 if(obtenerInsumosModificados(insProceso)){
                 	insProceso.setNuevoEnModificacion("MODIFICADO");
                 } 
            }           
        }
        insumoProceso = new InsumoProcesoPma();
    }
    
    /**
     * Cris F: Historico Insumos
     */    
    @Getter
    @Setter
    private List<InsumoProcesoPma> listaInsumosHistorico;
    
    private void obtenerHistoricoInsumos(List<InsumoProcesoPma> listaInsumosActual, List<InsumoProcesoPma> listaInsumosHistorico){    	
		listaInsumosEliminados = new ArrayList<InsumoProcesoPma>();
		listaInsumosModificados = new ArrayList<InsumoProcesoPma>();
		for(InsumoProcesoPma insumoHistorico : listaInsumosHistorico){
			Comparator<InsumoProcesoPma> c = new Comparator<InsumoProcesoPma>() {
				
				@Override
				public int compare(InsumoProcesoPma o1, InsumoProcesoPma o2) {							
					return o1.getId().compareTo(o2.getId());
				}
			};			
			
			Collections.sort(listaInsumosActual, c);
			
			int index = Collections.binarySearch(listaInsumosActual, new InsumoProcesoPma(insumoHistorico.getIdRegistroOriginal()), c);
			
			if(index >= 0){
				listaInsumosModificados.add(insumoHistorico);									
			}else{
				listaInsumosEliminados.add(insumoHistorico);
			}
		}    	
    }

    public boolean obtenerInsumosModificados(InsumoProcesoPma insumoModificado){   
    	boolean existeModificacion = false;
    	for(InsumoProcesoPma insumo : listaInsumosModificados){
    		if(insumo.getIdRegistroOriginal() != null){
    			if(insumo.getIdRegistroOriginal().equals(insumoModificado.getId())){
    				existeModificacion = true;
    				break;
    			}
    		}
    	}
    	
    	return existeModificacion;    	
    }
    
    public void obtenerListaInsumosModificacion(){    	
    	listaInsumosHistorico = listaInsumosEliminados;
		RequestContext.getCurrentInstance().update("tbl_insumos_proceso_historico");
		RequestContext.getCurrentInstance().execute("PF('dlgInsumos').show()");   	
    }
    
    public void obtenerHistoricoInsumo(InsumoProcesoPma insumoModificado){   
    	
    	listaInsumosHistorico = new ArrayList<InsumoProcesoPma>();
    	for(InsumoProcesoPma insumoMod : listaInsumosModificados){
    		if(insumoModificado.getId().equals(insumoMod.getIdRegistroOriginal())){
    			listaInsumosHistorico.add(insumoMod);
    		}
    	}
    	RequestContext.getCurrentInstance().update("tbl_insumos_proceso_historico");
		RequestContext.getCurrentInstance().execute("PF('dlgInsumos').show()");  
    }
    
    /**
     * Fin Historico Insumos
     */
    

    private void cargarInstalacionesProceso() {
        //Se cargan las instalaciones del proceso ya guardadas
        List<Instalacion> instalacionesProceso = fichaAmbientalPmaFacade.getInstalacionesProcesosFichaPorIdFicha(fichaAmbiental.getId());
        
        listaInstalacionHistorico = new ArrayList<Instalacion>();
        if(fichaAmbiental.getId() != null){
        	listaInstalacionHistorico = fichaAmbientalPmaFacade.getInstalacionesProcesosFichaHistoricoPorIdFicha(fichaAmbiental.getId());
        }

        for (Instalacion instalacionProceso : instalacionesProceso) {
            int indexCatalog = instalacionesCatalogo.indexOf(instalacionProceso.getCatalogoInstalacion());
            if (indexCatalog != -1) {
                instalacionesCatalogo.get(indexCatalog).setSeleccionado(true);
                
                if(instalacionProceso.getFechaHistorico() != null)
                	instalacionesCatalogo.get(indexCatalog).setNuevo(true);
            }            
        }
    }    

    private void cargarTecnicasProceso() {
        //Se cargan las tecnicas del proceso ya guardadas
        List<TecnicaProcesoPma> tecnicasProceso = fichaAmbientalPmaFacade.getTecnicasProcesosPorIdFicha(fichaAmbiental.getId());
        
        listaTecnicasProcesoHistorico = new ArrayList<TecnicaProcesoPma>();
        if(fichaAmbiental.getId() != null){
        	listaTecnicasProcesoHistorico = fichaAmbientalPmaFacade.getTecnicasProcesosPorIdFichaHistorico(fichaAmbiental.getId());
        }

        for (TecnicaProcesoPma tecnicaProceso : tecnicasProceso) {
            int indexCatalog = tecnicasCatalogo.indexOf(tecnicaProceso.getCatalogoTecnica());
            if (indexCatalog != -1) {
                tecnicasCatalogo.get(indexCatalog).setSeleccionado(true);
                
                if(tecnicaProceso.getFechaHistorico() != null){
                	tecnicasCatalogo.get(indexCatalog).setNuevo(true);
                }
            }
        }
    }
    
    private void cargarPlaguicidasProceso() {
        //Se cargan los plaguicidas del proceso ya guardados
        if (fichaAmbiental.getId() != null) {
            plaguicidasProceso = fichaAmbientalPmaFacade.getPlagicidasProcesosFichaPorIdFicha(fichaAmbiental.getId());
            
            List<PlaguicidaProcesoPma> listaPlaguicidaProcesoHistorico = fichaAmbientalPmaFacade.getPlagicidasProcesosFichaHistoricoPorIdFicha(fichaAmbiental.getId());
            if(listaPlaguicidaProcesoHistorico != null && !listaPlaguicidaProcesoHistorico.isEmpty()){
            	obtenerHistoricoPlaguicidas(plaguicidasProceso, listaPlaguicidaProcesoHistorico);
            }
            
            for(PlaguicidaProcesoPma plaguicida : plaguicidasProceso){
            	if(plaguicida.getFechaHistorico() != null){
            		plaguicida.setNuevoEnModificacion("NUEVO");
                }
                
                if(listaPlaguicidasModificados != null && !listaPlaguicidasModificados.isEmpty()){
                	 if(obtenerPlaguicidasModificados(plaguicida)){
                		 plaguicida.setNuevoEnModificacion("MODIFICADO");
                     } 
                }     
            }
            
        }
        plaguicidaProceso = new PlaguicidaProcesoPma();
    }
    
    /**
     * Cris F: historico plaguicidas
     */
    private void obtenerHistoricoPlaguicidas(List<PlaguicidaProcesoPma> listaPlaguicidasActual, List<PlaguicidaProcesoPma> listaPlaguicidaHistorico){    	
		listaPlaguicidasEliminados = new ArrayList<PlaguicidaProcesoPma>();
		listaPlaguicidasModificados = new ArrayList<PlaguicidaProcesoPma>();
		for(PlaguicidaProcesoPma plaguicidaHistorico : listaPlaguicidaHistorico){
			Comparator<PlaguicidaProcesoPma> c = new Comparator<PlaguicidaProcesoPma>() {
				
				@Override
				public int compare(PlaguicidaProcesoPma o1, PlaguicidaProcesoPma o2) {							
					return o1.getId().compareTo(o2.getId());
				}
			};			
			
			Collections.sort(listaPlaguicidasActual, c);
			
			int index = Collections.binarySearch(listaPlaguicidasActual, new PlaguicidaProcesoPma(plaguicidaHistorico.getIdRegistroOriginal()), c);
			
			if(index >= 0){
				listaPlaguicidasModificados.add(plaguicidaHistorico);									
			}else{
				listaPlaguicidasEliminados.add(plaguicidaHistorico);
			}
		}    	
    }

    public boolean obtenerPlaguicidasModificados(PlaguicidaProcesoPma plaguicidaModificado){   
    	boolean existeModificacion = false;
    	for(PlaguicidaProcesoPma plaguicida : listaPlaguicidasModificados){
    		if(plaguicida.getIdRegistroOriginal() != null){
    			if(plaguicida.getIdRegistroOriginal().equals(plaguicidaModificado.getId())){
    				existeModificacion = true;
    				break;
    			}
    		}
    	}
    	
    	return existeModificacion;    	
    }
    
    public void obtenerListaPlaguicidasModificacion(){    	
    	listaPlaguicidasHistorico = listaPlaguicidasEliminados;
		RequestContext.getCurrentInstance().update("tbl_plaguicidas_historico");
		RequestContext.getCurrentInstance().execute("PF('dlgPlaguicidas').show()");   	
    }
    
    public void obtenerHistoricoPlaguicida(PlaguicidaProcesoPma plaguicidaModificado){   
    	
    	listaPlaguicidasHistorico = new ArrayList<PlaguicidaProcesoPma>();
    	for(PlaguicidaProcesoPma plaguicidaMod : listaPlaguicidasModificados){
    		if(plaguicidaModificado.getId().equals(plaguicidaMod.getIdRegistroOriginal())){
    			listaPlaguicidasHistorico.add(plaguicidaMod);
    		}
    	}
    	RequestContext.getCurrentInstance().update("tbl_plaguicidas_historico");
		RequestContext.getCurrentInstance().execute("PF('dlgPlaguicidas').show()");  
    }
    
    
    /**
     * Fin historico
     */
    

    private void cargarFertilizantesProceso() {
        //Se carga el fertilizante del proceso ya guardado
        if (fichaAmbiental.getId() != null) {
            fertilizantesProceso = fichaAmbientalPmaFacade.getFertilizantesProcesoPorIdFicha(fichaAmbiental.getId());
            
            List<FertilizanteProcesoPma> listaFertilizanteHistorico = fichaAmbientalPmaFacade.getFertilizantesProcesoHistoricoPorIdFicha(fichaAmbiental.getId());
            
            if(listaFertilizanteHistorico != null && !listaFertilizanteHistorico.isEmpty()){
            	obtenerHistoricoFertilizantes(fertilizantesProceso, listaFertilizanteHistorico);
            }
            
            for(FertilizanteProcesoPma fertilizante : fertilizantesProceso){
            	if(fertilizante.getFechaHistorico() != null){
            		fertilizante.setNuevoEnModificacion("NUEVO");
            	}
            	
            	if(listaFertilizantesModificados != null && !listaFertilizantesModificados.isEmpty()){
            		if(obtenerFertilizantesModificados(fertilizante)){
            			fertilizante.setNuevoEnModificacion("MODIFICADO");
            		}
            	}            
            }            
        }
        fertilizanteProceso = new FertilizanteProcesoPma();
    }
    
    /**
     * Cris F: historico Fertilizantes
     */
    private void obtenerHistoricoFertilizantes(List<FertilizanteProcesoPma> listaFertilizantesActual, List<FertilizanteProcesoPma> listaFertilizantesHistorico){    	
		listaFertilizantesEliminados = new ArrayList<FertilizanteProcesoPma>();
		listaFertilizantesModificados = new ArrayList<FertilizanteProcesoPma>();
		for(FertilizanteProcesoPma fertilizanteHistorico : listaFertilizantesHistorico){
			Comparator<FertilizanteProcesoPma> c = new Comparator<FertilizanteProcesoPma>() {
				
				@Override
				public int compare(FertilizanteProcesoPma o1, FertilizanteProcesoPma o2) {							
					return o1.getId().compareTo(o2.getId());
				}
			};			
			
			Collections.sort(listaFertilizantesActual, c);
			
			int index = Collections.binarySearch(listaFertilizantesActual, new FertilizanteProcesoPma(fertilizanteHistorico.getIdRegistroOriginal()), c);
			
			if(index >= 0){
				listaFertilizantesModificados.add(fertilizanteHistorico);									
			}else{
				listaFertilizantesEliminados.add(fertilizanteHistorico);
			}
		}    	
    }
    
    public boolean obtenerFertilizantesModificados(FertilizanteProcesoPma fertilizanteModificado){
    	boolean existeModificacion = false;
    	for(FertilizanteProcesoPma fertilizante : listaFertilizantesModificados){
    		if(fertilizante.getIdRegistroOriginal() != null){
    			if(fertilizante.getIdRegistroOriginal().equals(fertilizanteModificado.getId())){
    				existeModificacion = true;
    				break;
    			}
    		}
    	}
    	return existeModificacion;
    }   
    
    
    public void obtenerListaFertilizanteModificacion(){    	
    	listaFertilizantesHistorico = listaFertilizantesEliminados;
    	RequestContext.getCurrentInstance().update("tbl_fertilizantes_historico");
		RequestContext.getCurrentInstance().execute("PF('dlgFertilizantes').show()");    	
    }
    
    public void obtenerHistoricoFertilizante(FertilizanteProcesoPma fertilizanteModificado){   
    	
    	listaFertilizantesHistorico = new ArrayList<FertilizanteProcesoPma>();
    	for(FertilizanteProcesoPma fertilizanteMod : listaFertilizantesModificados){
    		if(fertilizanteModificado.getId().equals(fertilizanteMod.getIdRegistroOriginal())){
    			listaFertilizantesHistorico.add(fertilizanteMod);
    		}
    	}
    	RequestContext.getCurrentInstance().update("tbl_fertilizantes_historico");
		RequestContext.getCurrentInstance().execute("PF('dlgFertilizantes').show()");  
    }    
    
    /**
     * Fin historico fertilizantes
     */
    

    private void cargarDesechoSanitarioProceso() {
        //Se carga el desecho sanitario del proceso ya guardado
        if (fichaAmbiental.getId() != null) {
            desechoSanitarioProceso = fichaAmbientalPmaFacade.getDesechoSanitarioProcesoPorIdFicha(fichaAmbiental.getId());
            
            //Cris F: Historico de desechos Sanitarios
            listaDesechosSanitariosHistorico = fichaAmbientalPmaFacade.getDesechoSanitarioProcesoHistoricoPorIdFicha(fichaAmbiental.getId());
            
            if(listaDesechosSanitariosHistorico  == null || listaDesechosSanitariosHistorico.isEmpty()){
            	listaDesechosSanitariosHistorico = new ArrayList<DesechoSanitarioProcesoPma>();
            }            
            //Fin de Historico
        }
        if (desechoSanitarioProceso.getId() != null) {
            incluyeDesechosSanitarios = true;
        }
    }
    

    private void cargarDesechosPeligrososProceso(Boolean esExpost) {
        //Se cargan los desechos peligrosos del proceso ya guardados
        if (fichaAmbiental.getId() != null) {
            desechosPeligrosoProceso = fichaAmbientalPmaFacade.getDesechosPeligrososProcesosPorIdFicha(fichaAmbiental.getId(), esExpost);
            
            List<DesechoPeligrosoProcesoPma> listaDesechosPeligrososProcesoHistorico = 
            		fichaAmbientalPmaFacade.getDesechosPeligrososProcesosHistoricoPorIdFicha(fichaAmbiental.getId(), esExpost);
            
            if(listaDesechosPeligrososProcesoHistorico != null && !listaDesechosPeligrososProcesoHistorico.isEmpty()){
            	obtenerHistoricoDesechosPeligrosos(desechosPeligrosoProceso, listaDesechosPeligrososProcesoHistorico);
            }
            
            for(DesechoPeligrosoProcesoPma desecho : desechosPeligrosoProceso){
            	if(desecho.getFechaHistorico() != null){
            		desecho.setNuevoEnModificacion("NUEVO");
            	}
            	
            	if(listaDesechosPeligrososModificados != null && !listaDesechosPeligrososModificados.isEmpty()){
            		if(obtenerDesechosPeligrososModificados(desecho)){
            			desecho.setNuevoEnModificacion("MODIFICADO");
            		}
            	}
            }            
        }
        //Se agregan a la lista de desechos los ya cargados para que no se puedan agregar nuevamente
        for (DesechoPeligrosoProcesoPma desecho : desechosPeligrosoProceso) {
            desechoPeligrosoProcesoBean.getDesechosSeleccionados().add(desecho.getDesechoPeligroso());
        }
        desechoPeligrosoProceso = new DesechoPeligrosoProcesoPma();
    }
    
    /**
     * Cris F: historico Desechos Peligrosos
     */
    private void obtenerHistoricoDesechosPeligrosos(List<DesechoPeligrosoProcesoPma> listaDesechosPeligrososActual, List<DesechoPeligrosoProcesoPma> listaDesechoPeligrosoHistorico){    	
		listaDesechosPeligrososModificados = new ArrayList<DesechoPeligrosoProcesoPma>();
		listaDesechosPeligrososEliminados = new ArrayList<DesechoPeligrosoProcesoPma>();
		for(DesechoPeligrosoProcesoPma desechoHistorico : listaDesechoPeligrosoHistorico){
			Comparator<DesechoPeligrosoProcesoPma> c = new Comparator<DesechoPeligrosoProcesoPma>() {
				
				@Override
				public int compare(DesechoPeligrosoProcesoPma o1, DesechoPeligrosoProcesoPma o2) {							
					return o1.getId().compareTo(o2.getId());
				}
			};			
			
			Collections.sort(listaDesechosPeligrososActual, c);
			
			int index = Collections.binarySearch(listaDesechosPeligrososActual, new DesechoPeligrosoProcesoPma(desechoHistorico.getIdRegistroOriginal()), c);
			
			if(index >= 0){
				listaDesechosPeligrososModificados.add(desechoHistorico);									
			}else{
				listaDesechosPeligrososEliminados.add(desechoHistorico);
			}
		}    	
    }
    
    public boolean obtenerDesechosPeligrososModificados(DesechoPeligrosoProcesoPma desechoModificado){
    	boolean existeModificacion = false;
    	for(DesechoPeligrosoProcesoPma desecho : listaDesechosPeligrososModificados){
    		if(desecho.getIdRegistroOriginal() != null){
    			if(desecho.getIdRegistroOriginal().equals(desechoModificado.getId())){
    				existeModificacion = true;
    				break;
    			}
    		}
    	}
    	return existeModificacion;
    }   
    
    
    public void obtenerListaDesechosPeligrososModificacion(){    	
    	listaDesechosPeligrososHistorico = listaDesechosPeligrososEliminados;
    	RequestContext.getCurrentInstance().update("tbl_desechos_peligrosos_historico");
		RequestContext.getCurrentInstance().execute("PF('dlgDesechosPeligrososExPost').show()");    	
    }
    
    public void obtenerHistoricoDesechosPeligrosos(DesechoPeligrosoProcesoPma desechoModificado){   
    	
    	listaDesechosPeligrososHistorico = new ArrayList<DesechoPeligrosoProcesoPma>();
    	for(DesechoPeligrosoProcesoPma desechoMod : listaDesechosPeligrososModificados){
    		if(desechoModificado.getId().equals(desechoMod.getIdRegistroOriginal())){
    			listaDesechosPeligrososHistorico.add(desechoMod);
    		}
    	}
    	RequestContext.getCurrentInstance().update("tbl_desechos_peligrosos_historico");
		RequestContext.getCurrentInstance().execute("PF('dlgDesechosPeligrososExPost').show()");
    }    
    
    
    //Exante
    public void obtenerHistoricoDesechosPeligrososExAnte(DesechoPeligrosoProcesoPma desechoModificado){   
    	
    	listaDesechosPeligrososHistorico = new ArrayList<DesechoPeligrosoProcesoPma>();
    	for(DesechoPeligrosoProcesoPma desechoMod : listaDesechosPeligrososModificados){
    		if(desechoModificado.getId().equals(desechoMod.getIdRegistroOriginal())){
    			listaDesechosPeligrososHistorico.add(desechoMod);
    		}
    	}
    	RequestContext.getCurrentInstance().update("tbl_desechos_peligrosos_historico_exAnte");
		RequestContext.getCurrentInstance().execute("PF('dlgDesechosPeligrososExAnte').show()");
    }    
    
    public void obtenerListaDesechosPeligrososModificacionExAnte(){    	
    	listaDesechosPeligrososHistorico = listaDesechosPeligrososEliminados;
    	RequestContext.getCurrentInstance().update("tbl_desechos_peligrosos_historico_ExAnte");
		RequestContext.getCurrentInstance().execute("PF('dlgDesechosPeligrososExAnte').show()");    	
    }
    /**
     * Fin historico Desechos Peligrosos
     */
    

    private void cargarVehiculosDesechoSanitarioProceso() {
        // Se cargan los veiculos del proceso ya guardados
        if (fichaAmbiental.getId() != null) {
            vehiculosDesechoSanitarioProceso = fichaAmbientalPmaFacade.getVehiculosProcesosPorIdFicha(fichaAmbiental.getId());
            
            //Cris F:  aumento para historico
            List<VehiculoDesechoSanitarioProcesoPma> listaHistoricoVehiculos = fichaAmbientalPmaFacade.getVehiculosProcesosHistoricoPorIdFicha(fichaAmbiental.getId());
            if(listaHistoricoVehiculos != null && !listaHistoricoVehiculos.isEmpty()){
            	obtenerHistoricoVehiculos(vehiculosDesechoSanitarioProceso, listaHistoricoVehiculos);
            }
            
            for(VehiculoDesechoSanitarioProcesoPma vehiculo : vehiculosDesechoSanitarioProceso){
            	if(vehiculo.getFechaHistorico() != null){
            		vehiculo.setNuevoEnModificacion("NUEVO");
            	}
            	
            	if(listaVehiculoDesechoSanitarioModificados != null && !listaVehiculoDesechoSanitarioModificados.isEmpty()){
            		if(obtenerVehiculosModificados(vehiculo)){
            			vehiculo.setNuevoEnModificacion("MODIFICADO");
            		}
            	}
            }
        }
        vehiculoDesechoSanitarioProceso = new VehiculoDesechoSanitarioProcesoPma();
    }
    
    /**
     * Cris F: historico Vehiculos
     */
    private void obtenerHistoricoVehiculos(List<VehiculoDesechoSanitarioProcesoPma> listaVehiculosActual, List<VehiculoDesechoSanitarioProcesoPma> listaVehiculosHistorico){    	
		listaVehiculoDesechoSanitarioModificados = new ArrayList<VehiculoDesechoSanitarioProcesoPma>();
		listaVehiculoDesechoSanitarioEliminados = new ArrayList<VehiculoDesechoSanitarioProcesoPma>();
		for(VehiculoDesechoSanitarioProcesoPma vehiculoHistorico : listaVehiculosHistorico){
			Comparator<VehiculoDesechoSanitarioProcesoPma> c = new Comparator<VehiculoDesechoSanitarioProcesoPma>() {
				
				@Override
				public int compare(VehiculoDesechoSanitarioProcesoPma o1, VehiculoDesechoSanitarioProcesoPma o2) {							
					return o1.getId().compareTo(o2.getId());
				}
			};			
			
			Collections.sort(listaVehiculosActual, c);
			
			int index = Collections.binarySearch(listaVehiculosActual, new VehiculoDesechoSanitarioProcesoPma(vehiculoHistorico.getIdRegistroOriginal()), c);
			
			if(index >= 0){
				listaVehiculoDesechoSanitarioModificados.add(vehiculoHistorico);									
			}else{
				listaVehiculoDesechoSanitarioEliminados.add(vehiculoHistorico);
			}
		}    	
    }
    
    public boolean obtenerVehiculosModificados(VehiculoDesechoSanitarioProcesoPma vehiculoModificado){
    	boolean existeModificacion = false;
    	for(VehiculoDesechoSanitarioProcesoPma vehiculo : listaVehiculoDesechoSanitarioModificados){
    		if(vehiculo.getIdRegistroOriginal() != null){
    			if(vehiculo.getIdRegistroOriginal().equals(vehiculoModificado.getId())){
    				existeModificacion = true;
    				break;
    			}
    		}
    	}
    	return existeModificacion;
    }   
    
    
    public void obtenerListaVehiculosModificacion(){    	
    	listaVehiculoDesechoSanitarioHistorico = listaVehiculoDesechoSanitarioEliminados;
    	RequestContext.getCurrentInstance().update("tbl_transporte_desechos_eliminados");
		RequestContext.getCurrentInstance().execute("PF('dlgVehiculosEliminados').show()");    	
    }
    
    
    @Getter
    List<Documento> listaDocumentoImagenHistorico;
    @Getter
    List<Documento> listaDocumentoCertificadoHistorico;
    @Getter
    List<Documento> listaDocumentoMatriculaHistorico;
    public void obtenerHistoricoVehiculos(VehiculoDesechoSanitarioProcesoPma vehiculoModificado){   
    	
    	listaVehiculoDesechoSanitarioHistorico = new ArrayList<VehiculoDesechoSanitarioProcesoPma>();
    	
    	listaDocumentoImagenHistorico = new ArrayList<Documento>();
    	listaDocumentoCertificadoHistorico = new ArrayList<Documento>();
    	listaDocumentoMatriculaHistorico = new ArrayList<Documento>();
    	
    	for(VehiculoDesechoSanitarioProcesoPma vehiculoMod : listaVehiculoDesechoSanitarioModificados){
    		if(vehiculoModificado.getId().equals(vehiculoMod.getIdRegistroOriginal())){
    			listaVehiculoDesechoSanitarioHistorico.add(vehiculoMod);
    		}
    	}   	
    	
    	for(VehiculoDesechoSanitarioProcesoPma vehiculoDoc : listaVehiculoDesechoSanitarioHistorico){    		
    		if(!vehiculoModificado.getDocumentoImagenVehículo().getId().equals(vehiculoDoc.getDocumentoImagenVehículo().getId())){
    			vehiculoDoc.getDocumentoImagenVehículo().setFechaModificacion(vehiculoDoc.getFechaHistorico());
    			listaDocumentoImagenHistorico.add(vehiculoDoc.getDocumentoImagenVehículo());
    		}
    		
    		if(!vehiculoModificado.getDocumentoMatricula().getId().equals(vehiculoDoc.getDocumentoMatricula().getId())){
    			vehiculoDoc.getDocumentoMatricula().setFechaModificacion(vehiculoDoc.getFechaHistorico());
    			listaDocumentoMatriculaHistorico.add(vehiculoDoc.getDocumentoMatricula());
    		}
    		
    		if(!vehiculoModificado.getDocumentoCertificadoInspeccion().getId().equals(vehiculoDoc.getDocumentoCertificadoInspeccion().getId())){
    			vehiculoDoc.getDocumentoCertificadoInspeccion().setFechaModificacion(vehiculoDoc.getFechaHistorico());    		
        		listaDocumentoCertificadoHistorico.add(vehiculoDoc.getDocumentoCertificadoInspeccion());
    		}
    	}
    	
    	
    	
    	RequestContext.getCurrentInstance().update("dlgVehiculoHistorico");
    	RequestContext.getCurrentInstance().update("tbl_vehiculos_historial");
    	RequestContext.getCurrentInstance().update("tbl_documentos_historial");
    	RequestContext.getCurrentInstance().update("tbl_documentos_matricula_historial");
    	RequestContext.getCurrentInstance().update("tbl_documentos_certificado_historial");
		RequestContext.getCurrentInstance().execute("PF('dlgVehiculoHistorico').show()");    	  
    }    
    
    
    public StreamedContent getStreamContentDocumentoOriginal(Documento documentoOriginal) throws Exception {
		DefaultStreamedContent content = null;
		try {
			documentoOriginal = this.descargarAlfresco(documentoOriginal);
			if (documentoOriginal != null && documentoOriginal.getNombre() != null && documentoOriginal.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoOriginal.getContenidoDocumento()));
				content.setName(documentoOriginal.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		} catch (Exception exception) {
			exception.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}
    
    public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}
    
    /**
     * Fin historico Vehiculos
     */
    
    

    // Actividades del proceso...

    public void guardarActividadProceso() {
        if (!actividadProceso.getActividadComercial().getNombreActividad().equals("Otros")) { //Si no es "Otros"
            todasActividadesComerciales.remove(actividadProceso.getActividadComercial()); //Se elimina la actvidad del catálogo
        } else {
            actividadProceso.setDescripcionFaseOtros(faseSeleccionada.getNombre());
        }

        if (!actividadesProceso.contains(actividadProceso)) {
            actividadProceso.setFichaAmbientalPma(fichaAmbiental); //Se especifica la ficha
            actividadesProceso.add(actividadProceso); //Se agrega la actividad
        }
        buttonMode = "Adicionar";
        actividadProceso = new ActividadProcesoPma();
    }

    public void editarActividadProceso(ActividadProcesoPma _actividadProceso) {
        if (_actividadProceso.getActividadComercial().getCategoriaFase() != null) { //Si no es Otros
            if (!todasActividadesComerciales.contains(_actividadProceso.getActividadComercial())) { //Si no está en la lista
                short index = posicionInsertar(todasActividadesComerciales, _actividadProceso.getActividadComercial());
                todasActividadesComerciales.add(index, _actividadProceso.getActividadComercial());
            }
            faseSeleccionada = _actividadProceso.getActividadComercial().getCategoriaFase().getFase();
        } else { //Para otros
            for (Fase faseOtros : fases) {
                if (faseOtros.getNombre().equals(_actividadProceso.getDescripcionFaseOtros())) {
                    faseSeleccionada = faseOtros;
                    break;
                }
            }
        }
        buttonMode = "Guardar";
        actividadProceso = _actividadProceso;
    }

    public void eliminarActividadProceso(ActividadProcesoPma _actividadProceso) {
        if (_actividadProceso.getActividadComercial() != null) { //Si no es Otros
            short index = posicionInsertar(todasActividadesComerciales, _actividadProceso.getActividadComercial());
            todasActividadesComerciales.add(index, _actividadProceso.getActividadComercial()); //Adiciono al catalogo
        }
        actividadesProceso.remove(_actividadProceso); //Elimino de la lista de actividades
    }

    private short posicionInsertar(List<CatalogoActividadComercial> lista, CatalogoActividadComercial paraInsertar) {
        short position = 0;
        for (CatalogoActividadComercial actividad : lista) {
            if (actividad.getNombreActividad().startsWith(paraInsertar.getNombreActividad().subSequence(0, 1).toString())) {
                return position;
            }
            position++;
        }
        return 0;
    }

    // Herramientas del proceso...

    public void guardarHerramientaProceso() {
        if (!herramientaProceso.getHerramienta().getNombreHerramienta().equals("Otras")) { //Si no es "Otras"
            herramientasCatalogo.remove(herramientaProceso.getHerramienta()); //Se elimina la herramienta del catálogo
        }
        if (!herramientasProceso.contains(herramientaProceso)) {
            herramientaProceso.setFichaAmbientalPma(fichaAmbiental); //Se especifica la ficha
            herramientasProceso.add(herramientaProceso); //Se agrega la herramienta
        }
        buttonMode = "Adicionar";
        herramientaProceso = new HerramientaProcesoPma();
    }

    public void editarHerramientaProceso(HerramientaProcesoPma _herramientaProceso) {
        if (!herramientasCatalogo.contains(_herramientaProceso.getHerramienta())) { //Si no está en la lista
            short index = posicionInsertar(herramientasCatalogo, _herramientaProceso.getHerramienta());
            herramientasCatalogo.add(index, _herramientaProceso.getHerramienta()); //Adiciono al catalogo
        }
        buttonMode = "Guardar";
        herramientaProceso = _herramientaProceso;
    }

    public void eliminarHerramientaProceso(HerramientaProcesoPma _herramientaProceso) {
        if (!herramientasCatalogo.contains(_herramientaProceso.getHerramienta())) { //Si no está en la lista
            short index = posicionInsertar(herramientasCatalogo, _herramientaProceso.getHerramienta());
            herramientasCatalogo.add(index, _herramientaProceso.getHerramienta()); //Adiciono al catalogo
        }
        herramientasProceso.remove(_herramientaProceso); //Elimino de la lista de herramientas
    }

    private short posicionInsertar(List<CatalogoHerramienta> lista, CatalogoHerramienta paraInsertar) {
        short position = 0;

        for (CatalogoHerramienta herramienta : lista) {
            if (herramienta.getNombreHerramienta().startsWith(paraInsertar.getNombreHerramienta().subSequence(0, 1).toString())) {
                return position;
            }
            position++;
        }
        return 0;
    }

    // Materiales e insumos...

    public void guardarInsumoProceso() {
        if (!insumoProceso.getCatalogoInsumo().getNombreInsumo().equals("Otros")) { //Si no es "Otros"
            insumosCatalogo.remove(insumoProceso.getCatalogoInsumo()); // Se elimina el insumo del catálogo
        }
        if (!insumosProceso.contains(insumoProceso)) {
            insumoProceso.setFichaAmbientalPma(fichaAmbiental); // Se especifica la ficha
            insumosProceso.add(insumoProceso); // Se agrega el insumo
        }
        buttonMode = "Adicionar";
        insumoProceso = new InsumoProcesoPma();
    }

    public void editarInsumoProceso(InsumoProcesoPma _insumoProceso) {
        if (!insumosCatalogo.contains(_insumoProceso.getCatalogoInsumo())) { //Si no está en la lista
            short index = posicionInsertar(insumosCatalogo, _insumoProceso.getCatalogoInsumo());
            insumosCatalogo.add(index, _insumoProceso.getCatalogoInsumo()); //Adiciono al catalogo
        }
        buttonMode = "Guardar";
        insumoProceso = _insumoProceso;
    }

    public void eliminarInsumoProceso(InsumoProcesoPma _insumoProceso) {
        if (!insumosCatalogo.contains(_insumoProceso.getCatalogoInsumo())) { //Si no está en la lista
            short index = posicionInsertar(insumosCatalogo, _insumoProceso.getCatalogoInsumo());
            insumosCatalogo.add(index, _insumoProceso.getCatalogoInsumo()); //Adiciono al catalogo
        }
        insumosProceso.remove(_insumoProceso); //Elimino de la lista de insumos
    }

    private short posicionInsertar(List<CatalogoInsumo> lista, CatalogoInsumo paraInsertar) {
        short position = 0;

        for (CatalogoInsumo insumo : lista) {
            if (insumo.getNombreInsumo().startsWith(paraInsertar.getNombreInsumo().subSequence(0, 1).toString())) {
                return position;
            }
            position++;
        }
        return 0;
    }

    // Plaguicidas del proceso...

    public void guardarPlaguicidaProceso() {
        if (!plaguicidasProceso.contains(plaguicidaProceso)) {
            plaguicidaProceso.setFichaAmbientalPma(fichaAmbiental); //Se especifica la ficha
            plaguicidasProceso.add(plaguicidaProceso); // Se agrega el plaguicida en caso de no existir ya.
        }
        buttonMode = "Adicionar";
        plaguicidaProceso = new PlaguicidaProcesoPma();
    }

    public void editarPlaguicidaProceso(PlaguicidaProcesoPma _plaguicidaProceso) {
        buttonMode = "Guardar";
        plaguicidaProceso = _plaguicidaProceso;
    }

    public void eliminarPlaguicidaProceso(PlaguicidaProcesoPma _plaguicidaProceso) {
        plaguicidasProceso.remove(_plaguicidaProceso); //Elimino de la lista de plaguicidas
    }

    //Fertilizantes

    public void guardarFertilizanteProceso() {
        if (!fertilizantesProceso.contains(fertilizanteProceso)) {
            fertilizanteProceso.setFichaAmbientalPma(fichaAmbiental);
            fertilizantesProceso.add(fertilizanteProceso);
        }
        buttonMode = "Adicionar";
        fertilizanteProceso = new FertilizanteProcesoPma();
    }

    public void editarFertilizanteProceso(FertilizanteProcesoPma _fertilizanteProceso) {
        buttonMode = "Guardar";
        fertilizanteProceso = _fertilizanteProceso;
    }

    public void eliminarFertilizanteProceso(FertilizanteProcesoPma _fertilizanteProceso) {
        fertilizantesProceso.remove(_fertilizanteProceso);
    }

    // Vehículos transporte desechos sanitarios...

    public void guardarVehiculoProceso() {
        Boolean requiereAdjunto = false;

        if (vehiculoDesechoSanitarioProceso.getMatricula() == null && vehiculoDesechoSanitarioProceso.getDocumentoMatricula() == null) {
            JsfUtil.addMessageError("Debe adjuntar una copia de la matrícula actualizada.");
            requiereAdjunto = true;
        }

        if (vehiculoDesechoSanitarioProceso.getCertificadoInspeccion() == null && vehiculoDesechoSanitarioProceso.getDocumentoCertificadoInspeccion() == null) {
            JsfUtil.addMessageError("Debe adjuntar una copia del Certificado de Inspección Técnica.");
            requiereAdjunto = true;
        }

        if (vehiculoDesechoSanitarioProceso.getImagenVehiculo() == null && vehiculoDesechoSanitarioProceso.getDocumentoImagenVehículo() == null) {
            JsfUtil.addMessageError("Debe adjuntar una imagen del vehículo.");
            requiereAdjunto = true;
        }

        if (requiereAdjunto) {
            return;
        }
        if (!vehiculosDesechoSanitarioProceso.contains(vehiculoDesechoSanitarioProceso)) {
            vehiculoDesechoSanitarioProceso.setFichaAmbientalPma(fichaAmbiental);
            vehiculosDesechoSanitarioProceso.add(vehiculoDesechoSanitarioProceso);
            vehiculoDesechoSanitarioProceso = new VehiculoDesechoSanitarioProcesoPma();
        }

      
        RequestContext.getCurrentInstance().execute("PF('dlgVehiculo').hide();");
    }

    public void editarVehiculoProceso(VehiculoDesechoSanitarioProcesoPma _vehiculoProceso) {
        vehiculoDesechoSanitarioProceso = _vehiculoProceso;
        RequestContext.getCurrentInstance().execute("PF('dlgVehiculo').show();"); //Abro el dialog
    }

    public void eliminarVehiculoProceso(VehiculoDesechoSanitarioProcesoPma _vehiculoProceso) {
        vehiculosDesechoSanitarioProceso.remove(_vehiculoProceso); //Elimino de la lista
    }

    // Desechos Peligrosos Proceso ...

    public void guardarDesechoPeligrosoProcesoExantes() {
        if (desechoPeligrosoProcesoBean.optenerDesechoSeleccionado().getId() == null) {
            JsfUtil.addMessageError("Debe seleccionar un desecho sanitario.");
            return;
        }
        if (!desechoPeligrosoProcesoBean.optenerDesechoSeleccionado().equals(desechoPeligrosoProceso.getDesechoPeligroso())) {
            desechoPeligrosoProceso.setDesechoPeligroso(desechoPeligrosoProcesoBean.optenerDesechoSeleccionado()); //Se especifica el desecho seleccionado para el caso que no exista o para cuando cambie en la edición
        }

        if (!desechosPeligrosoProceso.contains(desechoPeligrosoProceso)) {
            desechoPeligrosoProceso.setFichaAmbientalPma(fichaAmbiental); //Se especifica la ficha
            desechosPeligrosoProceso.add(desechoPeligrosoProceso); //Se agrega
        }
        buttonMode = "Adicionar";
        desechoPeligrosoProceso = new DesechoPeligrosoProcesoPma();
        desechoPeligrosoProcesoBean.setDesechoSeleccionado(null); //Se quita el desecho seleccionado
    }

    public void guardarDesechoPeligrosoProcesoExpost() {
        if (desechoPeligrosoProcesoBean.optenerDesechoSeleccionado().getId() == null) {
            JsfUtil.addMessageError("Debe seleccionar un desecho sanitario.");
            return;
        }
        if (!desechoPeligrosoProcesoBean.optenerDesechoSeleccionado().equals(desechoPeligrosoProceso.getDesechoPeligroso())) {
            desechoPeligrosoProceso.setDesechoPeligroso(desechoPeligrosoProcesoBean.optenerDesechoSeleccionado()); //Se especifica el desecho seleccionado para el caso que no exista o para cuando cambie en la edición
        }

        if (!desechosPeligrosoProceso.contains(desechoPeligrosoProceso)) {
            desechoPeligrosoProceso.setFichaAmbientalPma(fichaAmbiental); //Se especifica la ficha
            desechosPeligrosoProceso.add(desechoPeligrosoProceso); //Se agrega
        }
        buttonMode = "Adicionar";
        desechoPeligrosoProceso = new DesechoPeligrosoProcesoPma();
        desechoPeligrosoProcesoBean.setDesechoSeleccionado(null); //Se quita el desecho seleccionado
    }

    public void editarDesechoPeligrosoProceso(DesechoPeligrosoProcesoPma _desechoProceso) {
        buttonMode = "Guardar";
        desechoPeligrosoProceso = _desechoProceso;
        //desechoPeligrosoProcesoBean.setDesechoSeleccionado(desechoPeligrosoProceso.getDesechoPeligroso()); //Se coloca el desecho seleccionado
        desechoPeligrosoProcesoBean.setDesechoSeleccionado(_desechoProceso.getDesechoPeligroso()); //Se coloca el desecho seleccionado
       
    }

    public void eliminarDesechoPeligrosoProceso(DesechoPeligrosoProcesoPma _desechoProceso) {
        desechosPeligrosoProceso.remove(_desechoProceso); //Elimino de la lista
        desechoPeligrosoProcesoBean.setDesechoSeleccionado(null); //Se quita el desecho seleccionado
    }

    //File upload

    public void adjuntarImagenVehiculo(FileUploadEvent event) {
        if (event != null) {
            vehiculoDesechoSanitarioProceso.setImagenVehiculo(JsfUtil.upload(event));
            JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " fue adjuntado correctamente.");
        }
    }

    public void adjuntarMatricula(FileUploadEvent event) {
        if (event != null) {
            vehiculoDesechoSanitarioProceso.setMatricula(JsfUtil.upload(event));
            JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " fue adjuntado correctamente.");
        }
    }

    public void adjuntarCertificadoInspeccion(FileUploadEvent event) {
        if (event != null) {
            vehiculoDesechoSanitarioProceso.setCertificadoInspeccion(JsfUtil.upload(event));
            JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " fue adjuntado correctamente.");
        }
    }


    public String conformarNombre(CatalogoInsumo insumo) {
        String cat = insumo.getNombreInsumo();
        if (insumo.getCategoriaFase() != null && insumo.getCategoriaFase().getFase() != null) {
            cat += " -- " + insumo.getCategoriaFase().getFase().getNombre();
        }
        return cat;
    }
    
   
    
}