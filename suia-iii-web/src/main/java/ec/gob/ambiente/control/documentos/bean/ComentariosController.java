package ec.gob.ambiente.control.documentos.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.comentarios.facade.ComentarioTareaFacade;
import ec.gob.ambiente.suia.domain.ComentarioTarea;

@ManagedBean
@ViewScoped
public class ComentariosController implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2591333169934163442L;

	@EJB
	private ComentarioTareaFacade comentarioTareaService;

	@Getter
	@Setter
	@ManagedProperty(value = "#{comentariosBean}")
	private ComentariosBean comentariosBean;

	@PostConstruct
	public void init() {

		comentariosBean.setComentarios(comentarioTareaService
				.getComentarioTarea(comentariosBean.getComentarioTarea()));

		comentariosBean.setComentarioTarea(new ComentarioTarea());

	}

	public void habilitarCampoCorreccionesListener() {
		comentariosBean.setHabilitarCampoCorrecciones(comentariosBean
				.getOpcion().equals("true") ? true : false);
		comentariosBean.getComentarioTarea().setRequiereCorrecciones(
				comentariosBean.getOpcion().equals("true") ? false : true);
	}
}
