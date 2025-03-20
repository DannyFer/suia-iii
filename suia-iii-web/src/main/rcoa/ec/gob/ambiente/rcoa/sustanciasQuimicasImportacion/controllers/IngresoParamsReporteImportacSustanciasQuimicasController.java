package ec.gob.ambiente.rcoa.sustanciasQuimicasImportacion.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.jfree.util.Log;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.dto.EntityFormularioImportacionVueRSQDTO;
import ec.gob.ambiente.rcoa.dto.EntityReporteImportacionVueRSQDTO;
import ec.gob.ambiente.rcoa.dto.EntityTablaFormularioImportacionVueRSQDTO;
import ec.gob.ambiente.rcoa.dto.EntityTablaReporteImportacionesVueRSQDTO;
import ec.gob.ambiente.rcoa.dto.SolicitudImportacionRSQVueDTO;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DetalleSolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.SolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DetalleSolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQExtVue;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConstantesEnum;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class IngresoParamsReporteImportacSustanciasQuimicasController {
	
	private static final Logger LOG = Logger.getLogger(IngresoParamsReporteImportacSustanciasQuimicasController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private SolicitudImportacionRSQFacade solicitudImportacionRSQFacade;

	@EJB
	private DetalleSolicitudImportacionRSQFacade detalleSolicitudImportacionRSQFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
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
	private boolean tipo, accionBuscar;

	@Getter
	@Setter
	private SolicitudImportacionRSQVueDTO solicitudImportacionRSQVueDTO;
	
	@Getter
	@Setter
	private SolicitudImportacionRSQVueDTO paramSolicitudImportacionRSQVueDTO;
	
	@Getter
	@Setter
	private SolicitudImportacionRSQ solicitud;
	
	@Getter
	@Setter
	private List<SolicitudImportacionRSQVueDTO> listaSolicitudesDTO;
	
	@Getter
	@Setter
	private boolean solicitudSeleccionada;
	
//	@ManagedProperty(value = "#{documentoRISQBean}")
//	@Getter
//	@Setter
//	private DocumentoRISQBean documentoRISQBean;
	
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
	private byte[] archivoReporte;
	
	@Getter
	@Setter
	private byte[] archivoFormulario;
	
	@Getter
	@Setter
	private String urlFormulario, nombreFormulario;
	
	@Getter
	@Setter
	private List<DetalleSolicitudImportacionRSQ> lDetalleSolicitudImportacionRSQ;

	private Documento documento;
	
	@PostConstruct
	private void init(){
		try {
			paramSolicitudImportacionRSQVueDTO = new SolicitudImportacionRSQVueDTO();
			paramSolicitudImportacionRSQVueDTO.setFechaInicio(Calendar.getInstance().getTime());
			paramSolicitudImportacionRSQVueDTO.setFechaFin(Calendar.getInstance().getTime());
			
			listaSolicitudesDTO = new ArrayList<SolicitudImportacionRSQVueDTO>();		
			
			loginBean.getUsuario().getPersona().getNombre();
			
			tipo = true;
			
			solicitudSeleccionada = false;
		} catch (Exception e) {
			Log.error(e.getStackTrace());
			e.printStackTrace();
		}	
	}
	
	public void limpiar(){
		paramSolicitudImportacionRSQVueDTO.setTipoSolicitud("-1");
		paramSolicitudImportacionRSQVueDTO.setFechaInicio(Calendar.getInstance().getTime());
		paramSolicitudImportacionRSQVueDTO.setFechaFin(Calendar.getInstance().getTime());
		paramSolicitudImportacionRSQVueDTO.setTipoFormato(null);
		listaSolicitudesDTO = new ArrayList<>();
		
		accionBuscar = false;
	}

	private boolean validarParametros() {
		if(null == paramSolicitudImportacionRSQVueDTO.getTipoSolicitud() || "-1".equals(paramSolicitudImportacionRSQVueDTO.getTipoSolicitud())) {
			JsfUtil.addMessageError("Seleccione el tipo de Solicitud");
			return false;
		}
		
		if(paramSolicitudImportacionRSQVueDTO.getFechaInicio() == null || paramSolicitudImportacionRSQVueDTO.getFechaFin() == null) {
			JsfUtil.addMessageError("Debe ingresar el rango de fechas");
			return false;
		}
		
		return true;
	}
	
	
	public void buscar(){
		try {
			accionBuscar = false;			
			if(!validarParametros())
				return;
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(paramSolicitudImportacionRSQVueDTO.getFechaFin()); // Configuramos la fecha que se recibe
			calendar.add(Calendar.HOUR, 23);  // numero de horas a añadir, o restar en caso de horas<0
			calendar.add(Calendar.MINUTE, 59);
			calendar.add(Calendar.SECOND, 59);
			paramSolicitudImportacionRSQVueDTO.setFechaFin(calendar.getTime());
			
			paramSolicitudImportacionRSQVueDTO.setTipoSolicitud(!"0".equals(paramSolicitudImportacionRSQVueDTO.getTipoSolicitud()) ? paramSolicitudImportacionRSQVueDTO.getTipoSolicitud():null);
							
			List<Object[]>  lSolicitudImportacionRSQ = solicitudImportacionRSQFacade.listaSolicitudesPorEstFecha(paramSolicitudImportacionRSQVueDTO);
			
			listaSolicitudesDTO = new ArrayList<>();
			
			if(lSolicitudImportacionRSQ==null)
				return;
			
			int cont = 1;
			for (Iterator<Object[]> iterator = lSolicitudImportacionRSQ.iterator(); iterator.hasNext();) {
				Object[] objSolicitudImportacionRSQ = iterator.next();
				
				SolicitudImportacionRSQ solicitudImportacionRSQ = (SolicitudImportacionRSQ)objSolicitudImportacionRSQ[0];
				solicitudImportacionRSQ.setSolicitudImportacionRSQExtVue((SolicitudImportacionRSQExtVue)objSolicitudImportacionRSQ[1]);
				
				SolicitudImportacionRSQVueDTO solImportRSQVueDTO = armarRegistroDTO(solicitudImportacionRSQ);
				solImportRSQVueDTO.setIndice(cont++);
				
				listaSolicitudesDTO.add(solImportRSQVueDTO);
			}
			
			accionBuscar = true;
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
			JsfUtil.addMessageError("Error al buscar la información");
		}		
	}
	
	private SolicitudImportacionRSQVueDTO armarRegistroDTO(SolicitudImportacionRSQ solicitudImportacionRSQ){
		
		SolicitudImportacionRSQVueDTO solImportRSQVueDTO = new SolicitudImportacionRSQVueDTO();
		solImportRSQVueDTO.setSolicitudImportacionRSQ(solicitudImportacionRSQ);
		solImportRSQVueDTO.setSolicitudImportacionRSQExtVue(solicitudImportacionRSQ.getSolicitudImportacionRSQExtVue());
		solImportRSQVueDTO = setearCamposDeducidos(solImportRSQVueDTO);
		
		return solImportRSQVueDTO;
	}
	
	private SolicitudImportacionRSQVueDTO setearCamposDeducidos(SolicitudImportacionRSQVueDTO solImportRSQVueDTO) {
		
		String descripTipoSol = null;
		if (Constantes.SOLICITUD_APROBADA.equals(solImportRSQVueDTO.getSolicitudImportacionRSQExtVue().getEstadoFormulario())
			|| Constantes.AUCP_ENVIADO_A_LA_ADUANA.equals(solImportRSQVueDTO.getSolicitudImportacionRSQExtVue().getEstadoFormulario())) {
			//unSQL = unSQL + " and sExt.estadoFormulario in ('320', '510')";
			descripTipoSol = "Solicitud Aprobada";
			
			if(Constantes.AUCP_ENVIADO_A_LA_ADUANA.equals(solImportRSQVueDTO.getSolicitudImportacionRSQExtVue().getEstadoFormulario())) {
				descripTipoSol = "Solicitud Aprobada enviada Aduana";
			}
			
			solImportRSQVueDTO.setTipoSolicitud(descripTipoSol);
			solImportRSQVueDTO.setEstadoTramite(Constantes.TRAMITE_ATENDIDO);
			solImportRSQVueDTO.setFechaInicio(solImportRSQVueDTO.getSolicitudImportacionRSQ().getFechaInicioAutorizacion());
			solImportRSQVueDTO.setFechaFin(solImportRSQVueDTO.getSolicitudImportacionRSQ().getFechaFinAutorizacion());
		}else if (Constantes.ANULACION_APROBADA.equals(solImportRSQVueDTO.getSolicitudImportacionRSQExtVue().getEstadoFormulario())
			|| Constantes.SOLICITUD_CORRECCION_NO_APROBADA.equals(solImportRSQVueDTO.getSolicitudImportacionRSQExtVue().getEstadoFormulario())) {
			//unSQL = unSQL + " and sExt.estadoFormulario in ('650', '350')";
			descripTipoSol = "Solicitud Anulación Aprobada"; 
			Date fechaIni = solImportRSQVueDTO.getSolicitudImportacionRSQ().getFechaCreacion();
			Date fechaFin = solImportRSQVueDTO.getSolicitudImportacionRSQ().getFechaAnulacion();
			
			if(Constantes.SOLICITUD_CORRECCION_NO_APROBADA.equals(solImportRSQVueDTO.getSolicitudImportacionRSQExtVue().getEstadoFormulario())) {
				descripTipoSol = "Solicitud Corrección No Aprobada";
				fechaIni = solImportRSQVueDTO.getSolicitudImportacionRSQ().getFechaInicioCorreccion();
				fechaFin = solImportRSQVueDTO.getSolicitudImportacionRSQ().getFechaFinCorreccion();
			}
			
			solImportRSQVueDTO.setTipoSolicitud(descripTipoSol);
			solImportRSQVueDTO.setEstadoTramite(Constantes.TRAMITE_ATENDIDO);
			solImportRSQVueDTO.setFechaInicio(fechaIni);
			solImportRSQVueDTO.setFechaFin(fechaFin);
		}else if (Constantes.SOLICITUD_RECEPTADA.equals(solImportRSQVueDTO.getSolicitudImportacionRSQExtVue().getEstadoFormulario())
			|| Constantes.CORRECCION_SOLICITADA.equals(solImportRSQVueDTO.getSolicitudImportacionRSQExtVue().getEstadoFormulario())
			|| Constantes.ANULACION_SOLICITADA.equals(solImportRSQVueDTO.getSolicitudImportacionRSQExtVue().getEstadoFormulario())) {
			//unSQL = unSQL + " and sExt.estadoFormulario in ('210', '150', '640')";
			 
			descripTipoSol = "Anulación Solicitada";
			Date fechaIni = solImportRSQVueDTO.getSolicitudImportacionRSQ().getFechaCreacion();
			
			if(Constantes.SOLICITUD_RECEPTADA.equals(solImportRSQVueDTO.getSolicitudImportacionRSQExtVue().getEstadoFormulario()))
				descripTipoSol = "Solicitud Receptada";
			else if(Constantes.CORRECCION_SOLICITADA.equals(solImportRSQVueDTO.getSolicitudImportacionRSQExtVue().getEstadoFormulario())) {
				descripTipoSol = "Corrección Solicitada";
				fechaIni = solImportRSQVueDTO.getSolicitudImportacionRSQ().getFechaInicioCorreccion();
			}
			
			solImportRSQVueDTO.setTipoSolicitud(descripTipoSol);
			solImportRSQVueDTO.setEstadoTramite(Constantes.EN_TRAMITE);
			solImportRSQVueDTO.setFechaInicio(fechaIni);
		}else if (Constantes.SOLICITUD_CORRECCION_APROBADA.equals(solImportRSQVueDTO.getSolicitudImportacionRSQExtVue().getEstadoFormulario())) {
			//unSQL = unSQL + " and sExt.estadoFormulario in ('340')";
			
			descripTipoSol = "Solicitud Corrección Aprobada";
			Date fechaIni = solImportRSQVueDTO.getSolicitudImportacionRSQ().getFechaInicioCorreccion();
			Date fechaFin = solImportRSQVueDTO.getSolicitudImportacionRSQ().getFechaFinCorreccion();
			
			solImportRSQVueDTO.setTipoSolicitud(descripTipoSol);
			solImportRSQVueDTO.setEstadoTramite(Constantes.TRAMITE_ATENDIDO);
			solImportRSQVueDTO.setFechaInicio(fechaIni);
			solImportRSQVueDTO.setFechaFin(fechaFin);
		} 
		
		return solImportRSQVueDTO;
	}
	
	
	public void seleccionarListener(Integer id){
		try {
			System.out.println("id seleccionada" + id);
			solicitudSeleccionada = true;
			solicitudImportacionRSQVueDTO = listaSolicitudesDTO.get(id-1);
			
			generarFormularioImportacionVue(Boolean.FALSE);
			
			//urlFormulario = "/pages/rcoa/sustanciasQuimicas/importacionDesdeVue/reporteImportacionSustanciasQuimicaVue.jsf"+"?idSolicImportRSQ="+solicitudImportacionRSQVueDTOSelected.getSolicitudImportacionRSQ().getId();
			RequestContext.getCurrentInstance().execute("PF('dlgVisualizeDoc').show();");
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void verListenerVue(String tipoDescarga){
		try {
			
			generadorReporteVue(tipoDescarga);
			
			//urlFormulario = "/pages/rcoa/sustanciasQuimicas/importacionDesdeVue/reporteImportacionSustanciasQuimicaVue.jsf"+"?idSolicImportRSQ="+solicitudImportacionRSQVueDTOSelected.getSolicitudImportacionRSQ().getId();
			RequestContext.getCurrentInstance().execute("PF('dlgVisualizeDoc').show();");
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void seleccionarListener(SolicitudImportacionRSQVueDTO solicitudImportacionRSQVueDTOSelected){
		try {
			solicitudSeleccionada = true;
			solicitudImportacionRSQVueDTO = solicitudImportacionRSQVueDTOSelected;
			solicitud = solicitudImportacionRSQVueDTO.getSolicitudImportacionRSQ();
			lDetalleSolicitudImportacionRSQ = detalleSolicitudImportacionRSQFacade.buscarPorIdSolicitud(solicitud);
			
			generarFormularioImportacionVue(Boolean.FALSE);
			
			//urlFormulario = "/pages/rcoa/sustanciasQuimicas/importacionDesdeVue/reporteImportacionSustanciasQuimicaVue.jsf"+"?idSolicImportRSQ="+solicitudImportacionRSQVueDTOSelected.getSolicitudImportacionRSQ().getId();
			RequestContext.getCurrentInstance().execute("PF('dlgVisualizeDoc').show();");
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
		}else{
			solicitud.setAnulacion(true);
			solicitud.setFechaInicioAutorizacion(null);
			solicitud.setGestionarProductosQuimicosProyectoAmbiental(null);
		}
	}
	

	public void cerrar(){
		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	}


	public void actualizarFechas() {
		//feriadoSeleccionado.setFechaFin(null);
		RequestContext.getCurrentInstance().reset(":frmInformacion:fechaFin");
	}
	
	public void postProcessXLS(Object document) {
		List<Row> rowList = new ArrayList<>();
		
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        CellStyle style = wb.createCellStyle();
        Cell celdaDef;
        style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        for (Row row : sheet) {
        	rowList.add(row);
        }
        
        //RG       
        Row rowC = sheet.createRow((short)0); 
        rowC.createCell((int)0).setCellValue("REPORTE DE SOLICITUDES DE IMPORTACIÓN DE SUSTANCIAS QUIMICAS");
        
        rowC = sheet.createRow((short)1); 
        //rowC.createCell((int)0).setCellValue("REPORTE DE ALGOOOOOOOOOOOOO");
        
        String tipoSolicitud = ConstantesEnum.fromCodigo(Long.valueOf(paramSolicitudImportacionRSQVueDTO.getTipoSolicitud().substring(0,1))).getDescripcion(); 
		String fechaI = JsfUtil.devuelveFechaConFormato(paramSolicitudImportacionRSQVueDTO.getFechaInicio(), "yyyy-MM-dd"); 
		String fechaF = JsfUtil.devuelveFechaConFormato(paramSolicitudImportacionRSQVueDTO.getFechaFin(), "yyyy-MM-dd");
        
        rowC = sheet.createRow((short)2); 
        rowC.createCell((int)0).setCellValue("TIPO DE SOLICITUD: " + tipoSolicitud);
        rowC = sheet.createRow((short)3); 
        rowC.createCell((int)0).setCellValue("FECHA INICIO: " + fechaI);
        rowC = sheet.createRow((short)4); 
        rowC.createCell((int)0).setCellValue("FECHA FIN: " + fechaF);
        rowC = sheet.createRow((short)5); 
        rowC = sheet.createRow((short)6); 
        
        //RGFIN
        int fila = 7;
        for(int i=0; i < rowList.size(); i++) {
        	rowC = sheet.createRow((short)fila);
        	int celda = 0;
        	for (Cell cell : rowList.get(i)) {
        		if (celda < 6) {
        			if(celda !=0) {
        				sheet.autoSizeColumn(celda);	
        			} 
        			celdaDef = rowC.createCell((int)celda);
        			celdaDef.setCellStyle(style);
        			celdaDef.setCellValue(cell.getStringCellValue());
        			//rowC.createCell(((int)celda).setCellStyle(style)).setCellValue(cell.getStringCellValue()); 
        			//rowC.setCellStyle(style);
        			celda ++;            		
        		}        		
        	}
        	fila ++;
        }
        
        /*for (Row row : sheet) {
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                default:
                    cell.setCellValue(cell.getStringCellValue().toUpperCase());
                    cell.setCellStyle(style);
                    break;
                }
            }
        }*/
        
	}
        
    public void postProcessXLS2(Object document) {
		short bg = 0;
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(bg);
		cellStyle.setFillPattern(FillPatternType.NO_FILL);
		cellStyle.setWrapText(true);

		for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);

		}

	}
	
	public StreamedContent descargarDocumento(String tipoDescarga) throws Exception {
		if((listaSolicitudesDTO==null || listaSolicitudesDTO.isEmpty()) && !accionBuscar) {
			JsfUtil.addMessageError("No existe registros consultados");
			return null;
		}
		
		generadorReporteVue(tipoDescarga);
		
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			/*if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentoSustanciasQuimicasFacade
						.descargar(documento.getIdAlfresco()));
			}*/
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documento.getContenidoDocumento()), documento.getExtesion());
			content.setName(documento.getNombre());
		}
		
		//documentoDescargado = true;
		
		return content;
		
		
		/*
		generadorReporteVue(tipoDescarga);

		try {
        	if(documento.getNombre().contains("pdf"))
        		JsfUtil.descargarPdf(documento.getContenidoDocumento(), documento.getNombre().replace(".pdf", ""));
        	else {
        		String nombreReporte = "ReporteImportSustanciasQuimicas" + JsfUtil.devuelveFechaConFormato(JsfUtil.fechaHoraActual(), "yyyyMMddHHmmssSSS")+"."+paramSolicitudImportacionRSQVueDTO.getTipoFormato();
        		UtilDocumento.descargarExcel(documento.getContenidoDocumento(), nombreReporte);
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        }*/
        
	}
	
	private void generadorReporteVue(String tipoDescarga){	
		
    	try {
			EntityReporteImportacionVueRSQDTO entity = cargarDatosReporteTipoDataTable();

			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_REPORTE_VUE_IMPORTACION_SUSTANCIAS_QUIMICAS);
			String fechaConFormato = JsfUtil.devuelveFechaConFormato(JsfUtil.fechaHoraActual(), "yyyyMMddHHmmssSSS");
			String nombreReporte = "ReporteImportSustanciasQuimicas";
			
			documento = null;
			File unReporte =  UtilGenerarInforme.generarFicheroVue(plantillaReporte.getHtmlPlantilla(), nombreReporte, tipoDescarga, true, entity);
			
			nombreReporte = "ReporteImportSustanciasQuimicas"+fechaConFormato+"."+tipoDescarga;
			Path path = Paths.get(unReporte.getAbsolutePath());
			String reporteHtmlfinal = nombreReporte;
			archivoReporte = Files.readAllBytes(path);
			
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(Files.readAllBytes(path));
			file.close();
						
			if("pdf".equalsIgnoreCase(tipoDescarga)) {
				UtilDocumento.descargarPDF(archivoReporte, nombreReporte);
			} else {
				UtilDocumento.descargarExcel(archivoReporte, nombreReporte);
			}
			
			
			
/*			
			TipoDocumento tipoDoc = new TipoDocumento();
			tipoDoc.setId(TipoDocumentoSistema.RCOA_REPORTE_VUE_IMPORTACION_SUSTANCIAS_QUIMICAS.getIdTipoDocumento());
			
			String tipoMime = "pdf".equalsIgnoreCase(tipoDescarga) ? "application/pdf":"application/vnd.ms-excel";
			documento = new Documento();
			documento.setNombre(nombreReporte);
			documento.setExtesion("."+tipoDescarga);
			documento.setMime(tipoMime);					
			documento.setContenidoDocumento(archivoReporte);
			documento.setNombreTabla("REPORTE DE SOLICITUDES DE IMPORTACIÓN DE SUSTANCIAS QUIMICAS");
			documento.setTipoDocumento(tipoDoc);
			*/
			/*
			
			documento = null;
			File unReporte = null;
			if("pdf".equalsIgnoreCase(tipoDescarga)) {
				unReporte = UtilGenerarInforme.generarFicheroHtml(plantillaReporte.getHtmlPlantilla(), nombreReporte, true, entity);
				
				Path path = Paths.get(unReporte.getAbsolutePath());
				String reporteHtmlfinal = nombreReporte;
				archivoReporte = Files.readAllBytes(path);
				
				File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
				FileOutputStream file = new FileOutputStream(archivoFinal);
				file.write(Files.readAllBytes(path));
				file.close();
							
				TipoDocumento tipoDoc = new TipoDocumento();
				tipoDoc.setId(TipoDocumentoSistema.RCOA_REPORTE_VUE_IMPORTACION_SUSTANCIAS_QUIMICAS.getIdTipoDocumento());
				
				String tipoMime = "pdf".equalsIgnoreCase(tipoDescarga) ? "application/pdf":"application/vnd.ms-excel";
				documento = new Documento();
				documento.setNombre(nombreReporte);
				documento.setExtesion("."+tipoDescarga);
				documento.setMime(tipoMime);					
				documento.setContenidoDocumento(archivoReporte);
				documento.setNombreTabla("REPORTE DE SOLICITUDES DE IMPORTACIÓN DE SUSTANCIAS QUIMICAS");
				documento.setTipoDocumento(tipoDoc);
			}*/
		} catch (Exception ex) {
			Log.error(ex.getMessage());
			ex.printStackTrace();
		} 
	
	}
	
	public Documento descargarDocumento(Documento documento, String nombreReporte) throws CmisAlfrescoException {
        try {
        	if(documento.getNombre().contains("pdf"))
        		JsfUtil.descargarPdf(documento.getContenidoDocumento(), documento.getNombre().replace(".pdf", ""));
        	else
        		UtilDocumento.descargarExcel(documento.getContenidoDocumento(), nombreReporte);
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        return documento;
    }
	
	public EntityReporteImportacionVueRSQDTO cargarDatosReporteTipoDataTable() throws ServiceException {
		
		EntityReporteImportacionVueRSQDTO entity = new EntityReporteImportacionVueRSQDTO();
		
		entity.setTipoSolicitud(ConstantesEnum.fromCodigo(Long.valueOf(paramSolicitudImportacionRSQVueDTO.getTipoSolicitud().substring(0,1))).getDescripcion()); 
		entity.setFechaInicio(JsfUtil.devuelveFechaConFormato(paramSolicitudImportacionRSQVueDTO.getFechaInicio(), "yyyy-MM-dd")); 
		entity.setFechaFin(JsfUtil.devuelveFechaConFormato(paramSolicitudImportacionRSQVueDTO.getFechaFin(), "yyyy-MM-dd"));
		//entity.setTipoDocumento(paramSolicitudImportacionRSQVueDTO.getTipoFormato());
		
		List<EntityTablaReporteImportacionesVueRSQDTO> lEntityTablaReporteImportacionesVueRSQDTO = new ArrayList<>();
		
		String datosSolicitudes = "";
		
		for (Iterator<SolicitudImportacionRSQVueDTO> iterator = listaSolicitudesDTO.iterator(); iterator.hasNext();) {
			SolicitudImportacionRSQVueDTO solicitudImportacionRSQVueDTO = (SolicitudImportacionRSQVueDTO) iterator.next();
			
			EntityTablaReporteImportacionesVueRSQDTO etrivrDto = new EntityTablaReporteImportacionesVueRSQDTO();
			etrivrDto.setIndice(solicitudImportacionRSQVueDTO.getIndice());
			etrivrDto.setNumeroSolicitud(solicitudImportacionRSQVueDTO.getSolicitudImportacionRSQ().getTramite());
			etrivrDto.setTipoSolicitud(solicitudImportacionRSQVueDTO.getTipoSolicitud());
			etrivrDto.setFechaInicio(solicitudImportacionRSQVueDTO.getFechaInicio()!=null ? solicitudImportacionRSQVueDTO.getFechaInicio().toString():null);
			etrivrDto.setFechaFin(solicitudImportacionRSQVueDTO.getFechaFin()!=null ? solicitudImportacionRSQVueDTO.getFechaFin().toString():null);
			etrivrDto.setEstado(solicitudImportacionRSQVueDTO.getEstadoTramite());
			
			lEntityTablaReporteImportacionesVueRSQDTO.add(etrivrDto);
			
			datosSolicitudes += "<tr>"
				+ "<td style=\"width:5%\">"
				+ etrivrDto.getIndice()
				+ "</td><td style=\"width:20%\">"
				+ etrivrDto.getNumeroSolicitud()
				+ "</td><td style=\"width:20%\">"
				+ etrivrDto.getTipoSolicitud()
				+ "</td><td style=\"width:20%\">"
				+ etrivrDto.getFechaInicio()
				+ "</td><td style=\"width:10%\">"
				+ etrivrDto.getFechaFin()
				+ "</td><td style=\"width:10%\">"
				+ etrivrDto.getEstado()
				+ "</td>"
				+ "</tr>";
		}
		
		entity.setTablaReporteSolicitudes(datosSolicitudes);
		
		return entity;
	}
	
	public void generarFormularioImportacionVue(Boolean marcaAgua) {
		try {
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_FORMULARIO_VUE_IMPORTACION_SUSTANCIAS_QUIMICAS);
			
			EntityFormularioImportacionVueRSQDTO entity = cargarDatosReporteTipoFormulario();
			entity.setNombreReporte("FormularioImportacionRSQVue_" + UtilViabilidad.getFileNameEscaped(entity.getNumeroSolicitud().replace("/", "-")) + ".pdf");
			
			File formPdfAux = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), entity.getNombreReporte(), true, entity);
			File formPdf = JsfUtil.fileMarcaAgua(formPdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(formPdf.getAbsolutePath());
			archivoFormulario = Files.readAllBytes(path);
			
			String reporteHtmlfinal = entity.getNombreReporte().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(archivoFormulario);
			file.close();
			urlFormulario = JsfUtil.devolverContexto("/reportesHtml/" + entity.getNombreReporte());
			
			nombreFormulario = entity.getNombreReporte();
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Error al cargar el formulario de Importación de Sustancias Químicas", ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public EntityFormularioImportacionVueRSQDTO cargarDatosReporteTipoFormulario() throws ServiceException {
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
}
