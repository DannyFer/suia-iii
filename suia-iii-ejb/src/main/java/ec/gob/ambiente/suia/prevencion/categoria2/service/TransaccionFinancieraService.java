package ec.gob.ambiente.suia.prevencion.categoria2.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.TransaccionFinancieraLog;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.web.services.financial.financialws.controller.PaymentInVO;
import ec.gob.ambiente.suia.web.services.financial.financialws.controller.PaymentResponseVO;
import ec.gob.ambiente.suia.web.services.financial.financialws.controller.PaymentServiceInternal;
import ec.gob.ambiente.suia.web.services.financial.financialws.controller.PaymentServiceInternal_Service;

@Stateless
public class TransaccionFinancieraService {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public String dblinkSuiaVerde = Constantes.getDblinkSuiaVerde();

	@SuppressWarnings("unchecked")
	public List<TransaccionFinanciera> cargarTransacciones(Integer idProyecto, long ProcessInstanceId, long TaskId) {
		return crudServiceBean.getEntityManager()
				.createQuery("SELECT t From TransaccionFinancieraLog l inner join l.transaccionFinanciera t where t.proyecto.id = :idProyecto and l.idTarea = :idTarea and l.idInstanciaProceso = :idInstanciaProceso")
				.setParameter("idProyecto", idProyecto).setParameter("idTarea", TaskId).setParameter("idInstanciaProceso", ProcessInstanceId).getResultList();
	}
	//suia
	@SuppressWarnings("unchecked")
    public List<TransaccionFinanciera> cargarTransacciones(Integer idProyecto) {
        return crudServiceBean.getEntityManager()
                .createQuery("From TransaccionFinanciera t where t.proyecto.id = :idProyecto")
                .setParameter("idProyecto", idProyecto).getResultList();
    }
	//suia
	
	@SuppressWarnings("unchecked")
	public List<TransaccionFinanciera> cargarTransacciones(String identificadorMotivo) {
		return crudServiceBean.getEntityManager()
				.createQuery("From TransaccionFinanciera t where t.identificadorMotivo = :identificadorMotivo")
				.setParameter("identificadorMotivo", identificadorMotivo).getResultList();
	}

	public void guardarTransacciones(List<TransaccionFinanciera> transacciones, long idTarea, String nombreTarea,
                                     long idInstanciaProceso, String idProceso, String nombreProceso) {
		for (TransaccionFinanciera transaccion : transacciones) {
            //Se gurda la transaccion
			crudServiceBean.saveOrUpdate(transaccion);
            //Se guarda el log de la transaccion
            TransaccionFinancieraLog log = new TransaccionFinancieraLog();
            log.setIdInstanciaProceso(idInstanciaProceso);
            log.setIdTarea(idTarea);
            log.setNombreTarea(nombreTarea);
            log.setIdProceso(idProceso);
            log.setNombreProceso(nombreProceso);
            log.setTransaccionFinanciera(transaccion);
            crudServiceBean.saveOrUpdate(log);
		}
	}

	public double consultarSaldo(String codigoEntidadFinanciera, String numeroTransaccion) throws Exception {
		PaymentServiceInternal_Service service = new PaymentServiceInternal_Service();
		PaymentServiceInternal port = service.getPaymentServiceInternalPort();
		PaymentResponseVO response = port.consultarSaldo(codigoEntidadFinanciera, numeroTransaccion);
		if (response.getCode().trim().equals("1")) {
			return response.getBalance();
		} else {
			return 0;
		}
	}

