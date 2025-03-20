package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.UbicacionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.PagoRGDServiciosBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.CatalogoRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CatalogoRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.CodigoTasa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.ProyectosConPagoSinNutFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasFacade;
import ec.gob.ambiente.suia.recaudaciones.model.Cuentas;
import ec.gob.ambiente.suia.recaudaciones.model.EstadosNut;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.ProyectosConPagoSinNut;
import ec.gob.ambiente.suia.recaudaciones.model.SolicitudUsuario;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.recaudaciones.model.TarifasNUT;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean	
@ViewScoped
public class PagosRGDV1Controller {

	private static final Logger LOG = Logger.getLogger(PagoRGDServiciosBean.class);
	private static final String NOMBREMINISTERIO ="Ministerio del Ambiente, Agua y Transición Ecológica";

	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcofacade;	
	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;	
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private CrudServiceBean crudServiceBean;	
	@EJB
	private SecuenciasFacade secuenciasFacade;	
	@EJB
	private TarifasFacade tarifasFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private AreaFacade areaFacade;	
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private CatalogoRgdRcoaFacade catalogoRgdRcoaFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private UbicacionDigitalizacionFacade ubicacionDigitalizacionFacade;
	@EJB
	private ProyectosConPagoSinNutFacade proyectosConPagoSinNutFacade;
	

	/***************************************************************************************************/
	

	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoSuia;
	/****************************************************************************************************/
	@Getter
	@Setter
	public double montoTotal;
	
	@Getter
	@Setter
	public double montoTotaladicional;
	
	@Getter
	@Setter
	public double montounico;
	
	@Getter
	@Setter
	public double montoAdicional;	
	
	@Getter
	@Setter
	public Integer adicional;

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
	private List<DocumentosRgdRcoa> documentosRgdRcoasNUT;	
	
	@Getter
	@Setter
	private List<InstitucionFinanciera> institucionesFinancieras;
	
	@Getter
	@Setter
	private List<InstitucionFinanciera> institucionesFinancierasCobertura;

	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinanciera;
	
	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinancieraCobertura;

	@Getter
	@Setter
	private List<TransaccionFinanciera> transaccionesFinancieras;	

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
	public double montoTotalProyecto;
	
	@Getter
	@Setter
	public Boolean cumpleMontoProyecto = false;
	
	@Getter
	@Setter
	private boolean eligioEnte;
	
	@Getter
	@Setter
	private List<InstitucionFinanciera> entesAcreditados;
	
	@Getter
	@Setter
	public Boolean cumpleMonto = false;
	
	private String identificadorMotivo;
	
	@Getter
	@Setter
	public Boolean cumpleMontoCobertura = false;
	
	@Getter
	@Setter
	public double montoTotalCoberturaVegetal;
	
	@Getter
	@Setter
	public Boolean generarNUT;
	
	@Getter
	@Setter
	public Boolean valorAdicional=false;	
	
	@Getter
	@Setter
	private String mensaje;
	
	@Getter
	private String tramite, tipoPermisoRGD;

	private Map<String, Object> variables;
	private Integer proyectoId;
	
	@Getter
	@Setter
	private String mensajeFinalizar, urlVerTramite;
	
	@Getter
	public Boolean datoFormaspago=true;
	
	@Getter
    @Setter
    private Boolean condiciones = false;
	
	@Getter
	@Setter
	private Boolean persist = false;
		
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private RegistroGeneradorDesechosRcoa registroGenerador;
	
