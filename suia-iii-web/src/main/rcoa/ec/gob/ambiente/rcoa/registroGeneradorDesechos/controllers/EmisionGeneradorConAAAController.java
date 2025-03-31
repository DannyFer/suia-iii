package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosAdminBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.ExpedienteBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.ExpedienteBPC;
import ec.gob.ambiente.rcoa.digitalizacion.AutorizacionAdministrativaAmbientalBean;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.OficioResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.ResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.OficioResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ResolucionAmbiental;
import ec.gob.ambiente.rcoa.facade.ProyectoCertificadoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.ProyectosLicenciamientoCoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.EntityFichaCompletaRgd;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.eia.ficha.bean.EstudioImpactoAmbientalBean;
import ec.gob.ambiente.suia.integracion.bean.ContenidoExterno;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;


@ManagedBean
@ViewScoped
public class EmisionGeneradorConAAAController {
	
	private static final Logger LOG = Logger.getLogger(EmisionGeneradorConAAAController.class);
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalFacade;
	@EJB
	private LicenciaAmbientalFacade licenciaAmbientalFacade;
	@EJB
	private InformeOficioFacade informeOficioFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorFacade;
	@EJB
	private ProyectosLicenciamientoCoaFacade proyectosLicenciamientoCoaFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorProyectoFacade;
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcoaFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProyectoCertificadoAmbientalFacade proyectoCertificadoAmbientalFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	/**********************************************************************/
	@EJB
	private ResolucionAmbientalFacade resolucionAmbientalFacade;
	@EJB
	private OficioResolucionAmbientalFacade oficioResolucionAmbientalFacade;
	/*******************************************************************/
	@EJB
	private ExpedienteBPCFacade expedienteBPCFacade;

	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosAdminBean}")
	private ProyectosAdminBean proyectosBean;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosRcoaBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{autorizacionAdministrativaAmbientalBean}")
	private AutorizacionAdministrativaAmbientalBean autorizacionAdministrativaAmbientalBean;
	
	@Getter
	@Setter
	private List<EntityFichaCompletaRgd> listaProyectos;

	@Getter
	@Setter
	private String numeroAutorizacionAmbiental;
	@Getter
	@Setter
	private Date fechaOtorgamiento;
	
	@Getter
	@Setter
	private EntityFichaCompletaRgd proyectoSelecionado;
	
	@Getter
	@Setter
	private ProyectoCustom proyectoCustomSeleccionado;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@Getter
	@Setter
	private List<ProyectoCustom> listaProyectosSuia;
	
	@Getter
	@Setter
	private String numeroResolucion, plantillaDesactivacionRgd;
	
	@Getter
	@Setter
	private Date fechaProyecto;
	
	@Getter
	@Setter
	private RegistroGeneradorDesechosRcoa registroGenerador;	
	
	@Getter
	@Setter
	private RegistroGeneradorDesechosProyectosRcoa generadorProyecto;	
	
	@Getter
	@Setter
	private Integer motivoEmision;
	
	@Getter
	@Setter
	private List<RegistroGeneradorDesechosProyectosRcoa> listaGeneradorProyectos;
	
	@Getter
	@Setter
	private Integer proyectoSuiaId;
	
	@Getter
	@Setter
	private boolean mostrarFormulario, tieneBypassPPC;
	
	@Getter
	@Setter
	private DocumentosRgdRcoa documentoResolucionAAA;
	
	@Setter
	@Getter
	private ExpedienteBPC expedienteBPC = new ExpedienteBPC();
	
	@Getter
	@Setter
	private boolean habilitarSubir = true;
	private byte[] plantilla_byte;
	private Long idProceso;
    private static final String ESTADOCOMPLETADO = "Completado";
    private static final String ESTADOPORCOMPLETAR = "Por completar";
    private static final String SISTEMARCOA = "1";
    private static final String SISTEMASUIA = "2";
    private static final String SISTEMA4CATE = "3";
    private static final String SISTEMASECTOR = "4";
    private static final String SISTEMADIGITALIZACION = "6";
		
	@PostConstruct
	public void init(){
		try {
			plantillaDesactivacionRgd ="plantilla_desactivacion_tramites_rgd.pdf";
			Integer motivoEmisionAux =(Integer)(JsfUtil.devolverObjetoSession("motivoEmision"));
			if(motivoEmisionAux!=null){
				motivoEmision = motivoEmisionAux;
				JsfUtil.cargarObjetoSession("motivoEmision", null);
				autorizacionAdministrativaAmbientalBean.setIniciarRGD(false);
			}
			generadorProyecto = new RegistroGeneradorDesechosProyectosRcoa();
			listaGeneradorProyectos = new ArrayList<>();
			listaProyectos = new ArrayList<>();
			mostrarFormulario = false;
			if(autorizacionAdministrativaAmbientalBean.getIniciarRGD() == null || !autorizacionAdministrativaAmbientalBean.getIniciarRGD()){
				buscarProyectosPorOperador();
				plantilla_byte = documentosRgdRcoaFacade.descargarDocumentoPorNombre(plantillaDesactivacionRgd);
			}else if(autorizacionAdministrativaAmbientalBean.getIniciarRGD()){
				proyectoSelecionado = autorizacionAdministrativaAmbientalBean.getProyectoSeleccionado();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public Date fechaActual(){
		return new Date();
	}
	
	public void uploadListenerDocumentoResolucion(FileUploadEvent event){
		documentoResolucionAAA = this.uploadListener(event,RegistroGeneradorDesechosRcoa.class, "pdf");
	}
	 
	private DocumentosRgdRcoa uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentosRgdRcoa documento = crearDocumento(contenidoDocumento, clazz,	extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}

	public DocumentosRgdRcoa crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
		DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setExtesion("." + extension);

		documento.setMime(extension == "pdf" ? "application/pdf" : "application/vnd.ms-excel");
		return documento;
	}
	
	public StreamedContent getDocumentoResolucionAmbiental(){		
		return descargarDocumento(documentoResolucionAAA);
	}
	
	private StreamedContent descargarDocumento(DocumentosRgdRcoa documento){
		try {
			if (documento != null && documento.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(
								documento.getContenidoDocumento()),
						documento.getMime(), documento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;
	}	

	
	public void buscarProyectosPorOperador(){
		try {
			//getAllProjectsByUser usar custom
			mostrarFormulario = true;
			listaProyectos = new ArrayList<>();
			//Aqui deben ir todos los proyectos tanto como sector subsector, 4 categorias, suia y el nuevo sistema.
			listaProyectos = fichaAmbientalFacade.getFinalizadosPorProponente(loginBean.getUsuario().getNombre());
			// proyectos de Licencias y registros RCOA finalizados
			List<EntityFichaCompletaRgd> listaProyectosFinalizadosRcoa = proyectoLicenciamientoAmbientalFacade.getProyectosFinalizadosRCoa(loginBean.getUsuario().getNombre());
			if(listaProyectosFinalizadosRcoa != null && listaProyectosFinalizadosRcoa.size() > 0)
				listaProyectos.addAll(listaProyectosFinalizadosRcoa);
			// proyectos de 4 categorias finalizados
			List<EntityFichaCompletaRgd> listaProyectosFinalizados4Cat = proyectoLicenciamientoAmbientalFacade.getProyectosDigitalizados(loginBean.getUsuario().getNombre());
			listaProyectos.addAll(listaProyectosFinalizados4Cat);
			
			// verifico el numero de resolucion de cada proyecto
			for (EntityFichaCompletaRgd proyectosRGD : listaProyectos) {
				if(!proyectosRGD.getFechaRegistro().isEmpty()){
					proyectosRGD.setFechaRegistro(proyectosRGD.getFechaRegistro().substring(0, 10));
					expedienteBPC = expedienteBPCFacade.getByProyectoLicenciaCoa(Integer.parseInt(proyectosRGD.getId()));
					tieneBypassPPC = (expedienteBPC.getId() != null) ? true : false;
					proyectosRGD = obtenerResolucion(proyectosRGD);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public void datosProyecto(ProyectoLicenciamientoAmbiental proyecto){
		try {
			numeroResolucion = "";
			fechaProyecto = new Date();
			if(proyecto.getCatalogoCategoria().getCategoriaSistema().getCatalogoCategoriaPublico().getTipoSector().getNombre().equals("Registro Ambiental")){
				FichaAmbientalPma fichaAmbiental = fichaAmbientalFacade.getFichaAmbientalPorIdProyecto(proyecto.getId());
				
				if(fichaAmbiental != null && fichaAmbiental.getNumeroOficio() != null){
					numeroResolucion = fichaAmbiental.getNumeroOficio();
				}	
				
				if(fichaAmbiental != null && fichaAmbiental.getFechaModificacion() != null){
					fechaProyecto = fichaAmbiental.getFechaModificacion();
				}
			}else if(proyecto.getCatalogoCategoria().getCategoriaSistema().getCatalogoCategoriaPublico().getTipoSector().getNombre().equals("Licencia Ambiental")){
				
				numeroResolucion = informeOficioFacade.obtenerResolucion(proyecto.getCodigo());
				fechaProyecto = informeOficioFacade.obtenerFecha(proyecto.getCodigo());
			}				
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public EntityFichaCompletaRgd obtenerResolucion(EntityFichaCompletaRgd FichaCompletaRgd){
		try {
			String tramite = FichaCompletaRgd.getCodigo();
			String sistema = FichaCompletaRgd.getSistema();
			String resolucion = "", estado=ESTADOCOMPLETADO;
			Boolean resolucionFisica = null;
			switch (sistema) {
				case SISTEMARCOA: // proyectos RCOA
					ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
					if(tieneBypassPPC){
						if(proyecto.getExpedienteBPC().getTieneResolucionFisica() != null){
							resolucionFisica = proyecto.getExpedienteBPC().getTieneResolucionFisica();
						}else {
							resolucionFisica = false;
						}
					}
					if(proyecto.getCategoria().getNombrePublico().equals("Certificado Ambiental")){
						ProyectoCertificadoAmbiental certificado = proyectoCertificadoAmbientalFacade.getProyectoCertificadoAmbientalPorCodigoProyecto(proyecto);
						resolucion = certificado.getCodigoCertificado();
					}else if(proyecto.getCategoria().getNombrePublico().equals("Registro Ambiental")){
						RegistroAmbientalRcoa registro = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyecto);
						resolucion = registro.getNumeroResolucion();
					}else{
						ResolucionAmbiental resolucionAmbiental = resolucionAmbientalFacade.getByIdRegistroPreliminar(proyecto.getId());
						if(resolucionAmbiental != null && resolucionAmbiental.getId() != null){
							//resolucion = resolucionAmbiental.getNumeroResolucion();
							List<OficioResolucionAmbiental> listaResolucion = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalAndCategoria(resolucionAmbiental.getId(), 64);
							if(!listaResolucion.isEmpty()) {
								OficioResolucionAmbiental documentoResolucion = listaResolucion.get(0);
									resolucion = documentoResolucion.getCodigoReporte();
							}
						}
					}
					break;
				case SISTEMASUIA: // paraa proyectos suia
					ProyectoLicenciamientoAmbiental proyectoSuia = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(tramite);
					if(proyectoSuia != null){
						if(proyectoSuia.getCatalogoCategoria().getCategoria().getNombrePublico().equals("Registro Ambiental")){
							FichaAmbientalPma fichaAmbiental = fichaAmbientalFacade.getFichaAmbientalPorIdProyecto(Integer.valueOf(proyectoSuia.getId()));
							if(fichaAmbiental != null && fichaAmbiental.getNumeroLicencia() != null){
								resolucion = fichaAmbiental.getNumeroLicencia();
							}
						}else if(proyectoSuia.getCatalogoCategoria().getCategoria().getNombrePublico().equals("Licencia Ambiental")){
							resolucion = informeOficioFacade.obtenerResolucion(proyectoSuia.getCodigo());					
						}else if(proyectoSuia.getCatalogoCategoria().getCategoria().getNombrePublico().equals("Certificado Ambiental")){
							resolucion = " ";
						}
					}
					break;
				case SISTEMADIGITALIZACION:
					resolucion = FichaCompletaRgd.getNumeroResolucion();
					estado=FichaCompletaRgd.getEstadoProyecto();
					break;
				default:
					break;
			}
			if(resolucion == null || resolucion.isEmpty()){
				resolucion = "Por Completar Digitalización";
				estado=ESTADOPORCOMPLETAR;
			}
			if (resolucionFisica !=null && resolucionFisica ) {
			    resolucion = "Resolución emitida de manera física";
			    estado = ESTADOCOMPLETADO;
			}
			FichaCompletaRgd.setNumeroResolucion(resolucion);
			FichaCompletaRgd.setEstadoProyecto(estado);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return FichaCompletaRgd;
	}
	
	public void save(){
		try {
			if(proyectoSelecionado != null){
				registroGenerador = new RegistroGeneradorDesechosRcoa();
				generadorProyecto = new RegistroGeneradorDesechosProyectosRcoa();
				registroGenerador.setUsuario(loginBean.getUsuario());
				registroGenerador.setFinalizado(false);
				switch (proyectoSelecionado.getSistema()) {
				case SISTEMARCOA:
					ProyectoLicenciaCoa proyecto = new ProyectoLicenciaCoa();
					proyecto = proyectosLicenciamientoCoaFacade.findById(Integer.valueOf(proyectoSelecionado.getId()));	
					generadorProyecto.setProyectoLicenciaCoa(proyecto);
					generadorProyecto.setDescripcionSistema("rcoa");
					break;
				case SISTEMASUIA:
					//vienen los proyectos ingresados en suia
					ProyectoLicenciamientoAmbiental proyectoSuia = new ProyectoLicenciamientoAmbiental();
					proyectoSuia = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(proyectoSelecionado.getCodigo());
					generadorProyecto.setDescripcionSistema("suia_iii");
					generadorProyecto.setProyectoId(proyectoSuia.getId());
					break;
				case SISTEMA4CATE:
					//vienen los proyectos ingresados en fisico
					generadorProyecto.setProyectoId(Integer.valueOf(proyectoSelecionado.getId()));
					generadorProyecto.setDescripcionSistema("fisico");
					break;
				case SISTEMADIGITALIZACION:
					generadorProyecto.setDescripcionSistema("digitalizacion");
					AutorizacionAdministrativaAmbiental autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(Integer.valueOf(proyectoSelecionado.getId()));
					generadorProyecto.setProyectoDigitalizado(autorizacionAdministrativa);
					switch (autorizacionAdministrativa.getSistema().toString()) {
					case "0":  //NUEVO
						break;
					case "1":  //FISICO
						break;
					case "2":  //CUATRO_CATEGORIAS
					case "3":  //SECTOR_SUBSECTOR
						generadorProyecto.setCodigoProyecto(autorizacionAdministrativa.getCodigoProyecto());
						break;
					case "4":  //SUIA_III
						generadorProyecto.setProyectoId(Integer.valueOf(autorizacionAdministrativa.getIdProyecto()));
						break;
					case "5":  //RCOA
						break;

					default:
						break;
					}
					break;
				default:
					break;
				}
				generadorProyecto.setFormaEmision(proyectoSelecionado.getSistema());
				registroGeneradorFacade.save(registroGenerador, loginBean.getUsuario());
				generadorProyecto.setRegistroGeneradorDesechosRcoa(registroGenerador);
				registroGeneradorProyectoFacade.save(generadorProyecto, loginBean.getUsuario());		
				
				JsfUtil.addMessageInfo("Información enviada");
	        	JsfUtil.redirectTo("/pages/rcoa/generadorDesechos/informacionRegistroGeneradorAAA.jsf");
				
				
			}else{
				JsfUtil.addMessageError("Debe seleccionar un proyecto");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean validarProyecto(){
		boolean completado=false;
		autorizacionAdministrativaAmbientalBean.setIniciarRGD(false);
		// valido si ya existe un proceso de rgd iniciado
		completado = !validarRGD(proyectoSelecionado);
		if(completado){
			switch (proyectoSelecionado.getEstadoProyecto()) {
			case ESTADOCOMPLETADO:
				completado=true;
				break;
			case ESTADOPORCOMPLETAR:
				JsfUtil.addMessageError("Debe completar el proceso de digitalización");
				AutorizacionAdministrativa aaa = new AutorizacionAdministrativa();
				JsfUtil.cargarObjetoSession("nuevoRegistro", false);
				JsfUtil.cargarObjetoSession("editardatos", false);
				Integer idProyecto = Integer.parseInt(proyectoSelecionado.getId());
				bandejaTareasBean.setProcessId(0);
				AutorizacionAdministrativaAmbiental autorizacion = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(idProyecto);
				aaa.setCodigo(autorizacion.getCodigoProyecto());
				aaa.setEstado(ESTADOPORCOMPLETAR);
				aaa.setIdProceso(autorizacion.getIdProceso());
				aaa.setIdDigitalizacion(autorizacion.getId());
				autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(aaa);
				autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaPrincipal(null);
				autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(null);
				autorizacionAdministrativaAmbientalBean.setIniciarRGD(true);
				autorizacionAdministrativaAmbientalBean.setProyectoSeleccionado(proyectoSelecionado);
				if(autorizacion != null && autorizacion.getCodigoProyecto() != null){
					JsfUtil.cargarObjetoSession("tramite", autorizacion.getCodigoProyecto());
					if(autorizacion.getIdProceso() != null)
						bandejaTareasBean.setProcessId(autorizacion.getIdProceso());
				}
				JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoInformacionAAA.jsf");
				completado=false;
				break;

			default:
				completado=false;
				break;
			}
		}
		return completado;
	}
	
	public void enviar(){
		try {
			if(proyectoSelecionado != null){
				if(validarProyecto()){
					Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
					parametros.put("operador", JsfUtil.getLoggedUser().getNombre());
					parametros.put("tramite", proyectoSelecionado.getCodigo());
					if(proyectoSelecionado.getSistema().equals(SISTEMADIGITALIZACION)){
						parametros.put("idProyectoDigitalizacion", proyectoSelecionado.getId());
						if(proyectoSuiaId > 0)
							parametros.put("idProyecto", proyectoSuiaId);
						else
							parametros.put("idProyecto", "0"+proyectoSelecionado.getId());
					}else{
						parametros.put("idProyecto", proyectoSelecionado.getId());
					}
					parametros.put("emisionVariosProyectos", false);
					parametros.put(Constantes.VARIABLE_TIPO_RGD, Constantes.TIPO_RGD_AAA);
					parametros.put("responsabilidadExtendida", false);
					parametros.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, JsfUtil.getLoggedUser().getNombre());
//				
					idProceso = procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_REGISTRO_GENERADOR_DESECHOS, 
							proyectoSelecionado.getCodigo(), parametros);
					bandejaTareasBean.setProcessId(idProceso);
					try {
						TaskSummary tareaActual = procesoFacade.getCurrenTask(JsfUtil.getLoggedUser(), idProceso);
						if(tareaActual != null){
							TaskSummaryCustom tarea = new TaskSummaryCustom();
							tarea.setTaskSummary(tareaActual);
							tarea.setTaskName(tareaActual.getName());
							tarea.setProcessName("Registro de Generador de Residuos y Desechos Peligrosos y/o Especiales");
							tarea.setProcessInstanceId(idProceso);
							tarea.setTaskId(tareaActual.getId());
							tarea.setProcedure(proyectoSelecionado.getCodigo());
							bandejaTareasBean.setTarea(tarea);
						}
					} catch (JbpmException e) {
						e.printStackTrace();
					}
					save();
				}
			}else{
				JsfUtil.addMessageError("Debe seleccionar un proyecto");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void vincularProyectoSuia(ProyectoLicenciamientoAmbiental proyecto){
		proyectoCustomSeleccionado.getCategoria();
	}
	
	public void proyectoSeleccionado(){
		if(proyectoSelecionado != null){
			habilitarSubir = false;
		}else{
			habilitarSubir = true;
		}		
	}
	
	public boolean validarExistenciaRGD(){
		boolean valido = false;
		// verifico si ya existe un rgd del proyecto seleccionado
		if(proyectoSelecionado!= null)
			valido = validarRGD(proyectoSelecionado);
		// si ya existe no dejo seleccionar
		if(valido)
			proyectoSelecionado = null;
		return valido;
	}
	
	public String seleccionar(EntityFichaCompletaRgd proyectoCustom) {
		switch (proyectoCustom.getSistema()) {
		case SISTEMARCOA:  /// proyectos RCOA
			try {
				Integer idProyecto = Integer.parseInt(proyectoCustom.getId());
				ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
				proyectosRcoaBean.setProyectoRcoa(proyecto);
				JsfUtil.redirectTo("/proyectos/proyectoRcoaDatos.jsf");
				//return JsfUtil.actionNavigateTo("/proyectos/resumenProyectoRcoaRgd.jsf");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return null;
			}
			break;
		case SISTEMASUIA:  // Proyectos SUIA
			try {
				ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
						.buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyectoCustom.getId()));
				proyectosBean.setProyectoToShow(proyecto);
				proyectosBean.setProyecto(proyecto);
				if(proyecto.getCatalogoCategoria().getCategoria().getNombrePublico().contains("Licencia") && Usuario.isUserInRole(JsfUtil.getLoggedUser(), "SENAGUA")){
					return JsfUtil.getBean(EstudioImpactoAmbientalBean.class).verEIA2(proyecto);
				}else {
					JsfUtil.redirectTo("/proyectos/resumenProyectoRgd.jsf");
					//return JsfUtil.actionNavigateTo("/proyectos/resumenProyectoRgd.jsf");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return null;
			}
			break;
		case SISTEMA4CATE:  // proyectos fisicos
			JsfUtil.getBean(ContenidoExterno.class).executeAction(proyectoCustom.getCodigo(),
					IntegracionFacade.IntegrationActions.mostrar_dashboard);
			
			break;
			//DIGITALIZACION
			case SISTEMADIGITALIZACION:
				AutorizacionAdministrativa aaa = new AutorizacionAdministrativa();
				JsfUtil.cargarObjetoSession("nuevoRegistro", false);
				JsfUtil.cargarObjetoSession("editardatos", false);
				Integer idProyecto = Integer.parseInt(proyectoCustom.getId());
				bandejaTareasBean.setProcessId(0);
				AutorizacionAdministrativaAmbiental autorizacion = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(idProyecto);
				autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(aaa);
				autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaPrincipal(null);
				autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(null);
				if(autorizacion != null && autorizacion.getCodigoProyecto() != null){
					JsfUtil.cargarObjetoSession("tramite", autorizacion.getCodigoProyecto());
					if(autorizacion.getIdProceso() != null)
						bandejaTareasBean.setProcessId(autorizacion.getIdProceso());
				}
				JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoFormDigitalizacionVer.jsf");
				break;
		}
		return null;

	}
	
	public boolean validarRGD(EntityFichaCompletaRgd proyectoCustom){
		proyectoSuiaId=0;
		Integer idProyecto = Integer.parseInt(proyectoCustom.getId());
		RegistroGeneradorDesechosRcoa objRegistroGenerador = new RegistroGeneradorDesechosRcoa();
		try {
			switch (proyectoCustom.getSistema()) {
			//RCOA
			case SISTEMARCOA:
				objRegistroGenerador = registroGeneradorFacade.buscarRGDPorProyectoRcoa(idProyecto);
				break;
			case SISTEMASUIA:
				if(validarRGDSuia(idProyecto))
					return true;
				break;
			case SISTEMA4CATE:
				break;
			//DIGITALIZACION
			case SISTEMADIGITALIZACION:
				AutorizacionAdministrativaAmbiental autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(idProyecto);
				if(autorizacionAdministrativa.getSistema().toString().equals("4")){
					proyectoSuiaId = autorizacionAdministrativa.getIdProyecto();
					if(validarRGDSuia(proyectoSuiaId))
						return true;
				}
				// verifico si el prouecto digitalizado tiene rgd asociados
				objRegistroGenerador = registroGeneradorFacade.buscarRGDPorProyectoDigitalizado(idProyecto);
				if(objRegistroGenerador != null && objRegistroGenerador.getId() != null){
					if(objRegistroGenerador.getFinalizado() == true){
						RequestContext.getCurrentInstance().execute("PF('existeRgdDiag').show()");
					}else{
						RequestContext.getCurrentInstance().execute("PF('enTramiteRgdDiag').show()");
					}
					return true;
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(objRegistroGenerador != null && objRegistroGenerador.getId() != null){
			if(objRegistroGenerador.getFinalizado() == true){
				RequestContext.getCurrentInstance().execute("PF('existeRgdDiag').show()");
			}else{
				RequestContext.getCurrentInstance().execute("PF('enTramiteRgdDiag').show()");
			}
			return true;
		}else{
			return false;
		}
	}
	
	private boolean validarRGDSuia(Integer idProyecto){
		RegistroGeneradorDesechosRcoa objRegistroGenerador = registroGeneradorFacade.buscarRGDPorProyectoSuia(idProyecto);
		if(objRegistroGenerador == null){
			List<GeneradorDesechosPeligrosos> regisatrosRGD = registroGeneradorDesechosFacade.findRGDByProyectoLicenciaAmbientalId(idProyecto);
			if(regisatrosRGD != null && regisatrosRGD.size() > 0){
				boolean finalizado=true;
				for (GeneradorDesechosPeligrosos objRegistro : regisatrosRGD) {
					if(!objRegistro.isFinalizado())
						finalizado = false;
				}
				if(finalizado){
					RequestContext.getCurrentInstance().execute("PF('existeRgdDiag').show()");
				}else{
					RequestContext.getCurrentInstance().execute("PF('enTramiteRgdDiag').show()");
				}
				return true;
			}
		}else{
			if(objRegistroGenerador.getFinalizado()){
				RequestContext.getCurrentInstance().execute("PF('existeRgdDiag').show()");
			}else{
				RequestContext.getCurrentInstance().execute("PF('enTramiteRgdDiag').show()");
			}
			return true;
		}
		return false;
	}
	
	public StreamedContent getGuia() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (plantilla_byte != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(plantilla_byte));
				content.setName(plantillaDesactivacionRgd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
}
