package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.AdicionarDesechosPeligrososRcoaBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.PuntosRecuperacionRgdBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.ActividadDesechoFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.CoordenadaRgdCoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.FormaPuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoGeneracionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoGeneracionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class InformacionRegistroGeneradorController {
	
	private static final Logger LOG = Logger.getLogger(InformacionRegistroGeneradorController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private PuntoGeneracionRgdRcoaFacade puntoGeneracionRgdRcoaFacade;
	@EJB
	private ActividadDesechoFacade actividadDesechoFacade;
	@EJB
	private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcoaFacade;
	@EJB
	private CoordenadaRgdCoaFacade coordenadaRgdCoaFacade;
	@EJB
	private FormaPuntoRecuperacionRgdRcoaFacade formaPuntoRecuperacionRgdRcoaFacade;
	@EJB
	private PuntoRecuperacionRgdRcoaFacade puntoRecuperacionRgdRcoaFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private AreaFacade areaFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@ManagedProperty(value = "#{wizardBean}")
	@Getter
	@Setter
	private WizardBean wizardBean;
	
	@Getter
	@Setter
	private List<String> listaDesechosPorActividad;
	
	@Getter
	@Setter
	private boolean aceptarCondiciones = false;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	// variables de flujo
	@Getter
	private String tramite;

	private Map<String, Object> variables;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{adicionarDesechosPeligrosRcoaBean}")
	@Getter
	@Setter
	private AdicionarDesechosPeligrososRcoaBean adicionarDesechosPeligrosRcoaBean;

	@Getter
	@Setter
	private boolean existeObservaciones = false;
	
	@Getter
	@Setter
	private Integer idUbicacionGeografica;
	
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionGeografica;
	
	@Setter
	@Getter
	private List<UbicacionesGeografica> listaUbicaciones;
	
	@Getter
	@Setter
	private RegistroGeneradorDesechosRcoa registroGeneradorDesechos;
	
	@Getter
	@Setter
	private boolean desechoModificado = false;

	@Getter
	@Setter
	private String nombreEmpresaSolicitante, representanteLegal, rucEmpresa, actividadPrincipal, segundaActividad, terceraActividad, direccion, telefono, correo, celular, cedulaRepresentanteLegal, tipoOperador="N";
	
	@Getter
	@Setter
	private List<DesechosRegistroGeneradorRcoa> desechosRegistroLista;	
	
    @Getter
    @Setter
    private Integer idDesecho;
    
    @Getter
    @Setter
    private List<PuntoGeneracionRgdRcoa> listaOrigenGeneracion;
    
    @Getter
    @Setter
    private String codigoCiiu;
    
    @Getter
    @Setter
    private boolean guardado;
    
    @Getter
    @Setter
    private boolean enviar;    
    
    byte[] bytePlantilla;
    
    @Getter
    @Setter
    private String style;
	
	@PostConstruct
	public void init(){
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);			
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite); 
			
			JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).inicializar();
			
			List<RegistroGeneradorDesechosProyectosRcoa> lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyecto.getId());
			
			if(lista != null && !lista.isEmpty()){
				registroGeneradorDesechos = lista.get(0).getRegistroGeneradorDesechosRcoa();	
				List<PuntoRecuperacionRgdRcoa> puntosRecuperacion = puntoRecuperacionRgdRcoaFacade.buscarPorRgd(registroGeneradorDesechos.getId());
				
				for(PuntoRecuperacionRgdRcoa punto : puntosRecuperacion){
					String coordenadaString = "";
					if(punto.getFormasPuntoRecuperacionRgdRcoa() != null && !punto.getFormasPuntoRecuperacionRgdRcoa().isEmpty()){
						for(CoordenadaRgdCoa coordenada : punto.getFormasPuntoRecuperacionRgdRcoa().get(0).getCoordenadas()){
							coordenadaString += (coordenadaString == "") ? coordenada.getX().toString() + " " + coordenada.getY().toString() : "," + coordenada.getX().toString() + " " + coordenada.getY().toString();
						}
						punto.setCoordenadasIngresadas(coordenadaString);
					}					
				}				
				
				JsfUtil.getBean(PuntosRecuperacionRgdBean.class).setPuntosRecuperacion(puntosRecuperacion);
			}						
			
			if(proyecto.getUsuario().getNombre().length() == 10){
				nombreEmpresaSolicitante = proyecto.getUsuario().getPersona().getNombre();
				representanteLegal = proyecto.getUsuario().getPersona().getNombre();
				cedulaRepresentanteLegal = proyecto.getUsuario().getNombre();				
				rucEmpresa = proyecto.getUsuario().getNombre();				
				idUbicacionGeografica = proyecto.getUsuario().getPersona().getIdUbicacionGeografica();
				
				for (Contacto contacto : proyecto.getUsuario().getPersona().getContactos()) {
					switch (contacto.getFormasContacto().getId()) {
					case FormasContacto.DIRECCION:
						direccion = contacto.getValor();
						break;
					case FormasContacto.TELEFONO:
						if(contacto.getValor() != null && !contacto.getValor().isEmpty())
							telefono = contacto.getValor();
						break;
					case FormasContacto.CELULAR:
						if(telefono != null && !telefono.isEmpty())
							telefono = contacto.getValor();
						celular = contacto.getValor();
						break;
					case FormasContacto.EMAIL:
						correo = contacto.getValor();
						break;
					default:
						break;
					}
				}
				tipoOperador="N";
			}else{
				Organizacion organizacion = new Organizacion();
				organizacion = organizacionFacade.buscarPorRuc(proyecto.getUsuario().getNombre());
				
				if(organizacion != null && organizacion.getId() != null){
					nombreEmpresaSolicitante = organizacion.getNombre();
					representanteLegal = organizacion.getPersona().getNombre();
					cedulaRepresentanteLegal = organizacion.getPersona().getPin();
					rucEmpresa = organizacion.getRuc();
					idUbicacionGeografica = organizacion.getIdUbicacionGeografica();
					
					List<Contacto> contactos = contactoFacade.buscarPorOrganizacion(organizacion);
					
					for (Contacto contacto : contactos) {
						switch (contacto.getFormasContacto().getId()) {
						case FormasContacto.DIRECCION:
							direccion = contacto.getValor();
							break;
						case FormasContacto.TELEFONO:
							if(contacto.getValor() != null && !contacto.getValor().isEmpty())
								telefono = contacto.getValor();
							break;
						case FormasContacto.CELULAR:
							if(telefono != null && !telefono.isEmpty())
								telefono = contacto.getValor();
							celular = contacto.getValor();
							break;
						case FormasContacto.EMAIL:
							correo = contacto.getValor();
							break;
						default:
							break;
						}
					}
					tipoOperador="N";
				}else{
					nombreEmpresaSolicitante = proyecto.getUsuario().getPersona().getNombre();
					representanteLegal = proyecto.getUsuario().getPersona().getNombre();
					cedulaRepresentanteLegal = proyecto.getUsuario().getNombre();				
					rucEmpresa = proyecto.getUsuario().getNombre();				
					idUbicacionGeografica = proyecto.getUsuario().getPersona().getIdUbicacionGeografica();
					
					for (Contacto contacto : proyecto.getUsuario().getPersona().getContactos()) {
						switch (contacto.getFormasContacto().getId()) {
						case FormasContacto.DIRECCION:
							direccion = contacto.getValor();
							break;
						case FormasContacto.TELEFONO:
							if(contacto.getValor() != null && !contacto.getValor().isEmpty())
								telefono = contacto.getValor();
							break;
						case FormasContacto.CELULAR:
							if(telefono != null && !telefono.isEmpty())
								telefono = contacto.getValor();
							celular = contacto.getValor();
							break;
						case FormasContacto.EMAIL:
							correo = contacto.getValor();
							break;
						default:
							break;
						}
					}
					tipoOperador="J";
				}						
			}		
			
			List<ProyectoLicenciaCuaCiuu> listaProyectoActividadesPrincipal = registroGeneradorDesechosRcoaFacade.buscarActividadesCiuPrincipal(proyecto);
			if(listaProyectoActividadesPrincipal != null && !listaProyectoActividadesPrincipal.isEmpty()){
				actividadPrincipal = listaProyectoActividadesPrincipal.get(0).getCatalogoCIUU().getNombre();
				codigoCiiu = listaProyectoActividadesPrincipal.get(0).getCatalogoCIUU().getCodigo();
			}
			
			List<ProyectoLicenciaCuaCiuu> listaProyectoActividades = registroGeneradorDesechosRcoaFacade.buscarActividadesCiu(proyecto);
			
			
			for(int i = 0; i<= listaProyectoActividades.size() - 1; i++){
				
//				if(i==0){
//					actividadPrincipal = listaProyectoActividades.get(i).getCatalogoCIUU().getNombre();
//					codigoCiiu = listaProyectoActividades.get(i).getCatalogoCIUU().getCodigo();
//				}
//				else 
					if(i == 0)
					segundaActividad = listaProyectoActividades.get(i).getCatalogoCIUU().getNombre();
				else if(i == 1)
					terceraActividad = listaProyectoActividades.get(i).getCatalogoCIUU().getNombre();
			}
			
			ubicacionGeografica = new UbicacionesGeografica();
			ubicacionGeografica = ubicacionGeograficaFacade.buscarPorId(idUbicacionGeografica);		
			
			listaUbicaciones = new ArrayList<UbicacionesGeografica>();
			if(ubicacionGeografica != null){
				listaUbicaciones.add(ubicacionGeografica);
			}			
			
			listaDesechosPorActividad = new ArrayList<String>();	
			
			
			enviar = true;		
			guardado = true;
			
			style = "wizard-custom-btn-back";
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ocultarFormulario() {

		wizardBean.setCurrentStep("paso1");
		Wizard wizard = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:wizardGenerador");
		wizard.setStep("paso1");
		RequestContext.getCurrentInstance().update("form:wizardGenerador");
	}

	public String btnAtras() {

		String currentStep = wizardBean.getCurrentStep();	
		if(currentStep != null && currentStep.equals("paso2")){
			guardado = true;
		}
		style = "wizard-custom-btn-back";
		
		return null;
	}

	public String btnSiguiente() throws CmisAlfrescoException {

		String currentStep = wizardBean.getCurrentStep();
		
		if(currentStep == null || currentStep.equals("paso1")){
			guardarPaso1();
		}
		
		if(currentStep != null && currentStep.equals("paso3")){
			style = "wizard-custom-btn-next";
			RequestContext.getCurrentInstance().update("btnGuardar");
		}
		
		guardado = false;

		return null;
	}
	
	public void guardar(){		
		
		try {
			String currentStep = wizardBean.getCurrentStep();

			if (currentStep == null || currentStep.equals("paso1")) {
				guardarPaso1();
				guardado = true;
			} else if (currentStep.equals("paso2")) {
				guardarPaso2_();
			} else if (currentStep.equals("paso3")) {
				guardarPaso3();
			} 
//			guardado = true;

		} catch (Exception ae) {
			guardado = false;
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		} 	
	}
	
	private void guardarPaso1(){
		
		if(registroGeneradorDesechos == null || registroGeneradorDesechos.getId() == null){
			registroGeneradorDesechos = new RegistroGeneradorDesechosRcoa();
			
			registroGeneradorDesechos.setEstado(true);
			registroGeneradorDesechos.setUsuario(loginBean.getUsuario());	
			registroGeneradorDesechos.setFinalizado(false);
			
			registroGeneradorDesechosRcoaFacade.save(registroGeneradorDesechos, loginBean.getUsuario());		
			
			RegistroGeneradorDesechosProyectosRcoa registroProyecto = new RegistroGeneradorDesechosProyectosRcoa();
			registroProyecto.setRegistroGeneradorDesechosRcoa(registroGeneradorDesechos);
			registroProyecto.setProyectoLicenciaCoa(proyecto);
			registroGeneradorDesechosProyectosRcoaFacade.save(registroProyecto, loginBean.getUsuario());			
		}	
		
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}
	
	public void guardarPaso2_(){
		try {
			List<PuntoRecuperacionRgdRcoa> listaEliminar = JsfUtil.getBean(PuntosRecuperacionRgdBean.class).getPuntosRecuperacionEliminar();
			
			if(listaEliminar != null && !listaEliminar.isEmpty()){
				puntoRecuperacionRgdRcoaFacade.eliminarPuntoRecuperacion(listaEliminar, loginBean.getUsuario());
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		if(JsfUtil.getBean(PuntosRecuperacionRgdBean.class).getPuntosRecuperacion().isEmpty()){
			JsfUtil.addMessageError("Debe ingresar la ubicación de los puntos de generación dentro de la instalación regulada");
			return;
		}		
		
		guardado = true;
		
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}
	
	public void guardarPaso2(PuntoRecuperacionRgdRcoa puntoRecuperacion){		
		System.out.println("entro en paso 2");
		try {
			
			puntoRecuperacion.setRegistroGeneradorDesechosRcoa(registroGeneradorDesechos);
			
			puntoRecuperacionRgdRcoaFacade.savePuntoRecuperacion(puntoRecuperacion, loginBean.getUsuario());				
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void guardarPaso3() throws ServiceException, CmisAlfrescoException{
		System.out.println("ingreso en guardar");

		if (JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados().size() > 0) {
			
			for(DesechosRegistroGeneradorRcoa desechoG : JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados()){
				
				if((desechoG.getGeneraDesecho() == null || !desechoG.getGeneraDesecho()) && desechoG.getCantidadKilos() == null){
					JsfUtil.addMessageError("Generación anual es Requerido");
					guardado = false;
					return;					
				}
				
				if((desechoG.getGeneraDesecho() == null || !desechoG.getGeneraDesecho()) && desechoG.getPuntoGeneracionIdList().isEmpty()){					
					JsfUtil.addMessageError("Origen de la generación es requerido");
					guardado = false;
					return;	
				}
				
				if(desechoG.getGeneraDesecho() != null && desechoG.getGeneraDesecho()){
					if(desechoG.getDocumentoGenera() == null || desechoG.getDocumentoGenera().getContenidoDocumento() == null){
						JsfUtil.addMessageError("Debe adjuntar el documento de justificación");	
						guardado = false;
						return;
					}
				}
			}
			
			Boolean errorGuardadoDesecho = false;
			for(DesechosRegistroGeneradorRcoa desechoG : JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados()){
				try {
					desechoG.setRegistroGeneradorDesechosRcoa(registroGeneradorDesechos);		
					
					if(desechoG.getGeneraDesecho() == null){
						desechoG.setGeneraDesecho(false);
					}
					
					desechosRegistroGeneradorRcoaFacade.save(desechoG, loginBean.getUsuario());
					
					if(desechoG.getDocumentoGenera() != null && desechoG.getDocumentoGenera().getContenidoDocumento() != null && !desechoG.getDocumentoGenera().isSubido()){
						desechoG.getDocumentoGenera().setRegistroGeneradorDesechosRcoa(registroGeneradorDesechos);
						desechoG.getDocumentoGenera().setIdTable(desechoG.getId());
						desechoG.getDocumentoGenera().setIdProceso((int)bandejaTareasBean.getProcessId());
						desechoG.getDocumentoGenera().setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
						desechoG.getDocumentoGenera().setSubido(true);
						documentosRgdRcoaFacade.guardarDocumento(desechoG.getDocumentoGenera(), "REGISTRO GENERADOR DE DESECHOS", TipoDocumentoSistema.RGD_NO_GENERA_DESECHOS);					
					}				
					
				} catch (Exception ae) {
					errorGuardadoDesecho = true;
				}
			}
			
			if(errorGuardadoDesecho) {
				guardado = false;
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return;
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			guardado = true;
		}
		
		if(JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaEliminados() != null){
			for(DesechosRegistroGeneradorRcoa desechoE : JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaEliminados()){
				desechoE.setEstado(false);
				desechosRegistroGeneradorRcoaFacade.saveDesecho(desechoE, loginBean.getUsuario());
			}
		}
	}
	
	public String mostrarNumeroSolicitud(){
		
		String numeroRgd = "";
		
		if(registroGeneradorDesechos != null && registroGeneradorDesechos.getCodigo() != null){
			numeroRgd = registroGeneradorDesechos.getCodigo();
		}
				
		return numeroRgd;
	}	
	
	
	public void enviar(){
		try {
			String nombreTarea= "Ingresar datos del registro";
			//valido que sea la tarea de completar informacion para pasar de tarea
			if(!nombreTarea.equals(bandejaTareasBean.getTarea().getTaskName())){
				JsfUtil.addMessageError("Error al procesar la tarea.");
				return;
			}
			guardarPaso3();
			
			boolean certificado = false;
			boolean licenciaEjecucion = false;
			registroGeneradorDesechos.setFechaEnvioInformacion(new Date());
			if(proyecto.getCategorizacion() == 1){
				certificado = true;
				registroGeneradorDesechos.setFinalizado(true);
			}
			
			if(proyecto.getCategorizacion() == 3 || proyecto.getCategorizacion() == 4){
				if(proyecto.getTipoProyecto() == 1){
					certificado = true;
					registroGeneradorDesechos.setFinalizado(true);
				}else if(proyecto.getTipoProyecto() == 2){
					licenciaEjecucion = true;
					
					registroGeneradorDesechosRcoaFacade.save(registroGeneradorDesechos, loginBean.getUsuario());
					
//					List<RegistroGeneradorDesechosProyectosRcoa> registroList = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyecto.getId());
//					JsfUtil.getBean(ReporteRegistroGeneradorDesechosController.class).generarRegistro(registroList.get(0).getRegistroGeneradorDesechosRcoa(), true);					
				}
			}
			
			registroGeneradorDesechosRcoaFacade.save(registroGeneradorDesechos, loginBean.getUsuario());
			
			String rolDirector = "";
			Usuario usuarioAutoridad;
			String autoridad = "";
			
			if (proyecto.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				
				UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyecto.getIdCantonOficina());
				try {
					rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");				
					
					List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(rolDirector, ubicacion.getAreaCoordinacionZonal().getArea());
					
					if(listaUsuarios != null && !listaUsuarios.isEmpty()){
						usuarioAutoridad = listaUsuarios.get(0);
						autoridad = usuarioAutoridad.getNombre();
					}else{
						JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
						System.out.println("No existe usuario " + rolDirector + " para el area " + ubicacion.getAreaCoordinacionZonal().getArea().getAreaName());						
						return;
					}					
				} catch (Exception e) {
					JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
					System.out.println("No existe usuario " + rolDirector + "para el area " + ubicacion.getAreaCoordinacionZonal().getArea().getAreaName());					
					return;
				}			
			} else{
				Area areaTramite = (proyecto.getAreaInventarioForestal().getArea() != null) ? proyecto.getAreaInventarioForestal().getArea(): proyecto.getAreaInventarioForestal();
				try {
					rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");
					List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(rolDirector, areaTramite);
					
					if(listaUsuarios != null && !listaUsuarios.isEmpty()){
						usuarioAutoridad = listaUsuarios.get(0);
						autoridad = usuarioAutoridad.getNombre();
					}else{
						JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
						System.out.println("No existe usuario " + rolDirector + " para el area " + areaTramite);						
						return;
					}					
				} catch (Exception e) {
					JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
					System.out.println("No existe usuario " + rolDirector + "para el area " + areaTramite);					
					return;
				}	
			}
									
			
			Map<String, Object> params=new HashMap<>();
			params.put("certificadoNELicenciaN", certificado); 
			params.put("licenciaProyectoEjecucion", licenciaEjecucion);			
			params.put("actualizacionRGDREP", false);
			params.put("actualizacionTitularVariosProyectos", false);
			params.put("director", autoridad);
					
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
									
	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public StreamedContent getPlantilla() throws CmisAlfrescoException {
		
		bytePlantilla = documentosRgdRcoaFacade.descargarDocumentoPorNombre("Plantilla_justificacion_de_no_generacion.pdf");
		
		try {
			if (bytePlantilla != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(bytePlantilla),
						"application/pdf",
						"Plantilla_justificacion_de_no_generacion.pdf");		
				
				deleteFileTmp(streamedContent.getName());
				
				return streamedContent;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar la pantilla de justificación de no generación.");
			e.printStackTrace();
		}
		
		JsfUtil.addMessageError("No se pudo descargar la pantilla de justificación de no generación.");
		return null;
		
//		File directory = new File(System.getProperty("java.io.tmpdir") + "/Plantilla_justificacion_de_no_generacion.pdf");
//        File files = directory;
//        files.delete();
	}
	
	public static void deleteFileTmp(String directoryTmp) {
        try {
            File directory = new File(System.getProperty("java.io.tmpdir") + "/"+directoryTmp);
            File files = directory;
            files.delete();
        } catch (Exception e) {
            // info(e.getMessage());
        }
    }

	public void iniciarProceso(){
		iniciar(tramite);
	}
	
	private boolean iniciar(String tramite) {
		try {
			tramite = "MAE-RA-2020-415573";
			ProyectoLicenciaCoa proyectoInicial = proyectoLicenciaCoaFacade
					.buscarProyecto(tramite);

			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("operador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("tramite", tramite);
			parametros.put("idProyecto", proyectoInicial.getId());
			parametros.put("emisionVariosProyectos", false);
//			parametros.put("actualizacionRGDREP", true);

			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(),
					Constantes.RCOA_REGISTRO_GENERADOR_DESECHOS, tramite,
					parametros);

			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage() + " " + e.getCause().getMessage());
			return false;
		}
	}
	

}
