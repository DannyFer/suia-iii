/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.webservice;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author ishmael
 */
@Path("/RestServices/asignarTareaWs")
@Singleton
public class AsignarTareaWs {

    //    @EJB
//    private AsignarTareaFacade asignarTareaFacade;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(AsignarTareaWs.class);

    @POST
    @Path("/usuarioMenorCarga")
    @Lock(LockType.READ)
    public String usuarioMenorCarga(@FormParam("rol") String rol, @FormParam("area") String area, @FormParam("rolC") String rolC, @FormParam("user") String user) {
        try {//String b = "rolC=" + usuario_tmp + "&user=" + usuarioArea;
            AsignarTareaFacade asignarTareaFacade = (AsignarTareaFacade) BeanLocator.getInstance(AsignarTareaFacade.class);
            System.out.println("Ingresa primera vez: con rol: "+ rol +" y area "+ area);

            if(rol != null) {
            	boolean roles= rol.contains("T�CNICO ANALISTA DE REGISTRO");
            	if (roles==true) {
                	rol="TÉCNICO ANALISTA DE REGISTRO";
                }
            }
            
            if(area != null) {
            	boolean ofi= area.contains("OFICINA T�CNICA");
            	
            	if (ofi) {            	
    	            String areanueva= "";
    	            String[] areasplit = area.split("OFICINA T�CNICA");
    	            if (areasplit[0].contains("OFICINA T�CNICA")) {
    	            	areanueva="OFICINA TÉCNICA";
    	            }
    	            if (areasplit[1].contains("QUININD�")) {
    	            	area=areanueva+" QUININDÉ";            	
    	             }else if (areasplit[1].contains("MEJ�A")) {
    	            	area=areanueva+"MEJÍA";            	
    	            }else if (areasplit[1].contains("MACAR�")) {
    	            	area=areanueva+"MACARÁ";            	
    	            } else if (areasplit[1].contains("ALAUS�")) {
    	            	area=areanueva+"ALAUSÍ";            	
    	            }else if (areasplit[1].contains("LA MAN�")) {
    	            	area=areanueva+"LA MANÁ";            		                   
    	            }else {
    	            	area="OFICINA TÉCNICA"+areasplit[1];
    	            }
                }
            }
            
            try
            {
            	rol=rol.replace("Ã", "Á").replace("Ã", "É").replace("Ã", "Í").replace("Ã", "Ó").replace("Ã", "Ú").replace("Ã", "Ñ");
            }
            catch (Exception e) {}
            try
            {            	
            	area=area.replace("Ã", "Á").replace("Ã", "É").replace("Ã", "Í").replace("Ã", "Ó").replace("Ã", "Ú").replace("Ã", "Ñ");
            }
            catch (Exception e) {}
            
            if(rolC!=null){
                rol=  Constantes.getRoleAreaName(rolC.replace("coordinador","tecnico"));
            }
            if(user!=null){
                UsuarioFacade usuarioFacade = (UsuarioFacade) BeanLocator.getInstance(UsuarioFacade.class);
              Usuario u =  usuarioFacade.buscarUsuarioCompleta(user);
                if(user!= null && u.getListaAreaUsuario() !=null && u.getListaAreaUsuario().size() > 0){
                    area= u.getListaAreaUsuario().get(0).getArea().getAreaName();
                }
            }
            List<Usuario> usuarios = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rol, area);
            System.out.println("Ingresa lista de usuarios: "+ usuarios.size() + " con rol: "+ rol +" y area "+ area);
            if (usuarios.size()>0) {
            	System.out.println("Ingresa lista de usuarios: "+ usuarios.size() + "y asigna a :" + usuarios.get(0).getNombre());
                return usuarios.get(0).getNombre();
            } else {
            	System.out.println("No existe rol: "+rol +" en el area : "+area);
                return " ";
            }
        } catch (Exception e) {
            LOG.error(e, e);
        }
        return null;
    }

}
