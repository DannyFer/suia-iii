package ec.gob.ambiente.prevencion.tdr.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.tdr.facade.TdrFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@ManagedBean
@ViewScoped
public class VistaPreviaTdrBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7846384489727078474L;

	@EJB
	private TdrFacade tdrFacade;

	private byte[] fichaTecnica;

	@Getter
	@Setter
	private StreamedContent fichaTecnicaD;

	@Setter
	@Getter
	TdrEiaLicencia tdrEia;

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@PostConstruct
	public void init() {
		try {
			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
							.getProcessInstanceId());
			Integer idProyecto = Integer.parseInt((String) variables
					.get(Constantes.ID_PROYECTO));
			tdrEia = tdrFacade.getTdrEiaLicenciaPorIdProyecto(idProyecto);

			fichaTecnica = tdrFacade.recuperarInformeTdr(tdrEia.getId());
			fichaTecnicaD = getStream();
		} catch (Exception e) {

		}
	}

	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fichaTecnica != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					fichaTecnica), "application/pdf");
			content.setName("Ficha_Tecnica_TDR.pdf");
		}
		return content;

	}

}
