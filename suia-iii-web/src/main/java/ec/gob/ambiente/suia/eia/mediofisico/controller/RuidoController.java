package ec.gob.ambiente.suia.eia.mediofisico.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.coordenadageneral.facade.CoordenadaGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.Ruido;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.eia.mediofisico.bean.MedioFisicoBean;
import ec.gob.ambiente.suia.eia.mediofisico.facade.RuidoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.util.Map;

@ViewScoped
@ManagedBean
public class RuidoController implements Serializable {
	private static final Logger LOG = Logger.getLogger(RuidoController.class);
	private static final long serialVersionUID = -6546460627145729406L;
	@Getter
	@Setter
	@ManagedProperty(value = "#{medioFisicoBean}")
	private MedioFisicoBean medioFisicoBean;
	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;
	@EJB
	private RuidoFacade ruidoFacade;
	@EJB
	private CoordenadaGeneralFacade coordenadaGeneralFacade;

	public static String UNIDAD_MEDIDA = "dB";
	public static String HORARIO_DEFAULT = "Diurno";
	public static String ROJO="rojo";
	@Getter
	@Setter
	private Integer eiaId;
	
	@PostConstruct
	public void iniciar(){
		try{
		EstudioImpactoAmbiental eia = (EstudioImpactoAmbiental)JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
        setEiaId(eia.getId());
        List<Ruido> ruidos= ruidoFacade.ruidoXEiaId(getEiaId());
        if(!ruidos.isEmpty()){
        	medioFisicoBean.setListaRuido(ruidos);
        	for (Ruido ruido : ruidos) {
				CoordenadaGeneral coordenadaGeneral= coordenadaGeneralFacade.coordenadasGeneralXTablaId(ruido.getId(), Ruido.class.getSimpleName()).get(0);
				ruido.setCoordenadaGeneral(coordenadaGeneral);
				ruido.setLaboratorio(catalogoGeneralFacade.obtenerCatalogoXId(ruido.getIdLaboratorio()));
				if(ruido.getResultado()>ruido.getLimite())
					ruido.setColor(ROJO);
			}
        }
        medioFisicoBean.setLaboratorios(catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.LABORATORIOS));
        medioFisicoBean.setRuidoBorradas(new ArrayList<Ruido>());
		}catch(Exception ex){
        	LOG.error("Error al cargar parametros", ex);
        }
        
	}

	public void limiteUnidad() {
		try {
			CatalogoGeneral catalogoGeneral = catalogoGeneralFacade
					.obtenerCatalogoXPadreXCodigo(medioFisicoBean.getRuido().getCatalogoGeneral()
									.getId(),medioFisicoBean.getRuido().getHorario()).get(0);
			medioFisicoBean.getRuido().setLimite(Double.valueOf(catalogoGeneral.getDescripcion()));

		} catch (Exception ex) {
			LOG.error("Error al cargar la unidad", ex);
		}
	}

	public void agregarAListaRuido() {
		try {
			if(medioFisicoBean.getRuido().getResultado()>medioFisicoBean.getRuido().getLimite())
				medioFisicoBean.getRuido().setColor(ROJO);
			else
				medioFisicoBean.getRuido().setColor("");
			if (!medioFisicoBean.getRuido().isEditar())
				medioFisicoBean.getListaRuido().add(medioFisicoBean.getRuido());
			RequestContext.getCurrentInstance().addCallbackParam("ruidoIn",
					true);
		} catch (Exception e) {
			RequestContext.getCurrentInstance().addCallbackParam("ruidoIn",
					false);
		}
	}

	public void agregarRuido() {
		Ruido ruido = new Ruido();
		ruido.setEditar(false);
		ruido.setEiaId(getEiaId());
		medioFisicoBean.setRuido(ruido);
		medioFisicoBean.getRuido().setLimite(0.0);
		medioFisicoBean.getRuido().setHorario(HORARIO_DEFAULT);
		medioFisicoBean.getRuido().setCatalogoGeneral(
				medioFisicoBean.getListaCatalogoGeneral().get(0));
		medioFisicoBean.getRuido()
				.setCoordenadaGeneral(new CoordenadaGeneral());
		limiteUnidad();
	}

	public void seleccionarRuido(Ruido ruido) {
		ruido.setEditar(true);
		medioFisicoBean.setRuido(ruido);
	}

	public void eliminarRuido(Ruido ruido) {
		medioFisicoBean.getListaRuido().remove(ruido);
		if (ruido.getId() != null) {
			ruido.setEstado(false);
			medioFisicoBean.getRuidoBorradas().add(ruido);
		}
	}

	public void limpiar() {
		medioFisicoBean.setListaRuido(new ArrayList<Ruido>());
	}

	public String cancelar() {
		medioFisicoBean.setListaRuido(new ArrayList<Ruido>());
		return JsfUtil.actionNavigateTo("/pages/eia/lineaBase/ruido.jsf");
	}

	public String guardar() {
		try {
                    if(!medioFisicoBean.getListaRuido().isEmpty()){
                        EstudioImpactoAmbiental es = new EstudioImpactoAmbiental(getEiaId());
                        Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
                            ruidoFacade.guardarRuido(medioFisicoBean.getListaRuido(),medioFisicoBean.getRuidoBorradas(), es, mapOpciones.get(EiaOpciones.NIVEL_PRESION_SONORA_HIDRO));
                            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
                            limpiar();
                            return JsfUtil.actionNavigateTo("/pages/eia/default.jsf");
                        }else{
                            JsfUtil.addMessageError("Debe ingresar registros");
                    }
		} catch (Exception ex) {
			LOG.error("Error al guardar", ex);
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
		}
		return null;
	}


}
