/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.service.AreaService;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.TipoArea;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.retce.model.ProyectoInformacionUbicacionGeografica;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * @author christian
 */
@LocalBean
@Stateless
public class AreaFacade {

    private static final String ENTE_ACREDITADO = "EA";
    private static final String DIRECCION_PROVINCIAL = "DP";
//    private static final String DIRECTOR_SECRETARIA = "DIRECTOR SECRETARIA";
    private static final String DIRECTOR_PROVINCIAL = "AUTORIDAD AMBIENTAL";
    private static final String SUBSECRETARIO = "AUTORIDAD AMBIENTAL MAE";
    private static final String COORDINADOR_PROVINCIAL = "TÉCNICO REASIGNACIÓN COORDINADOR PROVINCIAL";
    private static final String COORDINADOR_PROVINCIAL_REGISTRO = "COORDINADOR PROVINCIAL DE REGISTRO";
    private static final String COORDINADOR_PROVINCIAL_PATRIMONIO = "COORDINADOR PROVINCIAL DE PATRIMONIO";
    public static final String JEFE_AREA = "JEFE DE AREA";//Rol Para Jefe de Area Protegida
    public static final String TEMP_CONST_NOMBRE_SECUENCIA_NUMERO = "seq_number_technical_report_eia";
    public static final String TEMP_CONST_NOMBRE_ESQUEMA_SUIA = "suia_iii";
    private static final String DIRECCION_ZONAL = "DZ";

    @EJB
    private AreaService areaService;

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private SecuenciasFacade secuenciasFacade;
    @EJB
    private InformeOficioFacade informeOficioFacade;
    
    @EJB
    private CrudServiceBean crudServiceBean;
    
    public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();
    
    public String dblinkIdeCartografia=Constantes.getDblinkIdeCartografia();

    public List<Area> listarAreasPadre() throws ServiceException {
        return areaService.listarAreasPadre();
    }
    
    public List<Area> getAreasAll() throws ServiceException {
        return areaService.getAreasAll();
    }

    public List<Area> listarAreasPadreInstitucional(Area areas) throws ServiceException {
        return areaService.listarAreasPadreInstitucional(areas);
    }
    
    public List<Area> listarAreasPadreInstitucionalTotal(Area areas) throws ServiceException {
        return areaService.listarAreasPadreInstitucionalTotal(areas);
    }
    
    
    public List<Area> listarAreasHijos(final Area areaPadre)
            throws ServiceException {
        return areaService.listarAreasHijos(areaPadre);
    }

    public Usuario getDirectorProvincial(Area direccion)
            throws ServiceException {
    	if(direccion.getAreaName().equals("SUBSECRETARIA DE CALIDAD AMBIENTAL") || direccion.getAreaName().equals("SUBSECRETARÍA DE CALIDAD AMBIENTAL"))
    	{
    		List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(
                    SUBSECRETARIO, direccion);
    		if (usuarios != null && !usuarios.isEmpty()) {
                for (Usuario usuario : usuarios) {
                	for (AreaUsuario areaUser : usuario.getListaAreaUsuario()) {
                		Area areaU = areaUser.getArea();
                		if(areaU.getAreaName().equals("SUBSECRETARIA DE CALIDAD AMBIENTAL") || areaU.getAreaName().equals("SUBSECRETARÍA DE CALIDAD AMBIENTAL"))
    						return usuario;
					}
				}
    			
            }
    	}
    	else
    	{
    		List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(
    				DIRECTOR_PROVINCIAL, direccion);
    		if (usuarios != null && !usuarios.isEmpty()) {
    			return usuarios.get(0);
    		}
    	}
        throw new ServiceException("No se encontró un usuario "
                + DIRECTOR_PROVINCIAL + " en el área "
                + direccion.getAreaName());
    }

    @Deprecated
    public Usuario getDirectorProvincial(UbicacionesGeografica ubicacion) {
        return usuarioFacade.buscarUsuariosPorRolYArea(DIRECTOR_PROVINCIAL,
                getDireccionProvincial(ubicacion)).get(0);
    }

