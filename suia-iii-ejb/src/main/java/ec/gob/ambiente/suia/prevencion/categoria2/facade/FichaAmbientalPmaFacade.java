package ec.gob.ambiente.suia.prevencion.categoria2.facade;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.dto.EntityFichaCompleta;
import ec.gob.ambiente.suia.dto.EntityFichaCompletaRgd;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.flujos.service.FlujoServiceBean;
import ec.gob.ambiente.suia.prevencion.categoria2.service.CoordenadaPmaServiceBean;
import ec.gob.ambiente.suia.prevencion.categoria2.service.DetalleFichaPmaServiceBean;
import ec.gob.ambiente.suia.prevencion.categoria2.service.EquipoAccesorioPmaServiceBean;
import ec.gob.ambiente.suia.prevencion.categoria2.service.FichaAmbientalPmaServiceBean;
import ec.gob.ambiente.suia.prevencion.categoria2.service.ImpactoAmbientalPmaServiceBean;
import ec.gob.ambiente.suia.prevencion.categoria2.service.MarcoLegalPmaServiceBean;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;

@LocalBean
@Stateless
public class FichaAmbientalPmaFacade {

	private static final Logger LOG = Logger.getLogger(CategoriaIIFacade.class);

	@EJB
	private FichaAmbientalPmaServiceBean fichaAmbientalPmaServiceBean;

	@EJB
	private DetalleFichaPmaServiceBean detalleFichaPmaServiceBean;

	@EJB
	private MarcoLegalPmaServiceBean marcoLegalPmaServiceBean;

	@EJB
	private EquipoAccesorioPmaServiceBean equipoAccesorioPmaServiceBean;

	@EJB
	private ImpactoAmbientalPmaServiceBean impactoAmbientalPmaServiceBean;

	@EJB
	private FlujoServiceBean flujoServiceBean;

	@EJB
	DocumentosFacade documentosFacade;

	@EJB
	CrudServiceBean crudServiceBean;

	@EJB
	private CoordenadaPmaServiceBean coordenadaPmaServiceBean;
	@EJB
	FaseFichaAmbientalFacade faseFichaAmbientalFacade;

	public FichaAmbientalPma guardarSoloFicha(
			final FichaAmbientalPma fichaAmbientalPma) {
		return fichaAmbientalPmaServiceBean
				.guardarFichaAmbientalPma(fichaAmbientalPma);
	}

	public void guardarFichaAmbientalPma(FichaAmbientalPma fichaAmbientalPma,
			List<CatalogoCategoriaFase> actividadesSeleccionadas,
			CatalogoGeneral infraestructurasSeleccionados,
			List<CatalogoGeneral> tiposPredioSeleccionados,
			List<CatalogoGeneral> tiposPredioSecundarios) throws Exception {
		fichaAmbientalPma.setValidarDatosGenerales(true);
		fichaAmbientalPmaServiceBean
				.guardarFichaAmbientalPma(fichaAmbientalPma);

		if (!actividadesSeleccionadas.isEmpty()) {
			if (fichaAmbientalPma.getId() != null) {
				faseFichaAmbientalFacade
						.eliminarFasesExistentes(fichaAmbientalPma.getId());
			}

			for (CatalogoCategoriaFase fase : actividadesSeleccionadas) {
				if (fase.isSeleccionado()) {
					FasesFichaAmbiental faseFicha = new FasesFichaAmbiental();
					faseFicha.setCatalogoCategoriaFase(fase);
					faseFicha.setEstado(true);
					faseFicha.setFichaAmbientalPma(fichaAmbientalPma);

					faseFichaAmbientalFacade.guardarFasesFicha(faseFicha);
				}
			}
		}

		List<CatalogoGeneral> tiposInfraestructurasSeleccionados = new ArrayList<CatalogoGeneral>();
		tiposInfraestructurasSeleccionados.add(infraestructurasSeleccionados);
		if (!tiposInfraestructurasSeleccionados.isEmpty()) {
			List<DetalleFichaPma> listaDetallesFichaPma = getDetallesFichaPmaPorIdFicha(
					fichaAmbientalPma.getId(),
					TipoCatalogo.TIPO_INFRAESTRUCTURA);

			detalleFichaPmaServiceBean
					.eliminarDetalleFichaPma(listaDetallesFichaPma);

			for (CatalogoGeneral infraestructura : tiposInfraestructurasSeleccionados) {
				DetalleFichaPma detalleFicha = new DetalleFichaPma();
				detalleFicha.setFichaAmbientalPma(fichaAmbientalPma);
				detalleFicha.setCatalogoGeneral(infraestructura);
				detalleFichaPmaServiceBean.guardarDetalleFichaPma(detalleFicha);
			}
		}

		if (!tiposPredioSeleccionados.isEmpty()) {
			List<DetalleFichaPma> listaDetallesFichaPma = getDetallesFichaPmaPorIdFicha(
					fichaAmbientalPma.getId(), TipoCatalogo.SITUACION_PREDIO);

			detalleFichaPmaServiceBean
					.eliminarDetalleFichaPma(listaDetallesFichaPma);

			for (CatalogoGeneral predio : tiposPredioSeleccionados) {
				DetalleFichaPma detalleFicha = new DetalleFichaPma();
				detalleFicha.setFichaAmbientalPma(fichaAmbientalPma);
				detalleFicha.setCatalogoGeneral(predio);
				detalleFichaPmaServiceBean.guardarDetalleFichaPma(detalleFicha);
			}
		}

		if (!tiposPredioSeleccionados.isEmpty()) {
			List<DetalleFichaPma> listaDetallesFichaPma = detalleFichaPmaServiceBean
					.getCatalogoGeneralFichaXIdFichaXTipoXCodigo(
							fichaAmbientalPma.getId(),
							TipoCatalogo.SITUACION_PREDIO,
							TipoCatalogo.CODIGO_PREDIO_SECUDARIO);

			detalleFichaPmaServiceBean
					.eliminarDetalleFichaPma(listaDetallesFichaPma);

			for (CatalogoGeneral predio : tiposPredioSecundarios) {
				DetalleFichaPma detalleFicha = new DetalleFichaPma();
				detalleFicha.setFichaAmbientalPma(fichaAmbientalPma);
				detalleFicha.setCatalogoGeneral(predio);
				detalleFichaPmaServiceBean.guardarDetalleFichaPma(detalleFicha);
			}
		}

	}
	
	public List<CatalogoGeneral> getCatalogoGeneralFichaPorIdFichaPorTipo(
			Integer idFicha, Integer tipo) {
		return detalleFichaPmaServiceBean
				.getCatalogoGeneralFichaPorIdFichaPorTipo(idFicha, tipo);
	}

	public List<CatalogoGeneral> getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigo(
			Integer idFicha, Integer tipo, String codigo) {
		return detalleFichaPmaServiceBean
				.getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigo(idFicha,
						tipo, codigo);
	}

	public FichaAmbientalPma getFichaAmbientalPorCodigoProyecto(String codigo) {
		return fichaAmbientalPmaServiceBean
				.getFichaAmbientalPorCodigoProyecto(codigo);
	}

	public FichaAmbientalPma getFichaAmbientalPorCodigoProyectoFull(
			String codigo) {
		FichaAmbientalPma ficha = fichaAmbientalPmaServiceBean
				.getFichaAmbientalPorCodigoProyecto(codigo);
		return ficha;
	}

	public FichaAmbientalPma getFichaAmbientalPorIdProyecto(Integer id) {
		return fichaAmbientalPmaServiceBean.getFichaAmbientalPorIdProyecto(id);
	}

	public List<DetalleFichaPma> getDetallesFichaPmaPorIdFicha(Integer idFicha,
			Integer idCatalogo) {
		return detalleFichaPmaServiceBean.getDetallesFichaPmaPorIdFichaAnnType(
				idFicha, idCatalogo);
	}

	public List<MarcoLegalPma> getMarcosLegalesPorFichaId(Integer idFicha) {
		return marcoLegalPmaServiceBean.getMarcosLegalesPorFichaId(idFicha);
	}

	public void guardarMarcoLegal(MarcoLegalPma marcoLegalPma) {
		marcoLegalPmaServiceBean.guardarMarcoLegal(marcoLegalPma);
	}

	public void eliminarMarcoLegal(MarcoLegalPma marcoLegalPma) {
		marcoLegalPmaServiceBean.eliminarMarcoLegal(marcoLegalPma);
	}

	public List<CatalogoGeneral> getArticulosCatalogoNativeQuery(
			Integer subSectorId, String codigoUso, String cacsCode) {
		return marcoLegalPmaServiceBean.getArticulosCatalogoNativeQuery(
				subSectorId, codigoUso, cacsCode);
	}

	public List<CatalogoGeneral> getArticulosCatalogoCoa() {
		return marcoLegalPmaServiceBean.getArticulosCatalogoCoa();
	}

	public List<EquipoAccesorioPma> getEquiposAccesoriosPorFichaId(
			Integer idFicha) {
		return equipoAccesorioPmaServiceBean
				.getEquiposAccesoriosPorFichaId(idFicha);
	}

	public void guardarEquipoAccesorio(EquipoAccesorioPma equipoAccesorioPma) {
		equipoAccesorioPmaServiceBean
				.guardarEquipoAccesorio(equipoAccesorioPma);
	}

	public void eliminarEquipoAccesorio(EquipoAccesorioPma equipoAccesorioPma) {
		equipoAccesorioPmaServiceBean
				.eliminarEquipoAccesorio(equipoAccesorioPma);
	}

