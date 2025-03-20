package ec.gob.ambiente.suia.recaudaciones.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.UbicacionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.rcoa.dto.EntidadPagoNUTRcoa;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.DocumentoResolucionAmbiental;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.suia.recaudaciones.model.Cuentas;
import ec.gob.ambiente.suia.recaudaciones.model.DocumentoNUT;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.PagoKushkiComisionFacade;
import ec.gob.ambiente.rcoa.facade.PagoKushkiFacade;
import ec.gob.ambiente.rcoa.facade.PagoKushkiJsonFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.PagoKushki;
import ec.gob.ambiente.rcoa.model.PagoKushkiComision;
import ec.gob.ambiente.rcoa.model.PagoKushkiJson;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.FacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.FacilitadorPPC;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.CodigoTasa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.recaudaciones.bean.PagoRcoaBean;
import ec.gob.ambiente.suia.recaudaciones.bean.ResponseData;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasFacade;
import ec.gob.ambiente.suia.recaudaciones.model.EstadosNut;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.SolicitudUsuario;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.recaudaciones.model.TarifasNUT;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.DocumentosNUTFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import lombok.Getter;
import lombok.Setter;  
import ec.gob.ambiente.suia.utils.Constantes;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


@ManagedBean
@ViewScoped
public class PagoKushkiController {	
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PagoKushkiController.class);
	@EJB
    private SecuenciasFacade secuenciasFacade;	
	@EJB
	private CrudServiceBean crudServiceBean;	
	@EJB
	private TarifasFacade tarifasFacade;	
	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private InformeProvincialGADFacade informeProvincialGADFacade;	
	@EJB
	private OrganizacionFacade organizacionFacade;	
	@EJB
    private DocumentosFacade documentosFacade;
	@EJB
	private DocumentosNUTFacade documentosNUTFacade;
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private PagoKushkiFacade pagokushkiFacade;
	@EJB
	private PagoKushkiJsonFacade pagokushkijsonFacade;	
	@EJB
	private PagoKushkiComisionFacade pagokushkicomisionFacade;	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;	
	@EJB
	private DocumentosImpactoEstudioFacade documentosEstudioFacade;	
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private UbicacionDigitalizacionFacade ubicacionDigitalizacionFacade;
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private FacilitadorPPCFacade facilitadorPPCFacade;

	@Getter
	@Setter
	private Tarifas tarifas = new Tarifas();
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;	
	@Getter
	@Setter
	private NumeroUnicoTransaccional numeroUnicoTransaccional= new NumeroUnicoTransaccional();
	@Getter
	@Setter
	private SolicitudUsuario solicitudUsuario;	
	@Getter
	@Setter
	private List<Tarifas>listTarifas= new ArrayList<Tarifas>();		
	@Getter
	@Setter
	@ManagedProperty(value = "#{pagoRcoaBean}")
	private PagoRcoaBean pagoRcoaBean;	  
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();
	@Getter
	@Setter
	private Double valorComisionKushki,valorServicioBancario,porcentajeIva,valorTotalAPagarK;	
	@Getter
	@Setter
	private List<String> listPathArchivos= new ArrayList<String>();	
	@Getter
	@Setter
	private List<String> mensajesTasa= new ArrayList<String>();		
	@Getter
	@Setter
	private String idKushki,llavepublicak,ambientesistema,mensaje_errorT, mensaje_errorC;	
	@Getter
	@Setter
	private Boolean tipoPago = false;
	@Getter
	@Setter
	private Date fechaEx;	
	@Getter
	@Setter
	private Boolean aceptaTerminos;	
	@Getter
	@Setter
	private Boolean rgd = false;	
	@Getter
	@Setter
	private Boolean esFisico = false;	
	@Getter
	@Setter
	private Integer numeroMeses,Id_Tabla_comision;
	@Getter
	@Setter
	private String nombre,numeroTarjeta,ccv,respuestaKushki;	
	@Getter
	@Setter
	private Map<String, Object> variables;	
	public int ResponseTransacion;
	@Getter
	@Setter
	private String Ticket_Response,usuarioAmbiental,operador,tramite,processId;
	@Getter
	@Setter
	private PagoKushki pagokushki = new PagoKushki();
	@Getter
	@Setter
	private PagoKushkiJson pagokushkijson = new PagoKushkiJson();
	@Getter
	@Setter
	private PagoKushkiComision pagokushkicomision = new PagoKushkiComision();	
	@Getter
	@Setter
	private String errorMensj,detailErrorMsj,successMsj,detailSuccessMsj,NameTramite,MsjInfo;
	
	@Getter
	@Setter
	private String SuccessMsjInfo,MsjMnt,SuccessMnt, autoridadP,tecnicoFinanciero;
	
	@Getter
	@Setter
	private Integer solicitudId; 
	@Getter
	@Setter
	private String codigoProyecto,tramite_t_tarea; 		
	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
	private Integer tipoPagoBPM;
	private boolean nuevoComprobante;
    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private Map<Double, Map<String,String>> lista_rates_nut = new TreeMap<Double, Map<String,String>>();
    private Map<String, String> elementosJSON;
    private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());

	public void init(){
		try {
			if (bandejaTareasBean.getTarea() != null) {		
				variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
				operador = (String) variables.get("operador");
				tramite = (String) variables.get("tramite");	
				proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
				processId = bandejaTareasBean.getTarea().getProcessId();
				if (variables.get("tecnicoFinanciero") != null)
				{
					tecnicoFinanciero = (String) variables.get("tecnicoFinanciero");
				}
				if (variables.get("autoridadAmbiental") != null)
				{
					usuarioAmbiental = (String) variables.get("autoridadAmbiental");
				}
				if (variables.get("RGD") != null)
				{
					rgd = true;
				}
				if (variables.get("esFisico") != null)
				{
					esFisico = true;
				}				
			}
			respuestaKushki = Constantes.respuestaKushki;
			/*Obtengo valores de cobro y comision de Kushki*/
			idKushki = Carga_Parametros(1, 2);
			llavepublicak = Carga_Parametros(2, 2);
			ambientesistema = Carga_Parametros(4, 2);
			mensaje_errorT = pagoRcoaBean.getMensaje_ValidacionT();
			mensaje_errorC = pagoRcoaBean.getMensaje_ValidacionC();
			/////////Control para cuando el valor a pagar supera el limite indicado
			Integer Monto_Maximo = Carga_Parametros_Numericos(18, 1);
			if ((pagoRcoaBean.getValorAPagar()) >= Monto_Maximo){
				PagoKushkiComision valores = pagokushkicomisionFacade.obtenerValoresComisionMaxima(Double.valueOf(Monto_Maximo));
				Id_Tabla_comision = valores.getPacoId();
				valorComisionKushki = Double.valueOf(valores.getPacoVariableFee().toString());
				valorServicioBancario = Double.valueOf(valores.getPacoFixedRate().toString());				
			}else{
				PagoKushkiComision valores = pagokushkicomisionFacade.obtenerValoresComision(pagoRcoaBean.getValorAPagar());				
				Id_Tabla_comision = valores.getPacoId();
				valorComisionKushki = Double.valueOf(valores.getPacoVariableFee().toString());
				valorServicioBancario = Double.valueOf(valores.getPacoFixedRate().toString());				
			}
			CatalogoGeneralCoa ivaKushki = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.TIPO_VALOR_KUSHKI, 7);
			porcentajeIva = Double.valueOf(ivaKushki.getValor());						
			valorComisionKushki += (valorComisionKushki * porcentajeIva)/100;
			valorServicioBancario += (valorServicioBancario * porcentajeIva)/100;			
			Double valorTotalAux = pagoRcoaBean.getValorAPagar();
			valorTotalAPagarK = Math.round(valorTotalAux * Math.pow(10, 2)) / Math.pow(10, 2);
			if (pagoRcoaBean.getDireccion() == null ){ pagoRcoaBean.setDireccion(Constantes.Aux_Direccion); }
			else if (pagoRcoaBean.getDireccion().length() == 0 ){ pagoRcoaBean.setDireccion(Constantes.Aux_Direccion); }    		
    		if (pagoRcoaBean.getPais() == null){ pagoRcoaBean.setPais(Constantes.Aux_Pais);}
    		else if (pagoRcoaBean.getPais().length() == 0){ pagoRcoaBean.setPais(Constantes.Aux_Pais); }       
    		if (pagoRcoaBean.getProvincia() == null ){ pagoRcoaBean.setProvincia(Constantes.Aux_Provincia);}
    		else if (pagoRcoaBean.getProvincia().length() == 0 ){ pagoRcoaBean.setProvincia(Constantes.Aux_Provincia); }  		
    		if (pagoRcoaBean.getCiudad() == null ){ pagoRcoaBean.setDireccion(Constantes.Aux_Ciudad);	}
    		else if (pagoRcoaBean.getCiudad().length() == 0 ){ pagoRcoaBean.setDireccion(Constantes.Aux_Ciudad); }
    		if (bandejaTareasBean.getTarea() != null) {	  
    			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
				if (variables.get("autoridadAmbiental") != null)
				{
					usuarioAmbiental = (String) variables.get("autoridadAmbiental");
				}
    		}
		} catch (Exception e) {	LOG.error("Error al inicia Kushki", e);	}			
	}
	
    public static boolean emailValidator(String email){
        if (email == null) {return false;}
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
    
	public void cargarTransacciones(){
		pagoRcoaBean.setTransaccionesFinancieras(transaccionFinancieraFacade.cargarTransaccionesRcoa(proyectosBean.getProyectoRcoa()
						.getId(), bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getTaskId()));
	}
	
    @SuppressWarnings("null")
	public String RegistroPago() throws IOException {
        HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();        
        String keytoken = req.getParameter("kushkiToken");
        String metadata = req.getParameter("metadata");   
        Boolean Celular_valida = true;
        Boolean ci_revisada = false;
        Boolean celular_revisada = false;
        Boolean correo_revisada = false;
        String finalString6 = " ";
        ResponseData responseData = new ResponseData();
        responseData.setPrivateKey(Carga_Parametros(5, 2));
        responseData.setTipo_documento(Carga_Parametros(6, 2));
        responseData.setIce(Carga_Parametros(8, 2));
        responseData.setTipo_moneda(Carga_Parametros(9, 2));
        responseData.setPagina_web(Carga_Parametros(10, 2));
        responseData.setDireccion2(Carga_Parametros(11, 2));
        responseData.setCantidad(Carga_Parametros(12, 2));
        responseData.setEstado_Response(Carga_Parametros(15, 2));
        responseData.setUrlK(Carga_Parametros(3, 2));  
        responseData.setIva("0");	
        responseData.setAmount(req.getParameter("valor_pago_token"));
        responseData.setKushkiToken(req.getParameter("kushkiToken"));
        responseData.setKushkiPaymentMethod(req.getParameter("kushkiPaymentMethod"));
        responseData.setMetadata(req.getParameter("metadata"));
        responseData.setUsuario(req.getParameter("usuario_token"));
        responseData.setTramite(req.getParameter("tramite_nombre"));            
        responseData.setValor_p_t(req.getParameter("valor_pago_token"));
        responseData.setUsuario_t(req.getParameter("usuario_token"));
        responseData.setTramite_t(req.getParameter("tramite_nombre"));            
        responseData.setValor_pagar_original(req.getParameter("valor_pagar_original"));
        responseData.setValor_transaccion(req.getParameter("valor_transaccion"));
        responseData.setComision_servicio(req.getParameter("comision_servicio"));
        responseData.setCodigo_financiero(req.getParameter("codigo_financiero"));
        responseData.setTipo_proyecto(req.getParameter("tipo_proyecto"));    
        responseData.setValor_pago(req.getParameter("valor_pago_token"));
        responseData.setUsuario_token(req.getParameter("usuario_token"));
        responseData.setTramite_nombre(req.getParameter("tramite_nombre"));	               
        NameTramite = req.getParameter("tramite_nombre");
        
        if (metadata != null ){
            //Formateo el JSON de la metadata recibida con el correo, celular y cedula reemplazando los caracteres especiales
            String finalString1 = metadata.replaceAll("%40", "@");
            String finalString2 = finalString1.replaceAll("%7B", "{");
            String finalString3 = finalString2.replaceAll("%22", "\"");
            String finalString4 = finalString3.replaceAll("%3A", ":");
            String finalString5 = finalString4.replaceAll("%2C", ",");
            finalString6 = finalString5.replaceAll("%7D", "}");  
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(finalString6);
            JsonObject jsonObject = element.getAsJsonObject();
            String email_json = jsonObject.get("Email").getAsString();
            //////////////////////////////////////////celular
            String telefono_json = jsonObject.get("Telefono").getAsString();
            if (telefono_json.length()==0){ telefono_json = Constantes.celular_response; }            
            for (int i = 0; i < telefono_json.length(); i++)
            {
                char c = telefono_json.charAt(i);
                if (c < '0' || c > '9') { Celular_valida=false; }
            }            
            if (Celular_valida){
            	try {
            		if (telefono_json.length() == 10){
            			int PrimerDigito = Integer.parseInt(telefono_json.substring(0, 1));
            			if (PrimerDigito == 0){
            				int SegundoDigito = Integer.parseInt(telefono_json.substring(1, 2));
            			     if (SegundoDigito <= 8 || SegundoDigito >= 9){
            					String Digitos = telefono_json.substring(2);
            					if (Digitos.matches("[0-9]*")){ celular_revisada = true; }
            				}
            			}
            		}            		
            	}
            	catch (Exception err) { celular_revisada = false; }
            }            
            if (celular_revisada){
                telefono_json = telefono_json.substring(0,0) + telefono_json.substring(0+1);
                String Codigo_Celular = Carga_Parametros(13, 2);
                responseData.setNumero_celular(Codigo_Celular + telefono_json);   
                responseData.setTelefono(Codigo_Celular + telefono_json);  
            }        
            ///////////////////////////////////////////////////////////////////////////////cedula-RUC
            String ci_json = jsonObject.get("CI").getAsString();                       
            if (ci_json.length() == 10) {
                Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(
                                Constantes.USUARIO_WS_MAE_SRI_RC,
                                Constantes.PASSWORD_WS_MAE_SRI_RC,
                                ci_json);  
                if (cedula != null && cedula.getError().equals(Constantes.NO_ERROR_WS_MAE_SRI_RC)) { 
                	ci_revisada = true; } else {ci_revisada = false;
                	System.out.println("Cédula no válida");}
            } else if (ci_json.length() == 13) {
                ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
                            .obtenerPorRucSRI(
                                    Constantes.USUARIO_WS_MAE_SRI_RC,
                                    Constantes.PASSWORD_WS_MAE_SRI_RC,
                                    ci_json);
                if (contribuyenteCompleto != null) {
                    if (contribuyenteCompleto.getTipoContribuyente() != null) {
                    	ci_revisada = true; } else {ci_revisada = false;}
                } else {ci_revisada = false;
                System.out.println("RUC no válido");}
            }            
            /////////////////////////////////////////////////////////////////////////////////                                       
            if (emailValidator(email_json)) {  correo_revisada = true;  }
            if(correo_revisada) { responseData.setEmail(email_json);}                      
            if (ci_revisada) { responseData.setCI(ci_json);  }      
        }         
      //Valido que existe el Token y que los datos esten correctos//
        if ((keytoken == null || keytoken.length() == 0) && (metadata == null || metadata.length() == 0)){            
        	System.out.println("Carga Datos Iniciales formulario Pago en Linea");
        }
        else if ((!ci_revisada) && (metadata != null || metadata.length() > 0) && (celular_revisada)){
               	errorMensj = mensaje_errorT;
               	detailErrorMsj = mensaje_errorC;
               	RequestContext context = RequestContext.getCurrentInstance();
               	context.execute("PF('dlgErrorMsj').show();");   	
        }
        else if ((ci_revisada) && (metadata != null || metadata.length() > 0) && (!celular_revisada)){
        		errorMensj = mensaje_errorT;
        		detailErrorMsj = mensaje_errorC;
        		RequestContext context = RequestContext.getCurrentInstance();
        		context.execute("PF('dlgErrorMsj').show();");   	
        }
        else if ((!ci_revisada) && (metadata != null || metadata.length() > 0) && (!celular_revisada)){
        		errorMensj = mensaje_errorT;
        		detailErrorMsj = mensaje_errorC;
        		RequestContext context = RequestContext.getCurrentInstance();
        		context.execute("PF('dlgErrorMsj').show();");   	
        }
        else if ((keytoken == null || keytoken.length() == 0) && (metadata != null || metadata.length() > 0) && (ci_revisada) && (celular_revisada) && (correo_revisada)){        	
            //Error Proceso no devuelve token//     
        	responseData.setPrice(Pasar_Micrones(responseData.getAmount())); 
            pagokushki.setCodigo_proyecto(pagoRcoaBean.getIdproy());
            pagokushki.setFinanciero_Id(Integer.parseInt(responseData.getCodigo_financiero()));
            pagokushki.setPago_valor(new BigDecimal(responseData.getValor_pagar_original()));
            pagokushki.setPago_valor_transaccion((new BigDecimal(responseData.getValor_transaccion())).setScale(2, RoundingMode.HALF_UP));
            pagokushki.setPago_comision_servicio((new BigDecimal(responseData.getComision_servicio())).setScale(2, RoundingMode.HALF_UP));            
            pagokushki.setPago_valor_total((new BigDecimal(responseData.getAmount())).setScale(2, RoundingMode.HALF_UP));
            pagokushki.setPago_response_status(ResponseTransacion);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            String DateToStoreInDataBase= sdf.format(new Date());
            Timestamp ts = Timestamp.valueOf(DateToStoreInDataBase);
            pagokushki.setPago_status_fecha(ts);
            pagokushki.setComision_id(Id_Tabla_comision);
            Guardar_Registro_Error_Front(responseData);
            Ticket_Response="0";
            respuestaKushki=Ticket_Response;             	
        }        
        else if ((keytoken != null || keytoken.length() > 0) && (metadata != null || metadata.length() > 0) && (ci_revisada) && (celular_revisada) && (correo_revisada)){      
        	System.out.println(req.getParameter("kushkiToken"));      
            /*Creo método de generación de Pago*/   
        	responseData.setPrice(Pasar_Micrones(responseData.getAmount())); 
            String TipoContenido = "application/json";  
            Integer Implent_Protocol = Carga_Parametros_Numericos(23, 1);
            if (Implent_Protocol != 0){	execute(); }
            else{
        		try {
    	        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
    	        ctx.init(null, null, null);
    	        SSLContext.setDefault(ctx);
    	        System.out.println("Cambio Implementation protocol  Exitoso");
    	        } catch (Exception e) {
    	        System.out.println(e.getMessage());
    	        LOG.error("Error al generar el certificado",e);
    	        System.out.println("Ocurrio un error al generar el certificado");
    	        }              	
            }
            String valorAux = responseData.getValor_pago();
            Double valorAuxDouble = Double.valueOf(valorAux);
            Double valorSubAux = Math.round(valorAuxDouble * Math.pow(10, 2)) / Math.pow(10, 2);
            String valor_pago =  Double.toString(valorSubAux);
    		URL url = new URL(responseData.getUrlK());
    		HttpsURLConnection http = (HttpsURLConnection)url.openConnection();
    		http.setRequestMethod("POST");
    		http.setDoOutput(true);
    		http.setRequestProperty("Content-Type", TipoContenido);
    		http.setRequestProperty("Private-Merchant-Id", responseData.getPrivateKey());  		
    		String data = "{\n  \"token\": \""+responseData.getKushkiToken()+"\",\n  \"amount\": {\n    \"subtotalIva\": 0,\n    \"subtotalIva0\": "+valor_pago+",\n    "
    				+ "\"ice\": "+responseData.getIce()+",\n    \"iva\": "+responseData.getIva()+",\n    \"currency\": \""+responseData.getTipo_moneda()+"\"\n  },\n  \"metadata\": {\n    \"Referencia\": \""+pagoRcoaBean.getIdproy()+"\"\n  },\n  \"contactDetails\": {\n    \"documentType\": \""+responseData.getTipo_documento()+"\",\n    "
    				+ "\"documentNumber\": \""+responseData.getCI()+"\",\n    \"email\": \""+responseData.getEmail()+"\",\n    \"firstName\": \""+responseData.getUsuario_t()+"\",\n    \"lastName\": \""+responseData.getUsuario_t()+"\",\n    "
    				+ "\"phoneNumber\": \""+responseData.getTelefono()+"\"\n  },\n  \"orderDetails\": {\n    \"siteDomain\": \""+responseData.getPagina_web()+"\",\n    \"shippingDetails\": {\n      "
    				+ "\"name\": \""+responseData.getUsuario_t()+"\",\n      \"phone\": \""+responseData.getNumero_celular()+"\",\n      \"address1\": \""+pagoRcoaBean.getDireccion()+"\",\n      "
    				+ "\"address2\": \""+responseData.getDireccion2()+"\",\n      \"city\": \""+pagoRcoaBean.getCiudad()+"\",\n      \"region\": \""+pagoRcoaBean.getProvincia()+"\",\n      \"country\": \""+pagoRcoaBean.getPais()+"\"\n    },\n    "
    				+ "\"billingDetails\": {\n      \"name\": \""+responseData.getUsuario_token()+"\",\n      \"phone\": \""+responseData.getTelefono()+"\",\n      \"address1\": \""+pagoRcoaBean.getDireccion()+"\",\n      "
    				+ "\"address2\": \""+responseData.getDireccion2()+"\",\n      \"city\": \""+pagoRcoaBean.getCiudad()+"\",\n      \"region\": \""+pagoRcoaBean.getProvincia()+"\",\n      \"country\": \""+pagoRcoaBean.getPais()+"\"\n    }\n  },\n  \"productDetails\": {\n    "
    				+ "\"product\": [\n      {\n        \"id\": \""+pagoRcoaBean.getIdproy()+"\",\n        \"title\": \""+responseData.getTramite_t()+"\",\n        \"price\": "+responseData.getPrice()+",\n        \"sku\": \""+pagoRcoaBean.getIdproy()+"\",\n        "
    				+ "\"quantity\": "+responseData.getCantidad()+"\n      }\n  ]\n  },\n  \"fullResponse\": \""+responseData.getEstado_Response()+"\"\n}";
    		
    		byte[] out = data.getBytes(StandardCharsets.UTF_8);
    		System.out.println(data);
    		try {
        		OutputStream stream = http.getOutputStream();
        		stream.write(out);    		   		
        		ResponseTransacion = http.getResponseCode();  
        		pagoRcoaBean.setMensajePago(true);
        		System.out.println(http.getResponseCode());  			
    		} catch (Exception e) {
    	        System.out.println("Ocurrio un error al realizar la transaccion de pago");
    	        }    
            if (ResponseTransacion != Constantes.respuesta_servicio_online) {          	
        		InputStream errorstream = http.getErrorStream();
        		String responseError = "";
        		String line1;
        		BufferedReader br = new BufferedReader(new InputStreamReader(errorstream));
        		while ((line1 = br.readLine()) != null) {
        		    responseError += line1;
        		}            	
            	String JsonError = responseError.toString();        		
            	System.out.println(JsonError);
                JsonParser parser1 = new JsonParser();
                JsonElement element1 = parser1.parse(JsonError);
                JsonObject jsonObject1 = element1.getAsJsonObject();
                if (!jsonObject1.has("processorError")){
                	MsjMnt = "Error Proceso Sistema de Pago en Línea";
                	SuccessMnt = jsonObject1.get("message").getAsString();
                	RequestContext context = RequestContext.getCurrentInstance();
                	context.execute("PF('dlgMntPago').show();");                 	
                }else{
                    Ticket_Response = jsonObject1.get("processorError").getAsString();
                    pagokushki.setCodigo_proyecto(pagoRcoaBean.getIdproy());
                    pagokushki.setFinanciero_Id(Integer.parseInt(responseData.getCodigo_financiero()));
                    pagokushki.setPago_valor(new BigDecimal(responseData.getValor_pagar_original()));
                    pagokushki.setPago_valor_transaccion((new BigDecimal(responseData.getValor_transaccion())).setScale(2, RoundingMode.HALF_UP));
                    pagokushki.setPago_comision_servicio((new BigDecimal(responseData.getComision_servicio())).setScale(2, RoundingMode.HALF_UP));            
                    pagokushki.setPago_valor_total((new BigDecimal(responseData.getAmount())).setScale(2, RoundingMode.HALF_UP));
                    pagokushki.setPago_response_status(ResponseTransacion);
                    pagokushki.setPago_Json(JsonError);
            		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            		String DateToStoreInDataBase= sdf.format(new Date()); 
            	    Timestamp ts = Timestamp.valueOf(DateToStoreInDataBase);
            	    pagokushki.setPago_status_fecha(ts);
            	    pagokushki.setComision_id(Id_Tabla_comision);
            	    leerJSONRespuesta(JsonError);
            	    Guardar_Registro_Error_Pago(responseData); 
                }
            }
            else{
            	StringBuilder sbexito = new StringBuilder();
            	try(BufferedReader reader_exito = new BufferedReader(new InputStreamReader(http.getInputStream(), "utf-8"))) 
            	{
            	    String line = null;
            	    while ((line = reader_exito.readLine()) != null){
            	    	sbexito.append(line.trim());
            	    }
            	}
                //Obtengo los campos y asigno a la clase            	                           
            	String JsonExito = sbexito.toString();
            	System.out.println(JsonExito);
                JsonParser parser1 = new JsonParser();
                JsonElement element1 = parser1.parse(JsonExito);
                JsonObject jsonObject1 = element1.getAsJsonObject();
                Ticket_Response = jsonObject1.get("ticketNumber").getAsString();
                pagokushki.setCodigo_proyecto(pagoRcoaBean.getIdproy());
                pagokushki.setFinanciero_Id(Integer.parseInt(responseData.getCodigo_financiero()));
                pagokushki.setPago_valor(new BigDecimal(responseData.getValor_pagar_original()));
                pagokushki.setPago_valor_transaccion((new BigDecimal(responseData.getValor_transaccion())).setScale(2, RoundingMode.HALF_UP));
                pagokushki.setPago_comision_servicio((new BigDecimal(responseData.getComision_servicio())).setScale(2, RoundingMode.HALF_UP));
                pagokushki.setPago_valor_total((new BigDecimal(responseData.getAmount())).setScale(2, RoundingMode.HALF_UP));
                pagokushki.setPago_response_status(ResponseTransacion);
                pagokushki.setPago_Json(JsonExito);
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        		String DateToStoreInDataBase= sdf.format(new Date()); // java.util.Date
        	    Timestamp ts = Timestamp.valueOf(DateToStoreInDataBase);
        	    pagokushki.setPago_status_fecha(ts);
                pagokushki.setComision_id(Id_Tabla_comision);
                leerJSONRespuesta(JsonExito);
                Guardar_Registro(responseData);
            }          
            http.disconnect(); 
        }  
        return Ticket_Response;
    }
    
    public void Guardar_Registro(ResponseData responseData) {		
    	try {	        	
        	Boolean ContinuarProceso = false;
        	if (elementosJSON.size() > 0) { ContinuarProceso = true; }        	
        	if (ContinuarProceso) {
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        		String DateToStoreInDataBase= sdf.format(new Date()); 
        	    Timestamp ts = Timestamp.valueOf(DateToStoreInDataBase);	
            	pagokushkiFacade.save(pagokushki, JsfUtil.getLoggedUser());
            	pagokushkijson.setPayment(pagokushki);
            	///////////////////////////////////////////
            	pagokushkijson.setPajsStatusDate(ts);
            	pagokushkijson.setFechaCreacion(ts);        		
        		for (Entry<String, String> entry : elementosJSON.entrySet()) {
					String clave = entry.getKey();
					String valor = entry.getValue();
					switch(clave){ 
								   case "phone": if (valor != null) { pagokushkijson.setPajsPhone(valor); } break;
								   case "documentType": if (valor != null) { pagokushkijson.setPajsDocumenttype(valor); } break;
								   case "address1": if (valor != null) { pagokushkijson.setPajsAddress1(valor); } break;
								   case "acquirerBank": if (valor != null) { pagokushkijson.setPajsAcquirerbank(valor); } break;
								   case "address2": if (valor != null) { pagokushkijson.setPajsAddress2(valor); } break;
								   case "ticketNumber": if (valor != null) { pagokushkijson.setPajsTicketnumber(valor); } break;
								   case "type": if (valor != null) { pagokushkijson.setPajsType(valor); } break;
								   case "phoneNumber": if (valor != null) { pagokushkijson.setPajsPhonenumber(valor); } break;
								   case "created": if (valor != null) { pagokushkijson.setPajsCreated(valor); } break;
								   case "issuingBank": if (valor != null) { pagokushkijson.setPajsPajsIssuingbank(valor); } break;
								   case "ivaValue": if (valor != null) { pagokushkijson.setPajsIvavalue(new BigDecimal(valor)); } break;
								   case "documentNumber": if (valor != null) { pagokushkijson.setPajsDocumentnumber(valor); } break;
								   case "bank": if (valor != null) { pagokushkijson.setPajsBank(valor); } break;
								   case "quantity": if (valor != null) { pagokushkijson.setPajsQuantity(Integer.valueOf(valor)); } break;
								   case "merchantId": if (valor != null) { pagokushkijson.setPajsMerchantid(valor); } break;
								   case "processorName": if (valor != null) { pagokushkijson.setPajsProcessorname(valor); } break;
								   case "processorBankName": if (valor != null) { pagokushkijson.setPajsProcessorbankname(valor); } break;
								   case "responseCode": if (valor != null) { pagokushkijson.setPajsResponsecode(valor); } break;
								   case "approvedTransactionAmount": if (valor != null) { pagokushkijson.setPajsApprovedtransactionamount(valor); } break;
								   case "subtotalIva0": if (valor != null) { pagokushkijson.setPajsSubtotaliva0(new BigDecimal(valor)); } break;
								   case "responseText": if (valor != null) { pagokushkijson.setPajsResponsetext(valor); } break;
								   case "sku": if (valor != null) { pagokushkijson.setPajsSku(valor); } break;
								   case "cardType": if (valor != null) { pagokushkijson.setPajsCardtype(valor); } break;
								   case "country": if (valor != null) { pagokushkijson.setPajsCountry(valor); } break;
								   case "maskedCardNumber": if (valor != null) { pagokushkijson.setPajsMaskedcardnumber(valor); } break;
								   case "price": if (valor != null) { pagokushkijson.setPajsPrice(valor); } break;
								   case "email": if (valor != null) { pagokushkijson.setPajsEmail(valor); } break;
								   case "Referencia": if (valor != null) { pagokushkijson.setPajsReferencia(valor); } break;
								   case "transactionReference": if (valor != null) { pagokushkijson.setPajsTransactionreference(valor); } break;
								   case "cardCountry": if (valor != null) { pagokushkijson.setPajsCardcountry(valor); } break;
								   case "region": if (valor != null) { pagokushkijson.setPajsDocumenttype(valor); } break;
								   case "transactionId": if (valor != null) { pagokushkijson.setPajsTransactionid(valor); } break;
								   case "iceValue": if (valor != null) { pagokushkijson.setPajsIcevalue(new BigDecimal(valor)); } break;
								   case "id": if (valor != null) { pagokushkijson.setPajsIdAr(valor); } break;
								   case "subtotalIva": if (valor != null) { pagokushkijson.setPajsSubtotaliva(new BigDecimal(valor)); } break;
								   case "paymentBrand": if (valor != null) { pagokushkijson.setPajsPaymentbrand(valor); } break;
								   case "title": if (valor != null) { pagokushkijson.setPajsName(valor); } break;
								   case "token": if (valor != null) { pagokushkijson.setPajsToken(valor); } break;
								   case "merchantName": if (valor != null) { pagokushkijson.setPajsMerchantname(valor); } break;
								   case "transactionStatus": if (valor != null) { pagokushkijson.setPajsTransactionstatus(valor); } break;
								   case "currencyCode": if (valor != null) { pagokushkijson.setPajsCurrencycode(valor); } break;
								   case "cardHolderName": if (valor != null) { pagokushkijson.setPajsCardholdername(valor); } break;								   
								   case "binCard": if (valor != null) { pagokushkijson.setPajsBincard(valor); } break;
								   case "requestAmount": if (valor != null) { pagokushkijson.setPajsRequestamount(new BigDecimal(valor)); } break;
								   case "siteDomain": if (valor != null) { pagokushkijson.setPajsSitedomain(valor); } break;
								   case "approvalCode": if (valor != null) { pagokushkijson.setPajsApprovalcode(valor); } break;
								   case "ip": if (valor != null) { pagokushkijson.setPajsIp(valor); } break;
								   case "transactionType": if (valor != null) { pagokushkijson.setPajsTransactiontype(valor); } break;
								   case "recap": if (valor != null) { pagokushkijson.setPajsRecap(valor); } break;
								   case "foreignCard": if (valor != null) { pagokushkijson.setPajsForeigncard(Boolean.valueOf(valor)); } else {pagokushkijson.setPajsForeigncard(false);} break;
								   case "processorType": if (valor != null) { pagokushkijson.setPajsProcessortype(valor); } break;
								   case "lastFourDigits": if (valor != null) { pagokushkijson.setPajsLastfourdigits(valor); } break;
								   case "fullResponse": if (valor != null) { pagokushkijson.setPajsFullresponse(Boolean.valueOf(valor)); } else {pagokushkijson.setPajsFullresponse(false);} break;
								   case "syncMode": if (valor != null) { pagokushkijson.setPajsSyncmode(valor); } break;								   
					}
        		}
        		pagoRcoaBean.setTicketNumber(pagokushkijson.getPajsTicketnumber());
            	String entidad = pagoRcoaBean.getEntidadPago();
            	pagokushkijson.setPajsSku2(entidad); 
            	pagokushkijson.setPajsMerchantname(NameTramite);
            	tramite_t_tarea =  NameTramite;    
            	pagokushkijson.setPajsToken(responseData.getKushkiToken());
            	pagokushkijson.setPajsRequestamount(new BigDecimal(responseData.getAmount().toString()));
            	pagokushkijson.setPajsRegion(pagoRcoaBean.getProvincia());
            	pagokushkijson.setPajsCountry(pagoRcoaBean.getPais());    	  	
            	pagokushkijson.setPajsSitedomain(responseData.getPagina_web());  	
            	pagokushkijson.setPajsAddress2(responseData.getDireccion2());   
            	pagokushkijson.setPajsEmail(responseData.getEmail());
            	pagokushkijson.setPajsPhonenumber(responseData.getNumero_celular());  
            	pagokushkijson.setPajsDocumenttype(responseData.getTipo_documento());
            	pagokushkijson.setPajsDocumentnumber(responseData.getCI()); 
            	pagokushkijson.setPajsName(responseData.getUsuario_t());
            	pagokushkijson.setPajsFirstname(responseData.getUsuario_t());
            	pagokushkijson.setPajsLastname(responseData.getUsuario_t());
            	////////en caso de no tener data de la tarjeta o del banco se coloca por defecto
            	if (pagokushkijson.getPajsPhone() == null) { pagokushkijson.setPajsPhone(responseData.getTelefono()); }
            	if (pagokushkijson.getPajsAddress1() == null) { pagokushkijson.setPajsAddress1(responseData.getDireccion2()); }
            	if ((pagokushkijson.getPajsForeigncard() == null) && (pagokushkijson.getPajsCardcountry() != null)) { pagokushkijson.setPajsForeigncard(false); }
            	if (pagokushkijson.getPajsQuantity() == null) { pagokushkijson.setPajsQuantity(1); }
            	if (pagokushkijson.getPajsCurrencycode() == null) { pagokushkijson.setPajsCurrencycode(responseData.getTipo_moneda()); }
            	if (((pagoRcoaBean.getValproy() != null)) && (Double.valueOf(pagoRcoaBean.getValproy()) > 0)){ 
                	pagokushkijson.setPajsSku(pagoRcoaBean.getValproy());
                	pagokushkijson.setPajsPrice(pagoRcoaBean.getVallicamb());      		
            	}	else { 
                	pagokushkijson.setPajsSku("0");
                	pagokushkijson.setPajsPrice("0");           		
            	}            	
            	pagokushkijsonFacade.save(pagokushkijson, JsfUtil.getLoggedUser());
            	////Genero NUT
            	generarNUT();
            	successMsj = Carga_Parametros(17, 2);
            	detailSuccessMsj = pagokushkijson.getPajsTicketnumber();
            	RequestContext context = RequestContext.getCurrentInstance();
            	context.execute("PF('dlgSuccessMsj').show();");   	
            	String msjResultadoCorrecto = Carga_Parametros(17, 2);
            	String msjTicketCorrecto = pagokushkijson.getPajsTicketnumber();
            	System.out.println(msjResultadoCorrecto + msjTicketCorrecto );          	
        	} else { 
        		LOG.error("No se recibio archivo de respuesta o no se genera correctamente el archivo JSON del Servicio de Pago no se guarda en la base"); 
            	RequestContext context = RequestContext.getCurrentInstance();
            	context.execute("PF('dlgErrorMsj').show();");    	
            	String msjResultadoError = Carga_Parametros(16, 2);
            	String msjDetalleError = "No se recibe respuesta Servicio de Pago";
            	System.out.println(msjResultadoError + msjDetalleError);
        	}   	
    	}catch(Exception ex) {	
    		LOG.error("Error al guardar registro Kushki", ex);
    	}    	
    } 
     
    public void Guardar_Registro_Error_Pago(ResponseData responseData) {	
     	try {	
     	
        	Boolean ContinuarProceso = false;
        	if (elementosJSON.size() > 0) { ContinuarProceso = true; }        	
        	if (ContinuarProceso) {
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        		String DateToStoreInDataBase= sdf.format(new Date()); 
        	    Timestamp ts = Timestamp.valueOf(DateToStoreInDataBase);	
            	pagokushkiFacade.save(pagokushki, JsfUtil.getLoggedUser());
            	pagokushkijson.setPayment(pagokushki);
            	pagokushkijson.setPajsStatusDate(ts);
            	pagokushkijson.setPajsStatusDate(ts);
            	String entidad = pagoRcoaBean.getEntidadPago();
            	pagokushkijson.setPajsSku2(entidad);    	
            	pagokushkijson.setPajsToken(responseData.getKushkiToken());
            	pagokushkijson.setPajsRequestamount(new BigDecimal(responseData.getAmount().toString()));
            	pagokushkijson.setPajsRegion(pagoRcoaBean.getProvincia());
            	pagokushkijson.setPajsCountry(pagoRcoaBean.getPais());    	  	
            	pagokushkijson.setPajsSitedomain(responseData.getPagina_web());
            	pagokushkijson.setPajsPhone(responseData.getNumero_celular());    	
            	pagokushkijson.setPajsAddress2(responseData.getDireccion2());   
            	pagokushkijson.setPajsEmail(responseData.getEmail());
            	pagokushkijson.setPajsPhonenumber(responseData.getNumero_celular());  
            	pagokushkijson.setPajsDocumenttype(responseData.getTipo_documento());
            	pagokushkijson.setPajsDocumentnumber(responseData.getCI());    	    	
        		for (Entry<String, String> entry : elementosJSON.entrySet()) {
        			String clave = entry.getKey();
        			String valor = entry.getValue();
        			switch(clave){ 
        						   case "code": if (valor != null) { pagokushkijson.setPajsApprovalcode(valor); } break;			   
        						   case "message": if (valor != null) { pagokushkijson.setPajsPajsIssuingbank(valor); } break;						   
        						   case "subtotalIva": if (valor != null) { pagokushkijson.setPajsSubtotaliva(new BigDecimal(valor)); } break;						   
        						   case "subtotalIva0": if (valor != null) { pagokushkijson.setPajsSubtotaliva0(new BigDecimal(valor)); } break;						   
        						   case "ice": if (valor != null) { pagokushkijson.setPajsIcevalue(new BigDecimal(valor)); } break;						   
        						   case "iva": if (valor != null) { pagokushkijson.setPajsIvavalue(new BigDecimal(valor)); } break;						   
        						   case "currency": if (valor != null) { pagokushkijson.setPajsCurrencycode(valor); } break;						   
        						   case "approvedTransactionAmount": if (valor != null) { pagokushkijson.setPajsApprovedtransactionamount(valor); } break;
        						   case "bank": if (valor != null) { pagokushkijson.setPajsBank(valor); } break;						   
        						   case "binCard": if (valor != null) { pagokushkijson.setPajsBincard(valor); } break;							   
        						   case "lastFourDigits": if (valor != null) { pagokushkijson.setPajsLastfourdigits(valor); } break;						   
        						   case "type": if (valor != null) { pagokushkijson.setPajsType(valor); } break;						   
        						   case "cardHolderName": if (valor != null) { pagokushkijson.setPajsCardholdername(valor); } break;						   
        						   case "created": if (valor != null) { pagokushkijson.setPajsCreated(valor); } break;							   
        						   case "merchantId": if (valor != null) { pagokushkijson.setPajsMerchantid(valor); } break;						   
        						   case "merchantName": if (valor != null) { pagokushkijson.setPajsMerchantname(valor); } break;						   
        						   case "paymentBrand": if (valor != null) { pagokushkijson.setPajsPaymentbrand(valor); } break;						   
        						   case "processorBankName": if (valor != null) { pagokushkijson.setPajsProcessorbankname(valor); } break;						   
        						   case "requestAmount": if (valor != null) { pagokushkijson.setPajsRequestamount(new BigDecimal(valor)); } break;						   
        						   case "responseCode": if (valor != null) { pagokushkijson.setPajsResponsecode(valor); } break;							   
        						   case "responseText": if (valor != null) { pagokushkijson.setPajsResponsetext(valor); } break;						   
        						   case "transactionId": if (valor != null) { pagokushkijson.setPajsTransactionid(valor); } break;						   
        						   case "transactionStatus": if (valor != null) { pagokushkijson.setPajsTransactionstatus(valor); } break;						   
        						   case "transactionType": if (valor != null) { pagokushkijson.setPajsTransactiontype(valor); } break;						   
        						   case "processorMessage": if (valor != null) { pagokushkijson.setPajsProcessortype(valor); } break;						   
        						   case "transactionReference": if (valor != null) { pagokushkijson.setPajsTransactionreference(valor); } break;						   
        						   case "processorError": if (valor != null) { pagokushkijson.setPajsApprovalcode(valor); } break;							   
        			}
        		}
            	pagokushkijson.setPajsName(responseData.getUsuario_t());
            	pagokushkijson.setPajsFirstname(responseData.getUsuario_t());
            	pagokushkijson.setPajsLastname(responseData.getUsuario_t());
            	////////en caso de no tener data de la tarjeta o del banco se coloca por defecto
            	if (pagokushkijson.getPajsPhone() == null) { pagokushkijson.setPajsPhone(responseData.getTelefono()); }
            	if (pagokushkijson.getPajsAddress1() == null) { pagokushkijson.setPajsAddress1(responseData.getDireccion2()); }            	
            	if (pagokushkijson.getPajsQuantity() == null) { pagokushkijson.setPajsQuantity(1); }
            	if (pagokushkijson.getPajsCurrencycode() == null) { pagokushkijson.setPajsCurrencycode(responseData.getTipo_moneda()); }            	
        		pagokushkijson.setPajsMerchantname(NameTramite);
            	pagokushkijsonFacade.save(pagokushkijson, JsfUtil.getLoggedUser());    
            	errorMensj = Carga_Parametros(16, 2);
            	detailErrorMsj = pagokushkijson.getPajsResponsetext();
            	RequestContext context = RequestContext.getCurrentInstance();
            	context.execute("PF('dlgErrorMsj').show();");    	
            	String msjResultadoError = Carga_Parametros(16, 2);
            	String msjDetalleError = pagokushkijson.getPajsResponsetext();
            	System.out.println("/////////////////////// error msj");
            	System.out.println(msjResultadoError + msjDetalleError);
            	System.out.println("///////////////////////");	        		
        	} else { 
        		LOG.error("No se recibio archivo de respuesta o no se genera correctamente el archivo JSON de error del Servicio de Pago no se guarda en la base"); 
            	RequestContext context = RequestContext.getCurrentInstance();
            	context.execute("PF('dlgErrorMsj').show();");    	
            	String msjResultadoError = Carga_Parametros(16, 2);
            	String msjDetalleError = "No se recibe respuesta Servicio de Pago";
            	System.out.println(msjResultadoError + msjDetalleError);
        	}
    	}catch(Exception ex) {	
    		LOG.error("Error al guardar registro Kushki", ex); 		
    	}
    }  
    
    public void Guardar_Registro_Error_Front(ResponseData responseData) {   
        try {   
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
       String DateToStoreInDataBase= sdf.format(new Date());
       Timestamp ts = Timestamp.valueOf(DateToStoreInDataBase);   
       pagokushkiFacade.save(pagokushki, JsfUtil.getLoggedUser());
       pagokushkijson.setPayment(pagokushki);
       pagokushkijson.setPajsStatusDate(ts);   
       pagokushkijson.setPajsToken(responseData.getKushkiToken());
       pagokushkijson.setPajsRequestamount(new BigDecimal(responseData.getAmount().toString()));
       pagokushkijson.setPajsRegion(pagoRcoaBean.getProvincia());
       pagokushkijson.setPajsCountry(pagoRcoaBean.getPais());             
       pagokushkijson.setPajsSitedomain(responseData.getPagina_web());
       pagokushkijson.setPajsPhone(responseData.getNumero_celular());       
       pagokushkijson.setPajsAddress2(responseData.getDireccion2());  
       pagokushkijson.setPajsEmail(responseData.getEmail());
       pagokushkijson.setPajsPhonenumber(responseData.getNumero_celular()); 
       pagokushkijson.setPajsDocumenttype(responseData.getTipo_documento());
       pagokushkijson.setPajsDocumentnumber(responseData.getCI());
       pagokushkijson.setPajsMerchantname(NameTramite);
   	   String entidad = pagoRcoaBean.getEntidadPago();
   	   pagokushkijson.setPajsSku2(entidad);       
       String Mensaje_Error_front = "Generación de Token Erronea";
       pagokushkijson.setPajsResponsetext(Mensaje_Error_front);
       //VALIDACION Y REVISAR EN BDD
       String Motivo = Carga_Parametros(20, 2);
       pagokushkijson.setPajsTransactionstatus(Motivo);       
       pagokushkijsonFacade.save(pagokushkijson, JsfUtil.getLoggedUser()); 
       errorMensj = Carga_Parametros(16, 2);
   	   detailErrorMsj = Motivo;
   	   RequestContext context = RequestContext.getCurrentInstance();
   	   context.execute("PF('dlgErrorMsj').show();");
       }catch(Exception ex) {   
           LOG.error("Error al guardar registro Kushki", ex);        
       }
   }   
       
	public String Carga_Parametros(Integer Orden,Integer Campo){
		 String dato = "";
	     CatalogoGeneralCoa parametro = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.TIPO_VALOR_KUSHKI, Orden); 
	     if (Campo == 1){ dato = parametro.getDescripcion(); }
	     else if (Campo == 2){ dato = parametro.getValor(); }
	     return dato;		
	}  
	
	public Integer Carga_Parametros_Numericos(Integer Orden,Integer Campo){
		 Integer dato = 0;
	     CatalogoGeneralCoa parametro = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.TIPO_VALOR_KUSHKI, Orden); 
	     if (Campo == 1){ dato = Integer.valueOf(parametro.getValor()); }
	     return dato;		
	}  	
	
    public String Pasar_Micrones(String Valor_Pago){ 
     String valor_micro = Carga_Parametros(14, 2);
     BigDecimal calculo = ((new BigDecimal(Valor_Pago)).multiply(new BigDecimal(valor_micro)));
     BigInteger i = calculo.toBigInteger();
     return i.toString();
    }
    
	public String generarCodigoSolicitud(){
        try {
        	String mae=Constantes.SIGLAS_INSTITUCION + "-REC-";	        	          
        	return mae
        			+ secuenciasFacade.getCurrentYear()
                    + "-"
                    + secuenciasFacade.getNextValueDedicateSequence("SOLICITUD_RECAUDACIONES_CODIGO", 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }	

	@SuppressWarnings("unchecked")
	public void obtenerPagosRealizados(){
		try {
			String sql="select reus_id, nut_project_code, sum(case nuts_id when 3 then nut_value else 0 end) totalPagado, sum(nut_value) total "
					+ "from payments.unique_transaction_number "
					+ " where nut_status  "
					+ " and nut_project_code= '"+tramite_t_tarea+"' "
					+ "group by reus_id, nut_project_code limit 1";					
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			if (resultList.size() > 0) {
				for (Object objNut : resultList) {
					Object[] row = (Object[]) objNut;
					Integer solicitudId = (Integer) row[0];
					String codigoProyecto = (String) row[1];
					// busco cual es la tarea de pago 
					obtenerTareasPagoRcoa(solicitudId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al obtener los pagos realizados");
		}
	}
	
	//cargar el metodo y la tarea de BPM: tramite_t_tarea 
	@SuppressWarnings("unchecked")
	public void obtenerTareasPagoRcoa(Integer solicitudId){
		try {
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime, bt.taskname, t.processid, p.processname "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ " inner join bamtasksummary bt on t.id = bt.taskid "
					+ " inner join processinstancelog p on t.processinstanceid = p.processinstanceid "
					+ "where value = ''"+tramite_t_tarea+"'' and v.variableid = ''tramite'' "
					+ "and t.status in (''Reserved'') "
					+ "and t.actualowner_id is not null and "
					+ "t.createdby_id is not null and bt.taskname is not null and (lower(bt.taskname) like ''%pago%'' or lower(bt.taskname) like ''%valor %'') and "
					+ "t.processid in( ''rcoa.CertificadoAmbiental'', ''rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales'', ''rcoa.RegistroAmbiental'', ''rcoa.RegistroAmbientalconPPC'', ''rcoa.ResolucionLicenciaAmbiental'', ''rcoa.ProcesoParticipacionCiudadanaBypass'', ''rcoa.DeclaracionSustanciasQuimicas'') ') "
					+ "as (id varchar, usuario varchar, processinstanceid varchar, tramite varchar, fecha varchar, nombretarea varchar, processid varchar, procesname varchar) "
					+ "order by 1";
			///////////en caso de que algun campo llegue nulo el campo createdby_id cambio la consulta
			String sqlconsulta2="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime, bt.taskname, t.processid, p.processname "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ " inner join bamtasksummary bt on t.id = bt.taskid "
					+ " inner join processinstancelog p on t.processinstanceid = p.processinstanceid "
					+ "where value = ''"+tramite_t_tarea+"'' and v.variableid = ''tramite'' "
					+ "and t.status in (''Reserved'') "
					+ "and t.actualowner_id is not null and "
					+ "bt.taskname is not null and (lower(bt.taskname) like ''%pago%'' or lower(bt.taskname) like ''%valor %'') and "
					+ "t.processid in( ''rcoa.CertificadoAmbiental'', ''rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales'', ''rcoa.RegistroAmbiental'', ''rcoa.RegistroAmbientalconPPC'', ''rcoa.ResolucionLicenciaAmbiental'', ''rcoa.ProcesoParticipacionCiudadanaBypass'', ''rcoa.DeclaracionSustanciasQuimicas'') ') "
					+ "as (id varchar, usuario varchar, processinstanceid varchar, tramite varchar, fecha varchar, nombretarea varchar, processid varchar, procesname varchar) "
					+ "order by 1";			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			List<Object>  resultado1 = new ArrayList<Object>();
			List<Object>  resultado2 = new ArrayList<Object>();
			resultado1 = query.getResultList();
			if (resultado1.size() > 0){ resultList=resultado1; }
			else{				
				Query query2 = crudServiceBean.getEntityManager().createNativeQuery(sqlconsulta2);
				resultado2 = query2.getResultList();
				if (resultado2.size() > 0){ resultList=resultado2; }
			}			
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					TaskSummaryCustom tarea = new TaskSummaryCustom();
					tarea.setTaskId(Long.parseLong(row[0].toString()));
					Long processInstanceId = Long.parseLong(row[2].toString());
					tarea.setProcessInstanceId(processInstanceId);
					tarea.setTaskName((String) row[5]);
					tarea.setProcessId((String) row[6]);
					tarea.setProcessName((String) row[7]);
					Usuario usuario = usuarioFacade.buscarUsuario((String) row[1]);
					boolean correcto =false;
					List<NumeroUnicoTransaccional> listaNuts = numeroUnicoTransaccionalFacade.listBuscaNUTPorIdSolicitud(solicitudId);					
					for(NumeroUnicoTransaccional objNut : listaNuts)
					{						
						switch (tarea.getProcessId()) {
							case "rcoa.CertificadoAmbiental":
								tipoPagoBPM = 2;  // pago por coberturavegetal nativa
								break;
							case "rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales":
								tipoPagoBPM = 3;  // pago por RGD
								break;
							case "rcoa.DeclaracionSustanciasQuimicas":
								tipoPagoBPM = 4;  // pago por RSQ
								break;
							case "rcoa.RegistroAmbiental":
							case "rcoa.RegistroAmbientalconPPC":
								tipoPagoBPM = 1;  // pago por Registro ambiental
								ProyectoLicenciaCoa proyectoRCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite_t_tarea);
								// verifico si no es un enete acreditasdo para finalizar la tarea
								if (proyectoRCoa.getAreaResponsable().getTipoEnteAcreditado() != null) {
									if (proyectoRCoa.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
										if(objNut.getCuentas().getId().equals(4))
											tipoPagoBPM = 2;  // pago por coberturavegetal nativa
									}else{
										tipoPagoBPM = 2;  // pago por coberturavegetal nativa
									}
								}
							case "rcoa.ResolucionLicenciaAmbiental":
								tipoPagoBPM = 1; // pago de licencia ambiental
								ProyectoLicenciaCoa proyectoRCoa1 = proyectoLicenciaCoaFacade.buscarProyecto(tramite_t_tarea);
								if(objNut.getCuentas().getId().equals(4)){
									tipoPagoBPM = 2;  // pago por coberturavegetal nativa
								}else{
									if(proyectoRCoa1.getGeneraDesechos() && objNut.getNutValor().equals(180.0))
										tipoPagoBPM = 3;  // pago por RGD 
								}
							break;
							case "rcoa.ProcesoParticipacionCiudadanaBypass":
								tipoPagoBPM = 5;  // pago por licencia ambiental
							break;
							default:
								break;
						}
						try
						{
							correcto = registrarPago(objNut, tarea);
						}
						catch (Exception e) {
							e.printStackTrace();
						}						
					}
					correcto = true;
					if(correcto){
						switch (tarea.getProcessId()) {
							case "rcoa.CertificadoAmbiental":
								finalizar(tarea, usuario);
								break;
							case "rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales":
								finalizarRGD(tarea, usuario);
								break;
							case "rcoa.DeclaracionSustanciasQuimicas":
								finalizar(tarea, usuario);
								break;
							case "rcoa.RegistroAmbiental":
							case "rcoa.RegistroAmbientalconPPC":
								ProyectoLicenciaCoa proyectoRCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite_t_tarea);
								// verifico si no es un enete acreditasdo para finalizar la tarea
								if (proyectoRCoa.getAreaResponsable().getTipoEnteAcreditado() != null) {
									if (proyectoRCoa.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
										finalizar(tarea, usuario);
									}else{
										// si es enete solo registro el pago porcobertura vegetal
									}
								}
								break;
							case "rcoa.ResolucionLicenciaAmbiental":
								// si es licencia solo registro el pago porcobertura vegetal
									finalizar(tarea, usuario);								
							break;
							case "rcoa.ProcesoParticipacionCiudadanaBypass":
								finalizarParticipacion(tarea, usuario);
							break;
							default:
								break;
						}
					}
					break;	
				}
			} else { System.out.println("No se pudo encontrar la tarea del tramite: "+tramite_t_tarea+", proceso no puede continuar con el envio de la tarea de pago" ); }  
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean registrarPago(NumeroUnicoTransaccional nutPrincipal, TaskSummaryCustom tarea ){
		boolean pagoSatisfactorio = false;
		List<TransaccionFinanciera> transaccionesFinancierasProyecto = new ArrayList<TransaccionFinanciera>();		
		ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(nutPrincipal.getNutCodigoProyecto());
		List<NumeroUnicoTransaccional> listaNuts = numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(nutPrincipal.getNutCodigoProyecto());
		for (NumeroUnicoTransaccional objNut : listaNuts) {
			//si esta caducado no lo tomo en cuenta
			if(objNut.getEstadosNut().getId().equals(5))
				continue;
			if(objNut.getEstadosNut().getId().equals(3)){
				// crea la transaccion financiera
				TransaccionFinanciera transaccionFinanciera = new TransaccionFinanciera();
				//busco la institucion financiera		
				List<InstitucionFinanciera> institucionesFinancieras = institucionFinancieraFacade.obtenerInstitucionFinancieraPorId(objNut.getSolicitudUsuario().getInstitucionFinanciera().getId());
				if(institucionesFinancieras != null && !institucionesFinancieras.isEmpty()){
					transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
				}
				transaccionFinanciera.setMontoTransaccion(objNut.getNutValor());
				transaccionFinanciera.setMontoPago(objNut.getNutValor());
				if(tarea.getProcessId().equals("rcoa.ResolucionLicenciaAmbiental"))
				{
					if(objNut.getNutDescripcion().contains("correspondiente a remoción de cobertura vegetal nativa"))
					{
						transaccionFinanciera.setTipoPago(2);
					} else if(objNut.getNutDescripcion().contains("correspondiente a RGDP"))
					{
						transaccionFinanciera.setTipoPago(3);
					}else if(objNut.getNutDescripcion().contains("costo del proyecto"))
					{
						transaccionFinanciera.setTipoPago(1);
					}
					else
					{
						transaccionFinanciera.setTipoPago(tipoPagoBPM);	
					}					
				}
				else
				{
					transaccionFinanciera.setTipoPago(tipoPagoBPM);	
				}				
				if(proyecto != null && proyecto.getId() != null){
					transaccionFinanciera.setProyectoRcoa(proyecto);
					if(proyecto.getCategorizacion() == 3 || proyecto.getCategorizacion() == 4)
						transaccionFinanciera.setMontoPago(objNut.getNutValor());
				}else{
					transaccionFinanciera.setIdentificadorMotivo(objNut.getNutCodigoProyecto());
				}
				transaccionFinanciera.setNumeroTransaccion(objNut.getBnfTramitNumber());
				transaccionesFinancierasProyecto.add(transaccionFinanciera);
			}
		}
//			//guarado la transacion log si todavia no ha sido registrado
			if((transaccionesFinancierasProyecto != null) && (transaccionesFinancierasProyecto.size() > 0)) {
				try
				{
		            transaccionFinancieraFacade.guardarTransacciones(transaccionesFinancierasProyecto,
		            		tarea.getTaskId(), tarea.getTaskName(),	tarea.getProcessInstanceId(), tarea.getProcessId(),
		            		tarea.getProcessName());	
					pagoSatisfactorio = true;
				}
				catch (Exception e) {
					pagoSatisfactorio = false;
				}
			}
		return pagoSatisfactorio;
	}
	
	
	public boolean finalizarParticipacion(TaskSummaryCustom tarea, Usuario usuario){
		try {
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(usuario, tarea.getProcessInstanceId());
			String tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			Integer numeroFacilitadores = Integer.valueOf((String) variables.get("numeroFacilitadores"));
			ProyectoLicenciaCoa proyectoRCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			List<Usuario> listaUsuariosFacilitadores = facilitadorPPCFacade.usuariosFacilitadoresAsignadosPendientesAceptacion(proyectoRCoa);

			//validar la asignacion de los facilitadores
			String[] facilitadoresLista = new String[numeroFacilitadores];
			Integer cont = 0;
			if(listaUsuariosFacilitadores == null || listaUsuariosFacilitadores.size()==0) {
				System.out.println(tramite + " No existe la cantidad de facilitadores solicitados disponibles.");
	            return false;
			}
			for (Usuario objUsuario : listaUsuariosFacilitadores) {
				if(cont<numeroFacilitadores)
					facilitadoresLista[cont++] = objUsuario.getNombre();
			}
			Map<String, Object> params = new HashMap<String, Object>();
            params.put("listaFacilitadores", facilitadoresLista);
            List<FacilitadorPPC> facilitadores = facilitadorPPCFacade.facilitadoresAsignadosPendientesAceptacion(proyectoRCoa);
            for (FacilitadorPPC facilitadorPPC : facilitadores) {
            	facilitadorPPC.setFechaRegistroPago(new Date());
				facilitadorPPCFacade.guardar(facilitadorPPC);
			}
			
            procesoFacade.modificarVariablesProceso(usuario, tarea.getProcessInstanceId(), params);
            procesoFacade.aprobarTarea(usuario,tarea.getTaskId(),tarea.getProcessInstanceId(), null);
            
            for (Usuario objUsuario : listaUsuariosFacilitadores) {
            	notificaciones(objUsuario, proyectoRCoa,  usuario);
            }
            return true;
		} catch (Exception e) {					
			e.printStackTrace();
			return false;
		}
	}
	
	private void notificaciones(Usuario usuarioFacilitador, ProyectoLicenciaCoa proyectoRCoa, Usuario usuario ) {
		try {
			Object[] parametrosCorreoTecnicos = new Object[] {usuarioFacilitador.getPersona().getTipoTratos().getNombre(), 
						usuarioFacilitador.getPersona().getNombre(), proyectoRCoa.getNombreProyecto(), 
						proyectoRCoa.getCodigoUnicoAmbiental() };
				
				String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
								"bodyNotificacionDesignacionFacilitadorByPass",
								parametrosCorreoTecnicos);

    			NotificacionAutoridadesController email=new NotificacionAutoridadesController();
    			List<Contacto> contactos = usuarioFacilitador.getPersona().getContactos();
    			String emailDestino = "";
    			for (Contacto contacto : contactos) {
    				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()){
    					emailDestino=contacto.getValor();
    					break;
    				}
    			}
    			email.sendEmailInformacion(emailDestino, notificacion, "Regularización Ambiental Nacional", proyectoRCoa.getCodigoUnicoAmbiental(), usuario, usuario);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean registrarPagoK(NumeroUnicoTransaccional nut, TaskSummaryCustom tarea ){
		List<TransaccionFinanciera> transaccionesFinancierasProyecto= new ArrayList<TransaccionFinanciera>();
		// crea la transaccion financiera
		TransaccionFinanciera transaccionFinanciera = new TransaccionFinanciera();
		//busco la institucion financiera
		List<InstitucionFinanciera> institucionesFinancieras = institucionFinancieraFacade.obtenerInstitucionFinancieraPagoOnline(pagoRcoaBean.getInstitucionFinancieraNut().getNombreInstitucion());
		if(institucionesFinancieras != null && !institucionesFinancieras.isEmpty()){
			transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
		}
		transaccionFinanciera.setMontoTransaccion(nut.getNutValor());
		transaccionFinanciera.setTipoPago(tipoPagoBPM);
		ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(nut.getNutCodigoProyecto());
		if(proyecto != null && proyecto.getId() != null){
			transaccionFinanciera.setProyectoRcoa(proyecto);
			if(proyecto.getCategorizacion() == 3 || proyecto.getCategorizacion() == 4)
			transaccionFinanciera.setMontoPago(nut.getNutValor());
		}else{
			transaccionFinanciera.setIdentificadorMotivo(nut.getNutCodigoProyecto());
		}
		transaccionFinanciera.setNumeroTransaccion(nut.getBnfTramitNumber());
		transaccionesFinancierasProyecto.add(transaccionFinanciera);
		//guarado la transacion
		boolean pagoSatisfactorio=transaccionFinancieraFacade.realizarPago(nut.getNutValor(), transaccionesFinancierasProyecto,nut.getNutCodigoProyecto());
		if(pagoSatisfactorio){
			// actualizo ael nut a usado
			nut.setNutUsado(true);
			crudServiceBean.saveOrUpdate(nut);
			//guarado la transacion log
            transaccionFinancieraFacade.guardarTransacciones(transaccionesFinancierasProyecto,
            		tarea.getTaskId(), tarea.getTaskName(),	tarea.getProcessInstanceId(), tarea.getProcessId(),
            		tarea.getProcessName());
		}
		return pagoSatisfactorio;
	}
	
	private boolean finalizarRGD(TaskSummaryCustom tarea, Usuario usuario){
		try {
			String rolDirector = "";
			Usuario usuarioAutoridad;
			String autoridad = "";
			Area areaTramite = new Area();
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(usuario, tarea.getProcessInstanceId());
			String tipoPermisoRGD =(String)variables.get(Constantes.VARIABLE_TIPO_RGD);
			String tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
				usuarioAutoridad = areaFacade.getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental");
				if(usuarioAutoridad != null && usuarioAutoridad.getId() != null){
					autoridad = usuarioAutoridad.getNombre();
				}else{
					System.out.println("No existe usuario " + Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental") );
					return false;
				}
			}else{
				ProyectoLicenciaCoa proyectoRCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
				// si es proyecto rcoa
				if(proyectoRCoa != null && proyectoRCoa.getId() != null){
					if (proyectoRCoa.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
						UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyectoRCoa.getIdCantonOficina());
						areaTramite = ubicacion.getAreaCoordinacionZonal().getArea();
					} else{
						areaTramite = (proyectoRCoa.getAreaInventarioForestal().getArea() != null) ? proyectoRCoa.getAreaInventarioForestal().getArea(): proyectoRCoa.getAreaInventarioForestal();
					}
				}else{
					ProyectoLicenciamientoAmbiental proyectoSuia = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(tramite);
					if(proyectoSuia != null && proyectoSuia.getId() != null){
						// si es proyectos suia busco la direccion zonal en base a la ubicacion geografica
						if(proyectoSuia.getProyectoUbicacionesGeograficas() != null && proyectoSuia.getProyectoUbicacionesGeograficas().size() > 0){
							UbicacionesGeografica ubicacion = proyectoSuia.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
							if(proyectoSuia.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){

							}else{
								if(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
									areaTramite = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
								} else {
									areaTramite = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
								}
							}
						}
					}
				}

				// si es proyecto digitalizado busco el area del proyecto digitalizado
				AutorizacionAdministrativaAmbiental autorizacionAdministrativa = new AutorizacionAdministrativaAmbiental();
				Integer proyectoId = Integer.valueOf(((String)variables.get("idProyectoDigitalizacion") == null)?"0":(String)variables.get("idProyectoDigitalizacion"));
				if(proyectoId > 0){
					autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(proyectoId);
					if(autorizacionAdministrativa != null && autorizacionAdministrativa.getId() != null){
						// listo las ubicaciones del proyecto original
						List<UbicacionDigitalizacion> ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(autorizacionAdministrativa.getId(), 1, "WGS84", "17S");
						// si no existen // listo las ubicaciones del proyecto ingresadas en digitalizacion
						if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
							ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(autorizacionAdministrativa.getId(), 3, "WGS84", "17S");
						}
						// si no existen // listo las ubicaciones del proyecto ingresadas en digitalizacion
						if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
							ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(autorizacionAdministrativa.getId(), 2);
						}
						// si  existen busco el arae 
						if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
							UbicacionesGeografica ubicacion = ListaUbicacionTipo.get(0).getUbicacionesGeografica();
							if(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
								areaTramite = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
							} else {
								areaTramite = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
							}
						}
						if(autorizacionAdministrativa.getAreaResponsableControl().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
							
						}
					}
				}
				
				if(areaTramite != null  && areaTramite.getId() != null){
					try {
						rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");
						List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(rolDirector, areaTramite);
						if(listaUsuarios != null && !listaUsuarios.isEmpty()){
							usuarioAutoridad = listaUsuarios.get(0);
							autoridad = usuarioAutoridad.getNombre();
						}else{
							System.out.println("No existe usuario " + rolDirector + " para el area " + areaTramite.getAreaName());						
							return false;
						}
					} catch (Exception e) {
						System.out.println("No existe usuario " + rolDirector + " para el area " + areaTramite.getAreaName());
						return false;
					}
				}else{
					System.out.println("No existe el area " + areaTramite);	
					return false;
				}
			}
			Map<String, Object> params=new HashMap<>();
			params.put("realizoPago", true);
			params.put("director", autoridad);
			procesoFacade.modificarVariablesProceso(usuario, tarea.getProcessInstanceId(), params);
			procesoFacade.aprobarTarea(usuario, tarea.getTaskId(), tarea.getProcessInstanceId(), null);
			JsfUtil.addMessageInfo("Proceso de pago finalizado correctamente.");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	///DIRECTOR ZONAL	Coordinador Control Zonal
	private boolean finalizar(TaskSummaryCustom tarea, Usuario usuario){
		try {
			Boolean requiereFirmaRgd = true;
			Boolean continuar = false;
			Area area=new Area();		
			area = proyectoLicenciaCoa.getAreaResponsable();
			
			Map<String, Object> params=new HashMap<>();
			if ((bandejaTareasBean.getTarea().getProcessId().equals("rcoa.ResolucionLicenciaAmbiental")) && (bandejaTareasBean.getTarea().getTaskName().equals("Generar NUT de pago")))
			{				
//				String roleKey="role.resolucion.cz.gad.autoridad";
//				List<Usuario> usuarios = new ArrayList<Usuario>();	
//				if(proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equals("OT")){
//					usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area.getArea());				
//				}else{
//					usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area);			
//				}	
//				
//				if (usuarios != null && usuarios.size()>0)
//				{
//					usuarioAmbiental = usuarios.get(0).getNombre();
//				}
//				
//				String autoridad="";
//				Usuario usuarioAutoridad = areaFacade.getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental");
//				if(usuarioAutoridad != null && usuarioAutoridad.getId() != null){
//					autoridad = usuarioAutoridad.getNombre();
//				}else{
//					System.out.println("No existe usuario " + Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental") );
//					return false;
//				}
				
				params.put("realizoPago", true);
//				params.put("director", autoridad);
				params.put("requiereFirmaRgd", requiereFirmaRgd);
				params.put("requiereFirmaRsq", false);
				if(requiereFirmaRgd) {
					//buscar zonal correspondiente
					String rolDirector = "";
					Area areaAutoridad = null;
					if (proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
						UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyectoLicenciaCoa.getIdCantonOficina());
						areaAutoridad = ubicacion.getAreaCoordinacionZonal().getArea();
						rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");
					} else {
						areaAutoridad = proyectoLicenciaCoa.getAreaInventarioForestal().getArea();
						rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");

						if(proyectoLicenciaCoa.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
							areaAutoridad = proyectoLicenciaCoa.getAreaResponsable();
							rolDirector = Constantes.getRoleAreaName("role.resolucion.galapagos.autoridad");
						} 
					}					
					try {
						List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(rolDirector, areaAutoridad);
						if(listaUsuarios != null && !listaUsuarios.isEmpty()){
							params.put("autoridadAmbientalRgd", listaUsuarios.get(0).getNombre());
							continuar = true;
						}else{
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
							LOG.error("No se encontro usuario " + rolDirector + " en " + areaAutoridad.getAreaName());						
						}
					} catch (Exception e) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						System.out.println("Error al recuperar la zonal correspondiente.");					
					}
				}
				
				
				if(continuar)
				{
					procesoFacade.modificarVariablesProceso(usuario, tarea.getProcessInstanceId(), params);
					procesoFacade.aprobarTarea(usuario, tarea.getTaskId(), tarea.getProcessInstanceId(), null);
					JsfUtil.addMessageInfo("Proceso de pago finalizado correctamente.");					
				}
			}
			else if ((bandejaTareasBean.getTarea().getProcessId().equals("rcoa.ResolucionLicenciaAmbiental")) && (bandejaTareasBean.getTarea().getTaskName().equals("Ingresar el valor del proyecto")))
			{
				Usuario usuarioFinanciero = asignarTecnicoFinanciero(proyectosBean.getProyectoRcoa());
				if ((usuarioFinanciero != null) && (usuarioFinanciero.getId() != null))
				{
					params.put("tecnicoFinanciero", usuarioFinanciero.getNombre());	
				
//				procesoFacade.modificarVariablesProceso(usuario, bandejaTareasBean.getProcessId(), params);
//				procesoFacade.aprobarTarea(usuario, bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);   			
//    			enviarNotificacion(usuarioFinanciero,proyectosBean.getProyectoRcoa());  requiereFirmaRgd
				JsfUtil.addMessageInfo("Proceso de pago finalizado correctamente.");
				}
			} else 
			{
				procesoFacade.modificarVariablesProceso(usuario, tarea.getProcessInstanceId(), params);
				Map<String, Object> data = new ConcurrentHashMap<String, Object>();
				procesoFacade.aprobarTarea(usuario, tarea.getTaskId(), tarea.getProcessInstanceId(), data);
				JsfUtil.addMessageInfo("Proceso de pago finalizado correctamente.");	
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	   
    public void redireccionar_error() {
		try {	JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (Exception e) {	JsfUtil.addMessageError(e.getMessage()); }
	}
     
    
    public void enviarNotificacion(Usuario tecnicoFinanciero, ProyectoLicenciaCoa proyectoLicenciaCoa) throws ServiceException {
    	String nombreTecnico = tecnicoFinanciero.getPersona().getNombre();
		String nombreOperador = "";
		Usuario usuarioOperador = proyectoLicenciaCoa.getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		Object[] parametrosCorreoNuevo = new Object[] {nombreTecnico, nombreOperador,  proyectoLicenciaCoa.getCodigoUnicoAmbiental(),  
				proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getAreaResponsable().getAreaName()};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionRegistroValoresResolucionAmbiental");
		String notificacionNuevo = String.format(mensajeNotificacion.getValor(), parametrosCorreoNuevo);
		
		Email.sendEmail(tecnicoFinanciero, mensajeNotificacion.getAsunto(), notificacionNuevo, tramite, loginBean.getUsuario());
    }
    
    public void redireccionar_mantenimiento() {
		try {	JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (Exception e) {	JsfUtil.addMessageError(e.getMessage()); }
	}    

    public void pago_info() {
			RequestContext.getCurrentInstance().execute("PF('dlgMsjInfo').hide();");
			JsfUtil.redirectTo(pagoRcoaBean.getUrl_enlace());
	}
	
	public void redireccionar_pago() {
		try {
	        	Usuario usuario = null;
	        	TaskSummaryCustom tarea = new TaskSummaryCustom();
	        	//LLAMADO AL METODO_QUE REDIRECIONA BMP
	        	if (pagoRcoaBean.getControl_salto()){
	        		///Proceder con los pagos faltantes y pulsar enviar        		
	        		System.out.println("//// Entro a los pagos faltantes");
	            	MsjInfo = "Mensaje información pagos";
	            	SuccessMsjInfo = "Proceder con los pagos faltantes y click en el botón enviar";
	            	System.out.println(MsjInfo + SuccessMsjInfo);
	            	RequestContext context = RequestContext.getCurrentInstance();
	            	context.execute("PF('dlgMsjInfo').show();");            	
	        	} else{
	        		obtenerPagosRealizados();
	        		JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
	        	} 	      	
		} catch (Exception e) {	JsfUtil.addMessageError(e.getMessage()); }
	}
	
	public static void execute(){
		  TrustManager[] trustAllCerts = new TrustManager[] {
		        new X509TrustManager() {
		          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		           return null;
		          }
		          @Override
		          public void checkClientTrusted(X509Certificate[] arg0, String arg1)
		           throws CertificateException {}
		 
		          @Override
		          public void checkServerTrusted(X509Certificate[] arg0, String arg1)
		            throws CertificateException {}
		          }
		     };
		  SSLContext sc=null;
		  try {
		   sc = SSLContext.getInstance("TLSv1.2");
		  } catch (NoSuchAlgorithmException e) {
		   e.printStackTrace();
		   System.out.println("Ocurrio un error al generar el protocolo SSL");
		  }
		  try {
		   sc.init(null, trustAllCerts, new java.security.SecureRandom());
		  } catch (KeyManagementException e) {
		   e.printStackTrace();
		   System.out.println("Ocurrio un error al generar el certificado de confianza");
		  }
		  HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		  // Create all-trusting host name verifier
		  HostnameVerifier validHosts = new HostnameVerifier() {
		  @Override
		  public boolean verify(String arg0, SSLSession arg1) {
		   return true;
		  }
		  };
		  // All hosts will be valid
		  HttpsURLConnection.setDefaultHostnameVerifier(validHosts);
		}
	
	public boolean consulta_datos_usuario(ResponseData Data) throws IOException
	{	
		Boolean Usuario = false;
        String TipoContenido = "application/json";  
        Integer Implent_Protocol = Carga_Parametros_Numericos(23, 1);
        if (Implent_Protocol != 0){  execute();  }
        else{
    		try {
	        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
	        ctx.init(null, null, null);
	        SSLContext.setDefault(ctx);
	        System.out.println("Cambio Implementation protocol  Exitoso");
	        } catch (Exception e) {
	        System.out.println(e.getMessage());
	        System.out.println("Ocurrio un error al generar el certificado");
	        }              	
        }
		URL url = new URL(Carga_Parametros(30, 2));
		HttpURLConnection http_SW = (HttpURLConnection)url.openConnection();
		http_SW.setRequestMethod("POST");
		http_SW.setDoOutput(true);
		http_SW.setRequestProperty("Content-Type", TipoContenido);		
		String data =  "{\n  \"usuario\": \""+ Data.getUsuarioPago() +"\",\n  "
		+ "\"contrasena\": \""+ Data.getContrasena() +"\",\n  \"nutCodigo\": \""+ Data.getNutCodigo() +"\", \n "
		+ "\"valorPago\":  \""+ Data.getValorPago() +"\",  \n "
		+ "\"numeroTramite\":  \""+ Data.getNumeroTramite() +"\",  \n "
		+ "\"fechaPago\":  \""+ Data.getFechaPago() +"\n}";	
		byte[] out = data.getBytes(StandardCharsets.UTF_8);
		System.out.println(data);
		try {
    		OutputStream stream = http_SW.getOutputStream();
    		stream.write(out);    		   		
    		ResponseTransacion = http_SW.getResponseCode();  
    		System.out.println(http_SW.getResponseCode());  			
		} catch (Exception e) {
			Usuario = false;
			System.out.println("Ocurrio un error al realizar la transaccion de registro pago");
	        }   		
		if (ResponseTransacion == 200){
	    	StringBuilder sbexito = new StringBuilder();
	    	try(BufferedReader reader_exito = new BufferedReader(new InputStreamReader(http_SW.getInputStream(), "utf-8"))) 
	    	{
	    	    String line = null;
	    	    while ((line = reader_exito.readLine()) != null) 
	    	    {
	    	    	sbexito.append(line.trim());
	    	    }
			} catch (Exception e) {
		        System.out.println("No se pudo recuperar la data del servicio de validacion del Usuario para su logueo de Pago en Línea");
		        }         	                           
        	String JsonExito = sbexito.toString();
            JsonParser parser1 = new JsonParser();
            JsonElement element1 = parser1.parse(JsonExito);
            JsonObject jsonObject1 = element1.getAsJsonObject();
            Data.setCodigoRespuesta(jsonObject1.get("codigoRespuesta").getAsString());
            Data.setMensaje(jsonObject1.get("mensaje").getAsString());
            String mensaje_respuesta_ws = Carga_Parametros(31, 2);
            if (Data.getMensaje().equals(mensaje_respuesta_ws)){ Usuario = false; } 
            else{ Usuario = true; }                  
		} else{
			System.out.println("Ocurrio un error en el servicio de validacion del Usuario para su logueo de Pago en Línea");
		}
		http_SW.disconnect();
		return Usuario;
	}		
	
	public void generarNUT() throws Exception{
		try
		{
			//////////////////Eliminar NUTs anteriores 
			List<NumeroUnicoTransaccional> listado = new ArrayList<NumeroUnicoTransaccional>();
			listado = numeroUnicoTransaccionalFacade.listNUTActivosPagoOnline(pagoRcoaBean.getTramite());
			if((listado != null) && (listado.size() > 0))
			{
				for(NumeroUnicoTransaccional numeroT : listado)
				{
					numeroT.setEstado(false);
					numeroUnicoTransaccionalFacade.guardarNUT(numeroT);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		String mensaje = "", tasaDescripcion="", tasaPagodescripcion="";
		Integer cuantaId=8;
		Double valorTotal = 0.0;
		String tasaDescripcionRgd = "";
		Map<Double, Map<String,String>> listaValores = new TreeMap<Double, Map<String,String>>();
		Map<String,String> listaDescripcion = new TreeMap<String,String>();
		nuevoComprobante=false;
		if ((rgd) && (esFisico))
		{
//			tasaDescripcion = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
//			cuantaId=8;
//			mensaje = "Pago correspondiente a RGDP, pago en linea";
//			valorTotal = pagoRcoaBean.getValorAPagar();
//			listaDescripcion.put(tasaDescripcion, mensaje);
//			listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
//			lista_rates_nut = listaValores;
			
			switch (pagoRcoaBean.getTipoProyecto()) {
			case "RGD":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a RGDP, pago en linea";
				valorTotal = pagoRcoaBean.getValorAPagar();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);
				break;
			case "ESTUDIOIMPACTO":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a revisión Estudio de impacto ambiental, pago en linea";
				valorTotal = pagoRcoaBean.getValorAPagar();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);
				break;
			case "RSQ":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.REGISTRO_SUSTANCIAS_QUIMICAS.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a pago de sustancias químicas, pago en linea";
				valorTotal = pagoRcoaBean.getValorAPagar();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);
				break;
			case "CERTIFICADO":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.INVENTARIO_FORESTAL_MADERA_PIE.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a remoción de cobertura vegetal nativa, pago en linea";
				valorTotal = pagoRcoaBean.getValorAPagar();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);
				break;
			case "REGISTRO":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.REGISTRO_AMBIENTAL.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a Registro Ambiental, pago en linea";
				valorTotal = pagoRcoaBean.getValorAPagar();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);
				if(pagoRcoaBean.getMontoTotalCoberturaVegetal() > 0){
					generarNutCoberturaVegetal();
				}
				break;
			case "PARTICIPACION":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.FACILIATDOR.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a la tasa por servicios de facilitación, pago en linea";
				valorTotal = pagoRcoaBean.getValorAPagar();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
				//listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), tasaDescripcion);
				lista_rates_nut.putAll(listaValores);
				//lista_rates_nut.put(pagoRcoaBean.getMontoTotalProyecto(), tasaDescripcion);
				break;
			case "LICENCIA":
				listaDescripcion = new TreeMap<String,String>();
				if (pagoRcoaBean.getCategorizacion()==3) {
					tasaDescripcion = CodigoTasa.EMISION_LICENCIA_PAGO_MINIMO_BAJO_IMPACTO.getDescripcion();
				} else {
					tasaDescripcion = CodigoTasa.EMISION_LICENCIA_PAGO_MINIMO_ALTO_IMPACTO.getDescripcion();
				}
				cuantaId=8;
	                tasaPagodescripcion = CodigoTasa.PAGO_EN_LINEA_COSTO_PROYECTO.getDescripcion();	
				if(!pagoRcoaBean.isEsEnteAcreditado()){
					listaDescripcion = new TreeMap<String,String>();
					listaValores = new TreeMap<Double, Map<String,String>>();
					mensaje = "Pago correspondiente al costo del proyecto, pago en linea";
					//lista_rates_nut.put(Double.valueOf(pagoRcoaBean.getValproy()), tasaPagodescripcion);
					//mensaje += (mensaje.isEmpty() ? "" : ", ")+"Pago correspondiente a la tasa por costo del proyecto, pago en linea";
					valorTotal = pagoRcoaBean.getValorAPagar();		
					listaDescripcion.put(tasaDescripcion, mensaje);
					listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
					lista_rates_nut.putAll(listaValores);
					//lista_rates_nut.put(pagoRcoaBean.getValorAPagar(), tasaDescripcion);
				}
				if(pagoRcoaBean.getMontoTotalRGD() > 0){
					listaDescripcion = new TreeMap<String,String>();
					listaValores = new TreeMap<Double, Map<String,String>>();
					tasaDescripcionRgd = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
					//mensaje += (mensaje.isEmpty() ? "" : ", ")+ "Pago correspondiente a RGDP, pago en linea";
					mensaje = "Pago correspondiente a RGDP, pago en linea";
					valorTotal = pagoRcoaBean.getValorAPagar();
					//lista_rates_nut.put(pagoRcoaBean.getMontoTotalRGD(), tasaDescripcionRgd);
					listaDescripcion.put(tasaDescripcionRgd, mensaje);
					listaValores.put(pagoRcoaBean.getMontoTotalRGD(), listaDescripcion);
					lista_rates_nut.putAll(listaValores);
				}
				if(pagoRcoaBean.getMontoTotalCoberturaVegetal() > 0){
					generarNutCoberturaLicencia();
					//mensaje += (mensaje.isEmpty() ? "" : ", ")+"Pago correspondiente al Inventario Forestal, pago en linea";
					//valorTotal = pagoRcoaBean.getValorAPagar();
				}
				//listaValores.put(pagoRcoaBean.getMontoTotalRGD(), tasaDescripcion);
				break;
			default:
				break;
			}			
			/////////////////////////			
			for (Entry<Double, Map<String,String>> valorPago : lista_rates_nut.entrySet()) {
				Map<String,String> valorDescripcion = new TreeMap<String,String>();
				valorDescripcion.putAll(valorPago.getValue());
				Double valor = valorPago.getKey();
				String mensajeTasa="";
				String codigoTasa ="";	
				for (Entry<String, String> entry1 : valorDescripcion.entrySet()) {
					codigoTasa = entry1.getKey();
					mensajeTasa = entry1.getValue();
				}
				//EntidadPagoNUTRcoa entidadpago=generarNUTPago(pagoRcoaBean.getTramite(), valorTotal, cuantaId, mensaje,tasaDescripcion,valorDescripcion.get(0).toString(),valor);
				///EntidadPagoNUTRcoa generarNUTPago(String tramite, Double montoTotalProyecto, Integer cuenta, String mensaje, String tasaDescripcion, Double valorTarifa) throws Exception{	
				EntidadPagoNUTRcoa entidadpago=generarNUTPago(pagoRcoaBean.getTramite(), valorTotal, cuantaId, mensajeTasa,codigoTasa,valor);
				if(!entidadpago.isCorrecto()){
					if(entidadpago.getNumeroUnicoTransaccional() != null && entidadpago.getNumeroUnicoTransaccional().getId() != null
						&& entidadpago.getNumeroUnicoTransaccional().getEstadosNut().getId().equals(2)){
						List<DocumentoNUT> comprobantes = listarDocumentosTarea(pagoRcoaBean.getTramite());				
						if (comprobantes.size() > 0) { pagoRcoaBean.getDocumentosNUT().addAll(comprobantes); } 
					}
					JsfUtil.addMessageError(entidadpago.getMensaje());
					return;
				}
				Integer numeroDocumento=1;
				DocumentoNUT documentoPago = new DocumentoNUT();
				documentoPago.setContenidoDocumento(entidadpago.getArchivo());
				documentoPago.setMime("application/pdf");
				documentoPago.setIdTabla(entidadpago.getSolicitudUsuario().getId());
				documentoPago.setNombreTabla(SolicitudUsuario.class.getSimpleName());
				documentoPago.setNombre("Orden_de_pago" + ".pdf");
				documentoPago.setExtesion(".pdf");
				documentoPago.setSolicitudId(entidadpago.getSolicitudUsuario().getId());
				documentoPago.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
				documentoPago = documentosNUTFacade.guardarDocumentoAlfresco(
								entidadpago.getNumeroUnicoTransaccional().getNutCodigoProyecto() != null ? entidadpago.getNumeroUnicoTransaccional().getNutCodigoProyecto() : entidadpago.getSolicitudUsuario().getSolicitudCodigo(),
								"RECAUDACIONES", null, documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
				if(pagoRcoaBean.getDocumentosNUT() == null)
					pagoRcoaBean.setDocumentosNUT(new ArrayList<DocumentoNUT>());
				pagoRcoaBean.getDocumentosNUT().add(documentoPago);
				numeroDocumento++;		
			}	
		}
		else
		{
			if(pagoRcoaBean.getInstitucionFinancieraNut() == null || pagoRcoaBean.getInstitucionFinancieraNut().getId() == null ){
				JsfUtil.addMessageError("El campo Banco es requerido.");
				return;
			}
			switch (pagoRcoaBean.getTipoProyecto()) {
			case "RGD":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a RGDP, pago en linea";
				valorTotal = pagoRcoaBean.getValorAPagar();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);
				break;
			case "ESTUDIOIMPACTO":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a revisión Estudio de impacto ambiental, pago en linea";
				valorTotal = pagoRcoaBean.getValorAPagar();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);
				break;
			case "RSQ":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.REGISTRO_SUSTANCIAS_QUIMICAS.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a pago de sustancias químicas, pago en linea";
				valorTotal = pagoRcoaBean.getValorAPagar();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);
				break;
			case "CERTIFICADO":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.INVENTARIO_FORESTAL_MADERA_PIE.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a remoción de cobertura vegetal nativa, pago en linea";
				valorTotal = pagoRcoaBean.getValorAPagar();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);
				break;
			case "REGISTRO":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.REGISTRO_AMBIENTAL.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a Registro Ambiental, pago en linea";
				valorTotal = pagoRcoaBean.getValorAPagar();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);
				if(pagoRcoaBean.getMontoTotalCoberturaVegetal() > 0){
					generarNutCoberturaVegetal();
				}
				break;
			case "PARTICIPACION":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.FACILIATDOR.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a la tasa por servicios de facilitación, pago en linea";
				valorTotal = pagoRcoaBean.getValorAPagar();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
				//listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), tasaDescripcion);
				lista_rates_nut.putAll(listaValores);
				//lista_rates_nut.put(pagoRcoaBean.getMontoTotalProyecto(), tasaDescripcion);
				break;
			case "LICENCIA":
				if (pagoRcoaBean.getCategorizacion()==3) {
					tasaDescripcion = CodigoTasa.EMISION_LICENCIA_PAGO_MINIMO_BAJO_IMPACTO.getDescripcion();
				} else {
					tasaDescripcion = CodigoTasa.EMISION_LICENCIA_PAGO_MINIMO_ALTO_IMPACTO.getDescripcion();
				}
				cuantaId=8;
	                tasaPagodescripcion = CodigoTasa.PAGO_EN_LINEA_COSTO_PROYECTO.getDescripcion();	
				if(!pagoRcoaBean.isEsEnteAcreditado()){
					listaDescripcion = new TreeMap<String,String>();
					listaValores = new TreeMap<Double, Map<String,String>>();
					mensaje = "Pago correspondiente al costo del proyecto, pago en linea";
					//lista_rates_nut.put(Double.valueOf(pagoRcoaBean.getValproy()), tasaPagodescripcion);
					//mensaje += (mensaje.isEmpty() ? "" : ", ")+"Pago correspondiente a la tasa por costo del proyecto, pago en linea";
					valorTotal = pagoRcoaBean.getValorAPagar();		
					listaDescripcion.put(tasaDescripcion, mensaje);
					listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), listaDescripcion);
					lista_rates_nut.putAll(listaValores);
					//lista_rates_nut.put(pagoRcoaBean.getValorAPagar(), tasaDescripcion);
				}
				if(pagoRcoaBean.getMontoTotalRGD() > 0){
					listaDescripcion = new TreeMap<String,String>();
					listaValores = new TreeMap<Double, Map<String,String>>();
					tasaDescripcionRgd = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
					//mensaje += (mensaje.isEmpty() ? "" : ", ")+ "Pago correspondiente a RGDP, pago en linea";
					mensaje = "Pago correspondiente a RGDP, pago en linea";
					valorTotal = pagoRcoaBean.getValorAPagar();
					//lista_rates_nut.put(pagoRcoaBean.getMontoTotalRGD(), tasaDescripcionRgd);
					listaDescripcion.put(tasaDescripcionRgd, mensaje);
					listaValores.put(pagoRcoaBean.getMontoTotalRGD(), listaDescripcion);
					lista_rates_nut.putAll(listaValores);
				}
				if(pagoRcoaBean.getMontoTotalCoberturaVegetal() > 0){
					generarNutCoberturaLicencia();
					//mensaje += (mensaje.isEmpty() ? "" : ", ")+"Pago correspondiente al Inventario Forestal, pago en linea";
					//valorTotal = pagoRcoaBean.getValorAPagar();
				}
				//listaValores.put(pagoRcoaBean.getMontoTotalRGD(), tasaDescripcion);
				break;
			default:
				break;
			}
			for (Entry<Double, Map<String,String>> valorPago : lista_rates_nut.entrySet()) {
				Map<String,String> valorDescripcion = new TreeMap<String,String>();
				valorDescripcion.putAll(valorPago.getValue());
				Double valor = valorPago.getKey();
				String mensajeTasa="";
				String codigoTasa ="";	
				for (Entry<String, String> entry1 : valorDescripcion.entrySet()) {
					codigoTasa = entry1.getKey();
				     mensajeTasa = entry1.getValue();
				}
				//EntidadPagoNUTRcoa entidadpago = generarNUTPago(pagoRcoaBean.getTramite(), valorTotal, cuantaId, mensaje,tasaDescripcion,codTarifa,valor);
				EntidadPagoNUTRcoa entidadpago=generarNUTPago(pagoRcoaBean.getTramite(), valorTotal, cuantaId, mensajeTasa,codigoTasa,valor);
				if(!entidadpago.isCorrecto()){
					if(entidadpago.getNumeroUnicoTransaccional() != null && entidadpago.getNumeroUnicoTransaccional().getId() != null
						&& entidadpago.getNumeroUnicoTransaccional().getEstadosNut().getId().equals(2)){
						List<DocumentoNUT> comprobantes = listarDocumentosTarea(pagoRcoaBean.getTramite());				
						if (comprobantes.size() > 0) { pagoRcoaBean.getDocumentosNUT().addAll(comprobantes); } 
					}
					JsfUtil.addMessageError(entidadpago.getMensaje());
					return;
				}
				Integer numeroDocumento=1;
				DocumentoNUT documentoPago = new DocumentoNUT();
				documentoPago.setContenidoDocumento(entidadpago.getArchivo());
				documentoPago.setMime("application/pdf");
				documentoPago.setIdTabla(entidadpago.getSolicitudUsuario().getId());
				documentoPago.setNombreTabla(SolicitudUsuario.class.getSimpleName());
				documentoPago.setNombre("Orden_de_pago" + ".pdf");
				documentoPago.setExtesion(".pdf");
				documentoPago.setSolicitudId(entidadpago.getSolicitudUsuario().getId());
				documentoPago.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
				documentoPago = documentosNUTFacade.guardarDocumentoAlfresco(
								entidadpago.getNumeroUnicoTransaccional().getNutCodigoProyecto() != null ? entidadpago.getNumeroUnicoTransaccional().getNutCodigoProyecto() : entidadpago.getSolicitudUsuario().getSolicitudCodigo(),
								"RECAUDACIONES", null, documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
				if(pagoRcoaBean.getDocumentosNUT() == null)
					pagoRcoaBean.setDocumentosNUT(new ArrayList<DocumentoNUT>());
				pagoRcoaBean.getDocumentosNUT().add(documentoPago);
				numeroDocumento++;	
			}
		}
	}
	
    public void generarNutCoberturaVegetal() {
		try{
			String codigoTramite=proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental();
			String mensaje = "", tasaDescripcion="";
			Integer cuantaId=8;
			Double valorTotal = 0.0;
			Map<Double, Map<String,String>> listaValores = new TreeMap<Double, Map<String,String>>();
			Map<String,String> listaDescripcion = new TreeMap<String,String>();
			switch (pagoRcoaBean.getTipoProyecto()) {
			case "RGD":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a RGDP, pago en linea";
				valorTotal = pagoRcoaBean.getMontoTotalCoberturaVegetal();				
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalCoberturaVegetal(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);	
				break;				
			case "RSQ":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.REGISTRO_SUSTANCIAS_QUIMICAS.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a pago de sustancias químicas, pago en linea";
				valorTotal = pagoRcoaBean.getMontoTotalCoberturaVegetal();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalCoberturaVegetal(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);	
				break;
			case "CERTIFICADO":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.INVENTARIO_FORESTAL_MADERA_PIE.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a remoción de cobertura vegetal nativa, pago en linea";
				valorTotal = pagoRcoaBean.getMontoTotalCoberturaVegetal();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalCoberturaVegetal(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);	
				break;
			case "REGISTRO":
				listaDescripcion = new TreeMap<String,String>();
				listaValores = new TreeMap<Double, Map<String,String>>();
				tasaDescripcion = CodigoTasa.INVENTARIO_FORESTAL_MADERA_PIE.getDescripcion();
				cuantaId=8;
				mensaje = "Pago correspondiente a remoción de cobertura vegetal nativa, pago en linea";
				valorTotal = pagoRcoaBean.getMontoTotalCoberturaVegetal();
				listaDescripcion.put(tasaDescripcion, mensaje);
				listaValores.put(pagoRcoaBean.getMontoTotalCoberturaVegetal(), listaDescripcion);
				lista_rates_nut.putAll(listaValores);	
				break;
			case "LICENCIA":
				cuantaId=8;
				tasaDescripcion = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
				if(pagoRcoaBean.getMontoTotalRGD() > 0){
					listaDescripcion = new TreeMap<String,String>();
					listaValores = new TreeMap<Double, Map<String,String>>();
					//mensaje += (mensaje.isEmpty() ? "" : ", ")+"Pago correspondiente a RGDP, pago en linea";
					mensaje = "Pago correspondiente a RGDP, pago en linea";
					valorTotal = pagoRcoaBean.getMontoTotalRGD();
					//lista_rates_nut.put(pagoRcoaBean.getMontoTotalRGD(), tasaDescripcion);
					listaDescripcion.put(tasaDescripcion, mensaje);
					listaValores.put(pagoRcoaBean.getMontoTotalRGD(), listaDescripcion);
					lista_rates_nut.putAll(listaValores);	
				}
				if(pagoRcoaBean.getMontoTotalCoberturaVegetal() > 0){
					generarNutCoberturaLicencia();
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generarNutCoberturaLicencia() {
		try{
			Map<Double, Map<String,String>> listaValores = new TreeMap<Double, Map<String,String>>();
			Map<String,String> listaDescripcion = new TreeMap<String,String>();
			String codigoTramite=proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental();
			String mensaje = "", tasaDescripcion="";
			Integer cuantaId=8;
			tasaDescripcion = CodigoTasa.INVENTARIO_FORESTAL_MADERA_PIE.getDescripcion();
			//mensaje += (mensaje.isEmpty() ? "" : ", ")+"Pago correspondiente al Inventario Forestal, pago en linea";
			mensaje ="Pago correspondiente al Inventario Forestal, pago en linea";		
			listaDescripcion.put(tasaDescripcion, mensaje);
			listaValores.put(pagoRcoaBean.getMontoTotalCoberturaVegetal(), listaDescripcion);
			lista_rates_nut.putAll(listaValores);
			
			
		//lista_rates_nut.put(pagoRcoaBean.getMontoTotalCoberturaVegetal(), tasaDescripcion);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<DocumentoNUT> listarDocumentosTarea(String tramite){
		List<DocumentoNUT> comprobantes = obtenerDocumentosNut(tramite);
		List<DocumentoNUT> elininarcomprobantesOtraTarea = new ArrayList<DocumentoNUT>();
		if(comprobantes.size() > 0){
			for (DocumentoNUT objDocumentoNUT : comprobantes) {
				if(objDocumentoNUT.getIdProceso() != null && !objDocumentoNUT.getIdProceso().equals(bandejaTareasBean.getTarea().getProcessInstanceId())){
					elininarcomprobantesOtraTarea.add(objDocumentoNUT);
				}
			}
			if(elininarcomprobantesOtraTarea.size() > 0){
				comprobantes.removeAll(elininarcomprobantesOtraTarea);
			}
		}
		return comprobantes;
	}
	
	public EntidadPagoNUTRcoa generarNUTPago(String tramite, Double montoTotalProyecto, Integer cuenta, String mensaje, String tasaDescripcion, Double valorTarifa) throws Exception{	
		EntidadPagoNUTRcoa entidadPagoNUTRcoa = new EntidadPagoNUTRcoa();
		List<NumeroUnicoTransaccional> listNUTXTramite=new ArrayList<NumeroUnicoTransaccional>();
		//for (Entry<Double, String> valorPago : listaMontoProyecto.entrySet()) {
			Tarifas tarifa = tarifasFacade.buscarTarifasPorCodigo(tasaDescripcion);
			if(tarifa == null) {
				entidadPagoNUTRcoa.setCorrecto(false);
				entidadPagoNUTRcoa.setMensaje("Ocurrió un error al generar el NUT. Por favor comunicarse con mesa de ayuda.");
				return entidadPagoNUTRcoa;
			}
		//}
		Boolean Val_Usuario = false;
		//ResponseData DataUser = new ResponseData();
		//DataUser.setUsuarioPago(Carga_Parametros(32, 2));
		//DataUser.setContrasena(Carga_Parametros(33, 2));
		//Val_Usuario = consulta_datos_usuario(DataUser);	
		SolicitudUsuario solicitudUsuario= new SolicitudUsuario();
		solicitudUsuario.setUsuario(loginBean.getUsuario());
		solicitudUsuario.setInstitucionFinanciera(pagoRcoaBean.getInstitucionFinancieraNut());
		solicitudUsuario.setValorTotal(valorTarifa);
		String codigoTramite=tramite;
		listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramitePorTarea(codigoTramite, bandejaTareasBean.getTarea().getProcessInstanceId());
		if(listNUTXTramite!=null && listNUTXTramite.size()>0 && !nuevoComprobante){
			for (NumeroUnicoTransaccional nut : listNUTXTramite) {
					if(nut.getEstadosNut().getId()==5){
						nut.setNutFechaActivacion(new Date());
						nut.setBnfFechaPago(new Date());
						nut.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
						nut.setEstadosNut(new EstadosNut(3));
						nut.setBnfTramitNumber(pagoRcoaBean.getTicketNumber());
						nut.setNutDescripcion(mensaje);
						nut.setNutUsado(true);
						nut.setNutValor(valorTarifa);
						if (!Val_Usuario)
						{
							nut.setUsuarioModificacion("userpayonline");
						}						
						crudServiceBean.saveOrUpdate(nut);
						entidadPagoNUTRcoa.setSolicitudUsuario(nut.getSolicitudUsuario());
						entidadPagoNUTRcoa.setNumeroUnicoTransaccional(nut);
						entidadPagoNUTRcoa.setMensaje("Trámite actualizado");
						SolicitudUsuario aux = new SolicitudUsuario();
						aux = nut.getSolicitudUsuario();
						aux.setInstitucionFinanciera(pagoRcoaBean.getInstitucionFinancieraNut());
						crudServiceBean.saveOrUpdate(aux);
					}					
					entidadPagoNUTRcoa.setCorrecto(false);
					if(entidadPagoNUTRcoa.getMensaje() == null || entidadPagoNUTRcoa.getMensaje().isEmpty())
						entidadPagoNUTRcoa.setMensaje("Usted ya tiene generado un Número Único de Trámite, por favor descargue en documentos del trámite.");
			}
			if(!entidadPagoNUTRcoa.isCorrecto()){
				nuevoComprobante=false;
				return entidadPagoNUTRcoa;
			}
		}else{ nuevoComprobante=true; }		
		solicitudUsuario.setSolicitudDescripcion(mensaje);
		solicitudUsuario.setSolicitudCodigo(generarCodigoSolicitud());
		crudServiceBean.saveOrUpdate(solicitudUsuario);
		setSolicitudUsuario(solicitudUsuario);
		//para crear varios nut para el pago
		//////////////////////////////////////////
		numeroUnicoTransaccional = new NumeroUnicoTransaccional();		
		//for (Entry<Double, String> valorPago : listaMontoProyecto.entrySet()) {
			numeroUnicoTransaccional.setNutCodigo(secuenciasFacade.getNextValueDedicateSequence("NUT_CODIGO", 10));
			numeroUnicoTransaccional.setNutFechaActivacion(new Date());
			numeroUnicoTransaccional.setBnfFechaPago(new Date());
			numeroUnicoTransaccional.setEstadosNut(new EstadosNut(3));
			numeroUnicoTransaccional.setNutDescripcion(mensaje);
			numeroUnicoTransaccional.setNutUsado(true);
			numeroUnicoTransaccional.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
			numeroUnicoTransaccional.setSolicitudUsuario(solicitudUsuario);
			numeroUnicoTransaccional.setCuentas(new Cuentas(cuenta));
			numeroUnicoTransaccional.setBnfTramitNumber(pagoRcoaBean.getTicketNumber());
			numeroUnicoTransaccional.setNutValor(valorTarifa);
			numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
			if (!Val_Usuario){
				numeroUnicoTransaccional.setUsuarioModificacion("userpayonline");	
			}			
			numeroUnicoTransaccional.setNutUsado(true);
			crudServiceBean.saveOrUpdate(numeroUnicoTransaccional);
		//}		
		//for (Entry<Double, String> valorPago : lista_rates_nut.entrySet()) {
		Tarifas tarifaN = tarifasFacade.buscarTarifasPorCodigo(tasaDescripcion);
		TarifasNUT tarifasNUT = new TarifasNUT();
		tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
		tarifasNUT.setTarifas(tarifaN);
		tarifasNUT.setCantidad(1);
		tarifasNUT.setValorUnitario(valorTarifa);
		crudServiceBean.saveOrUpdate(tarifasNUT);	
		//}
		byte[] contenidoDocumento = generarDocumentoVariosNut(solicitudUsuario);
		entidadPagoNUTRcoa.setSolicitudUsuario(solicitudUsuario);
		entidadPagoNUTRcoa.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
		entidadPagoNUTRcoa.setArchivo(contenidoDocumento);
		entidadPagoNUTRcoa.setCorrecto(true);
		return entidadPagoNUTRcoa;
	}
	
	public List<DocumentoNUT> obtenerDocumentosNut(String codigoTramite){
		List<DocumentoNUT> documentosAux = new ArrayList<>();
		InstitucionFinanciera banco = new InstitucionFinanciera();
		try{
			List<NumeroUnicoTransaccional> listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoTramite);
			if(listNUTXTramite!=null && listNUTXTramite.size()>0){
				for (NumeroUnicoTransaccional nut : listNUTXTramite) {
					if(nut.getEstadosNut().getId().equals(5))
					continue;
					List<DocumentoNUT> comprobantes =  documentosNUTFacade
							.documentoXTablaIdXIdDoc(nut.getSolicitudUsuario().getId(), 
									TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS, SolicitudUsuario.class.getSimpleName());					
					if (comprobantes.size() > 0) {
						for (DocumentoNUT documentoNUT : comprobantes) {
							if(!documentosAux.contains(documentoNUT))
								documentosAux.add(documentoNUT);
							if(documentoNUT.getIdProceso().equals(bandejaTareasBean.getTarea().getProcessInstanceId()))
								banco = nut.getSolicitudUsuario().getInstitucionFinanciera();
						}
					}
				}
			}else{
				listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTPorTramite(codigoTramite);
				if(listNUTXTramite!=null && listNUTXTramite.size()>0){
					for (NumeroUnicoTransaccional nut : listNUTXTramite) {
						List<DocumentoNUT> comprobantes =  documentosNUTFacade
								.documentoXTablaIdXIdDoc(nut.getSolicitudUsuario().getId(), 
										TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS, SolicitudUsuario.class.getSimpleName());
						
						if (comprobantes.size() > 0) {
							documentosAux.addAll(comprobantes);
							banco = nut.getSolicitudUsuario().getInstitucionFinanciera();
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(documentosAux != null && documentosAux.size() > 0){
			pagoRcoaBean.setTipoPago("NUT");
			if(banco.getId() != null)
				pagoRcoaBean.setInstitucionFinancieraNut(banco);
		}
		return documentosAux;
	}
	
	public  byte[] generarDocumentoVariosNut(SolicitudUsuario solicitudUsuario){
		FileOutputStream file;
		List<File> listaFiles = new ArrayList<File>();
		byte[] byteArchivo=null;
		String html="", nombreReporte="";
		Double totalNuts = 0.00;
		nombreReporte= "Recaudaciones-"+solicitudUsuario.getSolicitudCodigo();
		PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS_XBANCO.getIdTipoDocumento());
		NumeroUnicoTransaccional unicoTransaccional= new NumeroUnicoTransaccional();
		unicoTransaccional.setNombreProponenteReporte(nombreUsuario(solicitudUsuario.getUsuario()));
		unicoTransaccional.setCodigoSolicitudReporte(solicitudUsuario.getSolicitudCodigo());
		unicoTransaccional.setTasaDescripcion(solicitudUsuario.getSolicitudDescripcion());
		if(solicitudUsuario.getInstitucionFinanciera() != null)
			unicoTransaccional.setNombreBanco(solicitudUsuario.getInstitucionFinanciera().getNombreInstitucion());
		// para ponber la ciudad y fecha en el NUT generado
		String ciudad = "";
		if(loginBean.getUsuario().getPersona().getUbicacionesGeografica() != null && loginBean.getUsuario().getPersona().getUbicacionesGeografica().getUbicacionesGeografica() != null)
			ciudad = loginBean.getUsuario().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + ", ";
		unicoTransaccional.setFechaEmision(ciudad + JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
		try {
			//busco los nbut generados por solicitud
			List<NumeroUnicoTransaccional> listaNuts = numeroUnicoTransaccionalFacade.listBuscaNUTPorSolicitud(solicitudUsuario.getSolicitudCodigo());
			for (NumeroUnicoTransaccional objNut : listaNuts) {
				unicoTransaccional.setSolicitudReporte("trámite: "+objNut.getNutCodigoProyecto());
				html += "<br/>"+"<strong>N&uacute;mero de Pago: </strong>"+objNut.getNutCodigo()+"<br />";
				html += "<strong>Valor de Pago: </strong>"+objNut.getNutValor().toString()+"<br />";
				totalNuts += objNut.getNutValor();
			}
			unicoTransaccional.setDescripcionNuts(html);
			unicoTransaccional.setTotalNutReporte(totalNuts.toString());
			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true, unicoTransaccional,null);
            Path path = Paths.get(informePdf.getAbsolutePath());
            byteArchivo = Files.readAllBytes(path);
            File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(informePdf.getName()));
            file = new FileOutputStream(fileArchivo);
            file.write(byteArchivo);
            file.close();
            listaFiles.add(fileArchivo);
            listPathArchivos.add(System.getProperty("java.io.tmpdir")+"/"+informePdf.getName());
            return byteArchivo;
        } catch (Exception e) {
        	LOG.error(JsfUtil.ERROR_GUARDAR_REGISTRO, e);
        }
		return null;
	}
	
	public String nombreUsuario(Usuario usuario){
		if(usuario.getPersona()!=null){
			try {
				if (organizacionFacade.tieneOrganizacionPorPersona(usuario.getPersona())) {
					Organizacion organizacion = organizacionFacade.buscarPorPersona(usuario.getPersona(),
							usuario.getNombre());
					if (organizacion != null && organizacion.getNombre() != null
							&& organizacion.getRuc().trim().equals(usuario.getNombre())) {				
						return organizacion.getNombre();						
					}
				}
			} catch (ServiceException e) {
				LOG.error("Error al obtener la organización de la persona.", e);
			}	
		}
		if (usuario.getPersona() != null && !usuario.getPersona().getNombre().trim().isEmpty()) {
			return usuario.getPersona().getNombre().trim();
		}
		return "";
	}	
	
	public boolean isValid(String json) {
	    try {
	        new JSONObject(json);
	    } catch (JSONException e) {
	        return false;
	    }
	    return true;	    
	}	
	
	public boolean isValidArray(String json) {
	    try {
	        JSONArray jsonarreglo = new JSONArray(json);	        
	    } catch (JSONException e) {
	        return false;
	    }
	    return true;	    
	}		
	
	public void SerializarJSON(String json) {
	    try {
	    		JSONObject JsonAux = new JSONObject(json);
            	Iterator<?> keysAux = JsonAux.keys();
            	while (keysAux.hasNext()) {
                    String keyA = (String) keysAux.next();
                    String valueA = JsonAux.getString(keyA);	
                    elementosJSON.put(keyA, valueA);
                    Boolean esArreglo = false;
                    esArreglo = isValidArray(valueA);
                    if (esArreglo) {
                    	SerializarArrayJSON(keyA,valueA);
                    }
        }	                        		
	    } catch (JSONException e) {
	    	e.printStackTrace();
	    }
	}	
	
	public void SerializarArrayJSON(String llave, String json) {
		try {
			JSONArray jsonarreglo = new JSONArray(json);
			if (jsonarreglo.length() <= 0) {
				elementosJSON.remove(llave);
			} else {
				for (int i = 0; i < jsonarreglo.length(); i++) {
					JSONObject object = jsonarreglo.getJSONObject(i);
					String dato = object.toString();
					Boolean valor = isValid(dato);
					if (valor) {
						SerializarJSON(dato);
						elementosJSON.remove(llave);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void leerJSONRespuesta(String JSONRespuesta) {
		JSONObject objetoJson;
		try {
			objetoJson = new JSONObject(JSONRespuesta);
			elementosJSON = new HashMap<String, String>();
			try {
				Iterator<?> keys = objetoJson.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					String value = objetoJson.getString(key);
					elementosJSON.put(key, value);
				}
			} catch (Exception xx) {
				xx.toString();
			}
			/////Recorrer Hashmap de elementos para seguir listando cada elemento del JSON, se seguira ejecutando mientras encuentre un string 
			/////JSON en alguna posición del hashmap.
			Boolean isValido = true;
			Boolean isValidoArreglo = true;
			while (isValido) {
				for (Entry<String, String> entry : elementosJSON.entrySet()) {
					String clave = entry.getKey();
					String valor = entry.getValue();
					isValido = isValid(valor);
					if (isValido) {
						SerializarJSON(valor);
						elementosJSON.remove(clave);
						break;
					} else {
						isValidoArreglo = isValidArray(valor);
						if (isValidoArreglo) {
							SerializarArrayJSON(clave, valor);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
    private Usuario asignarTecnicoFinanciero(ProyectoLicenciaCoa proyecto) {
    	Area areaTramite = proyecto.getAreaResponsable();
    	if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT)){
    		areaTramite = areaTramite.getArea();
    	} else if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)){
    		areaTramite = areaFacade.getAreaSiglas(Constantes.SIGLAS_DIRECCION_FINANCIERA);
    	}
    	
    	String rolPrefijo;
		String rolTecnico;
		rolPrefijo = "role.resolucion.tecnico.financiero";
    	rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
    	List<Usuario> listaUsuariosCargaLaboral = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());

        if (listaUsuariosCargaLaboral == null || listaUsuariosCargaLaboral.isEmpty()){
            LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
            return null;
        } 

        Usuario tecnicoResponsable = null;
        
        // recuperar tecnico de bpm y validar si el usuario existe en el listado anterior       
        Usuario usuarioTecnico = usuarioFacade.buscarUsuario(tecnicoFinanciero);
        if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
            if (listaUsuariosCargaLaboral != null && listaUsuariosCargaLaboral.size() >= 0
                    && listaUsuariosCargaLaboral.contains(usuarioTecnico)) {
                tecnicoResponsable = usuarioTecnico;
            }
        }
        
        // si no se encontró el usuario se realiza la busqueda de uno nuevo y se actualiza en el bpm
        if (tecnicoResponsable == null) {
            tecnicoResponsable = listaUsuariosCargaLaboral.get(0);
        }

        return tecnicoResponsable;
    }
}
