/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.webservice.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilBaseDatos;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author jgras
 */
@Stateless
public class AsignarTareaFacade {

    private static final String QUERY_TAREAS_POR_USUARIO = "select * from workload2";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(AsignarTareaFacade.class);

    @EJB
    private CrudServiceBean crudServiceBean; //

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @Inject
    private UtilBaseDatos utilBaseDatos; //
    
    public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();

    @SuppressWarnings({ "unchecked", "unused" })
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Deprecated
    public List<Usuario> getCargaLaboralPorUsuarios(final String rol, String area) {
        if (area == null || area.isEmpty()) {
            area = "DIRECCIÓN NACIONAL DE PREVENCIÓN DE LA CONTAMINACIÓN AMBIENTAL";
        }
        LOG.info("Buscando usuarios para rol y area " + rol + " : " + area);
        List<Usuario> usuariosYCargas = new ArrayList<Usuario>();
        try {
            Query query = crudServiceBean.getEntityManager().createNativeQuery(
                    Usuario.FIND_BY_ROL_AND_AREA);
            query.setParameter("rol", rol);
            query.setParameter("area","%"+area+"%");

            List<String> nombresDeUsuarios = query.getResultList();

            if (nombresDeUsuarios.isEmpty()) {
                return new ArrayList<Usuario>();
            }

            Connection conexionBpm = utilBaseDatos.getConexionBpm();
            Statement statement = conexionBpm.createStatement();

            StringBuilder consulta = new StringBuilder(QUERY_TAREAS_POR_USUARIO
                    + "('");
            for (int i = 0; i < nombresDeUsuarios.size(); i++) {
                consulta.append(nombresDeUsuarios.get(i));
                if (i < nombresDeUsuarios.size() - 1) {
                    consulta.append(",");
                }
            }
            consulta.append("') as opt(deptid bigint, deptname text);");
            LOG.info(consulta.toString());

            ResultSet resultSet = statement.executeQuery(consulta.toString());

            int totalTareas = 0;
            while (resultSet.next()) {
                Usuario usuario = new Usuario();
                usuario = usuarioFacade.buscarUsuario(resultSet.getString(2));
                usuario.setNumeroTramites(resultSet.getInt(1));
                usuariosYCargas.add(usuario);
                totalTareas += resultSet.getInt(1);
            }

            DecimalFormat df = new DecimalFormat("#.##");
            for (Usuario userWorkload : usuariosYCargas) {
                if (userWorkload.getNumeroTramites() != 0)
                    userWorkload.setCarga(userWorkload
                            .getNumeroTramites() * 100d / totalTareas);
                else
                    userWorkload.setCarga(0d);
                userWorkload.setNombrePersona(userWorkload.getPersona().getNombre());
            }

            resultSet.close();
            statement.close();
            conexionBpm.close();

        } catch (Exception e) {
            LOG.error("No se puedo obtener la carga laboral para rol y area "
                    + rol + ":" + area, e);
        }

        Comparator<Usuario> comparadorUsuario = new Comparator<Usuario>() {
            @Override
            public int compare(Usuario usuario, Usuario t1) {
                return (int) (usuario.getCarga() - t1.getCarga());
            }
        };
        Collections.sort(usuariosYCargas, comparadorUsuario);
        return usuariosYCargas;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Deprecated
    public List<Usuario> obtenerCargaPorUsuarios(List<Usuario> usuarios) {
        try {
            Connection conexionBpm = utilBaseDatos.getConexionBpm();
            Statement statement = conexionBpm.createStatement();

            StringBuilder consulta = new StringBuilder(QUERY_TAREAS_POR_USUARIO
                    + "('");
            for (int i = 0; i < usuarios.size(); i++) {
                consulta.append(usuarios.get(i).getNombre());
                if (i < usuarios.size() - 1) {
                    consulta.append(",");
                }
            }
            consulta.append("') as opt(deptid bigint, deptname text);");
            LOG.info(consulta.toString());

            ResultSet resultSet = statement.executeQuery(consulta.toString());
            List<Usuario> usuariosYCargas = new ArrayList<Usuario>();
            int totalTareas = 0;
            while (resultSet.next()) {
                Usuario usuario = new Usuario();
                usuario = usuarioFacade.buscarUsuario(Objects.toString(resultSet.getString(2), null));
                System.out.println("+++++++++++++++++++++++++++");
                System.out.println(usuario);
                int resultado = resultSet.getInt(1);
                System.out.println("------------------------");
                usuario.setNumeroTramites(resultado);
                usuariosYCargas.add(usuario);
                totalTareas += resultSet.getInt(1);
            }

            for (Usuario userWorkload : usuariosYCargas) {
                if (userWorkload.getNumeroTramites() != 0)
                    userWorkload.setCarga(userWorkload
                            .getNumeroTramites() * 100d / totalTareas);
                else
                    userWorkload.setCarga(0d);
                userWorkload.setNombrePersona(userWorkload.getPersona().getNombre());
            }

            resultSet.close();
            statement.close();
            conexionBpm.close();

            Comparator<Usuario> comparadorUsuario = new Comparator<Usuario>() {
                @Override
                public int compare(Usuario usuario, Usuario t1) {
                    return (int) (usuario.getCarga() - t1.getCarga());
                }
            };
            Collections.sort(usuariosYCargas, comparadorUsuario);
            return usuariosYCargas;
        } catch (SecurityException e) {
            LOG.error(e);
        } catch (IllegalArgumentException e) {
            LOG.error(e);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    /**
     * <b> Método que permite obtener el listado de usuarios ordenados de acuerdo a su carga laboral. </b>
     *
     * @return
     * @author jgras
     * @version Revision: 1.0
     * <p>
     * [Autor: jgras, Fecha: 15/09/2015]
     * </p>
     */
    @SuppressWarnings("unchecked")
	public List<Usuario> getCargaLaboralPorUsuariosV2(final String rol, String areaName) {
        if (areaName == null || areaName.isEmpty()) {
            areaName = "DIRECCIÓN NACIONAL DE PREVENCIÓN DE LA CONTAMINACIÓN AMBIENTAL";
        }
        List<Usuario> usuariosYCargas = new ArrayList<Usuario>();
        Integer pesoTareasUsuario;
        Integer pesoTotal = 0;
        LOG.info("Buscando usuarios para rol y area " + rol + " : " + areaName);
        try {
            Query query = crudServiceBean.getEntityManager().createNativeQuery(
                    Usuario.FIND_BY_ROL_AND_AREA);
            query.setParameter("rol", rol);
            query.setParameter("area","%"+areaName+"%");

            List<String> usuariosArea = query.getResultList();

            if (usuariosArea.isEmpty()) {
                return new ArrayList<Usuario>();
            }

            for (String userName : usuariosArea) {
                pesoTareasUsuario = 0;
                Usuario user = usuarioFacade.buscarUsuario(userName);
//                List<TaskSummary> userTaskList = procesoFacade.getAllTaskAssigned(user);
                Query sqlProcesos = crudServiceBean.getEntityManager().createNativeQuery("select * from  dblink('"+dblinkBpmsSuiaiii+"','select count(actualowner_id) as tareas from task where actualowner_id in(''"+userName+"'') and status in(''InProgress'',''Reserved'',''Created'',''Ready'') group by actualowner_id order by tareas') as (tareas integer)");
                try{
	                Object result = sqlProcesos.getSingleResult();
	                pesoTareasUsuario=(Integer) result;
                }
                catch(Exception e){}

//                for (TaskSummary task : userTaskList) {
//                    //Si la tarea no tiene peso (Priority = 0) entonces le corresponde valor 1
//                    if(task.getPriority() == 0){
//                        pesoTareasUsuario++;
//                    }
//                    else {
//                        pesoTareasUsuario += task.getPriority();
//                    }
//                }
                pesoTotal += pesoTareasUsuario;
                //Se guardan los datos de las tareas en el usuario y se agrega el usuario a la lista para retornar
                user.setNumeroTramites(pesoTareasUsuario);
                user.setPesoTotalTareas(pesoTareasUsuario);
                usuariosYCargas.add(user);
            }

            //Se agrega el % de carga a cada usuario
            for (Usuario userWorkload : usuariosYCargas) {
                if(pesoTotal != 0)
                    userWorkload.setCarga(userWorkload.getPesoTotalTareas() * 100d / pesoTotal);
                else
                    userWorkload.setCarga(0d);

                userWorkload.setNombrePersona(userWorkload.getPersona().getNombre());
            }

        } catch (Exception e) {
            LOG.error("No se puedo obtener la carga laboral para rol y area " + rol + ":" + areaName, e);
        }

        Comparator<Usuario> comparadorUsuario = new Comparator<Usuario>() {
            @Override
            public int compare(Usuario usuario, Usuario t1) {
                if(usuario.getCarga() == t1.getCarga()) {
                	if(usuario.getCoeficienteRendimiento() == null || t1.getCoeficienteRendimiento() ==null){
                        return 0;
                    }
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
        return usuariosYCargas;
    }

    /**
     * <b> Método que permite obtener, según un listado de usuarios, el mismo listado ordenado de acuerdo a la carga laboral. </b>
     *
     * @return
     * @author jgras
     * @version Revision: 1.0
     * <p>
     * [Autor: jgras, Fecha: 15/09/2015]
     * </p>
     */
    public List<Usuario> obtenerCargaPorUsuariosV2(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            return usuarios;
        }
        List<Usuario> usuariosYCargas = new ArrayList<Usuario>();
        Integer pesoTareasUsuario;
        Integer pesoTotal = 0;
        try {
            for (Usuario user : usuarios) {
            	pesoTareasUsuario = 0;
                Query sqlProcesos = crudServiceBean.getEntityManager().createNativeQuery("select * from  dblink('"+dblinkBpmsSuiaiii+"','select count(actualowner_id) as tareas from task where actualowner_id in(''"+user.getNombre()+"'') and status in(''InProgress'',''Reserved'',''Created'',''Ready'') group by actualowner_id order by tareas') as (tareas integer)");
                try{
	                Object result = sqlProcesos.getSingleResult();
	                pesoTareasUsuario=(Integer) result;
                }
                catch(Exception e){}
                
                try{
	                //buscar las asignaciones de PPC RCOA que aún no han realizado el pago
	                Query sqlAsignaciones= crudServiceBean.getEntityManager().createQuery("select count(f) from FacilitadorPPC f where f.fechaRegistroPago=null and f.aceptaProyecto=null and f.estado=true and f.usuario.id = :idUsuario");
                    sqlAsignaciones.setParameter("idUsuario", user.getId());
	                Object resultAsignaciones = sqlAsignaciones.getSingleResult();
	                Long asignaciones = ((Number) resultAsignaciones).longValue();
	                pesoTareasUsuario += asignaciones.intValue();
                }
                catch(Exception e){}
                
                Usuario userAll = usuarioFacade.buscarUsuario(user.getNombre());
                pesoTotal += pesoTareasUsuario;                
                userAll.setNumeroTramites(pesoTareasUsuario);
                userAll.setPesoTotalTareas(pesoTareasUsuario);
                usuariosYCargas.add(userAll);
                //break;
            }
            for (Usuario userWorkload : usuariosYCargas) {
                if(pesoTotal != 0)
                	userWorkload.setCarga(userWorkload.getPesoTotalTareas() * 100d / pesoTotal);
                else
                    userWorkload.setCarga(0d);

                userWorkload.setNombrePersona(userWorkload.getPersona().getNombre());
            }

        } catch (Exception e) {
            LOG.error("No se puedo obtener la carga laboral", e);
        }

        Comparator<Usuario> comparadorUsuario = new Comparator<Usuario>() {
            @Override
            public int compare(Usuario usuario, Usuario t1) {
            	if(usuario.getCarga() > t1.getCarga())
            		return 1;
            	if(usuario.getCarga() < t1.getCarga())
            		return -1;
            	
            	if(usuario.getCoeficienteRendimiento() != null && t1.getCoeficienteRendimiento() != null) {
	            	if(usuario.getCoeficienteRendimiento() > t1.getCoeficienteRendimiento())
	            		return 1;
	            	if(usuario.getCoeficienteRendimiento() < t1.getCoeficienteRendimiento())
	            		return -1;
            	}
            	
            	return 0;
            }
        };
        Collections.sort(usuariosYCargas, comparadorUsuario);
        return usuariosYCargas;
    }
    
    @SuppressWarnings("unchecked")
	public Usuario autoridadXRolArea(String rol, String areaName)
    {
    	Usuario usuario=null;
    	Query query = crudServiceBean.getEntityManager().createNativeQuery(Usuario.FIND_BY_ROL_AND_AREA);
        query.setParameter("rol", rol);
        query.setParameter("area","%"+areaName+"%");
		List<String> usuariosArea = query.getResultList();
        for (String userName : usuariosArea) {            
        	usuario = usuarioFacade.buscarUsuario(userName);
        }        
        return usuario;
    }
    
    public List<Usuario> getCargaLaboralPorNombreUsuariosProceso(List<String> usuariosArea, String nombreProceso) {
        List<Usuario> usuariosYCargas = new ArrayList<Usuario>();
        Integer pesoTareasUsuario;
        Integer pesoTotal = 0;
        try {

            if (usuariosArea.isEmpty()) {
                return new ArrayList<Usuario>();
            }

            for (String userName : usuariosArea) {
                pesoTareasUsuario = 0;
                Usuario user = usuarioFacade.buscarUsuario(userName);
                if(user != null) {
	                Query sqlProcesos = crudServiceBean.getEntityManager().createNativeQuery("select * from  dblink('"+dblinkBpmsSuiaiii+"','select count(actualowner_id) as tareas from task where actualowner_id in(''"+userName+"'') and status in(''InProgress'',''Reserved'',''Created'',''Ready'') and processid = ''"+nombreProceso+"'' group by actualowner_id order by tareas') as (tareas integer)");
	                try{
		                Object result = sqlProcesos.getSingleResult();
		                pesoTareasUsuario=(Integer) result;
	                }
	                catch(Exception e){}
	
	                pesoTotal += pesoTareasUsuario;
	                //Se guardan los datos de las tareas en el usuario y se agrega el usuario a la lista para retornar
	                user.setNumeroTramites(pesoTareasUsuario);
	                user.setPesoTotalTareas(pesoTareasUsuario);
	                usuariosYCargas.add(user);
                }
            }

            //Se agrega el % de carga a cada usuario
            for (Usuario userWorkload : usuariosYCargas) {
                if(pesoTotal != 0)
                    userWorkload.setCarga(userWorkload.getPesoTotalTareas() * 100d / pesoTotal);
                else
                    userWorkload.setCarga(0d);

                userWorkload.setNombrePersona(userWorkload.getPersona().getNombre());
            }

        } catch (Exception e) {
            LOG.error("No se puedo obtener la carga laboral ", e);
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
        return usuariosYCargas;
    }
    
    public List<Usuario> getCargaLaboralPorUsuariosProceso(List<Usuario> usuariosArea, String nombreProceso) {
        List<Usuario> usuariosYCargas = new ArrayList<Usuario>();
        Integer pesoTareasUsuario;
        Integer pesoTotal = 0;
        try {

            if (usuariosArea.isEmpty()) {
                return new ArrayList<Usuario>();
            }

            for (Usuario user : usuariosArea) {
                pesoTareasUsuario = 0;
                String userName = user.getNombre();
                Query sqlProcesos = crudServiceBean.getEntityManager().createNativeQuery("select * from  dblink('"+dblinkBpmsSuiaiii+"','select count(actualowner_id) as tareas from task where actualowner_id in(''"+userName+"'') and status in(''InProgress'',''Reserved'',''Created'',''Ready'') and processid in (''"+nombreProceso+"'') group by actualowner_id order by tareas') as (tareas integer)");
                try{
	                Object result = sqlProcesos.getSingleResult();
	                pesoTareasUsuario=(Integer) result;
                }
                catch(Exception e){}

                pesoTotal += pesoTareasUsuario;
                //Se guardan los datos de las tareas en el usuario y se agrega el usuario a la lista para retornar
                user.setNumeroTramites(pesoTareasUsuario);
                user.setPesoTotalTareas(pesoTareasUsuario);
                usuariosYCargas.add(user);
            }

            //Se agrega el % de carga a cada usuario
            for (Usuario userWorkload : usuariosYCargas) {
                if(pesoTotal != 0)
                    userWorkload.setCarga(userWorkload.getPesoTotalTareas() * 100d / pesoTotal);
                else
                    userWorkload.setCarga(0d);

                userWorkload.setNombrePersona(userWorkload.getPersona().getNombre());
            }

        } catch (Exception e) {
            LOG.error("No se puedo obtener la carga laboral ", e);
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
        return usuariosYCargas;
    }
    
    public List<Usuario> obtenerCargaFacilitadoresByPass(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            return usuarios;
        }
        List<Usuario> usuariosYCargas = new ArrayList<Usuario>();
        Integer pesoTareasUsuario;
        Integer pesoTotal = 0;
        try {
            for (Usuario user : usuarios) {
            	pesoTareasUsuario = 0;                
                try{
	                //buscar las asignaciones de PPC RCOA que han sido confirmadas en el año presente
                	//y las que han sido asignadas en el presente año pero aún se encuentran en etapa de pago
	                Query sqlAsignaciones= crudServiceBean.getEntityManager().createQuery("select count(f) from FacilitadorPPC f "
	                		+ " where f.estado = true and f.usuario.id = :idUsuario and "
	                		+ " ((f.aceptaProyecto != null and year(fechaAceptaProyecto) = year(now())) or "
	                		+ " (f.fechaRegistroPago = null and f.aceptaProyecto=null and year(fechaCreacion) = year(now())) or " 
	                		+ " (f.fechaRegistroPago != null and year(fechaRegistroPago) = year(now()) and f.aceptaProyecto=null) )" );
                    sqlAsignaciones.setParameter("idUsuario", user.getId());
                    
	                Object resultAsignaciones = sqlAsignaciones.getSingleResult();
	                Long asignaciones = ((Number) resultAsignaciones).longValue();
	                pesoTareasUsuario += asignaciones.intValue();
                }
                catch(Exception e){}
                
                Usuario userAll = usuarioFacade.buscarUsuario(user.getNombre());
                pesoTotal += pesoTareasUsuario;                
                userAll.setNumeroTramites(pesoTareasUsuario);
                userAll.setPesoTotalTareas(pesoTareasUsuario);
                usuariosYCargas.add(userAll);
            }
            for (Usuario userWorkload : usuariosYCargas) {
                if(pesoTotal != 0)
                	userWorkload.setCarga(userWorkload.getPesoTotalTareas() * 100d / pesoTotal);
                else
                    userWorkload.setCarga(0d);

                userWorkload.setNombrePersona(userWorkload.getPersona().getNombre());
            }

        } catch (Exception e) {
            LOG.error("No se puedo obtener la carga laboral", e);
        }

        Comparator<Usuario> comparadorUsuario = new Comparator<Usuario>() {
            @Override
            public int compare(Usuario usuario, Usuario t1) {
            	if(usuario.getCarga() > t1.getCarga())
            		return 1;
            	if(usuario.getCarga() < t1.getCarga())
            		return -1;
            	
            	if(usuario.getCoeficienteRendimiento() != null && t1.getCoeficienteRendimiento() != null) {
	            	if(usuario.getCoeficienteRendimiento() > t1.getCoeficienteRendimiento())
	            		return 1;
	            	if(usuario.getCoeficienteRendimiento() < t1.getCoeficienteRendimiento())
	            		return -1;
            	}
            	
            	return 0;
            }
        };
        Collections.sort(usuariosYCargas, comparadorUsuario);
        return usuariosYCargas;
    }
}
