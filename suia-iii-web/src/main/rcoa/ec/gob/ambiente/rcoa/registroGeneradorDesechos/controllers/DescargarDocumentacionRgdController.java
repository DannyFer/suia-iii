package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProyectoAsociadoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PermisoRegistroGeneradorDesechosFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PermisoRegistroGeneradorDesechos;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DescargarDocumentacionRgdController {

	@EJB
	private SurveyResponseFacade surveyResponseFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcoaFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
	private PermisoRegistroGeneradorDesechosFacade permisoRegistroGeneradorDesechosFacade;
	@EJB
	private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorFacade;
	@EJB
	private ProyectoAsociadoDigitalizacionFacade proyectoAsociadoFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;

	/***************************************************************************************************/
	

	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoSuia;
	/****************************************************************************************************/
	
	@Getter
	@Setter
	public static String surveyLink = Constantes.getPropertyAsString("suia.survey.link");

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{datosOperadorRgdBean}")
	private DatosOperadorRgdBean datosOperadorRgdBean;

	@Getter
	@Setter
	private String tramite;
	@Getter
	@Setter
	private String urlLinkSurvey;

	@Getter
	@Setter
	private boolean mostrarEncuesta = true;

	@Getter
	@Setter
	private boolean showSurveyD = false;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;

	private Map<String, Object> variables;

	byte[] byteGuiasAlmacenamiento, byteGuiasEtiquetado;

	@Getter
	@Setter
	private boolean descargaGuiasAlmacenamiento = false;

	@Getter
	@Setter
	private boolean descargaGuiasEtiquetado = false;

	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;

	@Getter
	@Setter
	private DocumentosRgdRcoa documentoGenerador;

	@Getter
	@Setter
	private DocumentosRgdRcoa documentoOficioGenerador;

	@Getter
	@Setter
	private RegistroGeneradorDesechosRcoa registro;

	@Getter
	@Setter
	private String codigo, codigoTramiteRgd, urlVerTramite, nombreGuiaAlmacenamiento, tipoPermisoRGD;

	@Getter
	@Setter
	private boolean descargarDocumentos = false;

	@Getter
	@Setter
	private boolean documentoGeneradorDescargado = false;
	
	@Getter	
	@Setter
	private boolean documentoOficioDescargado = false;
	
	@Getter
	@Setter
	private PermisoRegistroGeneradorDesechos registroGenerador;
	
	@Getter
	@Setter
	private List<DocumentosRgdRcoa> listaDocumentos;
	
	@Getter
	@Setter
	private boolean provicional = false;
	private TipoDocumentoSistema oficioPronunciamiento, documentoRegistroGenerador;
	private Integer proyectoId;
	
	@PostConstruct
	public void init() {
		try {
			registro = new RegistroGeneradorDesechosRcoa();
			nombreGuiaAlmacenamiento="GUÍA REFERENCIAL DE ALMACENAMIENTO DE RESIDUOS O DESECHOS PELIGROSOS.pdf";
			urlVerTramite ="/pages/rcoa/generadorDesechos/informacionRegistroGeneradorVer.jsf";
			oficioPronunciamiento = TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO;
			documentoRegistroGenerador = TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS;
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			tipoPermisoRGD =(String)variables.get(Constantes.VARIABLE_TIPO_RGD);
			proyectoId = Integer.valueOf(((String)variables.get("idProyectoDigitalizacion") == null)?"0":(String)variables.get("idProyectoDigitalizacion"));
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_AAA)){
				oficioPronunciamiento = TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO_AAA;
				documentoRegistroGenerador = TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS_AAA;
				// si es de digitalizacion ya no busco el proyecto aosciado
				if(proyectoId > 0)
					registro = registroGeneradorFacade.buscarRGDPorProyectoDigitalizado(proyectoId);
			}else if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
				datosOperadorRgdBean.setTitulo("Responsable o representante de la empresa");
				urlVerTramite ="/pages/rcoa/generadorDesechos/informacionRegistroGeneradorVerREP.jsf";
				registro = registroGeneradorDesechosRcoaFacade.buscarRGDPorCodigo(tramite);
				oficioPronunciamiento = TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO_REP;
				documentoRegistroGenerador = TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS_REP;
			}
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			List<RegistroGeneradorDesechosProyectosRcoa> registroGeneradorDesechosProyectosRcoas = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
			if(proyecto.getId() != null){
				registroGeneradorDesechosProyectosRcoas = registroGeneradorDesechosProyectosRcoaFacade.asociados(proyecto.getId());
				if(proyecto.getTipoProyecto() == 2){
					provicional  = true;
				}
			}else{
				proyectoSuia = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(tramite);
				if(proyectoSuia != null && proyectoSuia.getId() != null){
					registroGeneradorDesechosProyectosRcoas= registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoSuia(proyectoSuia.getId());
					if(proyectoSuia.getCatalogoCategoria().getCategoria().getId().equals(Categoria.CATEGORIA_III) || proyectoSuia.getCatalogoCategoria().getCategoria().getId().equals(Categoria.CATEGORIA_IV)){
						provicional = true;
					}
				}
			}

			if (registroGeneradorDesechosProyectosRcoas != null
					&& !registroGeneradorDesechosProyectosRcoas.isEmpty()) {
				registro = registroGeneradorDesechosProyectosRcoas.get(0)
						.getRegistroGeneradorDesechosRcoa();
				
				codigoTramiteRgd = registro.getCodigo();
			}		
			if (registro != null && registro.getUsuario() != null){
				datosOperadorRgdBean.buscarDatosOperador(registro.getUsuario());
				codigoTramiteRgd = registro.getCodigo();
			}
			verEncuesta();

			byteGuiasAlmacenamiento = documentosRgdRcoaFacade
					.descargarDocumentoPorNombre(nombreGuiaAlmacenamiento);
			
			List<DocumentosRgdRcoa> documentoGeneradorList = documentosRgdRcoaFacade
					.descargarDocumentoRgd(
							registro.getId(),
							RegistroGeneradorDesechosProyectosRcoa.class.getSimpleName(),
							documentoRegistroGenerador);
			if (documentoGeneradorList != null
					&& !documentoGeneradorList.isEmpty()) {
				documentoGenerador = documentoGeneradorList.get(0);
			}

			List<DocumentosRgdRcoa> documentosOficiosList = documentosRgdRcoaFacade
					.descargarDocumentoRgd(
							registro.getId(),
							RegistroGeneradorDesechosProyectosRcoa.class.getSimpleName(),
							oficioPronunciamiento);

			if (documentosOficiosList != null
					&& !documentosOficiosList.isEmpty()) {
				documentoOficioGenerador = documentosOficiosList.get(0);
			}
			
			List<PermisoRegistroGeneradorDesechos> permisoList = new ArrayList<PermisoRegistroGeneradorDesechos>();
			registroGenerador = new PermisoRegistroGeneradorDesechos();
			permisoList = permisoRegistroGeneradorDesechosFacade.findByRegistroGenerador(registro.getId());
			
			if(permisoList != null && !permisoList.isEmpty()){
				registroGenerador = permisoList.get(0);
				codigo = registroGenerador.getNumeroDocumento();
			}else{
				codigo = "";
			}
			
			List<DesechosRegistroGeneradorRcoa> listaDesechos = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGeneradorGenerados(registro);
			listaDocumentos = new ArrayList<DocumentosRgdRcoa>();
			
			for(DesechosRegistroGeneradorRcoa desecho : listaDesechos){
				boolean existeDocumento = false;
				try{
					
					String clave = desecho.getDesechoPeligroso().getClave();
					String claveNombre = clave.replace("-", "."); 
					String nombreDocumento = claveNombre + ".pdf";
					
					byteGuiasEtiquetado = documentosRgdRcoaFacade.descargarDocumentoPorNombre(nombreDocumento);
					existeDocumento = true;
				}catch(Exception ex){
					existeDocumento = false;
				}
				if(existeDocumento){
					DocumentosRgdRcoa documento = new DocumentosRgdRcoa();				
					documento.setContenidoDocumento(byteGuiasEtiquetado);
					documento.setNombre(desecho.getDesechoPeligroso().getClave());
					documento.setDescripcion(desecho.getDesechoPeligroso().getClave() + "-" + desecho.getDesechoPeligroso().getDescripcion());
					listaDocumentos.add(documento);
				}
			}			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean verEncuesta() {
		/*if(!surveyResponseFacade.findByProjectApp(tramite, "registro-generador-rcoa")){
			mostrarEncuesta = true;
		} else {
			mostrarEncuesta = false;
		}*/
		// para que ya no  aparezca la encuesta
		mostrarEncuesta = false;
		return mostrarEncuesta;
	}

	public void showSurvey() {

		String url = surveyLink;
		String usuarioUrl = loginBean.getNombreUsuario();
		String proyectoUrl = tramite;
		String appUlr = "registro-generador-rcoa";
		String tipoPerUrl = getProponente();
		String tipoUsr = "externo";
		url = url + "/faces/index.xhtml?" + "usrid=" + usuarioUrl + "&app="
				+ appUlr + "&project=" + proyectoUrl + "&tipoper=" + tipoPerUrl
				+ "&tipouser=" + tipoUsr;
		System.out.println("enlace>>>" + url);
		urlLinkSurvey = url;
		showSurveyD = true;
	}

	private String getProponente() {
		try {
			Usuario user = loginBean.getUsuario();
			if (user.getNombre().length() == 13) {
				Organizacion orga = organizacionFacade.buscarPorRuc(user
						.getNombre());
				if (orga != null)
					return "juridico";
			}
			return "natural";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public StreamedContent getGuiasAlmacenamiento() {
		try {
			if (byteGuiasAlmacenamiento != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(byteGuiasAlmacenamiento),
						"application/pdf",
						nombreGuiaAlmacenamiento);
				descargaGuiasAlmacenamiento = true;
				verDocumentos();
				return streamedContent;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar la guía referencial de almacenamiento.");
			e.printStackTrace();
		}
		JsfUtil.addMessageError("No se pudo descargar la guía referencial de almacenamiento.");
		return null;
	}

	public StreamedContent getGuiasEtiquetado() {
		try {
			if (byteGuiasEtiquetado != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(byteGuiasEtiquetado),
						"application/pdf",
						"Guias Buenas Practicas Ambientales.pdf");
				descargaGuiasEtiquetado = true;
				verDocumentos();
				return streamedContent;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar las guías de buenas prácticas ambientales.");
			e.printStackTrace();
		}
		JsfUtil.addMessageError("No se pudo descargar las guías de buenas prácticas ambientales.");
		return null;
	}

	public StreamedContent getDocumentoOficio() {
		try {
			if(descargarDocumentos){
				if (documentoOficioGenerador != null
						&& documentoOficioGenerador.getContenidoDocumento() != null) {
					StreamedContent streamedContent = new DefaultStreamedContent(
							new ByteArrayInputStream(
									documentoOficioGenerador
											.getContenidoDocumento()),
							documentoOficioGenerador.getMime(),
							documentoOficioGenerador.getNombre());
					
					documentoOficioDescargado = true;
					return streamedContent;
				}
			}else{
				JsfUtil.addMessageError("Antes de descargar el Registro de Generador obtenido y el oficio de pronunciamiento, usted debe descargar tanto las etiquetas como la Guía referencial de almacenamiento");
			}		

		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar Documento Oficio de Registro Generador");
			e.printStackTrace();
		}

		return null;
	}

	public StreamedContent getDocumentoRegistroGenerador() {
		try {
			if(descargarDocumentos){
				if (documentoGenerador != null
						&& documentoGenerador.getContenidoDocumento() != null) {
					StreamedContent streamedContent = new DefaultStreamedContent(
							new ByteArrayInputStream(
									documentoGenerador.getContenidoDocumento()),
							documentoGenerador.getMime(),
							documentoGenerador.getNombre());							
					
					documentoGeneradorDescargado = true;
					return streamedContent;
				}
			}else{
				JsfUtil.addMessageError("Antes de descargar el Registro de Generador obtenido y el oficio de pronunciamiento, usted debe descargar tanto las etiquetas como la Guía referencial de almacenamiento");
			}		

		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar Documento Registro Generador");
			e.printStackTrace();
		}

		return null;
	}

	public void verDocumentos() {
		if (descargaGuiasAlmacenamiento && descargaDesechos()) {
			descargarDocumentos = true;
		}
		
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.update(":form:btnRgd");
	}
	
	
	public void finalizar(){
		try {	
			if(documentoGeneradorDescargado && documentoOficioDescargado){
				// copio los archivos descargados de etiquetas y guias REP
				if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
					coiparArchivosDescargados();
				}
				
				registro.setFinalizado(true);
				registroGeneradorDesechosRcoaFacade.save(registro, loginBean.getUsuario());

				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				//si proviene de un proyecto de digitalizacion vinculo el rgd al proyecto digitalizado
				if(proyectoId > 0){
					guardarOtrosProyectos();
				}
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	private void guardarOtrosProyectos(){
		try {
			//valido que el proyecto no este ya asociado 
			List<ProyectoAsociadoDigitalizacion> listaDigitalizacion= proyectoAsociadoFacade.buscarProyectoAsociado(registro.getId());
			if(listaDigitalizacion != null && listaDigitalizacion.size() > 0){
				return;
			}
			ProyectoAsociadoDigitalizacion proyectoAsociado = new ProyectoAsociadoDigitalizacion();
			AutorizacionAdministrativaAmbiental autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(proyectoId);
			proyectoAsociado.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
			proyectoAsociado.setProyectoAsociadoId(registro.getId());
			proyectoAsociado.setTipoProyecto(4);
			proyectoAsociado.setNombreTabla("coa_waste_generator_record.waste_generator_record_coa");
			proyectoAsociadoFacade.guardar(proyectoAsociado, loginBean.getUsuario());
		} catch (Exception e) {
			
		}
	}
	
	public void coiparArchivosDescargados(){
		//copio la guia de almacenamiento
		int procesoId = (int)bandejaTareasBean.getProcessId();
		TipoDocumento tipoDoc = new TipoDocumento();
		// verifivo si ya esta copiado el archivo de guias, si no esta lo copio
		List<DocumentosRgdRcoa> documentoGuia = documentosRgdRcoaFacade.descargarDocumentoRgd(
						registro.getId(),RegistroGeneradorDesechosProyectosRcoa.class.getSimpleName(),
						TipoDocumentoSistema.RGD_GUIA_REFERENCIAL_ALMACENAMIENTO);
		if (documentoGuia == null || documentoGuia.isEmpty()) {
			tipoDoc.setId(TipoDocumentoSistema.RGD_GUIA_REFERENCIAL_ALMACENAMIENTO.getIdTipoDocumento());
			DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
			documento.setNombre(nombreGuiaAlmacenamiento);
			documento.setContenidoDocumento(byteGuiasAlmacenamiento);
			documento.setTipoDocumento(tipoDoc);
			guardarDocumento(documento, procesoId, TipoDocumentoSistema.RGD_GUIA_REFERENCIAL_ALMACENAMIENTO);
		}

		// verifivo si ya esta copiado el archivo de etiquertas, si no esta lo copio
		List<DocumentosRgdRcoa> documentoEtiquetas = documentosRgdRcoaFacade.descargarDocumentoRgd(
						registro.getId(),RegistroGeneradorDesechosProyectosRcoa.class.getSimpleName(),
						TipoDocumentoSistema.RGD_GUIA_ETIQUETADO);
		if (documentoEtiquetas == null || documentoEtiquetas.isEmpty()) {
			tipoDoc.setId(TipoDocumentoSistema.RGD_GUIA_ETIQUETADO.getIdTipoDocumento());
			for (DocumentosRgdRcoa objDocumento : listaDocumentos) {
				String nombreArchivo = objDocumento.getNombre();
				objDocumento.setNombre(nombreArchivo+".pdf");
				objDocumento.setTipoDocumento(tipoDoc);
				guardarDocumento(objDocumento, procesoId, TipoDocumentoSistema.RGD_GUIA_ETIQUETADO);
				objDocumento.setNombre(nombreArchivo);
			}
		}
	}
	
	public void guardarDocumento(DocumentosRgdRcoa documento, int procesoId, TipoDocumentoSistema tipoDocumento){
		try {
			documento.setExtesion(".pdf");
			documento.setTipoContenido("application/pdf");
			documento.setMime("application/pdf");
			documento.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
			documento.setIdTable(registro.getId());
			documento.setIdProceso(procesoId);
			documento.setRegistroGeneradorDesechosRcoa(registro);//Registro de Generador de Desechos Peligrosos y Especiales
			
			documento = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
					registro.getCodigo(), "GUIASRGD", 0L, documento, tipoDocumento);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CmisAlfrescoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cerrar(){
		if(documentoGeneradorDescargado && documentoOficioDescargado){
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}	
		else{
			JsfUtil.addMessageWarning("Debe descargar los documentos de Registro Generador y Oficio de pronunciamiento");
		}
	}
	
	public StreamedContent getEtiquetado(DocumentosRgdRcoa documento) {
		try {
			
			String clave = documento.getNombre();
			String claveNombre = clave.replace("-", "."); 
			
			String documentoBuscar = claveNombre+".pdf";			
			byte[] guia = documentosRgdRcoaFacade.descargarDocumentoPorNombre(documentoBuscar);
			
			if (guia != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(guia),
						"application/pdf",
						documentoBuscar);
				
				documento.setNombreTabla("DESCARGADO");
				return streamedContent;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar el documento de etiquetas");
			e.printStackTrace();
		}
		return null;
	}	
	
	public boolean descargaDesechos() {

		boolean descargado = true;
		if (listaDocumentos != null && !listaDocumentos.isEmpty()) {
			for (DocumentosRgdRcoa doc : listaDocumentos) {
				if (doc.getNombreTabla() == null
						|| doc.getNombreTabla().isEmpty()) {
					descargado = false;
					break;
				}
			}
		}

		return descargado;
	}

}
