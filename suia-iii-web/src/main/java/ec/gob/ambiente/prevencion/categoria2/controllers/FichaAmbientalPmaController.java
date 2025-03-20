package ec.gob.ambiente.prevencion.categoria2.controllers;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.categoria2.bean.CatalogoGeneralPmaBean;
import ec.gob.ambiente.prevencion.categoria2.bean.CronogramaValoradoBean;
import ec.gob.ambiente.prevencion.categoria2.bean.DescripcionProyectoPmaBean;
import ec.gob.ambiente.prevencion.categoria2.bean.FichaAmbientalPmaBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.GenerarNotificacionesAplicativoController;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.autoridadambiental.service.AutoridadAmbientalFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectodesechopeligroso.facade.ProyectoDesechoPeligrosoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.ReporteLicenciaAmbientalCategoriaII;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class FichaAmbientalPmaController implements Serializable {
	/**
     *
     */
	private static final long serialVersionUID = -509930215520733156L;
	private static final String[] LIBRE_APROVECHAMIENTO = new String[] { "21.02.06.02" };
	private static final String[] MINERIA_ARTESANAL = new String[] { "21.02.01.01" };
	private static final String[] MINERIA_EXPLORACION_INICIAL = new String[] { "21.02.02.01", "21.02.03.06"};
	private static final String[] MINERIA_PERFORACION_EXPLORATIVA = new String[] { "21.02.03.05","21.02.04.03","21.02.05.03","21.02.02.03"};

	Logger LOG = Logger.getLogger(FichaAmbientalPmaController.class);

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private CategoriaIIFacade categoriaIIFacade;

	@EJB
	private ProcesoFacade procesoFacade;
    
    @EJB
	private ContactoFacade contactoFacade;

	@EJB
	private ProyectoDesechoPeligrosoFacade proyectoDesechoPeligrosoFacade;

	/**
	 * Nombre:SUIA Descripción: instancias del usuario facade.
	 * ParametrosIngreso: PArametrosSalida: Fecha:16/08/2015
	 */

	@EJB
	private UsuarioFacade usuarioFacade;
	/**
	 * FIN instancias del usuario facade.
	 */
	
	@EJB
	private InformeProvincialGADFacade informeProvincialGADFacade;

	@Setter
	@ManagedProperty(value = "#{catalogoGeneralPmaBean}")
	private CatalogoGeneralPmaBean catalogoGeneralPmaBean;

	@Setter
	@ManagedProperty(value = "#{fichaAmbientalPmaBean}")
	private FichaAmbientalPmaBean fichaAmbientalPmaBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{descripcionProyectoPmaBean}")
	private DescripcionProyectoPmaBean descripcionProyectoPmaBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{cronogramaValoradoBean}")
	private CronogramaValoradoBean cronogramaValoradoBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{adjuntosCat2Controller}")
	private AdjuntosCat2Controller adjuntosCat2Controller;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;

	public File subirLicenciaAmbiental(Boolean subir, Integer i) {

		try {
			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(fichaAmbientalPmaBean
							.getLoginBean().getUsuario(), fichaAmbientalPmaBean
							.getBandejaTareasBean().getTarea()
							.getProcessInstanceId());

			Integer idProyecto = Integer.parseInt((String) variables
					.get(Constantes.ID_PROYECTO));
			try {				
				if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null) {
					ProyectoLicenciaCoa proyectoActivo =proyectoLicenciaCoaFacade.buscarProyectoPorId((Integer.valueOf(idProyecto)));
					long idTask = fichaAmbientalPmaBean.getBandejaTareasBean()
							.getTarea().getTaskId();
					long idProcessInstance = fichaAmbientalPmaBean
							.getBandejaTareasBean().getTarea()

							.getProcessInstanceId();
					// Generar Archivo temporal para subir al Alfresco
					File archivoTemporal = generarLicenciaAmbiental(proyectoActivo,i);
					try { // Subir archivo al Alfresco
						if (subir) {
							categoriaIIFacade
							.ingresarDocumentoCoa(
									archivoTemporal,
									idProyecto,
									proyectosBean.getProyectoRcoa(),
									idProcessInstance,
									idTask,
									TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL,
									"Resolución del Registro Ambiental");
						}
						return archivoTemporal;
					} catch (Exception e) {
						LOG.error(
								"Error al intentar subir licencia Ambiental al Alfresco, Categoría II",
								e);
						JsfUtil.addMessageError("Error al realizar la operación.");
					}

				}
				else
				{
					ProyectoLicenciamientoAmbiental proyectoActivo = proyectoLicenciamientoAmbientalFacade
							.cargarProyectoFullPorId(idProyecto);
					long idTask = fichaAmbientalPmaBean.getBandejaTareasBean()
							.getTarea().getTaskId();
					long idProcessInstance = fichaAmbientalPmaBean
							.getBandejaTareasBean().getTarea()

							.getProcessInstanceId();
					// Generar Archivo temporal para subir al Alfresco
					File archivoTemporal = generarLicenciaAmbiental(proyectoActivo,i);

					try { // Subir archivo al Alfresco
						if (subir) {
							categoriaIIFacade
							.ingresarDocumentoCategoriaII(
									archivoTemporal,
									idProyecto,
									proyectoActivo.getCodigo(),
									idProcessInstance,
									idTask,
									TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL,
									"Resolución del Registro Ambiental");
						}
						return archivoTemporal;
					} catch (Exception e) {
						LOG.error(
								"Error al intentar subir licencia Ambiental al Alfresco, Categoría II",
								e);
						JsfUtil.addMessageError("Error al realizar la operación.");
					}
				}
			} catch (Exception e) {
				LOG.error("Error cargando el proyecto", e);
				JsfUtil.addMessageError("Error cargando los datos del proyecto.");
			}
		} catch (JbpmException e) {
			LOG.error(
					"Error al recuperar variables y generar Licencia Ambiental, Categoría II",
					e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}

		return null;
	}

	public void guardar() {		
		
		fichaAmbientalPmaBean.getFicha().setProyectoLicenciamientoAmbiental(fichaAmbientalPmaBean.getProyecto());
		boolean selFases = false;

		for (CatalogoCategoriaFase fase : catalogoGeneralPmaBean.getTiposActividades()) {
			if (fase.isSeleccionado()) {
				selFases = true;
				break;
			}
		}

		if (!selFases) {
			JsfUtil.addMessageError("El campo 'Estado del proyecto, obra o actividad' es requerido.");
		} else {
			try {
				
//				if(fichaAmbientalPmaBean.isGuardarHistorial()){
					
					fichaAmbientalPmaFacade.guardarFichaAmbientalPmaHistorico(fichaAmbientalPmaBean.getFicha(),
							catalogoGeneralPmaBean.getTiposActividades(),
							catalogoGeneralPmaBean.getTiposInfraestructurasSeleccionados(),
							catalogoGeneralPmaBean.getTiposPredioSeleccionados(),
							catalogoGeneralPmaBean.getTiposPedioSecundariosSeleccionados(), 
							fichaAmbientalPmaBean.getProyectosBean().getProyecto().getId());
					
					// ELIMINA DATOS SI SE MODIFICA ALGUNA FASE PREVIAMENTE GUARDADA
					fichaAmbientalPmaFacade.eliminarAsociadoFaseHistorico(fichaAmbientalPmaBean.getListaActividadesDeseleccionadas());
					
					JsfUtil.redirectTo("/prevencion/categoria2/v2/fichaAmbiental/proyectoObra.jsf");
//				}else{
//					fichaAmbientalPmaFacade.guardarFichaAmbientalPma(
//							fichaAmbientalPmaBean.getFicha(),
//							catalogoGeneralPmaBean.getTiposActividades(),
//							catalogoGeneralPmaBean.getTiposInfraestructurasSeleccionados(),
//							catalogoGeneralPmaBean.getTiposPredioSeleccionados(),
//							catalogoGeneralPmaBean.getTiposPedioSecundariosSeleccionados());
//
//					// ELIMINA DATOS SI SE MODIFICA ALGUNA FASE PREVIAMENTE GUARDADA
//					fichaAmbientalPmaFacade.eliminarAsociadoFase(fichaAmbientalPmaBean.getListaActividadesDeseleccionadas());
//				}			
								
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			} catch (Exception e) {
				LOG.error("Error al guardar ficha", e);
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			}
		}
	}

	public void guardarResumenEjecutivo() {
		fichaAmbientalPmaBean.getFicha().setProyectoLicenciamientoAmbiental(
				fichaAmbientalPmaBean.getProyecto());
		try {
			fichaAmbientalPmaFacade.guardarFichaAmbientalPma(
					fichaAmbientalPmaBean.getFicha(), catalogoGeneralPmaBean
							.getTiposActividades(), catalogoGeneralPmaBean
							.getTiposInfraestructurasSeleccionados(),
					catalogoGeneralPmaBean.getTiposPredioSeleccionados(),
					catalogoGeneralPmaBean
							.getTiposPedioSecundariosSeleccionados());

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			JsfUtil.redirectTo("/prevencion/categoria2/fichaAmbiental/proyectoObra.jsf");
		} catch (Exception e) {
			LOG.error("Error al guardar ficha", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	private File generarLicenciaAmbiental(
			ProyectoLicenciamientoAmbiental proyectoActivo, Integer marcaAgua) {

		File archivoTemporal = null;
		File archivoLicenciaTemporal = null;
		File archivoAnexoTemporal = null;
		List<File> listaFiles = new ArrayList<File>();

		Boolean mineria = isMineriaArtesanal(proyectoActivo);
		Boolean libreAprovechamiento = isLibreAprovechamiento(proyectoActivo);
		Boolean exploracionInicial = isExploracionInicial(proyectoActivo);
		Boolean perforacionExplorativa = isPerforacionExplorativa(proyectoActivo);
		Boolean comercializacionHidrocarburos = proyectoActivo.getCatalogoCategoria().getCodigo().equals("21.01.07.03"); //false; 
		Persona persona = null;
		/**
		 * Nombre:SUIA Descripción: Validación para obtener el cargo de la
		 * persona que firmará el documentos registro ambiental.
		 * ParametrosIngreso: PArametrosSalida: Fecha:16/08/2015
		 */

		String cargo = null;
		if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {
			cargo = "AUTORIDAD AMBIENTAL PROVINCIAL";

		} else {
			cargo = "SUBSECRETARIO DE CALIDAD AMBIENTAL";
		}
		/**
		 * FIN Validación para obtener el cargo de la persona que firmará el
		 * documentos registro ambiental.
		 */

		byte[] firma = null;

		Usuario usuarioSecretario = null;
		String reporteFinal = null;
		//
		try {

			/**
			 * Nombre:SUIA Descripción: Para obterner el anexo de las
			 * coordenadas de acuerdo al área. ParametrosIngreso:
			 * PArametrosSalida: Fecha:16/08/2015
			 */
			if (proyectoActivo.getAreaResponsable().getTipoArea().getId()
					.equals(3)) {
				cargo = proyectoActivo.getAreaResponsable().getAreaName();
				if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado()
						.equals("MUNICIPIO")) {
					reporteFinal = "anexo_coordenadas_licencia_ambiental_categoria_II_Completo_entes_municipio";
				} else {
					reporteFinal = "anexo_coordenadas_licencia_ambiental_categoria_II_Completo_entes";
				}
			} else {
				usuarioSecretario = usuarioFacade.buscarUsuarioPorRol(cargo)
						.get(0);
				reporteFinal = "anexo_coordenadas_licencia_ambiental_categoria_II_Completo";
			}
			/**
			 * FIN Para obterner el anexo de las coordenadas de acuerdo al área
			 */

			Usuario usuarioSecreUsuario = categoriaIIFacade
					.getPersonaSubsecretarioCalidadAmbiental(proyectoActivo
							.getAreaResponsable());
			if (usuarioSecretario != null) {
				persona = usuarioSecretario.getPersona();
				if (Usuario.isUserInRole(usuarioSecretario,
						AutoridadAmbientalFacade.GAD_MUNICIPAL)) {
					cargo = AutoridadAmbientalFacade.GAD_MUNICIPAL;
				} else {
					cargo = AutoridadAmbientalFacade.DIRECTOR_PROVINCIAL;
				}
			}
		} catch (Exception e) {
		}
		if (persona == null) {
			try {
				if (proyectoActivo.getAreaResponsable().getTipoArea().getId()
						.equals(3)) {
					persona = categoriaIIFacade
							.getPersonaAutoridadAmbientalEnte(proyectoActivo
									.getAreaResponsable().getAreaName());
				} else {
					persona = categoriaIIFacade
							.getPersonaSubsecretarioCalidadAmbiental();
				}
			} catch (Exception e) {
				LOG.error(
						"Error al intentar recuperar la Persona Subsecretario de CalidadAmbiental.",
						e);
				try {
					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
				enviarNotificaciones.enviarNotificacionError(proyectoActivo, "responsable");
				} catch (Exception ex) {
					LOG.error("Error al intentar enviar notificacion cuando no existir usuario responsable.",ex);
				} 
				return null;
			}
		} else {
			try {

				firma = categoriaIIFacade
						.getFirmaSubsecretarioCalidadAmbiental(proyectoActivo
								.getAreaResponsable());
			} catch (Exception ex) {
				LOG.error(
						"Error al recuperar la firma del responsable del área.",
						ex);
				JsfUtil.addMessageError("Error al recuperar la firma del responsable del área.");
				try {
					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
				enviarNotificaciones.enviarNotificacionError(proyectoActivo, "firma");
				} catch (Exception e) {
					LOG.error("Error al intentar recuperar la firma del responsable del area.",e);
				} 
				return null;
			}
		}
		// si no existe responsable envio notificacion
		if (persona == null) {
				try {
					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
					enviarNotificaciones.enviarNotificacionError(proyectoActivo, "responsable");
					//return null;
				} catch (Exception e) {
					LOG.error("Error al intentar enviar notificacion cuando no existir usuario responsable.",e);
				} 
		}
		try {// licencia_ambiental_categoria_ii cargarDatosLicenciaAmbiental

			if (firma == null) {
				try {
					if (proyectoActivo.getAreaResponsable().getTipoArea()
							.getId().equals(3)) {
						String d = "firma__"
								+ proyectoActivo.getAreaResponsable()
										.getAreaAbbreviation() + ".png";
						firma = categoriaIIFacade
								.getFirmaAutoridadAmbientalEntes(d);
					} else {
						firma = categoriaIIFacade
								.getFirmaSubsecretarioCalidadAmbiental();
					}
				} catch (Exception ex) {
					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
					enviarNotificaciones.enviarNotificacionError(proyectoActivo, "firma");
					LOG.error("Error al cargar la firma", ex);
				}
			}
			// si no existe firma envio notificacion
			if (firma == null) {
				GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
				enviarNotificaciones.enviarNotificacionError(proyectoActivo, "firma");
					//return null;
			}
			if (mineria) {
				
				PlantillaReporte plantillaReporte = informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.RESOLUCION_REGISTRO_AMBIENTAL_020.getIdTipoDocumento());
				
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII
						.crearLicenciaAmbientalCategoriaII(
								"LicenciaAmbientalCategoríaII",
								categoriaIIFacade
										.cargarDatosLicenciaAmbientalMineroLibreAprovechamiento(fichaAmbientalPmaBean.getFichaAmbientalMineria().getNumeroResolucion(),
												proyectoActivo, mineria,
												JsfUtil.getLoggedUser()
														.getNombre()),
								"licencia_ambiental_categoria_ii_mineria",
								"Licencia Ambiental Categoría II", persona,
								firma, cargo, proyectoActivo
										.getAreaResponsable(), marcaAgua,false);
			} else if(libreAprovechamiento){
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII
						.crearLicenciaAmbientalCategoriaII(
								"LicenciaAmbientalCategoríaII",
								categoriaIIFacade
										.cargarDatosLicenciaAmbientalMineroLibreAprovechamiento(fichaAmbientalPmaBean.getFicha().getNumeroOficio(),
												proyectoActivo, mineria,
												JsfUtil.getLoggedUser()
														.getNombre()),
								"licencia_ambiental_categoria_ii_mineria_la",
								"Licencia Ambiental Categoría II", persona,
								firma, cargo, proyectoActivo
										.getAreaResponsable(), marcaAgua,false);
			} else if (exploracionInicial) {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII
						.crearLicenciaAmbientalCategoriaII(
								"LicenciaAmbientalCategoríaII",
								categoriaIIFacade
										.cargarDatosLicenciaAmbientalExploracionInicial(
												proyectoActivo, JsfUtil
														.getLoggedUser()
														.getNombre()),
								"licencia_ambiental_categoria_ii_exploracion_inicial",
								"Licencia Ambiental Categoría II", persona,
								firma, cargo, proyectoActivo
										.getAreaResponsable(), marcaAgua,false);
			}else if (perforacionExplorativa) {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII
						.crearLicenciaAmbientalCategoriaII(
								"LicenciaAmbientalCategoríaII",
								categoriaIIFacade
										.cargarDatosLicenciaAmbientalPerforacionExplorativa(
												proyectoActivo, JsfUtil
														.getLoggedUser()
														.getNombre()),
								"licencia_ambiental_categoria_ii_exploracion_inicial",
								"Licencia Ambiental Categoría II", persona,
								firma, cargo, proyectoActivo
										.getAreaResponsable(), marcaAgua,true);
			} else if (comercializacionHidrocarburos) {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII
						.crearLicenciaAmbientalCategoriaII(
								"LicenciaAmbientalCategoríaII",
								categoriaIIFacade
										.cargarDatosLicenciaAmbientalCompleto(
												proyectoActivo, JsfUtil
														.getLoggedUser()
														.getNombre()),
								"licencia_ambiental_categoria_ii_comercializacion_hidrocarburos",
								"Licencia Ambiental Categoría II", persona,
								firma, cargo, proyectoActivo
										.getAreaResponsable(), marcaAgua,false);
			} 
			else {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII
						.crearLicenciaAmbientalCategoriaII(
								"LicenciaAmbientalCategoríaII",
								categoriaIIFacade
										.cargarDatosLicenciaAmbientalCompleto(
												proyectoActivo, JsfUtil
														.getLoggedUser()
														.getNombre()),
								"licencia_ambiental_categoria_ii_n",
								"Licencia Ambiental Categoría II", persona,
								firma, cargo, proyectoActivo
										.getAreaResponsable(), marcaAgua,false);
			}
			// cargarDatosAnexosLicenciaAmbiental
			// anexo_coordenadas_licencia_ambiental_categoria_IIMINERIA_PERFORACION_EXPLORATIVA
			archivoAnexoTemporal = ReporteLicenciaAmbientalCategoriaII
					.crearAnexoCoordenadasLicenciaAmbientalCategoriaII(
							"Anexo coordenadas",
							categoriaIIFacade
									.cargarDatosAnexosCompletoLicenciaAmbiental(proyectoActivo),
							reporteFinal, "Anexo coordenadas", proyectoActivo
									.getAreaResponsable(), marcaAgua);

			listaFiles.add(archivoLicenciaTemporal);
			listaFiles.add(archivoAnexoTemporal);
			archivoTemporal = UtilFichaMineria.unirPdf(listaFiles,
					"Ficha_Ambiental");
			return archivoTemporal;

		} catch (Exception e) {
			LOG.error(
					"Error al intentar generar el archivo del Registro Ambiental.",
					e);
			// JsfUtil.addMessageError("Error al intentar generar el archivo de licencia Ambiental Categoría II.");
			return null;
		}

	}

	protected boolean isCatalogoCategoriaCodigoInicia(
			ProyectoLicenciamientoAmbiental proyecto, String[] values) {
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

	private boolean isMineriaArtesanal(ProyectoLicenciamientoAmbiental proyecto) {
		boolean result = isCatalogoCategoriaCodigoInicia(proyecto,
				MINERIA_ARTESANAL);
		return result;
	}

	private boolean isLibreAprovechamiento(
			ProyectoLicenciamientoAmbiental proyecto) {
		boolean result = isCatalogoCategoriaCodigoInicia(proyecto,
				LIBRE_APROVECHAMIENTO);
		return result;
	}

	private boolean isExploracionInicial(
			ProyectoLicenciamientoAmbiental proyecto) {
		boolean result = isCatalogoCategoriaCodigoInicia(proyecto,
				MINERIA_EXPLORACION_INICIAL);
		return result;
	}
	
	private boolean isPerforacionExplorativa(
			ProyectoLicenciamientoAmbiental proyecto) {
		boolean result = isCatalogoCategoriaCodigoInicia(proyecto,
				MINERIA_PERFORACION_EXPLORATIVA);
		return result;
	}
	
	private File generarLicenciaAmbiental(ProyectoLicenciaCoa proyectoActivo, Integer marcaAgua) {
		
		File archivoTemporal = null;
		File archivoLicenciaTemporal = null;
		File archivoAnexoTemporal = null;
		List<File> listaFiles = new ArrayList<File>();
		
		Persona persona = null;
		
		String cargo = null;
		if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {
			cargo = "AUTORIDAD AMBIENTAL PROVINCIAL";

		} else {
			cargo = "SUBSECRETARIO DE CALIDAD AMBIENTAL";
		}
		
		byte[] firma = null;

		Usuario usuarioSecretario = null;
		String reporteFinal = null;
		//
		try {
			if (proyectoActivo.getAreaResponsable().getTipoArea().getId()
					.equals(3)) {
				cargo = proyectoActivo.getAreaResponsable().getAreaName();
				if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado()
						.equals("MUNICIPIO")) {
					reporteFinal = "anexo_coordenadas_licencia_ambiental_categoria_II_Completo_entes_municipio";
				} else {
					reporteFinal = "anexo_coordenadas_licencia_ambiental_categoria_II_Completo_entes";
				}
			} else {
				usuarioSecretario = usuarioFacade.buscarUsuarioPorRol(cargo)
						.get(0);
				reporteFinal = "anexo_coordenadas_licencia_ambiental_categoria_II_Completo";
			}
			
			Usuario usuarioSecreUsuario = categoriaIIFacade.getPersonaSubsecretarioCalidadAmbiental(proyectoActivo.getAreaResponsable());
			if (usuarioSecretario != null) {
				persona = usuarioSecretario.getPersona();
				if (Usuario.isUserInRole(usuarioSecretario,
						AutoridadAmbientalFacade.GAD_MUNICIPAL)) {
					cargo = AutoridadAmbientalFacade.GAD_MUNICIPAL;
				} else {
					cargo = AutoridadAmbientalFacade.DIRECTOR_PROVINCIAL;
				}
			}
		} catch (Exception e) {
		}
		if (persona == null) {
			try {
				if (proyectoActivo.getAreaResponsable().getTipoArea().getId()
						.equals(3)) {
					persona = categoriaIIFacade
							.getPersonaAutoridadAmbientalEnte(proyectoActivo
									.getAreaResponsable().getAreaName());
				} else {
					persona = categoriaIIFacade
							.getPersonaSubsecretarioCalidadAmbiental();
				}
			} catch (Exception e) {
				LOG.error(
						"Error al intentar recuperar la Persona Subsecretario de CalidadAmbiental.",
						e);
				try {
//					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
//				enviarNotificaciones.enviarNotificacionError(proyectoActivo, "responsable");
				} catch (Exception ex) {
					LOG.error("Error al intentar enviar notificacion cuando no existir usuario responsable.",ex);
				} 
				return null;
			}
		} else {
			try {

				firma = categoriaIIFacade
						.getFirmaSubsecretarioCalidadAmbiental(proyectoActivo
								.getAreaResponsable());
			} catch (Exception ex) {
				LOG.error(
						"Error al recuperar la firma del responsable del área.",
						ex);
				JsfUtil.addMessageError("Error al recuperar la firma del responsable del área.");
				try {
//					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
//				enviarNotificaciones.enviarNotificacionError(proyectoActivo, "firma");
				} catch (Exception e) {
					LOG.error("Error al intentar recuperar la firma del responsable del area.",e);
				} 
				return null;
			}
		}
		// si no existe responsable envio notificacion
		if (persona == null) {
				try {
//					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
//					enviarNotificaciones.enviarNotificacionError(proyectoActivo, "responsable");
					//return null;
				} catch (Exception e) {
					LOG.error("Error al intentar enviar notificacion cuando no existir usuario responsable.",e);
				} 
		}
		try {// licencia_ambiental_categoria_ii cargarDatosLicenciaAmbiental

			if (firma == null) {
				try {
					if (proyectoActivo.getAreaResponsable().getTipoArea()
							.getId().equals(3)) {
						String d = "firma__"
								+ proyectoActivo.getAreaResponsable()
										.getAreaAbbreviation() + ".png";
						firma = categoriaIIFacade
								.getFirmaAutoridadAmbientalEntes(d);
					} else {
						firma = categoriaIIFacade
								.getFirmaSubsecretarioCalidadAmbiental();
					}
				} catch (Exception ex) {
//					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
//					enviarNotificaciones.enviarNotificacionError(proyectoActivo, "firma");
					LOG.error("Error al cargar la firma", ex);
				}
			}
			// si no existe firma envio notificacion
			if (firma == null) {
//				GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
//				enviarNotificaciones.enviarNotificacionError(proyectoActivo, "firma");
					//return null;
			}
			
			archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII
					.crearLicenciaAmbientalCategoriaII(
							"LicenciaAmbientalCategoríaII",
							categoriaIIFacade.cargarDatosLicenciaAmbientalPerforacionExplorativaRCOA(proyectoActivo, JsfUtil.getLoggedUser().getNombre()),
							"licencia_ambiental_categoria_ii_exploracion_inicial",
							"Licencia Ambiental Categoría II", persona,
							firma, cargo, proyectoActivo.getAreaResponsable(), marcaAgua,true);
				return archivoLicenciaTemporal;	
		
		} catch (Exception e) {
			LOG.error(
					"Error al intentar generar el archivo del Registro Ambiental.",
					e);
			// JsfUtil.addMessageError("Error al intentar generar el archivo de licencia Ambiental Categoría II.");
			return null;
		}
	}
		
	

}