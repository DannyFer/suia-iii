package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.PagoKushkiJson;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DeclaracionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DeclaracionSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.bean.PagoRcoaBean;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;


@ManagedBean
@ViewScoped
public class DeclaracionPagoRSQController {
	
	@EJB
    private DeclaracionSustanciaQuimicaFacade declaracionSustanciaQuimicaFacade;

	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
	
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;	
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;	
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    
    @ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{pagoRcoaBean}")
	private PagoRcoaBean pagoRcoaBean;

	@Getter
	@Setter
	private String numeroTransaccion;
		
	@Getter
    @Setter
	private Tarifas tarifaSitema;
	
	@Getter
	@Setter
	public boolean cumpleValorTotal;
	
	@Getter
	@Setter
	public double valorTotalAPagar;

	@Getter
	@Setter
	private RegistroSustanciaQuimica rsq;
	
	private Map<String, Object> variables;
	
	@Getter	
	private List<DeclaracionSustanciaQuimica> listaDeclaracionesPendientes;
	
	@Getter
	@Setter
	private DeclaracionSustanciaQuimica ultimaDeclaracion;
	
	@Getter
	@Setter
	private List<String> mesesAtrasados;
	@Getter
	@Setter
	private List<CatalogoGeneralCoa> listaMeses;
	@Getter
	@Setter
	private String mensajeAdd;
	
	/*Ubicacion Geografica del Proyecto*/
	@Getter
	@Setter
	List<UbicacionesGeografica> ubicacionesSeleccionadas;

	private UbicacionesGeografica data_localizacion = new UbicacionesGeografica();	
	
	@Getter
	@Setter
	private String tramite;
	
	@PostConstruct
	public void initData() throws Exception {
		String url_redirec = "/pages/rcoa/sustanciasQuimicas/declaracion/realizarPago.jsf";
		pagoRcoaBean.setUrl_enlace(url_redirec);			
		variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());	
		String varTramite=(String)variables.get("tramite");
		tramite = varTramite;
				
		rsq = registroSustanciaQuimicaFacade.obtenerRegistroPorCodigo(varTramite);
		
		if(rsq != null && rsq.getId() != null){
			ultimaDeclaracion = declaracionSustanciaQuimicaFacade.obtenerUltimaDeclaracionPorRSQ(rsq);
			listaDeclaracionesPendientes = declaracionSustanciaQuimicaFacade.obtenerRSQPendientes(rsq);
		}else{
			ultimaDeclaracion = declaracionSustanciaQuimicaFacade.buscarPorTramite(varTramite);
			if(ultimaDeclaracion != null && ultimaDeclaracion.getId() != null)
				rsq = ultimaDeclaracion.getRegistroSustanciaQuimica();
			listaDeclaracionesPendientes = declaracionSustanciaQuimicaFacade.obtenerDeclaracionesPendientesPorRSQSustancia(rsq, ultimaDeclaracion.getSustanciaQuimica());
		}
			
		int numDeclaracion = listaDeclaracionesPendientes.size();
		
		if(listaDeclaracionesPendientes != null && !listaDeclaracionesPendientes.isEmpty() && listaDeclaracionesPendientes.get(numDeclaracion -1).getValorMulta() != null){
			valorTotalAPagar = listaDeclaracionesPendientes.get(numDeclaracion -1).getValorMulta();
		}
		listaMeses = new ArrayList<CatalogoGeneralCoa>();
		
		List<CatalogoGeneralCoa> listaMesesAux = catalogoCoaFacade.obtenerCatalogoOrden(CatalogoTipoCoaEnum.DSQ_MESES);
		
		for(DeclaracionSustanciaQuimica decPen : listaDeclaracionesPendientes){
			for (CatalogoGeneralCoa mes : listaMesesAux) {
				if(mes.getOrden()== decPen.getMesDeclaracion()){
					listaMeses.add(mes);
					break;
				}
			} 
		}

		cumpleValorTotal = false;
		
