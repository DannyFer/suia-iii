package ec.gob.ambiente.suia.eia.pma.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.suia.domain.CronogramaValoradoEiaPma;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.TipoPlanManejoAmbiental;
import ec.gob.ambiente.suia.eia.pma.facade.PlanManejoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean(name = "cronogramaPMA")
@ViewScoped
public class CronogramaValoradoPMABean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5607732877372490990L;

	@EJB
	PlanManejoAmbientalFacade planManejoAmbientalFacade;

	@Getter
	@Setter
	private List<CronogramaValoradoEiaPma> listaCronograma;
	@Getter
	@Setter
	private CronogramaValoradoEiaPma cronograma;
	
	@Getter
	@Setter
	private Double sumMes1 = 0.0;
	@Getter
	@Setter
	private Double sumMes2 = 0.0;
	@Getter
	@Setter
	private Double sumMes3 = 0.0;
	@Getter
	@Setter
	private Double sumMes4 = 0.0;
	@Getter
	@Setter
	private Double sumMes5 = 0.0;
	@Getter
	@Setter
	private Double sumMes6 = 0.0;
	@Getter
	@Setter
	private Double sumMes7 = 0.0;
	@Getter
	@Setter
	private Double sumMes8 = 0.0;
	@Getter
	@Setter
	private Double sumMes9 = 0.0;
	@Getter
	@Setter
	private Double sumMes10 = 0.0;
	@Getter
	@Setter
	private Double sumMes11 = 0.0;
	@Getter
	@Setter
	private Double sumMes12 = 0.0;
	@Getter
	@Setter
	private Double sumPresupuesto = 0.0;
	
	@PostConstruct
	public void init() {
		try {
			EstudioImpactoAmbiental eia = (EstudioImpactoAmbiental) JsfUtil
					.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);

			listaCronograma = planManejoAmbientalFacade.obtenerListaCronogramaPMA(eia.getId());
			
			if (listaCronograma == null || listaCronograma.isEmpty()) {
				listaCronograma = new ArrayList<CronogramaValoradoEiaPma>();
				
				List<TipoPlanManejoAmbiental> tiposPlan = planManejoAmbientalFacade
						.obtenerListaTipoPlanManejoAmbiental();
				for (TipoPlanManejoAmbiental tipoPlanManejoAmbiental : tiposPlan) {
					CronogramaValoradoEiaPma cronograma = new CronogramaValoradoEiaPma();
					cronograma
							.setTipoPlanManejoAmbiental(tipoPlanManejoAmbiental);
					cronograma.setEiaId(eia.getId());
					listaCronograma.add(cronograma);
				}
			}
			
			if(cronograma == null){
				cronograma = new CronogramaValoradoEiaPma();
			}
			
			sumatoriasTotales();
		} catch (Exception e) {
			JsfUtil.addMessageError("Error en carga inicial");
		}
	}
	
	public void guardarCronograma() {
		try {
			planManejoAmbientalFacade.guardarCronogramaPMA(listaCronograma);
			JsfUtil.addMessageInfo("Registro guardado correctamente");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al cargar el cronograma del PMA");
		}
	}
	
	public String cancelar() {
		return JsfUtil.actionNavigateTo("/pages/eia/default.jsf");
	}
	
	public void editarValores(
			CronogramaValoradoEiaPma cronogramaPMA) {
		setCronograma(cronogramaPMA);
	}

	public void agregarValores() {
		cronograma.setPresupuesto(cronograma.getSumatoriaPresupuesto());
		sumatoriasTotales();
		listaCronograma.remove(cronograma);
		listaCronograma.add(cronograma);
		cronograma = new CronogramaValoradoEiaPma();
		
		RequestContext.getCurrentInstance().addCallbackParam("puntoIn", true);
	}
	
	private void sumatoriasTotales(){
		sumMes1 = 0.0;
		sumMes2 = 0.0;
		sumMes3 = 0.0;
		sumMes4 = 0.0;
		sumMes5 = 0.0;
		sumMes6 = 0.0;
		sumMes7 = 0.0;
		sumMes8 = 0.0;
		sumMes9 = 0.0;
		sumMes10 = 0.0;
		sumMes11 = 0.0;
		sumMes12 = 0.0;
		sumPresupuesto = 0.0;
		
		for (CronogramaValoradoEiaPma cronograma : listaCronograma) {
			sumMes1 += cronograma.getMes1();
			sumMes2 += cronograma.getMes2();
			sumMes3 += cronograma.getMes3();
			sumMes4 += cronograma.getMes4();
			sumMes5 += cronograma.getMes5();
			sumMes6 += cronograma.getMes6();
			sumMes7 += cronograma.getMes7();
			sumMes8 += cronograma.getMes8();
			sumMes9 += cronograma.getMes9();
			sumMes10 += cronograma.getMes10();
			sumMes11 += cronograma.getMes11();
			sumMes12 += cronograma.getMes12();
			sumPresupuesto += cronograma.getPresupuesto();
		}
	}

}
