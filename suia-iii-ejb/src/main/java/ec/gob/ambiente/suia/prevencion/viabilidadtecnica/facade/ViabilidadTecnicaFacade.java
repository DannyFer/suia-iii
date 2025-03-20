package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;

@Stateless
public class ViabilidadTecnicaFacade {

    private static final String URI_GESTION_INTEGRAL = "/prevencion/viabilidadTecnica/gestionIntegral.jsf";
    private static final String URI_CIERRE_TECNICO = "/prevencion/viabilidadTecnica/diagnosticoFactibilidadAnex.jsf";

    @EJB
    private ProcesoFacade procesoFacade;

    public Map<String, Object> obtenerVariablesViabilidadTenicaRequisitosPrevios(Usuario usuario, ProyectoLicenciamientoAmbiental proyecto) {
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();

        //Pagina de inicio
        if(isGestionIntegral(proyecto)){
            params.put("viabilidad_uri_inicio", URI_GESTION_INTEGRAL);
        }
        else {
            params.put("viabilidad_uri_inicio", URI_CIERRE_TECNICO);
        }

        //Usuarios
        params.put("viabilidad_u_CoordinadorPNGIDS", usuario.getNombre());
        params.put("viabilidad_u_DirectorPNGIDS", usuario.getNombre());
        params.put("viabilidad_u_Subsecretaria", usuario.getNombre());

        //Mensajes del proceso
        /*params.put("bodyNotificacionNoFavorable", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionReqTecNoFavorable",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionReqTecFavorable", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionReqTecFavorable",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionAprobacionReq", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionAprobacionReq",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyCancelacionSNAPMinero", variableFacade.recuperarValorMensajeNotificacion("bodyCancelacionSNAPMinero",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionAtrazoInformeInspecion", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionAtrazoInformeInspecion",
                new Object[]{proyecto.getCodigo()}));
        params.put("bodyNotificacionInterseccionZP", variableFacade.recuperarValorMensajeNotificacion("bodyNotificacionInterseccionZP",
                new Object[]{proyecto.getCodigo()}));*/

        /*
        * El equipo multidisciplinario estará conformado por:
           1. técnico civil sanitario,
           2. técnico ambiental, (role.pc.tecnico.Ambiental)
           3. técnico económico financiero, (role.pc.tecnico.Financiero)
           4. técnico electromecánico,
           5. técnico geógrafo,
           6. técnico geólogo,
           7. técnico de aprovechamiento con fines energéticos, compostaje o reciclaje.
        * */

        String equipoMultidisciplinarioSolicitaApoyo = "role.pc.tecnico.Ambiental" + "--"
                + proyecto.getAreaResponsable().getId();

        equipoMultidisciplinarioSolicitaApoyo += ";;role.pc.tecnico.Financiero" + "--"
                + proyecto.getAreaResponsable().getId();

        equipoMultidisciplinarioSolicitaApoyo += ";;role.pc.tecnico" + "--"
                + proyecto.getAreaResponsable().getId();

        params.put("viabilidad_equipoMultidisciplinarioSolicitaApoyo", equipoMultidisciplinarioSolicitaApoyo);
        return params;
    }

    //Verificar cuando ocurre esto????????????????
    private boolean isGestionIntegral(ProyectoLicenciamientoAmbiental proyecto){
        return proyecto.getCatalogoCategoria().getRequiereViabilidad();
    }
}
