package ec.gob.ambiente.suia.proyectos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.ProyectoGeneralCatalogo;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.proyectos.service.ProyectoGeneralCatalogoServiceBean;

@Stateless
public class ProyectoGeneralCatalogoFacade {

	@EJB
	private ProyectoGeneralCatalogoServiceBean proyectoGeneralCatalogoServiceBean;

	public void guardar(ProyectoGeneralCatalogo proyectoGeneralCatalogo,
			ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental, List<CatalogoGeneral> catalogosGeneral,
			TdrEiaLicencia tdrEiaLicencia) {
		if (!proyectoGeneralCatalogo.isPersisted())

			proyectoGeneralCatalogo.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
		proyectoGeneralCatalogo.setTdrEiaLicencia(tdrEiaLicencia);

		for (CatalogoGeneral catalogoGeneral : catalogosGeneral) {
			proyectoGeneralCatalogo.setCatalogoGeneral(catalogoGeneral);
		}

		proyectoGeneralCatalogoServiceBean.guardarProyectoGeneralCatalogo(proyectoGeneralCatalogo);
	}

	public List<ProyectoGeneralCatalogo> listarProyectoGeneralCatalogoPorProyecto(Integer idProyecto) {
		return proyectoGeneralCatalogoServiceBean.listarProyectoGeneralCatalogoPorProyecto(idProyecto);
	}

	public void saveOrUpdateProyectoGeneralCatalogo(List<ProyectoGeneralCatalogo> listaProyectoGeneralCatalogo) {
		proyectoGeneralCatalogoServiceBean.saveOrUpdateProyectoGeneralCatalogo(listaProyectoGeneralCatalogo);
	}
}
