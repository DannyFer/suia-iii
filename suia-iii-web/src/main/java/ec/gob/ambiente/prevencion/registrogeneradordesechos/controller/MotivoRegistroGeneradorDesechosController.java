package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers.BuscarProyectoController;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.services.MaeLicenseResponse;
import ec.gob.ambiente.suia.comun.bean.IdentificarProyectoComunBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.BuscarProyectoFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.RegistroGeneradorDesechosAsociado;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class MotivoRegistroGeneradorDesechosController implements Serializable {
	
	private static final Logger LOG = Logger.getLogger(BuscarProyectoController.class);
	
	private static final long serialVersionUID = 1769026407817490750L;

	public static final String MOTIVO_REGISTRO_GENERADOR_DESECHOS = "motivoRegistro";
	public static final String MOTIVO_REGISTRO_GENERADOR_DESECHOS_ASOCIADO = "asociado";
	public static final String MOTIVO_REGISTRO_GENERADOR_DESECHOS_NO_ASOCIADO = "noAsociado";

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	private String tipoAccion;

	@Getter
	@Setter
	private String tipoMotivo;

	@Getter
	@Setter
	private ProyectoCustom proyectoCustom;
	
	@Getter
	@Setter
	private boolean disableItem1;
	
	@Getter
	@Setter
	private boolean disableItem2;
	
	@Getter
	@Setter
	private boolean disableItem3;
	
	@Getter
	@Setter
	private String noAsociado;
	
	@Getter
	@Setter
	private boolean iniciarDesechos;
	
	@Setter
	@Getter
	private boolean proyectoBuscado;
	
	@Setter
	@Getter
	private Date fechaRegistro;

	@Setter
	@Getter
	private boolean habilitarInicioProceso;

	@Setter
	@Getter
	private boolean habilitaPanelProyectoEncontrado;

	@Getter
	@Setter
	private String mensaje;

	@Getter
	@Setter
	private String pathImagen;

	@Setter
	@Getter
	private Boolean habilitarPanelLicenciaFisica;
	
	@Setter
	@Getter
	private boolean volverBuscarProyecto;
	
	@Setter
	@Getter
	private boolean buscarProyecto;
	
	@Setter
	@Getter
	private boolean habilitarPanelBusqueda;
	
	@EJB
	private BuscarProyectoFacade buscarProyectoFacade;
	
	@Setter
	@Getter
	private String proyectoConLicenciaFisica;
	
	@Setter
	@Getter
	private String numeroPermisoAmbiental;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@Setter
	@Getter
	private Documento documentoLicencia;

	@Setter
	@Getter
	private UploadedFile file;
	
	@Setter
	@Getter
	private MaeLicenseResponse registroAmbiental;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;
	
	@Getter
	@Setter
	private RegistroGeneradorDesechosAsociado registroGeneradorDesechosAsociado = new RegistroGeneradorDesechosAsociado();

	public HttpServletRequest servletRequest;
	
	@Getter
	@Setter
	private Date fechaActual;
	
	@PostConstruct
	private void init() {
		iniciarDesechos=true;
		disableItem1=false;
		disableItem2=false;
		disableItem3=false;
		habilitarPanelLicenciaFisica=false;
		documentoLicencia= new Documento();
		fechaActual= new Date();
	}
	
	public void validarItems() {
		proyectoLicenciamientoAmbiental = null;
		noAsociado = null;
		if (registroGeneradorDesechosAsociado.isPermisoSuia()) {
			buscarProyecto = true;
			disableItem2=true;
			disableItem3=true;
			registroGeneradorDesechosAsociado.setCodigoProyecto("");
			registroGeneradorDesechosAsociado.setCodigoPermisoAmbiental("");
			registroGeneradorDesechosAsociado.setNombreDocumento("");
			
		}else if (registroGeneradorDesechosAsociado.isPermisoEnte()) {
			buscarProyecto=true;
			disableItem1=true;
			disableItem3=true;
			registroGeneradorDesechosAsociado.setCodigoProyecto("");
			registroGeneradorDesechosAsociado.setCodigoPermisoAmbiental("");
			registroGeneradorDesechosAsociado.setNombreDocumento("");
		}else if (registroGeneradorDesechosAsociado.isPermisoFisico()) {
			buscarProyecto = false;
			disableItem1=true;
			disableItem2=true;
			registroGeneradorDesechosAsociado.setCodigoProyecto("");
			registroGeneradorDesechosAsociado.setCodigoPermisoAmbiental("");
			registroGeneradorDesechosAsociado.setNombreDocumento("");
		}
		if(!registroGeneradorDesechosAsociado.isPermisoSuia() && !registroGeneradorDesechosAsociado.isPermisoEnte() && !registroGeneradorDesechosAsociado.isPermisoFisico()){
			disableItem1=false;
			disableItem2=false;
			disableItem3=false;
			habilitarPanelLicenciaFisica=false;
			registroGeneradorDesechosAsociado.setCodigoProyecto("");
			registroGeneradorDesechosAsociado.setCodigoPermisoAmbiental("");
			registroGeneradorDesechosAsociado.setNombreDocumento("");
			registroAmbiental = new MaeLicenseResponse();
			habilitaPanelProyectoEncontrado=false;
			
		}
	}
	
	public String aceptar() {
		if(registroGeneradorDesechosAsociado.isPermisoSuia()||registroGeneradorDesechosAsociado.isPermisoEnte()||registroGeneradorDesechosAsociado.isPermisoFisico())
		tipoMotivo=noAsociado;
		if (getMotivoAsociado().equals(tipoMotivo) && proyectoCustom == null) {
			JsfUtil.addMessageError("Debe seleccionar el proyecto asociado al registro de generador de desechos.");
			return null;
		}

		int result = 0;

		if(proyectoLicenciamientoAmbiental !=null)
			result = validarProyecto();

		if(result == 0){
			servletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			servletRequest.getSession(false).setAttribute("proyecto", proyectoLicenciamientoAmbiental);
			servletRequest.getSession(false).setAttribute("registroAsociado", registroGeneradorDesechosAsociado);
			servletRequest.getSession(false).setAttribute("documentoLicencia", documentoLicencia);

			return JsfUtil.actionNavigateTo("/prevencion/registrogeneradordesechos/realizarPagoInformacion",
					MOTIVO_REGISTRO_GENERADOR_DESECHOS + "=" + tipoMotivo);

		}
		else{
			if(result == -1){
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_PROYECTO_AJENO);
				return null;
			} else if (result == 1){
				RequestContext.getCurrentInstance().execute(
						"PF('conGeneradorWdgt').show();");
			}else if (result == 2){
				RequestContext.getCurrentInstance().execute(
						"PF('sinPermisoWdgt').show();");
			}
		}

		return "";
	}

	@EJB
	ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	/***
	 * Método que realiza las validaciones pertinentes y, como resultado, permite o no
	 * al usuario iniciar un nuevo RGD.
	 *
	 * @Autor Denis Linares
	 */

	public int validarProyecto(){
			boolean tienePermiso = true;
			int resultado = 0;
			String categoriaNombrePublico = proyectoLicenciamientoAmbiental.getCatalogoCategoria().getCategoria().getNombrePublico();
			if(categoriaNombrePublico.compareTo("Registro Ambiental") == 0 || categoriaNombrePublico.compareTo("Licencia Ambiental") == 0){
				try {
					//Validamos que tenga permiso ambiental (Licencia o Registro).
					tienePermiso = this.proyectoLicenciaAmbientalFacade.validarPermisoPorIdProyecto(proyectoLicenciamientoAmbiental.getId(), categoriaNombrePublico, proyectoLicenciamientoAmbiental.getTipoSector().getNombre());

					if (tienePermiso){
						if(proyectoLicenciamientoAmbiental.getGeneraDesechos()!=null && proyectoLicenciamientoAmbiental.getGeneraDesechos()){

							//Validamos que no tenga un generador de desechos en curso asociado.
							boolean existeGeneradorEnCurso = proyectoLicenciaAmbientalFacade.tienenRgdEnCurso(proyectoLicenciamientoAmbiental.getId());
							if(existeGeneradorEnCurso){
								//Mostramos un msj que direcciona al menú  de procesos.
								/*RequestContext.getCurrentInstance().execute(
										"PF('conGeneradorWdgt').show();");*/
								resultado = 1;
							}
						}else
						resultado = 0;
					}
					else{
						//Mostramos un Msj informatativo
						/*RequestContext.getCurrentInstance().execute(
								"PF('sinPermisoWdgt').show();");*/
						resultado = 2;
					}

				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
			else{
				resultado = 0;
			}
			
		if (resultado == 0) {
			Boolean existeProcesoRgdIniciado = proyectoLicenciamientoAmbientalFacade.tieneProcesoRgdIniciado(proyectoLicenciamientoAmbiental.getCodigo());
			if (existeProcesoRgdIniciado)
				resultado = 1;
		}

		return resultado;
	}

	//AQUI
	public void buscarProyecto() {
		proyectoCustom = new ProyectoCustom();
		proyectoBuscado = true;
		habilitarInicioProceso = false;
		habilitaPanelProyectoEncontrado = false;
		habilitarPanelLicenciaFisica = false;
		volverBuscarProyecto = false;
		proyectoConLicenciaFisica = null;
		registroAmbiental = null;
		habilitarPanelBusqueda = true;

		try {
			registroAmbiental = buscarProyectoFacade.buscarProyecto(registroGeneradorDesechosAsociado.getCodigoProyecto(),loginBean.getUsuario().getNombre());
			if(registroAmbiental.getCodigoProyecto()!=null){
			proyectoCustom.setCodigo(registroAmbiental.getCodigoProyecto());
			proyectoLicenciamientoAmbiental = cargarProyecto();
			noAsociado = "asociado";
			}else {
				//proyectoCustom.setCodigo(registroGeneradorDesechosAsociado.getCodigoProyecto());
				proyectoCustom=null;
				JsfUtil.addMessageError(registroAmbiental.getMensaje());
				noAsociado = "asociado";
				return;
			}
		} catch (ServiceException e) {
			JsfUtil.addMessageInfo("No se pudo encontrar el proyecto.");
			LOG.error(e, e);
		}

		if (proyectoLicenciamientoAmbiental != null) {
			registroAmbiental.setCodigoProyecto(proyectoLicenciamientoAmbiental.getCodigo());
			registroAmbiental.setNombreProyecto(proyectoLicenciamientoAmbiental.getNombre());
			registroAmbiental.setFechaProyecto(proyectoLicenciamientoAmbiental.getFechaCreacion().toString());
			registroAmbiental.setSector(proyectoLicenciamientoAmbiental.getCatalogoCategoria().getCatalogoCategoriaPublico().getTipoSector().getNombre());
			registroAmbiental.setSubSectorOactividad(proyectoLicenciamientoAmbiental.getCatalogoCategoria().getDescripcion());
			habilitaPanelProyectoEncontrado = true;
			buscarProyecto = false;
		} else {
			if ((registroAmbiental != null	&& registroAmbiental.getCategoria() != null && (registroAmbiental.getCategoria().equals("Subsector"))) || (registroAmbiental != null && registroAmbiental.isLicencia() == null)) {
				JsfUtil.addMessageError("No se puede continuar con la aprobación del registro generador de desechos para el proyecto seleccionado. Contacte a Mesa de Ayuda.");
			}

			else if (registroAmbiental != null && registroAmbiental.isEstadoProyecto() != null) {
				habilitaPanelProyectoEncontrado = (registroAmbiental != null) && (registroAmbiental.getCodigoProyecto() != null);
				if (registroAmbiental.isLicencia()) {
					// habilitarInicioProceso = true;
					// aqui
					// habilitaPanelProyectoEncontrado=true;
					if (proyectoLicenciamientoAmbiental == null) {
						registroAmbiental = null;
						noAsociado = "noAsociado";
						iniciarDesechos = true;
						buscarProyecto = false;
						habilitarPanelLicenciaFisica = true;
						habilitaPanelProyectoEncontrado = false;
					} else {
						buscarProyecto = false;
						habilitarPanelLicenciaFisica = false;
						habilitaPanelProyectoEncontrado = true;
					}
				} else {
					if (registroGeneradorDesechosAsociado.isPermisoSuia()) {
						if (registroAmbiental.getProyectoSuia() != null	&& registroAmbiental.getProyectoSuia().equals("verde") && registroAmbiental.isLicencia()) {
							if (proyectoLicenciamientoAmbiental == null) {
								registroAmbiental = null;
								noAsociado = "noAsociado";
								iniciarDesechos = true;
								buscarProyecto = false;
								habilitarPanelLicenciaFisica = true;
							}
						} else {
							JsfUtil.addMessageInfo("No se puede continuar con la aprobación del registro generador de desechos para el proyecto seleccionado porque aún no ha obtenido su permiso ambiental.");
							iniciarDesechos = false;
							buscarProyecto = true;
							habilitarPanelLicenciaFisica = false;
							registroAmbiental = null;
							habilitaPanelProyectoEncontrado = false;
						}
					}
					if (registroGeneradorDesechosAsociado.isPermisoEnte()) {
						if (registroAmbiental.getProyectoSuia() != null	&& registroAmbiental.getProyectoSuia().equals("verde")	&& registroAmbiental.isLicencia()) {
							if (proyectoLicenciamientoAmbiental == null) {
								registroAmbiental = null;
								noAsociado = "noAsociado";
								iniciarDesechos = true;
								buscarProyecto = false;
								habilitarPanelLicenciaFisica = true;
							}
						} else {
							JsfUtil.addMessageInfo("Si usted obtuvo el permiso ambiental en el Ente Acreditado por favor ingresar la siguiente información");
							registroAmbiental = null;
							noAsociado = "noAsociado";
							iniciarDesechos = true;
							buscarProyecto = false;
							habilitarPanelLicenciaFisica = true;
							habilitaPanelProyectoEncontrado = false;
						}
					}
				}
				// if (!habilitaPanelProyectoEncontrado) {
				// JsfUtil.addMessageInfo("No se puede continuar con la aprobación del registro generador de desechos para el proyecto seleccionado porque aún no ha obtenido su permiso ambiental.");
				// }
			} else {
				RequestContext context = RequestContext.getCurrentInstance();
				if (registroAmbiental != null
						&& registroAmbiental.getMensaje() != null
						&& registroAmbiental
								.getMensaje()
								.toLowerCase()
								.contains("el proyecto no le pertenece a ese usuario")) {
					context.execute("PF('preguntaLicenciaFisicaOtroUsuarioWdgt').show();");
				} else {
					iniciarDesechos = true;
					buscarProyecto = true;
					habilitarPanelLicenciaFisica = false;
					// context.execute("PF('preguntaLicenciaFisicaWdgt').show();");
				}
			}
		}
		numeroPermisoAmbiental = "";
	}
	
	public void listenerLicenciaFisica() {
		habilitarPanelLicenciaFisica = new Boolean(proyectoConLicenciaFisica);
		if (habilitarPanelLicenciaFisica) {
			habilitarInicioProceso = true;
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		file = event.getFile();
		setDocumentoLicencia(UtilDocumento.generateDocumentPDFFromUpload(file.getContents(), file.getFileName()));
	}

	public void volverPanelBuscar() {
		habilitarPanelBusqueda = true;
		proyectoBuscado = false;
		habilitaPanelProyectoEncontrado = false;
		habilitarPanelLicenciaFisica = null;
		habilitarInicioProceso = false;
		proyectoConLicenciaFisica = null;
		numeroPermisoAmbiental = null;
		fechaRegistro = null;
		volverBuscarProyecto = false;
		buscarProyecto=true;
		registroGeneradorDesechosAsociado.setCodigoProyecto(null);
	}
	
	public void aceptarPreguntaLicenciaFisica() {
		this.numeroPermisoAmbiental = "";
		this.fechaRegistro = null;
		volverBuscarProyecto = (!(new Boolean(proyectoConLicenciaFisica)));
		habilitarInicioProceso = false;
		habilitaPanelProyectoEncontrado = false;
		habilitarPanelBusqueda = false;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('preguntaLicenciaFisicaWdgt').hide();");
	}
	//FIN AQUI
	
	public void seleccionarProyecto() {
		JsfUtil.getBean(IdentificarProyectoComunBean.class).initFunction(
				"/prevencion/registrogeneradordesechos/motivo", new CompleteOperation() {

					@Override
					public Object endOperation(Object object) {
						proyectoCustom = (ProyectoCustom) object;
						return null;
					}
				});
	}

	public void eliminarProyecto() {
		proyectoCustom = null;
	}

	public String registrarAccion(String tipo) {
		this.tipoAccion = tipo;
		this.tipoMotivo = null;
		this.proyectoCustom = null;
		return JsfUtil.actionNavigateTo("/prevencion/registrogeneradordesechos/motivo");
	}

	public ProyectoLicenciamientoAmbiental cargarProyecto() {
		try {
//			return proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(Integer
//					.parseInt(proyectoCustom.getId()));
			proyectoLicenciamientoAmbiental= proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo((proyectoCustom.getCodigo()));
//			HttpServletRequest servletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//			servletRequest.getSession(false).setAttribute("proyecto", proyectoLicenciamientoAmbiental);
		return proyectoLicenciamientoAmbiental;
		} catch (Exception e) {
			return null;
		}
	}

	public String getMotivoAsociado() {
		return MOTIVO_REGISTRO_GENERADOR_DESECHOS_ASOCIADO;
	}

	public String getMotivoNoAsociado() {
		return MOTIVO_REGISTRO_GENERADOR_DESECHOS_NO_ASOCIADO;
	}

	public String getAccionEmision() {
		return "emision";
	}
}
