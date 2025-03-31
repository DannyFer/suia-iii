package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.DocumentoRGBean;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProyectoAsociadoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers.FirmarPronunciamientoPermisoRGDController;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.InformeTecnicoRegistroGeneradorDesechos;
import ec.gob.ambiente.suia.domain.OficioEmisionRegistroGeneradorDesechos;
import ec.gob.ambiente.suia.domain.PuntoRecuperacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.InformeOficioRGFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class FirmarDocumentosRGController implements Serializable {

	private static final long serialVersionUID = 6923285356754002095L;

	private final Logger LOG = Logger.getLogger(FirmarDocumentosRGController.class);

	@EJB
	protected ProcesoFacade procesoFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private InformeOficioRGFacade informeOficioRGFacade;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
	private ProyectoAsociadoDigitalizacionFacade proyectoAsociadoFacade;

	@Getter
	@Setter
	private boolean token;
	
	@EJB
    private UsuarioFacade usuarioFacade;

	@Getter
	private Documento archivoAprobacion;

	private int docAprobVersion;

	private int docRegVersion;

	@Getter
	private Documento archivoRegistro;

	private boolean documentoAprobacionAdjuntado;

	private boolean documentoRegistroAdjuntado;

	private boolean firma;
	
	@Getter
	@Setter
	private boolean ambienteProduccion;
	
	@Getter
    @Setter
    public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");

	@PostConstruct
	public void init() {

		try {
			
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);

			if(JsfUtil.getCurrentTask() == null)
				return;
			if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
				token = true;
//cambio de finalizado por false...
			JsfUtil.getBean(DocumentoRGBean.class).init();
			JsfUtil.getBean(DocumentoRGBean.class).getGenerador().setFinalizado(false);
			if (JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getCodigo() == null
					|| JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getCodigo().isEmpty()) {
				JsfUtil.getBean(DocumentoRGBean.class)
						.getGenerador()
						.setCodigo(
								registroGeneradorDesechosFacade.generarCodigoRegistroGeneradorDesechos(JsfUtil
										.getBean(DocumentoRGBean.class).getGenerador().getAreaResponsable()));
			} else {
//				if (!JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getCodigo()
//						.startsWith(RegistroGeneradorDesechosFacade.CODE_PREFIX)) {
//					JsfUtil.getBean(DocumentoRGBean.class)
//							.getGenerador()
//							.setCodigo(
//									RegistroGeneradorDesechosFacade.CODE_PREFIX
//											+ JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getCodigo());
//				}
			}

			//Verificamos la tarea actual para generar el código del RGD e insertar la fecha.
			TaskSummaryCustom taskSummaryCustom = JsfUtil.getBean(BandejaTareasBean.class).getTarea();
			this.firma = false;
			if (taskSummaryCustom != null) {
				String urlTarea = taskSummaryCustom.getTaskSummary().getDescription();
				if (urlTarea.equalsIgnoreCase("/prevencion/registrogeneradordesechos/directorFirmarRegistroOficioAprobacion.jsf") ||
						urlTarea.equalsIgnoreCase("/prevencion/registrogeneradordesechos/subsecretariaFirmarRegistroOficioAprobacion.jsf"))
					firma = true;
			}
			
			archivoRegistro = JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getDocumentoBorrador();
			if(archivoRegistro==null){
			JsfUtil.getBean(DocumentoRGBean.class).visualizarBorrador(!token, this.firma);
			//if (token)
				registroGeneradorDesechosFacade.guardarBorrador(JsfUtil.getBean(DocumentoRGBean.class).getGenerador(),
					JsfUtil.getCurrentProcessInstanceId());
			}
			if (!firma){

				JsfUtil.getBean(DocumentoRGBean.class).inicializarInformeTecnicoAsociado();
				JsfUtil.getBean(DocumentoRGBean.class).visualizarOficio(true, !token, this.firma);

				JsfUtil.getBean(DocumentoRGBean.class).guardarOficio();
				JsfUtil.getBean(DocumentoRGBean.class).getOficio()
						.setDocumento(JsfUtil.getBean(DocumentoRGBean.class).guardarOficioDocumento());
				JsfUtil.getBean(DocumentoRGBean.class).guardarOficio();

				/*
				archivoAprobacion = JsfUtil.getBean(DocumentoRGBean.class).inicializarOficioEmisionActualizacionAsociado()
						.getDocumento();
				id = archivoAprobacion.getIdAlfresco();
				this.docAprobVersion = Integer.parseInt(id.substring(id.length()-1));

				archivoRegistro = JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getDocumentoBorrador();

				id = this.archivoRegistro.getIdAlfresco();
				this.docRegVersion = Integer.parseInt(id.substring(id.length()-1));


				if (!token) {
					archivoAprobacion.setContenidoDocumento(documentosFacade.descargar(archivoAprobacion.getIdAlfresco()));
					archivoRegistro.setContenidoDocumento(documentosFacade.descargar(archivoRegistro.getIdAlfresco()));
				}

				documentoAprobacionAdjuntado = false;
				documentoRegistroAdjuntado = false;*/
			}
			archivoAprobacion = JsfUtil.getBean(DocumentoRGBean.class).inicializarOficioEmisionActualizacionAsociado()
					.getDocumento();
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		    String fechaModificacion = sdf.format(JsfUtil.getBean(DocumentoRGBean.class).getOficio().getFechaModificacion());
		    
		    String fechaActual = sdf.format(new Date());
			
		    boolean nuevoDocumento = false;
			if(!fechaActual.equals(fechaModificacion)){
				nuevoDocumento = true;
			}
						
			if(firma && nuevoDocumento) {
				
				List<PuntoRecuperacion> listaPuntos = registroGeneradorDesechosFacade.cargarPuntosPorIdGenerador(JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getId());
				JsfUtil.getBean(DocumentoRGBean.class).getGenerador().setPuntosRecuperacion(listaPuntos);
				
				JsfUtil.getBean(DocumentoRGBean.class).inicializarInformeTecnicoAsociado();
				JsfUtil.getBean(DocumentoRGBean.class).visualizarOficio(true, true, this.firma);

				JsfUtil.getBean(DocumentoRGBean.class).getOficio()
						.setDocumento(JsfUtil.getBean(DocumentoRGBean.class).guardarOficioDocumento());
				JsfUtil.getBean(DocumentoRGBean.class).guardarOficioEmision();
				
				archivoAprobacion = JsfUtil.getBean(DocumentoRGBean.class).inicializarOficioEmisionActualizacionAsociado()
						.getDocumento();
				
				JsfUtil.getBean(DocumentoRGBean.class).visualizarBorrador(true, this.firma);
				registroGeneradorDesechosFacade.guardarBorrador(JsfUtil.getBean(DocumentoRGBean.class).getGenerador(),
						JsfUtil.getCurrentProcessInstanceId());
				
				JsfUtil.getBean(DocumentoRGBean.class).setGenerador(registroGeneradorDesechosFacade.cargarGeneradorParaDocumentoPorId(JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getId()));
			}

			String id = archivoAprobacion.getIdAlfresco();
//			this.docAprobVersion = Integer.parseInt(id.substring(id.length()-1));

			archivoRegistro = JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getDocumentoBorrador();
			if(archivoRegistro!=null){
				id = this.archivoRegistro.getIdAlfresco();
//				this.docRegVersion = Integer.parseInt(id.substring(id.length()-1));
			}
			
			token = true;//siempre firma electronica

			if (!ambienteProduccion) {
				archivoAprobacion.setContenidoDocumento(documentosFacade.descargar(archivoAprobacion.getIdAlfresco()));
				if (archivoRegistro != null)
					archivoRegistro.setContenidoDocumento(documentosFacade.descargar(archivoRegistro.getIdAlfresco()));
			}

			documentoAprobacionAdjuntado = false;
			documentoRegistroAdjuntado = false;

		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}

	}

	public String aceptar() throws ServiceException, CmisAlfrescoException {
		try {
			boolean errors = false;

			String idAlfresco = archivoAprobacion.getIdAlfresco();
			if(token){

//				idAlfresco = archivoAprobacion.getIdAlfresco().substring(0,idAlfresco.length()-1);
//				idAlfresco= idAlfresco+""+(++this.docAprobVersion);
				archivoAprobacion.setIdAlfresco(idAlfresco);

				if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("Oficio de pronunciamiento" + JsfUtil.MESSAGE_ERROR_DOCUMENTO_NO_FIRMADO);
					errors = true;
				}
				else{
					idAlfresco = archivoRegistro.getIdAlfresco();
//					idAlfresco= idAlfresco+""+(++this.docRegVersion);
					archivoRegistro.setIdAlfresco(idAlfresco);

					if (token && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
						JsfUtil.addMessageError("Registro de generador" + JsfUtil.MESSAGE_ERROR_DOCUMENTO_NO_FIRMADO);
						errors = true;
					}
				}
			}
			else{
				
				String urlIdAlfrescoAprobacion = archivoAprobacion.getIdAlfresco();
	        	urlIdAlfrescoAprobacion=(urlIdAlfrescoAprobacion.split(";"))[0];
				//idAlfresco = archivoAprobacion.getIdAlfresco().substring(0,idAlfresco.length()-4);
				archivoAprobacion.setIdAlfresco(urlIdAlfrescoAprobacion);

				idAlfresco = archivoRegistro.getIdAlfresco();
				String urlIdAlfrescoRegistro = archivoRegistro.getIdAlfresco();
				urlIdAlfrescoRegistro=(urlIdAlfrescoRegistro.split(";"))[0];
				//idAlfresco = idAlfresco.substring(0,idAlfresco.length()-4);
				archivoRegistro.setIdAlfresco(urlIdAlfrescoRegistro);

				if (!documentoAprobacionAdjuntado) {
					JsfUtil.addMessageError("Debe adjuntar el oficio de aprobación firmado.");
					errors = true;
				}
				else{
					if (!documentoRegistroAdjuntado) {
						JsfUtil.addMessageError("Debe adjuntar el registro de generador firmado.");
						errors = true;
					}
				}
			}

			if (errors)
				return null;

			this.documentosFacade.actualizarDocumento(archivoAprobacion);
			this.documentosFacade.actualizarDocumento(archivoRegistro);

			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(),
					JsfUtil.getCurrentProcessInstanceId(), null);

			procesoFacade.envioSeguimientoRGD(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId());
			//si el rgd esta asociado a un proyecto digitalizado actualizo el id del nuevo RGD
			actualizarRGDAsociado();
		} catch (JbpmException exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
		return JsfUtil.actionNavigateToBandeja();
	}
	
	private void actualizarRGDAsociado(){
		Map<String, Object> variables;
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getProcessInstanceId());
			Integer idActualizado = 0;//id del rgd actualizado
			if(variables.get("idRegistroGenerador") != null){
				idActualizado =Integer.valueOf((String) variables.get("idRegistroGenerador"));
			}
			//listo los RGD con el mismo codigo
			List<GeneradorDesechosPeligrosos> listaDesechos = registroGeneradorDesechosFacade.getGeneradoresPorCodigoRequest(JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getSolicitud(), idActualizado,
					JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getUsuario().getNombre());
			if(listaDesechos != null && listaDesechos.size() > 0){
				for (GeneradorDesechosPeligrosos objdesecho : listaDesechos) {
					List<ProyectoAsociadoDigitalizacion> lista = proyectoAsociadoFacade.buscarProyectoAsociado(objdesecho.getId());
					if(lista != null && lista.size() > 0){
						for (ProyectoAsociadoDigitalizacion objAsociado : lista) {
							ProyectoAsociadoDigitalizacion aux = new ProyectoAsociadoDigitalizacion();
							aux.setAutorizacionAdministrativaAmbiental(objAsociado.getAutorizacionAdministrativaAmbiental());
							aux.setProyectoAsociadoId(JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getId());
							aux.setCodigo(objAsociado.getCodigo());
							aux.setSistemaOriginal(objAsociado.getSistemaOriginal());
							aux.setNombreTabla(objAsociado.getNombreTabla());
							aux.setTipoProyecto(objAsociado.getTipoProyecto());
							aux.setEstado(true);
							aux.setFechaCreacion(new Date());
							aux.setUsuarioCreacion(JsfUtil.getLoggedUser().getNombre());
							proyectoAsociadoFacade.guardar(aux, JsfUtil.getLoggedUser());
							objAsociado.setEstado(false);
							proyectoAsociadoFacade.guardar(objAsociado, JsfUtil.getLoggedUser());
						}
					}
				}	
				}
		} catch (JbpmException e) {
			e.printStackTrace();
		}
	}

	public String firmarOficio() {
		try {
			String url="";
			OficioEmisionRegistroGeneradorDesechos oficio = JsfUtil.getBean(DocumentoRGBean.class).getOficio();
			String direccionDescargaOficioAprobacion = documentosFacade.direccionDescarga(oficio != null && oficio.getId() != null ? oficio.getDocumento() : JsfUtil
					.getBean(DocumentoRGBean.class).inicializarOficioEmisionActualizacionAsociado().getDocumento());

			url= DigitalSign.sign(direccionDescargaOficioAprobacion, JsfUtil.getBean(LoginBean.class).getUsuario()
					.getNombre());
			return url;
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del oficio", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return "";
		}
	}

	public boolean verificaToken (){
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	
	public void guardarToken(){
		Usuario usuario= JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken ();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String firmarRegistro() {
		try {
			String direccionDescargaRegistro = documentosFacade.direccionDescarga(JsfUtil
					.getBean(DocumentoRGBean.class).getGenerador().getDocumentoBorrador());

			return DigitalSign.sign(direccionDescargaRegistro, JsfUtil.getBean(LoginBean.class).getUsuario()
					.getNombre());
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del generador", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return "";
		}
	}

	public void uploadListenerAprobacion(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = new Documento();
		String accion = JsfUtil.getBean(DocumentoRGBean.class).isEmision() ? "Emision" : "Actualizacion";
		documento.setNombre("Oficio" + accion + "-" + JsfUtil.getBean(DocumentoRGBean.class).getOficio().getNumero()
				+ ".pdf");
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(OficioEmisionRegistroGeneradorDesechos.class.getSimpleName());
		documento.setIdTable(JsfUtil.getBean(DocumentoRGBean.class).getOficio().getId());
		documento.setMime("application/pdf");
		documento.setExtesion(".pdf");
		try {
			if (JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getProyecto() != null) {
				documento = documentosFacade.guardarDocumentoAlfresco(JsfUtil.getBean(DocumentoRGBean.class)
								.getGenerador().getProyecto().getCodigo(), Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS,
						JsfUtil.getCurrentProcessInstanceId(), documento, JsfUtil.getBean(DocumentoRGBean.class)
								.getOficio().getTipoDocumento().getTipoDocumentoSistema(), null);
			} else {
				documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(JsfUtil.getBean(DocumentoRGBean.class)
								.getGenerador().getSolicitud(), Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS,
						JsfUtil.getCurrentProcessInstanceId(), documento, JsfUtil.getBean(DocumentoRGBean.class)
								.getOficio().getTipoDocumento().getTipoDocumentoSistema(), null);
			}
			JsfUtil.getBean(DocumentoRGBean.class).getOficio().setDocumento(documento);
			JsfUtil.getBean(DocumentoRGBean.class).guardarOficio();
			this.archivoAprobacion = documento;
			this.archivoAprobacion.setContenidoDocumento(contenidoDocumento);
			documentoAprobacionAdjuntado = true;
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void uploadListenerRegistro(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		try {
			JsfUtil.getBean(DocumentoRGBean.class).getGenerador().setArchivoGenerador(contenidoDocumento);
			registroGeneradorDesechosFacade.guardarBorrador(JsfUtil.getBean(DocumentoRGBean.class).getGenerador(),
					JsfUtil.getCurrentProcessInstanceId());
			this.archivoRegistro = JsfUtil.getBean(DocumentoRGBean.class).getGenerador().getDocumentoBorrador();
			this.archivoRegistro.setContenidoDocumento(contenidoDocumento);
			documentoRegistroAdjuntado = true;
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public StreamedContent getStream(Documento documento) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento.getContenidoDocumento() != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),
					"application/octet-stream");
			content.setName(documento.getNombre());
		}
		return content;
	}
	
	public StreamedContent getStreamInforme() throws Exception {
		
		JsfUtil.getBean(DocumentoRGBean.class).inicializarInformeTecnicoAsociado();
		
		InformeTecnicoRegistroGeneradorDesechos informe = JsfUtil.getBean(DocumentoRGBean.class).getInforme();
		Documento documento = new Documento();
		documento = documentosFacade.documentoXTablaIdXIdDocUnico(informe.getId(), informe.getClass().getSimpleName(), TipoDocumentoSistema.TIPO_INFORME_TECNICO_RGD);
		byte[] contenido = documentosFacade.descargar(documento.getIdAlfresco());
		documento.setContenidoDocumento(contenido);
		
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento.getContenidoDocumento() != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),
					"application/octet-stream");
			content.setName(documento.getNombre());
		}
		return content;
	}
}
