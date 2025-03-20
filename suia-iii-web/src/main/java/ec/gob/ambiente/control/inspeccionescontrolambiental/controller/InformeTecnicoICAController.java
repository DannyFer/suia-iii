package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.inspeccionescontrolambiental.bean.DocumentoICABean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class InformeTecnicoICAController implements Serializable {

	private static final long serialVersionUID = 8702304418830150103L;

	private final Logger LOG = Logger.getLogger(InformeTecnicoICAController.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@ManagedProperty(value = "#{documentoICABean}")
	@Getter
	@Setter
	private DocumentoICABean documentoICABean;
	@EJB 
	ConexionBpms conexionBpms;
	
	@PostConstruct
	public void init() {
		updateInforme();
	}

	public void updateInforme() {
		try {
			documentoICABean.visualizarInforme(true);
		} catch (Exception e) {
			LOG.error("Error al cargar el informe tecnico de la inspeccion de control ambiental", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public String aceptar() throws ServiceException, CmisAlfrescoException {
		List<String> errors = new ArrayList<>();
		if (documentoICABean.getInforme().getNormaVigente() != null
				&& documentoICABean.getInforme().getNormaVigente().trim().isEmpty())
			errors.add("El campo '2. ANTECEDENTES' es requerido.");

		if (documentoICABean.getInforme().getConclusiones() != null
				&& documentoICABean.getInforme().getConclusiones().trim().isEmpty())
			errors.add("El campo '5. CONCLUSIONES' es requerido.");

		if (!errors.isEmpty()) {
			JsfUtil.addMessageError(errors);
			return "";
		}

		documentoICABean.guardarInforme();
		documentoICABean.getInforme().setDocumento(documentoICABean.guardarInformeDocumento());
		documentoICABean.guardarInforme();

		String tipoOficio = documentoICABean.getInforme().getCumple() ? "Pronunciamiento" : "Observaciones";

		try {
			Map<String, Object> params = new HashMap<>();
			params.put("existenObservacionesSujetoControl", !documentoICABean.getInforme().getCumple());
			params.put("tipoOficio", tipoOficio);
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
					params);
//			for (Map.Entry<String, Object> parametros : params.entrySet()) {
//                conexionBpms.updateVariables(JsfUtil.getCurrentProcessInstanceId(), parametros.getKey(),parametros.getValue().toString());
//            }			
		} catch (Exception e) {
			e.printStackTrace();
		}
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		return JsfUtil.actionNavigateTo(
				"/control/inspeccionescontrolambiental/tecnicoResponsableOficio" + tipoOficio + ".jsf");
	}

	public void guardar() {
		documentoICABean.guardarInforme();
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		updateInforme();
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/inspeccionescontrolambiental/tecnicoResponsableInformeTecnico.jsf");
	}

}
