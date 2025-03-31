/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.controllers;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.context.RequestContext;

import java.io.Serializable;

/**
 * @author frank torres
 */
@ManagedBean
@ViewScoped
public class FichaAmbientalGeneralFinalizarController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4192708780238873820L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(FichaAmbientalGeneralFinalizarController.class);

    private static final String MENSAJE_LISTA = "mensaje.listaproyecto";

    @Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineria;

    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @EJB
    private CategoriaIIFacade categoriaIIFacade;

    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    private FichaAmbientalPma fichaAmbiental;

    @Getter
    @Setter
    private Boolean descargarFicha = false;
    @Getter
    @Setter
    private Boolean descargarRegistro = false;

    @Getter
    @Setter
    ProyectoLicenciamientoAmbiental proyecto;

    @Getter
    @Setter
    private String mensaje;

    @Getter
    @Setter
    private String mensajeFinalizar;

    @PostConstruct
    private void cargarDatos() {
        cargarDatosBandeja();
    }

    private void cargarDatosBandeja() {

        proyecto = proyectosBean.getProyecto();
        mensaje = getNotaResponsabilidadInformacionRegistroProyecto(loginBean
                .getUsuario());

    }

    public void descargarFichaTecnico() {
        descargarFicha = true;
        try {
            byte[] archivoLicencia = categoriaIIFacade
                    .recuperarDocumentoCategoriaII(proyecto.getId(), TipoDocumentoSistema.TIPO_DOCUMENTO_REGISTRO_AMBIENTAL);
            if (archivoLicencia != null) {
                UtilFichaMineria.descargar(archivoLicencia,
                        "Registro_Ambiental_" + proyecto.getCodigo());
            }

        } catch (Exception e) {
            LOG.error("Error al descargar la ficha Registro del Ambiental", e);
        }
    }

    public void descargarLicencia() {
        descargarRegistro = true;
        try {

            byte[] archivoRegistro = categoriaIIFacade
                    .recuperarDocumentoCategoriaII(proyecto.getId(), TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL);
            if (archivoRegistro != null) {
                UtilFichaMineria.descargar(archivoRegistro,
                        "Resolución_Registro_Ambiental_" + proyecto.getCodigo());
            }
        } catch (Exception e) {
            LOG.error("Error al descargar el Registro Ambiental", e);
        }
    }

    public void redireccionarBandeja() {
        if (descargarFicha && descargarRegistro) {
            setMensajeFinalizar(Constantes.getMessageResourceString(MENSAJE_LISTA));
            RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");
        } else {
            JsfUtil.addMessageError("Debes descargar los documentos para ir a la bandeja de tareas.");
        }
    }

    public String getUrlContinuar() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        return url.substring(0, url.indexOf(req.getContextPath()) + req.getContextPath().length());
    }

    public boolean generaDesechosEspeciales() {
        return proyecto.getGeneraDesechos() != null
                && proyecto.getGeneraDesechos() == true;
    }

    public boolean proyectoExante() {
        return proyecto.getTipoEstudio().getId() == 1;
    }



    private String getNotaResponsabilidadInformacionRegistroProyecto(
            Usuario persona) {
        String[] parametros = { persona.getPersona().getNombre(), persona.getPin() };
        return DocumentoPDFPlantillaHtml.getPlantillaConParametros(
                "nota_responsabilidad_certificado_interseccion", parametros);
    }

}