	public boolean realizarPago(double valorPago, List<TransaccionFinanciera> transacciones,
			String identificadorMotivo) {
		try {
			List<PaymentInVO> transactionsIn = new ArrayList<PaymentInVO>();
			for (TransaccionFinanciera transaccion : transacciones) {
				PaymentInVO in = new PaymentInVO();
				in.setBankEntityCode(transaccion.getInstitucionFinanciera().getCodigoInstitucion());
				in.setTransactionNumber(transaccion.getNumeroTransaccion());
				transactionsIn.add(in);
			}

			PaymentServiceInternal_Service service = new PaymentServiceInternal_Service();
			PaymentServiceInternal port = service.getPaymentServiceInternalPort();
			port.realizarPago(transactionsIn, valorPago, identificadorMotivo);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	//Cris F: busqueda de log
	@SuppressWarnings("unchecked")
	public List<TransaccionFinancieraLog> buscarTransaccionFinancieraLog(int idTransaccion){
		try {
			return crudServiceBean.getEntityManager()
			.createQuery("SELECT t From TransaccionFinancieraLog t where t.transaccionFinanciera.id = :idTransaccion and t.estado = true")
			.setParameter("idTransaccion", idTransaccion).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<TransaccionFinanciera> cargarTransacciones(long ProcessInstanceId, long TaskId) {
		return crudServiceBean.getEntityManager()
				.createQuery("SELECT t From TransaccionFinancieraLog l inner join l.transaccionFinanciera t where l.idTarea = :idTarea and l.idInstanciaProceso = :idInstanciaProceso")
				.setParameter("idTarea", TaskId).setParameter("idInstanciaProceso", ProcessInstanceId).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<TransaccionFinanciera> cargarTransaccionesRcoa(Integer idProyecto, long ProcessInstanceId, long TaskId) {
		return crudServiceBean.getEntityManager()
				.createQuery("SELECT t From TransaccionFinancieraLog l inner join l.transaccionFinanciera t where t.proyectoRcoa.id = :idProyecto and l.idTarea = :idTarea and l.idInstanciaProceso = :idInstanciaProceso")
				.setParameter("idProyecto", idProyecto).setParameter("idTarea", TaskId).setParameter("idInstanciaProceso", ProcessInstanceId).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<TransaccionFinanciera> cargarTransaccionesRcoa(Integer idProyecto) {
		return crudServiceBean.getEntityManager()
				.createQuery("SELECT t From TransaccionFinanciera t where t.proyectoRcoa.id = :idProyecto")
				.setParameter("idProyecto", idProyecto).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<TransaccionFinanciera> cargarTransaccionesRcoaPorProceso(Integer idProyecto, long ProcessInstanceId) {
		return crudServiceBean.getEntityManager()
				.createQuery("SELECT t From TransaccionFinancieraLog l inner join l.transaccionFinanciera t "
						+ "where t.proyectoRcoa.id = :idProyecto and l.idInstanciaProceso = :idInstanciaProceso")
				.setParameter("idProyecto", idProyecto).setParameter("idInstanciaProceso", ProcessInstanceId).getResultList();
	}

	public void eliminarTransacciones(List<TransaccionFinanciera> transacciones) {
		for (TransaccionFinanciera transaccion : transacciones) {

			String sql =" UPDATE suia_iii.financial_transaction_log SET fitl_status=false WHERE fitr_id = "+transaccion.getId()+" ;";
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);

			transaccion.setEstado(false);
			crudServiceBean.saveOrUpdate(transaccion);
		}
	}
	
	@SuppressWarnings("unchecked")
	public String getCuentaTransaccion(String codigoEntidadFinanciera, String numeroTransaccion) {
		String sql="select * from dblink('"+dblinkSuiaVerde+"',"
				+ "'select account_number  "
				+ "from online_payment.online_payments "
				+ "where tramit_number=''" + numeroTransaccion+ "'' "
				+ "and bank_code = ''" + codigoEntidadFinanciera + "''"
				+ "order by 1 desc limit 1')"
				+ "as (account_number text)";
		Query queryPago = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object> result = (List<Object>) queryPago.getResultList();
		
		String nroCuenta = null;
		if (result.size() > 0) {
			nroCuenta = (String) result.get(0);
		}
		
		return nroCuenta;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean revertirPago(String codigoProyecto, String numeroTramitePago, String ip, String codigoEntidadFinanciera, Double montoPago) {
		try {
			String sql = "select * from dblink('" + dblinkSuiaVerde + "',"
					+ "'select h.id_online_payment_historical,h.online_payment_id,h.retired_value,h.tramit_number,p.used_value, h.value_updated  "
					+ "from online_payment.online_payments_historical h "
					+ "inner join online_payment.online_payments p on (p.id_online_payment=h.online_payment_id)  "
					+ "where h.tramit_number=''" + numeroTramitePago + "'' "
					+ "and p.bank_code = ''" + codigoEntidadFinanciera + "'' "
					+ "order by 1 desc limit 1') "
					+ "as (id_online_payment_historical integer,online_payment_id integer,retired_value text,tramit_number text,used_value text,value_updated text)";
			
			Query queryPago = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object[]> result = (List<Object[]>) queryPago.getResultList();
			if (result.size() > 0) {
				Object[] pagoHistorico = (Object[]) result.get(0);
				Integer idPago = (Integer) pagoHistorico[1];
				String nroComprobante = (String) pagoHistorico[3];
				String totalUsadoComprobante = (String) pagoHistorico[4];
				String saldoComprobante = (String) pagoHistorico[5];
	
				Double saldoComprobanteRevert = Double.valueOf(saldoComprobante) + montoPago ;
				
				String sqlInsert = "select dblink_exec('" + dblinkSuiaVerde + "'," 
						+ "'insert into online_payment.online_payments_historical  "
						+ "(id_online_payment_historical, description, project_id, retired_value,sender_ip, tramit_number, update_date, value_updated, online_payment_id) "
						+ "VALUES (nextval(''online_payment.seq_id_online_payments_historical''), ''Se revierte pago de " + montoPago + " por eliminación por parte del operador'', "
						+ "''" + codigoProyecto + "'', ''0.00'',''" + ip + "'', ''" + nroComprobante + "'', now(),  ''" + saldoComprobanteRevert + "''," + idPago + ")') as result";
				
				crudServiceBean.getEntityManager().createNativeQuery(sqlInsert).getResultList();

				Double valorUsado = Double.valueOf(totalUsadoComprobante) - montoPago;
				valorUsado = valorUsado >= 0.0 ? valorUsado : 0.0;

				String sqlUpdate = "select dblink_exec('" + dblinkSuiaVerde+ "'," 
						+ "'update online_payment.online_payments  "
						+ "SET is_used = false, used_value = ''" + valorUsado + "'' " 
						+ "WHERE tramit_number = ''" + nroComprobante + "''') as result ";
				crudServiceBean.getEntityManager()
						.createNativeQuery(sqlUpdate).getResultList();
				
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}