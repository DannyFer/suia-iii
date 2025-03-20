package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.UbicacionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PermisoRegistroGeneradorDesechosFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PermisoRegistroGeneradorDesechos;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class FirmarPronunciamientoPermisoRGDController {
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcoaFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private DocumentosFacade documentoFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private PermisoRegistroGeneradorDesechosFacade permisoRegistroGeneradorDesechosFacade;
	@EJB
	private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private UbicacionDigitalizacionFacade ubicacionDigitalizacionFacade;
	/***************************************************************************************************/

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoSuia;
	/****************************************************************************************************/
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{datosOperadorRgdBean}")
	private DatosOperadorRgdBean datosOperadorRgdBean;

	@Getter
	@Setter
	private boolean token, documentoDescargadoOficio,documentoDescargadoPermiso,informacionSubidaOficio,informacionSubidaPermiso, documentosFirmados;

	@Getter
	@Setter
	private DocumentosRgdRcoa documentoOficio, documentoPermiso;
	
	@Getter
	@Setter
	private String nombreDocumentoFirmadoOficio,nombreDocumentoFirmadoPermiso;
	
	@Getter
	@Setter
	private String tramite;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private RegistroGeneradorDesechosRcoa registro;
	
	@Getter
	@Setter
	private String codigo, tipoPermisoRGD, urlVerTramite;
	
	private Date fecha;
	
	@Getter
	@Setter
	private boolean ambienteProduccion;
	
	@Getter
	@Setter
	public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	
	@Getter
	@Setter
	public boolean provicional;

	private String correoOperador, nombreEmpresa, representante;
	private TipoDocumentoSistema oficioPronunciamiento, documentoRegistroGenerador;
	private Integer proyectoId;
	
	@PostConstruct
	public void init()
	{
		try
		{
			provicional=false;
			urlVerTramite = "/pages/rcoa/generadorDesechos/informacionRegistroGeneradorVer.jsf";
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			registro = new RegistroGeneradorDesechosRcoa();
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			proyectoId = Integer.valueOf(((String)variables.get("idProyectoDigitalizacion") == null)?"0":(String)variables.get("idProyectoDigitalizacion"));
			tipoPermisoRGD =(String)variables.get(Constantes.VARIABLE_TIPO_RGD);
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
				datosOperadorRgdBean.setTitulo("Responsable o representante de la empresa");
				registro = registroGeneradorDesechosRcoaFacade.buscarRGDPorCodigo(tramite);
				urlVerTramite = "/pages/rcoa/generadorDesechos/informacionRegistroGeneradorVerREP.jsf";
			}
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			List<RegistroGeneradorDesechosProyectosRcoa> registroGeneradorDesechosProyectosRcoas = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
			if(proyecto.getId() != null){
				registroGeneradorDesechosProyectosRcoas = registroGeneradorDesechosProyectosRcoaFacade.asociados(proyecto.getId());
				if(proyecto.getCategorizacion() == 3 || proyecto.getCategorizacion() == 4){
					if(proyecto.getTipoProyecto() == 2){
						provicional = true;
					}
				}
			}else{
				proyectoSuia = new ProyectoLicenciamientoAmbiental();
				proyectoSuia = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(tramite);
				if(proyectoSuia != null && proyectoSuia.getId() != null){
					registroGeneradorDesechosProyectosRcoas= registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoSuia(proyectoSuia.getId());
					if(proyectoSuia.getCatalogoCategoria().getCategoria().getId().equals(Categoria.CATEGORIA_III) || proyectoSuia.getCatalogoCategoria().getCategoria().getId().equals(Categoria.CATEGORIA_IV)){
						//provicional = true;
					}
				}
			}
			
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_AAA)){
				provicional=false;
			}
