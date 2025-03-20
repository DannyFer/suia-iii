package ec.gob.ambiente.prevencion.categoria2.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.prevencion.categoria2.bean.DescripcionAreaImplantacionPmaBean;
import ec.gob.ambiente.suia.catalogos.facade.CategoriaIICatalogoBioticoFacade;
import ec.gob.ambiente.suia.catalogos.facade.CategoriaIICatalogoFisicoFacade;
import ec.gob.ambiente.suia.catalogos.facade.CategoriaIICatalogoSocialFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneralBiotico;
import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.CatalogoGeneralSocial;
import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneralBiotico;
import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneralSocial;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * 
 * @author Frank Torres
 * 
 */
@ManagedBean
public class DescripcionAreaImplantacionPmaController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5808108562020597447L;

	@EJB
	private CategoriaIICatalogoFisicoFacade categoriaIICatalogoFisicoFacade;
	@EJB
	private CategoriaIICatalogoSocialFacade categoriaIICatalogoSocialFacade;
	@EJB
	private CategoriaIICatalogoBioticoFacade categoriaIICatalogoBioticoFacade;

	private List<CategoriaIICatalogoGeneralFisico> listaCatalogosFisicos = new ArrayList<CategoriaIICatalogoGeneralFisico>();
	private List<String> listaCatalogosFisicosCodigos = new ArrayList<String>();

	private List<CategoriaIICatalogoGeneralSocial> listaCatalogosSociales = new ArrayList<CategoriaIICatalogoGeneralSocial>();
	private List<String> listaCatalogosSocialesCodigos = new ArrayList<String>();
	private List<CategoriaIICatalogoGeneralBiotico> listaCatalogosBioticos = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
	private List<String> listaCatalogosBioticosCodigos = new ArrayList<String>();

	@Getter
	@Setter
	@ManagedProperty(value = "#{descripcionAreaImplantacionPmaBean}")
	private DescripcionAreaImplantacionPmaBean descripcionAreaImplantacionPmaBean;

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	public void actualizarClima(CatalogoGeneralFisico catalogoActivo) {
		List<CatalogoGeneralFisico> listaClima = descripcionAreaImplantacionPmaBean
				.getClima();
		List<CatalogoGeneralFisico> listaClimaModificada = new ArrayList<CatalogoGeneralFisico>();
		for (CatalogoGeneralFisico clima : listaClima) {
			if (clima.equals(catalogoActivo)) {
				clima.setSeleccionado(true);
			} else {
				clima.setSeleccionado(false);
			}
			listaClimaModificada.add(clima);
		}
		descripcionAreaImplantacionPmaBean.setClima(listaClimaModificada);
	}

	public void actualizarPendienteSuelo(CatalogoGeneralFisico catalogoActivo) {
		List<CatalogoGeneralFisico> lista = descripcionAreaImplantacionPmaBean
				.getPendienteSuelo();
		List<CatalogoGeneralFisico> listaModificada = new ArrayList<CatalogoGeneralFisico>();
		for (CatalogoGeneralFisico catalogo : lista) {
			if (catalogo.equals(catalogoActivo)) {
				catalogo.setSeleccionado(true);
			} else {
				catalogo.setSeleccionado(false);
			}
			listaModificada.add(catalogo);
		}
		descripcionAreaImplantacionPmaBean.setPendienteSuelo(listaModificada);
		;
	}

	public void actualizarDatosTablaSeleccionSimple(
			List<CatalogoGeneralFisico> lista,
			CatalogoGeneralFisico catalogoActivo) {
		System.out.println("...fisico...");
		for (CatalogoGeneralFisico catalogo : lista) {
			if (catalogo.equals(catalogoActivo)) {
				catalogo.setSeleccionado(true);
			} else {
				catalogo.setSeleccionado(false);
			}
		}
	}

	public void actualizarDatosTablaSeleccionSimpleSocial(
			List<CatalogoGeneralSocial> lista,
			CatalogoGeneralSocial catalogoActivo) {
		System.out.println(catalogoActivo.getDescripcion());
		for (CatalogoGeneralSocial catalogo : lista) {
			if (catalogo.equals(catalogoActivo)) {
				System.out.println(catalogo.getDescripcion());
				catalogo.setSeleccionado(true);
			} else {
				System.out.println(catalogo.getDescripcion());
				catalogo.setSeleccionado(false);
			}
		}
	}

	public void actualizarDatosTablaSeleccionSimpleBiotico(
			List<CatalogoGeneralBiotico> lista,
			CatalogoGeneralBiotico catalogoActivo) {
		System.out.println("...biotico...");
		for (CatalogoGeneralBiotico catalogo : lista) {
			if (catalogo.equals(catalogoActivo)) {
				catalogo.setSeleccionado(true);
			} else {
				catalogo.setSeleccionado(false);
			}
		}
	}

	public void actualizarTiposSuelo(CatalogoGeneralFisico catalogoActivo) {
		List<CatalogoGeneralFisico> actualizarTiposSuelo = descripcionAreaImplantacionPmaBean
				.getTipoSuelo();
		List<CatalogoGeneralFisico> listaModificada = new ArrayList<CatalogoGeneralFisico>();
		for (CatalogoGeneralFisico catalogo : actualizarTiposSuelo) {
			if (catalogo.equals(catalogoActivo)) {
				catalogo.setSeleccionado(true);
			} else {
				catalogo.setSeleccionado(false);
			}
			listaModificada.add(catalogo);
		}
		descripcionAreaImplantacionPmaBean.setTipoSuelo(actualizarTiposSuelo);
	}

	// -------
	public void guardarDatos() {
		FichaAmbientalPma fichaAmbientalPma = descripcionAreaImplantacionPmaBean.getFichaAmbientalPma();
		
		guardarDatosCatalogoFisico(
				descripcionAreaImplantacionPmaBean.getClimaActivo(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_CLIMA);

		guardarDatosCatalogoFisico(
				descripcionAreaImplantacionPmaBean.getTipoSueloSeleccionado(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_TIPO_SUELO,
				descripcionAreaImplantacionPmaBean.getTipoSueloOtros());

		guardarDatosCatalogoFisico(
				descripcionAreaImplantacionPmaBean
						.getPendienteSueloSeleccionado(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_PENDIENTE_SUELO);

		guardarDatosCatalogoFisico(
				descripcionAreaImplantacionPmaBean
						.getCondicionesDrenajeActivo(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_CONDICIONES_DRENAJE);

		guardarDatosCatalogoFisicoCompletar(fichaAmbientalPma);
		// social

		guardarDatosCatalogoSocial(
				descripcionAreaImplantacionPmaBean.getDemografia(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_DEMOGRAFIA);
        guardarDatosCatalogoSocial(
                descripcionAreaImplantacionPmaBean.getDemografiaSeleccionado(),
                fichaAmbientalPma, TipoCatalogo.CODIGO_DEMOGRAFIA);

       /* guardarDatosCatalogoSocial(
                descripcionAreaImplantacionPmaBean.getDemografiaActivo(),
                fichaAmbientalPma, TipoCatalogo.CODIGO_DEMOGRAFIA);*/

		guardarDatosCatalogoSocial(
				descripcionAreaImplantacionPmaBean
						.getAbastecimientoAguaPoblacionSeleccionado(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_ABASTECIMIENTO_AGUA);

		guardarDatosCatalogoSocial(
				descripcionAreaImplantacionPmaBean
						.getEvacuacionAguasServidasPonlacionSeleccionado(),
				fichaAmbientalPma,
				TipoCatalogo.CODIGO_EVACUACION_AGUAS_SERVIDAS_POBLACION);

		guardarDatosCatalogoSocial(
				descripcionAreaImplantacionPmaBean
						.getElectrificacionSeleccionado(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_ELECTRIFICACION,
				descripcionAreaImplantacionPmaBean.getElectrificacionOtros());

		guardarDatosCatalogoSocial(
				descripcionAreaImplantacionPmaBean
						.getVialidadAccesoPoblacionSeleccionado(),
				fichaAmbientalPma,
				TipoCatalogo.CODIGO_VIALIDAD_ACCESO_POBLACION,
				descripcionAreaImplantacionPmaBean
						.getVialidadAccesoPoblacionOtros());

		guardarDatosCatalogoSocial(
				descripcionAreaImplantacionPmaBean
						.getOrganizacionSocialSeleccionado(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_ORGANIZACION_SOCIAL);

		guardarDatosCatalogoSocialCompletar(fichaAmbientalPma);

		//
		guardarDatosCatalogoBiotico(
				descripcionAreaImplantacionPmaBean
						.getFormacionVegetalSeleccionado(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_FORMACION_VEGETAL);

		guardarDatosCatalogoBiotico(
				descripcionAreaImplantacionPmaBean.getHabitatSeleccionado(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_HABITAT);

		guardarDatosCatalogoBiotico(
				descripcionAreaImplantacionPmaBean.getTiposBosquesActivo(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_TIPOS_BOSQUES);

		guardarDatosCatalogoBiotico(
				descripcionAreaImplantacionPmaBean
						.getGradoIntervencionCoberturaVegetalActivo(),
				fichaAmbientalPma,
				TipoCatalogo.CODIGO_GRADO_INTERVENCION_COBERTURA_VEGETAL);

		guardarDatosCatalogoBiotico(
				descripcionAreaImplantacionPmaBean
						.getAspectosEcologicosSeleccionado(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS);

		guardarDatosCatalogoBiotico(
				descripcionAreaImplantacionPmaBean.getPisoZoogeograficoActivo(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_PISO_ZOOGEOLOGICO);

		guardarDatosCatalogoBiotico(
				descripcionAreaImplantacionPmaBean
						.getGruposFaunisticoSeleccionado(),
				fichaAmbientalPma, TipoCatalogo.CODIGO_GRUPO_FAUNISTICO);
		guardarDatosCatalogoBiotico(
				descripcionAreaImplantacionPmaBean
						.getSensibilidadPresentadaAreaSeleccionado(),
				fichaAmbientalPma,
				TipoCatalogo.CODIGO_SENSIBILIDAD_PRESENTADA_AREA);

		guardarDatosCatalogoBiotico(
				descripcionAreaImplantacionPmaBean
						.getDatosEcologicosPresentesSeleccionado(),
				fichaAmbientalPma,
				TipoCatalogo.CODIGO_DATOS_ECOLOGICOS_PRESENTES);

		guardarDatosCatalogoBioticoCompletar(fichaAmbientalPma);
		// biotico
		fichaAmbientalPma.setValidarDescripcionAreaImplantacion(true);
		fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbientalPma);

		JsfUtil.addMessageInfo("La operación se realizó satisfactoriamente.");
//		RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
		
		JsfUtil.redirectTo("/prevencion/categoria2/v2/fichaAmbiental/descripcionArea.jsf");
		
	}

	public void guardarTiposSuelo(FichaAmbientalPma fichaAmbientalPma) {
		List<CategoriaIICatalogoGeneralFisico> tiposSuelo = new ArrayList<CategoriaIICatalogoGeneralFisico>();

		for (CatalogoGeneralFisico catalogo : descripcionAreaImplantacionPmaBean
				.getTipoSuelo()) {
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralFisico suelo = new CategoriaIICatalogoGeneralFisico();
				suelo.setCatalogo(catalogo);
				suelo.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				suelo.setFichaAmbientalPma(fichaAmbientalPma);
				tiposSuelo.add(suelo);
				if (catalogo.equals(descripcionAreaImplantacionPmaBean
						.getTipoSueloOtros().getCatalogo())) {
					suelo.setValor(descripcionAreaImplantacionPmaBean
							.getTipoSueloOtros().getValor());
				}
			}
		}
		categoriaIICatalogoFisicoFacade.guardarCatalogoGeneralFisico(
				tiposSuelo, TipoCatalogo.CODIGO_TIPO_SUELO, fichaAmbientalPma,
				true,
				descripcionAreaImplantacionPmaBean.getSeccionDescipcionArea());
	}

	// Social
	/**
	 * Guardar catalogo físico, un solo elemento
	 * 
	 * @param catalogo
	 * @param fichaAmbientalPma
	 * @param codigo
	 */
	public void guardarDatosCatalogoFisico(CatalogoGeneralFisico catalogo,
			FichaAmbientalPma fichaAmbientalPma, String codigo) {

		CategoriaIICatalogoGeneralFisico catalogo_catii = new CategoriaIICatalogoGeneralFisico();
		catalogo_catii.setCatalogo(catalogo);
		catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
				.getSeccionDescipcionArea());
		catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
		listaCatalogosFisicos.add(catalogo_catii);
		listaCatalogosFisicosCodigos.add(codigo);
	}

	public void guardarDatosCatalogoFisicoCompletar(FichaAmbientalPma fichaAmbientalPma) {
		
		categoriaIICatalogoFisicoFacade.guardarCatalogoGeneralFisicoHistorico(
				listaCatalogosFisicos,
				listaCatalogosFisicosCodigos,// código
				fichaAmbientalPma, true,
				descripcionAreaImplantacionPmaBean.getSeccionDescipcionArea());
	}

	/**
	 * Guardar físico, solo seleccionados
	 * 
	 * @param catalogos
	 * @param fichaAmbientalPma
	 * @param codigo
	 */
	public void guardarDatosCatalogoFisico(CatalogoGeneralFisico[] catalogos,
			FichaAmbientalPma fichaAmbientalPma, String codigo) {

		for (CatalogoGeneralFisico catalogo : catalogos) {// obtener el listad
			CategoriaIICatalogoGeneralFisico catalogo_catii = new CategoriaIICatalogoGeneralFisico();
			catalogo_catii.setCatalogo(catalogo);
			catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
					.getSeccionDescipcionArea());
			catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
			// lista.add(catalogo_catii);
			listaCatalogosFisicos.add(catalogo_catii);
			listaCatalogosFisicosCodigos.add(codigo);
		}
	}

	/**
	 * Guardar físico, solo seleccionados, incluye el otro
	 * 
	 * @param catalogos
	 * @param fichaAmbientalPma
	 * @param codigo
	 * @param otros
	 */
	public void guardarDatosCatalogoFisico(CatalogoGeneralFisico[] catalogos,
			FichaAmbientalPma fichaAmbientalPma, String codigo,
			CategoriaIICatalogoGeneralFisico otros) {
		for (CatalogoGeneralFisico catalogo : catalogos) {// obtener el listado
			CategoriaIICatalogoGeneralFisico catalogo_catii = new CategoriaIICatalogoGeneralFisico();
			catalogo_catii.setCatalogo(catalogo);
			catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
					.getSeccionDescipcionArea());
			catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);

			if (catalogo.equals(otros.getCatalogo())) {
				catalogo_catii.setValor(otros.getValor());
			}

			// lista.add(catalogo_catii);
			listaCatalogosFisicos.add(catalogo_catii);
			listaCatalogosFisicosCodigos.add(codigo);

		}
	}

	public void guardarDatosCatalogoFisico(
			List<CatalogoGeneralFisico> catalogos,
			FichaAmbientalPma fichaAmbientalPma, String codigo) {

		for (CatalogoGeneralFisico catalogo : catalogos) {// obtener el listado
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralFisico catalogo_catii = new CategoriaIICatalogoGeneralFisico();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				// lista.add(catalogo_catii);
				listaCatalogosFisicos.add(catalogo_catii);
				listaCatalogosFisicosCodigos.add(codigo);

			}
		}
	}

	public void guardarDatosCatalogoFisico(
			List<CatalogoGeneralFisico> catalogos,
			FichaAmbientalPma fichaAmbientalPma, String codigo,
			CategoriaIICatalogoGeneralFisico otros) {

		for (CatalogoGeneralFisico catalogo : catalogos) {// obtener el listado
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralFisico catalogo_catii = new CategoriaIICatalogoGeneralFisico();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);

				if (catalogo.equals(otros.getCatalogo())) {
					catalogo_catii.setValor(otros.getValor());
				}

				// lista.add(catalogo_catii);
				listaCatalogosFisicos.add(catalogo_catii);
				listaCatalogosFisicosCodigos.add(codigo);

			}
		}
	}

	public void guardarDatosCatalogoSocialCompletar(
			FichaAmbientalPma fichaAmbientalPma) {
		
		categoriaIICatalogoSocialFacade.guardarCatalogoGeneralSocialHistorico(listaCatalogosSociales,
				listaCatalogosSocialesCodigos,// código
				fichaAmbientalPma, true,
				descripcionAreaImplantacionPmaBean.getSeccionDescipcionArea());		
	}

	public void guardarDatosCatalogoSocial(
			List<CatalogoGeneralSocial> catalogos,
			FichaAmbientalPma fichaAmbientalPma, String codigo) {

		for (CatalogoGeneralSocial catalogo : catalogos) {// obtener el listado
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralSocial catalogo_catii = new CategoriaIICatalogoGeneralSocial();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				// lista.add(catalogo_catii);
				listaCatalogosSociales.add(catalogo_catii);
				listaCatalogosSocialesCodigos.add(codigo);

			}
		}
	}

	/**
	 * Guardar catalogo físico, un solo elemento
	 * 
	 * @param catalogo
	 * @param fichaAmbientalPma
	 * @param codigo
	 */
	public void guardarDatosCatalogoSocial(CatalogoGeneralSocial catalogo,
			FichaAmbientalPma fichaAmbientalPma, String codigo) {

		CategoriaIICatalogoGeneralSocial catalogo_catii = new CategoriaIICatalogoGeneralSocial();
		catalogo_catii.setCatalogo(catalogo);
		catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
				.getSeccionDescipcionArea());
		catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
		// lista.add(catalogo_catii);
		listaCatalogosSociales.add(catalogo_catii);
		listaCatalogosSocialesCodigos.add(codigo);
	}

	/**
	 * 
	 * @param catalogos
	 * @param fichaAmbientalPma
	 * @param codigo
	 */
	public void guardarDatosCatalogoSocial(CatalogoGeneralSocial[] catalogos,
			FichaAmbientalPma fichaAmbientalPma, String codigo) {

		for (CatalogoGeneralSocial catalogo : catalogos) {// obtener el listado
			CategoriaIICatalogoGeneralSocial catalogo_catii = new CategoriaIICatalogoGeneralSocial();
			catalogo_catii.setCatalogo(catalogo);
			catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
					.getSeccionDescipcionArea());
			catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
			// lista.add(catalogo_catii);
			listaCatalogosSociales.add(catalogo_catii);
			listaCatalogosSocialesCodigos.add(codigo);

		}
	}

	/**
	 * 
	 * @param catalogos
	 * @param fichaAmbientalPma
	 * @param codigo
	 * @param otros
	 */
	public void guardarDatosCatalogoSocial(CatalogoGeneralSocial[] catalogos,
			FichaAmbientalPma fichaAmbientalPma, String codigo,
			CategoriaIICatalogoGeneralSocial otros) {

		for (CatalogoGeneralSocial catalogo : catalogos) {// obtener el listado
			CategoriaIICatalogoGeneralSocial catalogo_catii = new CategoriaIICatalogoGeneralSocial();
			catalogo_catii.setCatalogo(catalogo);
			catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
					.getSeccionDescipcionArea());
			catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
			if (catalogo.equals(otros.getCatalogo())) {
				catalogo_catii.setValor(otros.getValor());
			}

			// lista.add(catalogo_catii);
			listaCatalogosSociales.add(catalogo_catii);
			listaCatalogosSocialesCodigos.add(codigo);
		}
	}

	public void guardarDatosCatalogoSocial(
			List<CatalogoGeneralSocial> catalogos,
			FichaAmbientalPma fichaAmbientalPma, String codigo,
			CategoriaIICatalogoGeneralSocial otros) {

		for (CatalogoGeneralSocial catalogo : catalogos) {// obtener el listado
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralSocial catalogo_catii = new CategoriaIICatalogoGeneralSocial();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				if (catalogo.equals(otros.getCatalogo())) {
					catalogo_catii.setValor(otros.getValor());
				}

				// lista.add(catalogo_catii);
				listaCatalogosSociales.add(catalogo_catii);
				listaCatalogosSocialesCodigos.add(codigo);
			}
		}
	}

	// /biotico

	public void guardarDatosCatalogoBioticoCompletar(
			FichaAmbientalPma fichaAmbientalPma) {
		
		categoriaIICatalogoBioticoFacade.guardarCatalogoGeneralBioticoHistorico(
				listaCatalogosBioticos,
				listaCatalogosBioticosCodigos,// código
				fichaAmbientalPma, true,
				descripcionAreaImplantacionPmaBean.getSeccionDescipcionArea());
	}

	/**
	 * 
	 * @param catalogos
	 * @param fichaAmbientalPma
	 * @param codigo
	 */
	public void guardarDatosCatalogoBiotico(CatalogoGeneralBiotico[] catalogos,
			FichaAmbientalPma fichaAmbientalPma, String codigo) {

		for (CatalogoGeneralBiotico catalogo : catalogos) {// obtener el listado
			CategoriaIICatalogoGeneralBiotico catalogo_catii = new CategoriaIICatalogoGeneralBiotico();
			catalogo_catii.setCatalogo(catalogo);
			catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
					.getSeccionDescipcionArea());
			catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
			// lista.add(catalogo_catii);
			listaCatalogosBioticos.add(catalogo_catii);
			listaCatalogosBioticosCodigos.add(codigo);

		}
	}

	/**
	 * 
	 * @param catalogos
	 * @param fichaAmbientalPma
	 * @param codigo
	 */
	public void guardarDatosCatalogoBiotico(CatalogoGeneralBiotico catalogo,
			FichaAmbientalPma fichaAmbientalPma, String codigo) {

		CategoriaIICatalogoGeneralBiotico catalogo_catii = new CategoriaIICatalogoGeneralBiotico();
		catalogo_catii.setCatalogo(catalogo);
		catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
				.getSeccionDescipcionArea());
		catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
		// lista.add(catalogo_catii);
		listaCatalogosBioticos.add(catalogo_catii);
		listaCatalogosBioticosCodigos.add(codigo);
	}

	public void guardarDatosCatalogoBiotico(
			List<CatalogoGeneralBiotico> catalogos,
			FichaAmbientalPma fichaAmbientalPma, String codigo) {

		for (CatalogoGeneralBiotico catalogo : catalogos) {// obtener el listado
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralBiotico catalogo_catii = new CategoriaIICatalogoGeneralBiotico();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				// lista.add(catalogo_catii);
				listaCatalogosBioticos.add(catalogo_catii);
				listaCatalogosBioticosCodigos.add(codigo);
			}
		}
	}

	public void guardarDatosCatalogoBiotico(
			List<CatalogoGeneralBiotico> catalogos,
			FichaAmbientalPma fichaAmbientalPma, String codigo,
			CategoriaIICatalogoGeneralBiotico otros) {

		for (CatalogoGeneralBiotico catalogo : catalogos) {// obtener el listado
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralBiotico catalogo_catii = new CategoriaIICatalogoGeneralBiotico();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				if (catalogo.equals(otros.getCatalogo())) {
					catalogo_catii.setValor(otros.getValor());
				}
				// lista.add(catalogo_catii);
				listaCatalogosBioticos.add(catalogo_catii);
				listaCatalogosBioticosCodigos.add(codigo);
			}
		}
	}

	public void guardarDemografia(FichaAmbientalPma fichaAmbientalPma) {

		for (CatalogoGeneralSocial catalogo : descripcionAreaImplantacionPmaBean
				.getDemografia()) {
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralSocial catalogo_catii = new CategoriaIICatalogoGeneralSocial();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				// lista.add(catalogo_catii);
				listaCatalogosSociales.add(catalogo_catii);
				listaCatalogosSocialesCodigos
						.add(TipoCatalogo.CODIGO_DEMOGRAFIA);
			}
		}
	}

	public void guardarAbastecimientoAguaPoblacion(
			FichaAmbientalPma fichaAmbientalPma) {

		for (CatalogoGeneralSocial catalogo : descripcionAreaImplantacionPmaBean
				.getAbastecimientoAguaPoblacion()) {
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralSocial catalogo_catii = new CategoriaIICatalogoGeneralSocial();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				// lista.add(catalogo_catii);
				listaCatalogosSociales.add(catalogo_catii);
				listaCatalogosSocialesCodigos
						.add(TipoCatalogo.CODIGO_ABASTECIMIENTO_AGUA);

			}
		}
	}

	public void guardarEvacuacionAguasServidasPonlacion(
			FichaAmbientalPma fichaAmbientalPma) {

		for (CatalogoGeneralSocial catalogo : descripcionAreaImplantacionPmaBean
				.getEvacuacionAguasServidasPonlacion()) {// obtener el listado
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralSocial catalogo_catii = new CategoriaIICatalogoGeneralSocial();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				// lista.add(catalogo_catii);
				listaCatalogosSociales.add(catalogo_catii);
				listaCatalogosSocialesCodigos
						.add(TipoCatalogo.CODIGO_EVACUACION_AGUAS_SERVIDAS_POBLACION);

			}
		}
	}

	public void guardarElectrificacion(FichaAmbientalPma fichaAmbientalPma) {

		for (CatalogoGeneralSocial catalogo : descripcionAreaImplantacionPmaBean
				.getElectrificacion()) {// obtener el listado
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralSocial catalogo_catii = new CategoriaIICatalogoGeneralSocial();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);

				if (catalogo.equals(descripcionAreaImplantacionPmaBean
						.getElectrificacionOtros().getCatalogo())) {
					catalogo_catii.setValor(descripcionAreaImplantacionPmaBean
							.getElectrificacionOtros().getValor());
				}
				// lista.add(catalogo_catii);
				listaCatalogosSociales.add(catalogo_catii);
				listaCatalogosSocialesCodigos
						.add(TipoCatalogo.CODIGO_ELECTRIFICACION);
			}
		}
	}

	public void guardarVialidadAccesoPoblacion(
			FichaAmbientalPma fichaAmbientalPma) {

		for (CatalogoGeneralSocial catalogo : descripcionAreaImplantacionPmaBean
				.getVialidadAccesoPoblacion()) {// obtener el listado
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralSocial catalogo_catii = new CategoriaIICatalogoGeneralSocial();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				if (catalogo.equals(descripcionAreaImplantacionPmaBean
						.getVialidadAccesoPoblacionOtros().getCatalogo())) {
					catalogo_catii.setValor(descripcionAreaImplantacionPmaBean
							.getVialidadAccesoPoblacionOtros().getValor());
				}

				// lista.add(catalogo_catii);
				listaCatalogosSociales.add(catalogo_catii);
				listaCatalogosSocialesCodigos
						.add(TipoCatalogo.CODIGO_VIALIDAD_ACCESO_POBLACION);

			}
		}
	}

	public void guardarOrganizacionSocial(FichaAmbientalPma fichaAmbientalPma) {

		for (CatalogoGeneralSocial catalogo : descripcionAreaImplantacionPmaBean
				.getOrganizacionSocial()) {// obtener el listado
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralSocial catalogo_catii = new CategoriaIICatalogoGeneralSocial();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				// lista.add(catalogo_catii);
				listaCatalogosSociales.add(catalogo_catii);
				listaCatalogosSocialesCodigos
						.add(TipoCatalogo.CODIGO_ORGANIZACION_SOCIAL);
			}
		}
	}

	// /biotico
	public void guardarFormacionVegetal(FichaAmbientalPma fichaAmbientalPma) {

		for (CatalogoGeneralBiotico catalogo : descripcionAreaImplantacionPmaBean
				.getFormacionVegetal()) {
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralBiotico catalogo_catii = new CategoriaIICatalogoGeneralBiotico();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				// lista.add(catalogo_catii);
				listaCatalogosBioticos.add(catalogo_catii);
				listaCatalogosBioticosCodigos
						.add(TipoCatalogo.CODIGO_FORMACION_VEGETAL);

			}
		}
	}

	public void guardarHabitat(FichaAmbientalPma fichaAmbientalPma) {

		for (CatalogoGeneralBiotico catalogo : descripcionAreaImplantacionPmaBean
				.getHabitat()) {//
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralBiotico catalogo_catii = new CategoriaIICatalogoGeneralBiotico();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				// lista.add(catalogo_catii);
				listaCatalogosBioticos.add(catalogo_catii);
				listaCatalogosBioticosCodigos.add(TipoCatalogo.CODIGO_HABITAT);

			}
		}
	}

	public void guardarTiposBosques(FichaAmbientalPma fichaAmbientalPma) {

		for (CatalogoGeneralBiotico catalogo : descripcionAreaImplantacionPmaBean
				.getTiposBosques()) {//
			if (catalogo.isSeleccionado()) {
				CategoriaIICatalogoGeneralBiotico catalogo_catii = new CategoriaIICatalogoGeneralBiotico();
				catalogo_catii.setCatalogo(catalogo);
				catalogo_catii.setSeccion(descripcionAreaImplantacionPmaBean
						.getSeccionDescipcionArea());
				catalogo_catii.setFichaAmbientalPma(fichaAmbientalPma);
				// lista.add(catalogo_catii);
				listaCatalogosBioticos.add(catalogo_catii);
				listaCatalogosBioticosCodigos
						.add(TipoCatalogo.CODIGO_TIPOS_BOSQUES);
			}
		}
	}

	public Boolean campoRequerido(EntidadBase[] lista, Boolean activo) {
		if (!activo) {
			return false;
		}
		if (lista != null && lista.length > 0) {
			return false;
		}
		return true;
	}

	public Boolean campoFisicoRequerido(List<CatalogoGeneralFisico> lista) {
		for (CatalogoGeneralFisico catalogo : lista) {
			if (catalogo.isSeleccionado()) {
				return false;
			}
		}
		return true;
	}

	public Boolean campoFisicoRequerido(List<CatalogoGeneralFisico> lista,
			Boolean render) {
		if (render) {
			return campoFisicoRequerido(lista);
		}
		return false;
	}

	public Boolean campoSocialRequerido(List<CatalogoGeneralSocial> lista) {
		for (CatalogoGeneralSocial catalogo : lista) {
			if (catalogo.isSeleccionado()) {
				return false;
			}
		}
		return true;
	}

	public Boolean campoSocialRequerido(List<CatalogoGeneralSocial> lista,
			Boolean render) {
		if (render) {
			return campoSocialRequerido(lista);
		}
		return false;
	}

	public Boolean campoBioticoRequerido(List<CatalogoGeneralBiotico> lista) {
		for (CatalogoGeneralBiotico catalogo : lista) {
			if (catalogo.isSeleccionado()) {
				return false;
			}
		}
		return true;
	}

	public Boolean campoBioticoRequerido(List<CatalogoGeneralBiotico> lista,
			Boolean render) {
		if (render) {
			return campoBioticoRequerido(lista);
		}
		return false;
	}

	public Boolean campoOtrosVisible(EntidadBase campo, EntidadBase otros) {

		if (campo.isSeleccionado() && campo.equals(otros)) {
			return true;
		}
		return false;
	}

	public Boolean campoOtrosVisibleFisico(CatalogoGeneralFisico[] campos,
			CatalogoGeneralFisico otros) {

		for (CatalogoGeneralFisico catalogo : campos) {
			if (catalogo.equals(otros)) {
				return true;
			}
		}
		return false;
	}

	public Boolean campoOtrosVisibleSocial(CatalogoGeneralSocial[] campos,
			CatalogoGeneralSocial otros) {

		for (CatalogoGeneralSocial catalogo : campos) {
			if (catalogo.equals(otros)) {
				return true;
			}
		}
		return false;
	}

}