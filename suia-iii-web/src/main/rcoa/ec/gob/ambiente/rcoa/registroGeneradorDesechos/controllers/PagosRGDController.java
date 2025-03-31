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
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.PagoKushkiJson;
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
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.recaudaciones.bean.PagoRcoaBean;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
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

@ManagedBean	
@ViewScoped
public class PagosRGDController {

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
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;		
	

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
	@ManagedProperty(value = "#{pagoRcoaBean}")
	private PagoRcoaBean pagoRcoaBean;
	
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
	
	/*Ubicacion Geografica del Proyecto*/
	@Getter
	@Setter
	List<UbicacionesGeografica> ubicacionesSeleccionadas;

	private UbicacionesGeografica data_localizacion = new UbicacionesGeografica();		
	
	@PostConstruct
	public void init() throws JbpmException {
		String identificadorMotivo=null;
		String url_redirec = "/pages/rcoa/generadorDesechos/realizarPagosrgd.jsf";
		pagoRcoaBean.setUrl_enlace(url_redirec);
		urlVerTramite ="/pages/rcoa/generadorDesechos/informacionRegistroGeneradorVer.jsf";
		variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
		tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
		tipoPermisoRGD =(String)variables.get(Constantes.VARIABLE_TIPO_RGD);
		proyectoId = Integer.valueOf(((String)variables.get("idProyectoDigitalizacion") == null)?"0":(String)variables.get("idProyectoDigitalizacion"));
		registroGenerador = new RegistroGeneradorDesechosRcoa();
		if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
			datosOperadorRgdBean.setTitulo("Responsable o representante de la empresa");
			registroGenerador = registroGeneradorDesechosRcoaFacade.buscarRGDPorCodigo(tramite);
			identificadorMotivo = tramite;
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
			/*mensaje = "El pago corresponde a la tasa por concepto de Registro Generador de Residuos o "
					+ " Desechos Peligrosos y/o Especiales, con un valor de 180 USD que incluye a una "
					+ "actividad; y adicionalmente, 50 USD por cada una de las actividades agrupadas "
					+ "bajo un mismo Registro Generador. Estos valores serán depositados en la cuenta "
					+ "corriente No. 3001480620 sublínea No. 190499 a nombre del "+NOMBREMINISTERIO.toUpperCase()+".";*/
			
			mensaje = "El pago corresponde a la tasa por concepto de Registro Generador de Residuos o "
					+ " Desechos Peligrosos y/o Especiales, se lo debe realizar por medio de la opción de Pago en Línea del Ministerio del Ambiente, Agua y Transición Ecológica.";	
			
			valorAdicional=true;
			adicional=registroGeneradorDesechosProyectosRcoas.size();
			montoTotaladicional=(montoAdicional*adicional);
			montoTotal=montounico+montoTotaladicional;
		}else{
			/*mensaje = "El pago corresponde a la tasa por concepto de Registro Generador de Residuos o "
				+ " Desechos Peligrosos y/o Especiales, con un valor de 180 USD, los cuales deben "
				+ " ser depositados en BANEcuador, a la cuenta No.3001480620 Sublínea 190499  "
				+ "a nombre del "+NOMBREMINISTERIO.toUpperCase()+"";*/
			mensaje = "El pago corresponde a la tasa por concepto de Registro Generador de Residuos o "
					+ " Desechos Peligrosos y/o Especiales, se lo debe realizar por medio de la opción de Pago en Línea del Ministerio del Ambiente, Agua y Transición Ecológica.";	

			if (registroGeneradorDesechosProyectosRcoas.size()>0){
				registroGenerador = registroGeneradorDesechosProyectosRcoas.get(0).getRegistroGeneradorDesechosRcoa();
			}
			CatalogoRgdRcoa catalogoMensaje = catalogoRgdRcoaFacade.findByCodigo("rgd_mensaje_pago_registro");	
			mensaje = catalogoMensaje.getDescripcion();
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
				/*mensaje = "El pago correspondiente a la tasa por concepto de Registro de generador de residuos y desechos peligrosos y/o especiales por"
						+ " aplicación de Responsabilidad Extendida del Productor (REP), tiene un valor 180 USD, los cuales se deben depositar en BAN Ecuador, "
						+ "a la cuenta corriente No 3001480620 sublínea 190499 a nombre del "+NOMBREMINISTERIO.toUpperCase()+".";*/
				
				mensaje = "El pago correspondiente a la tasa por concepto de Registro de generador de residuos y desechos peligrosos y/o especiales por"
				+ " aplicación de Responsabilidad Extendida del Productor (REP), tiene un valor 180 USD, se lo debe realizar por medio de la opción de Pago en Línea del Ministerio del Ambiente, Agua y Transición Ecológica.";
			}
		}

		if (registroGenerador != null && registroGenerador.getUsuario() != null){
			datosOperadorRgdBean.buscarDatosOperador(registroGenerador.getUsuario());
			proyectosBean.setProponente(datosOperadorRgdBean.getDatosOperador().getNombre());
		}
		
		String codigoTramite=tramite;

		GenerarNUTController nutController = JsfUtil.getBean(GenerarNUTController.class); 
		List<DocumentoNUT> documentosNUTOtraTarea = new ArrayList<DocumentoNUT>();
		List<DocumentoNUT> documentosNUTAux = nutController.obtenerDocumentosNut(tramite);
		
		
		if (documentosNUTAux == null)
			documentosNUTAux = new ArrayList<DocumentoNUT>();
		for (DocumentoNUT documentoNUT : documentosNUTAux) {
			if(!documentoNUT.getIdProceso().equals(bandejaTareasBean.getProcessId())){
				documentosNUTOtraTarea.add(documentoNUT);
			}
		}
		//elimino los documentos de otras tareas
		if(documentosNUTOtraTarea.size() > 0){
			documentosNUTAux.removeAll(documentosNUTOtraTarea);
		}
		pagoRcoaBean.setDocumentosNUT(documentosNUTAux);
		pagoRcoaBean.setMontoTotalProyecto(montoTotal);
		pagoRcoaBean.setGenerarNUT(true);
		pagoRcoaBean.setEsEnteAcreditado(false);
		pagoRcoaBean.setValidaNutKushki(true);
		pagoRcoaBean.setCumpleMontoProyecto(false);
		List<PagoKushkiJson> PagoKushkiJsonAux = nutController.listPagosKushki(tramite);
		if (PagoKushkiJsonAux != null)
		{
			pagoRcoaBean.setPagoKushkiJson(PagoKushkiJsonAux);
		} 
		if (pagoRcoaBean.getPagoKushkiJson() != null)
		{
			pagoRcoaBean.setValidaNutKushki(false);
		} 		
		pagoRcoaBean.setValorAPagar(montoTotal);
		pagoRcoaBean.setTramite(tramite);
		pagoRcoaBean.setTipoProyecto("RGD");
		//pagoRcoaBean.setDireccion(proyectosBean.getProyectoRcoa().getDireccionProyecto());
		if (registroGenerador.getId() != null)
		{
			pagoRcoaBean.setIdproy(registroGenerador.getId());	
		}
		/*try {
			cargarUbicacionProyecto();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	*/	
		//pagoRcoaBean.setCiudad(data_localizacion.getUbicacionesGeografica().getNombre().toString());
		if (proyectosBean.getUbicacionProponente().size() > 0 && proyectosBean.getUbicacionProponente().get(0).getUbicacionesGeografica().getNombre() != null)
		{
			pagoRcoaBean.setCiudad(proyectosBean.getUbicacionProponente().get(0).getUbicacionesGeografica().getNombre().toString());
			pagoRcoaBean.setProvincia(proyectosBean.getUbicacionProponente().get(0).getUbicacionesGeografica().getUbicacionesGeografica().getNombre().toString());			
		}
		//pagoRcoaBean.setPais(data_localizacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre().toString());				
		pagoRcoaBean.setIdentificadorMotivo(identificadorMotivo);
		pagoRcoaBean.setTransaccionFinanciera(new TransaccionFinanciera());
		pagoRcoaBean.setInstitucionesFinancieras(institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC());
		//nutController.generarNUT();
		if(codigoTramite==null || pagoRcoaBean.getMontoTotalProyecto() <= 0){
			pagoRcoaBean.setGenerarNUT(false);
		}
		
		if(pagoRcoaBean.getInstitucionesFinancieras() != null && !pagoRcoaBean.getInstitucionesFinancieras().isEmpty()){
			pagoRcoaBean.getTransaccionFinanciera().setInstitucionFinanciera(pagoRcoaBean.getInstitucionesFinancieras().get(0));
		}		
		
		if (pagoRcoaBean.getIdentificadorMotivo() == null){
			if(proyecto != null && proyecto.getId() != null){
				pagoRcoaBean.setTransaccionesFinancieras(transaccionFinancieraFacade
						.cargarTransaccionesRcoa(proyectosBean.getProyectoRcoa()
								.getId(), bandejaTareasBean.getTarea()
								.getProcessInstanceId(), bandejaTareasBean
								.getTarea().getTaskId()));
			}else if(proyectoSuia != null && proyectoSuia.getId() != null){
				pagoRcoaBean.setTransaccionesFinancieras(transaccionFinancieraFacade
						.cargarTransacciones(bandejaTareasBean.getTarea()
								.getProcessInstanceId(), bandejaTareasBean
								.getTarea().getTaskId()));
			}
		}else{
			pagoRcoaBean.setTransaccionesFinancieras(transaccionFinancieraFacade.cargarTransacciones(pagoRcoaBean.getIdentificadorMotivo()));
		}
		
		if(pagoRcoaBean.getTransaccionesFinancieras().isEmpty())
			persist = true;
		
		if(pagoRcoaBean.getTransaccionesFinancieras().size()>0){
			pagoRcoaBean.setCumpleMontoProyecto(nutController.cumpleMonto());
		}
		nutController.cerrarMensaje();
	}
	
	public double Monto() {
		double montoTotal = 0;
		for (TransaccionFinanciera transa : pagoRcoaBean.getTransaccionesFinancieras()) {
			montoTotal += transa.getMontoTransaccion();
		}
		return montoTotal;
	}
	
	/*public void cargarUbicacionProyecto() throws ServiceException {
		//UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyecto.getIdCantonOficina());
		ubicacionesSeleccionadas = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(registroGenerador.getId());
		data_localizacion = ubicacionesSeleccionadas.get(0);
	}*/
	
	
	public String completarTarea() {
		this.montoTotal=pagoRcoaBean.getMontoTotalProyecto();
		
		DecimalFormat decimalValorTramite= new DecimalFormat(".##");
        String x="";
       
        x=decimalValorTramite.format(this.montoTotal).replace(",",".");
        
        this.montoTotal=Double.valueOf(x);
		
		if(pagoRcoaBean.getMontoTotalProyecto()>0){
			List<TransaccionFinanciera>listTransProyecto= new ArrayList<TransaccionFinanciera>();
			for (TransaccionFinanciera transaccionFinanciera : pagoRcoaBean.getTransaccionesFinancieras()) {
				listTransProyecto.add(transaccionFinanciera);
			}
			if(listTransProyecto.size()==0 || !pagoRcoaBean.isCumpleMontoProyecto()){
				JsfUtil.addMessageError("Debe registrar un pago para el proyecto antes de continuar.");
				return null;
			}
		}
				
		if (pagoRcoaBean.getTransaccionesFinancieras().size() == 0) {
			JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
			return null;
		}

		if (!pagoRcoaBean.isCumpleMontoProyecto()) {
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
		String motivo = pagoRcoaBean.getIdentificadorMotivo() == null ? 
				((proyectosBean.getProyecto() == null || proyectosBean.getProyecto().getCodigo() == null)  ? proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() : proyectosBean.getProyecto().getCodigo())
			: pagoRcoaBean.getIdentificadorMotivo();
						
		List<TransaccionFinanciera> transaccionesFinancierasProyecto= new ArrayList<TransaccionFinanciera>();
		for (TransaccionFinanciera transaccionFinanciera : pagoRcoaBean.getTransaccionesFinancieras()) {
			transaccionesFinancierasProyecto.add(transaccionFinanciera);
		}
		boolean pagoSatisfactorio=false;
		if(proyectosBean.getProyecto()!=null && proyectosBean.getProyecto().getCodigo() != null && !proyectosBean.getProyecto().isAreaResponsableEnteAcreditado() && transaccionesFinancierasProyecto.size()>0){
			if(pagoRcoaBean.getMontoTotalProyecto()!=montoTotal){
				pagoRcoaBean.setMontoTotalProyecto(montoTotal);
			}
		}						
				
		if(transaccionesFinancierasProyecto.size()>0){
			pagoSatisfactorio=transaccionFinancieraFacade.realizarPago(pagoRcoaBean.getMontoTotalProyecto(), transaccionesFinancierasProyecto,motivo);
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
            transaccionFinancieraFacade.guardarTransacciones(pagoRcoaBean.getTransaccionesFinancieras(),
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
					// si es proyectos suia busco la direccion zonal en base a la ubicacion geografica
					if(proyectoSuia.getProyectoUbicacionesGeograficas() != null && proyectoSuia.getProyectoUbicacionesGeograficas().size() > 0){
						UbicacionesGeografica ubicacion = proyectoSuia.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
						if(proyectoSuia.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
							areaTramite = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
						}else{
							areaTramite = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
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
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void validarTareaBpm() {
//        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/v2/pagoServicios.jsf");
    }
}
