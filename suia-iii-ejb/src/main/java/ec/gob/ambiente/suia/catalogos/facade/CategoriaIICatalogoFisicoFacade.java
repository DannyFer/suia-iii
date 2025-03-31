package ec.gob.ambiente.suia.catalogos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang.SerializationUtils;

import ec.gob.ambiente.suia.catalogos.service.CategoriaIICatalogoFisicoService;
import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;

@Stateless
public class CategoriaIICatalogoFisicoFacade {

	@EJB
	private CategoriaIICatalogoFisicoService categoriaIICatalogoFisicoService;

	public List<CategoriaIICatalogoGeneralFisico> catalogoSeleccionadosCategoriaIITipo(
			Integer idProyecto, String codigo) {
		return categoriaIICatalogoFisicoService
				.catalogoSeleccionadosCategoriaIITipo(idProyecto, codigo);
	}

	public List<CategoriaIICatalogoGeneralFisico> catalogoSeleccionadosCategoriaIITipo(
			Integer idProyecto, String codigo, String seccion) {
		return categoriaIICatalogoFisicoService
				.catalogoSeleccionadosCategoriaIITipo(idProyecto, codigo,
						seccion);
	}

	public List<CategoriaIICatalogoGeneralFisico> catalogoSeleccionadosCategoriaIITipo(
			Integer idProyecto, List<String> codigos, String seccion) {
		return categoriaIICatalogoFisicoService
				.catalogoSeleccionadosCategoriaIITipo(idProyecto, codigos,
						seccion);
	}

	public List<CategoriaIICatalogoGeneralFisico> climasSeleccionadosCategoriaII(
			Integer idProyecto) {
		return categoriaIICatalogoFisicoService
				.climasSeleccionadosCategoriaII(idProyecto);
	}

	public List<CategoriaIICatalogoGeneralFisico> tipoSuelosSeleccionadosCategoriaII(
			Integer idProyecto) {
		return categoriaIICatalogoFisicoService
				.tipoSuelosSeleccionadosCategoriaII(idProyecto);
	}

	// -----------------------------------

	public void guardarCatalogoGeneralFisico(
			List<CategoriaIICatalogoGeneralFisico> catalogos, String tipo,
			FichaAmbientalPma ficha, Boolean eliminar) {

		if (eliminar) {
			categoriaIICatalogoFisicoService
					.eliminarCatalogoaGeneralFisicoCategoriaIIPorFichaCodigo(
							ficha, tipo);
		}
		categoriaIICatalogoFisicoService
				.guardarCatalogoGeneralFisico(catalogos);
	}

	public void guardarCatalogoGeneralFisico(
			List<CategoriaIICatalogoGeneralFisico> catalogos, String tipo,
			FichaAmbientalPma ficha, Boolean eliminar, String seccion) {

		if (eliminar) {
			categoriaIICatalogoFisicoService
					.eliminarCatalogoaGeneralFisicoCategoriaIIPorFichaCodigo(
							ficha, tipo, seccion);
		}
		categoriaIICatalogoFisicoService
				.guardarCatalogoGeneralFisico(catalogos);
	}

	public void guardarCatalogoGeneralFisico(
			List<CategoriaIICatalogoGeneralFisico> catalogos,
			List<String> tipo, FichaAmbientalPma ficha, Boolean eliminar,
			String seccion) {

		if (eliminar) {
			categoriaIICatalogoFisicoService
					.eliminarCatalogoaGeneralFisicoCategoriaIIPorFichaCodigo(
							ficha, tipo, seccion);
		}
		categoriaIICatalogoFisicoService
				.guardarCatalogoGeneralFisico(catalogos);
	}

	public void guardarCatalogoGeneralFisicoClima(
			CategoriaIICatalogoGeneralFisico clima, FichaAmbientalPma ficha,
			Boolean eliminar) {
		List<CategoriaIICatalogoGeneralFisico> catalogos = new ArrayList<CategoriaIICatalogoGeneralFisico>();
		catalogos.add(clima);
		if (eliminar) {
			categoriaIICatalogoFisicoService.eliminarClimaPorFicha(ficha);
		}
		categoriaIICatalogoFisicoService
				.guardarCatalogoGeneralFisico(catalogos);
	}

