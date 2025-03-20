package ec.gob.ambiente.control.retce.beans;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.InformeTecnicoGenerador;
import ec.gob.ambiente.retce.model.InformeTecnicoRetce;
import ec.gob.ambiente.retce.model.OficioPronunciamientoGenerador;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.InformeTecnicoRetceFacade;
import ec.gob.ambiente.retce.services.OficioRetceFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class InformeTecnicoOficioGeneradorBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(InformeTecnicoOficioGeneradorBean.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private InformeTecnicoRetceFacade informeGeneradorFacade;
	
	@EJB
	private OficioRetceFacade oficioRetceFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ObservacionesFacade observacionesFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	/*************************** Rcoa   *********************************/

	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	/********************************************************************/
	
	@Getter
	@Setter
	private InformeTecnicoRetce informe;
	
	@Getter
	@Setter
	private OficioPronunciamientoRetce oficioRetce;
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme, plantillaReporteOficio;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;
	
	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	private Area areaTramite;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrososRetce generadorDesechosRetce;
	
	@Setter
	@Getter
	private Integer numeroObservaciones;
	
	@Setter
	@Getter
	private Boolean requiereCorreccionesInforme, requiereCorreccionesOficio;
	
	@Setter
	@Getter
	private Usuario usuarioOperador, usuarioElabora, usuarioRevisa, usuarioAutoridad;
	
	@Setter
	@Getter
	private String tipoOficio, ciudadOficio;
	
	@Setter
	@Getter
	private TipoDocumentoSistema tipoDocumento;
	
	@Setter
	@Getter
	private Documento documentoOficioPronunciamiento;	
	
	private LinkedHashMap<String, List<ObservacionesFormularios>> observaciones;
	
	private List<ObservacionesFormularios> observacionesTramite;
	
	private Map<String, Object> variables;
	
	/*************************** Rcoa   *********************************/
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@PostConstruct
	public void init() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			String codigoGenerador = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			String usuarioProponente = (String) variables.get("usuario_operador");
			String usuarioTecnico = (String) variables.get("usuario_tecnico");
			String usuarioCoordinador=(String)variables.get("usuario_coordinador");
	
			if(variables.get("numero_observaciones") == null)
				numeroObservaciones=0;
			else
				numeroObservaciones = Integer.valueOf(variables.get("numero_observaciones").toString());
			
			generadorDesechosRetce = generadorDesechosPeligrososFacade.getRgdRetcePorCodigo(codigoGenerador);
			if(generadorDesechosRetce == null){
				return;
			}

			informacionProyecto = generadorDesechosRetce.getInformacionProyecto();
			areaTramite = areaFacade.getArea(generadorDesechosRetce.getIdArea());
			usuarioOperador=usuarioFacade.buscarUsuario(usuarioProponente);
			usuarioElabora=usuarioFacade.buscarUsuarioWithOutFilters(usuarioTecnico);
			usuarioRevisa=usuarioFacade.buscarUsuario(usuarioCoordinador);
			if(usuarioRevisa == null){
				usuarioRevisa=areaFacade.getCoordinadorPorArea(areaTramite);
				if(usuarioRevisa == null){
					LOG.error("No se encontro usuario coordinador en " + areaTramite.getAreaName());
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
					return;
				}
			}

			if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)){
				ciudadOficio = areaTramite.getAreaName().replace("OFICINA TÉCNICA ", "");  
				areaTramite = areaTramite.getArea();
			}else if(areaTramite.getUbicacionesGeografica() != null){
				ciudadOficio = areaTramite.getUbicacionesGeografica().getNombre();
			}

