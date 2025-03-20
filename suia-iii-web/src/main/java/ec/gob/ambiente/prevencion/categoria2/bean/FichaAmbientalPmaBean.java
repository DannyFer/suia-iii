package ec.gob.ambiente.prevencion.categoria2.bean;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogocategorias.facade.CatalogoCategoriasFacade;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FaseFichaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author karla.carvajal
 */
@ManagedBean
@ViewScoped
public class FichaAmbientalPmaBean implements Serializable {

	/**
     *
     */
	private static final long serialVersionUID = 5393512542921376619L;

	Logger LOG = Logger.getLogger(FichaAmbientalPmaBean.class);

	@EJB
	private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@Setter
	@ManagedProperty(value = "#{catalogoGeneralPmaBean}")
	private CatalogoGeneralPmaBean catalogoGeneralPmaBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;
	@EJB
	private CatalogoCategoriasFacade catalogoCategoriasFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private FaseFichaAmbientalFacade faseFichaAmbientalFacade;

	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;

	@Getter
	@Setter
	private FichaAmbientalPma ficha;

	@Setter
	private String filter;

	@Getter
	@Setter
	private List<Coordenada> coordenadas;

	@Getter
	@Setter
	private boolean habilitarBtnGuardarParte1;

	@Getter
	@Setter
	private UbicacionesGeografica ubicacionProyecto;

	@Getter
	@Setter
	private List<UbicacionesGeografica> listaUbicacionProyecto,
			listaUbicacionProyectoConcesiones;

	private CatalogoCategoriaFase actividadDeseleccionada;

	@Getter
	@Setter
	private List<CatalogoCategoriaFase> listaActividadesDeseleccionadas;

	@Getter
	@Setter
	private String direccion;
	@Getter
	@Setter
	private String correo;
	@Getter
	@Setter
	private String mensaje;
	@Getter
	@Setter
	private String telefono;
	@Getter
	@Setter
	private boolean mostrarOtrosPredio;
	@Getter
	@Setter
	private boolean mostrarOtrosInfraestructura;

	@Setter
	@Getter
	private FichaAmbientalMineria fichaAmbientalMineria;

	@EJB
	private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	/**
	 * Cris F: Variables para historial
	 */
	@Getter
	@Setter
	private List<CatalogoCategoriaFase> actividadesHistorico = new ArrayList<CatalogoCategoriaFase>();
	
	@Getter
	@Setter
	private List<CatalogoCategoriaFase> listaHistoricosCategoria;
	
	@Getter
	@Setter
	private boolean guardarHistorial = false;
	
	List<FasesFichaAmbiental> listaNuevosElementos;
	@Getter
	List<FasesFichaAmbiental> listaHistorial;
	@Getter
	private CatalogoGeneral tiposInfraestructurasSeleccionadosHistorico;

	@Getter
	private List<FichaAmbientalPma> fichaHistoricoDescripcionZonaList, fichaAreaImplantacionList, fichaAguaPotableList,
			fichaEnergiaElectricaList, fichaAccesoVehicularList, fichaAlcantarilladoList, fichaHistoricoOtrosInfraestructura;

	@Getter
	private List<DetalleFichaPma> listaDetalleFichaHistorico, listaDetalleFichaHisPredio, listaDetalleFichaHisOtroPredio;

	@Getter
	@Setter
	private List<FasesFichaAmbiental> listaFasesHistorico;
	
	//fin de variables para historial

