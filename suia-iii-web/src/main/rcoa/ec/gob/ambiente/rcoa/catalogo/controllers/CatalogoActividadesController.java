package ec.gob.ambiente.rcoa.catalogo.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.facade.CatalogoCIUUFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;

@ManagedBean
@ViewScoped
public class CatalogoActividadesController {
	
	@EJB
	private CatalogoCIUUFacade catalogoCIUUFacade;
	
	@Getter
	@Setter
	private List<CatalogoCIUU> listaCatalogoCiiu = new ArrayList<CatalogoCIUU>();
	
	@PostConstruct
	public void inicio()
	{
		listaCatalogoCiiu=catalogoCIUUFacade.listaActiviadesCiuuDisponibles();
	}

}
