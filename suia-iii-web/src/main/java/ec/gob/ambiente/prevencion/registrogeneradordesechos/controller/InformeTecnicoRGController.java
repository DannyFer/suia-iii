package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

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

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.DocumentoRGBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class InformeTecnicoRGController implements Serializable {

	private static final long serialVersionUID = 165683211928358047L;

	private final Logger LOG = Logger.getLogger(InformeTecnicoRGController.class);

	@EJB
	private ProcesoFacade procesoFacade;


	@EJB
    private ConexionBpms conexionBpms;	
	
	@ManagedProperty(value = "#{documentoRGBean}")
	@Getter
	@Setter
	private DocumentoRGBean documentoRGBean;

	@PostConstruct
	public void init() {
		updateInforme();
	}

	public void updateInforme() {
		try {
				documentoRGBean.visualizarInforme(true);
		} catch (Exception e) {
			LOG.error("Error al cargar el informe tecnico del registro de generador", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public String aceptar() throws ServiceException, CmisAlfrescoException {
		List<String> errors = new ArrayList<>();
		if (documentoRGBean.getInforme().getNormaVigente() != null
				&& documentoRGBean.getInforme().getNormaVigente().trim().isEmpty())
			errors.add("El campo '2. ANTECEDENTES' es requerido.");
		
		if (documentoRGBean.getInforme().getConclusiones() != null
				&& documentoRGBean.getInforme().getConclusiones().trim().isEmpty())
			errors.add("El campo '5. CONCLUSIONES' es requerido.");

		if (!documentoRGBean.getInforme().getCumple() && !documentoRGBean.validarExistenciaObservacionesProponente())
			errors.add("El campo 'EL REGISTRO DE GENERADOR CUMPLE CON LAS NORMAS TÉCNICAS Y LEGALES' debe tener el valor 'Sí', porque no existen observaciones dirigidas al proponente sin corregir.");

		if (documentoRGBean.getInforme().getCumple() && documentoRGBean.validarExistenciaObservacionesProponente())
			errors.add("El campo 'EL REGISTRO DE GENERADOR CUMPLE CON LAS NORMAS TÉCNICAS Y LEGALES' debe tener el valor 'No', porque existen observaciones dirigidas al proponente sin corregir.");

		if (!errors.isEmpty()) {
			JsfUtil.addMessageError(errors);
			return "";
		}

		updateInforme();

		if(documentoRGBean.getInforme().getDocumento() == null) {//No se ha persistido el informe ni en BD ni en el Alfresco.

		documentoRGBean.guardarInforme();
		documentoRGBean.getInforme().setDocumento(documentoRGBean.guardarInformeDocumento());
		documentoRGBean.guardarInforme();
		}
		else if(this.documentoRGBean.getInforme().isModificado()){
			documentoRGBean.getInforme().setDocumento(documentoRGBean.guardarInformeDocumento());
			documentoRGBean.guardarInforme();
		}



		String tipoOficio = documentoRGBean.getInforme().getCumple() ? "EmisionActualizacion" : "Observaciones";

		try {
			Map<String, Object> params = new HashMap<>();
			params.put("existenObservacionesProponente", !documentoRGBean.getInforme().getCumple());
			params.put("tipoOficio", tipoOficio);
            procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(), params);

//            Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),
//            		JsfUtil.getCurrentProcessInstanceId());
//            for (Map.Entry<String, Object> parametros : params.entrySet()) {
//                if (variables.get(parametros.getKey()) == null) {
//                    procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(), params);
//                    break;
//                } else {
//                    conexionBpms.updateVariables(JsfUtil.getCurrentProcessInstanceId(), parametros.getKey(),parametros.getValue().toString());
//                }
//            }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		return JsfUtil.actionNavigateTo("/prevencion/registrogeneradordesechos/tecnicoResponsableOficio" + tipoOficio
				+ ".jsf");
	}

	public void guardar() {
		documentoRGBean.guardarInforme();
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		updateInforme();
	}

	/*public String cancelar() {
		return JsfUtil.actionNavigateToBandeja();
	}*/

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/prevencion/registrogeneradordesechos/tecnicoResponsableInformeTecnico.jsf");
	}

}
