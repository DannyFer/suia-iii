package ec.gob.ambiente.suia.facilitador.service;

import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;

import javax.ejb.Singleton;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.kie.api.task.model.TaskSummary;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/RestServices/facilitadorWs")
@Singleton
public class FacilitadorServiceWS {

    @GET
    // @Path("/facilitador/{codigoProyecto}/{f}")
    @Path("/facilitador/{facilitador}--{lista}-{listaRechazo}")
    @Produces("text/plain")
    public String getDetalleInterseccion(
            @PathParam("facilitador") String facilitador,
            @PathParam("lista") String listaExcluir,
            @PathParam("listaRechazo") String listaExcluirRechazo) {
        try {        	        	        	
        	
        	String url = Constantes.getFacilitatorServiceURL()+facilitador+"--"+listaExcluir+"-"+listaExcluirRechazo;        	
        	
        	String listaExcluirNueva = listaExcluir.replace("-", "");        	
        	
            List<String> usuarios = Arrays.asList(listaExcluirNueva
                    .split("\\s*,\\s*"));

            List<String> usuariosR = new ArrayList<String>();
            if (listaExcluirRechazo != null) {
                if (listaExcluirRechazo.charAt(0) == '-') {
                    listaExcluirRechazo = listaExcluirRechazo.substring(1);
                }                               
                
                usuariosR = Arrays.asList(listaExcluirRechazo
                        .split("\\s*,\\s*"));
            }
                      

            FacilitadorFacade facilitadorFacade = (FacilitadorFacade) BeanLocator
                    .getInstance(FacilitadorFacade.class);

            UsuarioFacade usuarioFacade = (UsuarioFacade) BeanLocator
                    .getInstance(UsuarioFacade.class);
            List<Usuario> listaUsuariosExcluir = new ArrayList<Usuario>();
            List<Usuario> listaUsuariosExcluirRechazo = new ArrayList<Usuario>();
            List<Usuario> listaUsuariosExcluirRechazoAuxiliar = new ArrayList<Usuario>();

            Usuario usuarioFacilitadorTemporal = usuarioFacade
                    .buscarUsuario(facilitador);
            if (usuarioFacilitadorTemporal != null) {
                listaUsuariosExcluir.add(usuarioFacilitadorTemporal);
            }
            List<String> excluidos= new ArrayList<String>();
			for (String nombre : usuarios) {
				if (!usuariosR.contains(nombre)) {
                    excluidos.add(nombre);
//					Usuario usuarioTemporal = usuarioFacade
//							.buscarUsuario(nombre);
//					if (usuarioTemporal != null) {
//						listaUsuariosExcluir.add(usuarioTemporal);
//					}
				}
			}
			//los excluidos sería los facilitadores activos del proceso
            if(!excluidos.isEmpty()){
            listaUsuariosExcluir.addAll(usuarioFacade.buscarUsuarioPorNombres(excluidos));
            }

            List<Usuario> tmpList =  usuarioFacade.buscarUsuarioPorNombres(usuariosR);
            for (Usuario usuarioTemporal: tmpList) {
                //Usuario usuarioTemporal = usuarioFacade.buscarUsuario(nombre);
             //   if (usuarioTemporal != null) {

                    if (listaUsuariosExcluirRechazo.contains(usuarioTemporal)) {
                        if (listaUsuariosExcluirRechazoAuxiliar
                                .contains(usuarioTemporal)) {
                            listaUsuariosExcluirRechazoAuxiliar = new ArrayList<Usuario>();

                        }
                        listaUsuariosExcluirRechazoAuxiliar
                                .add(usuarioTemporal);

                    } else {
                        listaUsuariosExcluirRechazo.add(usuarioTemporal);
                        listaUsuariosExcluirRechazoAuxiliar
                                .add(usuarioTemporal);
                    }
              //  }
            }
            // usuarioFacade.buscarUsuario(nombre)

            List<Usuario> listaUsuarios = facilitadorFacade
                    .seleccionarFacilitadores(1, listaUsuariosExcluir,
                            listaUsuariosExcluirRechazoAuxiliar);

            if (listaUsuarios.size() > 0) {	
            	enviarMailFacilitadores(listaUsuarios.get(0), url);
            	return listaUsuarios.get(0).getNombre();
            } else {
                return "";
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }    
    
    //Cris F: envio de mail a facilitadores
    private void enviarMailFacilitadores(Usuario facilitador, String url){
    	try {
			Usuario usuarioEnvio = new Usuario();
			usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
    		CrudServiceBean crudServiceBean = (CrudServiceBean) BeanLocator.getInstance(CrudServiceBean.class);
        	ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade = (ProyectoLicenciamientoAmbientalFacade) BeanLocator.getInstance(ProyectoLicenciamientoAmbientalFacade.class);
        	UsuarioFacade usuarioFacade = (UsuarioFacade) BeanLocator.getInstance(UsuarioFacade.class);
        	ContactoFacade contactoFacade = (ContactoFacade) BeanLocator.getInstance(ContactoFacade.class);    		
        	
    		String sql="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
                	+ "'select variableid,value "
                	+ "from variableinstancelog where processinstanceid =("
                	+ "select processinstanceid from variableinstancelog where value = substring(''" + url +"'',1,255) limit 1)') as (variableid text,value text)"; 
    		
        	Query q = crudServiceBean.getEntityManager().createNativeQuery(sql);		
    		
        	Map<String, Object> variables = new HashMap<String, Object>();
        	List<Object[]> resultList = (List<Object[]>) q.getResultList();
    		if (resultList.size() > 0) {
    			for (int i = 0; i < resultList.size(); i++) {
    				Object[] dataProject = (Object[]) resultList.get(i);
    				variables.put(dataProject[0].toString(), dataProject[1].toString());				
    			}
    		}
    		
    		Usuario usuario = usuarioFacade.buscarUsuarioPorId(facilitador.getId());
			
			List<Contacto> listaContactos = contactoFacade.buscarPorPersona(usuario.getPersona());
			String emailFacilitador = "";
			for(Contacto contacto : listaContactos){
				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
					emailFacilitador = contacto.getValor();
					break;
				}				
			}
			
			String nombreFacilitador = usuario.getPersona().getNombre();
			    		
    		NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();	  
    		
    		if(variables.isEmpty()){
        		mail_a.sendEmailFacilitadoresSinDatos("Asignación Facilitador", nombreFacilitador, emailFacilitador, usuario, usuarioEnvio);
    		}else{
    			ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(variables.get("tramite").toString());
        		
        		String nombreProceso = proyecto.getNombre();     			
        		
    			Usuario representanteLegal = proyectoLicenciamientoAmbientalFacade.getRepresentanteProyecto(proyecto.getId());		
    			UbicacionesGeografica ubicacion = proyectoLicenciamientoAmbientalFacade.getUbicacionProyectoPorIdProyecto(proyecto.getId());
    			
    			String ubicacionProyecto = proyecto.getDireccionProyecto()+ 
    					" - Cantón: " + ubicacion.getUbicacionesGeografica().getNombre() + 
    					" - Provincia: " + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
    			
    			List<Contacto> listaContactosProponente = contactoFacade.buscarPorPersona(representanteLegal.getPersona());    			
    			String emailProponente = "";
    			String telefonoProponente = "";
    			for(Contacto contacto : listaContactosProponente){
    				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
    					emailProponente = contacto.getValor();
    				}
    				
    				if(contacto.getFormasContacto().getId() == FormasContacto.TELEFONO){
    					telefonoProponente = contacto.getValor();
    				}
    				
    				if(contacto.getFormasContacto().getId() == FormasContacto.CELULAR){
    					telefonoProponente += " - " + contacto.getValor();
    			
    				}			
    			}            	
        				
        		mail_a.sendEmailFacilitadores("Asignación Facilitador", nombreFacilitador, nombreProceso, 
    					ubicacionProyecto, emailFacilitador,representanteLegal.getPersona().getNombre(), emailProponente, telefonoProponente, proyecto.getCodigo(), usuario, usuarioEnvio);    		
        		
    		}
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
