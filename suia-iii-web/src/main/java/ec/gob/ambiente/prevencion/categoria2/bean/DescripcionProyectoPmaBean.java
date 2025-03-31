package ec.gob.ambiente.prevencion.categoria2.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogocategorias.facade.CatalogoCategoriasFacade;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.ProyectoDesechoPeligroso;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FaseFichaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proyectodesechopeligroso.facade.ProyectoDesechoPeligrosoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * 
 * @author Jonathan Guerrero
 * 
 */
@ManagedBean
@ViewScoped
public class DescripcionProyectoPmaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8467128768519887464L;
	private Logger LOG = Logger.getLogger(DescripcionProyectoPmaBean.class);
	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	@EJB
	private FaseFichaAmbientalFacade faseFichaAmbientalFacade;
	@EJB
	private CatalogoCategoriasFacade catalogoCategoriasFacade;
	@EJB
	private ProyectoDesechoPeligrosoFacade proyectoDesechoPeligrosoFacade;
	@Setter
	@ManagedProperty(value = "#{catalogoGeneralPmaBean}")
	private CatalogoGeneralPmaBean catalogoGeneralPmaBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	@Getter
	@Setter
	private FichaAmbientalPma ficha;
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;
	@Getter
	@Setter
	private List<DesechoPeligroso> desechoPeligrososEspeciales;
	@Getter
	@Setter
	private List<DesechoPeligroso> desechoPeligrososEspecialesSeleccionados;
	@Setter
	@Getter
	private List<ProyectoDesechoPeligroso> proyectoDesechoPeligrosos;
	@Getter
	@Setter
	private boolean deshabilitarTablaTipoDesechoEspecial;
	@Getter
	@Setter
	private boolean deshabilitarBtnGuardar;
	@Getter
	@Setter
	private DesechoPeligroso desechoSeleccionado;

	private static final String CODIGO_TIPO_DESECHO_ESPECIAL_01 = "71.02.01";
	private static final String CODIGO_TIPO_DESECHO_ESPECIAL_02 = "71.02.02";
	private static final String CODIGO_TIPO_DESECHO_ESPECIAL_03 = "71.02.03";
	private static final Integer CODIGO_DESECHOS_PELIGROSOS_ESPECIALES = 49;

