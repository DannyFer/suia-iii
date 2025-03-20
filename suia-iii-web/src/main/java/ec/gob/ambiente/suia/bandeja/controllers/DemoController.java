package ec.gob.ambiente.suia.bandeja.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.demo.facade.DemoDocumentosFacade;
import ec.gob.ambiente.rcoa.demo.facade.DemoFacade;
import ec.gob.ambiente.rcoa.demo.facade.DemoRangoFechasFacade;
import ec.gob.ambiente.rcoa.demo.facade.MesaDeTrabajoFacade;
import ec.gob.ambiente.rcoa.demo.facade.NotaFacade;
import ec.gob.ambiente.rcoa.demo.model.Demo;
import ec.gob.ambiente.rcoa.demo.model.DemoDocumentos;
import ec.gob.ambiente.rcoa.demo.model.DemoRangoFechas;
import ec.gob.ambiente.rcoa.demo.model.MesaDeTrabajo;
import ec.gob.ambiente.rcoa.demo.model.Nota;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.suia.domain.EnvioNotificacionesMail;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificaciones.facade.EnvioNotificacionesMailFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped

public class DemoController {

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private DemoDocumentosFacade documentosFacade;

	@EJB
	private MesaDeTrabajoFacade mesaDeTrabajoFacade;

	@EJB
	private DemoRangoFechasFacade demoRangoFechasFacade;

	@EJB
	private NotaFacade notaFacade;

	@EJB
	private DemoFacade demoFacade;

	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;

	@EJB
	private EnvioNotificacionesMailFacade envioNotificacionesMailFacade;
//	@Getter
//	@Setter
//	private Demo demo;

	@Getter
	@Setter
	private MesaDeTrabajo mesaDeTrabajo;

	@Getter
	@Setter
	private Nota nota;

	@Getter
	@Setter
	private List<MesaDeTrabajo> mesaDeTrabajoLista;

	@Getter
	@Setter
	private List<DemoRangoFechas> RangoFechas;

	@Getter
	@Setter
	private List<String> mesaDeTrabajoOpciones;

	@Getter
	@Setter
	private String mesaSeleccionada;

	@Getter
	@Setter
	private String articuloSeleccionado;

	@Getter
	@Setter
	private String organizacion;

	@Getter
	@Setter
	private String propuesta;

	@Getter
	@Setter
	private String justificacion;

	@Getter
	@Setter
	private String nombreProponente;

	@Getter
	@Setter
	private String cedulaProponente;

	@Getter
	@Setter
	private String rucProponente;

	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private boolean esOrg;

	@Getter
	@Setter
	private List<String> notas;

	@Getter
	@Setter
	private boolean aceptarTerminos;

	@Getter
	@Setter
	private boolean habilitarGuardar;

	@Getter
	@Setter
	private String mensajeRUC;

	@Getter
	@Setter
	private String mensajeCedula;

	@Getter
	@Setter
	private String direccionZonal;

	@Getter
	private List<UbicacionesGeografica> provinciasLista = new ArrayList<UbicacionesGeografica>();

	@Getter
	@Setter
	private UbicacionesGeografica parroquiaSeleccionada, cantonSeleccionado, provinciaSeleccionada, ubicacionOpinion;

	@Getter
	@Setter
	private DemoDocumentos documentoInformacion, documentoInformacionManual;

	@Getter
	@Setter
	private String nombreDocumentoFirmado;

	@Getter
	@Setter
	private List<Boolean> OP_provincia = new ArrayList<Boolean>();

	@PostConstruct
	public void init() {

		cedulaProponente = "";
		nombreProponente = "";
		rucProponente = "";

		mesaDeTrabajoOpciones = mesaDeTrabajoFacade.obtenerMesaDeTrabajo2();
		mensajeRUC = "";
		mensajeCedula = "";
		notas = notaFacade.obtenerNotas();

		RangoFechas = demoRangoFechasFacade.rangoFechas();

		List<DemoRangoFechas> provinciasHabilitadas = demoRangoFechasFacade.buscarUbicacionGeograficaPorIdRangoFechas();
		for (DemoRangoFechas demoRangoFechas : provinciasHabilitadas) {
			provinciasLista.add(demoRangoFechas.getUbicacionesGeografica());
		}

	}

	public void uploadListenerInformacionFirmada(FileUploadEvent event) {

		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(TipoDocumentoSistema.OPINION_PUBLICA_ANEXO.getIdTipoDocumento());

		byte[] contenidoDocumento = event.getFile().getContents();
		documentoInformacionManual = new DemoDocumentos();
		documentoInformacionManual.setId(null);
		documentoInformacionManual.setContenidoDocumento(contenidoDocumento);
		documentoInformacionManual.setNombreDocumento(event.getFile().getFileName());
		documentoInformacionManual.setExtencionDocumento(event.getFile().getContentType());
		documentoInformacionManual.setTipo(event.getFile().getContentType());
		documentoInformacionManual.setNombreTabla(Demo.class.getSimpleName());
		documentoInformacionManual.setTipoDocumento(tipoDocumento);

		nombreDocumentoFirmado = event.getFile().getFileName();

	}