		String codigoTramite=varTramite;
		String identificadorMotivo = varTramite;
		GenerarNUTController nutController = JsfUtil.getBean(GenerarNUTController.class); 
		pagoRcoaBean.setDocumentosNUT(nutController.obtenerDocumentosNut(codigoTramite));
		pagoRcoaBean.setMontoTotalProyecto(valorTotalAPagar);
		pagoRcoaBean.setGenerarNUT(true);
		pagoRcoaBean.setEsEnteAcreditado(false);
		pagoRcoaBean.setValidaNutKushki(true);
		pagoRcoaBean.setCumpleMontoProyecto(false);
		pagoRcoaBean.setValorAPagar(valorTotalAPagar);
		pagoRcoaBean.setTramite(codigoTramite);
		pagoRcoaBean.setTipoProyecto("RSQ");
		mensajeAdd = pagoRcoaBean.getMensaje_Completo();
		List<PagoKushkiJson> PagoKushkiJsonAux = nutController.listPagosKushki(codigoTramite);
		if (PagoKushkiJsonAux != null)
		{
			pagoRcoaBean.setPagoKushkiJson(PagoKushkiJsonAux);
		} 
		if (pagoRcoaBean.getPagoKushkiJson() != null)
		{
			pagoRcoaBean.setValidaNutKushki(false);
		} 		
//		cargarUbicacionProyecto();		
//		pagoRcoaBean.setDireccion(proyectosBean.getProyectoRcoa().getDireccionProyecto());
//		pagoRcoaBean.setIdproy(proyectosBean.getProyectoRcoa().getId());
//		pagoRcoaBean.setCiudad(data_localizacion.getUbicacionesGeografica().getNombre().toString());
//		pagoRcoaBean.setProvincia(data_localizacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre().toString());
//		pagoRcoaBean.setPais(data_localizacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre().toString());		
		pagoRcoaBean.setIdentificadorMotivo(identificadorMotivo);
		pagoRcoaBean.setTransaccionFinanciera(new TransaccionFinanciera());
		pagoRcoaBean.setInstitucionesFinancieras(institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC());
		if(pagoRcoaBean.getInstitucionesFinancieras() != null && pagoRcoaBean.getInstitucionesFinancieras().size() > 0)
			pagoRcoaBean.getTransaccionFinanciera().setInstitucionFinanciera(pagoRcoaBean.getInstitucionesFinancieras().get(0));
		//nutController.generarNUT();
		if(codigoTramite==null || pagoRcoaBean.getMontoTotalProyecto() <= 0){
			pagoRcoaBean.setGenerarNUT(false);
		}
		
		if(pagoRcoaBean.getInstitucionesFinancieras() != null && pagoRcoaBean.getInstitucionesFinancieras().size() > 0){
			pagoRcoaBean.getTransaccionFinanciera().setInstitucionFinanciera(pagoRcoaBean.getInstitucionesFinancieras().get(0));
		}
		
		pagoRcoaBean.setTransaccionesFinancieras(transaccionFinancieraFacade.cargarTransaccionesRcoa(rsq.getId(),
						bandejaTareasBean.getTarea().getProcessInstanceId(),
						bandejaTareasBean.getTarea().getTaskId()));
		
