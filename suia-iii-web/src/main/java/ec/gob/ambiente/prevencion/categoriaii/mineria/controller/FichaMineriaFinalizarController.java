package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.File;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.prevencion.categoria2.controllers.FichaAmbientalPmaController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;

/**
 *
 * @author frank torres
 */
@ManagedBean
@ViewScoped
public class FichaMineriaFinalizarController implements Serializable {

    private static final long serialVersionUID = 3601189618161628450L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(FichaMineriaFinalizarController.class);
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
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{impresionFichaMineriaController}")
    private ImpresionFichaMineriaController impresionFichaMineriaController;
    @Getter
    @Setter
    @ManagedProperty(value = "#{fichaAmbientalPmaController}")
    private FichaAmbientalPmaController fichaAmbientalPmaController;
    @Getter
    @Setter
    private String mensaje;
    @Getter
    @Setter
    private byte[] archivo;
    @Getter
    @Setter
    private String pdf;
    @Getter
    @Setter
    private File archivoFinal;
    @Getter
    @Setter
    private Boolean descargarFicha = false;
    @Getter
    @Setter
    private Boolean descargarRegistro = false;
    @Getter
    @Setter
    private StreamedContent ficha;
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;
    @Getter
    @Setter
    private String mensajeFinalizar="";

    @PostConstruct
    public void cargarDatos() {
        cargarDatosBandeja();
    }

    private void cargarDatosBandeja() {

        proyecto = proyectosBean.getProyecto();
        mensaje = getNotaResponsabilidadInformacionRegistroProyecto(loginBean
                .getUsuario());
        setMensajeFinalizar(Constantes.getMessageResourceString(MENSAJE_LISTA));
    }

    public void descargarFichaTecnico() {
        descargarFicha = true;
        try {
            byte[] fichero = categoriaIIFacade.recuperarDocumentoCategoriaII(
                    proyecto.getId(), TipoDocumentoSistema.TIPO_DOCUMENTO_FICHA_MINERIA);

            if (fichero != null) {
                UtilFichaMineria.descargar(
                        fichero,
                        "Ficha_Registro_Ambiental_Minería_Artesanal"
                        + proyecto.getCodigo());
            } else {
                JsfUtil.addMessageError("Error al realizar la operación.");
            }
        } catch (Exception e) {
            LOG.error("Error al descargar la ficha Registro del Ambiental", e);
        }
    }

    public void descargarLicencia() {
        descargarRegistro = true;
        try {
            byte[] fichero = categoriaIIFacade.recuperarDocumentoCategoriaII(
                    proyecto.getId(), TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL);

            if (fichero != null) {
                UtilFichaMineria.descargar(
                        fichero,
                        "Registro_Ambiental_Minería_Artesanal"
                        + proyecto.getCodigo());
            } else {
                JsfUtil.addMessageError("Error al realizar la operación.");
            }
        } catch (Exception e) {
            LOG.error(
                    "Error al descargar el Registro Ambiental Mineria Artesanal",
                    e);
        }
    }

    public void redireccionarBandeja() {
        if (descargarFicha && descargarRegistro) {
            RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");
        } else {
            JsfUtil.addMessageError("Debes descargar los documentos para ir a la bandeja de tareas.");
        }
    }

    public boolean generaDesechosEspeciales() {
        return proyecto.getGeneraDesechos() != null
                && proyecto.getGeneraDesechos() == true;
    }

    public boolean proyectoExante() {
        return proyecto.getTipoEstudio().getId() == 1;
    }


    public String getUrlContinuar() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        return url.substring(0, url.indexOf(req.getContextPath()) + req.getContextPath().length());
    }


    private String getNotaResponsabilidadInformacionRegistroProyecto(
            Usuario persona) {
        String[] parametros = { persona.getPersona().getNombre(), persona.getPin() };
        return DocumentoPDFPlantillaHtml.getPlantillaConParametros(
                "nota_responsabilidad_certificado_interseccion", parametros);
    }

}
