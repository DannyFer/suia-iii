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
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DeclaracionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DeclaracionSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;


@ManagedBean
@ViewScoped
public class DeclaracionPagoRSQV1Controller {
	
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
	private List<InstitucionFinanciera> institucionesFinancieras;
	
	@Getter
	@Setter
	private List<TransaccionFinanciera> transaccionesFinancieras;

	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinanciera;

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
	private String tramite;


	@PostConstruct
	public void initData() throws Exception {
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
		transaccionFinanciera = new TransaccionFinanciera();		
		transaccionesFinancieras = new ArrayList<>();
		
		institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC();
		
		if(institucionesFinancieras != null && institucionesFinancieras.size() > 0)
			transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
				
		transaccionesFinancieras = transaccionFinancieraFacade.cargarTransaccionesRcoa(rsq.getId(),
						bandejaTareasBean.getTarea().getProcessInstanceId(),
						bandejaTareasBean.getTarea().getTaskId());
		
		if(transaccionesFinancieras != null && !transaccionesFinancieras.isEmpty())
			cumpleMonto();	
	}
	
	public String getMesNombre(int mesDeclaracion) {
    	return JsfUtil.devuelveMes(mesDeclaracion-1);
	}
	
	
	public void guardarTransaccion() {
			
		if (transaccionFinanciera.getInstitucionFinanciera() != null
				&& transaccionFinanciera.getNumeroTransaccion() != "") {
			try {			
				
				if (existeTransaccion(transaccionFinanciera)) {
					JsfUtil.addMessageInfo("El número de comprobante: " + transaccionFinanciera.getNumeroTransaccion()
							+ " (" + transaccionFinanciera.getInstitucionFinanciera().getNombreInstitucion()
							+ ") ya fue registrado por usted. Ingrese otro distinto.");
					reiniciarTransaccion();
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
						reiniciarTransaccion();
						return;
					} else {
						transaccionFinanciera.setMontoTransaccion(monto);
						transaccionFinanciera.setTipoPagoProyecto("Declaracion Sustancias Quimicas");
						transaccionFinanciera.setProyectoRcoa(rsq.getProyectoLicenciaCoa());
						transaccionFinanciera.setIdentificadorMotivo("Pago Multa Declaraciones Atrasadas");
						
						transaccionesFinancieras.add(transaccionFinanciera);
						cumpleValorTotal = cumpleMonto();
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
		transaccionFinanciera = new TransaccionFinanciera();
		if(institucionesFinancieras != null && institucionesFinancieras.size() > 0)
			transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
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

	private boolean cumpleMonto() {
        double montoTotal = 0;
        for (TransaccionFinanciera transa : transaccionesFinancieras) {
        	montoTotal += transa.getMontoTransaccion();
        }
       
        DecimalFormat decimalValorTramite= new DecimalFormat(".##");
        String x="";
       
		x = decimalValorTramite.format(this.valorTotalAPagar).replace(",", ".");

		this.valorTotalAPagar = Double.valueOf(x);
		if (montoTotal >= this.valorTotalAPagar) {
			cumpleValorTotal = true;
			return true;
		}
		cumpleValorTotal = false;
		return false;
    }
	
	public void eliminarTransacion(TransaccionFinanciera transaccion) throws Exception {
		transaccionesFinancieras.remove(transaccion);
		cumpleValorTotal = cumpleMonto();
	}
	
	public void completarTarea(){
		try {
			
			if (transaccionesFinancieras.size() == 0) {
				JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
				return;
			}

			if (!cumpleValorTotal) {
				JsfUtil.addMessageError("El monto de la transacción utilizada es insuficiente para completar el pago de tasas.");
				return;
			}
			
			if (realizarPago()) {
	            transaccionFinancieraFacade.guardarTransacciones(transaccionesFinancieras,
	                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getTaskName(),
	                    bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getProcessId(),
	                    bandejaTareasBean.getTarea().getProcessName());
	            
//	            for (DeclaracionSustanciaQuimica dsq : listaDeclaracionesPendientes) {
//					dsq.setPagoPendiente(false);
//					declaracionSustanciaQuimicaFacade.guardar(dsq, JsfUtil.getLoggedUser());
//				}
	            
//	            ultimaDeclaracion.setPagoPendiente(false);
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
		pagoSatisfactorio = transaccionFinancieraFacade.realizarPago(valorTotalAPagar, transaccionesFinancieras, rsq.getNumeroAplicacion());		
		return pagoSatisfactorio;
	}
	
}