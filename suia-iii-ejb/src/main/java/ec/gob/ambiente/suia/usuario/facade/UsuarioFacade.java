package ec.gob.ambiente.suia.usuario.facade;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang.SystemUtils;

import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.FormasContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.NacionalidadFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.administracion.facade.RolFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.AuditarUsuario;
import ec.gob.ambiente.suia.domain.AuditoriaSuplantacion;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Nacionalidad;
import ec.gob.ambiente.suia.domain.NotificacionesMails;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Rol;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.TipoArea;
import ec.gob.ambiente.suia.domain.TipoUsuario;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.EntityUsuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.tareas.programadas.EnvioMailCliente;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.service.UsuarioServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * *
 * <p/>
 * <b> Facade para la entidad Usuario</b>
 * 
 * @author pganan
 * @version Revision: 1.0
 *          <p>
 *          [Autor: pganan, Fecha: 22/12/2014]
 *          </p>
 */
@Stateless
public class UsuarioFacade {

	@EJB
	private UsuarioServiceBean usuarioServiceBean;
	@EJB
	private RolFacade rolFacade;
	@EJB
	private FormasContactoFacade formasContactoFacade;
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private EnvioMailCliente envioMailCliente;

	@EJB
	private IntegracionFacade integracionFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;

	
	@EJB
	private NacionalidadFacade nacionalidadFacade;
	
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	public String dblinkSuiaHidro=Constantes.getDblinkBpmsHyD();
	
	public String dblinkSuiaIII=Constantes.getDblinkBpmsSuiaiii();
	
	/**
	 * <b> Lista los usuarios registrados. </b>
	 * 
	 * @return
	 * @author pganan
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: pganan, Fecha: 22/12/2014]
	 *          </p>
	 */
	public List<Usuario> listarUsuarios() {
		List<Usuario> usuarios = usuarioServiceBean.listarUsuarios();
		return usuarios;
	}

	/**
	 * <b> Buscar usuarios por nombre. </b>
	 * 
	 * @param nombre
	 * @return Usuario
	 * @author pganan
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: carlos.pupo, Fecha: 22/12/2014]
	 *          </p>
	 */
	public Usuario buscarUsuario(String nombre) {
		return usuarioServiceBean.buscarUsuario(nombre);
	}
	
	public Usuario buscarUsuarioUrl(String nombre) {
		return usuarioServiceBean.buscarUsuarioUrl(nombre);
	}

	/**
	 * <b> Buscar usuarios por nombre. </b>
	 * 
	 * @param nombre
	 * @return Usuario
	 * @author caros.pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: carlos.pupo, Fecha: 22/12/2014]
	 *          </p>
	 */
	public Usuario buscarUsuarioWithOutFilters(String nombre) {
		return usuarioServiceBean.buscarUsuarioWithOutFilters(nombre);
	}

	/**
	 * <b> Método que permite buscar usuarios por rol. </b>
	 * 
	 * @param rol
	 * @return Lista de Usuarios
	 * @author pganan
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: pganan, Fecha: 22/12/2014]
	 *          </p>
	 */
	public List<Usuario> buscarUsuarioPorRol(String rol) {
		List<Usuario> usuarios = usuarioServiceBean.buscarUsuarioPorRol(rol);
		return usuarios;
	}

	public List<Usuario> buscarUsuarioPorRolNombreArea(String rol, String nombreArea) {
		List<Usuario> usuarios = usuarioServiceBean.buscarUsuarioPorRolNombreArea(rol, nombreArea);
		return usuarios;
	}
	
	public List<Usuario> buscarUsuarioPorRolNombreAreaUnico(String rol, String nombreArea, Integer usuarioId) throws ServiceException {
		List<Usuario> usuarios = usuarioServiceBean.buscarUsuarioPorRolNombreAreaUnico(rol, nombreArea,usuarioId);
		return usuarios;
	}

	public List<Usuario> buscarUsuarioPorRolNombreArea(List<String> rol, String nombreArea) {
		List<Usuario> usuarios = usuarioServiceBean.buscarUsuarioPorRolNombreArea(rol, nombreArea);
		return usuarios;
	}

	public Usuario getDirectorProvincialArea(Area area) {    
        Usuario director = null;
        if (area != null) {
            if(area.getTipoArea().getId().equals(3))
            {
                director = buscarUsuariosPorRolYArea(Constantes.ROL_DIRECTOR_EA, area).get(0);
            }
            else if(area.getTipoArea().getId().equals(2)){
                director = buscarUsuariosPorRolYArea(Constantes.ROL_DIRECTOR_PROVINCIAL, area).get(0);
            }
            else
            {
                director = buscarUsuarioPorRol(Constantes.ROL_DIRECTOR).get(0);    
                return director;
            }
        }
        return director;
    }	
	
	public List<EntityUsuario> listarUsuarioEntity() throws ServiceException {
		return usuarioServiceBean.listarUsuarioEntity();
	}
	
	public List<EntityUsuario> listarUsuarioEntityListarAdmin() throws ServiceException {
		return usuarioServiceBean.listarUsuarioEntityListarAdmin();
	}
	
	
	public List<EntityUsuario> listarUsuarioEntityReasigancion() throws ServiceException {
		return usuarioServiceBean.listarUsuarioEntityReasigancion();
	}
	
		
	public List<EntityUsuario> listarUsuarioEntityReasignacion(String nombreArea) throws ServiceException {
		return usuarioServiceBean.listarUsuarioEntityReasignacion(nombreArea);
	}
	
	
	public List<EntityUsuario> listarUsuarioEntityAdministrar(String nombreArea) throws ServiceException {
		return usuarioServiceBean.listarUsuarioEntityAdministrar(nombreArea);
	}

	public List<EntityUsuario> listarUsuariosCompletoEntity() throws ServiceException {
		return usuarioServiceBean.listarUsuariosCompletoEntity();
	}

	public List<EntityUsuario> listarUsuariosCompletoEntity(Integer inicio, Integer cantidad, String nombre,
			String nombreCompleto) throws ServiceException {
		return usuarioServiceBean.listarUsuariosCompletoEntity(inicio, cantidad, nombre, nombreCompleto);
	}

	public Integer contarUsuariosCompletoEntity(String nombre, String nombreCompleto) {
		return usuarioServiceBean.contarUsuariosCompletoEntity(nombre, nombreCompleto);
	}

	public Usuario buscarUsuarioPorId(Integer idUsuario) throws ServiceException {
		return usuarioServiceBean.buscarUsuarioPorId(idUsuario);
	}

