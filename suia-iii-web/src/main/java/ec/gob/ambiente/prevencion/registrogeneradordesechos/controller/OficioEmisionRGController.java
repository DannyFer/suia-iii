package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.DocumentoRGBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.SendCopyBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.InformeTecnicoRegistroGeneradorDesechos;
import ec.gob.ambiente.suia.domain.PuntoRecuperacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class OficioEmisionRGController implements Serializable {

	private static final long serialVersionUID = 2144274394627795921L;

	private final Logger LOG = Logger.getLogger(OficioEmisionRGController.class);

	@EJB
	protected ProcesoFacade procesoFacade;

	@ManagedProperty(value = "#{documentoRGBean}")
	@Getter
	@Setter
	private DocumentoRGBean documentoRGBean;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ConexionBpms conexionBpms;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@Getter
	@Setter
	private Boolean firmaSoloToken, token;
	
	@PostConstruct
	public void init() throws Exception {
		firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
		
		verificaToken();
		
		documentoRGBean.inicializarInformeTecnicoAsociado();
		
		List<PuntoRecuperacion> listaPuntos = registroGeneradorDesechosFacade.cargarPuntosPorIdGenerador(documentoRGBean.getGenerador().getId());
		documentoRGBean.getGenerador().setPuntosRecuperacion(listaPuntos);
		
		updateOficio();
	}
	
	public boolean verificaToken() {
		if(firmaSoloToken) {
			token = true;
			return token;
		}			
		
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getDiscriminador() {
		return "tecnico";
	}

	public void updateOficio() {
		try {
			documentoRGBean.visualizarOficio(true, true, false);
		} catch (Exception e) {
			LOG.error("Error al cargar el oficio de emision/actualizacion del registro de generador", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public String aceptar() throws ServiceException, CmisAlfrescoException {
		List<String> errors = new ArrayList<>();
		if(documentoRGBean.isMostrarCampos()) {
			if (documentoRGBean.getOficio().getCumplimiento() != null
					&& documentoRGBean.getOficio().getCumplimiento().trim().isEmpty())
				errors.add("El campo 'CUMPLIMIENTO' es requerido.");
			if (documentoRGBean.getOficio().getEstablecido() != null
					&& documentoRGBean.getOficio().getEstablecido().trim().isEmpty())
				errors.add("El campo 'ESTABLECIDO' es requerido.");
		}

		if (!validarPuedeContinuarExistenObservacionesSinCorregir())
			errors.add("El campo 'ES NECESARIO REALIZAR CORRECCIONES SOBRE EL INFORME TÉCNICO O AL OFICIO DE PRONUNCIAMIENTO' debe tener el valor sí, porque existen observaciones sin corregir.");

		if (!validarPuedeContinuarNoExistenObservacionesSinCorregir())
			errors.add("El campo 'ES NECESARIO REALIZAR CORRECCIONES SOBRE EL INFORME TÉCNICO O AL OFICIO DE PRONUNCIAMIENTO' debe tener el valor 'No', porque no existen observaciones sin corregir.");

		if (!errors.isEmpty()) {
			JsfUtil.addMessageError(errors);
			return "";
		}
		
		//validar firma, cuando es tecnico 
		if(JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskName().toUpperCase().contains("EMITIR")) {
			if (token) {
				String idAlfresco = documentoRGBean.getInforme().getDocumento().getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El informe técnico no está firmado electrónicamente.");
					return null;
				}
			} else {
				if(!documentoRGBean.guardarDocumentoManual()) {
					JsfUtil.addMessageError("Debe adjuntar el informe técnico firmado.");
					return null;
				}
			}
		}

		updateOficio();

		 String url = JsfUtil.getRequest().getRequestURI();
	     if (url.equals("/suia-iii/prevencion/registrogeneradordesechos/directorRevisarRegistroInformeOficioAprobacion.jsf") && !documentoRGBean.getOficio().getRequiereCorrecciones()) {
	    	documentoRGBean.getOficio().setDocumento(documentoRGBean.guardarOficioDocumento());
			documentoRGBean.guardarOficio();
		
			try {
				JsfUtil.getBean(DocumentoRGBean.class).visualizarBorrador(true,true);
				registroGeneradorDesechosFacade.guardarBorrador(JsfUtil.getBean(DocumentoRGBean.class).getGenerador(), JsfUtil.getCurrentProcessInstanceId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     }else{
			documentoRGBean.guardarOficio();
		}

		ejecutarLogicaAdicional();

		JsfUtil.getBean(SendCopyBean.class).sendFilesCopies(documentoRGBean.getOficio().getClass().getSimpleName(),
				documentoRGBean.getOficio().getId(), getDiscriminador(), new String[] { documentoRGBean.getOficio().getOficioRealPath() });

		try {
			//Si se cambio de coordinador, se actualiza la variable antes de aprobar la tarea
			Map<String, Object> variables =procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId());
			String coordinadorAnterior=(String)variables.get("coordinador");
			
			Usuario coordinador = null;
			if (documentoRGBean.getGenerador().getResponsabilidadExtendida()) {			
				coordinador = areaFacade.getUsuarioPorRolArea("role.pc.coordinador", documentoRGBean.getGenerador().getAreaResponsable());
			} else {				
				coordinador = areaFacade.getCoordinadorProvincialRegistro(documentoRGBean.getGenerador().getAreaResponsable());
			}
			
			if(!coordinadorAnterior.equals(coordinador.getNombre()))
			{
				Map<String, Object> params = new HashMap<String, Object>();						
				params.put("coordinador", coordinador.getNombre());
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(), params);
			}			
			
			procesoFacade.reasignarTarea(JsfUtil.getLoggedUser(), 
					JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskId(),
					JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskSummary().getActualOwner().getId(), 
					JsfUtil.getLoggedUser().getNombre(),
					conexionBpms.deploymentId(JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskId(), "S"));
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(),
					JsfUtil.getCurrentProcessInstanceId(), null);

			procesoFacade.envioSeguimientoRGD(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId());
		} catch (JbpmException e) {
			e.printStackTrace();
		}
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
		return JsfUtil.actionNavigateToBandeja();
	}

	public void guardar() {
		documentoRGBean.guardarOficio();
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		updateOficio();
		
		JsfUtil.getBean(SendCopyBean.class).guardarFilesCopies(documentoRGBean.getOficio().getClass().getSimpleName(),
				documentoRGBean.getOficio().getId(), getDiscriminador());
	}

	public String cancelar() {
		return JsfUtil.actionNavigateToBandeja();
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/prevencion/registrogeneradordesechos/tecnicoResponsableInformeTecnico.jsf");
	}

	public void ejecutarLogicaAdicional() {

	}

	public boolean validarPuedeContinuarExistenObservacionesSinCorregir() {
		return true;
	}

	public boolean validarPuedeContinuarNoExistenObservacionesSinCorregir() {
		return true;
	}

	public String guardarRegresar() {
		guardar();
		return JsfUtil.actionNavigateTo("/prevencion/registrogeneradordesechos/tecnicoResponsableInformeTecnico.jsf");
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
	
	public void guardarInforme() throws ServiceException, CmisAlfrescoException {
		List<String> errors = new ArrayList<>();
		if(documentoRGBean.isMostrarCampos()) {
			if (documentoRGBean.getOficio().getCumplimiento() != null
					&& documentoRGBean.getOficio().getCumplimiento().trim().isEmpty())
				errors.add("El campo 'CUMPLIMIENTO' es requerido.");
			if (documentoRGBean.getOficio().getEstablecido() != null
					&& documentoRGBean.getOficio().getEstablecido().trim().isEmpty())
				errors.add("El campo 'ESTABLECIDO' es requerido.");
		}

		if (!validarPuedeContinuarExistenObservacionesSinCorregir())
			errors.add("El campo 'ES NECESARIO REALIZAR CORRECCIONES SOBRE EL INFORME TÉCNICO O AL OFICIO DE PRONUNCIAMIENTO' debe tener el valor sí, porque existen observaciones sin corregir.");

		if (!validarPuedeContinuarNoExistenObservacionesSinCorregir())
			errors.add("El campo 'ES NECESARIO REALIZAR CORRECCIONES SOBRE EL INFORME TÉCNICO O AL OFICIO DE PRONUNCIAMIENTO' debe tener el valor 'No', porque no existen observaciones sin corregir.");

		if (!errors.isEmpty()) {
			JsfUtil.addMessageError(errors);
			return ;
		}
		
		updateOficio();
		
		RequestContext.getCurrentInstance().update("formDialog:pnlFirmar");

		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('signDialog').show();");
	}
}
