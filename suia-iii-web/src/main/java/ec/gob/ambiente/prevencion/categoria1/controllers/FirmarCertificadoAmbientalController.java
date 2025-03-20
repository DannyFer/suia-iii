package ec.gob.ambiente.prevencion.categoria1.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.certificado.ambiental.controllers.GenerarQRCertificadoAmbiental;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CertificadoRegistroAmbiental;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentoProyecto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityCertificadoAmbientalAmb;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.Categoria1Facade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.facade.ReportesFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class FirmarCertificadoAmbientalController {
	
	private final Logger LOG = Logger.getLogger(FirmarCertificadoAmbientalController.class);
	
	@EJB	
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private Categoria1Facade categoria1Facade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private ReportesFacade reportesFacade;		
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	private List<ProyectoLicenciamientoAmbiental> listaProyectos;
	
    @Getter
	@Setter
	private boolean ambienteProduccion;
	
	@Getter
    @Setter
    public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	
	@Getter
	@Setter
	private Documento documentoCertificado, documentoManual;
	
	@Getter
	@Setter
	private boolean token;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;
	
	@Getter
	@Setter
	private String tramite;
	
	@Getter
	@Setter
	private String nombreFichero, nombreReporte;
	
	@Getter
    @Setter
    private byte[] archivoInforme;
	
	@Getter
	@Setter
	private String idProceso, idTarea;
	
	@Getter
	@Setter
	private boolean documentoDescargado = false, documentoSubido = false, ingresoFirma = false;
	
	@Getter
	@Setter
	private List<String> notificaciones;
	
	@PostConstruct
	public void init(){
		try {
			
			listaProyectos = new ArrayList<ProyectoLicenciamientoAmbiental>();
			notificaciones = new ArrayList<String>();
			
			if(JsfUtil.devolverObjetoSession("codigoProyecto") != null){
				tramite = (String)(JsfUtil.devolverObjetoSession("codigoProyecto"));	
				ingresoFirma = true;
			}			
			
			if(JsfUtil.devolverObjetoSession("codigoProyecto") == null || JsfUtil.devolverObjetoSession("codigoProyecto").equals("")){
				
				if(loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getTipoArea().getSiglas().equals("PC") || loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getTipoArea().getSiglas().toUpperCase().equals("ZONALES") ||
						loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getTipoArea().getSiglas().equals("OT")){
					listaProyectos = proyectoFacade.obtenerCertificadosAmbientales(loginBean.getUsuario().getListaAreaUsuario().get(0).getArea());
				}else{
					listaProyectos = proyectoFacade.obtenerCertificadosAmbientalesEA(loginBean.getUsuario().getListaAreaUsuario().get(0).getArea());
				}
				
				ingresoFirma = false;
			}
			
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			token = true;
			if(!ambienteProduccion){
				verificaToken();
			}
			
			if(ingresoFirma){
				JsfUtil.cargarObjetoSession("codigoProyecto", "");
				
				proyecto = proyectoFacade.buscarProyectoPorCodigoCompleto(tramite);
				
				List<String[]> lista = proyectoFacade.obtenerProcesoId(proyecto.getCodigo());
				
				for (String[] codigoProyecto : lista) {
					
					idTarea = codigoProyecto[0];
					idProceso = codigoProyecto[2];
					break;
				}
			}		
			
			if(!ingresoFirma){
				JsfUtil.addMessageInfo("Estos Documentos por firmar, corresponden al módulo anterior del SUIA");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean verificaToken() {
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
			e.printStackTrace();
		}
	}
	
	public void seleccionar(ProyectoLicenciamientoAmbiental proyecto){
		try {
			
			JsfUtil.cargarObjetoSession("codigoProyecto", proyecto.getCodigo());
			
			JsfUtil.redirectTo("/prevencion/certificadoambiental/firmarCertificado.jsf");			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String firmarDocumento() {
		try {
			creaDocumento();
			
//			if(documentoCertificado != null) {
//				String documentOffice = documentosFacade.direccionDescarga(documentoCertificado);
//				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre()); 
//			}
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del certificado", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}	
	
	private void creaDocumento(){
		try {
			
			if(documentoCertificado != null && !documentoCertificado.getUsuarioCreacion().equals(loginBean.getUsuario().getNombre())){
				documentoCertificado = null;
			}
			
			if(documentoCertificado == null || documentoCertificado.getId() == null){				
				
				if(proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("PC") || proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("OT") || 
						proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("Zonales") || proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("DP")){
					
					List<Documento> documentos=new ArrayList<Documento>();//documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(),ProyectoLicenciamientoAmbiental.class.getSimpleName(),TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE);
					if(documentos==null || documentos.size()==0)
					{	
						generarCertificado();
												
						if(notificaciones.isEmpty()){
							documentos=documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(),ProyectoLicenciamientoAmbiental.class.getSimpleName(),TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE);
							documentosFacade.guardarDocumentoTareaProceso(documentos.get(0), Long.valueOf(idProceso), Long.valueOf(idTarea));
						}else{
							return;
						}
						
					}
					documentoCertificado=documentos.get(0);
				}else{
					List<Documento> documentos=null;
					if(documentos==null || documentos.size()==0)
					{	
						String errorCertificado =crearCertificadoRegistroAmbiental(proyecto, loginBean.getUsuario().getNombre(), false);
						if(errorCertificado!=null)
						{
							JsfUtil.addMessageError("Error al descargar el Certificado Ambiental. Comuníquese con mesa de ayuda");
							LOG.error(errorCertificado);
							return;
						}
						
						documentos=documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(),ProyectoLicenciamientoAmbiental.class.getSimpleName(),TipoDocumentoSistema.TIPO_CERTIFICADO_CATEGORIA_UNO);
						documentosFacade.guardarDocumentoTareaProceso(documentos.get(0), Long.valueOf(idProceso), Long.valueOf(idTarea));
						
					}				
					documentoCertificado=documentos.get(0);
				}				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generarCertificado(){
		try {
			
			String identificacionUsuario = proyecto.getUsuario().getNombre();
			String cargo = "";
			String direccion = "";
			String correo = "";
			String telefono = "";
			String celular = "";
			String nombreEmpresa = "";
			String representante = "";
//			personaJuridica = false;
			
			if(identificacionUsuario.length() == 10){
				cargo = proyecto.getUsuario().getPersona().getTitulo();				
				
				List<Contacto> contacto = contactoFacade.buscarPorPersona(proyecto.getUsuario().getPersona());
				
				for (Contacto con : contacto) {
	                if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
	                    direccion = con.getValor();
	                }
	                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
	                    correo = con.getValor();
	                }
	                if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
	                    telefono = con.getValor();
	                }
	                if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
	                    celular = con.getValor();
	                }
	            }
				
				nombreEmpresa = proyecto.getUsuario().getPersona().getNombre();
				representante = nombreEmpresa;
				
			}else{
				Organizacion organizacion = organizacionFacade.buscarPorRuc(identificacionUsuario);
				
				if(organizacion != null){
					cargo = organizacion.getCargoRepresentante();
					
					List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(organizacion);
					
					for (Contacto con : contacto) {
		                if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
		                    direccion = con.getValor();
		                }
		                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
		                    correo = con.getValor();
		                }
		                if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
		                    telefono = con.getValor();
		                }
		                if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
		                    celular = con.getValor();
		                }
		            }
					
					nombreEmpresa = organizacion.getNombre();
					representante = organizacion.getPersona().getNombre();
//					personaJuridica = true;
				}else{
					
					cargo = proyecto.getUsuario().getPersona().getTitulo();				
					
					List<Contacto> contacto = contactoFacade.buscarPorPersona(proyecto.getUsuario().getPersona());
					
					for (Contacto con : contacto) {
		                if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
		                    direccion = con.getValor();
		                }
		                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
		                    correo = con.getValor();
		                }
		                if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
		                    telefono = con.getValor();
		                }
		                if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
		                    celular = con.getValor();
		                }
		            }
					
					nombreEmpresa = proyecto.getUsuario().getPersona().getNombre();
					representante = nombreEmpresa;					
				}				
			}		
			
			
			EntityCertificadoAmbientalAmb entidad = new EntityCertificadoAmbientalAmb();
			
			entidad.setActividad(proyecto.getCatalogoCategoria().getDescripcion());
			entidad.setCedulausu(proyecto.getUsuario().getPersona().getPin());
			entidad.setCodigoproy(proyecto.getCodigo());
			entidad.setDireccion(proyecto.getDireccionProyecto());
			entidad.setDireccionpromotor(direccion);
			entidad.setEmailpromotor(correo);
			entidad.setFechaActual(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
			
			String ubicacionCompleta = "";
			ubicacionCompleta = "<table>";

			ubicacionCompleta += "<tr><td>PROVINCIA</td><td>CANTÓN</td><td>PARROQUIA</td></tr>";
			for (ProyectoUbicacionGeografica ubicacionActual : proyecto.getProyectoUbicacionesGeograficas()) {
				
				ubicacionCompleta += "<tr><td>" + ubicacionActual.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td><td>"
						+ ubicacionActual.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td><td>"
						+ ubicacionActual.getUbicacionesGeografica().getNombre() + "</td></tr>";								
			}

			ubicacionCompleta += "</table>";
			
			entidad.setUbicacionProyecto(ubicacionCompleta);
			
			String rol = "AUTORIDAD AMBIENTAL";
			
			List<Usuario> usuarios = new ArrayList<Usuario>();
			if(proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("OT")){
				usuarios=usuarioFacade.buscarUsuarioPorRolNombreArea(rol, proyecto.getAreaResponsable().getArea().getAreaName());
			}else{
				usuarios=usuarioFacade.buscarUsuarioPorRolNombreArea(rol, proyecto.getAreaResponsable().getAreaName());
			}			
			
			if(usuarios != null && !usuarios.isEmpty()){
				entidad.setNombreAutoridad(usuarios.get(0).getPersona().getNombre());
			}else{
				notificaciones.add("No existe usuario AUTORIDAD AMBIENTAL");
				return;
			}
			
			entidad.setNombreDireccionProvincial("DIRECCION ZONAL");
			
			entidad.setNombreProyecto(proyecto.getNombre());
			entidad.setNombreRepresentanteLegal(representante);
			entidad.setNumeroResolucion(secuenciasFacade.getSecuenciaResolucionAreaResponsableNuevoFormato(proyecto.getAreaResponsable()));
			entidad.setOperador(nombreEmpresa);
			entidad.setTelefonopromotor(telefono);
			
			List<String> lista = getCodigoQrUrl(proyecto.getCodigo(), GenerarQRCertificadoAmbiental.tipo_suia_iii); 
			entidad.setCodigoQrFirma(lista.get(1));
			
			UbicacionesGeografica parroquia = proyectoFacade.getUbicacionProyectoPorIdProyecto(proyecto.getId());
			if(proyecto.getAreaResponsable().getTipoArea().getSiglas().equals("OT")){
		        // incluir informacion de la sede de la zonal en el documento
				ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
				String sedeZonal = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarios.get(0), "PROYECTOSUIAIII", null, proyecto, null);
				entidad.setProvincia(sedeZonal);
			}else{
				entidad.setProvincia(parroquia.getUbicacionesGeografica().getNombre());
			}
			
			nombreFichero = "CertificadoAmbiental" + proyecto.getCodigo()+".pdf";
			nombreReporte = "CertificadoAmbiental.pdf";
			
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE);			
			
			File informePdf = UtilGenerarInforme.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					nombreReporte, true, entidad);
					

			Path path = Paths.get(informePdf.getAbsolutePath());
			String reporteHtmlfinal = nombreReporte;
			archivoInforme = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(Files.readAllBytes(path));
			file.close();
						
			TipoDocumento tipoDoc = new TipoDocumento();
			
			tipoDoc.setId(TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE.getIdTipoDocumento());
			
			Documento documento = new Documento();
			documento.setNombre(nombreReporte);
			documento.setExtesion(".pdf");
			documento.setTipoContenido("application/pdf");			
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
			documento.setTipoDocumento(tipoDoc);
			documento.setIdTable(proyecto.getId());
			documento.setCodigoPublico(entidad.getNumeroResolucion());
			
			documento = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigo(), "Certificado Ambiental", Long.valueOf(idProceso), documento, TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE, null);
			
			documento.setUsuarioCreacion(usuarios.get(0).getNombre());
			documentosFacade.actualizarDocumento(documento);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
	
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (documentoDescargado == true) {
			documentoManual=new Documento();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");		
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(ProyectoCertificadoAmbiental.class.getSimpleName());
			documentoManual.setIdTable(proyecto.getId());
			documentoSubido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}

	}

	public StreamedContent descargarDocumento() throws Exception {
		
		creaDocumento();
		
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documentoCertificado != null) {
			if (documentoCertificado.getContenidoDocumento() == null) {
				documentoCertificado.setContenidoDocumento(documentosFacade.descargar(documentoCertificado.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documentoCertificado.getContenidoDocumento()), documentoCertificado.getExtesion());
			content.setName(documentoCertificado.getNombre());
		}
		
		documentoDescargado = true;
		return content;
	}
	
	public void completarTarea(){
		try {
			
			if (token) {
				String idAlfrescoInforme = documentoCertificado.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				}				
			} else {
				if (!documentoSubido) {
					JsfUtil.addMessageError("Debe adjuntar el certificado ambiental firmado.");
					return;
				} else  {			
					documentoCertificado = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigo(), "Certificado Ambiental", Long.valueOf(idProceso), documentoManual, TipoDocumentoSistema.CERTIFICADO_AMBIENTAL_MAE, null);				
				
				}
			}
			
			Map<String, Object> parametros = new HashMap<>();
			
			procesoFacade.modificarVariablesProceso(proyecto.getUsuario(), Long.parseLong(idProceso), parametros);
					
		     procesoFacade.aprobarTarea(proyecto.getUsuario(), Long.parseLong(idTarea), Long.parseLong(idProceso), null);
		        
			notificacion();
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/prevencion/certificadoambiental/listaCertificados.jsf");
		        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void notificacion(){
		try {
			Object[] parametrosCorreoTecnicos = new Object[] {proyecto.getUsuario().getPersona().getNombre() };
			
			String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
					"bodyDocumentoCertificadoFirmadoParaOperador",
					parametrosCorreoTecnicos);	
			
			List<Contacto> contactos = proyecto.getUsuario().getPersona().getContactos();
			String emailDestino = "";
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()){
					emailDestino=contacto.getValor();
					break;
				}
			}		
						
			notificacion = notificacion.replace("nombre_operador", proyecto.getUsuario().getPersona().getNombre());

			NotificacionAutoridadesController email = new NotificacionAutoridadesController();
			email.sendEmailInformacionProponente(emailDestino, "", notificacion, "Notificación Certificado Ambiental Firmado", proyecto.getCodigo(), proyecto.getUsuario(), loginBean.getUsuario());	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void validarTareaBpm() {
		
	}
	
	public String crearCertificadoRegistroAmbiental(ProyectoLicenciamientoAmbiental proyecto, String userName, boolean marcaAgua) {
		
		if (proyecto != null) {
			try {
				Usuario usuario = proyectoLicenciamientoAmbientalFacade.getRepresentanteProyecto(proyecto.getId());
				proyecto = proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(proyecto.getId());
				String direccionPlantillaJasperReport = "prevencion/recibirCertificadoRegistroAmbiental";

				List<String> imagenesReporte = new ArrayList<String>();
				List<String> subreportes = new ArrayList<String>();
				List<String> subreportesUrl = new ArrayList<String>();
				String cargo="AUTORIDAD AMBIENTAL";
				byte[] firma_area = null;
				byte[] logo_area = null;
				byte[] pie_area = null;
			
				List<Usuario> usuario_area =null;
				String nombre_usuario = "";
				String cedula_usuario = "";

//				String firma_mae= "firma__"+ proyecto.getAreaResponsable().getAreaAbbreviation().replace("/", "_") + ".png";
				if (proyecto.getCatalogoCategoria().getTipoArea().getId()==3 && proyecto.getAreaResponsable().getTipoArea().getId()==3){
						
					String logo="logo__"+ proyecto.getAreaResponsable().getAreaAbbreviation() + ".png";
					String pie="pie__"+ proyecto.getAreaResponsable().getAreaAbbreviation() + ".png";
					logo_area = documentosFacade.descargarDocumentoPorNombre(logo);
					pie_area = documentosFacade.descargarDocumentoPorNombre(pie);
					
//					firma_area = documentosFacade.descargarDocumentoPorNombre(firma_mae);
					usuario_area=usuarioFacade.buscarUsuarioPorRolNombreArea(cargo, proyecto.getAreaResponsable().getAreaName());
					if(usuario_area==null || usuario_area.size()==0)
						return "Error al buscar usuario Autoridad en: "+proyecto.getAreaResponsable().getAreaName()+".";					
					nombre_usuario=usuario_area.get(0).getPersona().getNombre()+ "\n"+ proyecto.getAreaResponsable().getAreaName();
					cedula_usuario= usuario_area.get(0).getNombre();
				}else{
					imagenesReporte.add("logo_mae.png");
					imagenesReporte.add("fondo-documentos.png");

					direccionPlantillaJasperReport = "prevencion/recibirCertificadoRegistroAmbientalMae";
//					firma_area = documentosFacade.descargarDocumentoPorNombre(firma_mae);
					usuario_area = usuarioFacade.buscarUsuarioPorRolNombreArea(cargo, proyecto.getAreaResponsable().getAreaName());						
					if(usuario_area==null || usuario_area.size()==0)
						return "Error al buscar usuario Autoridad en: "+proyecto.getAreaResponsable().getAreaName()+".";
					nombre_usuario = usuario_area.get(0).getPersona().getNombre()+ "\n"+ cargo;
					cedula_usuario= usuario_area.get(0).getNombre();
				}

//				InputStream isFirmaSubsecretario = new ByteArrayInputStream(firma_area);
				InputStream islogo_area = null;
				InputStream ispie_area =null;
				if (proyecto.getCatalogoCategoria().getTipoArea().getId()==3 && proyecto.getAreaResponsable().getTipoArea().getId()==3){
					islogo_area = new ByteArrayInputStream(logo_area);
					ispie_area = new ByteArrayInputStream(pie_area);	
				}

				byte[] borrador;
				InputStream isborrador;
				borrador = documentosFacade.descargarDocumentoPorNombre("borrador"+(marcaAgua?"":"vacio")+".png");
				isborrador = new ByteArrayInputStream(borrador);
				subreportes.add("subreporteUbicacionesGeograficas");
				subreportesUrl.add("prevencion/verRegistroProyecto_listaUbicaciones");
				if (usuario != null) {
					if (proyecto != null) {
						Map<String, Object> parametrosReporte = new ConcurrentHashMap<String, Object>();

						parametrosReporte.put("proyecto", proyecto);

						String direccionRepresentanteLegal = "";
						String telefonoRepresentanteLegal = "";
						String emailRepresentanteLegal = "";
						String cedulaRepresentanteLegal = "";
						String entidadResponsable = "";
						String localizacionEntidadResponsable = "";

						Area area = proyecto.getAreaResponsable();

						if (usuario.getPersona() != null) {
							List<Contacto> listContacto= new ArrayList<Contacto>();
							listContacto= contactoFacade.buscarUsuarioNativeQuery(userName);
							for (Contacto contacto : listContacto) {
								if(contacto.getFormasContacto().getId()==FormasContacto.DIRECCION)
									direccionRepresentanteLegal=contacto.getValor();
								if(contacto.getFormasContacto().getId()==FormasContacto.TELEFONO)
									telefonoRepresentanteLegal=contacto.getValor();
								if(contacto.getFormasContacto().getId()==FormasContacto.EMAIL)
									emailRepresentanteLegal=contacto.getValor();
							}

							String cedula = categoria1Facade.determinarCedulaRepresentanteLegal(usuario);
							if (cedula != null)
								cedulaRepresentanteLegal = cedula;
						}

						if (area != null)
							entidadResponsable = area.getAreaName();
						
						UbicacionesGeografica parroquia = proyectoLicenciamientoAmbientalFacade.getUbicacionProyectoPorIdProyecto(proyecto.getId());

				        	// incluir informacion de la sede de la zonal en el documento
						ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
						String sedeZonal = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuario_area.get(0), "PROYECTOSUIAIII", null, proyecto, null);
						localizacionEntidadResponsable  = sedeZonal;
						//localizacionEntidadResponsable = parroquia.getUbicacionesGeografica().getNombre();
						
//						if (area != null && area.getUbicacionesGeografica() != null && area.getUbicacionesGeografica().getUbicacionesGeografica() != null)
//							localizacionEntidadResponsable = area.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();

//						if (isFirmaSubsecretario!=null){
							if (imagenesReporte.size()<=0){
								if (islogo_area !=null && ispie_area!=null){
									parametrosReporte.put("urlImagen-0", islogo_area);
									parametrosReporte.put("urlImagen-1", ispie_area);
								}else{
								return "Falta el logo de area.";
								}
							}
								
//						parametrosReporte.put("urlImagen-2", isFirmaSubsecretario);
						parametrosReporte.put("subsecretario", nombre_usuario);
						parametrosReporte.put("direccionRepresentanteLegal",direccionRepresentanteLegal);
						parametrosReporte.put("telefonoRepresentanteLegal",telefonoRepresentanteLegal);
						parametrosReporte.put("emailRepresentanteLegal",emailRepresentanteLegal);
						parametrosReporte.put("cedulaRepresentanteLegal",cedulaRepresentanteLegal);
						parametrosReporte.put("entidadResponsable",entidadResponsable);
						parametrosReporte.put("localizacionEntidadResponsable",localizacionEntidadResponsable);
						parametrosReporte.put("numeroResolucionCertificado",secuenciasFacade.getSecuenciaResolucionAreaResponsableNuevoFormato(area));
						parametrosReporte.put("urlImagen-3", isborrador);
						parametrosReporte.put("direccionMae","");
						String codigoProyecto = proyecto.getCodigo();

						long idTarea = proyecto.getId();
						String nombreProcesoDirectorioGuardar = Constantes.CARPETA_CATEGORIA_UNO;
						String nombreProcesoConcatenarNombreFichero = "Categoría Uno";
						String nombreFichero = "Certificado.pdf";
						String mime = "application/pdf";
						String extension = ".pdf";
						Integer idTabla = proyecto.getId();
						String nombreTabla = ProyectoLicenciamientoAmbiental.class.getSimpleName();
						TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_CATEGORIA_UNO;
						
						
						List<String> lista = getCodigoQrUrl(codigoProyecto, GenerarQRCertificadoAmbiental.tipo_suia_iii); 
						
						parametrosReporte.put("urlImagen-4", lista.get(1));						

						Documento resultado = reportesFacade
								.generarReporteGuardarAlfresco(
										parametrosReporte,
										direccionPlantillaJasperReport,
										subreportes, subreportesUrl,
										imagenesReporte, idTarea,
										codigoProyecto, idTarea,
										nombreProcesoDirectorioGuardar,
										nombreProcesoConcatenarNombreFichero,
										nombreFichero, mime, extension,
										idTabla, nombreTabla, tipoDocumento);

						if (resultado != null) {

							List<CertificadoRegistroAmbiental> certificados = categoria1Facade.getCertificadoRegistroAmbientalPorIdProyecto(proyecto
									.getId());
							if (certificados != null) {
								for (int i = 0; i < certificados.size(); i++) {
									categoria1Facade.guardarRegistro(certificados.get(i));
								}
							}

							CertificadoRegistroAmbiental certificadoRegistroAmbiental = new CertificadoRegistroAmbiental();
							certificadoRegistroAmbiental.setDocumento(resultado);
							certificadoRegistroAmbiental.setProyecto(proyecto);

							categoria1Facade.guardarRegistro(certificadoRegistroAmbiental);
							
							DocumentoProyecto documentoProyecto = new DocumentoProyecto();
						    documentoProyecto.setDocumento(resultado);
						    documentoProyecto.setProyectoLicenciamientoAmbiental(proyecto);							    
						    
						    categoria1Facade.guardarRegistro(documentoProyecto);
						    
						    resultado.setUsuarioCreacion(cedula_usuario);
						    documentosFacade.actualizarDocumento(resultado);
						}else {
							return "Error al Generar Documento.";
						}
//						} else {
//							return "Error de firma.";
//						}
					}
				}else {
					return "Error al buscar usuario representante.";
				}
				
				//Certificado generado correctamente (retorna error null)
				return null;
			} catch (Exception e) {
				LOG.error("Error al completar la tarea", e);
			}
		}
		return "Error al descargar el Certificado Ambiental. Comuníquese con mesa de ayuda";

	}
	
	public List<String> getCodigoQrUrl(String tramite, Integer tipo) {
		
		List<String> resultado = GenerarQRCertificadoAmbiental.getCodigoQrUrl(true, 
						tramite, tipo);
		return resultado;
	}
	
}
