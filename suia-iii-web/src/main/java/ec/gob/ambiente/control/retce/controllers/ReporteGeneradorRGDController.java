package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.control.retce.beans.AutogestionDesechosBean;
import ec.gob.ambiente.control.retce.beans.DeclaracionGeneradorRetceBean;
import ec.gob.ambiente.control.retce.beans.DisposicionDesechosRetceBean;
import ec.gob.ambiente.control.retce.beans.EliminacionDesechosRetceBean;
import ec.gob.ambiente.control.retce.beans.ExportacionDesechosBean;
import ec.gob.ambiente.control.retce.beans.IdentificacionDesechosBean;
import ec.gob.ambiente.control.retce.beans.TransporteDesechosBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.retce.model.TecnicoResponsable;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.OficioRetceFacade;
import ec.gob.ambiente.retce.services.TecnicoResponsableFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class ReporteGeneradorRGDController implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(ReporteGeneradorRGDController.class);
	
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{wizardBean}")
    @Getter
    @Setter
    private WizardBean wizardBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{declaracionGeneradorRetceBean}")
	private DeclaracionGeneradorRetceBean declaracionGeneradorRetceBean;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@EJB
    private TecnicoResponsableFacade tecnicoResponsableFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private InformacionProyectoFacade informacionProyectoFacade;
	
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
    private UsuarioFacade usuarioFacade;
	
	@EJB
    private OrganizacionFacade organizacionFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private OficioRetceFacade oficioRetceFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@Getter
	@Setter
	private List<GeneradorDesechosPeligrosos> listaGeneradores;
	
	@Getter
	@Setter
	private List<DesechoPeligroso> listaDesechosGenerador;
	
	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrosos generadorSeleccionado;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrososRetce generadorDesechosRetce;
	
	@Getter
	@Setter
	private TecnicoResponsable tecnicoResponsable;
	
	@Getter
	@Setter
	private Documento documentoObservaciones;
	
	@Getter
	@Setter
	private Double factorKgT;
	
	@Getter
	@Setter
	private List<Integer> listaAnios;
	
	@Getter
	@Setter
	private Boolean habilitarSiguiente, aceptarCondiciones;

	@PostConstruct
	public void init() {
		try {
			
			generadorDesechosRetce = declaracionGeneradorRetceBean.getGeneradorDesechosRetce();
			tecnicoResponsable = generadorDesechosRetce.getTecnicoResponsable();
			informacionProyecto = generadorDesechosRetce.getInformacionProyecto();
			
			if(tecnicoResponsable == null)
				tecnicoResponsable = new TecnicoResponsable();
			if(informacionProyecto == null)
				informacionProyecto = new InformacionProyecto();
			
			documentoObservaciones = new Documento();

			habilitarSiguiente = false;
			aceptarCondiciones = false;
			
			JsfUtil.getBean(AutogestionDesechosBean.class).cargarDatos();
			habilitarSiguiente = JsfUtil.getBean(AutogestionDesechosBean.class).isHabilitarBtnSiguiente();
			
//			JsfUtil.getBean(TransporteDesechosBean.class).cargarDatos();
//			JsfUtil.getBean(ExportacionDesechosBean.class).cargarDatos();
//			JsfUtil.getBean(EliminacionDesechosRetceBean.class).cargarDatos();
//			JsfUtil.getBean(DisposicionDesechosRetceBean.class).cargarDatos();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String btnSiguiente() {
		habilitarSiguiente = false;
        String currentStep = wizardBean.getCurrentStep();
        
        declaracionGeneradorRetceBean.limpiarListasDatosAsociados();
        
        //Cuando es correccion se presenta siempre los formularios para edicion, ya no solo cuando tienen obs del tecnico, x la relacion que existe entre las secciones 
        if (currentStep == null || currentStep.equals("paso2")) {
        	JsfUtil.getBean(AutogestionDesechosBean.class).cargarDatos();
        	habilitarSiguiente = JsfUtil.getBean(AutogestionDesechosBean.class).isHabilitarBtnSiguiente();
        } else if (currentStep.equals("paso3") || currentStep.equals("paso3Ver")){
        	JsfUtil.getBean(TransporteDesechosBean.class).cargarDatos();
        	habilitarSiguiente = JsfUtil.getBean(TransporteDesechosBean.class).isHabilitarBtnSiguiente();
        } else if (currentStep.equals("paso4") || currentStep.equals("paso4Ver")){
        	JsfUtil.getBean(ExportacionDesechosBean.class).cargarDatos();
        	habilitarSiguiente = JsfUtil.getBean(ExportacionDesechosBean.class).isHabilitarBtnSiguiente();
        } else if (currentStep.equals("paso5") || currentStep.equals("paso5Ver")){
        	JsfUtil.getBean(EliminacionDesechosRetceBean.class).cargarDatos();
        	habilitarSiguiente = JsfUtil.getBean(EliminacionDesechosRetceBean.class).isHabilitarBtnSiguiente();
        } else if (currentStep.equals("paso6") || currentStep.equals("paso6Ver")){
        	JsfUtil.getBean(DisposicionDesechosRetceBean.class).cargarDatos();
        	habilitarSiguiente = JsfUtil.getBean(DisposicionDesechosRetceBean.class).isHabilitarBtnSiguiente();
        }

        return null;

    }
	
	public String btnAtras() {
		declaracionGeneradorRetceBean.limpiarListasDatosAsociados();
        return null;
    }
		
	public void guardar() {
        try {
            String currentStep = wizardBean.getCurrentStep();
            Boolean guardadoExitoso = true;
            
            if (currentStep == null || currentStep.equals("paso2")) {
            	JsfUtil.getBean(IdentificacionDesechosBean.class).guardarIdentificacionDesechos();
            	guardadoExitoso = JsfUtil.getBean(IdentificacionDesechosBean.class).validateIdentificacionDesechos();
            	if(!guardadoExitoso) {
            		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            		JsfUtil.addMessageError("Para cotinuar debe completar la información de todos los desechos peligrosos del RGD.");
            	}
            } else if (currentStep.equals("paso3")) {
            	guardadoExitoso = JsfUtil.getBean(AutogestionDesechosBean.class).guardarAutogestionDesechos();
            } else if (currentStep.equals("paso4")) {
            	guardadoExitoso = JsfUtil.getBean(TransporteDesechosBean.class).guardarTransporte();
            } else if (currentStep.equals("paso5")) {
            	guardadoExitoso = JsfUtil.getBean(ExportacionDesechosBean.class).guardarExportacionDesechos();
            } else if (currentStep.equals("paso6")) {
            	guardadoExitoso = JsfUtil.getBean(EliminacionDesechosRetceBean.class).guardarEliminacion();
            } else if (currentStep.equals("paso7")) {
            	guardadoExitoso = JsfUtil.getBean(DisposicionDesechosRetceBean.class).guardarDisposicion();
            }
            
            if(guardadoExitoso){
	            habilitarSiguiente = true;
	            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            }

        } catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }
	
	// INFORMACIÓN DEL TÉCNICO
	public void validarCedulaListener() {
		
		String cedulaRuc=tecnicoResponsable.getIdentificador();
		if(tecnicoResponsable == null)
			tecnicoResponsable=new TecnicoResponsable();
		
		if(JsfUtil.validarCedulaORUC(cedulaRuc))
		{	
			try {
				if(cedulaRuc.length()==10){
					Usuario usuario =usuarioFacade.buscarUsuario(cedulaRuc);
					if(usuario!=null)
					{
						tecnicoResponsable.setIdentificador(usuario.getNombre());
						tecnicoResponsable.setNombre(usuario.getPersona().getNombre());						
						for (Contacto contacto : usuario.getPersona().getContactos()) {
							switch (contacto.getFormasContacto().getId()) {
							case FormasContacto.EMAIL:
								tecnicoResponsable.setCorreo(contacto.getValor());
								break;
							case FormasContacto.TELEFONO:
								tecnicoResponsable.setTelefono(contacto.getValor());
								break;
							case FormasContacto.CELULAR:
								tecnicoResponsable.setCelular(contacto.getValor());
								break;
							default:
								break;
							}
						}						
						return;
					}
					
					Cedula cedula = consultaRucCedula
			                .obtenerPorCedulaRC(
			                        Constantes.USUARIO_WS_MAE_SRI_RC,
			                        Constantes.PASSWORD_WS_MAE_SRI_RC,
			                        cedulaRuc);
					tecnicoResponsable.setIdentificador(cedula.getCedula());
					tecnicoResponsable.setNombre(cedula.getNombre());
				}else if(cedulaRuc.length()==13){
					Organizacion orga=organizacionFacade.buscarPorRuc(cedulaRuc);
					if(orga!=null)
					{
						tecnicoResponsable.setIdentificador(orga.getRuc());
						tecnicoResponsable.setNombre(orga.getNombre());
						for (Contacto contacto : orga.getContactos()) {
							switch (contacto.getFormasContacto().getId()) {
							case FormasContacto.EMAIL:
								tecnicoResponsable.setCorreo(contacto.getValor());
								break;
							case FormasContacto.TELEFONO:
								tecnicoResponsable.setTelefono(contacto.getValor());
								break;
							case FormasContacto.CELULAR:
								tecnicoResponsable.setCelular(contacto.getValor());
								break;
							default:
								break;
							}
						}
						return;
					}
					
					ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
			                .obtenerPorRucSRI(
			                        Constantes.USUARIO_WS_MAE_SRI_RC,
			                        Constantes.PASSWORD_WS_MAE_SRI_RC,
			                        cedulaRuc);
					if(contribuyenteCompleto!=null)
					{
						tecnicoResponsable.setIdentificador(contribuyenteCompleto.getNumeroRuc());
						tecnicoResponsable.setNombre(contribuyenteCompleto.getRazonSocial());
						tecnicoResponsable.setCorreo(contribuyenteCompleto.getEmail());
						tecnicoResponsable.setTelefono(contribuyenteCompleto.getTelefonoDomicilio());					
					}else
					{
						JsfUtil.addMessageError("Error al validar Ruc, Servicio Web No Disponible");
						return;
					}
				}				
			} catch (Exception e) {
				JsfUtil.addMessageError("Error al validar Cédula o Ruc");
				LOG.error(e.getMessage());
				return;
			}
		}
		
		if(tecnicoResponsable.getIdentificador()==null)
		{
			JsfUtil.addMessageError("Error en Cédula o Ruc no válido");
		}		
	}
	
	public void uploadObservaciones(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoObservaciones.setId(null);
		documentoObservaciones.setContenidoDocumento(contenidoDocumento);
		documentoObservaciones.setNombre(event.getFile().getFileName());
		documentoObservaciones.setMime("application/pdf");
		documentoObservaciones.setExtesion(".pdf");
				
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}

	public void aceptar() {
		try{
			tecnicoResponsableFacade.guardar(tecnicoResponsable, loginBean.getUsuario(), 0);
			
			if(documentoObservaciones.getContenidoDocumento()!=null){
				documentoObservaciones.setIdTable(generadorDesechosRetce.getId());
				documentoObservaciones.setNombreTabla(GeneradorDesechosPeligrososRetce.class.getSimpleName());
				documentoObservaciones.setDescripcion("Documento observaciones");
				documentoObservaciones.setEstado(true);
				documentosFacade.guardarDocumentoAlfrescoSinProyecto(
								generadorDesechosRetce.getCodigoGenerador(),
								"GENERADOR_DESECHOS",
								generadorDesechosRetce.getId().longValue(),
								documentoObservaciones,
								TipoDocumentoSistema.DOCUMENTO_OBSERVACIONES_GENERADOR,
								null);
	        }
			
			
			generadorDesechosRetce.setTecnicoResponsable(tecnicoResponsable);
			generadorDesechosPeligrososFacade.guardarRgdRetce(generadorDesechosRetce);
			
			ProcesoRetceController procesoRetceController=JsfUtil.getBean(ProcesoRetceController.class);
			if(procesoRetceController.iniciarProceso(generadorDesechosRetce)){
				generadorDesechosRetce.setRegistroFinalizado(true);
				generadorDesechosRetce.setFechaTramite(new Date());
				generadorDesechosPeligrososFacade.guardarRgdRetce(generadorDesechosRetce);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		} catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
	}
	
	// para observaciones
	public String getClassName() {
		return GeneradorDesechosPeligrososRetce.class.getSimpleName();
	}
	
	public StreamedContent descargar(Documento documento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
			} else if (documento.getContenidoDocumento() != null) {
				documentoContent = documento.getContenidoDocumento();
			}
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void enviarCorrecciones() {
		try {
			
			tecnicoResponsableFacade.guardar(tecnicoResponsable, loginBean.getUsuario(), declaracionGeneradorRetceBean.getNumeroObservacion());
			
			if(documentoObservaciones.getContenidoDocumento()!=null){
				documentoObservaciones.setIdTable(generadorDesechosRetce.getId());
				documentoObservaciones.setNombreTabla(GeneradorDesechosPeligrososRetce.class.getSimpleName());
				documentoObservaciones.setDescripcion("Documento observaciones");
				documentoObservaciones.setEstado(true);
				documentosFacade.guardarDocumentoAlfrescoSinProyecto(
								generadorDesechosRetce.getCodigoGenerador(),
								"GENERADOR_DESECHOS",
								generadorDesechosRetce.getId().longValue(),
								documentoObservaciones,
								TipoDocumentoSistema.DOCUMENTO_OBSERVACIONES_GENERADOR,
								null);
	        }
			
			
			generadorDesechosRetce.setTecnicoResponsable(tecnicoResponsable);
			generadorDesechosPeligrososFacade.guardarRgdRetce(generadorDesechosRetce);
			
			Map<String, Object> parametros = new HashMap<>();
			
			Area areaTramite = areaFacade.getArea(generadorDesechosRetce.getIdArea());
			
			String usuarioCoordinadorBpm = JsfUtil.getCurrentTask().getVariable("usuario_coordinador").toString();
			Usuario usuarioCoordinador = areaFacade.getCoordinadorPorArea(areaTramite); //el coordinador se busca en la OT
			if (usuarioCoordinador == null) {
				LOG.error("No se encontro usuario coordinador en " + areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} else if (!usuarioCoordinadorBpm.equals(usuarioCoordinador.getNombre())) {
				parametros.put("usuario_coordinador", usuarioCoordinador.getNombre());
			}

			if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)){
				areaTramite = areaTramite.getArea();
			}
			String usuarioAutoridadBpm = JsfUtil.getCurrentTask().getVariable("usuario_autoridad").toString();
			Usuario usuarioAutoridad = areaFacade.getDirectorProvincial(areaTramite);
			if (!usuarioAutoridadBpm.equals(usuarioAutoridad.getNombre())) {
				parametros.put("usuario_autoridad", usuarioAutoridad.getNombre());
			}
			
			OficioPronunciamientoRetce oficioRetce = oficioRetceFacade.getOficio(generadorDesechosRetce.getCodigoGenerador(), TipoDocumentoSistema.OFICIO_OBSERVACION_GENERADOR);
			if(oficioRetce != null) {
				oficioRetce.setFechaEnvioCorrecciones(new Date());
				oficioRetceFacade.actualizar(oficioRetce);
			}
			
			try {
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				Organizacion organizacion = organizacionFacade.buscarPorRuc(JsfUtil.getLoggedUser().getNombre());
				String nombreOperador = JsfUtil.getNombreOperador(JsfUtil.getLoggedUser(), organizacion);
				
				Object[] parametrosCorreo = new Object[] {
						nombreOperador, generadorDesechosRetce.getCodigoGenerador() };
				String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
								"bodyNotificacionOperadorRemiteCorreccionesRetce", parametrosCorreo);
				
				Email.sendEmail(usuarioCoordinador, "Ingreso correcciones trámite RETCE", notificacion, informacionProyecto.getCodigo(), loginBean.getUsuario());
	
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
}
