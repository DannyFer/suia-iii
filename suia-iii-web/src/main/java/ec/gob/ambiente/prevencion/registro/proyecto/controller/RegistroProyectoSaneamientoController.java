package ec.gob.ambiente.prevencion.registro.proyecto.controller;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.base.RegistroProyectoController;
import ec.gob.ambiente.suia.domain.OficioViabilidad;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.TipoSubsector;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RegistroProyectoSaneamientoController extends RegistroProyectoController {

	private static final long serialVersionUID = 2254978117842906417L;

	public static final String[] CATEGORIAS_SANEAMIENTO = new String[] { "71" };
	
	public void guardarProyecto() throws CmisAlfrescoException {
		registroProyectoBean.getProyecto().setTipoSector(
				proyectoFacade.getTipoSector(TipoSector.TIPO_SECTOR_SANEAMIENTO));
		if(adicionarUbicacionesBean.getListParroquiasGuardar().size()>0){
			//ubicaciones = adicionarUbicacionesBean.getListParroquiasGuardar();
			adicionarUbicacionesBean.setUbicacionesSeleccionadas(adicionarUbicacionesBean.getListParroquiasGuardar());
		}
		proyectoFacade.guardar(registroProyectoBean.getProyecto(),
                catalogoActividadesBean.getActividadSistemaSeleccionada(),
                adicionarUbicacionesBean.getUbicacionesSeleccionadas(), null, null, null, null,
                cargarCoordenadasBean.getCoordinatesWrappers(), cargarCoordenadasBean.generateDocumentFromUpload(),
                registroProyectoBean.getFasesDesechosSeleccionadas(), desechoPeligrosoBean.getDesechosSeleccionados(),
                registroProyectoBean.getSustanciasQuimicasSeleccionadas(), JsfUtil.getLoggedUser().getNombre());
		registroProyectoBean.getProyecto().setFormato(cargarCoordenadasBean.getFormato());
	}

	public void verificarOficioViabilidad() {
		OficioViabilidad result = proyectoFacade.verificarOficioViabilidad(registroProyectoBean
				.getNumeroOficioViabilidad());
		registroProyectoBean.getProyecto().setOficioViabilidad(result);
		if (result == null) {
			JsfUtil.addMessageError("El número de oficio de viabilidad técnica ingresado no es válido.");
			return;
		}
	}

	public void eliminarOficioViabilidad() {
		registroProyectoBean.getProyecto().setOficioViabilidad(null);
		registroProyectoBean.setNumeroOficioViabilidad(null);
	}

	public void adicionarValidaciones(List<String> errors) {
		if (isMostrarSaneamiento() && isGestionaOGeneraResiduosSolidos()) {
			if (registroProyectoBean.getViabilidad() != null && registroProyectoBean.getViabilidad()) {
				if (registroProyectoBean.getProyecto().getOficioViabilidad() == null) {
					errors.add("Debe especificar un oficio de viabilidad técnica validado por el sistema.");
				}
			} else if (registroProyectoBean.getViabilidad() != null && !registroProyectoBean.getViabilidad()) {
				errors.add("Si su proyecto, obra o actividad gestiona/genera desechos sólidos y no cuenta con un oficio de viabilidad técnica, el registro del proyecto no puede continuar.");
			}
		}
	}

	public List<String> validar() {
		return null;
	}

	public List<String> validarNegocio() {
		return null;
	}
	
	public boolean isMostrarSaneamiento() {
        //boolean result = isCatalogoCategoriaCodigoInicia(CATEGORIAS_SANEAMIENTO);
        if (catalogoActividadesBean.getActividadSistemaSeleccionada() != null){
            return catalogoActividadesBean.getActividadSistemaSeleccionada().getRequiereViabilidad();
        }
        return false;
	}

	public boolean isGestionaOGeneraResiduosSolidos() {
		String codigo = obtenerSubsectorCatalogoCategoriaSistema();
		boolean result = TipoSubsector.CODIGO_RESIDUOS_SOLIDOS.equals(codigo);
		return result;
	}
	
	public boolean isFinanciadoEstado() {
		boolean result =false;
		if (catalogoActividadesBean.getActividadSistemaSeleccionada()!=null){
			if (catalogoActividadesBean.getActividadSistemaSeleccionada().getActividadFinanciadaEstado()!=null){
			if (catalogoActividadesBean.getActividadSistemaSeleccionada().getActividadFinanciadaEstado()){				
			result=true;					
			}
			}
		}
		return result;
	}
	
}
