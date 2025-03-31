/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.SerializationUtils;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.prevencion.categoriaii.mineria.bean.MuestreoInicialLineaBaseBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoBioticoFacade;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoSocialFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoGeneralBiotico;
import ec.gob.ambiente.suia.domain.CatalogoGeneralSocial;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * 
 * @author lili
 *
 */

@ManagedBean
@ViewScoped
public class MuestreoInicialController implements Serializable {

    private static final long serialVersionUID = -5296396183799417114L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MuestreoInicialController.class);
    
    @EJB
	private CatalogoBioticoFacade catalogoBioticoFacade;
    
    @EJB
    private CatalogoSocialFacade catalogoSocialFacade;
    
    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;
    
    @EJB
	private CrudServiceBean crudServiceBean;
	
	@Setter
	@Getter
	private MuestreoInicialLineaBaseBean muestreoInicialLineaBaseBean;
	
	@Setter
	@Getter
	private CatalogoGeneralBiotico vegetalSeleccionado;
	
	
	@PostConstruct
	public void init() throws ServiceException{
		muestreoInicialLineaBaseBean = new MuestreoInicialLineaBaseBean();
		muestreoInicialLineaBaseBean.inicializar();
		getMuestreoInicialLineaBaseBean().setFichaAmbientalMineria(fichaAmbientalMineriaFacade.obtenerPorId(((FichaAmbientalMineria) JsfUtil.devolverObjetoSession(Constantes.SESSION_FICHA_AMBIENTAL_MINERA_OBJECT)).getId()));
		getMuestreoInicialLineaBaseBean().setProyecto(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental());
		
		//MarielaG para historial
		List<FichaAmbientalMineria> listaFichasEnBdd = fichaAmbientalMineriaFacade
				.obtenerAllFichaAmbientalMineria(getMuestreoInicialLineaBaseBean().getProyecto().getId());
		if (listaFichasEnBdd != null) {
			for (FichaAmbientalMineria ficha : listaFichasEnBdd) {
				if (ficha.getIdRegistroOriginal() == null) {
					getMuestreoInicialLineaBaseBean().setFichaAmbientalMineria(ficha);
					if (ficha.getFormacionVegetal() != null) {
						muestreoInicialLineaBaseBean.setFichaAmbientalMineriaOriginal((FichaAmbientalMineria) SerializationUtils.clone(ficha));
					}
				}
			}
			recuperarHistorialFicha(listaFichasEnBdd);
		}
		
		cargarMedioBiotico();
		cargarOpcionesMedioBiotico();
		cargarMedioSocial();
		cargarOpcionesMedioSocial();
		cargarVariablesMedioBiotico();
		cargarVariablesMedioSocial();
	}	
	
	private void cargarMedioBiotico() {
        try {
            List<CatalogoGeneralBiotico> listaCatalogo = catalogoBioticoFacade.obtenerListaBioticoTipo(TipoCatalogo.CODIGO_FORMACION_VEGETAL_MINERIA);
            for (CatalogoGeneralBiotico catalogoGeneralBiotico : listaCatalogo) {
            	getMuestreoInicialLineaBaseBean().getListaFormacionVegetal().add(new SelectItem(catalogoGeneralBiotico.getId(), catalogoGeneralBiotico.getDescripcion()));
			}
            listaCatalogo = catalogoBioticoFacade.obtenerListaBioticoTipo(TipoCatalogo.CODIGO_HABITAT_MINERIA);
            for (CatalogoGeneralBiotico catalogoGeneralBiotico : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaHabitat().add(new SelectItem(catalogoGeneralBiotico.getId(), catalogoGeneralBiotico.getDescripcion()));
			}
            listaCatalogo = catalogoBioticoFacade.obtenerListaBioticoTipo(TipoCatalogo.CODIGO_TIPO_BOSQUE_MINERIA);
            for (CatalogoGeneralBiotico catalogoGeneralBiotico : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaTipoBosque().add(new SelectItem(catalogoGeneralBiotico.getId(), catalogoGeneralBiotico.getDescripcion()));
			}
            listaCatalogo = catalogoBioticoFacade.obtenerListaBioticoTipo(TipoCatalogo.CODIGO_GRADO_INTERVENCION);
            for (CatalogoGeneralBiotico catalogoGeneralBiotico : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaGradoIntervencion().add(new SelectItem(catalogoGeneralBiotico.getId(), catalogoGeneralBiotico.getDescripcion()));
			}
            listaCatalogo = catalogoBioticoFacade.obtenerListaBioticoTipo(TipoCatalogo.CODIGO_PISO_ZOOGEOGRAFICO);
            for (CatalogoGeneralBiotico catalogoGeneralBiotico : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaPisoZoogeofrafico().add(new SelectItem(catalogoGeneralBiotico.getId(), catalogoGeneralBiotico.getDescripcion()));
			}
        } catch (Exception e) {
            LOG.error("Error al cargar medio biotico: ", e);
        }
    }
	
	private void cargarMedioSocial() {
        try {
            List<CatalogoGeneralSocial> listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_NIVEL_CONSOLIDACION_AREA_INFLUENCIA);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaNivelAreaInfluencia().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_TAMANIO_POBLACION);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaTamanioPoblacion().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_ABASTECIMIENTO_AGUA_MINERIA);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaAbastecimientoAgua().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_EVACUACION_AGUA_SERVIDAS);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaEvacuacionAguasServidas().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_EVACUACION_AGUAS_LLUVIA);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaEvacuacionAguasLluvia().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_DESECHOS_SOLIDOS);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaDesechosSolidos().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_ELECTRIFICACION_MINERIA);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaElectrificacion().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_TRANSPORTE_PUBLICO);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaTransportePublico().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_VIALIDAD_Y_ACCESOS);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
            	getMuestreoInicialLineaBaseBean().getListaVialidad().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
            }
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_TELEFONIA);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaTelefonia().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_APROVECHAMIENTO_TIERRA);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaAprovechamientoTierra().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_TENENCIA_TIERRA);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaTenenciaTierra().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_ORGANIZACION_SOCIAL_MINERIA);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaOrganizacionSocial().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_PAISAJE_Y_TURISMO);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaPaisaje().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_PELIGRO_DESLIZAMIENTO);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaPeligroDeslizamiento().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_PELIGRO_INUNDACIONES);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaPeligroInundaciones().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
            listaCatalogo = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_PELIGRO_TERREMOTO);
            for (CatalogoGeneralSocial catalogo : listaCatalogo) {
				getMuestreoInicialLineaBaseBean().getListaPeligroTerremoto().add(new SelectItem(catalogo.getId(), catalogo.getDescripcion()));
			}
        } catch (Exception e) {
            LOG.error("Error al cargar medio social: ", e);
        }
    }
	
	public void cargarOpcionesMedioBiotico(){
		try {
			List<CatalogoGeneralBiotico> lista = catalogoBioticoFacade.obtenerListaBioticoTipo(TipoCatalogo.CODIGO_AREAS_INTERVENIDAS);
			for (CatalogoGeneralBiotico catalogo : lista) {
				getMuestreoInicialLineaBaseBean().getListaAreasIntervenidas().add(new SelectItem(catalogo.getId().toString(), catalogo.getDescripcion()));
			}
			lista = catalogoBioticoFacade.obtenerListaBioticoTipo(TipoCatalogo.CODIGO_DATOS_ECOLOGICOS);
			for (CatalogoGeneralBiotico catalogo : lista) {
				getMuestreoInicialLineaBaseBean().getListaDatosEcologicos().add(new SelectItem(catalogo.getId().toString(), catalogo.getDescripcion()));
				getMuestreoInicialLineaBaseBean().getListaDatosEcologicosFauna().add(new SelectItem(catalogo.getId().toString(), catalogo.getDescripcion()));
			}
			lista = catalogoBioticoFacade.obtenerListaBioticoTipo(TipoCatalogo.CODIGO_USO_RECURSO);
			for (CatalogoGeneralBiotico catalogo : lista) {
				getMuestreoInicialLineaBaseBean().getListaUsoRecurso().add(new SelectItem(catalogo.getId().toString(), catalogo.getDescripcion()));
			}
			lista = catalogoBioticoFacade.obtenerListaBioticoTipo(TipoCatalogo.CODIGO_USO_RECURSO_FAUNA);
			for (CatalogoGeneralBiotico catalogo : lista) {
				getMuestreoInicialLineaBaseBean().getListaUsoRecursoFauna().add(new SelectItem(catalogo.getId().toString(), catalogo.getDescripcion()));
			}
			lista = catalogoBioticoFacade.obtenerListaBioticoTipo(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO);
			for (CatalogoGeneralBiotico catalogo : lista) {
				getMuestreoInicialLineaBaseBean().getListaComponenteBiotico().add(new SelectItem(catalogo.getId().toString(), catalogo.getDescripcion()));
			}
			lista = catalogoBioticoFacade.obtenerListaBioticoTipo(TipoCatalogo.CODIGO_SENSIBILIDAD);
			for (CatalogoGeneralBiotico catalogo : lista) {
				getMuestreoInicialLineaBaseBean().getListaSensibilidad().add(new SelectItem(catalogo.getId().toString(), catalogo.getDescripcion()));
			}
		} catch (Exception e) {
			LOG.error("Error al cargar opciones medio biotico", e);
		}
	}
	
	public void cargarOpcionesMedioSocial(){
		try {
			List<CatalogoGeneralSocial> lista = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_COMPOSICION_ETNICA);			
            for (CatalogoGeneralSocial catalogo : lista) {
				getMuestreoInicialLineaBaseBean().getListaComposicionEtnica().add(new SelectItem(catalogo.getId().toString(), catalogo.getDescripcion()));
			}
			lista = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_LENGUA);
            for (CatalogoGeneralSocial catalogo : lista) {
				getMuestreoInicialLineaBaseBean().getListaLengua().add(new SelectItem(catalogo.getId().toString(), catalogo.getDescripcion()));
			}
            lista = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_RELIGION);
            for (CatalogoGeneralSocial catalogo : lista) {
				getMuestreoInicialLineaBaseBean().getListaReligion().add(new SelectItem(catalogo.getId().toString(), catalogo.getDescripcion()));
			}
            lista = catalogoSocialFacade.obtenerListaSocialTipo(TipoCatalogo.CODIGO_TRADICIONES);
            for (CatalogoGeneralSocial catalogo : lista) {
				getMuestreoInicialLineaBaseBean().getListaTradiciones().add(new SelectItem(catalogo.getId().toString(), catalogo.getDescripcion()));
			}
		} catch (Exception e) {
			LOG.error("Error al cargar opciones medio social", e);
		}
	}
	
	private void cargarVariablesMedioBiotico() {
		getMuestreoInicialLineaBaseBean().setIdVegetal(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdVegetal() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdVegetal());
		getMuestreoInicialLineaBaseBean().setIdHabitat(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdHabitat() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdHabitat());
		getMuestreoInicialLineaBaseBean().setIdTipoBosque(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdTipoBosque() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdTipoBosque());
		getMuestreoInicialLineaBaseBean().setIdGradoIntervencion(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdGradoIntervencion() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdGradoIntervencion());
		getMuestreoInicialLineaBaseBean().setIdPisoZoogeografico(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdPisoZoogeografico() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdPisoZoogeografico());
		getMuestreoInicialLineaBaseBean().setOpcionesAreasIntervenidas(JsfUtil.devuelveVector(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getCatalogoAreasIntervenidas()));
		getMuestreoInicialLineaBaseBean().setOpcionesComponenteBiotico(JsfUtil.devuelveVector(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getCatalogoComponenteBiotico()));
		getMuestreoInicialLineaBaseBean().setOpcionesDatosEcologicos(JsfUtil.devuelveVector(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getCatalogoDatosEcologicos()));
		getMuestreoInicialLineaBaseBean().setOpcionesDatosEcologicosFauna(JsfUtil.devuelveVector(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getCatalogoDatosEcologicosFauna()));
		getMuestreoInicialLineaBaseBean().setOpcionesSensibilidad(JsfUtil.devuelveVector(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getCatalogoSensibilidad()));
		getMuestreoInicialLineaBaseBean().setOpcionesUsoRecurso(JsfUtil.devuelveVector(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getCatalogoUsoRecurso()));
		getMuestreoInicialLineaBaseBean().setOpcionesUsoRecursoFauna(JsfUtil.devuelveVector(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getCatalogoUsoRecursoFauna()));
    }
	
	private void cargarVariablesMedioSocial() {
		//getMuestreoInicialLineaBaseBean().setIdNivelAreaInfluencia(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdNivelAreaInfluencia() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdNivelAreaInfluencia());
		getMuestreoInicialLineaBaseBean().setIdTamanioPoblacion(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdTamanioPoblacion() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdTamanioPoblacion());
		getMuestreoInicialLineaBaseBean().setIdAbastecimientoAgua(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdAbastecimientoAgua() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdAbastecimientoAgua());
		getMuestreoInicialLineaBaseBean().setIdEvacuacionAguasServidas(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdAguasServidas() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdAguasServidas());
		getMuestreoInicialLineaBaseBean().setIdEvacuacionAguasLluvia(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdAguasLluvia() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdAguasLluvia());
		getMuestreoInicialLineaBaseBean().setIdDesechosSolidos(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdDesechosSolidos() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdDesechosSolidos());
		getMuestreoInicialLineaBaseBean().setIdElectrificacion(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdElectrificacion() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdElectrificacion());
		getMuestreoInicialLineaBaseBean().setIdTransportePublico(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdTransportePublico() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdTransportePublico());
		getMuestreoInicialLineaBaseBean().setIdVialidad(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdVialidadYAcceso() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdVialidadYAcceso());
		getMuestreoInicialLineaBaseBean().setIdTelefonia(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdTelefonia() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdTelefonia());
		getMuestreoInicialLineaBaseBean().setIdAprovechamientoTierra(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdAprovechamientoTierra()== null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdAprovechamientoTierra());
		getMuestreoInicialLineaBaseBean().setIdTenenciaTierra(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdTenenciaTierra()== null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdTenenciaTierra());
		getMuestreoInicialLineaBaseBean().setIdOrganizacionSocial(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdOrganizacionSocial() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdOrganizacionSocial());
		getMuestreoInicialLineaBaseBean().setIdPaisaje(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdPaisaje() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdPaisaje());
		getMuestreoInicialLineaBaseBean().setIdPeligroDeslizamiento(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdPeligroDeslizamiento() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdPeligroDeslizamiento().toString());
		getMuestreoInicialLineaBaseBean().setIdPeligroInundaciones(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdPeligroInundacion() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdPeligroInundacion().toString());
		getMuestreoInicialLineaBaseBean().setIdPeligroTerremoto(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdPeligroTerremoto() == null ? null : getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getIdPeligroTerremoto().toString());
		
		getMuestreoInicialLineaBaseBean().setOpcionesComposicionEtnica(JsfUtil.devuelveVector(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getCatalogoComposicionEtnica()));
		getMuestreoInicialLineaBaseBean().setOpcionesComposicionEtnica(JsfUtil.devuelveVector(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getCatalogoComposicionEtnica()));
		getMuestreoInicialLineaBaseBean().setOpcionesLengua(JsfUtil.devuelveVector(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getCatalogoLengua()));
		getMuestreoInicialLineaBaseBean().setOpcionesReligion(JsfUtil.devuelveVector(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getCatalogoReligion()));
		getMuestreoInicialLineaBaseBean().setOpcionesTradiciones(JsfUtil.devuelveVector(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().getCatalogoTradiciones()));
    }
	
	public void guardarMuestreoInicial(){
		try {
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setFormacionVegetal(new CatalogoGeneralBiotico(new Integer(getMuestreoInicialLineaBaseBean().getIdVegetal())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setHabitat(new CatalogoGeneralBiotico(new Integer(getMuestreoInicialLineaBaseBean().getIdHabitat())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setTipoBosque(new CatalogoGeneralBiotico(new Integer(getMuestreoInicialLineaBaseBean().getIdTipoBosque())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setGradoIntervencion(new CatalogoGeneralBiotico(new Integer(getMuestreoInicialLineaBaseBean().getIdGradoIntervencion())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setPisoZoogeografico(new CatalogoGeneralBiotico(new Integer(getMuestreoInicialLineaBaseBean().getIdPisoZoogeografico())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setCatalogoAreasIntervenidas(JsfUtil.transformaVector(getMuestreoInicialLineaBaseBean().getOpcionesAreasIntervenidas()));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setCatalogoComponenteBiotico(JsfUtil.transformaVector(getMuestreoInicialLineaBaseBean().getOpcionesComponenteBiotico()));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setCatalogoDatosEcologicos(JsfUtil.transformaVector(getMuestreoInicialLineaBaseBean().getOpcionesDatosEcologicos()));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setCatalogoDatosEcologicosFauna(JsfUtil.transformaVector(getMuestreoInicialLineaBaseBean().getOpcionesDatosEcologicosFauna()));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setCatalogoSensibilidad(JsfUtil.transformaVector(getMuestreoInicialLineaBaseBean().getOpcionesSensibilidad()));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setCatalogoUsoRecurso(JsfUtil.transformaVector(getMuestreoInicialLineaBaseBean().getOpcionesUsoRecurso()));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setCatalogoUsoRecursoFauna(JsfUtil.transformaVector(getMuestreoInicialLineaBaseBean().getOpcionesUsoRecursoFauna()));
			
			//getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setNivelAreaInfluencia(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdNivelAreaInfluencia())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setTamanioPoblacion(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdTamanioPoblacion())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setAbastecimientoAgua(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdAbastecimientoAgua())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setEvacuacionAguasServidas(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdEvacuacionAguasServidas())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setEvacuacionAguasLluvia(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdEvacuacionAguasLluvia())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setDesechosSolidos(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdDesechosSolidos())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setElectrificacion(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdElectrificacion())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setTransportePublico(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdTransportePublico())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setVialidadYAcceso(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdVialidad())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setTelefonia(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdTelefonia())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setAprovechamientoTierra(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdAprovechamientoTierra())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setTenenciaTierra(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdTenenciaTierra())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setOrganizacionSocial(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdOrganizacionSocial())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setPaisajeTurismo(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdPaisaje())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setPeligroDeslizamiento(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdPeligroDeslizamiento())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setPeligroInundacion(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdPeligroInundaciones())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setPeligroTerremoto(new CatalogoGeneralSocial(new Integer(getMuestreoInicialLineaBaseBean().getIdPeligroTerremoto())));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setCatalogoComposicionEtnica(JsfUtil.transformaVector(getMuestreoInicialLineaBaseBean().getOpcionesComposicionEtnica()));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setCatalogoLengua(JsfUtil.transformaVector(getMuestreoInicialLineaBaseBean().getOpcionesLengua()));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setCatalogoReligion(JsfUtil.transformaVector(getMuestreoInicialLineaBaseBean().getOpcionesReligion()));
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setCatalogoTradiciones(JsfUtil.transformaVector(getMuestreoInicialLineaBaseBean().getOpcionesTradiciones()));
			
			validarOtros();
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setValidarMuestreoInicialLineaBase(true);
			//MarielaG para historial
			guardarHistorial(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria());
			
			fichaAmbientalMineriaFacade.guardarFicha(getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria());
			setMuestreoInicialLineaBaseBean(null);
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
            init();
		}catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
            LOG.info("Error al guardar muestreo inicial linea base: ", e);
        } catch (RuntimeException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
            LOG.info("Error al guardar muestreo inicial linea base: ", e);
        }
	}
	
	public void validarOtros(){
		if (!muestraOpcionOtro(getMuestreoInicialLineaBaseBean().getOpcionesUsoRecurso(), getMuestreoInicialLineaBaseBean().getListaUsoRecurso())) {
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setOtrosUsosFlora(null);
		}
		if (!muestraOpcionOtroCombo(getMuestreoInicialLineaBaseBean().getIdDesechosSolidos(), getMuestreoInicialLineaBaseBean().getListaDesechosSolidos())) {
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setOtrosDesechoSolido(null);
		}
		if (!muestraOpcionOtroCombo(getMuestreoInicialLineaBaseBean().getIdTransportePublico(), getMuestreoInicialLineaBaseBean().getListaTransportePublico())) {
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setOtrosTransportePublico(null);
		}
		if (!muestraOpcionOtroCombo(getMuestreoInicialLineaBaseBean().getIdVialidad(), getMuestreoInicialLineaBaseBean().getListaVialidad())) {
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setOtrosVialidad(null);
		}
		if (!muestraOpcionOtroCombo(getMuestreoInicialLineaBaseBean().getIdAprovechamientoTierra(), getMuestreoInicialLineaBaseBean().getListaAprovechamientoTierra())) {
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setOtrosAprovechamientoTierra(null);
		}
		if (!muestraOpcionOtro(getMuestreoInicialLineaBaseBean().getOpcionesLengua(), getMuestreoInicialLineaBaseBean().getListaLengua())) {
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setOtrosLengua(null);
		}
		if (!muestraOpcionOtro(getMuestreoInicialLineaBaseBean().getOpcionesReligion(), getMuestreoInicialLineaBaseBean().getListaReligion())) {
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setOtrosReligion(null);
		}
		if (!muestraOpcionOtro(getMuestreoInicialLineaBaseBean().getOpcionesTradiciones(), getMuestreoInicialLineaBaseBean().getListaTradiciones())) {
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setOtrosTradiciones(null);
		}
		if (!muestraOpcionOtroCombo(getMuestreoInicialLineaBaseBean().getIdPaisaje(), getMuestreoInicialLineaBaseBean().getListaPaisaje())) {
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setOtrosPaisaje(null);
		}
		if (!muestraOpcionOtro(getMuestreoInicialLineaBaseBean().getOpcionesComposicionEtnica(), getMuestreoInicialLineaBaseBean().getListaComposicionEtnica())) {
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setOtrosComposicionEtnica(null);
		}
		if (!muestraOpcionOtroCombo(getMuestreoInicialLineaBaseBean().getIdOrganizacionSocial(), getMuestreoInicialLineaBaseBean().getListaOrganizacionSocial())) {
			getMuestreoInicialLineaBaseBean().getFichaAmbientalMineria().setOtrosOrganizacionSocial(null);
		}
	}
	
	/**
     * Metodo que valida la última posición de una lista, si esta fue seleccionada retorna true
     * @author lili
     * @param opcionesSeleccionadas
     * @param String[], lista
     * @return true si selecciona la última posición (opcion otro), caso contrario false
     */
	public boolean muestraOpcionOtro(String[] opcionesSeleccionadas, List<SelectItem> lista){
		if(opcionesSeleccionadas != null && !lista.isEmpty() && lista != null){
			Integer num = lista.size();
			for (String string : opcionesSeleccionadas) {
				if (string.equals(lista.get(num-1).getValue())) {
					return true;
				}
			}
			return false;
		}else {
			return false;
		}
	}
	
	/**
	 * Metodo que valida la última posición de una lista, si esta fue seleccionada retorna true
	 * @param opcionesSeleccionadas
	 * @param Integer, lista
	 * @return
	 */
	public boolean muestraOpcionOtroCombo(Integer opcionesSeleccionadas, List<SelectItem> lista){
		if(opcionesSeleccionadas != null && !lista.isEmpty() && lista != null){
			Integer num = lista.size();
			if (opcionesSeleccionadas.equals(lista.get(num - 1).getValue())) {
				return true;
			}
			return false;
		}else {
			return false;
		}
	}
		
	public void cancelar() {

    }
	
	//MarielaG metodos para historial
    public void guardarHistorial(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
        	
            FichaAmbientalMineria fichaAmbientalMineriaBdd = muestreoInicialLineaBaseBean.getFichaAmbientalMineriaOriginal();
           
            if(fichaAmbientalMineriaBdd != null){
                if(!fichaAmbientalMineria.equalsObjectMuestreoInicial(fichaAmbientalMineriaBdd)){
                    FichaAmbientalMineria fichaHistorial = (FichaAmbientalMineria)SerializationUtils.clone(fichaAmbientalMineriaBdd);
                    fichaHistorial.setId(null);
                    fichaHistorial.setFechaHistorico(new Date());
                    fichaHistorial.setIdRegistroOriginal(fichaAmbientalMineriaBdd.getId());
                    crudServiceBean.saveOrUpdate(fichaHistorial);
                }
            }
           
            crudServiceBean.saveOrUpdate(fichaAmbientalMineria);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public void recuperarHistorialFicha(List<FichaAmbientalMineria> listaFichasEnBdd){
    	FichaAmbientalMineria fichaInicial = null;
		for(FichaAmbientalMineria fichaBdd : listaFichasEnBdd){
			if(fichaBdd.getFormacionVegetal() != null && fichaBdd.getIdRegistroOriginal() != null){
				if(fichaInicial == null)
					fichaInicial = muestreoInicialLineaBaseBean.getFichaAmbientalMineria();
				
				recuperarHistorialFormacionVegetal(fichaBdd, fichaInicial);
		    	recuperarHistorialFlora(fichaBdd, fichaInicial);
		    	recuperarHistorialFauna(fichaBdd, fichaInicial);
		    	recuperarHistorialDemografia(fichaBdd, fichaInicial);
		    	recuperarHistorialInfraestructura(fichaBdd, fichaInicial);
		    	recuperarHistorialSocioEconomicas(fichaBdd, fichaInicial);
		    	recuperarHistorialAspectosCulturales(fichaBdd, fichaInicial);
		    	recuperarHistorialMedioPercentual(fichaBdd, fichaInicial);
		    	recuperarHistorialRiesgosNaturales(fichaBdd, fichaInicial);
		    	
				fichaInicial = fichaBdd;
			}
		}
    }
    
    public void recuperarHistorialFormacionVegetal(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial) {
		if (fichaBdd.getFormacionVegetal().getId() != fichaInicial.getFormacionVegetal().getId()) {
			FichaAmbientalMineria nuevoItem = new FichaAmbientalMineria();
			nuevoItem.setId(fichaBdd.getId());
			nuevoItem.setFechaHistorico(fichaBdd.getFechaHistorico());

			if (fichaBdd.getFormacionVegetal().getId() != fichaInicial.getFormacionVegetal().getId()) {
				nuevoItem.setFormacionVegetal(fichaBdd.getFormacionVegetal());
			}
			muestreoInicialLineaBaseBean.getListaHistorialFormacionVegetal().add(0, fichaBdd);
		}
	}
    public void recuperarHistorialFlora(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial){
    	if(fichaBdd.getHabitat().getId() != fichaInicial.getHabitat().getId() ||
    			fichaBdd.getTipoBosque().getId() != fichaInicial.getTipoBosque().getId() ||
    			fichaBdd.getGradoIntervencion().getId() != fichaInicial.getGradoIntervencion().getId() ||
    			!fichaBdd.getCatalogoAreasIntervenidas().equals(fichaInicial.getCatalogoAreasIntervenidas()) ||
    			!fichaBdd.getCatalogoDatosEcologicos().equals(fichaInicial.getCatalogoDatosEcologicos()) ||
    			!fichaBdd.getCatalogoUsoRecurso().equals(fichaInicial.getCatalogoUsoRecurso()) ||
    			((fichaBdd.getOtrosUsosFlora() != null && fichaInicial.getOtrosUsosFlora() == null) || 
    					(fichaBdd.getOtrosUsosFlora() != null && !fichaBdd.getOtrosUsosFlora().equals(fichaInicial.getOtrosUsosFlora())))
    			){

    		FichaAmbientalMineria nuevoItem = new FichaAmbientalMineria();
    		nuevoItem.setId(fichaBdd.getId());
    		nuevoItem.setFechaHistorico(fichaBdd.getFechaHistorico());

    		if(fichaBdd.getHabitat().getId() != fichaInicial.getHabitat().getId()){
    			nuevoItem.setHabitat(fichaBdd.getHabitat());
    		}
    		if(fichaBdd.getTipoBosque().getId() != fichaInicial.getTipoBosque().getId()){
    			nuevoItem.setTipoBosque(fichaBdd.getTipoBosque());
    		}
    		if(fichaBdd.getGradoIntervencion().getId() != fichaInicial.getGradoIntervencion().getId()){
    			nuevoItem.setGradoIntervencion(fichaBdd.getGradoIntervencion());
    		}
    		if(!fichaBdd.getCatalogoAreasIntervenidas().equals( fichaInicial.getCatalogoAreasIntervenidas())){
    			nuevoItem.setCatalogoAreasIntervenidas(getInfoCatalogoBiotico(fichaBdd.getCatalogoAreasIntervenidas()));
    		}
    		if(!fichaBdd.getCatalogoDatosEcologicos().equals(fichaInicial.getCatalogoDatosEcologicos())){
    			nuevoItem.setCatalogoDatosEcologicos(getInfoCatalogoBiotico(fichaBdd.getCatalogoDatosEcologicos()));
    		}
    		if(!fichaBdd.getCatalogoUsoRecurso().equals(fichaInicial.getCatalogoUsoRecurso())){
    			nuevoItem.setCatalogoUsoRecurso(getInfoCatalogoBiotico(fichaBdd.getCatalogoUsoRecurso()));
    		}
    		if ((fichaBdd.getOtrosUsosFlora() != null && fichaInicial.getOtrosUsosFlora() == null) || 
    				(fichaBdd.getOtrosUsosFlora() != null && 
    				!fichaBdd.getOtrosUsosFlora().equals(fichaInicial.getOtrosUsosFlora()))) {
    			nuevoItem.setOtrosUsosFlora(fichaBdd.getOtrosUsosFlora());
    			muestreoInicialLineaBaseBean.setOtrosUsosFlora(true);
    		}
    		muestreoInicialLineaBaseBean.getListaHistorialFlora().add(0, nuevoItem);
    	}
    }
    public void recuperarHistorialFauna(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial){
    	if(fichaBdd.getPisoZoogeografico().getId() != fichaInicial.getPisoZoogeografico().getId() ||
    			!fichaBdd.getCatalogoComponenteBiotico().equals(fichaInicial.getCatalogoComponenteBiotico()) ||
    			!fichaBdd.getCatalogoSensibilidad().equals(fichaInicial.getCatalogoSensibilidad()) ||
    			!fichaBdd.getCatalogoDatosEcologicosFauna().equals(fichaInicial.getCatalogoDatosEcologicosFauna()) ||
    			!fichaBdd.getCatalogoUsoRecursoFauna().equals(fichaInicial.getCatalogoUsoRecursoFauna())){

    		FichaAmbientalMineria nuevoItem = new FichaAmbientalMineria();
    		nuevoItem.setId(fichaBdd.getId());
    		nuevoItem.setFechaHistorico(fichaBdd.getFechaHistorico());

    		if(fichaBdd.getPisoZoogeografico().getId() != fichaInicial.getPisoZoogeografico().getId()){
    			nuevoItem.setPisoZoogeografico(fichaBdd.getPisoZoogeografico());
    		}
    		if(!fichaBdd.getCatalogoComponenteBiotico().equals( fichaInicial.getCatalogoComponenteBiotico())){
    			nuevoItem.setCatalogoComponenteBiotico(getInfoCatalogoBiotico(fichaBdd.getCatalogoComponenteBiotico()));
    		}
    		if(!fichaBdd.getCatalogoSensibilidad().equals(fichaInicial.getCatalogoSensibilidad())){
    			nuevoItem.setCatalogoSensibilidad(getInfoCatalogoBiotico(fichaBdd.getCatalogoSensibilidad()));
    		}
    		if(!fichaBdd.getCatalogoDatosEcologicosFauna().equals(fichaInicial.getCatalogoDatosEcologicosFauna())){
    			nuevoItem.setCatalogoDatosEcologicosFauna(getInfoCatalogoBiotico(fichaBdd.getCatalogoDatosEcologicosFauna()));
    		}
    		if(!fichaBdd.getCatalogoUsoRecursoFauna().equals(fichaInicial.getCatalogoUsoRecursoFauna())){
    			nuevoItem.setCatalogoUsoRecursoFauna(getInfoCatalogoBiotico(fichaBdd.getCatalogoUsoRecursoFauna()));
    		}
    		muestreoInicialLineaBaseBean.getListaHistorialFaunaSilvestre().add(0, nuevoItem);
    	}
    }
    public void recuperarHistorialDemografia(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial){
    	if(fichaBdd.getTamanioPoblacion().getId() != fichaInicial.getTamanioPoblacion().getId() ||
    			!fichaBdd.getCatalogoComposicionEtnica().equals(fichaInicial.getCatalogoComposicionEtnica()) ||
    			((fichaBdd.getOtrosComposicionEtnica() != null && fichaInicial.getOtrosComposicionEtnica() == null) || 
    					(fichaBdd.getOtrosComposicionEtnica() != null && !fichaBdd.getOtrosComposicionEtnica().equals(fichaInicial.getOtrosComposicionEtnica())))
    			){

    		FichaAmbientalMineria nuevoItem = new FichaAmbientalMineria();
    		nuevoItem.setId(fichaBdd.getId());
    		nuevoItem.setFechaHistorico(fichaBdd.getFechaHistorico());

    		if(fichaBdd.getTamanioPoblacion().getId() != fichaInicial.getTamanioPoblacion().getId()){
    			nuevoItem.setTamanioPoblacion(fichaBdd.getTamanioPoblacion());
    		}
    		if(!fichaBdd.getCatalogoComposicionEtnica().equals( fichaInicial.getCatalogoComposicionEtnica())){
    			nuevoItem.setCatalogoComposicionEtnica(getInfoCatalogoMedioSocial(fichaBdd.getCatalogoComposicionEtnica()));
    		}
    		if ((fichaBdd.getOtrosComposicionEtnica() != null && fichaInicial.getOtrosComposicionEtnica() == null) || 
    				(fichaBdd.getOtrosComposicionEtnica() != null && 
    				!fichaBdd.getOtrosComposicionEtnica().equals(fichaInicial.getOtrosComposicionEtnica()))) {
    			nuevoItem.setOtrosComposicionEtnica(fichaBdd.getOtrosComposicionEtnica());
    			muestreoInicialLineaBaseBean.setOtrosComposicionEtnica(true);
    		}
    		muestreoInicialLineaBaseBean.getListaHistorialDemografia().add(0, nuevoItem);
    	}
    }
    public void recuperarHistorialInfraestructura(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial){
    	if(fichaBdd.getAbastecimientoAgua().getId() != fichaInicial.getAbastecimientoAgua().getId() ||
    			fichaBdd.getEvacuacionAguasServidas().getId() != fichaInicial.getEvacuacionAguasServidas().getId() ||
    			fichaBdd.getEvacuacionAguasLluvia().getId() != fichaInicial.getEvacuacionAguasLluvia().getId() ||
    			fichaBdd.getDesechosSolidos().getId() != fichaInicial.getDesechosSolidos().getId() ||
    			fichaBdd.getElectrificacion().getId() != fichaInicial.getElectrificacion().getId() ||
    			fichaBdd.getTransportePublico().getId() != fichaInicial.getTransportePublico().getId() ||
    			fichaBdd.getVialidadYAcceso().getId() != fichaInicial.getVialidadYAcceso().getId() ||
    			fichaBdd.getTelefonia().getId() != fichaInicial.getTelefonia().getId() || 
    			((fichaBdd.getOtrosDesechoSolido() != null && fichaInicial.getOtrosDesechoSolido() == null) || 
    					(fichaBdd.getOtrosDesechoSolido() != null && !fichaBdd.getOtrosDesechoSolido().equals(fichaInicial.getOtrosDesechoSolido()))) || 
    					((fichaBdd.getOtrosTransportePublico() != null && fichaInicial.getOtrosTransportePublico() == null) || 
    							(fichaBdd.getOtrosTransportePublico() != null && !fichaBdd.getOtrosTransportePublico().equals(fichaInicial.getOtrosTransportePublico())))
    			){

    		FichaAmbientalMineria nuevoItem = new FichaAmbientalMineria();
    		nuevoItem.setId(fichaBdd.getId());
    		nuevoItem.setFechaHistorico(fichaBdd.getFechaHistorico());

    		if(fichaBdd.getAbastecimientoAgua().getId() != fichaInicial.getAbastecimientoAgua().getId()){
    			nuevoItem.setAbastecimientoAgua(fichaBdd.getAbastecimientoAgua());
    		}
    		if(fichaBdd.getEvacuacionAguasServidas().getId() != fichaInicial.getEvacuacionAguasServidas().getId()){
    			nuevoItem.setEvacuacionAguasServidas(fichaBdd.getEvacuacionAguasServidas());
    		}
    		if(fichaBdd.getEvacuacionAguasLluvia().getId() != fichaInicial.getEvacuacionAguasLluvia().getId()){
    			nuevoItem.setEvacuacionAguasLluvia(fichaBdd.getEvacuacionAguasLluvia());
    		}
    		if(fichaBdd.getDesechosSolidos().getId() != fichaInicial.getDesechosSolidos().getId()){
    			nuevoItem.setDesechosSolidos(fichaBdd.getDesechosSolidos());
    		}
    		if(fichaBdd.getElectrificacion().getId() != fichaInicial.getElectrificacion().getId()){
    			nuevoItem.setElectrificacion(fichaBdd.getElectrificacion());
    		}
    		if(fichaBdd.getTransportePublico().getId() != fichaInicial.getTransportePublico().getId()){
    			nuevoItem.setTransportePublico(fichaBdd.getTransportePublico());
    		}
    		if(fichaBdd.getVialidadYAcceso().getId() != fichaInicial.getVialidadYAcceso().getId()){
    			nuevoItem.setVialidadYAcceso(fichaBdd.getVialidadYAcceso());
    		}
    		if(fichaBdd.getTelefonia().getId() != fichaInicial.getTelefonia().getId()){
    			nuevoItem.setTelefonia(fichaBdd.getTelefonia());
    		}
    		if ((fichaBdd.getOtrosDesechoSolido() != null && fichaInicial.getOtrosDesechoSolido() == null) || 
    				(fichaBdd.getOtrosDesechoSolido() != null && 
    				!fichaBdd.getOtrosDesechoSolido().equals(fichaInicial.getOtrosDesechoSolido()))) {
    			nuevoItem.setOtrosDesechoSolido(fichaBdd.getOtrosDesechoSolido());
    			muestreoInicialLineaBaseBean.setOtrosDesechoSolido(true);
    		}
    		if ((fichaBdd.getOtrosTransportePublico() != null && fichaInicial.getOtrosTransportePublico() == null) || 
    				(fichaBdd.getOtrosTransportePublico() != null && 
    				!fichaBdd.getOtrosTransportePublico().equals(fichaInicial.getOtrosTransportePublico()))) {
    			nuevoItem.setOtrosTransportePublico(fichaBdd.getOtrosTransportePublico());
    			muestreoInicialLineaBaseBean.setOtrosTransportePublico(true);
    		}
    		muestreoInicialLineaBaseBean.getListaHistorialInfraestructuraSocial().add(0, nuevoItem);
    	}
    }
    public void recuperarHistorialSocioEconomicas(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial){
    	if(fichaBdd.getAprovechamientoTierra().getId() != fichaInicial.getAprovechamientoTierra().getId() ||
    			fichaBdd.getTenenciaTierra().getId() != fichaInicial.getTenenciaTierra().getId() ||
    			fichaBdd.getOrganizacionSocial().getId() != fichaInicial.getOrganizacionSocial().getId() ||
    			((fichaBdd.getOtrosAprovechamientoTierra() != null && fichaInicial.getOtrosAprovechamientoTierra() == null) || 
    					(fichaBdd.getOtrosAprovechamientoTierra() != null && 
    					!fichaBdd.getOtrosAprovechamientoTierra().equals(fichaInicial.getOtrosAprovechamientoTierra()))) ||
    					((fichaBdd.getOtrosOrganizacionSocial() != null && fichaInicial.getOtrosOrganizacionSocial() == null) || 
    							(fichaBdd.getOtrosOrganizacionSocial() != null && 
    							!fichaBdd.getOtrosOrganizacionSocial().equals(fichaInicial.getOtrosOrganizacionSocial())))
    			){

    		FichaAmbientalMineria nuevoItem = new FichaAmbientalMineria();
    		nuevoItem.setId(fichaBdd.getId());
    		nuevoItem.setFechaHistorico(fichaBdd.getFechaHistorico());

    		if(fichaBdd.getAprovechamientoTierra().getId() != fichaInicial.getAprovechamientoTierra().getId()){
    			nuevoItem.setAprovechamientoTierra(fichaBdd.getAprovechamientoTierra());
    		}
    		if(fichaBdd.getTenenciaTierra().getId() != fichaInicial.getTenenciaTierra().getId()){
    			nuevoItem.setTenenciaTierra(fichaBdd.getTenenciaTierra());
    		}
    		if(fichaBdd.getOrganizacionSocial().getId() != fichaInicial.getOrganizacionSocial().getId()){
    			nuevoItem.setOrganizacionSocial(fichaBdd.getOrganizacionSocial());
    		}
    		if ((fichaBdd.getOtrosAprovechamientoTierra() != null && fichaInicial.getOtrosAprovechamientoTierra() == null) || 
    				(fichaBdd.getOtrosAprovechamientoTierra() != null && 
    				!fichaBdd.getOtrosAprovechamientoTierra().equals(fichaInicial.getOtrosAprovechamientoTierra()))) {
    			nuevoItem.setOtrosAprovechamientoTierra(fichaBdd.getOtrosAprovechamientoTierra());
    			muestreoInicialLineaBaseBean.setOtrosAprovechamientoTierra(true);
    		}
    		if ((fichaBdd.getOtrosOrganizacionSocial() != null && fichaInicial.getOtrosOrganizacionSocial() == null) || 
    				(fichaBdd.getOtrosOrganizacionSocial() != null && 
    				!fichaBdd.getOtrosOrganizacionSocial().equals(fichaInicial.getOtrosOrganizacionSocial()))) {
    			nuevoItem.setOtrosOrganizacionSocial(fichaBdd.getOtrosOrganizacionSocial());
    			muestreoInicialLineaBaseBean.setOtrosOrganizacionSocial(true);
    		}
    		muestreoInicialLineaBaseBean.getListaHistorialSocioEconomicas().add(0, nuevoItem);
    	}
    }
    public void recuperarHistorialAspectosCulturales(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial){
    	if(!fichaBdd.getCatalogoLengua().equals(fichaInicial.getCatalogoLengua()) ||
    			!fichaBdd.getCatalogoReligion().equals(fichaInicial.getCatalogoReligion()) ||
    			!fichaBdd.getCatalogoTradiciones().equals(fichaInicial.getCatalogoTradiciones()) ||
    			((fichaBdd.getOtrosLengua() != null && fichaInicial.getOtrosLengua() == null) || 
    					(fichaBdd.getOtrosLengua() != null && 
    					!fichaBdd.getOtrosLengua().equals(fichaInicial.getOtrosLengua()))) ||
    					((fichaBdd.getOtrosReligion() != null && fichaInicial.getOtrosReligion() == null) || 
    							(fichaBdd.getOtrosReligion() != null && 
    							!fichaBdd.getOtrosReligion().equals(fichaInicial.getOtrosReligion()))) ||
    							((fichaBdd.getOtrosTradiciones() != null && fichaInicial.getOtrosTradiciones() == null) || 
    									(fichaBdd.getOtrosTradiciones() != null && 
    									!fichaBdd.getOtrosTradiciones().equals(fichaInicial.getOtrosTradiciones())))		
    			){

    		FichaAmbientalMineria nuevoItem = new FichaAmbientalMineria();
    		nuevoItem.setId(fichaBdd.getId());
    		nuevoItem.setFechaHistorico(fichaBdd.getFechaHistorico());

    		if(!fichaBdd.getCatalogoLengua().equals( fichaInicial.getCatalogoLengua())){
    			nuevoItem.setCatalogoLengua(getInfoCatalogoMedioSocial(fichaBdd.getCatalogoLengua()));
    		}
    		if(!fichaBdd.getCatalogoReligion().equals(fichaInicial.getCatalogoReligion())){
    			nuevoItem.setCatalogoReligion(getInfoCatalogoMedioSocial(fichaBdd.getCatalogoReligion()));
    		}
    		if(!fichaBdd.getCatalogoTradiciones().equals(fichaInicial.getCatalogoTradiciones())){
    			nuevoItem.setCatalogoTradiciones(getInfoCatalogoMedioSocial(fichaBdd.getCatalogoTradiciones()));
    		}
    		if ((fichaBdd.getOtrosLengua() != null && fichaInicial.getOtrosLengua() == null) || 
    				(fichaBdd.getOtrosLengua() != null && 
    				!fichaBdd.getOtrosLengua().equals(fichaInicial.getOtrosLengua()))) {
    			nuevoItem.setOtrosLengua(fichaBdd.getOtrosLengua());
    			muestreoInicialLineaBaseBean.setOtrosLengua(true);
    		}
    		if ((fichaBdd.getOtrosReligion() != null && fichaInicial.getOtrosReligion() == null) || 
    				(fichaBdd.getOtrosReligion() != null && 
    				!fichaBdd.getOtrosReligion().equals(fichaInicial.getOtrosReligion()))) {
    			nuevoItem.setOtrosReligion(fichaBdd.getOtrosReligion());
    			muestreoInicialLineaBaseBean.setOtrosReligion(true);
    		}
    		if ((fichaBdd.getOtrosTradiciones() != null && fichaInicial.getOtrosTradiciones() == null) || 
    				(fichaBdd.getOtrosTradiciones() != null && 
    				!fichaBdd.getOtrosTradiciones().equals(fichaInicial.getOtrosTradiciones()))) {
    			nuevoItem.setOtrosTradiciones(fichaBdd.getOtrosTradiciones());
    			muestreoInicialLineaBaseBean.setOtrosTradiciones(true);
    		}
    		muestreoInicialLineaBaseBean.getListaHistorialAspectosCulturales().add(0, nuevoItem);
    	}
    }

	public void recuperarHistorialMedioPercentual(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial) {
		if (fichaBdd.getPaisajeTurismo().getId() != fichaInicial.getPaisajeTurismo().getId() || 
			((fichaBdd.getOtrosPaisaje() != null && fichaInicial.getOtrosPaisaje() == null) || 
					(fichaBdd.getOtrosPaisaje() != null && !fichaBdd.getOtrosPaisaje().equals(fichaInicial.getOtrosPaisaje())))) {
			FichaAmbientalMineria nuevoItem = new FichaAmbientalMineria();
			nuevoItem.setId(fichaBdd.getId());
			nuevoItem.setFechaHistorico(fichaBdd.getFechaHistorico());

			if (fichaBdd.getPaisajeTurismo().getId() != fichaInicial.getPaisajeTurismo().getId()) {
				nuevoItem.setPaisajeTurismo(fichaBdd.getPaisajeTurismo());
			}
			if ((fichaBdd.getOtrosPaisaje() != null && fichaInicial.getOtrosPaisaje() == null) || 
					(fichaBdd.getOtrosPaisaje() != null && !fichaBdd.getOtrosPaisaje().equals(fichaInicial.getOtrosPaisaje()))) {
				nuevoItem.setOtrosPaisaje(fichaBdd.getOtrosPaisaje());
				muestreoInicialLineaBaseBean.setOtrosPaisaje(true);
			}
			muestreoInicialLineaBaseBean.getListaHistorialMedioPercentual().add(0, nuevoItem);
		}
	}
   	public void recuperarHistorialRiesgosNaturales(FichaAmbientalMineria fichaBdd, FichaAmbientalMineria fichaInicial){
   		if(fichaBdd.getPeligroDeslizamiento().getId() != fichaInicial.getPeligroDeslizamiento().getId() ||
   				fichaBdd.getPeligroInundacion().getId() != fichaInicial.getPeligroInundacion().getId() ||
   				fichaBdd.getPeligroTerremoto().getId() != fichaInicial.getPeligroTerremoto().getId()){

   			FichaAmbientalMineria nuevoItem = new FichaAmbientalMineria();
   			nuevoItem.setId(fichaBdd.getId());
   			nuevoItem.setFechaHistorico(fichaBdd.getFechaHistorico());

   			if(fichaBdd.getPeligroDeslizamiento().getId() != fichaInicial.getPeligroDeslizamiento().getId()){
   				nuevoItem.setPeligroDeslizamiento(fichaBdd.getPeligroDeslizamiento());
   			}
   			if(fichaBdd.getPeligroInundacion().getId() != fichaInicial.getPeligroInundacion().getId()){
   				nuevoItem.setPeligroInundacion(fichaBdd.getPeligroInundacion());
   			}
   			if(fichaBdd.getPeligroTerremoto().getId() != fichaInicial.getPeligroTerremoto().getId()){
   				nuevoItem.setPeligroTerremoto(fichaBdd.getPeligroTerremoto());
   			}
   			muestreoInicialLineaBaseBean.getListaHistorialRiesgosNaturales().add(0, nuevoItem);
   		}
    }
    
        
    public String getInfoCatalogoBiotico(String ids){
    	List<CatalogoGeneralBiotico> listaFisco = catalogoBioticoFacade.obtenerListaBioticoIds(ids);
    	StringBuilder concatena = new StringBuilder();
        for (CatalogoGeneralBiotico c : listaFisco) {
            concatena.append(c.getDescripcion()).append("<br/>");
        }
        return concatena.toString();
    }
    
	public String getInfoCatalogoMedioSocial(String ids) {
		List<CatalogoGeneralSocial> lista = catalogoSocialFacade.obtenerListaSocialIds(ids);
		StringBuilder concatena = new StringBuilder();
		for (CatalogoGeneralSocial c : lista) {
			concatena.append(c.getDescripcion()).append("<br/>");
		}
		return concatena.toString();
	}
    
    
    public void fillHistorialFicha(Integer opcion) {
    	muestreoInicialLineaBaseBean.setListaHistorialFicha(new ArrayList<FichaAmbientalMineria>());
    	muestreoInicialLineaBaseBean.setOpcionHistorial(opcion);
    	muestreoInicialLineaBaseBean.setTamanioModal(800);
    	
		switch (opcion) {
		case 1:
			muestreoInicialLineaBaseBean.setNombreHistorial("Historial formación vegetal");
			muestreoInicialLineaBaseBean.setListaHistorialFicha(muestreoInicialLineaBaseBean.getListaHistorialFormacionVegetal());
			muestreoInicialLineaBaseBean.setTamanioModal(400);
			break;
		case 2:
			muestreoInicialLineaBaseBean.setNombreHistorial("Historial flora");
			muestreoInicialLineaBaseBean.setListaHistorialFicha(muestreoInicialLineaBaseBean.getListaHistorialFlora());
			break;
		case 3:
			muestreoInicialLineaBaseBean.setNombreHistorial("Historial fauna");
			muestreoInicialLineaBaseBean.setListaHistorialFicha(muestreoInicialLineaBaseBean.getListaHistorialFaunaSilvestre());
			break;
		case 4:
			muestreoInicialLineaBaseBean.setNombreHistorial("Historial demografía");
			muestreoInicialLineaBaseBean.setListaHistorialFicha(muestreoInicialLineaBaseBean.getListaHistorialDemografia());
			muestreoInicialLineaBaseBean.setTamanioModal(600);
			break;
		case 5:
			muestreoInicialLineaBaseBean.setNombreHistorial("Historial infraestructura social");
			muestreoInicialLineaBaseBean.setListaHistorialFicha(muestreoInicialLineaBaseBean.getListaHistorialInfraestructuraSocial());
			break;
		case 6:
			muestreoInicialLineaBaseBean.setNombreHistorial("Historial actividades socio-económicas");
			muestreoInicialLineaBaseBean.setListaHistorialFicha(muestreoInicialLineaBaseBean.getListaHistorialSocioEconomicas());
			muestreoInicialLineaBaseBean.setTamanioModal(600);
			break;
		case 7:
			muestreoInicialLineaBaseBean.setNombreHistorial("Historial aspectos culturales");
			muestreoInicialLineaBaseBean.setListaHistorialFicha(muestreoInicialLineaBaseBean.getListaHistorialAspectosCulturales());
			muestreoInicialLineaBaseBean.setTamanioModal(600);
			break;
		case 8:
			muestreoInicialLineaBaseBean.setNombreHistorial("Historial medio percentual");
			muestreoInicialLineaBaseBean.setListaHistorialFicha(muestreoInicialLineaBaseBean.getListaHistorialMedioPercentual());
			muestreoInicialLineaBaseBean.setTamanioModal(400);
			break;
		case 9:
			muestreoInicialLineaBaseBean.setNombreHistorial("Historial riesgos naturales e inducidos");
			muestreoInicialLineaBaseBean.setListaHistorialFicha(muestreoInicialLineaBaseBean.getListaHistorialRiesgosNaturales());
			muestreoInicialLineaBaseBean.setTamanioModal(600);
			break;
		default:
			break;
		}
	}
    //MarielaG fin historial
    
    
}
