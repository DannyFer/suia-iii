package ec.gob.ambiente.suia.catalogos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang.SerializationUtils;

import ec.gob.ambiente.suia.catalogos.service.CategoriaIICatalogoSocialService;
import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneralSocial;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;

@Stateless
public class CategoriaIICatalogoSocialFacade {

	@EJB
	private CategoriaIICatalogoSocialService categoriaIICatalogoSocialService;

	public List<CategoriaIICatalogoGeneralSocial> catalogoSeleccionadosCategoriaIITipo(
			Integer idProyecto, String codigo) {
		return categoriaIICatalogoSocialService
				.catalogoSeleccionadosCategoriaIITipo(idProyecto, codigo);
	}

	public List<CategoriaIICatalogoGeneralSocial> catalogoSeleccionadosCategoriaIITipo(
			Integer idProyecto, String codigo, String seccion) {
		return categoriaIICatalogoSocialService
				.catalogoSeleccionadosCategoriaIITipo(idProyecto, codigo,
						seccion);
	}


	public List<CategoriaIICatalogoGeneralSocial> catalogoSeleccionadosCategoriaIITipo(
			Integer idProyecto, List<String> codigo, String seccion) {
		return categoriaIICatalogoSocialService
				.catalogoSeleccionadosCategoriaIITipo(idProyecto, codigo,
						seccion);
	}

	// -----------------------------------

	public void guardarCatalogoGeneralSocial(
			List<CategoriaIICatalogoGeneralSocial> catalogos, String tipo,
			FichaAmbientalPma ficha, Boolean eliminar) {

		if (eliminar) {
			categoriaIICatalogoSocialService
					.eliminarCatalogoaGeneralSocialCategoriaIIPorFichaCodigo(
							ficha, tipo);
		}
		categoriaIICatalogoSocialService
				.guardarCatalogoGeneralSocial(catalogos);
	}

	public void guardarCatalogoGeneralSocial(
			List<CategoriaIICatalogoGeneralSocial> catalogos, String tipo,
			FichaAmbientalPma ficha, Boolean eliminar, String seccion) {

		if (eliminar) {
			categoriaIICatalogoSocialService
					.eliminarCatalogoaGeneralSocialCategoriaIIPorFichaCodigo(
							ficha, tipo, seccion);
		}
		categoriaIICatalogoSocialService
				.guardarCatalogoGeneralSocial(catalogos);
	}
	public void guardarCatalogoGeneralSocial(
			List<CategoriaIICatalogoGeneralSocial> catalogos, List<String> tipo,
			FichaAmbientalPma ficha, Boolean eliminar, String seccion) {

		if (eliminar) {
			categoriaIICatalogoSocialService
					.eliminarCatalogoaGeneralSocialCategoriaIIPorFichaCodigo(
							ficha, tipo, seccion);
		}
		categoriaIICatalogoSocialService
				.guardarCatalogoGeneralSocial(catalogos);
	}
	
	
	//Cris F: métodos de historico
	public void guardarCatalogoGeneralSocialHistorico(List<CategoriaIICatalogoGeneralSocial> catalogos, List<String> tipo,
			FichaAmbientalPma ficha, Boolean eliminar, String seccion) {
		
		try {
			List<CategoriaIICatalogoGeneralSocial> listaCatalogos = new ArrayList<CategoriaIICatalogoGeneralSocial>();
			List<CategoriaIICatalogoGeneralSocial> listaCatalogosBddAux = new ArrayList<CategoriaIICatalogoGeneralSocial>();
			
			listaCatalogos.addAll(catalogos);
			
			List<CategoriaIICatalogoGeneralSocial> listaCatalogosBdd = 
					categoriaIICatalogoSocialService.obtenerCatalogoGeneralSocialCategoriaIIPorFichaCodigo(ficha, tipo, seccion);
			
			if(listaCatalogosBdd != null && !listaCatalogosBdd.isEmpty()){
				listaCatalogosBddAux.addAll(listaCatalogosBdd);
				
				for(CategoriaIICatalogoGeneralSocial catalogoBdd : listaCatalogosBdd){
					for(CategoriaIICatalogoGeneralSocial catalogoActual : catalogos){
						if(catalogoActual.getCatalogo() != null){
							if(catalogoBdd.getCatalogo().getId() == catalogoActual.getCatalogo().getId() && 
									(catalogoBdd.getValor() == null && catalogoActual.getValor() == null)){
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
				
				for(CategoriaIICatalogoGeneralSocial catalogo : listaCatalogosBddAux){
					
					CategoriaIICatalogoGeneralSocial catalogoHistorico = (CategoriaIICatalogoGeneralSocial)SerializationUtils.clone(catalogo);
					catalogoHistorico.setId(null);
					catalogoHistorico.setIdRegistroOriginal(catalogo.getId());
					catalogoHistorico.setFechaHistorico(new Date());
					categoriaIICatalogoSocialService.guardarCatalogoGeneralSocial(catalogoHistorico);
					
					catalogo.setEstado(false);
					categoriaIICatalogoSocialService.guardarCatalogoGeneralSocial(catalogo);
				}
			}
			
			for(CategoriaIICatalogoGeneralSocial catalogoNuevo : listaCatalogos){
				if(listaCatalogosBdd != null && !listaCatalogosBdd.isEmpty())
					catalogoNuevo.setFechaHistorico(new Date());
				categoriaIICatalogoSocialService.guardarCatalogoGeneralSocial(catalogoNuevo);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public List<CategoriaIICatalogoGeneralSocial> catalogoSeleccionadosCategoriaIITipoHistorico(
			Integer idProyecto, List<String> codigo, String seccion) {
		return categoriaIICatalogoSocialService
				.catalogoSeleccionadosCategoriaIITipoHistorico(idProyecto, codigo,
						seccion);
	}
	
	//Fin metodos historico
}