    public Usuario getCoordinadorProvincial(Area direccion)
            throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(
                COORDINADOR_PROVINCIAL, direccion);
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario "
                + COORDINADOR_PROVINCIAL + " en el área "
                + direccion.getAreaName());
    }
    
    public Usuario getCoordinadorProvincialRegistro(Area direccion)
            throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(
                COORDINADOR_PROVINCIAL_REGISTRO, direccion);
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario "
                + COORDINADOR_PROVINCIAL_REGISTRO + " en el área "
                + direccion.getAreaName());
    }

    public Usuario getCoordinadorProvincial(UbicacionesGeografica ubicacion)
            throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(
                COORDINADOR_PROVINCIAL, getDireccionProvincial(ubicacion));
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario "
                + COORDINADOR_PROVINCIAL + " en la ubucación "
                + ubicacion.getNombre());
    }

    public List<Area> getDirecciones() {
        return areaService.getAreas(getTipoArea(DIRECCION_PROVINCIAL));

    }

    private TipoArea getTipoArea(String siglas) {
        TipoArea tipo = areaService.getTipoArea(siglas);
        return tipo;
    }

    /**
     * @Deprecated, Usar getAreaCoordinacionZonal
     */
    @Deprecated
    public Area getDireccionProvincial(
            UbicacionesGeografica ubicacionesGeografica) {
        return (Area) areaService.getArea(ubicacionesGeografica,
                getTipoArea(DIRECCION_PROVINCIAL));
    }
    
    public Area getAreaCoordinacionZonal(UbicacionesGeografica ubicacionesGeografica) {
        return (Area) areaService.getAreaCoordinacionZonal(ubicacionesGeografica);
    }

    public List<Area> getEntesAcreditados() {
        return areaService.getAreas(getTipoArea(ENTE_ACREDITADO));

    }

    public Area getArea(Integer idArea) {
        return areaService.getArea(idArea);
    }

    public Area getAreaPorId(Integer idArea) {
        return areaService.getAreaPorId(idArea);
    }
    
    public Area getArea(String nombreArea) {
        return areaService.getArea(nombreArea);
    }

    public Area getAreaFull(Integer idArea) {
        Area area = areaService.getArea(idArea);
        try {
            area.getArea().getId();
        } catch (Exception e) {

        }
        try { // area.getAreaList().size();
            area.getTipoArea().getId();
        } catch (Exception e) {

        }
        try {
            area.getUbicacionesGeografica().getId();
        } catch (Exception e) {

        }
        // area.getUsersList().size();
        return area;
    }

    public Area getAreaSiglas(String siglaArea) {
        return areaService.getAreaSiglas(siglaArea);
    }

    public Area getAreaTipoAreaUbicacion(TipoArea tipoArea,
                                         UbicacionesGeografica ubicacionesGeografica) {
        return areaService.getAreaTipoAreaUbicacion(tipoArea,
                ubicacionesGeografica);

    }

    public Usuario getViceministra() throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYTipoArea(
                Constantes.getRoleAreaName("role.viceministra"),
                getTipoArea(Constantes.getRoleAreaName("area.plantacentral")));
        if (usuarios.size() > 0) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario "
                + Constantes.getRoleAreaName("role.viceministra")
                + " en   Planta Central ");
    }

    public Usuario getMinistra() throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuarioPorRol(Constantes
                .getRoleAreaName("role.ministra"));
        if (usuarios.size() > 0) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario "
                + Constantes.getRoleAreaName("role.ministra")
                + " en Planta Central ");

    }

    public Usuario getDirectorPlantaCentralPorArea(String area)
            throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYTipoArea(
                Constantes.getRoleAreaName(area),
                getTipoArea(Constantes.getRoleAreaName("area.plantacentral")));
        if (usuarios.size() > 0) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario "
                + Constantes.getRoleAreaName(area) + " en Planta Central ");
    }

    public Usuario getDirectorPlantaCentralPorArea(String areaRole, Area area) {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(
                Constantes.getRoleAreaName(areaRole), area);
        if (usuarios.size() > 0) {
            return usuarios.get(0);
        }

        return null;
    }

    public Usuario getDirectorPlantaCentral(String areaRole)
            throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYTipoArea(
                Constantes.getRoleAreaName(areaRole),
                getTipoArea(Constantes.getRoleAreaName("area.plantacentral")));
        if (usuarios.size() > 0) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario "
                + Constantes.getRoleAreaName(areaRole) + " en Planta Central ");
    }

    /**
     * Retorna el coordinador de areas que no estén en Planta Central
     *
     * @param area Area donde se ubica el coordinador
     * @return
     */
    public Usuario getCoordinadorPorArea(Area area) throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(
                Constantes.getRoleAreaName("role.area.coordinador"), area);
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }

        throw new ServiceException("No se encontró un usuario "
                + Constantes.getRoleAreaName("role.area.coordinador")
                + " en el área " + area.getAreaName());
    }

    public Usuario getDirectorPorArea(Area area) throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(
                Constantes.getRoleAreaName("role.area.director"), area);
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }

        throw new ServiceException("No se encontró un usuario "
                + Constantes.getRoleAreaName("role.area.director")
                + " en el área " + area.getAreaName());
    }

    public Usuario geSubsecretariaPatrimonioNaturalPorArea(Area area) {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(
                Constantes.getRoleAreaName("role.area.subsecretario"), area);
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        return null;
    }

    public Usuario geSubsecretariaPatrimonioNatural() {
        List<Usuario> usuarios = usuarioFacade.buscarUsuarioPorRol(Constantes
                .getRoleAreaName("role.area.subsecretario"));
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        return null;
    }

    /**
     * Retorna el técnico de areas que no estén en Planta Central
     *
     * @param area Area donde se ubica el técnico
     * @return
     */
    public Usuario getTecnicoPorArea(Area area) throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(
                Constantes.getRoleAreaName("role.area.tecnico"), area);
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario "
                + Constantes.getRoleAreaName("role.area.tecnico")
                + " en el área " + area.getAreaName());
    }

    /**
     * Obtener usuarios por áreas en planta central
     *
     * @param area String del área ejemplo "role.pc.director"
     * @return
     */
    public Usuario getUsuarioPlantaCentralPorArea(String area)
            throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYTipoArea(
                Constantes.getRoleAreaName(area),
                getTipoArea(Constantes.getRoleAreaName("area.plantacentral")));
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario "
                + Constantes.getRoleAreaName(area) + " del área "
                + areaService.getPlantaCentral().getAreaName());

    }

    public Usuario getDirectorPlantaCentral() throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYTipoArea(
                Constantes.getRoleAreaName("role.pc.director"),
                getTipoArea(Constantes.getRoleAreaName("area.plantacentral")));
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario "
                + Constantes.getRoleAreaName("role.area.director"));

    }
    
    public Usuario getDirectorPrevencionPlantaCentral() throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYTipoArea(
                Constantes.getRoleAreaName("role.pc.director.prevencion"),
                getTipoArea(Constantes.getRoleAreaName("area.plantacentral")));
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario "
                + Constantes.getRoleAreaName("role.area.director"));

    }

    public Usuario getCoordinadorControlAmbientalPlantaCentral()
            throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYTipoArea(
                Constantes.getRoleAreaName("role.pc.coordinador"),
                getTipoArea(Constantes.getRoleAreaName("area.plantacentral")));
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario "
                + Constantes.getRoleAreaName("role.area.coordinador"));

    }

    public Usuario getSubsecretariaCalidadAmbiental() throws ServiceException {
        return getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental");
    }

    public Area getAreaDireccionProvincialPorUbicacion(Integer idTipo,
                                                       UbicacionesGeografica ubicacionesGeografica) {
        return areaService.getAreaDireccionProvincialPorUbicacion(idTipo,
                ubicacionesGeografica);

    }

    public Area getAreaEntePorUbicacion(Integer idTipo,
                                        UbicacionesGeografica ubicacionesGeografica) {
        return areaService.getAreaEntePorUbicacion(idTipo,
                ubicacionesGeografica);

    }
    
    public Area getAreaGadProvincialPorUbicacion(Integer idTipo,UbicacionesGeografica ubicacionesGeografica) {
    	return areaService.getAreaGadProvincial(idTipo,ubicacionesGeografica);
    }

    public Area getAreaEnteAcreditado(Integer idTipo,
                                      String userNameLogin) {
        return areaService.getAreaEnteAcreditado(idTipo, userNameLogin);
    }

    public Area getAreaPorAreaAbreviacion(String areaAbbreviation) {
        return areaService.getAreaPorAreaAbreviacion(areaAbbreviation);

    }

    public Usuario getCoordinadorPorSector(TipoSector tipoSector)
            throws ServiceException {
        String role = Constantes.getRoleAreaName("role.pc.coordinador.area."
                + tipoSector.getId().toString());
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYTipoArea(
                role,
                getTipoArea(Constantes.getRoleAreaName("area.plantacentral")));
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        throw new ServiceException("No se encontró un usuario " + role
                + " en planta central");

    }

    /**
     * Retorna el coordinador de areas que no estén en Planta Central
     *
     * @param area Area donde se ubica el coordinador
     * @return
     */
    @EJB
    private ProcesoFacade procesoFacade;
    
    public Usuario getUsuarioPorRolArea(String role, Area area)
            throws ServiceException {
        String roleName = Constantes.getRoleAreaName(role);
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(
                roleName, area);
        //--------------------------------------------
        List<Usuario> usuariosYCargas = new ArrayList<Usuario>();
        Integer pesoTareasUsuario;
        Integer pesoTotal = 0;        
        for (Usuario userName : usuarios) {
            pesoTareasUsuario = 0;
            Usuario user = usuarioFacade.buscarUsuario(userName.getNombre());
//          List<TaskSummary> userTaskList = procesoFacade.getAllTaskAssigned(user);
          Query sqlProcesos = crudServiceBean.getEntityManager().createNativeQuery("select * from  dblink('"+dblinkBpmsSuiaiii+"','select count(actualowner_id) as tareas from task where actualowner_id in(''"+userName.getNombre()+"'') and status in(''InProgress'',''Reserved'',''Created'',''Ready'') group by actualowner_id order by tareas') as (tareas integer)");
          try{
              Object result = sqlProcesos.getSingleResult();
              pesoTareasUsuario=(Integer) result;
          }
          catch(Exception e){}

//          for (TaskSummary task : userTaskList) {
//              //Si la tarea no tiene peso (Priority = 0) entonces le corresponde valor 1
//              if(task.getPriority() == 0){
//                  pesoTareasUsuario++;
//              }
//              else {
//                  pesoTareasUsuario += task.getPriority();
//              }
//          }
            pesoTotal += pesoTareasUsuario;
            user.setNumeroTramites(pesoTareasUsuario);
            user.setPesoTotalTareas(pesoTareasUsuario);
            usuariosYCargas.add(user);
        }
        for (Usuario userWorkload : usuariosYCargas) {
            if(pesoTotal != 0)
                userWorkload.setCarga(userWorkload.getPesoTotalTareas() * 100d / pesoTotal);
            else
                userWorkload.setCarga(0d);

            userWorkload.setNombrePersona(userWorkload.getPersona().getNombre());
        }
        Comparator<Usuario> comparadorUsuario = new Comparator<Usuario>() {
            @Override
            public int compare(Usuario usuario, Usuario t1) {
                if(usuario.getCarga() == t1.getCarga()) {
                    double resta = usuario.getCoeficienteRendimiento() - t1.getCoeficienteRendimiento();
                    if(resta > 0)
                        return -1;
                    else
                        return 1;
                }
                else
                    return (int) (usuario.getCarga() - t1.getCarga());
            }
        };
        Collections.sort(usuariosYCargas, comparadorUsuario);
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuariosYCargas.get(0);
        }
        
        //------------------------
