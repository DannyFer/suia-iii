package ec.gob.ambiente.rcoa.sustanciasQuimicasImportacion.controllers;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.primefaces.context.DefaultRequestContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ActividadSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DetalleSolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosSustanciasQuimicasRcoaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.PermisoDeclaracionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.SolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DetalleSolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.PermisoDeclaracionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.configuration.domain.ConfigEntry;
import ec.gob.ambiente.suia.configuration.facade.ConfigurationFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class IngresoInformacionImportacionController {
	
	private static final Logger LOG = Logger.getLogger(IngresoInformacionImportacionController.class);
	
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQumicaFacade;
	@EJB
	private ActividadSustanciaQuimicaFacade actividadSustanciaQuimicaFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private SolicitudImportacionRSQFacade solicitudImportacionRSQFacade;
	@EJB
	private DetalleSolicitudImportacionRSQFacade detalleSolicitudImportacionRSQFacade;
	@EJB
	private DocumentosSustanciasQuimicasRcoaFacade documentosFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private DocumentosRSQFacade documentosRSQFacade;
	@EJB
	private InformesOficiosRSQFacade informesOficiosRSQFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private PermisoDeclaracionRSQFacade permisoDeclaracionRSQFacade;
	@EJB
	private ConfigurationFacade configurationFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	private boolean tipo;
		
	@Getter
	@Setter
	private GestionarProductosQuimicosProyectoAmbiental sustancia;
	
	@Getter
	@Setter
	private double cantidad, pesoNeto, pesoBruto;
	
	@Getter
	@Setter
	private List<UbicacionesGeografica> listaUbicaciones;
	
	@Getter
	@Setter
	private List<RegistroSustanciaQuimica> listaRegistrosRSQ;
	
	@Getter
	@Setter
	private RegistroSustanciaQuimica registroRSQ;
	
	@Getter
	@Setter
	private String tramiteRSQ;
	
	@Getter
	private List<ActividadSustancia> actividadSustanciaProyectoList;
	
	@Getter
	@Setter
	private ActividadSustancia actividadSustanciaSeleccionada;
	
	@Getter
	@Setter
	private SolicitudImportacionRSQ solicitud;
	
	@Getter
	@Setter
	private DetalleSolicitudImportacionRSQ detalle;
	
	@Getter
	@Setter
	private Integer idActividadSustanciaSeleccionada;
	
	@Getter
	@Setter
	private List<SolicitudImportacionRSQ> listaSolicitudes, listaSolicitudesAnuladas;
	
	@Getter
	@Setter
	private Integer idPais;
	
	@Getter
	@Setter
	private Double cupoCantidad;
	
	@Getter
	@Setter
	private DocumentosSustanciasQuimicasRcoa documentoEvidencia, documentoOficio;
	
	@Getter
    @Setter
    private InformeOficioRSQ oficio;
	
	@Getter
	@Setter
	private String nombre, representanteLegal, telefono, correo, celular;
	
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionGeografica;
	
	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionesGeografica;
	
	@Getter
	@Setter
	private String numeroTramite;
	
	@Getter
	@Setter
	private String mensajeAyudaPesoNeto = "Valor peso Neto: corresponde a la cantidad de peso de la sustancia química";
	
	@Getter
	@Setter
	private String mensajeAyudaPesoBruto = "Valor peso bruto: corresponde a la cantidad de peso de la sustancia química más los envases.";
	
	@Getter
	@Setter
	private boolean solicitudSeleccionada;
	
	@Getter
	@Setter
	private boolean guardado, enviado;
	
	@Getter
	@Setter
	private boolean accederImportacion = false;
	
//	@Getter
//	@Setter
	private String urlEcuapass;
	
	@Getter
	@Setter
	private Boolean bloquearEnviar;
	
	@PostConstruct
	private void init(){
		try {
			if(JsfUtil.devolverObjetoSession("registroRSQ") != null){
				int idRegistroRSQ = (Integer)(JsfUtil.devolverObjetoSession("registroRSQ"));
				registroRSQ= registroSustanciaQumicaFacade.obtenerRegistroPorId(idRegistroRSQ);
			}
			
			listaUbicaciones = ubicacionGeograficaFacade.getListaPaises();		
			
			loginBean.getUsuario().getPersona().getNombre();
			
			listaRegistrosRSQ = getListaRegistro();
			
			tramiteRSQ = "";
			if(registroRSQ != null){
				tramiteRSQ = registroRSQ.getNumeroAplicacion();
				cargarDatosRSQ();
			}
			
			tipo = true;
			
			cargarInformacionOperador();
			
			solicitudSeleccionada = false;
			guardado = true;
			enviado = false;
			
			bloquearEnviar = false;
			
			try {
				obtenerUrlEcuapass();
			}catch(Exception ex) {
				Log.error("No se logró encontrar url de Ecuapass en ConfigEntry");
			}
		} catch (Exception e) {
			Log.error(e.getStackTrace());
			e.printStackTrace();
		}	
	}
	
	private void obtenerUrlEcuapass() throws Exception{
		//"https://ecuapasstest.aduana.gob.ec/";
		List<ConfigEntry> lConfigEntry = configurationFacade.getConfigEntries(); 
		
		for (Iterator<ConfigEntry> iterator = lConfigEntry.iterator(); iterator.hasNext();) {
			ConfigEntry configEntry = (ConfigEntry) iterator.next();
			
			if(Constantes.URL_ECUAPASS.equals(configEntry.getKey())) {
				urlEcuapass = configEntry.getValue();
				break;
			}
		}
	}
	
	public void accesoImportacion(){
		if(Constantes.getAccesoImportacion().contains(loginBean.getNombreUsuario())){
			accederImportacion = true;
			return;
		}
	}
	
	private void cargarInformacionOperador(){
		try {
			
			if (JsfUtil.getLoggedUser().getNombre().length()<=10) {
				setNombre(JsfUtil.getLoggedUser().getPersona().getNombre());
				getEmailPersona(JsfUtil.getLoggedUser().getPersona());				
			
			} else {
				Organizacion organizacion = organizacionFacade.buscarPorRuc(JsfUtil.getLoggedUser().getNombre());					

				if(organizacion==null){
					setNombre(JsfUtil.getLoggedUser().getPersona().getNombre());
					getEmailPersona(JsfUtil.getLoggedUser().getPersona());
				
				}else{
					setNombre(organizacion.getNombre());
					setRepresentanteLegal(organizacion.getPersona().getNombre());
					getEmailOrganizacion(organizacion);
				}
			}	
			
			ubicacionGeografica = ubicacionGeograficaFacade.ubicacionCompleta(JsfUtil.getLoggedUser().getPersona().getIdUbicacionGeografica());
			
			ubicacionesGeografica = new ArrayList<>();
			ubicacionesGeografica.add(ubicacionGeografica);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void getEmailPersona(Persona persona){
		try {
			
			String correo = "";
			String telefono = "";
			String celular = "";
			
			List<Contacto> listaContactosPersona = contactoFacade.buscarPorPersona(persona);
			
			for(Contacto contacto : listaContactosPersona){
				if(contacto.getFormasContacto().getId()==FormasContacto.EMAIL){
					correo = contacto.getValor();
				}
				
				if(contacto.getFormasContacto().getId()==FormasContacto.TELEFONO){
					telefono = contacto.getValor();
				}
				
				if(contacto.getFormasContacto().getId()== FormasContacto.CELULAR){
					celular = contacto.getValor();
				}
			}	
			
			setCorreo(correo);
			setTelefono(telefono);
			setCelular(celular);
			
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}
	
	public void getEmailOrganizacion(Organizacion organizacion){
		try {
			String correo = "";
			String telefono = "";
			String celular = "";
			
			List<Contacto> listaContactosOrganizacion = contactoFacade.buscarPorOrganizacion(organizacion);
			
			for(Contacto contacto : listaContactosOrganizacion){
				if(contacto.getFormasContacto().getId()==FormasContacto.EMAIL){
					correo = contacto.getValor();
				}
				
				if(contacto.getFormasContacto().getId()==FormasContacto.TELEFONO){
					telefono = contacto.getValor();
				}
				
				if(contacto.getFormasContacto().getId()== FormasContacto.CELULAR){
					celular = contacto.getValor();
				}
			}
			
			setCorreo(correo);
			setTelefono(telefono);
			setCelular(celular);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void cargarDatosRSQ(){
		try {
			actividadSustanciaProyectoList = new ArrayList<ActividadSustancia>();
			listaSolicitudes = new ArrayList<>();
			actividadSustanciaProyectoList=actividadSustanciaQuimicaFacade.obtenerActividadesPorRSQImportacion(registroRSQ);
			
			solicitud = new SolicitudImportacionRSQ();	
			detalle = new DetalleSolicitudImportacionRSQ();
			detalle.setUnidadPesoBruto("kg");
			detalle.setUnidadPesoNeto("kg");
			
			if(registroRSQ != null && registroRSQ.getId() != null){
				List<SolicitudImportacionRSQ> listaSolicitudesAux = solicitudImportacionRSQFacade.listaPorRSQ(registroRSQ.getId());
				
				if(listaSolicitudesAux != null){
					for(SolicitudImportacionRSQ sol : listaSolicitudesAux){
						List<SolicitudImportacionRSQ> listaSolicitudesAnuladasAux =  solicitudImportacionRSQFacade.listaSolicitudesAnuladasPorSolicitud(sol.getId());
						if(listaSolicitudesAnuladasAux == null || listaSolicitudesAnuladasAux.isEmpty()){
							listaSolicitudes.add(sol);
						}
					}
				}else{
					listaSolicitudes = new ArrayList<>();	
				}			
			}else
				listaSolicitudes = new ArrayList<>();	
			
			List<InformeOficioRSQ> oficios=informesOficiosRSQFacade.obtenerPorRSQListaPorTipo(registroRSQ,TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO);
			if(oficios != null && !oficios.isEmpty()){
				oficio = oficios.get(0);
				documentoOficio=documentosRSQFacade.obtenerDocumentoPorTipo(registroRSQ.pronunciamientoAprobado()?TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_OFICIO_NEGADO,"InformeOficioRSQ",oficio.getId());
			}		
			
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}
	
	public List<RegistroSustanciaQuimica> getListaRegistro(){
		List<RegistroSustanciaQuimica> lista = new ArrayList<RegistroSustanciaQuimica>();		
		
		try {
						
			List<RegistroSustanciaQuimica> listaAux = registroSustanciaQumicaFacade.obtenerRegistrosPorUsuario(loginBean.getUsuario());
			
			for(RegistroSustanciaQuimica reg : listaAux){
//				System.out.println(reg.getNumeroAplicacion());
				
				List<PermisoDeclaracionRSQ> listaPermisos = new ArrayList<PermisoDeclaracionRSQ>();
				
				listaPermisos = permisoDeclaracionRSQFacade.obtenerPermisoImportacion(reg);
				
				if(listaPermisos != null && !listaPermisos.isEmpty()){
					List<ActividadSustancia> actividadSustanciaProyectoLista=actividadSustanciaQuimicaFacade.obtenerActividadesPorRSQImportacion(reg);
					
					if(actividadSustanciaProyectoLista != null && !actividadSustanciaProyectoLista.isEmpty()){
						if(!lista.contains(reg)){
							lista.add(reg);
						}						
					}
				}
			}			
			
		} catch (Exception e) {
			Log.error(e.getStackTrace());
			e.printStackTrace();
		}
		return lista;
	}
	
	public void ingresar(RegistroSustanciaQuimica registro){
		
		JsfUtil.cargarObjetoSession("registroRSQ", registro.getId());
		
		JsfUtil.redirectTo("/pages/rcoa/sustanciasQuimicas/importacion/ingresoInformacionImportacion.jsf");
		
	}
	
	public void sustanciaListener(){
		try {		
			limpiar();
			
			if(idActividadSustanciaSeleccionada != 0){
				actividadSustanciaSeleccionada = actividadSustanciaQuimicaFacade.obtenerActividadesPorId(idActividadSustanciaSeleccionada);		

//				if(actividadSustanciaSeleccionada.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion().equals("Mercurio")){
					List<SolicitudImportacionRSQ> listaSolicitudesAux =  solicitudImportacionRSQFacade.listaSolicitudesTotalPorRSQ(registroRSQ.getId(), actividadSustanciaSeleccionada.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getId());
					
					
					if(listaSolicitudesAux != null && !listaSolicitudesAux.isEmpty()){
						DetalleSolicitudImportacionRSQ detalleAnterior = detalleSolicitudImportacionRSQFacade.buscarPorSolicitud(listaSolicitudesAux.get(0));
						cupoCantidad = detalleAnterior.getCupoCantidad().doubleValue();
					}
					else{						
						cupoCantidad = actividadSustanciaSeleccionada.getCupo();
					}
//				}	
			}					
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void limpiar(){
		cupoCantidad = null;
		actividadSustanciaSeleccionada = new ActividadSustancia();
		idPais = 0;
		detalle.setUbicacionGeografica(null);
		detalle.setPesoBruto(null);
		detalle.setPesoNeto(null);
	}
	
	public void paisListener(){
		try {
			
			UbicacionesGeografica pais = ubicacionGeograficaFacade.nivelNAcional(idPais);
			
			if(pais != null){
				detalle.setUbicacionGeografica(pais);
			}else{
				JsfUtil.addMessageError("Ocurrió un error al seleccionar el pais.");
				return;
			}			
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void uploadDocumento(FileUploadEvent event){		
		documentoEvidencia = new DocumentosSustanciasQuimicasRcoa();
		documentoEvidencia = this.uploadListener(event, DocumentosSustanciasQuimicasRcoa.class, "pdf");
//		solicitud.setDocumentosSustanciasQuimicasRcoa(documento);
	}
	
	private DocumentosSustanciasQuimicasRcoa uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentosSustanciasQuimicasRcoa documento = crearDocumento(contenidoDocumento, clazz, extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}

	public DocumentosSustanciasQuimicasRcoa crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
		DocumentosSustanciasQuimicasRcoa documento = new DocumentosSustanciasQuimicasRcoa();
		documento.setContenidoDocumento(contenidoDocumento);		
		documento.setExtesion("." + extension);

		documento.setMime("application/pdf");
		return documento;
	}
	
	public void guardar(boolean mensaje){
		try {
						
			if(tipo){
				
				if(cupoCantidad != null){
					Double cupoGuardarAux = cupoCantidad - detalle.getPesoNeto().doubleValue(); 
					
					if(cupoGuardarAux < 0){					
						JsfUtil.addMessageError("El peso neto ingresado es mayor al cupo cantidad de la sustancia química");
						return;
					}
				}
				
				solicitud.setAnulacion(false);
				solicitud.setAutorizacion(true);
				solicitud.setGestionarProductosQuimicosProyectoAmbiental(actividadSustanciaSeleccionada.getGestionarProductosQuimicosProyectoAmbiental());	
				solicitud.setFechaInicioAutorizacion(new Date());
				solicitud.setRegistroSustanciaQuimica(registroRSQ);
				solicitud.setSustanciaQuimicaPeligrosa(actividadSustanciaSeleccionada.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica());
								
				solicitudImportacionRSQFacade.save(solicitud, loginBean.getUsuario());
				
				if(cupoCantidad != null){
					Double cupoGuardar = cupoCantidad - detalle.getPesoNeto().doubleValue(); 
								
					detalle.setCupoCantidad(BigDecimal.valueOf(cupoGuardar));
					actividadSustanciaSeleccionada.setCupoControl(cupoGuardar);
					detalle.setActividadSustancia(actividadSustanciaSeleccionada);
					
					actividadSustanciaQuimicaFacade.guardar(actividadSustanciaSeleccionada, JsfUtil.getLoggedUser());
				}
				
				detalle.setSubPartidaArancelaria(actividadSustanciaSeleccionada.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getPartidaArancelaria());				
				detalle.setSolicitudImportacionRSQ(solicitud);
				
				detalleSolicitudImportacionRSQFacade.save(detalle, loginBean.getUsuario());
				
			}else{
				
				if(!solicitudSeleccionada){
					JsfUtil.addMessageError("Debe seleccionar un trámte");
					return;
				}
				solicitud.setAnulacion(true);
				solicitud.setAutorizacion(false);
				solicitud.setRegistroSustanciaQuimica(registroRSQ);
				solicitud.setFechaAnulacion(new Date());
								
//				if(documentoEvidencia == null || documentoEvidencia.getContenidoDocumento() == null){
//					JsfUtil.addMessageError("Adjunte documento de Evidencia");
//					return;
//				}
				
				solicitudImportacionRSQFacade.save(solicitud, loginBean.getUsuario());
				
				if(documentoEvidencia != null && documentoEvidencia.getContenidoDocumento() != null){
					
					documentoEvidencia.setIdTable(solicitud.getId());					
					documentoEvidencia.setNombreTabla(SolicitudImportacionRSQ.class.getSimpleName());					
					DocumentosSustanciasQuimicasRcoa documento = documentosFacade.guardarDocumentoAlfrescoImportacion(solicitud.getTramite(), "IMPORTACION SUSTANCIAS QUIMICAS", bandejaTareasBean.getProcessId(), documentoEvidencia, TipoDocumentoSistema.RCOA_RSQ_JUSTIFICACION_ANULACION_IMPORTACION);
											
					solicitud.setDocumentosSustanciasQuimicasRcoa(documento);
				}	
				solicitudImportacionRSQFacade.save(solicitud, loginBean.getUsuario());
				detalle.setSolicitudImportacionRSQ(solicitud);			
				
				actividadSustanciaQuimicaFacade.guardar(actividadSustanciaSeleccionada, JsfUtil.getLoggedUser());
				
				detalleSolicitudImportacionRSQFacade.save(detalle, loginBean.getUsuario());
			}			
			
			if(mensaje){
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}
			
			guardado = false;
			
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
			JsfUtil.addMessageError("Error al guardar la informacion");
		}		
	}
	
	public void enviar(){
		try {
			guardar(false);			
			bloquearEnviar = true;
			Usuario usuarioAutoridad = new Usuario();
			List<Usuario> listaUsuarios = usuarioFacade.buscarUsuarioPorRolActivo(Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental"));
			
			if(listaUsuarios != null && !listaUsuarios.isEmpty()){
				usuarioAutoridad = listaUsuarios.get(0);
			}else
				usuarioAutoridad = null;
			
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("usuario_operador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("tramite", solicitud.getTramite());
			
			if(registroRSQ.getProyectoLicenciaCoa() != null){
				parametros.put("idProyecto", registroRSQ.getProyectoLicenciaCoa().getId());	
			}
			
			parametros.put("usuario_subsecretario", usuarioAutoridad.getNombre());
			parametros.put("emision", tipo);
			parametros.put("usuarioVistaMisProcesos", JsfUtil.getLoggedUser().getNombre());
			parametros.put("codigoProyecto",solicitud.getTramite());			
			
			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(),
					Constantes.RCOA_PROCESO_SUSTANCIAS_QUIMICAS_IMPORTACION, solicitud.getTramite(),
					parametros);
						
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");

		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage() + " " + e.getCause().getMessage());			
		}
		
	}
		
	public void seleccionarListener(Integer id){
		try {
			System.out.println("id seleccionada" + id);
			solicitudSeleccionada = true;
			
			SolicitudImportacionRSQ solicitudSeleccionadaAux = new SolicitudImportacionRSQ();
			solicitudSeleccionadaAux = solicitudImportacionRSQFacade.buscarPorId(id);
			
			solicitud.setSolicitudAnulada(solicitudSeleccionadaAux);
			solicitud.setSustanciaQuimicaPeligrosa(solicitudSeleccionadaAux.getSustanciaQuimicaPeligrosa());	
			DetalleSolicitudImportacionRSQ detalleSeleccionado = detalleSolicitudImportacionRSQFacade.buscarPorSolicitud(solicitudSeleccionadaAux);		
			
			List<ActividadSustancia> actividadSustanciaSeleccionadaList = actividadSustanciaQuimicaFacade.obtenerActividadesImportacion(solicitudSeleccionadaAux.getRegistroSustanciaQuimica(), solicitudSeleccionadaAux.getSustanciaQuimicaPeligrosa());
			actividadSustanciaSeleccionada = actividadSustanciaSeleccionadaList.get(0);
			List<SolicitudImportacionRSQ> listaSolicitudesAux =  solicitudImportacionRSQFacade.listaSolicitudesTotalPorRSQ(registroRSQ.getId(), solicitud.getSustanciaQuimicaPeligrosa().getId());
						
			if(listaSolicitudesAux != null && !listaSolicitudesAux.isEmpty()){
				DetalleSolicitudImportacionRSQ detalleAnterior = detalleSolicitudImportacionRSQFacade.buscarPorSolicitud(listaSolicitudesAux.get(0));
				cupoCantidad = detalleAnterior.getCupoCantidad().doubleValue();
			}			
			
			
			
			if(detalleSeleccionado.getCupoCantidad() != null){
				
				Double cupoGuardar = cupoCantidad + detalleSeleccionado.getPesoNeto().doubleValue(); 
				
				detalle.setCupoCantidad(BigDecimal.valueOf(cupoGuardar));
				actividadSustanciaSeleccionada.setCupoControl(cupoGuardar);
			}			
			
			RequestContext.getCurrentInstance().update("pnlDatosAnulacion");
			
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void tipoTramite(){
		
		if(tipo){
			solicitud.setSolicitudAnulada(null);
			solicitud.setJustificacionAnulacion(null);
			solicitud.setAutorizacion(true);
			solicitud.setDocumentosSustanciasQuimicasRcoa(null);
			solicitudSeleccionada = false;
			detalle = new DetalleSolicitudImportacionRSQ();
			idActividadSustanciaSeleccionada = null;
			cupoCantidad = null;
			actividadSustanciaSeleccionada = null;
		}else{
			solicitud.setAnulacion(true);
			solicitud.setFechaInicioAutorizacion(null);
			solicitud.setGestionarProductosQuimicosProyectoAmbiental(null);
			detalle = new DetalleSolicitudImportacionRSQ();
			idActividadSustanciaSeleccionada = null;
			cupoCantidad = null;
			actividadSustanciaSeleccionada = null;
		}
	}
	
	public StreamedContent descargarDocumento() throws Exception {		
		
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documentoOficio != null) {
			if (documentoOficio.getContenidoDocumento() == null) {
				documentoOficio.setContenidoDocumento(documentosFacade
						.descargar(documentoOficio.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documentoOficio.getContenidoDocumento()), documentoOficio.getExtesion());
			content.setName(documentoOficio.getNombre());
		}
		
		return content;
	}
		

	public void abrirDialog(){
		numeroTramite = solicitud.getTramite();
		DefaultRequestContext.getCurrentInstance().execute("PF('finalizeDlg').show();");		
	}
	
	public void cerrar(){
		
		if(!guardado){
			solicitud.setEstado(false);
			solicitudImportacionRSQFacade.save(solicitud, loginBean.getUsuario());
		}
		
		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	}

	public String getUrlEcuapass() {
		return urlEcuapass;
	}

	public void setUrlEcuapass(String urlEcuapass) {
		this.urlEcuapass = urlEcuapass;
	}

	
}
