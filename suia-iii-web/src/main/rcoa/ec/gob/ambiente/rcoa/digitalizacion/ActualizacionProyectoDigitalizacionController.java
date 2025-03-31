package ec.gob.ambiente.rcoa.digitalizacion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.DocumentoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.HistorialActualizacionesDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.DocumentoDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.HistorialactualizacionesDigitalizacion;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class ActualizacionProyectoDigitalizacionController {
	
	Logger LOG = Logger.getLogger(ActualizacionProyectoDigitalizacionController.class);
	
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private HistorialActualizacionesDigitalizacionFacade historialActualizacionesDigitalizacionFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private DocumentoDigitalizacionFacade documentoDigitalizacionFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{datosOperadorRgdBean}")
	private DatosOperadorRgdBean datosOperadorRgdBean;

	@Getter
	@Setter
	private LazyDataModel<AutorizacionAdministrativaAmbiental> listaAutorizacionPrincipal;

	@Getter
	@Setter
	private boolean esTecnico;

	@Getter
	@Setter
	private String historialPdf;

	@Getter
	@Setter
	private List<HistorialactualizacionesDigitalizacion> listaHistorial;
	
	private String areasTecnico;
	private DocumentoDigitalizacion documento;
	
	@PostConstruct
	public void init(){
		try {
			areasTecnico="";
			esTecnico = true;
			for (AreaUsuario areaUser : JsfUtil.getLoggedUser().getListaAreaUsuario()) {
				if (areaUser.getArea().getAreaName().contains("PROPONENTE")) {
					esTecnico = false;
					datosOperadorRgdBean.buscarDatosOperador(JsfUtil.getLoggedUser());
					break;
				}
			}
			if(esTecnico){
				for (AreaUsuario areaUser : JsfUtil.getLoggedUser().getListaAreaUsuario()) {
					areasTecnico += (areasTecnico.isEmpty()?"":",")+areaUser.getArea().getId().toString();
					if(areaUser.getArea().getArea() != null && !areasTecnico.contains(areaUser.getArea().getArea().getId().toString()))
						areasTecnico += "," + areaUser.getArea().getArea().getId().toString();
				}
			}
			listaAutorizacionPrincipal = new LazyProyectosDigitalizacionActualizacionDataModel(esTecnico, areasTecnico);
		} catch (Exception e) {
			LOG.error("Error al recuperar la información", e);
		}
	}
	
	public void actualizarProyecto (AutorizacionAdministrativaAmbiental proyecto){
		if(proyecto.getEstadoActualizacion() != null && proyecto.getEstadoActualizacion() && !proyecto.getUsuarioModificacion().equals(loginBean.getUsuario().getNombre())){
			JsfUtil.addMessageError("El proyecto está siendo actualizado por otro usuario, se habilitará la opción de actualizar una vez que la actualización en curso finalice.");
			return;
		}
		bandejaTareasBean.setProcessId(proyecto.getIdProceso());
		JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoFormDigitalizacion.jsf");
	}
	
	public void historialProyecto (AutorizacionAdministrativaAmbiental proyecto){
		listaHistorial = historialActualizacionesDigitalizacionFacade.obtenerHistorialAAA(proyecto);
		for (HistorialactualizacionesDigitalizacion objHistorial : listaHistorial) {
			String area="";
			for (AreaUsuario areaUser :objHistorial.getUsuario().getListaAreaUsuario()){
				area += (area.isEmpty()?"":", ")+areaUser.getArea().getAreaName();
			}
			objHistorial.setAreaTecnico(area);
		}
	}
	
	public void generarPdfHistorial(){
		DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.DIGITALIZACION_DOCUMENTO_HISTORIAL_ACTUALIZACIONES);
		AutorizacionAdministrativa proyecto = new AutorizacionAdministrativa();
		AutorizacionAdministrativaAmbiental autorizacionAdministrativa = new AutorizacionAdministrativaAmbiental();
		try{
		String tablaHistorial = "<br/><table style=\"width: 700px; font-size: 11px !important;\" border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\">"
					+ "<tbody><tr BGCOLOR=\"#B2E6FF\">"
					+ "<td><strong>Motivo de la actualización</strong></td>"
					+ "<td><strong>Nombre del técnico que actualizó</strong></td>"
					+ "<td><strong>Entidad a la cual pertenece el técnico que actualizó</strong></td>"
					+ "<td><strong>Fecha de actualización de la información de la AAA</strong></td>"
					+ "</tr>";
		for (HistorialactualizacionesDigitalizacion objHistorial : listaHistorial) {
			proyecto.setCodigo(objHistorial.getAutorizacionAdministrativaAmbiental().getCodigoProyecto());
			proyecto.setId(objHistorial.getAutorizacionAdministrativaAmbiental().getId());
			autorizacionAdministrativa = objHistorial.getAutorizacionAdministrativaAmbiental();
			tablaHistorial += "<tr>";
			tablaHistorial += "<td>" + objHistorial.getDescripcionActualizacion() + "</td>";
			tablaHistorial += "<td>" + objHistorial.getUsuario().getPersona().getNombre() + "</td>";
			tablaHistorial += "<td>" + objHistorial.getAreaTecnico() + "</td>";
			tablaHistorial += "<td>" + inputFormat.format(objHistorial.getFechaCreacion()) + "</td>";
			tablaHistorial += "</tr>";
		}
		tablaHistorial += "</tbody></table><br/>";
		proyecto.setResumen(tablaHistorial);
		
		proyecto.setFecha(JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), "historial_", true, proyecto,null);
            Path path = Paths.get(informePdf.getAbsolutePath());
            byte[] byteArchivo = Files.readAllBytes(path);
            File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(informePdf.getName()));
            FileOutputStream file = new FileOutputStream(fileArchivo);
            file.write(byteArchivo);
    		historialPdf = JsfUtil.devolverContexto("/reportesHtml/" + fileArchivo.getName());
            documento = new DocumentoDigitalizacion();
    		documento.setContenidoDocumento(byteArchivo);
    		documento.setMime("application/pdf");
    		documento.setIdTabla(autorizacionAdministrativa.getId());
    		documento.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
    		documento.setNombre("historial de actualizaciones" + ".pdf");
    		documento.setExtension(".pdf");
    		documento.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
    	//	documento = documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(),
    		//				"HISTORIAL ACTUALIZACIONES", null, documento, TipoDocumentoSistema.DIGITALIZACION_DOCUMENTO_HISTORIAL_ACTUALIZACIONES);
    		RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlgHistorialDocumento').show();");
		}catch(Exception e){
            e.printStackTrace();
		}
	}
	
	public void redireccionarBandeja(){
			JsfUtil.redirectToBandeja();
	}

	public StreamedContent getDocumentoDownload(){
		try {
			if (documento != null && documento.getContenidoDocumento() !=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),documento.getMime(),documento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {
			LOG.error("Error al guardar documento Certificado", e);
		}
		return null;
	}
}
