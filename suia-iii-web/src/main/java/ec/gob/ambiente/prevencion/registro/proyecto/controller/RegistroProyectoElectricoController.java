package ec.gob.ambiente.prevencion.registro.proyecto.controller;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.base.RegistroProyectoController;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RegistroProyectoElectricoController extends RegistroProyectoController {

	private static final long serialVersionUID = 942540739206208208L;

	public void guardarProyecto() throws CmisAlfrescoException {
		registroProyectoBean.getProyecto().setTipoSector(
				proyectoFacade.getTipoSector(TipoSector.TIPO_SECTOR_ELECTRICO));
		if(adicionarUbicacionesBean.getListParroquiasGuardar().size()>0){
			//ubicaciones = adicionarUbicacionesBean.getListParroquiasGuardar();
			adicionarUbicacionesBean.setUbicacionesSeleccionadas(adicionarUbicacionesBean.getListParroquiasGuardar());
		}
		proyectoFacade.guardar(registroProyectoBean.getProyecto(),
				catalogoActividadesBean.getActividadSistemaSeleccionada(),
				adicionarUbicacionesBean.getUbicacionesSeleccionadas(), null, null, null, null,
				cargarCoordenadasBean.getCoordinatesWrappers(), cargarCoordenadasBean.generateDocumentFromUpload(),
				registroProyectoBean.getFasesDesechosSeleccionadas(), desechoPeligrosoBean.getDesechosSeleccionados(),
				registroProyectoBean.getSustanciasQuimicasSeleccionadas(),JsfUtil.getLoggedUser().getNombre());
		registroProyectoBean.getProyecto().setFormato(cargarCoordenadasBean.getFormato());
	}

	public void adicionarValidaciones(List<String> errors) {
	}

	public List<String> validar() {
		return null;
	}

	public List<String> validarNegocio() {
		return null;
	}
}
