package ec.gob.ambiente.pronunciamiento.bean;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarPronunciamientoAreaBean implements Serializable {

	private static final long serialVersionUID = -6958491982694252430L;

	private static final Logger LOGGER = Logger.getLogger(ElaborarPronunciamientoAreaBean.class);

	@Setter
	@Getter
	private String area;

	@Setter
	@Getter
	private String urlPage;

	@Getter
	private long idInstanciaProceso;

	@EJB
	private PronunciamientoFacade pronunciamientoFacade;

	@Getter
	@Setter
	private boolean corregir;

	@Getter
	@Setter
	private Pronunciamiento pronunciamiento;

	private final String clasePronunciamiento = "Pronunciamiento";

	@PostConstruct
	public void init() {
		try {
			inicializar();

			idInstanciaProceso = JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessInstanceId();

			pronunciamiento = pronunciamientoFacade.getPronunciamientosPorClaseTipo(clasePronunciamiento,
					idInstanciaProceso, area);

			if (pronunciamiento == null) {
				pronunciamiento = new Pronunciamiento();
				pronunciamiento.setNombreClase(clasePronunciamiento);
				pronunciamiento.setIdClase(idInstanciaProceso);
				pronunciamiento.setTipo(area);
				pronunciamiento.setUsuario(JsfUtil.getBean(LoginBean.class).getUsuario());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
			LOGGER.error(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA, exception);
		}
	}

	/**
	 * Metodo para inicializar variables
	 */
	public void inicializar() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String area_tmp = params.get("area");
		area = "";
		corregir = false;

		if (area_tmp != null && !area_tmp.isEmpty()) {
			area = area_tmp;

		}
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getBean(BandejaTareasBean.class).getTarea(),
				"/pronunciamiento/elaborarPronunciamientoArea.jsf?area=" + area);
	}

}
