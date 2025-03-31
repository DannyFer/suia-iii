package ec.gob.ambiente.suia.iniciadorproceso.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Singleton;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.iniciadorproceso.base.ProcessInitiatorClass;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;

@Path("/RestServices/proceso")
@Singleton
public class IniciarProcesoServiceWS {

    private static boolean isThreadAlive;
    private static boolean error;

    @GET
    @Path("/inicar/{idProyectoPS}--{clase}--{processInstanceId}--{usuario}")
    @Produces("text/plain")
    public void iniciarProceso(
            @PathParam("clase") String clase,
            @PathParam("processInstanceId") Long processInstanceId,
            @PathParam("usuario") String usuario,
            @PathParam("idProyectoPS") Integer idProyectoPS) {

        try {

            if (clase != null) {

                final String usuarioFinal = usuario;
                final Long processInstanceIdFinal = processInstanceId;
                final Integer idProyectoPSFInal = idProyectoPS;
                final ProcessInitiatorClass processInitiatorClass = (ProcessInitiatorClass) Class.forName(clase).newInstance();
                final UsuarioFacade usuarioFacade = (UsuarioFacade) BeanLocator
                        .getInstance(UsuarioFacade.class);
                final ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade = (ProyectoLicenciaAmbientalFacade) BeanLocator
                        .getInstance(ProyectoLicenciaAmbientalFacade.class);
                final ParticipacionSocialFacade participacionSocialFacade = (ParticipacionSocialFacade) BeanLocator
                        .getInstance(ParticipacionSocialFacade.class);

                processInitiatorClass.inicializar();

                Runnable myRunnable = new Runnable(){

                    public void run(){

                        try {

                            IniciarProcesoServiceWS.error = true;

                            processInitiatorClass.setUsuario(usuarioFacade.buscarUsuario(usuarioFinal));
                            processInitiatorClass.setProcessInstanceId(processInstanceIdFinal);
                            processInitiatorClass.setVariables(new HashMap<String, Object>());
                            processInitiatorClass.setVariablesProceso(new HashMap<String, Object>());
                            processInitiatorClass.configurarVariables(idProyectoPSFInal, proyectoLicenciaAmbientalFacade, participacionSocialFacade);

                            if (processInitiatorClass.guardarVariables()){
                                IniciarProcesoServiceWS.isThreadAlive = false;
                                IniciarProcesoServiceWS.error = false;
                            }

                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (JbpmException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                
                if (!this.isThreadAlive){//Si no existe un hilo en ejecución:

                    if (!IniciarProcesoServiceWS.error){
                        this.isThreadAlive = true;
                        Thread thread = new Thread(myRunnable);
                        thread.start();
                    }
                    else{//Si ocurrió un error en la ejecución del hilo anterior
                        //Seteamos el valor a la variable que detiene el ciclo del BPM

                        ProcesoFacade procesoFacade = (ProcesoFacade) BeanLocator
                                .getInstance(ProcesoFacade.class);

                        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                        params.put("inclusionAmbiental", true);

                        Usuario user = usuarioFacade.buscarUsuario(usuario);
                        procesoFacade.modificarVariablesProceso(user, processInstanceId, params);
                    }
                }
                
                //Cris F: aumento de correo de notificación al coordinador para asignación de técnico
                CrudServiceBean crudServiceBean = (CrudServiceBean) BeanLocator.getInstance(CrudServiceBean.class);
                
                String sql_Tareas="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
                    	+ "'select formname,actualowner_id "
                    	+ "from task where processinstanceid = " + processInstanceId
                    	+ "') as (formname text,actualowner_id text)"; 
        		
            	Query q_tareas = crudServiceBean.getEntityManager().createNativeQuery(sql_Tareas);		
        		
            	Map<String, Object> tareas = new HashMap<String, Object>();
            	List<Object[]> resultListTareas = (List<Object[]>) q_tareas.getResultList();
        		if (resultListTareas.size() > 0) {
        			for (int i = 0; i < resultListTareas.size(); i++) {
        				Object[] dataProject = (Object[]) resultListTareas.get(i);
        				tareas.put(dataProject[0].toString(), dataProject[1].toString());				
        			}
        		}
                
                if(!tareas.isEmpty()){
                	if(tareas.get("asignar_tecnico_social_resposables") != null){
                		
                		String sql="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
                            	+ "'select variableid,value "
                            	+ "from variableinstancelog where processinstanceid =" + processInstanceId
                            	+ "') as (variableid text,value text)"; 
                		
                    	Query q = crudServiceBean.getEntityManager().createNativeQuery(sql);		
                		
                    	Map<String, Object> variables = new HashMap<String, Object>();
                    	List<Object[]> resultList = (List<Object[]>) q.getResultList();
                		if (resultList.size() > 0) {
                			for (int i = 0; i < resultList.size(); i++) {
                				Object[] dataProject = (Object[]) resultList.get(i);
                				variables.put(dataProject[0].toString(), dataProject[1].toString());				
                			}
                		}
                        
                        if(!variables.isEmpty()){
                        
                        	if(Boolean.valueOf(variables.get("requiereFacilitador").toString())){
                        		MensajeNotificacionFacade mensajeNotificacionFacade = (MensajeNotificacionFacade) BeanLocator.getInstance(MensajeNotificacionFacade.class);
            	                ContactoFacade contactoFacade = (ContactoFacade) BeanLocator.getInstance(ContactoFacade.class);
            	                PersonaFacade personaFacade = (PersonaFacade) BeanLocator.getInstance(PersonaFacade.class);
            	                
            	                List<Contacto> listaContactosCoordinador = contactoFacade.buscarUsuarioNativeQuery(variables.get("u_Coordinador").toString());
            	                Usuario usuarioCoordinador = usuarioFacade.buscarUsuario(variables.get("u_Coordinador").toString());
            	                Persona persona = personaFacade.buscarPersonaPorUsuario(usuarioCoordinador.getId());
            	                
            					String emailCoordinador = "";
            					for(Contacto contacto : listaContactosCoordinador){
            						if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
            							emailCoordinador = contacto.getValor();    
            							break;
            						}	
            					}
            	                
            	                
            	                String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionCoordinadorAreaTecnica", new Object[]{});
            					mensaje = mensaje.replace("numero_tramite", variables.get("tramite").toString());
            					mensaje = mensaje.replace("nombre_coordinador", persona.getNombre());

            					Usuario usuarioEnvio = new Usuario();
            					usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
            	                NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
            	    			mail_a.sendEmailInformacionProponente(emailCoordinador, "", mensaje, "Notificación de Coordinador de Área Técnica", variables.get("tramite").toString(), usuarioCoordinador, usuarioEnvio);
                        	}
                        }
                	}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
