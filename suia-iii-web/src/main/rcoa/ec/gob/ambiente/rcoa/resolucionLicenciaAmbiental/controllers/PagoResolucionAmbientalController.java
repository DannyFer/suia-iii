package ec.gob.ambiente.rcoa.resolucionLicenciaAmbiental.controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.UbicacionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.rcoa.dto.EntityDatosOperador;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.ResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ResolucionAmbiental;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.VinculoProyectoRgdSuiaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.BienesServiciosInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.PromedioInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.BienesServiciosInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.PromedioInventarioForestal;
import ec.gob.ambiente.rcoa.model.PagoKushkiJson;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.VinculoProyectoRgdSuia;
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
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.suia.administracion.controllers.CombosController;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoOrganizacion;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.recaudaciones.bean.PagoRcoaBean;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.model.DocumentoNUT;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validaPago.bean.PagoConfiguracionesBean;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class PagoResolucionAmbientalController {
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
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
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
	@EJB
	private ResolucionAmbientalFacade resolucionAmbientalFacade;	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private VinculoProyectoRgdSuiaFacade vinculoProyectoRgdSuiaFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoSuia;

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
	private Usuario usuarioOperador;
	
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
	private String tramite, tipoPermisoRGD, usuarioAmbiental,operador,tramiteProyecto, tramiteRGD;

	private Map<String, Object> variables;
	private String proyectoId;
	
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
	private ProyectoLicenciaCoa proyecto, proyectoTEMP;
	
	@Getter
	@Setter
	private RegistroGeneradorDesechosRcoa registroGenerador;
	
	/*Ubicacion Geografica del Proyecto*/
	@Getter
	@Setter
	List<UbicacionesGeografica> ubicacionesSeleccionadas;
	
	@PostConstruct
	public void init() throws ServiceException {
		try
		{
			if (bandejaTareasBean.getTarea() != null) {		
				variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
				operador = (String) variables.get("operador");
				tramite = (String) variables.get("tramite");
				usuarioOperador = new Usuario();
				usuarioOperador = usuarioFacade.buscarUsuarioCompleta(operador);
				proyectosBean.setProponente(usuarioOperador.getPersona().getNombre());
				datosOperadorRgdBean.buscarDatosOperador(usuarioOperador);
				ProyectoLicenciaCoa proyectoRLA = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
				tramiteProyecto = proyectoRLA.getCodigoUnicoAmbiental();				
				if (variables.get("autoridadAmbiental") != null)
				{
					usuarioAmbiental = (String) variables.get("autoridadAmbiental");
				}
			}
				
			ProyectoLicenciaCoa proyectoRLA = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			ResolucionAmbiental resolucionTemp = new ResolucionAmbiental();
			resolucionTemp = resolucionAmbientalFacade.getByCodigoUnicoAmbiental(proyectoRLA.getCodigoUnicoAmbiental());
			proyecto = proyectoRLA;			
//			GeneradorDesechosPeligrosos auxRegistro = new GeneradorDesechosPeligrosos();
//			VinculoProyectoRgdSuia obj = new VinculoProyectoRgdSuia();
//			obj = vinculoProyectoRgdSuiaFacade.getProyectoVinculadoRgd(proyecto.getId()); 
//			if ((obj != null) && (obj.getId() != null)) 
//			{
//				auxRegistro = generadorDesechosPeligrososFacade.GeneradorActivosVinculado(obj.getIdRgdSuia());
//				if ((auxRegistro!=null) && (auxRegistro.getId() != null))
//				{
//					//continua = true;
//				}				
//			}
			///zonal
			//////////////////////////////RCOA
			RegistroGeneradorDesechosRcoa objRCOA = new RegistroGeneradorDesechosRcoa();
			List<RegistroGeneradorDesechosProyectosRcoa> lista = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
			lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyecto.getId());
			if ((lista != null) && (lista.size() > 0))
			{
				objRCOA = lista.get(0).getRegistroGeneradorDesechosRcoa();
				registroGenerador = new RegistroGeneradorDesechosRcoa();
				if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
					datosOperadorRgdBean.setTitulo("Responsable o representante de la empresa");
					registroGenerador = registroGeneradorDesechosRcoaFacade.buscarRGDPorCodigo(objRCOA.getCodigo());
					urlVerTramite ="/pages/rcoa/generadorDesechos/informacionRegistroGeneradorVerREP.jsf";
				}					
			}
			String identificadorMotivo=null;
			String url_redirec = "pages/rcoa/resolucionLicenciaAmbiental/pagoResolucionAmbiental.jsf";
			pagoRcoaBean.setUrl_enlace(url_redirec);
			urlVerTramite ="/pages/rcoa/generadorDesechos/informacionRegistroGeneradorVer.jsf";
			tipoPermisoRGD =Constantes.TIPO_RGD_REP;
			List<RegistroGeneradorDesechosRcoa> registroGeneradorDesechosProyectosRcoas = new ArrayList<RegistroGeneradorDesechosRcoa>();
			if ((objRCOA != null) && (objRCOA.getId() != null))
			{		
				// si es proyecto rcoa
				if(proyecto != null && proyecto.getId() != null){
					registroGeneradorDesechosProyectosRcoas.add(objRCOA);
				}			
			}
			CatalogoRgdRcoa catalogoPago = catalogoRgdRcoaFacade.findByCodigo("rgd_valor_pago_registro");
			Double pago = Double.valueOf(catalogoPago.getDescripcion());
			this.montoTotal = pago;
			this.montoAdicional=50;	
			this.montounico=90; 		
			if (objRCOA != null && objRCOA.getId() != null){
				datosOperadorRgdBean.buscarDatosOperador(usuarioOperador);
				proyectosBean.setProponente(datosOperadorRgdBean.getDatosOperador().getNombre());
			}
			
			if (registroGeneradorDesechosProyectosRcoas.size()>1){
				valorAdicional=true;
				adicional=registroGeneradorDesechosProyectosRcoas.size();
				montoTotaladicional=(montoAdicional*adicional);
				montoTotal=montounico+montoTotaladicional;		
				mensaje = "El pago correspondiente a la tasa por concepto de emisión de Registro Generador de Residuos o "
						+ " Desechos Peligrosos y/o Especiales, se lo debe realizar por medio de la opción de Pago en Línea del Ministerio del Ambiente, Agua y Transición Ecológica.";	
			}
			else
			{
				mensaje = "El pago correspondiente a la tasa por concepto de emisión de Registro Generador de Residuos o "
						+ " Desechos Peligrosos y/o Especiales, se lo debe realizar por medio de la opción de Pago en Línea del Ministerio del Ambiente, Agua y Transición Ecológica.";	
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
			pagoRcoaBean.setTipoPago("");
			pagoRcoaBean.setIdproy(proyectosBean.getProyectoRcoa().getId());
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
			pagoRcoaBean.setDireccion(proyectosBean.getProyectoRcoa().getDireccionProyecto());
			List<UbicacionesGeografica> ubicacionProyectoLista = proyectoLicenciaCoaUbicacionFacade
					.buscarPorProyecto(proyectosBean.getProyectoRcoa());
			for (UbicacionesGeografica ubicacion : ubicacionProyectoLista) {
				pagoRcoaBean.setProvincia(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());					
				pagoRcoaBean.setCiudad(ubicacion.getNombre());
				pagoRcoaBean.setPais(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			}					
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
		catch (Exception e) {
e.printStackTrace();
		}
	}
	
	public double Monto() {
		double montoTotal = 0;
		for (TransaccionFinanciera transa : pagoRcoaBean.getTransaccionesFinancieras()) {
			montoTotal += transa.getMontoTransaccion();
		}
		return montoTotal;
	}
	
	public void cargarUbicacionProyecto() throws ServiceException {
		ubicacionesSeleccionadas = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectosBean.getProyectoRcoa());
	}
	
	
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
			{
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}
				
		} else {
			JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
		}
		
		}else{
			JsfUtil.addMessageInfo("Pago satisfactorio.");
			if(finalizar())
			{
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}
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
				if(Integer.valueOf(proyectoId) > 0){
					autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(Integer.valueOf(proyectoId));
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
			params.put("autoridadAmbiental", usuarioAmbiental);
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
