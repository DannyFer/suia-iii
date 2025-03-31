package ec.gob.ambiente.pronunciamiento.bean;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Map;

@ManagedBean
@ViewScoped
public class RevisarPronunciamientoAreaBean implements Serializable {

	private static final long serialVersionUID = 2975347857643455827L;

	private static final Logger LOGGER = Logger.getLogger(RevisarPronunciamientoAreaBean.class);

	@EJB
	private PronunciamientoFacade pronunciamientoFacade;

	@Getter
	@Setter
	private Boolean correcto;

	@Getter
	@Setter
	private String area;

	@Getter
	@Setter
	private Pronunciamiento pronunciamiento;

	private final String clasePronunciamiento = "Pronunciamiento";

	@Getter
	private long idInstanciaProceso;

	@PostConstruct
	public void init() {
		//correcto = true;
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		area = params.get("area");
		try {
			idInstanciaProceso = JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessInstanceId();

			pronunciamiento = pronunciamientoFacade.getPronunciamientosPorClaseTipo(clasePronunciamiento,
					idInstanciaProceso, area);

		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
			LOGGER.error(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA, exception);
		}
	}

}
