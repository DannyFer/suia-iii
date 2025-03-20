package ec.gob.ambiente.suia.recaudaciones.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.dto.EntidadPagoNUTRcoa;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.FacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.model.PagoKushkiJson;
import ec.gob.ambiente.prevencion.certificado.ambiental.controllers.PagoCertificadoController;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.CodigoTasa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.bean.PagoRcoaBean;
import ec.gob.ambiente.suia.recaudaciones.facade.DocumentosNUTFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasFacade;
import ec.gob.ambiente.suia.recaudaciones.model.Cuentas;
import ec.gob.ambiente.suia.recaudaciones.model.DocumentoNUT;
import ec.gob.ambiente.suia.recaudaciones.model.EstadosNut;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.SolicitudUsuario;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.recaudaciones.model.TarifasNUT;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.ambiente.suia.recaudaciones.controllers.PagoKushkiController;


@ManagedBean
@ViewScoped
public class GenerarNUTController {
	
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenerarNUTController.class);

	@Getter
	@Setter
	private List<Tarifas>listTarifas= new ArrayList<Tarifas>();
	
	@EJB
	private TarifasFacade tarifasFacade;
	
	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
	
	@Getter
	@Setter
	private Tarifas tarifas = new Tarifas();
	
	@Getter
	@Setter
	private NumeroUnicoTransaccional numeroUnicoTransaccional= new NumeroUnicoTransaccional();
	
	@Getter
	@Setter
	private SolicitudUsuario solicitudUsuario;
	
	@EJB
	private InformeProvincialGADFacade informeProvincialGADFacade;
	
	@EJB
	private ContactoFacade contactoFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private DocumentosNUTFacade documentosNUTFacade;
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	@EJB
	private FacilitadorPPCFacade facilitadorPPCFacade;
	@EJB
	public CatalogoCoaFacade catalogoCoaFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
    
	@Getter
	@Setter
	@ManagedProperty(value = "#{pagoRcoaBean}")
	private PagoRcoaBean pagoRcoaBean;
    
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	private TarifasNUT tarifasNUT = new TarifasNUT();
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@Getter
	@Setter
	private String nombreUsuarioSolicitud;
	
	@Getter
	@Setter
	private List<TarifasNUT>listTarifasNut= new ArrayList<TarifasNUT>();
	
	@EJB
    private SecuenciasFacade secuenciasFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	@Getter
	@Setter
	private List<String> listPathArchivos= new ArrayList<String>();
	private boolean nuevoComprobante, nutGenerado;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{pagoKushkiController}")
    private PagoKushkiController pagoKushkiController;
    
	@Getter
	@Setter
	private Map<String, Object> variables;	
	
	@Getter
	@Setter
	private String operador,tramite,usuarioAmbiental;	
	@Getter
	@Setter
	private Boolean rgd = false;	
	@Getter
	@Setter
	private Boolean esFisico = false;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init(){
		try {
			if (bandejaTareasBean.getTarea() != null) {		
				variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());				
				if (variables.get("operador") != null)
				{
					operador = (String) variables.get("operador");
				}
				if (variables.get("tramite") != null)
				{
					tramite = (String) variables.get("tramite");
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
			pagoRcoaBean.setInstitucionesFinancierasNut(institucionFinancieraFacade.obtenerInstitucionesNut());
			pagoRcoaBean.setGenerarNUT(true);
			pagoRcoaBean.setCumpleMontoProyecto(false);
			pagoRcoaBean.setCalcularValorPagar(true);
			pagoRcoaBean.setIdentificadorMotivo(null);
			pagoRcoaBean.setTransaccionFinanciera(new TransaccionFinanciera());
			pagoRcoaBean.setTransaccionFinancieraCobertura(new TransaccionFinanciera());
			pagoRcoaBean.setTransaccionesFinancieras(new ArrayList<TransaccionFinanciera>());
			pagoRcoaBean.setInstitucionFinancieraNut(new InstitucionFinanciera());
			SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date fechaCambio = formatoFecha.parse(Constantes.getFechaPagosConNut());
			boolean mostrarVentana = true;
			FacesContext ctx = FacesContext.getCurrentInstance();
	        String urls = ctx.getViewRoot().getViewId();
	        if(urls.contains("pagoRegistroAmbientalCoa")  // si es registro ambiental
	        		|| urls.contains("operadorCostoProyecto") // si es licencia
	        	){
				// busco la institucion financiera si es un ente acreditado
				if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getAreaResponsable().getTipoEnteAcreditado() != null) {
					if (!proyectosBean.getProyectoRcoa().getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
						mostrarVentana=false;
					}
				}
	        }
			if(mostrarVentana && (bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("pago") || bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("valor"))){
				Date fechaTarea = formatoFecha.parse(bandejaTareasBean.getTarea().getActivationDate());
				if(fechaTarea.before(fechaCambio)){
			    	RequestContext.getCurrentInstance().execute("PF('dlgAviso').show();");
				}
			}
		}catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
			LOG.error("Error al obtener la organización de la persona.", e);
		}
	}
	
	public void cargarTransacciones(){
		pagoRcoaBean.setTransaccionesFinancieras(transaccionFinancieraFacade.cargarTransaccionesRcoa(proyectosBean.getProyectoRcoa()
						.getId(), bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getTaskId()));
	}
	
	public String generarCodigoSolicitud() {
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
	
	public void generarDocumentoNutPago(NumeroUnicoTransaccional numeroUnicoTransaccional, SolicitudUsuario solicitudUsuario, int numeroDocumento){
		FileOutputStream file;
		try {
			NumeroUnicoTransaccional unicoTransaccional= new NumeroUnicoTransaccional();
			unicoTransaccional.setCodigoNutReporte(numeroUnicoTransaccional.getNutCodigo());
			unicoTransaccional.setNombreProponenteReporte(nombreUsuario(solicitudUsuario.getUsuario()));
			unicoTransaccional.setCodigoSolicitudReporte(solicitudUsuario.getSolicitudCodigo());
			unicoTransaccional.setSolicitudReporte(solicitudUsuario.getSolicitudDescripcion());
			
			Cuentas cuentas= new Cuentas();
			cuentas=numeroUnicoTransaccional.getCuentas();
			
			unicoTransaccional.setCuentaApagarReporte(cuentas.getCuentaNumero());
			unicoTransaccional.setTotalNutReporte(numeroUnicoTransaccional.getNutValor().toString());
			
			String nombreReporte="";
			nombreReporte= "Recaudaciones-"+numeroUnicoTransaccional.getNutCodigo();
			
			PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS.getIdTipoDocumento());
			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,unicoTransaccional,null);
            Path path = Paths.get(informePdf.getAbsolutePath());
            byte[] byteArchivo = Files.readAllBytes(path);
            File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(informePdf.getName()));
            file = new FileOutputStream(fileArchivo);
            file.write(byteArchivo);
            file.close();
            listPathArchivos.add(System.getProperty("java.io.tmpdir")+"/"+informePdf.getName());
            
            Documento documentoPago = new Documento();
    		JsfUtil.uploadApdoDocument(informePdf, documentoPago);						
    		documentoPago.setMime("application/pdf");
    		documentoPago.setIdTable(solicitudUsuario.getId());

			documentoPago.setNombreTabla("NUT RECAUDACIONES");
			documentoPago.setNombre("Orden de pago"+ numeroDocumento +".pdf");
			documentoPago.setExtesion(".pdf");
			documentosFacade.guardarDocumentoAlfrescoSinProyecto(numeroUnicoTransaccional.getNutCodigoProyecto()!=null ? numeroUnicoTransaccional.getNutCodigoProyecto() :solicitudUsuario.getSolicitudCodigo(), "RECAUDACIONES", bandejaTareasBean.getTarea().getProcessInstanceId(), documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS,null);
           
        } catch (Exception e) {
        	LOG.error(JsfUtil.ERROR_GUARDAR_REGISTRO, e);
        }
	}
	
	public  byte[] generarDocumentoNutRcoa(NumeroUnicoTransaccional numeroUnicoTransaccional, SolicitudUsuario solicitudUsuario, int numeroDocumento){
		FileOutputStream file;
		try {
			NumeroUnicoTransaccional unicoTransaccional= new NumeroUnicoTransaccional();
			unicoTransaccional.setCodigoNutReporte(numeroUnicoTransaccional.getNutCodigo());
			unicoTransaccional.setNombreProponenteReporte(nombreUsuario(solicitudUsuario.getUsuario()));
			unicoTransaccional.setCodigoSolicitudReporte(solicitudUsuario.getSolicitudCodigo());
			unicoTransaccional.setTasaDescripcion(solicitudUsuario.getSolicitudDescripcion());
			unicoTransaccional.setSolicitudReporte("Pago por el trámite: "+numeroUnicoTransaccional.getNutCodigoProyecto());
			
			Cuentas cuentas= new Cuentas();
			cuentas=numeroUnicoTransaccional.getCuentas();
			
			unicoTransaccional.setCuentaApagarReporte(cuentas.getCuentaNumero());
			unicoTransaccional.setTotalNutReporte(numeroUnicoTransaccional.getNutValor().toString());
			
			String nombreReporte="";
			nombreReporte= "Recaudaciones-"+numeroUnicoTransaccional.getNutCodigo();
			
			PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS.getIdTipoDocumento());
			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,unicoTransaccional,null);
            Path path = Paths.get(informePdf.getAbsolutePath());
            byte[] byteArchivo = Files.readAllBytes(path);
            File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(informePdf.getName()));
            file = new FileOutputStream(fileArchivo);
            file.write(byteArchivo);
            file.close();
            listPathArchivos.add(System.getProperty("java.io.tmpdir")+"/"+informePdf.getName());
            
            return byteArchivo;
            
        } catch (Exception e) {
        	LOG.error(JsfUtil.ERROR_GUARDAR_REGISTRO, e);
        }
		return null;
	}
	
	public  byte[] generarDocumentoVariosNut(SolicitudUsuario solicitudUsuario) throws ServiceException{
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
		
		if(loginBean.getUsuario().getNombre().length() == 10){
			ciudad = loginBean.getUsuario().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + ", ";
		}else{
			Organizacion organizacion = organizacionFacade.buscarPorRuc(loginBean.getUsuario().getNombre());
			
			if(organizacion != null && organizacion.getIdUbicacionGeografica() != null){
				UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(organizacion.getIdUbicacionGeografica());
				if(ubicacion != null){
					ciudad = ubicacion.getUbicacionesGeografica().getNombre() + ", ";
				}
			}else{
				ciudad = loginBean.getUsuario().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + ", ";
			}
		}	
		
//		if(loginBean.getUsuario().getPersona().getUbicacionesGeografica() != null && loginBean.getUsuario().getPersona().getUbicacionesGeografica().getUbicacionesGeografica() != null)
//			ciudad = loginBean.getUsuario().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + ", ";
		
		unicoTransaccional.setFechaEmision(ciudad + JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
		try {
			//busco los nbut generados por solicitud
			List<NumeroUnicoTransaccional> listaNuts = numeroUnicoTransaccionalFacade.listBuscaNUTPorSolicitud(solicitudUsuario.getSolicitudCodigo());
			for (NumeroUnicoTransaccional objNut : listaNuts) {
				unicoTransaccional.setSolicitudReporte("trámite: "+objNut.getNutCodigoProyecto());
				html += "<br/>"+"<strong>N&uacute;mero de Pago: </strong>"+objNut.getNutCodigo()+"<br />";
				html += "<strong>Valor de Pago: </strong>"+objNut.getNutValor().toString()+"<br />";
				html += "<strong>Fecha de caducidad orden: </strong>"+objNut.getNutFechaDesactivacion().toString().substring(0, 16)+"<br />";
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

	public boolean enviarNotificacionPago(Usuario usuario, String codigoProyecto, List<String> listPathArchivo) {		
		try {
			List<Contacto> contactos = contactoFacade.buscarUsuarioNativeQuery(usuario.getNombre());
			
			if(contactos.size()==0){
				contactos = contactoFacade.buscarUsuarioNativeQuery(usuario.getNombre());
			}
			
			String email= null;
			String nombreProponente=null;
			
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)){
					email=contacto.getValor();
					nombreProponente=contacto.getOrganizacion()!=null?contacto.getOrganizacion().getNombre():contacto.getPersona().getNombre();
					
					if(nombreProponente!=null)
					break;
				}
			}
			NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			mail_a.sendEmailNotificacionPago(email, "NOTIFICACIÓN PARA PAGO", "Este correo fue enviado usando JavaMail", nombreProponente, codigoProyecto, listPathArchivo, usuario, loginBean.getUsuario());
			Thread.sleep(2000);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
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
	
	private Integer contarDiasSemana(){
		Integer Valor_dia = 3;
		Date fecha = null;
		for (int i = 0; i < 4; i++) {
			fecha = JsfUtil.sumarDiasAFecha(new Date(), i);
			GregorianCalendar fechaCalendario = new GregorianCalendar();
			fechaCalendario.setTime(fecha);
			int diaSemana = fechaCalendario.get(Calendar.DAY_OF_WEEK);
			if (diaSemana == 1) {
				Valor_dia=4;
				break;
			}
		}
		return Valor_dia;
	}
	
	public EntidadPagoNUTRcoa generarNUTPago(String tramite, Double montoTotalProyecto, Map<Double, String> listaMontoProyecto, Integer cuenta, String mensaje) throws Exception{
		EntidadPagoNUTRcoa entidadPagoNUTRcoa = new EntidadPagoNUTRcoa();
		List<NumeroUnicoTransaccional> listNUTXTramite=new ArrayList<NumeroUnicoTransaccional>();
		for (Entry<Double, String> valorPago : listaMontoProyecto.entrySet()) {
			Tarifas tarifa = tarifasFacade.buscarTarifasPorCodigo(valorPago.getValue());
			if(tarifa == null) {
				entidadPagoNUTRcoa.setCorrecto(false);
				entidadPagoNUTRcoa.setMensaje("Ocurrió un error al generar el NUT. Por favor comunicarse con mesa de ayuda.");
				return entidadPagoNUTRcoa;
			}
		}
		Integer diasHabilitados = contarDiasSemana();
		SolicitudUsuario solicitudUsuario= new SolicitudUsuario();
		solicitudUsuario.setUsuario(loginBean.getUsuario());
		solicitudUsuario.setInstitucionFinanciera(pagoRcoaBean.getInstitucionFinancieraNut());
		solicitudUsuario.setValorTotal(montoTotalProyecto);
		String codigoTramite=tramite;
		listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramitePorTareaPorBancoPorCuenta(codigoTramite, bandejaTareasBean.getTarea().getProcessInstanceId(), pagoRcoaBean.getInstitucionFinancieraNut().getId(), cuenta);
		if(listNUTXTramite!=null && listNUTXTramite.size()>0 && !nuevoComprobante){
			for (NumeroUnicoTransaccional nut : listNUTXTramite) {
					if(nut.getEstadosNut().getId()==5 && pagoRcoaBean.getInstitucionFinancieraNut().equals(nut.getSolicitudUsuario().getInstitucionFinanciera())){
						nut.setNutFechaActivacion(new Date());
						nut.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), diasHabilitados));
						nut.setEstadosNut(new EstadosNut(2));
						crudServiceBean.saveOrUpdate(nut);
						entidadPagoNUTRcoa.setSolicitudUsuario(nut.getSolicitudUsuario());
						entidadPagoNUTRcoa.setNumeroUnicoTransaccional(nut);
						entidadPagoNUTRcoa.setMensaje("Su trámite está caducado, se activo nuevamente por 3 días, por favor revisar.");
					}
					entidadPagoNUTRcoa.setCorrecto(false);
					if(entidadPagoNUTRcoa.getMensaje() == null || entidadPagoNUTRcoa.getMensaje().isEmpty())
						entidadPagoNUTRcoa.setMensaje("Usted ya tiene generado un Número Único de Trámite, por favor descargue en documentos del trámite.");
			}
			if(!entidadPagoNUTRcoa.isCorrecto()){
				// busco si tiene generados  nuts activos o caducados en otro banco
				deshabilitarNutcaducados(codigoTramite);
				nuevoComprobante=false;
				return entidadPagoNUTRcoa;
			}
		}else{
			// busco si tiene generados  nuts activos o caducados en otro banco
			deshabilitarNutcaducados(codigoTramite);
			nuevoComprobante=true;
		}
		
		solicitudUsuario.setSolicitudDescripcion(mensaje);
		solicitudUsuario.setSolicitudCodigo(generarCodigoSolicitud());
		crudServiceBean.saveOrUpdate(solicitudUsuario);
		setSolicitudUsuario(solicitudUsuario);
		//para crear varios nut para el pago
		for (Entry<Double, String> valorPago : listaMontoProyecto.entrySet()) {
			Tarifas tarifa = tarifasFacade.buscarTarifasPorCodigo(valorPago.getValue());
			String mensajeSinValores="";
			if(valorPago.getValue().equals(CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion()))
				mensajeSinValores = "correspondiente a RGDP.";
			else if(valorPago.getValue().equals(CodigoTasa.REGISTRO_SUSTANCIAS_QUIMICAS.getDescripcion()))
				mensajeSinValores = "correspondiente a pago de sustancias químicas.";
			else if(valorPago.getValue().equals(CodigoTasa.INVENTARIO_FORESTAL_MADERA_PIE.getDescripcion()))
				mensajeSinValores = "correspondiente a remoción de cobertura vegetal nativa.";
			else if(valorPago.getValue().equals(CodigoTasa.REGISTRO_AMBIENTAL.getDescripcion()))
				mensajeSinValores = "correspondiente a Registro Ambiental";
			else if(valorPago.getValue().equals(CodigoTasa.FACILIATDOR.getDescripcion()))
				mensajeSinValores = "correspondiente a la tasa por servicios de facilitación.";
			else if(valorPago.getValue().equals(CodigoTasa.EMISION_LICENCIA_PAGO_MINIMO_BAJO_IMPACTO.getDescripcion()))
				mensajeSinValores = "correspondiente a la tasa por costo del proyecto";
			else if(valorPago.getValue().equals(CodigoTasa.EMISION_LICENCIA_PAGO_MINIMO_ALTO_IMPACTO.getDescripcion()))
				mensajeSinValores = "correspondiente a la tasa por costo del proyecto";
			
			numeroUnicoTransaccional = new NumeroUnicoTransaccional();
			numeroUnicoTransaccional.setNutCodigo(secuenciasFacade.getNextValueDedicateSequence("NUT_CODIGO", 10));
			numeroUnicoTransaccional.setNutFechaActivacion(new Date());
			numeroUnicoTransaccional.setEstadosNut(new EstadosNut(2));
			numeroUnicoTransaccional.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), diasHabilitados));
			numeroUnicoTransaccional.setSolicitudUsuario(solicitudUsuario);
			numeroUnicoTransaccional.setCuentas(new Cuentas(cuenta));
			numeroUnicoTransaccional.setNutValor(valorPago.getKey());
			numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
			numeroUnicoTransaccional.setNutDescripcion(mensajeSinValores);
			crudServiceBean.saveOrUpdate(numeroUnicoTransaccional);

			TarifasNUT tarifasNUT= new TarifasNUT();
			tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
			tarifasNUT.setTarifas(tarifa);
			tarifasNUT.setCantidad(1);
			tarifasNUT.setValorUnitario(valorPago.getKey());
			crudServiceBean.saveOrUpdate(tarifasNUT);
		}
		// fin
		byte[] contenidoDocumento = generarDocumentoVariosNut(solicitudUsuario);

		entidadPagoNUTRcoa.setSolicitudUsuario(solicitudUsuario);
		entidadPagoNUTRcoa.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
		entidadPagoNUTRcoa.setArchivo(contenidoDocumento);
		entidadPagoNUTRcoa.setCorrecto(true);
		return entidadPagoNUTRcoa;
	}
	
	private void deshabilitarNutcaducados(String codigoTramite){
		List<NumeroUnicoTransaccional> listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramitePorTarea(codigoTramite, bandejaTareasBean.getTarea().getProcessInstanceId());
		if(listNUTXTramite!=null && listNUTXTramite.size()>0 && !nuevoComprobante){
			for (NumeroUnicoTransaccional nut : listNUTXTramite) {
					if(nut.getEstadosNut().getId()==5 && !pagoRcoaBean.getInstitucionFinancieraNut().equals(nut.getSolicitudUsuario().getInstitucionFinanciera())){
						nut.setEstado(false);
						nut.setDescripcionNuts(nut.getDescripcionNuts()+" - deshabilitado porque se genero otro nut en otro banco");
						crudServiceBean.saveOrUpdate(nut);
						nut.getSolicitudUsuario().setEstado(false);
						crudServiceBean.saveOrUpdate(nut.getSolicitudUsuario());
					}
			}
		}
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

	public void eliminarTransacion(TransaccionFinanciera transaccion) throws Exception {
		pagoRcoaBean.getTransaccionesFinancieras().remove(transaccion);
		pagoRcoaBean.setCumpleMontoProyecto(cumpleMonto());
	}
    
    public void eliminarTransacionGuardada(TransaccionFinanciera item) {
    	pagoRcoaBean.getTransaccionesFinancieras().remove(item);
    	pagoRcoaBean.setCumpleMontoProyecto(cumpleMonto());
    	List<TransaccionFinanciera> transaccionesFinancierasEliminar = new ArrayList<TransaccionFinanciera>();
		if(item.getId() != null) {
			transaccionesFinancierasEliminar.add(item);
			transaccionFinancieraFacade.eliminarTransacciones(transaccionesFinancierasEliminar);
			for (TransaccionFinanciera transaccion : transaccionesFinancierasEliminar) {
				transaccionFinancieraFacade.revertirPago(pagoRcoaBean.getTramite(),
						transaccion.getNumeroTransaccion(), JsfUtil.getSenderIp(), 
						transaccion.getInstitucionFinanciera().getCodigoInstitucion(),
						transaccion.getMontoPago());
			}
			transaccionesFinancierasEliminar = new ArrayList<>();
		}
    }
	
	public boolean cumpleMonto() {
		double montoTotal = 0, montoTotalRegistro=0, montoTotalcobertura=0;
		for (TransaccionFinanciera transa : pagoRcoaBean.getTransaccionesFinancieras()) {
			/*if((transa.getTipoPago() == null || transa.getTipoPago() == 1) || (transa.getTipoPagoProyecto() == null || transa.getTipoPagoProyecto().equals("Proyecto"))){
				montoTotal += transa.getMontoTransaccion();
				montoTotalRegistro += transa.getMontoTransaccion();
			}else if(transa.getTipoPago() == null || transa.getTipoPago() == 2){
				montoTotal += transa.getMontoTransaccion();
				montoTotalcobertura += transa.getMontoTransaccion();
			}*/
			if(transa.getTipoPago() != null){
				if(transa.getTipoPago() == 1){
					montoTotal += transa.getMontoTransaccion();
					montoTotalRegistro += transa.getMontoTransaccion();
				}else{
					montoTotal += transa.getMontoTransaccion();
					montoTotalcobertura += transa.getMontoTransaccion();
				}
			}else{
				if(transa.getTipoPagoProyecto() == null || transa.getTipoPagoProyecto().equals("Proyecto")){
					montoTotal += transa.getMontoTransaccion();
					montoTotalRegistro += transa.getMontoTransaccion();
				}else{
					montoTotal += transa.getMontoTransaccion();
					montoTotalcobertura += transa.getMontoTransaccion();
				}
			}
		}
		// validacion solo para registro ambiental para saber si pago en linea solo para cuando es un ente acreditado y tiene pago de cobertura vegetal
		if(pagoRcoaBean.getTipoProyecto().equals("REGISTRO") && pagoRcoaBean.isEsEnteAcreditado() && pagoRcoaBean.isMostrarPnlCobertura() && montoTotalcobertura == 0){
			List<NumeroUnicoTransaccional> listaNutsTramite = numeroUnicoTransaccionalFacade.listNUTPagadoPorTramitePorTarea(pagoRcoaBean.getTramite(), bandejaTareasBean.getTarea().getProcessInstanceId());
			if(listaNutsTramite != null && listaNutsTramite.size() > 0){
				for (NumeroUnicoTransaccional objNUT : listaNutsTramite) {
					//verifico si rl nut pagado es con kushku
					if(objNUT.getSolicitudUsuario().getInstitucionFinanciera().getCodigoInstitucion().equals("KUSHKI")){
						montoTotalcobertura = objNUT.getNutValor();
					}
				}
			}
		}
		//fin avlidacion
		DecimalFormat decimalValorTramite= new DecimalFormat(".##");
		String x="";
		x = decimalValorTramite.format(pagoRcoaBean.getValorAPagar()).replace(",",".");
		pagoRcoaBean.setValorAPagar(Double.valueOf(x));
		pagoRcoaBean.setCumpleValorRegistro(false);
		pagoRcoaBean.setCumpleMontoCobertura(false);
		if(montoTotalRegistro >= pagoRcoaBean.getMontoTotalProyecto()){
			pagoRcoaBean.setCumpleValorRegistro(true);
		}
		if(montoTotalcobertura >= pagoRcoaBean.getMontoTotalCoberturaVegetal()){
			pagoRcoaBean.setCumpleMontoCobertura(true);
		}
		if(pagoRcoaBean.isMostrarPnlCobertura()){
			return pagoRcoaBean.isCumpleValorRegistro() && pagoRcoaBean.isCumpleMontoCobertura();
		}
		if (montoTotal >= pagoRcoaBean.getValorAPagar()) {
			return true;
		}
		return false;
	}

	public void guardarTransaccion(Integer pagoProyecto) {
		if(pagoProyecto==2){
			pagoRcoaBean.setTransaccionFinanciera(new TransaccionFinanciera());
			pagoRcoaBean.setTransaccionFinanciera(pagoRcoaBean.getTransaccionFinancieraCobertura());
		}else {
			pagoRcoaBean.setTransaccionFinancieraCobertura(new TransaccionFinanciera());
		}
		if (pagoRcoaBean.getTransaccionFinanciera().getInstitucionFinanciera() != null
				&& pagoRcoaBean.getTransaccionFinanciera().getNumeroTransaccion() != "") {
			double monto = 0.0;
			try {
				//recaudaciones
				String codigoTramite = "";
				if (proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null) {
					codigoTramite = proyectosBean.getProyecto().getCodigo();
				} else if (proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() != null) {
					codigoTramite = proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental();
				} else {
					codigoTramite = bandejaTareasBean.getTarea().getProcedure();
				}
				if(!pagoRcoaBean.isEsEnteAcreditado() || pagoProyecto==2){
					List<NumeroUnicoTransaccional> listaNutsTramite = numeroUnicoTransaccionalFacade.listNUTActivoPorTramitePorTarea(codigoTramite, bandejaTareasBean.getTarea().getProcessInstanceId());
					if (listaNutsTramite != null && listaNutsTramite.size() > 0) {
						// verificacion de NUT en estado pagado 
						boolean existeNut=false;
						for (NumeroUnicoTransaccional nut : listaNutsTramite) {
							if(pagoRcoaBean.isMostrarPnlCobertura()){
								if(nut.getCuentas().getId().equals(pagoProyecto == 1 ? 1: 4)){
									if (!nut.getEstadosNut().getId().equals(3)) {
										JsfUtil.addMessageWarning("El número NUT " + nut.getNutCodigo() + " aún no ha sido pagado");
										return;
									}
								//verificación de que el #comprobante ingresado corresponda con el numero de referencia de pago del NUT
								if (nut.getBnfTramitNumber().equals(pagoRcoaBean.getTransaccionFinanciera().getNumeroTransaccion())) {
										existeNut=true;
										monto = nut.getNutValor();
										break;
									}
								}
				}else{
					if (!nut.getEstadosNut().getId().equals(3)) {
						JsfUtil.addMessageWarning("El número NUT " + nut.getNutCodigo() + " aún no ha sido pagado");
						return;
					}

								//verificación de que el #comprobante ingresado corresponda con el numero de referencia de pago del NUT
								if (nut.getBnfTramitNumber().equals(pagoRcoaBean.getTransaccionFinanciera().getNumeroTransaccion())) {
									existeNut=true;
									monto = nut.getNutValor();
									break;
								}
							}
						}
						if(!existeNut){
							JsfUtil.addMessageWarning("El número de comprobante " + pagoRcoaBean.getTransaccionFinanciera().getNumeroTransaccion() + " no se relaciona con el NUT generado");
							reiniciarTransaccion();
							return;
						}
					}
					//fin recaudaciones
				}
				
				if (existeTransaccion(pagoRcoaBean.getTransaccionFinanciera())) {
					JsfUtil.addMessageInfo("El número de comprobante: " + pagoRcoaBean.getTransaccionFinanciera().getNumeroTransaccion()
							+ " (" + pagoRcoaBean.getTransaccionFinanciera().getInstitucionFinanciera().getNombreInstitucion()
							+ ") ya fue registrado por usted. Ingrese otro distinto.");
					reiniciarTransaccion();
					return;
				} else {
					// verifico si ya fue pagado con NUT
					if(monto == 0){
						monto = transaccionFinancieraFacade.consultarSaldo(pagoRcoaBean.getTransaccionFinanciera()
								.getInstitucionFinanciera().getCodigoInstitucion(), pagoRcoaBean.getTransaccionFinanciera().getNumeroTransaccion());
					}
					if (monto == 0) {
						JsfUtil.addMessageError("El número de comprobante: "
								+ pagoRcoaBean.getTransaccionFinanciera().getNumeroTransaccion() + " ("
								+ pagoRcoaBean.getTransaccionFinanciera().getInstitucionFinanciera().getNombreInstitucion()
								+ ") no ha sido registrado.");
						reiniciarTransaccion();
						return;
					} else {
						if(pagoRcoaBean.getTipoProyecto().equals("LICENCIA")){
							pagoRcoaBean.getTransaccionFinanciera().setTipoPago(pagoProyecto);
						}
						double montoPago = 0.0;
						switch (pagoProyecto) {
						case 1:
							montoPago = pagoRcoaBean.getMontoTotalProyecto();
							break;
						case 2:
							montoPago =  pagoRcoaBean.getMontoTotalCoberturaVegetal();
							break;
						case 3:
							montoPago =  pagoRcoaBean.getMontoTotalRGD();
							break;
						default:
							break;
						}
						
						Double montoPagado = getValorPagadoPorTipoPago(pagoRcoaBean.getTransaccionFinanciera());
						montoPago = montoPago - montoPagado; // resto el valor total del pago - el valor ya cancelado 
						
						if(montoPago > monto)
							montoPago = monto;
						
						pagoRcoaBean.getTransaccionFinanciera().setMontoTransaccion(monto); //montoTransaccion es el valor total del número de comprobante
						pagoRcoaBean.getTransaccionFinanciera().setMontoPago(montoPago); //montoPago es el valor cancelado o valor cubierto con el valor del comprobante (el valor del comprobante puede ser mayor al valor requerido)
						pagoRcoaBean.getTransaccionFinanciera().setTipoPagoProyecto((pagoProyecto==1)?"Proyecto":"Cobertura");
						if (pagoRcoaBean.getIdentificadorMotivo() == null) {
							if(proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null){
								pagoRcoaBean.getTransaccionFinanciera().setProyecto(proyectosBean.getProyecto());
							}else if(proyectosBean.getProyectoRcoa() != null){
								pagoRcoaBean.getTransaccionFinanciera().setProyectoRcoa(proyectosBean.getProyectoRcoa());
							}
							pagoRcoaBean.getTransaccionFinanciera().setIdentificadorMotivo(null);
						} else {
							pagoRcoaBean.getTransaccionFinanciera().setProyecto(null);
							pagoRcoaBean.getTransaccionFinanciera().setIdentificadorMotivo(pagoRcoaBean.getIdentificadorMotivo());
						}
						pagoRcoaBean.getTransaccionesFinancieras().add(pagoRcoaBean.getTransaccionFinanciera());
						pagoRcoaBean.setCumpleMontoProyecto(cumpleMonto());
						reiniciarTransaccion();
						return;
					}
				}
			} catch (Exception e) {
				JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
				return;
			}
		} else {
			JsfUtil.addMessageWarning("Debe ingresar datos correctos para la transacción");
		}
	}

	private void reiniciarTransaccion() {
		pagoRcoaBean.setTransaccionFinanciera(new TransaccionFinanciera());
		if(pagoRcoaBean.getInstitucionesFinancieras() != null && pagoRcoaBean.getInstitucionesFinancieras().size() > 0)
			pagoRcoaBean.getTransaccionFinanciera().setInstitucionFinanciera(pagoRcoaBean.getInstitucionesFinancieras().get(0));
		pagoRcoaBean.setTransaccionFinancieraCobertura(new TransaccionFinanciera());
		if(pagoRcoaBean.getInstitucionesFinancierasCobertura() != null && pagoRcoaBean.getInstitucionesFinancierasCobertura().size() > 0)
			pagoRcoaBean.getTransaccionFinancieraCobertura().setInstitucionFinanciera(pagoRcoaBean.getInstitucionesFinancierasCobertura().get(0));
	}
	
	private boolean existeTransaccion(TransaccionFinanciera _transaccionFinanciera) {
		switch (pagoRcoaBean.getTipoProyecto()) {
		case "LICENCIA":
			for (TransaccionFinanciera transaccion : pagoRcoaBean.getTransaccionesFinancieras()) {
				if (transaccion.getTipoPago().equals(_transaccionFinanciera.getTipoPago())
						&& transaccion.getNumeroTransaccion().trim().equals(_transaccionFinanciera.getNumeroTransaccion())
						&& transaccion.getInstitucionFinanciera().getCodigoInstitucion().trim()
								.equals(_transaccionFinanciera.getInstitucionFinanciera().getCodigoInstitucion())) {
					return true;
				}
			}
			break;
		default:
			for (TransaccionFinanciera transaccion : pagoRcoaBean.getTransaccionesFinancieras()) {
				if (transaccion.getNumeroTransaccion().trim().equals(_transaccionFinanciera.getNumeroTransaccion())
						&& transaccion.getInstitucionFinanciera().getCodigoInstitucion().trim()
								.equals(_transaccionFinanciera.getInstitucionFinanciera().getCodigoInstitucion())) {
					return true;
				}
			}
			break;
		}
		return false;
	}

	
	public void generarNUT() throws Exception{
		nuevoComprobante=false;
		if(nutGenerado)
			return;
		//valido que se haya seleccionado el banco
		if(pagoRcoaBean.getInstitucionFinancieraNut() == null || pagoRcoaBean.getInstitucionFinancieraNut().getId() == null ){
			JsfUtil.addMessageError("El campo Banco es requerido.");
			return;
		}
		// si es un ente acreditado no genero NUT para el pago del Registro ambiental solo para cobertura vegetal
		if(pagoRcoaBean.isEsEnteAcreditado()){
			if(pagoRcoaBean.isMostrarPnlCobertura() && (pagoRcoaBean.getMontoTotalCoberturaVegetal() > 0 || pagoRcoaBean.getMontoTotalRGD() > 0)){
				generarNutCoberturaVegetal();
			}
			return;
		}
		nutGenerado=true;
		String mensaje = "", tasaDescripcion="";
		Integer cuantaId=1;
		Double valorTotal = 0.0;
		Map<Double, String> listaValores = new TreeMap<Double, String>();
		switch (pagoRcoaBean.getTipoProyecto()) {
		case "RGD":
			tasaDescripcion = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
			cuantaId=1;
			mensaje = "$"+pagoRcoaBean.getMontoTotalProyecto()+" correspondiente a RGDP.";
			valorTotal += pagoRcoaBean.getMontoTotalProyecto();
			listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), tasaDescripcion);
			break;
		case "ESTUDIOIMPACTO":
			tasaDescripcion = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
			cuantaId=1;
			mensaje = "$"+pagoRcoaBean.getMontoTotalProyecto()+" correspondiente a revisión Estudio de impacto ambiental.";
			valorTotal += pagoRcoaBean.getMontoTotalProyecto();
			listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), tasaDescripcion);
			break;
		case "RSQ":
			tasaDescripcion = CodigoTasa.REGISTRO_SUSTANCIAS_QUIMICAS.getDescripcion();
			cuantaId=1;
			mensaje = "$"+pagoRcoaBean.getMontoTotalProyecto()+" correspondiente a pago de sustancias químicas.";
			valorTotal += pagoRcoaBean.getMontoTotalProyecto();
			listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), tasaDescripcion);
			break;
		case "CERTIFICADO":
			tasaDescripcion = CodigoTasa.INVENTARIO_FORESTAL_MADERA_PIE.getDescripcion();
			cuantaId=4;
			mensaje = "$"+pagoRcoaBean.getMontoTotalProyecto()+" correspondiente a remoción de cobertura vegetal nativa.";
			valorTotal += pagoRcoaBean.getMontoTotalProyecto();
			listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), tasaDescripcion);
			break;
		case "REGISTRO":
			tasaDescripcion = CodigoTasa.REGISTRO_AMBIENTAL.getDescripcion();
			cuantaId=1;
			mensaje = "$"+pagoRcoaBean.getMontoTotalProyecto()+" correspondiente a Registro Ambiental";
			valorTotal += pagoRcoaBean.getMontoTotalProyecto();
			listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), tasaDescripcion);
			if(pagoRcoaBean.getMontoTotalCoberturaVegetal() > 0){
				generarNutCoberturaVegetal();
			}
			break;
		case "PARTICIPACION":
			CatalogoGeneralCoa valorIvaS = catalogoCoaFacade.obtenerCatalogoPorCodigo("valor.impuesto.iva");
			double valorIva = Double.parseDouble(valorIvaS.getValor()); 
			valorIva = 1 + (valorIva /100);
			tasaDescripcion = CodigoTasa.FACILIATDOR.getDescripcion();
			cuantaId=1;
			mensaje = "$"+pagoRcoaBean.getMontoTotalProyecto()+" correspondiente a la tasa por servicios de facilitación.";
			if(proyectosBean.getProyectoRcoa().getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
				Double valorTotalFacilitadores = 0.0, valorTotalFacilitadoresGalapagos = 0.0;
				Tarifas tarifaFacilitadorGalapagos = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.FACILIATDOR_GALAPAGOS.getDescripcion()); 
				Tarifas tarifaFacilitadorEC = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.FACILIATDOR.getDescripcion());
				if(tarifaFacilitadorGalapagos == null || tarifaFacilitadorEC == null){
					JsfUtil.addMessageError("Ocurrió un error al cargar la información. Por favor comunicarse con mesa de ayuda.");
					pagoRcoaBean.setCumpleMontoProyecto(true);
					return;
				}
				List<Usuario> listaUsuariosFacilitadores = facilitadorPPCFacade.usuariosFacilitadoresAsignadosPendientesAceptacion(proyectosBean.getProyectoRcoa());
				for (Usuario user : listaUsuariosFacilitadores) {
					Usuario ufacilitador = usuarioFacade.buscarUsuarioPorId(user.getId());
					String codeProvinciaFacilitador = ufacilitador.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec();
					if(!codeProvinciaFacilitador.equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
						valorTotalFacilitadoresGalapagos += (tarifaFacilitadorGalapagos.getTasasValor() * valorIva);
					} else{
						valorTotalFacilitadores += (tarifaFacilitadorEC.getTasasValor() * valorIva);
					}
				}
				if(valorTotalFacilitadoresGalapagos > 0)
					listaValores.put(valorTotalFacilitadoresGalapagos, CodigoTasa.FACILIATDOR_GALAPAGOS.getDescripcion());
				if(valorTotalFacilitadores > 0)
				listaValores.put(valorTotalFacilitadores, tasaDescripcion);
			}else{
				valorTotal += pagoRcoaBean.getMontoTotalProyecto();
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), tasaDescripcion);
			}
			break;
		case "LICENCIA":
			if (pagoRcoaBean.getCategorizacion()==3) {
				tasaDescripcion = CodigoTasa.EMISION_LICENCIA_PAGO_MINIMO_BAJO_IMPACTO.getDescripcion();
			} else {
				tasaDescripcion = CodigoTasa.EMISION_LICENCIA_PAGO_MINIMO_ALTO_IMPACTO.getDescripcion();
			}
			cuantaId=1;
			if(!pagoRcoaBean.isEsEnteAcreditado()){
				mensaje = "$"+pagoRcoaBean.getMontoTotalProyecto()+" correspondiente a la tasa por costo del proyecto";
				valorTotal += pagoRcoaBean.getMontoTotalProyecto();
				listaValores.put(pagoRcoaBean.getMontoTotalProyecto(), tasaDescripcion);
			}
			if(pagoRcoaBean.getMontoTotalRGD() > 0){
				String tasaDescripcionRgd = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
				mensaje += (mensaje.isEmpty() ? "" : ", ")+ "$"+pagoRcoaBean.getMontoTotalRGD()+" correspondiente a RGDP.";
				valorTotal += pagoRcoaBean.getMontoTotalRGD();
				listaValores.put(pagoRcoaBean.getMontoTotalRGD(), tasaDescripcionRgd);
			}
			if(pagoRcoaBean.getMontoTotalCoberturaVegetal() > 0){
				generarNutCoberturaLicencia();
			}
			break;

		default:
			break;
		}
		// valido para que no se genere otro nut si ya existe uno activo
		Thread.sleep(2000);
		boolean existe=false;
		List<NumeroUnicoTransaccional> objNuts =numeroUnicoTransaccionalFacade.listNUTActivoPorTramitePorTarea(pagoRcoaBean.getTramite(), bandejaTareasBean.getTarea().getProcessInstanceId());
		if(objNuts != null){
			for (NumeroUnicoTransaccional numeroUnicoTransaccional : objNuts) {
				if(numeroUnicoTransaccional.getEstadosNut().getId().equals(2) && !numeroUnicoTransaccional.getCuentas().getId().equals(4)){
					existe=true;
					break;
				}
			}
		}
		if(existe){
			JsfUtil.addMessageInfo("Usted ya tiene generado un Número Único de Trámite.");
			return;
		}
		//busco si hay nuts activos para ya no generar
		List<NumeroUnicoTransaccional> listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramitePorTareaPorBancoPorCuenta(pagoRcoaBean.getTramite(), bandejaTareasBean.getTarea().getProcessInstanceId(), pagoRcoaBean.getInstitucionFinancieraNut().getId(), cuantaId);
		if(listNUTXTramite!=null && listNUTXTramite.size()>0 && !nuevoComprobante){
			for (NumeroUnicoTransaccional nut : listNUTXTramite){
				if(nut.getEstadosNut().getId() == 2){
					List<DocumentoNUT> comprobantes = listarDocumentosTarea(pagoRcoaBean.getTramite());
					if (comprobantes.size() > 0) {
						// cargo los documentos generados
						for (DocumentoNUT documentoNUT : comprobantes) {
							if(nut.getSolicitudUsuario().getId().equals(documentoNUT.getSolicitudId())){
								pagoRcoaBean.getDocumentosNUT().add(documentoNUT);
							}
						}
					}
					JsfUtil.addMessageInfo("Usted ya tiene generado un Número Único de Trámite, por favor descargue en documentos del trámite.");
					return;
				}
			}
		}
		EntidadPagoNUTRcoa entidadpago = generarNUTPago(pagoRcoaBean.getTramite(), valorTotal, listaValores, cuantaId, mensaje);
		if(!entidadpago.isCorrecto()){
			if(entidadpago.getNumeroUnicoTransaccional() != null && entidadpago.getNumeroUnicoTransaccional().getId() != null
				&& entidadpago.getNumeroUnicoTransaccional().getEstadosNut().getId().equals(2)){
				List<DocumentoNUT> comprobantes = listarDocumentosTarea(pagoRcoaBean.getTramite());
				if (comprobantes.size() > 0) {
					//pagoRcoaBean.getDocumentosNUT().addAll(comprobantes);
					// elimino los documento generados para generar nuevamente
					for (DocumentoNUT documentoNUT : comprobantes) {
						if(entidadpago.getSolicitudUsuario().getId().equals(documentoNUT.getSolicitudId())){
							documentoNUT.setEstado(false);
							documentosNUTFacade.guardar(documentoNUT);
						}
					}
					byte[] contenidoDocumento = generarDocumentoVariosNut(entidadpago.getSolicitudUsuario());
					entidadpago.setArchivo(contenidoDocumento);
					DocumentoNUT documentoPago = generarDocumento(entidadpago);
					pagoRcoaBean.getDocumentosNUT().add(documentoPago);
				}
			}
			JsfUtil.addMessageError(entidadpago.getMensaje());
			return;
		}
		Integer numeroDocumento=1;
		DocumentoNUT documentoPago = generarDocumento(entidadpago);
		if(pagoRcoaBean.getDocumentosNUT() == null)
			pagoRcoaBean.setDocumentosNUT(new ArrayList<DocumentoNUT>());
		pagoRcoaBean.getDocumentosNUT().add(documentoPago);
		numeroDocumento++;
		List<String>listaArchivos= new ArrayList<String>();
		listaArchivos=getListPathArchivos();
		enviarNotificacionPago(entidadpago.getSolicitudUsuario().getUsuario(),entidadpago.getSolicitudUsuario().getSolicitudCodigo(), listaArchivos);
		JsfUtil.addMessageInfo("Se ha enviado un correo electrónico con las ordenes de pago a realizar.");
	}
	
	private DocumentoNUT generarDocumento(EntidadPagoNUTRcoa entidadpago ) throws Exception{
		DocumentoNUT documentoPago = new DocumentoNUT();
		documentoPago.setContenidoDocumento(entidadpago.getArchivo());
		documentoPago.setMime("application/pdf");
		documentoPago.setIdTabla(entidadpago.getSolicitudUsuario().getId());
		documentoPago.setNombreTabla(SolicitudUsuario.class.getSimpleName());
		documentoPago.setNombre("Orden de pago" + ".pdf");
		documentoPago.setExtesion(".pdf");
		documentoPago.setSolicitudId(entidadpago.getSolicitudUsuario().getId());
		documentoPago.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
		documentoPago = documentosNUTFacade.guardarDocumentoAlfresco(
						entidadpago.getNumeroUnicoTransaccional().getNutCodigoProyecto() != null ? entidadpago.getNumeroUnicoTransaccional().getNutCodigoProyecto() : entidadpago.getSolicitudUsuario().getSolicitudCodigo(),
						"RECAUDACIONES", null, documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
		return documentoPago;
	}
	
	public void generarNutCoberturaVegetal() {
		try{
			String codigoTramite=proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental();
			String mensaje = "", tasaDescripcion="";
			Integer cuantaId=1;
			Double valorTotal = 0.0;
			Map<Double, String> listaValores = new TreeMap<Double, String>();
			switch (pagoRcoaBean.getTipoProyecto()) {
			case "RGD":
				tasaDescripcion = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
				cuantaId=1;
				mensaje = "$"+pagoRcoaBean.getMontoTotalCoberturaVegetal()+ " correspondiente a RGDP.";
				valorTotal += pagoRcoaBean.getMontoTotalCoberturaVegetal();
				listaValores.put(pagoRcoaBean.getMontoTotalCoberturaVegetal(), tasaDescripcion);
				break;
			case "RSQ":
				tasaDescripcion = CodigoTasa.REGISTRO_SUSTANCIAS_QUIMICAS.getDescripcion();
				cuantaId=1;
				mensaje = "$"+pagoRcoaBean.getMontoTotalCoberturaVegetal()+" correspondiente a pago de sustancias químicas.";
				valorTotal += pagoRcoaBean.getMontoTotalCoberturaVegetal();
				listaValores.put(pagoRcoaBean.getMontoTotalCoberturaVegetal(), tasaDescripcion);
				break;
			case "CERTIFICADO":
				tasaDescripcion = CodigoTasa.INVENTARIO_FORESTAL_MADERA_PIE.getDescripcion();
				cuantaId=4;
				mensaje = "$"+pagoRcoaBean.getMontoTotalCoberturaVegetal()+" correspondiente a remoción de cobertura vegetal nativa.";
				valorTotal += pagoRcoaBean.getMontoTotalCoberturaVegetal();
				listaValores.put(pagoRcoaBean.getMontoTotalCoberturaVegetal(), tasaDescripcion);
				break;
			case "REGISTRO":
				tasaDescripcion = CodigoTasa.INVENTARIO_FORESTAL_MADERA_PIE.getDescripcion();
				cuantaId=4;
				mensaje = "$"+pagoRcoaBean.getMontoTotalCoberturaVegetal()+" correspondiente a remoción de cobertura vegetal nativa.";
				valorTotal += pagoRcoaBean.getMontoTotalCoberturaVegetal();
				listaValores.put(pagoRcoaBean.getMontoTotalCoberturaVegetal(), tasaDescripcion);
				break;
			case "LICENCIA":
				cuantaId=1;
				tasaDescripcion = CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion();
				if(pagoRcoaBean.getMontoTotalRGD() > 0){
					mensaje += (mensaje.isEmpty() ? "" : ", ")+"$"+pagoRcoaBean.getMontoTotalRGD()+" correspondiente a RGDP.";
					valorTotal += pagoRcoaBean.getMontoTotalRGD();
					listaValores.put(pagoRcoaBean.getMontoTotalRGD(), tasaDescripcion);
				}
				if(pagoRcoaBean.getMontoTotalCoberturaVegetal() > 0){
					generarNutCoberturaLicencia();
				}
				break;
			default:
				break;
			}
			if(valorTotal == 0.0 || listaValores.size() == 0){
				return;
			}
			Integer numeroDocumento=2;
			EntidadPagoNUTRcoa entidadpago = generarNUTPago(codigoTramite, valorTotal, listaValores, cuantaId, mensaje);
			if(!entidadpago.isCorrecto()){
				JsfUtil.addMessageError(entidadpago.getMensaje());
				if(entidadpago.getNumeroUnicoTransaccional() != null && entidadpago.getNumeroUnicoTransaccional().getId() != null
						&& entidadpago.getNumeroUnicoTransaccional().getEstadosNut().getId().equals(2)){
					List<DocumentoNUT> comprobantes = listarDocumentosTarea(pagoRcoaBean.getTramite());
					if (comprobantes.size() > 0) {
						//pagoRcoaBean.getDocumentosNUT().addAll(comprobantes);//comentado para que se genere el documento nuevamente
						// elimino los documento generados para generar nuevamente
						for (DocumentoNUT documentoNUT : comprobantes) {
							if(entidadpago.getSolicitudUsuario().getId().equals(documentoNUT.getSolicitudId())){
								documentoNUT.setEstado(false);
								documentosNUTFacade.guardar(documentoNUT);	
							}
						}
						// genero el nuevo documento
						byte[] contenidoDocumento = generarDocumentoVariosNut(entidadpago.getSolicitudUsuario());
						entidadpago.setArchivo(contenidoDocumento);
						DocumentoNUT documentoPago = new DocumentoNUT();
						documentoPago.setContenidoDocumento(entidadpago.getArchivo());
						documentoPago.setNombre("Orden de pago"+ numeroDocumento +".pdf");
						documentoPago.setExtesion(".pdf");
						documentoPago.setMime("application/pdf");
						documentoPago.setNombreTabla(SolicitudUsuario.class.getSimpleName());
						documentoPago.setIdTabla(entidadpago.getSolicitudUsuario().getId());
						documentoPago.setSolicitudId(entidadpago.getSolicitudUsuario().getId());
						documentoPago.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
						
						documentoPago = documentosNUTFacade.guardarDocumentoAlfresco(codigoTramite,
								"RECAUDACIONES", bandejaTareasBean.getTarea().getProcessInstanceId(),
								documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
						pagoRcoaBean.getDocumentosNUT().add(documentoPago);
						
						
					}
				}
				return;
			}
			DocumentoNUT documentoPago = new DocumentoNUT();
			documentoPago.setContenidoDocumento(entidadpago.getArchivo());
			documentoPago.setNombre("Orden de pago"+ numeroDocumento +".pdf");
			documentoPago.setExtesion(".pdf");
			documentoPago.setMime("application/pdf");
			documentoPago.setNombreTabla(SolicitudUsuario.class.getSimpleName());
			documentoPago.setIdTabla(entidadpago.getSolicitudUsuario().getId());
			documentoPago.setSolicitudId(entidadpago.getSolicitudUsuario().getId());
			documentoPago.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
			
			documentoPago = documentosNUTFacade.guardarDocumentoAlfresco(codigoTramite,
					"RECAUDACIONES", bandejaTareasBean.getTarea().getProcessInstanceId(),
					documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
			
			pagoRcoaBean.getDocumentosNUT().add(documentoPago);
			
			List<String>listaArchivos= new ArrayList<String>();
			listaArchivos=getListPathArchivos();
			if(pagoRcoaBean.isEsEnteAcreditado()){
				enviarNotificacionPago(entidadpago.getSolicitudUsuario().getUsuario(),entidadpago.getSolicitudUsuario().getSolicitudCodigo(), listaArchivos);
				JsfUtil.addMessageInfo("Se ha enviado un correo electrónico con las ordenes de pago a realizar.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generarNutCoberturaLicencia() {
		try{
			String codigoTramite=proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental();
			String mensaje = "", tasaDescripcion="";
			Integer cuantaId=4;
			tasaDescripcion = CodigoTasa.INVENTARIO_FORESTAL_MADERA_PIE.getDescripcion();
			mensaje += (mensaje.isEmpty() ? "" : ", ")+"$"+pagoRcoaBean.getMontoTotalCoberturaVegetal()+" correspondiente al Inventario Forestal.";
			Map<Double, String> listaValores = new TreeMap<Double, String>();
			Integer numeroDocumento=3;
			if(pagoRcoaBean.getMontoTotalCoberturaVegetal() > 0){
				listaValores.put(pagoRcoaBean.getMontoTotalCoberturaVegetal(), tasaDescripcion);
			}
			EntidadPagoNUTRcoa entidadpago = generarNUTPago(codigoTramite, pagoRcoaBean.getMontoTotalCoberturaVegetal(), listaValores, cuantaId, mensaje);
			if(!entidadpago.isCorrecto()){
				JsfUtil.addMessageError(entidadpago.getMensaje());
				if(entidadpago.getNumeroUnicoTransaccional() != null && entidadpago.getNumeroUnicoTransaccional().getId() != null
						&& entidadpago.getNumeroUnicoTransaccional().getEstadosNut().getId().equals(2)){
					List<DocumentoNUT> comprobantes = listarDocumentosTarea(pagoRcoaBean.getTramite());
					if (comprobantes.size() > 0) {
						//pagoRcoaBean.getDocumentosNUT().addAll(comprobantes);//comentado para que se genere el documento nuevamente
						// elimino los documento generados para generar nuevamente
						for (DocumentoNUT documentoNUT : comprobantes) {
							if(entidadpago.getSolicitudUsuario().getId().equals(documentoNUT.getSolicitudId())){
								documentoNUT.setEstado(false);
								documentosNUTFacade.guardar(documentoNUT);	
							}
						}
						// genero el nuevo documento
						byte[] contenidoDocumento = generarDocumentoVariosNut(entidadpago.getSolicitudUsuario());
						entidadpago.setArchivo(contenidoDocumento);
						DocumentoNUT documentoPago = new DocumentoNUT();
						documentoPago.setContenidoDocumento(entidadpago.getArchivo());
						documentoPago.setNombre("Orden de pago"+ numeroDocumento +".pdf");
						documentoPago.setExtesion(".pdf");
						documentoPago.setMime("application/pdf");
						documentoPago.setNombreTabla(SolicitudUsuario.class.getSimpleName());
						documentoPago.setIdTabla(entidadpago.getSolicitudUsuario().getId());
						documentoPago.setSolicitudId(entidadpago.getSolicitudUsuario().getId());
						documentoPago.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
						
						documentoPago = documentosNUTFacade.guardarDocumentoAlfresco(codigoTramite,
								"RECAUDACIONES", bandejaTareasBean.getTarea().getProcessInstanceId(),
								documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
						pagoRcoaBean.getDocumentosNUT().add(documentoPago);
					}
				}
				return;
			}
			DocumentoNUT documentoPago = new DocumentoNUT();
			documentoPago.setContenidoDocumento(entidadpago.getArchivo());
			documentoPago.setNombre("Orden de pago"+ numeroDocumento +".pdf");
			documentoPago.setExtesion(".pdf");
			documentoPago.setMime("application/pdf");
			documentoPago.setNombreTabla(SolicitudUsuario.class.getSimpleName());
			documentoPago.setIdTabla(entidadpago.getSolicitudUsuario().getId());
			documentoPago.setSolicitudId(entidadpago.getSolicitudUsuario().getId());
			documentoPago.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
			
			documentoPago = documentosNUTFacade.guardarDocumentoAlfresco(codigoTramite,
					"RECAUDACIONES", bandejaTareasBean.getTarea().getProcessInstanceId(),
					documentoPago, TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
			
			pagoRcoaBean.getDocumentosNUT().add(documentoPago);
			
			List<String>listaArchivos= new ArrayList<String>();
			listaArchivos=getListPathArchivos();
			if(pagoRcoaBean.isEsEnteAcreditado() && pagoRcoaBean.getMontoTotalRGD() <= 0 ){
				enviarNotificacionPago(entidadpago.getSolicitudUsuario().getUsuario(),entidadpago.getSolicitudUsuario().getSolicitudCodigo(), listaArchivos);
				JsfUtil.addMessageInfo("Se ha enviado un correo electrónico con las ordenes de pago a realizar.");
			}
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
	
	public void mostratVentana(){
        if(pagoRcoaBean.getInstitucionFinancieraNut() != null && pagoRcoaBean.getInstitucionFinancieraNut().getPagoEnLinea() != null && pagoRcoaBean.getInstitucionFinancieraNut().getPagoEnLinea() )
        {
        	//String men_aux = " ";
        	String entidad_pago = pagoRcoaBean.getInstitucionFinancieraNut().getNombreInstitucion();
        	pagoRcoaBean.setEntidadPago(entidad_pago);        	
            pagoRcoaBean.setTipoPago("");
            pagoKushkiController.init();
            pagoRcoaBean.setPagaLinea(true);
              if (pagoRcoaBean.getValorAPagar() < Double.parseDouble(pagoRcoaBean.getMontoMinimopago()))
              {
            	  RequestContext.getCurrentInstance().execute("PF('dlgPagoMin').show();");            	  
              }
              else if (pagoRcoaBean.getValorAPagar() > Double.parseDouble(pagoRcoaBean.getMontoMaximopago()))
              {
            	  RequestContext.getCurrentInstance().execute("PF('dlgPagoMax').show();");
              }
        }
        else
        {
        	pagoRcoaBean.setPagaLinea(true);
            pagoRcoaBean.setTipoPago("NUT");
            if(pagoRcoaBean.getInstitucionFinancieraNut() == null)
            	pagoRcoaBean.setTipoPago("");
        }
    }

	public StreamedContent descargar(DocumentoNUT documento) {
		DefaultStreamedContent content = null;
		try {
			if (documento.getAlfrescoId() != null) {
				byte[] documentoContent = documentosNUTFacade.descargar(documento.getAlfrescoId());
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
    
    public String getConceptoPago(Integer tipoPago) {
    	String tipoConceptoPago = "";
    	switch (pagoRcoaBean.getTipoProyecto()) {
		case "RGD":
			tipoConceptoPago = "Pago por RGD.";
			break;
		case "RSQ":
			tipoConceptoPago = "Pago por Declaración de sustancias químicas.";
			break;
		case "PARTICIPACION":
			tipoConceptoPago = "Pago por servicios de facilitadores.";
			break;
		case "CERTIFICADO":
			tipoConceptoPago = "Pago por remoción de cobertura vegetal nativa.";
			break;
		case "REGISTRO":
			tipoConceptoPago = "Pago por Registro Ambiental";
			break;
		case "LICENCIA":
			tipoConceptoPago = "Pago por Licencia Ambiental.";
			break;
		default:
			break;
		}
		switch (tipoPago) {
		case 2:
			tipoConceptoPago = "Pago por Inventario Forestal";
			break;
		case 3:
			tipoConceptoPago = "Pago por RGD";
			break;
		default:
			break;
		}
		return tipoConceptoPago;
    }
    
	@SuppressWarnings("unchecked")
	public List<PagoKushkiJson> listPagosKushki(String tramite) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tramite", tramite);
	    List<PagoKushkiJson> listaPagos = (List<PagoKushkiJson>) crudServiceBean.findByNamedQuery(PagoKushkiJson.LISTAR_POR_TRAMITE,params);

		if (listaPagos != null && !listaPagos.isEmpty()) {
			String Num_Ticket = listaPagos.get(0).getPajsTicketnumber();
			String Entidad_P = listaPagos.get(0).getPajsSku2();
			pagoRcoaBean.setTicketPago(Num_Ticket);
			pagoRcoaBean.setEntidad_Pagada(Entidad_P);
			return listaPagos;
		} else {
			return null;
		}
	}

	public void validateValorProyecto(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		String mensaje="";
		if (!pagoRcoaBean.isCalcularValorPagar()) {
			switch (pagoRcoaBean.getTipoProyecto()) {
			case "ESTUDIOIMPACTO":
				mensaje = "Debe ingresar el valor total del contrato";
				break;
			case "LICENCIA":
				mensaje = "Debe realizar el cálculo del valor total.";
				if(pagoRcoaBean.getMontoTotalProyecto() > 0 || pagoRcoaBean.getMontoTotalCoberturaVegetal() > 0 || pagoRcoaBean.getMontoTotalRGD() > 0	)
					mensaje = "Debe guardar la información.";
				break;

			default:
				break;
			}
    		errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,mensaje, null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void mostrarVersionAnterior() throws ParseException{
		String urlPagina="";
		switch(bandejaTareasBean.getTarea().getProcessName()){
		case "Registro de Generador de Residuos y Desechos Peligrosos yo Especiales":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("pago")){
				urlPagina = bandejaTareasBean.getTarea().getTaskSummary().getDescription().replace("realizarPagosrgd.", "realizarPagosrgdV1.");
			}
			break;
		case "Declaracion Sustancias Quimicas":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("pago")){
				urlPagina = bandejaTareasBean.getTarea().getTaskSummary().getDescription().replace("realizarPago.", "realizarPagoV1.");
			}
			break;
		case "Proceso de Participacion Ciudadana":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("pago")){
				urlPagina = bandejaTareasBean.getTarea().getTaskSummary().getDescription().replace("realizarPagoFacilitadores.", "realizarPagoFacilitadoresV1.");
			}
			break;
		case "Resolucion Licencia Ambiental":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("valor")){
				urlPagina = bandejaTareasBean.getTarea().getTaskSummary().getDescription().replace("operadorCostoProyecto.", "operadorCostoProyectoV1.");
			}
			break;
		case "Registro Ambiental":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("pago")){
				urlPagina = bandejaTareasBean.getTarea().getTaskSummary().getDescription().replace("pagoRegistroAmbientalCoa.", "pagoRegistroAmbientalCoaV1.");
			}
			break;
		case "Certificado Ambiental":
			PagoCertificadoController certificadoController = JsfUtil.getBean(PagoCertificadoController.class);
			certificadoController.continuar_();
			break;
		}
		if(urlPagina != null && !urlPagina.isEmpty())
			JsfUtil.redirectTo(urlPagina);
	}
	
	public void cerrarMensaje(){
		if(pagoRcoaBean.getDocumentosNUT() != null && pagoRcoaBean.getDocumentosNUT().size() > 0){
	    	RequestContext.getCurrentInstance().execute("PF('dlgAviso').hide();");
		}
	}
	
	private Double getValorPagadoPorTipoPago(TransaccionFinanciera _transaccionFinanciera) {
    	Double montoPagado = 0.00;
		for (TransaccionFinanciera transaccion : pagoRcoaBean.getTransaccionesFinancieras()) {
			if (transaccion.getTipoPago().equals(_transaccionFinanciera.getTipoPago())
					&& transaccion.getInstitucionFinanciera().getCodigoInstitucion().trim()
							.equals(_transaccionFinanciera.getInstitucionFinanciera().getCodigoInstitucion())) {
				montoPagado += transaccion.getMontoPago();
			}
		}
		return montoPagado;
	}
}
