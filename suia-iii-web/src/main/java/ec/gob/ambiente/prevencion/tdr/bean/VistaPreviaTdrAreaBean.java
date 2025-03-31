package ec.gob.ambiente.prevencion.tdr.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

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
public class VistaPreviaTdrAreaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2899469836414801129L;

	@EJB
	private TdrFacade tdrFacade;

	private byte[] fichaTecnica;
	@Getter
	@Setter
	private String area;

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
			Map<String, String> params = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap();
			String area_tmp = params.get("area");

			if (area_tmp != null && !area_tmp.isEmpty()) {
				area = area_tmp;

			} else {
				area = "Consolidado";
			}
			if (area.equals("Consolidado")) {
				area_tmp = params.get("areaC");

				if (area_tmp != null && !area_tmp.isEmpty()) {
					area = area_tmp;
				}

			}
			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
							.getProcessInstanceId());
			Integer idProyecto = Integer.parseInt((String) variables
					.get(Constantes.ID_PROYECTO));
			tdrEia = tdrFacade.getTdrEiaLicenciaPorIdProyecto(idProyecto);

			fichaTecnica = tdrFacade.recuperarInformeTdrArea(tdrEia.getId(),
					area);
			fichaTecnicaD = getStream();

		} catch (Exception e) {

		}
	}

	public void cargarFichero(String areaNueva) {
		try {
			System.out.println("---a-sas-a-s-as-as-a-s-as-as--as-sa");
			if (areaNueva != null && !areaNueva.isEmpty()) {
				fichaTecnica = tdrFacade.recuperarInformeTdrArea(
						tdrEia.getId(), areaNueva);
				fichaTecnicaD = getStream();
			}
		} catch (Exception e) {

		}
	}

	// public StreamedContent getStream() throws IOException {
	// FacesContext context = FacesContext.getCurrentInstance();
	//
	// if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
	// // So, we're rendering the HTML. Return a stub StreamedContent so
	// // that it will generate right URL.
	// return new DefaultStreamedContent();
	// } else {
	// // So, browser is requesting the media. Return a real
	// // StreamedContent with the media bytes.
	// String id = context.getExternalContext().getRequestParameterMap()
	// .get("id");
	// try {
	// return new DefaultStreamedContent(new ByteArrayInputStream(
	// tdrFacade.recuperarInformeTdr(Integer.parseInt(id))));
	// } catch (Exception e) {
	// }
	// }
	// return null;
	// }

	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = null;
		if (fichaTecnica != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					fichaTecnica));
			content.setName("Ficha_Tecnica_TDR_" + area + ".pdf");
		}
		return content;

	}

}
