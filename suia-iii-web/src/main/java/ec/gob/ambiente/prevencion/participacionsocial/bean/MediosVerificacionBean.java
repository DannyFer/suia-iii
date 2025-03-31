package ec.gob.ambiente.prevencion.participacionsocial.bean;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.CatalogoMediosParticipacionSocial;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.MediosVerificacionFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ViewScoped
@ManagedBean
public class MediosVerificacionBean implements Serializable{
	

	private static final long serialVersionUID = -4587273090187211780L;

	@EJB
	private ParticipacionSocialFacade participacionSocialFacade;
	
	@EJB
	private MediosVerificacionFacade mediosVerificacionFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@Setter
	@Getter
	private CatalogoMediosParticipacionSocial catalogoMedio;	
	
	@Setter
	@Getter
	private List<CatalogoMediosParticipacionSocial> catalogoMediosParticipacionSociales;
	
	@Setter
	@Getter
	private RegistroMediosParticipacionSocial registroMediosParticipacionSocial;
	
	@Setter
	@Getter
	private List<RegistroMediosParticipacionSocial> registrosMediosParticipacionSocial;
	
	@Setter
	@Getter
	private ParticipacionSocialAmbiental participacionSocialAmbiental;
	
	@Getter
	@Setter
	private String documentoActivo = "";

	@Getter
	@Setter
	private Map<String, Documento> documentos;
	@Getter
	@Setter
	private boolean revisar;

	@Getter
	@Setter
	private Boolean informacionCompleta;

	@Getter
	@Setter
	private String tipo = "";
    @Getter
    @Setter
    private List<String> listaClaves;
	
	@PostConstruct
	public void init() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		if (params.containsKey("tipo")) {
			tipo = params.get("tipo");
			if (params.get("tipo").equals("revisar")) {
				revisar = true;
			}
		}
		setCatalogoMediosParticipacionSociales(mediosVerificacionFacade.getCatalogoMediosParticipacionSocial());
		if(proyectosBean.getProyecto()!=null){
			setParticipacionSocialAmbiental(participacionSocialFacade.getProyectoParticipacionSocialByProject(proyectosBean.getProyecto()));
			loadDataTable();
		}
		registroMediosParticipacionSocial=new RegistroMediosParticipacionSocial();
		
		  listaClaves = new ArrayList<>(1);
		  listaClaves.add("respaldoMediosVerificacionPPS");
		  documentos=new HashMap<>();
		  
		  try {
			documentos=participacionSocialFacade.recuperarDocumentosTipo(proyectosBean.getProyecto().getId(), RegistroMediosParticipacionSocial.class.getSimpleName(),listaClaves);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Setter
    private boolean verDiag=true;	
	
	public boolean isVerDiag() throws ParseException {
		verDiag=true;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechabloqueo = sdf.parse(Constantes.getFechaBloqueoTdrMineria());
        Date fechaproyecto=sdf.parse(proyectosBean.getProyecto().getFechaRegistro().toString());
        boolean bloquear=false;        
        if (fechaproyecto.before(fechabloqueo)){
        	bloquear=true;
        }    		
    	if ((proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.01") 
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.02")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.03")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.04")    			
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.04.01")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.04.02")    			
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.05.01")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.05.02")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.06.01")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.06.03")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.07.01")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.07.02")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.08.01")    			
    			) && proyectosBean.getProyecto().getIdEstadoAprobacionTdr()==null && bloquear){    		        	
			if (verDiag) {
				verDiag = false;
				return true;
			}
		} else {
			verDiag = false;
		}
		return verDiag;
	}
	
	public void cancelarActividadesMineria() {
		 JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	}
	
	public void loadDataTable()
	{
		registrosMediosParticipacionSocial=mediosVerificacionFacade.getRecordsByProjectId(this.participacionSocialAmbiental);
	}

}
