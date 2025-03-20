package ec.gob.ambiente.suia.catalogos.facade;


import ec.gob.ambiente.suia.catalogos.service.CatalogoLibroRojoService;
import ec.gob.ambiente.suia.domain.CatalogoLibroRojo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class CatalogoLibroRojoFacade {

    @EJB
    private CatalogoLibroRojoService catalogoLibroRojoService;

    public List<CatalogoLibroRojo> obtenerListaCatalogoLibroRojo() {
        return catalogoLibroRojoService.obtenerListaCatalogoLibroRojo();
    }

}
