/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.categoria2.bean;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.imageio.ImageIO;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.suia.AutorizacionCatalogo.facade.AutorizacionCatalogoFacade;
import ec.gob.ambiente.suia.catalogocategoriasflujo.facade.CatalogoCategoriasFlujoFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.comparator.OrdenarTareaPorEstadoComparator;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreasAutorizadasCatalogo;
import ec.gob.ambiente.suia.domain.CategoriaFlujo;
import ec.gob.ambiente.suia.domain.CertificadoInterseccion;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentoProyecto;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.Flujo;
import ec.gob.ambiente.suia.domain.OficioAprobacionEia;
import ec.gob.ambiente.suia.domain.OficioObservacionEia;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProcesoSuspendido;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityFichaCompleta;
import ec.gob.ambiente.suia.dto.EntityPermisoAmbiental;
import ec.gob.ambiente.suia.dto.PermisoAmbiental;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoSuspendidoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.PermisoAmbientalFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConvertidorObjetosDominioUtil;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.ambiente.suia.utils.UtilPlantilla;

/**
 * <b> Clase que muestra todas la fichas con su respectivo resumen. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 14/04/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ResumenFichaAmbientalPmaBean implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger
			.getLogger(ResumenFichaAmbientalPmaBean.class);

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private CatalogoCategoriasFlujoFacade catalogoCategoriasFlujoFacade;
	@EJB
	private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProcesoSuspendidoFacade procesoSuspendidoFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private IntegracionFacade integracionFacade;
	@EJB
	private AutorizacionCatalogoFacade autorizacionCatalogoFacade;
	@EJB
	private PermisoAmbientalFacade permisoAmbientalFacade;
	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionFacade;
	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
	@EJB
	private InformeOficioFacade informeOficioFacade;

	@Getter
	@Setter
	private List<Documento> documentosProyecto;

	@Getter
	@Setter
	private Documento documentoDescargar;

	@Getter
	@Setter
	private String nombreProponente;

	@Getter
	@Setter
	private String codigoProyecto;

	@Getter
	@Setter
	private String nombreProyecto;

	@Getter
	@Setter
	private String rucProponente;

	@Getter
	@Setter
	private String tipoTramite;

	@Getter
	@Setter
	private boolean enTramite = false;

	@Getter
	@Setter
	private boolean certificadoAmbiental = false;

	@Getter
	@Setter
	private ProcesoSuspendido procesoLicenciamientoSuspendido;

	@Getter
	@Setter
	private Boolean mostrarFlujo;

	@Getter
	@Setter
	private String flujosActivos;

	@Getter
	@Setter
	private boolean archivado = false;
	@Getter
	@Setter
	private String tareaActiva = null;

	@Getter
	@Setter
	private List<String> procesosAsociados = null;

	@Getter
	@Setter
	private List<String> estadoFlujos4Categorias = null;

	@Getter
	@Setter
	private List<PermisoAmbiental> listaPermisosAmbientales;

	@Getter
	@Setter
	private PermisoAmbiental permisoAmbientalSeleccionado;

	@Getter
	@Setter
	private boolean generarDocumentoEstado = false;

	@Getter
	@Setter
	EntityPermisoAmbiental entityPermiso;

	@PostConstruct
	public void init() {

	}

	public void getPermisosAmbientales() throws Exception {

		listaPermisosAmbientales = new ArrayList<PermisoAmbiental>();

		if ((!codigoProyecto.isEmpty()) || (!nombreProyecto.isEmpty())
				|| (!rucProponente.isEmpty()) || (!nombreProponente.isEmpty())) {
			try {
				// 4CATEGORIAS
				List<PermisoAmbiental> listaProyecto4Categorias = permisoAmbientalFacade
						.getProyectos4cat(codigoProyecto, nombreProyecto,
								rucProponente, nombreProponente);
				if (listaProyecto4Categorias != null)
					listaPermisosAmbientales.addAll(listaProyecto4Categorias);

				// SECTOR-SUBSECTOR
				List<PermisoAmbiental> listaProyectoSectorSubsector = permisoAmbientalFacade
						.getProyectosSectorSubsector(codigoProyecto,
								nombreProyecto, rucProponente, nombreProponente);
				if (listaProyectoSectorSubsector != null)
					listaPermisosAmbientales
							.addAll(listaProyectoSectorSubsector);

				// cargamos todos los proyectos suia activos y archivados
				List<PermisoAmbiental> proyectosSuia = permisoAmbientalFacade
						.getRegistrosAmbientales(codigoProyecto,
								nombreProyecto, rucProponente, nombreProponente);
				if (proyectosSuia != null)
					listaPermisosAmbientales.addAll(proyectosSuia);

				// ART
				List<PermisoAmbiental> proyectosART = permisoAmbientalFacade
						.getProyectosAprobacionRequisitosTecnicos(
								codigoProyecto, nombreProyecto, rucProponente,
								nombreProponente);
				if (proyectosART != null)
					listaPermisosAmbientales.addAll(proyectosART);
				// RGD
				List<PermisoAmbiental> proyectosRGD = permisoAmbientalFacade
						.getProyectosGeneradorDesechosPeligrosos(
								codigoProyecto, rucProponente, nombreProponente);
				if (proyectosRGD != null)
					listaPermisosAmbientales.addAll(proyectosRGD);
			} catch (Exception e) {
				e.printStackTrace();
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			}
		} else {
			JsfUtil.addMessageInfo("Debe ingresar al menos un criterio de búsqueda");
			listaPermisosAmbientales = null;
		}
	}

	public void cargarResumenProyecto() {
		setMostrarFlujo(false);
		setFlujosActivos("0");
		tareaActiva = null;
		archivado = false;
		procesoLicenciamientoSuspendido = null;
		tipoTramite = null;
		enTramite = false;
		generarDocumentoEstado = false;

		procesosAsociados = new ArrayList<String>();
		estadoFlujos4Categorias = new ArrayList<String>();
		documentosProyecto = new ArrayList<Documento>();

		getTipoRegistroAmbiental();

		ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigoSinFiltro(permisoAmbientalSeleccionado.getCodigo());
		permisoAmbientalSeleccionado.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);

		if (permisoAmbientalSeleccionado.getEstado() == false) {
			archivado = true;
			if (!permisoAmbientalSeleccionado.getFuente().equals("1")
					&& !permisoAmbientalSeleccionado.getFuente().equals("5")) {
				procesoLicenciamientoSuspendido = procesoSuspendidoFacade
						.getProcesoSuspendidoPorCodigo(permisoAmbientalSeleccionado.getCodigo());
				if (procesoLicenciamientoSuspendido != null) {
					permisoAmbientalSeleccionado.setMotivoEliminar(procesoLicenciamientoSuspendido.getDescripcion());
					permisoAmbientalSeleccionado.setFechaArchivacion(procesoLicenciamientoSuspendido.getFechaCreacion());
				} else if(permisoAmbientalSeleccionado.getProyectoLicenciamientoAmbiental() != null) {
					permisoAmbientalSeleccionado.setMotivoEliminar(permisoAmbientalSeleccionado.getProyectoLicenciamientoAmbiental().getMotivoEliminar());
					permisoAmbientalSeleccionado.setFechaArchivacion(permisoAmbientalSeleccionado.getProyectoLicenciamientoAmbiental().getFechaModificacion());
				} 
			}
			if (!permisoAmbientalSeleccionado.getFuente().equals("2"))
				return;
		}

		switch (permisoAmbientalSeleccionado.getFuente()) {
		case "1":
			// 4categorias excel
			getInfo4Categorias();
			break;
		case "2":
			getInfoSuia();
			break;
		case "3":
			getInfoArtRgd();
			break;
		case "4":
			getInfoArtRgd();
			break;
		case "5":
			// sector-subsector
			getInfo4Categorias();
		default:
			break;
		}
	}

	public void getTipoRegistroAmbiental() {
		if (permisoAmbientalSeleccionado.getCategoria() != null) {
			switch (permisoAmbientalSeleccionado.getCategoria()) {
			case "I":
				tipoTramite = "Certificado Ambiental";
				break;
			case "II":
				tipoTramite = "Registro Ambiental";
				break;
			case "III":
				tipoTramite = "Licencia Ambiental";
				break;
			case "IV":
				tipoTramite = "Licencia Ambiental";
				break;
			default:
				tipoTramite = permisoAmbientalSeleccionado.getCategoria();
				break;
			}
		}
	}

	public void getInfo4Categorias() {
		try {
			enTramite = false;
			Boolean isHidrocarburos = false;

			// busqueda de flujos 4categorias
			if (permisoAmbientalSeleccionado.getFuente().equals("1")) {
				isHidrocarburos = esHidrocarburos(permisoAmbientalSeleccionado);
			}
			if (!isHidrocarburos) {
				List<Object[]> listaEnte = permisoAmbientalFacade.getEnteResponsable4cat(permisoAmbientalSeleccionado.getProvincia(), permisoAmbientalSeleccionado.getEstrategico(), permisoAmbientalSeleccionado.getFuente());
				if (listaEnte != null && listaEnte.size() > 0) {
					permisoAmbientalSeleccionado.setEnteResponsable(listaEnte.get(0)[4].toString());
					permisoAmbientalSeleccionado.setAbreviacionArea(listaEnte.get(0)[5].toString());
				}
				
				List<Object[]> flujos = permisoAmbientalFacade.getFlujos4cat(
						permisoAmbientalSeleccionado.getCodigo(),
						permisoAmbientalSeleccionado.getFuente());
				if (flujos != null && flujos.size() > 0) {
					estadoFlujos4Categorias = new ArrayList<String>();
					estadoFlujos4Categorias = getFlujos4CategoriasPorProyecto(flujos);
				}

				List<Object[]> tareasActuales = permisoAmbientalFacade
						.getTaskId4cat(
								permisoAmbientalSeleccionado.getCodigo(),
								permisoAmbientalSeleccionado.getFuente());
				if (tareasActuales != null && tareasActuales.size() > 0) {
					enTramite = true;
					if (tipoTramite.equals("Licencia Ambiental")) {
						getInfoTareas4Categorias();
					}
				}
			} else {
				String ente = (permisoAmbientalSeleccionado
						.getProyectoLicenciamientoAmbiental() != null) ? permisoAmbientalSeleccionado
						.getProyectoLicenciamientoAmbiental()
						.getAreaResponsable().getAreaName()
						: "";
				permisoAmbientalSeleccionado.setEnteResponsable(ente);

				List<Object> hidrocarburoFinalizado = permisoAmbientalFacade.getHidrocarburosFinalizados(permisoAmbientalSeleccionado.getCodigo());
				if (hidrocarburoFinalizado == null) {
					enTramite = true;
				}
			}

			if (!enTramite && !isHidrocarburos) {
				String tiposDocumento = "";
				if (permisoAmbientalSeleccionado.getFuente().equals("1")) {
					if (tipoTramite.equals("Certificado Ambiental")) {
						tiposDocumento = "''pciCertificadoCatIFirmadoAutomatico''";
					} else if (tipoTramite.equals("Registro Ambiental")) {
						tiposDocumento = "''certCategoriaIIFirma'', ''certCategoriaIIFinal''";
					} else if (tipoTramite.equals("Licencia Ambiental")) {
						tiposDocumento = "''oficioFirmadoAprobacionTDRs'', ''catIVCertificado'', ''catIVCertificadoFirmado'', "
								+ "''catIIIoficioAprobado'', ''borradorResolucion''";
					}
				} else {
					if (tipoTramite.equals("Ficha Ambiental")) {
						tiposDocumento = "''efaOficioAprobacionFirmadoEFA''";
					} else if (tipoTramite.equals("Licencia Ambiental")) {
						tiposDocumento = "''oficioElaFirmado''";
					}
				}

				List<Object[]> documentos = null;
				if (tiposDocumento != "") {
					documentos = permisoAmbientalFacade
							.getDocumentosProyecto4cat(
									permisoAmbientalSeleccionado.getCodigo(),
									tiposDocumento,
									permisoAmbientalSeleccionado.getFuente());
				}
				if (documentos != null && documentos.size() > 0) {
					Boolean validarDocumento = false;
					if (tipoTramite.equals("Registro Ambiental")
							&& documentos.size() > 1) {
						validarDocumento = true;
					}
					for (int i = 0; i < documentos.size(); i++) {
						Object[] documento = documentos.get(i);
						String urlfile = null;
						Date fechaDocumento = new SimpleDateFormat("yyyy-MM-dd")
								.parse(documento[2].toString());

						if (documento[4] == null) {
							urlfile = documentosFacade.direccionDescarga(
									documento[0].toString(), fechaDocumento);
						} else {
							urlfile = documento[4].toString();
						}
						if (urlfile != "") {
							Documento doc = new Documento();
							doc.setNombre(documento[1].toString());
							doc.setIdAlfresco(urlfile);
							doc.setFechaCreacion(fechaDocumento);
							if (validarDocumento == true) {
								if (documento[3].toString().equals(
										"certCategoriaIIFirma"))
									documentosProyecto.add(doc);
							} else {
								documentosProyecto.add(doc);
							}
						}
					}
				}
			}

			if (!enTramite && isHidrocarburos) {
				String tiposHidrocarburos = "331";
				List<Object[]> documentosHidrocarburos = permisoAmbientalFacade.getDocumentosProyectoHidrocarburos(
								permisoAmbientalSeleccionado.getCodigo(), tiposHidrocarburos);
				if (documentosHidrocarburos != null && documentosHidrocarburos.size() > 0) {
					for (int i = 0; i < documentosHidrocarburos.size(); i++) {
						Object[] documento = documentosHidrocarburos.get(i);
						Date fechaDocumento = new SimpleDateFormat("yyyy-MM-dd").parse(documento[1].toString());

						String urlfile = documentosFacade.getWorkspaceByDocumentName(documento[3].toString());
						if (urlfile != "") {
							Documento doc = new Documento();
							doc.setNombre(documento[4].toString());
							doc.setIdAlfresco(urlfile);
							doc.setFechaCreacion(fechaDocumento);
							documentosProyecto.add(doc);
						}
					}
				}
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void getInfoTareas4Categorias() {
		entityPermiso = new EntityPermisoAmbiental();

		String procesoProyecto = "";
		if (permisoAmbientalSeleccionado.getFuente().equals("5"))
			procesoProyecto = "Pronunciamiento."
					+ permisoAmbientalSeleccionado.getCodigo();
		else
			procesoProyecto = "CategoriaIV2."
					+ permisoAmbientalSeleccionado.getCodigo();

		String plantillaFavorable = "<p>Con fecha %s, el <strong>$F{enteResponsable}</strong>, emite pronunciamiento favorable al Estudio de Impacto Ambiental del proyecto $F{nombreProyecto}, ubicado en la provincia de $F{provinciaProyecto}.</p>";
		String plantillaSubsanacion = "<p>Con fecha %s, el operador $F{proponente}, ingres&oacute; al Sistema &Uacute;nico de Informaci&oacute;n Ambiental (SUIA) las respuestas a observaciones del Estudio de Impacto Ambiental del proyecto $F{nombreProyecto}, ubicado en la provincia de $F{provinciaProyecto}.</p>";
		String plantillaObservaciones = "<p>Con fecha %s, el <strong>$F{enteResponsable}</strong>, solicita información aclaratoria y/o complementaria del Estudio de Impacto Ambiental del proyecto $F{nombreProyecto}, ubicado en la provincia de $F{provinciaProyecto}.</p>";
		String plantillaIngresoEia = "<p>Mediante tr&aacute;mite Nro. $F{codigoProyecto} con fecha %s, el operador $F{proponente}, ingres&oacute; al Sistema &Uacute;nico de Informaci&oacute;n Ambiental (SUIA) el Estudio de Impacto Ambiental del proyecto $F{nombreProyecto}, ubicado en la provincia de $F{provinciaProyecto}.</p>";

		List<Object[]> historialTareas = permisoAmbientalFacade.getHistorialTareas4cat(procesoProyecto,permisoAmbientalSeleccionado.getFuente());
		if (permisoAmbientalSeleccionado.getFuente().equals("5")
				&& historialTareas != null && historialTareas.size() > 0) {
			generarDocumentoEstado = true;
			DateFormat fecha = DateFormat.getDateInstance(DateFormat.LONG,new Locale("es"));

			for (int i = 0; i < historialTareas.size(); i++) {
				Object[] tarea = historialTareas.get(i);
				String[] parametros = { fecha.format(tarea[3]) };
				if (tarea[0].toString().equals("pro_pronunciamiento_favorable")
						&& tarea[3] != null) {
					entityPermiso.setTxtEstadoProyecto(UtilPlantilla
							.getPlantillaConParametros(plantillaFavorable,
									parametros));
					break;
				}

				if (tarea[0].toString().equals("pro_elaboracion_correcciones")
						&& tarea[3] != null) {
					entityPermiso.setTxtEstadoProyecto(UtilPlantilla
							.getPlantillaConParametros(plantillaSubsanacion,
									parametros));
					break;

				}

				if (tarea[0].toString().equals("revisionaceptada")
						&& tarea[1].toString().equals("NO") && tarea[3] != null) {
					entityPermiso.setTxtEstadoProyecto(UtilPlantilla
							.getPlantillaConParametros(plantillaObservaciones,
									parametros));
					break;
				}

				if (tarea[0].toString().equals("pro_revision_em")
						&& tarea[3] == null) {
					String[] parametro = { fecha.format(tarea[2]) };
					entityPermiso.setTxtEstadoProyecto(UtilPlantilla
							.getPlantillaConParametros(plantillaIngresoEia,
									parametro));
					break;
				}
			}
		}

		if (permisoAmbientalSeleccionado.getFuente().equals("1")
				&& historialTareas != null && historialTareas.size() > 0) {
			generarDocumentoEstado = true;
			DateFormat fecha = DateFormat.getDateInstance(DateFormat.LONG,
					new Locale("es"));

			for (int i = 0; i < historialTareas.size(); i++) {
				Object[] tarea = historialTareas.get(i);
				
				if (tarea[0].toString().equals("aprobadoFirmaPronunciamiento")
						&& tarea[1].toString().equals("SI") && tarea[3] != null) {
					String[] parametros = { fecha.format(tarea[3]) };
					entityPermiso.setTxtEstadoProyecto(UtilPlantilla
							.getPlantillaConParametros(plantillaFavorable,
									parametros));
					break;
				}

				if (tarea[0].toString().equals("cat_iv_corregirEstudios")
						&& tarea[3] != null) {
					String[] parametros = { fecha.format(tarea[3]) };
					entityPermiso.setTxtEstadoProyecto(UtilPlantilla
							.getPlantillaConParametros(plantillaSubsanacion,
									parametros));
					break;

				}

				if (tarea[0].toString().equals("aprobadoFirmaObservaciones")
						&& tarea[1].toString().equals("SI") && tarea[3] != null) {
					String[] parametros = { fecha.format(tarea[3]) };
					entityPermiso.setTxtEstadoProyecto(UtilPlantilla
							.getPlantillaConParametros(plantillaObservaciones,
									parametros));
					break;
				}

				if (tarea[0].toString().equals("cat_iv_asignarTareaEstudios")
						&& tarea[3] == null) {
					String[] parametro = { fecha.format(tarea[2]) };
					entityPermiso.setTxtEstadoProyecto(UtilPlantilla
							.getPlantillaConParametros(plantillaIngresoEia,
									parametro));
					break;
				}
			}
		}
	}

	public void getInfoArtRgd() {
		try {
			tipoTramite = null;
			enTramite = false;
			Boolean proyectoAsociado = false;

			Usuario usuario = usuarioFacade.buscarUsuarioCompleta(permisoAmbientalSeleccionado.getCedulaProponente());

			String codigoProyecto = "";
			if (permisoAmbientalSeleccionado.getIdProyectoAsociado() == null
					&& permisoAmbientalSeleccionado.getCodigoProyectoAsociado() == null) {
				codigoProyecto = permisoAmbientalSeleccionado.getCodigo();
			} else {
				if (permisoAmbientalSeleccionado.getIdProyectoAsociado() != null) {
					ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciaAmbientalFacade
							.getProyectoPorId(permisoAmbientalSeleccionado.getIdProyectoAsociado());
					codigoProyecto = proyecto.getCodigo();
					proyectoAsociado = true;
				}
				if (permisoAmbientalSeleccionado.getCodigoProyectoAsociado() != null) {
					codigoProyecto = permisoAmbientalSeleccionado.getCodigoProyectoAsociado();
					proyectoAsociado = true;
				}
			}

			List<ProcessInstanceLog> procesosTramite = procesoFacade
					.getProcessInstancesLogsVariableValue(usuario,
							Constantes.VARIABLE_PROCESO_TRAMITE, codigoProyecto);

			TaskSummary tareaActual = null;
			Long processInstanceId = null;
			if (procesosTramite.size() > 0) {
				for (ProcessInstanceLog processLog : procesosTramite) {
					if (processLog
							.getProcessId()
							.equals(Constantes.NOMBRE_PROCESO_APROBACION_REQUISITOS_TECNICOS)
							|| processLog.getProcessId().equals(
									Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS)) {
						processInstanceId = processLog.getProcessInstanceId();
						tipoTramite = processLog.getProcessName();
	
						tareaActual = procesoFacade.getCurrenTask(usuario,
								processInstanceId);
	
						if (tareaActual != null) {
							enTramite = true;
							return;
						}
					}
	
					enTramite = false;
				}
			} else if(!proyectoAsociado) {
				enTramite = true;
				tipoTramite = (codigoProyecto.startsWith("MAAE-SOL-RGD") || codigoProyecto.startsWith("MAATE-SOL-RGD")) ? "Registro de generador de desechos especiales y peligrosos" : "Aprobacion Requisitos Tecnicos Gestion de Desechos";
			}

			if (!enTramite && processInstanceId != null) {
				List<Documento> documentos = documentosFacade
						.recuperarDocumentosConArchivosPorFlujoTodasVersionesNombresUnicos(processInstanceId);
				List<Integer> tiposDocumento = new ArrayList<Integer>();
				if (permisoAmbientalSeleccionado.getFuente().equals("4")) {
					tiposDocumento
							.add(TipoDocumentoSistema.TIPO_OFICIO_EMISION_RGD
									.getIdTipoDocumento());
					tiposDocumento.add(TipoDocumentoSistema.TIPO_BORRADOR_RGD
							.getIdTipoDocumento());
				} else if (permisoAmbientalSeleccionado.getFuente().equals("3")) {
					tiposDocumento
							.add(TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART
									.getIdTipoDocumento());
					tiposDocumento
							.add(TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART
									.getIdTipoDocumento());
				}
				documentosProyecto.addAll(getDocumentosPorProyecto(documentos,
						tiposDocumento));
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void getInfoSuia() {
		try {
			List<EntityFichaCompleta> registrosFinalizados = fichaAmbientalPmaFacade
					.getFinalizados(permisoAmbientalSeleccionado.getCodigo());
			if (registrosFinalizados.size() > 0) {

				List<DocumentoProyecto> documentosCA = documentosFacade
						.getDocumentosPorIdProyecto(permisoAmbientalSeleccionado
								.getId());
				enTramite = false;

				if (permisoAmbientalSeleccionado.getCategoria().equals("I")) {
					List<Integer> tiposDocCertificado = new ArrayList<Integer>();
					tiposDocCertificado
							.add(TipoDocumentoSistema.TIPO_CERTIFICADO_CATEGORIA_UNO
									.getIdTipoDocumento());
					documentosProyecto.addAll(getDocumentosProyectoPorProyecto(
							documentosCA, tiposDocCertificado));
				} else if (permisoAmbientalSeleccionado.getCategoria().equals(
						"II")) {
					List<Integer> tiposDocCertificado = new ArrayList<Integer>();
					tiposDocCertificado
							.add(TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL
									.getIdTipoDocumento());
					documentosProyecto.addAll(getDocumentosProyectoPorProyecto(
							documentosCA, tiposDocCertificado));
				} else {
					List<Integer> tiposDocCertificado = new ArrayList<Integer>();
					tiposDocCertificado
							.add(TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA
									.getIdTipoDocumento());
					documentosProyecto.addAll(getDocumentosProyectoPorProyecto(
							documentosCA, tiposDocCertificado));
				}
			} else if (archivado == false) {
				enTramite = true;
			}

			if(permisoAmbientalSeleccionado.getCategoria().equals("III") || 
					permisoAmbientalSeleccionado.getCategoria().equals("IV"))
				getTareasProyecto();

		} catch (Exception e) {
			LOG.error("No se puede cargar los Documentos.", e);
			JsfUtil.addMessageError("No se puede cargar los Documentos.");
		}

	}

	public void getTareasProyecto() {
		try {

			Usuario usuario = usuarioFacade
					.buscarUsuarioCompleta(permisoAmbientalSeleccionado
							.getCedulaProponente());

			ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = permisoAmbientalSeleccionado
					.getProyectoLicenciamientoAmbiental();

			List<String> flujosPrincipales = new ArrayList<String>();
			flujosPrincipales.add(Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL);
			flujosPrincipales.add(Constantes.NOMBRE_PROCESO_PARTICIPACION_SOCIAL);

			List<String> flujosSecundarios = new ArrayList<String>();
			flujosSecundarios.add(Constantes.NOMBRE_PROCESO_APROBACION_REQUISITOS_TECNICOS);
			flujosSecundarios.add(Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS);
//			flujosSecundarios.add(Constantes.NOMBRE_PROCESO_ELIMINAR_PROYECTO);

			List<Flujo> flujosIniciados = new ArrayList<Flujo>();

			List<CategoriaFlujo> flujosProyecto = catalogoCategoriasFlujoFacade
					.obtenerFlujosDeProyectoPorCategoria(proyectoLicenciamientoAmbiental, "idProyecto", usuario);
			for (CategoriaFlujo categoriaFlujo : flujosProyecto) {
				if (!categoriaFlujo.getFlujo().getEstadoProceso().equals("No iniciado")) {
					flujosIniciados.add(categoriaFlujo.getFlujo());
				}
			}

			String flujosActivos = "";
			String tareasActivas = "";
			procesosAsociados = new ArrayList<String>();
			entityPermiso = new EntityPermisoAmbiental();

			if (flujosIniciados.size() == 0) {
				setMostrarFlujo(true);
				flujosActivos += "1,";
			} else {
				TaskSummary tareaActual = null;

				for (Flujo flujo : flujosIniciados) {
					String nombreFlujoAsociado = "";
					if (flujosPrincipales.contains(flujo.getIdProceso())) {
						setMostrarFlujo(true);

						Long processInstanceId = flujo.getProcessInstanceId();
						tareaActual = procesoFacade.getCurrenTask(usuario,
								processInstanceId);

						if (tareaActual != null) {
							if (flujo
									.getIdProceso()
									.equals(Constantes.NOMBRE_PROCESO_PARTICIPACION_SOCIAL)) {
								flujosActivos += "4,";
								tareasActivas += "\"4\":\""
										+ tareaActual.getName() + "\",";
							} else if (flujo
									.getIdProceso()
									.equals(Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL)) {
								if (tareaActual.getName().equals(
										"Descargar TDR")) {
									flujosActivos += "2,";
									tareasActivas += "\"2\":\""
											+ tareaActual.getName() + "\",";
								} else {
									List<TaskSummary> tareasFlujo = procesoFacade
											.getTaskBySelectFlow(usuario,
													processInstanceId);
									if (tareasFlujo.size() > 1) {
										flujosActivos += "3,";
										tareasActivas += "\"3\":\""
												+ tareaActual.getName() + "\",";

										if (!archivado) {
											getInfoEstadoTareas(tareasFlujo, flujo, usuario);
										}
									}
								}
							}
						} else if (flujo.getIdProceso().equals(
								Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL)) {
							flujosActivos += "5,";
						}
					} else if (flujosSecundarios.contains(flujo.getIdProceso())) {
						if (flujo.getEstadoProceso().equals("Completado")) {
							nombreFlujoAsociado = flujo.getNombreFlujo()
									+ " - Completado";
							procesosAsociados.add(nombreFlujoAsociado);
						} else if (flujo.getEstadoProceso().equals("Activo")) {
							nombreFlujoAsociado = flujo.getNombreFlujo()
									+ " - En Trámite";
							procesosAsociados.add(nombreFlujoAsociado);
						}
					}
				}
			}

			if (flujosActivos != "") {
				setFlujosActivos(flujosActivos.substring(0,
						flujosActivos.length() - 1));
			}
			
			if (tareasActivas != null && tareasActivas != "") {
				tareaActiva = tareasActivas.substring(0,
						tareasActivas.length() - 1);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void getInfoEstadoTareas(List<TaskSummary> tareasFlujo, Flujo flujo,
			Usuario usuario) throws Exception {
		generarDocumentoEstado = true;
		List<Tarea> tareas = new ArrayList<Tarea>();
		ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = permisoAmbientalSeleccionado
				.getProyectoLicenciamientoAmbiental();

		for (TaskSummary tareaSummary : tareasFlujo) {
			Tarea tarea = new Tarea();
			ConvertidorObjetosDominioUtil.convertirTaskSummaryATarea(
					tareaSummary, tarea);
			if (!tareas.contains(tarea))
				tareas.add(tarea);
		}

		Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());
		DateFormat fecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		Boolean getFechaSubsanacion = false;

		for (Tarea tarea : tareas) {
			if (!getFechaSubsanacion) {
				if (tarea.getNombre().equals(
						"Firmar pronunciamiento de observacion")
						&& tarea.getEstado().equals("Completada")) {
					EstudioImpactoAmbiental estudioImpactoAmbiental = estudioImpactoAmbientalFacade
							.obtenerPorProyecto(proyectoLicenciamientoAmbiental);

					OficioObservacionEia oficioObservacionEia = informeOficioFacade.obtenerOficioObservacionEiaPorEstudio(
									TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_EIA, estudioImpactoAmbiental.getId());
					if(oficioObservacionEia != null) {
						String[] fechaPartes = oficioObservacionEia.getFechaInforme().split(",");
						entityPermiso.setNroOficioObservacion(oficioObservacionEia.getNumeroOficio());
						entityPermiso.setFechaOficioObservacion(fechaPartes[1]);
					}
					entityPermiso.setTxtEstadoProyecto(getTxtEiaObservado());
					break;
				}
				if (tarea.getNombre().equals(
						"Firmar pronunciamiento de aprobacion")
						&& tarea.getEstado().equals("Completada")) {
					EstudioImpactoAmbiental estudioImpactoAmbiental = estudioImpactoAmbientalFacade
							.obtenerPorProyecto(proyectoLicenciamientoAmbiental);

					OficioAprobacionEia oficioAprobacionEia = informeOficioFacade.obtenerOficioAprobacionEiaPorEstudio(
									TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA, estudioImpactoAmbiental.getId());
					if(oficioAprobacionEia != null){
						String[] fechaPartes = oficioAprobacionEia.getFechaInforme().split(",");
						entityPermiso.setNroOficioAprobacion(oficioAprobacionEia.getNumeroOficio());
						entityPermiso.setFechaOficioAprobacion(fechaPartes[1]);
					}
					entityPermiso.setTxtEstadoProyecto(getTxtEiaFavorable());
					break;
				}
				if (tarea.getNombre().equals("Pago de licencia ambiental")
						&& tarea.getEstado().equals("Completada")) {
					entityPermiso.setTxtEstadoPagoProyecto(getTxtEnPago());
					entityPermiso
							.setFechaPago(fecha.format(tarea.getFechaFin()));
					break;
				}
				if (tarea.getNombre().equals(
						"Revisar y analizar estudio ingresado")) {
					Map<String, Object> variables = new ConcurrentHashMap<String, Object>();
					Integer cantidadNotificaciones = 0;

					variables = procesoFacade.recuperarVariablesProceso(
							usuario, flujo.getProcessInstanceId());
					cantidadNotificaciones = Integer.valueOf(variables.get(
							"cantidadNotificaciones").toString());
					if (cantidadNotificaciones > 0) {
						entityPermiso.setTxtEstadoProyecto(getTxtEiaSubsanado());
						getFechaSubsanacion = true;
					} else {
						entityPermiso.setTxtEstadoProyecto(getTxtEiaIngresado());
						break;
					}
				}
				if (tarea.getNombre().equals("Delegar tecnico responsable")) {
					entityPermiso.setTxtEstadoProyecto(getTxtEiaIngresado());
					break;
				}
			}
			if (getFechaSubsanacion
					&& tarea.getNombre().equals("Ingresar correcciones")
					&& tarea.getEstado().equals("Completada")) {
				entityPermiso.setFechaSubsanacion(fecha.format(tarea.getFechaFin()));
				break;
			}
		}
	}

	public String getTxtEiaIngresado() {
		return DocumentoPDFPlantillaHtml
				.getValorResourcePlantillaInformes("documento_permiso_ambiental_eia_ingresado");
	}

	public String getTxtEiaObservado() {
		return DocumentoPDFPlantillaHtml
				.getValorResourcePlantillaInformes("documento_permiso_ambiental_eia_observado");
	}

	public String getTxtEiaSubsanado() {
		return DocumentoPDFPlantillaHtml
				.getValorResourcePlantillaInformes("documento_permiso_ambiental_eia_subsanado");
	}

	public String getTxtEiaFavorable() {
		return DocumentoPDFPlantillaHtml
				.getValorResourcePlantillaInformes("documento_permiso_ambiental_eia_aprobado");
	}

	public String getTxtEnPago() {
		return DocumentoPDFPlantillaHtml
				.getValorResourcePlantillaInformes("documento_permiso_ambiental_eia_pago");
	}

	public List<Documento> getDocumentosProyectoPorProyecto(
			List<DocumentoProyecto> documentosProyectos,
			List<Integer> tiposDocumento) {
		List<Documento> documentosFinales = new ArrayList<Documento>();
		Map<Integer, DocumentoProyecto> documentosFiltrados = new HashMap<Integer, DocumentoProyecto>();
		for (DocumentoProyecto documento : documentosProyectos) {
			if (tiposDocumento.contains(documento.getDocumento()
					.getTipoDocumento().getId())
					&& documento
							.getDocumento()
							.getIdTable()
							.equals(documento
									.getProyectoLicenciamientoAmbiental()
									.getId())) {
				if (!documentosFiltrados.containsKey(documento.getDocumento()
						.getTipoDocumento().getId()))
					documentosFiltrados.put(documento.getDocumento()
							.getTipoDocumento().getId(), documento);
				else {
					Integer maxId = documentosFiltrados
							.get(documento.getDocumento().getTipoDocumento()
									.getId()).getId();
					if (documento.getId() > maxId) {
						documentosFiltrados.put(documento.getDocumento()
								.getTipoDocumento().getId(), documento);
					}
				}
			}
		}

		for (Integer doc : documentosFiltrados.keySet()) {
			documentosFinales.add(documentosFiltrados.get(doc).getDocumento());
		}

		return documentosFinales;
	}

	public List<Documento> getDocumentosPorProyecto(
			List<Documento> documentosProyecto, List<Integer> tiposDocumento) {
		List<Documento> documentosFinales = new ArrayList<Documento>();
		Map<Integer, Documento> documentosFiltrados = new HashMap<Integer, Documento>();
		for (Documento documento : documentosProyecto) {
			if (tiposDocumento.contains(documento.getTipoDocumento().getId())) {
				if (!documentosFiltrados.containsKey(documento
						.getTipoDocumento().getId()))
					documentosFiltrados.put(documento.getTipoDocumento()
							.getId(), documento);
				else {
					Integer maxId = documentosFiltrados.get(
							documento.getTipoDocumento().getId()).getId();
					if (documento.getId() > maxId) {
						documentosFiltrados.put(documento.getTipoDocumento()
								.getId(), documento);
					}
				}
			}
		}

		for (Integer doc : documentosFiltrados.keySet()) {
			documentosFinales.add(documentosFiltrados.get(doc));
		}

		return documentosFinales;
	}

	public void descargarDocumento() {
		try {
			JsfUtil.descargarPdf(documentosFacade.descargar(
					documentoDescargar.getIdAlfresco(),
					documentoDescargar.getFechaCreacion()), documentoDescargar
					.getNombre().replaceAll(".pdf", ""));
		} catch (Exception e) {
			LOG.error("No se puede descargar los Documentos.", e);
			JsfUtil.addMessageError("No se puede descargar los Documentos.");
		}
	}

	public Boolean esHidrocarburos(PermisoAmbiental proyecto) {
		Boolean isHidrocarburos = false;
		try {

			Usuario usuario = usuarioFacade.buscarUsuarioCompleta(proyecto
					.getCedulaProponente());
			if (usuario == null) {
				usuario = new Usuario();
				usuario.setNombre(proyecto.getCedulaProponente());
			}

			isHidrocarburos = integracionFacade.isProjectHydrocarbons(
					proyecto.getCodigo(), usuario.getNombre(),
					usuario.getPasswordSha1Base64());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ProyectoLicenciamientoAmbiental proyectoLicenciamiento = proyectoLicenciamientoAmbientalFacade
				.getProyectoPorCodigo(proyecto.getCodigo());
		if (proyectoLicenciamiento != null) {
			AreasAutorizadasCatalogo estacionServicio = autorizacionCatalogoFacade
					.getaAreasAutorizadasCatalogo(
							proyectoLicenciamiento.getCatalogoCategoria(),
							proyectoLicenciamiento.getAreaResponsable());
			if (estacionServicio != null) {
				if ((proyectoLicenciamientoAmbientalFacade.consultaTaskhyd4cat(proyecto.getCodigo()) > 0)) {
					isHidrocarburos = false;
				} else {
					if ((proyectoLicenciamientoAmbientalFacade.consultaTaskhyd(proyecto.getCodigo()) > 0)) {
						isHidrocarburos = true;
					} else {
						if (!estacionServicio.getActividadBloqueada()) {
							isHidrocarburos = true;
						}
					}
				}
			}
		}

		return isHidrocarburos;
	}

	public List<String> getFlujos4CategoriasPorProyecto(List<Object[]> flujos) {
		Map<String, String> flujosFiltrados = new LinkedHashMap<String, String>();
		for (int i = 0; i < flujos.size(); i++) {
			Object[] flujo = flujos.get(i);
			String nombre = flujo[1].toString();
			String[] split = nombre.split("\\.");
			String nombreFlujo = split[0];
			String estado = (flujo[2] == null) ? "En Trámite" : (flujo[2]
					.toString().equals("completed")) ? "Completado" : "";

			if (flujosFiltrados.containsKey(nombreFlujo)) {
				flujosFiltrados.remove(nombreFlujo);
			}
			flujosFiltrados.put(nombreFlujo, estado);
		}
		String flujosActivos = "";

		Map<String, String[]> flujosGraficos = new LinkedHashMap<String, String[]>();
		if (permisoAmbientalSeleccionado.getFuente().equals("1")) {
			flujosGraficos.put("CategoriaIV", new String[] { "TDR", "2," });
			flujosGraficos.put("ExAnte", new String[] { "PPS", "4," });
			flujosGraficos.put("CategoriaIV2", new String[] {
					"Licenciamiento Ambiental", "3," });
			flujosGraficos.put("EliminacionProyectos", new String[] {
					"Eliminación Proyectos", null });
		} else {
			flujosGraficos.put("TerminosDeReferencia", new String[] { "TDR",
					"2," });
			flujosGraficos.put("ExAnte", new String[] { "PPS", "4," });
			flujosGraficos.put("EmisionLicenciaAmbiental", new String[] {
					"Licenciamiento Ambiental", "3," });
			flujosGraficos.put("EliminacionProyectos", new String[] {
					"Eliminación Proyectos", null });
		}

		List<String> flujosFinales = new ArrayList<String>();
		for (String flujo : flujosFiltrados.keySet()) {
			String nombreFlujo = null;
			if (flujosGraficos.containsKey(flujo)) {
				String[] infoFlujo = flujosGraficos.get(flujo);
				nombreFlujo = infoFlujo[0];

				if (infoFlujo[1] != null
						&& flujosFiltrados.get(flujo).equals("En Trámite")) {
					flujosActivos += infoFlujo[1];
				}
			}

			if (nombreFlujo != null) {
				flujosFinales.add(nombreFlujo + " - "
						+ flujosFiltrados.get(flujo));
			}
		}

		if (flujosActivos != "") {
			setFlujosActivos(flujosActivos.substring(0,
					flujosActivos.length() - 1));
			setMostrarFlujo(true);
			// tareaActiva = tareasActivas.substring(0,
			// tareasActivas.length()-1);
		}

		return flujosFinales;
	}

	public StreamedContent getStreamContent() throws Exception {
		DefaultStreamedContent content = null;
		documentoDescargar = descargarAlfresco(documentoDescargar);
		try {
			if (documentoDescargar != null
					&& documentoDescargar.getNombre() != null
					&& documentoDescargar.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoDescargar.getContenidoDocumento()));
				content.setName(documentoDescargar.getNombre());

			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}

	private Documento descargarAlfresco(Documento documento)
			throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null) {
			documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
		}
		if (documentoContenido != null) {
			documento.setContenidoDocumento(documentoContenido);
		}
		return documento;
	}

	public StreamedContent getStreamDocumentoEstado() throws Exception {
		PlantillaReporte plantillaReporte = new PlantillaReporte();
		if (permisoAmbientalSeleccionado.getFuente().equals("1")
				|| permisoAmbientalSeleccionado.getFuente().equals("5")) {
			getInfoDocumento4Categorias();
			plantillaReporte = permisoAmbientalFacade
					.obtenerPlantillaReporte(TipoDocumentoSistema.DOCUMENTO_ESTADO_PERMISO_AMBIENTAL_4CATEGORIAS
							.getIdTipoDocumento());
		} else {
			getInfoDocumento();
			plantillaReporte = permisoAmbientalFacade
					.obtenerPlantillaReporte(TipoDocumentoSistema.DOCUMENTO_ESTADO_PERMISO_AMBIENTAL
							.getIdTipoDocumento());
		}

		String nombreReporte = "PermisoAmbiental.pdf";

		File informePdf = UtilGenerarInforme.generarFichero(
				plantillaReporte.getHtmlPlantilla(), nombreReporte, entityPermiso, 
				Boolean.valueOf(false), Boolean.valueOf(true));

		if (Constantes.getDocumentosBorrador()) {
			informePdf = JsfUtil.fileMarcaAgua(informePdf, "BORRADOR", BaseColor.GRAY);
		}
		
		File fileQr = new File(entityPermiso.getQrCode());
		fileQr.delete();
		
		File fileQrFirma = new File (entityPermiso.getQrCodeFirma());
		fileQrFirma.delete();
		
		File fileQrFirmaEnte = new File (entityPermiso.getUrlFirmaEnte());
		fileQrFirmaEnte.delete();

		DefaultStreamedContent content = null;
		try {
			content = new DefaultStreamedContent(
					new FileInputStream(informePdf),
					new MimetypesFileTypeMap().getContentType(informePdf));
			content.setName(nombreReporte);

		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}

	public void getInfoDocumento() throws Exception {
		try {
			DateFormat fecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));

			entityPermiso.setFechaEmision(fecha.format(new Date()));
			entityPermiso.setCodigoProyecto(permisoAmbientalSeleccionado.getCodigo());
			entityPermiso.setEnteResponsable(permisoAmbientalSeleccionado.getEnteResponsable());
			entityPermiso.setFechaProyecto(fecha.format(permisoAmbientalSeleccionado.getFechaRegistro()));
			entityPermiso.setNombreProyecto(permisoAmbientalSeleccionado.getNombre());
			entityPermiso.setProponente(permisoAmbientalSeleccionado.getNombreProponente());

			// verificar para los de 4 categorias remitente
			CertificadoInterseccion certificadoInterseccion = certificadoInterseccionFacade
					.getCertificadoInterseccion(permisoAmbientalSeleccionado
							.getId());
			entityPermiso.setFechaOficioCI(fecha.format(certificadoInterseccion
					.getFechaCreacion()));
			entityPermiso.setNroOficioCI(certificadoInterseccion.getCodigo());
			String comentarioInter = certificadoInterseccionFacade
					.getComentarioInterseccion(
							permisoAmbientalSeleccionado.getCodigo(),
							DocumentoPDFPlantillaHtml
									.getValorResourcePlantillaInformes("comentario_proyecto_no_intersecta"),
							DocumentoPDFPlantillaHtml
									.getValorResourcePlantillaInformes("comentario_proyecto_si_intersecta"));
			if (comentarioInter.contains("SI INTERSECTA"))
				comentarioInter = comentarioInter.substring(0,
						comentarioInter.length() - 1);
			entityPermiso.setComentarioInterseccion(comentarioInter);

			String provincia = permisoAmbientalSeleccionado
					.getProyectoLicenciamientoAmbiental().getPrimeraProvincia()
					.getNombre();
			entityPermiso.setProvinciaProyecto(provincia);

			Area area = permisoAmbientalSeleccionado.getProyectoLicenciamientoAmbiental().getAreaResponsable();
			String nombrefirma = "";
			if (area.getAreaAbbreviation().equals("DNPCA"))
				nombrefirma = "firmaDirectorPrevencion.png";
			else
				nombrefirma = "firma__" + area.getAreaAbbreviation().replace("/", "_") + ".png";
			
			entityPermiso.setUrlFirmaEnte(getFirma(nombrefirma));
			
			Usuario usuario_area = usuarioFacade.buscarResponsableArea(area);
			String nombre_usuario=usuario_area.getPersona().getNombre();
			
			entityPermiso.setNombreAutoridad(nombre_usuario);

			if (entityPermiso.getTxtEstadoPagoProyecto() == null) {
				entityPermiso.setTxtEstadoPagoProyecto("");
			} else {
				String estado = generarHtml(
						entityPermiso.getTxtEstadoPagoProyecto(),
						entityPermiso, null);
				entityPermiso.setTxtEstadoPagoProyecto(estado);
			}
			if (entityPermiso.getTxtEstadoProyecto() == null) {
				entityPermiso.setTxtEstadoProyecto("");
			} else {
				String estado = generarHtml(
						entityPermiso.getTxtEstadoProyecto(), entityPermiso,
						null);
				entityPermiso.setTxtEstadoProyecto(estado);
			}

			// generar QR firmaDirector
			entityPermiso.setQrCode(getCodigoQrInfo());
			
			entityPermiso.setQrCodeFirma(getCodigoQrFirma());

		} catch (Exception exception) {
			throw exception;
		}
	}

	public void getInfoDocumento4Categorias(){
		try {
			DateFormat fecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
        
			entityPermiso.setFechaEmision(fecha.format(new Date()));
			entityPermiso.setCodigoProyecto(permisoAmbientalSeleccionado.getCodigo());
			entityPermiso.setFechaProyecto(fecha.format(permisoAmbientalSeleccionado.getFechaRegistro()));
			entityPermiso.setNombreProyecto(permisoAmbientalSeleccionado.getNombre());
			entityPermiso.setProponente(permisoAmbientalSeleccionado.getNombreProponente());
			entityPermiso.setProvinciaProyecto(permisoAmbientalSeleccionado.getProvincia());
			
			List<Object[]> listaEnte = permisoAmbientalFacade.getEnteResponsable4cat(permisoAmbientalSeleccionado.getProvincia(), permisoAmbientalSeleccionado.getEstrategico(), permisoAmbientalSeleccionado.getFuente());
			if (listaEnte != null && listaEnte.size() > 0) {
				entityPermiso.setEnteResponsable(listaEnte.get(0)[4].toString());
				entityPermiso.setNombreAutoridad(listaEnte.get(0)[1].toString() + " " + listaEnte.get(0)[2].toString());
			}
			
			String comentarioInter = "";
			List<Object[]> listaInterseccion = permisoAmbientalFacade.getInterseccion4cat(permisoAmbientalSeleccionado.getCodigo(),  permisoAmbientalSeleccionado.getFuente());
			if (listaInterseccion != null && listaInterseccion.size() > 0) {
				Object[] interseccion = listaInterseccion.get(0);
				if(interseccion[1].toString().equals("SI")){
					comentarioInter = "<strong>SI INTERSECTA</strong> con: " + interseccion[2].toString() + ".";
				} else {
					comentarioInter = DocumentoPDFPlantillaHtml.getValorResourcePlantillaInformes("comentario_proyecto_no_intersecta");
				}
			}
			
			entityPermiso.setComentarioInterseccion(comentarioInter);
			
			String nombrefirma = "firma__" + permisoAmbientalSeleccionado.getAbreviacionArea().replace("/", "_") + ".png";
			entityPermiso.setUrlFirmaEnte(getFirma(nombrefirma));

			if(entityPermiso.getTxtEstadoProyecto()== null) {
				entityPermiso.setTxtEstadoProyecto("");
			} else{
				String estado = generarHtml(entityPermiso.getTxtEstadoProyecto(), entityPermiso, null);
				entityPermiso.setTxtEstadoProyecto(estado);
			}
			
			//generar QR
			entityPermiso.setQrCode(getCodigoQrInfo());
			
			entityPermiso.setQrCodeFirma(getCodigoQrFirma());
			
		} catch (Exception exception) {
			
		}
	}

	private static String generarHtml(final String cadenaHtml,
			final Object entityInforme, String textoNull) {
		try {
			String buf = cadenaHtml;
			List<String> listaTags = new ArrayList<>();
			Pattern pa = Pattern.compile("\\$F[{]\\w+[}$]");
			Matcher mat = pa.matcher(buf);
			while (mat.find()) {
				listaTags.add(mat.group());
			}
			Map<String, Object> mapa = new HashMap<>();
			Class<?> clase = entityInforme.getClass();
			Method[] campos = clase.getMethods();
			for (Method f : campos) {
				if (f.getName().startsWith("get")) {
					String metodo = "$F{" + f.getName().replace("get", "")
							+ "}";
					for (String s : listaTags) {
						if (s.equalsIgnoreCase(metodo)) {
							mapa.put(s, f.invoke(entityInforme, null));
							break;
						}
					}
				}
			}
			for (Map.Entry<String, Object> m : mapa.entrySet()) {
				buf = buf
						.replace(
								m.getKey(),
								m.getValue() == null ? "<span style='color:red'>INGRESAR</span>"
										: m.getValue().toString());
			}

			return buf;
		} catch (Exception e) {
			LOG.error(e, e);
			return "";
		}
	}

	private String getFirma(String nombrefirma) throws IOException {
		if (nombrefirma != null) {
			byte[] firma = permisoAmbientalFacade.getFirmaEnteResponsable(nombrefirma);

			ByteArrayInputStream inputStreamFirma = new ByteArrayInputStream(
					firma);
			BufferedImage bImageFirma = ImageIO.read(inputStreamFirma);
			File imageFile = new File(nombrefirma);
			ImageIO.write(bImageFirma, "png", imageFile);

			return nombrefirma;
		}
		return null;
	}

	private String getCodigoQrInfo() {
		DateFormat fecha = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, new Locale("es"));

		String qRContent = "Trámite: " + entityPermiso.getCodigoProyecto();
		qRContent += "\nFecha expedición: " + fecha.format(new Date());
		qRContent += "\nAutoridad: " + entityPermiso.getNombreAutoridad();
		qRContent += "\nEnte: " + entityPermiso.getEnteResponsable();
		qRContent += "\nProponente: " + entityPermiso.getProponente();

		if (Constantes.getDocumentosBorrador()) {
			qRContent = "Documento Sin Validez \n " + qRContent;
		}

		String nombreImagen = entityPermiso.getCodigoProyecto().replace("/", "-") + "-qr.png";
		String imageFileSrc = nombreImagen;
		JsfUtil.writeQRCode(qRContent, imageFileSrc, 128, 128);

		return imageFileSrc;
	}
	
	private String getCodigoQrFirma() {
		String qRContent = entityPermiso.getNombreAutoridad();
		qRContent += "\n " + entityPermiso.getEnteResponsable();

		if (Constantes.getDocumentosBorrador()) {
			qRContent = "Documento Sin Validez \n " + qRContent;
		}

		String nombreImagen = entityPermiso.getCodigoProyecto().replace("/", "-") + "-qr-firma.png";
		String imageFileSrc = nombreImagen;
		JsfUtil.writeQRCode(qRContent, imageFileSrc, 128, 128);

		return imageFileSrc;
	}

}
