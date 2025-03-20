package ec.gob.ambiente.suia.comun.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;

@ManagedBean
@ViewScoped
public class WizardBean implements Serializable {

	private static final long serialVersionUID = -4574929205472255938L;

	@Getter
	@Setter
	private boolean skip;
	
	@Getter
	@Setter
	private String currentStep;

	public String onFlowProcess(FlowEvent event) {
		RequestContext.getCurrentInstance().execute("resetPageScroll()");
		if (skip) {
			skip = false; // reset in case user goes back
			return "confirm";
		} else {
			this.currentStep = event.getNewStep();
			return this.currentStep;
		}
	}

}
