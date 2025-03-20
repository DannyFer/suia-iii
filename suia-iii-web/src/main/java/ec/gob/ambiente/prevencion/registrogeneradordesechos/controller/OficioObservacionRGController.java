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

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.InformeTecnicoRegistroGeneradorDesechos;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;

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
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class OficioObservacionRGController implements Serializable {

	private static final long serialVersionUID = -3224235096257306941L;

	private final Logger LOG = Logger.getLogger(OficioObservacionRGController.class);

	@EJB
	protected ProcesoFacade procesoFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ConexionBpms conexionBpms;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;

	@ManagedProperty(value = "#{documentoRGBean}")
	@Getter
	@Setter
	private DocumentoRGBean documentoRGBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{observacionesController}")
	private ObservacionesController observacionesController;
	
	@Getter
	@Setter
	private Boolean firmaSoloToken, token;

	@PostConstruct
	public void init() {
		firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
		
		verificaToken();
		
		documentoRGBean.inicializarInformeTecnicoAsociado();
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
			documentoRGBean.visualizarOficioObservaciones(true, true);
		} catch (Exception e) {
			LOG.error("Error al cargar el oficio de observación del registro de generador", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public String aceptar() throws ServiceException, CmisAlfrescoException {
		List<String> errors = new ArrayList<>();
		Boolean Validar = false;
		if(documentoRGBean.isMostrarCampos()) {
			if (documentoRGBean.getOficioObservaciones().getCumplimiento() != null
					&& documentoRGBean.getOficioObservaciones().getCumplimiento().trim().isEmpty())
				errors.add("El campo 'CUMPLIMIENTO' es requerido.");
			if (documentoRGBean.getOficioObservaciones().getEstablecido() != null
					&& documentoRGBean.getOficioObservaciones().getEstablecido().trim().isEmpty())
				errors.add("El campo 'ESTABLECIDO' es requerido.");
		}

		for (RolUsuario rol : JsfUtil.getLoggedUser().getRolUsuarios()){
			if (rol.getRol().getNombre().contains("TÉCNICO ANALISTA"))
					Validar = true;
		}

		if (Validar) {
			if (!validarPuedeContinuarExistenObservacionesSinCorregir())
				errors.add("El campo 'ES NECESARIO REALIZAR CORRECCIONES SOBRE EL INFORME TÉCNICO O AL OFICIO DE PRONUNCIAMIENTO' debe tener el valor 'Sí', porque existen observaciones sin corregir.");
		}else {
			if (!validarPuedeContinuarNoExistenObservacionesSinCorregir())
				errors.add("El campo 'ES NECESARIO REALIZAR CORRECCIONES SOBRE EL INFORME TÉCNICO O AL OFICIO DE PRONUNCIAMIENTO' debe tener el valor 'No', porque no existen observaciones sin corregir.");
		}
		
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

		documentoRGBean.guardarOficioObservaciones();
		String url = JsfUtil.getRequest().getRequestURI();
		if (url.equals("/suia-iii/prevencion/registrogeneradordesechos/directorRevisarInformeOficioObservaciones.jsf") && !documentoRGBean.getOficioObservaciones().getRequiereCorrecciones()) {
			documentoRGBean.getOficioObservaciones().setDocumento(documentoRGBean.guardarOficioObservacionesDocumento());
			documentoRGBean.guardarOficioObservaciones();
		 }

		ejecutarLogicaAdicional();

		updateOficio();

		JsfUtil.getBean(SendCopyBean.class).sendFilesCopies(
				documentoRGBean.getOficioObservaciones().getClass().getSimpleName(),
				documentoRGBean.getOficioObservaciones().getId(), getDiscriminador(),
				new String[] { documentoRGBean.getOficioObservaciones().getOficioRealPath() });

		try {
			//Si se cambio de coordinador, se actualiza la variable antes de aprobar la tarea
			Map<String, Object> variables =procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId());
			String coordinadorAnterior=(String)variables.get("coordinador");
			String directorAnterior=(String)variables.get("director");
			
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
			
			//Si se cambio de director, se actualiza la variable antes de aprobar la tarea
			Usuario director = null;
			if (documentoRGBean.getGenerador().getResponsabilidadExtendida()) {			
				director = areaFacade.getUsuarioPorRolArea("role.pc.director", documentoRGBean.getGenerador().getAreaResponsable());
			} else {				
				if(documentoRGBean.getGenerador().getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS))
					director = areaFacade.getDirectorProvincial(documentoRGBean.getGenerador().getAreaResponsable());
				else
					director = areaFacade.getDirectorProvincial(documentoRGBean.getGenerador().getAreaResponsable().getArea());
			}
			
			if(!directorAnterior.equals(director.getNombre()))
			{
				Map<String, Object> params = new HashMap<String, Object>();						
				params.put("director", director.getNombre());
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

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/prevencion/registrogeneradordesechos/tecnicoResponsableInformeTecnico.jsf");
	}

	public void guardar() {
		documentoRGBean.guardarOficioObservaciones();
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		updateOficio();
		
		JsfUtil.getBean(SendCopyBean.class).guardarFilesCopies(documentoRGBean.getOficioObservaciones().getClass().getSimpleName(),
				documentoRGBean.getOficioObservaciones().getId(), getDiscriminador());
	}

	public String cancelar() {
		return JsfUtil.actionNavigateToBandeja();
	}

	public void ejecutarLogicaAdicional() {

	}

	public boolean validarPuedeContinuarExistenObservacionesSinCorregir() {
		if (documentoRGBean.getOficioObservaciones().getRequiereCorrecciones() == Boolean.FALSE &&
				!observacionesController.getListaObservacionesBB().get("top").getListaObservaciones().isEmpty()){
			return false;
		}

		return true;
	}

	public boolean validarPuedeContinuarNoExistenObservacionesSinCorregir() {
		if (documentoRGBean.getOficioObservaciones().getRequiereCorrecciones() == Boolean.TRUE &&
				observacionesController.getListaObservacionesBB().get("top").getListaObservaciones().isEmpty()){
			return false;
		}

		return true;
	}

	public String guardarRegresar() {
		guardar();
		return JsfUtil.actionNavigateTo("/prevencion/registrogeneradordesechos/tecnicoResponsableInformeTecnico.jsf");
	}


	public void guardarInforme() throws ServiceException, CmisAlfrescoException {
		List<String> errors = new ArrayList<>();
		Boolean Validar = false;
		
		if(documentoRGBean.isMostrarCampos()){
			if (documentoRGBean.getOficioObservaciones().getCumplimiento() != null
					&& documentoRGBean.getOficioObservaciones().getCumplimiento().trim().isEmpty())
				errors.add("El campo 'CUMPLIMIENTO' es requerido.");
			if (documentoRGBean.getOficioObservaciones().getEstablecido() != null
					&& documentoRGBean.getOficioObservaciones().getEstablecido().trim().isEmpty())
				errors.add("El campo 'ESTABLECIDO' es requerido.");
		}		

		for (RolUsuario rol : JsfUtil.getLoggedUser().getRolUsuarios()){
			if (rol.getRol().getNombre().contains("TÉCNICO ANALISTA"))
					Validar = true;
		}

		if (Validar) {
			if (!validarPuedeContinuarExistenObservacionesSinCorregir())
				errors.add("El campo 'ES NECESARIO REALIZAR CORRECCIONES SOBRE EL INFORME TÉCNICO O AL OFICIO DE PRONUNCIAMIENTO' debe tener el valor 'Sí', porque existen observaciones sin corregir.");
		}else {
			if (!validarPuedeContinuarNoExistenObservacionesSinCorregir())
				errors.add("El campo 'ES NECESARIO REALIZAR CORRECCIONES SOBRE EL INFORME TÉCNICO O AL OFICIO DE PRONUNCIAMIENTO' debe tener el valor 'No', porque no existen observaciones sin corregir.");
		}
		
		if (!errors.isEmpty()) {
			JsfUtil.addMessageError(errors);
			return ;
		}
		
		RequestContext.getCurrentInstance().update("formDialog:pnlFirmar");

		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('signDialog').show();");
		
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
