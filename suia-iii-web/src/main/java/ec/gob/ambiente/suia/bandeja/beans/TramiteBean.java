package ec.gob.ambiente.suia.bandeja.beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class TramiteBean implements Serializable {

	private static final long serialVersionUID = 8818441531869924073L;

	@PostConstruct
	private void init() {
		if (JsfUtil.getBean(BandejaTareasBean.class).getResolverTramite() != null
				&& JsfUtil.getBean(BandejaTareasBean.class).getResolverTramite().isDataResolved())
			JsfUtil.getBean(BandejaTareasBean.class).getResolverTramite().endOperation(null);
	}

	public String verTramite() {
		if (!isResolverLoaded())
			return null;
		return JsfUtil.actionNavigateTo(JsfUtil.getBean(BandejaTareasBean.class).getResolverTramite()
				.getUrlToShowTramite());
	}

	public boolean isUrlToShow() {
		if (isResolverLoaded()
				&& JsfUtil.getBean(BandejaTareasBean.class).getResolverTramite().getUrlToShowTramite() != null)
			return true;
		return false;
	}

	private boolean isResolverLoaded() {
		return JsfUtil.getBean(BandejaTareasBean.class).getResolverTramite() != null
				&& JsfUtil.getBean(BandejaTareasBean.class).getResolverTramite().isDataResolved();
	}
}
