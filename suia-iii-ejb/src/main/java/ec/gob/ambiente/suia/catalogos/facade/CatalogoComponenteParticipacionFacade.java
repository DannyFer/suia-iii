package ec.gob.ambiente.suia.catalogos.facade;


import ec.gob.ambiente.suia.catalogos.service.CatalogoComponenteParticipacionService;
import ec.gob.ambiente.suia.domain.CatalogoComponenteParticipacion;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class CatalogoComponenteParticipacionFacade {

    @EJB
    private CatalogoComponenteParticipacionService catalogoComponenteParticipacionService;

    public List<CatalogoComponenteParticipacion> obtenerListaCatalogoLibroRojo() {
        return catalogoComponenteParticipacionService.obtenerListaCatalogoComponenteParticipacion();
    }

    public List<CatalogoComponenteParticipacion> obtenerListaCatalogoLibroRojoNotHidrocarburo() {
        return catalogoComponenteParticipacionService.obtenerListaCatalogoComponenteParticipacionNotHidro();
    }

}
