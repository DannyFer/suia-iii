package ec.gob.ambiente.prevencion.participacionsocial.bean;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.PreguntasFacilitadoresAmbientales;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.PreguntasFacilitadoresAmbientalesFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class IngresarDocumentacionBean implements Serializable {

    private static final long serialVersionUID = -5717236974675749283L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(IngresarDocumentacionBean.class);

    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;

    @EJB
    private PreguntasFacilitadoresAmbientalesFacade preguntasFacilitadoresAmbientalesFacade;

    @Getter
    @Setter
    private ParticipacionSocialAmbiental proyectoPPS;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;


    @Getter
    @Setter
    private Map<String, Documento> documentos;

    @Getter
    @Setter
    private List<String> listaClaves;

    @Getter
    @Setter
    private List<PreguntasFacilitadoresAmbientales> listaPreguntas;


    @PostConstruct
    public void init() {

        listaClaves = new ArrayList<>(1);
        listaClaves.add("correccionDocumentacionPPS");
        documentos = new HashMap<>();


        try {
            proyectoPPS = participacionSocialFacade.getProyectoParticipacionSocialByProject(proyectosBean.getProyecto());
            documentos = participacionSocialFacade.recuperarDocumentosTipo(proyectosBean.getProyecto().getId(), RegistroMediosParticipacionSocial.class.getSimpleName(), listaClaves);



            listaPreguntas = preguntasFacilitadoresAmbientalesFacade.obtenerPreguntasPorParticipacion(proyectoPPS.getId());


        } catch (Exception e) {
            LOG.error(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
        }


    }


}
