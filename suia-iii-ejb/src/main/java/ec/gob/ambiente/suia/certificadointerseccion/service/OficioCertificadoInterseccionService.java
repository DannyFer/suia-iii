package ec.gob.ambiente.suia.certificadointerseccion.service;

import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.persona.service.PersonaServiceBean;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.UtilPlantilla;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.text.DateFormat;
import java.util.*;

@Stateless
public class OficioCertificadoInterseccionService {

    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    @EJB
    private OrganizacionFacade organizacionFacade;

    @EJB
    private PersonaServiceBean personaService;

    public String[] getParametrosCertificadoInterseccion(ProyectoLicenciamientoAmbiental proyecto,
                                                         String etiquetaRepresentanteLegal, String tituloTratamientoPersonaJuridica, String notaEnteAcreditado,
                                                         String comentarioInterseccion, Persona representante, Area areaResponsable,
                                                         CertificadoInterseccion certificadoInterseccion, String notaMdq,Integer marcaAgua) throws Exception {

        String[] parametros = new String[39];
        int i = 0;
        parametros[i++] = certificadoInterseccion.getCodigo();
        if (proyecto.getCatalogoCategoria().getCodigo().equals("21.02.03.05") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.04.03")|| 
    			proyecto.getCatalogoCategoria().getCodigo().equals("21.02.05.03") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.02.03")) {
    		parametros[i++] ="QUITO";
        }else{
        	parametros[i++] = obtenerCiudad(proyecto);
        }
        parametros[i++] = fechaActual();
        parametros[i++] = obtenerTipoTratoTitulo(representante);
        parametros[i++] = obtenerDescripcionDestinatario(representante, etiquetaRepresentanteLegal, proyecto.getUsuario());
        parametros[i++] = proyecto.getNombre().toUpperCase();
        parametros[i++] = getUbicacionProyecto(proyecto).toUpperCase();

        // antecedentes
        parametros[i++] = obtenerPrefijoProponente(representante, tituloTratamientoPersonaJuridica, proyecto.getUsuario());
        parametros[i++] = obtenerSujetoSeccionAntecedente(representante, proyecto.getUsuario());
        parametros[i++] = proyecto.getNombre().toUpperCase();
        parametros[i++] = getUbicacionProyecto(proyecto);
        // Analisis de la documento
        parametros[i++] = proyecto.getNombre().toUpperCase();
        parametros[i++] = getUbicacionProyecto(proyecto);
        parametros[i++] = comentarioInterseccion;

        parametros[i++] = obtenerPrefijoProponente(representante, tituloTratamientoPersonaJuridica, proyecto.getUsuario());
        parametros[i++] = obtenerSujetoSeccionAntecedente(representante, proyecto.getUsuario());
        parametros[i++] = proyecto.getCatalogoCategoria().getCodigo();
        parametros[i++] = proyecto.getCatalogoCategoria().getDescripcion();
        parametros[i++] = proyecto.getCatalogoCategoria().getTipoLicenciamiento().getNombre();

        parametros[i++] = proyecto.getCodigo();
        String[] param = {areaResponsable.getAreaName()};
        parametros[i++] = UtilPlantilla.getPlantillaConParametros(notaEnteAcreditado, param);
		parametros[i++] = notaMdq;

        return parametros;
    }

    private String obtenerTipoTratoTitulo(Persona representante) {
        return obtenerTipoTrato(representante) + " " + representante.getTitulo();
    }

    private String obtenerSujetoSeccionAntecedente(Persona representante, Usuario usuario) {
        return isPersonaJuridica(representante, usuario) ? obtenerNombreEmpresa(representante, usuario) : representante.getNombre();
    }

    private String obtenerTipoTrato(Persona representante) {
        return representante.getTipoTratos() == null ? "" : representante.getTipoTratos().getNombre();
    }

    private String obtenerPrefijoProponente(Persona representante, String tituloTratamientoPersonaJuridica, Usuario usuario) {
        return isPersonaJuridica(representante, usuario) ? tituloTratamientoPersonaJuridica : obtenerTipoTrato(representante);
    }

    private String obtenerDescripcionDestinatario(Persona representante, String etiquetaRepresentanteLegal, Usuario usuario) {
        if (isPersonaJuridica(representante, usuario)) {
            String cargo = "";
            for (Organizacion org : representante.getOrganizaciones()) {
                if (org.getRuc().equals(usuario.getNombre())) {
                    cargo = org.getCargoRepresentante();
                }
            }
            return representante.getNombre() + ((cargo == null) ? etiquetaRepresentanteLegal : "<br/>" + cargo + "<br/>")
                    + obtenerNombreEmpresa(representante, usuario);
        } else {
            return representante.getNombre();
        }

    }

    private String obtenerNombreEmpresa(Persona representante, Usuario usuario) {
        if (isPersonaJuridica(representante, usuario)) {
            for (Organizacion org : representante.getOrganizaciones()) {
                if (org.getRuc().equals(usuario.getNombre())) {
                    return org.getNombre();
                }
            }
        }
        return null;
    }

    public static String fechaActual() {
        DateFormat fecha = DateFormat.getDateInstance(DateFormat.FULL, new Locale("es"));
        return fecha.format(new Date());
    }

//    private boolean isPersonaJuridica(Persona persona) {
//
//        return (persona.getOrganizaciones() == null || persona.getOrganizaciones().isEmpty()) ? false : true;
//
//    }

    private boolean isPersonaJuridica(Persona persona, Usuario usuario) {

        try {
            return organizacionFacade.buscarPorPersona(persona, usuario.getNombre()) != null;
        } catch (ServiceException e) {
            return false;
        }

    }

    private String getUbicacionProyecto(ProyectoLicenciamientoAmbiental proyecto) throws Exception {
        List<UbicacionesGeografica> ubicaciones = proyecto.getUbicacionesGeograficas();
        Set<UbicacionesGeografica> ubicacionesProvincia = new LinkedHashSet<UbicacionesGeografica>();
        for (UbicacionesGeografica ubicacionesGeografica : ubicaciones) {
            ubicacionesProvincia.add(ubicacionesGeografica.getUbicacionesGeografica().getUbicacionesGeografica());
        }
        String stringUbicaciones = ubicacionesProvincia.toString();
        return stringUbicaciones.replace('[', '(').replace(']', ')');

    }

    private String obtenerCiudad(ProyectoLicenciamientoAmbiental proyecto) throws Exception {
        UbicacionesGeografica canton = proyecto.getPrimeraParroquia().getUbicacionesGeografica();
        return canton.getNombre();
    }
}