	public void guardarCatalogoGeneralFisicoTipoSuelo(
			List<CategoriaIICatalogoGeneralFisico> catalogos,
			FichaAmbientalPma ficha, Boolean eliminar) {

		if (eliminar) {
			categoriaIICatalogoFisicoService.eliminarTipoSueloPorFicha(ficha);
		}
		categoriaIICatalogoFisicoService
				.guardarCatalogoGeneralFisico(catalogos);
	}
	
	
	/**
	 * Cris F: Historico Descripcion area implantacion
	 */
	public void guardarCatalogoGeneralFisicoHistorico(
			List<CategoriaIICatalogoGeneralFisico> catalogos,
			List<String> tipo, FichaAmbientalPma ficha, Boolean eliminar,
			String seccion) {
		
		try {
			List<CategoriaIICatalogoGeneralFisico> listaCatalogos = new ArrayList<CategoriaIICatalogoGeneralFisico>();
			
			List<CategoriaIICatalogoGeneralFisico> listaCatalogosBddAux = new ArrayList<CategoriaIICatalogoGeneralFisico>();				
			listaCatalogos.addAll(catalogos);
			
			List<CategoriaIICatalogoGeneralFisico> listaCatalogosBdd = 
					categoriaIICatalogoFisicoService.obtenerCatalogoGeneralFisicoCategoriaIIPorFichaCodigo(ficha, tipo, seccion);
			
			
			if(listaCatalogosBdd != null && !listaCatalogosBdd.isEmpty()){
				listaCatalogosBddAux.addAll(listaCatalogosBdd);
				for(CategoriaIICatalogoGeneralFisico catalogoBdd : listaCatalogosBdd){
					for(CategoriaIICatalogoGeneralFisico catalogoActual : catalogos){
						if(catalogoActual.getCatalogo() != null){
							if(catalogoBdd.getCatalogo().getId() == catalogoActual.getCatalogo().getId() && 
									catalogoActual.getValor() == null && catalogoBdd.getValor() == null ){
								listaCatalogosBddAux.remove(catalogoBdd);
								listaCatalogos.remove(catalogoActual);
							}else if(catalogoBdd.getCatalogo().getId() == catalogoActual.getCatalogo().getId() && 
									(catalogoBdd.getValor() != null && catalogoActual.getValor() != null && 
									catalogoBdd.getValor().equals(catalogoActual.getValor()))){
								listaCatalogosBddAux.remove(catalogoBdd);
								listaCatalogos.remove(catalogoActual);
							}
						}else{
							listaCatalogos.remove(catalogoActual);
						}					
					}
				}	
				
				for(CategoriaIICatalogoGeneralFisico catalogo : listaCatalogosBddAux){
					
					CategoriaIICatalogoGeneralFisico catalogoHistorico = (CategoriaIICatalogoGeneralFisico)SerializationUtils.clone(catalogo);
					catalogoHistorico.setId(null);
					catalogoHistorico.setIdRegistroOriginal(catalogo.getId());
					catalogoHistorico.setFechaHistorico(new Date());
					categoriaIICatalogoFisicoService.guardarCatalogoGeneral(catalogoHistorico);
										
					catalogo.setEstado(false);
					categoriaIICatalogoFisicoService.guardarCatalogoGeneral(catalogo);
				}	
			}	
			
			for(CategoriaIICatalogoGeneralFisico catalogoNuevo : listaCatalogos){
				if(listaCatalogosBdd != null && !listaCatalogosBdd.isEmpty())
					catalogoNuevo.setFechaHistorico(new Date());
				
				categoriaIICatalogoFisicoService.guardarCatalogoGeneral(catalogoNuevo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/*
	 * Cris F: historico
	 */
	
	public List<CategoriaIICatalogoGeneralFisico> catalogoSeleccionadosCategoriaIITipoHistorico(
			Integer idProyecto, List<String> codigos, String seccion) {
		return categoriaIICatalogoFisicoService
				.catalogoSeleccionadosCategoriaIITipoHistorico(idProyecto, codigos, seccion);
	}
}
