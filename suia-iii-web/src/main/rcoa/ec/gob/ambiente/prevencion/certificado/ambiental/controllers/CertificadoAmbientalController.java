package ec.gob.ambiente.prevencion.certificado.ambiental.controllers;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.Query;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.facade.CertificadoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.CertificadoAmbientalMaeFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCertificadoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.autoridadambiental.service.AutoridadAmbientalFacade;
import ec.gob.ambiente.suia.certificado.ambiental.CertificadoAmbientalMAE;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CertificadoAmbientalController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 243212747354506993L;
	Logger LOG = Logger.getLogger(CertificadoAmbientalController.class);
	
	@EJB
	private CertificadoAmbientalMaeFacade certificadoAmbientalInterseccionMaeFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private CertificadoAmbientalFacade certificadoAmbientalFacade;
	
	@EJB
	DocumentosCertificadoAmbientalFacade documentosCertificadoAmbientalFacade;
	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private DocumentosCoaFacade documentosCoaFacade;
	
	@EJB
	private ContactoFacade contactoFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
	
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@Getter
	@Setter
	private Usuario usuarioAutoridad;
	
	@Getter
	@Setter
	private DocumentoCertificadoAmbiental documentoCertificadoAmbiental;
	
	byte[] byteGuiasRgd;
	
	public String dblinkBpmsSuiaiii =Constantes.getDblinkBpmsSuiaiii();
	
	public File generarLicenciaAmbiental(ProyectoLicenciaCoa proyectoActivo, Integer marcaAgua, long idProceso) {
		try {
			File archivoTemporal = null;
			File archivoLicenciaTemporal = null;
			File archivoAnexoTemporal = null;
			List<File> listaFiles = new ArrayList<File>();
			String[] vc = null;
			byte[] firma = null;
			Persona persona = null;
			
			PlantillaReporte plantillaReporte = new PlantillaReporte();
			TipoDocumentoSistema tipoDocumento;
			
			if(proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("DP") || proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("PC")
					|| proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("ZONALES") || proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("OT")){
				plantillaReporte = certificadoAmbientalInterseccionMaeFacade
						.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_MAE
								.getIdTipoDocumento());	
				tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_MAE;
			}else{
				plantillaReporte = certificadoAmbientalInterseccionMaeFacade
						.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_GAD
								.getIdTipoDocumento());	
				tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_GAD;
			}
			Area areaResponsable = new Area();
			if(proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("OT")){
				areaResponsable = proyectoActivo.getAreaResponsable().getArea();
			}else{
				areaResponsable = proyectoActivo.getAreaResponsable();
			}
						
			String roleKey="role.certificado.autoridad";
			
			if(areaResponsable.getTipoArea().getSiglas().equals("OT")){
				usuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), areaResponsable).get(0);				
			}else if(areaResponsable.getTipoArea().getSiglas().equals("PC")){
				List<Usuario> listaUsuarios = usuarioFacade.buscarUsuarioPorRolActivo(Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental"));				
				if(listaUsuarios != null && !listaUsuarios.isEmpty()){
					usuarioAutoridad = listaUsuarios.get(0);
				}else
					usuarioAutoridad = null;			
			}else{
				usuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), areaResponsable).get(0);				
			}		
			
			proyectoActivo.setProyectoFinalizado(true);
			proyectoActivo.setProyectoFechaFinalizado(new Date());
			proyectoLicenciaCoaFacade.guardar(proyectoActivo);
								
			archivoLicenciaTemporal = CertificadoAmbientalMAE
					.crearCertificadoMae(
							"CertificadoAmbiental",
							"Certificado Ambiental", persona,
							"", areaResponsable,
							marcaAgua, false,proyectoActivo, usuarioAutoridad);
			
			listaFiles.add(archivoLicenciaTemporal);
			
			documentosCertificadoAmbientalFacade.ingresarDocumentoCategoriaII(
					archivoLicenciaTemporal,
					proyectoActivo.getId(),
					proyectoActivo.getCodigoUnicoAmbiental(),
					idProceso,
					0,
					tipoDocumento,
					"Certificado Ambiental");			
			
			enviarMail(proyectoActivo);			
			modificarDocumento(idProceso);
			
			guardarGuias(proyectoActivo, idProceso);
			enviarMailOperador(proyectoActivo);
			