	@SuppressWarnings("unchecked")
	public List<ActividadProcesoPma> getActividadesProcesosFichaPorIdFicha(
			Integer idFicha) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idFicha", idFicha);
		return (List<ActividadProcesoPma>) crudServiceBean.findByNamedQuery(
				ActividadProcesoPma.OBTENER_POR_FICHA_ID, params);
	}

	@SuppressWarnings("unchecked")
	public List<Instalacion> getInstalacionesProcesosFichaPorIdFicha(
			Integer idFicha) {
		List<Instalacion> instalacionesProcesos = new ArrayList<Instalacion>();
		instalacionesProcesos = (List<Instalacion>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From Instalacion i where i.fichaAmbientalPma.id =:idFicha and i.estado=true and i.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha).getResultList();

		return instalacionesProcesos;
	}

	@SuppressWarnings("unchecked")
	public List<TecnicaProcesoPma> getTecnicasProcesosPorIdFicha(Integer idFicha) {
		List<TecnicaProcesoPma> tecnicasProcesos = new ArrayList<TecnicaProcesoPma>();
		tecnicasProcesos = (List<TecnicaProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From TecnicaProcesoPma t where t.fichaAmbientalPma.id =:idFicha and t.estado=true and t.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha).getResultList();

		return tecnicasProcesos;
	}

	@SuppressWarnings("unchecked")
	public List<InsumoProcesoPma> getInsumosProcesosFichaPorIdFicha(
			Integer idFicha) {
		List<InsumoProcesoPma> insumosProcesos = new ArrayList<InsumoProcesoPma>();
		insumosProcesos = (List<InsumoProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From InsumoProcesoPma i where i.fichaAmbientalPma.id =:idFicha and i.estado=true and i.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha).getResultList();

		return insumosProcesos;
	}

	@SuppressWarnings("unchecked")
	public List<PlaguicidaProcesoPma> getPlagicidasProcesosFichaPorIdFicha(
			Integer idFicha) {
		List<PlaguicidaProcesoPma> plaguicidasProcesos = new ArrayList<PlaguicidaProcesoPma>();
		plaguicidasProcesos = (List<PlaguicidaProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From PlaguicidaProcesoPma p where p.fichaAmbientalPma.id =:idFicha and p.estado=true and p.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha).getResultList();

		return plaguicidasProcesos;
	}

	@SuppressWarnings("unchecked")
	public List<FertilizanteProcesoPma> getFertilizantesProcesoPorIdFicha(
			Integer idFicha) {
		List<FertilizanteProcesoPma> fertilizantesProcesos;
		fertilizantesProcesos = (List<FertilizanteProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From FertilizanteProcesoPma f where f.fichaAmbientalPma.id =:idFicha and f.estado=true and f.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha).getResultList();

		return fertilizantesProcesos;
	}

	@SuppressWarnings("unchecked")
	public DesechoSanitarioProcesoPma getDesechoSanitarioProcesoPorIdFicha(
			Integer idFicha) {
		List<DesechoSanitarioProcesoPma> desechoProcesos = new ArrayList<DesechoSanitarioProcesoPma>();
		desechoProcesos = (List<DesechoSanitarioProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From DesechoSanitarioProcesoPma d where d.fichaAmbientalPma.id =:idFicha and d.estado=true and d.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha).getResultList();

		if (!desechoProcesos.isEmpty()) {
			return desechoProcesos.get(0);
		} else {
			return new DesechoSanitarioProcesoPma();
		}
	}

	@SuppressWarnings("unchecked")
	public List<VehiculoDesechoSanitarioProcesoPma> getVehiculosProcesosPorIdFicha(
			Integer idFicha) {
		List<VehiculoDesechoSanitarioProcesoPma> vehiculoProcesos = new ArrayList<VehiculoDesechoSanitarioProcesoPma>();
		vehiculoProcesos = (List<VehiculoDesechoSanitarioProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From VehiculoDesechoSanitarioProcesoPma v where v.fichaAmbientalPma.id =:idFicha and v.estado=true and v.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha).getResultList();

		return vehiculoProcesos;
	}

	@SuppressWarnings("unchecked")
	public List<DesechoPeligrosoProcesoPma> getDesechosPeligrososProcesosPorIdFicha(
			Integer idFicha, Boolean esExpost) {
		List<DesechoPeligrosoProcesoPma> desechosProcesos = new ArrayList<DesechoPeligrosoProcesoPma>();

		if (esExpost) {
			desechosProcesos = (List<DesechoPeligrosoProcesoPma>) crudServiceBean
					.getEntityManager()
					.createQuery(
							"From DesechoPeligrosoProcesoPma d where d.fichaAmbientalPma.id =:idFicha and d.vehiculoDescripcionExantes=null and d.estado=true and d.idRegistroOriginal = null")
					.setParameter("idFicha", idFicha).getResultList();
		} else {
			desechosProcesos = (List<DesechoPeligrosoProcesoPma>) crudServiceBean
					.getEntityManager()
					.createQuery(
							"From DesechoPeligrosoProcesoPma d where d.fichaAmbientalPma.id =:idFicha and d.vehiculoDescripcionExantes is not null and d.estado=true and d.idRegistroOriginal = null")
					.setParameter("idFicha", idFicha).getResultList();
		}

		return desechosProcesos;
	}

	@SuppressWarnings("unchecked")
	public List<HerramientaProcesoPma> getHerramientasProcesosFichaPorIdFicha(
			Integer idFicha) {
		List<HerramientaProcesoPma> herramientasProcesos = new ArrayList<HerramientaProcesoPma>();
		herramientasProcesos = (List<HerramientaProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From HerramientaProcesoPma h where h.fichaAmbientalPma.id =:idFicha and h.estado=true and h.idRegistroOriginal = null")
				.setParameter("idFicha", idFicha).getResultList();

		return herramientasProcesos;
	}

	public void guardarActividadesProceso(Integer idFichaAmbiental,
			List<ActividadProcesoPma> actividadesProceso) {
		// Solo se buscan las actividades viejas
		List<ActividadProcesoPma> actividadesExistentes = new ArrayList<ActividadProcesoPma>();

		for (ActividadProcesoPma actividad : actividadesProceso) {
			if (actividad.getId() != null) {
				actividadesExistentes.add(actividad);
			}
		}

		if (!actividadesExistentes.isEmpty()) {
			crudServiceBean
					.getEntityManager()
					.createQuery(
							"Update ActividadProcesoPma a SET a.estado=false where a.fichaAmbientalPma.id=:idFicha and a.estado=true and a not in(:activityList)")
					.setParameter("idFicha", idFichaAmbiental)
					.setParameter("activityList", actividadesExistentes)
					.executeUpdate();
		}

		for (ActividadProcesoPma actividad : actividadesProceso) {
			crudServiceBean.saveOrUpdate(actividad);
		}
	}

	public void guardarInstalacionesProceso(Integer idFichaAmbiental,
			List<Instalacion> newInstalacionesProceso) {
		// Se eliminan las viejas
		crudServiceBean
				.getEntityManager()
				.createQuery(
						"Update Instalacion i SET i.estado='false' where i.fichaAmbientalPma.id=:idFicha and i.estado=true")
				.setParameter("idFicha", idFichaAmbiental).executeUpdate();

		for (Instalacion instalacion : newInstalacionesProceso) {
			crudServiceBean.saveOrUpdate(instalacion);
		}
	}

	public void guardarTecnicasProceso(Integer idFichaAmbiental,
			List<TecnicaProcesoPma> tecnicasProceso) {
		// Se eliminan las viejas
		crudServiceBean
				.getEntityManager()
				.createQuery(
						"Update TecnicaProcesoPma t SET t.estado='false' where t.fichaAmbientalPma.id=:idFicha and t.estado=true")
				.setParameter("idFicha", idFichaAmbiental).executeUpdate();

		for (TecnicaProcesoPma tecnica : tecnicasProceso) {
			crudServiceBean.saveOrUpdate(tecnica);
		}
	}

	public void guardarPlaguicidasProceso(Integer idFichaAmbiental,
			List<PlaguicidaProcesoPma> plaguicidasProceso) {
		// Solo se buscan las plaguicidas viejas
		List<PlaguicidaProcesoPma> plagicidasExistentes = new ArrayList<PlaguicidaProcesoPma>();

		for (PlaguicidaProcesoPma plaguicida : plaguicidasProceso) {
			if (plaguicida.getId() != null) {
				plagicidasExistentes.add(plaguicida);
			}
		}

		if (!plagicidasExistentes.isEmpty()) {
			crudServiceBean
					.getEntityManager()
					.createQuery(
							"Update PlaguicidaProcesoPma p SET p.estado='false' where p.fichaAmbientalPma.id=:idFicha and p.estado=true and p not in(:plaguicidaList)")
					.setParameter("idFicha", idFichaAmbiental)
					.setParameter("plaguicidaList", plagicidasExistentes)
					.executeUpdate();
		}

		for (PlaguicidaProcesoPma plaguicida : plaguicidasProceso) {
			crudServiceBean.saveOrUpdate(plaguicida);
		}
	}

	public void guardarVehiculosProceso(FichaAmbientalPma fichaAmbiental,
			List<VehiculoDesechoSanitarioProcesoPma> vehiculosProceso) {
		// Solo se buscan las herramientas viejas
		List<VehiculoDesechoSanitarioProcesoPma> vehiculosExistentes = new ArrayList<VehiculoDesechoSanitarioProcesoPma>();

		for (VehiculoDesechoSanitarioProcesoPma vehiculo : vehiculosProceso) {
			if (vehiculo.getId() != null) {
				vehiculosExistentes.add(vehiculo);
			}
		}

		if (!vehiculosExistentes.isEmpty()) {
			crudServiceBean
					.getEntityManager()
					.createQuery(
							"Update VehiculoDesechoSanitarioProcesoPma v SET v.estado='false' where v.fichaAmbientalPma.id=:idFicha and v.estado=true and v not in(:vehiculosList)")
					.setParameter("idFicha", fichaAmbiental.getId())
					.setParameter("vehiculosList", vehiculosExistentes)
					.executeUpdate();
		}

		for (VehiculoDesechoSanitarioProcesoPma vehiculo : vehiculosProceso) {
			try {
				// Imagen del vehículo
				if (vehiculo.getImagenVehiculo() != null) {
					vehiculo.setDocumentoImagenVehículo(ingresarAnexoVehiculo(
							vehiculo.getImagenVehiculo(), fichaAmbiental
									.getProyectoLicenciamientoAmbiental()
									.getId(), fichaAmbiental
									.getProyectoLicenciamientoAmbiental()
									.getCodigo(),
							fichaAmbiental.getProcessId(), 0L,
							TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
				}
				// Certificado de inspección
				if (vehiculo.getCertificadoInspeccion() != null) {
					vehiculo.setDocumentoCertificadoInspeccion(ingresarAnexoVehiculo(
							vehiculo.getCertificadoInspeccion(), fichaAmbiental
									.getProyectoLicenciamientoAmbiental()
									.getId(), fichaAmbiental
									.getProyectoLicenciamientoAmbiental()
									.getCodigo(),
							fichaAmbiental.getProcessId(), 0L,
							TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
				}
				// Matricula
				if (vehiculo.getMatricula() != null) {
					vehiculo.setDocumentoMatricula(ingresarAnexoVehiculo(
							vehiculo.getMatricula(), fichaAmbiental
									.getProyectoLicenciamientoAmbiental()
									.getId(), fichaAmbiental
									.getProyectoLicenciamientoAmbiental()
									.getCodigo(),
							fichaAmbiental.getProcessId(), 0L,
							TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
				}
			} catch (Exception e) {
				LOG.error("Error subiendo archivos del vehículo al Alfresco", e);
			}

			// Se guarda el objeto vehículo
			crudServiceBean.saveOrUpdate(vehiculo);
		}
	}

	private String getExtension(String fullPath) {
		String extension = "";
		int i = fullPath.lastIndexOf('.');
		if (i > 0) {
			extension = fullPath.substring(i + 1);
		}
		return extension;
	}

	public Documento ingresarAnexoVehiculo(File file, Integer id,
			String codProyecto, long idProceso, long idTarea,
			TipoDocumentoSistema tipoDocumento) throws Exception {
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		Documento documento1 = new Documento();
		documento1.setIdTable(id);
		String ext = getExtension(file.getAbsolutePath());
		documento1.setNombre(file.getName());
		documento1.setExtesion(ext);
		documento1.setNombreTabla(VehiculoDesechoSanitarioProcesoPma.class
				.getSimpleName());
		documento1.setMime(mimeTypesMap.getContentType(file));
		documento1.setContenidoDocumento(data);
		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		documentoTarea.setIdTarea(idTarea);
		documentoTarea.setProcessInstanceId(idProceso);

		return documentosFacade.guardarDocumentoAlfresco(codProyecto, "Anexos",
				idProceso, documento1, tipoDocumento, documentoTarea);
	}

	public void guardarDesechosPeligrososProceso(Integer idFichaAmbiental,
			List<DesechoPeligrosoProcesoPma> desechosProceso) {
		// Solo se buscan los desechos viejos
		List<DesechoPeligrosoProcesoPma> desechosExistentes = new ArrayList<DesechoPeligrosoProcesoPma>();

		for (DesechoPeligrosoProcesoPma desecho : desechosProceso) {
			if (desecho.getId() != null) {
				desechosExistentes.add(desecho);
			}
		}

		if (!desechosExistentes.isEmpty()) {
			crudServiceBean
					.getEntityManager()
					.createQuery(
							"Update DesechoPeligrosoProcesoPma d SET d.estado='false' where d.fichaAmbientalPma.id=:idFicha and d.estado=true and d not in(:desechoList)")
					.setParameter("idFicha", idFichaAmbiental)
					.setParameter("desechoList", desechosExistentes)
					.executeUpdate();
		}

		for (DesechoPeligrosoProcesoPma desecho : desechosProceso) {
			crudServiceBean.saveOrUpdate(desecho);
		}
	}

	public void guardarFertilizanteProceso(Integer idFichaAmbiental,
			List<FertilizanteProcesoPma> fertilizantesProceso) {
		List<FertilizanteProcesoPma> fertilizantesExistentes = new ArrayList<FertilizanteProcesoPma>();

		for (FertilizanteProcesoPma fertilizante : fertilizantesProceso) {
			if (fertilizante.getId() != null) {
				fertilizantesExistentes.add(fertilizante);
			}
		}

		if (!fertilizantesExistentes.isEmpty()) {
			crudServiceBean
					.getEntityManager()
					.createQuery(
							"Update FertilizanteProcesoPma f SET f.estado='false' where f.fichaAmbientalPma.id=:idFicha and f.estado=true and f not in(:fertilizanteList)")
					.setParameter("idFicha", idFichaAmbiental)
					.setParameter("fertilizanteList", fertilizantesExistentes)
					.executeUpdate();
		}

		for (FertilizanteProcesoPma fertilizante : fertilizantesProceso) {
			crudServiceBean.saveOrUpdate(fertilizante);
		}
	}

	public void guardarDesechoSanitarioProceso(
			DesechoSanitarioProcesoPma desechoSanitarioProceso) {
		crudServiceBean.saveOrUpdate(desechoSanitarioProceso);
	}

	public void guardarHerramientaProceso(Integer idFichaAmbiental,
			List<HerramientaProcesoPma> herramientasProceso) {
		// Solo se buscan las herramientas viejas
		List<HerramientaProcesoPma> herramientasExistentes = new ArrayList<HerramientaProcesoPma>();

		for (HerramientaProcesoPma herramienta : herramientasProceso) {
			if (herramienta.getId() != null) {
				herramientasExistentes.add(herramienta);
			}
		}

		if (!herramientasExistentes.isEmpty()) {
			crudServiceBean
					.getEntityManager()
					.createQuery(
							"Update HerramientaProcesoPma h SET h.estado='false' where h.fichaAmbientalPma.id=:idFicha and h.estado=true and h not in(:toolList)")
					.setParameter("idFicha", idFichaAmbiental)
					.setParameter("toolList", herramientasExistentes)
					.executeUpdate();
		}

		for (HerramientaProcesoPma herramienta : herramientasProceso) {
			crudServiceBean.saveOrUpdate(herramienta);
		}
	}

	public void guardarInsumoProceso(Integer idFichaAmbiental,
			List<InsumoProcesoPma> insumosProceso) {

		List<InsumoProcesoPma> insumosExistentes = new ArrayList<InsumoProcesoPma>();

		for (InsumoProcesoPma insumo : insumosProceso) {
			if (insumo.getId() != null) {
				insumosExistentes.add(insumo);
			}
		}

		if (!insumosExistentes.isEmpty()) {
			crudServiceBean
					.getEntityManager()
					.createQuery(
							"Update InsumoProcesoPma i SET i.estado='false' where i.fichaAmbientalPma.id=:idFicha and i.estado=true and i not in(:supplieList)")
					.setParameter("idFicha", idFichaAmbiental)
					.setParameter("supplieList", insumosExistentes)
					.executeUpdate();
		}

		for (InsumoProcesoPma insumo : insumosProceso) {
			crudServiceBean.saveOrUpdate(insumo);
		}
	}

	/*
	 * public void guardarOrigenRecursoAguaProceso( OrigenRecursoAguaProcesoPma
	 * origenRecursoAguaProcesoPma) {
	 * crudServiceBean.saveOrUpdate(origenRecursoAguaProcesoPma); }
	 */

	public List<ImpactoAmbientalPma> getImpactosAmbientalesPorFichaId(
			Integer idFicha) throws ServiceException {
		return impactoAmbientalPmaServiceBean
				.getImpactosAmbientalesPorFichaId(idFicha);
	}

	public List<MatrizFactorImpactoOtros> getImpactosAmbientalesPorFichaIdOtros(
			Integer idFicha) {
		return impactoAmbientalPmaServiceBean
				.getImpactosAmbientalesPorFichaIdOtros(idFicha);
	}

	public void guardarImpactoAmbiental(
    		//MarielaG cambios para manejo historial
            List<ActividadProcesoPma> listaActividadProcesoPma,
            List<MatrizFactorImpactoOtros> listaImpactoAmbientalPmaOtros,FichaAmbientalPma fichaAmbientalPma,
            List<MatrizFactorImpacto> listaImpactoEliminados, List<MatrizFactorImpactoOtros> listaImpactosOtrosEliminados) throws Exception{
        impactoAmbientalPmaServiceBean.guardarImpactoAmbiental(
                listaActividadProcesoPma, listaImpactoAmbientalPmaOtros, fichaAmbientalPma, listaImpactoEliminados, listaImpactosOtrosEliminados);
    }

	public void eliminarImpactoAmbiental(ImpactoAmbientalPma impactoAmbientalPma) {
		impactoAmbientalPmaServiceBean
				.eliminarImpactoAmbiental(impactoAmbientalPma);
	}

	public List<Coordenada> getCoordenadasProyectoPma(Integer idProyecto) {
		return coordenadaPmaServiceBean.getCoordenadasProyectoPma(idProyecto);
	}

	public FichaAmbientalPma obtenerPorId(final Integer id) {
		return crudServiceBean.find(FichaAmbientalPma.class, id);
	}

	public void eliminarAsociadoFase(
			List<CatalogoCategoriaFase> catalogoCategoriaFases)
			throws Exception {
		try {
			for (CatalogoCategoriaFase catalogoCategoriaFase : catalogoCategoriaFases) {
				// ELIMINAMOS ACTIVIDADES
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("p_categoriaFaseId",
						catalogoCategoriaFase.getId());

				List<ActividadProcesoPma> actividadesProcesoPma = crudServiceBean
						.findByNamedQueryGeneric(
								ActividadProcesoPma.OBTENER_POR_FASE,
								parameters);

				crudServiceBean.delete(actividadesProcesoPma);

				// ELIMINAMOS HERRAMIENTAS
				List<HerramientaProcesoPma> herramientaProcesoPma = crudServiceBean
						.findByNamedQueryGeneric(
								HerramientaProcesoPma.OBTENER_POR_FASE,
								parameters);

				crudServiceBean.delete(herramientaProcesoPma);

				// ELIMINAMOS INSUMOS
				List<InsumoProcesoPma> insumoProcesoPma = crudServiceBean
						.findByNamedQueryGeneric(
								InsumoProcesoPma.OBTENER_POR_FASE, parameters);

				crudServiceBean.delete(insumoProcesoPma);

				// ELIMINAMOS CRONOGRAMAS Y ACTIVIDADES
				// List<CronogramaActividadesPma> cronogramas = crudServiceBean
				// .findByNamedQueryGeneric(
				// CronogramaActividadesPma.OBTENER_POR_FASE,
				// parameters);
				//
				// for (CronogramaActividadesPma cronogramaActividadesPma :
				// cronogramas) {
				// crudServiceBean.delete(cronogramaActividadesPma
				// .getActividadList());
				// crudServiceBean.delete(cronogramaActividadesPma);
				// }
				//
				// ELIMINAMOS IMPACTOS AMBIENTALES
				List<ImpactoAmbientalPma> impactosAmbientalPma = crudServiceBean
						.findByNamedQueryGeneric(
								ImpactoAmbientalPma.OBTENER_POR_FASE,
								parameters);

				crudServiceBean.delete(impactosAmbientalPma);

			}
		} catch (Exception e) {
			throw new Exception(e);
		}

	}

	public List<EntityFichaCompleta> getFichasAmbiental() {
		List<EntityFichaCompleta> fichasCompletas = fichaAmbientalPmaServiceBean
				.getFichasAmbientalPorProyecto();
		return fichasCompletas;
	}

	public EntityAdjunto obternerPorFichaPuntosPMA(final FichaAmbientalPma fichaAmbiental) throws ServiceException, CmisAlfrescoException {
        try {
            EntityAdjunto obj = new EntityAdjunto();
            List<Documento> docs = documentosFacade.documentoXTablaId(fichaAmbiental.getId(), fichaAmbiental.getClass().getSimpleName() + "-1");
            if (docs != null && !docs.isEmpty()) {
            	int index = docs.size() - 1;
                obj.setExtension(docs.get(index).getExtesion());
                obj.setMimeType(docs.get(index).getMime());
                obj.setNombre(docs.get(index).getNombre());
                obj.setArchivo(documentosFacade.descargar(docs.get(index).getIdAlfresco()));
            } else {
                obj = null;
            }
            return obj;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

	public void cambiarEstadoFinalizacion(FichaAmbientalPma fichaAmbientalPma) {
		fichaAmbientalPma.setFinalizado(true);
		fichaAmbientalPmaServiceBean
				.guardarFichaAmbientalPma(fichaAmbientalPma);
	}
	
	@EJB
   	private EjecutarSentenciasNativas ejecutarSentenciasNativas;
       
       public List<EntityFichaCompleta> getFinalizados(String codigoProyecto) {
   		try {
   			Map<String, Object> parametros = new HashMap<String, Object>();
   			String like = "";
   			like = " LOWER(cod_proyecto) like LOWER(:codigoProyecto)";
   			parametros.put("codigoProyecto", "%" + codigoProyecto + "%");
   			
   			StringBuilder sb = new StringBuilder();
   			sb.append("select CAST(id_proyecto  AS varchar(255)),cod_proyecto,nombre_proyecto,cedula_proponente,nombre_proponente,orga_name_organization, id_licencia from vw_certificados_finalizados where "+like+"  union("
   					+ " select CAST(id_proyecto  AS varchar(255)),cod_proyecto,nombre_proyecto,cedula_proponente,nombre_proponente,orga_name_organization, id_licencia  from vw_licencias_finalizadas where "+like+"  union"
   					+ " 					 (select CAST(id_proyecto  AS varchar(255)),cod_proyecto,nombre_proyecto,cedula_proponente,nombre_proponente, orga_name_organization, id_licencia from vw_registros_finalizados where "+like+" ))")					
   					.append(" ORDER BY cod_proyecto DESC ");
   			return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityFichaCompleta.class, parametros);

   		} catch (Exception e) {
   			System.out.println("ssssssssss_"+e);
   			return null;
   		}
   	}
       
       /**
        * Ivan: metodo para obtener el número de resolución y la fecha de emision de una licencia desde la vista vw_licencias_finalizadas 
        * @param codigoProyecto
        * @return
        */
       public List<EntityFichaCompleta> getLicenses(String codigoProyecto) {
	   		try {
	   			Map<String, Object> parametros = new HashMap<String, Object>();
	   			String like = "";
	   			like = " LOWER(cod_proyecto) like LOWER(:codigoProyecto)";
	   			parametros.put("codigoProyecto", "%" + codigoProyecto + "%");
	   			
	   			StringBuilder sb = new StringBuilder();
	   			sb.append("select numero_resolucion,fechafinproceso from vw_licencias_finalizadas where "+like);
	   			return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityFichaCompleta.class, parametros);
	
	   		} catch (Exception e) {
	   			System.out.println("ssssssssss_"+e);
	   			return null;
	   		}
       }

	/**
	 * Cris F: Aumento de método para guardar el histórico
	 */
	public void guardarFichaAmbientalPmaHistorico(
			FichaAmbientalPma fichaAmbientalPma,
			List<CatalogoCategoriaFase> actividadesSeleccionadas,
			CatalogoGeneral infraestructurasSeleccionados,
			List<CatalogoGeneral> tiposPredioSeleccionados,
			List<CatalogoGeneral> tiposPredioSecundarios, Integer idProyecto) throws Exception {
		
		FichaAmbientalPma fichaAmbientalPmaBdd = getFichaAmbientalPorIdProyecto(idProyecto); 
				
		fichaAmbientalPma.setValidarDatosGenerales(true);		
				
		if(fichaAmbientalPmaBdd.getAreaImplantacion() != null){
			if(!compararFicha(fichaAmbientalPmaBdd, fichaAmbientalPma)){
				FichaAmbientalPma fichaAmbientalPmaHistorico = (FichaAmbientalPma) SerializationUtils.clone(fichaAmbientalPmaBdd);
				fichaAmbientalPmaHistorico.setId(null);
				fichaAmbientalPmaHistorico.setIdRegistroOriginal(fichaAmbientalPma.getId());
				fichaAmbientalPmaHistorico.setFechaHistorico(new Date());
				fichaAmbientalPmaServiceBean.guardarFichaAmbientalPma(fichaAmbientalPmaHistorico);
			}
		}
		fichaAmbientalPmaServiceBean.guardarFichaAmbientalPma(fichaAmbientalPma);

		if (!actividadesSeleccionadas.isEmpty()) {
			List<CatalogoCategoriaFase> fasesAGuardarList = new ArrayList<>();
			
			if (fichaAmbientalPma.getId() != null) {				
				/**
				 * En lugar de eliminar las fases se deben ver cuales son las que se eliminaron y luego
				 * comparar con las fases seleccionadas.
				 */						
				List<FasesFichaAmbiental> listaFasesParaEliminar = new ArrayList<>();
				List<FasesFichaAmbiental> listaFasesAEliminar = faseFichaAmbientalFacade.buscarFasesExistentes(fichaAmbientalPma.getId());
				fasesAGuardarList.addAll(actividadesSeleccionadas);
				if(listaFasesAEliminar != null)
					listaFasesParaEliminar.addAll(listaFasesAEliminar);
				
				if(listaFasesAEliminar != null){
					for(FasesFichaAmbiental fichaBdd : listaFasesAEliminar){
						for(CatalogoCategoriaFase fase : actividadesSeleccionadas){
							if(fase.isSeleccionado()){
								if(fichaBdd.getCatalogoCategoriaFase().getId().equals(fase.getId())){
									fasesAGuardarList.remove(fase);
									listaFasesParaEliminar.remove(fichaBdd);
									break;
								}							
							}
						}								
					}					
				}
				
				
				for(FasesFichaAmbiental faseEliminar : listaFasesParaEliminar){
					FasesFichaAmbiental fichaHistorico = (FasesFichaAmbiental) SerializationUtils.clone(faseEliminar);
					fichaHistorico.setId(null);
					fichaHistorico.setEstado(true);
					fichaHistorico.setIdRegistroOriginal(faseEliminar.getId());
					fichaHistorico.setFechaHistorico(new Date());
					faseFichaAmbientalFacade.guardarFasesFicha(fichaHistorico);					
				}				
				faseFichaAmbientalFacade.eliminarFases(listaFasesParaEliminar);
			
				for (CatalogoCategoriaFase fase : fasesAGuardarList) {
					if (fase.isSeleccionado()) {					
						FasesFichaAmbiental faseFicha = new FasesFichaAmbiental();
						faseFicha.setCatalogoCategoriaFase(fase);
						faseFicha.setEstado(true);
						faseFicha.setFichaAmbientalPma(fichaAmbientalPma);
						if(listaFasesAEliminar != null && !listaFasesAEliminar.isEmpty())
							faseFicha.setFechaHistorico(new Date());

						faseFichaAmbientalFacade.guardarFasesFicha(faseFicha);
					}
				}			
			}
		}

		List<CatalogoGeneral> tiposInfraestructurasSeleccionados = new ArrayList<CatalogoGeneral>();
		tiposInfraestructurasSeleccionados.add(infraestructurasSeleccionados);
		if (!tiposInfraestructurasSeleccionados.isEmpty()) {
			
			List<DetalleFichaPma> listaDetallesFichaPmaBdd = new ArrayList<DetalleFichaPma>();			
			List<CatalogoGeneral> tiposInfraestructurasSeleccionadosGuardar = new ArrayList<>();
			List<DetalleFichaPma> listaDetallesFichaPma = getDetallesFichaPmaPorIdFicha(fichaAmbientalPma.getId(),TipoCatalogo.TIPO_INFRAESTRUCTURA);			
			listaDetallesFichaPmaBdd.addAll(listaDetallesFichaPma);
			tiposInfraestructurasSeleccionadosGuardar.addAll(tiposInfraestructurasSeleccionados);
			
			for(DetalleFichaPma detalleFichaBdd : listaDetallesFichaPma){
				for(CatalogoGeneral infraestructura : tiposInfraestructurasSeleccionados){
					if(detalleFichaBdd.getCatalogoGeneral().getId().equals(infraestructura.getId())){
						listaDetallesFichaPmaBdd.remove(detalleFichaBdd);
						tiposInfraestructurasSeleccionadosGuardar.remove(infraestructura);
						break;
					}
				}
			}			
			
			for(DetalleFichaPma detalleBdd : listaDetallesFichaPmaBdd){
				DetalleFichaPma detalleHistorico = (DetalleFichaPma)SerializationUtils.clone(detalleBdd);
				detalleHistorico.setId(null);
				detalleHistorico.setIdRegistroOriginal(detalleBdd.getId());
				detalleHistorico.setFechaHistorico(new Date());
				detalleFichaPmaServiceBean.guardarDetalleFichaPma(detalleHistorico);
			}
			
			detalleFichaPmaServiceBean.eliminarDetalleFichaPma(listaDetallesFichaPmaBdd);

			for (CatalogoGeneral infraestructura : tiposInfraestructurasSeleccionadosGuardar) {
				DetalleFichaPma detalleFicha = new DetalleFichaPma();
				detalleFicha.setFichaAmbientalPma(fichaAmbientalPma);
				detalleFicha.setCatalogoGeneral(infraestructura);
				if(listaDetallesFichaPma != null && !listaDetallesFichaPma.isEmpty())
					detalleFicha.setFechaHistorico(new Date());
				detalleFichaPmaServiceBean.guardarDetalleFichaPma(detalleFicha);
			}
		}

		boolean guardarOtroOriginal = false;
		if (!tiposPredioSeleccionados.isEmpty()) {
			List<CatalogoGeneral> tiposPredioSeleccionadosGuardar = new ArrayList<>();
			List<DetalleFichaPma> listaDetallesFichaPmaBdd = new ArrayList<DetalleFichaPma>();			
			List<DetalleFichaPma> listaDetallesFichaPma = detalleFichaPmaServiceBean.getCatalogoGeneralFichaXIdFichaXTipoXCodigo(
					fichaAmbientalPma.getId(), TipoCatalogo.SITUACION_PREDIO, TipoCatalogo.CODIGO_PREDIO_PRIMARIO);
			
			tiposPredioSeleccionadosGuardar.addAll(tiposPredioSeleccionados);
			listaDetallesFichaPmaBdd.addAll(listaDetallesFichaPma);
			
			for(DetalleFichaPma detalleFicha : listaDetallesFichaPma){
				for(CatalogoGeneral predio: tiposPredioSeleccionados){
					if(detalleFicha.getCatalogoGeneral().getId().equals(predio.getId())){
						tiposPredioSeleccionadosGuardar.remove(predio);
						listaDetallesFichaPmaBdd.remove(detalleFicha);
						break;
					}					
				}
			}			
			
			for(DetalleFichaPma detalleFichaBdd: listaDetallesFichaPmaBdd){				
				DetalleFichaPma detalleFichaHistorico = (DetalleFichaPma) SerializationUtils.clone(detalleFichaBdd);
				detalleFichaHistorico.setId(null);
				detalleFichaHistorico.setIdRegistroOriginal(detalleFichaBdd.getId());
				detalleFichaHistorico.setFechaHistorico(new Date());
				detalleFichaPmaServiceBean.guardarDetalleFichaPma(detalleFichaHistorico);				
			}

			detalleFichaPmaServiceBean.eliminarDetalleFichaPma(listaDetallesFichaPmaBdd);
			
			if(listaDetallesFichaPma == null || listaDetallesFichaPma.isEmpty())
				guardarOtroOriginal = true;
				

			for (CatalogoGeneral predio : tiposPredioSeleccionadosGuardar) {				
				DetalleFichaPma detalleFicha = new DetalleFichaPma();
				detalleFicha.setFichaAmbientalPma(fichaAmbientalPma);
				detalleFicha.setCatalogoGeneral(predio);
				if(listaDetallesFichaPma != null && !listaDetallesFichaPma.isEmpty())
					detalleFicha.setFechaHistorico(new Date());				
				detalleFichaPmaServiceBean.guardarDetalleFichaPma(detalleFicha);				
			}
		}

		if (!tiposPredioSecundarios.isEmpty()) {
			
			List<DetalleFichaPma> listaDetallesFichaPmaBdd = new ArrayList<DetalleFichaPma>();
			List<CatalogoGeneral> tipoPrediosSecundariosGuardar = new ArrayList<>();
			
			List<DetalleFichaPma> listaDetallesFichaPma = detalleFichaPmaServiceBean.getCatalogoGeneralFichaXIdFichaXTipoXCodigo(
							fichaAmbientalPma.getId(), TipoCatalogo.SITUACION_PREDIO, TipoCatalogo.CODIGO_PREDIO_SECUDARIO);
			
			listaDetallesFichaPmaBdd.addAll(listaDetallesFichaPma);
			tipoPrediosSecundariosGuardar.addAll(tiposPredioSecundarios);
			
			//Le quita de la lista de bdd a los que están seleccionados
			for(DetalleFichaPma detalle : listaDetallesFichaPma){
				for(CatalogoGeneral predioSecundario : tiposPredioSecundarios){
					if(detalle.getCatalogoGeneral().getId().equals(predioSecundario.getId())){
						listaDetallesFichaPmaBdd.remove(detalle);
						tipoPrediosSecundariosGuardar.remove(predioSecundario);
						break;
					}
				}
			}

			for(DetalleFichaPma detalleFichaPmaBdd : listaDetallesFichaPmaBdd){
				DetalleFichaPma detalleFichaHistorico = (DetalleFichaPma) SerializationUtils.clone(detalleFichaPmaBdd);
				detalleFichaHistorico.setId(null);
				detalleFichaHistorico.setIdRegistroOriginal(detalleFichaPmaBdd.getId());
				detalleFichaHistorico.setFechaHistorico(new Date());
				detalleFichaHistorico.setEstado(true);
				detalleFichaPmaServiceBean.guardarDetalleFichaPma(detalleFichaHistorico);
			}			
			
			detalleFichaPmaServiceBean.eliminarDetalleFichaPma(listaDetallesFichaPmaBdd);

			for (CatalogoGeneral predio : tipoPrediosSecundariosGuardar) {
				DetalleFichaPma detalleFicha = new DetalleFichaPma();
				detalleFicha.setFichaAmbientalPma(fichaAmbientalPma);
				detalleFicha.setCatalogoGeneral(predio);
				if(!guardarOtroOriginal)
					detalleFicha.setFechaHistorico(new Date());
				detalleFichaPmaServiceBean.guardarDetalleFichaPma(detalleFicha);
			}
		}
	}
	
	public void eliminarAsociadoFaseHistorico(List<CatalogoCategoriaFase> catalogoCategoriaFases) throws Exception {
		try {
			for (CatalogoCategoriaFase catalogoCategoriaFase : catalogoCategoriaFases) {
				// ELIMINAMOS ACTIVIDADES
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("p_categoriaFaseId", catalogoCategoriaFase.getId());

				List<ActividadProcesoPma> actividadesProcesoPma = crudServiceBean.findByNamedQueryGeneric(ActividadProcesoPma.OBTENER_POR_FASE,	parameters);
				
				for(ActividadProcesoPma actividadesProcesoPmaBdd : actividadesProcesoPma){
					ActividadProcesoPma actividadProcesoPmaHistorico = (ActividadProcesoPma) SerializationUtils.clone(actividadesProcesoPmaBdd);
					actividadProcesoPmaHistorico.setId(null);
					actividadProcesoPmaHistorico.setIdRegistroOriginal(actividadesProcesoPmaBdd.getId());
					actividadProcesoPmaHistorico.setFechaHistorico(new Date());
					actividadProcesoPmaHistorico.setEstado(true);
					crudServiceBean.saveOrUpdate(actividadProcesoPmaHistorico);
				}				
				crudServiceBean.delete(actividadesProcesoPma);

				// ELIMINAMOS HERRAMIENTAS
				List<HerramientaProcesoPma> herramientaProcesoPma = crudServiceBean.findByNamedQueryGeneric(HerramientaProcesoPma.OBTENER_POR_FASE,parameters);
				for(HerramientaProcesoPma herramientaBdd : herramientaProcesoPma){
					HerramientaProcesoPma herramientaHistorico = (HerramientaProcesoPma) SerializationUtils.clone(herramientaBdd);
					herramientaHistorico.setId(null);
					herramientaHistorico.setIdRegistroOriginal(herramientaBdd.getId());
					herramientaHistorico.setFechaHistorico(new Date());
					herramientaHistorico.setEstado(true);
					crudServiceBean.saveOrUpdate(herramientaHistorico);
				}				
				crudServiceBean.delete(herramientaProcesoPma);

				// ELIMINAMOS INSUMOS
				List<InsumoProcesoPma> insumoProcesoPma = crudServiceBean.findByNamedQueryGeneric(InsumoProcesoPma.OBTENER_POR_FASE, parameters);
				
				for(InsumoProcesoPma insumoBdd : insumoProcesoPma){
					InsumoProcesoPma insumoHistorico = (InsumoProcesoPma)SerializationUtils.clone(insumoBdd);
					insumoHistorico.setId(null);
					insumoHistorico.setIdRegistroOriginal(insumoBdd.getId());
					insumoHistorico.setFechaHistorico(new Date());
					insumoHistorico.setEstado(true);
					crudServiceBean.saveOrUpdate(insumoHistorico);
				}
				crudServiceBean.delete(insumoProcesoPma);
				
				List<ImpactoAmbientalPma> impactosAmbientalPma = crudServiceBean.findByNamedQueryGeneric(ImpactoAmbientalPma.OBTENER_POR_FASE,parameters);
				
				for(ImpactoAmbientalPma impactoAmbientalBdd : impactosAmbientalPma){
					ImpactoAmbientalPma impactoAmbientalHistorico = (ImpactoAmbientalPma)SerializationUtils.clone(impactoAmbientalBdd);
					impactoAmbientalHistorico.setId(null);
					impactoAmbientalHistorico.setIdRegistroOriginal(impactoAmbientalBdd.getId());
					impactoAmbientalHistorico.setFechaHistorial(new Date());
					impactoAmbientalHistorico.setEstado(true);
					crudServiceBean.saveOrUpdate(impactoAmbientalHistorico);
				}

				crudServiceBean.delete(impactosAmbientalPma);

			}
		} catch (Exception e) {
			throw new Exception(e);
		}

	}
	
	private boolean compararFicha(FichaAmbientalPma fichaAmbientalPmaBdd, FichaAmbientalPma fichaAmbientalPma){
		
		try {
			if(((fichaAmbientalPmaBdd.getOtrosInfraestructura() == null && fichaAmbientalPma.getOtrosInfraestructura() == null) || 
					fichaAmbientalPmaBdd.getOtrosInfraestructura() != null && fichaAmbientalPma.getOtrosInfraestructura() != null && 
					fichaAmbientalPmaBdd.getOtrosInfraestructura().equals(fichaAmbientalPma.getOtrosInfraestructura())) && 
				((fichaAmbientalPmaBdd.getDescripcionZona() == null && fichaAmbientalPma.getDescripcionZona() == null) || 
						fichaAmbientalPmaBdd.getDescripcionZona() != null && fichaAmbientalPma.getDescripcionZona() != null && 
						fichaAmbientalPmaBdd.getDescripcionZona().equals(fichaAmbientalPma.getDescripcionZona())) &&
				((fichaAmbientalPmaBdd.getAreaImplantacion() == null && fichaAmbientalPma.getAreaImplantacion() == null) || 
						fichaAmbientalPmaBdd.getAreaImplantacion() != null && fichaAmbientalPma.getAreaImplantacion() != null && 
						fichaAmbientalPmaBdd.getAreaImplantacion().equals(fichaAmbientalPma.getAreaImplantacion())) && 
				((fichaAmbientalPmaBdd.getAguaPotable() == null && fichaAmbientalPma.getAguaPotable() == null) || 
						fichaAmbientalPmaBdd.getAguaPotable() != null && fichaAmbientalPma.getAguaPotable() != null && 
						fichaAmbientalPmaBdd.getAguaPotable().equals(fichaAmbientalPma.getAguaPotable())) && 
				((fichaAmbientalPmaBdd.getConsumoAgua() == null && fichaAmbientalPma.getConsumoAgua() == null) || 
						fichaAmbientalPmaBdd.getConsumoAgua() != null && fichaAmbientalPma.getConsumoAgua() != null && 
						fichaAmbientalPmaBdd.getConsumoAgua().equals(fichaAmbientalPma.getConsumoAgua())) && 
				((fichaAmbientalPmaBdd.getEnergiaElectrica() == null && fichaAmbientalPma.getEnergiaElectrica() == null) || 
						fichaAmbientalPmaBdd.getEnergiaElectrica() != null && fichaAmbientalPma.getEnergiaElectrica() != null && 
						fichaAmbientalPmaBdd.getEnergiaElectrica().equals(fichaAmbientalPma.getEnergiaElectrica())) && 
				((fichaAmbientalPmaBdd.getConsumoElectrico() == null && fichaAmbientalPma.getConsumoElectrico() == null) || 
						fichaAmbientalPmaBdd.getConsumoElectrico() != null && fichaAmbientalPma.getConsumoElectrico() != null && 
						fichaAmbientalPmaBdd.getConsumoElectrico().equals(fichaAmbientalPma.getConsumoElectrico())) && 
				((fichaAmbientalPmaBdd.getAccesoVehicular() == null && fichaAmbientalPma.getAccesoVehicular() == null) || 
						fichaAmbientalPmaBdd.getAccesoVehicular() != null && fichaAmbientalPma.getAccesoVehicular() != null && 
						fichaAmbientalPmaBdd.getAccesoVehicular().equals(fichaAmbientalPma.getAccesoVehicular())) &&
				((fichaAmbientalPmaBdd.getTipoVia() == null && fichaAmbientalPma.getTipoVia() == null) || 
						fichaAmbientalPmaBdd.getTipoVia() != null && fichaAmbientalPma.getTipoVia() != null && 
						fichaAmbientalPmaBdd.getTipoVia().equals(fichaAmbientalPma.getTipoVia())) && 
				((fichaAmbientalPmaBdd.getAlcantarillado() == null && fichaAmbientalPma.getAlcantarillado() == null) || 
						fichaAmbientalPmaBdd.getAlcantarillado() != null && fichaAmbientalPma.getAlcantarillado() != null && 
						fichaAmbientalPmaBdd.getAlcantarillado().equals(fichaAmbientalPma.getAlcantarillado())) && 
				((fichaAmbientalPmaBdd.getObservacionesEspacioFisico() == null && fichaAmbientalPma.getObservacionesEspacioFisico() == null) || 
						fichaAmbientalPmaBdd.getObservacionesEspacioFisico() != null && fichaAmbientalPma.getObservacionesEspacioFisico() != null && 
						fichaAmbientalPmaBdd.getObservacionesEspacioFisico().equals(fichaAmbientalPma.getObservacionesEspacioFisico())) && 
				((fichaAmbientalPmaBdd.getObservacionesPredio() == null && fichaAmbientalPma.getObservacionesPredio() == null) || 
						fichaAmbientalPmaBdd.getObservacionesPredio() != null && fichaAmbientalPma.getObservacionesPredio() != null && 
						fichaAmbientalPmaBdd.getObservacionesPredio().equals(fichaAmbientalPma.getObservacionesPredio()))
					){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();			
			return false;
		}		
	}
	
	
	public List<FichaAmbientalPma> getFichaAmbientalPorIdProyectoHistorico(Integer id) {
		return fichaAmbientalPmaServiceBean.getFichaAmbientalPorIdProyectoHistorico(id);
	}
		
	public List<DetalleFichaPma> getDetalleFichaPorIdFichaPorTipoHistorico(Integer idFicha, Integer tipo){
		return detalleFichaPmaServiceBean.getDetalleFichaPorIdFichaPorTipoHistorico(idFicha, tipo);
	}
	
	public List<DetalleFichaPma> getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigoHistorico(Integer idFicha, Integer tipo, String tipoCatalogo){
		return detalleFichaPmaServiceBean.getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigoHistorico(idFicha, tipo, tipoCatalogo);
	}
	
	public List<DetalleFichaPma> getDetalleFichaPorIdFichaPorTipoPorCodigo(Integer idFicha, Integer tipo, String tipoCatalogo){
		return detalleFichaPmaServiceBean.getDetalleFichaPorIdFichaPorTipoPorCodigo(idFicha, tipo, tipoCatalogo);
	}
	
	public List<DetalleFichaPma> getCatalogoGeneralFichaPorIdFichaPorTipoGeneral(
			Integer idFicha, Integer tipo) {
		return detalleFichaPmaServiceBean.getCatalogoGeneralFichaPorIdFichaPorTipoGeneral(idFicha, tipo);
	}
	
	/**
	 * Metodos de guardar de Descripcion del proyecto
	 */
	@SuppressWarnings("unchecked")
	public void guardarInstalacionesProcesoHistorico(Integer idFichaAmbiental,List<Instalacion> newInstalacionesProceso) {
		try {
			List<Instalacion> listaInstalacionBdd = crudServiceBean.getEntityManager().createQuery(
					"Select i from Instalacion i where i.fichaAmbientalPma.id=:idFicha and i.estado=true and i.idRegistroOriginal = null order by 1 asc")
					.setParameter("idFicha", idFichaAmbiental).getResultList();
			
			List<Integer> listaIdCatalogos = new ArrayList<Integer>();
			List<Instalacion> listaHistoricoGuardar = new ArrayList<Instalacion>();
			List<Instalacion> listaNuevasInstalaciones = new ArrayList<Instalacion>();
			List<Integer> listaHistoricoGuardarAux = new ArrayList<Integer>();
			listaNuevasInstalaciones.addAll(newInstalacionesProceso);
			
			if(listaInstalacionBdd != null && !listaInstalacionBdd.isEmpty()){
				listaHistoricoGuardar.addAll(listaInstalacionBdd);
			
				for(Instalacion instalacionBdd : listaInstalacionBdd){
					listaIdCatalogos.add(instalacionBdd.getCatalogoInstalacion().getId());
				}		
				
				for(Instalacion instalacionAct : newInstalacionesProceso){				
					
					Comparator<Integer> c = new Comparator<Integer>() {
						
						@Override
						public int compare(Integer o1, Integer o2) {
							return o1.compareTo(o2);
						}
					};
					
					Collections.sort(listaIdCatalogos, c);
					
					int index = Collections.binarySearch(listaIdCatalogos, instalacionAct.getCatalogoInstalacion().getId());
					
					if(index >= 0){
						listaNuevasInstalaciones.remove(instalacionAct);
						listaHistoricoGuardarAux.add(instalacionAct.getCatalogoInstalacion().getId());
					}
				}
				
				for(Integer instalacionIngresada : listaHistoricoGuardarAux){
					for(Instalacion instalacionBdd : listaInstalacionBdd){
						if(instalacionIngresada.equals(instalacionBdd.getCatalogoInstalacion().getId())){
							listaHistoricoGuardar.remove(instalacionBdd);
							break;
						}
					}
				}		
				
				//guarda el historial
				
				for (Instalacion instalacion : listaHistoricoGuardar) {
					Instalacion instalacionHis = new Instalacion();
					instalacionHis = (Instalacion) SerializationUtils.clone(instalacion);
					instalacionHis.setId(null);
					instalacionHis.setFechaHistorico(new Date());
					instalacionHis.setIdRegistroOriginal(instalacion.getId());
					crudServiceBean.saveOrUpdate(instalacionHis);
					
					instalacion.setEstado(false);
					crudServiceBean.saveOrUpdate(instalacion);
				}			
			}
			
			for (Instalacion instalacion : listaNuevasInstalaciones) {
				if(listaInstalacionBdd != null && !listaInstalacionBdd.isEmpty())
					instalacion.setFechaHistorico(new Date());
				crudServiceBean.saveOrUpdate(instalacion);
			}
		
		} catch (Exception e) {
			e.addSuppressed(e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void guardarTecnicasProcesoHistorico(Integer idFichaAmbiental, List<TecnicaProcesoPma> tecnicasProceso) {
		try {
			
			List<TecnicaProcesoPma> listaTecnicaProcesoBdd = crudServiceBean.getEntityManager().createQuery(
					"SELECT t FROM TecnicaProcesoPma t where "
					+ "t.fichaAmbientalPma.id=:idFicha and t.estado=true "
					+ "and t.idRegistroOriginal = null").setParameter("idFicha", idFichaAmbiental).getResultList();
			
			List<Integer> listaIdCatalogos = new ArrayList<Integer>();
			List<TecnicaProcesoPma> listaHistoricoGuardar = new ArrayList<TecnicaProcesoPma>();
			List<TecnicaProcesoPma> listaNuevasTecnicas = new ArrayList<TecnicaProcesoPma>();
			List<Integer> listaHistoricoGuardarAux = new ArrayList<Integer>();
			listaNuevasTecnicas.addAll(tecnicasProceso);
			
			if(listaTecnicaProcesoBdd != null && !listaTecnicaProcesoBdd.isEmpty()){
				listaHistoricoGuardar.addAll(listaTecnicaProcesoBdd);
				
				for(TecnicaProcesoPma tecnicaBdd : listaTecnicaProcesoBdd){
					listaIdCatalogos.add(tecnicaBdd.getCatalogoTecnica().getId());
				}
				
				for(TecnicaProcesoPma tecnicaAct : tecnicasProceso){				
					
					Comparator<Integer> c = new Comparator<Integer>() {
						
						@Override
						public int compare(Integer o1, Integer o2) {
							return o1.compareTo(o2);
						}
					};
					
					Collections.sort(listaIdCatalogos, c);
					
					int index = Collections.binarySearch(listaIdCatalogos, tecnicaAct.getCatalogoTecnica().getId());
					
					if(index >= 0){
						listaNuevasTecnicas.remove(tecnicaAct);
						listaHistoricoGuardarAux.add(tecnicaAct.getCatalogoTecnica().getId());
					}
				}
				
				for(Integer tecnicaIngresada : listaHistoricoGuardarAux){
					for(TecnicaProcesoPma tecnicaBdd : listaTecnicaProcesoBdd){
						if(tecnicaIngresada.equals(tecnicaBdd.getCatalogoTecnica().getId())){
							listaHistoricoGuardar.remove(tecnicaBdd);
							break;
						}
					}
				}		
				
				for (TecnicaProcesoPma tecnica : listaHistoricoGuardar) {
					TecnicaProcesoPma tecnicaHistorico = (TecnicaProcesoPma) SerializationUtils.clone(tecnica);
					tecnicaHistorico.setId(null);
					tecnicaHistorico.setIdRegistroOriginal(tecnica.getId());
					tecnicaHistorico.setFechaHistorico(new Date());
					crudServiceBean.saveOrUpdate(tecnicaHistorico);
					
					tecnica.setEstado(false);
					crudServiceBean.saveOrUpdate(tecnica);
				}			
			}
			
			for (TecnicaProcesoPma tecnica : listaNuevasTecnicas) {
				if(listaTecnicaProcesoBdd != null && !listaTecnicaProcesoBdd.isEmpty())
					tecnica.setFechaHistorico(new Date());
				crudServiceBean.saveOrUpdate(tecnica);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void guardarPlaguicidasProcesoHistorico(Integer idFichaAmbiental, List<PlaguicidaProcesoPma> plaguicidasProceso) {
		// Solo se buscan las plaguicidas viejas
		List<PlaguicidaProcesoPma> listaPlaguicidasBdd = new ArrayList<PlaguicidaProcesoPma>();
		List<PlaguicidaProcesoPma> listaPlaguicidasGuardar= new ArrayList<PlaguicidaProcesoPma>();
		List<PlaguicidaProcesoPma> listaPlaguicidasGuardarAux= new ArrayList<PlaguicidaProcesoPma>();
		
		listaPlaguicidasBdd = crudServiceBean.getEntityManager().createQuery(
				"SELECT p FROM PlaguicidaProcesoPma p where "
				+ "p.fichaAmbientalPma.id=:idFicha and p.estado=true "
				+ "and p.idRegistroOriginal = null").setParameter("idFicha", idFichaAmbiental).getResultList();
		
		if(listaPlaguicidasBdd != null){
			listaPlaguicidasGuardarAux.addAll(listaPlaguicidasBdd);
			
			//codigo anterior
			for (PlaguicidaProcesoPma plaguicida : plaguicidasProceso) {
				
				if (plaguicida.getId() != null) {
					//codigo nuevo
					
					Comparator<PlaguicidaProcesoPma> c = new Comparator<PlaguicidaProcesoPma>() {
						
						@Override
						public int compare(PlaguicidaProcesoPma o1, PlaguicidaProcesoPma o2) {						
							return o1.getId().compareTo(o2.getId());
						}
					};
					
					Collections.sort(listaPlaguicidasBdd, c);
					int index = Collections.binarySearch(listaPlaguicidasBdd, new PlaguicidaProcesoPma(plaguicida.getId()), c);
					
					if(index >= 0){
						PlaguicidaProcesoPma plaguicidaBdd = listaPlaguicidasBdd.get(index);
						listaPlaguicidasGuardarAux.remove(plaguicidaBdd);
						
						if(plaguicidaBdd.getCategoriaToxicologica().equals(plaguicida.getCategoriaToxicologica()) && 
								plaguicidaBdd.getDosisAplicacion() == plaguicida.getDosisAplicacion() && 
								plaguicidaBdd.getFechaCaducidad().equals(plaguicida.getFechaCaducidad()) && 
								plaguicidaBdd.getFichaAmbientalPma().equals(plaguicida.getFichaAmbientalPma()) && 
								plaguicidaBdd.getFrecuenciaAplicacion().equals(plaguicida.getFrecuenciaAplicacion()) && 
								plaguicidaBdd.getNombreComercial().equals(plaguicida.getNombreComercial()) && 
								plaguicidaBdd.getPresentacion().equals(plaguicida.getPresentacion())
								){				
								continue;
						}else{
							PlaguicidaProcesoPma plaguicidaHistorico = new PlaguicidaProcesoPma();
							plaguicidaHistorico = (PlaguicidaProcesoPma) SerializationUtils.clone(plaguicidaBdd);
							plaguicidaHistorico.setId(null);
							plaguicidaHistorico.setFechaHistorico(new Date());
							plaguicidaHistorico.setIdRegistroOriginal(plaguicidaBdd.getId());
							listaPlaguicidasGuardar.add(plaguicidaHistorico);
							
							listaPlaguicidasGuardar.add(plaguicida);
						}										
					}				
				}else{
					if(listaPlaguicidasBdd != null && !listaPlaguicidasBdd.isEmpty())
						plaguicida.setFechaHistorico(new Date());
					listaPlaguicidasGuardar.add(plaguicida);
				}
			}
			
			for(PlaguicidaProcesoPma plaguicidaEliminado : listaPlaguicidasGuardarAux){
				PlaguicidaProcesoPma plaguicidaHistorico = new PlaguicidaProcesoPma();
				plaguicidaHistorico = (PlaguicidaProcesoPma) SerializationUtils.clone(plaguicidaEliminado);
				plaguicidaHistorico.setId(null);
				plaguicidaHistorico.setFechaHistorico(new Date());
				plaguicidaHistorico.setIdRegistroOriginal(plaguicidaEliminado.getId());
				listaPlaguicidasGuardar.add(plaguicidaHistorico);			
				
				plaguicidaEliminado.setEstado(false);	
				listaPlaguicidasGuardar.add(plaguicidaEliminado);
			}
			
			for (PlaguicidaProcesoPma plaguicida : listaPlaguicidasGuardar) {
//				if(plaguicida.getId() == null)
//					plaguicida.setFechaHistorico(new Date());
				crudServiceBean.saveOrUpdate(plaguicida);
			}		
		}else{
			crudServiceBean.saveOrUpdate(plaguicidasProceso);
		}
	}
	
	public void guardarDesechoSanitarioProcesoHistorico(DesechoSanitarioProcesoPma desechoSanitarioProceso) {		
		try {
			if(desechoSanitarioProceso.getId() != null){
				if(!desechoSanitarioProceso.getEstado()){
					DesechoSanitarioProcesoPma desechoHistorico = new DesechoSanitarioProcesoPma();
					desechoHistorico = (DesechoSanitarioProcesoPma) SerializationUtils.clone(desechoSanitarioProceso);
					desechoHistorico.setId(null);
					desechoHistorico.setEstado(true);
					desechoHistorico.setFechaHistorico(new Date());
					desechoHistorico.setIdRegistroOriginal(desechoSanitarioProceso.getId());
					crudServiceBean.saveOrUpdate(desechoHistorico);
					
					//aqui el metodo
					desactivarDesechosPeligrososProceso(desechoSanitarioProceso.getFichaAmbientalPma().getId());
					
				}else{
					DesechoSanitarioProcesoPma desechoBdd = crudServiceBean.find(DesechoSanitarioProcesoPma.class, desechoSanitarioProceso.getId());
					if(desechoBdd != null){
						if(desechoBdd.getCapacidadAlmacenamiento().equals(desechoSanitarioProceso.getCapacidadAlmacenamiento()) && 
								desechoBdd.getMetodoReduccion().equals(desechoSanitarioProceso.getMetodoReduccion()) && 
								desechoBdd.getOperacionesPrevias().equals(desechoSanitarioProceso.getOperacionesPrevias())){
							//son iguales
						}else{
							DesechoSanitarioProcesoPma desechoHistorico = new DesechoSanitarioProcesoPma();
							desechoHistorico = (DesechoSanitarioProcesoPma) SerializationUtils.clone(desechoBdd);
							desechoHistorico.setId(null);
							desechoHistorico.setEstado(true);
							desechoHistorico.setFechaHistorico(new Date());
							desechoHistorico.setIdRegistroOriginal(desechoBdd.getId());
							crudServiceBean.saveOrUpdate(desechoHistorico);
							
							crudServiceBean.saveOrUpdate(desechoSanitarioProceso);
						}
					}				
				}	
			}else{
				crudServiceBean.saveOrUpdate(desechoSanitarioProceso);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("unchecked")
	private void desactivarDesechosPeligrososProceso(Integer idFichaAmbiental){
		try {
			List<DesechoPeligrosoProcesoPma> desechosBdd = new ArrayList<DesechoPeligrosoProcesoPma>();
			desechosBdd = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DesechoPeligrosoProcesoPma d "
					+ "where d.fichaAmbientalPma.id=:idFicha and d.estado=true and d.idRegistroOriginal = null")
					.setParameter("idFicha", idFichaAmbiental).getResultList();
			
			for(DesechoPeligrosoProcesoPma desechoEliminado : desechosBdd){
				DesechoPeligrosoProcesoPma desechoHistorico = new DesechoPeligrosoProcesoPma();
				desechoHistorico = (DesechoPeligrosoProcesoPma)SerializationUtils.clone(desechoEliminado);
				desechoHistorico.setId(null);
				desechoHistorico.setFechaHistorico(new Date());
				desechoHistorico.setIdRegistroOriginal(desechoEliminado.getId());
				crudServiceBean.saveOrUpdate(desechoHistorico);
				
				desechoEliminado.setEstado(false);
				crudServiceBean.saveOrUpdate(desechoEliminado);		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	//Exante historico
	@SuppressWarnings("unchecked")
	public void guardarDesechosPeligrososProcesoHistorico(Integer idFichaAmbiental, List<DesechoPeligrosoProcesoPma> desechosProceso) {
		try {
			
			List<DesechoPeligrosoProcesoPma> desechosBdd = new ArrayList<DesechoPeligrosoProcesoPma>();
			List<DesechoPeligrosoProcesoPma> desechosGuardar = new ArrayList<DesechoPeligrosoProcesoPma>();
			List<DesechoPeligrosoProcesoPma> desechosBddAux = new ArrayList<DesechoPeligrosoProcesoPma>();
			
			desechosBdd = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DesechoPeligrosoProcesoPma d "
							+ "where d.fichaAmbientalPma.id=:idFicha and d.estado=true and d.idRegistroOriginal = null")
							.setParameter("idFicha", idFichaAmbiental).getResultList();
			
			if(desechosBdd != null && !desechosBdd.isEmpty()){
				desechosBddAux.addAll(desechosBdd);
				
				for(DesechoPeligrosoProcesoPma desecho : desechosProceso){
					if(desecho.getId() != null){
						
						Comparator<DesechoPeligrosoProcesoPma> c = new Comparator<DesechoPeligrosoProcesoPma>() {
							
							@Override
							public int compare(DesechoPeligrosoProcesoPma o1, DesechoPeligrosoProcesoPma o2) {							
								return o1.getId().compareTo(o2.getId());
							}
						};
						
						Collections.sort(desechosBdd, c);
						int index = Collections.binarySearch(desechosBdd, new DesechoPeligrosoProcesoPma(desecho.getId()), c);
						
						if(index >= 0){
												
							DesechoPeligrosoProcesoPma desechoBdd = desechosBdd.get(index);						
							desechosBddAux.remove(desechoBdd);
							
							if(((desechoBdd.getCapacidadRecoleccion() == null && desecho.getCapacidadRecoleccion() == null) ||
									desechoBdd.getCapacidadRecoleccion() != null && desecho.getCapacidadRecoleccion() != null && 
									desechoBdd.getCapacidadRecoleccion().equals(desecho.getCapacidadRecoleccion())) && 
									desechoBdd.getDesechoPeligroso().equals(desecho.getDesechoPeligroso()) && 
									desechoBdd.getTipoEnvalaje().equals(desecho.getTipoEnvalaje()) && 
									((desechoBdd.getVehiculo() == null && desecho.getVehiculo() == null) || 
									desechoBdd.getVehiculo() != null && desecho.getVehiculo() != null && 
									desechoBdd.getVehiculo().equals(desecho.getVehiculo())) && 
									((desechoBdd.getVehiculoDescripcionExantes() == null && desecho.getVehiculoDescripcionExantes() == null) ||
									desechoBdd.getVehiculoDescripcionExantes() != null && desecho.getVehiculoDescripcionExantes() != null && 
									desechoBdd.getVehiculoDescripcionExantes().equals(desecho.getVehiculoDescripcionExantes()))){
								//son iguales
							}else{
								
								DesechoPeligrosoProcesoPma desechoHistorico = new DesechoPeligrosoProcesoPma();
								desechoHistorico = (DesechoPeligrosoProcesoPma)SerializationUtils.clone(desechoBdd);
								desechoHistorico.setId(null);
								desechoHistorico.setFechaHistorico(new Date());
								desechoHistorico.setIdRegistroOriginal(desechoBdd.getId());
								desechosGuardar.add(desechoHistorico);
								
								desechosGuardar.add(desecho);							
							}						
						}					
					}else{
						desecho.setFechaHistorico(new Date());
						desechosGuardar.add(desecho);
					}
				}
				
				for(DesechoPeligrosoProcesoPma desechoEliminado : desechosBddAux){
					DesechoPeligrosoProcesoPma desechoHistorico = new DesechoPeligrosoProcesoPma();
					desechoHistorico = (DesechoPeligrosoProcesoPma)SerializationUtils.clone(desechoEliminado);
					desechoHistorico.setId(null);
					desechoHistorico.setFechaHistorico(new Date());
					desechoHistorico.setIdRegistroOriginal(desechoEliminado.getId());
					desechosGuardar.add(desechoHistorico);
					
					desechoEliminado.setEstado(false);
					desechosGuardar.add(desechoEliminado);				
				}
				
				for(DesechoPeligrosoProcesoPma desecho : desechosGuardar){
					if(desecho.getId() == null)
						desecho.setFechaHistorico(new Date());
					
					crudServiceBean.saveOrUpdate(desecho);
				}
			}else{
				crudServiceBean.saveOrUpdate(desechosProceso);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
		
	@SuppressWarnings("unchecked")
	public void guardarVehiculosProcesoHistorico(FichaAmbientalPma fichaAmbiental, List<VehiculoDesechoSanitarioProcesoPma> vehiculosProceso) {
		try {
			List<VehiculoDesechoSanitarioProcesoPma> listaVehiculosBdd = new ArrayList<VehiculoDesechoSanitarioProcesoPma>();
			List<VehiculoDesechoSanitarioProcesoPma> listaVehiculosGuardar = new ArrayList<VehiculoDesechoSanitarioProcesoPma>();
			List<VehiculoDesechoSanitarioProcesoPma> listaVehiculosGuardarAux = new ArrayList<VehiculoDesechoSanitarioProcesoPma>();
			
			listaVehiculosBdd = crudServiceBean.getEntityManager().createQuery("SELECT v FROM VehiculoDesechoSanitarioProcesoPma v "
					+ "where v.fichaAmbientalPma.id=:idFicha and v.estado=true and v.idRegistroOriginal = null").
					setParameter("idFicha", fichaAmbiental.getId()).getResultList();
			
			if(listaVehiculosBdd != null && !listaVehiculosBdd.isEmpty()){
				listaVehiculosGuardarAux.addAll(listaVehiculosBdd);
				
				for(VehiculoDesechoSanitarioProcesoPma vehiculo : vehiculosProceso){
					if(vehiculo.getId() != null){
						
						Comparator<VehiculoDesechoSanitarioProcesoPma> c = new Comparator<VehiculoDesechoSanitarioProcesoPma>() {
							
							@Override
							public int compare(VehiculoDesechoSanitarioProcesoPma o1,
									VehiculoDesechoSanitarioProcesoPma o2) {							
								return o1.getId().compareTo(o2.getId());
							}
						};
						
						Collections.sort(listaVehiculosBdd, c);
						int index = Collections.binarySearch(listaVehiculosBdd, new VehiculoDesechoSanitarioProcesoPma(vehiculo.getId()), c);
						
						if(index >= 0){
							VehiculoDesechoSanitarioProcesoPma vehiculoBdd = listaVehiculosBdd.get(index);
							listaVehiculosGuardarAux.remove(vehiculoBdd);
							
							if(vehiculoBdd.getCilindraje().equals(vehiculo.getCilindraje()) && 
									vehiculoBdd.getCodigoDNCA().equals(vehiculo.getCodigoDNCA()) && 								
									vehiculo.getCertificadoInspeccion() == null &&
									vehiculo.getImagenVehiculo() == null && 
									vehiculo.getMatricula() == null &&
									vehiculoBdd.getLugarLavado().equals(vehiculo.getLugarLavado()) && 
									vehiculoBdd.getModelo().equals(vehiculo.getModelo()) && 
									vehiculoBdd.getNumeroMotor().equals(vehiculo.getNumeroMotor()) && 
									vehiculoBdd.getNumeroPlaca().equals(vehiculo.getNumeroPlaca()) &&
									vehiculoBdd.getPbv().equals(vehiculo.getPbv()) && 
									vehiculoBdd.getPv().equals(vehiculo.getPv()) && 
									vehiculoBdd.getTipo().equals(vehiculo.getTipo()) && 
									vehiculoBdd.getTonelaje().equals(vehiculo.getTonelaje())){
								//son iguales							
							}else{
								VehiculoDesechoSanitarioProcesoPma vehiculoHistorico = new VehiculoDesechoSanitarioProcesoPma();
								vehiculoHistorico = (VehiculoDesechoSanitarioProcesoPma)SerializationUtils.clone(vehiculoBdd);
								vehiculoHistorico.setId(null);
								vehiculoHistorico.setFechaHistorico(new Date());
								vehiculoHistorico.setIdRegistroOriginal(vehiculoBdd.getId());
								listaVehiculosGuardar.add(vehiculoHistorico);
								
								//Para guardar los cambios hechos en el objeto origianl
								// Imagen del vehículo
								if (vehiculo.getImagenVehiculo() != null) {
									vehiculo.setDocumentoImagenVehículo(ingresarAnexoVehiculo(
											vehiculo.getImagenVehiculo(), fichaAmbiental.getProyectoLicenciamientoAmbiental().getId(), 
											fichaAmbiental.getProyectoLicenciamientoAmbiental().getCodigo(),
											fichaAmbiental.getProcessId(), 0L, TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
								}
								// Certificado de inspección
								if (vehiculo.getCertificadoInspeccion() != null) {
									vehiculo.setDocumentoCertificadoInspeccion(ingresarAnexoVehiculo(
											vehiculo.getCertificadoInspeccion(), fichaAmbiental.getProyectoLicenciamientoAmbiental().getId(), 
											fichaAmbiental.getProyectoLicenciamientoAmbiental().getCodigo(),
											fichaAmbiental.getProcessId(), 0L, TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
								}
								// Matricula
								if (vehiculo.getMatricula() != null) {
									vehiculo.setDocumentoMatricula(ingresarAnexoVehiculo(vehiculo.getMatricula(), 
											fichaAmbiental.getProyectoLicenciamientoAmbiental().getId(), 
											fichaAmbiental.getProyectoLicenciamientoAmbiental().getCodigo(),
											fichaAmbiental.getProcessId(), 0L, TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
								}													
								listaVehiculosGuardar.add(vehiculo);
							}						
						}					
					}else{
						//Para guardar los cambios hechos en el objeto original
						// Imagen del vehículo
						if (vehiculo.getImagenVehiculo() != null) {
							vehiculo.setDocumentoImagenVehículo(ingresarAnexoVehiculo(
									vehiculo.getImagenVehiculo(), fichaAmbiental.getProyectoLicenciamientoAmbiental().getId(), 
									fichaAmbiental.getProyectoLicenciamientoAmbiental().getCodigo(),
									fichaAmbiental.getProcessId(), 0L, TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
						}
						// Certificado de inspección
						if (vehiculo.getCertificadoInspeccion() != null) {
							vehiculo.setDocumentoCertificadoInspeccion(ingresarAnexoVehiculo(
									vehiculo.getCertificadoInspeccion(), fichaAmbiental.getProyectoLicenciamientoAmbiental().getId(), 
									fichaAmbiental.getProyectoLicenciamientoAmbiental().getCodigo(),
									fichaAmbiental.getProcessId(), 0L, TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
						}
						// Matricula
						if (vehiculo.getMatricula() != null) {
							vehiculo.setDocumentoMatricula(ingresarAnexoVehiculo(vehiculo.getMatricula(), 
									fichaAmbiental.getProyectoLicenciamientoAmbiental().getId(), 
									fichaAmbiental.getProyectoLicenciamientoAmbiental().getCodigo(),
									fichaAmbiental.getProcessId(), 0L, TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
						}							
						vehiculo.setFechaHistorico(new Date());
						listaVehiculosGuardar.add(vehiculo);					
					}
				}
				
				for(VehiculoDesechoSanitarioProcesoPma vehiculoEliminado : listaVehiculosGuardarAux){
					VehiculoDesechoSanitarioProcesoPma vehiculoHistorico = new VehiculoDesechoSanitarioProcesoPma();
					vehiculoHistorico = (VehiculoDesechoSanitarioProcesoPma)SerializationUtils.clone(vehiculoEliminado);
					vehiculoHistorico.setId(null);
					vehiculoHistorico.setFechaHistorico(new Date());
					vehiculoHistorico.setIdRegistroOriginal(vehiculoEliminado.getId());
					listaVehiculosGuardar.add(vehiculoHistorico);
					
					vehiculoEliminado.setEstado(false);
					listaVehiculosGuardar.add(vehiculoEliminado);
				}
				
				for(VehiculoDesechoSanitarioProcesoPma vehiculoGuardar : listaVehiculosGuardar)	{
					if(listaVehiculosBdd != null && !listaVehiculosBdd.isEmpty())
						vehiculoGuardar.setFechaHistorico(new Date());
					
					crudServiceBean.saveOrUpdate(vehiculoGuardar);
				}				
			}else{
				crudServiceBean.saveOrUpdate(vehiculosProceso);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	@SuppressWarnings("unchecked")
	public void guardarFertilizanteProcesoHistorico(Integer idFichaAmbiental, List<FertilizanteProcesoPma> fertilizantesProceso) {
		try {
			List<FertilizanteProcesoPma> listaFertilizanteBdd = new ArrayList<FertilizanteProcesoPma>();
			List<FertilizanteProcesoPma> listaFertilizanteGuardar = new ArrayList<FertilizanteProcesoPma>();
			List<FertilizanteProcesoPma> listaFertilizanteGuardarAux = new ArrayList<FertilizanteProcesoPma>();
			
			listaFertilizanteBdd = crudServiceBean.getEntityManager().createQuery(
					"SELECT f from FertilizanteProcesoPma f where f.fichaAmbientalPma.id=:idFicha "
					+ "and f.estado=true and f.idRegistroOriginal = null").setParameter("idFicha", idFichaAmbiental).getResultList();
			
			if(listaFertilizanteBdd != null && !listaFertilizanteBdd.isEmpty()){
				listaFertilizanteGuardarAux.addAll(listaFertilizanteBdd);
				
				for(FertilizanteProcesoPma fertilizante : fertilizantesProceso){
					if(fertilizante.getId() != null){
						Comparator<FertilizanteProcesoPma> c = new Comparator<FertilizanteProcesoPma>() {
							
							@Override
							public int compare(FertilizanteProcesoPma f1, FertilizanteProcesoPma f2) {
								return f1.getId().compareTo(f2.getId());
							}
						};
						
						Collections.sort(listaFertilizanteBdd, c);
						int index = Collections.binarySearch(listaFertilizanteBdd, new FertilizanteProcesoPma(fertilizante.getId()), c);
						
						if(index >= 0){
							FertilizanteProcesoPma fertilizanteBdd = listaFertilizanteBdd.get(index);
							listaFertilizanteGuardarAux.remove(fertilizanteBdd);
							
							if(fertilizanteBdd.getEsOrganico().equals(fertilizante.getEsOrganico()) && 
									fertilizanteBdd.getDosisAplicacion() == fertilizante.getDosisAplicacion() && 
									fertilizanteBdd.getNombreComercial().equals(fertilizante.getNombreComercial())){
								//son iguales
							}else{
								FertilizanteProcesoPma fertilizanteHistorico = new FertilizanteProcesoPma();
								fertilizanteHistorico = (FertilizanteProcesoPma)SerializationUtils.clone(fertilizanteBdd);
								fertilizanteHistorico.setId(null);
								fertilizanteHistorico.setFechaHistorico(new Date());
								fertilizanteHistorico.setIdRegistroOriginal(fertilizanteBdd.getId());
								listaFertilizanteGuardar.add(fertilizanteHistorico);
								
								listaFertilizanteGuardar.add(fertilizante);							
							}
						}					
					}else{
						fertilizante.setFechaHistorico(new Date());
						listaFertilizanteGuardar.add(fertilizante);
					}
				}		
				
				for(FertilizanteProcesoPma fertilizanteEliminar : listaFertilizanteGuardarAux){
					FertilizanteProcesoPma fertilizanteHistorico = new FertilizanteProcesoPma();
					fertilizanteHistorico = (FertilizanteProcesoPma)SerializationUtils.clone(fertilizanteEliminar);
					fertilizanteHistorico.setId(null);
					fertilizanteHistorico.setFechaHistorico(new Date());
					fertilizanteHistorico.setIdRegistroOriginal(fertilizanteEliminar.getId());
					listaFertilizanteGuardar.add(fertilizanteHistorico);
					
					fertilizanteEliminar.setEstado(false);
					listaFertilizanteGuardar.add(fertilizanteEliminar);
				}
				
				for(FertilizanteProcesoPma fertilizanteGuardar : listaFertilizanteGuardar){
					if(listaFertilizanteBdd != null && !listaFertilizanteBdd.isEmpty())
						fertilizanteGuardar.setFechaHistorico(new Date());
					
					crudServiceBean.saveOrUpdate(fertilizanteGuardar);
				}
			}else{
				crudServiceBean.saveOrUpdate(fertilizantesProceso);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void guardarHerramientaProcesoHistorico(Integer idFichaAmbiental, List<HerramientaProcesoPma> herramientasProceso) {
		try {
			
			List<HerramientaProcesoPma> listaHerramientasBdd = new ArrayList<HerramientaProcesoPma>();
			List<HerramientaProcesoPma> listaHerramientasGuardar = new ArrayList<HerramientaProcesoPma>();
			List<HerramientaProcesoPma> listaHerramientasGuardarAux = new ArrayList<HerramientaProcesoPma>();
			
			listaHerramientasBdd = crudServiceBean.getEntityManager()
					.createQuery("SELECT h FROM HerramientaProcesoPma h where h.fichaAmbientalPma.id=:idFicha and "
							+ "h.estado=true and h.idRegistroOriginal = null")
					.setParameter("idFicha", idFichaAmbiental).getResultList();
			
			if(listaHerramientasBdd != null && !listaHerramientasBdd.isEmpty()){
				listaHerramientasGuardarAux.addAll(listaHerramientasBdd);
				
				for(HerramientaProcesoPma herramienta : herramientasProceso){
					if(herramienta.getId() != null){
						Comparator<HerramientaProcesoPma> c = new Comparator<HerramientaProcesoPma>() {
							
							@Override
							public int compare(HerramientaProcesoPma o1, HerramientaProcesoPma o2) {							
								return o1.getId().compareTo(o2.getId());
							}
						};
						
						Collections.sort(listaHerramientasBdd, c);
						int index = Collections.binarySearch(listaHerramientasBdd, new HerramientaProcesoPma(herramienta.getId()), c);
						
						if(index >= 0){
							HerramientaProcesoPma herramientaBdd = listaHerramientasBdd.get(index);
							listaHerramientasGuardarAux.remove(herramientaBdd);
							
							if(((herramientaBdd.getCantidadHerramientas() == null && herramienta.getCantidadHerramientas() == null) || 
									herramientaBdd.getCantidadHerramientas() != null && herramienta.getCantidadHerramientas() != null && 								
									herramientaBdd.getCantidadHerramientas().equals(herramienta.getCantidadHerramientas())) && 
									((herramientaBdd.getDescripcionOtras() == null && herramienta.getDescripcionOtras() == null) ||
									herramientaBdd.getDescripcionOtras() != null && herramienta.getDescripcionOtras() != null &&								
									herramientaBdd.getDescripcionOtras().equals(herramienta.getDescripcionOtras())) && 
									((herramientaBdd.getHerramienta() == null && herramienta.getHerramienta() == null) ||
									herramientaBdd.getHerramienta() != null && herramienta.getHerramienta() != null && 
									herramientaBdd.getHerramienta().equals(herramienta.getHerramienta()))){
								//son iguales
							}else{
								HerramientaProcesoPma herramientaHistorico = new HerramientaProcesoPma();
								herramientaHistorico = (HerramientaProcesoPma)SerializationUtils.clone(herramientaBdd);
								herramientaHistorico.setId(null);
								herramientaHistorico.setFechaHistorico(new Date());
								herramientaHistorico.setIdRegistroOriginal(herramientaBdd.getId());
								listaHerramientasGuardar.add(herramientaHistorico);
								
								listaHerramientasGuardar.add(herramienta);							
							}					
						}					
					}else{
						herramienta.setFechaHistorico(new Date());
						listaHerramientasGuardar.add(herramienta);
					}
				}
				
				for(HerramientaProcesoPma herramientaEliminada : listaHerramientasGuardarAux){
					HerramientaProcesoPma herramientaHistorico = new HerramientaProcesoPma();
					herramientaHistorico = (HerramientaProcesoPma)SerializationUtils.clone(herramientaEliminada);
					herramientaHistorico.setId(null);
					herramientaHistorico.setFechaHistorico(new Date());
					herramientaHistorico.setIdRegistroOriginal(herramientaEliminada.getId());
					listaHerramientasGuardar.add(herramientaHistorico);
					
					herramientaEliminada.setEstado(false);
					listaHerramientasGuardar.add(herramientaEliminada);
				}
				
				for(HerramientaProcesoPma herramientaGuardar : listaHerramientasGuardar){
					if(listaHerramientasBdd != null && !listaHerramientasBdd.isEmpty())
						herramientaGuardar.setFechaHistorico(new Date());				
					
					crudServiceBean.saveOrUpdate(herramientaGuardar);
				}
			}else{
				crudServiceBean.saveOrUpdate(herramientasProceso);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void guardarInsumoProcesoHistorico(Integer idFichaAmbiental, List<InsumoProcesoPma> insumosProceso) {
		
		try {
			List<InsumoProcesoPma> listaInsumosBdd = new ArrayList<InsumoProcesoPma>();
			List<InsumoProcesoPma> listaInsumosGuardar = new ArrayList<InsumoProcesoPma>();
			List<InsumoProcesoPma> listaInsumosGuardarAux = new ArrayList<InsumoProcesoPma>();
			
			listaInsumosBdd = crudServiceBean.getEntityManager().createQuery(
					"SELECT f from InsumoProcesoPma f where f.fichaAmbientalPma.id=:idFicha "
					+ "and f.estado=true and f.idRegistroOriginal = null").setParameter("idFicha", idFichaAmbiental).getResultList();
			
			if(listaInsumosBdd != null && !listaInsumosBdd.isEmpty()){
				listaInsumosGuardarAux.addAll(listaInsumosBdd);
				
				for(InsumoProcesoPma insumo: insumosProceso){
					if(insumo.getId() != null){
						
						Comparator<InsumoProcesoPma> c = new Comparator<InsumoProcesoPma>() {
							
							@Override
							public int compare(InsumoProcesoPma o1, InsumoProcesoPma o2) {
								return o1.getId().compareTo(o2.getId());
							}
						};
						
						Collections.sort(listaInsumosBdd, c);
						
						int index = Collections.binarySearch(listaInsumosBdd, new InsumoProcesoPma(insumo.getId()), c);
						
						if(index >= 0){
							InsumoProcesoPma insumoBdd = listaInsumosBdd.get(index);
							listaInsumosGuardarAux.remove(insumoBdd);
							
							if(((insumoBdd.getCantidad() == null && insumo.getCantidad() == null) || 
									insumoBdd.getCantidad() != null && insumo.getCantidad() != null &&
									insumoBdd.getCantidad().equals(insumo.getCantidad())) && 
									((insumoBdd.getCatalogoInsumo() == null && insumo.getCatalogoInsumo() == null) || 
									insumoBdd.getCatalogoInsumo() != null && insumo.getCatalogoInsumo() != null &&
									insumoBdd.getCatalogoInsumo().equals(insumo.getCatalogoInsumo())) && 
									((insumoBdd.getDescripcionOtros() == null && insumo.getDescripcionOtros() == null) ||
									insumoBdd.getDescripcionOtros() != null && insumo.getDescripcionOtros() != null && 
									insumoBdd.getDescripcionOtros().equals(insumo.getDescripcionOtros())) && 
									((insumoBdd.getUnidadMedida() == null && insumo.getUnidadMedida() == null) ||
									insumoBdd.getUnidadMedida() != null && insumo.getUnidadMedida() != null &&
									insumoBdd.getUnidadMedida().equals(insumo.getUnidadMedida()))){
								//son iguales
							}else{
								InsumoProcesoPma insumoHistorico = new InsumoProcesoPma();
								insumoHistorico = (InsumoProcesoPma)SerializationUtils.clone(insumoBdd);
								insumoHistorico.setId(null);
								insumoHistorico.setFechaHistorico(new Date());
								insumoHistorico.setIdRegistroOriginal(insumoBdd.getId());
								listaInsumosGuardar.add(insumoHistorico);
								
								listaInsumosGuardar.add(insumo);
							}
						}					
					}else{
						insumo.setFechaHistorico(new Date());
						listaInsumosGuardar.add(insumo);
					}
				}
				
				for(InsumoProcesoPma insumoEliminado : listaInsumosGuardarAux){
					InsumoProcesoPma insumoHistorico = new InsumoProcesoPma();
					insumoHistorico = (InsumoProcesoPma)SerializationUtils.clone(insumoEliminado);
					insumoHistorico.setId(null);
					insumoHistorico.setFechaHistorico(new Date());
					insumoHistorico.setIdRegistroOriginal(insumoEliminado.getId());
					listaInsumosGuardar.add(insumoHistorico);
					
					insumoEliminado.setEstado(false);
					listaInsumosGuardar.add(insumoEliminado);
				}
				
				for(InsumoProcesoPma insumoGuardar : listaInsumosGuardar){
					if(listaInsumosBdd != null && !listaInsumosBdd.isEmpty())
						insumoGuardar.setFechaHistorico(new Date());
					
					crudServiceBean.saveOrUpdate(insumoGuardar);				
				}	
			}else{
				crudServiceBean.saveOrUpdate(insumosProceso);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void guardarActividadesProcesoHistorico(Integer idFichaAmbiental, List<ActividadProcesoPma> actividadesProceso) {
		try {
			List<ActividadProcesoPma> listaActividadesBdd = new ArrayList<ActividadProcesoPma>();
			List<ActividadProcesoPma> listaActividadesGuardar = new ArrayList<ActividadProcesoPma>();
			List<ActividadProcesoPma> listaActividadesGuardarAux = new ArrayList<ActividadProcesoPma>();			

			listaActividadesBdd = crudServiceBean.getEntityManager().createQuery(
					"SELECT a from ActividadProcesoPma a where a.fichaAmbientalPma.id=:idFicha "
					+ "and a.estado=true and a.idRegistroOriginal = null").setParameter("idFicha", idFichaAmbiental).getResultList();
			
			if(listaActividadesBdd != null && !listaActividadesBdd.isEmpty()){
				listaActividadesGuardarAux.addAll(listaActividadesBdd);
				
				for(ActividadProcesoPma actividad : actividadesProceso){
					if(actividad.getId() != null){
						Comparator<ActividadProcesoPma> c = new Comparator<ActividadProcesoPma>() {
							
							@Override
							public int compare(ActividadProcesoPma o1, ActividadProcesoPma o2) {							
								return o1.getId().compareTo(o2.getId());
							}
						};
						
						
						Collections.sort(listaActividadesBdd, c);
						
						int index = Collections.binarySearch(listaActividadesBdd, new ActividadProcesoPma(actividad.getId()), c);
						
						if(index >= 0){
							ActividadProcesoPma actividadBdd = listaActividadesBdd.get(index);
							listaActividadesGuardarAux.remove(actividadBdd);
							
							if(((actividadBdd.getActividadComercial() == null && actividad.getActividadComercial() == null) ||
									actividadBdd.getActividadComercial() != null && actividad.getActividadComercial() != null && 
									actividadBdd.getActividadComercial().equals(actividad.getActividadComercial())) && 
									((actividadBdd.getDescripcion() == null && actividad.getDescripcion() == null) || 
									actividadBdd.getDescripcion() != null && actividad.getDescripcion() != null && 
									actividadBdd.getDescripcion().equals(actividad.getDescripcion())) && 
									((actividadBdd.getDescripcionFaseOtros() == null && actividad.getDescripcionFaseOtros() == null) || 
											actividadBdd.getDescripcionFaseOtros() != null && actividad.getDescripcionFaseOtros() != null && 
											actividadBdd.getDescripcionFaseOtros().equals(actividad.getDescripcionFaseOtros())) && 
									((actividadBdd.getDescripcionOtros() == null && actividad.getDescripcionOtros() == null) || 
											actividadBdd.getDescripcionOtros() != null && actividad.getDescripcionOtros() != null && 
											actividadBdd.getDescripcionOtros().equals(actividad.getDescripcionOtros())) && 
									((actividadBdd.getFechaFin() == null && actividad.getFechaFin() == null) || 
											actividadBdd.getFechaFin() != null && actividad.getFechaFin() != null && 
											actividadBdd.getFechaFin().equals(actividad.getFechaFin())) && 
									((actividadBdd.getFechaInicio() == null && actividadBdd.getFechaInicio() == null) || 
											actividadBdd.getFechaInicio() != null && actividad.getFechaInicio() != null && 
											actividadBdd.getFechaInicio().equals(actividad.getFechaInicio())) 
									){
								//iguales
							}else{
								ActividadProcesoPma actividadHistorico = new ActividadProcesoPma();
								actividadHistorico = (ActividadProcesoPma)SerializationUtils.clone(actividadBdd);
								actividadHistorico.setId(null);
								actividadHistorico.setFechaHistorico(new Date());
								actividadHistorico.setIdRegistroOriginal(actividadBdd.getId());
								listaActividadesGuardar.add(actividadHistorico);
								
								listaActividadesGuardar.add(actividad);
							}
						}
						
					}else{
						actividad.setFechaHistorico(new Date());
						listaActividadesGuardar.add(actividad);
					}
				}
				
				for(ActividadProcesoPma actividadEliminada : listaActividadesGuardarAux){
					ActividadProcesoPma actividadHistorico = new ActividadProcesoPma();
					actividadHistorico = (ActividadProcesoPma)SerializationUtils.clone(actividadEliminada);
					actividadHistorico.setId(null);
					actividadHistorico.setFechaHistorico(new Date());
					actividadHistorico.setIdRegistroOriginal(actividadEliminada.getId());
					listaActividadesGuardar.add(actividadHistorico);
					
					actividadEliminada.setEstado(false);
					listaActividadesGuardar.add(actividadEliminada);
				}
				
				for(ActividadProcesoPma actividadGuardar : listaActividadesGuardar){
					if(actividadGuardar.getId() == null)
						actividadGuardar.setFechaHistorico(new Date());
					
					crudServiceBean.saveOrUpdate(actividadGuardar);
				}
			}else{
				crudServiceBean.saveOrUpdate(actividadesProceso);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	//CF: Recuperacion de Historico
	@SuppressWarnings("unchecked")
	public List<ActividadProcesoPma> getActividadesProcesosFichaHistoricoPorIdFicha(Integer idFicha) {
		try {
			List<ActividadProcesoPma> listaActividades = crudServiceBean.getEntityManager().createQuery(
					"SELECT a from ActividadProcesoPma a where a.fichaAmbientalPma.id=:idFicha "
					+ "and a.estado=true and a.idRegistroOriginal != null").setParameter("idFicha", idFicha).getResultList();
			
			if(listaActividades != null && !listaActividades.isEmpty()){
				return listaActividades;
			}			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<HerramientaProcesoPma> getHerramientasProcesosFichaHistoricoPorIdFicha(
			Integer idFicha) {
		List<HerramientaProcesoPma> herramientasProcesos = new ArrayList<HerramientaProcesoPma>();
		herramientasProcesos = (List<HerramientaProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From HerramientaProcesoPma h where h.fichaAmbientalPma.id =:idFicha and h.estado=true "
						+ "and h.idRegistroOriginal != null")
				.setParameter("idFicha", idFicha).getResultList();

		return herramientasProcesos;
	}
	
	@SuppressWarnings("unchecked")
	public List<InsumoProcesoPma> getInsumosProcesosFichaHistoricoPorIdFicha(
			Integer idFicha) {
		List<InsumoProcesoPma> insumosProcesos = new ArrayList<InsumoProcesoPma>();
		insumosProcesos = (List<InsumoProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From InsumoProcesoPma i where i.fichaAmbientalPma.id =:idFicha and i.estado=true and i.idRegistroOriginal != null")
				.setParameter("idFicha", idFicha).getResultList();

		return insumosProcesos;
	}
	
	@SuppressWarnings("unchecked")
	public List<Instalacion> getInstalacionesProcesosFichaHistoricoPorIdFicha(
			Integer idFicha) {
		List<Instalacion> instalacionesProcesos = new ArrayList<Instalacion>();
		instalacionesProcesos = (List<Instalacion>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From Instalacion i where i.fichaAmbientalPma.id =:idFicha and i.estado=true and i.idRegistroOriginal != null")
				.setParameter("idFicha", idFicha).getResultList();

		return instalacionesProcesos;
	}
	
	@SuppressWarnings("unchecked")
	public List<TecnicaProcesoPma> getTecnicasProcesosPorIdFichaHistorico(Integer idFicha) {
		List<TecnicaProcesoPma> tecnicasProcesos = new ArrayList<TecnicaProcesoPma>();
		tecnicasProcesos = (List<TecnicaProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From TecnicaProcesoPma t where t.fichaAmbientalPma.id =:idFicha and t.estado=true and t.idRegistroOriginal != null")
				.setParameter("idFicha", idFicha).getResultList();

		return tecnicasProcesos;
	}
	
	@SuppressWarnings("unchecked")
	public List<PlaguicidaProcesoPma> getPlagicidasProcesosFichaHistoricoPorIdFicha(
			Integer idFicha) {
		List<PlaguicidaProcesoPma> plaguicidasProcesos = new ArrayList<PlaguicidaProcesoPma>();
		plaguicidasProcesos = (List<PlaguicidaProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From PlaguicidaProcesoPma p where p.fichaAmbientalPma.id =:idFicha and p.estado=true and p.idRegistroOriginal != null")
				.setParameter("idFicha", idFicha).getResultList();

		return plaguicidasProcesos;
	}
	
	@SuppressWarnings("unchecked")
	public List<FertilizanteProcesoPma> getFertilizantesProcesoHistoricoPorIdFicha(
			Integer idFicha) {
		List<FertilizanteProcesoPma> fertilizantesProcesos;
		fertilizantesProcesos = (List<FertilizanteProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From FertilizanteProcesoPma f where f.fichaAmbientalPma.id =:idFicha and f.estado=true and f.idRegistroOriginal != null")
				.setParameter("idFicha", idFicha).getResultList();

		return fertilizantesProcesos;
	}
	
	@SuppressWarnings("unchecked")
	public List<DesechoSanitarioProcesoPma> getDesechoSanitarioProcesoHistoricoPorIdFicha(Integer idFicha) {
		List<DesechoSanitarioProcesoPma> desechoProcesos = new ArrayList<DesechoSanitarioProcesoPma>();
		desechoProcesos = (List<DesechoSanitarioProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From DesechoSanitarioProcesoPma d where d.fichaAmbientalPma.id =:idFicha and d.estado=true and d.idRegistroOriginal != null")
				.setParameter("idFicha", idFicha).getResultList();

		return desechoProcesos;
	}
	
	@SuppressWarnings("unchecked")
	public List<DesechoPeligrosoProcesoPma> getDesechosPeligrososProcesosHistoricoPorIdFicha(Integer idFicha, Boolean esExpost) {
		List<DesechoPeligrosoProcesoPma> desechosProcesos = new ArrayList<DesechoPeligrosoProcesoPma>();

		if (esExpost) {
			desechosProcesos = (List<DesechoPeligrosoProcesoPma>) crudServiceBean
					.getEntityManager()
					.createQuery(
							"From DesechoPeligrosoProcesoPma d where d.fichaAmbientalPma.id =:idFicha and d.vehiculoDescripcionExantes=null and d.estado=true and d.idRegistroOriginal != null")
					.setParameter("idFicha", idFicha).getResultList();
		} else {
			desechosProcesos = (List<DesechoPeligrosoProcesoPma>) crudServiceBean
					.getEntityManager()
					.createQuery(
							"From DesechoPeligrosoProcesoPma d where d.fichaAmbientalPma.id =:idFicha and d.vehiculoDescripcionExantes is not null and d.estado=true and d.idRegistroOriginal != null")
					.setParameter("idFicha", idFicha).getResultList();
		}

		return desechosProcesos;
	}
	
	@SuppressWarnings("unchecked")
	public List<VehiculoDesechoSanitarioProcesoPma> getVehiculosProcesosHistoricoPorIdFicha(
			Integer idFicha) {
		List<VehiculoDesechoSanitarioProcesoPma> vehiculoProcesos = new ArrayList<VehiculoDesechoSanitarioProcesoPma>();
		vehiculoProcesos = (List<VehiculoDesechoSanitarioProcesoPma>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From VehiculoDesechoSanitarioProcesoPma v where v.fichaAmbientalPma.id =:idFicha and v.estado=true and v.idRegistroOriginal != null")
				.setParameter("idFicha", idFicha).getResultList();

		return vehiculoProcesos;
	}

	
	/**
	 * FIN Cris F. getDetalleFichaPorIdFichaPorTipoPorCodigo
	 */ 
	//MarielaG cambios para manejo historial
	public void guardarMatrizFactorImpacto(List<MatrizFactorImpacto> listaMatriz) throws Exception{
        impactoAmbientalPmaServiceBean.guardarMatrizFactorImpacto(listaMatriz);
    }
    
    public void guardarMatrizFactorOtroImpacto(List<MatrizFactorImpactoOtros> listaMatriz) throws Exception{
        impactoAmbientalPmaServiceBean.guardarMatrizFactorOtroImpacto(listaMatriz);
    }
    
    public List<MatrizFactorImpactoOtros> listarTodoMatrizOtrosImpactos(final Integer idFicha) throws ServiceException{
        List<MatrizFactorImpactoOtros> result = new ArrayList<MatrizFactorImpactoOtros>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("idFicha", idFicha);
        List<MatrizFactorImpactoOtros> lista = (List<MatrizFactorImpactoOtros>) crudServiceBean.findByNamedQuery(MatrizFactorImpactoOtros.LISTAR_TODO_POR_FICHA_ID, params);
        if (lista != null && !lista.isEmpty()) {
        	result = lista;
        }
        return result;
    }
    
    //CF: para buscar proyectos por proponente
    public List<EntityFichaCompletaRgd> getFinalizadosPorProponente(String cedulaProponente) {
   		try {
   			Map<String, Object> parametros = new HashMap<String, Object>();
   			String like = "";
   			like = " LOWER(cedula_proponente) like LOWER(:cedulaProponente) and cod_proyecto not like  '%MAAE%' ";
   			parametros.put("cedulaProponente", "%" + cedulaProponente + "%");
   			
   			StringBuilder sb = new StringBuilder();
   			sb.append("select * from ( ");
   			//sb.append("select CAST(id_proyecto  AS varchar(255)), area_abreviatura, cod_proyecto, nombre_proyecto, fecha_registro_proyecto, sector, '2' AS sistema, 'certificado' AS tipo from vw_certificados_finalizados where "+like+"  union("
			sb.append("  select CAST(id_proyecto  AS varchar(255)), area_abreviatura, cod_proyecto, nombre_proyecto, fecha_registro_proyecto, sector, '2' AS sistema, 'registro' AS tipo from vw_licencias_finalizadas where "+like+"  union"
   					+ " (select CAST(id_proyecto  AS varchar(255)), area_abreviatura, cod_proyecto, nombre_proyecto, fecha_registro_proyecto, sector, '2' AS sistema, 'licencia' AS tipo from vw_registros_finalizados where "+like+" )")
   					.append(" ) as proyectos ")
   					.append(" where id_proyecto not in ("
   							+ "	select cast(enaa_id_proyecto as varchar)"
   							+ " from coa_digitalization_linkage.environmental_administrative_authorizations "
   							+ "	where enaa_status is true and enaa_id_proyecto is not null and enaa_history is false "
   							+ "   )")
   					.append(" ORDER BY cod_proyecto DESC ");
   			return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityFichaCompletaRgd.class, parametros);

   		} catch (Exception e) {
   			e.printStackTrace();
   			return null;
   		}
   	}    
    
    public List<EntityFichaCompletaRgd> getFinalizadosRgd(String codigoProyecto) {
   		try {
   			Map<String, Object> parametros = new HashMap<String, Object>();
   			String like = "";
   			like = " LOWER(cod_proyecto) like LOWER(:codigoProyecto)";
   			parametros.put("codigoProyecto", "%" + codigoProyecto + "%");
   			
   			StringBuilder sb = new StringBuilder();
   			sb.append("select CAST(id_proyecto  AS varchar(255)), area_abreviatura, cod_proyecto, nombre_proyecto, fecha_registro_proyecto, sector from vw_certificados_finalizados where "+like+"  union("
   					+ " select CAST(id_proyecto  AS varchar(255)), area_abreviatura, cod_proyecto, nombre_proyecto, fecha_registro_proyecto, sector from vw_licencias_finalizadas where "+like+"  union"
   					+ " (select CAST(id_proyecto  AS varchar(255)), area_abreviatura, cod_proyecto, nombre_proyecto, fecha_registro_proyecto, sector from vw_registros_finalizados where "+like+" ))")					
   					.append(" ORDER BY cod_proyecto DESC ");
   			return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityFichaCompletaRgd.class, parametros);

   		} catch (Exception e) {
   			e.printStackTrace();
   			return null;
   		}
   	}
    
    
    
}
