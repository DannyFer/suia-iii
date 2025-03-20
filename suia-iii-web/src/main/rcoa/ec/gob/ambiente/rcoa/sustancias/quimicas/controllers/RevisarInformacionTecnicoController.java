package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.GestionarProductosQuimicosProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.UbicacionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ResponsableSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.UbicacionSustancia;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RevisarInformacionTecnicoController {
	
	private static final Logger LOG = Logger.getLogger(RevisarInformacionTecnicoController.class);
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{buscarUsuarioBean}")
    private BuscarUsuarioBean buscarUsuarioBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	
    
    /*EJBs*/
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
    private CatalogoCoaFacade catalogoCoaFacade;
	
	@EJB
    private ContactoFacade contactoFacade;
	
	@EJB
    private GestionarProductosQuimicosProyectoAmbientalFacade sustanciasProyectoFacade;
	
	@EJB
    private OrganizacionFacade organizacionFacade;    
    
    @EJB
    private ProcesoFacade procesoFacade;
    
    @EJB
    private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
    
    @EJB
    private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;   
    
    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    
    @EJB
    private UbicacionSustanciaQuimicaFacade ubicacionSustanciaQuimicaFacade;
    
    @EJB
    private UsuarioFacade usuarioFacade;
           
    /*List*/    
    @Getter
    private List<CatalogoGeneralCoa> lugaresLista;
    
    @Getter
    private List<CatalogoGeneralCoa> tipoIdentificacionLista;
    
    @Getter
    private List<CatalogoGeneralCoa> unidadesMedidaLista;
                
    @Getter
    private List<GestionarProductosQuimicosProyectoAmbiental> gestionarProductosQuimicosProyectoAmbientalLista;
    
    @Getter
    private List<UbicacionesGeografica> provinciasLista;
    
    @Getter
    private List<UbicacionSustancia> ubicacionSustanciaProyectoLista;
      
       
	/*Object*/
	private Map<String, Object> variables;	
	
	private Usuario usuarioOperador;
	
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private RegistroSustanciaQuimica registroSustanciaQuimica;
	
	@Getter
	@Setter
	private ResponsableSustanciaQuimica responsableSustanciaQuimica;	
	
	@Getter
	@Setter
	private GestionarProductosQuimicosProyectoAmbiental gestionarProductosQuimicosProyectoAmbientalSeleccionado;
	
	@Getter
	@Setter
    private UbicacionesGeografica provinciaSeleccionada;
	
	@Getter
	@Setter
    private UbicacionSustancia ubicacionSustanciaProyecto;
		
	/*Boolean*/	
	
    /*Integer*/
      
    
	/*String*/
	private String varUsuarioOperador,varTramite;
	
	//Datos del operador
    @Getter       
    private String rucCedula,nombreRazonSocial,telefono,correo,direccion,provincia,canton,parroquia,cedulaPersona,nombrePersona;    
    
    
	
	@PostConstruct
	public void init(){		
		try {
			cargarDatosIniciales();			
			cargarDatosTarea();
			cargarDatosOperador();
			cargarDatosProyecto();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void cargarDatosIniciales(){		
		//Iniciar Objetos
		ubicacionSustanciaProyecto=new UbicacionSustancia();
		responsableSustanciaQuimica=new ResponsableSustanciaQuimica();
		
		//Iniciar Listas		
		ubicacionSustanciaProyectoLista=new ArrayList<>();	
			
		//Cargar Catalogos 
		provinciasLista=ubicacionGeograficaFacade.getProvincias();
		lugaresLista=catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.RSQ_TIPO_LUGAR);
		tipoIdentificacionLista=catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.TIPO_IDENTIFICACION);
		unidadesMedidaLista=catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.RSQ_UNIDAD_MEDIDA);
	}
			
	private void cargarDatosTarea() throws ServiceException{

		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());			
			varUsuarioOperador=(String)variables.get("usuario_operador");
			varTramite=(String)variables.get("tramite");
			//varTramite="MAE-RA-2020-415478";
						
			//numeroObservaciones=variables.containsKey("numero_observaciones")?(Integer.valueOf((String)variables.get("numero_observaciones"))):0;
		} catch (JbpmException e) {
			LOG.error("Error al recuperar tarea "+e.getCause()+" "+e.getMessage());
		}
	}
	
	private void cargarDatosOperador() throws ServiceException{	
		
		usuarioOperador=usuarioFacade.buscarUsuario(varUsuarioOperador);
		rucCedula=usuarioOperador.getNombre();
				
		Organizacion orga= organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
		nombreRazonSocial=orga!=null?orga.getNombre():usuarioOperador.getPersona().getNombre();	
		
		cedulaPersona=orga!=null?orga.getPersona().getPin():usuarioOperador.getPersona().getPin();
		nombrePersona=orga!=null?orga.getPersona().getNombre():usuarioOperador.getPersona().getNombre();
		
		provincia=usuarioOperador.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
		canton=usuarioOperador.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
		parroquia=usuarioOperador.getPersona().getUbicacionesGeografica().getNombre();
		
		List<Contacto> contactos=contactoFacade.buscarPorPersona(usuarioOperador.getPersona());
		for (Contacto contacto : contactos) {
			switch (contacto.getFormasContacto().getId()) {
			case FormasContacto.TELEFONO:
				telefono=contacto.getValor();
				break;
			case FormasContacto.DIRECCION:
				direccion=contacto.getValor();
				break;
			case FormasContacto.EMAIL:
				correo=contacto.getValor();
				break;	

			default:
				break;
			} 
		}
	}
	
	private void cargarDatosProyecto(){		
		proyectoLicenciaCoa=proyectoLicenciaCoaFacade.buscarProyecto(varTramite);
		registroSustanciaQuimica=registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyectoLicenciaCoa);
		
		if(registroSustanciaQuimica!=null) {		
			ubicacionSustanciaProyectoLista=ubicacionSustanciaQuimicaFacade.obtenerUbicacionesPorRSQ(registroSustanciaQuimica);				
		}		
	}	
		
	public List<UbicacionesGeografica> getCantonesLista(){
		if(provinciaSeleccionada!=null)
			return ubicacionGeograficaFacade.getUbicacionPadre(provinciaSeleccionada);
		else
			return new ArrayList<UbicacionesGeografica>();
	}	
	public List<UbicacionSustancia> getUbicacionSustancia(GestionarProductosQuimicosProyectoAmbiental sustancia){
		List<UbicacionSustancia> lista=new ArrayList<>();
		if(sustancia!=null) {
			for (UbicacionSustancia ubicacion : ubicacionSustanciaProyectoLista) {
				if(ubicacion.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getId().intValue()==sustancia.getSustanciaquimica().getId().intValue())
					lista.add(ubicacion);
			}
		}		
		return lista;
	}
	
	private boolean requiereInspeccion() {
		for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
			if(item.getNecesitaInspeccion()!=null && item.getNecesitaInspeccion()){
				return true;					
			}
		}		
		return false;
	}
	
	private boolean requiereApoyo() {
		if(requiereInspeccion()) {
			for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
				if(item.getNecesitaApoyo()!=null && item.getNecesitaApoyo()){
					return true;				
				}
			}
		}	
		return false;
	}
	
	private List<String> usuariosApoyo() {
		List<String> lista=new ArrayList<String>();
		if(requiereApoyo()) {
			for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
				if(item.getNecesitaInspeccion()!=null && item.getNecesitaInspeccion() 
				&& item.getNecesitaApoyo()!=null && item.getNecesitaApoyo()){
					if(item.getUsuarioApoyo()!=null && !lista.contains(item.getUsuarioApoyo().getNombre())) {
						lista.add(item.getUsuarioApoyo().getNombre());
					}								
				}
			}
		}
			
		return lista;
	}
	
	public boolean validarInspecciones() {
//		if(ubicacionSustanciaProyectoLista.isEmpty())
//			return false;
		
		for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
			if(item.getNecesitaInspeccion()!=null &&item.getNecesitaInspeccion() 
			&& item.getNecesitaApoyo()!=null && item.getNecesitaApoyo()){				
				if(item.getUsuarioApoyo()==null) {
					return false;
				}					
			}
		}
		if(requiereApoyo() && usuariosApoyo().isEmpty())
			return false;
		return true;				
	}
	
	public void solicitarApoyoListener(UbicacionSustancia ubicacionSustancia) {
		if(!ubicacionSustancia.getNecesitaInspeccion()) {
			ubicacionSustancia.setNecesitaApoyo(false);
		}
		
		if(ubicacionSustancia.getNecesitaApoyo()!=null && ubicacionSustancia.getNecesitaApoyo()){
			buscarTecnicoApoyo(ubicacionSustancia);
			if(ubicacionSustancia.getUsuarioApoyo()==null) {
				JsfUtil.addMessageError("Usuario Técnico de Apoyo no encontrado en "+ubicacionSustancia.getUbicacionesGeografica());
			}					
		}else {
			ubicacionSustancia.setArea(null);
			ubicacionSustancia.setUsuarioApoyo(null);
			ubicacionSustancia.setFechaSolicitudApoyo(null);
		}						
	}
	
	private Usuario buscarTecnicoApoyo(UbicacionSustancia ubicacionSustancia){
		
		String roleKey="role.rsq.cz.tecnico.revision.proyecto";		
		Area area=areaFacade.getAreaCoordinacionZonal(ubicacionSustancia.getUbicacionesGeografica().getUbicacionesGeografica());
				
		try {
			ubicacionSustancia.setArea(area);
			ubicacionSustancia.setUsuarioApoyo(buscarUsuarioBean.buscarUsuario(roleKey,area));
			ubicacionSustancia.setFechaSolicitudApoyo(new Date());
			return ubicacionSustancia.getUsuarioApoyo();
		} catch (Exception e) {
			LOG.error("No se ha encontrado usuario con en rol "+Constantes.getRoleAreaName(roleKey)+"("+roleKey+") en el area "+area.getAreaName());			
		}
		
		return null;
	}
	
	public boolean guardar() {        
		if(validarInspecciones()) {
			for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {				
				ubicacionSustanciaQuimicaFacade.guardar(item,  JsfUtil.getLoggedUser());
			}
			JsfUtil.addMessageInfo("Información Guardada Correctamente");
			return true;
		}
		return false;
    }
	
	public void enviar(){
		boolean operacionCorrecta=false;
		if(!guardar())
			return;
		
		try {
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("requiere_inspeccion",requiereInspeccion());
			parametros.put("requiere_apoyo",requiereApoyo());
			if(requiereApoyo()){
				String[] usuarios=new String[usuariosApoyo().size()];
				for (int i=0;i<usuariosApoyo().size();i++) {
					usuarios[i]=usuariosApoyo().get(i);
				}							
				parametros.put("usuario_tecnico_apoyo_lista",usuarios);
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getProcessInstanceId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			operacionCorrecta=true;			
			
		} catch (JbpmException e) {					
			e.printStackTrace();
		}
		
		if(operacionCorrecta){
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			JsfUtil.redirectToBandeja();
		}
	}	
}