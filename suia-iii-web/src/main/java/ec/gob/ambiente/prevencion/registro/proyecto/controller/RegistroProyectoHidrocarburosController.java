package ec.gob.ambiente.prevencion.registro.proyecto.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.BloquesBean;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.base.RegistroProyectoController;
import ec.gob.ambiente.suia.AutorizacionCatalogo.facade.AutorizacionCatalogoFacade;
import ec.gob.ambiente.suia.domain.AreasAutorizadasCatalogo;
import ec.gob.ambiente.suia.domain.Bloque;
import ec.gob.ambiente.suia.domain.ContributorData;
import ec.gob.ambiente.suia.domain.ProyectoBloque;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class RegistroProyectoHidrocarburosController extends RegistroProyectoController {

	private static final long serialVersionUID = 867790677809250180L;

	public static final int[] FASES_BLOQUE = new int[] { 1, 2, 3 };
	public static final int[] FASES_REFINERIA = new int[] { 4 };
	public static final int[] FASES_TRANSPORTE = new int[] { 5 };
	public static final int[] FASES_COMERCIO = new int[] { 6 };

	@Getter
	private final String rucRefineria = "refinería";

	@Getter
	private final String rucInfraestructura = "infraestructura";

	@Getter
	private final String rucComercializadora = "comercializadora";

	@Getter
	private final String rucEstacionServicio = "estacion de servicio";

	@Setter
	@ManagedProperty(value = "#{bloquesBean}")
	private BloquesBean bloquesBean;
	
	@EJB
	private AutorizacionCatalogoFacade autorizacionCatalogoFacade;
	
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());

	@PostConstruct
	private void initHidrocarburos() {
		if (registroProyectoBean.isEdicion()) {
			Iterator<ProyectoBloque> iteratorPB = registroProyectoBean.getProyecto().getProyectoBloques().iterator();
			while (iteratorPB.hasNext()) {
				bloquesBean.setBloqueSeleccionado(iteratorPB.next().getBloque());
			}
		}
	}