	public Usuario buscarUsuarioPorIdFull(Integer idUsuario) throws ServiceException {
		try{
			Usuario u = usuarioServiceBean.buscarUsuarioPorId(idUsuario);
			u.getPersona().getOrganizaciones().size();
			for (Organizacion org : u.getPersona().getOrganizaciones()) {
				org.getContactos().size();
				for (Contacto c : org.getContactos()) {
					c.getFormasContacto().getId();
				}
			}
			u.getPersona().getTipoTratos().getId();
			u.getPersona().getUbicacionesGeografica().getId();
			return u;
		}catch(Exception e){
			return null;
		}		
	}

//	public void guardarAsignacionRol(List<Rol> listaRoles, Usuario usuario,String descripcion) throws ServiceException {
//		try {
//			for (Rol rol : listaRoles) {
////				if (rol.getUnicoPorProvincia() && validaRolUnico(rol.getId(), usuario.getId()) > 0) {
//				if (!(rol.getNombre().equals("sujeto de control"))){
//				List<Usuario> usuarios=buscarUsuarioPorRolNombreAreaUnico(rol.getNombre(), usuario.getArea().getAreaName(),usuario.getId());
//				if (!rol.getAutoridadUso().contains("PC")){
//					if (rol.getUnicoPorProvincia() != null && rol.getUnicoPorProvincia()  && (usuarios.size()>0)) {
//						throw new ServiceException(": " + rol.getDescripcion() + " no puede ser asignado más de una vez");
//					}
//				}else{
////					if (validaRolUnico(rol.getId(), usuario.getId()) > 0) {
////					if (usuarios.size()>0){
//					if (rol.getUnicoPorProvincia()  && (usuarios.size()>0)) {
//						throw new ServiceException(": " + rol.getDescripcion() + " no puede ser asignado más de una vez");
//					}
//				}
//				}
//			}
//			rolFacade.eliminarRolUsuario(usuario);
//			eliminarRolesUsuarioVerde(usuario.getNombre());
//								
//			List<RolUsuario> listaRol = new ArrayList<RolUsuario>();
//			for (Rol rol : listaRoles) {
//				RolUsuario ru = new RolUsuario();
//				ru.setRol(rol);
//				ru.setUsuario(usuario);
//				if(rol.getNombre().compareTo(AreaFacade.JEFE_AREA)==0)
//					ru.setDescripcion(descripcion);
//				listaRol.add(ru);
//				if (usuario.getArea().getTipoArea().getId()!=3){
//					Integer idRol=0;
//					if (rol.getNombre().equals("sujeto de control")){
//						idRol=25;
//					}else{
//						idRol=idRolesUsuarioVerde(rol.getNombre());
//					}
//					
//					if (usuario.getArea().getAreaName().equals("COORDINACIÓN GENERAL DE ASESORIA JURIDICA")||
//							usuario.getArea().getAreaName().equals("DIRECCIÓN NACIONAL DE BIODIVERSIDAD")||
//							usuario.getArea().getAreaName().equals("DIRECCIÓN NACIONAL FORESTAL")||
//							usuario.getArea().getAreaName().equals("DIRECCIÓN NACIONAL DE PREVENCIÓN DE LA CONTAMINACIÓN AMBIENTAL")||
//							usuario.getArea().getAreaName().equals("SUBSECRETARIA DE CALIDAD AMBIENTAL")||
//							usuario.getArea().getAreaName().equals("DIRECCIÓN NACIONAL DE PREVENCIÓN DE LA CONTAMINACIÓN AMBIENTAL")||
//							usuario.getArea().getAreaName().equals("DIRECCIÓN NACIONAL DE BIODIVERSIDAD")||
//							usuario.getArea().getAreaName().equals("DIRECCIÓN NACIONAL FORESTAL")){
//						if (idRol!=null){
//							insertarRolesUsuarioVerde(usuario.getNombre(), idRol);
//						}
//					}else {
//						if (!(rol.getNombre().equals("AUTORIDAD AMBIENTAL MAE")||
//								rol.getNombre().equals("DIRECTOR JURÍDICO MAE")||
//								rol.getNombre().equals("TÉCNICO SOCIAL MAE")||
//								rol.getNombre().equals("ASESOR DEL DESPACHO MINISTERIAL")||
//								rol.getNombre().equals("SUBSECRETARIO DE CALIDAD AMBIENTAL")||
//								rol.getNombre().equals("MINISTRO")||
//								rol.getNombre().equals("TÉCNICO HIDROCARBUROS")||
//								rol.getNombre().equals("DIRECTOR FORESTAL")||
//								rol.getNombre().equals("TÉCNICO FORESTAL")||
//								rol.getNombre().equals("DIRECTOR BIODIVERSIDAD")||
//								rol.getNombre().equals("TÉCNICO BIODIVERSIDAD")||
//								rol.getNombre().equals("COORDINADOR MINISTERIO DE JUSTICIA")||
//								rol.getNombre().equals("COORDINADOR HIDROCARBUROS")||
//								rol.getNombre().equals("TÉCNICO BIÓTICO")||
//								rol.getNombre().equals("TÉCNICO CARTOGRAFO")||
//								rol.getNombre().equals("ASESOR LEGAL SCA"))){
//							if (idRol!=null){
//								insertarRolesUsuarioVerde(usuario.getNombre(), idRol);
//							}
//						}												
//					}									
//				}else{
//					if (rol.getNombre().equals("VISUALIZACIÓN GAD")){
//						Integer idRol=idRolesUsuarioVerde(rol.getNombre());
//						if (idRol!=null){
//							insertarRolesUsuarioVerde(usuario.getNombre(), idRol);
//						}
//					}
//				}
//			}
//			crudServiceBean.saveOrUpdate(listaRol);
//		} catch (RuntimeException e) {
//			throw new ServiceException(e);
//		}
//	}

	public void guardar(Usuario usuario) throws ServiceException {
		if(usuario.getNumeroSesiones() == null) {
			usuario.setNumeroSesiones(1);
		}
		crudServiceBean.saveOrUpdate(usuario);
	}