//	@PostConstruct
//	public void init() {
//		try {
//			proyecto = proyectosBean.getProyecto();
//			deshabilitarBtnGuardar = true;
//			this.ficha = fichaAmbientalPmaFacade
//					.getFichaAmbientalPorIdProyecto(proyecto.getId());
//			if (ficha != null) {
//				catalogoGeneralPmaBean
//						.setActividadesSeleccionadas(faseFichaAmbientalFacade
//								.obtenerCatalogoCategoriaFasesPorFicha(ficha
//										.getId()));
//				CatalogoCategoriaSistema catalogoTiposDesecho = catalogoCategoriasFacade
//						.buscarCatalogoCategoriaSistemaPorId(ficha
//								.getProyectoLicenciamientoAmbiental()
//								.getCatalogoCategoria().getId());
//				if (catalogoTiposDesecho != null
//						&& (catalogoTiposDesecho.getCodigo().equals(
//								CODIGO_TIPO_DESECHO_ESPECIAL_01)
//								|| catalogoTiposDesecho.getCodigo().equals(
//										CODIGO_TIPO_DESECHO_ESPECIAL_02) || catalogoTiposDesecho
//								.getCodigo().equals(
//										CODIGO_TIPO_DESECHO_ESPECIAL_03))) {
//					desechoPeligrososEspecialesSeleccionados = new ArrayList<DesechoPeligroso>();
//					deshabilitarTablaTipoDesechoEspecial = true;
//					desechoPeligrososEspeciales = proyectoDesechoPeligrosoFacade
//							.buscarDesechoPeligrososPorCodigoTipoDeDesecho(CODIGO_DESECHOS_PELIGROSOS_ESPECIALES);
//					proyectoDesechoPeligrosos = proyectoDesechoPeligrosoFacade
//							.buscarProyectoDesechoPeligrosoPorProyecto(proyecto
//									.getId());
//					if (proyectoDesechoPeligrosos == null) {
//						proyectoDesechoPeligrosos = new ArrayList<ProyectoDesechoPeligroso>();
//					} else {
//						for (ProyectoDesechoPeligroso pdp : proyectoDesechoPeligrosos) {
//							for (DesechoPeligroso dpe : desechoPeligrososEspeciales) {
//								if (pdp.getDesechoPeligroso().getDescripcion()
//										.equals(dpe.getDescripcion())) {
//									dpe.setCapacidadGestion(pdp
//											.getCapacidadGestion()
//											.doubleValue());
//									dpe.setSeleccionado(true);
//									deshabilitarBtnGuardar = false;
//								}
//							}
//						}
//					}
//				} else {
//					deshabilitarTablaTipoDesechoEspecial = false;
//					desechoPeligrososEspeciales = new ArrayList<DesechoPeligroso>();
//					ficha.setValidarDescripcionProyectoObraActividad(true);
//					fichaAmbientalPmaFacade.guardarSoloFicha(ficha);
//				}
//			} else {
//				ficha = new FichaAmbientalPma();
//				ficha.setProyectoLicenciamientoAmbiental(proyecto);
//				ficha.setProcessId(bandejaTareasBean.getProcessId());
//				ficha = fichaAmbientalPmaFacade.guardarSoloFicha(ficha);
//				init();
//			}
//		} catch (Exception e) {
//			LOG.error("ERROR", e);
//			JsfUtil.addMessageError("No se logró cargar los datos iniciales.");
//		}
//	}

	@PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = request.getRequestURI();
        if (!url.equals("/suia-iii/prevencion/categoria2/v2/fichaAmbiental/marcoReferencial.jsf")) {
            if (!url.equals("/suia-iii/prevencion/categoria2/v2/fichaAmbiental/descripcionProceso.jsf")) {
            	if (!url.equals("/suia-iii/prevencion/categoria2/v2/fichaAmbiental/envioFicha.jsf")) {
            		if (!url.equals("/suia-iii/prevencion/categoria2/v2/fichaAmbiental/impactoAmbientales.jsf")) {
            			cargarDatos();
            		}
            	}
            }
        }
    }

    public void cargarDatos() {
        try {
        	if(proyectosBean.getProyecto() != null){
        		proyecto = proyectosBean.getProyecto();
                deshabilitarBtnGuardar = true;
                this.ficha = fichaAmbientalPmaFacade
                        .getFichaAmbientalPorIdProyecto(proyecto.getId());
                if (ficha != null) {
                    catalogoGeneralPmaBean
                            .setActividadesSeleccionadas(faseFichaAmbientalFacade
                                    .obtenerCatalogoCategoriaFasesPorFicha(ficha
                                            .getId()));
                    CatalogoCategoriaSistema catalogoTiposDesecho = catalogoCategoriasFacade
                            .buscarCatalogoCategoriaSistemaPorId(ficha
                                    .getProyectoLicenciamientoAmbiental()
                                    .getCatalogoCategoria().getId());
                    if (catalogoTiposDesecho != null
                            && (catalogoTiposDesecho.getCodigo().equals(
                                    CODIGO_TIPO_DESECHO_ESPECIAL_01)
                                    || catalogoTiposDesecho.getCodigo().equals(
                                            CODIGO_TIPO_DESECHO_ESPECIAL_02) || catalogoTiposDesecho
                                    .getCodigo().equals(
                                            CODIGO_TIPO_DESECHO_ESPECIAL_03))) {
                        desechoPeligrososEspecialesSeleccionados = new ArrayList<DesechoPeligroso>();
                        deshabilitarTablaTipoDesechoEspecial = true;
                        desechoPeligrososEspeciales = proyectoDesechoPeligrosoFacade
                                .buscarDesechoPeligrososPorCodigoTipoDeDesecho(CODIGO_DESECHOS_PELIGROSOS_ESPECIALES);
                        proyectoDesechoPeligrosos = proyectoDesechoPeligrosoFacade
                                .buscarProyectoDesechoPeligrosoPorProyecto(proyecto
                                        .getId());
                        if (proyectoDesechoPeligrosos == null) {
                            proyectoDesechoPeligrosos = new ArrayList<ProyectoDesechoPeligroso>();
                        } else {
                            for (ProyectoDesechoPeligroso pdp : proyectoDesechoPeligrosos) {
                                for (DesechoPeligroso dpe : desechoPeligrososEspeciales) {
                                    if (pdp.getDesechoPeligroso().getDescripcion()
                                            .equals(dpe.getDescripcion())) {
                                        dpe.setCapacidadGestion(pdp
                                                .getCapacidadGestion()
                                                .doubleValue());
                                        dpe.setSeleccionado(true);
                                        deshabilitarBtnGuardar = false;
                                    }
                                }
                            }
                        }
                    } else {
                        deshabilitarTablaTipoDesechoEspecial = false;
                        desechoPeligrososEspeciales = new ArrayList<DesechoPeligroso>();
                        ficha.setValidarDescripcionProyectoObraActividad(true);
                        fichaAmbientalPmaFacade.guardarSoloFicha(ficha);
                    }
                } else {
                    ficha = new FichaAmbientalPma();
                    ficha.setProyectoLicenciamientoAmbiental(proyecto);
                    ficha.setProcessId(bandejaTareasBean.getProcessId());
                    ficha = fichaAmbientalPmaFacade.guardarSoloFicha(ficha);
                    init();
                }            
        	}
        } catch (Exception e) {
            LOG.error("ERROR", e);
            JsfUtil.addMessageError("No se logró cargar los datos iniciales.");
        }
    }
	
    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/fichaAmbiental/default.jsf");
    }

    public void validarTareaBpmV2() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/v2/fichaAmbiental/default.jsf");
    }

	public void validarSeleccionDesechoPeligroso(
			DesechoPeligroso desechoPeligroso) {
		if (desechoPeligroso.isSeleccionado()) {
			desechoPeligroso.setHabilitarCapacidadGestion(false);
		} else {
			desechoPeligroso.setHabilitarCapacidadGestion(true);
			desechoPeligroso.setCapacidadGestion(0.0);
		}
		boolean eligioDesecho = false;
		for (int i = 0; i < desechoPeligrososEspeciales.size(); i++) {
			if (desechoPeligrososEspeciales.get(i).getClave()
					.equals(desechoPeligroso.getClave())) {
				desechoPeligrososEspeciales.get(i).setSeleccionado(desechoPeligroso.isSeleccionado());
				if (desechoPeligrososEspeciales.get(i).isSeleccionado()) {
					eligioDesecho = true;
				}
				break;
			}
		}
		if (eligioDesecho)
			deshabilitarBtnGuardar = false;
		else
			deshabilitarBtnGuardar = true;
	}

	public void guardarDescripcionProyectoAmbiental() {
		ficha.setValidarDescripcionProyectoObraActividad(false);
		if (deshabilitarTablaTipoDesechoEspecial) {
			boolean eligioDesecho = false;
			for (DesechoPeligroso dp : desechoPeligrososEspeciales) {
				ProyectoDesechoPeligroso pdp = proyectoDesechoPeligrosoFacade
						.buscarProyectoDesechoPeligrosoPorProyecto(
								proyecto.getId(), dp.getId());
				if (dp.isSeleccionado()) {
					if (dp.getCapacidadGestion() > 0.0) {
						ProyectoDesechoPeligroso proyectoDesechoPeligroso;
						if (pdp == null) {
							proyectoDesechoPeligroso = new ProyectoDesechoPeligroso();
						} else {
							proyectoDesechoPeligroso = pdp;
						}
						proyectoDesechoPeligroso
								.setProyectoLicenciamientoAmbiental(proyecto);
						proyectoDesechoPeligroso.setDesechoPeligroso(dp);
						proyectoDesechoPeligroso.setEstado(true);
						proyectoDesechoPeligroso.setCapacidadGestion(dp
								.getCapacidadGestion());
						proyectoDesechoPeligrosos.add(proyectoDesechoPeligroso);
						proyectoDesechoPeligrosoFacade
								.guardarProyectoDesechoPeligroso(proyectoDesechoPeligroso);
						eligioDesecho = true;
					} else {
						JsfUtil.addMessageError("Debe llenar el campo Capacidad de gesti&oacute;n con un valor mayor a 0");
						return;
					}
				} else {
					dp.setCapacidadGestion(0.0);
					if (pdp != null) {
						pdp.setEstado(false);
						pdp.setCapacidadGestion(0.0);
						proyectoDesechoPeligrosoFacade
								.guardarProyectoDesechoPeligroso(pdp);
					}
				}
			}
			if (proyectoDesechoPeligrosos.isEmpty() || !eligioDesecho) {
				ficha.setValidarDescripcionProyectoObraActividad(false);
				fichaAmbientalPmaFacade.guardarSoloFicha(ficha);
				JsfUtil.addMessageError("Debe elegir al menos 1 desecho especial para poder continuar");
				return;
			}
			ficha.setValidarDescripcionProyectoObraActividad(true);
			fichaAmbientalPmaFacade.guardarSoloFicha(ficha);
			JsfUtil.addMessageInfo("La operación se ha completado satisfactoriamente.");
		}
	}
}