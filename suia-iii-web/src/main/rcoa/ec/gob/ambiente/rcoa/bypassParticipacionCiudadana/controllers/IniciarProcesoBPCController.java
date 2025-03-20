package ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.controllers;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.BPCBypassFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformeTecnicoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class IniciarProcesoBPCController implements Serializable{

	private static final long serialVersionUID = -1214584329270287726L;

	@EJB
	private ProcesoFacade procesoFacade;		
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;	
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private InformeTecnicoEsIAFacade informeTecnicoEsIAFacade;
	@EJB
    private OrganizacionFacade organizacionFacade;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
	private BPCBypassFacade bPCBypassFacade;

	@Getter	
	private ProyectoLicenciaCoa proyecto;
	
	private InformacionProyectoEia esiaProyecto;
	
	@Getter
	@Setter
	private String tramite;

	@Getter	
	private Usuario tecnicoResponsable;
	
	@PostConstruct
	public void init() {
		
	}
	
	public void buscarProyecto() {
		try {
			tecnicoResponsable=null;
			proyecto= (tramite==null || tramite.isEmpty())?null:proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			if(proyecto!=null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date fechaSuspensionBypass = sdf.parse(Constantes.getFechaSuspensionPpcBypass());
				Date fechaProyecto = sdf.parse(proyecto.getFechaGeneracionCua().toString());
				
				//suspensión de PPC por solicitud Ticket#10351907
				if(fechaProyecto.compareTo(fechaSuspensionBypass) >= 0) {
					//envio notificación suspensión proyectos registrados a partir de 2021-10-12
					JsfUtil.addMessageWarning("Suspensión de PPC para proyectos registrados a partir del 12-10-2021");
				} else {
					esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
					if(esiaProyecto!=null) {
						InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, InformeTecnicoEsIA.consolidado);
						
						if(informeTecnico!=null) {
							if(informeTecnico.getTipoPronunciamiento()!=null && informeTecnico.getTipoPronunciamiento()==1) {
								tecnicoResponsable=usuarioFacade.buscarUsuario(informeTecnico.getUsuarioCreacion());
							}else {
								JsfUtil.addMessageWarning("Informe de Estudio de Impacto Ambiental no es Aprobado");
							}					
						}else {
							JsfUtil.addMessageWarning("Informe de Estudio de Impacto Ambiental no encontrado");
						}
					}else {
						JsfUtil.addMessageWarning("Estudio de Impacto Ambiental no encontrado");
					}
				}
			}else {
				JsfUtil.addMessageWarning("Proyecto no encontrado");
			}		
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public String getNombreProponente() {

		String nombreOperador = "";
		
		if(proyecto != null ) {
			Usuario usuarioOperador = proyecto.getUsuario();
			try {
				if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
					nombreOperador = usuarioOperador.getPersona().getNombre();
				} else {
					Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
					nombreOperador = organizacion.getNombre();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return nombreOperador;
	}
	
	public String getCategoria() {		
		switch (proyecto!=null?proyecto.getCategorizacion():0) {
		case 1:
			return "Certificado Ambiental";			
		case 2:
			return "Registro Ambiental";
		case 3:
			return "Licencia Ambiental";
		case 4:
			return "Licencia Ambiental";
		default:
			break;
		}
		return "";
	}

	public String getImpacto() {		
		switch (proyecto!=null?proyecto.getCategorizacion():0) {
		case 1:
			return "Impacto NO SIGNIFICATIVO";			
		case 2:
			return "Impacto BAJO";
		case 3:
			return "Impacto MEDIO";
		case 4:
			return "Impacto ALTO";
		default:
			break;
		}
		return "";
	}
	
	public int getNumeroFacilitadores() {
		return (esiaProyecto!=null && esiaProyecto.getNumeroFacilitadores()!=null)?esiaProyecto.getNumeroFacilitadores():0;
	}
	
	public boolean validar() {
		if(proyecto==null || proyecto.getCategorizacion()<3 || getNumeroFacilitadores()==0 || tecnicoResponsable ==null) {
			return false;
		}
		
		return true;
	}
	
	public void iniciarBypass() {
		if(!validar()) {			
			return;
		}
		
		if(bPCBypassFacade.verificarProcesoIniciado(proyecto.getCodigoUnicoAmbiental())) {
			JsfUtil.addMessageWarning("El proceso ya ha sido Iniciado");
			return;
		}
		
		try {			
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("tramite",proyecto.getCodigoUnicoAmbiental());
			parametros.put("idProyecto", proyecto.getId());
			parametros.put("operador", proyecto.getUsuario().getNombre());
			parametros.put("tecnicoResponsableEIA", tecnicoResponsable.getNombre());
			parametros.put("numeroFacilitadores", getNumeroFacilitadores());
			parametros.put("facilitadorAdicional", false);
			
			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_PROCESO_BYPASS_PARTICIPACION_CIUDADANA, tramite, parametros);
						
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String [] args) {
		Date fecha =new Date((2021-1900),9,11);
		System.out.println(fecha.toString());
	}
	
	public void iniciarAutomatico() {
		List<String> listaProyectos = bPCBypassFacade.getProyectosEiaAprobados();
		
		Integer iniciados = 0;
		
		for (String tramite : listaProyectos) {
			proyecto= proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			if (proyecto != null && proyecto.getId() != null) {
				if(!bPCBypassFacade.verificarProcesoIniciado(proyecto.getCodigoUnicoAmbiental())) {
					esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
					if (esiaProyecto != null) {
						InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, InformeTecnicoEsIA.consolidado);
						
						if (informeTecnico != null) {
							tecnicoResponsable=usuarioFacade.buscarUsuario(informeTecnico.getUsuarioCreacion());
							try {			
								Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
								parametros.put("tramite",proyecto.getCodigoUnicoAmbiental());
								parametros.put("idProyecto", proyecto.getId());
								parametros.put("operador", proyecto.getUsuario().getNombre());
								parametros.put("tecnicoResponsableEIA", tecnicoResponsable.getNombre());
								parametros.put("numeroFacilitadores", getNumeroFacilitadores());
								parametros.put("facilitadorAdicional", false);
								
								procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_PROCESO_BYPASS_PARTICIPACION_CIUDADANA, tramite, parametros);
								
								iniciados++;
											
								System.out.println("Iniciado proceso bypass del proyecto " + proyecto.getCodigoUnicoAmbiental());
							} catch (Exception e) {
								e.printStackTrace();
								JsfUtil.addMessageError("Error al realizar la operación.");
							}
						}
					}
				} else {
					System.out.println("El proyecto " + proyecto.getCodigoUnicoAmbiental() + " ya ha iniciado BYPASS");
				}
			}
		}
		
		JsfUtil.addMessageInfo("Se iniciarion " + iniciados + " procesos de " + listaProyectos.size());
		
	}
}