	public void guardar(Usuario usuario, NotificacionesMails notificacionesMail, boolean usaurioExterno)
			throws ServiceException {
		if(usuario.getNumeroSesiones() == null) {
			usuario.setNumeroSesiones(1);
		}
		if (usuario.getId() == null) {
			envioMailCliente.enviarMensaje(notificacionesMail);
		}
		
		Usuario uPersist = new Usuario();
		try {
			uPersist = crudServiceBean.saveOrUpdate(usuario);	
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if (usaurioExterno) {
			RolUsuario ru = new RolUsuario();
			ru.setRol(rolFacade.obtenerRolProponente());
			ru.setUsuario(uPersist);
			crudServiceBean.saveOrUpdate(ru);
		}
	}

	public void modificar(Usuario usuario) throws ServiceException {
		crudServiceBean.saveOrUpdate(usuario);
	}
	
	public void modificarAuditoriAUsuario(AuditarUsuario usuario) throws ServiceException {
		crudServiceBean.saveOrUpdate(usuario);
	}

	public void modificar(Usuario usuario, List<Contacto> listaContactoRemover) throws ServiceException {
		crudServiceBean.saveOrUpdate(usuario);
		crudServiceBean.saveOrUpdate(listaContactoRemover);
	}

	public void modificar(Usuario usuario, NotificacionesMails notificacionesMail) throws ServiceException {
		crudServiceBean.saveOrUpdate(usuario);
		envioMailCliente.enviarMensaje(notificacionesMail);
	}
	
	public void notificarFiscal(NotificacionesMails notificacionesMail) throws ServiceException {
		envioMailCliente.enviarMensaje(notificacionesMail);
	}

	public Integer validaRolUnico(Integer idRol, Integer idUsuario) throws ServiceException {
		return usuarioServiceBean.validaRolUnico(idRol, idUsuario).intValue();
	}

	public List<RolUsuario> listarPorLoginPassword(final String login, final String password) throws ServiceException {
		return usuarioServiceBean.listarPorLoginPassword(login, password);
	}

	public List<Usuario> buscarUsuariosPorRolYArea(String rol, Area area) {
		return usuarioServiceBean.buscarUsuariosPorRolYArea(rol, area);
	}
	
	public List<Usuario> buscarUsuariosPorRolYAreaAleatorio(String rol, Area area) {
		return usuarioServiceBean.buscarUsuariosPorRolYAreaAleatorio(rol, area);
	}
	
	public List<Usuario> buscarUsuarios(String rol, String descripcion) {
		return usuarioServiceBean.buscarUsuarios(rol,descripcion);
	}

	/**
	 * Retorna el listado de los usuarios que pertenecen a un área específica
	 * 
	 * @param area
	 * @return
	 */
	public List<Usuario> buscarUsuariosPorArea(Area area) {
		return usuarioServiceBean.buscarUsuariosPorArea(area);
	}

	public Usuario buscarJefeInmediatoSuperior(String nombreUsuario) {
		return usuarioServiceBean.buscarUsuarioJefeInmediato(nombreUsuario);
	}

	public Usuario buscarUsuarioResponsableArea(Area area) throws ServiceException {
		List<Usuario> usuarios = usuarioServiceBean.buscarUsuarioResponsableArea(area);
		if (usuarios.isEmpty())
			throw new ServiceException("No se encontró un responsable para el área: " + area.getAreaName());
		if (usuarios.size() > 1)
			throw new ServiceException("Se encontró más de un responsable para el área: " + area.getAreaName());
		return usuarios.get(0);
	}

	public Boolean esUsuarioUnicoResponsableArea(Usuario usuario) {
		List<Usuario> usuarios = new ArrayList<Usuario>();
		
		for(AreaUsuario areaU : usuario.getListaAreaUsuario()){
			usuarios.addAll(usuarioServiceBean.buscarUsuarioResponsableArea(areaU.getArea()));
		}
		
//		List<Usuario> usuarios = usuarioServiceBean.buscarUsuarioResponsableArea(usuario.getArea());
		if (usuarios.isEmpty())
			return true;
		else if (usuarios.get(0).getId() != usuario.getId()) {
			return false;
		}
		return true;
	}
	
	public List<Usuario> esUsuarioUnicoResponsableAreaLista(Usuario usuario) {
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		
		for(AreaUsuario areaU : usuario.getListaAreaUsuario()){
			usuarios.addAll(usuarioServiceBean.buscarUsuarioResponsableArea(areaU.getArea()));
		}
		
//		List<Usuario> usuarios = usuarioServiceBean.buscarUsuarioResponsableArea(usuario.getArea());
		return usuarios;
	}
	
	public List<Usuario> esUsuarioUnicoResponsableAreaListaMAAE(Usuario usuario) {
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		
		for(AreaUsuario areaU : usuario.getListaAreaUsuario()){
			usuarios.addAll(usuarioServiceBean.buscarUsuarioResponsableArea(areaU.getArea()));
		}
		
//		List<Usuario> usuarios = usuarioServiceBean.buscarUsuarioResponsableAreaMAAE(usuario.getArea());
		return usuarios;
	}
	
	public List<Usuario> buscarUsuariosPorRolYTipoArea(String rol, TipoArea tipoArea) {
		return usuarioServiceBean.buscarUsuariosPorRolYTipoArea(rol, tipoArea);
	}

	public String obtenerCedulaNaturalJuridico(Integer idUsuario) throws ServiceException {
		Usuario usuario = usuarioServiceBean.buscarUsuarioPorId(idUsuario);
		if (usuario.getPersona().getOrganizaciones().isEmpty()) {
			return usuario.getPin();
		} else {
			return usuario.getPersona().getPin();
		}

	}

	public List<EntityUsuario> listarUsuarioSujetoControl() throws ServiceException {
		return usuarioServiceBean.listarUsuarioSujetoControl();
	}

	public List<RolUsuario> listarPorIdUsuario(final Integer idUsuario) throws ServiceException {
		return usuarioServiceBean.listarPorIdUsuario(idUsuario);
	}

	public void guardarSuplantacion(final AuditoriaSuplantacion auditoriaSuplantacion) throws ServiceException {
		usuarioServiceBean.guardarSuplantacion(auditoriaSuplantacion);
	}

	public void ejecutarMigracionUsuario() throws Exception {
		if (Constantes.getAppIntegrationSuiaEnabled() && Constantes.getAppIntegrationSuiaETLEnabled()) {
			try {
				String pentaho = Constantes.getAppIntegrationPentahoDir();
				String fileToRun = pentaho + "work_files/Users/replicar_usuarios_internos.kjb";

				String linux = pentaho + "kitchen.sh";
				String[] linuxArguments = new String[] { " -file=" + fileToRun, " -level=Minimal" };

				String windows = pentaho + "Kitchen.bat";
				String[] windowsArguments = new String[] { " /file:" + fileToRun, " /level:Minimal" };

				if (SystemUtils.IS_OS_WINDOWS)
					integracionFacade.executeExternalCommand(windows, windowsArguments);
				else
					integracionFacade.executeExternalCommand(linux, linuxArguments);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public Usuario buscarUsuarioCompleta(String nombre) {
		return usuarioServiceBean.buscarUsuarioCompleta(nombre);
	}

	public List<Usuario> buscarUsuarioPorNombres(List<String> nombres) {
		return usuarioServiceBean.buscarUsuarioPorNombres(nombres);
	}

	public String getEmailFromUser(String user) {
		String queryString = "SELECT c.cont_value FROM contacts c WHERE c.cont_id in ( "
				+ "  SELECT DISTINCT c.cont_id FROM users u INNER JOIN people p on u.peop_id = p.peop_id "
				+ "    INNER JOIN organizations o on p.peop_id = o.peop_id INNER JOIN  contacts c on  o.orga_id = c.orga_id "
				+ "    INNER JOIN  contacts_forms fc on c.cofo_id = fc.cofo_id "
				+ "  WHERE u.user_name= :userName and c.cont_status = true and fc.cofo_id = :tipoContacto "
				+ ") or  c.cont_id in ( "
				+ "  SELECT DISTINCT c.cont_id  FROM users u INNER JOIN  people p on u.peop_id = p.peop_id "
				+ "    INNER JOIN contacts c on c.pers_id = p.peop_id INNER JOIN  contacts_forms fc "
				+ "      on c.cofo_id = fc.cofo_id where u.user_name = :userName and fc.cofo_id = :tipoContacto "
				+ ") LIMIT 1";

		try {
			Query query = crudServiceBean.getEntityManager().createNativeQuery(queryString);
			query.setParameter("userName", user);
			query.setParameter("tipoContacto", FormasContacto.EMAIL);
			List<?> result = query.getResultList();
			if (result != null && !result.isEmpty())
				return query.getResultList().get(0).toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public String getUserNameFromEmail(String email) {
		String queryString = "SELECT u.user_name FROM users u WHERE u.user_id in ( "
				+ "  SELECT DISTINCT u.user_id FROM users u INNER JOIN people p on u.peop_id = p.peop_id "
				+ "    INNER JOIN organizations o on p.peop_id = o.peop_id INNER JOIN  contacts c on  o.orga_id = c.orga_id "
				+ "    INNER JOIN  contacts_forms fc on c.cofo_id = fc.cofo_id "
				+ "  WHERE c.cont_value = :email and fc.cofo_id = :tipoContacto " + ") or  u.user_id in ( "
				+ "  SELECT DISTINCT u.user_id FROM users u INNER JOIN  people p on u.peop_id = p.peop_id "
				+ "    INNER JOIN contacts c on c.pers_id = p.peop_id INNER JOIN  contacts_forms fc "
				+ "      on c.cofo_id = fc.cofo_id where c.cont_value = :email and fc.cofo_id = :tipoContacto "
				+ ") LIMIT 1";

		try {
			Query query = crudServiceBean.getEntityManager().createNativeQuery(queryString);
			query.setParameter("email", email);
			query.setParameter("tipoContacto", FormasContacto.EMAIL);
			List<?> result = query.getResultList();
			if (result != null && !result.isEmpty())
				return query.getResultList().get(0).toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<String> getUserNamesOrEmails(String pattern) {
		String stringQuery1 = "SELECT DISTINCT user_name FROM users WHERE lower(user_name) LIKE lower('" + pattern
				+ "%')";
		String stringQuery2 = "SELECT DISTINCT cont_value FROM contacts WHERE cofo_id = " + FormasContacto.EMAIL
				+ " AND lower(cont_value) LIKE lower('" + pattern + "%')";

		List<String> resultData = new ArrayList<String>();

		try {
			Query query1 = crudServiceBean.getEntityManager().createNativeQuery(stringQuery1);
			Query query2 = crudServiceBean.getEntityManager().createNativeQuery(stringQuery2);
			List<?> result1 = query1.getResultList();
			List<?> result2 = query2.getResultList();
			if (result1 != null && !result1.isEmpty()) {
				for (Object object : result1) {
					resultData.add(object.toString());
				}
			}
			if (result2 != null && !result2.isEmpty()) {
				for (Object object : result2) {
					resultData.add(object.toString());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultData;
	}
	
	public void actualizarContraseniaVerde(String pass, String usuario) {
		 String sql="select dblink_exec('"+dblinkSuiaVerde+"','update usuario set password=''"+pass+"'' where nombreusuario=''"+usuario+"''') as result";
         Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
         if(query.getResultList().size()>0)
             query.getSingleResult();
		
	}
	
	public void actualizarEstadoUsuarioVerde(boolean estado, boolean funcionario, boolean plantaCentral,boolean encargado, String usuario) {
		 String sql="select dblink_exec('"+dblinkSuiaVerde+"','update usuario set estadousuario=''"+estado+"'', funcionario =''"+funcionario+"'', plantacentral =''"+plantaCentral+"'' , encargado =''"+encargado+"'' where nombreusuario=''"+usuario+"''') as result";
        Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
        if(query.getResultList().size()>0)
            query.getSingleResult();		
	}
	
	
	public void eliminarRolesUsuarioVerde(String usuario) {//		
		Integer usuInteger=null;
		String sqlusua="select id from dblink('"+dblinkSuiaVerde+"','select id from usuario where nombreusuario =''"+usuario+"''') as t1 (id character varying(255))";		
		Query queryusua =  crudServiceBean.getEntityManager().createNativeQuery(sqlusua);
		if(queryusua.getResultList().size()>0){
			usuInteger= Integer.valueOf((String) queryusua.getSingleResult());
			String sql="select dblink_exec('"+dblinkSuiaVerde+"','delete from rolusuario where usuarioid = ''"+usuInteger+"''')";
	       Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
	       query.getSingleResult();	
		}
       
	}
	
	public void insertarRolesUsuarioVerde(String usuario,Integer rolusuario) {
		Integer usuInteger=null;
		String sqlusua="select id from dblink('"+dblinkSuiaVerde+"','select id from usuario where nombreusuario =''"+usuario+"''') as t1 (id character varying(255))";		
		Query queryusua =  crudServiceBean.getEntityManager().createNativeQuery(sqlusua);
		if(queryusua.getResultList().size()>0)
			usuInteger= Integer.valueOf((String) queryusua.getSingleResult());
		if (usuInteger!=null){
		String sql = "select dblink_exec('"
				+ dblinkSuiaVerde
				+ "',' insert into rolusuario (usuarioid, rolid, aprobado) values(''"+usuInteger+"'',''"+rolusuario+"'',''true'')')";
			Query query = crudServiceBean.getEntityManager().createNativeQuery(
					sql);
			query.getSingleResult();
		}		
	}
	
	public Integer idRolesUsuarioVerde(String rol) {
		String sql="select id from dblink('"+dblinkSuiaVerde+"','select id from rol where nombre =''"+rol+"''') as t1 (id character varying(255))";		
		Query query =  crudServiceBean.getEntityManager().createNativeQuery(sql);
		if(query.getResultList().size()>0)
			return Integer.valueOf((String) query.getSingleResult());
		return null;     
	}
	
	public void insertarPersonaVerde(String pin, String titulo,String tratamiento,String primerapellido,String genero,Integer nacionalidad_id,
			String idTratamiento,Integer parroquia, String password, List<Contacto> contactos, Area areaUsuario, boolean token, boolean estadoUsuario,boolean funcionario,boolean plantacentral,boolean encargado) throws ServiceException {

		Query queryusua=null;
			Nacionalidad nacionalidad = nacionalidadFacade.buscarPorId(nacionalidad_id);
			UbicacionesGeografica ubicacion= ubicacionGeograficaFacade.buscarPorId(parroquia);
			
			String sqlNacionalidad="select id from dblink('"+dblinkSuiaVerde+"','select id from nacionalidad where descripcion =''"+nacionalidad.getDescripcion()+"''') as t1 (id character varying(255))";		
			Query queryNacionalidad =  crudServiceBean.getEntityManager().createNativeQuery(sqlNacionalidad);		
			
			String sqlParroquia="select id from dblink('"+dblinkSuiaVerde+"','select p.id from parroquia p , canton c where p.canton=c.id and c.nombre=''"+ubicacion.getUbicacionesGeografica().getNombre()+"''  and p.nombre =''"+ubicacion.getNombre()+"''') as t1 (id character varying(255))";		
			Query queryParroquia =  crudServiceBean.getEntityManager().createNativeQuery(sqlParroquia);
			
			String sqlTratamiento="select tto from dblink('"+dblinkSuiaVerde+"','select tto from tipotratamiento where id =''"+tratamiento+"''') as t1 (tto character varying(255))";		
			Query queryTratamiento =  crudServiceBean.getEntityManager().createNativeQuery(sqlTratamiento);
			
			if(queryNacionalidad.getResultList().size()>0 && queryParroquia.getResultList().size()>0 && queryTratamiento.getResultList().size()>0)		{
				String sqlPersona="select id from dblink('"+dblinkSuiaVerde+"','select id from persona where pin =''"+pin+"''') as t1 (id character varying(255))";		
				Query queryPersonabuscar =  crudServiceBean.getEntityManager().createNativeQuery(sqlPersona);
				if (!(queryPersonabuscar.getResultList().size()>0)){
				
				
				//inserta persona
				String tratamientoPersona=(String) queryTratamiento.getSingleResult();
				String sqlpersona = "select dblink_exec('"
						+ dblinkSuiaVerde
						+ "','INSERT INTO persona(id, pin, titulo, tratamiento, primerapellido,segundoapellido, primernombre, segundonombre, genero, nacionalidad_id,tipotratamiento, nombresapellidos, parroquia)"
						+ " values(nextval(''hibernate_sequence''),''"+pin+"'',''"+titulo+"'', ''"+tratamientoPersona.toUpperCase()+"'', ''"+primerapellido+"'','' '', '' '', '' '', ''"+genero+"'', ''"+Integer.valueOf((String) queryNacionalidad.getSingleResult())+"'',''"+idTratamiento+"'', ''"+primerapellido+"'',''"+ Integer.valueOf((String) queryParroquia.getSingleResult())+"'')')";
					Query queryPersona = crudServiceBean.getEntityManager().createNativeQuery(
							sqlpersona);
					queryPersona.getSingleResult();
				}else{
					//actualiza persona
					 String sqlActualizaPersona="select dblink_exec('"+dblinkSuiaVerde+"','update persona set titulo=''"+titulo+"'', parroquia =''"+Integer.valueOf((String) queryParroquia.getSingleResult())+"'', nacionalidad_id =''"+Integer.valueOf((String) queryNacionalidad.getSingleResult())+"'' where pin=''"+pin+"''') as result";
				        Query queryActualizaPersona = crudServiceBean.getEntityManager().createNativeQuery(sqlActualizaPersona);
				        if(queryActualizaPersona.getResultList().size()>0)
				        	queryActualizaPersona.getSingleResult();	
				}
					
					Integer area=null;
					String areaAbreviacion;
					if (areaUsuario!=null){
						if (areaUsuario.getTipoArea().getId()!=3){
							if (areaUsuario==null){
								areaAbreviacion="PTE";
							}else{
								areaAbreviacion=areaUsuario.getAreaAbbreviation();
								if (areaUsuario.getAreaAbbreviation().contains("SUB-")){
									String datos[]=areaAbreviacion.split("SUB-");
									areaAbreviacion=datos[1];
								}
							}
						}else{
							areaAbreviacion="ENTES";
						}
					}else{
						areaAbreviacion="PTE";
					}
					
					
					String sqlArea="select id from dblink('"+dblinkSuiaVerde+"','select id from area where abreviacion =''"+areaAbreviacion+"''') as t1 (id character varying(255))";		
					Query queryArea =  crudServiceBean.getEntityManager().createNativeQuery(sqlArea);
					
					String sqlAreaNombre="select nombre from dblink('"+dblinkSuiaVerde+"','select nombre from area where abreviacion =''"+areaAbreviacion+"''') as t1 (nombre character varying(255))";		
					Query queryAreaNombre =  crudServiceBean.getEntityManager().createNativeQuery(sqlAreaNombre);
					
					if(queryArea.getResultList().size()>0 && queryAreaNombre.getResultList().size()>0 ){
						
						area=Integer.valueOf((String) queryArea.getSingleResult());
						String sqlSubarea="select id from dblink('"+dblinkSuiaVerde+"','select id from subarea where nombre=''"+queryAreaNombre.getSingleResult()+"'' and area_id =''"+area+"''') as t1 (id character varying(255))";		
						Query querySubarea =  crudServiceBean.getEntityManager().createNativeQuery(sqlSubarea);	
						Integer SubAreas=null;
						if (!(querySubarea.getResultList().size()>0)){
							SubAreas=area;
						}else{
							SubAreas=Integer.valueOf((String) querySubarea.getSingleResult());
						}			
						String sqlusua="select entidad_id from dblink('"+dblinkSuiaVerde+"','select entidad_id from usuario where nombreusuario =''"+pin+"''') as t1 (entidad_id character varying(255))";		
						queryusua =  crudServiceBean.getEntityManager().createNativeQuery(sqlusua);
//						if(!(queryusua.getResultList().size()>0)){
						
						if(!(queryusua.getResultList().size()>0) ){
							//inserta usuario
							String sqlusuario = "select dblink_exec('"
									+ dblinkSuiaVerde
									+ "','INSERT INTO usuario(id, nombreusuario, password, temporalpassword, fechacreacion,"
									+ " tipoentidad, datoscompletos, justificacionaccesos, parroquia, entidad_id, area,"
									+ " subarea, token,estadousuario,funcionario,plantacentral,encargado)"
									+ " values(nextval(''hibernate_sequence''),''"+pin+"'',''"+password+"'', ''false'', ''now()'',''ec.fugu.ambiente.model.organizacion.Persona '', ''true'', ''ACCESO POR MIGRACION''"
											+ ", ''"+Integer.valueOf((String) queryParroquia.getSingleResult())+"'', ''"+Integer.valueOf((String) queryPersonabuscar.getSingleResult())+"'',''"+area+"'', ''"+SubAreas+"'',''"+token+"'',''"+estadoUsuario+"''"
													+ ",''"+funcionario+"'',''"+plantacentral+"'',''"+encargado+"'' )')";
								Query queryUsuario = crudServiceBean.getEntityManager().createNativeQuery(
										sqlusuario);
								queryUsuario.getSingleResult();
						}else{
							//actualiza usuario
							 String sqlActualizaUsuario="select dblink_exec('"+dblinkSuiaVerde+"','update usuario set funcionario= ''"+funcionario+"'' ,plantacentral= ''"+plantacentral+"'',encargado=''"+encargado+"'',  estadousuario=''"+estadoUsuario+"'', area=''"+area+"'', subarea =''"+SubAreas+"'', parroquia =''"+Integer.valueOf((String) queryParroquia.getSingleResult())+"'' where nombreusuario=''"+pin+"''') as result";
						        Query queryActualizaUsuario = crudServiceBean.getEntityManager().createNativeQuery(sqlActualizaUsuario);
						        if(queryActualizaUsuario.getResultList().size()>0)
						        	queryActualizaUsuario.getSingleResult();	
							
						}
						}
						//inserta contactos		
					String sqlusuaContactos="select id from dblink('"+dblinkSuiaVerde+"','select id from contacto where entidad =''"+Integer.valueOf((String) queryusua.getSingleResult())+"''') as t1 (id character varying(255))";		
					Query queryusuaContactos =  crudServiceBean.getEntityManager().createNativeQuery(sqlusuaContactos);
					if (queryusuaContactos.getResultList().size()>0){
						String sql="select dblink_exec('"+dblinkSuiaVerde+"','delete from contacto where entidad = ''"+Integer.valueOf((String) queryusua.getSingleResult())+"''')";
					       Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
					       query.getSingleResult();	
					}
					
						for (Contacto contacto : contactos) {
							String valorContacto=null;
							FormasContacto formaContacto= formasContactoFacade.buscarPorId(contacto.getFormasContacto().getId());
							if (formaContacto.getNombre().contains("*EMAIL")){
								valorContacto="EMAIL";
							}else if (formaContacto.getNombre().contains("*DIRECCIÓN")){
								valorContacto="DIRECCION";
							}else if (formaContacto.getNombre().contains("*TELÉFONO")){
								valorContacto="TELEFONO";
							}else if (formaContacto.getNombre().contains("*CELULAR")){
								valorContacto="CELULAR";
							}else{
								valorContacto= formaContacto.getNombre();
							}
							
							
								String sqlContactos="select dblink_exec('"
										+ dblinkSuiaVerde
										+ "','INSERT INTO contacto(id, valor, tipocontacto, entidad)"
										+ " values(nextval(''hibernate_sequence''),''"+contacto.getValor()+"'',''"+valorContacto+"'', ''"+Integer.valueOf((String) queryusua.getSingleResult())+"'')')";
									Query queryContactos = crudServiceBean.getEntityManager().createNativeQuery(
											sqlContactos);
									queryContactos.getSingleResult();	
							}
			}else{
				
			}
	}
	
	public void insertarOrganizacionVerde(String pin, String titulo,String tratamiento,String primerapellido,String idTratamiento,
			Integer parroquia,String ruc,String nombreOrganizacion,String posicion,String tipoOrganizacion,Integer parroquiaorga,Area areaUsuario, String password, List<Contacto> contactos, 
			boolean token, boolean estadoUsuario,boolean funcionario,boolean plantacentral,boolean encargado) throws ServiceException {

		Query queryusua=null;			
			UbicacionesGeografica ubicacion= ubicacionGeograficaFacade.buscarPorId(parroquia);
			
			String sqlParroquia="select id from dblink('"+dblinkSuiaVerde+"','select p.id from parroquia p , canton c where p.canton=c.id and c.nombre=''"+ubicacion.getUbicacionesGeografica().getNombre()+"''  and p.nombre =''"+ubicacion.getNombre()+"''') as t1 (id character varying(255))";		
			Query queryParroquia =  crudServiceBean.getEntityManager().createNativeQuery(sqlParroquia);
			
			String sqlTratamiento="select tto from dblink('"+dblinkSuiaVerde+"','select tto from tipotratamiento where id =''"+tratamiento+"''') as t1 (tto character varying(255))";		
			Query queryTratamiento =  crudServiceBean.getEntityManager().createNativeQuery(sqlTratamiento);
			
			if( queryParroquia.getResultList().size()>0 && queryTratamiento.getResultList().size()>0)		{
				String sqlPersona="select id from dblink('"+dblinkSuiaVerde+"','select id from persona where pin =''"+pin+"''') as t1 (id character varying(255))";		
				Query queryPersonabuscar =  crudServiceBean.getEntityManager().createNativeQuery(sqlPersona);
				if (!(queryPersonabuscar.getResultList().size()>0)){
								
				//inserta persona
				String tratamientoPersona=(String) queryTratamiento.getSingleResult();
				String sqlpersona = "select dblink_exec('"
						+ dblinkSuiaVerde
						+ "','INSERT INTO persona(id, pin, titulo, tratamiento, primerapellido,segundoapellido, primernombre, segundonombre,  tipotratamiento, nombresapellidos, parroquia)"
						+ " values(nextval(''hibernate_sequence''),''"+pin+"'',''"+titulo+"'', ''"+tratamientoPersona.toUpperCase()+"'', ''"+primerapellido+"'','' '', '' '', '' '',''"+idTratamiento+"'', ''"+primerapellido+"'',''"+ Integer.valueOf((String) queryParroquia.getSingleResult())+"'')')";
					Query queryPersona = crudServiceBean.getEntityManager().createNativeQuery(
							sqlpersona);
					queryPersona.getSingleResult();
				}else{
					//actualiza persona
					 String sqlActualizaPersona="select dblink_exec('"+dblinkSuiaVerde+"','update persona set titulo=''"+titulo+"'', parroquia =''"+Integer.valueOf((String) queryParroquia.getSingleResult())+"'' where pin=''"+pin+"''') as result";
				        Query queryActualizaPersona = crudServiceBean.getEntityManager().createNativeQuery(sqlActualizaPersona);
				        if(queryActualizaPersona.getResultList().size()>0)
				        	queryActualizaPersona.getSingleResult();	
				}
					//insertar o actualizar la organización
				String idPersona = null;
				String sqlPersonaOr="select id from dblink('"+dblinkSuiaVerde+"','select id from persona where pin =''"+pin+"''') as t1 (id character varying(255))";		
				Query queryPersonaOrbuscar =  crudServiceBean.getEntityManager().createNativeQuery(sqlPersonaOr);
				if ((queryPersonaOrbuscar.getResultList().size()>0)){
					idPersona=queryPersonaOrbuscar.getResultList().get(0).toString();
				}
				
				String sqlOrganizacion="select id from dblink('"+dblinkSuiaVerde+"','select id from organizacion where pin =''"+ruc+"''') as t1 (id character varying(255))";		
				Query queryOrganizacionbuscar =  crudServiceBean.getEntityManager().createNativeQuery(sqlOrganizacion);
				if (!(queryOrganizacionbuscar.getResultList().size()>0)){
					//inserta organización
					String tratamientoPersona=(String) queryTratamiento.getSingleResult();
					String sqlpersona = "select dblink_exec('"
							+ dblinkSuiaVerde
							+ "','INSERT INTO organizacion(id, pin, nombre, cargo, tipoorganizacion, representante, parroquia)"
							+ " values(nextval(''hibernate_sequence''),''"+ruc+"'',''"+nombreOrganizacion+"'', ''"+posicion.toUpperCase()+"'', ''"+tipoOrganizacion+"'', ''"+idPersona+"'',''"+ Integer.valueOf((String) queryParroquia.getSingleResult())+"'')')";
						Query queryPersona = crudServiceBean.getEntityManager().createNativeQuery(
								sqlpersona);
						queryPersona.getSingleResult();
				}else{
					//actualiza organización
					 String sqlActualizaPersona="select dblink_exec('"+dblinkSuiaVerde+"','update organizacion set nombre=''"+nombreOrganizacion+"'', cargo=''"+posicion.toUpperCase()+"'',tipoorganizacion=''"+tipoOrganizacion+"'',representante=''"+idPersona+"'', parroquia =''"+Integer.valueOf((String) queryParroquia.getSingleResult())+"'' where pin=''"+ruc+"''') as result";
				        Query queryActualizaPersona = crudServiceBean.getEntityManager().createNativeQuery(sqlActualizaPersona);
				        if(queryActualizaPersona.getResultList().size()>0)
				        	queryActualizaPersona.getSingleResult();	
				}
				String IdRud=null;
				String sqlOrganizacionusuario="select id from dblink('"+dblinkSuiaVerde+"','select id from organizacion where pin =''"+ruc+"''') as t1 (id character varying(255))";		
				Query queryOrganizacionusuariobuscar =  crudServiceBean.getEntityManager().createNativeQuery(sqlOrganizacionusuario);
				if ((queryOrganizacionusuariobuscar.getResultList().size()>0)){
					IdRud=queryOrganizacionusuariobuscar.getResultList().get(0).toString();
				}		
				
					Integer area=null;
					String areaAbreviacion;
					if (areaUsuario==null){
						areaAbreviacion="PTE";
					}else{
						areaAbreviacion=areaUsuario.getAreaAbbreviation();
						if (areaUsuario.getAreaAbbreviation().contains("SUB-")){
							String datos[]=areaAbreviacion.split("SUB-");
							areaAbreviacion=datos[1];
						}
					}
					
//					
					String sqlArea="select id from dblink('"+dblinkSuiaVerde+"','select id from area where abreviacion =''"+areaAbreviacion+"''') as t1 (id character varying(255))";		
					Query queryArea =  crudServiceBean.getEntityManager().createNativeQuery(sqlArea);
					
					String sqlAreaNombre="select nombre from dblink('"+dblinkSuiaVerde+"','select nombre from area where abreviacion =''"+areaAbreviacion+"''') as t1 (nombre character varying(255))";		
					Query queryAreaNombre =  crudServiceBean.getEntityManager().createNativeQuery(sqlAreaNombre);
					
					if(queryArea.getResultList().size()>0 && queryAreaNombre.getResultList().size()>0 ){
						
						area=Integer.valueOf((String) queryArea.getSingleResult());
						String sqlSubarea="select id from dblink('"+dblinkSuiaVerde+"','select id from subarea where nombre=''"+queryAreaNombre.getSingleResult()+"'' and area_id =''"+area+"''') as t1 (id character varying(255))";		
						Query querySubarea =  crudServiceBean.getEntityManager().createNativeQuery(sqlSubarea);	
						Integer SubAreas=null;
						if (!(querySubarea.getResultList().size()>0)){
							SubAreas=area;
						}else{
							SubAreas=Integer.valueOf((String) querySubarea.getSingleResult());
						}			
						String sqlusua="select entidad_id from dblink('"+dblinkSuiaVerde+"','select entidad_id from usuario where nombreusuario =''"+ruc+"''') as t1 (entidad_id character varying(255))";		
						queryusua =  crudServiceBean.getEntityManager().createNativeQuery(sqlusua);
//						if(!(queryusua.getResultList().size()>0)){
						
						if(!(queryusua.getResultList().size()>0) ){
							//inserta usuario
							String sqlusuario = "select dblink_exec('"
									+ dblinkSuiaVerde
									+ "','INSERT INTO usuario(id, nombreusuario, password, temporalpassword, fechacreacion,"
									+ " tipoentidad, datoscompletos, justificacionaccesos, parroquia, entidad_id, area,"
									+ " subarea,token,estadousuario,funcionario,plantacentral,encargado)"
									+ " values(nextval(''hibernate_sequence''),''"+ruc+"'',''"+password+"'', ''false'', ''now()'',''ec.fugu.ambiente.model.organizacion.Organizacion'', ''true'', ''ACCESO POR MIGRACION''"
											+ ", ''"+Integer.valueOf((String) queryParroquia.getSingleResult())+"'', ''"+IdRud+"'',''"+area+"'', ''"+SubAreas+"'',''"+token+"'',''"+estadoUsuario+"''"
													+ ",''"+funcionario+"'',''"+plantacentral+"'',''"+encargado+"'' )')";
	
								Query queryUsuario = crudServiceBean.getEntityManager().createNativeQuery(
										sqlusuario);
								queryUsuario.getSingleResult();
						}else{
							//actualiza usuariow
							 String sqlActualizaUsuario="select dblink_exec('"+dblinkSuiaVerde+"','update usuario set funcionario= ''"+funcionario+"'' ,plantacentral= ''"+plantacentral+"'',encargado=''"+encargado+"'',  estadousuario=''"+estadoUsuario+"'', area=''"+area+"'', entidad_id =''"+IdRud+"'',subarea =''"+SubAreas+"'', parroquia =''"+Integer.valueOf((String) queryParroquia.getSingleResult())+"'' where nombreusuario=''"+ruc+"''') as result";
						        Query queryActualizaUsuario = crudServiceBean.getEntityManager().createNativeQuery(sqlActualizaUsuario);
						        if(queryActualizaUsuario.getResultList().size()>0)
						        	queryActualizaUsuario.getSingleResult();	
							
						}
						}
						//inserta contactos		
					String sqlusuaContactos="select id from dblink('"+dblinkSuiaVerde+"','select id from contacto where entidad =''"+Integer.valueOf((String) queryusua.getSingleResult())+"''') as t1 (id character varying(255))";		
					Query queryusuaContactos =  crudServiceBean.getEntityManager().createNativeQuery(sqlusuaContactos);
					if (queryusuaContactos.getResultList().size()>0){
						String sql="select dblink_exec('"+dblinkSuiaVerde+"','delete from contacto where entidad = ''"+Integer.valueOf((String) queryusua.getSingleResult())+"''')";
					       Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
					       query.getSingleResult();	
					}
					
						for (Contacto contacto : contactos) {
							String valorContacto=null;
							FormasContacto formaContacto= formasContactoFacade.buscarPorId(contacto.getFormasContacto().getId());
							if (formaContacto.getNombre().contains("*EMAIL")){
								valorContacto="EMAIL";
							}else if (formaContacto.getNombre().contains("*DIRECCIÓN")){
								valorContacto="DIRECCION";
							}else if (formaContacto.getNombre().contains("*TELÉFONO")){
								valorContacto="TELEFONO";
							}else if (formaContacto.getNombre().contains("*CELULAR")){
								valorContacto="CELULAR";
							}else{
								valorContacto= formaContacto.getNombre();
							}
							
							
								String sqlContactos="select dblink_exec('"
										+ dblinkSuiaVerde
										+ "','INSERT INTO contacto(id, valor, tipocontacto, entidad)"
										+ " values(nextval(''hibernate_sequence''),''"+contacto.getValor()+"'',''"+valorContacto+"'', ''"+Integer.valueOf((String) queryusua.getSingleResult())+"'')')";
									Query queryContactos = crudServiceBean.getEntityManager().createNativeQuery(
											sqlContactos);
									queryContactos.getSingleResult();	
							}
			}else{
				
			}
	}
	
	
	public Integer consultaTaskhyd4cat (String codigo) {
		Integer resultado=0;
		String consultaTaskhyd="select count (dbid_ )from dblink('"+dblinkSuiaVerde+"', "
				+ "'select dbid_ from  jbpm4_task where assignee_ = ''"+codigo+"'''"
				+ ") t1 (dbid_ integer)";		    			
		List<BigInteger> lista = crudServiceBean.getEntityManager().createNativeQuery(consultaTaskhyd).getResultList();	
		if (!(lista.get(0).intValue()==0)){
			resultado=1;
		}		    		
		return resultado;
		
	}
	
	public Integer consultaTaskhyd(String codigo) {
		Integer resultado=0;	    			
		String consultaTaskhyd="select count (id )from dblink('"+dblinkSuiaHidro+"', "
				+ "'select id from  task where (status=''InProgress'' or status=''Reserved'') and actualowner_id= ''"+codigo+"'''"
				+ ") t1 (id integer)";		    			
		List<BigInteger> lista = crudServiceBean.getEntityManager().createNativeQuery(consultaTaskhyd).getResultList();
		if (!(lista.get(0).intValue()==0)){
			resultado=1;
		}		    		
		return resultado;
		
	}
	
	public Integer consultaTaskhydSuiaiii (String codigo) {
		Integer resultado=0;	    			
		String consultaTaskhyd="select count (id )from dblink('"+dblinkSuiaIII+"', "
				+ "'select id from  task where (status=''InProgress'' or status=''Reserved'') and actualowner_id= ''"+codigo+"'''"
				+ ") t1 (id integer)";		    			
		List<BigInteger> lista = crudServiceBean.getEntityManager().createNativeQuery(consultaTaskhyd).getResultList();
		if (!(lista.get(0).intValue()==0)){
			resultado=1;
		}		    		
		return resultado;
	}
	
	public Object[] getUsuarioSuiaVerde (String usuario){
		String sqlPproyecto="select * from dblink('"+dblinkSuiaVerde+"',"
				+ "'select nombreusuario, nombresapellidos, o.nombre from usuario u "
				+ "left join persona p on u.nombreusuario = p.pin "
				+ "left join organizacion o on u.nombreusuario = o.pin  where u.nombreusuario=''"+usuario+"''') "
						+ "as t1 (usuario character varying(255), nombre character varying(255), organizacion character varying(255))";		
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		if(queryProyecto.getResultList().size()>0){
			List<Object>  resultPro = new ArrayList<Object>();
    		resultPro=queryProyecto.getResultList();
    		return (Object[]) resultPro.get(0);
		}
		return null;
	}	

	public Usuario buscarResponsableArea(Area area) throws ServiceException {
		List<Usuario> usuarios = usuarioServiceBean.buscarUsuarioResponsableArea(area);
		if (usuarios.isEmpty())
			throw new ServiceException("No se encontró un responsable para el área: " + area.getAreaName());
		if (usuarios.size() > 1) {
			for (Usuario usuario : usuarios) {
				if(usuario.getRolUsuarios().size() > 0)
					return usuario;
			}
			
			throw new ServiceException("No se encontró un responsable para el área: " + area.getAreaName());
		}
		return usuarios.get(0);
	}
	
	//SD
	public List<Usuario> buscarUsuariosPorRolTipoArea(String rol, TipoArea tipoArea) {
		return usuarioServiceBean.buscarUsuariosPorRolTipoArea(rol, tipoArea);
	}
	
	public List<Usuario> buscarUsuariosPorRolYAreaEspecifica(String rol, Area area) {
		return usuarioServiceBean.buscarUsuariosPorRolYAreaEspecifica(rol, area);
	}
	
	public List<Usuario> buscarUsuarioPorRolActivo(String rol) {
		List<Usuario> usuarios = usuarioServiceBean.buscarUsuarioPorRolActivo(rol);
		return usuarios;
	}
	
	//validación para varias áreas
	public void guardarAsignacionRolAreas(List<Rol> listaRoles, List<Area> listaAreas,String descripcion, Usuario usuario) throws ServiceException {
		try {
			for (Rol rol : listaRoles) {

				if (!(rol.getNombre().equals("sujeto de control"))){
					
					for(Area areas : listaAreas){
						List<Usuario> usuarios=buscarUsuarioPorRolNombreAreaUnico(rol.getNombre(), areas.getAreaName(),usuario.getId());
						if (!rol.getAutoridadUso().contains("PC")){
							if (rol.getUnicoPorProvincia() != null && rol.getUnicoPorProvincia()  && (usuarios.size()>0)) {
								throw new ServiceException(": " + rol.getDescripcion() + " no puede ser asignado más de una vez");
							}
						}else{
							if (rol.getUnicoPorProvincia()  && (usuarios.size()>0)) {
								throw new ServiceException(": " + rol.getDescripcion() + " no puede ser asignado más de una vez");
							}
						}						
					}
									
				}
			}
			rolFacade.eliminarRolUsuario(usuario);
			eliminarRolesUsuarioVerde(usuario.getNombre());
								
			List<RolUsuario> listaRol = new ArrayList<RolUsuario>();
			for (Rol rol : listaRoles) {
				RolUsuario ru = new RolUsuario();
				ru.setRol(rol);
				ru.setUsuario(usuario);
				if(rol.getNombre().compareTo(AreaFacade.JEFE_AREA)==0)
					ru.setDescripcion(descripcion);
				listaRol.add(ru);
				
				TipoArea tipoArea = listaAreas.get(0).getTipoArea();
				
				if (tipoArea.getId()!=3){
					Integer idRol=0;
					if (rol.getNombre().equals("sujeto de control")){
						idRol=25;
					}else{
						idRol=idRolesUsuarioVerde(rol.getNombre());
					}
					
					if (validadArea(listaAreas)){
						if (idRol!=null){
							insertarRolesUsuarioVerde(usuario.getNombre(), idRol);
						}
					}else {
						if (!(rol.getNombre().equals("AUTORIDAD AMBIENTAL MAE")||
								rol.getNombre().equals("DIRECTOR JURÍDICO MAE")||
								rol.getNombre().equals("TÉCNICO SOCIAL MAE")||
								rol.getNombre().equals("ASESOR DEL DESPACHO MINISTERIAL")||
								rol.getNombre().equals("SUBSECRETARIO DE CALIDAD AMBIENTAL")||
								rol.getNombre().equals("MINISTRO")||
								rol.getNombre().equals("TÉCNICO HIDROCARBUROS")||
								rol.getNombre().equals("DIRECTOR FORESTAL")||
								rol.getNombre().equals("TÉCNICO FORESTAL")||
								rol.getNombre().equals("DIRECTOR BIODIVERSIDAD")||
								rol.getNombre().equals("TÉCNICO BIODIVERSIDAD")||
								rol.getNombre().equals("COORDINADOR MINISTERIO DE JUSTICIA")||
								rol.getNombre().equals("COORDINADOR HIDROCARBUROS")||
								rol.getNombre().equals("TÉCNICO BIÓTICO")||
								rol.getNombre().equals("TÉCNICO CARTOGRAFO")||
								rol.getNombre().equals("ASESOR LEGAL SCA"))){
							if (idRol!=null){
								insertarRolesUsuarioVerde(usuario.getNombre(), idRol);
							}
						}												
					}									
				}else{
					if (rol.getNombre().equals("VISUALIZACIÓN GAD")){
						Integer idRol=idRolesUsuarioVerde(rol.getNombre());
						if (idRol!=null){
							insertarRolesUsuarioVerde(usuario.getNombre(), idRol);
						}
					}
				}
			}
			crudServiceBean.saveOrUpdate(listaRol);
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}
	
	public boolean validadArea(List<Area> listaAreas){
		
		boolean areaIgual = false;
		for(Area area: listaAreas){
			if (area.getAreaName().equals("COORDINACIÓN GENERAL DE ASESORIA JURIDICA")||
					area.getAreaName().equals("DIRECCIÓN NACIONAL DE BIODIVERSIDAD")||
					area.getAreaName().equals("DIRECCIÓN NACIONAL FORESTAL")||
					area.getAreaName().equals("DIRECCIÓN NACIONAL DE PREVENCIÓN DE LA CONTAMINACIÓN AMBIENTAL")||
					area.getAreaName().equals("SUBSECRETARIA DE CALIDAD AMBIENTAL")||
					area.getAreaName().equals("DIRECCIÓN NACIONAL DE PREVENCIÓN DE LA CONTAMINACIÓN AMBIENTAL")||
					area.getAreaName().equals("DIRECCIÓN NACIONAL DE BIODIVERSIDAD")||
					area.getAreaName().equals("DIRECCIÓN NACIONAL FORESTAL") ||
					area.getAreaAbbreviation().equals(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL) ||
					area.getAreaAbbreviation().equals(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS) ||
					area.getAreaAbbreviation().equals(Constantes.SIGLAS_DIRECCION_BOSQUES)){
				areaIgual = true;
				break;
			}
		}
		
		return areaIgual;
		
	}
	
	public List<String> recuperarNombreOperador(Usuario usuarioOperador){
		List<String> datosOperador = new ArrayList<>();
		
		String nombreOperador = "";
		String nombreRepresentante = "";
		String tratamientoOperador = "";
		String cedulaResponsable = "";
		String nombreOrganizacion= "";
		
		try {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());

			if (organizacion != null) {
				nombreOperador = organizacion.getNombre();
				nombreRepresentante = usuarioOperador.getPersona().getNombre();
				cedulaResponsable = usuarioOperador.getPersona().getPin();
				nombreOrganizacion = organizacion.getNombre();
			} else {
				nombreOperador = usuarioOperador.getPersona().getNombre();
				tratamientoOperador = usuarioOperador.getPersona().getTipoTratos().getNombre();
				cedulaResponsable = usuarioOperador.getPersona().getPin();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		datosOperador.add(0, nombreOperador);
		datosOperador.add(1, nombreRepresentante);
		datosOperador.add(2, tratamientoOperador);
		datosOperador.add(3, cedulaResponsable);
		datosOperador.add(4, nombreOrganizacion);
		
		return datosOperador;
	}
	
	public Usuario buscarUsuarioSujetoControl(String nombre){
		
		return usuarioServiceBean.buscarUsuarioSujetoControl(nombre);
		
	}
	
	public Usuario buscarUsuarioPorIdDesactivado(Integer id){
		return usuarioServiceBean.buscarUsuarioPorIdDesactivado(id);
	}
	
	@SuppressWarnings("unchecked")
	public String getTipoUsuarioAutoridad(Usuario usuario) {
		Query sql = crudServiceBean.getEntityManager().createQuery("Select t from TipoUsuario t where usuario.id = :id and estado = true");
		sql.setParameter("id", usuario.getId());
		
		List<TipoUsuario> listaTipos = (ArrayList<TipoUsuario>) sql.getResultList();
		
		String cargoAdicional = "";
		if(listaTipos != null && !listaTipos.isEmpty()){
			if(listaTipos.get(0).getTipo().equals(1)){
				cargoAdicional = ", (SUBROGANTE)";
			}else{
				cargoAdicional = ", (ENCARGADO/A)";
			}
		}
		
		return cargoAdicional;
	}
	
}