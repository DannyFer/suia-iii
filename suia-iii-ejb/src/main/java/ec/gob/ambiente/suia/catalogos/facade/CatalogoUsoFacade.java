package ec.gob.ambiente.suia.catalogos.facade;


import ec.gob.ambiente.suia.catalogos.service.CatalogoUsoService;
import ec.gob.ambiente.suia.domain.CatalogoUso;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class CatalogoUsoFacade {

    @EJB
    private CatalogoUsoService catalogoUsoService;

    public List<CatalogoUso> obtenerCatalogosUsos() {
        return catalogoUsoService.obtenerListaCatalogosUsos();
    }

    public List<CatalogoUso> obtenerCatalogosUsosByNormativa() {
        return catalogoUsoService.obtenerCatalogosUsosByNormativa();
    }

}
