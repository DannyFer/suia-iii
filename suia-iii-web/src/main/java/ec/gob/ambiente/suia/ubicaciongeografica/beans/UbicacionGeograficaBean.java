package ec.gob.ambiente.suia.ubicaciongeografica.beans;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;

@ManagedBean
@SessionScoped
public class UbicacionGeograficaBean {

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@Getter
	@Setter
	private List<UbicacionesGeografica> paises;

	@PostConstruct
	public void ini() {
		paises = ubicacionGeograficaFacade.listarPais();
	}

}