	@PostConstruct
	public void init() {
        try {
            if (proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getId() != null) {

                this.proyecto = proyectosBean.getProyecto();

                this.fichaAmbientalMineria =fichaAmbientalMineriaFacade.obtenerPorProyecto(proyecto);

                if (listaActividadesDeseleccionadas == null) {
                    listaActividadesDeseleccionadas = new ArrayList<CatalogoCategoriaFase>();
                }

                if (proyecto.getUnidad() == null) {
                    try {
                        List<ConcesionMinera> concesiones = proyectoLicenciaAmbientalFacade.getConcesionesMineraPorIdProyecto(proyecto.getId());
                        Double area = 0.0d;
                        for (ConcesionMinera c : concesiones) {
                            if (c.getUnidad().equals("ha")) {
                                area += c.getArea() * 10000;
                            } else if (c.getUnidad().equals("km2")) {
                                area += c.getArea() * 1000000;
                            } else {
                                area += c.getArea();
                            }
                        }
                        proyecto.setArea(area);
                        proyecto.setUnidad("m2");

                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }
                    proyecto.setUnidad("m2");

                }

                this.ficha = fichaAmbientalPmaFacade.getFichaAmbientalPorIdProyecto(proyectosBean.getProyecto().getId());
                catalogoGeneralPmaBean.init();

                if (ficha == null) {
                    ficha = new FichaAmbientalPma();
                    ficha.setProyectoLicenciamientoAmbiental(proyecto);
                    ficha.setProcessId(bandejaTareasBean.getProcessId());
                    ficha = fichaAmbientalPmaFacade.guardarSoloFicha(ficha);
                }

                if (proyecto.getGeneraDesechos() != null && proyecto.getGeneraDesechos()) {
                    proyecto.getProyectoDesechoPeligrosos();
                } else {
                    proyecto.setProyectoDesechoPeligrosos(new ArrayList<ProyectoDesechoPeligroso>());
                }
                this.coordenadas = fichaAmbientalPmaFacade.getCoordenadasProyectoPma(this.proyecto.getId());
                this.ubicacionProyecto = proyectoLicenciaAmbientalFacade
                        .getUbicacionProyectoPorIdProyecto(this.proyecto
                                .getId());

                catalogoGeneralPmaBean
                        .setTiposActividades(faseFichaAmbientalFacade
                                .obtenerCatalogoCategoriaFasesPorSubsectorCodigo(proyecto
                                        .getCatalogoCategoria()
                                        .getTipoSubsector().getCodigo()));

                catalogoGeneralPmaBean
                        .setActividadesSeleccionadas(faseFichaAmbientalFacade
                                .obtenerCatalogoCategoriaFasesPorFicha(ficha
                                        .getId()));             

                if (proyecto.getCatalogoCategoria().getTipoSubsector()
                        .getCodigo() != null
                        && proyecto.getCatalogoCategoria().getTipoSubsector()
                        .getCodigo()
                        .equals(TipoSubsector.CODIGO_OTROS_SECTORES)) {
                    catalogoGeneralPmaBean
                            .setTiposInfraestructura(catalogoGeneralPmaBean
                                    .llenarCatalogos(TipoCatalogo.TIPO_INFRAESTRUCTURA));

                    List<CatalogoGeneral> prediosRemoved = new ArrayList<CatalogoGeneral>();

                    for (CatalogoGeneral predio : catalogoGeneralPmaBean
                            .getTiposInfraestructura()) {
                        if (predio.getCodigo() == null) {
                            prediosRemoved.add(predio);
                        } else if (predio.getCodigo() != null
                                && !predio.getCodigo().equals(
                                TipoSubsector.CODIGO_OTROS_SECTORES)) {
                            prediosRemoved.add(predio);
                        }
                    }

                    for (CatalogoGeneral predioRemoved : prediosRemoved) {
                        catalogoGeneralPmaBean.getTiposInfraestructura()
                                .remove(predioRemoved);
                    }
                } else {
                    catalogoGeneralPmaBean
                            .setTiposInfraestructura(catalogoGeneralPmaBean
                                    .llenarCatalogos(TipoCatalogo.TIPO_INFRAESTRUCTURA));
                }

                List<CatalogoCategoriaFase> listaActividades = new ArrayList<CatalogoCategoriaFase>();
                for (CatalogoCategoriaFase actividad : catalogoGeneralPmaBean
                        .getTiposActividades()) {
                    if (catalogoGeneralPmaBean.getActividadesSeleccionadas()
                            .contains(actividad)) {
                        actividad.setSeleccionado(true);
                    }
                    listaActividades.add(actividad);
                }
                catalogoGeneralPmaBean.setTiposActividades(listaActividades);

                List<CatalogoGeneral> listaInfraestructura = llenarCatalogosSeleccionadosFicha(TipoCatalogo.TIPO_INFRAESTRUCTURA);
                if (listaInfraestructura != null
                        && !listaInfraestructura.isEmpty()) {
                    catalogoGeneralPmaBean
                            .setTiposInfraestructurasSeleccionados(listaInfraestructura
                                    .get(0));
                }

                catalogoGeneralPmaBean
                        .setTiposPredioSeleccionados(fichaAmbientalPmaFacade
                                .getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigo(
                                        ficha.getId(),
                                        TipoCatalogo.SITUACION_PREDIO,
                                        TipoCatalogo.CODIGO_PREDIO_PRIMARIO));
                catalogoGeneralPmaBean
                        .setTiposPedioSecundariosSeleccionados(fichaAmbientalPmaFacade
                                .getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigo(
                                        ficha.getId(),
                                        TipoCatalogo.SITUACION_PREDIO,
                                        TipoCatalogo.CODIGO_PREDIO_SECUDARIO));
                this.validarOtrosInfraestructura();
                this.validarOtrosPredio();

                mensaje = getNotaResponsabilidadInformacionRegistroProyecto(loginBean
                        .getUsuario());
                
                
                List<Contacto> listaContactos=null;
                Organizacion organiza=null;
                if(proyectosBean.getProyecto().getUsuario().getPersona().getOrganizaciones().size()>0)
                {
                	for(Organizacion organi: proyectosBean.getProyecto().getUsuario().getPersona().getOrganizaciones())
                	{
                		if(proyectosBean.getProyecto().getUsuario().getNombre().equals(organi.getRuc()))
                		{
                			organiza=organi;
                		}
                	}
                }
                if(organiza!=null)
                {
                	listaContactos = contactoFacade.buscarPorOrganizacion(organiza);                
                }
                else{
                	listaContactos = contactoFacade
                        .buscarUsuarioNativeQuery(loginBean.getUsuario()
                                .getNombre());
                }

                for (Contacto c : listaContactos) {
                    if (c.getFormasContacto().getId().intValue() == FormasContacto.EMAIL) {
                        correo = c.getValor();
                        continue;
                    } else if (c.getFormasContacto().getId().intValue() == FormasContacto.DIRECCION) {
                        direccion = c.getValor();
                        continue;
                    } else if (c.getFormasContacto().getId().intValue() == FormasContacto.TELEFONO) {
                        telefono = c.getValor();
                        continue;
                    }
                }

//                if (this.ubicacionProyecto == null) {
                    listaUbicacionProyecto = ubicacionGeograficaFacade
                            .listarPorProyecto(getProyecto());
                    listaUbicacionProyectoConcesiones = ubicacionGeograficaFacade
                            .listarPorProyectoConConcesionesMineras(getProyecto());
//                } else {
//                    listaUbicacionProyecto = new ArrayList<UbicacionesGeografica>();
//                    listaUbicacionProyecto.add(ubicacionProyecto);
//                }
                    
                    /**
    				 * Cris F:
    				 * Datos Originales del registro ambiental
    				 */
    				consultarDatosOriginales(ficha.getId(), catalogoGeneralPmaBean.getActividadesSeleccionadas());
                    
            } else {
                JsfUtil.addMessageError("No se logró cargar los datos iniciales.");
            }
        } catch (Exception e) {
            LOG.error("ERROR", e);
            JsfUtil.addMessageError("No se logró cargar los datos iniciales.");
        }
    }


