package ec.gob.ambiente.prevencion.registro.proyecto.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.DefaultRequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.client.SuiaServicesArcon;
import ec.gob.ambiente.client.SuiaServices_ServiceLocator;
import ec.gob.ambiente.client.SuiaServices_Service_Arcon;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.comun.bean.AdicionarUbicacionesBean;
import ec.gob.ambiente.suia.comun.bean.CargarCoordenadasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FaseDesecho;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.FormaProyecto;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.SustanciaQuimica;
import ec.gob.ambiente.suia.domain.TipoEmisionInclusionAmbiental;
import ec.gob.ambiente.suia.domain.TipoEstudio;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.TipoMaterial;
import ec.gob.ambiente.suia.domain.TipoPoblacion;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.TipoUbicacion;
import ec.gob.ambiente.suia.domain.TipoUsoSustancia;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.v2.CategoriaIIFacadeV2;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.arcom.dm.ws.Coordenadas;
import ec.gob.arcom.dm.ws.DerechoMineroMAEDTO;

@ManagedBean
@ViewScoped
public class RegistroProyectoBean implements Serializable {

	private static final long serialVersionUID = -5145709934621039011L;

	private static final Logger LOGGER = Logger.getLogger(RegistroProyectoBean.class);

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	protected ProyectoLicenciamientoAmbientalFacade proyectoFacade;

	@Setter
	@Getter
	private ProyectoLicenciamientoAmbiental proyecto;

	@Setter
	private List<TipoEstudio> tiposEstudios;

	@Getter
	private List<TipoPoblacion> tiposPoblaciones;

	@Getter
	private List<TipoUbicacion> tiposUbicaciones;

	@Getter
	private List<TipoEmisionInclusionAmbiental> tiposEmisionesInclusionAmbiental;

	@Getter
	private List<FaseDesecho> fasesDesechos;

	@Getter
	@Setter
	private List<FaseDesecho> fasesDesechosSeleccionadas;

	@Getter
	private List<SustanciaQuimica> sustanciasQuimicas;

	@Getter
	@Setter
	private List<SustanciaQuimica> sustanciasQuimicasSeleccionadas;

	@Getter
	private List<TipoUsoSustancia> tipoUsoSustancias;

	@Getter
	private List<TipoMaterial> tiposMateriales;

	@Getter
	@Setter
	private String refineria;

	@Getter
	@Setter
	private String infraestructura;

	@Getter
	@Setter
	private String comercializadora;

	@Getter
	@Setter
	private String estacionServicio;

	@Getter
	@Setter
	private boolean mostrarUbicacionGeografica;

	@Getter
	@Setter
	private boolean mostrarAreaAltitud;

	@Getter
	@Setter
	private boolean edicion;

	private boolean skip;

	@Getter
	@Setter
	private boolean ocultarCoordenadas;

	@Getter
	private Boolean viabilidad;

	@Getter
	@Setter
	private String numeroOficioViabilidad;

	@Getter
	private Documento ayudaGeneracionDesechosEspecialesOPeligrosos;

	@Getter
	private Documento ayudaEmpleoSustanciasQuimicas;

	@Getter
	private Documento ayudaTransporteSustanciasQuimicasPeligrosas;

	@Getter
	@Setter
	private String numeroOficioViabilidadcamaronera;
	
	@Getter
	private Boolean viabilidadcamaronera;
	
	private String[] listPermisos;
	
    public String[] getListPermisos() {
        return listPermisos;
    }
    
  //Cris F: aumento de variable para buscar Trámite
    @Getter
    @Setter
    private String numeroTramite;
    
    boolean actualizar = false;
    
    @EJB
	private CategoriaIIFacadeV2 categoriaIIFacadeV2;
	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	
	private ProyectoLicenciamientoAmbiental proyectoMineria;
	
	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;
	
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineraFacade;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
 
    public void setListPermisos(String[] listPermisos) {
    	
        this.listPermisos = listPermisos;
        if(listPermisos.length>1){
			proyecto.setProyectoPlayasBahias(true);
			proyecto.setProyectoTierrasAltas(true);
        }else {
        	if(listPermisos[0].equals("playas")){
        		proyecto.setProyectoPlayasBahias(true);
        		proyecto.setProyectoTierrasAltas(false);
        	}else {
        		proyecto.setProyectoTierrasAltas(true);
        		proyecto.setProyectoPlayasBahias(false);
        	}
		}
    }
	
