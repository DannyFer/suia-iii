package ec.gob.ambiente.prevencion.actualizacionPma.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.comun.bean.IdentificarProyectoComunBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class BuscarProyectoActualizacionPMAController implements Serializable {

	private static final long serialVersionUID = 1769026407817490750L;

	public static final String MOTIVO_REGISTRO_GENERADOR_DESECHOS = "motivoRegistro";
	public static final String MOTIVO_REGISTRO_GENERADOR_DESECHOS_ASOCIADO = "asociado";
	public static final String MOTIVO_REGISTRO_GENERADOR_DESECHOS_NO_ASOCIADO = "noAsociado";

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

	@Getter
	private String tipoAccion;

	@Getter
	@Setter
	private String tipoMotivo;

	@Getter
	private ProyectoCustom proyectoCustom;

	@PostConstruct
	private void init() {

	}

	/*public void aceptar(){
		JsfUtil.getBean(IdentificarProyectoComunBean.class).initFunction(
				"/prevencion/actualizacionPma/pma/planManejoAmbiental", new CompleteOperation() {

					@Override
					public Object endOperation(Object object) {
						proyectoCustom = (ProyectoCustom) object;
						ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyectoCustom.getId()));

						try {
							EstudioImpactoAmbiental proyectoEIA = estudioImpactoAmbientalFacade.obtenerPorProyecto(proyecto);

							if (proyectoEIA == null){
								JsfUtil.addMessageError("No se ha realizado el estudio de impacto ambiental");
								return false;
							} else {
								JsfUtil.cargarObjetoSession(ec.gob.ambiente.suia.utils.Constantes.SESSION_EIA_OBJECT,proyectoEIA);
							}


						} catch (Exception e){}
						return null;
					}
				});
	}*/

	public String aceptar() {
		/*if (getMotivoAsociado().equals(tipoMotivo) && proyectoCustom == null) {
			JsfUtil.addMessageError("Debe seleccionar el proyecto asociado al registro de generador de desechos.");
			return null;
		}*/

		return JsfUtil.actionNavigateTo("/prevencion/actualizacionPma/pma/planManejoAmbiental");
				//,MOTIVO_REGISTRO_GENERADOR_DESECHOS + "=" + tipoMotivo);
	}

	public void seleccionarProyecto() {
		JsfUtil.getBean(IdentificarProyectoComunBean.class).initFunction(
				"/prevencion/actualizacionPma/utiles/buscarProyectoActualizacionPMA", new CompleteOperation() {

					@Override
					public Object endOperation(Object object) {
						proyectoCustom = (ProyectoCustom) object;
						return null;
					}
				});
	}

	public void eliminarProyecto() {
		proyectoCustom = null;
	}

	public String registrarAccion(String tipo) {
		this.tipoAccion = tipo;
		this.tipoMotivo = null;
		this.proyectoCustom = null;
		return JsfUtil.actionNavigateTo("/prevencion/actualizacionPma/utiles/buscarProyectoActualizacionPMA");
	}

	public ProyectoLicenciamientoAmbiental cargarProyecto() {
		try {
			return proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(Integer
					.parseInt(proyectoCustom.getId()));
		} catch (Exception e) {
			return null;
		}
	}

	public String getMotivoAsociado() {
		return MOTIVO_REGISTRO_GENERADOR_DESECHOS_ASOCIADO;
	}

	public String getMotivoNoAsociado() {
		return MOTIVO_REGISTRO_GENERADOR_DESECHOS_NO_ASOCIADO;
	}

	public String getAccionEmision() {
		return "emision";
	}
}
