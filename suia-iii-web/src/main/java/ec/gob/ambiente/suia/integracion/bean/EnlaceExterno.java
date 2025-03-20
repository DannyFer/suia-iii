/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.integracion.bean;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 12/03/2015]
 *          </p>
 */
@ManagedBean
@SessionScoped
public class EnlaceExterno {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(EnlaceExterno.class);
    @Getter
    private String action;


    public String getUrl() {
        if (action != null) {
            if (action.equals("RepositorioDocumental")) {
                return mostrarRepositorioDocumental();
            } else if (action.equals("ProcedimientosAdministrativos")) {
                return mostrarProcedimientosAdministrativos();
            }
            return "";
        /*return (servlet == 1 ? Constantes.getAppIntegrationServletSuia() : Constantes
                .getAppIntegrationServletHydrocarbons()) + "?time=" + System.currentTimeMillis();*/
        }
        JsfUtil.redirectTo("/errors/error.xhtml");
        return "";
    }


    public void execute(String action) {
        this.action = action;
        JsfUtil.redirectTo("/integracion/enlaces.jsf");
    }

    public void executeMostrarRepositorio() {
        execute("RepositorioDocumental");

    }


    public void executeProcedimientosAdministrativos() {
        execute("ProcedimientosAdministrativos");

    }


    private String mostrarRepositorioDocumental() {
        try {
            String tipo = "";
            Usuario usuario = JsfUtil.getLoggedUser();
            if (Usuario.isUserInRole(usuario, "TÉCNICO_LICENCIAS")) {
                tipo = "todos";
            } else if (Usuario.isUserInRole(usuario, "TECNICO_LICENCIAS_LECTURA")) {
                tipo = "lectura";
            }else{
                JsfUtil.redirectTo("/errors/error.xhtml");
            }
            return Constantes.getRepositorioDocumentalURL()+"?roles=true&cedulaUser=" +
                    usuario.getPin() + "&tipo=" + tipo;
        } catch (Exception e) {
            LOG.error("Error al cargar los datos del usuario", e);
            return "";
        }
    }


    private String mostrarProcedimientosAdministrativos() {
        try {
            String tipo = "";
            String admin = "";
            Usuario usuario = JsfUtil.getLoggedUser();
            if (Usuario.isUserInRole(usuario, "PROCESO_ADMINISTRATIVO_ADMIN")) {
                tipo = "todos";
                admin = "true";
            } else if (Usuario.isUserInRole(usuario, "PROCESO_ADMINISTRATIVO")) {
                tipo = "todos";
            } else if (Usuario.isUserInRole(usuario, "PROCESO_ADMINISTRATIVO_ESCRITURA")) {
                tipo = "escritura";
            } else if (Usuario.isUserInRole(usuario, "PROCESO_ADMINISTRATIVO_LECTURA")) {
                tipo = "lectura";
            }else{
                 JsfUtil.redirectTo("/errors/error.xhtml");
            }
            return Constantes.getProcedimientosAdministrativosURL()+"?roles=true&cedulaUser=" + usuario.getPin() + "&tipo=" + tipo + "&adminpa=" + admin;
        } catch (Exception e) {
            LOG.error("Error al cargar los datos del usuario", e);
            return "";
        }
    }
}
