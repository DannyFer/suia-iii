package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import ant.DatosMatricula;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.InformacionPatioManiobrasBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.RequisitosVehiculoBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformacionPatioManiobrasFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.RequisitosVehiculoFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RequisitosVehiculo;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * 
 * <b> Controlador de la pagina requisitos vehículo del proceso de aprobación de
 * requisitos técnicos. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 12/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class RequisitosVehiculoController implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = -3377168284712538567L;
	@Setter
	@Getter
	@ManagedProperty(value = "#{requisitosVehiculoBean}")
	private RequisitosVehiculoBean requisitosVehiculoBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{informacionPatioManiobrasBean}")
	private InformacionPatioManiobrasBean informacionPatioManiobrasBean;

	@EJB
	private InformacionPatioManiobrasFacade informacionPatioManiobrasFacade;

	@EJB
	private RequisitosVehiculoFacade requisitosVehiculoFacade;

	private static final Logger LOGGER = Logger.getLogger(RequisitosVehiculoController.class);

	@Setter
	@Getter
	private UploadedFile file;

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
	private void init() {
		requisitosVehiculoBean.setListaRequisitosVehiculo(new ArrayList<RequisitosVehiculo>());
		requisitosVehiculoBean.setListaRequisitosVehiculoEliminados(new ArrayList<RequisitosVehiculo>());
		requisitosVehiculoBean.setListaRequisitosVehiculoModificados(new ArrayList<RequisitosVehiculo>());
		try {
			informacionPatioManiobrasBean.setInformacionPatioManiobra(informacionPatioManiobrasFacade
					.getInformacionPatioManiobra(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
			if (existeIngresoInformacionPatio()) {
				obtenerListaRequisitosVehiculo();
			} else {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('importanteWdgt').show();");
			}
			calcularSiRequitosVehiculoEsRequerido();
			aprobacionRequisitosTecnicosBean.verART(RequisitosVehiculo.class.getName());
		} catch (ServiceException e) {
			JsfUtil.addMessageError(e.getMessage());
			LOGGER.error(e, e);
		}
	}

	private void calcularSiRequitosVehiculoEsRequerido() {
		requisitosVehiculoBean.setRequistosVehiculoRequerido(requisitosVehiculoFacade
				.isCamposPageRequitosVehiculoRequeridos(aprobacionRequisitosTecnicosBean
						.getAprobacionRequisitosTecnicos()));
	}

	private boolean existeIngresoInformacionPatio() {
		return (informacionPatioManiobrasBean.getInformacionPatioManiobra() != null);
	}

	private void obtenerListaRequisitosVehiculo() throws ServiceException {
		requisitosVehiculoBean.setListaRequisitosVehiculo(requisitosVehiculoFacade
				.getListaRequisitosVehiculo(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
		if (requisitosVehiculoBean.getListaRequisitosVehiculo() == null) {
			requisitosVehiculoBean.setListaRequisitosVehiculo(new ArrayList<RequisitosVehiculo>());
		}

	}

	public void agregarRequisito() {
		if (requisitosVehiculoBean.isVehiculoEncontrado()) {

			if (!requisitosVehiculoBean.isEditar()) {

				requisitosVehiculoBean.getRequisitoVehiculo().setAprobacionRequisitosTecnicos(
						aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
				requisitosVehiculoBean.getListaRequisitosVehiculo().add(requisitosVehiculoBean.getRequisitoVehiculo());
			}
			requisitosVehiculoBean.getListaRequisitosVehiculoModificados().add(
					requisitosVehiculoBean.getRequisitoVehiculo());
			requisitosVehiculoBean.setRequisitoVehiculo(new RequisitosVehiculo());
			requisitosVehiculoBean.setEditar(false);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

		} else {
			JsfUtil.addMessageError("El vehículo no ha sido encontrado.");
		}
	}

	private boolean validarSiVehiculoYaFueRegistrado() {

		for (RequisitosVehiculo rv : requisitosVehiculoBean.getListaRequisitosVehiculo()) {
			if (rv.getNumeroPlaca().equals(requisitosVehiculoBean.getNumPlaca())) {
				return true;
			}
		}
		return false;
	}

	private boolean isPosibleAgregarVehiculo() {
		return true; /*requisitosVehiculoBean.getListaRequisitosVehiculo().size() < informacionPatioManiobrasBean
				.getInformacionPatioManiobra().getNumeroVehiculos() ? true : false;*/
	}

	private void validarNumeroVehiculosIngresados() throws ServiceException {
		RequestContext context = RequestContext.getCurrentInstance();
		/*if (requisitosVehiculoBean.getListaRequisitosVehiculo().size() < informacionPatioManiobrasBean
				.getInformacionPatioManiobra().getNumeroVehiculos()) {
			context.addCallbackParam("numVehiculos", true);
		} else {*/
			context.addCallbackParam("numVehiculos", false);
			requisitosVehiculoFacade.guardarPaginaComoCompleta(aprobacionRequisitosTecnicosBean
					.getAprobacionRequisitosTecnicos());
		//}

	}

	public void remover(RequisitosVehiculo requisitosVehiculo) {
		requisitosVehiculoBean.getListaRequisitosVehiculo().remove(requisitosVehiculo);
		requisitosVehiculoBean.getListaRequisitosVehiculoEliminados().add(requisitosVehiculo);
	}

	public void seleccionarRequisito(RequisitosVehiculo requisitosVehiculo) {
		requisitosVehiculoBean.setRequisitoVehiculo(requisitosVehiculo);
		requisitosVehiculoBean.setEditar(true);
		requisitosVehiculoBean.setNumPlaca(requisitosVehiculoBean.getRequisitoVehiculo().getNumeroPlaca());
		requisitosVehiculoBean.setVehiculoEncontrado(true);
		requisitosVehiculoBean.setHabilitarCertificadoCalibracion(requisitosVehiculo
				.getDocumentoCertificadoCalibracionTanque() != null
				&& requisitosVehiculo.getDocumentoCertificadoCalibracionTanque().getNombre() != null ||
				requisitosVehiculoFacade
						.isTipoVehiculoParaCertificadoCalibracion(requisitosVehiculoBean.getRequisitoVehiculo()));
	}

	public void prepararNuevo() {
		requisitosVehiculoBean.setEditar(false);
		requisitosVehiculoBean.setVehiculoEncontrado(false);
		requisitosVehiculoBean.setNumPlaca(null);
		requisitosVehiculoBean.setRequisitoVehiculo(new RequisitosVehiculo());
		requisitosVehiculoBean.setHabilitarCertificadoCalibracion(false);
		if (!requisitosVehiculoBean.isEditar() && isPosibleAgregarVehiculo()) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("maxNumVehiculos", false);
		} else {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("maxNumVehiculos", true);
		}
	}

	public void guardarPagina() {
		try {
			requisitosVehiculoFacade.guardar(requisitosVehiculoBean.getListaRequisitosVehiculoModificados(),
					bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId());
			if (!requisitosVehiculoBean.getListaRequisitosVehiculoEliminados().isEmpty())
				requisitosVehiculoFacade.eliminar(requisitosVehiculoBean.getListaRequisitosVehiculoEliminados());

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			if (requisitosVehiculoFacade.isPageRequitosVehiculoRequerida(aprobacionRequisitosTecnicosBean
					.getAprobacionRequisitosTecnicos())) {
				validarNumeroVehiculosIngresados();
			} else {
				requisitosVehiculoFacade.guardarPaginaComoCompleta(aprobacionRequisitosTecnicosBean
						.getAprobacionRequisitosTecnicos());
			}

		} catch (ServiceException e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Error al guardar");
		} catch (CmisAlfrescoException e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al cargar los documentos al alfresco.");
		}

	}

	public void handleFileUpload(FileUploadEvent event) {
		Map<String, Object> att = event.getComponent().getAttributes();
		int indice = new Integer(att.get("indice").toString());
		file = event.getFile();
		switch (indice) {
		case 0:
			requisitosVehiculoBean.getRequisitoVehiculo().setDocumentoFoto(
					UtilDocumento.generateDocumentPDFFromUpload(file.getContents(), file.getFileName()));
			break;
		case 1:
			requisitosVehiculoBean.getRequisitoVehiculo().setDocumentoCertificadoInspeccionTecnicaVehicular(
					UtilDocumento.generateDocumentPDFFromUpload(file.getContents(), file.getFileName()));
			break;
		case 2:
			requisitosVehiculoBean.getRequisitoVehiculo().setDocumentoCertificadoCalibracionTanque(
					UtilDocumento.generateDocumentPDFFromUpload(file.getContents(), file.getFileName()));
			break;
		case 3:
			requisitosVehiculoBean.getRequisitoVehiculo().setDocumentoMatricula(
					UtilDocumento.generateDocumentPDFFromUpload(file.getContents(), file.getFileName()));
			break;
		default:
			LOGGER.error("Indice de archivo adjunto no esperado");
			JsfUtil.addMessageError("Ocurrió un error al tratar de subir el archivo, por favor comunicarse con mesa de ayuda.");
			break;
		}

	}

	public Documento generateDocumentFromUpload() {
		try {
			byte[] contenidoDocumento = file.getContents();
			Documento documento = new Documento();
			documento.setContenidoDocumento(contenidoDocumento);
			documento.setExtesion(".pdf");
			documento.setMime("application/pdf");
			documento.setNombre(file.getFileName());
			return documento;
		} catch (Exception e) {
			return null;
		}
	}

	public void descargar(int indice) throws IOException {
		try {
			switch (indice) {
			case 0:
				if (requisitosVehiculoBean.getRequisitoVehiculo().getDocumentoFoto() != null
						&& requisitosVehiculoBean.getRequisitoVehiculo().getDocumentoFoto().getNombre() != null) {
					UtilDocumento.descargarFile(requisitosVehiculoFacade.descargarFile(requisitosVehiculoBean
							.getRequisitoVehiculo().getDocumentoFoto()), requisitosVehiculoBean.getRequisitoVehiculo()
							.getDocumentoFoto().getNombre());
				} else {
					JsfUtil.addMessageInfo("No existe el archivo.");
				}
				break;
			case 1:
				UtilDocumento.descargarFile(requisitosVehiculoFacade.descargarFile(requisitosVehiculoBean
						.getRequisitoVehiculo().getDocumentoMatricula()), requisitosVehiculoBean.getRequisitoVehiculo()
						.getDocumentoMatricula().getNombre());
				break;
			case 2:
				UtilDocumento.descargarFile(requisitosVehiculoFacade.descargarFile(requisitosVehiculoBean
						.getRequisitoVehiculo().getDocumentoCertificadoInspeccionTecnicaVehicular()),
						requisitosVehiculoBean.getRequisitoVehiculo()
								.getDocumentoCertificadoInspeccionTecnicaVehicular().getNombre());
				break;
			case 3:
				UtilDocumento.descargarFile(requisitosVehiculoFacade.descargarFile(requisitosVehiculoBean
						.getRequisitoVehiculo().getDocumentoCertificadoCalibracionTanque()), requisitosVehiculoBean
						.getRequisitoVehiculo().getDocumentoCertificadoCalibracionTanque().getNombre());
				break;

			default:
				LOGGER.error("Indice de archivo adjunto no esperado para descargar el adjunto");
				JsfUtil.addMessageError("Ocurrió un error al tratar de descargar el archivo, por favor comunicarse con mesa de ayuda.");
				break;
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}

	}

	public void buscar() {
		try {
			DatosMatricula datos = requisitosVehiculoFacade.buscarVehiculo(requisitosVehiculoBean.getNumPlaca()
					.toUpperCase());
			requisitosVehiculoBean.setVehiculoEncontrado(datos != null && datos.getAnio() != null);
			if (!validarSiVehiculoYaFueRegistrado() && requisitosVehiculoBean.isVehiculoEncontrado()) {
				requisitosVehiculoBean.getRequisitoVehiculo().setNumeroPlaca(datos.getPlaca());
				requisitosVehiculoBean.getRequisitoVehiculo().setAnioFabriacacion(datos.getAnio());
				requisitosVehiculoBean.getRequisitoVehiculo().setClase(datos.getClase());
				requisitosVehiculoBean.getRequisitoVehiculo().setNumeroChasis(datos.getChasis());
				requisitosVehiculoBean.getRequisitoVehiculo().setNumeroMotor(datos.getMotor());
				requisitosVehiculoBean.getRequisitoVehiculo().setTipo(datos.getTipo());
				requisitosVehiculoBean.setHabilitarCertificadoCalibracion(requisitosVehiculoFacade
						.isTipoVehiculoParaCertificadoCalibracion(requisitosVehiculoBean.getRequisitoVehiculo()));

			} else {
				if (validarSiVehiculoYaFueRegistrado()) {
					JsfUtil.addMessageError("El vehículo con esas placas ya se encuentra registrado.");
					return;
				} else {
					JsfUtil.addMessageError("No se pudo encontrar el vehículo.");
				}
				limpiar();
			}

		} catch (ServiceException e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("En este momento no se puede hacer la busqueda del vehiculo intente mas tarde o comuniquese con mesa de ayuda.");
		}

	}

	public void limpiar() {
		requisitosVehiculoBean.setVehiculoEncontrado(false);
		requisitosVehiculoBean.getRequisitoVehiculo().setAnioFabriacacion(null);
		requisitosVehiculoBean.getRequisitoVehiculo().setClase(null);
		requisitosVehiculoBean.getRequisitoVehiculo().setNumeroChasis(null);
		requisitosVehiculoBean.getRequisitoVehiculo().setNumeroMotor(null);
		requisitosVehiculoBean.getRequisitoVehiculo().setTipo(null);

	}

}
