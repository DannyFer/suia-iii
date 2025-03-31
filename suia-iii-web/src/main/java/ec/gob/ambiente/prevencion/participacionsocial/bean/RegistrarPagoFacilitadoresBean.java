package ec.gob.ambiente.prevencion.participacionsocial.bean;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.ImpuestosFacilitadoresAmbientales;
import ec.gob.ambiente.suia.domain.Pago;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ImpuestosFacilitadoresAmbientalesFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RegistrarPagoFacilitadoresBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2342724045587316092L;

    private static final Logger LOG = Logger.getLogger(RegistrarPagoFacilitadoresBean.class);

    @Getter
    @Setter
    private Pago pagoFacilitador;

    @Getter
    @Setter
    private Integer nroFacilitadores;

    @Getter
    @Setter
    private double totalAPagarXFacilitadores;

    @Getter
    @Setter
    private UploadedFile fichero;

    @Setter
    @Getter
    private File adjuntoPago;

    @Setter
    @Getter
    private String nombreAdjunto = "";

    @Setter
    @Getter
    private Boolean completado;

    @Getter
    @Setter
    private String facilitadores;

    @Getter
    @Setter
    private Integer nroFacilitadoresPrevios = 0;

    @Getter
    @Setter
    private boolean facilitadoresAdicionales;

    @Getter
    @Setter
    private Map<String, Object> variables;

    @EJB
    private ProcesoFacade procesoFacade;


    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;

    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
    @EJB
    private ImpuestosFacilitadoresAmbientalesFacade impuestosFacilitadoresAmbientalesFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @PostConstruct
    public void init() {
        try {
            variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
                    bandejaTareasBean.getTarea().getProcessInstanceId());
            pagoFacilitador = new Pago();
            nroFacilitadoresPrevios = Integer.parseInt((String) variables.get("numeroFacilitadores"));
            nroFacilitadores = nroFacilitadoresPrevios;
            facilitadoresAdicionales = false;
            isVerDiag();
            if (variables.get("requiereMasFacilitadores") != null) {
                facilitadoresAdicionales = Boolean.parseBoolean((String) variables.get("requiereMasFacilitadores"));
                if (facilitadoresAdicionales) {
                    nroFacilitadores =
                            Integer.parseInt((String) variables.get("numeroFacilitadoresAdicionales"));
                    facilitadores = participacionSocialFacade.listadoFacilitadores(
                            proyectosBean.getProyecto());
                }
            }

            calculoTotalAPagar();
            completado = true;
        } catch (ServiceException e) {
            LOG.error("Error al calcular el pago", e);
        } catch (JbpmException e) {
//            JsfUtil.addMessageError("Error al cargar los datos.");
            LOG.error("Error al recuperar las variables del proceso.", e);
        } catch (Exception e) {
//            JsfUtil.addMessageError("Error al cargar los datos.");
            LOG.error("Error al recuperar las variables del proceso.", e);
        }
    }


    /**
     * Calcula el valor total a pagar de acuerdo al numero de facilitadores registrados
     */
    public void calculoTotalAPagar() throws Exception {
        ImpuestosFacilitadoresAmbientales impuestos = impuestosFacilitadoresAmbientalesFacade.obtenerImpuestoPorUbicacion(obtenerCodigoInec());
        totalAPagarXFacilitadores = 0;
        
        Double valor = impuestos.getValorFacilitadorExterno() != null ? impuestos.getValorFacilitadorExterno() : impuestos.getValorFacilitadorInterno();
        totalAPagarXFacilitadores = nroFacilitadores * valor;
        
        /* Se comenta porque está configuración ya fue quitada, porque ahora los facilitadores son solo de mae. 
        String areaRep= proyectosBean.getProyecto().getAreaResponsable().getAreaName();
        if(areaRep.equals("MUY ILUSTRE MUNICIPALIDAD DE GUAYAQUIL") || areaRep.equals("GOBIERNO AUTÓNOMO DESCENTRALIZADO PROVINCIAL DE EL ORO")){
        	if(areaRep.equals("MUY ILUSTRE MUNICIPALIDAD DE GUAYAQUIL")){
        		Double valor=800.00;
        		totalAPagarXFacilitadores = nroFacilitadores * valor;
        	}
        	if (areaRep.equals("GOBIERNO AUTÓNOMO DESCENTRALIZADO PROVINCIAL DE EL ORO")) {
        		Double valor=550.00;
        		totalAPagarXFacilitadores = nroFacilitadores * valor;
			}
        }else {
            Double valor = impuestos.getValorFacilitadorExterno() != null ? impuestos.getValorFacilitadorExterno() : impuestos.getValorFacilitadorInterno();
            totalAPagarXFacilitadores = nroFacilitadores * valor;
		}*/

        proyectosBean.getProyecto().getProyectoUbicacionesGeograficas();
    }

    public String obtenerCodigoInec() throws Exception {
        ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = proyectoLicenciamientoAmbientalFacade.cargarProyectoFullPorId(proyectosBean.getProyecto().getId());

        /*if (proyectosBean.getProyecto().isConcesionesMinerasMultiples()) {
            ConcesionMinera primeraConcesion = proyectosBean.getProyecto().getConcesionMinera();
            if (primeraConcesion != null && !primeraConcesion.getConcesionesUbicacionesGeograficas().isEmpty()) {
                return primeraConcesion.getConcesionesUbicacionesGeograficas().get(0).getUbicacionesGeografica().getCodificacionInec();
            }
        } else {*/
            List<UbicacionesGeografica> ubicacionGeograficas = proyectoLicenciamientoAmbiental.getUbicacionesGeograficas();
            for( UbicacionesGeografica u : ubicacionGeograficas) {
                return u.getCodificacionInec();
            }
        //}


        return "";
    }
    
    public void cancelarActividadesMineria() {
		 JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
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

}
