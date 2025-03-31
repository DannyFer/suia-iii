package ec.gob.ambiente.suia.comun.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ConductorFacade;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.Conductor;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CargarConductoresBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7670413797300934447L;

	@Getter
	@Setter
	private boolean modalLoadFile;

	@Getter
	@Setter
	private boolean mostrarprevisualizar;
	
	@Getter
	@Setter
	private boolean mostrargeneral;
	
	@Getter
	@Setter
	private String updateComponentRoute;
	

	@Getter
	@Setter
	private String observaciones;
	
	@Getter
	private UploadedFile uploadedFile;

	@Getter
	@Setter
	List<Conductor> listConsultants = new ArrayList<Conductor>();
	
	
	@Getter
	@Setter
	List<Conductor> listConsultantsdb = new ArrayList<Conductor>();
	
	
   @EJB
   private ConductorFacade conductorFacade;


	private byte[] plantillaConductores;

	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)	
	private AlfrescoServiceBean alfrescoServiceBean;

	@PostConstruct
	public void init() {
		try {
			setMostrarprevisualizar(false);
			setMostrargeneral(true);
			listConsultantsdb= conductorFacade.listconductores();
			plantillaConductores = alfrescoServiceBean.downloadDocumentByNameAndFolder(Constantes.PLANTILLA_CONDUCTORES,Constantes.getRootStaticDocumentsId(), true);
		} catch (Exception e) {
		}
		modalLoadFile = true;
	}

	public void handleFileUpload(final FileUploadEvent event) {
		int rows = 0;
		String message = "";
		String anio="";	
		String ruc = "";
		String certificado="";
		
		listConsultants.clear();
		uploadedFile = event.getFile();

		try {

			uploadedFile = event.getFile();
			Workbook workbook = new HSSFWorkbook(uploadedFile.getInputstream());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				boolean isEmptyRow = true;
				for (int cellNum = row.getFirstCellNum(); cellNum < row
						.getLastCellNum(); cellNum++) {
					Cell cell = row.getCell(cellNum);
					if (cell != null
							&& cell.getCellType() != Cell.CELL_TYPE_BLANK
							&& StringUtils.isNotBlank(cell.toString())) {
						isEmptyRow = false;
					}
				}
				if (isEmptyRow)
					break;

				if (rows > 0) {
					try {
						
						Iterator<Cell> cellIterator = row.cellIterator();
						Conductor consultants = new Conductor();
						anio = String.valueOf(cellIterator.next());
						double f= Double.valueOf(anio);						
						consultants.setAnio((int)f);
						SimpleDateFormat formato = new SimpleDateFormat(
								"yyyy-MM-dd");
						String emisiondate =formato.format(cellIterator.next().getDateCellValue());
						Date emision = formato.parse(emisiondate);
						consultants.setEmision(emision);
						String vigenciadate =formato.format(cellIterator.next().getDateCellValue());
						Date vigencia = formato.parse(vigenciadate);						
						consultants.setVigencia(vigencia);
						consultants.setCiudad(String.valueOf(cellIterator
								.next()));
						consultants.setNombre(String.valueOf(cellIterator
								.next()));
						ruc = cellIterator.next().getStringCellValue();
						System.out.println("ruc:"+ruc);
						if(ruc.length() < 6){
							message = "Ci : "+ruc+" de la fila: "+rows+" esta incorrecta. Por favor verifique.";
							JsfUtil.addMessageInfo(message);
							break;
						}else if(ruc.length() < 20 && ruc.length() > 10){
							message = "Ci / Ruc / pasaporte : "+ruc+" de la fila: "+rows+" esta incorrecta. Por favor verifique.";
							JsfUtil.addMessageInfo(message);
							break;
						}else if (ruc.length() > 20){
							message = "Ci / Ruc / pasaporte : "+ruc+" de la fila: "+rows+" esta incorrecta. Por favor verifique.";
							JsfUtil.addMessageInfo(message);
							break;
						}
						consultants.setDocumento(ruc);
						consultants.setEmpresa(cellIterator.next().getStringCellValue().toUpperCase()); //Natural (N) o Juridico (J)
						//consultants.setConsCategory(cellIterator.next().getStringCellValue()); //Categoria A, B, C
						certificado= cellIterator.next().getStringCellValue().toUpperCase();	
						consultants.setCodigoCurso(certificado);
						if (listConsultants.isEmpty()) {
							listConsultants.add(consultants);
						} else {
							boolean existe = false;
							for (Conductor lista : listConsultants) {
								if (lista.getCodigoCurso().equals(certificado)) {
									existe = true;
									message = "El código: "
											+ certificado
											+ " de la fila: "
											+ rows
											+ " no se cargo en el sistema porque se encuentra repetido.";
									JsfUtil.addMessageInfo(message);
									break;

								}else if(lista.getDocumento().equals(ruc)){
									existe = true;
									message = "La cédula / ruc : "
											+ ruc
											+ " de la fila: "
											+ rows
											+ " no se cargo en el sistema porque se encuentra repetido.";
									JsfUtil.addMessageInfo(message);
									break;
								}

							}
							if (!existe) {
								listConsultants.add(consultants);
							}

						}
						
					} catch (Exception e) {
						setMostrarprevisualizar(false);
						setMostrargeneral(true);
						listConsultants.clear();
						message = "Error procesando el archivo excel. Por favor revise su estructura e intente nuevamente.";
						JsfUtil.addMessageInfo(message);
						return;
					}

				}

				rows++;
				setMostrarprevisualizar(true);
				setMostrargeneral(false);
			}

		} catch (IOException e) {
			listConsultants.clear();
			if (rows == 0)
				message = "Error procesando el archivo excel. Por favor revise su estructura e intente nuevamente.";
			JsfUtil.addMessageInfo(message);
			setMostrarprevisualizar(false);
			setMostrargeneral(true);

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public StreamedContent getPlantillaConsultores() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (plantillaConductores != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						plantillaConductores));
				content.setName(Constantes.PLANTILLA_CONDUCTORES);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;

	}

	public void saveConsultores() {

		String certificado = "";
		String ciRuc = "";
		boolean existConsultor=false;
		boolean exito;
		boolean respuesta;
		boolean respuestaSectorSubsector;
		try{
		for (Conductor lisConsultants : getListConsultants()) {
			
			certificado = lisConsultants.getCodigoCurso();
			ciRuc = lisConsultants.getDocumento();
			Conductor datoConsultants = new Conductor();
			datoConsultants = conductorFacade.buscarConductoresPorCedula(ciRuc);
		
		if(datoConsultants==null){
			existConsultor=false;
			datoConsultants = new Conductor();
			datoConsultants.setAnio(lisConsultants.getAnio());
			datoConsultants.setEmision(lisConsultants.getEmision());
			datoConsultants.setVigencia(lisConsultants.getVigencia());
			datoConsultants.setCiudad(lisConsultants.getCiudad());
			datoConsultants.setNombre(lisConsultants.getNombre());
			datoConsultants.setDocumento(ciRuc);
			datoConsultants.setEstadoPermiso("VIGENTE");
			datoConsultants.setEmpresa(lisConsultants.getEmpresa());
			datoConsultants.setCodigoCurso(lisConsultants.getCodigoCurso());	
			datoConsultants.setObservacion(observaciones);
		}else{
			existConsultor=true;
			datoConsultants.setAnio(lisConsultants.getAnio());
			datoConsultants.setEmision(lisConsultants.getEmision());
			datoConsultants.setVigencia(lisConsultants.getVigencia());
			datoConsultants.setCiudad(lisConsultants.getCiudad());
			datoConsultants.setNombre(lisConsultants.getNombre());
			datoConsultants.setDocumento(ciRuc);
			datoConsultants.setEstadoPermiso("VIGENTE");
			datoConsultants.setEmpresa(lisConsultants.getEmpresa());
			datoConsultants.setCodigoCurso(lisConsultants.getCodigoCurso());	
			datoConsultants.setObservacion(observaciones);
		}
			
		exito = conductorFacade.guardar(datoConsultants, existConsultor);
			if(exito==true){				
				
			}else{
				JsfUtil.addMessageInfo("No se actualizaron algunos registros");
			}

		}
		
		setMostrarprevisualizar(false);
		setMostrargeneral(true);
		JsfUtil.addMessageInfo("Registros Guardados.");
		listConsultants.clear();
		} catch (Exception e) {
			listConsultants.clear();
			JsfUtil.addMessageInfo("Error al guardar los datos.");
			return;
		}
	}
	
	public void limpiardatos() {
		try {
			setMostrarprevisualizar(false);
			setMostrargeneral(true);
			listConsultantsdb= conductorFacade.listconductores();
			plantillaConductores = alfrescoServiceBean.downloadDocumentByNameAndFolder(Constantes.PLANTILLA_CONDUCTORES,Constantes.getRootStaticDocumentsId(), true);
		} catch (Exception e) {
		}
		modalLoadFile = true;
		// TODO Auto-generated method stub

	}
	
	
		
	
}