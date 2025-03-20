package ec.gob.ambiente.control.retce.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.retce.model.DescargasLiquidas;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.GestorDesechosPeligrosos;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ProcesoRetceController {
	
	private static final Logger LOG = Logger.getLogger(ProcesoRetceController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	private static final String TIPO_TRAMITE_REVISION_PROYECTO="revisionProyecto";		
	private static final String TIPO_TRAMITE_EMISIONES_ATMOSFERICAS="emisionesAtmosfericas";
	private static final String TIPO_TRAMITE_DESCARGAS_LIQUIDAS="descargas";
	private static final String TIPO_TRAMITE_GENERADOR_DESECHOS="generadorDesechos";
	private static final String TIPO_TRAMITE_GESTOR_DESECHOS="gestorDesechos";
	private static final String[] TIPO_AREA={"","pc","dp","ea","","","dp"}; //para el tipo de area oficina tecnica se toma los roles de dp
	
	private boolean iniciarProceso(String tramite, String tipoTramite,Area area,TipoSector sector){
		try {
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("usuario_operador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("tramite", tramite);	
			parametros.put("tipo_tramite", "informacionBasica");
			parametros.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, JsfUtil.getLoggedUser().getNombre());

			Usuario usuarioCoordinador = new Usuario();
			Usuario usuarioTecnico = new Usuario();
			if(tipoTramite.equals(TIPO_TRAMITE_REVISION_PROYECTO)){
				//Revisar Informacion Basica
				usuarioTecnico=buscarTecnicoRevisionProyecto(area, sector);				
				if(usuarioTecnico==null){
					LOG.error("No se ha encontrado usuario tecnico en flujo RETCE en "+area.getAreaName());
					return false;
				}
				parametros.put("usuario_tecnico_revision_proyecto",usuarioTecnico.getNombre());
				parametros.put("requiere_revision_proyecto", true);
			}else{
				//Declaracion Reporte Retce
				parametros.put("tipo_tramite", tipoTramite);
				usuarioCoordinador=buscarCoordinadorRevision(area);				
				if(usuarioCoordinador==null){
					LOG.error("No se ha encontrado usuario coordinador en flujo RETCE en "+area.getAreaName());				
					return false;
				}
				String roleKeyTecnico="role.retce."+TIPO_AREA[area.getTipoArea().getId()]+".tecnico.revisar.tarea";
				
				if(tipoTramite.equals(TIPO_TRAMITE_GESTOR_DESECHOS))
					roleKeyTecnico+=".gestor";
				
				if(tipoTramite.equals(TIPO_TRAMITE_GENERADOR_DESECHOS))
					roleKeyTecnico+=".generador";
								
				parametros.put("usuario_coordinador",usuarioCoordinador.getNombre());
				parametros.put("role_key_tecnico",roleKeyTecnico);
				
			}
			
			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.NOMBRE_PROCESO_REGISTRO_CONTAMINANTES,tramite, parametros);			
			notificacion(tipoTramite.equals(TIPO_TRAMITE_REVISION_PROYECTO)?usuarioTecnico:usuarioCoordinador,  tramite,tipoTramite.equals(TIPO_TRAMITE_REVISION_PROYECTO));
			
			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
			return false;
		}
	}
	private boolean iniciarProceso(String tramite, String tipoTramite,Area area){
		return iniciarProceso(tramite,tipoTramite,area,null);
	}
	public boolean iniciarProceso(InformacionProyecto informacionProyecto){
		return iniciarProceso(informacionProyecto.getCodigo(),TIPO_TRAMITE_REVISION_PROYECTO,informacionProyecto.getAreaSeguimiento(),informacionProyecto.getTipoSector());
	}	
	public boolean iniciarProceso(DescargasLiquidas descargasLiquidas){
		return iniciarProceso(descargasLiquidas.getCodigo(), TIPO_TRAMITE_DESCARGAS_LIQUIDAS,descargasLiquidas.getInformacionProyecto().getAreaSeguimiento());
	}
	public boolean iniciarProceso(GeneradorDesechosPeligrososRetce generador){
		Area areaTramite = areaFacade.getArea(generador.getIdArea());
		return iniciarProceso(generador.getCodigoGenerador(), TIPO_TRAMITE_GENERADOR_DESECHOS, areaTramite);
	}
	public boolean iniciarProceso(GestorDesechosPeligrosos gestor){
		Area areaTramite = areaFacade.getArea(gestor.getArea());
		return iniciarProceso(gestor.getCodigo(), TIPO_TRAMITE_GESTOR_DESECHOS, areaTramite);
	}
	public boolean iniciarProceso(EmisionesAtmosfericas emisionAtmosferica){
		return iniciarProceso(emisionAtmosferica.getCodigo(), TIPO_TRAMITE_EMISIONES_ATMOSFERICAS,emisionAtmosferica.getInformacionProyecto().getAreaSeguimiento());
	}
	
	private Usuario buscarTecnicoRevisionProyecto(Area area,TipoSector sector){
		
		String roleKey="role.retce."+TIPO_AREA[area.getTipoArea().getId()]+".tecnico.revision.proyecto";		
		if(area.getTipoArea().getId()==1){			
			area=areaFacade.getArea(253);//DNCA			
			if(sector.getId()==1 || sector.getId()==2 || sector.getId()==4){
				roleKey+=".sector."+sector.getId();
			}
		}
		
		try {
			return areaFacade.getUsuarioPorRolArea(roleKey,area);
		} catch (Exception e) {
			LOG.error("No se ha encontrado usuario con en rol "+roleKey+" en el area "+area.getAreaName());			
		}
		
		return null;
	}
	
	private Usuario buscarCoordinadorRevision(Area area){
		String roleKey="role.retce."+TIPO_AREA[area.getTipoArea().getId()]+".coordinador.asignar.tarea";
		try {
			return areaFacade.getUsuarioPorRolArea(roleKey,area);
		} catch (Exception e) {
			LOG.error("No se ha encontrado usuario con en roleKey "+roleKey+" en el area "+area.getAreaName());			
		}
		
		return null;
	}
	
	public Usuario buscarAutoridadFirma(Area area,Boolean aprobado){
		String roleKey="role.retce."+TIPO_AREA[area.getTipoArea().getId()]+".autoridad.firmar.pronunciamiento";
		if(area.getTipoArea().getId()==1){
			roleKey+=aprobado?".aprobado":".observado";
			Area areaControl = areaFacade.getAreaSiglas(Constantes.SIGLAS_DIRECCION_NORMATIVA_CONTROL);
			area=areaFacade.getArea(aprobado?253:areaControl.getId());//SCA o DNCA			
		}else if(area.getTipoArea().getId()==6){
			area = area.getArea();
		}		
		try {			
			return usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area).get(0);
		} catch (Exception e) {
			LOG.error("No se ha encontrado usuario con en roleKey "+roleKey+" en el area "+area.getAreaName());			
		}
		
		return null;
	}
	
	//Usuario Para Notificacion
	public List<Usuario> buscarUsuariosNotificacionDeclaracionRetce(Area area){
		if(area.getTipoArea().getId()==1){			
			area=areaFacade.getArea(253);//DNCA			
		}
		
		List<Usuario> usuariosNotifica=new ArrayList<Usuario>();
		String roleKey="role.retce."+TIPO_AREA[area.getTipoArea().getId()]+".tecnico.desechos";		
		try {			
			Usuario usuario = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area).get(0);
			if(usuario!=null)
				usuariosNotifica.add(usuario);
		} catch (Exception e) {
			LOG.error("No se ha encontrado usuario con en roleKey "+roleKey+" en el area "+area.getAreaName());			
		}
		
		roleKey=roleKey.replace("tecnico","coordinador");
		try {			
			Usuario usuario = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area).get(0);
			if(usuario!=null)
				usuariosNotifica.add(usuario);
		} catch (Exception e) {
			LOG.error("No se ha encontrado usuario con en roleKey "+roleKey+" en el area "+area.getAreaName());			
		}
		
		return usuariosNotifica;
	}
	
	private void notificacion(Usuario usuario, String tramite,boolean revisionProyecto){
		try {
			String nombreOperador = JsfUtil.getNombreOperador(JsfUtil.getLoggedUser(),JsfUtil.getLoggedUser().getNombre().length() == 13?organizacionFacade.buscarPorRuc(JsfUtil.getLoggedUser().getNombre()):null);
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(revisionProyecto?"bodyNotificacionIngresoTramiteProyectoRetce":"bodyNotificacionIngresoTramiteRetce", new Object[]{nombreOperador, tramite});
			Email.sendEmail(usuario, "Ingreso Trámite RETCE", mensaje, tramite, JsfUtil.getLoggedUser());
		} catch (Exception e) {
			LOG.error("No se pudo enviar la notificacion al usuario "+usuario+" del tramite "+tramite);
		}		
	}
}