	@PostConstruct
	public void init() throws JbpmException {
		urlVerTramite ="/pages/rcoa/generadorDesechos/informacionRegistroGeneradorVer.jsf";
		variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
		tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
		tipoPermisoRGD =(String)variables.get(Constantes.VARIABLE_TIPO_RGD);
		proyectoId = Integer.valueOf(((String)variables.get("idProyectoDigitalizacion") == null)?"0":(String)variables.get("idProyectoDigitalizacion"));
		registroGenerador = new RegistroGeneradorDesechosRcoa();
		if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
			datosOperadorRgdBean.setTitulo("Responsable o representante de la empresa");
			registroGenerador = registroGeneradorDesechosRcoaFacade.buscarRGDPorCodigo(tramite);
			this.identificadorMotivo = tramite;
			urlVerTramite ="/pages/rcoa/generadorDesechos/informacionRegistroGeneradorVerREP.jsf";
		}
		List<RegistroGeneradorDesechosProyectosRcoa> registroGeneradorDesechosProyectosRcoas = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
		// si es de digitalizacion ya no busco el proyecto aosciado
		if(proyectoId > 0)
			registroGenerador = registroGeneradorFacade.buscarRGDPorProyectoDigitalizado(proyectoId);
		// si es de digitalizacion ya no busco el proyecto aosciado
		if(registroGenerador == null || registroGenerador.getId() == null){
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			// si es proyecto rcoa
			if(proyecto != null && proyecto.getId() != null){
				registroGeneradorDesechosProyectosRcoas= registroGeneradorDesechosProyectosRcoaFacade.asociados(proyecto.getId());
			}else{
				proyectoSuia = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(tramite);
				if(proyectoSuia != null && proyectoSuia.getId() != null)
					registroGeneradorDesechosProyectosRcoas= registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoSuia(proyectoSuia.getId());
				if(proyectosBean.getProyecto() == null)
					proyectosBean.setProyecto(proyectoSuia);
			}
		}
		
		CatalogoRgdRcoa catalogoPago = catalogoRgdRcoaFacade.findByCodigo("rgd_valor_pago_registro");
		Double pago = Double.valueOf(catalogoPago.getDescripcion());

		this.montoTotal = pago;
		this.montoAdicional=50;	
		this.montounico=90; //180

