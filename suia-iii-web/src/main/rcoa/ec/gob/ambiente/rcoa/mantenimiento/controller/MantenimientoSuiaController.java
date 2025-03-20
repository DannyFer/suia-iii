package ec.gob.ambiente.rcoa.mantenimiento.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ModificacionEsIAFacade;
import ec.gob.ambiente.rcoa.facade.NotificacionDiagnosticoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.TestEsIAFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ActualizarTramitesFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.OficioPronunciamientoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProyectoPlaguicidasFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.OficioPronunciamientoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidas;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class MantenimientoSuiaController {
	
	private static final Logger LOG = Logger.getLogger(MantenimientoSuiaController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private NotificacionDiagnosticoAmbientalFacade notificacionDiagnosticoAmbientalFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private TestEsIAFacade testEsIAFacade;
	@EJB
	private ProyectoPlaguicidasFacade proyectoPlaguicidasFacade;
	@EJB
	private ActualizarTramitesFacade actualizarTramitesFacade;
	@EJB
	private OficioPronunciamientoPquaFacade oficioPronunciamientoPquaFacade;
	@EJB
	private ModificacionEsIAFacade modificacionEsIAFacade;

	@Getter
	@Setter
	private Integer idProceso, procesoAnterior, procesoNuevo;
	
	@Getter
	@Setter
	private String tramite, nombreVariable, valorVariable;
	
	public void notificarDiagnostico() {
		notificacionDiagnosticoAmbientalFacade.obtenerTareasFirma();
	}


	public boolean actualizarVariableInicioViabilidad(){
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("intersecaSnapForestIntanManglar", false);
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(),new Long(idProceso), parametros);
			
			JsfUtil.addMessageInfo("Actualización ejecutada correctamente");
			
			idProceso = null;

			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
			return false;
		}
	}
	
	
	public boolean iniciarBack(){
		try {
			
			ProyectoLicenciaCoa proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			proyectoLicenciaCoa.setEstadoRegistro(false);
			proyectoLicenciaCoa=proyectoLicenciaCoaFacade.guardar(proyectoLicenciaCoa);
			
			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
			return false;
		}
	}
	
	public void igualarProcesos() {
		testEsIAFacade.igualarProcesoBack(new Long(procesoAnterior), new Long(procesoNuevo));
	}
	
	public void actualizarVariable() {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			
			Integer valor = Integer.parseInt(valorVariable);
			parametros.put(nombreVariable, valor);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), new Long(idProceso), parametros);
			
			JsfUtil.addMessageInfo("Actualización ejecutada correctamente");
			
			idProceso = null;
			valorVariable = null;
			nombreVariable = null;
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
		}

	}
	
	public boolean actualizarInfoProductoPqua(){
		try {
			
			Integer totalActualizados = actualizarTramitesFacade.actualizarInfoProducto();
			
			JsfUtil.addMessageInfo("Actualización ejecutada correctamente " + totalActualizados + " registros");
						
			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
			return false;
		}
	}

	public boolean actualizarFechaEnvioPqua(){
		try {
			
			List<ProyectoPlaguicidas> proyectos = proyectoPlaguicidasFacade.getTramitesIngresados();
			
			for (ProyectoPlaguicidas item : proyectos) {
				if(item.getFechaEnvioRevision() == null) {
					Date fechaEnvio = actualizarTramitesFacade.actualizarTramite(item.getCodigoProyecto());
					if(fechaEnvio != null) {
						item.setFechaEnvioRevision(fechaEnvio);
						proyectoPlaguicidasFacade.guardar(item);

						System.out.println("PquaUpdateCodigo: " + item.getCodigoProyecto() + " fechaEnvio:" + fechaEnvio);
					}
				}
			}
			
			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
			return false;
		}
	}
	
	public boolean actualizarPronunciamientoPqua() {
		List<ProyectoPlaguicidas> proyectos = proyectoPlaguicidasFacade.getTramitesIngresados();
		Integer totalActualizados = 0;
		
		for (ProyectoPlaguicidas item : proyectos) {
			try {
				
				if(item.getResultadoRevision() == null) {
					List<Object> objectProceso = actualizarTramitesFacade.recuperarEstadoProceso(item.getCodigoProyecto());
					if(objectProceso != null && objectProceso.size() > 0) {
						Object[] objAux = (Object[]) objectProceso.get(0);
						Integer estado = (Integer) objAux[1];
						Long idProceso = new Long(objAux[0].toString());
						
						Boolean actualizado = false;
						
						if(estado.equals(1)) {
							List<OficioPronunciamientoPqua> oficios = oficioPronunciamientoPquaFacade.getPorProyecto(item.getId());
							if(oficios != null && oficios.size() > 0 && oficios.get(0).getFechaFirma() != null) {
								Boolean pronunciamiento = oficios.get(0).getEsAprobacion();
								Integer tipoPronunciamiento = (pronunciamiento) ? ProyectoPlaguicidas.TRAMITE_APROBADO : ProyectoPlaguicidas.TRAMITE_OBSERVADO;
								item.setResultadoRevision(tipoPronunciamiento);
								item.setFechaResultado(oficios.get(0).getFechaFirma());
								
								proyectoPlaguicidasFacade.guardar(item);
								actualizado = true;
							}
						} else if(estado.equals(2)) {
							Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), idProceso);
							Boolean esPronunciamientoFavorable = Boolean.valueOf((String) variables.get("esPronunciamientoAprobacion"));
							
							if(esPronunciamientoFavorable) {
								List<OficioPronunciamientoPqua> oficios = oficioPronunciamientoPquaFacade.getPorProyecto(item.getId());
								if(oficios != null && oficios.size() > 0) {
									Boolean pronunciamiento = oficios.get(0).getEsAprobacion();
									Integer tipoPronunciamiento = (pronunciamiento) ? ProyectoPlaguicidas.TRAMITE_APROBADO : ProyectoPlaguicidas.TRAMITE_OBSERVADO;
									item.setResultadoRevision(tipoPronunciamiento);
									item.setFechaResultado(oficios.get(0).getFechaFirma());
									
									proyectoPlaguicidasFacade.guardar(item);
									actualizado = true;
								}
							} else {
								Timestamp tsFecha = (Timestamp) objAux[2];
								Date fechaFin = new Date(tsFecha.getTime());
								
								item.setResultadoRevision( ProyectoPlaguicidas.TRAMITE_ARCHIVADO);
								item.setFechaResultado(fechaFin);
								
								proyectoPlaguicidasFacade.guardar(item);
								actualizado = true;
							}
						}					
						
						if(actualizado) {
							totalActualizados++;
							System.out.println("PquaUpdateCodigo: " + item.getCodigoProyecto() + " pronunciamiento:" + item.getResultadoRevision());
						}
					}
				}
			
			} catch (Exception e) {
				JsfUtil.addMessageError("Ha ocurrido un error. Tramite " + item.getCodigoProyecto());
				System.out.println("Error_PquaUpdateCodigo: " + item.getCodigoProyecto());
				e.printStackTrace();
				return false;
			}
		}
		
		JsfUtil.addMessageInfo("Actualización ejecutada correctamente " + totalActualizados + " registros");
		
		return true;
	}
	
	public void actualizarProrrogaEsia(){
		try {
			modificacionEsIAFacade.actualizarFechasProrroga();

			JsfUtil.addMessageInfo("Actualización ejecutada correctamente ");
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
		}
	}
	
	public void migrarIniciarBypass(){
		try {
			String mensaje = modificacionEsIAFacade.iniciarNuevoBypass();

			JsfUtil.addMessageInfo(mensaje);
			JsfUtil.addMessageInfo("Actualización ejecutada correctamente ");
			
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
		}
	}
	
}