	public void direccionZonal() {
		try {
			direccionZonal = provinciaSeleccionada.getAreaCoordinacionZonal().getAreaName();

		} catch (Exception e) {
			direccionZonal = "DIRECCIÓN DEL PARQUE NACIONAL GALÁPAGOS";// TODO: handle exception
		}

	}

	public List<UbicacionesGeografica> getCantonesLista() {
		if (provinciaSeleccionada != null)
			return ubicacionGeograficaFacade.getUbicacionPadre(provinciaSeleccionada);
		else
			return new ArrayList<UbicacionesGeografica>();
	}

	public List<UbicacionesGeografica> getParroquiasLista() {
		if (cantonSeleccionado != null)
			return ubicacionGeograficaFacade.getUbicacionPadre(cantonSeleccionado);
		else
			return new ArrayList<UbicacionesGeografica>();
	}

	public List<String> getAtriculosLista() {
		if (mesaSeleccionada != null)
			return mesaDeTrabajoFacade.obtenerArticulos(mesaSeleccionada);
		else
			return null;
	}

	public void esOrganizacion() {
		if (!esOrg) {
			rucProponente = "";
			organizacion = "";

		} else {
		}

	}

	public void aceptar() {
		try {

			if (aceptarTerminos) {
				habilitarGuardar = true;
			} else {
				habilitarGuardar = false;
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void actualizarDialogoGuardar() {

		if (nombreProponente == null || nombreProponente == "") {
			mensajeCedula = "";
		} else {
			mensajeCedula = nombreProponente + " con cédula de identidad " + cedulaProponente;
		}

		if (organizacion == null || organizacion == "") {
			mensajeRUC = "";

		} else {
			mensajeRUC = " de la organización " + organizacion + " con RUC " + rucProponente;
		}

		RequestContext context = RequestContext.getCurrentInstance();
		RequestContext.getCurrentInstance().update("dialogo");
		context.execute("PF('dlg1').show();");
	}

	/* Object */
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());

	private String validarCedula(String cedulaNum) {
		if (JsfUtil.validarCedulaORUC(cedulaNum)) {
			Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC,
					Constantes.PASSWORD_WS_MAE_SRI_RC, cedulaNum);
			if (cedula != null && cedula.getCedula() != null) {

				return cedula.getNombre();
			}
		}
		JsfUtil.addMessageError("Introduzca una cédula válida.");
		cedulaProponente = "";
		return null;
	}

	public void validarCedulaRepLegal() {

		nombreProponente = validarCedula(cedulaProponente);

		if (nombreProponente != null || nombreProponente != "") {
			mensajeCedula = nombreProponente + " con cédula de identidad " + cedulaProponente;
		} else {
			mensajeCedula = "";
		}

	}

	public void validarRucRepLegal() {

		organizacion = validarRuc(rucProponente);

		if (organizacion != null || organizacion != "") {
			mensajeRUC = "de la organización " + organizacion + " con RUC " + rucProponente;

		} else {
			mensajeRUC = "";
		}
	}

	public String validarRuc(String ruc) {
		try {

			ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
					.obtenerPorRucSRI(Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, ruc);

			if (contribuyenteCompleto.getRazonSocial() == null) {
				JsfUtil.addMessageError("Ingrese un RUC válido");
				rucProponente = "";
			}
			return contribuyenteCompleto.getRazonSocial();
		} catch (Exception e) {
			System.out.println("rror");
		}
		return ruc;
	}

	public void guardar() {

		Demo demo = new Demo();

		demo.setCedula(cedulaProponente);
		demo.setNombre(nombreProponente);
		demo.setCorreo(email);
		demo.setEsOrg(esOrg);
		demo.setOrgRuc(rucProponente);
		demo.setOrgDescripcion(organizacion);
		demo.setMesaDeTrabajo(mesaSeleccionada);
		demo.setArticulo(articuloSeleccionado);
//		demo.setDetalleAtriculo(getDescripcionAtriculos());
		demo.setPropuesta(propuesta);
		demo.setJustificacion(justificacion);
		demo.setAceptar(aceptarTerminos);
		demo.setUbicacionesGeografica(ubicacionOpinion);

		notificacion(email);
		demo = demoFacade.guardar(demo);

		cedulaProponente = "";
		nombreProponente = "";
		email = "";
		rucProponente = "";
		esOrg = false;
		organizacion = "";
		mesaSeleccionada = "Seleccionar la mesa de Trabajo ...";
		articuloSeleccionado = "Seleccione el Atrículo";
		propuesta = "";
		justificacion = "";
		nombreDocumentoFirmado = null;
		aceptarTerminos = false;
		provinciaSeleccionada = null;
		direccionZonal = "";
		cantonSeleccionado = null;
		cantonSeleccionado = null;
		habilitarGuardar = false;

		try {
			if (documentoInformacionManual != null) {
				if (demo.getCedula().length()==10) {
					documentoInformacionManual.setIdTabla(demo.getId());
					documentoInformacion = documentosFacade.guardarDocumentoAlfresco(demo.getCedula(),
							"OPINION_PUBLICA_" + demo.getId().toString(), 0L, documentoInformacionManual,
							TipoDocumentoSistema.OPINION_PUBLICA_ANEXO);
				} else {
					documentoInformacionManual.setIdTabla(demo.getId());
					documentoInformacion = documentosFacade.guardarDocumentoAlfresco(demo.getOrgRuc(),
							"OPINION_PUBLICA_" + demo.getId().toString(), 0L, documentoInformacionManual,
							TipoDocumentoSistema.OPINION_PUBLICA_ANEXO);
				}
			}

		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CmisAlfrescoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JsfUtil.addMessageInfo("Opinión Pública Registrada");

	}

	private void notificacion(String email) {

		try {
//			Usuario uOperador = JsfUtil.getLoggedUser();
			String nombreDestino = nombreProponente == "" ? organizacion : nombreProponente;

			String mensajeUsuario = "";
			if (mensajeCedula != null && mensajeRUC != null) {
				mensajeUsuario = mensajeCedula + " " + mensajeRUC;
			} else if (mensajeCedula != null && mensajeRUC == null) {
				mensajeUsuario = mensajeCedula;
			} else if (mensajeCedula == null && mensajeRUC != null) {
				mensajeUsuario = mensajeRUC;
			}

			String cabecera = "Estimado (a)<br/>El Ministerio del Ambiente, Agua y Transición Ecológica agradece su aporte para la Mesa de Trabajo "
					+ mesaSeleccionada + ", " + articuloSeleccionado + ".<br/> La propuesta " + propuesta
					+ " y la justificación " + justificacion
					+ " servirán como aporte para la construcción de la nueva ley de recursos Hídricos.";

			String mensajeResp =

					"Por favor recuerde que, usted, " + mensajeUsuario
							+ "; declaro bajo juramento que toda la información ingresada corresponde a "
							+ "la realidad y reconozco la responsabilidad que genera la falsedad u ocultamiento"
							+ " de proporcionar datos falsos o errados, como consta en el PROYECTO DE LEY ORGÁNICA "
							+ "PARA LA OPTIMIZACIÓN Y EFICIENCIA DE TRÁMITES ADMINISTRATIVOS , Capítulo II, NORMAS"
							+ " COMUNES EN MATERIA DE TRÁMITES ADMINISTRATIVOS, Sección I, DE LA PLANIFICACIÓN,"
							+ " CREACIÓN Y SIMPLIFICACIÓN DE TRÁMITES ADMINISTRATIVOS, \"<i> Art. 10.- Veracidad"
							+ " de la información.- Las entidades reguladas por esta Ley presumirán que las "
							+ "declaraciones, documentos y actuaciones de las personas efectuadas en virtud "
							+ "de trámites administrativos son verdaderas, bajo aviso a la o al administrado "
							+ "de que, en caso de verificarse lo contrario, el trámite y resultado final de "
							+ "la gestión podrán ser negados y archivados, o los documentos emitidos carecerán "
							+ "de validez alguna, sin perjuicio de las sanciones y otros efectos jurídicos "
							+ "establecidos en la ley. El listado de actuaciones anuladas por la entidad en "
							+ "virtud de lo establecido en este inciso estará disponible para las demás "
							+ "entidades del Estado.(...)" + " <br/> Además, con respecto a la participación "
							+ "de los espacios, en la Constitución de la República del Ecuador, en su TÍTULO I, "
							+ "ELEMENTOS CONSTITUTIVOS DEL ESTADO, Capítulo segundo, Derechos del buen vivir, "
							+ "Sección tercera, Comunicación e Información, consta: <br/> \"<i>Art 16.- Todas "
							+ "las personas, en forma individual o colectiva, tienen derecho a:" + "<br/>(...) "
							+ "<br/> 5.- Integrar los espacios de participación previstos en la Constitución"
							+ " en el campo de la comunicación.</i>\"";

			String emailEnviar = email;

			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
					"bodyNotificacionRevisarRespuestasDemoPropuesta", new Object[] { cabecera, mensajeResp });

			Email.sendEmail(emailEnviar,
					"Propuesta para la construcción de la nueva ley de recursos Hídricos enviada: ", mensaje, "");

			EnvioNotificacionesMail envioNotificacionesMail = new EnvioNotificacionesMail();
			envioNotificacionesMail.setCodigoProyecto(null);
			envioNotificacionesMail.setEmail(emailEnviar);
			envioNotificacionesMail.setAsunto("Propuesta");
			envioNotificacionesMail.setContenido(mensaje);
			envioNotificacionesMail.setEnviado(true);
			envioNotificacionesMail.setFechaEnvio(new Date());
			envioNotificacionesMail.setProcesoId(null);
			envioNotificacionesMail.setTareaId(null);
			envioNotificacionesMail.setUsuarioEnvioId(null);
			envioNotificacionesMail.setUsuarioDestinoId(null);
			envioNotificacionesMail.setNombreUsuarioDestino(null);
			envioNotificacionesMailFacade.save(envioNotificacionesMail);

		} catch (Exception e) {
			System.out.println("error");
		}

	}

}
