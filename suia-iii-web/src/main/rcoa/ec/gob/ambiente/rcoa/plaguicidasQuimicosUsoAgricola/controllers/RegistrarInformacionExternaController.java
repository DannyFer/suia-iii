package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.DocumentoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProductoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.RegistroProductoFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DocumentoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProductoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.RegistroProducto;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class RegistrarInformacionExternaController {
	
	@EJB
	private RegistroProductoFacade registroProductoFacade;	
	@EJB
	private ProductoPquaFacade productoPquaFacade;	
	@EJB
	private DocumentoPquaFacade documentosFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@Getter
	@Setter
	private List<RegistroProducto> listaRegistros;
	
	@Getter
	@Setter
	private List<ProductoPqua> listaProductos, listaProductosEliminar;
	
	@Getter
	@Setter
	private List<CatalogoGeneralCoa> listaColor;
	
	@Getter
	@Setter
	private RegistroProducto registroSeleccionado;
	
	@Getter
	@Setter
	private ProductoPqua nuevoProducto;

	@Getter
	@Setter
	private List<String> listaAnioMes;
	
	@Getter
	private List<String> anioList,mesList;
	
	@Getter
	@Setter
	private Date fechaMesIngresoItemSeleccionado;
	
	@Getter
	@Setter
	private Boolean esEdicion;
	
	@Getter
	@Setter
	private String informacionProducto, mesIngresoString, anioIngresoString;
	
	@Getter
    @Setter
	private Integer indexTab;
	
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	
	@PostConstruct
	public void init(){
		try {
			
			iniciarInformacion();
			
			listaRegistros = registroProductoFacade.getRegistroProducto();
			
			if(listaRegistros != null && listaRegistros.size() > 0) {
				registroSeleccionado = listaRegistros.get(0);
				fechaMesIngresoItemSeleccionado = registroSeleccionado.getMesIngreso();
				mesIngresoString = registroSeleccionado.getMesIngresoSeleccion();
				anioIngresoString = registroSeleccionado.getAnioIngresoSeleccion();
			}
			
			indexTab = 0;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void iniciarInformacion(){
		listaRegistros = new ArrayList<>();
		listaAnioMes = new ArrayList<String>();
		nuevoProducto = new ProductoPqua();
		listaProductos = new ArrayList<ProductoPqua>();
		listaProductosEliminar = new ArrayList<ProductoPqua>();
		
		anioList=new ArrayList<String>();
		mesList=new ArrayList<String>();
		fechaMesIngresoItemSeleccionado = null;
		mesIngresoString = null;
		anioIngresoString = null;
		
		cargarListaAniosMeses();
		
		listaColor = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_COLOR_FRANJA);
	}
	
	private void cargarListaAniosMeses(){	
		mesList=new ArrayList<String>( Arrays.asList("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"));		
				
		int anioActual=JsfUtil.getCurrentYear();		
		for(int i=(anioActual-2);i<=(anioActual+5);i++)
			anioList.add(String.valueOf(i));

	}
	
	public void agregarRegistroPqua(){
		RegistroProducto registro = new RegistroProducto();
		registroSeleccionado = new RegistroProducto();
		fechaMesIngresoItemSeleccionado = null;
		mesIngresoString = null;
		anioIngresoString = null;
		
		listaRegistros.add(0, registro);
		
		indexTab = 0;
	}
	
	public void cancelarRegistroPqua(){
		listaRegistros = registroProductoFacade.getRegistroProducto();
		indexTab = -1;
	}
	
	public void adjuntarArchivoProductos(RegistroProducto infoRegistro){
		registroSeleccionado = infoRegistro;
	}
	
	public void onTabChange(TabChangeEvent event) {
		registroSeleccionado = (RegistroProducto) event.getData();
		fechaMesIngresoItemSeleccionado = registroSeleccionado.getMesIngreso();
		
		mesIngresoString = registroSeleccionado.getMesIngresoSeleccion();
		anioIngresoString = registroSeleccionado.getAnioIngresoSeleccion();
    }
	
	public StreamedContent getPlantilla() throws Exception {
		DefaultStreamedContent content = null;
		try {
    		byte[] plantillaPmi = documentosFacade.descargarDocumentoPorNombre("Anexo05_SITEAA_Listado PQUA.xlsx");
        
            if (plantillaPmi != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaPmi));
                content.setName("Listado PQUA.xlsx");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
	
	public void handleFileUpload(final FileUploadEvent event) throws IOException {
    	int rows = 0;
    	
    	listaProductos = new ArrayList<ProductoPqua>();
    	List<String> listaCedulas = new ArrayList<>();
    	
    	if(registroSeleccionado.getListaDetalleProductos() != null) {
    		listaProductosEliminar.addAll(registroSeleccionado.getListaDetalleProductos());
    	}
    	
    	XSSFWorkbook libroPma = null;
    	try {
    		UploadedFile uploadedFile = event.getFile();
			libroPma = new XSSFWorkbook(uploadedFile.getInputstream());
    		Sheet sheet = libroPma.getSheetAt(0);
    		Iterator<Row> rowIterator = sheet.iterator();
    		
    		while (rowIterator.hasNext()) {
    			Row row = rowIterator.next();                
    			boolean isEmptyRow = true;
    			for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
    				Cell cell = row.getCell(cellNum);
    				if (cell != null && cell.getCellTypeEnum() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
    					isEmptyRow = false;
    				}
    			}
    			if (isEmptyRow)
    				break;
    			
    			if (rows > 0) {
    				ProductoPqua detalle = new ProductoPqua();
    				for (int i=0; i<row.getLastCellNum(); i++) {
    			        Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
    			        
    			        if (cell == null) {
    			            throw new RuntimeException("_blank_cell");
    			        }
    			        
    			        DataFormatter dataFormatter = new DataFormatter();
    			        String valorCelda = dataFormatter.formatCellValue(cell);
    			        
    			        switch (i) {
						case 0:
							detalle.setCedulaRuc(valorCelda);
							
							Boolean existe = false;
							if(listaCedulas.contains(valorCelda)) {
								for (ProductoPqua prod : listaProductos) {
									if(prod.getCedulaRuc().equals(valorCelda)) {
										detalle.setNombreOperador(prod.getNombreOperador());
										detalle.setRepresentanteLegal(prod.getRepresentanteLegal());
										existe = true;
										break;
									}
								}
							}
							
							if(!existe) {
								Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC,
		    							Constantes.PASSWORD_WS_MAE_SRI_RC, valorCelda);
		    					if (cedula != null && cedula.getError().equals(Constantes.NO_ERROR_WS_MAE_SRI_RC)) {
		    						detalle.setNombreOperador(cedula.getNombre());
		    						detalle.setRepresentanteLegal(null);
		    					}else{
		    						ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula.obtenerPorRucSRI(
		    								Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, valorCelda);
		    						if (contribuyenteCompleto != null && contribuyenteCompleto.getRazonSocial() != null) {
		    								detalle.setNombreOperador(contribuyenteCompleto.getRazonSocial());
		    								detalle.setRepresentanteLegal(contribuyenteCompleto.getRepresentanteLegal().getNombre());
		    						}else{
		    							System.out.println(valorCelda);
		    							throw new RuntimeException("_ruc_invalid");
		    						}
		    					}
		    					
		    					listaCedulas.add(valorCelda);
							}
	    					
							break;
						case 1:
							detalle.setNumeroRegistro(valorCelda);
							break;
						case 2:
							Date fechaCelda = cell.getDateCellValue();							
							detalle.setFechaRegistro(fechaCelda);
							break;
						case 3:
							detalle.setNombreComercialProducto(valorCelda);
							break;
						case 4:
							detalle.setComposicionProducto(valorCelda);
							break;
						case 5:
							detalle.setValor(valorCelda);
							break;
						case 6:
							String[] composicionList = detalle.getComposicionProducto().split(";");
							String[] valorList = detalle.getValor().split(";");
							String[] unidadList = valorCelda.split(";");
							
							if(composicionList.length == valorList.length && composicionList.length == unidadList.length) {
								detalle.setUnidadMedida(valorCelda);
							} else {
								throw new RuntimeException("_invalid_length_cell");
							}
							break;
						case 7:
							detalle.setFormulacion(valorCelda);
							break;
						case 8:
							detalle.setAbreviaturaFormulacion(valorCelda);
							break;
						case 9:
							detalle.setCategoriaToxicologica(valorCelda);
							break;
						case 10:
							CatalogoGeneralCoa colorFranja = null;
							for (CatalogoGeneralCoa color : listaColor) {
								if(color.getNombre().toUpperCase().equals(valorCelda.toUpperCase())) {
									colorFranja = color;
									break;
								}
							}
							
							if(colorFranja != null) {
								detalle.setColorFranja(colorFranja);
							} else {
								throw new RuntimeException("_invalid_color_cell");
							}
							break;
						case 11:
							if(valorCelda.toLowerCase().equals("si") || valorCelda.toLowerCase().equals("no")) {
								Boolean prorroga = (valorCelda.toLowerCase().equals("si")) ? true : false;
								
								detalle.setProrroga(prorroga);
							} else {
								throw new RuntimeException("_invalid_extension_cell");
							}
							
							break;
						default:
							break;
						}
    			    }
    				
    				listaProductos.add(detalle);
    			}
    			rows++;    			
    		}
    		libroPma.close();
    		
    		if(rows > 1) {
    			registroSeleccionado.setListaDetalleProductos(listaProductos);
    			String[] split = event.getFile().getContentType().split("/");
    		    String extension = "."+split[split.length-1];
    		    
    			byte[] contenidoDocumento = event.getFile().getContents();
    			DocumentoPqua documentoRespaldo = new DocumentoPqua();
    			documentoRespaldo.setContenidoDocumento(contenidoDocumento);
    			documentoRespaldo.setNombre(event.getFile().getFileName());
    			documentoRespaldo.setMime("application/vnd.ms-excel");
    			documentoRespaldo.setIdTipoDocumento(TipoDocumentoSistema.PQUA_LISTADO_REGISTROS_PQUA.getIdTipoDocumento());
    			documentoRespaldo.setExtension(extension);
    			
    			registroSeleccionado.setDocumentoAgrocalidad(documentoRespaldo);
    			
    		} else {
    			JsfUtil.addMessageError("Ingrese un archivo válido");
    		}
    		
    		RequestContext.getCurrentInstance().execute("PF('dlgAdjuntarArchivo').hide()");
    		
		} catch (Exception e) {
			listaProductos = new ArrayList<>();
			registroSeleccionado.setListaDetalleProductos(listaProductos);
			
			e.printStackTrace();
			String mensaje = "Error en la lectura del archivo";
			if(e.getMessage().equals("_blank_cell")) {
				mensaje = "La información de cada fila debe estar completa";
			} else if(e.getMessage().equals("_invalid_length_cell")) {
				mensaje = "Verifique que el valor de las columnas Composición del producto, Valor y Unidad de medida tenga igual número de elementos";
			} else if(e.getMessage().equals("_invalid_extension_cell")) {
				mensaje = "Verifique que la columna Prórroga contenga unicamente valores SI o NO";
			} else if(e.getMessage().equals("_invalid_color_cell")) {
				mensaje = "Verifique que la columna Color de franja contenga los valores indicados";
			}
			
			JsfUtil.addMessageError(mensaje);
		} finally {
	        libroPma.close();
	    }
    }
	
	public void validarMesIngreso(RegistroProducto infoRegistro) throws ParseException{		
		if (mesIngresoString == null
				|| anioIngresoString == null)
			return;
		
		String sDate1 = "01/" + mesIngresoString + "/" + anioIngresoString;
		fechaMesIngresoItemSeleccionado = new SimpleDateFormat("dd/MMMM/yyyy", new Locale("es")).parse(sDate1);
		
		List<RegistroProducto> registrosMesIngreso = registroProductoFacade.getPorMesIngreso(fechaMesIngresoItemSeleccionado);
		if(registrosMesIngreso.size() > 0) {
			if(infoRegistro.getId() != null) {
				Integer nroRegistros = 0;
				for (RegistroProducto registroProducto : registrosMesIngreso) {
					if(!registroProducto.getId().equals(infoRegistro.getId())) {
						nroRegistros ++;
					}
				}
				
				if(nroRegistros > 0) {
					JsfUtil.addMessageError("Ya existe información para el mes ingresado");

					mesIngresoString = null;
					anioIngresoString = null;
				}
			} else {
				JsfUtil.addMessageError("Ya existe información para el mes ingresado");
				
				mesIngresoString = null;
				anioIngresoString = null;
			}
		} else {
			infoRegistro.setMesIngreso(fechaMesIngresoItemSeleccionado);
			registroSeleccionado = infoRegistro;
		}
	}
	
	public void validateRegistroProducto(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(registroSeleccionado != null 
				&& (registroSeleccionado.getListaDetalleProductos() == null 
				|| registroSeleccionado.getListaDetalleProductos().size() == 0)){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe agregar el detalle de productos", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void guardarRegistroProducto(RegistroProducto infoRegistro){
		try {
			
			List<ProductoPqua> listaProductosItem = new ArrayList<>();
			listaProductosItem.addAll(registroSeleccionado.getListaDetalleProductos());
			
			DocumentoPqua documentoPlantilla = null;
			if(infoRegistro.getDocumentoAgrocalidad() != null && infoRegistro.getDocumentoAgrocalidad().getContenidoDocumento() != null) {
				documentoPlantilla = infoRegistro.getDocumentoAgrocalidad();
			}
			
			infoRegistro.setMesIngreso(fechaMesIngresoItemSeleccionado);
			registroProductoFacade.guardar(infoRegistro);
			
			for (ProductoPqua producto : listaProductosItem) {
				producto.setRegistroProducto(infoRegistro);
				if(producto.getProductoActualizado() == null) {
					producto.setProductoActualizado(false);
				}
				
				productoPquaFacade.guardar(producto);
			}
			
			infoRegistro.setListaDetalleProductos(listaProductosItem);
			
			if(documentoPlantilla != null) {
				List<DocumentoPqua> documentos = documentosFacade.documentoPorTablaIdPorIdDoc(infoRegistro.getId(), TipoDocumentoSistema.PQUA_LISTADO_REGISTROS_PQUA, "AnexoListadoAgrocalidad");
				if(documentos != null && documentos.size() > 0) {
					DocumentoPqua documentoA = documentos.get(0);
					documentoA.setEstado(false);
					
					documentosFacade.guardar(documentoA);
				}
				
				documentoPlantilla.setIdTabla(infoRegistro.getId());
				documentoPlantilla.setNombreTabla("AnexoListadoAgrocalidad");
				
				documentosFacade.guardarDocumentoAlfresco("DOCUMENTOS_SGA_PLAGUICIDAS", "ANEXOS_AGROCALIDAD", 
						0L, documentoPlantilla, TipoDocumentoSistema.PQUA_DOCUMENTO_ETIQUETA);
			}
			
			if(listaProductosEliminar.size() > 0) {
				for (ProductoPqua producto : listaProductosEliminar) {
					if(producto.getId() != null) {
						producto.setEstado(false);
						productoPquaFacade.guardar(producto);
					}
				}
			}
			
			listaProductosEliminar = new ArrayList<ProductoPqua>();
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void agregarProducto(RegistroProducto infoRegistro){
		registroSeleccionado = infoRegistro;
		
		nuevoProducto = new ProductoPqua();
		
		esEdicion = false;
	}
	
	public void limpiarAdicionarProducto() {
		nuevoProducto = null;
	}

	public void aceptarAdicionarProducto() {
		if(nuevoProducto.getComposicionProducto() != null 
				&& nuevoProducto.getValor() != null 
				&& nuevoProducto.getUnidadMedida() != null){
			String[] composicionList = nuevoProducto.getComposicionProducto().split(";");
			String[] valorList = nuevoProducto.getValor().split(";");
			String[] unidadList = nuevoProducto.getUnidadMedida().split(";");
			
			if(composicionList.length == valorList.length && composicionList.length == unidadList.length) {
			} else {
				JsfUtil.addMessageError("El valor de las columnas Composición del producto, Valor y Unidad de medida deben tener igual número de elementos.");
				return;
			}
		}
		
		nuevoProducto.setRegistroProducto(registroSeleccionado);
		nuevoProducto.setProductoActualizado(false);
		
		if(registroSeleccionado.getId() != null) {
			productoPquaFacade.guardar(nuevoProducto);
		}
		
		if(!esEdicion) {
			if(registroSeleccionado.getListaDetalleProductos() == null) {
				registroSeleccionado.setListaDetalleProductos(new ArrayList<ProductoPqua>());
			}
			registroSeleccionado.getListaDetalleProductos().add(nuevoProducto);
		}

		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		
		JsfUtil.addCallbackParam("addProducto");
	}
	
	public void editarAdicionarProducto(RegistroProducto infoRegistro, ProductoPqua producto){
		registroSeleccionado = infoRegistro;
		
		nuevoProducto = producto;
		
		esEdicion = true;
	}
	
	public void eliminarAdicionarProducto(RegistroProducto infoRegistro, ProductoPqua producto){
		if(producto.getId() != null) {
			listaProductosEliminar.add(producto);
		}
		
		infoRegistro.getListaDetalleProductos().remove(producto);
		
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}
	
	public void validarCedula() {
		Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC,
						nuevoProducto.getCedulaRuc());
		if (cedula != null
				&& cedula.getError().equals(Constantes.NO_ERROR_WS_MAE_SRI_RC)) {
			nuevoProducto.setNombreOperador(cedula.getNombre());
			nuevoProducto.setRepresentanteLegal(null);
		} else {
			ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula.obtenerPorRucSRI(Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC,
							nuevoProducto.getCedulaRuc());
			if (contribuyenteCompleto != null
					&& contribuyenteCompleto.getRazonSocial() != null) {
				nuevoProducto.setNombreOperador(contribuyenteCompleto.getRazonSocial());
				nuevoProducto.setRepresentanteLegal(contribuyenteCompleto.getRepresentanteLegal().getNombre());
			} else {
				nuevoProducto.setNombreOperador(null);
				nuevoProducto.setRepresentanteLegal(null);
				JsfUtil.addMessageError("No se encontró información con la cédula registrada");
			}
		}
	}
	
	public void validateProducto(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(nuevoProducto != null) {
			if(nuevoProducto.getNombreOperador() == null 
					|| nuevoProducto.getNombreOperador() == ""){
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Nombre Operador' es requerido.", null));
			}
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void generarReporteProductos(RegistroProducto infoRegistro) throws IOException {

		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

		response.reset();
		response.setContentType("application/ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ "Anexo 5  Listado PQUA.xlsx" + "\"");
		HSSFWorkbook objWB = new HSSFWorkbook();
		HSSFSheet hoja1 = objWB.createSheet("hoja 1");

		int nroFila = 0;
		
		HSSFRow fila = hoja1.createRow(nroFila);
		HSSFCell celdaPlan = fila.createCell(0);
		celdaPlan.setCellValue(infoRegistro.getInfoMesingreso());
		nroFila++;
		
		List<String> listaCabecera = new ArrayList<>();
		listaCabecera.add("Ruc / Cédula");
		listaCabecera.add("N° de Registro");
		listaCabecera.add("Fecha de registro");
		listaCabecera.add("Nombre comercial del producto");
		listaCabecera.add("Composición del producto");
		listaCabecera.add("Valor");
		listaCabecera.add("Unidad de medida");
		listaCabecera.add("Formulación");
		listaCabecera.add("Abreviatura de formulación");
		listaCabecera.add("Categoría toxicológica (anterior)");
		listaCabecera.add("Color de franja (anterior)");
		listaCabecera.add("Prórroga");
		
		HSSFRow filaHeader = hoja1.createRow(nroFila);
		for(int col=0; col < listaCabecera.size(); col++) {
			HSSFCell celdaHeader = filaHeader.createCell(col);
			celdaHeader.setCellValue(listaCabecera.get(col));
		}
		nroFila++;
		
		for (ProductoPqua item : infoRegistro.getListaDetalleProductos()) {
			int indexCol = 0;
			HSSFRow filaProducto = hoja1.createRow(nroFila);
			
			HSSFCell celdaCedula = filaProducto.createCell(indexCol++);
			celdaCedula.setCellValue(item.getCedulaRuc());
			
			HSSFCell celdaNroRegistro = filaProducto.createCell(indexCol++);
			celdaNroRegistro.setCellValue(item.getNumeroRegistro());
			
			CellStyle cellStyle = objWB.createCellStyle();
			CreationHelper createHelper = objWB.getCreationHelper();
			cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("d/m/yyyy"));
			HSSFCell celdaFechaRegistro = filaProducto.createCell(indexCol++);
			celdaFechaRegistro.setCellValue(item.getFechaRegistro());
			celdaFechaRegistro.setCellStyle(cellStyle);
			
			HSSFCell celdaNombre = filaProducto.createCell(indexCol++);
			celdaNombre.setCellValue(item.getNombreComercialProducto());
			
			HSSFCell celdaComposicion = filaProducto.createCell(indexCol++);
			celdaComposicion.setCellValue(item.getComposicionProducto());
			
			HSSFCell celdaValor = filaProducto.createCell(indexCol++);
			celdaValor.setCellValue(item.getValor());
			
			HSSFCell celdaMedida = filaProducto.createCell(indexCol++);
			celdaMedida.setCellValue(item.getUnidadMedida());
			
			HSSFCell celdaFormulacion = filaProducto.createCell(indexCol++);
			celdaFormulacion.setCellValue(item.getFormulacion());
			
			HSSFCell celdaAbreviatura = filaProducto.createCell(indexCol++);
			celdaAbreviatura.setCellValue(item.getAbreviaturaFormulacion());
			
			HSSFCell celdaCategoria = filaProducto.createCell(indexCol++);
			celdaCategoria.setCellValue(item.getCategoriaToxicologica());
			
			HSSFCell celdaColor = filaProducto.createCell(indexCol++);
			celdaColor.setCellValue(item.getColorFranja().getNombre());
			
			String prorroga = (item.getProrroga()) ? "SI" : "NO";
			HSSFCell celdaProrroga = filaProducto.createCell(indexCol++);
			celdaProrroga.setCellValue(prorroga);
			
			nroFila++;
		}
		
		try {
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			objWB.write(outByteStream);
			byte[] outArray = outByteStream.toByteArray();
			OutputStream output = response.getOutputStream();
			output.write(outArray);
			output.flush();
			output.close();

			fc.responseComplete();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		objWB.close();
	}
	
	public void eliminarRegistroPqua(RegistroProducto infoRegistro){
		try {
			
			List<ProductoPqua> listaProductosItem = new ArrayList<>();
			listaProductosItem.addAll(infoRegistro.getListaDetalleProductos());
			
			DocumentoPqua documentoPlantilla = null;
			if(infoRegistro.getDocumentoAgrocalidad() != null && infoRegistro.getDocumentoAgrocalidad().getId() != null) {
				documentoPlantilla = infoRegistro.getDocumentoAgrocalidad();
			}
			
			infoRegistro.setEstado(false);
			registroProductoFacade.guardar(infoRegistro);
			
			for (ProductoPqua producto : listaProductosItem) {
				producto.setEstado(false);
				
				productoPquaFacade.guardar(producto);
			}
			if(documentoPlantilla != null) {
				documentoPlantilla.setEstado(false);
				
				documentosFacade.guardar(documentoPlantilla);
			}
			
			listaRegistros.remove(infoRegistro);
			
			indexTab = -1;
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
}
