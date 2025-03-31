package ec.gob.ambiente.suia.prevencion.categoria2.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.TransaccionFinancieraLog;
import ec.gob.ambiente.suia.prevencion.categoria2.service.TransaccionFinancieraService;

@Stateless
public class TransaccionFinancieraFacade {
	
	@EJB
	private TransaccionFinancieraService transaccionFinancieraService;

	public List<TransaccionFinanciera> cargarTransacciones(Integer idProyecto){
        return transaccionFinancieraService.cargarTransacciones(idProyecto);
    }	

    public List<TransaccionFinanciera> cargarTransacciones(Integer idProyecto, long ProcessInstanceId, long TaskId){
        return transaccionFinancieraService.cargarTransacciones(idProyecto, ProcessInstanceId, TaskId);
    }
    
    public List<TransaccionFinanciera> cargarTransacciones(String identificadorMotivo){
        return transaccionFinancieraService.cargarTransacciones(identificadorMotivo);
    }
		
	public void guardarTransacciones(List<TransaccionFinanciera> transacciones, long idTarea, String nombreTarea,
                                     long idInstanciaProceso, String idProceso, String nombreProceso){
		transaccionFinancieraService.guardarTransacciones(transacciones, idTarea, nombreTarea, idInstanciaProceso,
                idProceso, nombreProceso);
	}
	
	public double consultarSaldo(String codigoEntidadFinanciera, String numeroTransaccion) throws Exception{
		return transaccionFinancieraService.consultarSaldo(codigoEntidadFinanciera, numeroTransaccion);
	}
	
	public boolean realizarPago(double valorPago, List<TransaccionFinanciera> transacciones, String identificadorMotivo){
		return transaccionFinancieraService.realizarPago(valorPago, transacciones, identificadorMotivo);
	}
	
	//Cris F: aumento de metodo para obtener pago para fecha en reporte
	public List<TransaccionFinancieraLog> buscarTransaccionFinancieraLog(int idTransaccion){
		return transaccionFinancieraService.buscarTransaccionFinancieraLog(idTransaccion);
	}
	
	public List<TransaccionFinanciera> cargarTransacciones(long ProcessInstanceId, long TaskId){
        return transaccionFinancieraService.cargarTransacciones(ProcessInstanceId, TaskId);
    }
	
	public List<TransaccionFinanciera> cargarTransaccionesRcoa(Integer idProyecto, long ProcessInstanceId, long TaskId){
        return transaccionFinancieraService.cargarTransaccionesRcoa(idProyecto, ProcessInstanceId, TaskId);
    }
	
	public List<TransaccionFinanciera> cargarTransaccionesRcoa(Integer idProyecto){
        return transaccionFinancieraService.cargarTransaccionesRcoa(idProyecto);
    }
	
	public List<TransaccionFinanciera> cargarTransaccionesRcoaPorProceso(Integer idProyecto, long processInstanceId){
        return transaccionFinancieraService.cargarTransaccionesRcoaPorProceso(idProyecto, processInstanceId);
    }

    public void eliminarTransacciones(List<TransaccionFinanciera> transacciones){
		transaccionFinancieraService.eliminarTransacciones(transacciones);
	}
    
    public String getCuentaTransaccion(String codigoEntidadFinanciera, String numeroTransaccion) {
		return transaccionFinancieraService.getCuentaTransaccion(codigoEntidadFinanciera, numeroTransaccion);
	}
    
    public Boolean revertirPago(String codigoProyecto, String numeroTramitePago, String ip, String codigoEntidadFinanciera, Double montoPago) {
		return transaccionFinancieraService.revertirPago(codigoProyecto, numeroTramitePago, ip, codigoEntidadFinanciera, montoPago);
	}
}