//	public void guardarProyecto() throws CmisAlfrescoException {
//		registroProyectoBean.getProyecto().setTipoSector(
//				proyectoFacade.getTipoSector(TipoSector.TIPO_SECTOR_HIDROCARBUROS));
//		if(adicionarUbicacionesBean.getListParroquiasGuardar().size()>0){
//			//ubicaciones = adicionarUbicacionesBean.getListParroquiasGuardar();
//			adicionarUbicacionesBean.setUbicacionesSeleccionadas(adicionarUbicacionesBean.getListParroquiasGuardar());
//		}
//		proyectoFacade.guardar(registroProyectoBean.getProyecto(),
//				catalogoActividadesBean.getActividadSistemaSeleccionada(),
//				adicionarUbicacionesBean.getUbicacionesSeleccionadas(), null,
//				isFaseMostrarBloque() ? bloquesBean.getBloquesSeleccionados() : null, null, null,
//				cargarCoordenadasBean.getCoordinatesWrappers(), cargarCoordenadasBean.generateDocumentFromUpload(),
//				registroProyectoBean.getFasesDesechosSeleccionadas(), desechoPeligrosoBean.getDesechosSeleccionados(),
//				registroProyectoBean.getSustanciasQuimicasSeleccionadas(),JsfUtil.getLoggedUser().getNombre());
//	}
	
	public void guardarProyecto() throws CmisAlfrescoException {
		registroProyectoBean.getProyecto().setTipoSector(
				proyectoFacade.getTipoSector(TipoSector.TIPO_SECTOR_HIDROCARBUROS));
		if(adicionarUbicacionesBean.getListParroquiasGuardar().size()>0){
			//ubicaciones = adicionarUbicacionesBean.getListParroquiasGuardar();
			adicionarUbicacionesBean.setUbicacionesSeleccionadas(adicionarUbicacionesBean.getListParroquiasGuardar());
		}
		AreasAutorizadasCatalogo estacionServicio=autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(catalogoActividadesBean.getActividadSistemaSeleccionada(), adicionarUbicacionesBean.getUbicacionesSeleccionadas().get(0).getEnteAcreditado());
		if(estacionServicio!=null)
		{
			if(estacionServicio.getActividadBloqueada())
			{
				JsfUtil.addMessageError("No se puede registrar el proyecto, Ente responsable no habilitado");
			}
			else
			{
				proyectoFacade.guardar(registroProyectoBean.getProyecto(),
						catalogoActividadesBean.getActividadSistemaSeleccionada(),
						adicionarUbicacionesBean.getUbicacionesSeleccionadas(), null,
						isFaseMostrarBloque() ? bloquesBean.getBloquesSeleccionados() : null, null, null,
								cargarCoordenadasBean.getCoordinatesWrappers(), cargarCoordenadasBean.generateDocumentFromUpload(),
								registroProyectoBean.getFasesDesechosSeleccionadas(), desechoPeligrosoBean.getDesechosSeleccionados(),
								registroProyectoBean.getSustanciasQuimicasSeleccionadas(),JsfUtil.getLoggedUser().getNombre());
				registroProyectoBean.getProyecto().setFormato(cargarCoordenadasBean.getFormato());
			}
		}
		else
		{
			proyectoFacade.guardar(registroProyectoBean.getProyecto(),
					catalogoActividadesBean.getActividadSistemaSeleccionada(),
					adicionarUbicacionesBean.getUbicacionesSeleccionadas(), null,
					isFaseMostrarBloque() ? bloquesBean.getBloquesSeleccionados() : null, null, null,
							cargarCoordenadasBean.getCoordinatesWrappers(), cargarCoordenadasBean.generateDocumentFromUpload(),
							registroProyectoBean.getFasesDesechosSeleccionadas(), desechoPeligrosoBean.getDesechosSeleccionados(),
							registroProyectoBean.getSustanciasQuimicasSeleccionadas(),JsfUtil.getLoggedUser().getNombre());
			registroProyectoBean.getProyecto().setFormato(cargarCoordenadasBean.getFormato());
		}
	}


	public void adicionarValidaciones(List<String> errors) {
		if (isFaseMostrarBloque() && bloquesBean.getBloquesSeleccionados().isEmpty())
			errors.add("Bloque es requerido. Especifique el/los bloque(s) en el/los que se desarrolla el proyecto.");
	}

	public List<String> validar() {
		return null;
	}
	
	public List<String> validarNegocio() {
		return validateContributorsData();
	}

	public String consultarDatosSriContribuyente(String entidad, String ruc, boolean propagateMessage) {
		ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula.obtenerPorRucSRI(
				Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, ruc);
		if (validarRUCContribuyenteCompleto(contribuyenteCompleto)) {
			ContributorData contributorData = new ContributorData();
			contributorData.setNombre(getNombreRazonContribuyente(contribuyenteCompleto));
			contributorData.setRuc(contribuyenteCompleto.getNumeroRuc());

			switch (entidad) {
			case rucRefineria:
				registroProyectoBean.getProyecto().setRefineria(contributorData);
				break;
			case rucInfraestructura:
				registroProyectoBean.getProyecto().setInfraestructura(contributorData);
				break;
			case rucComercializadora:
				registroProyectoBean.getProyecto().setComercializadora(contributorData);
				break;
			case rucEstacionServicio:
				registroProyectoBean.getProyecto().setEstacionServicio(contributorData);
				break;
			default:
				break;
			}
			return null;
		}
		String message = "RUC para " + entidad + " no encontrado";

		if (propagateMessage)
			return message;

		JsfUtil.addMessageError(message);
		return null;
	}

	public void eliminarContribuyente(String entidad) {
		switch (entidad) {
		case rucRefineria:
			registroProyectoBean.getProyecto().setRefineria(null);
			break;
		case rucInfraestructura:
			registroProyectoBean.getProyecto().setInfraestructura(null);
			break;
		case rucComercializadora:
			registroProyectoBean.getProyecto().setComercializadora(null);
			break;
		case rucEstacionServicio:
			registroProyectoBean.getProyecto().setEstacionServicio(null);
			break;
		default:
			break;
		}
	}

	private boolean validarRUCContribuyenteCompleto(ContribuyenteCompleto contribuyenteCompleto) {
		if (contribuyenteCompleto == null || contribuyenteCompleto.getNumeroRuc() == null
				|| contribuyenteCompleto.getNumeroRuc().isEmpty())
			return false;
		return true;
	}

	private String getNombreRazonContribuyente(ContribuyenteCompleto contribuyenteCompleto) {
		if (contribuyenteCompleto.getNombreComercial() != null
				&& !contribuyenteCompleto.getNombreComercial().trim().isEmpty())
			return contribuyenteCompleto.getNombreComercial();
		return contribuyenteCompleto.getRazonSocial();
	}

	private void clearContributorsData() {
		registroProyectoBean.getProyecto().setRefineria(
				isFaseMostrarRefineria() ? registroProyectoBean.getProyecto().getRefineria() : null);
		registroProyectoBean.getProyecto().setInfraestructura(
				isFaseMostrarTransporte() ? registroProyectoBean.getProyecto().getInfraestructura() : null);
		registroProyectoBean.getProyecto().setComercializadora(
				isFaseMostrarComercio() ? registroProyectoBean.getProyecto().getComercializadora() : null);
		registroProyectoBean.getProyecto().setRefineria(
				isFaseMostrarComercio() ? registroProyectoBean.getProyecto().getEstacionServicio() : null);
	}

	private List<String> validateContributorsData() {
		List<String> messages = new ArrayList<String>();
		String result = null;

		clearContributorsData();

		if (isFaseMostrarRefineria() && registroProyectoBean.getProyecto().getRefineria() == null) {
			if (registroProyectoBean.getRefineria() != null) {
				result = consultarDatosSriContribuyente(rucRefineria, registroProyectoBean.getRefineria(), true);
				if (result != null)
					messages.add(result);
			}
		}
		if (isFaseMostrarTransporte() && registroProyectoBean.getProyecto().getInfraestructura() == null) {
			if (registroProyectoBean.getInfraestructura() != null) {
				result = consultarDatosSriContribuyente(rucInfraestructura, registroProyectoBean.getInfraestructura(),
						true);
				if (result != null)
					messages.add(result);
			}
		}
		if (isFaseMostrarComercio() && registroProyectoBean.getProyecto().getComercializadora() == null) {
			if (registroProyectoBean.getComercializadora() != null) {
				result = consultarDatosSriContribuyente(rucComercializadora,
						registroProyectoBean.getComercializadora(), true);
				if (result != null)
					messages.add(result);
			}
		}
		if (isFaseMostrarComercio() && registroProyectoBean.getProyecto().getEstacionServicio() == null) {
			if (registroProyectoBean.getEstacionServicio() != null) {
				result = consultarDatosSriContribuyente(rucEstacionServicio,
						registroProyectoBean.getEstacionServicio(), true);
				if (result != null)
					messages.add(result);
			}
		}

		return messages;
	}

	public void quitarBloque(Bloque bloque) {
		bloque.setSeleccionado(false);
	}

	public boolean isFaseMostrarBloque() {
		if (catalogoActividadesBean.isCategoriaI())
			return false;
		return isCatalogoCategoriaIdPresente(FASES_BLOQUE);
	}

	public boolean isFaseMostrarRefineria() {
		return isCatalogoCategoriaIdPresente(FASES_REFINERIA);
	}

	public boolean isFaseMostrarTransporte() {
		return isCatalogoCategoriaIdPresente(FASES_TRANSPORTE);
	}

	public boolean isFaseMostrarComercio() {
		return isCatalogoCategoriaIdPresente(FASES_COMERCIO);
	}
}