//        if (usuarios != null && !usuarios.isEmpty()) {
//            return usuarios.get(0);
//        }
        throw new ServiceException("No se encontró un usuario " + roleName
                + " en el área " + area.getAreaName());
    }

    public List<Usuario> getUsuariosPorRolArea(String role, Area area) throws ServiceException {
        String roleName = Constantes.getRoleAreaName(role);
        return usuarioFacade.buscarUsuariosPorRolYArea(roleName, area);
    }

    /**
     * Retorna el responsable del area
     *
     * @param area Area donde se ubica el coordinador
     * @return
     */
    public Usuario getUsuarioResponsableArea(Area area) throws ServiceException {
        return usuarioFacade.buscarUsuarioResponsableArea(area);
    }

    public Area getAreaDireccionPorRango(Integer idUbicacion) {
    	return areaService.getAreaDireccionProvincialPorRango(idUbicacion);
    }

    public Area getAreaMunicipioPorRango(Integer idUbicacion) {
    	return areaService.getAreaDireccionMunicipioPorRango(idUbicacion);
    }
    public String getAreaUsuario(Usuario usuario) {
    	return  areaService.getAreaUsuario(usuario);
    }
    

	public Area getAreaEnteMunicipioPorUbicacion(Integer idTipo,
			UbicacionesGeografica ubicacionesGeografica) {
		return areaService.getAreaEnteMunicipioPorUbicacion(idTipo,
				ubicacionesGeografica);
	}

    public String generarNoResolucion(){
        String numeroResolucion = null;
        try {

            // numeroResolucion = Long.toString(secuenciasFacade.getNextValueDedicateSequence("numeroResolucionCategoriaII"));
            numeroResolucion = ""+informeOficioFacade.obtenerNumeroInforme(TEMP_CONST_NOMBRE_SECUENCIA_NUMERO, TEMP_CONST_NOMBRE_ESQUEMA_SUIA).toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return numeroResolucion;
    }

    public Area getareaFa(String nombre){
    	return (Area)areaService.getAreasFacil(nombre);
    }
    
    public Area getareaPafre(Integer area){
    	return (Area)areaService.getAreasPadre(area);
    }
    
    public Usuario getJefeArea(String nombreAreaProtegida) throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuarios(JEFE_AREA,"%"+nombreAreaProtegida.toUpperCase()+"%");
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        System.out.println("No se encontró un usuario " + JEFE_AREA + " en el área "+nombreAreaProtegida);
        throw new ServiceException("Ha ocurrido un error al guardar la información. Comuníquese con Mesa de Ayuda.");        
    }
    
    public Usuario getCoordinadorProvincialPatrimonio(Area area) throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(COORDINADOR_PROVINCIAL_PATRIMONIO, area);
        if (usuarios != null && !usuarios.isEmpty()) {
            return usuarios.get(0);
        } 
        System.out.println("No se encontró un usuario " + COORDINADOR_PROVINCIAL_PATRIMONIO + " en el área " + area.getAreaName());
        throw new ServiceException("Ha ocurrido un error al guardar la información. Comuníquese con Mesa de Ayuda.");
    }
        
    @SuppressWarnings("unchecked")
 	public List<String> getAreasProtegidas(){
     	try{
     		Query query = crudServiceBean.getEntityManager().createNativeQuery("SELECT UPPER(nombre) AS nombre FROM  dblink('"+dblinkIdeCartografia+"',"
                  		+ "'SELECT cmap_etiq||'' ''||nombre AS nombre,''AREA PROTEGIDA'' AS tipo FROM lan_demarcaciones.car_pane where subsistema=''ESTATAL''"
                  		+ " UNION"
                  		+ " SELECT ''RAMSAR AREA ''||nombre,''RAMSAR AREA'' AS tipo FROM lan_demarcaciones.car_ramsar_area"
                  		+ " UNION"
                  		+ " SELECT ''RAMSAR PUNTO ''||nombre,''RAMSAR PUNTO'' AS tipo FROM lan_demarcaciones.car_ramsar_punto')"
                		+ " as (nombre character varying,tipo character varying)"                		
                		+ " ORDER BY tipo,nombre");              
             List<String> result = query.getResultList();
             return result;
            }
            catch(Exception e){
          	  e.printStackTrace();
            }
            return new ArrayList<String>();
     }
        
	public String getRolDetalle(Usuario usuario,String rol){
    	try{
    		Query query = crudServiceBean.getEntityManager().createQuery("SELECT o.descripcion FROM RolUsuario o"               		
               		+ " where o.usuario.id =:idUsuario"
               		+ " and o.rol.nombre= :rol");              
            List<String> result = query.setParameter("idUsuario",usuario.getId()).setParameter("rol",rol).getResultList();
            String r= result.get(0);
            if(!r.isEmpty())
            	return r;
           }
           catch(Exception e){
         	  e.printStackTrace();
           }
           return null;
    }
	
	public Integer getRolDetalleAsignado(String detalle,String rol){
    	try{
    		Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM RolUsuario o"               		
               		+ " where o.descripcion like :detalle"
               		+ " and o.rol.nombre= :rol"
               		+ " and o.usuario.estado=true");            
            
            List<RolUsuario> r= (List<RolUsuario>)query.setParameter("detalle","%"+detalle+";%").setParameter("rol",rol).getResultList();
            if(!r.isEmpty())
            	return r.get(0).getIdUsuario();
           }
           catch(Exception e){
         	  e.printStackTrace();
           }
           return null;
    }
	
	public void guardar(Area area) throws ServiceException {
		crudServiceBean.saveOrUpdate(area);
	}
	
	@SuppressWarnings("unchecked")
	public List<Area> getAreasResponsables() {
		List<Area> lista = new ArrayList<Area>();
		try {
			lista = crudServiceBean
					.getEntityManager()
					.createQuery(
							" FROM Area a where a.estado = true and(a.id=257 or ((a.tipoArea.id=2 or a.tipoArea=3) and a.areaAbbreviation not like 'SUB%' and a.area = null and a.ubicacionesGeografica != null)) ORDER BY a.areaName")					
					.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Area> getAreasSeguimiento(UbicacionesGeografica ubicacionParroquia) {
		List<Area> lista = new ArrayList<Area>();		
		try {
			Area areaControl = getAreaSiglas(Constantes.SIGLAS_DIRECCION_NORMATIVA_CONTROL);
			
			lista = crudServiceBean
					.getEntityManager()
					.createQuery(
							  " FROM Area a where a.estado = true and(a.id="+areaControl.getId()+" or ((a.tipoArea.id=2 or a.tipoArea=3) and a.areaAbbreviation not like 'SUB%' and a.area = null and a.ubicacionesGeografica != null"
							+ " and(a.ubicacionesGeografica.codificacionInec like '"+ubicacionParroquia.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec()+"%' or a.areaName like '%"+ubicacionParroquia.getUbicacionesGeografica().getUbicacionesGeografica().getNombre()+"%' or a.areaName like '%"+ubicacionParroquia.getUbicacionesGeografica().getNombre()+"%')"
							+ ")) ORDER BY a.areaName")					
					.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<Area> getAreasSeguimientoPorParroquias(List<ProyectoInformacionUbicacionGeografica> ubicacionParroquia) {
		List<Area> lista = new ArrayList<Area>();
		String sql = " ";
		try {
			if(ubicacionParroquia.size() == 1){
				lista = getAreasSeguimiento(ubicacionParroquia.get(0).getUbicacionesGeografica());
			}else{
				List<String> listaprovincias = new ArrayList<String>();
				for (ProyectoInformacionUbicacionGeografica ubicacion : ubicacionParroquia) {
					if(!listaprovincias.contains(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre())){
						listaprovincias.add(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
					}
				}
				if(listaprovincias.size() == 1){
					lista = getAreasSeguimiento(ubicacionParroquia.get(0).getUbicacionesGeografica());
				}else if(listaprovincias.size() == 2){
					sql = " FROM Area a where a.estado = true and a.tipoArea.id=2 and a.areaAbbreviation not like 'SUB%' and a.area is null and a.ubicacionesGeografica is not null and ( ";
					String sqlOr="";
					for (String provincia : listaprovincias) {
						sqlOr  += (sqlOr.isEmpty()?"":" or ") +"a.areaName like  '%"+provincia+"%'" ;
					}
					sql += sqlOr + ") ORDER BY a.areaName asc" ;
				}if(listaprovincias.size() >= 3){
					sql = " FROM Area a where a.estado = true  and a.id in (257)  ";
					sql += " ORDER BY a.areaName asc" ;
				}
				if(!sql.isEmpty()){
					lista = crudServiceBean
							.getEntityManager()
							.createQuery(sql)
							.getResultList();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return lista;
	}

	public List<Usuario> getUsuariosPlantaCentralPorRol(String rol)
            throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYTipoArea(
                Constantes.getRoleAreaName(rol),
                getTipoArea(Constantes.getRoleAreaName("area.plantacentral")));
        if (usuarios.size() > 0) {
            return usuarios;
        }
        throw new ServiceException("No se encontró un usuario "
                + Constantes.getRoleAreaName(rol) + " en Planta Central ");
    }

	public Area getAreaWithOutFilters(Integer id) {
		return (Area) crudServiceBean.getEntityManagerWithOutFilters()
				.createQuery(" FROM Area a where a.id =:id ")
				.setParameter("id", id).getSingleResult();
    }
	
	public List<Area> listarAreasPadreOT() throws ServiceException {
        return areaService.listarAreasPadreOT();
    }
	
	public List<Area> listarAreasPadreSinOT() throws ServiceException {
        return areaService.listarAreasPadreSinOT();
    }
	
	@SuppressWarnings("unchecked")
	public List<Area> getAutoridadAmbientalCompetente() {
		List<Area> lista = new ArrayList<Area>();
		try {
			lista = crudServiceBean
					.getEntityManager()
					.createQuery(
							" FROM Area a where a.estado = true "
							+ "and(a.areaAbbreviation = 'DRA' or "
							+ "a.areaAbbreviation = 'PNG/DIR' or "
							+ "("
							+ " a.tipoArea=3 "
							+ " and a.areaAbbreviation not like 'SUB%' "
							+ " and a.area = null and a.ubicacionesGeografica != null"
							+ ") or a.tipoArea=6) ORDER BY a.areaName")
					.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Area> getAutoridadAmbientalMaae() {
		List<Area> lista = new ArrayList<Area>();
		try {
			lista = crudServiceBean
					.getEntityManager()
					.createQuery(
							" FROM Area a where a.estado = true "
							+ "and(a.areaAbbreviation = 'DRA' or "
							+ "a.areaAbbreviation = 'PNG/DIR' or "
							+ "a.tipoArea=6) ORDER BY a.areaName")
					.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Area> getAreasEmisorasAAA() {
		List<Area> lista = new ArrayList<Area>();
		try {
			lista = (List<Area>) crudServiceBean
					.getEntityManager()
					.createNativeQuery(
							" select * FROM Areas a where a.area_issue = true ORDER BY a.area_name", Area.class)					
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Area> getAreasSeguimientoAAA() {
		List<Area> lista = new ArrayList<Area>();
		try {
			lista = (List<Area>) crudServiceBean
					.getEntityManager()
					.createNativeQuery(
							" select * FROM Areas a where a.area_tracing = true ORDER BY a.area_name", Area.class)					
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Area> getAreasDireccionaZonales() {
		List<Area> lista = new ArrayList<Area>();		
		try {
			lista = crudServiceBean
					.getEntityManager()
					.createQuery(" FROM Area a where a.estado = true and (a.id in(272, 1139) or a.tipoArea=5) "							
							+ " ORDER BY a.areaName")					
					.getResultList();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return lista;
	}
	
	public List<Area> listarDireccionesZonales() throws ServiceException {
		List<Area> listaGalapagos = areaService.areaGalapagos();
		List<Area> listaTodas = areaService.listarDireccionesZonales();
		listaTodas.addAll(listaGalapagos);
		
        return listaTodas;
    }
	
	@SuppressWarnings("unchecked")
	public List<AreasSnapProvincia> obtenerAreasProtegidasSnap(){
		List<AreasSnapProvincia> lista = new ArrayList<AreasSnapProvincia>();	
		try {
			lista = crudServiceBean
					.getEntityManager()
					.createQuery("SELECT a FROM AreasSnapProvincia a where a.estado = true ORDER BY a.id ASC")					
					.getResultList();		
			
			List<String> nombres = new ArrayList<>();
			List<AreasSnapProvincia> listaAux = new ArrayList<AreasSnapProvincia>();
			
			for(AreasSnapProvincia nombre : lista){
				if(nombres.isEmpty()){
					nombres.add(nombre.getNombreAreaProtegida() + nombre.getZona());
					listaAux.add(nombre);
				}else{
					if(!nombres.contains(nombre.getNombreAreaProtegida() + nombre.getZona())){
						nombres.add(nombre.getNombreAreaProtegida() + nombre.getZona());
						listaAux.add(nombre);
					}
				}
			}
			return listaAux;
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return lista;
	}
}
