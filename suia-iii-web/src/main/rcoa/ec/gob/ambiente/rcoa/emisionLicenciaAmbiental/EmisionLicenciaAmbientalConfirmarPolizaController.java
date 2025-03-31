package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.DocumentoResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.ResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.DocumentoResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ResolucionAmbiental;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.administracion.controllers.CombosController;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TipoOrganizacion;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class EmisionLicenciaAmbientalConfirmarPolizaController {
	
	private static final Logger LOG = Logger.getLogger(EmisionLicenciaAmbientalConfirmarPolizaController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	private Map<String, Object> variables;
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;
    
	/*EJBs*/
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
    
    
    // FACADES GENERALES
    @EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
    @EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
    @EJB
	private ResolucionAmbientalFacade resolucionAmbientalFacade;
    @EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
    @EJB
	private DocumentoResolucionAmbientalFacade  documentoResolucionAmbientalFacade;
    @EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
    @EJB
	private OrganizacionFacade organizacionFacade;
    
    /*List*/
    @Getter
    @Setter
    private List<InstitucionFinanciera> institucionesFinancieras;
    @Getter
	@Setter
	private List<TransaccionFinanciera> transaccionesFinancieras = new ArrayList<TransaccionFinanciera>();
    
    
    /*Object*/
    @Setter
    @Getter
    private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();
    @Setter
    @Getter
    private ResolucionAmbiental resolucionAmbiental = new ResolucionAmbiental();
    @Setter
    @Getter
    private InformacionProyectoEia informacionProyectoEia = new InformacionProyectoEia();
    @Setter
    @Getter
    private InstitucionFinanciera institucionFinanciera = new InstitucionFinanciera();
    @Setter
    @Getter
    private TransaccionFinanciera transaccionFinancieraSelected = new TransaccionFinanciera();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental facturaPermisoAmbiental = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental pagoEmisionPermiso = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental polizaCostoImplementacion = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental justificacionCostoMedidas = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental cronogramaValorado = new DocumentoResolucionAmbiental();
    
    
	/*Variables*/
    @Getter
	@Setter
    private String operador, tramite, tecnicoResponsable;
    @Setter
    @Getter
    private Integer idRegistroPreliminar;
    @Setter
    @Getter
    private Boolean detalleRevisionEmision, esEmpresaPublica;
    @Setter
    @Getter
    private String fechaActual; 
       
    @PostConstruct
	public void init() {
    	try {
    		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
    		tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
    		operador = (String) variables.get("operador");
    		tecnicoResponsable = (String) variables.get("tecnicoResponsable");
    		idRegistroPreliminar = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
    		
    		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idRegistroPreliminar);
    		informacionProyectoEia = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectoLicenciaCoa);
        	resolucionAmbiental = resolucionAmbientalFacade.getByIdRegistroPreliminar(idRegistroPreliminar);
        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        	fechaActual = simpleDateFormat.format(new Date());
        	Boolean entregoPolizaGarantia = false;
        	if (resolucionAmbiental.getEntregoPolizaGarantia() == null) {
        		resolucionAmbiental.setEntregoPolizaGarantia(entregoPolizaGarantia);
    		}
        	
        	esEmpresaPublica = false;
        	Organizacion organizacion = organizacionFacade.buscarPorRuc(proyectoLicenciaCoa.getUsuario().getNombre());
        	if(organizacion != null && organizacion.getId() != null && organizacion.getTipoOrganizacion() != null 
    				&& (organizacion.getTipoOrganizacion().getTipoEmpresa().equals(TipoOrganizacion.publica)
        				|| (organizacion.getTipoOrganizacion().getTipoEmpresa().equals(TipoOrganizacion.mixta)
        					&& organizacion.getParticipacionEstado().equalsIgnoreCase(CombosController.IGUAL_MAS_PARTICIPACION)))) {
    			esEmpresaPublica = true;
    		}
    	} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del Emisión.");
		}
	}
       
    public boolean validarDatos() {
    	boolean validar = true;
    	if(esEmpresaPublica) {
    		resolucionAmbiental.setEntregoPolizaGarantia(true);
    		validar = true;
    	} else {
	    	if (!resolucionAmbiental.getEntregoPolizaGarantia()) {
	    		JsfUtil.addMessageError("Por favor debe selecionar la opción (Si) para continuar con el trámite");
				validar = false;
			}
    	}
    	return validar;
    }
    
    private Usuario asignarTecnicoResponsable() {
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
    	
		String tipoRol = "role.esia.cz.tecnico.responsable";

		if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
			ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
			CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();

			Integer idSector = actividadPrincipal.getTipoSector().getId();

			tipoRol = "role.esia.pc.tecnico.responsable.tipoSector." + idSector;
		} else if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_EA)) {
			tipoRol = "role.esia.gad.tecnico.responsable";
		}

		String rolTecnico = Constantes.getRoleAreaName(tipoRol);

		// buscar usuarios por rol y area
		List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaTramite);
		if (listaUsuario == null || listaUsuario.size() == 0) {
			JsfUtil.addMessageError("Ocurrió un error. Comuníquese con Mesa de Ayuda.");
			System.out.println("No se encontró técnico responsable en " + areaTramite.getAreaName());
			return null;
		}

		// recuperar tecnico de bpm y validar si el usuario existe en el
		// listado anterior
		Usuario tecnicoResponsable = null;
		Usuario usuarioTecnico = usuarioFacade.buscarUsuario(this.tecnicoResponsable);
		if (usuarioTecnico != null
				&& usuarioTecnico.getEstado().equals(true)) {
			if (listaUsuario != null && listaUsuario.size() >= 0
					&& listaUsuario.contains(usuarioTecnico)) {
				tecnicoResponsable = usuarioTecnico;
			}
		}

		// si no se encontró el usuario se realiza la busqueda de uno nuevo
		// y se actualiza en el bpm
		if (tecnicoResponsable == null) {
			
			String proceso = Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL + "'' , ''" + Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2;
			List<Usuario> listaTecnicosResponsables = asignarTareaFacade.getCargaLaboralPorUsuariosProceso(listaUsuario,proceso);
			tecnicoResponsable = listaTecnicosResponsables.get(0);
		}
		
		return tecnicoResponsable;
    }
    
    public void enviar() {
    	if(validarDatos()) {
    		resolucionAmbiental = resolucionAmbientalFacade.guardar(resolucionAmbiental);
    		
    		try {
    			Map<String, Object> params=new HashMap<>();
    			
    			Usuario tecnico = asignarTecnicoResponsable();
    			if(tecnico != null) {
    				if (tecnicoResponsable == null) 
        				params.put("tecnicoResponsable",tecnico.getNombre());
        			else if (!tecnicoResponsable.equals(tecnico.getNombre())) 
        				params.put("tecnicoResponsable",tecnico.getNombre());
        			
        			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
        			
        			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
        			
        			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
    			}
    			
    	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
    		} catch (Exception e) {
    			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
    			e.printStackTrace();
    		}
    	}
    }

}