//			String provicionalS = (String) variables.get("licenciaProyectoEjecucion");
//			provicional = Boolean.getBoolean(provicionalS);

			if (registroGeneradorDesechosProyectosRcoas != null && !registroGeneradorDesechosProyectosRcoas.isEmpty()) 
			{
				registro = registroGeneradorDesechosProyectosRcoas.get(0).getRegistroGeneradorDesechosRcoa();
			}
			// si es de digitalizacion ya no busco el proyecto aosciado
			if(proyectoId > 0)
				registro = registroGeneradorFacade.buscarRGDPorProyectoDigitalizado(proyectoId);
			if(registro != null && registro.getUsuario() != null){
				datosOperadorRgdBean.buscarDatosOperador(registro.getUsuario());
			}
			codigo = registro.getCodigo();
			oficioPronunciamiento = TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO;
			documentoRegistroGenerador = TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS;
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_AAA)){
				oficioPronunciamiento = TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO_AAA;
				documentoRegistroGenerador = TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS_AAA;
			} else if(tipoPermisoRGD != null
					&& tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
				oficioPronunciamiento = TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO_REP;
				documentoRegistroGenerador = TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS_REP;
			}
			documentoOficio = null;
			documentoPermiso = null;
			/*List<DocumentosRgdRcoa> documentosOficiosList = documentosRgdRcoaFacade.descargarDocumentoRgd(registro.getId(),RegistroGeneradorDesechosProyectosRcoa.class.getSimpleName(),oficioPronunciamiento);
			if (documentosOficiosList != null && !documentosOficiosList.isEmpty())
				documentoOficio = documentosOficiosList.get(0);
			List<DocumentosRgdRcoa> documentoGeneradorList = documentosRgdRcoaFacade.descargarDocumentoRgd(registro.getId(),RegistroGeneradorDesechosProyectosRcoa.class.getSimpleName(),documentoRegistroGenerador);
			if (documentoGeneradorList != null && !documentoGeneradorList.isEmpty())
				documentoPermiso = documentoGeneradorList.get(0);
			if(documentoPermiso != null)
				fecha = documentoPermiso.getFechaCreacion();*/
			token = true;
			if(!ambienteProduccion){
				verificaToken();
				if(!token){
					documentosFirmados= false;
				}else{
					documentosFirmados = true;
				}
			}else{
				documentosFirmados = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean verificaToken() {
		token = false;
		if(JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}
	
	public void guardarToken(){
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();

			if(!token){
				documentosFirmados= false;
			}else{
				documentosFirmados = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public StreamedContent getDescargarOficio() throws IOException {
		crearDocumentos();
		DefaultStreamedContent content = null;
		try {
			if (documentoOficio != null && documentoOficio.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoOficio.getContenidoDocumento()),documentoOficio.getMime(),documentoOficio.getNombre());
				documentoDescargadoOficio=true;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar Documento Oficio de Registro Generador");
			e.printStackTrace();
		}
		return content;
	}
	
	public StreamedContent getDescargarRegistro() throws IOException {
		crearDocumentos();
		DefaultStreamedContent content = null;
		try {
			if (documentoPermiso != null && documentoPermiso.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoPermiso.getContenidoDocumento()),documentoPermiso.getMime(),documentoPermiso.getNombre());
				documentoDescargadoPermiso=true;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar Documento Registro Generador");
			e.printStackTrace();
		}
		return content;
	}
	
	public void uploadListenerOficioFirmada(FileUploadEvent event) {
		if(documentoDescargadoOficio) {
			TipoDocumento tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(oficioPronunciamiento.getIdTipoDocumento());

			byte[] contenidoDocumento = event.getFile().getContents();
			documentoOficio = new DocumentosRgdRcoa();
			documentoOficio.setId(null);
			documentoOficio.setContenidoDocumento(contenidoDocumento);
			documentoOficio.setNombre(event.getFile().getFileName());
			documentoOficio.setExtesion(".pdf");
			documentoOficio.setTipoContenido("application/pdf");
			documentoOficio.setMime("application/pdf");
			documentoOficio.setIdTable(registro.getId());
			documentoOficio.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
			documentoOficio.setTipoDocumento(tipoDocumento);
			documentoOficio.setRegistroGeneradorDesechosRcoa(registro);
			//documentoOficio.setIdProceso((int)bandejaTareasBean.getProcessId());
			
			informacionSubidaOficio = true;
			nombreDocumentoFirmadoOficio = event.getFile().getFileName();
			if(informacionSubidaOficio = true && informacionSubidaPermiso == true)
				documentosFirmados = true;
		}else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}

	public void uploadListenerGeneradorFirmada(FileUploadEvent event) {
		if(documentoDescargadoPermiso) {
			TipoDocumento tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(documentoRegistroGenerador.getIdTipoDocumento());
			
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoPermiso = new DocumentosRgdRcoa();
			documentoPermiso.setId(null);
			documentoPermiso.setContenidoDocumento(contenidoDocumento);
			documentoPermiso.setNombre(event.getFile().getFileName());
			documentoPermiso.setExtesion(".pdf");
			documentoPermiso.setTipoContenido("application/pdf");
			documentoPermiso.setMime("application/pdf");
			documentoPermiso.setIdTable(registro.getId());
			documentoPermiso.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
			documentoPermiso.setTipoDocumento(tipoDocumento);
			documentoPermiso.setRegistroGeneradorDesechosRcoa(registro);
			//documentoPermiso.setIdProceso((int)bandejaTareasBean.getProcessId());

			informacionSubidaPermiso = true;
			nombreDocumentoFirmadoPermiso = event.getFile().getFileName();

			if(informacionSubidaOficio = true && informacionSubidaPermiso == true)
				documentosFirmados = true;
		}else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	@SuppressWarnings("static-access")
	public String firmarOficio() {
		try {
			crearDocumentos();
			if(documentoOficio != null && documentoOficio.getContenidoDocumento() != null) {
				String documento = documentosRgdRcoaFacade.direccionDescarga(documentoOficio);
				DigitalSign firmaE = new DigitalSign();
				return firmaE.sign(documento, loginBean.getUsuario().getNombre());
			}
			else
				return "";
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
	}
	
	@SuppressWarnings("static-access")
	public String firmarGenerador() {
		try {
			crearDocumentos();
			if(documentoPermiso != null && documentoPermiso.getContenidoDocumento() != null) {
				String documento = documentosRgdRcoaFacade.direccionDescarga(documentoPermiso);
				DigitalSign firmaE = new DigitalSign();
				
				return firmaE.sign(documento, loginBean.getUsuario().getNombre());
			}
			else
				return "";
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
	}

	public void cancelar() {
		JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
	}

	public void completarTarea(){
		try {
			String mensajeFinal = "";
			if(token){
				mensajeFinal = "El oficio de pronunciamiento y RGD fueron firmados con éxito";
				if(!documentoFacade.verificarFirmaVersion(documentoOficio.getIdAlfresco())){
					JsfUtil.addMessageError("El oficio de pronunciamiento no está firmado electrónicamente.");
					return;
				}
				if(!documentoFacade.verificarFirmaVersion(documentoPermiso.getIdAlfresco())){
					JsfUtil.addMessageError("El registro generador de desechos no está firmado electrónicamente.");
					return;
				}
			}else{
				if(informacionSubidaPermiso && informacionSubidaOficio){
					
					documentoOficio = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
							registro.getCodigo(), "OFICIO PRONUNCIAMIENTO GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documentoOficio, oficioPronunciamiento);
					
					documentoPermiso = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
							registro.getCodigo(), "REGISTRO GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documentoPermiso, documentoRegistroGenerador);
					mensajeFinal = "El oficio de pronunciamiento y el RGD firmados fueron adjuntados con éxito";
				}else{
					JsfUtil.addMessageError("Debe subir los documentos firmados");
					return;
				}
			}
			int procesoId = (int)bandejaTareasBean.getProcessId();
			documentoOficio.setIdProceso(procesoId);
			documentosRgdRcoaFacade.save(documentoOficio, loginBean.getUsuario());
			documentoPermiso.setIdProceso(procesoId);
			documentosRgdRcoaFacade.save(documentoPermiso, loginBean.getUsuario());
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP))
				notificacionREP();
			else
				notificacion();

			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(mensajeFinal);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void notificacion(){
		try {
			correoOperador = "";
			nombreEmpresa = "";
			representante = "";
			String codigoCiiu = "";
			String ubicacion = "";
			String codigoRgd = "";
			String codigoProyecto = "";
			String nombreProyecto = "";
			
			List<PermisoRegistroGeneradorDesechos> listaPermiso = permisoRegistroGeneradorDesechosFacade.findByRegistroGenerador(registro.getId());
			if(listaPermiso != null && !listaPermiso.isEmpty()){
				codigoRgd = listaPermiso.get(0).getNumeroDocumento();
			}else{
				codigoRgd = codigo;
			}
			Usuario usuarioProponente = new Usuario();
			if(proyectoId > 0){
				AutorizacionAdministrativaAmbiental proyectoAAA = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(proyectoId);
				if(proyectoAAA != null && proyectoAAA.getId() != null){
					usuarioProponente = proyectoAAA.getUsuario();
					codigoProyecto = proyectoAAA.getCodigoProyecto();
					nombreProyecto = proyectoAAA.getNombreProyecto();
				}
			}else if(proyecto != null && proyecto.getId() != null){
				usuarioProponente = proyecto.getUsuario();
				codigoProyecto = proyecto.getCodigoUnicoAmbiental();
				nombreProyecto = proyecto.getNombreProyecto();
			}else if(proyectoSuia != null && proyectoSuia.getId() != null){
				usuarioProponente = proyectoSuia.getUsuario();
				codigoProyecto = proyectoSuia.getCodigo();
				nombreProyecto = proyectoSuia.getNombre();
			}

			datosOperador(usuarioProponente);

			List<ProyectoLicenciaCuaCiuu> listaProyectoActividadesPrincipal = new ArrayList<ProyectoLicenciaCuaCiuu>();
			List<UbicacionesGeografica> ubicaciones = new ArrayList<UbicacionesGeografica>();
			if(proyectoId > 0){
				// listo las ubicaciones del proyecto original
				ubicaciones = ubicacionDigitalizacionFacade.obtenerTodasUbicacinesPorProyecto(proyectoId);
			}else if(proyecto != null && proyecto.getId() != null){
				listaProyectoActividadesPrincipal = registroGeneradorDesechosRcoaFacade.buscarActividadesCiuPrincipal(proyecto);
				ubicaciones = proyectoLicenciaCoaUbicacionFacade.ubicacionesGeograficas(proyecto);
			}else{
				ubicaciones = proyectoSuia.getUbicacionesGeograficas();
			}
			if(listaProyectoActividadesPrincipal != null && !listaProyectoActividadesPrincipal.isEmpty()){
				codigoCiiu = listaProyectoActividadesPrincipal.get(0).getCatalogoCIUU().getCodigo() 
						+ " " + listaProyectoActividadesPrincipal.get(0).getCatalogoCIUU().getNombre();
			}

			if(ubicaciones != null && !ubicaciones.isEmpty()){
				if(ubicaciones.size() == 1){
					ubicacion = "la provincia de " + ubicaciones.get(0).getUbicacionesGeografica().getUbicacionesGeografica().getNombre() 
							+ ", cantón " + ubicaciones.get(0).getUbicacionesGeografica().getNombre() 
							+ ", parroquia " + ubicaciones.get(0).getNombre() + ".";
				}else{
					ubicacion = "<br/><table border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\" style=\"font-family:arial;font-size:12px;\">";

					ubicacion += "<tr><td><b>PROVINCIA</b></td><td><b>CANTÓN</b></td><td><b>PARROQUIA</b></td></tr>";
					for (UbicacionesGeografica ubicacionActual : ubicaciones) {
						ubicacion += "<tr><td>" + ubicacionActual.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td><td>"
								+ ubicacionActual.getUbicacionesGeografica().getNombre() + "</td><td>"
								+ ubicacionActual.getNombre() + "</td></tr>";
					}
					ubicacion += "</table>";
				}
			}
			
			SimpleDateFormat fechaFormato = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy",new Locale("es"));
			
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionOperadorRcoaRgd", new Object[]{});

			mensaje = mensaje.replace("nombre_operador", representante);
			mensaje = mensaje.replace("nombre_empresa", nombreEmpresa);
			mensaje = mensaje.replace("codigo_proyecto", codigoRgd);
			mensaje = mensaje.replace("codigo_ciiu", codigoCiiu);
			mensaje = mensaje.replace("nombre_proyecto", nombreProyecto);
			mensaje = mensaje.replace("codigo_AAA", codigoProyecto);
			mensaje = mensaje.replace("fecha_ingresada", fechaFormato.format(fecha));
			mensaje = mensaje.replace("tabla_ubicacion", ubicacion);
			mensaje = mensaje.replace("nombre_autoridad", JsfUtil.getLoggedUser().getPersona().getNombre());
			mensaje = mensaje.replace("codigo_tramite", codigoProyecto);
			if(codigoCiiu.isEmpty())
				mensaje = mensaje.replace("con código CIIU , ", "");
			NotificacionAutoridadesController mail = new NotificacionAutoridadesController();
			mail.sendEmailInformacionProponente(correoOperador, "", mensaje, "Notificación", codigoProyecto, usuarioProponente, loginBean.getUsuario());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void notificacionREP(){
		try {
			correoOperador = "";
			nombreEmpresa = "";
			representante = "";
			String nombreDeseccho = "";
			String codigoRgd = "";
			Usuario usuarioOperador = new Usuario();
			List<PermisoRegistroGeneradorDesechos> listaPermiso = permisoRegistroGeneradorDesechosFacade.findByRegistroGenerador(registro.getId());
			if(listaPermiso != null && !listaPermiso.isEmpty()){
				codigoRgd = listaPermiso.get(0).getNumeroDocumento();
				PermisoRegistroGeneradorDesechos registroRgdREP = listaPermiso.get(0);
				usuarioOperador = registroRgdREP.getRegistroGeneradorDesechosRcoa().getUsuario();
			}else{
				codigoRgd = codigo;
			}
			List<DesechosRegistroGeneradorRcoa> listaDesechosSeleccionados = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGenerador(registro);
			if(listaDesechosSeleccionados != null
					&& listaDesechosSeleccionados.size() > 0){
				nombreDeseccho = listaDesechosSeleccionados.get(0).getDesechoPeligroso().getDescripcion();
			}
			datosOperador(usuarioOperador);
			SimpleDateFormat fechaFormato = new SimpleDateFormat(
					"dd 'de' MMMM 'de' yyyy", new Locale("es"));

			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
							"bodyNotificacionOperadorRcoaRgdREP",
							new Object[] {});

			mensaje = mensaje.replace("nombre_operador", representante);
			mensaje = mensaje.replace("nombre_empresa", nombreEmpresa);
			mensaje = mensaje.replace("codigo_desecho", codigoRgd);
			mensaje = mensaje.replace("nombre_desecho", nombreDeseccho);
			mensaje = mensaje.replace("fecha_ingresada",
					fechaFormato.format(fecha));
			mensaje = mensaje.replace("nombre_autoridad", JsfUtil.getLoggedUser().getPersona().getNombre());
			mensaje = mensaje.replace("codigo_tramite", tramite);
			NotificacionAutoridadesController mail = new NotificacionAutoridadesController();
			mail.sendEmailInformacionProponente(correoOperador, "", mensaje,
					"Notificación", codigoRgd, usuarioOperador, loginBean.getUsuario());

		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void datosOperador(Usuario operador){
		try {
			if(operador.getNombre().length() == 10){
				List<Contacto> contacto = contactoFacade.buscarPorPersona(operador.getPersona());
				for (Contacto con : contacto){
					if(con.getFormasContacto().getId() == 5
							&& con.getEstado().equals(true)){
						correoOperador = con.getValor();
						break;
					}
				}
				representante = operador.getPersona().getNombre();
			}else{
				Organizacion organizacion = organizacionFacade.buscarPorRuc(operador.getNombre());
				if(organizacion != null){
					List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(organizacion);
					for (Contacto con : contacto){
						if(con.getFormasContacto().getId() == 5
								&& con.getEstado().equals(true)){
							correoOperador = con.getValor();
							break;
						}
					}
					nombreEmpresa = organizacion.getNombre();
					representante = organizacion.getPersona().getNombre();
				}else{
					List<Contacto> contacto = contactoFacade.buscarPorPersona(operador.getPersona());
					for (Contacto con : contacto){
						if(con.getFormasContacto().getId() == 5
								&& con.getEstado().equals(true)){
							correoOperador = con.getValor();
							break;
						}
					}
					representante = operador.getPersona().getNombre();
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void crearDocumentos(){
		try {
			/*
			 if(documentoPermiso != null && !documentoPermiso.getUsuarioCreacion().equals(loginBean.getNombreUsuario())){ 
				documentoOficio = null;
			 	documentoPermiso = null; }
			 */

			if(documentoOficio == null || documentoPermiso == null){
				List<RegistroGeneradorDesechosProyectosRcoa> registroList = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyecto.getId());
				if((registroList == null || registroList.isEmpty()) && proyectoSuia != null && proyectoSuia.getId() != null){
					registroList = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoSuia(proyectoSuia.getId());
				}
				// si es de digitalizacion ya no busco el proyecto aosciado
				if(proyectoId > 0){
					registroList = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoDigitalizado(proyectoId);
				}
				if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_AAA)){
					JsfUtil.getBean(ReporteRegistroGeneradorDesechosAAAController.class).generarRegistro(registroList.get(0).getRegistroGeneradorDesechosRcoa(),provicional);
				} else if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
					JsfUtil.getBean(ReporteRegistroGeneradorDesechosController.class).generarRegistroREP(registro);
				}else{
					JsfUtil.getBean(ReporteRegistroGeneradorDesechosController.class).generarRegistro(registroList.get(0).getRegistroGeneradorDesechosRcoa(),	provicional);
				}
				List<DocumentosRgdRcoa> documentosOficiosList = documentosRgdRcoaFacade.descargarDocumentoRgd(registro.getId(),RegistroGeneradorDesechosProyectosRcoa.class.getSimpleName(),oficioPronunciamiento);
				if (documentosOficiosList != null && !documentosOficiosList.isEmpty())
					documentoOficio = documentosOficiosList.get(0);
				List<DocumentosRgdRcoa> documentoGeneradorList = documentosRgdRcoaFacade.descargarDocumentoRgd(registro.getId(),RegistroGeneradorDesechosProyectosRcoa.class.getSimpleName(),documentoRegistroGenerador);
				if (documentoGeneradorList != null && !documentoGeneradorList.isEmpty())
					documentoPermiso = documentoGeneradorList.get(0);

				fecha = documentoPermiso.getFechaCreacion();
			}

			if(!token){
				//onclick="PF('iddialogReporteOficio').show();"
				RequestContext.getCurrentInstance().execute("PF('iddialogReporteOficio').show();");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
