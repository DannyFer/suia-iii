package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.dto.EntityDocumentoResponsabilidad;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.CatalogoGeneralEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.CatalogoGeneralEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.CodigoTasa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasFacade;
import ec.gob.ambiente.suia.recaudaciones.model.Cuentas;
import ec.gob.ambiente.suia.recaudaciones.model.EstadosNut;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.SolicitudUsuario;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.recaudaciones.model.TarifasNUT;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarPdf;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;
import lombok.Getter;
import lombok.Setter;

@ViewScoped
@ManagedBean
public class PagoTasaRevisionEsIAController {

	private static final Logger LOG = Logger
			.getLogger(PagoTasaRevisionEsIAController.class);

	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private DocumentosImpactoEstudioFacade documentosEstudioFacade;
	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	@EJB
	private TarifasFacade tarifasFacade;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private AsignarTareaFacade asignarTareaFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;

	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private CatalogoGeneralEsIAFacade catalogoGeneralEsIAFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private double valorAPagar;

	@Getter
	@Setter
	private boolean visualizarPopup = false;

	@Getter
	@Setter
	private String mensaje;

	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;

	private String tramite;

	private Map<String, Object> variables;

	@Getter
	@Setter
	private DocumentoEstudioImpacto documento;

	@Getter
	@Setter
	private Double valorContrato;

	@Getter
	@Setter
	public Boolean generarNUT;

	@Getter
	@Setter
	public DocumentoEstudioImpacto documentoEiaNut;

	@Getter
	@Setter
	private List<DocumentoEstudioImpacto> documentosNUT;

	@Getter
	@Setter
	private List<InstitucionFinanciera> institucionesFinancieras;
	@Getter
	@Setter
	private List<TransaccionFinanciera> transaccionesFinancieras;
	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinanciera;
	@Getter
	@Setter
	public Boolean cumpleMontoProyecto = false;
	@Getter
	@Setter
	private boolean eligioEnte;
	@Getter
	@Setter
	private List<InstitucionFinanciera> entesAcreditados;

	private String identificadorMotivo;
	@Getter
	@Setter
	public Boolean cumpleMonto = false;
	@Getter
	@Setter
	public double montoTotal;

	@Getter
	@Setter
	private Boolean persist = false;
	@Getter
	@Setter
	private String mensajeFinalizar;
	@Getter
	@Setter
	private Boolean condiciones = false;
	@Getter
	@Setter
	private Boolean ingresoCostoContrato = false;
	@Getter
	@Setter
	private Boolean token;
	@Getter
	@Setter
	private Boolean subido, descargado;
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoManual;
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoResponsabilidad;

	private PlantillaReporte plantillaReporte;

	@Getter
	@Setter
	private byte[] archivoInforme;

	private InformacionProyectoEia estudio;

	@Getter
	@Setter
	private String urlAlfresco, nombreBotonDialogo;
	
	@Getter
    @Setter
    public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	
	@Getter
	@Setter
	private boolean ambienteProduccion;