	public void setViabilidad(Boolean viabilidad) {
		this.viabilidad = viabilidad;
		if (!viabilidad)
			JsfUtil.addCallbackParam("showNoViabilidad");
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public String onFlowProcess(FlowEvent event) {
		if (event.getOldStep().equals("paso2") && 
				(JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.02.03") || 
						JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.03.05")) 
				&& event.getNewStep().equals("paso3") ) {
			if (JsfUtil.getBean(CatalogoActividadesBean.class).getDocumentName() == null || JsfUtil.getBean(CatalogoActividadesBean.class).getDocumentName().equals("") ) {
				JsfUtil.addMessageError("Archivo derecho minero requerido");
				return event.getOldStep();
			} else {
				return event.getNewStep();
			}
		}
		if (skip) {
			skip = false; // reset in case user goes back
			return "confirm";
		} else {
			return event.getNewStep();
		}
	}

	@PostConstruct
	public void init() {
		proyecto = new ProyectoLicenciamientoAmbiental();
		proyecto.setEstadoProyecto(true);
		proyecto.setFormasProyectos(new ArrayList<FormaProyecto>());
		proyecto.setProyectoUbicacionesGeograficas(new ArrayList<ProyectoUbicacionGeografica>());
		proyecto.setConcesionesMineras(new ArrayList<ConcesionMinera>());
		tiposEstudios = proyectoLicenciamientoAmbientalFacade.getTiposEstudios();
		tiposPoblaciones = proyectoLicenciamientoAmbientalFacade.getTiposPoblaciones();
		tiposUbicaciones = proyectoLicenciamientoAmbientalFacade.getTiposUbicaciones();
		tiposEmisionesInclusionAmbiental = proyectoLicenciamientoAmbientalFacade.getTiposEmisionesInclusionAmbiental();
		fasesDesechos = proyectoLicenciamientoAmbientalFacade.getFasesDesecho();
		sustanciasQuimicas = proyectoLicenciamientoAmbientalFacade.getSustanciasQuimicas();
		tipoUsoSustancias = proyectoLicenciamientoAmbientalFacade.getTiposUsosSustancias();
		tiposMateriales = proyectoLicenciamientoAmbientalFacade.getTiposMateriales();
		fasesDesechosSeleccionadas = new ArrayList<FaseDesecho>();
		sustanciasQuimicasSeleccionadas = new ArrayList<SustanciaQuimica>();

		mostrarUbicacionGeografica = true;
		mostrarAreaAltitud = true;

		try {
			inicializarAyudas();
		} catch (Exception exception) {
			LOGGER.error("Ocurrió un error en el alfresco al iniciar las ayudas del registro de proyecto.", exception);
		}

	}

	public void inicializarAyudas() throws Exception {
		ayudaEmpleoSustanciasQuimicas = new Documento();
		ayudaGeneracionDesechosEspecialesOPeligrosos = new Documento();
		ayudaTransporteSustanciasQuimicasPeligrosas = new Documento();

		byte[] ayudaEmpleoSustanciasQuimicasContenido = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(
				Constantes.AYUDA_EMPLEO_SUSTANCIAS_QUIMICAS, null);
		if (ayudaEmpleoSustanciasQuimicasContenido != null) {
			ayudaEmpleoSustanciasQuimicas.setContenidoDocumento(ayudaEmpleoSustanciasQuimicasContenido);
			ayudaEmpleoSustanciasQuimicas.setNombre(Constantes.AYUDA_EMPLEO_SUSTANCIAS_QUIMICAS);
		}

		byte[] ayudaGeneracionDesechosEspecialesOPeligrososContenido = documentosFacade
				.descargarDocumentoPorNombreYDirectorioBase(
						Constantes.AYUDA_GENERACION_DESECHOS_ESPECIALES_O_PELIGROSOS, null);
		if (ayudaGeneracionDesechosEspecialesOPeligrososContenido != null) {
			ayudaGeneracionDesechosEspecialesOPeligrosos
					.setContenidoDocumento(ayudaGeneracionDesechosEspecialesOPeligrososContenido);
			ayudaGeneracionDesechosEspecialesOPeligrosos
					.setNombre(Constantes.AYUDA_GENERACION_DESECHOS_ESPECIALES_O_PELIGROSOS);
		}

		byte[] ayudaTransporteSustanciasQuimicasPeligrosasContenido = documentosFacade
				.descargarDocumentoPorNombreYDirectorioBase(Constantes.AYUDA_TRANSPORTE_SUSTANCIAS_QUIMICAS_PELIGROSAS,
						null);
		if (ayudaTransporteSustanciasQuimicasPeligrosasContenido != null) {
			ayudaTransporteSustanciasQuimicasPeligrosas
					.setContenidoDocumento(ayudaTransporteSustanciasQuimicasPeligrosasContenido);
			ayudaTransporteSustanciasQuimicasPeligrosas
					.setNombre(Constantes.AYUDA_TRANSPORTE_SUSTANCIAS_QUIMICAS_PELIGROSAS);
		}
	}

	public StreamedContent getStreamContent(Documento documento) throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (documento != null && documento.getNombre() != null && documento.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception exception) {
			LOGGER.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;

	}

	public void cargarProvinciasSegunTipoUbicacion() {
		JsfUtil.getBean(AdicionarUbicacionesBean.class).cargarProvinciasSegunTipoUbicacion(
				getProyecto().getTipoUbicacion().getId());
	}

	public List<TipoEstudio> getTiposEstudios() {
		List<TipoEstudio> tiposEstudiosFinales = new ArrayList<TipoEstudio>();
		if (tiposEstudios == null)
			tiposEstudios = new ArrayList<TipoEstudio>();
		for (int i = 0; i < tiposEstudios.size(); i++) {
			if (!tiposEstudios.get(i).getId().equals(TipoEstudio.AUDITORIA_FINES_LICENCIAMIENTO)
					&& !tiposEstudios.get(i).getId().equals(TipoEstudio.ESTUDIO_EMISION_INCLUSION_AMBIENTAL))
					tiposEstudiosFinales.add(tiposEstudios.get(i));
			if (JsfUtil.getBean(CatalogoActividadesBean.class).getTipoSector().getId()
					.equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)
					|| JsfUtil.getBean(CatalogoActividadesBean.class).getTipoSector().getId()
							.equals(TipoSector.TIPO_SECTOR_MINERIA)) {
				if (JsfUtil.getBean(CatalogoActividadesBean.class).isCategoriaIII_IV()
						&& JsfUtil.getBean(CatalogoActividadesBean.class).getTipoSector().getId()
								.equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)
						&& (tiposEstudios.get(i).getId().equals(TipoEstudio.AUDITORIA_FINES_LICENCIAMIENTO) || tiposEstudios
								.get(i).getId().equals(TipoEstudio.ESTUDIO_EMISION_INCLUSION_AMBIENTAL)))
					if(!JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getId().equals(840))
						tiposEstudiosFinales.add(tiposEstudios.get(i));
				else if (JsfUtil.getBean(CatalogoActividadesBean.class).isCategoriaIII_IV()
						&& JsfUtil.getBean(CatalogoActividadesBean.class).getTipoSector().getId()
								.equals(TipoSector.TIPO_SECTOR_MINERIA)
						&& tiposEstudios.get(i).getId().equals(TipoEstudio.ESTUDIO_EMISION_INCLUSION_AMBIENTAL))
					tiposEstudiosFinales.add(tiposEstudios.get(i));
			}
		}
		return tiposEstudiosFinales;
	}


