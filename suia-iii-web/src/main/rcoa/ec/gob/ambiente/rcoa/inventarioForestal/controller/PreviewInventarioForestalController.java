package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class PreviewInventarioForestalController {

	private static final Logger LOG = Logger.getLogger(PreviewInventarioForestalController.class);

	@EJB
	private ProcesoFacade procesoFacade;
	private Map<String, Object> variables;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@Setter
    @Getter
    private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();

	/* BEANs */
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;

	@Getter
	@Setter
	String urlIngresarInformacion;

	@Getter
	@Setter
	String urlVisualizarInformacion;
	
	/*String*/
	@Getter
	@Setter
    private String tramite;
	@Getter
	@Setter
    private Integer idRegistroPreliminar, categoria;

	@PostConstruct
	public void init() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			idRegistroPreliminar = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
			categoria = Integer.valueOf((String) variables.get("categoria"));
			if (categoria == 1) {
				urlIngresarInformacion = "/pages/rcoa/inventarioForestal/inventarioForestalCertificado.xhtml";
				urlVisualizarInformacion = "/pages/rcoa/inventarioForestal/inventarioForestalCertificadoVisualizar.xhtml";
			} else if (categoria == 2) {
				urlIngresarInformacion = "/pages/rcoa/inventarioForestal/inventarioForestalRegistro.xhtml";
				urlVisualizarInformacion = "/pages/rcoa/inventarioForestal/inventarioForestalRegistroVisualizar.xhtml";
			} else {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}

}