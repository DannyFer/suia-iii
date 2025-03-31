package ec.gob.ambiente.suia.comun.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.comun.base.ComunBean;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class PagoServiciosBeanSaltarValidacion extends ComunBean {

	private static final long serialVersionUID = 4910545245153339126L;

	@Getter
	@Setter
	public Boolean cumpleMonto = false;

	@Getter
	@Setter
	public double montoTotal;

	@Getter
	@Setter
	private List<InstitucionFinanciera> institucionesFinancieras;

	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinanciera;

	@Getter
	@Setter
	private List<TransaccionFinanciera> transaccionesFinancieras;

	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;

	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	private String identificadorMotivo;

	public void initData() {
		institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancieras();
		transaccionFinanciera = new TransaccionFinanciera();
		if (this.identificadorMotivo == null)
			transaccionesFinancieras = transaccionFinancieraFacade.cargarTransacciones(proyectosBean.getProyecto().getId(),
                    bandejaTareasBean.getTarea().getProcessInstanceId(),
                    bandejaTareasBean.getTarea().getTaskId());
		else
			transaccionesFinancieras = transaccionFinancieraFacade.cargarTransacciones(this.identificadorMotivo);
	}

	/**
	 * @param nextURL
	 *            Próxima URL a navegar después de ejecutar el pago.
	 * @param completeOperation
	 *            Lógica a ejecutar después de ejecutar el pago.
	 * @param montoTotal
	 *            Total del monto a pagar.
	 * @param identificadorMotivo
	 *            Código del motivo, si el pago es asociado al proyecto, debe ser <b>null</b>.
	 * @throws ServiceException
	 */
	public void initFunctionWithProjectOrMotive(String nextURL, CompleteOperation completeOperation, double montoTotal,
			String identificadorMotivo) {
		super.initFunction(nextURL, completeOperation);
		this.montoTotal = montoTotal;
		this.identificadorMotivo = identificadorMotivo;
		initData();
	}

	@Override
	public String getFunctionURL() {
		return "/comun/pagoServicios.jsf";
	}

	@Override
	public void cleanData() {
		identificadorMotivo = null;
		transaccionFinanciera = new TransaccionFinanciera();
		transaccionesFinancieras = new ArrayList<TransaccionFinanciera>();
	}

	@Override
	public boolean executeBusinessLogic(Object object) {
		 /*String motivo = this.identificadorMotivo == null ? proyectosBean.getProyecto().getCodigo()
				: this.identificadorMotivo;
		boolean pagoSatisfactorio = transaccionFinancieraFacade.realizarPago(montoTotal,
				getTransaccionesFinancieras(), motivo);
		if (pagoSatisfactorio) {
			transaccionFinancieraFacade.guardarTransacciones(getTransaccionesFinancieras());
			JsfUtil.addMessageInfo("Pago satisfactorio.");*/
			executeOperation(object);
		 /*} else {
			JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
		}

		return pagoSatisfactorio;*/
			
			return true;
	}

	public void guardarTransaccion() {
		if (transaccionFinanciera.getInstitucionFinanciera() != null
				&& transaccionFinanciera.getNumeroTransaccion() != "") {
			try {
				if (existeTransaccion(transaccionFinanciera)) {
					JsfUtil.addMessageInfo("El número de comprobante: " + transaccionFinanciera.getNumeroTransaccion()
							+ " (" + transaccionFinanciera.getInstitucionFinanciera().getNombreInstitucion()
							+ ") ya fue registrado por usted. Ingrese otro distinto.");
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
						return;
					} else {
						transaccionFinanciera.setMontoTransaccion(monto);
						if (this.identificadorMotivo == null) {
							transaccionFinanciera.setProyecto(proyectosBean.getProyecto());
							transaccionFinanciera.setIdentificadorMotivo(null);
						} else {
							transaccionFinanciera.setProyecto(null);
							transaccionFinanciera.setIdentificadorMotivo(this.identificadorMotivo);
						}
						transaccionesFinancieras.add(transaccionFinanciera);
						cumpleMonto = cumpleMonto();
						transaccionFinanciera = new TransaccionFinanciera();
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

	private boolean cumpleMonto() {
		/*double montoTotal = 0;
		for (TransaccionFinanciera transa : transaccionesFinancieras) {
			montoTotal += transa.getMontoTransaccion();
		}
		if (montoTotal >= this.montoTotal) {
			return true;
		}
		return false;*/
		return true;
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
		cumpleMonto = cumpleMonto();
	}

	public String completarTarea() {
		/* if (getTransaccionesFinancieras().size() == 0) {
			JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
			return null;
		}*/

	/*	if (!getCumpleMonto()) {
			JsfUtil.addMessageError("El monto de la transacción utilizada es insuficiente para completar el pago de tasas.");
			return null;
		}*/

		double montoTotal = Monto();

		/*if (montoTotal > this.montoTotal) {
			RequestContext.getCurrentInstance().execute("PF('dlg1').show();");
		} else if (montoTotal == this.montoTotal) {*/
			return executeOperationAction(montoTotal);
		 /*}
		return null;*/
	}
}