		if(pagoRcoaBean.getTransaccionesFinancieras().size()>0){
			pagoRcoaBean.setCumpleMontoProyecto(nutController.cumpleMonto());
		}
		nutController.cerrarMensaje();
	}
	
	public void cargarUbicacionProyecto() {
		ubicacionesSeleccionadas = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectosBean.getProyectoRcoa());
		data_localizacion = ubicacionesSeleccionadas.get(0);
	}
	
	public String getMesNombre(int mesDeclaracion) {
    	return JsfUtil.devuelveMes(mesDeclaracion-1);
	}
	
	
	public void guardarTransaccion() {
			
		if (pagoRcoaBean.getTransaccionFinanciera().getInstitucionFinanciera() != null
				&& pagoRcoaBean.getTransaccionFinanciera().getNumeroTransaccion() != "") {
			try {			
				
				if (existeTransaccion(pagoRcoaBean.getTransaccionFinanciera())) {
					JsfUtil.addMessageInfo("El número de comprobante: " + pagoRcoaBean.getTransaccionFinanciera().getNumeroTransaccion()
							+ " (" + pagoRcoaBean.getTransaccionFinanciera().getInstitucionFinanciera().getNombreInstitucion()
							+ ") ya fue registrado por usted. Ingrese otro distinto.");
					reiniciarTransaccion();
					return;
				} else {
					double monto = transaccionFinancieraFacade.consultarSaldo(pagoRcoaBean.getTransaccionFinanciera()
							.getInstitucionFinanciera().getCodigoInstitucion(), pagoRcoaBean.getTransaccionFinanciera()
							.getNumeroTransaccion());

					if (monto == 0) {
						JsfUtil.addMessageError("El número de comprobante: "
								+ pagoRcoaBean.getTransaccionFinanciera().getNumeroTransaccion() + " ("
								+ pagoRcoaBean.getTransaccionFinanciera().getInstitucionFinanciera().getNombreInstitucion()
								+ ") no ha sido registrado.");
						reiniciarTransaccion();
						return;
					} else {
						pagoRcoaBean.getTransaccionFinanciera().setMontoTransaccion(monto);
						pagoRcoaBean.getTransaccionFinanciera().setTipoPagoProyecto("Declaracion Sustancias Quimicas");
						pagoRcoaBean.getTransaccionFinanciera().setProyectoRcoa(rsq.getProyectoLicenciaCoa());
						pagoRcoaBean.getTransaccionFinanciera().setIdentificadorMotivo("Pago Multa Declaraciones Atrasadas");
						
						pagoRcoaBean.getTransaccionesFinancieras().add(pagoRcoaBean.getTransaccionFinanciera());
						GenerarNUTController nutController = JsfUtil.getBean(GenerarNUTController.class); 
						nutController.cumpleMonto();
						reiniciarTransaccion();

						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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
	}
	
	private boolean existeTransaccion(TransaccionFinanciera _transaccionFinanciera) {

		for (TransaccionFinanciera transaccion : pagoRcoaBean.getTransaccionesFinancieras()) {
			if (transaccion.getNumeroTransaccion().trim().equals(_transaccionFinanciera.getNumeroTransaccion())
					&& transaccion.getInstitucionFinanciera().getCodigoInstitucion().trim()
							.equals(_transaccionFinanciera.getInstitucionFinanciera().getCodigoInstitucion())) {
				return true;
			}
		}
		return false;
	}
	
	public void eliminarTransacion(TransaccionFinanciera transaccion) throws Exception {
		pagoRcoaBean.getTransaccionesFinancieras().remove(transaccion);
		GenerarNUTController nutController = JsfUtil.getBean(GenerarNUTController.class); 
		nutController.cumpleMonto();
	}
	
	public void completarTarea(){
		try {
			
			if (pagoRcoaBean.getTransaccionesFinancieras().size() == 0) {
				JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
				return;
			}

			if (!cumpleValorTotal) {
				JsfUtil.addMessageError("El monto de la transacción utilizada es insuficiente para completar el pago de tasas.");
				return;
			}
			
			if (realizarPago()) {
	            transaccionFinancieraFacade.guardarTransacciones(pagoRcoaBean.getTransaccionesFinancieras(),
	                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getTaskName(),
	                    bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getProcessId(),
	                    bandejaTareasBean.getTarea().getProcessName());
	            
	            declaracionSustanciaQuimicaFacade.guardar(ultimaDeclaracion, JsfUtil.getLoggedUser());
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
				
			} else {
				JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
			}
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}
	
	private Boolean realizarPago() {
		Boolean pagoSatisfactorio = false;
		pagoSatisfactorio = transaccionFinancieraFacade.realizarPago(valorTotalAPagar, pagoRcoaBean.getTransaccionesFinancieras(), rsq.getNumeroAplicacion());		
		return pagoSatisfactorio;
	}
	
}
