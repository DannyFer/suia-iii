package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class InformeTecnicoIFController implements Serializable {

	private static final long serialVersionUID = -42729579304117925L;

	private final Logger LOG = Logger.getLogger(InformeTecnicoIFController.class);

	@ManagedProperty(value = "#{inventarioForestalInformeTecnicoPronunciamientoController}")
	@Getter
	@Setter
	private InventarioForestalInformeTecnicoPronunciamientoController documentoIF;
	
	@PostConstruct
	public void init() {
		updateInforme();
	}
	
	public void updateInforme() {
		try {
			documentoIF.visualizarInforme(true);
		} catch (Exception e) {
			LOG.error("Error al cargar el informe tecnico del registro de generador", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
}