package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.io.Serializable;
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

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ObservacionesRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ObservacionesFormulariosRSQ;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class ObservacionesRSQController implements Serializable {

    private static final long serialVersionUID = -1L;
    private static final Logger LOG = Logger.getLogger(ObservacionesRSQController.class);
    @EJB
    private ObservacionesRSQFacade observacionesFacade;


    @Getter
    @Setter
    private Map<String, ObservacionesRSQBean> listaObservacionesBB;
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    @Getter
    @Setter
    private boolean adicionado;
    
    @Getter
    @Setter
    private Map<String, ObservacionesRSQBean> listaObservacionesH;

    @PostConstruct
    private void cargarDatos() {
        listaObservacionesBB = new HashMap<>();
        listaObservacionesH = new HashMap<>();
    }

    /**
     * @param idFormulario
     * @return
     */
    public String obtenerIdSeccion(String idFormulario) {
        ObservacionesRSQBean observacion = listaObservacionesBB.get(idFormulario);
        String sec = "";
        for (String s : observacion.getSeccion()) {
            sec += "-" + s;
        }
        sec = sec.replace("/", "_");
        sec = sec.replace(" ", "_");
        sec = sec.replace(".", "_");
        sec = sec.replace("*", "full");
        return "pnlObservaciones-" + observacion.getNombreClase() + observacion.getIdClase().toString() + sec;
    }


    /**
     * @param idFormulario
     * @param nombreClase
     * @param idClase
     * @param secciones
     */
    public String cargarDatosIniciales(String idFormulario, String nombreClase, String idClase, String secciones) {
        if (!adicionado) {
            int id = idClase != null && !idClase.isEmpty()? Integer.parseInt(idClase):0;
            cargarObservaciones(idFormulario, nombreClase,id, secciones);
        }
        return "label-" + obtenerIdSeccion(idFormulario);
    }

    /**
     * @param idFormulario
     * @param nombreClase
     * @param idClase
     * @param secciones
     */
    public void cargarObservaciones(String idFormulario, String nombreClase, Integer idClase, String... secciones) {
        try {
            ObservacionesRSQBean observacion = new ObservacionesRSQBean();
            observacion.setNombreClase(nombreClase);
            observacion.setIdClase(idClase);
            observacion.setSeccion(secciones);

            observacion.setListaObservaciones(observacionesFacade.listarPorIdClaseNombreClase(idClase, nombreClase, secciones));
            if (observacion.getListaObservaciones() != null && !observacion.getListaObservaciones().isEmpty()) {
                for (ObservacionesFormulariosRSQ ob : observacion.getListaObservaciones()) {
                	if (ob.getIdUsuario().equals(JsfUtil.getLoggedUser().getId())) {
                        if(ob.isObservacionCorregida()==true)
                        	ob.setDisabled(true);
                        else
                        	ob.setDisabled(false);
                    }
                    else{
                    	if(ob.isObservacionCorregida()==true)
                        	ob.setDisabled(true);
                        else
                        	ob.setDisabled(false);
                    }
                }
            }
            for (ObservacionesFormulariosRSQ ob : observacion.getListaObservaciones()) {
                List<ObservacionesFormulariosRSQ> elementos = new ArrayList<>();
                String seccion = ob.getSeccionFormulario();
                if (observacion.getMapaSecciones().containsKey(seccion)) {
                    elementos = observacion.getMapaSecciones().get(ob.getSeccionFormulario());
                }
                elementos.add(ob);
                observacion.getMapaSecciones().put(seccion, elementos);
            }
            for (String seccion : secciones) {
                if (!observacion.getMapaSecciones().containsKey(seccion) && !seccion.equals("*")) {
                    observacion.getMapaSecciones().put(seccion, new ArrayList<ObservacionesFormulariosRSQ>());
                }
            }
            for (String s : observacion.getMapaSecciones().keySet()) {
                reasignarIndice(observacion, s);
            }

            listaObservacionesBB.put(idFormulario, observacion);
        } catch (ServiceException e) {
            LOG.error(e, e);
        } catch (RuntimeException e) {
            LOG.error(e, e);
        }
    }


    /**
     * @param observacionesBB
     * @param seccion
     */
    public void reasignarIndice(ObservacionesRSQBean observacionesBB, String seccion) {
        if (observacionesBB.getMapaSecciones() != null && !observacionesBB.getMapaSecciones().isEmpty()) {
            if (observacionesBB.getMapaSecciones().get(seccion) != null && !observacionesBB.getMapaSecciones().get(seccion).isEmpty()) {
                int i = 0;
                for (ObservacionesFormulariosRSQ ob : observacionesBB.getMapaSecciones().get(seccion)) {
                    ob.setIndice(i);
                    i++;
                }
            }
        }

    }

    /**
     * @param idFormulario
     * @param idClase
     * @param nombreClase
     * @param seccion
     */
    public void agregarObservacion(String idFormulario, String idClase, String nombreClase, String seccion) {
        ObservacionesFormulariosRSQ ob = new ObservacionesFormulariosRSQ();
        ob.setCampo("");
        ob.setDescripcion("");
        ob.setIdClase(new Integer(idClase));
        ob.setNombreClase(nombreClase);
        ob.setSeccionFormulario(seccion);
        ob.setUsuario(JsfUtil.getLoggedUser());
        ob.setFechaRegistro(new Date());
        ob.setDisabled(false);
        try {
            ob.setIdTarea(bandejaTareasBean.getTarea().getTaskId());

        } catch (Exception e) {

        }
        if (!listaObservacionesBB.get(idFormulario).getMapaSecciones().containsKey(seccion)) {
            listaObservacionesBB.get(idFormulario).getMapaSecciones().put(seccion, new ArrayList<ObservacionesFormulariosRSQ>());
        }

        listaObservacionesBB.get(idFormulario).getMapaSecciones().get(seccion).add(ob);
        listaObservacionesBB.get(idFormulario).getListaObservaciones().add(ob);
        reasignarIndice(listaObservacionesBB.get(idFormulario), seccion);
        adicionado = true;
    }

    /**
     * @param idFormulario
     * @param seccion
     */
    public void guardar(String idFormulario, String seccion) {
        try {
            observacionesFacade.guardar(listaObservacionesBB.get(idFormulario).getMapaSecciones().get(seccion));
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e, e);
        } catch (RuntimeException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e, e);
        }

        adicionado = false;
    }


    public void eliminar(String idFormulario, String seccion, ObservacionesFormulariosRSQ observacionesFormularios) {
        ObservacionesFormulariosRSQ obj = listaObservacionesBB.get(idFormulario).getMapaSecciones().get(seccion).get(observacionesFormularios.getIndice());
        if (obj.getId() != null) {
            obj.setEstado(false);
            guardar(idFormulario, seccion);
        }
        listaObservacionesBB.get(idFormulario).getMapaSecciones().get(seccion).remove(observacionesFormularios);
        reasignarIndice(listaObservacionesBB.get(idFormulario), seccion);

    }

    public ObservacionesRSQBean getObservacionesBB() {
        return getObservacionesBB("id");
    }

    public ObservacionesRSQBean getObservacionesBB(String idFormulario) {
        if (listaObservacionesBB.containsKey(idFormulario)) {
            return listaObservacionesBB.get(idFormulario);
        }
        return new ObservacionesRSQBean();
    }



    public Boolean validarObservaciones(Usuario usuario, Boolean estado, String seccion,String idComponete) {
        List<ObservacionesFormulariosRSQ> observaciones = this.getObservacionesBB(idComponete).getMapaSecciones().get(seccion);

        return validarObservaciones(usuario,  estado, seccion, observaciones );
    }



    public Boolean validarObservaciones(Usuario usuario, Boolean estado, String seccion) {
        List<ObservacionesFormulariosRSQ> observaciones = this.getObservacionesBB().getMapaSecciones().get(seccion);

        return validarObservaciones(usuario,  estado, seccion, observaciones );
    }

    public Boolean validarObservaciones(Usuario usuario, Boolean estado, String seccion,List<ObservacionesFormulariosRSQ> observaciones ) {

        List<ObservacionesFormulariosRSQ> observacionesPendientes = new ArrayList<>();
        if (estado) {
            for (ObservacionesFormulariosRSQ observacion : observaciones) {
                if (observacion.getUsuario().equals(usuario) && !observacion.isObservacionCorregida()) {
                    JsfUtil.addMessageError("Existen observaciones sin corregir. Por favor rectifique los datos.");
                    return false;
                }
                if (observacion.getId() == null) {
                    observacionesPendientes.add(observacion);
                }
            }
        } else {
            int posicion = 0;
            int cantidad = observaciones.size();
            Boolean encontrado = false;
            while (!encontrado && posicion < cantidad) {
                if (observaciones.get(posicion).getUsuario().equals(usuario)
                        && !observaciones.get(posicion).isObservacionCorregida()) {
                    encontrado = true;

                }
                if (observaciones.get(posicion).getId() == null) {
                    observacionesPendientes.add(observaciones.get(posicion));
                }
                posicion++;
            }
            if (!encontrado) {
                JsfUtil.addMessageError("No existen observaciones sin corregir. Por favor rectifique los datos.");
                return false;
            }
        }
        if (observacionesPendientes.size() > 0) {
            try {
                observacionesFacade.guardar(observacionesPendientes);
            } catch (ServiceException e) {
                LOG.error(e);
                JsfUtil.addMessageError("Ocurrió un error al validar la información.");
                return false;
            }
        }
        return true;
    }

    public Boolean esNuevoComentario(ObservacionesFormulariosRSQ ob) {
        return ob.getId() == null || (ob.getIdTarea() != null && ob.getIdTarea() == bandejaTareasBean.getTarea().getTaskId());

    }

    
    /**
     * MarielaG
     * Busca las observaciones del informe y oficio (aprobacion u observacion) actuales, mediante la fecha de registro del informe.
     * @param idFormulario
     * @param nombreClase
     * @param idClase
     * @param secciones
     * @param fechaRegistro
     * @return
     */
    public String cargarDatosInicialesHistorial(String idFormulario, String nombreClase, String idClase, String secciones, Date fechaRegistro) {
        if (!adicionado) {
            int id = idClase != null && !idClase.isEmpty()? Integer.parseInt(idClase):0;
            cargarObservacionesHistorial(idFormulario, nombreClase,id,fechaRegistro, secciones);
            cargarObservacionesHistorialAnterior(idFormulario, nombreClase, id, fechaRegistro, secciones);
        }
//        if(historico){
//        	return "label-" + obtenerIdSeccionHistorico(idFormulario);
//        }else{ 
        	return "label-" + obtenerIdSeccion(idFormulario);
//        }
    }

    /**
     * MarielaG
     * Busca las observaciones del informe y oficio (aprobacion u observacion) actuales, mediante la fecha de registro del informe.
     * @param idFormulario
     * @param nombreClase
     * @param idClase
     * @param fechaRegistro
     * @param secciones
     */
    public void cargarObservacionesHistorial(String idFormulario, String nombreClase, Integer idClase, Date fechaRegistro, String... secciones) {
        try {
            ObservacionesRSQBean observacion = new ObservacionesRSQBean();
            observacion.setNombreClase(nombreClase);
            observacion.setIdClase(idClase);
            observacion.setSeccion(secciones);

           
			observacion.setListaObservaciones(observacionesFacade.listarHistorialPorIdClaseNombreClase(idClase, nombreClase,fechaRegistro, secciones));
			if (observacion.getListaObservaciones() != null
					&& !observacion.getListaObservaciones().isEmpty()) {
				for (ObservacionesFormulariosRSQ ob : observacion
						.getListaObservaciones()) {
					if (ob.getIdUsuario().equals(
							JsfUtil.getLoggedUser().getId())) {
						ob.setDisabled(false);
					} else
						ob.setDisabled(false);
				}
			}
                       
            
            for (ObservacionesFormulariosRSQ ob : observacion.getListaObservaciones()) {
                List<ObservacionesFormulariosRSQ> elementos = new ArrayList<>();
                String seccion = ob.getSeccionFormulario();
                if (observacion.getMapaSecciones().containsKey(seccion)) {
                    elementos = observacion.getMapaSecciones().get(ob.getSeccionFormulario());
                }
                elementos.add(ob);
                observacion.getMapaSecciones().put(seccion, elementos);
            }
            for (String seccion : secciones) {
                if (!observacion.getMapaSecciones().containsKey(seccion) && !seccion.equals("*")) {
                    observacion.getMapaSecciones().put(seccion, new ArrayList<ObservacionesFormulariosRSQ>());
                }
            }
            for (String s : observacion.getMapaSecciones().keySet()) {
                reasignarIndice(observacion, s);
            }
           
            listaObservacionesBB.put(idFormulario, observacion);
            
        } catch (ServiceException e) {
            LOG.error(e, e);
        } catch (RuntimeException e) {
            LOG.error(e, e);
        }
    }

    /**
     * Cris F: Para historico
     * Busca las observaciones del informe y oficio (aprobacion u observacion) actuales, mediante la fecha de registro del informe.
     * @param idFormulario
     * @param nombreClase
     * @param idClase
     * @param fechaRegistro
     * @param secciones
     */
    public void cargarObservacionesHistorialAnterior(String idFormulario, String nombreClase, Integer idClase, Date fechaRegistro, String... secciones) {
        try {
            ObservacionesRSQBean observacion = new ObservacionesRSQBean();
            observacion.setNombreClase(nombreClase);
            observacion.setIdClase(idClase);
            observacion.setSeccion(secciones);

			observacion.setListaObservaciones(observacionesFacade.listarHistorialPorIdClaseNombreClaseHistorico(idClase,nombreClase, fechaRegistro, secciones));
			if (observacion.getListaObservaciones() != null	&& !observacion.getListaObservaciones().isEmpty()) {
				for (ObservacionesFormulariosRSQ ob : observacion.getListaObservaciones()) {
					if (ob.getIdUsuario().equals(JsfUtil.getLoggedUser().getId())) {
						ob.setDisabled(false);
					} else
						ob.setDisabled(false);
				}
			}    	             
            
            for (ObservacionesFormulariosRSQ ob : observacion.getListaObservaciones()) {
                List<ObservacionesFormulariosRSQ> elementos = new ArrayList<>();
                String seccion = ob.getSeccionFormulario();
                if (observacion.getMapaSecciones().containsKey(seccion)) {
                    elementos = observacion.getMapaSecciones().get(ob.getSeccionFormulario());
                }
                elementos.add(ob);
                observacion.getMapaSecciones().put(seccion, elementos);
            }
            for (String seccion : secciones) {
                if (!observacion.getMapaSecciones().containsKey(seccion) && !seccion.equals("*")) {
                    observacion.getMapaSecciones().put(seccion, new ArrayList<ObservacionesFormulariosRSQ>());
                }
            }
            for (String s : observacion.getMapaSecciones().keySet()) {
                reasignarIndice(observacion, s);
            }

            listaObservacionesH.put(idFormulario, observacion);

        } catch (ServiceException e) {
            LOG.error(e, e);
        } catch (RuntimeException e) {
            LOG.error(e, e);
        }
    }
    
    
    /**
     * Cris F: Para el historico
     * @param idFormulario
     * @return
     */
    public String obtenerIdSeccionHistorico(String idFormulario) {
        ObservacionesRSQBean observacion = listaObservacionesH.get(idFormulario);
        String sec = "";
        for (String s : observacion.getSeccion()) {
            sec += "-" + s;
        }
        sec = sec.replace("/", "_");
        sec = sec.replace(" ", "_");
        sec = sec.replace(".", "_");
        sec = sec.replace("*", "full");
        return "pnlObservaciones-" + observacion.getNombreClase() + observacion.getIdClase().toString() + sec;
    }


}
