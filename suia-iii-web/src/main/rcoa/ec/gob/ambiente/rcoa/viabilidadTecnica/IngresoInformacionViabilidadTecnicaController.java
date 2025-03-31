package ec.gob.ambiente.rcoa.viabilidadTecnica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.ViabilidadTecFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.ViabilidadTecnicaProyectoFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.ViabilidadTecnicaRcoaFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnica;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnicaProyecto;
import ec.gob.ambiente.suia.administracion.bean.UbicacionGeograficaBean;
import ec.gob.ambiente.suia.administracion.controllers.UbicacionGeograficaController;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

import java.util.List;

@ManagedBean
@ViewScoped
public class IngresoInformacionViabilidadTecnicaController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String SIN_SERVICIO = "Sin servicio";
	
	@EJB
	private UbicacionGeograficaFacade ubicacionesGeograficaDao;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	@EJB
	private ViabilidadTecnicaRcoaFacade viabilidadTecnicaRCOAFacade;
	
	@EJB
	private ViabilidadTecFacade viabilidadTecFacade;

	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@EJB
	private ViabilidadTecnicaProyectoFacade viabilidadTecnicaProyectoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@SuppressWarnings("unused")
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());

	@Getter
	@Setter
	private Date fechaActual;

	@Getter
	@Setter
	private ViabilidadTecnica viabilidadTecnica = new ViabilidadTecnica();

	@Getter
	@Setter
	private String razonSocial;

	@Getter
	@Setter
	private UbicacionGeograficaBean ubicacionGeograficaBean = new UbicacionGeograficaBean();	

	@Getter
	@Setter
	private Integer idCanton, idProvincia;

	@Getter
	private List<CatalogoGeneralCoa> listaFasesViabilidad;

	@Getter
	private List<ViabilidadTecnica> listaViabilidadTecnica;	

	@Getter
	@Setter
	private Integer idTipoViabilidad;

	@Getter
	@Setter
	private Integer idFases;

	@Getter
	@Setter
	private Boolean verificacionRuc;

	@Getter
	@Setter
	@ManagedProperty(value = "#{ubicacionGeograficaController}")
	private UbicacionGeograficaController ubicacionGeograficaController;
	
	@Getter
    @Setter
    private List<UbicacionesGeografica> listaProvincia;

    @Getter
    @Setter
    private List<UbicacionesGeografica> listaCanton;
    
    @Getter
    @Setter
    private String codigoOficioMod;

	@PostConstruct
	public void init() {
		fechaActual = new Date();
		this.verificacionRuc = true;
		this.idTipoViabilidad = 1;		
		listaViabilidadTecnica = viabilidadTecFacade.getListaViabilidadPorUsuario(loginBean.getUsuario().getId());
		listaFasesViabilidad = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.TIPO_FASE_VIABILIDAD);
		this.viabilidadTecnica = new ViabilidadTecnica();
		this.razonSocial = "";
		this.idFases = null;
		cargarProvincias();
		idProvincia = null;
		idCanton = null;
	}

	public void guardarVialbilidadTecnica(ViabilidadTecnica viabilidad) throws ServiceException {
		try {
			if (validarVialidadTecnica()) {
				String mensaje = JsfUtil.REGISTRO_GUARDADO;
				
				if (viabilidad.getId() == null) {
					viabilidad.setFechaCreacion(new Date());
					viabilidad.setUsuarioCreacion(loginBean.getUsuario().getNombre());
				} else {
					viabilidad.setFechaModificacion(new Date());
					viabilidad.setUsuarioModificacion(loginBean.getUsuario().getNombre());
					mensaje = JsfUtil.REGISTRO_ACTUALIZADO;
				}
				
				viabilidad.setEstado(true);
				viabilidad.setUsuario(loginBean.getUsuario());
				viabilidad.setTipoViabilidad(idTipoViabilidad);
				viabilidad.setCatalogoGeneralFases(catalogoCoaFacade.obtenerCatalogoPorId(this.idFases));
				UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(idCanton);
				viabilidad.setUbicacionGeografica(ubicacion);
				viabilidad.setRazonSocial(this.razonSocial);
				viabilidad.setEsRellenoSanitario(null);
				if(idTipoViabilidad == 3) {
					CatalogoGeneralCoa catalogoFinal =  catalogoCoaFacade.obtenerCatalogoPorCodigo("fase.viabilidad.tecnica.relleno.sanitario");
					if(catalogoFinal.getId().equals(idFases)) {
						viabilidad.setEsRellenoSanitario(true);
					}
				}
				
				viabilidadTecFacade.guardarViabilidadTecnica(viabilidad);
				JsfUtil.addMessageInfo(mensaje);
				
				limpiar();
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void validarRuc() {
		if (viabilidadTecnica.getRuc().length() == 13) {
			ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula.obtenerPorRucSRI(
					Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, viabilidadTecnica.getRuc());
			if (contribuyenteCompleto == null || contribuyenteCompleto.getCodEstado() == null) {
				JsfUtil.addMessageError("RUC inválido");
				this.verificacionRuc = true;
				return;
			}
			if(contribuyenteCompleto.getTipoContribuyente().getUltimoNivel().equals("PERSONAS NATURALES")){
				JsfUtil.addMessageError("El número de RUC debe se de una empresa");
				this.verificacionRuc = true;
				return;
			}
			
			if (!contribuyenteCompleto.getCodEstado().equals("PAS") || Constantes.getPermitirRUCPasivo()) {
				cargarDatosWsRucPersonaNatural(contribuyenteCompleto);
			} else {

				JsfUtil.addMessageError(
						"El estado de su RUC es PASIVO. Si desea registrarse con el mismo debe activarlo en el SRI.");
				this.verificacionRuc = true;
			}
		} else {

			JsfUtil.addMessageError("El RUC debe tener 13 dígitos.");
			this.verificacionRuc = false;
		}

	}

	private void cargarDatosWsRucPersonaNatural(ContribuyenteCompleto contribuyenteCompleto) {
		if (contribuyenteCompleto != null) {
			if (contribuyenteCompleto.getRazonSocial() != null) {
				viabilidadTecnica.setRuc(contribuyenteCompleto.getNumeroRuc());
				this.razonSocial = contribuyenteCompleto.getRazonSocial();
				this.verificacionRuc = true;
			} else {
				this.verificacionRuc = false;
				JsfUtil.addMessageError("RUC no encontrado.");
			}
		} else {
			this.verificacionRuc = false;
			JsfUtil.addMessageError(SIN_SERVICIO);
		}
	}

	public void editarViabilidadTecnica(ViabilidadTecnica viabilidadT) {
		this.viabilidadTecnica = viabilidadT;
		this.idTipoViabilidad = viabilidadT.getTipoViabilidad();
		this.idFases = viabilidadT.getCatalogoGeneralFases() == null ? 0 : viabilidadT.getCatalogoGeneralFases().getId();		
		this.idProvincia = viabilidadT.getUbicacionGeografica().getUbicacionesGeografica().getId();
		this.razonSocial=viabilidadT.getRazonSocial();		
		cargarCanton();
		this.idCanton = viabilidadT.getUbicacionGeografica().getId();
		this.codigoOficioMod = viabilidadT.getNumeroOficio();
	}		
	
	private void cargarProvincias() {
        try {
        	listaProvincia = ubicacionGeograficaFacade.listarProvincia();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public void cargarCanton() {
        try {
            if (idProvincia != null) {
            	listaCanton = ubicacionGeograficaFacade.listarPorPadre(new UbicacionesGeografica(Integer.valueOf(idProvincia)));                
            } else {
            	listaCanton = null;
            	idCanton = null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

	public void eliminarViabilidadTecnica(ViabilidadTecnica viabilidadE) {
		
		if(validarViabilidadExistente(viabilidadE)){
			JsfUtil.addMessageError("El registro que desea eliminar se encuentra utilizado en un trámite");
			return;
		}
		
		viabilidadE.setEstado(false);
		viabilidadE.setFechaModificacion(new Date());
		viabilidadE.setUsuarioModificacion(loginBean.getUsuario().getNombre());
		viabilidadE.setUsuario(loginBean.getUsuario());
		viabilidadTecFacade.guardarViabilidadTecnica(viabilidadE);
		listaViabilidadTecnica.remove(viabilidadE);
		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);

	}
	
	public boolean validarViabilidadExistente(ViabilidadTecnica viabilidad){
		try {
			List<ViabilidadTecnicaProyecto> lista = viabilidadTecnicaProyectoFacade.buscarPorViabilidad(viabilidad.getId());
			if(lista != null && !lista.isEmpty()){
				return true;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void limpiar() {
		this.verificacionRuc = true;
		this.idTipoViabilidad = 1;
		this.viabilidadTecnica = new ViabilidadTecnica();
		this.razonSocial = "";
		this.idFases = null;
		cargarProvincias();
		this.idProvincia = null;
		this.idCanton=null;		
		listaViabilidadTecnica = viabilidadTecFacade.getListaViabilidadPorUsuario(loginBean.getUsuario().getId());
		
	}

	public boolean validarVialidadTecnica() {
		List<String> listaMensajes = new ArrayList<String>();
		if (this.viabilidadTecnica.getRuc() != null && this.viabilidadTecnica.getRuc().isEmpty()) {
			listaMensajes.add("El campo  RUC es requerido.");
		}
		
		if (this.viabilidadTecnica.getNumeroOficio() == null|| this.viabilidadTecnica.getNumeroOficio().isEmpty()) {
			listaMensajes.add("El campo 'Número de oficio' es requerido.");
		}
		if (this.viabilidadTecnica.getFecha() == null) {
			listaMensajes.add("El campo 'Fecha' es requerido.");
		}

		if (this.idCanton==0) {
			listaMensajes.add("Los campos de 'Ubicación' son requeridos.");
		}
		if(viabilidadTecnica.getTipoViabilidad() != null && viabilidadTecnica.getTipoViabilidad() == 3){
			if (this.idFases != null && this.idFases==0) {
				listaMensajes.add("Los campos de 'Fases' son requeridos.");
			}
		}
		
		if (this.razonSocial==null|| this.razonSocial.isEmpty()) {
			listaMensajes.add("Los campos de 'Razon Social' son requeridos.");
		}
				
		if(this.viabilidadTecnica.getNumeroOficio() != null){
			List<ViabilidadTecnica> listaRegistros = viabilidadTecnicaRCOAFacade.buscarPorCodigo(this.viabilidadTecnica.getNumeroOficio());
			if(listaRegistros != null && !listaRegistros.isEmpty()){
				for(ViabilidadTecnica via : listaRegistros){
					if(viabilidadTecnica.getId() != null){
						if(!viabilidadTecnica.getRuc().equals(via.getRuc())){
							listaMensajes.add("El código de Oficio ya se encuentra ingresado.");
							break;							
						}else{
							if(!codigoOficioMod.equals(this.viabilidadTecnica.getNumeroOficio())){
								listaMensajes.add("El código de Oficio ya se encuentra ingresado.");
								break;
							}
						}
					}else{
						listaMensajes.add("El código de Oficio ya se encuentra ingresado.");
						break;
					}					
				}				
			}
		}

		if (listaMensajes.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			return false;
		}
	}
	


}