	public void validarOtrosPredio() {
		mostrarOtrosPredio = false;
		for (CatalogoGeneral catalogo : catalogoGeneralPmaBean
				.getTiposPredioSeleccionados()) {
			if (catalogo.getDescripcion().equals("Otros")) {
				mostrarOtrosPredio = true;
			}
		}
	}

	private String getNotaResponsabilidadInformacionRegistroProyecto(
			Usuario persona) {
		String[] parametros = { persona.getPersona().getNombre(),
				persona.getPin() };
		return DocumentoPDFPlantillaHtml.getPlantillaConParametros(
				"nota_responsabilidad_certificado_interseccion", parametros);
	}

	private List<CatalogoGeneral> llenarCatalogosSeleccionadosFicha(Integer tipo) {
		try {
			return fichaAmbientalPmaFacade.getCatalogoGeneralFichaPorIdFichaPorTipo(ficha.getId(),tipo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void validarOtrosInfraestructura() {
		mostrarOtrosInfraestructura = false;
		if (catalogoGeneralPmaBean.getTiposInfraestructurasSeleccionados() != null
				&& catalogoGeneralPmaBean
						.getTiposInfraestructurasSeleccionados()
						.getDescripcion().equals("Otros")) {
			mostrarOtrosInfraestructura = true;
		}else
			ficha.setOtrosInfraestructura(null);
		
	}

	public void enviarFicha() {
		try {
			if (validarFichaCompletada()) {
				// Falta validar para enviar
				taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
						bandejaTareasBean.getTarea().getTaskId(),
						bandejaTareasBean.getTarea().getProcessInstanceId(),
						null, Constantes.getDeploymentId(),
						loginBean.getPassword(),
						Constantes.getRemoteApiTimeout(),
						Constantes.getUrlBusinessCentral());
				JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
				JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
			} else {
				JsfUtil.addMessageError("Debe completar los datos del Registro Ambiental.");
			}
		} catch (Exception e) {

			LOG.error("ERROR", e);
		}
	}

	public Boolean validarFichaCompletada() {
		CatalogoCategoriaSistema catalogo = proyecto.getCatalogoCategoria();
		if (catalogo.getCodigo().equals(Constantes.SECTOR_HIDROCARBURO_CODIGO)) {
			ficha.setValidarDescripcionAreaImplantacion(true);
		}
		try {
			return ficha.getValidarDatosGenerales() != null
					&& ficha.getValidarMarcoLegalReferencial() != null
					&& ficha.getValidarDescripcionProyectoObraActividad() != null
					&& ficha.getValidarDescripcionProceso() != null
					&& ficha.getValidarDescripcionAreaImplantacion() != null
					&& ficha.getValidarPrincipalesImpactosAmbientales() != null
					&& ficha.getValidarPlanManejoAmbiental() != null
					&& ficha.getValidarCronogramaConstruccionOperacionProyecto() != null
					&& ficha.getValidarCronogramaValoradoPlanManejoAmbiental() != null
					&& ficha.getValidarDatosGenerales()
					&& ficha.getValidarMarcoLegalReferencial()
					&& ficha.getValidarDescripcionProyectoObraActividad()
					&& ficha.getValidarDescripcionProceso()
					&& ficha.getValidarDescripcionAreaImplantacion()
					&& ficha.getValidarPrincipalesImpactosAmbientales()
					&& ficha.getValidarPlanManejoAmbiental()
					&& ficha.getValidarCronogramaConstruccionOperacionProyecto()
					&& ficha.getValidarCronogramaValoradoPlanManejoAmbiental();
		} catch (Exception e) {
			LOG.error("ERROR", e);
			return false;
		}

		// return false;
	}

	public void reset() {
		filter = null;
		init();
	}

	public void limpiar() {
		this.proyecto = new ProyectoLicenciamientoAmbiental();
		this.ficha = new FichaAmbientalPma();
		coordenadas = new ArrayList<Coordenada>();
		ubicacionProyecto = new UbicacionesGeografica();
	}

	//
	// private List<CatalogoGeneral> llenarCatalogosPorCodigo(Integer tipo,
	// String codigo) {
	// try {
	// return catalogoGeneralFacade.obtenerCatalogoXTipoXCodigo(tipo,
	// codigo);
	// } catch (Exception e) {
	//
	// LOG.error("ERROR", e);
	// return null;
	// }
	// }

	public boolean actulizarConsumo() {
		if (ficha != null && !ficha.getAccesoVehicular()) {
			ficha.setTipoVia(null);
		} else if (ficha != null && !ficha.getAguaPotable()) {
			ficha.setConsumoAgua(null);
		} else if (ficha != null && !ficha.getEnergiaElectrica()) {
			ficha.setConsumoElectrico(null);
		}
		return true;
	}

	public StreamedContent descargarCertificado() {
		InputStream is = null;
		try {
			byte[] certificado = documentosFacade
					.descargarDocumentoAlfrescoQueryDocumentos(
							ProyectoLicenciamientoAmbiental.class
									.getSimpleName(),
							proyecto.getId(),
							TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA);
			if (certificado != null) {
				is = new ByteArrayInputStream(certificado);
				return new DefaultStreamedContent(is, "pdf",

				"mapaCertificado.pdf");
			} else {
				JsfUtil.addMessageError("No se logró recuperar el certificado de intersección.");
				return null;
			}
		} catch (Exception e) {
			LOG.error("ERROR", e);
			JsfUtil.addMessageError("No se logró recuperar el certificado de intersección.");
			return null;
		}
	}

	public void validarFaseDeseleccionada(CatalogoCategoriaFase actividad) {
		if (!actividad.isSeleccionado()
				&& catalogoGeneralPmaBean.getActividadesSeleccionadas()
						.contains(actividad)) {
			actividadDeseleccionada = actividad;
			openDialogEliminarPorFase();
		} else if (actividad.isSeleccionado()
				&& catalogoGeneralPmaBean.getActividadesSeleccionadas()
						.contains(actividad)) {
			if (listaActividadesDeseleccionadas.contains(actividad)) {
				listaActividadesDeseleccionadas.remove(actividad);
			}
		}
	}

	private void openDialogEliminarPorFase() {
		Map<String, Object> opciones = new HashMap<String, Object>();
		opciones.put("modal", true);
		opciones.put("draggable", false);
		opciones.put("resizable", true);
		opciones.put("contentWidth", 600);
		opciones.put("contentHeight", 400);

		RequestContext.getCurrentInstance().execute(
				"PF('dg_eliminar_por_fase').show()");
	}

	public void eliminarAsociadoFase() {
		try {
			if (!listaActividadesDeseleccionadas
					.contains(actividadDeseleccionada)) {
				listaActividadesDeseleccionadas.add(actividadDeseleccionada);
			}

			actividadDeseleccionada = null;
			RequestContext.getCurrentInstance().addCallbackParam("dgClose",
					true);
		} catch (Exception e) {
			LOG.error("ERROR", e);
		}
	}

	public void cancelarAsociadoFase() {
		try {
			int index = catalogoGeneralPmaBean.getTiposActividades().indexOf(
					actividadDeseleccionada);
			catalogoGeneralPmaBean.getTiposActividades().get(index)
					.setSeleccionado(true);
			actividadDeseleccionada = null;
			RequestContext.getCurrentInstance().addCallbackParam("dgClose",
					true);
		} catch (Exception e) {
			LOG.error("ERROR", e);
		}
	}

	/**
	 * Cris F: Métodos para obtener los historicos de los registros guardados.
	 */
	

	public void consultarDatosOriginales(Integer idFicha, List<CatalogoCategoriaFase> actividadesAlmacenadas) {
		try {			
									
			//obtiene todos los registros historicos
			listaFasesHistorico = faseFichaAmbientalFacade.obtenerFasesHistoricoPorFicha(ficha.getId());
			
			
			if(listaFasesHistorico == null || listaFasesHistorico.isEmpty()){
				listaFasesHistorico = new ArrayList<FasesFichaAmbiental>();
			}
			
			
			
//			List<CatalogoCategoriaFase> listaTotalCategoria = new ArrayList<CatalogoCategoriaFase>();
//			List<CatalogoCategoriaFase> listaSeleccionados = new ArrayList<CatalogoCategoriaFase>();
//			listaHistoricosCategoria = new ArrayList<CatalogoCategoriaFase>();
//			
//			listaTotalCategoria = faseFichaAmbientalFacade.obtenerCatalogoCategoriaFasesPorFicha(ficha.getId());
//			
//			for(CatalogoCategoriaFase categoriaFase : listaTotalCategoria){
//				if(categoriaFase.getNuevoEnModificacion() != null && categoriaFase.getNuevoEnModificacion().equals("Modificacion")){
//					listaHistoricosCategoria.add(categoriaFase);
//				}else{
//					listaSeleccionados.add(categoriaFase);				
//					if(categoriaFase.getNuevoEnModificacion() != null && categoriaFase.getNuevoEnModificacion().equals("Nuevo")){
//						for (CatalogoCategoriaFase fase : catalogoGeneralPmaBean.getTiposActividades()) {
//							if (categoriaFase.getId().equals(fase.getId())) {
//								fase.setNuevoEnModificacion("Nuevo");
//							}							
//						}								
//					}
//				}
//			}
//				

			
			
			List<FichaAmbientalPma> fichaHistoricoAuxList = fichaAmbientalPmaFacade.getFichaAmbientalPorIdProyectoHistorico(proyectosBean.getProyecto().getId());
			
			if(fichaHistoricoAuxList != null && !fichaHistoricoAuxList.isEmpty()){
				
				fichaAreaImplantacionList = new ArrayList<FichaAmbientalPma>();
				fichaAguaPotableList = new ArrayList<FichaAmbientalPma>();
				fichaEnergiaElectricaList = new ArrayList<FichaAmbientalPma>();
				fichaAccesoVehicularList = new ArrayList<FichaAmbientalPma>();
				fichaAlcantarilladoList = new ArrayList<FichaAmbientalPma>();
				fichaHistoricoDescripcionZonaList = new ArrayList<FichaAmbientalPma>();
				fichaHistoricoOtrosInfraestructura = new ArrayList<FichaAmbientalPma>();
				
				FichaAmbientalPma fichaInicial = null;
				for(FichaAmbientalPma fichaBdd : fichaHistoricoAuxList){					
					if(fichaInicial == null)
						fichaInicial = ficha;
					
					//area de implantacion
					if((fichaBdd.getAreaImplantacion() == null && fichaInicial.getAreaImplantacion() == null) || 
							(fichaBdd.getAreaImplantacion() != null && fichaInicial.getAreaImplantacion() != null && 
							fichaBdd.getAreaImplantacion().equals(fichaInicial.getAreaImplantacion()))){
						//iguales
					}else{
						if(fichaBdd.getAreaImplantacion() != null)
							fichaAreaImplantacionList.add(0, fichaBdd);
					}
					
					
					//Agua potable
					if((fichaBdd.getAguaPotable() != null && fichaInicial.getAguaPotable() != null && 
							!fichaBdd.getAguaPotable().equals(fichaInicial.getAguaPotable())) || 
							(fichaBdd.getConsumoAgua() != null && fichaInicial.getConsumoAgua() != null && 
							!fichaBdd.getConsumoAgua().equals(fichaInicial.getConsumoAgua()))){
						fichaAguaPotableList.add(0, fichaBdd);
					}
					
					//Energía eléctrica
					if((fichaBdd.getEnergiaElectrica() != null && fichaInicial.getEnergiaElectrica() != null && 
							!fichaBdd.getEnergiaElectrica().equals(fichaInicial.getEnergiaElectrica())) || 
							(fichaBdd.getConsumoElectrico() != null && fichaInicial.getConsumoElectrico() != null && 
							!fichaBdd.getConsumoElectrico().equals(fichaInicial.getConsumoElectrico()))){
						fichaEnergiaElectricaList.add(0, fichaBdd);
					}
					
					//Acceso vehicular
					if((fichaBdd.getAccesoVehicular() != null && fichaInicial.getAccesoVehicular() != null && 
							!fichaBdd.getAccesoVehicular().equals(fichaInicial.getAccesoVehicular())) ||
							(fichaBdd.getTipoVia() != null && fichaInicial.getTipoVia() != null && 
							!fichaBdd.getTipoVia().equals(fichaInicial.getTipoVia()))){
						fichaAccesoVehicularList.add(0, fichaBdd);
					}
					
					//Descripcion de la zona 
					if (fichaBdd.getDescripcionZona() != null && fichaInicial.getDescripcionZona() != null && 
							!fichaBdd.getDescripcionZona().equals(fichaInicial.getDescripcionZona())) {
						fichaHistoricoDescripcionZonaList.add(0, fichaBdd);
					}
					
					//otros infraestructura
					if((fichaBdd.getOtrosInfraestructura() == null && fichaInicial.getOtrosInfraestructura() == null) ||
						(fichaBdd.getOtrosInfraestructura() != null && fichaInicial.getOtrosInfraestructura() != null && 
						fichaBdd.getOtrosInfraestructura().equals(fichaInicial.getOtrosInfraestructura()))){
						continue;
					}else{
						if(fichaBdd.getOtrosInfraestructura() != null)
							fichaHistoricoOtrosInfraestructura.add(0, fichaBdd);
					}
					
					fichaInicial = fichaBdd;
				}				
			}
			
			List<DetalleFichaPma> listaDetalleFichaHistoricoAux = new ArrayList<DetalleFichaPma>();
			listaDetalleFichaHistoricoAux = fichaAmbientalPmaFacade.getDetalleFichaPorIdFichaPorTipoHistorico(ficha.getId(),TipoCatalogo.TIPO_INFRAESTRUCTURA);
			
			listaDetalleFichaHistorico = new ArrayList<DetalleFichaPma>();
			
			if(listaDetalleFichaHistoricoAux == null){
				listaDetalleFichaHistorico = new ArrayList<DetalleFichaPma>();
			}else{
				if(!listaDetalleFichaHistoricoAux.isEmpty()){
					CatalogoGeneral infraestructuraInicial = null;
					
					for(DetalleFichaPma detalleBdd : listaDetalleFichaHistoricoAux){
						if(infraestructuraInicial == null)
							infraestructuraInicial = catalogoGeneralPmaBean.getTiposInfraestructurasSeleccionados();
						
						if(!detalleBdd.getCatalogoGeneral().equals(infraestructuraInicial)){
							listaDetalleFichaHistorico.add(0, detalleBdd);
						}
						
						infraestructuraInicial = detalleBdd.getCatalogoGeneral();
					}
					
					if(!listaDetalleFichaHistorico.isEmpty()){
						int tamanio = 0;
						for(DetalleFichaPma detalleOtr : listaDetalleFichaHistorico){
							if(detalleOtr.getCatalogoGeneral().getDescripcion().equals("Otros")){								
								detalleOtr.setOtraInfraestructura(fichaHistoricoOtrosInfraestructura.get(tamanio).getOtrosInfraestructura());
								tamanio ++;
							}
						}
					}
				}				
			}
			
			// Predios
			listaDetalleFichaHisPredio = new ArrayList<DetalleFichaPma>();
			listaDetalleFichaHisOtroPredio = new ArrayList<DetalleFichaPma>();

			listaDetalleFichaHisPredio = fichaAmbientalPmaFacade.getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigoHistorico(
							ficha.getId(), TipoCatalogo.SITUACION_PREDIO, TipoCatalogo.CODIGO_PREDIO_PRIMARIO);

			listaDetalleFichaHisOtroPredio = fichaAmbientalPmaFacade.getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigoHistorico(
							ficha.getId(), TipoCatalogo.SITUACION_PREDIO, TipoCatalogo.CODIGO_PREDIO_SECUDARIO);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}