	public Date getCurrentDate() {
		return new Date();
	}

	public boolean validarActividadMinera() {
        if (!proyecto.isConcesionesMinerasMultiples()) {
            try {
                if (JsfUtil.getBean(CatalogoActividadesBean.class)
                        .getActividadSistemaSeleccionada().getId().equals(869)
                        || JsfUtil.getBean(CatalogoActividadesBean.class)
                                .getActividadSistemaSeleccionada().getId()
                                .equals(870)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }
	
	 public boolean validarSectorMineria(){
	        boolean aux=false;
	        try {
	            if (JsfUtil.getBean(CatalogoActividadesBean.class)
	                    .getActividadSistemaSeleccionada().getTipoLicenciamiento()
	                    .getId() == 3
	                    || JsfUtil.getBean(CatalogoActividadesBean.class)
	                            .getActividadSistemaSeleccionada()
	                            .getTipoLicenciamiento().getId() == 4) {
	                if (JsfUtil.getBean(CatalogoActividadesBean.class)
	                        .getActividadSistemaSeleccionada()
	                        .getCatalogoCategoriaPublico().getTipoSector().getId()
	                        .equals(2)) {
	                    aux = true;
	                }
	            } else {
	                aux = false;
	                
	            }
	        } catch (Exception e) {
	            aux = false;
	        }
	        return aux;
	    }

	public void setViabilidadcamaronera(Boolean viabilidadcamaronera) {
		System.out.println("valor de viabilidad camaronera::::"
				+ viabilidadcamaronera);
		this.viabilidadcamaronera = viabilidadcamaronera;
		if (!viabilidadcamaronera)
			JsfUtil.addCallbackParam("showNoViabilidadcamaronera");

	}
	
	public double validarValorMinimoCamaroneras(){
		if(JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada()!=null){
			if(JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("11.03.03")){
				return 100.01;
			}
		}
		if(esGranjaAcuicola()){
			return 0.01;
		}
		return 0.00;
	}
	
	public double validarValorMaximoCamaroneras(){
		if(JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada()!=null){
			if(JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("11.03.03")){
				return 500000.00;
			}
			if(JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("11.03.04")){
				return 100.00;
			}
		}
		return 500000.00;
	}
	
	public void buscarTramiteMineria(){		
		try {
			if(getNumeroTramite() != null && !getNumeroTramite().isEmpty()){
				
				proyectoMineria = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(getNumeroTramite());
				
				if(loginBean.getNombreUsuario().equals(proyectoMineria.getUsuario().getNombre())){
					if(proyectoMineria != null){
						FichaAmbientalPma ficha = fichaAmbientalPmaFacade.getFichaAmbientalPorIdProyecto(proyectoMineria.getId());
						
						if(ficha != null && ficha.getNumeroLicencia() != null && !ficha.getNumeroLicencia().isEmpty()){ //&& ficha.getFinalizado() != null && ficha.getFinalizado() == true
							if(!proyectoLicenciamientoAmbientalFacade.obtenerProcesoActivo(getNumeroTramite())){
							
								PerforacionExplorativa perforacionExplorativa = fichaAmbientalMineraFacade.cargarPerforacionExplorativa(proyectoMineria);	
														
								if(perforacionExplorativa == null || perforacionExplorativa.getId() == null){
									if((JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.02.03"))){
										if(proyectoMineria.getCatalogoCategoria().getCodigo().equals("21.02.02.01")){ //.getCatalogoCategoriaPublico().getNombre().equals("EXPLORACIÓN INICIAL EN MEDIANA Y GRAN MINERÍA (METÁLICOS Y NO METÁLICOS)")){
											if(proyectoMineria.getGeneraDesechos().equals(false)){
	                                            iniciaDesechos();
	                                        }
											DefaultRequestContext.getCurrentInstance().execute("PF('actualizarDlg').show();");
											
										}else{
											JsfUtil.addMessageInfo("El proyecto no corresponde a EXPLORACIÓN INICIAL EN MEDIANA Y GRAN MINERÍA (METÁLICOS Y NO METÁLICOS)");
											setNumeroTramite("");
										}
									}else if((JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.03.05"))){
										if(proyectoMineria.getCatalogoCategoria().getCodigo().equals("21.02.03.06")){ 
											if(proyectoMineria.getGeneraDesechos().equals(false)){
	                                            iniciaDesechos();
	                                        }
											DefaultRequestContext.getCurrentInstance().execute("PF('actualizarDlg').show();");
											
										}else{
											JsfUtil.addMessageInfo("El proyecto no corresponde a EXPLORACIÓN INICIAL EN PEQUEÑA MINERIA (METÁLICOS Y NO METÁLICOS)");
											setNumeroTramite("");
										}
									}
								}else{
									JsfUtil.addMessageError("El trámite tiene una actualización anterior.");
									setNumeroTramite("");								
								}
							}else{
								JsfUtil.addMessageError("El proceso ya tiene iniciado una proceso de actualización.");								
								setNumeroTramite("");
							}
						}else{
							JsfUtil.addMessageError("El Registro Ambiental no esta finalizado.");
							setNumeroTramite("");
						}
					}else{
						JsfUtil.addMessageError("No existe el número de trámite.");
						setNumeroTramite("");
					}
				}else{
					JsfUtil.addMessageError("El proponente no es el mismo del proyecto ingresado."); 
					setNumeroTramite("");
				}
				
			}else{
				JsfUtil.addMessageError("Ingrese el código del trámite a buscar.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void iniciaDesechos(){
        proyectoMineria.setGeneraDesechos(true);
        proyectoLicenciamientoAmbientalFacade.actualizarSimpleProyecto(proyectoMineria);
    }
	
	public void actualizarProyecto(){
		try {				
			
			if(!actualizar){
				inicarProcesoCategoriaII(proyectoMineria.getUsuario(), proyectoMineria);
				actualizar = true;
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public Map<String, Object> getParametrosIniciarProcesoCategoriaII(String nombreUsuario,
            ProyectoLicenciamientoAmbiental proyecto) {
		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
		params.put("nombreFormularioPMA", "/prevencion/categoria2/v2/fichaMineria020/default.jsf");
		params.put("u_Promotor", nombreUsuario);
		params.put("codigoProyecto", proyecto.getCodigo());
		params.put("exentoPago", categoriaIIFacadeV2.getExentoPago(proyecto));
		params.put("factorCovertura", Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal")));
		params.put("costoTramite", Float.parseFloat(Constantes.getPropertyAsString("costo.tramite.registro.ambiental")));
		return params;
	}
	
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	public void inicarProcesoCategoriaII(Usuario usuario, ProyectoLicenciamientoAmbiental proyecto) throws ServiceException{
		Map<String, Object> params = getParametrosIniciarProcesoCategoriaII(usuario.getNombre(), proyecto);
		params.put(Constantes.ID_PROYECTO, proyecto.getId());
		Area areaProyecto = areaFacade.getArea(proyecto.getAreaResponsable().getId());
		String usrMaximaAutoridad = null;
		try {
			if (areaProyecto.getTipoArea().getId() == 3) {
				usrMaximaAutoridad = areaFacade.getDirectorProvincial(
						areaProyecto).getNombre(); // Director Provincial (por
				// ministra)
				if (usrMaximaAutoridad != null) {
					procesoFacade.iniciarProceso(usuario,
							Constantes.NOMBRE_PROCESO_CATEGORIA2V2,
							proyecto.getCodigo(), params);
				}
			} else {
				procesoFacade.iniciarProceso(usuario,
						Constantes.NOMBRE_PROCESO_CATEGORIA2V2,
						proyecto.getCodigo(), params);
			}
		} catch (JbpmException | ec.gob.ambiente.suia.exceptions.ServiceException e) {
			throw new ServiceException(e);
		}
	}
	
	
	
	private List<TipoForma> tiposFormas;
	private CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper();
	@Setter
	@Getter
	private List<CoordinatesWrapper> listaCoordinatesWrapper = new ArrayList<CoordinatesWrapper>();
	@Setter
	@Getter
	private boolean estadoConcesion=true;
	@EJB
    private CrudServiceBean crudServiceBean;
	@Getter
	@Setter	
	private boolean habilitartextinput = false;
	
	private URL getUrlWS() throws MalformedURLException {
		URL url = null;
		URL baseUrl;
		baseUrl = ec.gob.ambiente.client.SuiaServices_Service.class.getResource(".");
		url = new URL(baseUrl, Constantes.getUrlWsRegistroCivilSri());
		return url;
	}
	
	public void validarConcesion()
	{
		DerechoMineroMAEDTO derechoMinero = new DerechoMineroMAEDTO();
		SuiaServices_Service_Arcon concesion;
		try {
			concesion = new SuiaServices_Service_Arcon(getUrlWS());
			SuiaServicesArcon arcon=concesion.getSuiaServicesPort();
			derechoMinero=arcon.getConsultarCatastral(proyecto.getCodigoMinero());
		} catch (Exception e1) {
			JsfUtil.addMessageError("Sin servicio");
		}
		
		tiposFormas = proyectoLicenciamientoAmbientalFacade.getTiposFormas();
		
		CoordinatesWrapper coordinatesWrapperVc = new CoordinatesWrapper();
		List<CoordinatesWrapper> listaCoordinatesWrapper = new ArrayList<CoordinatesWrapper>();
		
		List<Coordenada> listaCoordenada = new ArrayList<Coordenada>();
		Coordenada coordenada = new Coordenada();
		if(derechoMinero.getCoordenadasPSAD56().size()>0)
		{
			if((JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.03.05")
        			|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.04.03")
        			|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.05.03")
        			|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.02.03"))){
				habilitartextinput=true;
			}
			JsfUtil.getBean(RegistroProyectoBean.class).getProyecto().setArea(derechoMinero.getDerechoMinero().getSuperficie());
			if (!loginBean.getNombreUsuario().equals(derechoMinero.getDerechoMinero().getTitularDocumento())) 
			{
				JsfUtil.addMessageError("El Código de concesión minera no pertenece a este usuario");
				return;
			}
			
			TipoForma tipoForma = getTipoForma(derechoMinero.getCoordenadasPSAD56().get(0).getTipoArea());
			coordinatesWrapperVc.setTipoForma(tipoForma);
			// cargo el tipo de material
			proyecto.setTipoMaterial(getTipoMaterial(derechoMinero.getDerechoMinero().getMineral()));
			proyecto.setUnidad("ha");
			int x=1;
			for (Coordenadas concesiones : derechoMinero.getCoordenadasPSAD56()) {
				coordenada = new Coordenada();
				if(x==1)
				{	
					coordenada.setOrden(x);
					coordenada.setX(Double.valueOf(concesiones.getUtmEste()));
					coordenada.setY(Double.valueOf(concesiones.getUtmNorte()));
					coordenada.setZona("17S");
					coordenada.setDescripcion("Inicio");
					listaCoordenada.add(coordenada);
					x++;
				}
				else
				{
					coordenada.setOrden(x);
					coordenada.setX(Double.valueOf(concesiones.getUtmEste()));
					coordenada.setY(Double.valueOf(concesiones.getUtmNorte()));
					coordenada.setZona("17S");
					coordenada.setDescripcion("");
					listaCoordenada.add(coordenada);
					x++;
				}				
			}
			for (Coordenadas concesiones : derechoMinero.getCoordenadasPSAD56()) {
				coordenada = new Coordenada();
				coordenada.setOrden(x);
				coordenada.setX(Double.valueOf(concesiones.getUtmEste()));
				coordenada.setY(Double.valueOf(concesiones.getUtmNorte()));
				coordenada.setZona("17S");
				coordenada.setDescripcion("Fin");
				listaCoordenada.add(coordenada);
				break;
			}			
			coordinatesWrapperVc.setCoordenadas(listaCoordenada);
			coordinatesWrapperVc.setTipoForma(tipoForma);
			listaCoordinatesWrapper.add(coordinatesWrapperVc);
			
			if(proyecto.getCodigo()==null && proyecto.getNombre()!=null){
				proyecto.setCodigo(secuenciasFacade.getSecuenciaProyecto());
			}			
			List<CoordinatesWrapper> listareproyectar = new ArrayList<CoordinatesWrapper>();
			listareproyectar=JsfUtil.getBean(CargarCoordenadasBean.class).reproyectarTodasCoordenadas(listaCoordinatesWrapper);			
			HashMap<String, Double> varUbicacion = new HashMap<String, Double>();
	        try{
	        	varUbicacion = JsfUtil.getBean(CargarCoordenadasBean.class).retornarParroquias(listareproyectar);
	        	JsfUtil.getBean(CargarCoordenadasBean.class).cargarUbicacionProyecto(varUbicacion);
		        JsfUtil.getBean(CargarCoordenadasBean.class).formatoArcom(listareproyectar);
	        }
	        catch(Exception e){
	        	JsfUtil.getBean(CargarCoordenadasBean.class).cargarUbicacionProyecto(new HashMap<String, Double>());
	        	JsfUtil.getBean(CargarCoordenadasBean.class).formatoArcom(new ArrayList<CoordinatesWrapper>());
	        	JsfUtil.addMessageError(JsfUtil.getBean(CargarCoordenadasBean.class).getMensajeErrorCoordenada()); 
	        }
		}
		else
		{
			JsfUtil.addMessageError("No encontró resultado del código minero"); 
		}
		
	}
	
	private TipoForma getTipoForma(String nombre) {
        for (TipoForma tipoForma : tiposFormas) {
            if (JsfUtil.comparePrimaryStrings(tipoForma.getNombre(), nombre))
                return tipoForma;
        }
        return null;
    }
	
	private TipoMaterial getTipoMaterial(String nombre) {
        for (TipoMaterial tipoMaterial : tiposMateriales) {
            if (JsfUtil.comparePrimaryStrings(tipoMaterial.getNombre(), nombre))
                return tipoMaterial;
        }
        return null;
    }
	
	public void validarConcesiones(String codigo)
	{			
		DerechoMineroMAEDTO derechoMinero = new DerechoMineroMAEDTO();
		SuiaServices_Service_Arcon concesion;
		try {
			concesion = new SuiaServices_Service_Arcon(getUrlWS());
			SuiaServicesArcon arcon=concesion.getSuiaServicesPort();
			derechoMinero=arcon.getConsultarCatastral(codigo);
		} catch (Exception e1) {
			JsfUtil.addMessageError("Sin servicio");
		}
		
		tiposFormas = proyectoLicenciamientoAmbientalFacade.getTiposFormas();		
		List<Coordenada> listaCoordenada = new ArrayList<Coordenada>();
		Coordenada coordenada = new Coordenada();
		if(derechoMinero.getCoordenadasPSAD56().size()>0)
		{
			if (!loginBean.getNombreUsuario().equals(derechoMinero.getDerechoMinero().getTitularDocumento())) 
			{
				JsfUtil.addMessageError("El Código de concesión minera no pertenece a este usuario");
				return;
			}
			habilitartextinput=true;
			
			JsfUtil.getBean(ConcesionesBean.class).getConcesionMinera().setNombre(derechoMinero.getDerechoMinero().getNombreDerechoMinero());		
			JsfUtil.getBean(ConcesionesBean.class).getConcesionMinera().setFase(derechoMinero.getDerechoMinero().getFase());
			JsfUtil.getBean(ConcesionesBean.class).getConcesionMinera().setMaterial(derechoMinero.getDerechoMinero().getMineral());
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
	        Date fechaDate = null;
	        
	        try {
	            fechaDate = formato.parse(derechoMinero.getDerechoMinero().getFechaInscripcion());
	        } 
	        catch (ParseException ex) 
	        {
				
	        }
	        
			JsfUtil.getBean(ConcesionesBean.class).getConcesionMinera().setFechaInscripcion(fechaDate);
				
			ConcesionMinera concesionMinera = new ConcesionMinera();		
			concesionMinera.setNombre(derechoMinero.getDerechoMinero().getNombreDerechoMinero());
			JsfUtil.getBean(ConcesionesBean.class).getConcesionMinera().setArea(derechoMinero.getDerechoMinero().getSuperficie());
			concesionMinera.setCodigo(codigo);
			
			coordinatesWrapper = new CoordinatesWrapper();
			TipoForma tipoForma = getTipoForma(derechoMinero.getCoordenadasPSAD56().get(0).getTipoArea());
			coordinatesWrapper.setTipoForma(tipoForma);
			int x=1;
			for (Coordenadas concesiones : derechoMinero.getCoordenadasPSAD56()) {
				coordenada = new Coordenada();
				if(x==1)
				{	
					coordenada.setOrden(x);
					coordenada.setX(Double.valueOf(concesiones.getUtmEste()));
					coordenada.setY(Double.valueOf(concesiones.getUtmNorte()));
					coordenada.setZona("17S");
					coordenada.setDescripcion("Inicio");
					listaCoordenada.add(coordenada);
					x++;
				}
				else
				{
					coordenada.setOrden(x);
					coordenada.setX(Double.valueOf(concesiones.getUtmEste()));
					coordenada.setY(Double.valueOf(concesiones.getUtmNorte()));
					coordenada.setZona("17S");
					coordenada.setDescripcion("");
					listaCoordenada.add(coordenada);
					x++;
				}				
			}
			for (Coordenadas concesiones : derechoMinero.getCoordenadasPSAD56()) {
				coordenada = new Coordenada();
				coordenada.setOrden(x);
				coordenada.setX(Double.valueOf(concesiones.getUtmEste()));
				coordenada.setY(Double.valueOf(concesiones.getUtmNorte()));
				coordenada.setZona("17S");
				coordenada.setDescripcion("Fin");
				listaCoordenada.add(coordenada);
				break;
			}			
			coordinatesWrapper.setCoordenadas(listaCoordenada);
			coordinatesWrapper.setTipoForma(tipoForma);			
			
			List<CoordinatesWrapper> listareproyectar = new ArrayList<CoordinatesWrapper>();
			List<CoordinatesWrapper> listaCoordinatesWrapperAuxiliar = new ArrayList<CoordinatesWrapper>();
			HashMap<String, Double> varUbicacion = new HashMap<String, Double>();
			listaCoordinatesWrapperAuxiliar.add(coordinatesWrapper); 
			
			if(proyecto.getCodigo()==null && proyecto.getNombre()!=null){
				proyecto.setCodigo(secuenciasFacade.getSecuenciaProyecto());
			}	
			listareproyectar=JsfUtil.getBean(CargarCoordenadasBean.class).reproyectarTodasCoordenadas(listaCoordinatesWrapperAuxiliar);	
			try{
				varUbicacion = JsfUtil.getBean(CargarCoordenadasBean.class).retornarParroquias(listareproyectar);
				Map<String, Object> parametros = new HashMap<String, Object>();
                parametros.put("codeInec", derechoMinero.getDerechoMinero().getCodigoParroquia());
                List<UbicacionesGeografica> ubicacionesSeleccionadas = crudServiceBean.findByNamedQueryGeneric(UbicacionesGeografica.FIND_PARROQUIA,parametros);
                JsfUtil.getBean(AdicionarUbicaciones1Bean.class).ubicacionConcesion(ubicacionesSeleccionadas);
               estadoConcesion=false;
			}catch(Exception e){				
				JsfUtil.addMessageError(JsfUtil.getBean(CargarCoordenadasBean.class).getMensajeErrorCoordenada());
				estadoConcesion=true;
				return;
			}
		}
		else
		{
			JsfUtil.addMessageError("No encontró resultado del código minero"); 
		}		
	}
	
	public String cargarConcesionesCoordenadas(String codigo)
	{	
		
		DerechoMineroMAEDTO derechoMinero = new DerechoMineroMAEDTO();
		SuiaServices_Service_Arcon concesion;
		try {
			concesion = new SuiaServices_Service_Arcon(getUrlWS());
			SuiaServicesArcon arcon=concesion.getSuiaServicesPort();
			derechoMinero=arcon.getConsultarCatastral(codigo);
		} catch (Exception e1) {
			JsfUtil.addMessageError("Sin servicio");
		}		
		tiposFormas = proyectoLicenciamientoAmbientalFacade.getTiposFormas();		
		List<Coordenada> listaCoordenada = new ArrayList<Coordenada>();
		Coordenada coordenada = new Coordenada();
		if(derechoMinero.getCoordenadasPSAD56().size()>0)
		{
//			JsfUtil.getBean(ConcesionesBean.class).getConcesionMinera().setNombre(derechoMinero.getDerechoMinero().getNombreDerechoMinero());
//			concesionMinera.setNombre(derechoMinero.getDerechoMinero().getNombreDerechoMinero());
//			concesionMinera.setCodigo(codigo);
			
			coordinatesWrapper = new CoordinatesWrapper();
			TipoForma tipoForma = getTipoForma(derechoMinero.getCoordenadasPSAD56().get(0).getTipoArea());
			coordinatesWrapper.setTipoForma(tipoForma);
			int x=1;
			for (Coordenadas concesiones : derechoMinero.getCoordenadasPSAD56()) {
				coordenada = new Coordenada();
				if(x==1)
				{	
					coordenada.setOrden(x);
					coordenada.setX(Double.valueOf(concesiones.getUtmEste()));
					coordenada.setY(Double.valueOf(concesiones.getUtmNorte()));
					coordenada.setZona("17S");
					coordenada.setDescripcion("Inicio");
					listaCoordenada.add(coordenada);
					x++;
				}
				else
				{
					coordenada.setOrden(x);
					coordenada.setX(Double.valueOf(concesiones.getUtmEste()));
					coordenada.setY(Double.valueOf(concesiones.getUtmNorte()));
					coordenada.setZona("17S");
					coordenada.setDescripcion("");
					listaCoordenada.add(coordenada);
					x++;
				}				
			}
			for (Coordenadas concesiones : derechoMinero.getCoordenadasPSAD56()) {
				coordenada = new Coordenada();
				coordenada.setOrden(x);
				coordenada.setX(Double.valueOf(concesiones.getUtmEste()));
				coordenada.setY(Double.valueOf(concesiones.getUtmNorte()));
				coordenada.setZona("17S");
				coordenada.setDescripcion("Fin");
				listaCoordenada.add(coordenada);
				break;
			}			
			coordinatesWrapper.setCoordenadas(listaCoordenada);
			coordinatesWrapper.setTipoForma(tipoForma);
			listaCoordinatesWrapper.add(coordinatesWrapper);
			if(proyecto.getCodigo()==null && proyecto.getNombre()!=null){
				proyecto.setCodigo(secuenciasFacade.getSecuenciaProyecto());
			}	
			HashMap<String, Double> varUbicacion = new HashMap<String, Double>();
			List<CoordinatesWrapper> listareproyectar = new ArrayList<CoordinatesWrapper>();
			listareproyectar = new ArrayList<CoordinatesWrapper>();
			listareproyectar=JsfUtil.getBean(CargarCoordenadasBean.class).reproyectarTodasCoordenadas(listaCoordinatesWrapper);			
	        varUbicacion = JsfUtil.getBean(CargarCoordenadasBean.class).retornarParroquias(listareproyectar);
	        JsfUtil.getBean(CargarCoordenadasBean.class).cargarUbicacionProyecto(varUbicacion);
	        JsfUtil.getBean(CargarCoordenadasBean.class).formatoArcom(listareproyectar);
		}
		else
		{
			JsfUtil.addMessageError("No encontró resultado del código minero"); 
		}
		return "";
	}
	
	public String cargarColindantes(String codigo)
	{
		String coordendasColindantes="";
		DerechoMineroMAEDTO derechoMinero = new DerechoMineroMAEDTO();
		SuiaServices_Service_Arcon concesion;
		try {
			concesion = new SuiaServices_Service_Arcon(getUrlWS());
			SuiaServicesArcon arcon=concesion.getSuiaServicesPort();
			derechoMinero=arcon.getConsultarCatastral(codigo);
		} catch (Exception e1) {
			JsfUtil.addMessageError("Sin servicio");
		}		
		tiposFormas = proyectoLicenciamientoAmbientalFacade.getTiposFormas();
		if(derechoMinero.getCoordenadasPSAD56().size()>0)
		{			
			for (Coordenadas concesiones : derechoMinero.getCoordenadasPSAD56()) {
				coordendasColindantes=coordendasColindantes+concesiones.getUtmEste()+" "+concesiones.getUtmNorte()+",";
			}
			//cerrar el poligono con la coordenada de la primera posicion.
			for (Coordenadas concesiones : derechoMinero.getCoordenadasPSAD56()) {
				coordendasColindantes=coordendasColindantes+concesiones.getUtmEste()+" "+concesiones.getUtmNorte();
				break;
			}
		}
		return coordendasColindantes;		
	}
	public void limpiarListaCoordenadas()
	{	
		JsfUtil.getBean(CargarCoordenadasBean.class).limpiarCoorUbicacion();		
		listaCoordinatesWrapper=new ArrayList<CoordinatesWrapper>();
	}

	 /**
	  * metodo para saber si el proyectos es de una actividad de scout drilling
	  * @return
	  */
	 public boolean mostrarSistemareferenciasSD(){
		 boolean mostrar=false;
		 try
		 {
			 if(JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals( "21.02.03.05")
					 || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals( "21.02.04.03")
					 || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals( "21.02.05.03")
					 || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals( "21.02.02.03") ){
				 mostrar = true;
			 }
		 }
		 catch(Exception e){
			 mostrar=false;
		 }
		 
		 return mostrar;
	 }
	 
	 public boolean mostrarSistemareferenciasTitulo(){
		 boolean mostrar=false;
		 try
		 {
			 if(JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals( "21.02.04.03")
					 || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals( "21.02.05.03")
					 || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals( "21.02.02.03") ){
				 mostrar = true;
			 }
		 }
		 catch(Exception e){
			 mostrar=false;
		 }
		 
		 return mostrar;
	 }
	 
	 public boolean mostrarSistemareferenciasPM(){
		 boolean mostrar=false;
		 try
		 {
			 if(JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals( "21.02.03.05")){
				 mostrar = true;
			 }
		 }
		 catch(Exception e){
			 mostrar=false;
		 }
		 
		 return mostrar;
	 }
	 
	 public Boolean esGranjaAcuicola() {
			boolean resultado = false;
			try {
				resultado = proyectoFacade
						.esGranjasAcuicolas(JsfUtil.getBean(CatalogoActividadesBean.class)
								.getActividadSistemaSeleccionada().getId());
			} catch (Exception e) {
				// TODO: handle exception
			}
			return resultado;
		}
}
