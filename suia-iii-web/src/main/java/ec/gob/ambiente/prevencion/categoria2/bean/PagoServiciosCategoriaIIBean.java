package ec.gob.ambiente.prevencion.categoria2.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validaPago.bean.PagoConfiguracionesBean;

@ManagedBean
@ViewScoped
public class PagoServiciosCategoriaIIBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2013863581401149879L;
    private static final Logger LOG = Logger.getLogger(PagoServiciosCategoriaIIBean.class);

    @Getter
    @Setter
    public Boolean cumpleMonto = false;

    @Getter
    @Setter
    public double montoTotal = Constantes.getPropertyAsDouble("costo.tramite.registro.ambiental");

    @Getter
    public  double costoTramite = Constantes.getPropertyAsDouble("costo.tramite.registro.ambiental");

    @Getter
    @Setter
    private List<InstitucionFinanciera> institucionesFinancieras;

    @Getter
    @Setter
    private TransaccionFinanciera transaccionFinanciera;

    @Getter
    @Setter
    private List<TransaccionFinanciera> transaccionesFinancieras;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB
    private InstitucionFinancieraFacade institucionFinancieraFacade;

    @EJB
    private TransaccionFinancieraFacade transaccionFinancieraFacade;

    /****** campos para validar el tipo de pago (2016/01/21) ******/
    
    private Organizacion organizacion= new Organizacion();

    @EJB
    private OrganizacionFacade organizacionFacade;

    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoFacade;
    
    /***** módulo de pagos (2016-02-04)  *****/
    private static final String MENOS_PARTICIPACION = "EMPRESAS MIXTAS CUANDO SU CAPITAL SUSCRITO SEA MENOR A LAS DOS TERCERAS PARTES A ENTIDADES DE DERECHO PÚBLICO.";
    private static final String IGUAL_MAS_PARTICIPACION = "EMPRESAS MIXTAS CUANDO SU CAPITAL SUSCRITO PERTENEZCA POR LO MENOS A LAS DOS TERCERAS PARTES A ENTIDADES DE DERECHO PÚBLICO.";
    /***** módulo de pagos (2016-02-04)  *****/
    
    @PostConstruct
    public void init() throws Exception {

    	/***** módulo de pagos (2016-02-04) *****/
    	montoTotal = validaCostoRegistroAmbiental(montoTotal);
    	// System.out.println("valor a pagar>>> " + montoTotal);
    	/***** módulo de pagos (2016-02-04) *****/
    	/**
		* Walter
		* se realizo la separacion por entes acreditados y planta central.
		*/
    	UbicacionesGeografica ug = null;
		try {
			ug = proyectosBean.getProyecto()
					.getUbicacionesGeograficas().get(0)
					.getUbicacionesGeografica().getUbicacionesGeografica();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(proyectosBean.getProyecto().getAreaResponsable().getTipoEnteAcreditado()!=null)
		{
			if(proyectosBean.getProyecto().getAreaResponsable().getTipoEnteAcreditado().equals("GOBIERNO"))
			{
				institucionesFinancieras = institucionFinancieraFacade
						.obtenerListaInstitucionesFinancierasProv(ug.getNombre(),ug.getCodificacionInec());
			}
			if(proyectosBean.getProyecto().getAreaResponsable().getTipoEnteAcreditado().equals("MUNICIPIO"))
			{
//				institucionesFinancieras = institucionFinancieraFacade
//						.obtenerListaInstitucionesFinancierasMuni(ug.getNombre(),ug.getCodificacionInec());
				UbicacionesGeografica ug1 = proyectosBean.getProyecto()
						.getUbicacionesGeograficas().get(0)
						.getUbicacionesGeografica();
				institucionesFinancieras = institucionFinancieraFacade
						.obtenerListaInstitucionesFinancierasMuni(
								ug1.getNombre(), ug1.getCodificacionInec());
			}
		}
		else
		{
			institucionesFinancieras = institucionFinancieraFacade
					.obtenerListaInstitucionesFinancierasPC();
		}
//    	institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancieras();
		/**
		* fin de la separacion por entes acreditados y planta central.
		*/    
	   
        transaccionFinanciera = new TransaccionFinanciera();
        transaccionesFinancieras = transaccionFinancieraFacade.cargarTransacciones(proyectosBean.getProyecto().getId(),
                bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getTaskId());
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/pagoServicios.jsf");
    }

    public void guardarTransaccion(){
        if (transaccionFinanciera.getInstitucionFinanciera() != null && transaccionFinanciera.getNumeroTransaccion() != "") {
            try {
                if (existeTransaccion(transaccionFinanciera)) {
                    JsfUtil.addMessageInfo("El número de comprobante: "
                            + transaccionFinanciera.getNumeroTransaccion() + " (" + transaccionFinanciera.getInstitucionFinanciera().getNombreInstitucion() + ") ya fue registrado por usted. Ingrese otro distinto.");
                    return;
                } else {
                    double monto = transaccionFinancieraFacade.consultarSaldo(
                            transaccionFinanciera.getInstitucionFinanciera()
                                    .getCodigoInstitucion(), transaccionFinanciera
                                    .getNumeroTransaccion());

                    if (monto == 0) {
                        JsfUtil.addMessageError("El número de comprobante: "
                                + transaccionFinanciera.getNumeroTransaccion() + " (" + transaccionFinanciera.getInstitucionFinanciera().getNombreInstitucion()
                                + ") no ha sido registrado.");
                        return;
                    } else {
                        transaccionFinanciera.setMontoTransaccion(monto);
                        transaccionFinanciera.setProyecto(proyectosBean.getProyecto());
                        transaccionesFinancieras.add(transaccionFinanciera);
                        cumpleMonto = cumpleMonto();
                        transaccionFinanciera = new TransaccionFinanciera();
                        return;
                    }
                }
            }
            catch (Exception e){
                LOG.error("Error en...", e);
                JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
                return;
            }
        }
        else {
            JsfUtil.addMessageWarning("Debe ingresar datos correctos para la transacción");
        }
    }

    private boolean existeTransaccion(TransaccionFinanciera _transaccionFinanciera){

        for (TransaccionFinanciera transaccion : transaccionesFinancieras) {
            if (transaccion.getNumeroTransaccion().trim()
                    .equals(_transaccionFinanciera.getNumeroTransaccion())
                    && transaccion
                    .getInstitucionFinanciera()
                    .getCodigoInstitucion()
                    .trim()
                    .equals(_transaccionFinanciera
                            .getInstitucionFinanciera()
                            .getCodigoInstitucion())) {
                return true;
            }
        }
        return false;
    }

    private boolean cumpleMonto(){
        double montoTotal = 0;
        for(TransaccionFinanciera transa : transaccionesFinancieras){
            montoTotal += transa.getMontoTransaccion();
        }
        if(montoTotal >= costoTramite){
            return true;
        }
        return false;
    }

    public double Monto(){
        double montoTotal = 0;
        for(TransaccionFinanciera transa : transaccionesFinancieras){
            montoTotal += transa.getMontoTransaccion();
        }
        return montoTotal;
    }

    public void eliminarTransacion(TransaccionFinanciera transaccion) throws Exception {
        transaccionesFinancieras.remove(transaccion);
        cumpleMonto = cumpleMonto();
    }
    
    public double validaCostoRegistroAmbiental(double montoPagar) throws ServiceException {
    	double valorAPagar = 0.00f;
    	organizacion = organizacionFacade.buscarPorPersona(proyectosBean.getProyecto().getUsuario().getPersona()); 
    	
    	// obtención de valores desde bdd
    	Float costoEmisionRegistro = 0f;
    	Float costoControlSeguimiento = 0f;
    	
    	// validación del flujo: Registro Ambiental
    	if(bandejaTareasBean.getTarea().getProcessName().equals("Registro ambiental")) {
    		costoEmisionRegistro = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
    				getPagoConfigValue("Valor por registro ambiental v1"));	//100.00
    		costoControlSeguimiento = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
    				getPagoConfigValue("Valor por seguimiento de registro ambiental v1"));	//80.00
    		
    	}
    	// validación del flujo: Registro Ambiental v2
    	else if(bandejaTareasBean.getTarea().getProcessName().equals("Registro ambiental v2")) {
    		costoEmisionRegistro = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
    				getPagoConfigValue("Valor por registro ambiental v2"));	//100.00
    		costoControlSeguimiento = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
    				getPagoConfigValue("Valor por seguimiento de registro ambiental v2"));	//80.00
    		
    	}
    	
    	// validación del flujo: Registro Ambiental
    	if(bandejaTareasBean.getTarea().getProcessName().equals("Registro ambiental")) {
    		costoEmisionRegistro = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
    				getPagoConfigValue("Valor por registro ambiental v1"));	//100.00
    		costoControlSeguimiento = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
    				getPagoConfigValue("Valor por seguimiento de registro ambiental v1"));	//80.00
    		
    	}
    	// validación del flujo: Registro Ambiental v2
    	else if(bandejaTareasBean.getTarea().getProcessName().equals("Registro ambiental v2")) {
    		costoEmisionRegistro = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
    				getPagoConfigValue("Valor por registro ambiental v2"));	//100.00
    		costoControlSeguimiento = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
    				getPagoConfigValue("Valor por seguimiento de registro ambiental v2"));	//80.00
    		
    	}
    	// System.out.println("costoEmision>>>" + costoEmisionRegistro + "\ncostoControlSeguimiento>>>" + costoControlSeguimiento);
    	
    	//PAGOS PARA PERSONAS JURIDICAS
    	if(organizacion != null) {
    		// valido la categoria del proyecto
    		if(proyectosBean.getProyecto().getCatalogoCategoria().getId() != null) {
    			// No pagan las siguientes actividades:
        		// CULTIVO DE BANANO MENOR O IGUAL A 100 HECTÁREAS: 794
        		// MINERÍA ARTESANAL (METÁLICA, NO METÁLICA Y MATERIALES DE CONSTRUCCIÓN): 848
        		// MINERÍA ARTESANAL: EXPLOTACIÓN: 849
        		// MINERÍA ARTESANAL: EXPLOTACIÓN DE MATERIALES DE CONSTRUCCIÓN (ÁRIDOS Y PÉTREOS): 1457
    			if(proyectosBean.getProyecto().getCatalogoCategoria().getId()==794 
    					|| proyectosBean.getProyecto().getCatalogoCategoria().getId()==848
    					|| proyectosBean.getProyecto().getCatalogoCategoria().getId()==849 
    					|| proyectosBean.getProyecto().getCatalogoCategoria().getId()==1457) {
    				valorAPagar = 0.00f;
    			}
    			// pago de control ($80.00)
        		// OG:2, Gobiernos Autónomos:5, Empresas Públicas:7
    			else if(organizacion.getTipoOrganizacion().getId()==2 || organizacion.getTipoOrganizacion().getId()==5 
    					|| organizacion.getTipoOrganizacion().getId()==7) {
    				valorAPagar = costoControlSeguimiento;
    			}
    			// pago de control y emisión ($180.00)
                // ONG:1, Asociaciones:3, Comunidades:4, Empresas Privadas:6
    			else if(organizacion.getTipoOrganizacion().getId()==1 
    					|| organizacion.getTipoOrganizacion().getId()==3
    					|| organizacion.getTipoOrganizacion().getId()==4 
    					|| organizacion.getTipoOrganizacion().getId()==6){
    				valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
    			}
    			// validación de empresas mixtas: 8
    			// Pagan 180: Menos del 70% de participación del Estado
    			// Pagan 80: Igual o más que el 70% de participación del Estado
    			else if(organizacion.getTipoOrganizacion().getId() == 8) {
    				//if(organizacion.getParticipacionEstado() == null || organizacion.getParticipacionEstado().equals("Menos del 70% de participación del Estado")) {
    				if(organizacion.getParticipacionEstado() == null 
    						|| organizacion.getParticipacionEstado().equals(MENOS_PARTICIPACION)) {
    					valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
    				//} else if(organizacion.getParticipacionEstado().equals("Igual o más que el 70% de participación del Estado")) {
    				} else if(organizacion.getParticipacionEstado().equals(IGUAL_MAS_PARTICIPACION)) {
    					valorAPagar = costoControlSeguimiento;
    				}
    				
    			}
    			
    		}
		} else {
			valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
		}
    	
    	return valorAPagar;
    }
    /****** método para validar el costo del pago de Registro Ambiental (2016/02/04) ******/
}
