package ec.gob.ambiente.suia.catalogos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang.SerializationUtils;

import ec.gob.ambiente.suia.catalogos.service.CategoriaIICatalogoBioticoService;
import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneralBiotico;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;

@Stateless
public class CategoriaIICatalogoBioticoFacade {

	@EJB
	private CategoriaIICatalogoBioticoService categoriaIICatalogoBioticoService;

	public List<CategoriaIICatalogoGeneralBiotico> catalogoSeleccionadosCategoriaIITipo(
			Integer idProyecto, String codigo) {
		return categoriaIICatalogoBioticoService
				.catalogoSeleccionadosCategoriaIITipo(idProyecto, codigo);
	}

	public List<CategoriaIICatalogoGeneralBiotico> catalogoSeleccionadosCategoriaIITipo(
			Integer idProyecto, String codigo, String seccion) {
		return categoriaIICatalogoBioticoService
				.catalogoSeleccionadosCategoriaIITipo(idProyecto, codigo,
						seccion);
	}

	public List<CategoriaIICatalogoGeneralBiotico> catalogoSeleccionadosCategoriaIITipo(
			Integer idProyecto, List<String> codigos, String seccion) {
		return categoriaIICatalogoBioticoService
				.catalogoSeleccionadosCategoriaIITipo(idProyecto, codigos,
						seccion);
	}

	// -----------------------------------

	public void guardarCatalogoGeneralBiotico(
			List<CategoriaIICatalogoGeneralBiotico> catalogos, String tipo,
			FichaAmbientalPma ficha, Boolean eliminar) {

		if (eliminar) {
			categoriaIICatalogoBioticoService
					.eliminarCatalogoaGeneralBioticoCategoriaIIPorFichaCodigo(
							ficha, tipo);
		}
		categoriaIICatalogoBioticoService
				.guardarCatalogoGeneralBiotico(catalogos);
	}

	public void guardarCatalogoGeneralBiotico(
			List<CategoriaIICatalogoGeneralBiotico> catalogos, String tipo,
			FichaAmbientalPma ficha, Boolean eliminar, String seccion) {

		if (eliminar) {
			categoriaIICatalogoBioticoService
					.eliminarCatalogoaGeneralBioticoCategoriaIIPorFichaCodigo(
							ficha, tipo, seccion);
		}
		categoriaIICatalogoBioticoService
				.guardarCatalogoGeneralBiotico(catalogos);
	}

	public void guardarCatalogoGeneralBiotico(
			List<CategoriaIICatalogoGeneralBiotico> catalogos,
			List<String> tipo, FichaAmbientalPma ficha, Boolean eliminar,
			String seccion) {

		if (eliminar) {
			categoriaIICatalogoBioticoService
					.eliminarCatalogoaGeneralBioticoCategoriaIIPorFichaCodigo(
							ficha, tipo, seccion);
		}
		categoriaIICatalogoBioticoService
				.guardarCatalogoGeneralBiotico(catalogos);
	}
	
	/**
	 * Cris F: guardado del historico
	 */
	
	public void guardarCatalogoGeneralBioticoHistorico(
			List<CategoriaIICatalogoGeneralBiotico> catalogos,
			List<String> tipo, FichaAmbientalPma ficha, Boolean eliminar,
			String seccion) {

		try {
			
			List<CategoriaIICatalogoGeneralBiotico> listaCatalogos = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
			List<CategoriaIICatalogoGeneralBiotico> listaCatalogosBddAux = new ArrayList<CategoriaIICatalogoGeneralBiotico>();
			
			listaCatalogos.addAll(catalogos);
			
			List<CategoriaIICatalogoGeneralBiotico> listaCatalogosBdd = 
					categoriaIICatalogoBioticoService.obtenerCatalogoGeneralBioticoCategoriaIIPorFichaCodigo(ficha, tipo, seccion);
			
			if(listaCatalogosBdd != null && !listaCatalogosBdd.isEmpty()){
				listaCatalogosBddAux.addAll(listaCatalogosBdd);
				
				
				for(CategoriaIICatalogoGeneralBiotico catalogoBdd : listaCatalogosBdd){
					for(CategoriaIICatalogoGeneralBiotico catalogoActual : catalogos){
						if(catalogoActual.getCatalogo() != null){
							if(catalogoBdd.getCatalogo().getId() == catalogoActual.getCatalogo().getId()){
								listaCatalogosBddAux.remove(catalogoBdd);
								listaCatalogos.remove(catalogoActual);
							}						
						}else{
							listaCatalogos.remove(catalogoActual);
						}
					}
				}
				
				for(CategoriaIICatalogoGeneralBiotico catalogo : listaCatalogosBddAux){
					CategoriaIICatalogoGeneralBiotico catalogoHistorico = (CategoriaIICatalogoGeneralBiotico)SerializationUtils.clone(catalogo);
					catalogoHistorico.setId(null);
					catalogoHistorico.setIdRegistroOriginal(catalogo.getId());
					catalogoHistorico.setFechaHistorico(new Date());
					categoriaIICatalogoBioticoService.guardarCatalogoGeneralBiotico(catalogoHistorico);
					
					catalogo.setEstado(false);
					categoriaIICatalogoBioticoService.guardarCatalogoGeneralBiotico(catalogo);
				}
			}
			
			for(CategoriaIICatalogoGeneralBiotico catalogoNuevo : listaCatalogos){
				if(listaCatalogosBdd != null && !listaCatalogosBdd.isEmpty())
					catalogoNuevo.setFechaHistorico(new Date());
				categoriaIICatalogoBioticoService.guardarCatalogoGeneralBiotico(catalogoNuevo);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<CategoriaIICatalogoGeneralBiotico> catalogoSeleccionadosCategoriaIITipoHistorico(
			Integer idProyecto, List<String> codigos, String seccion) {
		return categoriaIICatalogoBioticoService
				.catalogoSeleccionadosCategoriaIITipoHistorico(idProyecto, codigos,
						seccion);
	}
	
	//fin historico
}
