package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.EspecialistaAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.InterseccionProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.BienesServiciosInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.CoordenadasInventarioForestalCertificadoFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.DocumentoInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.HigherClassificationFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.IndiceValorImportanciaInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalDetalleFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.PromedioInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.RegistroAmbientalSumatoriaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.RegistroEspeciesForestalesFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ShapeInventarioForestalCertificadoFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.SpecieTaxaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.BienesServiciosInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.CoordenadasInventarioForestalCertificado;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.HigherClassification;
import ec.gob.ambiente.rcoa.inventarioForestal.model.IndiceValorImportanciaInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalDetalle;
import ec.gob.ambiente.rcoa.inventarioForestal.model.PromedioInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.RegistroAmbientalSumatoria;
import ec.gob.ambiente.rcoa.inventarioForestal.model.RegistroEspeciesForestales;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ShapeInventarioForestalCertificado;
import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.EspecialistaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.tipoforma.facade.TipoFormaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class InventarioForestalRegistroVisualizarController {
	
	private static final Logger LOG = Logger.getLogger(InventarioForestalRegistroVisualizarController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	private Map<String, Object> variables;
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;
    
	/*EJBs*/
	@EJB
	private CrudServiceBean crudServiceBean;
    
    
    // FACADES GENERALES
    @EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
    @EJB
    private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
    @EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalCoaShapeFacade;
    @EJB
	private CapasCoaFacade capasCoaFacade;
    @EJB
	private InterseccionProyectoLicenciaAmbientalFacade interseccionProyectoLicenciaAmbientalFacade;
    @EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
    @EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
    @EJB
	private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
    @EJB
	private InventarioForestalDetalleFacade inventarioForestalDetalleFacade;
    @EJB
	private HigherClassificationFacade higherClassificationFacade;
    @EJB	
	private SpecieTaxaFacade specieTaxaFacade;
    @EJB
	private TipoFormaFacade tipoFormaFacade;
    @EJB
	private ShapeInventarioForestalCertificadoFacade shapeInventarioForestalCertificadoFacade;
    @EJB
	private CoordenadasInventarioForestalCertificadoFacade coordenadasInventarioForestalCertificadoFacade;
    @EJB
	private RegistroEspeciesForestalesFacade registroEspeciesForestalesFacade;
    @EJB
	private RegistroAmbientalSumatoriaFacade registroAmbientalSumatoriaFacade;
    @EJB
	private PromedioInventarioForestalFacade promedioInventarioForestalFacade;
    @EJB
	private IndiceValorImportanciaInventarioForestalFacade indiceValorImportanciaInventarioForestalFacade;
    @EJB
	private BienesServiciosInventarioForestalFacade bienesServiciosInventarioForestalFacade;
    @EJB
	private DocumentoInventarioForestalFacade documentoInventarioForestalFacade;
    @EJB
    private AsignarTareaFacade asignarTareaFacade;
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private EspecialistaAmbientalCoaFacade especialistaAmbientalCoaFacade;
    @EJB
    private CatalogoCoaFacade catalogoCoaFacade;

    
    /*List*/
    @Setter
	@Getter
	private List<RegistroEspeciesForestales> listRegistroEspeciesForestales = new ArrayList<RegistroEspeciesForestales>();
    @Setter
	@Getter
	private List<RegistroEspeciesForestales> listRegistroEspeciesForestalesEliminar = new ArrayList<RegistroEspeciesForestales>();
    @Setter
	@Getter
	private List<HigherClassification> listFamilia = new ArrayList<HigherClassification>();
	@Setter
	@Getter
	private List<HigherClassification> listGenero = new ArrayList<HigherClassification>();
    @Setter
	@Getter
	private List<SpecieTaxa> listEspecie = new ArrayList<SpecieTaxa>();
    @Setter
	@Getter
	private List<InventarioForestalAmbiental> listInventarioForestalAmbiental = new ArrayList<InventarioForestalAmbiental>();
    @Getter
    @Setter
    private List<CoordendasPoligonosCertificado> coordinatesWrappers = new ArrayList<CoordendasPoligonosCertificado>();
    @Getter
    private List<TipoForma> tiposFormas  = new ArrayList<TipoForma>();
    @Getter
    private List<RegistroAmbientalSumatoria> listRegistroAmbientalSumatoria = new ArrayList<RegistroAmbientalSumatoria>();
    @Getter
    private List<PromedioInventarioForestal> listPromedioInventarioForestal = new ArrayList<PromedioInventarioForestal>();
    @Getter
    private List<IndiceValorImportanciaInventarioForestal> listIndiceValorImportanciaInventarioForestal = new ArrayList<IndiceValorImportanciaInventarioForestal>();
    @Getter
    private List<CatalogoGeneralCoa> listMetodoRecoleccionDatos;
    
    
    /*Object*/
    @Setter
    @Getter
    private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();
    @Setter
    @Getter
    private ProyectoLicenciaCuaCiuu actividadPrincipal = new ProyectoLicenciaCuaCiuu();
    @Setter
    @Getter
    private InventarioForestalAmbiental inventarioForestalAmbiental;
    @Setter
    @Getter
    private InventarioForestalDetalle inventarioForestalDetalle = new InventarioForestalDetalle();
    @Setter
    @Getter
    private RegistroEspeciesForestales registroEspeciesForestales = new RegistroEspeciesForestales();;
    @Setter
    @Getter
    private HigherClassification objFamilia = new HigherClassification();
    @Setter
    @Getter
    private HigherClassification objGenero = new HigherClassification();
    @Setter
    @Getter
    private SpecieTaxa objEspecie = new SpecieTaxa();
    @Setter
    @Getter
    private SpecieTaxa objEspecieOtro = new SpecieTaxa();
    @Getter
    private UploadedFile uploadedFile;
    private TipoForma poligono=new TipoForma();
    @Getter
    @Setter
    private ShapeInventarioForestalCertificado shape = new ShapeInventarioForestalCertificado();
    @Getter
    @Setter
    private CoordendasPoligonosCertificado coor = new CoordendasPoligonosCertificado();
    @Getter
    @Setter
    private BienesServiciosInventarioForestal bienesServiciosInventarioForestal = new BienesServiciosInventarioForestal();
    @Setter
    @Getter
    private EspecialistaAmbiental especialistaAmbiental = new EspecialistaAmbiental();
    @Setter
    @Getter
	private DocumentoInventarioForestal documentoCoordenadas = new DocumentoInventarioForestal();
    @Setter
    @Getter
	private DocumentoInventarioForestal documentoEspecies = new DocumentoInventarioForestal();
	@Setter
    @Getter
	private DocumentoInventarioForestal documentoValoracion = new DocumentoInventarioForestal();
    
    
	/*Boolean*/
	@Getter
    @Setter
    private boolean mostrarValoracion = false;
    @Getter
    private boolean blockFamilia=true, blockGenero=true, blockEspecie=true, blockOtro=true, esRevisarInformacion=false;
    @Getter
    private Integer idRegistroPreliminar;
    @Getter
    @Setter
    private String tramite, tecnicoForestal="";
    @Getter
    @Setter
    private Integer idInventarioForestalRegistro;
    
    
    
    /*CONSTANTES*/
    public static final Double VALOR_AREA_BASAL = 0.7854;
    public static final Double VALOR_VOLUMEN = 0.7;
    public static final Double VALOR_M3_MADERA_PIE = 3.00;
    public static final Integer ID_LAYER_COBERTURA = 12;
    public static final Integer ID_LAYER_ECOSISTEMAS = 13;
    public static final Integer RCOA_COORDENADAS_MUESTRAS = 5200;
    public static final Integer RCOA_LISTA_ESPECIES = 5201;
    public static final Integer RCOA_VALORACION_BIENES = 5202;
    public static final String TAREA_REVISAR_INFORMACION = "Revisar informacion";
    public static final String ELABORAR_INFORME_OFICIO="Elaborar informe y oficio de pronunciamiento";
   
    @PostConstruct
	public void init() {
    	try {
    		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
    		tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
    		tecnicoForestal = (String) variables.get("tecnicoForestal");
    		idRegistroPreliminar = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
    		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idRegistroPreliminar);
    		inventarioForestalAmbiental = new InventarioForestalAmbiental();
    		inventarioForestalAmbiental = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(idRegistroPreliminar);
	    	idInventarioForestalRegistro = inventarioForestalAmbiental.getId();
	    	// Listado de Familias PLANTAE
    		listFamilia = obtenerListaFamilia();
    		tiposFormas = tipoFormaFacade.listarTiposForma();
    		poligono = getTipoForma(TipoForma.TIPO_FORMA_POLIGONO);
    		
    		actividadPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
    		actividadPrincipal.getCatalogoCIUU().getTipoSector();
    		
			if (actividadPrincipal.getCatalogoCIUU().getTipoSector().getId() == 1
					|| actividadPrincipal.getCatalogoCIUU().getTipoSector().getId() == 2
					|| actividadPrincipal.getCatalogoCIUU().getTipoSector().getId() == 4) {
				mostrarValoracion = true;
			} 

    		esRevisarInformacion = verificaTareaRevisarInformacion();
    		cargarinventarioForestalRegistro();
    		listMetodoRecoleccionDatos=catalogoCoaFacade.obtenerCatalogoOrden(CatalogoTipoCoaEnum.IF_METODO_RECOLECCION_DATOS);
    	} catch (Exception e) {
			LOG.error(e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del Inventario Forestal.");
		}
	}
    
    public TipoForma getTipoForma(Integer id) {
        for (TipoForma tf : tiposFormas) {
            if (tf.getId()==id)
                return tf;
        }
        return null;
    }
    
    public List<HigherClassification> obtenerListaFamilia() {
    	List<HigherClassification> familia = new ArrayList<HigherClassification>();
    	try {
			familia = higherClassificationFacade.getFamilia();
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Error en consulta de familia");
            LOG.error(e);
		}
    	return familia;
    }
    
    private boolean verificaTareaRevisarInformacion() {
    	boolean isTareaRevision = false;
    	String tarea = bandejaTareasBean.getTarea().getTaskNameHuman();
		if (tarea.equals(TAREA_REVISAR_INFORMACION) || tarea.equals(ELABORAR_INFORME_OFICIO)) {
			isTareaRevision = true;
		}
    	return isTareaRevision;
    }
    
    public StreamedContent getStreamValoracion() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		content = new DefaultStreamedContent(new ByteArrayInputStream(documentoValoracion.getContenidoDocumento()), documentoValoracion.getMimeDocumento());
		content.setName(documentoValoracion.getNombreDocumento());
		return content;
	}

    public void cargarinventarioForestalRegistro() {
    	try {
    		Integer idRegistroPreliminarProyecto = proyectoLicenciaCoa.getId();
	    	InventarioForestalAmbiental inventarioBase = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(idRegistroPreliminarProyecto);
	    	if (inventarioBase.getId() != null) {
				inventarioForestalAmbiental = inventarioBase;
				idInventarioForestalRegistro = inventarioForestalAmbiental.getId();
				inventarioForestalDetalle = inventarioForestalDetalleFacade.getByInventarioForestalAmbiental(idInventarioForestalRegistro);
			}

	    	List<ShapeInventarioForestalCertificado> listShapes = shapeInventarioForestalCertificadoFacade.getByInventarioForestalAmbiental(idInventarioForestalRegistro);
	    	for(int i=0;i<=listShapes.size()-1;i++){
	    		shape = new ShapeInventarioForestalCertificado();
    			shape = listShapes.get(i);
    			List<CoordenadasInventarioForestalCertificado> coorImpl= new ArrayList<CoordenadasInventarioForestalCertificado>();
    			coorImpl = coordenadasInventarioForestalCertificadoFacade.getByShape(shape.getId());
    			CoordendasPoligonosCertificado coordinatesWrapper = new CoordendasPoligonosCertificado(coorImpl, poligono);
    			coordinatesWrappers.add(coordinatesWrapper);
			}
	    	
	    	listRegistroEspeciesForestales = registroEspeciesForestalesFacade.getByInventarioForestalCertificado(idInventarioForestalRegistro);
	    	for (RegistroEspeciesForestales rowEspecies : listRegistroEspeciesForestales) {
	    		HigherClassification higherClassification = higherClassificationFacade.getById(rowEspecies.getIdGenero());
	    		rowEspecies.setGeneroEspecie(higherClassification);
	    		rowEspecies.setFamiliaEspecie(higherClassification.getHiclIdParent());
	    		SpecieTaxa especie = new SpecieTaxa();
	    		if (rowEspecies.getIdEspecie() == null) {
	    			especie.setSptaScientificName("sp");
				} else if (rowEspecies.getIdEspecie() == 0) {
					especie.setSptaOtherScientificName(rowEspecies.getOtroEspecie());
				} else {
					especie = specieTaxaFacade.getById(rowEspecies.getIdEspecie());
				}
	    		rowEspecies.setEspecieEspecie(especie);
			}
	    	
	    	listRegistroAmbientalSumatoria = registroAmbientalSumatoriaFacade.getByIdInventarioForestal(idInventarioForestalRegistro);
	    	
	    	PromedioInventarioForestal promedioBase = promedioInventarioForestalFacade.getByIdInventarioForestalRegistro(idInventarioForestalRegistro);
	    	if (promedioBase.getId() != null) {
				listPromedioInventarioForestal.add(promedioBase);
			}
	    	
	    	listIndiceValorImportanciaInventarioForestal = indiceValorImportanciaInventarioForestalFacade.getByIdInventarioForestalRegistro(idInventarioForestalRegistro);
	    	if (listIndiceValorImportanciaInventarioForestal.size() > 0) {
	    		for (IndiceValorImportanciaInventarioForestal rowIndice : listIndiceValorImportanciaInventarioForestal) {
	    			HigherClassification higherClassification = higherClassificationFacade.getById(rowIndice.getIdGenero());
	    			rowIndice.setGeneroEspecie(higherClassification);
		    		rowIndice.setFamiliaEspecie(higherClassification.getHiclIdParent());
		    		SpecieTaxa especie = new SpecieTaxa();
		    		if (rowIndice.getIdEspecie() == null) {
		    			especie.setSptaScientificName("sp");
					} else {
						especie = specieTaxaFacade.getById(rowIndice.getIdEspecie());
					}
		    		rowIndice.setEspecieEspecie(especie);
				}
			}
	    	
	    	bienesServiciosInventarioForestal = bienesServiciosInventarioForestalFacade.getByIdInventarioForestalRegistro(idInventarioForestalRegistro);
	    	documentoValoracion = documentoInventarioForestalFacade.getByInventarioTipoDocumento(idInventarioForestalRegistro, TipoDocumentoSistema.RCOA_VALORACION_BIENES);

	    	if (inventarioForestalAmbiental.getEspecialistaAmbiental() != null) {
				especialistaAmbiental = especialistaAmbientalCoaFacade.getById(inventarioForestalAmbiental.getEspecialistaAmbiental().getId());
			}
    	} catch (ServiceException | CmisAlfrescoException e) {
    		JsfUtil.addMessageError("Error obtener los Datos");
    		LOG.error(e);
    	}
    }
    
    public String getMetodoRecoleccionDatos() {
    	if(inventarioForestalAmbiental.getMetodoRecoleccionDatos()!=null)
    		return inventarioForestalAmbiental.getMetodoRecoleccionDatos().getDescripcion().toUpperCase();
    	return "";
    }
    
    public String sumatoria(String campo) {
    	double valor=0;
    	for (IndiceValorImportanciaInventarioForestal item : listIndiceValorImportanciaInventarioForestal) {    		
    		
    		switch (campo) {
    		case "fr":
				valor+= item.getFrecuenciaEspecies();
				break;
			case "m2":
				valor+= item.getAreaBasal();
				break;
			case "dnr":
				valor+= item.getDnr();
				break;	
			case "dmr":
				valor+= item.getDmr();
				break;
			case "ivi":
				valor+= item.getIvi();
				break;
			default:
				break;
			}
    		
		}
    	
    	DecimalFormat df=new DecimalFormat(campo.contains("fr")?"#0":"#0.00");
    	return df.format(valor);
    }

}