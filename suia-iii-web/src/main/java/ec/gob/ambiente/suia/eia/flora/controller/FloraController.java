package ec.gob.ambiente.suia.eia.flora.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.FloraEspecie;
import ec.gob.ambiente.suia.domain.FloraGeneral;
import ec.gob.ambiente.suia.domain.PuntosMuestreoFlora;
import ec.gob.ambiente.suia.eia.flora.bean.FloraBean;
import ec.gob.ambiente.suia.eia.flora.bean.FloraCualitativoBean;
import ec.gob.ambiente.suia.eia.flora.bean.FloraCualitativoEspecieBean;
import ec.gob.ambiente.suia.eia.flora.bean.FloraCuantitativoBean;
import ec.gob.ambiente.suia.eia.flora.bean.FloraCuantitativoEspecieBean;
import ec.gob.ambiente.suia.eia.flora.facade.FloraFacade;

public class FloraController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1764872257248934017L;

	private static final Logger LOG = Logger.getLogger(FloraController.class);

	@EJB
	FloraFacade floraFacade;
	@EJB
	CatalogoGeneralFacade catalogoGeneralFacade;

	public void guardarFloraCuanlitativo(FloraBean floraBean) throws Exception {
		FloraGeneral floraGeneral = null;
		FloraEspecie floraEspecie = null;
		PuntosMuestreoFlora puntoMuestreoFlora = null;
		CoordenadaGeneral coordenadaGeneral = null;
		List<CoordenadaGeneral> listaCoordenadas = null;
		List<PuntosMuestreoFlora> listaPuntosMuestreo = null;
		List<FloraEspecie> listaFloraEspecies = null;

		try {
			floraGeneral = new FloraGeneral();
			floraGeneral.setId(floraBean.getId());
			floraGeneral.setAnexoMetodologia(floraBean
					.getAnexoMetodologiaName());
			floraGeneral.setFileAnexo(floraBean.getAnexoMetodologia());
			floraGeneral.setContentType(floraBean
					.getAnexoMetodologiaContentType());
			floraGeneral.setEsfuerzoMuestreo(floraBean.getEsfuerzoMuestreo());
			floraGeneral.setEstado(true);
			floraGeneral.setMetodologia(floraBean.getMetodologia().getId());
			floraGeneral.setTipoMuestreo(floraBean.getTipoMuestreo().getId());
			floraGeneral.setArchivoEditado(floraBean.isArchivoEditado());
			floraGeneral.setEiaId(floraBean.getEiaId());
			
			listaPuntosMuestreo = new ArrayList<PuntosMuestreoFlora>();
			for (FloraCualitativoBean puntoMuestreo : floraBean
					.getFloraCualitativos()) {
				puntoMuestreoFlora = new PuntosMuestreoFlora();
				puntoMuestreoFlora.setId(puntoMuestreo.getId());
				puntoMuestreoFlora.setEsfuerzoMuestreo(floraBean
						.getEsfuerzoMuestreo());
				puntoMuestreoFlora.setEstado(true);
				puntoMuestreoFlora.setFechaMuestreo(puntoMuestreo
						.getFechaMuestreo());
				puntoMuestreoFlora
						.setIdCatalogoTipoMuestreo(CatalogoGeneral.CUALITATIVO);
				puntoMuestreoFlora.setTipoVegetacion(puntoMuestreo
						.getTipoVegetacion().getId());
				puntoMuestreoFlora.setPuntoMuestreo(puntoMuestreo
						.getPuntoMuestreo());

				listaCoordenadas = new ArrayList<CoordenadaGeneral>();

				coordenadaGeneral = new CoordenadaGeneral();
				coordenadaGeneral.setId(puntoMuestreo.getP1Id());
				coordenadaGeneral.setDescripcion("EIA - Flora "
						+ floraBean.getTipoMuestreo().getDescripcion());
				coordenadaGeneral.setEstado(true);
				coordenadaGeneral.setNombreTabla(puntoMuestreo.getClass()
						.getCanonicalName());
				coordenadaGeneral.setX(new BigDecimal(puntoMuestreo.getPx1()));
				coordenadaGeneral.setY(new BigDecimal(puntoMuestreo.getPy1()
						.floatValue()));
				coordenadaGeneral.setZ(new BigDecimal(puntoMuestreo
						.getAltitud1().floatValue()));
				coordenadaGeneral.setOrden(1);

				listaCoordenadas.add(coordenadaGeneral);

				if (puntoMuestreo.getPx2() != null
						&& puntoMuestreo.getPx2() > 0) {
					coordenadaGeneral = new CoordenadaGeneral();
					coordenadaGeneral.setId(puntoMuestreo.getP2Id());
					coordenadaGeneral.setDescripcion("EIA - Flora "
							+ floraBean.getTipoMuestreo().getDescripcion());
					coordenadaGeneral.setEstado(true);
					coordenadaGeneral.setNombreTabla(puntoMuestreo.getClass()
							.getCanonicalName());
					coordenadaGeneral.setX(new BigDecimal(puntoMuestreo
							.getPx2().floatValue()));
					coordenadaGeneral.setY(new BigDecimal(puntoMuestreo
							.getPy2().floatValue()));
					coordenadaGeneral.setZ(new BigDecimal(puntoMuestreo
							.getAltitud2().floatValue()));
					coordenadaGeneral.setOrden(2);
					listaCoordenadas.add(coordenadaGeneral);
				}
				if (puntoMuestreo.getPx3() != null
						&& puntoMuestreo.getPx3() > 0) {
					coordenadaGeneral = new CoordenadaGeneral();
					coordenadaGeneral.setId(puntoMuestreo.getP3Id());
					coordenadaGeneral.setDescripcion("EIA - Flora "
							+ floraBean.getTipoMuestreo().getDescripcion());
					coordenadaGeneral.setEstado(true);
					coordenadaGeneral.setNombreTabla(puntoMuestreo.getClass()
							.getCanonicalName());
					coordenadaGeneral.setX(new BigDecimal(puntoMuestreo
							.getPx3().floatValue()));
					coordenadaGeneral.setY(new BigDecimal(puntoMuestreo
							.getPy3().floatValue()));
					coordenadaGeneral.setZ(new BigDecimal(puntoMuestreo
							.getAltitud3().floatValue()));
					coordenadaGeneral.setOrden(3);
					listaCoordenadas.add(coordenadaGeneral);
				}
				if (puntoMuestreo.getPx4() != null
						&& puntoMuestreo.getPx4() > 0) {
					coordenadaGeneral = new CoordenadaGeneral();
					coordenadaGeneral.setId(puntoMuestreo.getP4Id());
					coordenadaGeneral.setDescripcion("EIA - Flora "
							+ floraBean.getTipoMuestreo().getDescripcion());
					coordenadaGeneral.setEstado(true);
					coordenadaGeneral.setNombreTabla(puntoMuestreo.getClass()
							.getCanonicalName());
					coordenadaGeneral.setX(new BigDecimal(puntoMuestreo
							.getPx4().floatValue()));
					coordenadaGeneral.setY(new BigDecimal(puntoMuestreo
							.getPy4().floatValue()));
					coordenadaGeneral.setZ(new BigDecimal(puntoMuestreo
							.getAltitud4().floatValue()));
					coordenadaGeneral.setOrden(4);
					listaCoordenadas.add(coordenadaGeneral);
				}

				listaFloraEspecies = new ArrayList<FloraEspecie>();
				for (FloraCualitativoEspecieBean especie : floraBean
						.getFloraCualitativosEspecie()) {
					if (puntoMuestreo.getPuntoMuestreo().equals(
							especie.getFloraCualitativo().getPuntoMuestreo())) {
						floraEspecie = new FloraEspecie();
						floraEspecie.setId(especie.getId());
						floraEspecie.setCentroTenencia(especie
								.getCentroTenencia());
						floraEspecie.setCities(especie.getCities().getId());
						floraEspecie.setDescripcionFoto(especie
								.getDescripcionFoto());
						floraEspecie.setEstado(true);
						floraEspecie.setEstadoIndividuo(especie
								.getEstadoIndividuo().getId());
						floraEspecie.setFamilia(especie.getFamilia());
						floraEspecie.setGenero(especie.getGenero());
						floraEspecie.setHabito(especie.getHabito().getId());
						floraEspecie.setLibroRojo(especie.getLibroRojo()
								.getId());
						floraEspecie.setNivelIdentificacion(especie
								.getNivelIdentificacion().getId());
						floraEspecie.setNombreCientifico(especie
								.getNombreCientifico());
						floraEspecie.setNombreComun(especie.getNombreComun());
						floraEspecie.setNumeroColeccion(especie
								.getNroColeccion());
						floraEspecie.setOrigen(especie.getOrigen().getId());
						floraEspecie.setPuntoMuestreo(especie
								.getFloraCualitativo().getPuntoMuestreo());
						floraEspecie.setTipoVegetacion(especie
								.getTipoVegetacion().getId());
						floraEspecie.setUicnInternacional(especie
								.getUicnInternacional().getId());
						floraEspecie.setArchivoEditado(especie.isArchivoEditado());
						floraEspecie.setUso(especie.getUso().getId());
						floraEspecie.setEspecie(especie.getEspecie());
						if (especie.getFotoEspecie() != null) {
							floraEspecie.setAnexoFoto(especie
									.getFotoEspecieName());
							floraEspecie.setFotoEspecie(especie
									.getFotoEspecie());
							floraEspecie.setContentType(especie
									.getFotoEspecieContentType());
							floraEspecie.setDescripcionFoto(especie
									.getDescripcionFoto());
						}

						listaFloraEspecies.add(floraEspecie);
					}
				}

				puntoMuestreoFlora.setListaCoordenadas(listaCoordenadas);
				puntoMuestreoFlora.setListaFloraEspecie(listaFloraEspecies);
				listaPuntosMuestreo.add(puntoMuestreoFlora);

			}

			floraGeneral.setListaPuntosMuestreo(listaPuntosMuestreo);
			Integer floraId = floraFacade.guardarFlora(floraGeneral,
					floraBean.getEliminados());
			floraBean.setId(floraId);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e);
		}

	}

	public void guardarFloraCuantitativo(FloraBean floraBean) throws Exception {
		FloraGeneral floraGeneral = null;
		FloraEspecie floraEspecie = null;
		PuntosMuestreoFlora puntoMuestreoFlora = null;
		CoordenadaGeneral coordenadaGeneral = null;
		List<CoordenadaGeneral> listaCoordenadas = null;
		List<PuntosMuestreoFlora> listaPuntosMuestreo = null;
		List<FloraEspecie> listaFloraEspecies = null;

		try {
			floraGeneral = new FloraGeneral();
			floraGeneral.setId(floraBean.getId());
			floraGeneral.setAnexoMetodologia(floraBean
					.getAnexoMetodologiaName());
			floraGeneral.setFileAnexo(floraBean.getAnexoMetodologia());
			floraGeneral.setContentType(floraBean
					.getAnexoMetodologiaContentType());
			floraGeneral.setEsfuerzoMuestreo(floraBean.getEsfuerzoMuestreo());
			floraGeneral.setEstado(true);
			floraGeneral.setMetodologia(floraBean.getMetodologia().getId());
			floraGeneral.setTipoMuestreo(floraBean.getTipoMuestreo().getId());
			floraGeneral.setArchivoEditado(floraBean.isArchivoEditado());
			floraGeneral.setEiaId(floraBean.getEiaId());
			
			listaPuntosMuestreo = new ArrayList<PuntosMuestreoFlora>();
			for (FloraCuantitativoBean puntoMuestreo : floraBean
					.getFloraCuantitativos()) {
				puntoMuestreoFlora = new PuntosMuestreoFlora();
				puntoMuestreoFlora.setId(puntoMuestreo.getId());
				puntoMuestreoFlora.setEstado(true);
				puntoMuestreoFlora.setFechaMuestreo(puntoMuestreo
						.getFechaMuestreo());
				puntoMuestreoFlora
						.setIdCatalogoTipoMuestreo(CatalogoGeneral.CUANTITATIVO);
				puntoMuestreoFlora.setTipoVegetacion(puntoMuestreo
						.getTipoVegetacion().getId());
				puntoMuestreoFlora.setEsfuerzoMuestreo(puntoMuestreo
						.getEsfuerzoMuestreo());
				puntoMuestreoFlora.setPuntoMuestreo(puntoMuestreo
						.getPuntoMuestreo());

				listaCoordenadas = new ArrayList<CoordenadaGeneral>();

				coordenadaGeneral = new CoordenadaGeneral();
				coordenadaGeneral.setId(puntoMuestreo.getP1Id());
				coordenadaGeneral.setDescripcion("EIA - Flora "
						+ floraBean.getTipoMuestreo().getDescripcion());
				coordenadaGeneral.setEstado(true);
				coordenadaGeneral.setNombreTabla(puntoMuestreo.getClass()
						.getCanonicalName());
				coordenadaGeneral.setX(new BigDecimal(puntoMuestreo.getPx1()));
				coordenadaGeneral.setY(new BigDecimal(puntoMuestreo.getPy1()
						.floatValue()));
				coordenadaGeneral.setOrden(1);
				coordenadaGeneral.setZ(new BigDecimal(puntoMuestreo
						.getAltitud1().floatValue()));
				listaCoordenadas.add(coordenadaGeneral);

				if (puntoMuestreo.getPx2() != null
						&& puntoMuestreo.getPx2() > 0) {
					coordenadaGeneral = new CoordenadaGeneral();
					coordenadaGeneral.setId(puntoMuestreo.getP2Id());
					coordenadaGeneral.setDescripcion("EIA - Flora "
							+ floraBean.getTipoMuestreo().getDescripcion());
					coordenadaGeneral.setEstado(true);
					coordenadaGeneral.setNombreTabla(puntoMuestreo.getClass()
							.getCanonicalName());
					coordenadaGeneral.setX(new BigDecimal(puntoMuestreo
							.getPx2().floatValue()));
					coordenadaGeneral.setY(new BigDecimal(puntoMuestreo
							.getPy2().floatValue()));
					coordenadaGeneral.setZ(new BigDecimal(puntoMuestreo
							.getAltitud2().floatValue()));
					coordenadaGeneral.setOrden(2);
					listaCoordenadas.add(coordenadaGeneral);
				}
				if (puntoMuestreo.getPx3() != null
						&& puntoMuestreo.getPx3() > 0) {
					coordenadaGeneral = new CoordenadaGeneral();
					coordenadaGeneral.setId(puntoMuestreo.getP3Id());
					coordenadaGeneral.setDescripcion("EIA - Flora "
							+ floraBean.getTipoMuestreo().getDescripcion());
					coordenadaGeneral.setEstado(true);
					coordenadaGeneral.setNombreTabla(puntoMuestreo.getClass()
							.getCanonicalName());
					coordenadaGeneral.setX(new BigDecimal(puntoMuestreo
							.getPx3().floatValue()));
					coordenadaGeneral.setY(new BigDecimal(puntoMuestreo
							.getPy3().floatValue()));
					coordenadaGeneral.setZ(new BigDecimal(puntoMuestreo
							.getAltitud3().floatValue()));
					coordenadaGeneral.setOrden(3);
					listaCoordenadas.add(coordenadaGeneral);
				}
				if (puntoMuestreo.getPx4() != null
						&& puntoMuestreo.getPx4() > 0) {
					coordenadaGeneral = new CoordenadaGeneral();
					coordenadaGeneral.setId(puntoMuestreo.getP4Id());
					coordenadaGeneral.setDescripcion("EIA - Flora "
							+ floraBean.getTipoMuestreo().getDescripcion());
					coordenadaGeneral.setEstado(true);
					coordenadaGeneral.setNombreTabla(puntoMuestreo.getClass()
							.getCanonicalName());
					coordenadaGeneral.setX(new BigDecimal(puntoMuestreo
							.getPx4().floatValue()));
					coordenadaGeneral.setY(new BigDecimal(puntoMuestreo
							.getPy4().floatValue()));
					coordenadaGeneral.setZ(new BigDecimal(puntoMuestreo
							.getAltitud4().floatValue()));
					coordenadaGeneral.setOrden(4);
					listaCoordenadas.add(coordenadaGeneral);
				}

				listaFloraEspecies = new ArrayList<FloraEspecie>();

				for (FloraCuantitativoEspecieBean especie : floraBean
						.getFloraCuantitativosEspecie()) {
					if (puntoMuestreo.getPuntoMuestreo().equals(
							especie.getFloraCuantitativo().getPuntoMuestreo())) {
						floraEspecie = new FloraEspecie();
						floraEspecie.setId(especie.getId());
						floraEspecie.setCentroTenencia(especie
								.getCentroTenencia());
						floraEspecie.setCities(especie.getCities().getId());
						floraEspecie.setEstado(true);
						floraEspecie.setEstadoIndividuo(especie
								.getEstadoIndividuo().getId());
						floraEspecie.setFamilia(especie.getFamilia());
						floraEspecie.setGenero(especie.getGenero());
						floraEspecie.setHabito(especie.getHabito().getId());
						floraEspecie.setLibroRojo(especie.getLibroRojo()
								.getId());
						floraEspecie.setNivelIdentificacion(especie
								.getNivelIdentificacion().getId());
						floraEspecie.setNombreCientifico(especie
								.getNombreCientifico());
						floraEspecie.setNombreComun(especie.getNombreComun());
						floraEspecie.setNumeroColeccion(especie
								.getNroColeccion());
						floraEspecie.setOrigen(especie.getOrigen().getId());
						floraEspecie.setPuntoMuestreo(especie
								.getFloraCuantitativo().getPuntoMuestreo());
						floraEspecie.setTipoVegetacion(especie
								.getTipoVegetacion().getId());
						floraEspecie.setUicnInternacional(especie
								.getUicnInternacional().getId());
						floraEspecie.setArchivoEditado(especie.isArchivoEditado());
						floraEspecie.setUso(especie.getUso().getId());
						floraEspecie.setEspecie(especie.getEspecie());
						if (especie.getFotoEspecie() != null) {
							floraEspecie.setAnexoFoto(especie
									.getFotoEspecieName());
							floraEspecie.setFotoEspecie(especie
									.getFotoEspecie());
							floraEspecie.setContentType(especie
									.getFotoEspecieContentType());
							floraEspecie.setDescripcionFoto(especie
									.getDescripcionFoto());
						}
						floraEspecie
								.setAlturaComercial(especie.getAComercial());
						floraEspecie.setAlturaTotal(especie.getATotal());
						floraEspecie.setDap(especie.getDap());

						listaFloraEspecies.add(floraEspecie);
					}
				}

				puntoMuestreoFlora.setListaCoordenadas(listaCoordenadas);
				puntoMuestreoFlora.setListaFloraEspecie(listaFloraEspecies);
				listaPuntosMuestreo.add(puntoMuestreoFlora);

			}

			floraGeneral.setListaPuntosMuestreo(listaPuntosMuestreo);
			Integer floraId = floraFacade.guardarFlora(floraGeneral,
					floraBean.getEliminados());
			floraBean.setId(floraId);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e);
		}

	}

	public FloraBean cargarDatosFlora(Integer id) throws Exception {
		FloraGeneral floraGeneral = floraFacade.obtenerFlora(id);
		FloraBean floraBean = new FloraBean();
		// floraBean.setAnexoMetodologia(floraGeneral.getAnexoMetodologia());
		floraBean.setEsfuerzoMuestreo(floraGeneral.getEsfuerzoMuestreo());
		floraBean.setId(floraGeneral.getId());
		floraBean.setMetodologia(catalogoGeneralFacade
				.obtenerCatalogoXId(floraGeneral.getMetodologia()));
		floraBean.setTipoMuestreo(catalogoGeneralFacade
				.obtenerCatalogoXId(floraGeneral.getTipoMuestreo()));
		floraBean.setAnexoMetodologia(floraGeneral.getFileAnexo());
		floraBean.setAnexoMetodologiaContentType(floraGeneral.getContentType());
		floraBean.setAnexoMetodologiaName(floraGeneral.getAnexoMetodologia());
		return floraBean;
	}

	public FloraBean cargarDatosPuntoMuestreo(Integer floraId) throws Exception {
		FloraBean floraBean = new FloraBean();
		List<PuntosMuestreoFlora> puntosMuestreoFloras = floraFacade
				.obtenerPuntosMuestreo(floraId);

		List<FloraCualitativoBean> floraCualitativoBeans = new ArrayList<FloraCualitativoBean>();
		List<FloraCuantitativoBean> floraCuantitativoBeans = new ArrayList<FloraCuantitativoBean>();
		List<FloraCuantitativoEspecieBean> floraCuantitativoEspecieBeans = new ArrayList<FloraCuantitativoEspecieBean>();
		List<FloraCualitativoEspecieBean> floraCualitativoEspecieBeans = new ArrayList<FloraCualitativoEspecieBean>();

		int i = 0;
		int j = 0;
		for (PuntosMuestreoFlora punto : puntosMuestreoFloras) {
			if (punto.getIdCatalogoTipoMuestreo().equals(
					CatalogoGeneral.CUALITATIVO)) {
				FloraCualitativoBean floraCualitativoBean = new FloraCualitativoBean(
						punto.getId(), punto.getPuntoMuestreo(), i++, null,
						null, null, null, null, null, null, null, null, null,
						null, null,
						catalogoGeneralFacade.obtenerCatalogoXId(punto
								.getTipoVegetacion()), punto.getFechaMuestreo());

				int k = 1;
				for (CoordenadaGeneral coordenada : punto.getListaCoordenadas()) {
					if (coordenada.getX() != null) {
						if (k == 1) {
							floraCualitativoBean.setPx1(coordenada.getX()
									.doubleValue());
							floraCualitativoBean.setPy1(coordenada.getY()
									.doubleValue());
							floraCualitativoBean.setAltitud1(coordenada.getZ()
									.doubleValue());
							floraCualitativoBean.setP1Id(coordenada.getId());
							k++;
						} else if (k == 2) {
							floraCualitativoBean.setPx2(coordenada.getX()
									.doubleValue());
							floraCualitativoBean.setPy2(coordenada.getY()
									.doubleValue());
							floraCualitativoBean.setAltitud2(coordenada.getZ()
									.doubleValue());
							floraCualitativoBean.setP2Id(coordenada.getId());
							k++;
						} else if (k == 3) {
							floraCualitativoBean.setPx3(coordenada.getX()
									.doubleValue());
							floraCualitativoBean.setPy3(coordenada.getY()
									.doubleValue());
							floraCualitativoBean.setAltitud3(coordenada.getZ()
									.doubleValue());
							floraCualitativoBean.setP3Id(coordenada.getId());
							k++;
						} else if (k == 4) {
							floraCualitativoBean.setPx4(coordenada.getX()
									.doubleValue());
							floraCualitativoBean.setPy4(coordenada.getY()
									.doubleValue());
							floraCualitativoBean.setAltitud4(coordenada.getZ()
									.doubleValue());
							floraCualitativoBean.setP4Id(coordenada.getId());
							k++;
						}
					}
				}

				int p = 0;
				for (FloraEspecie floraEspecie : punto.getListaFloraEspecie()) {
					FloraCualitativoEspecieBean floraCualitativoEspecieBean = new FloraCualitativoEspecieBean(
							floraEspecie.getId(), p++, floraCualitativoBean,
							floraEspecie.getFamilia(),
							floraEspecie.getGenero(),
							floraEspecie.getEspecie(),
							floraEspecie.getNombreCientifico(),
							floraEspecie.getNombreComun(),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getTipoVegetacion()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getHabito()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getEstadoIndividuo()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie.getUso()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getOrigen()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getUicnInternacional()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getNivelIdentificacion()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getCities()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getLibroRojo()),
							floraEspecie.getNumeroColeccion(),
							floraEspecie.getCentroTenencia(),
							floraEspecie.getDescripcionFoto(),
							floraEspecie.getFotoEspecie(),
							floraEspecie.getContentType(),
							floraEspecie.getAnexoFoto());

					floraCualitativoEspecieBeans
							.add(floraCualitativoEspecieBean);
				}

				floraCualitativoBeans.add(floraCualitativoBean);

			} else if (punto.getIdCatalogoTipoMuestreo().equals(
					CatalogoGeneral.CUANTITATIVO)) {
				FloraCuantitativoBean floraCuantitativoBean = new FloraCuantitativoBean(
						punto.getId(), punto.getPuntoMuestreo(), j++, null,
						null, null, null, null, null, null, null, null, null,
						null, null,
						catalogoGeneralFacade.obtenerCatalogoXId(punto
								.getTipoVegetacion()),
						punto.getFechaMuestreo(), punto.getEsfuerzoMuestreo());

				int k = 1;
				for (CoordenadaGeneral coordenada : punto.getListaCoordenadas()) {
					if (coordenada.getX() != null) {
						if (k == 1) {
							floraCuantitativoBean.setPx1(coordenada.getX()
									.doubleValue());
							floraCuantitativoBean.setPy1(coordenada.getY()
									.doubleValue());
							floraCuantitativoBean.setAltitud1(coordenada.getZ()
									.doubleValue());
							floraCuantitativoBean.setP1Id(coordenada.getId());
							k++;
						} else if (k == 2) {
							floraCuantitativoBean.setPx2(coordenada.getX()
									.doubleValue());
							floraCuantitativoBean.setPy2(coordenada.getY()
									.doubleValue());
							floraCuantitativoBean.setAltitud2(coordenada.getZ()
									.doubleValue());
							floraCuantitativoBean.setP2Id(coordenada.getId());
							k++;
						} else if (k == 3) {
							floraCuantitativoBean.setPx3(coordenada.getX()
									.doubleValue());
							floraCuantitativoBean.setPy3(coordenada.getY()
									.doubleValue());
							floraCuantitativoBean.setAltitud3(coordenada.getZ()
									.doubleValue());
							floraCuantitativoBean.setP3Id(coordenada.getId());
							k++;
						} else if (k == 4) {
							floraCuantitativoBean.setPx4(coordenada.getX()
									.doubleValue());
							floraCuantitativoBean.setPy4(coordenada.getY()
									.doubleValue());
							floraCuantitativoBean.setAltitud4(coordenada.getZ()
									.doubleValue());
							floraCuantitativoBean.setP4Id(coordenada.getId());
							k++;
						}
					}
				}

				int p = 0;
				for (FloraEspecie floraEspecie : punto.getListaFloraEspecie()) {
					FloraCuantitativoEspecieBean floraCuantitativoEspecieBean = new FloraCuantitativoEspecieBean(
							floraEspecie.getId(), p++, floraCuantitativoBean,
							floraEspecie.getFamilia(),
							floraEspecie.getGenero(),
							floraEspecie.getEspecie(),
							floraEspecie.getNombreCientifico(),
							floraEspecie.getNombreComun(),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getTipoVegetacion()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getHabito()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getEstadoIndividuo()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie.getUso()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getOrigen()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getUicnInternacional()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getNivelIdentificacion()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getCities()),
							catalogoGeneralFacade
									.obtenerCatalogoXId(floraEspecie
											.getLibroRojo()),
							floraEspecie.getNumeroColeccion(),
							floraEspecie.getCentroTenencia(),
							floraEspecie.getDescripcionFoto(),
							floraEspecie.getDap(),
							floraEspecie.getAlturaTotal(),
							floraEspecie.getAlturaComercial(),
							floraEspecie.getFotoEspecie(),
							floraEspecie.getContentType(),
							floraEspecie.getAnexoFoto());

					floraCuantitativoEspecieBeans
							.add(floraCuantitativoEspecieBean);
				}

				floraCuantitativoBeans.add(floraCuantitativoBean);

			}
		}

		floraBean.setFloraCualitativos(floraCualitativoBeans);
		floraBean.setFloraCuantitativos(floraCuantitativoBeans);
		floraBean.setFloraCuantitativosEspecie(floraCuantitativoEspecieBeans);
		floraBean.setFloraCualitativosEspecie(floraCualitativoEspecieBeans);
		return floraBean;
	}
}
