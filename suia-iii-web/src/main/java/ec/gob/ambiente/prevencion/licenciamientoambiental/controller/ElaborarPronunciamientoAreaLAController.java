package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.licenciamientoambiental.bean.ElaborarPronunciamientoAreaLABean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.carga.facade.CargaLaboralFacade;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class ElaborarPronunciamientoAreaLAController implements Serializable {

    private static final long serialVersionUID = -3524836879863L;
    private static final Logger LOGGER = Logger
            .getLogger(ElaborarPronunciamientoAreaLAController.class);
    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private AreaFacade areaFacade;

    @EJB
    private PronunciamientoFacade pronunciamientoFacade;
    
    @EJB
	private CargaLaboralFacade cargaLaboralFacade;
    
    @EJB
    private UsuarioFacade usuarioFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{elaborarPronunciamientoAreaLABean}")
    private ElaborarPronunciamientoAreaLABean elaborarPronunciamientoAreaLABean;
    
    private String tecnicoResponsable;
    
    private Map<String,Object>variables;

    public String culminarTarea() {

        try {
        	        	
        	if(!buscarTecnicoResponsable()){
        		JsfUtil.addMessageError("Error al realizar la operación.");
        		return "";
        	}
        	
            elaborarPronunciamientoAreaLABean.getPronunciamiento().setFecha(new Date());
            pronunciamientoFacade.saveOrUpdate(elaborarPronunciamientoAreaLABean.getPronunciamiento());

            if(elaborarPronunciamientoAreaLABean.getArea().equals("Forestal") || elaborarPronunciamientoAreaLABean.getArea().equals("Biodiversidad")){
            elaborarPronunciamientoAreaLABean.generarDocumentoPronunciamiento(false);
            }
            
            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data,
                    loginBean.getPassword(),
                    Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";
    }
    
    

    public void guardar() {

        try {
            pronunciamientoFacade.saveOrUpdate(elaborarPronunciamientoAreaLABean.getPronunciamiento());
            if(elaborarPronunciamientoAreaLABean.getArea().equals("Forestal") || elaborarPronunciamientoAreaLABean.getArea().equals("Biodiversidad")){
            elaborarPronunciamientoAreaLABean.generarDocumentoPronunciamiento(true);
            }

            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");

        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
    }
    
    public boolean buscarTecnicoResponsable(){
    	try {
    		
    		variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());
    		String tecnicoRes=(String)variables.get("u_TecnicoResponsable");
    		
    		Usuario usuarioAsignado = usuarioFacade.buscarUsuario(tecnicoRes);
    		
    		if(usuarioAsignado != null && usuarioAsignado.getId() != null){
    			return true;
    		}    		
    		
    		Integer codigoSector=variables.containsKey("codigoSector")?Integer.valueOf((String)variables.get("codigoSector")):0;
    		
    		String roleType = "role.area.tecnico";
    		String role = "";

            if (!elaborarPronunciamientoAreaLABean.getProyectoActivo().getAreaResponsable().getTipoArea().getSiglas()
                    .equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {

            } else {
                roleType = "role.pc.tecnico.area." + elaborarPronunciamientoAreaLABean.getProyectoActivo().getTipoSector().getId().toString();
            }
            String subArea = elaborarPronunciamientoAreaLABean.getProyectoActivo().getAreaResponsable().getAreaName();
        	
            role = Constantes.getRoleAreaName(roleType);
            
            List<Usuario> tecnicosSociales=new ArrayList<Usuario>();
			List<Usuario> tecnicosCartografos=new ArrayList<Usuario>();
			List<Usuario> tecnicosElectricos=new ArrayList<Usuario>();
			List<Usuario> tecnicosMineros=new ArrayList<Usuario>();
			List<Usuario> tecnicosOtrosSectores=new ArrayList<Usuario>();
			List<Usuario> users=new ArrayList<Usuario>();
			if(codigoSector==2){
				if (role.equals("TÉCNICO SOCIAL") || role.equals("TÉCNICO SOCIAL MAE")) {
					String roleSocial="TÉCNICO SOCIAL MAE MINERÍA";

					tecnicosSociales = cargaLaboralFacade.cargaLaboralPorUsuarioArea(roleSocial, subArea);
				}else 
				if(role.equals("TÉCNICO CARTÓGRAFO") || role.equals("TÉCNICO CARTÓGRAFO MAE") || role.equals("TÉCNICO CARTOGRAFO") ){
					String roleCartografo="TÉCNICO CARTÓGRAFO MAE MINERÍA";

					tecnicosCartografos = cargaLaboralFacade.cargaLaboralPorUsuarioArea(roleCartografo, subArea);
				}else 
				if(role.equals("TÉCNICO ELÉCTRICO") || role.equals("TÉCNICO ELÉCTRICO MAE") || role.equals("TÉCNICO ELECTRICO")){
					String rolElectrico="TÉCNICO ELÉCTRICO MAE MINERÍA";

					tecnicosElectricos = cargaLaboralFacade.cargaLaboralPorUsuarioArea(rolElectrico, subArea);
				}else
				if(role.equals("TÉCNICO MINERÍA") || role.equals("TÉCNICO MINERÍA MAE") || role.equals("TÉCNICO MINERIA")){
					String rolMinero="TÉCNICO MINERÍA MAE";

					tecnicosMineros = cargaLaboralFacade.cargaLaboralPorUsuarioArea(rolMinero, subArea);
				}else
				if(role.equals("TÉCNICO OTROS SECTORES") ||role.equals("TÉCNICO OTROS SECTORES MAE")){
					String rolOtrosSectores="TÉCNICO OTROS SECTORES MAE";

					tecnicosMineros = cargaLaboralFacade.cargaLaboralPorUsuarioArea(rolOtrosSectores, subArea);
				}else{
					users = cargaLaboralFacade.cargaLaboralPorUsuarioArea(role, subArea);
				}
				if(tecnicosSociales.size()>0){
					for (Usuario usuario : tecnicosSociales) {
						users.add(usuario);
					}
				}
				if(tecnicosCartografos.size()>0){
					for (Usuario usuario : tecnicosCartografos) {
						users.add(usuario);
					} 


				}
				if(tecnicosElectricos.size()>0){
					for (Usuario usuario : tecnicosElectricos) {
						users.add(usuario);
					}
				}
				if(tecnicosMineros.size()>0){
					for (Usuario usuario : tecnicosMineros) {
						users.add(usuario);
					}
				}
				if(tecnicosOtrosSectores.size()>0){
					for (Usuario usuario : tecnicosOtrosSectores) {
						users.add(usuario);
					}
				}			
			}else{
				users = cargaLaboralFacade.cargaLaboralPorUsuarioArea(role, subArea);
			}
    					
			if(users != null && !users.isEmpty()){
				
				tecnicoResponsable = users.get(0).getNombre();
				Map<String, Object> params = new ConcurrentHashMap<String, Object>();
				params.put("u_TecnicoResponsable", tecnicoResponsable);   
				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);				
				
				return true;
			}else{
				return false;
			}
    		
		} catch (Exception e) {
			LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
            return false;
		}
    }
}
