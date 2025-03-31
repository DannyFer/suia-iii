package ec.gob.ambiente.control.documentos.bean;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.ComentarioTarea;

@ManagedBean
@ViewScoped
public class ComentariosBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -700794539542498276L;

	public ComentariosBean() {
		habilitarCampoCorrecciones = true;
		comentarioTarea = new ComentarioTarea();
	}

	@Getter
	@Setter
	private List<ComentarioTarea> comentarios;
	@Getter
	@Setter
	private ComentarioTarea comentarioTarea;

	@Getter
	@Setter
	private boolean aprNeg;

	@Getter
	@Setter
	private boolean aprMod;

	@Getter
	@Setter
	private boolean aprNegMod;

	@Getter
	@Setter
	private String accion;

	@Getter
	@Setter
	private boolean habilitarCampoCorrecciones;

	@Getter
	@Setter
	private String opcion;
}