//			proyectoActivo.setProyectoFinalizado(true);
//			proyectoActivo.setProyectoFechaFinalizado(new Date());
//			proyectoLicenciaCoaFacade.guardar(proyectoActivo);
			
			return archivoLicenciaTemporal;
		} catch (Exception e) {
			LOG.error("Error al intentar generar el archivo Certificado Ambiental.",e);
			return null;
		}
	}
	
	
	public String enviarFicha(Integer idProyecto, long idProceso) {
		 try {
			File licenciaTmp = subirLicenciaAmbiental(true,2,idProyecto, idProceso);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return "";
	}

	
	public File subirLicenciaAmbiental(Boolean subir, Integer i, Integer idProyecto, long idProceso) throws Exception {
		try {
			ProyectoLicenciaCoa proyectoActivo = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			File archivoTemporal = generarLicenciaAmbiental(proyectoActivo,i,idProceso);
		} catch (Exception e) {
			LOG.error(
					"Error al recuperar variables y generar Certificado Ambiental",
					e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
		return null;
	}
	
	public void enviarMail(ProyectoLicenciaCoa proyecto){
		try {
			List<String> urls = new ArrayList<>();
			
			DocumentoCertificadoAmbiental documentoCertificado = new DocumentoCertificadoAmbiental();
			String siglas=proyecto.getAreaResponsable().getTipoArea().getSiglas();			
			if(siglas.compareTo("DP")==0 || siglas.compareTo("PC")==0 || siglas.compareTo("ZONALES")==0 || siglas.compareTo("OT")==0){
				documentoCertificado = documentosCertificadoAmbientalFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoCertificadoAmbiental.class.getSimpleName(), TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_MAE).get(0);
			}else{
				documentoCertificado = documentosCertificadoAmbientalFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoCertificadoAmbiental.class.getSimpleName(), TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_GAD).get(0);
			}	
			
			documentoCertificadoAmbiental = documentoCertificado;
			
			String urlCA = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + documentoCertificado.getDescripcion();
			
			urls.add(urlCA + ";"+documentoCertificado.getNombre());
			
			CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
			
			TipoDocumentoSistema tipoDocumentoCertificado = (oficioCI.getNroActualizacion() == 0) ? TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO : TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO_ACTUALIZADO;
			
			List<DocumentosCOA> listaDocumentosInt = documentosCoaFacade.documentoXTablaIdXIdDocXNroActualizacion(oficioCI.getId(), tipoDocumentoCertificado, "CertificadoInterseccionOficioCoa", oficioCI.getNroActualizacion());
							
			if(listaDocumentosInt != null && !listaDocumentosInt.isEmpty()){
				DocumentosCOA documentoCertificadoInterseccion = listaDocumentosInt.get(0);				
				File file = documentosCoaFacade.descargarFile(documentoCertificadoInterseccion);				
				urls.add(file.getPath() + ";"+listaDocumentosInt.get(0).getNombreDocumento());
			}
			
			List<Contacto> listaContactosAutoridad = contactoFacade.buscarPorPersona(usuarioAutoridad.getPersona());
			String emailAutoridad = "";
			for(Contacto contacto : listaContactosAutoridad){
				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
					emailAutoridad = contacto.getValor();
					break;
				}						
			}
			
			Object[] parametrosCorreo = new Object[] {usuarioAutoridad.getPersona().getNombre(), 
					proyecto.getCodigoUnicoAmbiental(), proyecto.getNombreProyecto(), 
					proyecto.getUsuario().getPersona().getNombre()};
			
			String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
							"bodyNotificacionCertificadoAutoridadAmbiental",
							parametrosCorreo);			
			
			NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			mail_a.sendEmailDocumentoAdjunto("Certificado Ambiental Generado", notificacion, emailAutoridad, urls);
			
		} catch (Exception e) {
			LOG.error("Error en envio de notificación", e);
		}		
	}	
	
	public void modificarDocumento(Long idProceso){
		documentoCertificadoAmbiental.setIdProceso(idProceso);
		
		documentosCertificadoAmbientalFacade.guardar(documentoCertificadoAmbiental);
	}
	
	public void guardarGuias(ProyectoLicenciaCoa proyecto, Long idProceso){
		try {
			
			List<DocumentoCertificadoAmbiental> listaDocumentos = new ArrayList<DocumentoCertificadoAmbiental>();
			
			String documento = documentosCertificadoAmbientalFacade.descargarDireccionDocumentoPorNombre("GUIAS_DE_BUENAS_PRACTICAS_AMBIENTALES_CA_RCOA.pdf");
			
			String[] parts = documento.split("node/");
			String part1 = parts[0];
			String part2 = parts[1]; 
			
			String[] workspace = part2.split("/content?");
			String workspaceDoc = workspace[0];		
			
			DocumentoCertificadoAmbiental documentoGuia = new DocumentoCertificadoAmbiental();
			documentoGuia.setAlfrescoId(workspaceDoc);
			documentoGuia.setNombre("GUIAS_DE_BUENAS_PRACTICAS_AMBIENTALES_CA_RCOA.pdf");
			documentoGuia.setMime("application/pdf");
			documentoGuia.setEstado(true);
			documentoGuia.setExtesion(".pdf");
			documentoGuia.setFechaCreacion(new Date());
			documentoGuia.setDescripcion("GUIAS_DE_BUENAS_PRACTICAS_AMBIENTALES_CA_RCOA.pdf");
			documentoGuia.setNombreTabla("ProyectoCertificadoAmbiental");
			documentoGuia.setIdProceso(idProceso);
			TipoDocumento tipoDocumentoG = new TipoDocumento();
			tipoDocumentoG.setId(TipoDocumentoSistema.GUIA_BUENAS_PRACTICAS_AMBIENTALES.getIdTipoDocumento());
			documentoGuia.setTipoDocumento(tipoDocumentoG);
			
			listaDocumentos.add(documentoGuia);
			
			if(proyecto.getGeneraDesechos()){				
				
				String documentoRGD = documentosCertificadoAmbientalFacade.descargarDireccionDocumentoPorNombre("GUÍA REFERENCIAL DE ALMACENAMIENTO DE RESIDUOS O DESECHOS PELIGROSOS.pdf");
				
				String[] partRgd = documentoRGD.split("node/");
				String partRgd1 = partRgd[0];
				String partRgd2 = partRgd[1]; 
				
				String[] workspaceRgd = partRgd2.split("/content?");
				String workspaceRgdDoc = workspaceRgd[0];	
				
				DocumentoCertificadoAmbiental documentoGuiaRgd = new DocumentoCertificadoAmbiental();
				documentoGuiaRgd.setAlfrescoId(workspaceRgdDoc);
				documentoGuiaRgd.setNombre("GUÍA REFERENCIAL DE ALMACENAMIENTO DE RESIDUOS O DESECHOS PELIGROSOS.pdf");
				documentoGuiaRgd.setMime("application/pdf");
				documentoGuiaRgd.setEstado(true);
				documentoGuiaRgd.setExtesion(".pdf");
				documentoGuiaRgd.setFechaCreacion(new Date());
				documentoGuiaRgd.setDescripcion("GUÍA REFERENCIAL DE ALMACENAMIENTO DE RESIDUOS O DESECHOS PELIGROSOS.pdf");
				documentoGuiaRgd.setNombreTabla("ProyectoCertificadoAmbiental");
				documentoGuiaRgd.setIdProceso(idProceso);
				TipoDocumento tipoDocumento = new TipoDocumento();
				tipoDocumento.setId(TipoDocumentoSistema.GUIA_REFERENCIAL_ALMACENAMIENTO_RESIDUOS.getIdTipoDocumento());
				documentoGuiaRgd.setTipoDocumento(tipoDocumento);
				
				listaDocumentos.add(documentoGuiaRgd);
				
				
				List<RegistroGeneradorDesechosProyectosRcoa> registroGeneradorDesechosProyectosRcoas = registroGeneradorDesechosProyectosRcoaFacade.asociados(proyecto.getId());

				RegistroGeneradorDesechosRcoa registro = new RegistroGeneradorDesechosRcoa();
				if (registroGeneradorDesechosProyectosRcoas != null && !registroGeneradorDesechosProyectosRcoas.isEmpty()) {
					registro = registroGeneradorDesechosProyectosRcoas.get(0).getRegistroGeneradorDesechosRcoa();					
				}	
				
				List<DesechosRegistroGeneradorRcoa> listaDesechos = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGeneradorGenerados(registro);
				
				
				for(DesechosRegistroGeneradorRcoa desecho : listaDesechos){
					boolean existeDocumento = false;
					String nombreDocumento = "";
					try{
						String clave = desecho.getDesechoPeligroso().getClave();
						String claveNombre = clave.replace("-", "."); 
						
						nombreDocumento = claveNombre + ".pdf";
						
						byteGuiasRgd = documentosCertificadoAmbientalFacade.descargarDocumentoPorNombre(nombreDocumento);
						existeDocumento = true;
					}catch(Exception ex){
						existeDocumento = false;
					}
					if(existeDocumento){
						
						String documentoDesecho = documentosCertificadoAmbientalFacade.descargarDireccionDocumentoPorNombre(nombreDocumento);
						
						String[] partDesecho = documentoDesecho.split("node/");
						String partDesecho1 = partDesecho[0];
						String partDesecho2 = partDesecho[1]; 
						
						String[] workspaceDesecho = partDesecho2.split("/content?");
						String workspaceDesechoDoc = workspaceDesecho[0];	
						
						
						DocumentoCertificadoAmbiental documentoDesechoInd= new DocumentoCertificadoAmbiental();
						documentoDesechoInd.setAlfrescoId(workspaceDesechoDoc);
						documentoDesechoInd.setNombre(desecho.getDesechoPeligroso().getClave());
						documentoDesechoInd.setMime("application/pdf");
						documentoDesechoInd.setEstado(true);
						documentoDesechoInd.setExtesion(".pdf");
						documentoDesechoInd.setFechaCreacion(new Date());
						documentoDesechoInd.setDescripcion(desecho.getDesechoPeligroso().getClave());
						documentoDesechoInd.setNombreTabla("ProyectoCertificadoAmbiental");
						documentoDesechoInd.setIdProceso(idProceso);
						TipoDocumento tipoDocumentoDI = new TipoDocumento();
						tipoDocumentoDI.setId(TipoDocumentoSistema.DOCUMENTO_DESECHO_PELIGROSO.getIdTipoDocumento());
						documentoDesechoInd.setTipoDocumento(tipoDocumentoDI);
																		
						listaDocumentos.add(documentoDesechoInd);
					}
				}				
			}
			
			if(listaDocumentos != null && !listaDocumentos.isEmpty()){
				for(DocumentoCertificadoAmbiental documentoGuardar : listaDocumentos){
					documentosCertificadoAmbientalFacade.guardar(documentoGuardar);
				}					
			}
			
		} catch (Exception e) {
			LOG.error("Error en guardado de guias", e);
		}
	}
	
	public void enviarMailOperador(ProyectoLicenciaCoa proyecto){
		try {					
			
			Organizacion organizacion = organizacionFacade.buscarPorRuc(proyecto.getUsuario().getNombre());
			
			List<Contacto> listaContactosAutoridad = new ArrayList<Contacto>();
			if(organizacion != null && organizacion.getId() != null){
				listaContactosAutoridad = contactoFacade.buscarPorOrganizacion(organizacion);
			}else{
				listaContactosAutoridad = contactoFacade.buscarPorPersona(proyecto.getUsuario().getPersona());
			}
			
			
			String emailOperador = "";
			for(Contacto contacto : listaContactosAutoridad){
				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
					emailOperador = contacto.getValor();
					break;
				}						
			}
			
			String ente = "";
			String siglas=proyecto.getAreaResponsable().getTipoArea().getSiglas();			
			if(siglas.compareTo("OT")==0){
				ente = proyecto.getAreaResponsable().getArea().getAreaName();
			}else{
				ente = proyecto.getAreaResponsable().getAreaName();
			}
			
			
			Object[] parametrosCorreo = new Object[] {proyecto.getUsuario().getPersona().getNombre(), 
					proyecto.getCodigoUnicoAmbiental(), proyecto.getNombreProyecto(), 
					ente};
			
			String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
							"bodyNotificacionCertificadoOperador",
							parametrosCorreo);			
			
			NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			mail_a.sendEmailInformacionProponente(emailOperador, "MAAETE", notificacion, "Certificado Ambiental Generado", proyecto.getCodigoUnicoAmbiental(), proyecto.getUsuario(), loginBean.getUsuario());			
			
		} catch (Exception e) {
			LOG.error("Error en envio de notificación", e);
		}		
	}	
	
}
