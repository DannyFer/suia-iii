package ec.gob.ambiente.prevencion.certificadoviabilidad.controller;

import ec.gob.ambiente.prevencion.certificadoviabilidad.bean.AdjuntarPronunciamientoBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoViabilidadAmbientalInterseccionProyectoFacade;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestScoped
@ManagedBean
public class AdjuntarPronunciamientoController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3710728822083232386L;

    @Getter
    @Setter
    @ManagedProperty(value = "#{adjuntarPronunciamientoBean}")
    private AdjuntarPronunciamientoBean adjuntarPronunciamientoBean;

    @EJB
    private CertificadoViabilidadAmbientalInterseccionProyectoFacade certificadoViabilidadAmbientalInterseccionProyectoService;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @EJB
    private ProcesoFacade procesoFacade;


    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;


    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;
    @EJB
    private CategoriaIIFacade categoriaIIFacade;

    private static final Logger LOGGER = Logger.getLogger(AdjuntarPronunciamientoController.class);

    public void completarTarea() {
        try {
            //Para los casos que se requieran preguntas del proponente
            if(adjuntarPronunciamientoBean.fileStreamPreguntasProponente() != null){
                if(!adjuntarPronunciamientoBean.getDescargado())
                {
                    JsfUtil.addMessageError("Para continuar debe descargar el documento con las respuestas del proponente.");
                    return;
                }
            }

            if (adjuntarPronunciamientoBean.getAdjuntoPronunciamiento() != null) {
                Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                params.put("pronunciamiento", adjuntarPronunciamientoBean.getEsPronunciamientoFavorable());
                params.put("exentoPagos", categoriaIIFacade.getExentoPago(proyectosBean.getProyecto()));
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                                .getProcessInstanceId(), params);

                certificadoViabilidadAmbientalInterseccionProyectoService.completarTareaYAdjuntarFile(bandejaTareasBean.getTarea()
                                .getProcessInstanceId(),
                        adjuntarPronunciamientoBean.getAdjuntoPronunciamiento(), proyectosBean.getProyecto(),
                        bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getTaskId(),
                        loginBean.getUsuario(), adjuntarPronunciamientoBean.getEsPronunciamientoFavorable());

                if (adjuntarPronunciamientoBean.getEsPronunciamientoFavorable()) {

                    Map<String, Object> variables = procesoFacade
                            .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                                    .getProcessInstanceId());

                    Integer idProyecto = Integer.parseInt((String) variables
                            .get(Constantes.ID_PROYECTO));

                    ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyecto);

                    if (isCategoriaIII(proyecto) || isCategoriaIV(proyecto)) {
                        licenciaAmbientalFacade.iniciarProcesoLicenciaAmbiental(loginBean.getUsuario(), proyecto);
                    }
                }
                JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
            }
            else {
                JsfUtil.addMessageError("Para continuar debe adjuntar el documento con el Pronunciamiento del Certificado de Viabilidad.");
            }
        } catch (Exception e) {
        	LOGGER.error("AdjuntarPronunciamientoController para el usuario: "+JsfUtil.getLoggedUser().getNombre()+" error a continuación: ",e);
        }
    }

    public void descargarMapa() {
        try {
            UtilDocumento.descargarPDF(certificadoViabilidadAmbientalInterseccionProyectoService
                    .getAlfrescoMapa(proyectosBean.getProyecto()), "Mapa del certificado intersección");
        } catch (Exception e) {
        	LOGGER.error("AdjuntarPronunciamientoController para el usuario: "+JsfUtil.getLoggedUser().getNombre()+" error a continuación: ", e);
            JsfUtil.addMessageError("El documento no fue correctamente generado, por lo que no se puede descargar. Por favor comunicarse con mesa de ayuda.");
        }
    }

    public void descargarOficio() {
        try {
            UtilDocumento.descargarPDF(certificadoViabilidadAmbientalInterseccionProyectoService
                    .getAlfrescoOficio(proyectosBean.getProyecto()), "Oficio del certificado intersección");
        } catch (Exception e) {
            JsfUtil.addMessageError("El documento no fue correctamente generado, por lo que no se puede descargar. Por favor comunicarse con mesa de ayuda.");
        }
    }

    public void descargarCoordenadas() {
        try {
            UtilDocumento.descargarExcel(certificadoViabilidadAmbientalInterseccionProyectoService
                    .descargarCoordenadas(proyectosBean.getProyecto()), "Coordenadas del proyecto");
        } catch (Exception e) {
            LOGGER.error("error al descargar ", e);
        }

    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/certificadoviabilidad/adjuntarPronunciamientoCertificadoViabilidad.jsf");
    }


    public boolean isCategoriaIII(ProyectoLicenciamientoAmbiental proyecto) {
        return proyecto.getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_III;
    }

    public boolean isCategoriaIV(ProyectoLicenciamientoAmbiental proyecto) {
        return proyecto.getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_IV;
    }
}
