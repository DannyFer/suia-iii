package ec.gob.ambiente.prevencion.categoria2.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoSocialFacade;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.CatalogoGeneralSocial;
import ec.gob.ambiente.suia.domain.TipoCatalogo;

/**
 * 
 * @author karla.carvajal
 * 
 */
@ManagedBean
@ViewScoped
public class CatalogoGeneralPmaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2204117107272930736L;

	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;
	@EJB
	private CatalogoSocialFacade catalogoSocialFacade;
	@Getter
	@Setter
	private List<CatalogoCategoriaFase> tiposActividades = new ArrayList<CatalogoCategoriaFase>();

	@Getter
	@Setter
	private List<CatalogoGeneral> tiposPredio = new ArrayList<CatalogoGeneral>();

	@Getter
	@Setter
	private List<CatalogoGeneral> tiposInfraestructura = new ArrayList<CatalogoGeneral>();

	@Getter
	@Setter
	private List<CatalogoCategoriaFase> actividadesSeleccionadas = new ArrayList<CatalogoCategoriaFase>();

	@Getter
	@Setter
	private CatalogoGeneral tiposInfraestructurasSeleccionados;

	@Getter
	@Setter
	private List<CatalogoGeneral> tiposPedioSecundariosSeleccionados;

	@Getter
	@Setter
	private List<CatalogoGeneral> tiposPredioSeleccionados = new ArrayList<CatalogoGeneral>();

	@Getter
	@Setter
	private List<CatalogoGeneral> tiposPlanes = new ArrayList<CatalogoGeneral>();

	@Getter
	@Setter
	private List<CatalogoGeneral> prediosSecundarios = new ArrayList<CatalogoGeneral>();

	@Getter
	@Setter
	private List<CatalogoGeneralSocial> listaTipoVias = new ArrayList<CatalogoGeneralSocial>();

	public void init() {
		try {
			tiposPredio = llenarCatalogosCodigo(TipoCatalogo.SITUACION_PREDIO,
					TipoCatalogo.CODIGO_PREDIO_PRIMARIO);
			prediosSecundarios = llenarCatalogosCodigo(
					TipoCatalogo.SITUACION_PREDIO,
					TipoCatalogo.CODIGO_PREDIO_SECUDARIO);
			tiposPlanes = llenarCatalogos(TipoCatalogo.TIPO_PLAN);
			tiposInfraestructura = llenarCatalogos(TipoCatalogo.TIPO_INFRAESTRUCTURA);
			listaTipoVias = llenarCatalogoSocial(TipoCatalogo.TIPO_VIA);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<CatalogoGeneral> llenarCatalogos(int tipo) {

		try {
			return catalogoGeneralFacade.obtenerCatalogoXTipo(tipo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<CatalogoGeneral> llenarCatalogosCodigo(int tipo, String codigo) {

		try {
			return catalogoGeneralFacade.obtenerCatalogoXTipoXCodigo(tipo,
					codigo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<CatalogoGeneralSocial> llenarCatalogoSocial(int tipo) {

		try {
			return catalogoSocialFacade.obtenerListaSocialIdTipo(tipo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}