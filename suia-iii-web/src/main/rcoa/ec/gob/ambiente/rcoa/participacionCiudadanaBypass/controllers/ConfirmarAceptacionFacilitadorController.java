package ec.gob.ambiente.rcoa.participacionCiudadanaBypass.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformeTecnicoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.DocumentoPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.FacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.ProyectoFacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.DocumentosPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.FacilitadorPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.ProyectoFacilitadorPPC;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FacilitadorProyecto;
import ec.gob.ambiente.suia.domain.FacilitadorProyectoLog;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoFacade;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoLogFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class ConfirmarAceptacionFacilitadorController {
	
	private static final Logger LOGGER = Logger.getLogger(ConfirmarAceptacionFacilitadorController.class);
	private final String nombreDocumento = "Formatos_Participación_Social_Bypass.zip";
	@EJB
	private ProcesoFacade procesoFacade;	
	@EJB
	private DocumentoPPCFacade documentosFacade;	
	@EJB
	private FacilitadorPPCFacade facilitadorFacade;	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;	
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
    private DocumentosFacade documentosSuiaIIIFacade;
	@EJB
	private ProyectoFacilitadorPPCFacade proyectoFacilitadorPPCFacade;
	@Inject
	private UtilPPCBypass utilPPC;
	@EJB
	private FacilitadorPPCFacade facilitadorPPCFacade;
	@EJB
    private FacilitadorProyectoFacade facilitadorProyectoFacade;
	@EJB
	private FacilitadorProyectoLogFacade facilitadorProyectoLogFacade;
	@EJB
	private BandejaFacade bandejaFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	
	@EJB
	private InformeTecnicoEsIAFacade informeTecnicoEsIAFacade;
	
	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	private ProyectoFacilitadorPPC proyectoFacilitadorPPC;
	@Getter
	@Setter
	private DocumentosPPC documentosFactura,documentoJustificacion;
    @Getter
	@Setter
    private FacilitadorPPC facilitador = new FacilitadorPPC();
    @Getter
	@Setter
    private Usuario usuarioFacilitador;
	
	@Getter
	@Setter
	private Map<String, Object> variables;	
	@Getter
    @Setter
    private String tramite = "";	
	@Getter
    @Setter
    private String mensajeAprobacion = "";
    @Getter
    @Setter
    private String mensajeUsuario = "";
    @Getter
    @Setter
    private String mensajeAprobacionFA = "";
    @Getter
    @Setter
    private Boolean descargado;
    @Getter
    private Boolean correcto;
    @Getter
	@Setter
	private boolean facilitadorAdicional, existeFacilitadorPrincipal;
    @Getter
	@Setter
	private Integer numeroFacilitadores;
    @Getter
	@Setter
	private Integer verDialogos=0;        
    @Getter
    @Setter
    private byte[] documento;
    
    private Integer idProyecto;
    
    @Getter
    @Setter
    private boolean habilitarEnviar = true;
	
	@PostConstruct
	public void inicio()
	{
		try {
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
			tramite = (String) variables.get("tramite");
			numeroFacilitadores = Integer.valueOf((String) variables.get("numeroFacilitadores"));
			facilitadorAdicional= Boolean.valueOf((String) variables.get("facilitadorAdicional"));			
			facilitador=facilitadorFacade.facilitador(proyectosBean.getProyectoRcoa(), loginBean.getUsuario());
			
			idProyecto = Integer.valueOf((String) variables.get("idProyecto"));
			proyectoFacilitadorPPC = proyectoFacilitadorPPCFacade.getByIdProyecto(idProyecto);
			
			existeFacilitadorPrincipal = facilitadorFacade.existeFacilitadorPrincipal(proyectosBean.getProyectoRcoa());
			
			descargado = false;
			
			configurarMensajes();
		} catch (JbpmException e) {

		}
	}
	
	public void configurarMensajes() {
        mensajeUsuario = "Yo, " +
                loginBean.getUsuario().getPersona().getNombre().toUpperCase() +
                " acepto realizar el Proceso de Participación Ciudadana como facilitador Ambiental.";
        
        mensajeAprobacion = "Usted debe realizar la visita previa al área de influencia social del proyecto " + 
        		proyectosBean.getProyectoRcoa().getNombreProyecto().toUpperCase()+" en coordinación con el operador para identificar las condiciones socio-comunicacionales locales y establecer los mecanismos de Participación Ciudadana más adecuados en función de las características sociales del sector. Finalizada la visita previa usted debe presentar el informe de Planificación del Proceso de Participación Ciudadana con los debidos respaldos de manera física a la Autoridad Ambiental Competente";
        
        mensajeAprobacionFA = "Usted deberá coordinar con el facilitador coordinador, el proceso de Participación Ciudadana, las actividades que ejecutará y de las cuales será responsable en lo que resta del Proceso de Participación Ciudadana.";
    }
	
	public void setCorrecto(Boolean correcto) {
		verDialogos=0;
		if(correcto)
			verDialogos=1;
		else
			verDialogos=2;
		
		facilitador.setAceptaProyecto(correcto);
		this.correcto=correcto;
	}
	
	public void cerrar() {
		verDialogos=0;
	}
	
	public void aceptar() {
		if(descargado)
			verDialogos=0;
		else 
			JsfUtil.addMessageError("Debe descargar los formatos");
	}
	
	public void descargarFactura()
	{
		try {
			List<DocumentosPPC> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyectoRcoa().getId(), TipoDocumentoSistema.FACTURA_PAGO_FACILITADORES, "FACTURA PAGOS");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Factura-"+new Date().getTime());
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	public void uploadDocumentoJustificativo(FileUploadEvent event)
	{
		documentoJustificacion = new DocumentosPPC();
		byte[] contenido=event.getFile().getContents();
		documentoJustificacion.setContenidoDocumento(contenido);
		documentoJustificacion.setNombreDocumento(event.getFile().getFileName());
		documentoJustificacion.setExtencionDocumento(".pdf");		
		documentoJustificacion.setTipo("application/pdf");
		documentoJustificacion.setNombreTabla(FacilitadorPPC.class.getSimpleName());
		documentoJustificacion.setIdTabla(facilitador.getId());
		documentoJustificacion.setDescripcion(FacilitadorPPC.class.getSimpleName());
		documentoJustificacion.setProyectoFacilitadorPPC(proyectoFacilitadorPPC);
		
		habilitarEnviar = false;
	}
	
	public boolean guardarDocumentoJustificativo()
	{
		boolean estado = false;		
		try {
			documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental(), "PARTICIPACION CIUDADANA", bandejaTareasBean.getTarea().getProcessInstanceId(), documentoJustificacion, TipoDocumentoSistema.JUSTIFICACION_RECHAZO_PPS);
			estado=true;
		} catch (ServiceException | CmisAlfrescoException e) {				
			estado=false;
		}
		return estado;
	}
	
	public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        documento = documentosSuiaIIIFacade.descargarDocumentoPorNombre(nombreDocumento);
        if (documento != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(documento), "application/zip");
            content.setName(nombreDocumento);
        }
        
        descargado = true;
        habilitarEnviar = false;
        return content;

    }
	
	public void completarTarea(){
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
            params.put("aceptaProyecto", correcto);
            
            //busqueda usuario tecnico responsable PPC para envío de notificación
            Boolean tecnicoActualizado = false;
            String usuarioResponsable = (String) variables.get("tecnicoResponsableEIA");
            Usuario tecnicoResponsable = null;
            if (usuarioResponsable == null) {
            	tecnicoResponsable = utilPPC.asignarRol(proyectoFacilitadorPPC.getProyectoLicenciaCoa(), "tecnicoPPC");
            	tecnicoActualizado = true;
			} else {
				tecnicoResponsable = usuarioFacade.buscarUsuario(usuarioResponsable);
				if (tecnicoResponsable.getEstado() == false) {
					tecnicoResponsable = utilPPC.asignarRol(proyectoFacilitadorPPC.getProyectoLicenciaCoa(), "tecnicoPPC");
					tecnicoActualizado = true;
				}
			}
			
			if(tecnicoResponsable == null) {
				verDialogos=0;
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return;
			}
			
			if(tecnicoActualizado)
				params.put("tecnicoResponsableEIA",tecnicoResponsable.getNombre());

			if (correcto) {
				if(!descargado) {
					JsfUtil.addMessageError("Debe descargar los formatos antes de enviar");
					return;
				}
				
//	            verificar si hay facilitador principal ya asignado
				if(!facilitadorFacade.existeFacilitadorPrincipal(proyectosBean.getProyectoRcoa()))
				{
					facilitador.setEsPrincipal(true);
					facilitador.setFechaAceptaProyecto(new Date());
					facilitadorFacade.guardar(facilitador);
					params.put("facilitadorPrincipal", loginBean.getUsuario().getNombre());
				}
				else
				{
					facilitador.setEsPrincipal(false);
					facilitador.setFechaAceptaProyecto(new Date());
					facilitadorFacade.guardar(facilitador);
				}
				
				FacilitadorProyecto facilitadorProyecto = new FacilitadorProyecto();
                facilitadorProyecto.setUsuario(loginBean.getUsuario());
                facilitadorProyecto.setProyecto(null);
                facilitadorProyecto.setAceptacion(true);
                facilitadorProyecto.setFacilitadorEncargado(facilitador.getEsPrincipal());
                facilitadorProyecto.setFechaAceptacion(new Date());
                facilitadorProyecto.setResetarTareaok(1);                    
                facilitadorProyecto.setParticipacionSocialAmbiental(null);
                facilitadorProyecto.setProyectoRcoa(proyectosBean.getProyectoRcoa().getId());                
                facilitadorProyectoFacade.guardar(facilitadorProyecto);
                
			}
			else
			{
				if(documentoJustificacion==null || documentoJustificacion.getContenidoDocumento()==null)
				{
					JsfUtil.addMessageError("Debe adjuntar el justificativo.");
					return;
				}
				if(!guardarDocumentoJustificativo())
				{
					JsfUtil.addMessageError("Error al guardar el archivo de justificación.");
					return;
				}
				
				//validar la asignacion de los facilitadores
				String[] facilitadoresLista = new String[1];
				List<String> facilitadores = new ArrayList<String>();
				if(facilitador.getTarifaEspecial() != null && facilitador.getTarifaEspecial()) {
					//si el pago del facilitador se realizó por tarifa especial
					//el nuevo facilitador tampoco debe ser de galapagos para mantener el valor de la tarifa asignada
					facilitadores = buscarFacilitadorContinental();
					if(facilitadores == null)
					{
						JsfUtil.addMessageError("Error al completar la tarea. No existen la cantidad de facilitadores solicitados disponibles.");
		                return;
					}
				} else {
					facilitadores = utilPPC.facilitadoresBypass(1, proyectosBean.getProyectoRcoa());			
					if(facilitadores.size()==0)
					{
						JsfUtil.addMessageError("Error al completar la tarea. No existen la cantidad de facilitadores solicitados disponibles.");
		                return;
					}
				}
				
				facilitador.setFechaAceptaProyecto(new Date());
				facilitadorFacade.guardar(facilitador);
				
				Integer cont = 0;
				for (String usuario : facilitadores) {
	              facilitadoresLista[cont++] = usuario;
	              FacilitadorPPC facilitador= new FacilitadorPPC();
	              facilitador.setEstado(true);
	              facilitador.setEsAdicional(facilitadorAdicional);
	              facilitador.setProyectoLicenciaCoa(proyectosBean.getProyectoRcoa());
	              facilitador.setUsuario(usuarioFacade.buscarUsuario(usuario));
	              facilitador.setTarifaEspecial(this.facilitador.getTarifaEspecial());
	              facilitador.setFechaRegistroPago(this.facilitador.getFechaRegistroPago());
	              facilitadorPPCFacade.guardar(facilitador);
	              params.put("facilitadorNuevo",usuario);
	              
	              usuarioFacilitador = facilitador.getUsuario();
				}
				
				FacilitadorProyectoLog facilitadorProyectoLog = new FacilitadorProyectoLog();
                facilitadorProyectoLog.setUsuario(loginBean.getUsuario());
                facilitadorProyectoLog.setFechaNegacion(new Date());
                facilitadorProyectoLog.setObservacion("");
                facilitadorProyectoLog.setResetarTarea(1);
                facilitadorProyectoLog.setProyecto(null);
                facilitadorProyectoLog.setNegacionAutomatica(false);
                facilitadorProyectoLog.setProyectoRcoa(proyectosBean.getProyectoRcoa().getId());
                facilitadorProyectoLogFacade.guardar(facilitadorProyectoLog);
			}
			
            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);

            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),  bandejaTareasBean.getProcessId(), null);
            
            notificaciones(tecnicoResponsable);
            
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (Exception e) {					
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	private void notificaciones(Usuario tecnicoResponsablePPC) {
		try {
			if(!correcto) {
	        	//enviar notificacion de rechazo
	
				Object[] parametrosCorreoTecnicos = new Object[] {loginBean.getUsuario().getPersona().getTipoTratos().getNombre(), 
						loginBean.getUsuario().getPersona().getNombre(), proyectoFacilitadorPPC.getProyectoLicenciaCoa().getNombreProyecto(), 
						proyectoFacilitadorPPC.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), facilitador.getJustificacion() };
				
				String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
								"bodyNotificacionRechazoFacilitadorJustificativoByPass",
								parametrosCorreoTecnicos);
				
				//guardar el documento de justificacion para adjuntarlo en la notificacion
				FileOutputStream file;
				File fileArchivo = new File(System.getProperty("java.io.tmpdir")+"/"+documentoJustificacion.getNombreDocumento());
				file = new FileOutputStream(fileArchivo);
				file.write(documentoJustificacion.getContenidoDocumento());
				file.close();
				
				List<String> listaArchivos = new ArrayList<>();
				listaArchivos.add(documentoJustificacion.getNombreDocumento());
				
				InformacionProyectoEia esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectosBean.getProyectoRcoa());
				InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, InformeTecnicoEsIA.social);
				
				if(informeTecnico != null && informeTecnico.getId() != null) {
					//si existe técnico social
					tecnicoResponsablePPC = usuarioFacade.buscarUsuario(informeTecnico.getUsuarioCreacion());
				}
	
				Email.sendEmailAdjuntos(tecnicoResponsablePPC, "Regularización Ambiental Nacional", notificacion, listaArchivos, tramite, loginBean.getUsuario());
				
				//envia notificacion al facilitador principal
				FacilitadorPPC facilitadorPrincipal = new FacilitadorPPC();
				facilitadorPrincipal = facilitadorFacade.facilitadorPrincipal(proyectoFacilitadorPPC.getProyectoLicenciaCoa());
				if(facilitadorPrincipal != null) {
					Usuario coordinador = facilitadorPrincipal.getUsuario();
					Email.sendEmailAdjuntos(coordinador, "Regularización Ambiental Nacional", notificacion, listaArchivos, tramite, loginBean.getUsuario());
				}
	        	
				//envio notificación a nuevo facilitador asignado
				if(usuarioFacilitador != null) {
					Object[] parametrosCorreoNuevo = new Object[] {usuarioFacilitador.getPersona().getTipoTratos().getNombre(), 
							usuarioFacilitador.getPersona().getNombre(), proyectoFacilitadorPPC.getProyectoLicenciaCoa().getNombreProyecto(), 
							proyectoFacilitadorPPC.getProyectoLicenciaCoa().getCodigoUnicoAmbiental() };
					
					String notificacionNuevo = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
									"bodyNotificacionDesignacionFacilitadorByPass",
									parametrosCorreoNuevo);
					
					Email.sendEmail(usuarioFacilitador, "Regularización Ambiental Nacional", notificacionNuevo, tramite, loginBean.getUsuario());
				}
	        } else {
	        	if(numeroFacilitadores > 1 && !existeFacilitadorPrincipal) {
	        		//Notificación de facilitador coordinador cuando hay varios facilitadores
	        		
	        		Usuario coordinador = JsfUtil.getLoggedUser();
					
					Object[] parametrosCorreoFP = new Object[] {coordinador.getPersona().getTipoTratos().getNombre(), 
							coordinador.getPersona().getNombre(), proyectoFacilitadorPPC.getProyectoLicenciaCoa().getNombreProyecto(), 
	    					proyectoFacilitadorPPC.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), proyectoFacilitadorPPC.getProyectoLicenciaCoa().getNombreProyecto(), 
	    					proyectoFacilitadorPPC.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), numeroFacilitadores };
	    			
	    			String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
	    							"bodyNotificacionDesignacionCoordinadorByPass",
	    							parametrosCorreoFP);
	    			
	    			Email.sendEmail(coordinador, "Regularización Ambiental Nacional", notificacion, tramite, loginBean.getUsuario());
	        	}
	        	
	        	if(numeroFacilitadores > 1 && existeFacilitadorPrincipal) {
	        		//Notificación desde el segundo facilitar que acepta indicando quien es el facilitador coordinador
	        		
	        		FacilitadorPPC facilitadorPrincipal = new FacilitadorPPC();
					facilitadorPrincipal = facilitadorFacade.facilitadorPrincipal(proyectoFacilitadorPPC.getProyectoLicenciaCoa());
					Usuario coordinador = facilitadorPrincipal.getUsuario();
					
					List<FacilitadorPPC> listaFacilitadores = new ArrayList<FacilitadorPPC>();
					listaFacilitadores.add(facilitadorPrincipal);
					String contactoCoordinador = htmlInfoFacilitadores(listaFacilitadores, false).get(1);
					
					Object[] parametrosCorreoFP = new Object[] {JsfUtil.getLoggedUser().getPersona().getNombre(), 
							proyectoFacilitadorPPC.getProyectoLicenciaCoa().getNombreProyecto(), 
	    					proyectoFacilitadorPPC.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), coordinador.getPersona().getNombre(), contactoCoordinador};
	    			
	    			String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
	    							"bodyNotificacionCoordinadorFacilitadoresByPass",
	    							parametrosCorreoFP);
	    			
	    			Email.sendEmail(JsfUtil.getLoggedUser(), "Regularización Ambiental Nacional", notificacion, tramite, loginBean.getUsuario());
	        	}
	        	
	        	int tareasConfirmar = 0;
	        	if(numeroFacilitadores > 1) {
	        		//verificar si se asignaron todos los facilitadores
	        		List<TaskSummary> listaTareas=procesoFacade.getTaskReservedInProgress(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
	    			if(listaTareas != null && listaTareas.size() > 0) {
	    				for (TaskSummary tarea : listaTareas) {
	    					String nombretarea = bandejaFacade.getNombreTarea(tarea.getId());
	    					if(nombretarea.equals("confirmarFacilitadorPPCBypass")) {
	    						tareasConfirmar++;
	    						break;
	    					}
	    				}
	    			}
	        	}
	        	
	        	if(tareasConfirmar == 0) {
	        		if(numeroFacilitadores > 1) {		    			
		    			//Notificacion al facilitador principal cuando hay facilitadores adicionales
	        			FacilitadorPPC facilitadorPrincipal = new FacilitadorPPC();
						facilitadorPrincipal = facilitadorFacade.facilitadorPrincipal(proyectoFacilitadorPPC.getProyectoLicenciaCoa());
						Usuario coordinador = facilitadorPrincipal.getUsuario();
						
						List<String> infoFacilitadores =  getInfoFacilitadores(proyectoFacilitadorPPC.getProyectoLicenciaCoa(), false, false);
						
						String nombresFacilitadores = infoFacilitadores.get(0);
						String facilitadores = infoFacilitadores.get(1);
						
						Object[] parametrosCorreoFPa = new Object[] {coordinador.getPersona().getTipoTratos().getNombre(), 
								coordinador.getPersona().getNombre(), nombresFacilitadores, proyectoFacilitadorPPC.getProyectoLicenciaCoa().getNombreProyecto(), 
		    					proyectoFacilitadorPPC.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), facilitadores };
		    			
		    			String notificacionAdicionales = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
		    							"bodyNotificacionFacilitadoresAdicionalesByPass",
		    							parametrosCorreoFPa);
		    			
		    			Email.sendEmail(coordinador, "Regularización Ambiental Nacional", notificacionAdicionales, tramite, loginBean.getUsuario());
					}
	    			
	    			//Notificacion al operador
	    			Usuario usuarioOperador = proyectoFacilitadorPPC.getProyectoLicenciaCoa().getUsuario();
					
					List<String> datosOperador = usuarioFacade.recuperarNombreOperador(usuarioOperador);
					String nombreOperador = datosOperador.get(0);
					String tratamientoOperador = datosOperador.get(2);
					
					List<String> infoFacilitadores =  getInfoFacilitadores(proyectoFacilitadorPPC.getProyectoLicenciaCoa(), false, true);
					
					String facilitadores = infoFacilitadores.get(1);
					
					Object[] parametrosCorreoFD = new Object[] {tratamientoOperador, 
							nombreOperador, facilitadores };
	    			
	    			String notificacionFD = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
	    							"bodyNotificacionFacilitadoresDesignadosByPass",
	    							parametrosCorreoFD);
	    			
	    			Organizacion orga = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
	    			if (orga != null) {
	    				Email.sendEmail(orga, "Regularización Ambiental Nacional", notificacionFD, tramite, usuarioOperador, loginBean.getUsuario());
	    			} else {
	    				Email.sendEmail(usuarioOperador, "Regularización Ambiental Nacional", notificacionFD, tramite, loginBean.getUsuario());
	    			}
	    			
				}
	        }
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}
	
	public List<String> getInfoFacilitadores(ProyectoLicenciaCoa proyecto, Boolean adicionales, Boolean agregarPrincipal) {
		List<FacilitadorPPC> listaFacilitadores = new ArrayList<FacilitadorPPC>();
		List<String> resultado = new ArrayList<>();
		
		if(!adicionales) {
			if(agregarPrincipal) {
				FacilitadorPPC principal = facilitadorPPCFacade.facilitadorPrincipal(proyecto);
				listaFacilitadores.add(principal);
			}
			
			listaFacilitadores.addAll(facilitadorPPCFacade.facilitadores(proyecto));
			
			resultado = htmlInfoFacilitadores(listaFacilitadores, true);
		} else {
			listaFacilitadores = facilitadorPPCFacade.facilitadoresAdicionales(proyecto);
			
			resultado = htmlInfoFacilitadores(listaFacilitadores, true);
		}
		
		return resultado;
	}
	
	public List<String> htmlInfoFacilitadores(List<FacilitadorPPC> listaFacilitadores, Boolean agregarNombre) {
		String htmlFacilitadores = "";
		String nombreFacilitadores = "";
		
		for (FacilitadorPPC facilitador : listaFacilitadores) {
			String info = "";
			String numero = "", correo = null;
			
			String tratamiento = facilitador.getUsuario().getPersona().getTipoTratos().getNombre();
			String nombre = (agregarNombre) ? facilitador.getUsuario().getPersona().getNombre() : "";
			
			List<Contacto> contactos = facilitador.getUsuario().getPersona().getContactos();
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()){
					if(correo == null)
						correo=contacto.getValor();
				} else if (contacto.getFormasContacto().getId().equals(FormasContacto.TELEFONO) && contacto.getEstado() && !contacto.getValor().isEmpty()){
					numero += (numero == "") ? contacto.getValor() : " - " + contacto.getValor();
				}else if (contacto.getFormasContacto().getId().equals(FormasContacto.CELULAR) && contacto.getEstado() && !contacto.getValor().isEmpty()){
					numero += (numero == "") ? contacto.getValor() : " - " + contacto.getValor();
				}
			}
			
			info = nombre +"<br>Número de contacto: " + numero + "<br>Correo electrónico: " + correo;
			
			htmlFacilitadores += (htmlFacilitadores == "") ? info : "<br><br>" + info;
			
			nombreFacilitadores += (nombreFacilitadores == "") ? tratamiento + " " + nombre : ", " +  tratamiento + " " + nombre;
		}
		
		List<String> resultado = new ArrayList<>();
		resultado.add(nombreFacilitadores);
		resultado.add(htmlFacilitadores);
		
		return resultado;
	}
	
	public void validateDatos(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
			if(!correcto && (documentoJustificacion==null || documentoJustificacion.getContenidoDocumento()==null))
			{
				errorMessages.add(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Debe adjuntar el justificativo.", null)); 
			}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void seleccionar()
	{
		habilitarEnviar = true;
	}
	
	private List<String> buscarFacilitadorContinental(){
		List<String> result = new ArrayList<String>();
		List<String> facilitadores = utilPPC.facilitadoresBypass(2, proyectosBean.getProyectoRcoa());			
		if(facilitadores.size()==0)
			return null;
		
		String codeGalapagos = Constantes.CODIGO_INEC_GALAPAGOS;
		
		for (String usuario : facilitadores) {
			Usuario ufacilitador = usuarioFacade.buscarUsuario(usuario);
			String codeProvinciaFacilitador = ufacilitador.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec();
			if(!codeProvinciaFacilitador.equals(codeGalapagos)) {
				result.add(usuario);
				break;
			}
		}
		
		if(result.size() == 0)
			buscarFacilitadorContinental();
		
		return result;
	}

}