	@PostConstruct
	public void init() {
		try {

			variables = procesoFacade.recuperarVariablesProceso(
					JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);

			String codigoTramite = "";
			if (proyectosBean.getProyecto() != null) {
				codigoTramite = proyectosBean.getProyecto().getCodigo();
			} else {
				codigoTramite = bandejaTareasBean.getTarea().getProcedure();
			}
			
			nombreBotonDialogo = "Generar";

			token = true;
			if(!ambienteProduccion){
				verificaToken();
			}			

			codigoTramite = tramite;

			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);

			estudio = informacionProyectoEIACoaFacade
					.obtenerInformacionProyectoEIAPorProyecto(proyecto);

			if (estudio != null) {
				if (estudio.getValorAPagar() != null) {
					valorAPagar = estudio.getValorAPagar();
				}
				if (estudio.getValorContrato() != null) {
					valorContrato = estudio.getValorContrato();
				}
			}

			transaccionFinanciera = new TransaccionFinanciera();

			if (codigoTramite != null) {
				generarNUT = true;
			}
			transaccionesFinancieras = new ArrayList<>();

			if (proyectosBean.getProyectoRcoa().getAreaResponsable().getTipoEnteAcreditado() != null) {
				if (proyectosBean.getProyectoRcoa().getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
					institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC();
				} else {
					institucionesFinancieras = institucionFinancieraFacade.obtenerPorNombre(proyecto.getAreaResponsable().getAreaName());
					if (institucionesFinancieras == null)
						JsfUtil.addMessageError("No se encontró institución financiara para: "
								+ proyecto.getAreaResponsable().getAreaName());
				}
			} else {
				institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC();
			}

			if (institucionesFinancieras != null
					&& institucionesFinancieras.size() > 0)
				transaccionFinanciera
						.setInstitucionFinanciera(institucionesFinancieras
								.get(0));

			transaccionesFinancieras = transaccionFinancieraFacade
					.cargarTransaccionesRcoa(proyectosBean.getProyectoRcoa()
							.getId(), bandejaTareasBean.getTarea()
							.getProcessInstanceId(), bandejaTareasBean
							.getTarea().getTaskId());

			documentosNUT = new ArrayList<>();
			List<NumeroUnicoTransaccional> listNUTXTramite = numeroUnicoTransaccionalFacade
					.listNUTActivoPorTramite(codigoTramite);
			if (listNUTXTramite != null && listNUTXTramite.size() > 0) {
				for (NumeroUnicoTransaccional nut : listNUTXTramite) {
					List<DocumentoEstudioImpacto> comprobantes = documentosEstudioFacade
							.documentoXTablaIdXIdDocLista(
									nut.getSolicitudUsuario().getId(),
									"NUT RECAUDACIONES",
									TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);

					if (comprobantes.size() > 0) {
						documentosNUT.addAll(comprobantes);
					}
				}
			}

			if (transaccionesFinancieras.isEmpty())
				persist = true;

			if (transaccionesFinancieras.size() > 0) {
				cumpleMonto = cumpleMonto();
			}

			documento = documentosEstudioFacade.documentoXTablaIdXIdDoc(
					estudio.getId(),
					EstudioImpactoAmbiental.class.getSimpleName(),
					TipoDocumentoSistema.EIA_CONTRATO_VALOR_ELABORACION);
			
			documentoResponsabilidad = documentosEstudioFacade
					.documentoXTablaIdXIdDoc(
							estudio.getId(),
							InformacionProyectoEia.class.getSimpleName(),
							TipoDocumentoSistema.EIA_RESPONSABLIDAD_INFORMACION_CONTENIDA);

			if (persist)
				cumpleMontoProyecto = true;
			
			subido = false;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generarNUT() throws Exception {

		List<NumeroUnicoTransaccional> listNUTXTramite = new ArrayList<NumeroUnicoTransaccional>();
		NumeroUnicoTransaccional numeroUnicoTransaccional;
		TarifasNUT tarifasNUT = new TarifasNUT();
		SolicitudUsuario solicitudUsuario = new SolicitudUsuario();
		solicitudUsuario.setUsuario(loginBean.getUsuario());

		String codigoTramite = "";

		codigoTramite = tramite;

		listNUTXTramite = numeroUnicoTransaccionalFacade
				.listNUTActivoPorTramite(codigoTramite);
		if (listNUTXTramite != null && listNUTXTramite.size() > 0) {
			for (NumeroUnicoTransaccional nut : listNUTXTramite) {
				if (nut.getEstadosNut().getId() == 5) {
					nut.setNutFechaActivacion(new Date());
					nut.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(
							new Date(), 3));
					nut.setEstadosNut(new EstadosNut(2));
					crudServiceBean.saveOrUpdate(nut);
					JsfUtil.addMessageWarning("Su trámite está caducado, se activo nuevamente por 3 días, por favor revisar.");
				}
			}
			JsfUtil.addMessageWarning("Usted ya tiene generado un Número Único de Trámite, por favor descargue en documentos del trámite.");
			return;
		}

		Tarifas tarifa = tarifasFacade
				.buscarTarifasPorCodigo(CodigoTasa.REGISTRO_GENERADOR_DESECHOS
						.getDescripcion());
		if (tarifa == null) {
			JsfUtil.addMessageError("Ocurrió un error al generar el NUT. Por favor comunicarse con mesa de ayuda.");
			return;
		}

		solicitudUsuario.setSolicitudDescripcion("Pagos por el trámite: "
				+ codigoTramite);
		solicitudUsuario.setSolicitudCodigo(JsfUtil.getBean(
				GenerarNUTController.class).generarCodigoSolicitud());

		crudServiceBean.saveOrUpdate(solicitudUsuario);
		Integer numeroDocumento = 1;
		numeroUnicoTransaccional = new NumeroUnicoTransaccional();
		numeroUnicoTransaccional.setNutCodigo(secuenciasFacade
				.getNextValueDedicateSequence("NUT_CODIGO", 10));
		numeroUnicoTransaccional.setNutFechaActivacion(new Date());
		numeroUnicoTransaccional.setEstadosNut(new EstadosNut(2));
		numeroUnicoTransaccional.setNutFechaDesactivacion(JsfUtil
				.sumarDiasAFecha(new Date(), 3));
		numeroUnicoTransaccional.setSolicitudUsuario(solicitudUsuario);
		numeroUnicoTransaccional.setCuentas(new Cuentas(1));
		numeroUnicoTransaccional.setNutValor(valorAPagar);
		numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
		crudServiceBean.saveOrUpdate(numeroUnicoTransaccional);

		tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
		tarifasNUT.setTarifas(tarifa);

		tarifasNUT.setCantidad(1);
		tarifasNUT.setValorUnitario(valorAPagar);
		crudServiceBean.saveOrUpdate(tarifasNUT);
		JsfUtil.getBean(GenerarNUTController.class).setSolicitudUsuario(
				solicitudUsuario);
		byte[] contenidoDocumento = JsfUtil.getBean(GenerarNUTController.class)
				.generarDocumentoNutRcoa(numeroUnicoTransaccional,
						solicitudUsuario, numeroDocumento);

		DocumentoEstudioImpacto documentoPago = new DocumentoEstudioImpacto();
		documentoPago.setContenidoDocumento(contenidoDocumento);
		documentoPago.setMime("application/pdf");
		documentoPago.setIdTable(solicitudUsuario.getId());
		documentoPago.setNombreTabla("NUT RECAUDACIONES");
		documentoPago.setNombre("ComprobantePago" + numeroDocumento + ".pdf");
		documentoPago.setExtesion(".pdf");
		documentoPago = documentosEstudioFacade
				.guardarDocumentoAlfrescoSinProyecto(
						numeroUnicoTransaccional.getNutCodigoProyecto() != null ? numeroUnicoTransaccional
								.getNutCodigoProyecto() : solicitudUsuario
								.getSolicitudCodigo(), "RECAUDACIONES", null,
						documentoPago,
						TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);

		numeroDocumento++;
		List<String> listaArchivos = new ArrayList<String>();
		listaArchivos = JsfUtil.getBean(GenerarNUTController.class)
				.getListPathArchivos();

		JsfUtil.getBean(GenerarNUTController.class).enviarNotificacionPago(
				solicitudUsuario.getUsuario(), codigoTramite, listaArchivos);

		JsfUtil.addMessageInfo("Se ha enviado un correo electrónico con los comprobantes para realizar el pago.");
	}

