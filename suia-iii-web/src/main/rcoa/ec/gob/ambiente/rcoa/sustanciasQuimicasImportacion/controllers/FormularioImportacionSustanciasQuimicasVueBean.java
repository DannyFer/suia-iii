package ec.gob.ambiente.rcoa.sustanciasQuimicasImportacion.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.dto.EntityFormularioImportacionVueRSQDTO;
import ec.gob.ambiente.rcoa.dto.EntityTablaFormularioImportacionVueRSQDTO;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DetalleSolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.SolicitudImportacionRSQExtVueFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.SolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DetalleSolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class FormularioImportacionSustanciasQuimicasVueBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(FormularioImportacionSustanciasQuimicasVueBean.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{buscarUsuarioBean}")
    private BuscarUsuarioBean buscarUsuarioBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private  UsuarioFacade usuarioFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private DocumentosImpactoEstudioFacade documentosFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@EJB
	private SolicitudImportacionRSQFacade solicitudImportacionRSQFacade;
	@EJB
	private SolicitudImportacionRSQExtVueFacade solicitudImportacionRSQExtVueFacade;
	
	@EJB
	private DetalleSolicitudImportacionRSQFacade detalleSolicitudImportacionRSQFacade;
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporte;

	@Getter
	@Setter
	private InformeTecnicoEsIA informeTecnico;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoInforme, documentoInspeccion;

	@Getter
	@Setter
	private Integer idProyecto, numeroObservaciones, idTarea, tipoInforme;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte;
	
	@Getter
	@Setter
	private byte[] archivoReporte;

	private SolicitudImportacionRSQ solicitud;

	@Getter
	@Setter
	private List<DetalleSolicitudImportacionRSQ> lDetalleSolicitudImportacionRSQ;
	
	@PostConstruct
	public void init() {
		
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			String idImport = params.get("idSolicImportRSQ");
			
			solicitud = solicitudImportacionRSQFacade.buscarPorId(Integer.valueOf(idImport));
			solicitud.setSolicitudImportacionRSQExtVue(solicitudImportacionRSQExtVueFacade.buscarPorId(solicitud.getId()));
			
			lDetalleSolicitudImportacionRSQ = detalleSolicitudImportacionRSQFacade.buscarPorIdSolicitud(solicitud);
			
			urlReporte = JsfUtil.devolverContexto("/reportesHtml/" + "formularioVue");
			
			generarFormularioImportacionVue(Boolean.FALSE);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos Informe.");
		}
	}
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		
		return content;
	}
	
	public void generarFormularioImportacionVue(Boolean marcaAgua) {
		try {
			plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_FORMULARIO_VUE_IMPORTACION_SUSTANCIAS_QUIMICAS);
			
			EntityFormularioImportacionVueRSQDTO entity = cargarDatosInforme();
			entity.setNombreReporte("FormularioImportacionRSQVue_" + UtilViabilidad.getFileNameEscaped(entity.getNumeroSolicitud().replace("/", "-")) + ".pdf");
			
			File formPdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					entity.getNombreReporte(), true, entity);
			
			File formPdf = JsfUtil.fileMarcaAgua(formPdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(formPdf.getAbsolutePath());
			archivoReporte = Files.readAllBytes(path);
			
			String reporteHtmlfinal = entity.getNombreReporte().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(archivoReporte);
			file.close();
			urlReporte = JsfUtil.devolverContexto("/reportesHtml/" + entity.getNombreReporte());
			
			nombreReporte = entity.getNombreReporte();
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Error al cargar el formulario de Importación de Sustancias Químicas", ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public EntityFormularioImportacionVueRSQDTO cargarDatosInforme() throws ServiceException {
		
		EntityFormularioImportacionVueRSQDTO entity = new EntityFormularioImportacionVueRSQDTO();
		
		entity.setNumeroSolicitud(solicitud.getReqNo()); 
		entity.setFechaSolicitud(solicitud.getSolicitudImportacionRSQExtVue().getReqDe()); 
		entity.setCiudadSolicitud(solicitud.getSolicitudImportacionRSQExtVue().getReqCityNm());
		entity.setFechaAprobacion(solicitud.getSolicitudImportacionRSQExtVue().getApvDe());
		entity.setFechaInicioVigenciaLicencia(solicitud.getSolicitudImportacionRSQExtVue().getLicIniDe());
		entity.setFechaFinVigenciaLicencia(solicitud.getSolicitudImportacionRSQExtVue().getLicExpDe());
		entity.setClasificacionSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrClNm());
		entity.setTipoSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrIdtNoTypeNm());
		entity.setNumeroIdentificacionSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrIdtNo());
		entity.setNombreRazonSocialSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrCmpNm());
		entity.setNombreSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrNm());
		entity.setProvinciaSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrPrvhcNm());
		entity.setCantonSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrCutyNm());
		entity.setParroquiaSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrPrqiNm());
		entity.setDireccionSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrAd());
		entity.setTelefonoSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrTelNo());
		entity.setCelularSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrCelNo());
		entity.setCorreoElectronicoSoliciante(solicitud.getSolicitudImportacionRSQExtVue().getDclrEm());
		entity.setClasificacionImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprClNm());
		entity.setTipoImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprIdtNoTypeNm());
		entity.setNumeroIdentificacionImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprIdtNo());
		entity.setNombreRazonSocialImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprCmpNm());
		entity.setCodigoRSQ(solicitud.getSolicitudImportacionRSQExtVue().getImprRsqCd());
		entity.setNombreImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprNm());
		entity.setProvinciaImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprPrvhcNm());
		entity.setCantonImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprCutyNm());
		entity.setParroquiaImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprPrqiNm());
		entity.setDireccionImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprAd());
		entity.setTelefonoImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprTelNo());
		entity.setCorreoElectronicoImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprEm());
		entity.setPaisEmbarque(solicitud.getSolicitudImportacionRSQExtVue().getLdCtryNm());
		entity.setMedioTransporte(solicitud.getSolicitudImportacionRSQExtVue().getLdTrspWayNm());
		entity.setPuertoEmbarque(solicitud.getSolicitudImportacionRSQExtVue().getLdPortNm());
		entity.setLugarDesembarque(solicitud.getSolicitudImportacionRSQExtVue().getLdUnldPlNm());

		List<EntityTablaFormularioImportacionVueRSQDTO> lEntityTablaFormularioImportacionVueRSQDTO = new ArrayList<>();
		
		
		String datosSustancias = "<table border=\"1\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1;\"><tbody>"
		+ "<tr><td style=\\\"width:20%\\\" align=\"center\">No.</td><td style=\"width:20%\" align=\"center\">Sustancia</td><td style=\"width:20%\" align=\"center\">Subpartida Arancelaria</td><td style=\"width:20%\" align=\"center\">País de Origen</td><td style=\"width:15%\" align=\"center\">Peso Neto Kg</td><td style=\"width:15%\" align=\"center\">Peso Bruto Kg</td><td style=\\\"width:20%\\\" align=\"center\">Tipo Recipiente</td></tr>";
		
		int con = 1;
		for (Iterator<DetalleSolicitudImportacionRSQ> iterator = lDetalleSolicitudImportacionRSQ.iterator(); iterator.hasNext();) {
			DetalleSolicitudImportacionRSQ detalleSolicitudImportacionRSQ = (DetalleSolicitudImportacionRSQ) iterator.next();
			
			EntityTablaFormularioImportacionVueRSQDTO etfivrDto = new EntityTablaFormularioImportacionVueRSQDTO();
			etfivrDto.setCodigoSustancia(detalleSolicitudImportacionRSQ.getActividadSustancia().getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getNumeroCas());
			etfivrDto.setNombreSustancia(detalleSolicitudImportacionRSQ.getActividadSustancia().getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion());
			etfivrDto.setSubPartida(detalleSolicitudImportacionRSQ.getSubPartidaArancelaria());
			etfivrDto.setUbicacionGeografica(detalleSolicitudImportacionRSQ.getUbicacionGeografica().getNombre());
			etfivrDto.setPesoNeto(detalleSolicitudImportacionRSQ.getPesoNeto());
			etfivrDto.setPesoBruto(detalleSolicitudImportacionRSQ.getPesoBruto());
			etfivrDto.setTipoRecipiente(detalleSolicitudImportacionRSQ.getTipoRecipiente());
			
			lEntityTablaFormularioImportacionVueRSQDTO.add(etfivrDto);
			
			datosSustancias += "<tr>"
				+ "<td style=\"width:5%\">"
				+ con++
				+ "</td><td style=\"width:20%\">"
				+ etfivrDto.getNombreSustancia()
				+ "</td><td style=\"width:20%\">"
				+ etfivrDto.getSubPartida()
				+ "</td><td style=\"width:20%\">"
				+ etfivrDto.getUbicacionGeografica()
				+ "</td><td style=\"width:10%\">"
				+ etfivrDto.getPesoNeto()
				+ "</td><td style=\"width:10%\">"
				+ etfivrDto.getPesoBruto()
				+ "</td><td style=\"width:20%\">"
				+ etfivrDto.getTipoRecipiente()
				+ "</td>"
				+ "</tr>";
		}
		
		datosSustancias += "</tbody></table>";
		entity.setTablaSustancias(datosSustancias);
		
		return entity;
	}
	
	public void guardarDocumentosAlfresco() {
		try {
			generarFormularioImportacionVue(false);
			
			documentoInforme = new DocumentoEstudioImpacto();
			documentoInforme.setContenidoDocumento(informeTecnico.getArchivo());
			documentoInforme.setNombre(informeTecnico.getNombreReporte());
			documentoInforme.setExtesion(".pdf");		
			documentoInforme.setMime("application/pdf");
			documentoInforme.setNombreTabla(InformeTecnicoEsIA.class.getSimpleName());
			documentoInforme.setIdTable(informeTecnico.getId());
			
			documentoInforme = documentosFacade.guardarDocumentoAlfrescoCA(/*proyecto.getCodigoUnicoAmbiental*/null, "INFORME_TECNICO", documentoInforme, TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void recuperarInformeTecnico() {
		try{
			informeTecnico = null; //informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, tipoInforme);
			List<DocumentoEstudioImpacto> documentos = documentosFacade.documentoXTablaIdXIdDocLista(informeTecnico.getId(), InformeTecnicoEsIA.class.getSimpleName(), TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);
			if(documentos.size() > 0) {
				documentoInforme = documentos.get(0);
				
				File fileDoc = documentosFacade.descargarFile(documentoInforme);
				
				Path path = Paths.get(fileDoc.getAbsolutePath());
				byte[] contenido = Files.readAllBytes(path);
				String reporteHtmlfinal = documentoInforme.getNombre().replace("/", "-");
				File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
				FileOutputStream file = new FileOutputStream(archivoFinal);
				file.write(contenido);
				file.close();
				
				urlReporte = JsfUtil.devolverContexto("/reportesHtml/" + documentoInforme.getNombre());
				nombreReporte = documentoInforme.getNombre();
				archivoReporte = contenido;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	 // Documento 
	public void uploadDocumentoEIA(FileUploadEvent file) {
		String[] split=file.getFile().getContentType().split("/");
		String extension = "."+split[split.length-1];
		documentoInspeccion = new DocumentoEstudioImpacto();
		documentoInspeccion.setNombre(file.getFile().getFileName());
		documentoInspeccion.setMime(file.getFile().getContentType());
		documentoInspeccion.setContenidoDocumento(file.getFile().getContents());
		documentoInspeccion.setExtesion(extension);
		documentoInspeccion.setNombreTabla(InformacionProyectoEia.class.getSimpleName());
		documentoInspeccion.setIdTable(null/*esiaProyecto.getId()*/);
		//documentoInspeccion.setIdProceso(bandejaTareasBean.getProcessId());
	}
	
	public StreamedContent descargarInforme() throws Exception {
		recuperarInformeTecnico() ;
		
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (archivoReporte != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(archivoReporte), "application/octet-stream");
			content.setName(nombreReporte);
		}
		
		return content;
	}
	
	public void cerrar(){
		//JsfUtil.redirectTo("/pages/rcoa/sustanciasQuimicas/importacionDesdeVue/ingresoParametrosReporteImportacionSustanciasQuimicas.jsf");
	}
}