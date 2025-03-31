package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.MenuAprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class AprobacionRequisitosTecnicosBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -82217916391993282L;

	@Getter
	@Setter
	private List<ModalidadGestionDesechos> modalidades = new ArrayList<ModalidadGestionDesechos>();

	@Getter
	@Setter
	private List<ModalidadGestionDesechos> modalidadesSeleccionadas = new ArrayList<ModalidadGestionDesechos>();

	@Setter
	@Getter
	private boolean mostrarOtros;

	@Setter
	@Getter
	private boolean mostrarModalidades;

	@Setter
	@Getter
	private List<MenuAprobacionRequisitosTecnicos> menu;

	@Setter
	@Getter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	private String mensaje;

	@Getter
	@Setter
	private String numeroPermisoAmbiental;

	@Getter
	@Setter
	private String tipo = "";
	@Getter
	@Setter
	private boolean revisar;

	@Setter
	@Getter
	private String seccion;

	@Getter
	@Setter
	private boolean habilitarTipoRequisitos;

	@Getter
	@Setter
	private boolean habilitarMensajeNumVehiculoConductores;

	@Getter
	@Setter
	private boolean completado;
	
	@Setter
	@Getter
	private String mensajesError;

	public String verART(String sectionName) {
		ArtGeneralBean artGeneralBean = JsfUtil.getBean(ArtGeneralBean.class);
		artGeneralBean.setSectionName(sectionName);
		setSeccion(sectionName);
		return artGeneralBean.actionVerART(this.aprobacionRequisitosTecnicos, true, false, false);

	}

}
