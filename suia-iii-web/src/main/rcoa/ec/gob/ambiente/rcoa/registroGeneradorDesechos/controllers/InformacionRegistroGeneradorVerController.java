package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import org.primefaces.component.wizard.Wizard;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
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
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoGeneracionDesecho;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoGeneracionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class InformacionRegistroGeneradorVerController {
	
	private static final Logger LOG = Logger.getLogger(InformacionRegistroGeneradorVerController.class);
	
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
	private PuntoGeneracionDesechoFacade puntoGeneracionDesechoFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	
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

	/***************************************************************************************************/
	

	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoSuia;
	/****************************************************************************************************/
	
	// variables de flujo
	@Getter
	private String tramite;

	private Map<String, Object> variables;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;
	
//	@ManagedProperty(value = "#{adicionarDesechosPeligrosRcoaBean}")
//	@Getter
//	@Setter
//	private AdicionarDesechosPeligrososRcoaBean adicionarDesechosPeligrosRcoaBean;

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
    private String codigoCiiu, urlPuntorecuperacion;
    
    @Getter
    @Setter
    private boolean guardado;
    
    @Getter
    @Setter
    private boolean enviar;    
    
    byte[] bytePlantilla;
    
    @Setter
    @Getter
    private List<PuntoRecuperacionRgdRcoa> puntosRecuperacion;
    
    @Getter
	@Setter
	private List<DesechosRegistroGeneradorRcoa> desechosRcoaSeleccionados;
    
    private List<DesechosRegistroGeneradorRcoa> listaDesechosSeleccionados;
    private Usuario usuarioProponente;
	
	@PostConstruct
	public void init(){
		try {
			Long idProceso =(Long)(JsfUtil.devolverObjetoSession("idProceso"));
			if(idProceso != null && idProceso > 0){
				bandejaTareasBean.setProcessId(idProceso);
			}
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			String tipoPermisoRGD =(String)variables.get(Constantes.VARIABLE_TIPO_RGD);
			proyectoId = Integer.valueOf(((String)variables.get("idProyectoDigitalizacion") == null)?"0":(String)variables.get("idProyectoDigitalizacion"));
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_AAA)){
				urlPuntorecuperacion = "/pages/rcoa/generadorDesechos/informacionRegistroAAA/puntosRecuperacionAAAVer.xhtml";
			}else if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
				urlPuntorecuperacion = "/pages/rcoa/generadorDesechos/informacionRegistro/puntosRecuperacionVer.xhtml";
			}else{
				urlPuntorecuperacion = "/pages/rcoa/generadorDesechos/informacionRegistro/puntosRecuperacionVer.xhtml";
			}
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
			List<RegistroGeneradorDesechosProyectosRcoa> lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyecto.getId());
			// si es de digitalizacion ya no busco el proyecto aosciado
			if(proyectoId > 0){
				lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoDigitalizado(proyectoId);
			}
			if((lista == null || lista.isEmpty()) && proyectoSuia != null && proyectoSuia.getId() != null){
				lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoSuia(proyectoSuia.getId());
			}
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
					punto.setListaUbicacion(new ArrayList<UbicacionesGeografica>());
					if(punto.getUbicacionesGeografica() != null){
						punto.getListaUbicacion().add(punto.getUbicacionesGeografica());
					}
					punto.setNombresGeneracion(obtenerGeneracion(punto));
				}				
				
				setPuntosRecuperacion(puntosRecuperacion);
			}						
			if(usuarioProponente == null)
				usuarioProponente = registroGeneradorDesechos.getUsuario();
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
				
				if(organizacion == null || organizacion.getId() == null){
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
				if(i == 0)
					segundaActividad = listaProyectoActividades.get(i).getCatalogoCIUU().getNombre();
				else if(i == 1)
					terceraActividad = listaProyectoActividades.get(i).getCatalogoCIUU().getNombre();
			}
			// busco la actividad si es un proyecto digitalizado
			if(proyectoId > 0){
				AutorizacionAdministrativaAmbiental autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(proyectoId);
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
			
			if(registroGeneradorDesechos != null && registroGeneradorDesechos.getId() != null){
				desechosRcoaSeleccionados = new ArrayList<DesechosRegistroGeneradorRcoa>();
				listaDesechosSeleccionados = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGenerador(registroGeneradorDesechos);				
				
				for(DesechosRegistroGeneradorRcoa desecho : listaDesechosSeleccionados){
					
					List<PuntoGeneracionDesecho> listaGeneracion = puntoGeneracionDesechoFacade.buscarPorDesechoRcoa(desecho.getId());
					String origen = "";
					for(PuntoGeneracionDesecho punto : listaGeneracion){
						if(punto.getPuntoGeneracionRgdRcoa().getClave().equals("OT")){
							origen += "Otro: " + desecho.getOtroGeneracion() +"<br />";
							desecho.setOtroGeneracionVer(true);
						}else{
							origen += punto.getPuntoGeneracionRgdRcoa().getClave() + " - " + punto.getPuntoGeneracionRgdRcoa().getNombre() + "<br />";
						}						
					}
					desecho.setNombresGeneracion(origen);
					
					desechosRcoaSeleccionados.add(desecho);
				}				
			}						
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
		
		return null;
	}

	public String btnSiguiente() throws CmisAlfrescoException {

		String currentStep = wizardBean.getCurrentStep();	
		

		return null;
	}	
	
	public StreamedContent getDocumentoDownload(DocumentosRgdRcoa documento){		
		try {
			if (documento != null && documento.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),
						documento.getMime(), documento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;
	}
	
	public String valorDouble(Double numero){	
		String valor = "";
		valor = new BigDecimal(numero).toPlainString();
		return valor;
	}
	
	public String valorDoubleToneladas(BigDecimal numero){
		 String valor = "";
		 DecimalFormat df = new DecimalFormat("#0.0000");
		 valor = df.format(numero);
		
		return valor;
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
}
