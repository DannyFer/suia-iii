/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.entidadResponsable.facade;

import java.util.ArrayList;
import java.util.List;

import ec.gob.ambiente.suia.AutorizacionCatalogo.facade.AutorizacionCatalogoFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreasAutorizadasCatalogo;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.tipoarea.facade.TipoAreaFacade;
import ec.gob.ambiente.suia.utils.Constantes;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 10/02/2015]
 *          </p>
 */
@Stateless
public class EntidadResponsableFacade {

	private static final int ID_TIPO_AREA_ENTE_ACREDITADO = 3;
	private static final int ID_TIPO_AREA_DIRECCION_PROVINCIAL = 2;

	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionService;

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private AutorizacionCatalogoFacade autorizacionCatalogoFacade;

	@EJB
	private TipoAreaFacade tipoAreaFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade;

	/**
	 * Aqui va la logica para determinar si el proyecto lo atiende un ente
	 * acreditado, planta central o direccion provincial
	 * 
	 * @param proyecto
	 * @return
	 * @throws Exception
	 */
	public Area obtenerEntidadResponsable(
			ProyectoLicenciamientoAmbiental proyecto)
			throws Exception {
		Area area = obtenerEntidadResponsableProyecto(proyecto);
		if (area == null)
			area = getDireccionNacionalControlAmbiental();
		return area;
	}

	/**
	 * Metodo para determinar si el proyecto lo atiende un ente acreditado,
	 * planta central o direccion provincial
	 * 
	 * @param proyecto
	 * @return
	 * @throws Exception
	 */
	
