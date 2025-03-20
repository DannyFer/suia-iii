package ec.gob.ambiente.control.aprobacionrequisitostecnicos.oficios.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.services.MaeLicenseResponse;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.BuscarProyectoFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeOficioArtFacade;
import ec.gob.ambiente.suia.domain.InformeTecnicoAproReqTec;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.OficioObservacionesReqTec;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityOficioObservReqTec;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class OficioObservacionRtBean implements Serializable {

	public static final String TEMP_CONST_NOMBRE_ESQUEMA_SUIA = "suia_iii";
	public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;
	public static final String SEQ_NUMERO_REPORTE_OFICIO_ORT = "seq_number_office_observations_art";
	private static final long serialVersionUID = 165683211928358047L;
	private final Logger LOG = Logger.getLogger(OficioObservacionRtBean.class);
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;
	@Getter
	@Setter
	private File oficioPdf;
	@Getter
	@Setter
	private byte[] archivoOficioObservaciones;
	@Getter
	@Setter
	private String informePath;
	@Getter
	@Setter
	private String nombreReporte;
	@Getter
	@Setter
	private OficioObservacionesReqTec oficioArt;
	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;
	@Setter
	@Getter
	private MaeLicenseResponse registroAmbiental;
	@EJB
	private InformeOficioArtFacade informeOficioFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private UsuarioFacade usuarioFace;
	@EJB
	private ObservacionesFacade observacionesFacade;
	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;
	@EJB
	private BuscarProyectoFacade buscarProyectoFacade;
	@Getter
	@Setter
	@ManagedProperty("#{proyectosBean}")
	private ProyectosBean proyectosBean;
	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@PostConstruct
	public void init() {
		try {
			this.tipoDocumento = new TipoDocumento();
			this.tipoDocumento.setId(TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_ART.getIdTipoDocumento());
			this.aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosFacade
					.recuperarCrearAprobacionRequisitosTecnicos(bandejaTareasBean.getProcessId(),
							loginBean.getUsuario());
			registroAmbiental = buscarProyectoFacade.buscarProyecto(aprobacionRequisitosTecnicos.getProyecto(), loginBean.getNombreUsuario());
			this.oficioArt = this.informeOficioFacade.obtenerOficioObservacionPorArt(this.tipoDocumento.getId(),
					this.aprobacionRequisitosTecnicos.getId());

			if (this.oficioArt == null) {
				this.oficioArt = new OficioObservacionesReqTec();
			}
			InformeTecnicoAproReqTec informeTecnicoArt = this.informeOficioFacade.obtenerInformeTecnicoPorArt(
					TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART, this.aprobacionRequisitosTecnicos.getId(),
					aprobacionRequisitosTecnicosFacade.getCantidadVecesObservadoInformacion(loginBean.getUsuario(),
							bandejaTareasBean.getProcessId()));
			SimpleDateFormat fechaInf = new SimpleDateFormat("dd/MM/YYYY");
			oficioArt.setFechaInfTecnico(fechaInf.format(informeTecnicoArt.getFechaCreacion()));
			oficioArt.setNumeroInfTecnico(informeTecnicoArt.getNumeroOficio());
			this.oficioArt.setAprobacionRequisitosTecnicos(this.aprobacionRequisitosTecnicos);
			this.oficioArt.setTipoDocumentoId(tipoDocumento.getId());
			oficioArt.setNumeroTramite(String.valueOf(aprobacionRequisitosTecnicos.getId()));
			oficioArt.setFechaTramite(fechaInf.format(aprobacionRequisitosTecnicos.getFechaCreacion()));

			// if (registroAmbiental.getProponenteNatural() != null
			// && !registroAmbiental.getProponenteNatural().equals("")) {
			// this.oficioArt.setEmpresa(registroAmbiental.getProponenteNatural());
			// } else {
			// this.oficioArt.setProponente(registroAmbiental.getProponenteNatural());
			// }
			visualizarOficio();
		} catch (Exception e) {
			this.LOG.error("Error al inicializar: OficioObservacionRtBean: ", e);
			JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
		}
	}

	public String visualizarOficio() {
		String pathPdf = null;
		try {
			PlantillaReporte plantillaReporte = this.informeOficioFacade.obtenerPlantillaReporte(getTipoDocumento()
					.getId());

			if (this.oficioArt.getNumeroOficio() == null) {
				BigInteger numeroSecuencia = this.informeOficioFacade.obtenerNumeroInforme(
						SEQ_NUMERO_REPORTE_OFICIO_ORT, "suia_iii");
				String numeroInforme = plantillaReporte.getCodigoProceso() + "-" + JsfUtil.getCurrentYear() + "-"
						+ (numeroSecuencia != null ? JsfUtil.rellenarCeros(numeroSecuencia.toString(), 6) : "N/A");
				this.oficioArt.setNumeroOficio(numeroInforme);
				this.oficioArt.setFechaOficio(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
			}

			this.oficioArt.setNombreProyecto(registroAmbiental.getNombreProyecto());
			this.oficioArt.setNumeroProyecto(registroAmbiental.getCodigoProyecto());
			EntityOficioObservReqTec entityOficio = new EntityOficioObservReqTec();
			if (aprobacionRequisitosTecnicos.isGestion() && aprobacionRequisitosTecnicos.isTransporte()) {
				entityOficio
						.setTipoTramite("Gestión de desechos peligrosos y/o especiales y Transporte de sustancias químicas peligrosas");
			} else if (aprobacionRequisitosTecnicos.isGestion()) {
				entityOficio.setTipoTramite("Gestión de desechos peligrosos y/o especiales");
			} else if (aprobacionRequisitosTecnicos.isTransporte()) {
				entityOficio.setTipoTramite("Transporte de sustancias químicas peligrosas");
			}
			entityOficio.setNumeroOficio(this.oficioArt.getNumeroOficio());
			entityOficio.setFechaOficio(oficioArt.getFechaOficio());
			if (oficioArt.getEmpresa().equals("")) {
				entityOficio.setProponente(oficioArt.getProponente());
			} else {
				entityOficio.setEmpresa(oficioArt.getEmpresa());
				entityOficio.setProponente(oficioArt.getEmpresa());
			}
			if (aprobacionRequisitosTecnicos.isGestion()) {
				if (aprobacionRequisitosTecnicos.getModalidades() != null
						|| !aprobacionRequisitosTecnicos.getModalidades().isEmpty()) {
					int i = 0;
					String modalidades = null;
					for (ModalidadGestionDesechos mgd : aprobacionRequisitosTecnicos.getModalidades()) {
						if (i == 0) {
							modalidades = mgd.getNombre();
						} else {
							modalidades += ", " + mgd.getNombre();
						}
						i++;
					}
					entityOficio.setModalidad(modalidades);
				}
			}
			List<ObservacionesFormularios> listaObservaciones = null;
			if (oficioArt.getObservaciones() == null) {
				listaObservaciones = this.observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
						aprobacionRequisitosTecnicos.getId(), AprobacionRequisitosTecnicos.class.getSimpleName());
			}
			String tablaObservaciones = "No existen observaciones";
			if (listaObservaciones != null || !listaObservaciones.isEmpty()) {
				tablaObservaciones = "<table  class='tablaConBorde' style='width:100%'>"
						+ "<tr> <th style=\"width:5%\">Item</th> <th style=\"width:25%\">Sección</th> <th style=\"width:7%\">Cumple</th>"
						+ "<th style=\"width:25%\">Campo</th> <th style=\"width:38%\">Observaciones / Criterio técnico</th> </tr>";
				int item = 0;
				for (ObservacionesFormularios observaciones : listaObservaciones) {
					String cumple;
					item++;
					if (observaciones.isObservacionCorregida())
						cumple = "Si";
					else
						cumple = "No";
					String[] secciones = observaciones.getSeccionFormulario().split("\\.");
					String seccionObtenida = secciones[secciones.length - 1].replaceAll("(.)(\\p{Lu})", "$1 $2");
					tablaObservaciones += "<tr> <td>" + item + "</td> <td>" + seccionObtenida + "</td> <td>" + cumple
							+ "</td> <td>" + observaciones.getDescripcion() + " </td>" + "</tr>";

				}
				tablaObservaciones += "</table>";
			}
			entityOficio.setCargo(oficioArt.getCargo());
			entityOficio.setTxtOpcional1(oficioArt.getTxtOpcional1());
			entityOficio.setNumeroTramite(oficioArt.getNumeroTramite());
			entityOficio.setFechaTramite(oficioArt.getFechaTramite());
			entityOficio.setNombreProyecto(oficioArt.getNombreProyecto());
			entityOficio.setNormativa(oficioArt.getNormativa());
			entityOficio.setTxtOpcional2(oficioArt.getTxtOpcional2());
			entityOficio.setNumeroInfTecnico(oficioArt.getNumeroInfTecnico());
			entityOficio.setFechaInfTecnico(oficioArt.getFechaInfTecnico());
			entityOficio.setObservaciones(tablaObservaciones);
			entityOficio.setTxtAdicional(oficioArt.getTxtAdicional());
			nombreReporte = "OficioAprobacionRequisitosTecnicos.pdf";
			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					Boolean.valueOf(true), entityOficio);

			setOficioPdf(informePdf);

			Path path = Paths.get(getOficioPdf().getAbsolutePath(), new String[0]);
			this.archivoOficioObservaciones = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(getOficioPdf().getName()));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(this.archivoOficioObservaciones);
			file.close();

			setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + getOficioPdf().getName()));

			pathPdf = informePdf.getParent();
		} catch (Exception e) {
			this.LOG.error("Error al visualizar el informe técnico", e);
			JsfUtil.addMessageError("Error al visualizar el informe técnico");
		}
		return pathPdf;
	}

	public void guardar() {
		try {
			visualizarOficio();
			this.oficioArt.setDocumentoOficioObservacion(UtilDocumento.generateDocumentPDFFromUpload(
					this.archivoOficioObservaciones, this.nombreReporte));
			this.informeOficioFacade.guardarOficioObservacionesArt(this.oficioArt, bandejaTareasBean.getProcessId(),
					bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART);
			JsfUtil.redirectTo("/control/aprobacionRequisitosTecnicos/documentos/oficioObservacionesArt.jsf");
		} catch (Exception e) {
			this.LOG.error("Error al guardar oficio de observaciones", e);
		}
	}
}
