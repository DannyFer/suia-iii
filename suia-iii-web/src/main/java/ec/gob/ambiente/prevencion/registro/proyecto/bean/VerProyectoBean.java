package ec.gob.ambiente.prevencion.registro.proyecto.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.prevencion.registro.proyecto.controller.RegistroProyectoHidrocarburosController;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.RegistroProyectoMineriaController;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.RegistroProyectoOtrosSectoresController;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.RegistroProyectoSaneamientoController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.administracion.facade.ProyectosCamaronerasFacade;
import ec.gob.ambiente.suia.catalogocategoriasflujo.facade.CatalogoCategoriasFlujoFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.CategoriaFlujo;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeForestal;
import ec.gob.ambiente.suia.domain.CertificadoRegistroAmbiental;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.ProyectoCamaronera;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.SistemasIntegrales;
import ec.gob.ambiente.suia.domain.TipoEstudio;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.TipoSubsector;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.Categoria1Facade;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.InformeInspeccionForestalCAFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.reportes.GenerarReporteBean;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class VerProyectoBean implements Serializable, GenerarReporteBean {

    private static final long serialVersionUID = -4019782385343101507L;

    @EJB
    private OrganizacionFacade organizacionFacade;
    
    @EJB
    private ProyectosCamaronerasFacade proyectosCamaronerasFacade;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;
    
    /***** código para visualizar el pago según el tipo de organización (2016-01-27) *****/
    private Organizacion organizacion;
  	/***** fin código para visualizar el pago según el tipo de organización *****/
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private boolean mostrarAcciones;

	@Setter
	private boolean showModalCertificadoIntercepcion;

	@Setter
	private boolean showModalAceptarResponsabilidad;

	@Getter
	@Setter
	private String alertaProyectoIntesecaZonasProtegidas;

	@Getter
	@Setter
	private String notaResponsabilidadRegistro;

	@Getter
	@Setter
	private boolean showModalErrorProcesoInterseccion;

	@Getter
	@Setter
	private boolean showMensajeErrorGeneracionDocumentos;

	@Getter
	@Setter
	private String mensaje;

	@Getter
	@Setter
	private String pathImagen;

	@Getter
	@Setter
	private boolean generadoCorrectamenteDocumentosCertificadoInterseccion;

	@Getter
    @Setter
    private String tieneSistInt="";
    
    @Getter
    @Setter
    private boolean tablaTieneSistInt;
    
    @Getter 
    @Setter
    private String codigoSistInt="";
    
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@EJB
    private Categoria1Facade categoria1Facade;
	
	/***** módulo de encuesta *****/
	@EJB 
	private SurveyResponseFacade surveyResponseFacade;
	@Getter
    @Setter
    public static String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
	
	@Getter
    @Setter
    public static String reportLink = Constantes.getPropertyAsString("suia.report.link");
	/***** fin módulo de encuesta *****/

	@Getter
	@Setter
	private boolean transformarAlerta;
	
	@Getter
	@Setter
	private boolean showSurveyG, mostrarEncuesta;
	
	public static final String[] MINERIA_EXPLORACION_INICIAL = new String[] { "21.02.02.01","21.02.02.02"};
	public static final String[] MINERIA_EXPLORACION_INICIAL_BENEFICIO = new String[] { "21.02.03.03","21.02.04.02","21.02.05.02","21.02.03.04" };
	public static final String[] MINERIA_EXPLORACION_RELAVES = new String[] { "21.02.07.01"};	
	public static final String[] MINERIA_EXPLORACION_RELAVESDEPOSITAR = new String[] { "21.02.07.02"};
	public static final String[] MINERIA_EXPLORACION_EXPLOTAR = new String[] { "21.02.06.01","21.02.06.02","21.02.04.01","21.02.06.03","21.02.08.01"};
	public static final String[] MINERIA_EXPLORACION_EXPLOTAR_TOTAL = new String[] { "21.02.04.01","21.02.05.01"};
	
	
	public String urlMapaCI() {
		return Constantes.getSuiaCertificadoInterseccion() + proyecto.getCodigo();
	}

	public boolean isMostrarHidrocarburos() {
		return isSector(TipoSector.TIPO_SECTOR_HIDROCARBUROS);
	}

	public boolean isMostrarMineria() {
		return isSector(TipoSector.TIPO_SECTOR_MINERIA);
	}

	public boolean isMostrarOtrosSectores() {
		return isSector(TipoSector.TIPO_SECTOR_OTROS);
	}

	public boolean isMostrarElectrico() {
		return isSector(TipoSector.TIPO_SECTOR_ELECTRICO);
	}
	
	public boolean isMostrarTelecomunicaciones() {
		return isSector(TipoSector.TIPO_SECTOR_TELECOMUNICACIONES);
	}
	
	public boolean isMostrarSectorSaneamiento() {
		return isSector(TipoSector.TIPO_SECTOR_SANEAMIENTO);
	}

	public boolean isMostrarSaneamiento() {
		boolean result = isCatalogoCategoriaCodigoInicia(RegistroProyectoSaneamientoController.CATEGORIAS_SANEAMIENTO);
		return result;
	}

	public boolean isMineriaArtesanalOLibreAprovechamiento() {
		boolean result = isCatalogoCategoriaCodigoInicia(RegistroProyectoMineriaController.MINERIA_ARTESANAL_O_LIBRE_APROVECHAMIENTO);
		return result;
	}

	private boolean isSector(int sectorId) {
		return proyecto.getTipoSector().getId().intValue() == sectorId;
	}

	public boolean isCategoriaI() {
		return (proyecto.getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_I);
	}
	
	public boolean isCategoriaCoor() {
		return (proyecto.getFormasProyectos().size()>0);
	}

	public boolean isCategoriaII() {
		return proyecto.getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_II;
	}

	public boolean isCategoriaIII() {
		return proyecto.getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_III;
	}

    public boolean isHidrocarburos() {
        return proyecto.getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_HIDROCARBUROS;
    }

	public boolean isCategoriaIV() {
		return proyecto.getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_IV;
	}
	
	@Getter
	@Setter
	public List<ProyectoCamaronera>listProyectoCamaroneras= new ArrayList<ProyectoCamaronera>();
	
	@Getter
	@Setter
	public boolean registroCamaroneras;

	public String getUrlSector() {
		if (isMostrarHidrocarburos())
			return "verProyectoHidrocarburosDatos.xhtml";
		if (isMostrarMineria())
			return "verProyectoMineriaDatos.xhtml";
		if (isMostrarOtrosSectores())
			return "verProyectoOtrosSectoresDatos.xhtml";
		if (isMostrarSectorSaneamiento())
			return "verProyectoSaneamientoDatos.xhtml";
		if (isMostrarTelecomunicaciones())
			return "verProyectoTelecomunicacionesDatos.xhtml";
		return "verProyectoElectricoDatos.xhtml";
	}

	@Override
	public Map<String, Object> getParametrosReporte() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("proyecto", proyecto);
		return params;
	}

	public String getReporteUrl() {
		if (proyecto.getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_HIDROCARBUROS)
			return "prevencion/verRegistroProyectoHidrocarburos";
		else if (proyecto.getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_OTROS)
			return "prevencion/verRegistroProyectoOtrosSectores";
		else
			return "prevencion/verRegistroProyectoMineria";
	}

	public String getReporteSubReportes() {
		if (proyecto.getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_HIDROCARBUROS)
			return "subReporte,subReporteBloques,subReporteSistemaReferencias,subReporteElementosCondicionales,subReporte1,subReporte2,subReporteListaFormasProyecto";
		else if (proyecto.getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_OTROS)
			return "subReporteListaUbicacionesRutas,subReporte,subReporteSistemaReferencias,subReporte1,subReporte2,subReporteListaFormasProyecto";
		else
			return "subReporte,subReporteSistemaReferencias,subReporteListaConcesionesMineras,subReporte1,subReporte2,subReporteListaFormasProyecto";
	}

	public String getReporteSubReportesUrl() {
		if (proyecto.getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_HIDROCARBUROS)
			return "prevencion/verRegistroProyecto_listaUbicaciones,prevencion/verRegistroProyecto_listaBloques,prevencion/verRegistroProyecto_listaCoordenadas,prevencion/verRegistroProyectoHidrocarburos_elementosCondicionales,prevencion/verRegistroProyectoOtrosSectores_subreporte1,prevencion/verRegistroProyectoOtrosSectores_subreporte2,prevencion/verRegistroProyectoHidrocarburos_listaFormasProyectos";
		else if (proyecto.getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_OTROS)
			return "prevencion/verRegistroProyecto_listaUbicacionesRutas,prevencion/verRegistroProyecto_listaUbicaciones,prevencion/verRegistroProyecto_listaCoordenadas,prevencion/verRegistroProyectoOtrosSectores_subreporte1,prevencion/verRegistroProyectoOtrosSectores_subreporte2,prevencion/verRegistroProyectoHidrocarburos_listaFormasProyectos";
		else
			return "prevencion/verRegistroProyecto_listaUbicaciones,prevencion/verRegistroProyecto_listaCoordenadas,prevencion/verRegistroProyectoMineria_listaConcesionesMineras,prevencion/verRegistroProyectoOtrosSectores_subreporte1,prevencion/verRegistroProyectoOtrosSectores_subreporte2,prevencion/verRegistroProyectoHidrocarburos_listaFormasProyectos";
	}

	public boolean isEstudioExPost() {
		return proyecto.getTipoEstudio() != null && proyecto.getTipoEstudio().getId() == TipoEstudio.ESTUDIO_EX_POST;
	}
	
	public String isPlayasBahias() {
		if(proyecto.getProyectoPlayasBahias()!=null && proyecto.getProyectoPlayasBahias())
		return "Si";
		return "No";
	}
	
	public String isTierrasAltas() {
		if (proyecto.getProyectoTierrasAltas()!=null && proyecto.getProyectoTierrasAltas())
			return "Si";
		return "No";
	}

	public boolean isEstudioExAnte() {
		return proyecto.getTipoEstudio() != null && proyecto.getTipoEstudio().getId() == TipoEstudio.ESTUDIO_EX_ANTE;
	}

	public boolean isEmisionInclusionAmbiental() {
		return proyecto.getTipoEstudio() != null
				&& proyecto.getTipoEstudio().getId() == TipoEstudio.ESTUDIO_EMISION_INCLUSION_AMBIENTAL;
	}

	public boolean isShowModalCertificadoIntercepcion() {
		if (showModalCertificadoIntercepcion) {
			JsfUtil.addCallbackParam("showCertificadoIntercepcion");
		}
		return showModalCertificadoIntercepcion;
	}

	public boolean isMostrarPescaPlantados() {
		boolean result = isCatalogoCategoriaCodigoInicia(RegistroProyectoOtrosSectoresController.CATEGORIAS_PESCA_PLANTADOS);
		return result;
	}

	public boolean isFaseMostrarBloque() {
		if (isCategoriaI())
			return false;
		return isCatalogoCategoriaIdPresente(RegistroProyectoHidrocarburosController.FASES_BLOQUE);
	}

	public boolean isFaseMostrarRefineria() {
		return isCatalogoCategoriaIdPresente(RegistroProyectoHidrocarburosController.FASES_REFINERIA);
	}

	public boolean isFaseMostrarTransporte() {
		return isCatalogoCategoriaIdPresente(RegistroProyectoHidrocarburosController.FASES_TRANSPORTE);
	}

	public boolean isFaseMostrarComercio() {
		return isCatalogoCategoriaIdPresente(RegistroProyectoHidrocarburosController.FASES_COMERCIO);
	}

	private boolean isCatalogoCategoriaIdPresente(int[] values) {
		try {
			if (proyecto.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)) {
				int id = proyecto.getCatalogoCategoria().getFase().getId();
				return Arrays.binarySearch(values, id) >= 0;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isCatalogoCategoriaCodigoInicia(String[] values) {
		try {
			String code = proyecto.getCatalogoCategoria().getCodigo();
			for (String string : values) {
				if (code.startsWith(string))
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isShowModalAceptarResponsabilidad() {
		if (showModalAceptarResponsabilidad) {
			JsfUtil.addCallbackParam("showModalAceptarResponsabilidad");
		}
		return showModalAceptarResponsabilidad;
	}

	protected String obtenerSubsectorCatalogoCategoriaSistema() {
		if (proyecto.getCatalogoCategoria() != null && proyecto.getCatalogoCategoria().getTipoSubsector() != null
				&& proyecto.getCatalogoCategoria().getTipoSubsector().getCodigo() != null)
			return proyecto.getCatalogoCategoria().getTipoSubsector().getCodigo();
		return "";
	}

	public boolean isGestionaOGeneraResiduosSolidos() {
		String codigo = obtenerSubsectorCatalogoCategoriaSistema();
		boolean result = TipoSubsector.CODIGO_RESIDUOS_SOLIDOS.equals(codigo);
		return result;
	}

	public boolean isMineriaLibreAprovechamientoProyectoExPost() {
		boolean result = isCatalogoCategoriaCodigoInicia(RegistroProyectoMineriaController.MINERIA_LIBRE_APROVECHAMIENTO)
				&& isEstudioExPost();
		return result;
	}

	public String getLabelCodigoMineroMineriaAreaConcesionada() {
		String label = "Código del minero artesanal";
		if (isCatalogoCategoriaCodigoInicia(RegistroProyectoMineriaController.MINERIA_ARTESANAL))
			label = "Código de la labor de minería artesanal";
		else if (isCatalogoCategoriaCodigoInicia(RegistroProyectoMineriaController.MINERIA_LIBRE_APROVECHAMIENTO)
				&& isEstudioExPost())
			label = "Código para libre aprovechamiento";
		return label;
	}

	public String getLabelCodigoMineroMineriaAreaLibre() {
		String label = "Código del minero artesanal";
		if (isCatalogoCategoriaCodigoInicia(RegistroProyectoMineriaController.MINERIA_ARTESANAL))
			label = "Código de la labor de minería artesanal";
		else if (isCatalogoCategoriaCodigoInicia(RegistroProyectoMineriaController.MINERIA_LIBRE_APROVECHAMIENTO)
				&& isEstudioExAnte())
			label = "Código del área para libre aprovechamiento(.pdf)";
		return label;
	}

	public String getLabelRegistroMineroArtesanalMRNNRMineriaAreaConcesionada() {
		String label = "Registro de minero artesanal MRNNR(.pdf)";
		if (isCatalogoCategoriaCodigoInicia(RegistroProyectoMineriaController.MINERIA_LIBRE_APROVECHAMIENTO)
				&& isEstudioExPost())
			label = "Autorización para libre aprovechamiento Ministerio de Minas(.pdf)";
		return label;
	}

	public String getLabelRegistroMineroArtesanalMRNNRMineriaAreaLibre() {
		String label = "Registro de minero artesanal MRNNR(.pdf)";
		if (isCatalogoCategoriaCodigoInicia(RegistroProyectoMineriaController.MINERIA_LIBRE_APROVECHAMIENTO)
				&& isEstudioExAnte())
			label = "Autorización para libre aprovechamiento Ministerio de Minería(.pdf)";
		return label;
	}

	public String getLabelProponente() {
        String label = proyecto.getUsuario().getPersona().getNombre();
        try {
            Organizacion organizacion = organizacionFacade.buscarPorPersona(proyecto.getUsuario().getPersona(), proyecto.getUsuario().getNombre());
            if (organizacion != null) {
                return organizacion.getNombre();
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        return label;
    }
	
	public List<SistemasIntegrales> listaSistemasIntgrales() {
		SistemasIntegrales sistemasIntegrales = new SistemasIntegrales();
		if (proyecto.getCodigo() != null) {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("p_proyecto", proyecto);

			List<SistemasIntegrales> lista = crudServiceBean
					.findByNamedQueryGeneric(
							sistemasIntegrales.GET_SISTEMAS_INTEGRALES_PROYECTO,
							parametros);

			if (lista.size() > 0) {
				codigoSistInt = lista.get(0).getCodigo();
				return lista;
			}
		}
		return null;
	}

	public boolean verListaSistInt() {
		boolean aux = false;
		try {
			if (proyecto.getCatalogoCategoria().getTipoLicenciamiento().getId() == 3
					|| proyecto.getCatalogoCategoria().getTipoLicenciamiento()
							.getId() == 4) {
				if (listaSistemasIntgrales().size() > 0) {
					tieneSistInt = "Si";
					tablaTieneSistInt = true;
					aux = true;
				}
			} else {
				aux = false;
			}
		} catch (Exception e) {
			if (proyecto.getCatalogoCategoria().getTipoLicenciamiento().getId() == 3
					|| proyecto.getCatalogoCategoria().getTipoLicenciamiento()
							.getId() == 4) {
				tieneSistInt = "No";
				tablaTieneSistInt = false;
				aux = true;
			} else {
				aux = false;
			}
		}
		return aux;
	}
	
	
	/****** módulo encuesta ******/	
	// metodo para crear url de la encuesta
	public String urlLinkSurvey() {
		String url = surveyLink;
		String usuarioUrl = proyecto.getUsuario().getNombre();
		String proyectoUrl = proyecto.getCodigo();
		String appUlr = "suia";
		String tipoPerUrl = (organizacion!=null)?"juridico":"natural";
		String tipoUsr = "externo";
		url = url + "/faces/index.xhtml?" 
				+ "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl 
				+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
		System.out.println("enlace>>>" + url);
		return url;
	}
	
	// método para mostrar el enlace de la encuesta
	public boolean showSurvey() {
		// validación para Certificado Ambiental - proyecto finalizado
		if(proyecto.isFinalizado()) {
			// validar que no sea Ente Acreditado arty_id=3
			if(proyecto.getAreaResponsable().getTipoArea().getId() != 3) {
				// si el proyecto es Categoría I
				if(proyecto.getCatalogoCategoria().getCategoria().getDescripcion().equals("Categoría I") && (listaCertificadoAmbiental())){
					// si se respondió la encuesta 
					if(surveyResponseFacade.findByProject(proyecto.getCodigo())) {
						return false;
					} else {	// no se respondió la encuesta
						return true;
					}
				}
			}
		}
		//showSurveyD = false;
		return false;
	}
	/****** fin módulo encuesta ******/
	
	
//	public String urlLinkReport() {
//		String url =reportLink; 
//		String areaId=JsfUtil.getLoggedUser().getArea().getId().toString();
//		String nombreUsuario= JsfUtil.getLoggedUser().getPersona().getNombre();
//		
//		url=url+"area="+areaId+"&usuario="+nombreUsuario;
//		return url;
//	}
	
	public String getlabelMaterialesEtiquetas() {
		return isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_INICIAL) ? "Material a explorar"
				:isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_INICIAL_BENEFICIO) ?"Material a beneficiar"
						:isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_RELAVES)? "Material Transportado" 
								:isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_EXPLOTAR)? "Material a explotar" 
										:isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_EXPLOTAR_TOTAL)? "Material a explotar" 
												:isCatalogoCategoriaCodigoInicia(MINERIA_EXPLORACION_RELAVESDEPOSITAR)?"Material a Depositar":"Material a explorar/explotar";
	}
	public boolean isCatalogoCategoriaCodigoIniciaEtiquetas(String[] values) {		
		try {
			String code =proyecto.getCatalogoCategoria().getCodigo() ;
			for (String string : values) {
				if (code.compareTo(string)==0)
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isFinanciadoEstado() {
		boolean result =false;	
		if (proyecto.getCatalogoCategoria().getActividadFinanciadaEstado()!=null){
			if (proyecto.getCatalogoCategoria().getActividadFinanciadaEstado()){
				result=true;						
			}
		}
		return result;
	}
	
	 @EJB
	    private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;
	 
	 public boolean viabilidarCertificadoAmbiental() {
		 return requisitosPreviosFacade.requiereRequisitosPrevios(proyecto); 
	}
	 
	 public boolean listaCertificadoAmbiental() {
		 List<CertificadoRegistroAmbiental>listaDoc=categoria1Facade.getCertificadoRegistroAmbientalPorIdProyecto(proyecto.getId());
		 if (listaDoc!=null){	
			 if (listaDoc.size()>0){
		 }
			 return true;
		 }
		 return false;
	}	 
	 
	@EJB
	private InformeInspeccionForestalCAFacade informeForestalFacade;
		
	private CertificadoAmbientalInformeForestal informeForestal=null;
	
	private boolean informeForestalBuscar=false;
	
	/**
	 * Buscar Informe forestal 
	 * para mostrar el valor de campo de cobertura vegetal 
	 * en Certificado Ambiental
	 * @return
	 */
	public CertificadoAmbientalInformeForestal getInformeForestal()
	{
		if(!informeForestalBuscar){
			if(proyectosBean.getProyecto().getId()!=null)
				informeForestal = informeForestalFacade.getInformePorIdProyecto(proyectosBean.getProyecto().getId());
			informeForestalBuscar=true;
		}
		
		return informeForestal;
	}
	 
	
	@Setter
	@Getter
	private boolean showSurveyRA = false;
	
	public void showDialogSurveyRA() {
		showSurveyRA = true;
	}
	private static final String COMPLETADA = "Completada";
	
	 @EJB
	    private CatalogoCategoriasFlujoFacade catalogoCategoriasFlujoFacade;
	 @Getter
	    @Setter
	    private List<CategoriaFlujo> flujos;
	 
	// método para mostrar el enlace de la encuesta	
		public boolean showLinkSurveyRA() throws Exception {
			boolean completado=false;
			if(proyecto.getAreaResponsable().getTipoArea().getId() != 3 && proyecto.getCatalogoCategoria().getCategoria().getDescripcion().equals("Categoría II")) {
				setFlujos(catalogoCategoriasFlujoFacade.obtenerFlujosDeProyectoPorCategoria(proyecto,
						Constantes.ID_PROYECTO, JsfUtil.getLoggedUser()));
						
				for (CategoriaFlujo cf : getFlujos()) {
	                if (cf.getFlujo().getNombreFlujo().equals("Registro ambiental v2") && (cf.getFlujo().getEstadoProceso().equals("Completado") || COMPLETADA.equals(cf.getFlujo().getEstadoProceso()))) {
	                    completado=true;
	                }


	            }
				
				if(proyecto.getCatalogoCategoria().getCategoria().getDescripcion().equals("Categoría II") && proyecto.getUsuario().getNombre().equals(JsfUtil.getLoggedUser().getNombre())
						&& completado){
					// si el proyecto tiene registrada la encuesta
					if(surveyResponseFacade.findByProject(proyecto.getCodigo())) {
						return false;
					} else {
						return true;
					}
				}
			}
			
			return false;
		}
		
		// metodo para crear url de la encuesta
				public String urlLinkSurveyRA() {
					
					String url = surveyLink;
					String usuarioUrl = proyecto.getUsuario().getNombre();
					String proyectoUrl = proyecto.getCodigo();
					String appUlr = "suia";
					String tipoPerUrl = (organizacion!=null)?"juridico":"natural";
					String tipoUsr = "externo";
					url = url + "/faces/index.xhtml?" 
							+ "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl 
							+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
					return url;
				}
			
				
		/****** fin módulo encuesta ******/
	
	public boolean mensajeBloqueadoEnte(){
		if(proyecto.getAreaResponsable()!=null)
			return false;
		return true;
	}
	
	/**
	 * scout drilling
	 */
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	public boolean tieneActualizacionSondeo()
	{
		boolean visible=false;
		try {
			PerforacionExplorativa perforacionExplorativa= null;
			perforacionExplorativa=fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyecto);
			if(perforacionExplorativa!=null)
			{
				if(perforacionExplorativa.getCodeUpdate()!=null)
					visible=true;
			}
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return visible;
	}	
		
	 public String getUrlContinuar() {
	    return JsfUtil.getStartPage();
	 }
	
	@Getter
	@Setter
	public List<Documento> certificadosInterseccionActualizados = new ArrayList<Documento>();
	
}
	
