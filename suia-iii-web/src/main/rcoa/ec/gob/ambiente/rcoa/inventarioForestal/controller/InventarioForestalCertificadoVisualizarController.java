package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.EspecialistaAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.CertificadoAmbientalSumatoriaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.CoordenadasInventarioForestalCertificadoFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.DocumentoInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.HigherClassificationFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.RegistroEspeciesForestalesFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ShapeInventarioForestalCertificadoFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.SpecieTaxaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.CertificadoAmbientalSumatoria;
import ec.gob.ambiente.rcoa.inventarioForestal.model.CoordenadasInventarioForestalCertificado;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.HigherClassification;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.RegistroEspeciesForestales;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ShapeInventarioForestalCertificado;
import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;
import ec.gob.ambiente.rcoa.model.EspecialistaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoForma;
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
public class InventarioForestalCertificadoVisualizarController {
	
	private static final Logger LOG = Logger.getLogger(InventarioForestalCertificadoVisualizarController.class);
	
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
		private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalCoaShapeFacade;
		@EJB
		private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
		@EJB
		private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
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
		private CertificadoAmbientalSumatoriaFacade certificadoAmbientalSumatoriaFacade;
	    @EJB
		private DocumentoInventarioForestalFacade documentoInventarioForestalFacade;
	    @EJB
	    private AsignarTareaFacade asignarTareaFacade;
	    @EJB
	    private UsuarioFacade usuarioFacade;
	    @EJB
	    private EspecialistaAmbientalCoaFacade especialistaAmbientalCoaFacade;
	    
	    
	    /*List*/
	    @Setter
		@Getter
		private List<RegistroEspeciesForestales> listRegistroEspeciesForestales, listRegistroEspeciesForestalesEliminar;
	    @Setter
		@Getter
		private List<HigherClassification> listFamilia, listGenero;
	    @Setter
		@Getter
		private List<SpecieTaxa> listEspecie;
	    @Setter
		@Getter
		private List<CertificadoAmbientalSumatoria> listCertificadoAmbientalSumatoria = new ArrayList<CertificadoAmbientalSumatoria>();
	    @Getter
	    @Setter
	    private List<CoordendasPoligonosCertificado> coordinatesWrappers;
	    @Getter
	    private List<TipoForma> tiposFormas  = new ArrayList<TipoForma>();
	    
	    
	    
	    /*Object*/
	    @Setter
	    @Getter
	    private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();
	    @Setter
	    @Getter
	    private InventarioForestalAmbiental inventarioForestalAmbiental;
	    @Setter
	    @Getter
	    private RegistroEspeciesForestales registroEspeciesForestales;
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
	    @Setter
	    @Getter
		private DocumentoInventarioForestal documentoCoordenadas = new DocumentoInventarioForestal();
	    @Setter
	    @Getter
		private DocumentoInventarioForestal documentoEspecies = new DocumentoInventarioForestal();
	    @Setter
	    @Getter
	    private EspecialistaAmbiental especialistaAmbiental = new EspecialistaAmbiental();
	    @Setter
	    @Getter
	    private CertificadoAmbientalSumatoria certificadoAmbientalSumatoria = new CertificadoAmbientalSumatoria();
	    
	    
		/*Boolean*/
	    @Getter
	    private boolean blockFamilia=true, blockGenero=true, blockEspecie=true, blockOtro=true, esRevisarInformacion=false;
	    @Getter
	    private Integer idRegistroPreliminar;
	    @Setter
	    @Getter
	    private String tramite, tecnicoForestal="";
	    
	    
	    /*CONSTANTES*/
	    public static final Integer TIPO_INVENTARIO = 1;
	    public static final Double VALOR_AREA_BASAL = 0.7854;
	    public static final Double VALOR_VOLUMEN = 0.7;
	    public static final Double VALOR_M3_MADERA_PIE = 3.00;
	    public static final Integer ID_LAYER_COBERTURA = 12;
	    public static final Integer ID_ECOSISTEMAS = 13;
	    public static final String TAREA_REVISAR_INFORMACION = "Revisar informacion";
    