	private Area obtenerEntidadResponsableProyecto(
			ProyectoLicenciamientoAmbiental proyecto) throws Exception {
		
		Boolean interseca = false;
		final Boolean isEstrategico = proyecto.getCatalogoCategoria()
				.getEstrategico();
		final Boolean isAcreditado = proyecto.getCatalogoCategoria()
				.getTipoArea().getId().equals(ID_TIPO_AREA_ENTE_ACREDITADO);

		Area areaPC = areaFacade
				.getArea(Constantes.ID_DIRECCION_NACIONAL_CONTROL_AMBIENTAL);
		UbicacionesGeografica parroquia = proyecto.getPrimeraParroquia();
		UbicacionesGeografica provincia = proyecto.getPrimeraProvincia();
		
		List<UbicacionesGeografica>listaProvincias=new ArrayList<UbicacionesGeografica>();
		listaProvincias=proyecto.getUbicacionesGeograficas();
		boolean variasProvincias=false;
		String codigoInecProvincia=proyecto.getPrimeraProvincia().getCodificacionInec();
		for (UbicacionesGeografica ubicacionesGeografica : listaProvincias) {
				if(ubicacionesGeografica.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec()!=null && codigoInecProvincia.compareTo(ubicacionesGeografica.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec())!=0){
					variasProvincias=true;
					break;
				}
		}
		
		boolean variosCantones=false;
		String codigoInecCanton=proyecto.getPrimeraParroquia().getUbicacionesGeografica().getCodificacionInec();
		for (UbicacionesGeografica ubicacionesGeografica : listaProvincias) {
				if(codigoInecCanton.compareTo(ubicacionesGeografica.getUbicacionesGeografica().getCodificacionInec())!=0){
					variosCantones=true;
					break;
				}
		}
		
		
		Boolean esEnteRepresentante =false;

		int valorcapa = certificadoInterseccionService
				.getDetalleInterseccionlista(proyecto.getCodigo());

		if (valorcapa == 3) {
			interseca = certificadoInterseccionService
					.isProyectoIntersecaCapas(proyecto.getCodigo());
			if(interseca && !provincia.getCodificacionInec().startsWith("20"))
			{				
				List<UbicacionesGeografica> parroquias=proyecto.getUbicacionesGeograficas();
				for (UbicacionesGeografica ubicacion : parroquias) {
					if(ubicacion.getCodificacionInec()!=null && ubicacion.getCodificacionInec().startsWith("20"))
					{
						parroquia=ubicacion;
						provincia=ubicacion.getUbicacionesGeografica().getUbicacionesGeografica();
						break;
					}
				}
			}
		} else {
			try {
				if ((parroquia.getEnteAcreditado()) == null
						| parroquia.getEnteAcreditado().getId().equals(556)) {
					interseca = false;
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		
		Area res=null;
		if (isAcreditado){
			res=areaFacade.getAreaEnteAcreditado(ID_TIPO_AREA_ENTE_ACREDITADO, proyecto.getUsuario().getNombre());					
		}
		if (res !=null){
			esEnteRepresentante=true;
		}else{
			esEnteRepresentante = proyecto.getAreaResponsable() != null
					&& proyecto.getAreaResponsable().getIdentificacionEnte() != null
					&& proyecto
							.getUsuario()
							.getNombre()
							.equals(proyecto.getAreaResponsable()
									.getIdentificacionEnte());
		}

		if (isEstrategico) {
			return areaPC;
		} else if (!isAcreditado) {
			return buscarAreaDireccionProvincialPorUbicacion(provincia);

		} else if (interseca) {			
			return buscarAreaDireccionProvincialPorUbicacion(provincia);
		} else {
			Area arearesponsables =new Area();
			Area enteParroquia = buscarAreaEntePorUbicacion(parroquia);
			Area entemunicipio = buscarAreaEnteMunicipioPorUbicacion(parroquia);
			String usuariomtop= Constantes.getUsuarioMtop();				
			String usuarioMtopArray[]={};			
			usuarioMtopArray= usuariomtop.split(",");			
			Integer valorusu=0;
			Integer valoract=0;
			
			 
			
			for (String usuarioMt : usuarioMtopArray) {
				if (proyecto.getUsuario().getNombre().contains(usuarioMt)){
					valorusu=1;
					break;
				}				
			}
			if (proyecto.getCatalogoCategoria().getActividadMtop()==null){
				valoract=0;
			}else{
			if (proyecto.getCatalogoCategoria().getActividadMtop()){
					valoract=1;
				}
			}
			if (valorusu==1 && valoract==1){
				return buscarAreaDireccionProvincialPorUbicacion(provincia);				
			}else{
			if (esEnteRepresentante) {				
				if (enteParroquia==null){
					return buscarAreaDireccionProvincialPorUbicacion(provincia);
				}else{
					if (enteParroquia.getTipoEnteAcreditado().equals("MUNICIPIO")) {
						if (proyecto.getCatalogoCategoria().getCodigo().equals("21.02.08.01") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.01.02")) {	
							if (enteParroquia==entemunicipio){
								if (res.getTipoEnteAcreditado().equals("MUNICIPIO")){
									arearesponsables= buscarAreaDireccionProvincialPorUbicacion(provincia);
								}else{
									if (res.getTipoEnteAcreditado().equals("GOBIERNO")){
										arearesponsables= buscarAreaDireccionProvincialPorUbicacion(provincia);
									}else{
										arearesponsables= enteParroquia;
									}
								}
							}else{
								arearesponsables= enteParroquia;
							}						
						}else{
							//aki la logica
							if (buscarAreaDireccionPorRango(enteParroquia.getUbicacionesGeografica().getId())!=null){
								if (enteParroquia==entemunicipio){
									if (res.getTipoEnteAcreditado().equals("MUNICIPIO")){
										Area arearespo= buscarAreaDireccionPorRango(enteParroquia.getUbicacionesGeografica().getId());
										AreasAutorizadasCatalogo areasAutorizadasCatalogo= autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(proyecto.getCatalogoCategoria(), arearespo);
										if (areasAutorizadasCatalogo!=null ){
											arearesponsables= areasAutorizadasCatalogo.getAreaResponsable();
										}else{
											arearesponsables= buscarAreaDireccionPorRango(enteParroquia.getUbicacionesGeografica().getId());
										}
									}else{
										if (res.getTipoEnteAcreditado().equals("GOBIERNO")){
//											return buscarAreaDireccionPorRango(enteParroquia.getUbicacionesGeografica().getId());
											Area arearespo= buscarAreaDireccionPorRango(enteParroquia.getUbicacionesGeografica().getId());
											AreasAutorizadasCatalogo areasAutorizadasCatalogo= autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(proyecto.getCatalogoCategoria(), arearespo);
											if (areasAutorizadasCatalogo!=null ){
												arearesponsables= areasAutorizadasCatalogo.getAreaResponsable();
											}else{
												arearesponsables= buscarAreaDireccionProvincialPorUbicacion(provincia);
											}																					
										}else{											
											Area responsable= enteParroquia;
											AreasAutorizadasCatalogo areasAutorizadasCatalogomunicipio= autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(proyecto.getCatalogoCategoria(), responsable);
											if (areasAutorizadasCatalogomunicipio!=null ){
												arearesponsables= areasAutorizadasCatalogomunicipio.getAreaResponsable();
											}else{
												arearesponsables= enteParroquia;
											}																					
										}
									}
								}else{
									arearesponsables= buscarAreaDireccionPorRango(enteParroquia.getUbicacionesGeografica().getId());
								}						
							}else{
								arearesponsables= buscarAreaDireccionProvincialPorUbicacion(provincia);
							}			
						}
					}else if (enteParroquia.getTipoEnteAcreditado().equals("GOBIERNO")){
						if (proyecto.getCatalogoCategoria().getCodigo().equals("21.02.08.01") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.01.02")) {
							if (buscarAreaMunicipioPorRango(enteParroquia.getUbicacionesGeografica().getId())!=null){
//								return entemunicipio;
								if (res.getTipoEnteAcreditado().equals("GOBIERNO")){
									arearesponsables= buscarAreaDireccionProvincialPorUbicacion(provincia);
								}else{
									if (res.getTipoEnteAcreditado().equals("MUNICIPIO")){
										arearesponsables= buscarAreaDireccionProvincialPorUbicacion(provincia);
									}else{
										arearesponsables= entemunicipio;
									}
								}
							}else{
								return buscarAreaDireccionProvincialPorUbicacion(provincia);
							}
						}else if (!proyecto.getUsuarioCreacion().equals(enteParroquia.getIdentificacionEnte())){														
							Area responsable= enteParroquia;
							AreasAutorizadasCatalogo areasAutorizadasCatalogomunicipio= autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(proyecto.getCatalogoCategoria(), responsable);
							if (areasAutorizadasCatalogomunicipio!=null ){
								arearesponsables= areasAutorizadasCatalogomunicipio.getAreaResponsable();
							}else{
								arearesponsables= enteParroquia;
							}														
						}else{
							Area responsable= buscarAreaDireccionProvincialPorUbicacion(provincia);
							AreasAutorizadasCatalogo areasAutorizadasCatalogomunicipio= autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(proyecto.getCatalogoCategoria(), responsable);
							if (areasAutorizadasCatalogomunicipio!=null ){
								arearesponsables= areasAutorizadasCatalogomunicipio.getAreaResponsable();
							}else{
								arearesponsables= buscarAreaDireccionProvincialPorUbicacion(provincia);
							}
						}										
					} else {						
						Area responsable= enteParroquia;
						AreasAutorizadasCatalogo areasAutorizadasCatalogomunicipio= autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(proyecto.getCatalogoCategoria(), responsable);
						if (areasAutorizadasCatalogomunicipio!=null ){
							arearesponsables= areasAutorizadasCatalogomunicipio.getAreaResponsable();
						}else{
							arearesponsables= enteParroquia;
						}
					}
				}
			} else {

				if (proyecto.getCatalogoCategoria().getCodigo()
						.equals("21.02.08.01")
						|| proyecto.getCatalogoCategoria().getCodigo()
								.equals("21.02.01.02")) {
//aqui modificar
					if (parroquia.getEnteAcreditadomunicipio() != null) {
						arearesponsables = parroquia.getEnteAcreditadomunicipio();
						if(variasProvincias || variosCantones)
						arearesponsables = buscarAreaDireccionProvincialPorUbicacion(provincia);
					} else {
						if (enteParroquia != null) {
							if (enteParroquia.getTipoEnteAcreditado().equals(
									"GOBIERNO")) {
								arearesponsables= buscarAreaDireccionProvincialPorUbicacion(provincia);
							} else {
								arearesponsables= enteParroquia;
							}
						} else {
							arearesponsables= buscarAreaDireccionProvincialPorUbicacion(provincia);
						}
					}
				}else{
//					//aki logica para el nuevo requerimiento
//					AreasAutorizadasCatalogo areasAutorizadasCatalogomunicipio= autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(proyecto.getCatalogoCategoria(), entemunicipio);
//					AreasAutorizadasCatalogo areasAutorizadasCatalogoGobierno= autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(proyecto.getCatalogoCategoria(), enteParroquia);
//					
//					if (areasAutorizadasCatalogomunicipio!=null || areasAutorizadasCatalogoGobierno !=null){
//						return areasAutorizadasCatalogoGobierno.getAreaResponsable();
//					}else if (areasAutorizadasCatalogomunicipio.getArea().getAreaName().equals(areasAutorizadasCatalogoGobierno.getArea().getAreaName())){
//						return areasAutorizadasCatalogoGobierno.getAreaResponsable();
//					}else{	
						if (enteParroquia != null && !variasProvincias) {
							Area responsable= enteParroquia;
							AreasAutorizadasCatalogo areasAutorizadasCatalogomunicipio= autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(proyecto.getCatalogoCategoria(), responsable);
							if (areasAutorizadasCatalogomunicipio!=null ){
								arearesponsables= areasAutorizadasCatalogomunicipio.getAreaResponsable();
							}else{
								if (proyecto.getFinanciadoEstado()!=null){
									if (proyecto.getFinanciadoEstado().equals("SI")){
										arearesponsables= buscarAreaDireccionProvincialPorUbicacion(provincia);
									}else{
										arearesponsables= enteParroquia;
									}									
								}else{
									arearesponsables= enteParroquia;
									if(variosCantones){
										arearesponsables=buscarAreaGadProvincialPorUbicacion(provincia);
									}									
								}
							}
						} else {
							arearesponsables= buscarAreaDireccionProvincialPorUbicacion(provincia);
						}
//				}
				}
			}
		}
			if (proyecto.getFinanciadoEstado()!=null){
				if (proyecto.getFinanciadoEstado().equals("SI")){
					return buscarAreaDireccionProvincialPorUbicacion(provincia);
				}else{
					return arearesponsables;
				}
			}else{
				return arearesponsables;
			}
		}
	}

	public Area getDireccionNacionalControlAmbiental() {
		return crudServiceBean.find(Area.class, Constantes.ID_DIRECCION_NACIONAL_CONTROL_AMBIENTAL);
	}	

	private Area buscarAreaEntePorUbicacion(UbicacionesGeografica parroquia) {
		Area area = areaFacade.getAreaEntePorUbicacion(ID_TIPO_AREA_ENTE_ACREDITADO, parroquia);
		return area;
	}
	
	private Area buscarAreaGadProvincialPorUbicacion(UbicacionesGeografica provincia) {
		Area area = areaFacade.getAreaGadProvincialPorUbicacion(ID_TIPO_AREA_ENTE_ACREDITADO, provincia);
		return area;
	}

	public Area buscarAreaDireccionProvincialPorUbicacion(UbicacionesGeografica provincia) {
		Area area = areaFacade.getAreaDireccionProvincialPorUbicacion(ID_TIPO_AREA_DIRECCION_PROVINCIAL, provincia);
		return area;
	}

	public Area buscarAreaDireccionProvincialPorUbicacion(ProyectoLicenciamientoAmbiental proyecto) throws Exception {
		return areaFacade.getAreaDireccionProvincialPorUbicacion(ID_TIPO_AREA_DIRECCION_PROVINCIAL, proyecto.getPrimeraProvincia());
	
	}
	
	public Area buscarAreaDireccionZonalPorUbicacion(UbicacionesGeografica provincia) {
		Area area = areaFacade.getAreaCoordinacionZonal(provincia);
		return area;
	}
	
	
	public Area buscarAreaDireccionPorRango(Integer ubicacion) throws Exception {
		return areaFacade.getAreaDireccionPorRango(ubicacion);
	
	}	

	public Area buscarAreaMunicipioPorRango(Integer ubicacion) throws Exception {
		return areaFacade.getAreaMunicipioPorRango(ubicacion);
	}	

	private Area buscarAreaEnteMunicipioPorUbicacion(UbicacionesGeografica parroquia) {
		Area area = areaFacade.getAreaEnteMunicipioPorUbicacion(ID_TIPO_AREA_ENTE_ACREDITADO, parroquia);
		return area;
	}

}