		//Se debe realizar código para cuando sean varios proyectos
		if (registroGeneradorDesechosProyectosRcoas.size()>1){
			registroGenerador = registroGeneradorDesechosProyectosRcoas.get(0).getRegistroGeneradorDesechosRcoa();
			mensaje = "El pago corresponde a la tasa por concepto de Registro Generador de Residuos o "
					+ " Desechos Peligrosos y/o Especiales, con un valor de 180 USD que incluye a una "
					+ "actividad; y adicionalmente, 50 USD por cada una de las actividades agrupadas "
					+ "bajo un mismo Registro Generador. Estos valores serán depositados en la cuenta "
					+ "corriente No. 3001480620 sublínea No. 190499 a nombre del "+NOMBREMINISTERIO.toUpperCase()+".";			
			
			valorAdicional=true;
			adicional=registroGeneradorDesechosProyectosRcoas.size();
			montoTotaladicional=(montoAdicional*adicional);
			montoTotal=montounico+montoTotaladicional;
			montoTotalProyecto=montoTotal;
		}else{
			mensaje = "El pago corresponde a la tasa por concepto de Registro Generador de Residuos o "
				+ " Desechos Peligrosos y/o Especiales, con un valor de 180 USD, se lo debe realizar por medio de la opción de Pago en Línea del "+NOMBREMINISTERIO.toUpperCase()+"";
		
			if (registroGeneradorDesechosProyectosRcoas.size()>0){
				registroGenerador = registroGeneradorDesechosProyectosRcoas.get(0).getRegistroGeneradorDesechosRcoa();
			}
			montoTotalProyecto=montoTotal;
			CatalogoRgdRcoa catalogoMensaje = catalogoRgdRcoaFacade.findByCodigo("rgd_mensaje_pago_registro");	
			mensaje = catalogoMensaje.getDescripcion();
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
				mensaje = "El pago correspondiente a la tasa por concepto de Registro de generador de residuos y desechos peligrosos y/o especiales por"
						+ " aplicación de Responsabilidad Extendida del Productor (REP), tiene un valor 180 USD, se lo debe realizar por medio de la opción de Pago en Línea del "+NOMBREMINISTERIO.toUpperCase()+"";
			}
		}

		if (registroGenerador != null && registroGenerador.getUsuario() != null){
			datosOperadorRgdBean.buscarDatosOperador(registroGenerador.getUsuario());
		}
		cumpleMontoProyecto=false;
		transaccionFinanciera = new TransaccionFinanciera();
		
		String codigoTramite=tramite;
		documentosRgdRcoasNUT = new ArrayList<>();
		List<NumeroUnicoTransaccional> listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoTramite);
		if(listNUTXTramite!=null && listNUTXTramite.size()>0){
			for (NumeroUnicoTransaccional nut : listNUTXTramite) {
				List<DocumentosRgdRcoa> comprobantes =  documentosRgdRcofacade
						.documentoXTablaIdXIdDoc(nut.getSolicitudUsuario().getId(), 
								"NUT RECAUDACIONES", TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
				
				if (comprobantes.size() > 0) {
					documentosRgdRcoasNUT.addAll(comprobantes);
				} 
			}
		}
		if(codigoTramite!=null && montoTotalProyecto>0){
			generarNUT=true;
		}
		
		institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC();
		
		if(institucionesFinancieras != null && !institucionesFinancieras.isEmpty()){
			transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
		}		
		
		if (this.identificadorMotivo == null){
			if(proyecto != null && proyecto.getId() != null){
				transaccionesFinancieras = transaccionFinancieraFacade
						.cargarTransaccionesRcoa(proyectosBean.getProyectoRcoa()
								.getId(), bandejaTareasBean.getTarea()
								.getProcessInstanceId(), bandejaTareasBean
								.getTarea().getTaskId());
			}else if(proyectoSuia != null && proyectoSuia.getId() != null){
				transaccionesFinancieras = transaccionFinancieraFacade
						.cargarTransacciones(bandejaTareasBean.getTarea()
								.getProcessInstanceId(), bandejaTareasBean
								.getTarea().getTaskId());
			}else if(proyectoId > 0){
				this.identificadorMotivo = tramite;
				transaccionesFinancieras = transaccionFinancieraFacade.cargarTransacciones(this.identificadorMotivo);
			}
		}else{
			transaccionesFinancieras = transaccionFinancieraFacade
					.cargarTransacciones(this.identificadorMotivo);
		}
		
		if(transaccionesFinancieras.isEmpty())
			persist = true;
		
		if(transaccionesFinancieras.size()>0){
			cumpleMonto = cumpleMonto();
		}		
	}
	
	private boolean cumpleMonto() {
        double montoTotal = 0;
        for (TransaccionFinanciera transa : transaccionesFinancieras) {
           montoTotal += transa.getMontoTransaccion();
        }
       
        DecimalFormat decimalValorTramite= new DecimalFormat(".##");
        String x="";
       
        x=decimalValorTramite.format(this.montoTotal).replace(",",".");
        
        if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() != null){
        	
			this.montoTotal = Double.valueOf(decimalValorTramite.format(this.montoTotalProyecto).replace(",", "."));
			if (montoTotal >= this.montoTotal) {
				cumpleMontoProyecto = true;
				return true;
			}
			cumpleMontoProyecto = false;
			return false;                     
        }                
        
        this.montoTotal=Double.valueOf(x);        
        if (montoTotal >= this.montoTotal) {
        	cumpleMontoProyecto=true;
            return true;
        }
        cumpleMontoProyecto=false;
        return false;
    }
	
	public void obtenerEntesAcreditados() {
		try {
			if (transaccionFinanciera.getInstitucionFinanciera() != null
					&& transaccionFinanciera.getInstitucionFinanciera()
							.getCodigoInstitucion().equals("2")) {
				eligioEnte = true;
				entesAcreditados = institucionFinancieraFacade
						.obtenerEntesAcreditados();
				RequestContext.getCurrentInstance().update(
						":#{p:component('entidadBancaria')}");
				if (entesAcreditados == null) {
					JsfUtil.addMessageError("No se obtuvieron entes acreditados disponibles, intente más tarde nuevamente");
				}
			} else {
				eligioEnte = false;
			}
		} catch (Exception e) {
			LOG.error("Error en PagoServiciosBean", e);
			JsfUtil.addMessageError("No se obtuvieron entes acreditados disponibles, intente más tarde nuevamente");
		}
	}
	
	public void generarNUT() throws Exception{	
		
		List<NumeroUnicoTransaccional> listNUTXTramite=new ArrayList<NumeroUnicoTransaccional>();
		NumeroUnicoTransaccional numeroUnicoTransaccional;
		TarifasNUT tarifasNUT= new TarifasNUT();
		SolicitudUsuario solicitudUsuario= new SolicitudUsuario();
		solicitudUsuario.setUsuario(loginBean.getUsuario());
		
		String codigoTramite="";
		
		codigoTramite=tramite;
		 
		listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoTramite);
		if(listNUTXTramite!=null && listNUTXTramite.size()>0){			
			for (NumeroUnicoTransaccional nut : listNUTXTramite) {
				if(nut.getEstadosNut().getId()==5){
					nut.setNutFechaActivacion(new Date());
					nut.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
					nut.setEstadosNut(new EstadosNut(2));
					crudServiceBean.saveOrUpdate(nut);
					JsfUtil.addMessageWarning("Su trámite está caducado, se activo nuevamente por 3 días, por favor revisar.");
				}
			}
			JsfUtil.addMessageWarning("Usted ya tiene generado un Número Único de Trámite, por favor descargue en documentos del trámite.");
			return;
		}
		
		Tarifas tarifa = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.REGISTRO_GENERADOR_DESECHOS.getDescripcion());
		if(tarifa == null) {
			JsfUtil.addMessageError("Ocurrió un error al generar el NUT. Por favor comunicarse con mesa de ayuda.");
			return;
		}
		
		solicitudUsuario.setSolicitudDescripcion("Pagos por el trámite: "+codigoTramite);
		solicitudUsuario.setSolicitudCodigo(JsfUtil.getBean(GenerarNUTController.class).generarCodigoSolicitud());
		
		crudServiceBean.saveOrUpdate(solicitudUsuario);
		Integer numeroDocumento=1;
		numeroUnicoTransaccional = new NumeroUnicoTransaccional();
		numeroUnicoTransaccional.setNutCodigo(secuenciasFacade.getNextValueDedicateSequence("NUT_CODIGO", 10));
		numeroUnicoTransaccional.setNutFechaActivacion(new Date());
		numeroUnicoTransaccional.setEstadosNut(new EstadosNut(2));
		numeroUnicoTransaccional.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
		numeroUnicoTransaccional.setSolicitudUsuario(solicitudUsuario);
		numeroUnicoTransaccional.setCuentas(new Cuentas(1));
		numeroUnicoTransaccional.setNutValor(montoTotalProyecto);
		numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
		crudServiceBean.saveOrUpdate(numeroUnicoTransaccional);

		tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
		tarifasNUT.setTarifas(tarifa);

		tarifasNUT.setCantidad(1);
		tarifasNUT.setValorUnitario(montoTotalProyecto);
		crudServiceBean.saveOrUpdate(tarifasNUT);
		JsfUtil.getBean(GenerarNUTController.class).setSolicitudUsuario(solicitudUsuario);
		byte[] contenidoDocumento = JsfUtil.getBean(GenerarNUTController.class).generarDocumentoNutRcoa(numeroUnicoTransaccional,solicitudUsuario, numeroDocumento);

		DocumentosRgdRcoa documentoPago = new DocumentosRgdRcoa();
		documentoPago.setContenidoDocumento(contenidoDocumento);
		documentoPago.setMime("application/pdf");
		documentoPago.setIdTable(solicitudUsuario.getId());
		documentoPago.setNombreTabla("NUT RECAUDACIONES");
		documentoPago.setNombre("ComprobantePago" + numeroDocumento + ".pdf");
		documentoPago.setExtesion(".pdf");
		documentoPago = documentosRgdRcofacade.guardarDocumentoAlfrescoSinProyecto(
						numeroUnicoTransaccional.getNutCodigoProyecto() != null ? numeroUnicoTransaccional
								.getNutCodigoProyecto() : solicitudUsuario
								.getSolicitudCodigo(), "RECAUDACIONES", null, documentoPago,
						TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);

		numeroDocumento++;	
		List<String>listaArchivos= new ArrayList<String>();
		listaArchivos=JsfUtil.getBean(GenerarNUTController.class).getListPathArchivos();
		
		JsfUtil.getBean(GenerarNUTController.class).enviarNotificacionPago(solicitudUsuario.getUsuario(),codigoTramite,listaArchivos);
				
		JsfUtil.addMessageInfo("Se ha enviado un correo electrónico con los comprobantes para realizar el pago.");
	}
	
	public void guardarTransaccion(Integer pagoProyecto) {		
		
		if (transaccionFinanciera.getInstitucionFinanciera() != null
				&& transaccionFinanciera.getNumeroTransaccion() != "") {
			try {
				//recaudaciones
				String codigoTramite = "";
				if (proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null) {
					codigoTramite = proyectosBean.getProyecto().getCodigo();
				} else {
					codigoTramite = bandejaTareasBean.getTarea().getProcedure();
				}
				//fin recaudaciones
				//verifico si tiene un pago liberado por el numero de tramite
				boolean existePagoLiberado = false;
				List<ProyectosConPagoSinNut> ListProyectoNoNut = proyectosConPagoSinNutFacade.buscarPorProyectoPorUsuarioPorCodigoProyecto(JsfUtil.getLoggedUser(), tramite);
				if(ListProyectoNoNut != null){
					for (ProyectosConPagoSinNut objProyectoNoNut : ListProyectoNoNut) {
						if(objProyectoNoNut.getNumeroTramite().equals(transaccionFinanciera.getNumeroTransaccion())){
							existePagoLiberado = true;
							break;
						}
					}
					if(!existePagoLiberado){
						JsfUtil.addMessageError("El número de transacción ingresado no corresponde al pago liberado del proyecto "+tramite+".");
						return;
					}
				}
				// verifico si tiene un pago liberado por el numero de transaccion
				if(!existePagoLiberado){
					ListProyectoNoNut = proyectosConPagoSinNutFacade.buscarPorProyectoPorUsuarioPorNumero(transaccionFinanciera.getNumeroTransaccion());
					if(ListProyectoNoNut != null){
						//valido que sea del mismo operador
						for (ProyectosConPagoSinNut objProyectoNoNut : ListProyectoNoNut) {
							if(!objProyectoNoNut.getUsuario().equals(JsfUtil.getLoggedUser())){
								JsfUtil.addMessageError("El número de transacción ingresado no corresponde al operador del proyecto "+tramite+".");
								return;
							}
						}
						for (ProyectosConPagoSinNut objProyectoNoNut : ListProyectoNoNut) {
							if(objProyectoNoNut.getTipoProceso().equals(1)){
								existePagoLiberado = true;
								break;
							}
						}
						if(!existePagoLiberado){
							JsfUtil.addMessageError("El número de transacción ingresado corresponde al pago liberado de otro proyecto "+".");
							return;
						}
					}
				}
				if (existeTransaccion(transaccionFinanciera)) {
					JsfUtil.addMessageInfo("El número de comprobante: " + transaccionFinanciera.getNumeroTransaccion()
							+ " (" + transaccionFinanciera.getInstitucionFinanciera().getNombreInstitucion()
							+ ") ya fue registrado por usted. Ingrese otro distinto.");
					transaccionFinanciera = new TransaccionFinanciera();
					if(institucionesFinancieras != null && !institucionesFinancieras.isEmpty()){
						transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
					}
					return;
				} else {
					double monto = transaccionFinancieraFacade.consultarSaldo(transaccionFinanciera
							.getInstitucionFinanciera().getCodigoInstitucion(), transaccionFinanciera
							.getNumeroTransaccion());

					if (monto == 0) {
						JsfUtil.addMessageError("El número de comprobante: "
								+ transaccionFinanciera.getNumeroTransaccion() + " ("
								+ transaccionFinanciera.getInstitucionFinanciera().getNombreInstitucion()
								+ ") no ha sido registrado.");
						transaccionFinanciera = new TransaccionFinanciera();
						if(institucionesFinancieras != null && !institucionesFinancieras.isEmpty()){
							transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
						}
						return;
					} else {
						transaccionFinanciera.setMontoTransaccion(monto);
						transaccionFinanciera.setTipoPagoProyecto((pagoProyecto==1)?"Proyecto":"Cobertura");
						if (this.identificadorMotivo == null) {
							if(proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null){
								transaccionFinanciera.setProyecto(proyectosBean.getProyecto());
							}else if(proyectosBean.getProyectoRcoa() != null){
								transaccionFinanciera.setProyectoRcoa(proyectosBean.getProyectoRcoa());
							}							
							transaccionFinanciera.setIdentificadorMotivo(null);
						} else {
							transaccionFinanciera.setProyecto(null);
							transaccionFinanciera.setIdentificadorMotivo(this.identificadorMotivo);
						}
						transaccionesFinancieras.add(transaccionFinanciera);
						cumpleMonto = cumpleMonto();
						transaccionFinanciera = new TransaccionFinanciera();
						if(institucionesFinancieras != null && !institucionesFinancieras.isEmpty()){
							transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
						}
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
	
	
	private boolean existeTransaccion(TransaccionFinanciera _transaccionFinanciera) {

		for (TransaccionFinanciera transaccion : transaccionesFinancieras) {
			if (transaccion.getNumeroTransaccion().trim().equals(_transaccionFinanciera.getNumeroTransaccion())
					&& transaccion.getInstitucionFinanciera().getCodigoInstitucion().trim()
							.equals(_transaccionFinanciera.getInstitucionFinanciera().getCodigoInstitucion())) {
				return true;
			}
		}
		return false;
	}
	
	public double Monto() {
		double montoTotal = 0;
		for (TransaccionFinanciera transa : transaccionesFinancieras) {
			montoTotal += transa.getMontoTransaccion();
		}
		return montoTotal;
	}

	public void eliminarTransacion(TransaccionFinanciera transaccion) throws Exception {
		transaccionesFinancieras.remove(transaccion);
		cumpleMontoProyecto = cumpleMonto();		
	}	
	
	public String completarTarea() {
		this.montoTotal=montoTotalProyecto;
		
		DecimalFormat decimalValorTramite= new DecimalFormat(".##");
        String x="";
       
        x=decimalValorTramite.format(this.montoTotal).replace(",",".");
        
        this.montoTotal=Double.valueOf(x);
		
		if(montoTotalProyecto>0){
			List<TransaccionFinanciera>listTransProyecto= new ArrayList<TransaccionFinanciera>();
			for (TransaccionFinanciera transaccionFinanciera : getTransaccionesFinancieras()) {
				listTransProyecto.add(transaccionFinanciera);
			}
			if(listTransProyecto.size()==0 || !cumpleMontoProyecto){
				JsfUtil.addMessageError("Debe registrar un pago para el proyecto antes de continuar.");
				return null;
			}
		}
				
		if (getTransaccionesFinancieras().size() == 0) {
			JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
			return null;
		}

		if (!getCumpleMonto()) {
			JsfUtil.addMessageError("El monto de la transacción utilizada es insuficiente para completar el pago de tasas.");
			return null;
		}				

		double montoTotal = Monto();
		if (montoTotal > this.montoTotal) {
			RequestContext.getCurrentInstance().execute("PF('dlg1V2').show();");
		} else if (montoTotal == this.montoTotal) {
			executeBusinessLogic();
		}
		return null;
	}
	
	public void mostrarDialogo() {
		RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");
	}
	
	public void executeBusinessLogic() {
		if(persist) {
		String motivo = this.identificadorMotivo == null ? 
				((proyectosBean.getProyecto() == null || proyectosBean.getProyecto().getCodigo() == null)  ? proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() : proyectosBean.getProyecto().getCodigo())
			: this.identificadorMotivo;
						
		List<TransaccionFinanciera> transaccionesFinancierasProyecto= new ArrayList<TransaccionFinanciera>();
		for (TransaccionFinanciera transaccionFinanciera : getTransaccionesFinancieras()) {
			transaccionesFinancierasProyecto.add(transaccionFinanciera);
		}
		boolean pagoSatisfactorio=false;
		if(proyectosBean.getProyecto()!=null && proyectosBean.getProyecto().getCodigo() != null && !proyectosBean.getProyecto().isAreaResponsableEnteAcreditado() && transaccionesFinancierasProyecto.size()>0){
			if(montoTotalProyecto!=montoTotal){
				montoTotalProyecto=montoTotal;
			}
		}						
				
		if(transaccionesFinancierasProyecto.size()>0){
			pagoSatisfactorio=transaccionFinancieraFacade.realizarPago(montoTotalProyecto, transaccionesFinancierasProyecto,motivo);
			for (TransaccionFinanciera transaccionFinanciera : transaccionesFinancierasProyecto) {
				try {
					NumeroUnicoTransaccional nut=new NumeroUnicoTransaccional();
					nut=numeroUnicoTransaccionalFacade.buscarNUTPorNumeroTramite(transaccionFinanciera.getNumeroTransaccion());
					if(nut!=null){
						nut.setNutUsado(true);
						crudServiceBean.saveOrUpdate(nut);
					}
				} catch (ec.gob.ambiente.suia.exceptions.ServiceException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (pagoSatisfactorio) {
            transaccionFinancieraFacade.guardarTransacciones(getTransaccionesFinancieras(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getTaskName(),
                    bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getProcessId(),
                    bandejaTareasBean.getTarea().getProcessName());
			JsfUtil.addMessageInfo("Pago satisfactorio.");
			if(finalizar())
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} else {
			JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
		}
		
		}else{
			JsfUtil.addMessageInfo("Pago satisfactorio.");
			if(finalizar())
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}
	}
	
	private boolean finalizar(){
		try {
			String rolDirector = "";
			Usuario usuarioAutoridad;
			String autoridad = "";
			Area areaTramite = new Area();
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
				usuarioAutoridad = areaFacade.getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental");
				if(usuarioAutoridad != null && usuarioAutoridad.getId() != null){
					autoridad = usuarioAutoridad.getNombre();
				}else{
					JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
					System.out.println("No existe usuario " + Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental") );
					return false;
				}
			}else{
				// si es proyectos de rcoa
				if(proyecto != null && proyecto.getId() != null){
					if (proyecto.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
						UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyecto.getIdCantonOficina());
						areaTramite = ubicacion.getAreaCoordinacionZonal().getArea();
					} else{
						areaTramite = (proyecto.getAreaInventarioForestal().getArea() != null) ? proyecto.getAreaInventarioForestal().getArea(): proyecto.getAreaInventarioForestal();
					}
				}else if(proyectoSuia != null && proyectoSuia.getId() != null){
					// si es proyectos suia busco la direccion zonal en base a la ubicacion geografica y galapagos
					if(proyectoSuia.getProyectoUbicacionesGeograficas() != null && proyectoSuia.getProyectoUbicacionesGeograficas().size() > 0){
						UbicacionesGeografica ubicacion = proyectoSuia.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
						if(proyectoSuia.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
							areaTramite = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
						}else{if(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
									areaTramite = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
								} else {
									areaTramite = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
								}
							}
						}
					}

				// si es proyecto digitalizado busco el area del proyecto digitalizado
				AutorizacionAdministrativaAmbiental autorizacionAdministrativa = new AutorizacionAdministrativaAmbiental();
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
							JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
							System.out.println("No existe usuario " + rolDirector + " para el area " + areaTramite.getAreaName());						
							return false;
						}
					} catch (Exception e) {
						JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
						System.out.println("No existe usuario " + rolDirector + " para el area " + areaTramite.getAreaName());
						return false;
					}
				}else{
					JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
					System.out.println("No existe el area " + areaTramite);					
					return false;
				}
			}

			Map<String, Object> params=new HashMap<>();


			params.put("realizoPago", true);
			params.put("director", autoridad);
					
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			//busco si era un pago liberado
			for (TransaccionFinanciera transaccionFinanciera : getTransaccionesFinancieras()) {
				ProyectosConPagoSinNut objProyectoNoNut = proyectosConPagoSinNutFacade.buscarPorProyectoPorUsuarioPorNumero(tramite, JsfUtil.getLoggedUser(), 0L, transaccionFinanciera.getNumeroTransaccion());
				if(objProyectoNoNut != null){
					proyectosConPagoSinNutFacade.guardarNUT(objProyectoNoNut);
					objProyectoNoNut.setNumeroTramiteUsado(true);
					objProyectoNoNut.setProcessId(bandejaTareasBean.getProcessId());
					objProyectoNoNut.setFechaNumeroTramiteUsado(new Date());
					proyectosConPagoSinNutFacade.guardarNUT(objProyectoNoNut);
				}
			}
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public StreamedContent descargar(DocumentosRgdRcoa documento) {
		DefaultStreamedContent content = null;
		try {
			if (documento.getIdAlfresco() != null) {
				byte[] documentoContent = documentosRgdRcofacade.descargar(documento
						.getIdAlfresco());

				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}	
	
	public void validarTareaBpm() {
//        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/v2/pagoServicios.jsf");
    }


	

}