    @PostConstruct
	public void init() {
    	try {
    		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
    		tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
    		tecnicoForestal = (String) variables.get("tecnicoForestal");
    		idRegistroPreliminar = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
    		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idRegistroPreliminar);
    		inventarioForestalAmbiental = new InventarioForestalAmbiental();
    		inventarioForestalAmbiental.setSuperficieProyecto(proyectoLicenciaCoa.getSuperficie());
    		registroEspeciesForestales = new RegistroEspeciesForestales();
    		listRegistroEspeciesForestales = new ArrayList<RegistroEspeciesForestales>();
    		listRegistroEspeciesForestalesEliminar = new ArrayList<RegistroEspeciesForestales>();
    		listFamilia = new ArrayList<HigherClassification>();
    		listGenero = new ArrayList<HigherClassification>();
    		listEspecie = new ArrayList<SpecieTaxa>();
    		// Listado de Familias PLANTAE
    		listFamilia = obtenerListaFamilia();
    		tiposFormas = tipoFormaFacade.listarTiposForma();
    		poligono = getTipoForma(TipoForma.TIPO_FORMA_POLIGONO);
    		coordinatesWrappers = new ArrayList<CoordendasPoligonosCertificado>();
    		esRevisarInformacion = verificaTareaRevisarInformacion();
    		cargarInventarioForestalCertificado();
    	} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
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
		if (tarea.equals(TAREA_REVISAR_INFORMACION)) {
			isTareaRevision = true;
		}
    	return isTareaRevision;
    }
    
    public void cargarInventarioForestalCertificado() {
    	try {
    		Integer idRegistroPreliminarProyecto = proyectoLicenciaCoa.getId();
	    	InventarioForestalAmbiental inventarioBase = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(idRegistroPreliminarProyecto);
	    	if (inventarioBase.getId() != null) {
				inventarioForestalAmbiental = inventarioBase;
			}
	    	Integer idInventarioForestalAmbiental = inventarioForestalAmbiental.getId();
	    	certificadoAmbientalSumatoria = certificadoAmbientalSumatoriaFacade.getByIdInventarioForestalAmbiental(idInventarioForestalAmbiental);
	    	if (!listCertificadoAmbientalSumatoria.contains(certificadoAmbientalSumatoria)) {
	    		listCertificadoAmbientalSumatoria.add(certificadoAmbientalSumatoria);
			}

	    	List<ShapeInventarioForestalCertificado> listShapes = shapeInventarioForestalCertificadoFacade.getByInventarioForestalAmbiental(idInventarioForestalAmbiental);
	    	for(int i=0;i<=listShapes.size()-1;i++){
	    		shape = new ShapeInventarioForestalCertificado();
    			shape = listShapes.get(i);
    			List<CoordenadasInventarioForestalCertificado> coorImpl= new ArrayList<CoordenadasInventarioForestalCertificado>();
    			coorImpl = coordenadasInventarioForestalCertificadoFacade.getByShape(shape.getId());
    			CoordendasPoligonosCertificado coordinatesWrapper = new CoordendasPoligonosCertificado(coorImpl, poligono);
    			coordinatesWrappers.add(coordinatesWrapper);
			}
	    	
	    	listRegistroEspeciesForestales = registroEspeciesForestalesFacade.getByInventarioForestalCertificado(idInventarioForestalAmbiental);
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
	    	
	    	CertificadoAmbientalSumatoria certificadoBase = certificadoAmbientalSumatoriaFacade.getByIdInventarioForestalAmbiental(idInventarioForestalAmbiental);
	    	if (certificadoBase.getId() != null) {
	    		listCertificadoAmbientalSumatoria = new ArrayList<CertificadoAmbientalSumatoria>();
	    		certificadoBase.setSuperficieDesbroce(inventarioForestalAmbiental.getSuperficieDesbroce());
				listCertificadoAmbientalSumatoria.add(certificadoBase);
			}
	    	
	    	if (inventarioForestalAmbiental.getEspecialistaAmbiental() != null) {
				especialistaAmbiental = especialistaAmbientalCoaFacade.getById(inventarioForestalAmbiental.getEspecialistaAmbiental().getId());
			}
    	} catch (ServiceException e) {
    		JsfUtil.addMessageError("Error obtener los Datos");
    		LOG.error(e);
    	}
    }

}