	public void uploadListenerDocumento(FileUploadEvent event) {
		documento = this.uploadListener(event, EstudioImpactoAmbiental.class,
				"pdf");
	}

	private DocumentoEstudioImpacto uploadListener(FileUploadEvent event,
			Class<?> clazz, String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentoEstudioImpacto documento = crearDocumento(contenidoDocumento,
				clazz, extension);
		documento.setNombre(event.getFile().getFileName());
		documento.setIdTable(estudio.getId());

		return documento;
	}

	public DocumentoEstudioImpacto crearDocumento(byte[] contenidoDocumento,
			Class<?> clazz, String extension) {
		DocumentoEstudioImpacto documento = new DocumentoEstudioImpacto();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setExtesion("." + extension);

		documento.setMime(extension == "pdf" ? "application/pdf"
				: "application/vnd.ms-excel");
		return documento;
	}

	public StreamedContent descargar(DocumentoEstudioImpacto documento) {
		DefaultStreamedContent content = null;
		try {
			if (documento.getAlfrescoId() != null) {
				byte[] documentoContent = documentosEstudioFacade
						.descargar(documento.getAlfrescoId());

				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	/**
	 * Nombre:SUIA Descripción: obtener entes acreditados para pagos
	 * ParametrosIngreso: PArametrosSalida: Fecha:17/09/2015
	 **/
	public void obtenerEntesAcreditados() {
		try {
			if (transaccionFinanciera.getInstitucionFinanciera() != null
					&& transaccionFinanciera.getInstitucionFinanciera()
							.getCodigoInstitucion().equals("2")) {
				eligioEnte = true;
				entesAcreditados = institucionFinancieraFacade
						.obtenerEntesAcreditados();
				RequestContext.getCurrentInstance().update(
						":#{p:component('entidadBancaria')}");
				if (entesAcreditados == null) {
					JsfUtil.addMessageError("No se obtuvieron entes acreditados disponibles, intente más tarde nuevamente");
				}
			} else {
				eligioEnte = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("Error en PagoServiciosBean", e);
			JsfUtil.addMessageError("No se obtuvieron entes acreditados disponibles, intente más tarde nuevamente");
		}
	}

	public void calculoCincoPorciento() {
		try {

			if (valorContrato != null && !valorContrato.equals(0)) {
				
				Double porcentaje = new Double(0);
				
				int categorizacion = proyecto.getCategorizacion();
				if(categorizacion == 3){					
					CatalogoGeneralEsIA valor = catalogoGeneralEsIAFacade.buscarPorCodigo("valor_proyectos_impacto_moderado");					
					porcentaje = Double.valueOf(valor.getDescripcion());
					
				}else if(categorizacion == 4){
					CatalogoGeneralEsIA valor = catalogoGeneralEsIAFacade.buscarPorCodigo("valor_proyectos_alto_impacto");
					porcentaje = Double.valueOf(valor.getDescripcion());
				}else{
					JsfUtil.addMessageError("Ocurrió un error .Por favor comunicarse con mesa de ayuda.");
					System.out.println("Error en la categorizacion");
					return;
				}				
				
				valorAPagar = (valorContrato * porcentaje) / 100;
				ingresoCostoContrato = true;
				cumpleMontoProyecto = false;
				montoTotal = valorAPagar;
				
				cumpleMonto = cumpleMonto();
			} else {
				JsfUtil.addMessageError("Debe ingresar un valor mayor a cero");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void guardarTransaccion(Integer pagoProyecto) {

		if (transaccionFinanciera.getInstitucionFinanciera() != null
				&& transaccionFinanciera.getNumeroTransaccion() != "") {
			try {
				String codigoTramite = "";
				if (proyectosBean.getProyecto() != null
						&& proyectosBean.getProyecto().getCodigo() != null) {
					codigoTramite = proyectosBean.getProyecto().getCodigo();
				} else {
					codigoTramite = bandejaTareasBean.getTarea().getProcedure();
				}

				List<NumeroUnicoTransaccional> listaNutsTramite = numeroUnicoTransaccionalFacade
						.listNUTActivoPorTramite(codigoTramite);
				if (listaNutsTramite != null && listaNutsTramite.size() > 0) {
					// verificacion de NUT en estado pagado
					NumeroUnicoTransaccional nut = listaNutsTramite.get(0);
					if (!nut.getEstadosNut().getId().equals(3)) {
						JsfUtil.addMessageWarning("El número NUT "
								+ nut.getNutCodigo() + " aún no ha sido pagado");
						return;
					}

					// verificación de que el #comprobante ingresado corresponda
					// con el numero de referencia de pago del NUT
					if (!nut.getBnfTramitNumber().equals(
							transaccionFinanciera.getNumeroTransaccion())) {
						JsfUtil.addMessageWarning("El número de comprobante "
								+ transaccionFinanciera.getNumeroTransaccion()
								+ " no se relaciona con el NUT generado");
						return;
					}
				}
				// fin recaudaciones

				if (existeTransaccion(transaccionFinanciera)) {
					JsfUtil.addMessageInfo("El número de comprobante: "
							+ transaccionFinanciera.getNumeroTransaccion()
							+ " ("
							+ transaccionFinanciera.getInstitucionFinanciera()
									.getNombreInstitucion()
							+ ") ya fue registrado por usted. Ingrese otro distinto.");
					transaccionFinanciera = new TransaccionFinanciera();
					return;
				} else {
					double monto = transaccionFinancieraFacade.consultarSaldo(
							transaccionFinanciera.getInstitucionFinanciera()
									.getCodigoInstitucion(),
							transaccionFinanciera.getNumeroTransaccion());

					if (monto == 0) {
						JsfUtil.addMessageError("El número de comprobante: "
								+ transaccionFinanciera.getNumeroTransaccion()
								+ " ("
								+ transaccionFinanciera
										.getInstitucionFinanciera()
										.getNombreInstitucion()
								+ ") no ha sido registrado.");
						transaccionFinanciera = new TransaccionFinanciera();
						return;
					} else {
						transaccionFinanciera.setMontoTransaccion(monto);
						transaccionFinanciera.setTipoPagoProyecto("Proyecto");

						if (proyectosBean.getProyecto() != null
								&& proyectosBean.getProyecto().getCodigo() != null) {
							transaccionFinanciera.setProyecto(proyectosBean
									.getProyecto());
						} else if (proyectosBean.getProyectoRcoa() != null) {
							transaccionFinanciera.setProyectoRcoa(proyectosBean
									.getProyectoRcoa());
						}
						transaccionFinanciera.setIdentificadorMotivo(null);

						transaccionesFinancieras.add(transaccionFinanciera);
						cumpleMonto = cumpleMonto();
						transaccionFinanciera = new TransaccionFinanciera();
						return;
					}
				}
			} catch (Exception e) {
				JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
				return;
			}
		} else {
			JsfUtil.addMessageWarning("Debe ingresar datos correctos para la transacción");
		}
	}

	private boolean existeTransaccion(
			TransaccionFinanciera _transaccionFinanciera) {

		for (TransaccionFinanciera transaccion : transaccionesFinancieras) {
			if (transaccion.getNumeroTransaccion().trim()
					.equals(_transaccionFinanciera.getNumeroTransaccion())
					&& transaccion
							.getInstitucionFinanciera()
							.getCodigoInstitucion()
							.trim()
							.equals(_transaccionFinanciera
									.getInstitucionFinanciera()
									.getCodigoInstitucion())) {
				return true;
			}
		}
		return false;
	}

	private boolean cumpleMonto() {
		double montoTotal = 0;
		for (TransaccionFinanciera transa : transaccionesFinancieras) {
			montoTotal += transa.getMontoTransaccion();
		}

		DecimalFormat decimalValorTramite = new DecimalFormat(".##");
		String x = "";

		x = decimalValorTramite.format(this.montoTotal).replace(",", ".");

		if (proyectosBean.getProyectoRcoa() != null
				&& proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() != null) {

			this.montoTotal = Double.valueOf(decimalValorTramite.format(
					this.valorAPagar).replace(",", "."));
			if (montoTotal >= this.montoTotal) {
				cumpleMontoProyecto = true;
				return true;
			}
			cumpleMontoProyecto = false;
			return false;
		}

		this.montoTotal = Double.valueOf(x);
		if (montoTotal >= this.montoTotal) {
			cumpleMontoProyecto = true;
			return true;
		}
		cumpleMontoProyecto = false;
		return false;
	}

	public void eliminarTransacion(TransaccionFinanciera transaccion)
			throws Exception {
		transaccionesFinancieras.remove(transaccion);
		cumpleMonto = cumpleMonto();
		persist = true;
	}

	public boolean validar() {

		if (valorAPagar > 0) {
			if (getTransaccionesFinancieras() == null
					|| getTransaccionesFinancieras().size() == 0) {
				JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
				return false;
			}

			if (!getCumpleMonto()) {
				JsfUtil.addMessageError("El monto de la transacción utilizada es insuficiente para completar el pago de tasas.");
				return false;
			}
		} else {
			if(valorContrato == null || valorContrato <= 0) {
				JsfUtil.addMessageError("El campo 'Valor total del contrato' es requerido.");
				return false;
			}
		}

		if (documento == null) {
			JsfUtil.addMessageError("Debe adjuntar el documento de contrato");
			return false;
		}

		return true;
	}

	public String completarTarea() {
		this.montoTotal = valorAPagar;

		DecimalFormat decimalValorTramite = new DecimalFormat(".##");
		String x = "";

		x = decimalValorTramite.format(this.montoTotal).replace(",", ".");

		this.montoTotal = Double.valueOf(x);

		if (!validar()) {
			return null;
		}

		executeBusinessLogic();

		return null;
	}

	public void mostrarDialogo() {
		RequestContext.getCurrentInstance().execute(
				"PF('continuarDialog').show();");
	}

	public void executeBusinessLogic() {

		try {
			if (subido && documentoManual != null && documentoManual.getId() == null
					&& documentoManual.getContenidoDocumento() != null) {
				documentosEstudioFacade
						.guardarDocumentoAlfrescoCA(
								proyecto.getCodigoUnicoAmbiental(),
								Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL,
								documentoManual,
								TipoDocumentoSistema.EIA_RESPONSABLIDAD_INFORMACION_CONTENIDA);
			}
			
			if (token) {
				if (!documentosEstudioFacade.verificarFirmaVersion(documentoResponsabilidad.getAlfrescoId())) {
					JsfUtil.addMessageError("El documento no está firmado electrónicamente.");
					return;
				}
			} else {
				if (!subido) {
					JsfUtil.addMessageError("Debe adjuntar el documento firmado.");
					return;
				}
			}
		} catch (Exception ex) {
			JsfUtil.addMessageError("Error al verificar documento firmado.");
			return;
		}

		if (persist) {
			String motivo = this.identificadorMotivo == null ? proyectosBean
					.getProyectoRcoa().getCodigoUnicoAmbiental() : this.identificadorMotivo;

			List<TransaccionFinanciera> transaccionesFinancierasProyecto = new ArrayList<TransaccionFinanciera>();
			for (TransaccionFinanciera transaccionFinanciera : getTransaccionesFinancieras()) {
				transaccionesFinancierasProyecto.add(transaccionFinanciera);
			}

			boolean pagoSatisfactorio = false;

			if (!pagoRealizado()) {
				if (transaccionesFinancierasProyecto.size() > 0) {
					pagoSatisfactorio = transaccionFinancieraFacade
							.realizarPago(montoTotal,
									transaccionesFinancierasProyecto, motivo);
					
					if (pagoSatisfactorio) {
						transaccionFinancieraFacade.guardarTransacciones(
								getTransaccionesFinancieras(), bandejaTareasBean
										.getTarea().getTaskId(), bandejaTareasBean
										.getTarea().getTaskName(), bandejaTareasBean
										.getTarea().getProcessInstanceId(),
								bandejaTareasBean.getTarea().getProcessId(),
								bandejaTareasBean.getTarea().getProcessName());
					}
					
				}

				for (TransaccionFinanciera transaccionFinanciera : transaccionesFinancierasProyecto) {
					try {
						NumeroUnicoTransaccional nut = new NumeroUnicoTransaccional();
						nut = numeroUnicoTransaccionalFacade
								.buscarNUTPorNumeroTramite(transaccionFinanciera
										.getNumeroTransaccion());
						if (nut != null) {
							nut.setNutUsado(true);
							crudServiceBean.saveOrUpdate(nut);
						}
					} catch (ec.gob.ambiente.suia.exceptions.ServiceException e) {
						e.printStackTrace();
					}
				}
			} else {
				pagoSatisfactorio = pagoRealizado();
			}

			if (pagoSatisfactorio) {
				estudio.setValorAPagar(valorAPagar);
				estudio.setValorContrato(valorContrato);

				informacionProyectoEIACoaFacade.guardar(estudio);

				finalizar();
			} else {
				JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
			}

		} else {
			
			estudio.setValorAPagar(valorAPagar);
			estudio.setValorContrato(valorContrato);
			
			informacionProyectoEIACoaFacade.guardar(estudio);

			JsfUtil.addMessageInfo("Pago satisfactorio.");
			finalizar();
		}
	}

	private boolean pagoRealizado() {
		List<TransaccionFinanciera> lista = transaccionFinancieraFacade
				.cargarTransacciones(bandejaTareasBean.getTarea()
						.getProcessInstanceId(), bandejaTareasBean.getTarea()
						.getTaskId());
		if (!lista.isEmpty())
			return true;
		return false;
	}

	public double Monto() {
		double montoTotal = 0;
		for (TransaccionFinanciera transa : transaccionesFinancieras) {
			montoTotal += transa.getMontoTransaccion();
		}
		return montoTotal;
	}

	private void finalizar() {
		try {
			if (documento.getId() == null
					&& documento.getContenidoDocumento() != null) {
				documentosEstudioFacade
						.guardarDocumentoAlfrescoCA(
								proyecto.getCodigoUnicoAmbiental(),
								Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL,
								documento,
								TipoDocumentoSistema.EIA_CONTRATO_VALOR_ELABORACION);
			}
			
			Map<String, Object> parametros = new HashMap<>();

			Area areaResponsable = proyecto.getAreaResponsable();
			String tipoRol = "role.esia.cz.tecnico.responsable";

			if (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
				CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();

				Integer idSector = actividadPrincipal.getTipoSector().getId();

				tipoRol = "role.esia.pc.tecnico.responsable.tipoSector." + idSector;
			} else if (!areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
				tipoRol = "role.esia.gad.tecnico.responsable";
			}

			String rolTecnico = Constantes.getRoleAreaName(tipoRol);

			// buscar usuarios por rol y area
			List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
			if (listaUsuario == null || listaUsuario.size() == 0) {
				JsfUtil.addMessageError("Ocurrió un error. Comuníquese con Mesa de Ayuda.");
				System.out.println("No se encontró técnico responsable en " + areaResponsable.getAreaName());
				return;
			}

			// recuperar tecnico de bpm y validar si el usuario existe en el
			// listado anterior
			Usuario tecnicoResponsable = null;
			String usrTecnico = (String) variables.get("tecnicoResponsable");
			Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
			if (usuarioTecnico != null
					&& usuarioTecnico.getEstado().equals(true)) {
				if (listaUsuario != null && listaUsuario.size() >= 0
						&& listaUsuario.contains(usuarioTecnico)) {
					tecnicoResponsable = usuarioTecnico;
				}
			}

			// si no se encontró el usuario se realiza la busqueda de uno nuevo
			// y se actualiza en el bpm
			if (tecnicoResponsable == null) {
				String proceso = Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL + "'' , ''" + Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2;
				List<Usuario> listaTecnicosResponsables = asignarTareaFacade
						.getCargaLaboralPorUsuariosProceso(listaUsuario,proceso);
				tecnicoResponsable = listaTecnicosResponsables.get(0);

				parametros.put("tecnicoResponsable",
						tecnicoResponsable.getNombre());
			}

			parametros.put("numeroRevision", 1); // al realizar el pago se envía a la primera revisión del tecnico
			parametros.put("esPrimeraRevision", true);

			// si el proyecto interseca con SNAP o Forestal buscar los usuarios
			// a los que se debe asignar para revision
			Map<String, Object> infoInterseccion = recuperarInfoInterseccion();
			if (infoInterseccion == null)
				return;

			parametros.putAll(infoInterseccion);
			
			
			boolean esPlantaCentral = false;
			boolean esGad = false;
			if (proyecto.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) 
				esPlantaCentral = true;
			else if (!proyecto.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) 
				esGad = true;
			
			parametros.put("esPlantaCentral", esPlantaCentral);
			parametros.put("esGad", esGad);

			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);

			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

			// enviar notificacion tecnico

			Object[] parametrosCorreoTecnicos = new Object[] {
					tecnicoResponsable.getPersona().getNombre(),
					proyecto.getNombreProyecto() };
			String notificacion = mensajeNotificacionFacade
					.recuperarValorMensajeNotificacion(
							"bodyNotificacionIngresoEsIA",
							parametrosCorreoTecnicos);

			Email.sendEmail(tecnicoResponsable, "Regularización Ambiental Nacional", notificacion, tramite, loginBean.getUsuario());

			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargado == true) {
			byte[] contenidoDocumento = event.getFile().getContents();

			documentoManual = new DocumentoEstudioImpacto();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(InformeTecnicoEsIA.class
					.getSimpleName());
			documentoManual.setIdTable(estudio.getId());

			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else {
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}

	public void generarResponsabilidad() {
		try {

			plantillaReporte = plantillaReporteFacade
					.getPlantillaReporte(TipoDocumentoSistema.EIA_RESPONSABLIDAD_INFORMACION_CONTENIDA);

			EntityDocumentoResponsabilidad entity = new EntityDocumentoResponsabilidad();

			String nombreOperador = "";
			String nombreRepresentante = "";
			Usuario usuarioOperador = proyecto.getUsuario();
			try {
				if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
					nombreOperador = usuarioOperador.getPersona().getNombre();
				} else {
					Organizacion organizacion = organizacionFacade
							.buscarPorRuc(usuarioOperador.getNombre());
					nombreOperador = organizacion.getNombre();
					nombreRepresentante = usuarioOperador.getPersona().getNombre();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			String actividadPrincipal = "";
			String codigoCiiu = "";

			ProyectoLicenciaCuaCiuu actividad = proyectoLicenciaCuaCiuuFacade
					.actividadPrincipal(proyecto);
			if (actividad != null && actividad.getId() != null) {
				actividadPrincipal = actividad.getCatalogoCIUU().getNombre();
				codigoCiiu = actividad.getCatalogoCIUU().getCodigo();
			}

			SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-YYYY");
			
			entity.setOperador(nombreOperador);
			entity.setFecha(formatoFecha.format(proyecto.getFechaCreacion()));
			entity.setNombreActividad(actividadPrincipal);
			entity.setCodigoCiiu(codigoCiiu);
			entity.setCodigo(proyecto.getCodigoUnicoAmbiental());
			entity.setNombreProyecto(proyecto.getNombreProyecto());
			entity.setArea(proyecto.getAreaResponsable());
			entity.setNombreRepresentante(nombreRepresentante);
			
			String displayRep = (nombreRepresentante.equals("")) ? "none" : "inline";
			String displayOperador = (nombreRepresentante.equals("")) ? "inline" : "none";
			
			entity.setDisplayRepresentante(displayRep);
			entity.setDisplayOperador(displayOperador);

			File informePdf = UtilGenerarPdf.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					"Documento de Responsabilidad", true, entity);

			String nombreReporte = "Documento de Responsabilidad.pdf";

			Path path = Paths.get(informePdf.getAbsolutePath());
			String reporteHtmlfinal = nombreReporte;
			archivoInforme = Files.readAllBytes(path);
			File archivoFinal = new File(
					JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(Files.readAllBytes(path));
			file.close();

			TipoDocumento tipoDoc = new TipoDocumento();
			tipoDoc.setId(TipoDocumentoSistema.EIA_RESPONSABLIDAD_INFORMACION_CONTENIDA
					.getIdTipoDocumento());

			DocumentoEstudioImpacto documento = new DocumentoEstudioImpacto();
			documento.setNombre(nombreReporte);
			documento.setExtesion(".pdf");
			documento.setMime("application/pdf");
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombreTabla(InformacionProyectoEia.class
					.getSimpleName());
			documento.setIdTable(estudio.getId());
			documento.setTipoDocumento(tipoDoc);
			documento.setIdProceso(bandejaTareasBean.getProcessId());

			documentoResponsabilidad = documentosEstudioFacade
					.guardarDocumentoAlfrescoCA(
							proyecto.getCodigoUnicoAmbiental(),
							Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL,
							documento,
							TipoDocumentoSistema.EIA_RESPONSABLIDAD_INFORMACION_CONTENIDA);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void guardarDocumentos() {
		try {
			if (!validar()) {
				return;
			}
			
			nombreBotonDialogo = "Aceptar";
			
			RequestContext.getCurrentInstance().update(":formPagoInformacion:btnAceptarDialog");

			if(documentoResponsabilidad == null)
				generarResponsabilidad();

			if (documentoResponsabilidad != null) {
				String cedulaOperador = "";
				
				Usuario usuarioOperador = proyecto.getUsuario();
				try {
					if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
						cedulaOperador = usuarioOperador.getNombre();
					} else {
						Organizacion organizacion = organizacionFacade
								.buscarPorRuc(usuarioOperador.getNombre());
						cedulaOperador = organizacion.getPersona().getPin();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}				
				
				String documentOffice = documentosEstudioFacade
						.direccionDescarga(documentoResponsabilidad);
				urlAlfresco = DigitalSign.sign(documentOffice, cedulaOperador);

				descargado = false;
				RequestContext.getCurrentInstance().execute(
						"PF('signDialog').show()");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public StreamedContent descargar() {
		DefaultStreamedContent content = null;
		try {

			byte[] documentoContent = null;
			
			if(documentoResponsabilidad != null  && documentoResponsabilidad.getContenidoDocumento() != null)
				documentoContent = documentoResponsabilidad.getContenidoDocumento();
			else if (documentoResponsabilidad != null && documentoResponsabilidad.getAlfrescoId() != null)
				documentoContent = documentosEstudioFacade.descargar(documentoResponsabilidad.getAlfrescoId());

			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent));
				content.setName(documentoResponsabilidad.getNombre());
			} else
				JsfUtil.addMessageError("Error al generar el archivo");

			descargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	public Map<String, Object> recuperarInfoInterseccion() {
		Map<String, Object> parametros = new HashMap<String, Object>();

		Boolean intersecaSnap = proyecto.getInterecaSnap();
		Boolean intersecaForestal = false;
		if (proyecto.getInterecaBosqueProtector())
			intersecaForestal = true;
		else if (proyecto.getInterecaPatrimonioForestal())
			intersecaForestal = true;

		Boolean interseca = false;
		if (intersecaForestal || intersecaSnap || proyecto.getRenocionCobertura())
			interseca = true;

		parametros.put("requierePronunciamientoPatrimonio", interseca);
		
		parametros.put("requierePronunciamientoSnap", intersecaSnap);
		parametros.put("requierePronunciamientoForestal", intersecaForestal);
		parametros.put("requierePronunciamientoInventario", proyecto.getRenocionCobertura());

		if (intersecaForestal || proyecto.getRenocionCobertura()) {
			Area areaResponsable = proyecto.getAreaResponsable();
			String tipoRol = (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) ? "role.esia.pc.tecnico.bosques" : "role.esia.cz.tecnico.bosques";
			
			if(areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC))
				areaResponsable = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES);
			else if (!areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
				UbicacionesGeografica ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto).getUbicacionesGeografica();
				areaResponsable = areaFacade.getAreaCoordinacionZonal(ubicacionPrincipal.getUbicacionesGeografica());
			}

			String rolTecnico = Constantes.getRoleAreaName(tipoRol);

			List<Usuario> listaUsuario = usuarioFacade
					.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
			if (listaUsuario == null || listaUsuario.size() == 0) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return null;
			}

			Usuario tecnicoBosques = null;
			String proceso = Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL + "'' , ''" + Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2;
			List<Usuario> listaTecnicosResponsables = asignarTareaFacade
					.getCargaLaboralPorUsuariosProceso(listaUsuario,proceso);
			tecnicoBosques = listaTecnicosResponsables.get(0);

			parametros.put("tecnicoBosques", tecnicoBosques.getNombre());
		}

		if (intersecaSnap) {
			Area areaResponsable = proyecto.getAreaResponsable();
			String tipoRol =  (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) ? "role.esia.pc.tecnico.conservacion" : "role.esia.cz.tecnico.conservacion";
			
			if(areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC))
				areaResponsable = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS);

			String rolTecnico = Constantes.getRoleAreaName(tipoRol);

			List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
			if (listaUsuario == null || listaUsuario.size() == 0) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return null;
			}

			Usuario tecnicoConservacion = null;
			String proceso = Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL + "'' , ''" + Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2;
			List<Usuario> listaTecnicosResponsables = asignarTareaFacade
					.getCargaLaboralPorUsuariosProceso(listaUsuario,proceso);
			tecnicoConservacion = listaTecnicosResponsables.get(0);

			parametros.put("tecnicoConservacion",
					tecnicoConservacion.getNombre());
		}

		return parametros;
	}

}
