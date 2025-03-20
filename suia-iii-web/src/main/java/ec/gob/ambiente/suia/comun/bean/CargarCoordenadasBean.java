package ec.gob.ambiente.suia.comun.bean;

import index.Intersecado_capa;
import index.Intersecado_coordenada;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.Reproyeccion_entrada;
import index.Reproyeccion_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jfree.util.Log;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.prevencion.registro.proyecto.bean.RegistroProyectoBean;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.RegistroGeneradorDesechoBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.PostgisFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.TipoProyeccion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CargarCoordenadasBean implements Serializable {

    private static final long serialVersionUID = 6500117981086913328L;

    private static final Logger LOGGER = Logger.getLogger(CargarCoordenadasBean.class);

    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private PostgisFacade postgisFacade;

    @Getter
    private List<TipoForma> tiposFormas;

    @Getter
    private String[] nombresTiposFormas;

    @Getter
    @Setter
    private String updateComponentRoute;

    @Getter
    @Setter
    private boolean modalLoadFile;

    @Getter
    private boolean formatoVerificado;

    @Getter
    private boolean transformarFormato;

    @Getter
    @Setter
    private List<CoordinatesWrapper> coordinatesWrappers;

    @Getter
    private List<TipoProyeccion> tiposProyecciones;

    private List<TipoProyeccion> tiposProyeccionesFiltradas;

    @Getter
    @Setter
    private TipoProyeccion tipoProyeccion;

    @Getter
    private UploadedFile uploadedFile;

    @Getter
    private boolean transformarFormatoalerta;
    
    private byte[] transformedFile;

    private byte[] plantillaCoordenadas;
    
    private byte[] plantillaCoordenadasGeograficas;
    
    private byte[] plantillaCoordenadasImplantacion;

    private byte[] ayudaCoordenadas;
    
    @ManagedProperty(value = "#{registroProyectoBean}")
    @Setter
    @Getter
    private RegistroProyectoBean registroProyectoBean;
    
    @EJB
    private CrudServiceBean crudServiceBean;
    
    @Setter
    @ManagedProperty(value = "#{adicionarUbicacionesBean}")
    protected AdicionarUbicacionesBean adicionarUbicacionesBean;
    
    @Setter
    @Getter
    @ManagedProperty(value = "#{registroGeneradorDesechoBean}")
    private RegistroGeneradorDesechoBean registroGeneradorDesechoBean;
    
    @Getter
    private String mensajeErrorCoordenada="";
    
    @Getter
    @Setter
    private boolean verUbicacion=true;
    
    @Getter
    @Setter
    private String formato="";
    
    @Getter
    @Setter
    private String zonaDescripcion="";
    
    @Getter
    protected boolean dialogWidgetVarCoordenadas;
    
    @EJB
	private SecuenciasFacade secuenciasFacade;
    
    public String codigoProyecto="";

    @PostConstruct
    public void init() {
        coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
        tiposFormas = proyectoLicenciamientoAmbientalFacade.getTiposFormas();
        nombresTiposFormas = new String[tiposFormas.size()];
        int pos = 0;
        for (TipoForma tipoForma : tiposFormas) {
            nombresTiposFormas[pos++] = tipoForma.getNombre();
        }
        this.updateComponentRoute = ":form:containerCoordenadas";
        this.modalLoadFile = true;

        tiposProyecciones = postgisFacade.getTiposProyeccionesSoportadas();

        try {
            plantillaCoordenadas = documentosFacade
                    .descargarDocumentoPorNombreYDirectorioBase(Constantes.PLANTILLA_COORDENADAS, null);
        } catch (Exception e) {
        }
        try {
        	plantillaCoordenadasGeograficas = documentosFacade
                    .descargarDocumentoPorNombreYDirectorioBase(Constantes.PLANTILLA_COORDENADAS_GEOGRAFICAS, null);
        } catch (Exception e) {
        }
        try {
        	plantillaCoordenadasImplantacion = documentosFacade
                    .descargarDocumentoPorNombreYDirectorioBase(Constantes.PLANTILLA_COORDENADAS_IMPLANTACION, null);
        } catch (Exception e) {
        }

        try {
            ayudaCoordenadas = documentosFacade.descargarDocumentoPorNombreYDirectorioBase(Constantes.AYUDA_COORDENADAS,null);
        } catch (Exception e) {
        }
        dialogWidgetVarCoordenadas=false;
        codigoProyecto="";
    }

    public void reset() {
        coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
        tipoProyeccion = null;
        formatoVerificado = false;
        transformarFormato = false;
        transformedFile = null;
        transformarFormatoalerta=true;
    }

    public void transformar() {
        Workbook workbook = null;
        Iterator<Row> rowIterator = null;
        try {
            workbook = new HSSFWorkbook(uploadedFile.getInputstream());
            Sheet sheet = workbook.getSheetAt(0);
            rowIterator = sheet.iterator();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (rowIterator != null)
            rowIterator.next();

        try {
            List<String> coordinates = new ArrayList<String>();
            for (CoordinatesWrapper cw : coordinatesWrappers) {
                coordinates.clear();
                for (Coordenada coordenada : cw.getCoordenadas()) {
                    coordinates.add(coordenada.toString());
                }
                List<String> coordinatesTransformed = postgisFacade.transformarCoordenadas(coordinates, tipoProyeccion);
                for (int i = 0; i < coordinatesTransformed.size(); i++) {
                    String string = coordinatesTransformed.get(i);
                    String x = string.split(" ")[0];
                    String y = string.split(" ")[1];
                    cw.getCoordenadas().get(i).setX((new Double(x)));
                    cw.getCoordenadas().get(i).setY((new Double(y)));

                    if (rowIterator != null) {
                        Row row = rowIterator.next();
                        Iterator<Cell> cellIterator = row.cellIterator();
                        cellIterator.next().getNumericCellValue();
                        cellIterator.next().setCellValue(new Double(x));
                        cellIterator.next().setCellValue(new Integer(y));
                        cellIterator.next().getStringCellValue();
                    }
                }
            }
        } catch (NumberFormatException e) {
            JsfUtil.addMessageError("Las coordenadas no pueden ser transformadas desde el formato especificado.");
            coordinatesWrappers.clear();
        } catch (Exception e) {
            JsfUtil.addMessageError("Ha ocurrido un error al intentar transformar las coordenadas.");
            coordinatesWrappers.clear();
            LOGGER.error("Error en la trasnformacion de coordenadas", e);
            return;
        }

        if (workbook != null) {
            try {
                File tempFile = File.createTempFile(uploadedFile.getFileName(), ".xls");
                workbook.write(new FileOutputStream(tempFile));
                transformedFile = documentosFacade.getBytesFromFile(tempFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        formatoVerificado = true;
    }

    public void solititarTransformacion() {
        transformarFormato = true;
    }

    public void handleFileUpload(final FileUploadEvent event) {
        formatoVerificado = false;
        transformarFormato = false;
        transformarFormatoalerta=true;
        transformedFile = null;

        coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
        CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper();
        coordinatesWrappers.add(coordinatesWrapper);
        boolean formatoCorrecto=true;
        int rows = 0;
        
        try {
            uploadedFile = event.getFile();
            Workbook workbook = new HSSFWorkbook(uploadedFile.getInputstream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();                
                boolean isEmptyRow = true;
                for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK
                            && StringUtils.isNotBlank(cell.toString())) {
                        isEmptyRow = false;
                    }
                }
                if (isEmptyRow)
                    break;

                if (rows > 0) {
                    
                    Iterator<Cell> cellIterator = row.cellIterator();
                    Coordenada coordenada = new Coordenada();
                    coordenada.setOrden((int) cellIterator.next().getNumericCellValue());
                    coordenada.setX((double)cellIterator.next().getNumericCellValue());
                    coordenada.setY((double)cellIterator.next().getNumericCellValue());
                    String tipo = JsfUtil.getStringAsAnyPrimaryStrings(cellIterator.next().getStringCellValue(),
                            getNombresTiposFormas());

                    TipoForma tipoForma = getTipoForma(tipo);
                    if (coordinatesWrapper.getTipoForma() == null)
                        coordinatesWrapper.setTipoForma(tipoForma);
                    else if (!coordinatesWrapper.getTipoForma().equals(tipoForma)
                            || coordinatesWrapper.getTipoForma().getId().intValue() == TipoForma.TIPO_FORMA_PUNTO
                            || coordenada.getOrden() == 1) {
                        coordinatesWrapper = new CoordinatesWrapper(null, tipoForma);
                        coordinatesWrappers.add(coordinatesWrapper);
                    }
                                        
                    String zona = "";
                    try {
                        zona = cellIterator.next().getStringCellValue().toUpperCase();
                        coordenada.setZona(zona);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }                    
                    
                    String descripcion = " ";
                    try {
                        descripcion = cellIterator.next().getStringCellValue();
                        coordenada.setDescripcion(descripcion);
                    } catch (Exception ex) {
                    }
                    
                    if (JsfUtil.isStringInPrimaryStrings(tipo, getNombresTiposFormas())) {
                        if (coordenada.isDataComplete()){
                            coordinatesWrapper.getCoordenadas().add(coordenada);
                        }
                        else {
                            showErrorClearCoordenadas("La fila " + rows
                                    + " no cuenta con todos los datos necesarios. Por favor corrija.");
                            return;
                        }
                    } else {
                        showErrorClearCoordenadas("Se ha encontrado '" + tipo + "' en la fila " + rows
                                + " correspondiente a Tipo. Los posibles valores son: Polígono, Línea y Punto. Por favor corrija.");
                        return;
                    }

                }
                rows++;
            }
            //OBTENER UBICACIÓN
            
            if(formatoCorrecto){
            	if(registroProyectoBean.getProyecto().getCodigo()==null && registroProyectoBean.getProyecto().getNombre()!=null){
            	registroProyectoBean.getProyecto().setCodigo(secuenciasFacade.getSecuenciaProyecto());
            	}
                coordinatesWrappers = reproyectarTodasCoordenadas(coordinatesWrappers);
                HashMap<String, Double> varUbicacion = new HashMap<String, Double>();
                varUbicacion = retornarParroquias(coordinatesWrappers);

                // Recorremos el hashMap y mostramos por pantalla el par valor y
                // clave
                Iterator it = varUbicacion.entrySet().iterator();
                List<String> parroquia = new ArrayList<String>();
                Double valor = null;
                while (it.hasNext()) {
                    Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
                        parroquia.add(e.getKey());
                        valor = e.getValue();
                }
                // cargar ubicación
                
                boolean verUbicacionRetce=(Boolean)JsfUtil.devolverObjetoSession("verUbicacionRetce")!=null?true:false;
                if(parroquia.size()>0 && (registroProyectoBean.getProyecto().getNombre()!= null || verUbicacionRetce)){
                	registroGeneradorDesechoBean.setGeneradorDesechosPeligrosos(null);
                	cargarUbicacionProyecto(varUbicacion);
                	if(verUbicacionRetce)
                		JsfUtil.cargarObjetoSession("verUbicacionRetce",null);
                }else if(registroProyectoBean.getProyecto().getNombre() != null){
                	registroGeneradorDesechoBean.setGeneradorDesechosPeligrosos(null);
                    verUbicacion=true;
                    coordinatesWrappers.clear();
                    adicionarUbicacionesBean.getUbicacionesSeleccionadas().clear();
                    adicionarUbicacionesBean.getListParroquiasGuardar().clear();
                    adicionarUbicacionesBean.getParroquiaSeleccionadas().clear();
                    RequestContext.getCurrentInstance().update("form:ubicacionGeograficaContainer");
                    JsfUtil.addMessageError("Error de auto-intersección y/o topología del objeto geográfico subido.");
                    dialogWidgetVarCoordenadas=false;
                }
                
                if(parroquia.size()>0 && registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getSolicitud()!=null)
                {
                	cargarUbicacionProyecto(varUbicacion);
                }else if(registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getSolicitud()!=null)
                {
                	verUbicacion=true;
                    coordinatesWrappers.clear();
                    adicionarUbicacionesBean.getUbicacionesSeleccionadas().clear();
                    adicionarUbicacionesBean.getListParroquiasGuardar().clear();
                    adicionarUbicacionesBean.getParroquiaSeleccionadas().clear();
                    RequestContext.getCurrentInstance().update("form:ubicacionGeograficaContainer");
                    JsfUtil.addMessageError("Error de auto-intersección y/o topología del objeto geográfico subido.");
                    dialogWidgetVarCoordenadas=false;
                	
                }
            //fin cargar ubicación
                for (int i = 0; i < coordinatesWrappers.size(); i++) {
                    if (!validate(coordinatesWrappers.get(i), i + 1)) {
                        uploadedFile = null;
                        return;
                    }
                }
                
                
            }else {
                coordinatesWrappers.clear();
                verUbicacion=true;
                adicionarUbicacionesBean.getUbicacionesSeleccionadas().clear();
                adicionarUbicacionesBean.getListParroquiasGuardar().clear();
                adicionarUbicacionesBean.getParroquiaSeleccionadas().clear();
                RequestContext.getCurrentInstance().update("form:ubicacionGeograficaContainer");
                JsfUtil.addMessageError("Error procesando el archivo excel de coordenadas. Por favor revise su estructura e intente nuevamente.");
                dialogWidgetVarCoordenadas=false;
            }            

        } catch (Exception e) {
            coordinatesWrappers.clear();
            verUbicacion=true;
            adicionarUbicacionesBean.getUbicacionesSeleccionadas().clear();
            adicionarUbicacionesBean.getListParroquiasGuardar().clear();
            adicionarUbicacionesBean.getParroquiaSeleccionadas().clear();
            RequestContext.getCurrentInstance().update("form:ubicacionGeograficaContainer");
            dialogWidgetVarCoordenadas=false;
            LOGGER.error("Error procesando el excel de coordenadas", e);
            String message = "";
            if(!mensajeErrorCoordenada.equals("")){
            message=mensajeErrorCoordenada;
            }else{
            if (rows == 0)
                message = "Error procesando el archivo excel de coordenadas. Por favor revise su estructura e intente nuevamente.";
            else
                message = "Error procesando el archivo excel de coordenadas en la fila " + rows
                        + ". Por favor corrija e intente nuevamente.";
            }
            JsfUtil.addMessageError(message);
            return;
        }
    }
    public void formato(){
        RequestContext.getCurrentInstance().update("form:containerCoordenadas");
    }
    
    public void formatoArcom(List<CoordinatesWrapper> coordendas){
    	formato="WGS84";
    	coordinatesWrappers=coordendas;
        RequestContext.getCurrentInstance().update("form:containerCoordenadas");
    }
    
    public HashMap<String, Double>retornarParroquias(List<CoordinatesWrapper> coordinatesWrappers){
    	codigoProyecto=registroProyectoBean.getProyecto().getCodigo();
        String coordUbicacion="";
        int tipoForma=0;
        //OBTENER UBICACIÓN
        HashMap<String, Double>varUbicacion= new HashMap<String,Double>();
        Integer orden=2;
        for(int i=0;i<=coordinatesWrappers.size()-1;i++){
            String tipoF="";
            String parroquia="";
            Double valorParroquia=0.0;
            for (int j = 0; j <=coordinatesWrappers.get(i).getCoordenadas().size()-1; j++) {
                if(j==0){
                	coordUbicacion = coordinatesWrappers.get(i).getCoordenadas().get(j).getX().toString().replace("," , ".")+" "+coordinatesWrappers.get(i).getCoordenadas().get(j).getY().toString().replace("," , ".");
                    tipoForma=coordinatesWrappers.get(i).getTipoForma().getId();
                }else {
                    coordUbicacion = coordUbicacion +","+coordinatesWrappers.get(i).getCoordenadas().get(j).getX().toString().replace("," , ".")+" "+coordinatesWrappers.get(i).getCoordenadas().get(j).getY().toString().replace("," , ".");
                }
            }
            //aqui
            if (!coordUbicacion.equals("")) {
                if(tipoForma==(TipoForma.TIPO_FORMA_PUNTO)){
                    tipoF="pu";
                }else if(tipoForma==(TipoForma.TIPO_FORMA_LINEA)){
                    tipoF="li";
                } else {
                    tipoF="po";
                }
                // Intersecado_entrada inter= new Intersecado_entrada(Constantes.getUserWebServicesSnap(), codigoProyecto!=null?codigoProyecto:" ",tipoF, coordUbicacion, "dp");
                // Intersecado_entrada inter= new Intersecado_entrada(Constantes.getUserWebServicesSnap(), codigoProyecto.trim()!=""?codigoProyecto:" ",tipoF, coordUbicacion, "dp");
                Intersecado_entrada inter = new Intersecado_entrada();
                inter.setU(Constantes.getUserWebServicesSnap());
                inter.setIsla(Constantes.SIGLAS_INSTITUCION + "-RA");
                inter.setTog(tipoF);
                inter.setXy(coordUbicacion);
                inter.setShp("dp");
                // Intersecado_entrada inter= new Intersecado_entrada("n8ignp/LypTH", "MAE-RA-2016-2201",tipoF, coordUbicacion, "dp,pn,rs,bp,zi,rb,za,pf,qv");
                SVA_Reproyeccion_IntersecadoPortTypeProxy wsInter= new SVA_Reproyeccion_IntersecadoPortTypeProxy();
                wsInter.setEndpoint(Constantes.getInterseccionesWS());
                Intersecado_resultado[]intRest;
                try {
                    intRest=wsInter.interseccion(inter);
                if (intRest[0].getInformacion().getError() != null) {
                    mensajeErrorCoordenada = intRest[0].getInformacion().getError().toString();
                }
                
                for (Intersecado_capa intersecado_capa : intRest[0].getCapa()) {
                    String capaNombre=intersecado_capa.getNombre();   
                    if(intersecado_capa.getError()!=null){
                        mensajeErrorCoordenada = intersecado_capa.getError().toString();
                        JsfUtil.addMessageError(mensajeErrorCoordenada);
                    }
                        Intersecado_coordenada[] intersecadoCoordenada=intersecado_capa.getCruce();
                        if (intersecadoCoordenada != null){
                        	
                            for (Intersecado_coordenada intersecado_coordenad : intersecadoCoordenada) {
                                if (intersecado_coordenad.getValor() != null) {
                                    valorParroquia = Double.valueOf(intersecado_coordenad.getValor());
                                }
                                if (valorParroquia >= 0) {
                                    if (capaNombre.equals("dpa")) {
                                        parroquia=intersecado_coordenad.getObjeto();
                                        if (varUbicacion.get(parroquia) != null) {
                                            if (valorParroquia >= varUbicacion.get(parroquia)) {
                                                //varUbicacion.put(parroquia,valorParroquia);
                                            	varUbicacion.put(parroquia,orden.doubleValue());
                                            }
                                        } else {
                                            //varUbicacion.put(parroquia,valorParroquia);
                                        	varUbicacion.put(parroquia,orden.doubleValue());
                                        }
                                    }
                                }
                                orden ++;
                            }
                        }
                    }
                    tipoF = "";
                    tipoForma = 0;
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        String inec=null;
        Double ord=0.0;
        String logInecNoValidos="";
        for (Map.Entry<String, Double> entry : varUbicacion.entrySet()) {
            if(entry.getKey().length()==6 && (inec==null || ord > (Double)entry.getValue()))
            {
                try {
                    Integer.valueOf(entry.getKey());//valida si es parroquia de 6 digitos numericos
                    inec=entry.getKey();
                    ord=(Double)entry.getValue();
                } catch (Exception e) {
                	logInecNoValidos+=entry.getKey()+" "+entry.getValue()+"; ";
                }                
            }else if(entry.getKey().length()==4 && (inec==null || ord > (Double)entry.getValue()))
            {
                try {
                    inec=entry.getKey();
                    ord=(Double)entry.getValue();
                } catch (Exception e) {
                	logInecNoValidos+=entry.getKey()+" "+entry.getValue()+"; ";
                }                
            }else
            	logInecNoValidos+=entry.getKey()+" "+entry.getValue()+"; ";
        }
        if(inec!=null)
        	varUbicacion.put(inec,1.0);
        else{
        	JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
        	System.out.println("No se valida ente responsable. Codigos Inec No Validos: "+logInecNoValidos);
        }
        	
        return varUbicacion;
    }
    
    public List<CoordinatesWrapper> reproyectarTodasCoordenadas(List<CoordinatesWrapper> coordinatesWrappers){
        String coordUbicacion="";
        codigoProyecto=registroProyectoBean.getProyecto().getCodigo();
        if(codigoProyecto==null || codigoProyecto.equals("")){
        	codigoProyecto=registroGeneradorDesechoBean.getGeneradorDesechosPeligrosos().getSolicitud();
        }
        for(int i=0;i<=coordinatesWrappers.size()-1;i++){
            for (int j = 0; j <=coordinatesWrappers.get(i).getCoordenadas().size()-1; j++) {
                if(j==0){                    
                    coordUbicacion = coordinatesWrappers.get(i).getCoordenadas().get(j).getX().toString().replace("," , ".")+" "+coordinatesWrappers.get(i).getCoordenadas().get(j).getY().toString().replace("," , ".")+" "+coordinatesWrappers.get(i).getCoordenadas().get(j).getZona();
                }else {
                    coordUbicacion = coordUbicacion +","+coordinatesWrappers.get(i).getCoordenadas().get(j).getX().toString().replace("," , ".")+" "+coordinatesWrappers.get(i).getCoordenadas().get(j).getY().toString().replace("," , ".")+" "+coordinatesWrappers.get(i).getCoordenadas().get(j).getZona();
                }
            }
            if(formato==null || formato.equals(""))
                formato="WGS84";
            Reproyeccion_entrada reproyeccion_entrada = new Reproyeccion_entrada(Constantes.getUserWebServicesSnap(), codigoProyecto!=null?codigoProyecto:"",formato, coordUbicacion);
            
            try {
                SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
                Reproyeccion_resultado[] resultado;
                resultado=ws.reproyeccion(reproyeccion_entrada);
                if(resultado[0].getCoordenada()[0].getError()==null){
                for (int j = 0; j <=coordinatesWrappers.get(i).getCoordenadas().size()-1; j++) {
                    String[] parts=resultado[0].getCoordenada()[j].getXy().split(" ");
                    String x=parts[0];
                    String y=parts[1];
                    coordinatesWrappers.get(i).getCoordenadas().get(j).setX(Double.valueOf(x));
                    coordinatesWrappers.get(i).getCoordenadas().get(j).setY(Double.valueOf(y));
                    coordinatesWrappers.get(i).getCoordenadas().get(j).setZona("17S");
                    if(coordinatesWrappers.get(i).getCoordenadas().get(j).getDescripcion()==null){
                        coordinatesWrappers.get(i).getCoordenadas().get(j).setDescripcion("");
                        }
                    }
                } else {
                    coordinatesWrappers.clear();
                    verUbicacion=true;
                    RequestContext.getCurrentInstance().update("form:ubicacionGeograficaContainer");
                    adicionarUbicacionesBean.getListParroquiasGuardar().clear();
                    adicionarUbicacionesBean.getUbicacionesSeleccionadas().clear();
                    adicionarUbicacionesBean.getParroquiaSeleccionadas().clear();
                    dialogWidgetVarCoordenadas=false;
                    String message = "Error procesando el archivo excel de coordenadas. Por favor revise su estructura e intente nuevamente.";
                    JsfUtil.addMessageError(message);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }    
        return coordinatesWrappers;
    } 

    public void cargarUbicacionProyecto(HashMap<String, Double> parroquia){
        adicionarUbicacionesBean.getUbicacionesSeleccionadas().clear();
        adicionarUbicacionesBean.getParroquiaSeleccionadas().clear();
        
        Iterator it = parroquia.entrySet().iterator();
        Integer orden=0;
        String inec="";
        while (it.hasNext()) {
            Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
                inec  = e.getKey();
                orden = e.getValue().intValue();
                if (!inec.equals("")) { 
                    Map<String, Object> parametros = new HashMap<String, Object>();
                    // parametros.put("codeInec", inec.substring(0, (inec.length()==6?4:inec.length())));
                    parametros.put("codeInec", inec);
                    List<UbicacionesGeografica> lista = crudServiceBean.findByNamedQueryGeneric(UbicacionesGeografica.FIND_PARROQUIA,parametros);
                    if(lista.size()>0){
                    	lista.get(0).setOrden(orden);
                        // adicionarUbicacionesBean.setUbicacionSeleccionada(lista.get(0), 2);
                    	adicionarUbicacionesBean.setUbicacionSeleccionada(lista.get(0), 3);
                        adicionarUbicacionesBean.agregarUbicacion();
                    }
                }
        } 
        
        if(adicionarUbicacionesBean.getUbicacionesSeleccionadas().size()>0){
            verUbicacion=true;
            RequestContext.getCurrentInstance().update("form:ubicacionGeograficaContainer");
        }else {
            verUbicacion=false;
            RequestContext.getCurrentInstance().update("form:ubicacionGeograficaContainer");
        }
    }

    public StreamedContent getPlantillaCoordenadas() throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (plantillaCoordenadas != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaCoordenadas));
                content.setName(Constantes.PLANTILLA_COORDENADAS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;

    }
    
    public StreamedContent getPlantillaCoordenadasGeograficas() throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (plantillaCoordenadasGeograficas != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaCoordenadasGeograficas));
                content.setName(Constantes.PLANTILLA_COORDENADAS_GEOGRAFICAS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;

    }
    
    public StreamedContent getPlantillaCoordenadasImplantacion() throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (plantillaCoordenadasImplantacion != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaCoordenadasImplantacion));
                content.setName(Constantes.PLANTILLA_COORDENADAS_IMPLANTACION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;

    }

    public StreamedContent getAyudaCoordenadas() throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (ayudaCoordenadas != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(ayudaCoordenadas));
                content.setName(Constantes.AYUDA_COORDENADAS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;

    }

    private boolean validate(CoordinatesWrapper coordinatesWrapper,int shapeOrder) {
        Collections.sort(coordinatesWrapper.getCoordenadas());
        int current = 1;
        List<Coordenada>listaCoordenadasPuntos= new ArrayList<Coordenada>();
        Coordenada aux;
        Coordenada actual;
        boolean coord = true;
        if(coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_PUNTO))
        {
            for (CoordinatesWrapper val: coordinatesWrappers) {
                if(val.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_PUNTO))
                listaCoordenadasPuntos.addAll(val.getCoordenadas());
            }
            if(listaCoordenadasPuntos.size()>=4){
                for (int i = 0; i < 4; i++) {
                    actual = listaCoordenadasPuntos.get(i);
                    if (i == 0) {
                        for (int j = i + 1; j <= 3; j++) {
                            aux = listaCoordenadasPuntos.get(j);
                            if (actual.getX().equals(aux.getX()) && actual.getY().equals(aux.getY())) {
                                if (!(j == 3)) {
                                    coord = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (coord) {
                        for (int j = i + 1; j <= 3; j++) {
                            aux = listaCoordenadasPuntos.get(j);
                            if (actual.getX().equals(aux.getX())&& actual.getY().equals(aux.getY())) {
                                if (!(j == 3)) {
                                    coord = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }else {
                coord=false;
            }
            if(!coord){
                showErrorClearCoordenadas("Ingresar mínimo 4 pares de coordenadas UTM que forme un punto que represente el área útil del proyecto, obra o actividad; con la mayor precisión posible en la toma de coordenadas.");
                adicionarUbicacionesBean.getUbicacionesSeleccionadas().clear();
                adicionarUbicacionesBean.getParroquiaSeleccionadas().clear();
                RequestContext.getCurrentInstance().update("form:ubicacionGeograficaContainer");
                return false;
            }
        }       
        else if (registroProyectoBean.getProyecto().getNombre() != null) {
            for (Coordenada coordenada : coordinatesWrapper.getCoordenadas()) {
                if (coordinatesWrappers.size() > 3 || coordinatesWrapper.getCoordenadas().size() > 3) {
                    for (int i = 0; i < 4; i++) {
                        actual = coordinatesWrapper.getCoordenadas().get(i);
                        if (i == 0) {
                            for (int j = i + 1; j <= 3; j++) {
                                aux = coordinatesWrapper.getCoordenadas().get(j);
                                if (actual.getX().equals(aux.getX())&& actual.getY().equals(aux.getY())) {
                                    if (!(j == 3)) {
                                        coord = false;
                                        break;
                                    }
                                }
                            }
                        } 
                        if (coord) {
                            for (int j = i + 1; j <= 3; j++) {
                                aux = coordinatesWrapper.getCoordenadas().get(j);
                                if (actual.getX().equals(aux.getX())&& actual.getY().equals(aux.getY())) {
                                     if (!(j == 3)) {
                                         coord = false;
                                         break;
                                     }
                                }
                            }
                        }
                    }
                    if (coord) {
                        if (coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_PUNTO)&& coordinatesWrapper.getCoordenadas().size() < 3
                                && coordenada.getOrden().intValue() != current) {
                            showErrorClearCoordenadas("Fila con valor Shape "+ coordenada.getOrden()
                                    + ", es inválida, para el tipo '"+ coordinatesWrapper.getTipoForma().getNombre()
                                    + "' siempre debe ser 4.");
                            return false;
                        }
                        if (coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO)    && coordinatesWrapper.getCoordenadas().size() < 3
                                || coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_LINEA)    && coordinatesWrapper.getCoordenadas().size() < 3) {
                            if (coordenada.getOrden().intValue() != current) {
                                showErrorClearCoordenadas("Fila con valor Shape "+ coordenada.getOrden()
                                        + ", es inválida, debe especificar todos los puntos consecutivamente.");
                                return false;
                            }
                            if (coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO)
                                    && coordinatesWrapper.getCoordenadas().size() < 3) {
                                showErrorClearCoordenadas("El tipo "+ coordinatesWrapper.getTipoForma().getNombre()
                                        + ", Ingresar mínimo 4 pares de coordenadas UTM que forme un polígono que represente el área útil del proyecto, obra o actividad; con la mayor precisión posible en la toma de coordenadas.");
                                return false;
                            }
                            if (coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_LINEA)
                                    && coordinatesWrapper.getCoordenadas().size() < 3) {
                                showErrorClearCoordenadas("El tipo "+ coordinatesWrapper.getTipoForma().getNombre()
                                        + ", Ingresar mínimo 4 pares de coordenadas UTM que forme un polígono que represente el área útil del proyecto, obra o actividad; con la mayor precisión posible en la toma de coordenadas.");
                                return false;
                            }
                            current++;
                        }
                    }else {
                        showErrorClearCoordenadas("Ingresar mínimo 4 pares de coordenadas UTM que forme un polígono que represente el área útil del proyecto, obra o actividad; con la mayor precisión posible en la toma de coordenadas.");
                        return false;
                    }
                } else {
                    showErrorClearCoordenadas("El tipo "+ coordinatesWrapper.getTipoForma().getNombre()
                            + ", Ingresar mínimo 4 pares de coordenadas UTM que forme un polígono que represente el área útil del proyecto, obra o actividad; con la mayor precisión posible en la toma de coordenadas.");
                    return false;
                }
            }
        } else {
            Collections.sort(coordinatesWrapper.getCoordenadas());
            current = 1;
            for (Coordenada coordenada : coordinatesWrapper.getCoordenadas()) {
                if (coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_PUNTO)
                        && coordenada.getOrden().intValue() != current) {
                    showErrorClearCoordenadas("Fila con valor Shape "+ coordenada.getOrden()
                            + ", es inválida, para el tipo '"+ coordinatesWrapper.getTipoForma().getNombre()
                            + "' siempre debe ser 1.");
                    return false;
                }
                if (coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO)
                        || coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_LINEA)) {
                    if (coordenada.getOrden().intValue() != current) {
                        showErrorClearCoordenadas("Fila con valor Shape "+ coordenada.getOrden()
                                + ", es inválida, debe especificar todos los puntos consecutivamente.");
                        return false;
                    }
                    if (coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO)
                            && coordinatesWrapper.getCoordenadas().size() < 3) {
                        showErrorClearCoordenadas("El tipo "+ coordinatesWrapper.getTipoForma().getNombre()
                                + ", debe estar conformado por al menos 3 puntos. Por favor, corrija.");
                        return false;
                    }
                    if (coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_LINEA)
                            && coordinatesWrapper.getCoordenadas().size() < 2) {
                        showErrorClearCoordenadas("El tipo "+ coordinatesWrapper.getTipoForma().getNombre()
                                + ", debe estar conformado por al menos 2 puntos. Por favor, corrija.");
                        return false;
                    }
                    current++;
                }
            }
    }
        Coordenada first = coordinatesWrapper.getCoordenadas().get(0);
        if (coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO)
                || coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_LINEA)) {
            Coordenada last = coordinatesWrapper.getCoordenadas().get(coordinatesWrapper.getCoordenadas().size() - 1);
            if (coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO)
                    && !first.isEqualsPoints(last)) {
                showErrorClearCoordenadas("En el grupo de coordenadas "
                        + shapeOrder+ ": para formar un polígono, las coordenadas primera y última deben coincidir.");
                return false;
            }
            if (coordinatesWrapper.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_LINEA)
                    && first.isEqualsPoints(last)) {
                showErrorClearCoordenadas("En el grupo de coordenadas "
                        + shapeOrder + ": para formar una línea, las coordenadas primera y última nunca deben coincidir, en este caso es un polígono.");
                return false;
            }
        }
        dialogWidgetVarCoordenadas=true;
        RequestContext.getCurrentInstance().update("form:dialogWidgetVarCoordenadas");
        return true;
    }
    
    private void showErrorClearCoordenadas(String message) {
        JsfUtil.addMessageError(message);
        coordinatesWrappers.clear();
    }

    private TipoForma getTipoForma(String nombre) {
        for (TipoForma tipoForma : getTiposFormas()) {
            if (JsfUtil.comparePrimaryStrings(tipoForma.getNombre(), nombre))
                return tipoForma;
        }
        return null;
    }

    public Documento generateDocumentFromUpload() {
        try {
            byte[] contenidoDocumento = transformedFile == null ? uploadedFile.getContents() : transformedFile;
            Documento documento = new Documento();
            documento.setContenidoDocumento(contenidoDocumento);
            documento.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
            documento.setIdTable(0);
            documento.setMime("application/vnd.ms-excel");
            documento.setExtesion(".xls");
            documento.setNombre(uploadedFile.getFileName());
            return documento;
        } catch (Exception e) {
            return null;
        }
    }

    public List<TipoProyeccion> cargarTiposProyecciones(String param) {
        if (param == null || param.isEmpty())
            return tiposProyecciones;
        else {
            if (tiposProyeccionesFiltradas == null) {
                tiposProyeccionesFiltradas = new ArrayList<>();
                String[] tipos = param.split(";");
                for (TipoProyeccion tipoProyeccion : tiposProyecciones) {
                    for (String string : tipos) {
                        if (tipoProyeccion.getNombre().toLowerCase().startsWith(string.trim().toLowerCase())) {
                            tiposProyeccionesFiltradas.add(tipoProyeccion);
                            break;
                        }
                    }
                }
            }
            return tiposProyeccionesFiltradas;
        }
    }

    public void setTipoProyeccionByName(String name) {
        for (TipoProyeccion tipoProyeccion : tiposProyecciones) {
            if(tipoProyeccion.getNombre().equals(name)) {
                this.tipoProyeccion = tipoProyeccion;
                break;
            }
        }
    }
    
    public void solititarTransformacionalerta() {
        transformarFormatoalerta = false;
    } 
    
    public String guardarFormato(){
    	if (formato=="WGS84"){
    		return formato;
    	}else{
    		formato="PSAD56";
    	}
    	return formato;
    }
    
    public void limpiarCoorUbicacion()
    {
    	coordinatesWrappers.clear();
    	adicionarUbicacionesBean.getUbicacionesSeleccionadas().clear();
        adicionarUbicacionesBean.getParroquiaSeleccionadas().clear();
    }

    /**
     * metodo para oibtener la parroquia por las coordenadas para el retce
     * @param coordenadaX
     * @param coordenadaY
     * @return
     */
    public List<String> getParroquiaByCoordenadas(String coordenadaX, String coordenadaY) {
        formatoVerificado = false;
        transformarFormato = false;
        transformarFormatoalerta=true;
        transformedFile = null;
        coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
        CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper();
        //coordinatesWrappers.add(coordinatesWrapper);
        boolean formatoCorrecto=true;
        int rows = 0;

        Double x=Double.valueOf(coordenadaX), y=Double.valueOf(coordenadaY);
        Coordenada coordenada = new Coordenada();
        coordenada.setOrden(1);
        coordenada.setX(x);
        coordenada.setY(y);
        TipoForma tipoForma = getTipoForma("Punto");
        coordinatesWrapper.setTipoForma(tipoForma);
        coordinatesWrapper = new CoordinatesWrapper(null, tipoForma);
        coordinatesWrappers.add(coordinatesWrapper);
       
        
                                        
                    String zona = "17s";
                    try {
                        coordenada.setZona(zona);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }                    
                    
                    String descripcion = " ";
                    try {
                        coordenada.setDescripcion(descripcion);
                    } catch (Exception ex) {
                    }
                    
                        if (coordenada.isDataComplete()){
                            coordinatesWrapper.getCoordenadas().add(coordenada);
                        }
                        else {
                            showErrorClearCoordenadas("La fila " + rows
                                    + " no cuenta con todos los datos necesarios. Por favor corrija.");
                            return null;
                        }
               
            //OBTENER UBICACIÓN
            	
            	int i=0;
                for (int j = 0; j <=coordinatesWrappers.get(i).getCoordenadas().size()-1; j++) {
                   
                    coordinatesWrappers.get(i).getCoordenadas().get(j).setX(Double.valueOf(x));
                    coordinatesWrappers.get(i).getCoordenadas().get(j).setY(Double.valueOf(y));
                    coordinatesWrappers.get(i).getCoordenadas().get(j).setZona("17S");
                    if(coordinatesWrappers.get(i).getCoordenadas().get(j).getDescripcion()==null){
                        coordinatesWrappers.get(i).getCoordenadas().get(j).setDescripcion("");
                        }
                }
                
                HashMap<String, Double> varUbicacion = new HashMap<String, Double>();

                codigoProyecto = null;// "MAE-RA-2020-349360";
                varUbicacion = retornarParroquias(coordinatesWrappers);
                // Recorremos el hashMap y mostramos por pantalla el par valor y
                // clave
                Iterator it = varUbicacion.entrySet().iterator();
                List<String> parroquia = new ArrayList<String>();
                Double valor = null;
                while (it.hasNext()) {
                    Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
                        parroquia.add(e.getKey());
                        valor = e.getValue();
                }
                // cargar ubicación
                return parroquia;
    
    }
    
}
