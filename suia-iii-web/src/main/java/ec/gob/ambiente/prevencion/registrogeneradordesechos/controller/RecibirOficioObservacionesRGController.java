package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.DocumentoRGBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RecibirOficioObservacionesRGController implements Serializable {

	private static final long serialVersionUID = 9029735399975303197L;

	private final Logger LOG = Logger.getLogger(RecibirOficioObservacionesRGController.class);

	@EJB
	private DocumentosFacade documentosFacade;

	@ManagedProperty(value = "#{documentoRGBean}")
	@Getter
	@Setter
	private DocumentoRGBean documentoRGBean;

	@PostConstruct
	public void init() {
		try {
			
			documentoRGBean.setContadorBandejaTecnico(documentoRGBean.getContadorBandejaTecnico() - 1);
			Documento documentoOficioObservaciones = documentoRGBean.inicializarOficioObservacionesAsociado()
					.getDocumento();

			if (documentoOficioObservaciones != null)
				documentoOficioObservaciones.setContenidoDocumento(documentosFacade
						.descargar(documentoOficioObservaciones.getIdAlfresco()));

		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
			LOG.error("Error cargando datos del registro de generador", e);
		}
	}

	public String siguiente() {
		return JsfUtil.actionNavigateTo("/prevencion/registrogeneradordesechos/realizarCorreccionesRegistroActualizar");
	}
}
