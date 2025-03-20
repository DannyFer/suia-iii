package ec.gob.ambiente.prevencion.categoria2.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.CabeceraPma;
import ec.gob.ambiente.suia.domain.DetalleCabeceraPma;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.PlanPmaFacade;

/**
 * 
 * @author karla.carvajal
 *
 */
@ManagedBean
@ViewScoped
public class PlanPmaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Setter
	@ManagedProperty(value = "#{fichaAmbientalPmaBean}")
	private FichaAmbientalPmaBean fichaAmbientalPmaBean;
	
	@Setter
	@ManagedProperty(value = "#{catalogoGeneralPmaBean}")
	private CatalogoGeneralPmaBean catalogoGeneralPmaBean;
	
	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	
	@EJB
	private PlanPmaFacade planPmaFacade;
	
	@Getter
	@Setter
	private List<SelectItem> itemsPlanes;	
	
	@Getter
	@Setter
	private String planSeleccionado;
	
	@Getter
	@Setter
	private CabeceraPma plan;
	
	@Getter
	@Setter
	private List<DetalleCabeceraPma> detallesPlan;
	
	@Getter
	@Setter
	private DetalleCabeceraPma detallePlan;
	
			
	@PostConstruct
	public void init() {		
		llenarPlanes();		
		plan = new CabeceraPma();
		detallesPlan = new ArrayList<DetalleCabeceraPma>();
		limpiarDetallePlan();
		if(fichaAmbientalPmaBean.getFicha().getId() != null)
		{
			plan = planPmaFacade.getCabeceraPma(fichaAmbientalPmaBean.getFicha().getId());
			if(plan.getId() != null)
			{
				planSeleccionado = plan.getCatalogoGeneral().getId().toString();			
				llenarDetallesPlan();
			}
		}
	}
	
	public void cleanPlanes()
	{
		plan = new CabeceraPma();
		planSeleccionado = "";
		detallePlan = new DetalleCabeceraPma();
		detallesPlan = new ArrayList<DetalleCabeceraPma>();
	}	
		
	public void llenarPlanes()
	{
		try {
			itemsPlanes = new ArrayList<SelectItem>();
			this.itemsPlanes.clear();
			this.itemsPlanes.add(new SelectItem("", "Seleccione..."));

			for (CatalogoGeneral plan : catalogoGeneralPmaBean.getTiposPlanes()) {
				this.itemsPlanes.add(new SelectItem(plan.getId(),
						plan.getDescripcion()));
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void llenarDetallesPlan()
	{		
		if(plan.getId() != null)
		detallesPlan = planPmaFacade.getDetallesPlanesPorPlanId(plan.getId());
	}
	
	public void agregar()
	{
		if (detallesPlan.contains(this.detallePlan)) {
			this.detallesPlan.set(
					this.detallesPlan.lastIndexOf(this.detallePlan),
					detallePlan);
		} else {
			this.detallesPlan.add(this.detallePlan);
		}
		limpiarDetallePlan();
	}
	
	public void editarDetallePlan(DetalleCabeceraPma _detallePlan)
	{
		this.detallePlan = _detallePlan;
	}
	
	public void eliminarDetallePlan(DetalleCabeceraPma _detallePlan)
	{
		detallesPlan.remove(_detallePlan);
	}
	
	public void limpiarDetallePlan()
	{
		detallePlan = new DetalleCabeceraPma();
	}
}