//			usuarioAutoridad = areaFacade.getDirectorProvincial(areaTramite);
			String roleKey = "role.retce.dp.autoridad.firmar.pronunciamiento";
			usuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), areaTramite).get(0);
			
			if(usuarioAutoridad==null){
				LOG.error("No se encontro usuario coordinador en " + areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}
			
			plantillaReporteInforme = informeGeneradorFacade.getPlantillaReporte(TipoDocumentoSistema.INFORME_TECNICO_GENERADOR);
			
			informe = informeGeneradorFacade.getInforme(generadorDesechosRetce.getCodigoGenerador(), TipoDocumentoSistema.INFORME_TECNICO_GENERADOR, numeroObservaciones + 1);
			
			if(!informacionProyecto.getEsEmisionFisica()){
				if(informacionProyecto.isEsProyectoRcoa())
					proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(informacionProyecto.getCodigo());
				else
					proyectoLicenciamientoAmbiental = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(informacionProyecto.getCodigo());
			}
			
			observacionesTramite = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
						generadorDesechosRetce.getId(), GeneradorDesechosPeligrososRetce.class.getSimpleName());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos InformeTecnicoOficioGeneradorRetce.");
		}
	}

	public void generarInforme(Boolean marcaAgua) {
		try {
			if (plantillaReporteInforme == null)
				plantillaReporteInforme = informeGeneradorFacade.getPlantillaReporte(TipoDocumentoSistema.INFORME_TECNICO_GENERADOR);

			informe = informeGeneradorFacade.getInforme(generadorDesechosRetce.getCodigoGenerador(), TipoDocumentoSistema.INFORME_TECNICO_GENERADOR, numeroObservaciones + 1);
			
			if (informe == null) {
				informe = new InformeTecnicoRetce();
				informe.setFechaInforme(new Date());
				informe.setObservaciones("");
				informe.setConclusiones("");
				informe.setRecomendaciones("");
				informe.setNumeroRevision(numeroObservaciones + 1);
				informe.setFinalizado(false);
				informe.setCodigoTramite(generadorDesechosRetce.getCodigoGenerador());
				informe.setIdTipoDocumento(TipoDocumentoSistema.INFORME_TECNICO_GENERADOR.getIdTipoDocumento());

				informeGeneradorFacade.guardar(informe, areaTramite, JsfUtil.getLoggedUser());
			}
			
			informe.setNombreFichero("InformeTecnico_" + getFileNameEscaped(informe.getNumeroInforme().replace("/", "-")) + ".pdf");
			informe.setNombreReporte("InformeTecnico.pdf");
			
			InformeTecnicoGenerador informeTecnicoGenerador = cargarDatosInforme();

			File informePdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporteInforme.getHtmlPlantilla(),
					informe.getNombreReporte(), true, informeTecnicoGenerador);
			
			File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(informePdf.getAbsolutePath());
			informe.setArchivoInforme(Files.readAllBytes(path));
			String reporteHtmlfinal = informe.getNombreFichero().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(informe.getArchivoInforme());
			file.close();
			informe.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informe.getNombreFichero()));

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(
					"Error al cargar el informe tecnico del registro de generador",
					e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	private InformeTecnicoGenerador cargarDatosInforme() throws ServiceException {
		observacionesTramite = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
				generadorDesechosRetce.getId(), GeneradorDesechosPeligrososRetce.class.getSimpleName());
		
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));

		String razonSocial = "";
		String representante = "";
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			razonSocial = usuarioOperador.getPersona().getNombre();
		} else {
			representante = usuarioOperador.getPersona().getNombre();
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			razonSocial = organizacion.getNombre();
		}
		
		//llenar todos los campos
		InformeTecnicoGenerador informeTecnicoGenerador = new InformeTecnicoGenerador();
		informeTecnicoGenerador.setEnteResponsable(areaTramite.getAreaName());
		if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT) || areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES)){
			informeTecnicoGenerador.setEnteResponsable("DIRECCIÓN ZONAL");
		}
		informeTecnicoGenerador.setInformeNumero(informe.getNumeroInforme());
		informeTecnicoGenerador.setRazonSocial(razonSocial);
		informeTecnicoGenerador.setRepresentante(representante);
		
		if(informacionProyecto.getEsEmisionFisica()){
			informeTecnicoGenerador.setProyecto(informacionProyecto.getNombreProyecto());
			informeTecnicoGenerador.setSector(informacionProyecto.getTipoSector().getNombre());
		} else {
			if(proyectoLicenciamientoAmbiental != null){
				informeTecnicoGenerador.setProyecto(proyectoLicenciamientoAmbiental.getNombre());
				informeTecnicoGenerador.setSector(proyectoLicenciamientoAmbiental.getTipoSector().getNombre());
			}
			if(proyectoLicenciaCoa != null){
				informeTecnicoGenerador.setProyecto(proyectoLicenciaCoa.getNombreProyecto());
				// obtengo el sector
				List<ProyectoLicenciaCuaCiuu>  listaactividades = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyectoLicenciaCoa);
				for (ProyectoLicenciaCuaCiuu objActividad : listaactividades) {
					if(objActividad.getPrimario()){
						TipoSector sector = objActividad.getCatalogoCIUU().getTipoSector();
						if(sector != null)
							informeTecnicoGenerador.setSector(sector.getNombre());
						break;
					}
				}
			}
			// si no corresponde ni a un proyecto rcoa o suia_iii busco la informacion del sector y nombre del proyecto en la tabla del retce
			if(proyectoLicenciamientoAmbiental == null && proyectoLicenciaCoa == null){
				informeTecnicoGenerador.setProyecto(informacionProyecto.getNombreProyecto());
				informeTecnicoGenerador.setSector(informacionProyecto.getTipoSector().getNombre());
			}
		}
		
		informeTecnicoGenerador.setAutorizacion(generadorDesechosRetce.getInformacionProyecto().getCodigo());
		informeTecnicoGenerador.setCodigoRgd(generadorDesechosRetce.getCodigoGeneradorDesechosPeligrosos());
		informeTecnicoGenerador.setAnioReporte(generadorDesechosRetce.getAnioDeclaracion().toString());
		informeTecnicoGenerador.setFechaTramite(formatoFecha.format(generadorDesechosRetce.getFechaTramite()));
		informeTecnicoGenerador.setFechaEvaluacion(formatoFecha.format(informe.getFechaInforme()));
		informeTecnicoGenerador.setEvaluador(usuarioElabora.getPersona().getNombre());
		informeTecnicoGenerador.setNumeroTramite(generadorDesechosRetce.getCodigoGenerador());
		
		if(numeroObservaciones > 0)
			informeTecnicoGenerador.setAntecedentesAdicionales(getAntecedentesAdicionales(razonSocial));
		else
			informeTecnicoGenerador.setAntecedentesAdicionales("");
		
		Integer nroIndiceHeader = 3;
		informeTecnicoGenerador.setObservaciones(getObservaciones(true));
		if(!informeTecnicoGenerador.getObservaciones().isEmpty()) {
			informeTecnicoGenerador.setHeaderObservaciones(nroIndiceHeader + ". Observaciones:<br />");
			informeTecnicoGenerador.setInitObservaciones("De la revisión de la información presentada por el operador a continuación se detallan las siguientes observaciones:<br />");
			nroIndiceHeader++;
		}
		informeTecnicoGenerador.setConclusiones(informe.getConclusiones());
		informeTecnicoGenerador.setHeaderConclusiones("<br />" + nroIndiceHeader + ". Conclusiones:");
		nroIndiceHeader++;
		informeTecnicoGenerador.setRecomendaciones(informe.getRecomendaciones());
		if(informe.getRecomendaciones() != null && !informe.getRecomendaciones().isEmpty()) {
			informeTecnicoGenerador.setHeaderRecomendaciones("<br />" + nroIndiceHeader + ". Recomendaciones:<br />");
			nroIndiceHeader++;
		}
		
		informeTecnicoGenerador.setHeaderElaborado(nroIndiceHeader + ". Elaborado y revisado por:");
		
		informeTecnicoGenerador.setTecnicoElabora(usuarioElabora.getPersona().getNombre());
		informeTecnicoGenerador.setCoordinador(usuarioRevisa.getPersona().getNombre());
		
		return informeTecnicoGenerador;
	}
	
	private String getAntecedentesAdicionales(String razonSocial) {
		Integer indice = 2;
		String extras = "";
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		List<OficioPronunciamientoRetce> oficiosObservacion = oficioRetceFacade.getOficiosObservacionSubsanados(generadorDesechosRetce.getCodigoGenerador());
		for (OficioPronunciamientoRetce oficio : oficiosObservacion) {
			extras += "<br /><br />";
			String[] parametros = {
					formatoFecha.format(oficio.getFechaEnvioCorrecciones()),
					razonSocial, oficio.getNumeroOficio(),
					formatoFecha.format(oficio.getFechaOficio()),
					generadorDesechosRetce.getAnioDeclaracion().toString() };
			extras += "2." + indice + ". " + getNotaResponsabilidadInformacionRegistroProyecto(parametros);
			indice++;
		}
		return extras;
	}
	
	private String getNotaResponsabilidadInformacionRegistroProyecto(String[] parametros) {
		return DocumentoPDFPlantillaHtml.getPlantillaConParametros("antecedentes_generador_retce",parametros);
	}
	
	private String getObservaciones(Boolean numeracion) throws ServiceException {
		observaciones = new LinkedHashMap<String, List<ObservacionesFormularios>>();
		
		if (observacionesTramite != null && !observacionesTramite.isEmpty()) {
			for (ObservacionesFormularios observacion : observacionesTramite) {
				if (!observacion.isObservacionCorregida()) {
					if (!observaciones.containsKey(observacion.getSeccionFormulario()))
						observaciones.put(observacion.getSeccionFormulario(), new ArrayList<ObservacionesFormularios>());
					observaciones.get(observacion.getSeccionFormulario()).add(observacion);
				}
			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		if (observaciones.isEmpty() && (informe.getObservaciones() == null || informe.getObservaciones().isEmpty()))
			return "";
		
		List<String> secciones = new ArrayList<>();
		secciones.add("GeneradorRETCE_Generación");
		secciones.add("GeneradorRETCE_Autogestión");
		secciones.add("GeneradorRETCE_Transporte");
		secciones.add("GeneradorRETCE_Transporte Medios propios");
		secciones.add("GeneradorRETCE_Transporte Gestor ambiental");
		secciones.add("GeneradorRETCE_Exportación");
		secciones.add("GeneradorRETCE_Eliminación");
		secciones.add("GeneradorRETCE_Disposición");
		
		Map<String, String> etiquetas = new HashMap<String, String>();
		etiquetas.put("Autogestión", "Gestión interna (autogestión)");
		etiquetas.put("Transporte", "Transporte fuera de la instalación");
		etiquetas.put("Eliminación", "Eliminación fuera de la instalación");
		etiquetas.put("Disposición", "Disposición final fuera de la instalación");
	        
	        
		
		Integer indice = 1;
		Integer subIndice = 0;
		Boolean tieneSubNivel = false;
		Boolean tieneSubNivelAnterior = null;
		String headerNivelPrincipal = null;
		
		if (informe.getObservaciones() != null && !informe.getObservaciones().isEmpty()){
			String observacionesGenerales = "";
			if(informe.getObservaciones().substring(0, 3).equals("<p>")){
				Integer total = informe.getObservaciones().length() - 6;
				observacionesGenerales = informe.getObservaciones().subSequence(3, total).toString();
			}
			
			stringBuilder.append("<strong>" + ((numeracion) ? ("3." + indice + ". ") : ""));
			stringBuilder.append("Observaciones generales:</strong>" + observacionesGenerales + "<br/><br/>");
			indice++;
		}
				
		for (String seccion : secciones) {
			List<ObservacionesFormularios> observacionesSeccion = observaciones.get(seccion);
			
			String[] partesSeccion = seccion.split("_");
			tieneSubNivel = partesSeccion[1].contains("Transporte");
			if(tieneSubNivelAnterior != null && tieneSubNivelAnterior && !tieneSubNivel && headerNivelPrincipal == null)
				indice++;
			tieneSubNivelAnterior = tieneSubNivel;
			String etiquetaObs = (etiquetas.containsKey(partesSeccion[1])) ? etiquetas.get(partesSeccion[1]) : partesSeccion[1];
			
			if(observacionesSeccion != null && observacionesSeccion.size() > 0){
				Integer nroObs = 1;
				
				if(!tieneSubNivel || (tieneSubNivel && subIndice == 0)) {
					stringBuilder.append("<strong>" + ((numeracion) ? ("3." + indice + ". ") : ""));
					stringBuilder.append(etiquetaObs + "</strong><br/>");
				} else {
					if(subIndice == 1) {
						stringBuilder.append(headerNivelPrincipal);
						headerNivelPrincipal = null;
					}
					
					String[] partesEtiqueta = etiquetaObs.split(" ", 2);
					stringBuilder.append("<strong>" + ((numeracion) ? ("3." + indice + "." + subIndice + ". ") : ""));	
					stringBuilder.append(partesEtiqueta[1] + "</strong><br/>");
				}
				
				for (ObservacionesFormularios observacion : observacionesSeccion) {
					stringBuilder.append(nroObs + ". " + observacion.getCampo() + ": " + observacion.getDescripcion() + "<br/>");
					
					nroObs++;
				}
				stringBuilder.append("<br/>");
				
				if(tieneSubNivel) {
					subIndice++;
				} else {
					indice++;
					tieneSubNivel = false;
					subIndice = 0;
				}
			} else if(tieneSubNivel && subIndice == 0) {
				headerNivelPrincipal = "<strong>" + ((numeracion) ? ("3." + indice + ". ") : "");
				headerNivelPrincipal += etiquetaObs + "</strong><br/>";
				subIndice++;
			}
		}
		
		if(stringBuilder.length() > 0)
			return stringBuilder.substring(0, stringBuilder.length() - 5).toString();
		else 
			return stringBuilder.toString();
	}
	
	public void getOficio() {
		if(informe != null && !informe.getEsReporteAprobacion()){
			tipoOficio = "Observacion";
			tipoDocumento = TipoDocumentoSistema.OFICIO_OBSERVACION_GENERADOR;
		} else {
			tipoOficio = "Aprobacion";
			tipoDocumento = TipoDocumentoSistema.OFICIO_APROBACION_GENERADOR;
		}
		
		oficioRetce = oficioRetceFacade.getOficioPorInforme(informe.getId());
	}
	
	public void generarOficio(Boolean marcaAgua) {
		try {
			if(informe != null && !informe.getEsReporteAprobacion()){
				tipoOficio = "Observacion";
				tipoDocumento = TipoDocumentoSistema.OFICIO_OBSERVACION_GENERADOR;
			} else {
				tipoOficio = "Aprobacion";
				tipoDocumento = TipoDocumentoSistema.OFICIO_APROBACION_GENERADOR;
			}
			
			plantillaReporteOficio = oficioRetceFacade.getPlantillaReporte(tipoDocumento);
	
			if (oficioRetce == null)
				oficioRetce = oficioRetceFacade.getOficioPorInforme(informe.getId());
	
			if (oficioRetce == null) {
				oficioRetce = new OficioPronunciamientoRetce();
				oficioRetce.setFechaOficio(new Date());
				oficioRetce.setIdTipoDocumento(tipoDocumento.getIdTipoDocumento());
				oficioRetce.setIdInformeTecnico(informe.getId());
				oficioRetce.setNumeroRevision(numeroObservaciones + 1);
				oficioRetce.setCodigoTramite(generadorDesechosRetce.getCodigoGenerador());
	
				oficioRetceFacade.guardar(oficioRetce, areaTramite, JsfUtil.getLoggedUser());
			} else {
				oficioRetce.setIdTipoDocumento(tipoDocumento.getIdTipoDocumento());
				oficioRetceFacade.guardar(oficioRetce, areaTramite, JsfUtil.getLoggedUser());
			}
			
			OficioPronunciamientoGenerador oficioGenerador = cargarDatosOficio();
	
			oficioRetce.setNombreFichero("Oficio" + tipoOficio + "_"
					+ getFileNameEscaped(oficioRetce.getNumeroOficio()).replace("/", "-") + ".pdf");
			oficioRetce.setNombreReporte("Oficio" + tipoOficio + ".pdf");
	
			File oficioPdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporteOficio.getHtmlPlantilla(),
					oficioRetce.getNombreReporte(), true, oficioGenerador);
			
			File oficioPdf = JsfUtil.fileMarcaAgua(oficioPdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);
	
			Path path = Paths.get(oficioPdf.getAbsolutePath());
			oficioRetce.setArchivoOficio(Files.readAllBytes(path));
			oficioRetce.setOficioPath(oficioPdf.getAbsolutePath());
			File archivoFinal = new File(
					JsfUtil.devolverPathReportesHtml(oficioRetce.getNombreFichero()));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(oficioRetce.getArchivoOficio());
			file.close();
			oficioRetce.setOficioPath(JsfUtil.devolverContexto("/reportesHtml/"
					+ oficioRetce.getNombreFichero()));
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(
					"Error al cargar el informe tecnico del registro de generador",
					e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	private OficioPronunciamientoGenerador cargarDatosOficio() throws ServiceException {
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));

		String razonSocial = "";

		Usuario usuarioProyecto = usuarioFacade
				.buscarUsuarioCompleta(informacionProyecto.getUsuarioCreacion());
		if (usuarioProyecto.getPersona().getOrganizaciones().size() == 0) {
			razonSocial = usuarioProyecto.getPersona().getNombre();
		} else {
//			representante = usuarioProyecto.getPersona().getNombre();
			Organizacion organizacion = organizacionFacade
					.buscarPorRuc(usuarioProyecto.getNombre());
			razonSocial = organizacion.getNombre();
		}

		//llenar todos los campos
		OficioPronunciamientoGenerador oficioGenerador = new OficioPronunciamientoGenerador();
		oficioGenerador.setNumeroOficio(oficioRetce.getNumeroOficio());
		oficioGenerador.setCiudadOficio(ciudadOficio);
		oficioGenerador.setFechaOficio(formatoFecha.format(oficioRetce.getFechaOficio()));
		oficioGenerador.setProponente(razonSocial);
		oficioGenerador.setCodigoRgd(generadorDesechosRetce.getCodigoGeneradorDesechosPeligrosos());
		oficioGenerador.setFechaTramite(formatoFecha.format(generadorDesechosRetce.getFechaTramite()));
		oficioGenerador.setNumeroTramite(generadorDesechosRetce.getCodigoGenerador());
		oficioGenerador.setNroInforme(informe.getNumeroInforme());
		oficioGenerador.setFechaInforme(formatoFecha.format(informe.getFechaInforme()));
		oficioGenerador.setObservaciones(getObservaciones(false));
		oficioGenerador.setAutoridad(usuarioAutoridad.getPersona().getNombre());
		oficioGenerador.setCargoAutoridad(JsfUtil.obtenerCargoUsuario(usuarioAutoridad));

		return oficioGenerador;
	}
	
	private String getFileNameEscaped(String file) {
		String result = file.replaceAll("Ñ", "N");
		result = result.replaceAll("Á", "A");
		result = result.replaceAll("É", "E");
		result = result.replaceAll("Í", "I");
		result = result.replaceAll("Ó", "O");
		result = result.replaceAll("Ú", "U");
		return result;
	}
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}
	
	public boolean validarExistenciaObservacionesProponente() {
		boolean existenObservacionesProponente = true;

		if (observacionesTramite == null || observacionesTramite.isEmpty())
			existenObservacionesProponente = false;

		return existenObservacionesProponente;
	}
	
	public void guardarInforme() {		
//		informe.setEsReporteAprobacion(!validarExistenciaObservacionesProponente());
		informe.setFinalizado(false);
		
		informeGeneradorFacade.guardar(informe, areaTramite, JsfUtil.getLoggedUser());
	}
	
	public void guardarOficio() {
		oficioRetceFacade.guardar(oficioRetce, areaTramite, JsfUtil.getLoggedUser());
	}
	
	public void subirInforme() {
		try {
			generarInforme(false);
			
			Documento documentoInforme = new Documento();
			documentoInforme.setNombre("InformeTecnico_" +informe.getNumeroInforme() + ".pdf");
			documentoInforme.setContenidoDocumento(informe.getArchivoInforme());
			documentoInforme.setNombreTabla("InformeTecnicoRetceGenerador");
			documentoInforme.setIdTable(generadorDesechosRetce.getId());
			documentoInforme.setDescripcion("Informe de técnico RETCE Generador");
			documentoInforme.setMime("application/pdf");
			documentoInforme.setExtesion(".pdf");
	
			documentoInforme = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
					generadorDesechosRetce.getCodigoGenerador(),
					"INFORMES_OFICIOS", generadorDesechosRetce.getId().longValue(), 
					documentoInforme,
					TipoDocumentoSistema.INFORME_TECNICO_GENERADOR,
					null);			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al guardar los documentos de informe y oficio retce generador.");
		}
	}
	
	public void subirOficio() {
		try {
			generarInforme(false);			
			guardarOficio();
			generarOficio(false);
			guardarDocumentoOficio();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al guardar los documentos de informe y oficio retce generador.");
		}
	}
	
	public void guardarDocumentoOficio() throws ServiceException, CmisAlfrescoException {
		Documento documentoOficio = new Documento();
		documentoOficio.setNombre("Oficio" + tipoOficio + "_" + oficioRetce.getNumeroOficio() + ".pdf");
		documentoOficio.setContenidoDocumento(oficioRetce.getArchivoOficio());
		documentoOficio.setNombreTabla("OficioPronunciamientoRetceGenerador");
		documentoOficio.setIdTable(generadorDesechosRetce.getId());
		documentoOficio.setDescripcion("Oficio " + tipoOficio +" RETCE Generador");
		documentoOficio.setMime("application/pdf");
		documentoOficio.setExtesion(".pdf");

		documentoOficioPronunciamiento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
				generadorDesechosRetce.getCodigoGenerador(),
				"INFORMES_OFICIOS", generadorDesechosRetce.getId().longValue(), 
				documentoOficio,
				tipoDocumento,
				null);
	}
	
	public void generarDocumentoOficio() {
		generarOficio(false);
		
		Documento documentoOficio = new Documento();
		documentoOficio.setNombre("Oficio" + tipoOficio + "_" + oficioRetce.getNumeroOficio() + ".pdf");
		documentoOficio.setContenidoDocumento(oficioRetce.getArchivoOficio());
		documentoOficio.setNombreTabla("OficioPronunciamientoRetceGenerador");
		documentoOficio.setIdTable(generadorDesechosRetce.getId());
		documentoOficio.setDescripcion("Oficio " + tipoOficio +" RETCE Generador");
		documentoOficio.setMime("application/pdf");
		documentoOficio.setExtesion(".pdf");

		documentoOficioPronunciamiento = documentoOficio;
	}
}
