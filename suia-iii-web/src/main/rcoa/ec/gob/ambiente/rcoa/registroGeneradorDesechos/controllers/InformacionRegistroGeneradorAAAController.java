package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.DecimalFormat;
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
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.UbicacionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.AdicionarDesechosPeligrososRcoaBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.PuntosRecuperacionRgdBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.ActividadDesechoFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.CoordenadaRgdCoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.FormaPuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoGeneracionDesechoFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoGeneracionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
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
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.ProyectosConPagoSinNutFacade;
import ec.gob.ambiente.suia.recaudaciones.model.ProyectosConPagoSinNut;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class InformacionRegistroGeneradorAAAController {
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
	@EJB
	private PuntoGeneracionDesechoFacade puntoGeneracionDesechoFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private UbicacionDigitalizacionFacade ubicacionDigitalizacionFacade;
	@EJB
	private ProyectosConPagoSinNutFacade proyectosConPagoSinNutFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoSuia;

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
	@ManagedProperty(value = "#{datosOperadorRgdBean}")
	private DatosOperadorRgdBean datosOperadorRgdBean;
	
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
	private AdicionarDesechosPeligrososRcoaBean adicionarDesechosPeligrosBean;

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
	private Integer idDesecho, proyectoId;
	
	@Getter
	@Setter
	private List<PuntoGeneracionRgdRcoa> listaOrigenGeneracion;
	
	@Getter
	@Setter
	private String codigoCiiu, tipoPermisoRGD;
	
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
	
	private Usuario usuarioProponente;
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativa;
	
	@PostConstruct
	public void init(){
		try {
			autorizacionAdministrativa = new AutorizacionAdministrativaAmbiental();
			Integer procesoId =(Integer)(JsfUtil.devolverObjetoSession("processInstanceId"));
			if(procesoId != null)
				bandejaTareasBean.setProcessId(procesoId);
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			tipoPermisoRGD =(String)variables.get(Constantes.VARIABLE_TIPO_RGD);
			proyectoId = Integer.valueOf(((String)variables.get("idProyectoDigitalizacion") == null)?"0":(String)variables.get("idProyectoDigitalizacion"));
			registroGeneradorDesechos = registroGeneradorFacade.buscarRGDPorProyectoDigitalizado(proyectoId);
			// si es de digitalizacion ya no busco el proyecto aosciado
			if(registroGeneradorDesechos == null || registroGeneradorDesechos.getId() == null){
				proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
				if(proyecto.getId() != null){
					usuarioProponente = proyecto.getUsuario();
				}else{
					proyectoSuia = new ProyectoLicenciamientoAmbiental();
					proyectoSuia = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(tramite);
					if(proyectoSuia != null && proyectoSuia.getId() != null){
						usuarioProponente = proyectoSuia.getUsuario();
					}
				}
			}else{
				//proyecto = new ProyectoLicenciaCoa();
				usuarioProponente = registroGeneradorDesechos.getUsuario();
			}
			JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).inicializar();
			//cargo los puntos de ubicacion
			cargarPuntosRecuepracion();
			
			if (registroGeneradorDesechos != null && registroGeneradorDesechos.getUsuario() != null){
				datosOperadorRgdBean.buscarDatosOperador(registroGeneradorDesechos.getUsuario());
			}else{
				if(usuarioProponente != null)
					datosOperadorRgdBean.buscarDatosOperador(usuarioProponente);
			}
			JsfUtil.getBean(PuntosRecuperacionRgdBean.class).setListaOrigenGeneracion(puntoGeneracionRgdRcoaFacade.findAll());
			
			if(usuarioProponente.getNombre().length() == 10){
				nombreEmpresaSolicitante = usuarioProponente.getPersona().getNombre();
				representanteLegal = usuarioProponente.getPersona().getNombre();
				cedulaRepresentanteLegal = usuarioProponente.getNombre();
				rucEmpresa = usuarioProponente.getNombre();
				idUbicacionGeografica = usuarioProponente.getPersona().getIdUbicacionGeografica();
				
				for (Contacto contacto : usuarioProponente.getPersona().getContactos()) {
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
				organizacion = organizacionFacade.buscarPorRuc(usuarioProponente.getNombre());
				
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
					nombreEmpresaSolicitante = usuarioProponente.getPersona().getNombre();
					representanteLegal = usuarioProponente.getPersona().getNombre();
					cedulaRepresentanteLegal = usuarioProponente.getNombre();
					rucEmpresa = usuarioProponente.getNombre();
					idUbicacionGeografica = usuarioProponente.getPersona().getIdUbicacionGeografica();
					
					for (Contacto contacto : usuarioProponente.getPersona().getContactos()) {
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

			List<ProyectoLicenciaCuaCiuu> listaProyectoActividadesPrincipal = new ArrayList<ProyectoLicenciaCuaCiuu>();
			List<ProyectoLicenciaCuaCiuu> listaProyectoActividades = new ArrayList<ProyectoLicenciaCuaCiuu>();
			if(proyecto != null && proyecto.getId() != null){
				listaProyectoActividadesPrincipal = registroGeneradorDesechosRcoaFacade.buscarActividadesCiuPrincipal(proyecto);
				if(listaProyectoActividadesPrincipal != null && !listaProyectoActividadesPrincipal.isEmpty()){
					actividadPrincipal = listaProyectoActividadesPrincipal.get(0).getCatalogoCIUU().getNombre();
					codigoCiiu = listaProyectoActividadesPrincipal.get(0).getCatalogoCIUU().getCodigo();
				}
				listaProyectoActividades = registroGeneradorDesechosRcoaFacade.buscarActividadesCiu(proyecto);
			}
			
			
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
			// busco la actividad si es un proyecto digitalizado
			if(proyectoId > 0){
				autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(proyectoId);
				if(autorizacionAdministrativa != null && autorizacionAdministrativa.getId() != null){
					actividadPrincipal = autorizacionAdministrativa.getCatalogoCIUU().getNombre();
					codigoCiiu = autorizacionAdministrativa.getCatalogoCIUU().getCodigo();
				}
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

	public void cargarPuntosRecuepracion(){
		List<RegistroGeneradorDesechosProyectosRcoa> lista = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
		if(registroGeneradorDesechos != null && registroGeneradorDesechos.getId() != null){
			lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorRegistroGenerador(registroGeneradorDesechos.getId());
		}else{
			if(proyecto != null && proyecto.getId() != null )
				lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyecto.getId());
			if((lista == null || lista.isEmpty()) && proyectoSuia != null && proyectoSuia.getId() != null){
				lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoSuia(proyectoSuia.getId());
			}
		}
		
		if(lista != null && !lista.isEmpty()){
			registroGeneradorDesechos = lista.get(0).getRegistroGeneradorDesechosRcoa();
			List<PuntoRecuperacionRgdRcoa> puntosRecuperacion = puntoRecuperacionRgdRcoaFacade.buscarPorRgd(registroGeneradorDesechos.getId());
			
			for(PuntoRecuperacionRgdRcoa punto : puntosRecuperacion){
				String coordenadaString = "";
				if(punto.getFormasPuntoRecuperacionRgdRcoa() != null && !punto.getFormasPuntoRecuperacionRgdRcoa().isEmpty()){
					for(CoordenadaRgdCoa coordenada : punto.getFormasPuntoRecuperacionRgdRcoa().get(0).getCoordenadas()){
						DecimalFormat formato = new DecimalFormat("#.00000");
						String coorX = formato.format(coordenada.getX()).replace(",", ".");
						String coorY = formato.format(coordenada.getY()).replace(",", ".");
						
						coordenadaString += (coordenadaString == "") ? coorX.toString() + " " + coorY.toString() : "," + coorX.toString() + " " + coorY.toString();
					}
					punto.setCoordenadasIngresadas(coordenadaString);
				}
				punto.setListaUbicacion(new ArrayList<UbicacionesGeografica>());
				if(punto.getUbicacionesGeografica() != null){
					punto.getListaUbicacion().add(punto.getUbicacionesGeografica());
				}
				punto.setNombresGeneracion(obtenerGeneracion(punto));
			}

			JsfUtil.getBean(PuntosRecuperacionRgdBean.class).setPuntosRecuperacion(puntosRecuperacion);
		}
	}
	
	
    public String obtenerGeneracion(PuntoRecuperacionRgdRcoa puntoRecuperacion){
    	String nombre="";
    	if(puntoRecuperacion != null && puntoRecuperacion.getPuntoGeneracion() != null){
    		nombre =" "+puntoRecuperacion.getPuntoGeneracion().getClave() + " - "+ puntoRecuperacion.getPuntoGeneracion().getNombre();
    		if(puntoRecuperacion.getPuntoGeneracion().getClave().equals("OT"))
    			nombre += " - "+ puntoRecuperacion.getGeneracionOtros();
    	}
    	return nombre;
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
			ae.printStackTrace();
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
			if(proyecto != null && proyecto.getId() != null)
				registroProyecto.setProyectoLicenciaCoa(proyecto);
			if(proyectoSuia != null && proyectoSuia.getId() != null)
				registroProyecto.setProyectoId(proyectoSuia.getId());
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
		// valido que no haya datos pendientes de actualizar
		if(JsfUtil.getBean(PuntosRecuperacionRgdBean.class).isPanelAdicionarVisible()){
			JsfUtil.addMessageError("Debe "+(JsfUtil.getBean(PuntosRecuperacionRgdBean.class).isEditar()?"Actualizar":"Aceptar")+" la información antes de continuar.");
			guardado = false;
			return;
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
			// inicializo a no utilizado para luego verificar
			List<PuntoRecuperacionRgdRcoa> listaPuntosRecuperacion = JsfUtil.getBean(PuntosRecuperacionRgdBean.class).getPuntosRecuperacion();
			for(PuntoRecuperacionRgdRcoa punto : listaPuntosRecuperacion){
						punto.setUtilizado(false);
			}
			Integer identificador=1;
			for(DesechosRegistroGeneradorRcoa desechoG : JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados()){
				desechoG.setIdentificador(identificador);
				identificador++;
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
				
				if(desechoG.getGestionInterna() != null && desechoG.getGestionInterna()){
					if(desechoG.getDocumentosGestion() == null || desechoG.getDocumentosGestion().size() == 0){
						JsfUtil.addMessageError("Debe adjuntar el documento de gestión Propia");
						guardado = false;
						return;
					}
				}
				
				// verifico si el area ya fue utilizada
				if(desechoG.getPuntoGeneracionIdList() != null && desechoG.getPuntoGeneracionIdList().size() > 0){
					for (String puntogeneracion : desechoG.getPuntoGeneracionIdList()) {
						for(PuntoRecuperacionRgdRcoa punto : listaPuntosRecuperacion){
							if(punto.getPuntoGeneracion() != null){
								if(punto.getPuntoGeneracion().getId().toString().equals(puntogeneracion)){
									punto.setUtilizado(true);
									//break;
								}
							}
						}
					}
				}
			}

			// valido que todos los origenes de generacopn seleccionados en puntos de recuperacion esten usando almenos en algun desecho
        	String nombreAreaGeneracion = "";
        	List<Integer> listaGeneracionSinusar= new ArrayList<Integer>();
			for(PuntoRecuperacionRgdRcoa punto : listaPuntosRecuperacion){
				if(!punto.isUtilizado() && punto.getPuntoGeneracion() != null && !listaGeneracionSinusar.contains(punto.getPuntoGeneracion().getId())){
					nombreAreaGeneracion += (nombreAreaGeneracion.isEmpty() ? "" : ", ") + punto.getPuntoGeneracion().getClave() + " - "+ punto.getPuntoGeneracion().getNombre();
					listaGeneracionSinusar.add(punto.getPuntoGeneracion().getId());
				}
			}
	        if (listaGeneracionSinusar.size() > 0) {
	        	if(listaGeneracionSinusar.size() == 1)
	        		JsfUtil.addMessageError("Estimado usuario el área de generación "+nombreAreaGeneracion+" debe ser vinculado a algún desecho o residuo de esta sección por lo que no podrá continuar hasta completar esta vinculación.");
	        	else
	        		JsfUtil.addMessageError("Estimado usuario las áreas de generación "+nombreAreaGeneracion+" debe ser vinculadas a algún desecho o residuo de esta sección por lo que no podrá continuar hasta completar esta vinculación.");
				guardado = false;
				return;
	        }

	        	boolean duplicado = false;
			//valido si existen desechos duplicados para que la descripcion sea diferente
			List<DesechosRegistroGeneradorRcoa> listaDesechosAux = JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados();
			for(DesechosRegistroGeneradorRcoa desechoG : JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados()){
				for(DesechosRegistroGeneradorRcoa desechoAux : listaDesechosAux){
					if(desechoAux.getIdentificador() > desechoG.getIdentificador()
							&& desechoG.getDesechoPeligroso().getId().equals(desechoAux.getDesechoPeligroso().getId())){
						if(desechoG.getCantidadKilos() != null && 
								desechoG.getCantidadKilos().equals(desechoAux.getCantidadKilos()) && desechoG.getDescripcionDesecho().equals(desechoAux.getDescripcionDesecho())){
			        		JsfUtil.addMessageError("El desecho "+desechoG.getDesechoPeligroso().getDescripcion() +" se encuentra repetido, la descripción y las cantidades deben ser diferentes.");
			        		duplicado = true;
			        		guardado = false;
						}
					}
				}
			}
			if(duplicado)
				return;
	        
			Boolean errorGuardadoDesecho = false;
			for(DesechosRegistroGeneradorRcoa desechoG : JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados()){
				try {
					desechoG.setRegistroGeneradorDesechosRcoa(registroGeneradorDesechos);
					
					if(desechoG.getGeneraDesecho() == null){
						desechoG.setGeneraDesecho(false);
					}
					
					desechosRegistroGeneradorRcoaFacade.save(desechoG, loginBean.getUsuario());
					
					if(desechoG.getDocumentoGenera() != null && desechoG.getDocumentoGenera().getContenidoDocumento() != null && desechoG.getDocumentoGenera().getId() == null){
						desechoG.getDocumentoGenera().setRegistroGeneradorDesechosRcoa(registroGeneradorDesechos);
						desechoG.getDocumentoGenera().setIdTable(desechoG.getId());
						desechoG.getDocumentoGenera().setIdProceso((int)bandejaTareasBean.getProcessId());
						desechoG.getDocumentoGenera().setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
						
						DocumentosRgdRcoa objDocumento = documentosRgdRcoaFacade.guardarDocumento(desechoG.getDocumentoGenera(), "REGISTRO GENERADOR DE DESECHOS", TipoDocumentoSistema.RGD_NO_GENERA_DESECHOS);
						desechoG.getDocumentoGenera().setId(objDocumento.getId());
					}
					if(desechoG.getDocumentosGestion() != null && desechoG.getDocumentosGestion().size() > 0 ){
						for (DocumentosRgdRcoa objDocumentosRgdRcoa : desechoG.getDocumentosGestion()) {
							if(objDocumentosRgdRcoa != null && objDocumentosRgdRcoa.getContenidoDocumento() != null && objDocumentosRgdRcoa.getId() == null){
								objDocumentosRgdRcoa.setRegistroGeneradorDesechosRcoa(registroGeneradorDesechos);
								objDocumentosRgdRcoa.setIdTable(desechoG.getId());
								objDocumentosRgdRcoa.setIdProceso((int)bandejaTareasBean.getProcessId());
								objDocumentosRgdRcoa.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
								
								DocumentosRgdRcoa objDocumento = documentosRgdRcoaFacade.guardarDocumento(objDocumentosRgdRcoa, "REGISTRO GENERADOR DE DESECHOS", TipoDocumentoSistema.RGD_GESTION_INTERNA);
								objDocumentosRgdRcoa.setId(objDocumento.getId());
							}
						}
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
			if(proyecto != null && proyecto.getId() != null){
				if(proyecto.getCategorizacion() == 1){
					registroGeneradorDesechos.setFinalizado(true);
				}
				
				if(proyecto.getCategorizacion() == 3 || proyecto.getCategorizacion() == 4){
					if(proyecto.getTipoProyecto() == 1){
						registroGeneradorDesechos.setFinalizado(true);
					}else if(proyecto.getTipoProyecto() == 2){
						registroGeneradorDesechosRcoaFacade.save(registroGeneradorDesechos, loginBean.getUsuario());
					}
				}
			}
			registroGeneradorDesechosRcoaFacade.save(registroGeneradorDesechos, loginBean.getUsuario());
			
			String rolDirector = "";
			Usuario usuarioAutoridad;
			String autoridad = "";
			Area areaTramite = new Area();
			// si es proyectos de rcoa
			if(proyecto != null && proyecto.getId() != null){
				if (proyecto.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
					UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyecto.getIdCantonOficina());
					areaTramite = ubicacion.getAreaCoordinacionZonal().getArea();
				} else{
					areaTramite = (proyecto.getAreaInventarioForestal().getArea() != null) ? proyecto.getAreaInventarioForestal().getArea(): proyecto.getAreaInventarioForestal();
				}
			}else if(proyectoSuia != null && proyectoSuia.getId() != null){
				// si es proyectos suia busco la direccion zonal en base a la ubicacion geografica
				if(proyectoSuia.getProyectoUbicacionesGeograficas() != null && proyectoSuia.getProyectoUbicacionesGeograficas().size() > 0){
					UbicacionesGeografica ubicacion = proyectoSuia.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
					if(proyectoSuia.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
						areaTramite = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
					}else if(proyectoSuia.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)){
						areaTramite = proyectoSuia.getAreaResponsable();
					}else{
						areaTramite = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
					}
				}
			}
			// si es proyecto digitalizado busco el area del proyecto digitalizado
			if(autorizacionAdministrativa != null && autorizacionAdministrativa.getId() != null){
				// listo las ubicaciones del proyecto original
				List<UbicacionDigitalizacion> ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(autorizacionAdministrativa.getId(), 1, "WGS84", "17S");
				if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
					ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(autorizacionAdministrativa.getId(), 3, "WGS84", "17S");
				}
				// si no existen // listo las ubicaciones del proyecto ingresadas en digitalizacion
				if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
					ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(autorizacionAdministrativa.getId(), 2);
				}
				// si  existen busco el area
				if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
					UbicacionesGeografica ubicacion = ListaUbicacionTipo.get(0).getUbicacionesGeografica();
					// si la ubicacion es de galapagos busco en el area PARQUE NACIONAL GALAPAGOS
					if(ubicacion.getCodificacionInec().startsWith(Constantes.CODIGO_INEC_GALAPAGOS)){
						areaTramite = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
					}else{
						if(ubicacion.getCodificacionInec().length() == 2){
							areaTramite = ubicacion.getAreaCoordinacionZonal();
						}else if(ubicacion.getCodificacionInec().length() == 4){
							areaTramite = ubicacion.getAreaCoordinacionZonal().getArea();
						}else{
							areaTramite = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
						}
					}
				}
			}
			if(areaTramite != null  && areaTramite.getId() != null){
				try {
					rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");
					List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(rolDirector, areaTramite);
					if(listaUsuarios != null && !listaUsuarios.isEmpty()){
						usuarioAutoridad = listaUsuarios.get(0);
						autoridad = usuarioAutoridad.getNombre();
					}else{
						JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
						System.out.println("No existe usuario " + rolDirector + " para el area " + areaTramite.getAreaName());
						return;
					}
				} catch (Exception e) {
					JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
					System.out.println("No existe usuario " + rolDirector + "para el area " + areaTramite.getAreaName());
					return;
				}
			}else{
				JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
				System.out.println("No existe el area " + areaTramite);
				return;
			}

			Map<String, Object> params=new HashMap<>();

			params.put("certificadoNELicenciaN", certificado);
			params.put("licenciaProyectoEjecucion", licenciaEjecucion);
			params.put("actualizacionRGDREP", false);
			params.put("actualizacionTitularVariosProyectos", false);
			params.put("responsabilidadExtendida", false);
			params.put("director", autoridad);

			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			//obtengo la tarea de pago generada
			TaskSummary tareaActual = procesoFacade.getCurrenTask(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			if(tareaActual != null && tareaActual.getName().equals("Realizar Pago")){
				TaskSummaryCustom tarea = new TaskSummaryCustom();
				tarea.setTaskSummary(tareaActual);
				tarea.setTaskName(tareaActual.getName());
				tarea.setProcessName("Registro de Generador de Residuos y Desechos Peligrosos y/o Especiales");
				tarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
				tarea.setTaskId(tareaActual.getId());
				tarea.setProcedure(tramite);
				tarea.setActivationDate(tareaActual.getActivationTime().toString());
				bandejaTareasBean.setTarea(tarea);
			}
			String urlPago = "/pages/rcoa/generadorDesechos/realizarPagosrgd.jsf";
			//verifico si tiene un pago liberado para RGD
			ProyectosConPagoSinNut objProyectoNoNut = proyectosConPagoSinNutFacade.buscarPorProyectoPorUsuarioPagoLiberado(tramite, JsfUtil.getLoggedUser(), 0L);
			if(objProyectoNoNut != null && objProyectoNoNut.getId() != null)
				urlPago = "/pages/rcoa/generadorDesechos/realizarPagosrgdV1.jsf";
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        	JsfUtil.redirectTo(urlPago);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	
	
	public StreamedContent getPlantilla() {
		
		try {
			bytePlantilla = documentosRgdRcoaFacade.descargarDocumentoPorNombre("Plantilla_justificacion_de_no_generacion.pdf");
		} catch (CmisAlfrescoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
	}
}
