package ec.gob.ambiente.prevencion.categoria2.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.prevencion.categoria2.bean.DescripcionProcesoPmaBean;
import ec.gob.ambiente.suia.domain.CatalogoInstalacion;
import ec.gob.ambiente.suia.domain.CatalogoTecnica;
import ec.gob.ambiente.suia.domain.Instalacion;
import ec.gob.ambiente.suia.domain.TecnicaProcesoPma;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class DescripcionProcesoPmaController implements Serializable {
    private static final long serialVersionUID = -357796798891L;

    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{descripcionProcesoPmaBean}")
    private DescripcionProcesoPmaBean descripcionProcesoPmaBean;

    public void guardarDatos() {
        if (descripcionProcesoPmaBean.getFichaAmbiental() != null) {
            Boolean guardadoTodo = true;
            RequestContext context = RequestContext.getCurrentInstance();

            //Guardar Instalaciones
            if (descripcionProcesoPmaBean.getAplicaInstalaciones()) {
                List<Instalacion> instalacionesProceso = new ArrayList<Instalacion>();

                for (CatalogoInstalacion catalogo : descripcionProcesoPmaBean.getInstalacionesCatalogo()) {
                    if (catalogo.isSeleccionado()) {
                        Instalacion objInstalacion = new Instalacion();
                        objInstalacion.setCatalogoInstalacion(catalogo);
                        objInstalacion.setFichaAmbientalPma(descripcionProcesoPmaBean.getFichaAmbiental());
                        instalacionesProceso.add(objInstalacion);
                    }
                }
                
                if (!instalacionesProceso.isEmpty()) {
                	fichaAmbientalPmaFacade.guardarInstalacionesProcesoHistorico(descripcionProcesoPmaBean.getFichaAmbiental().getId(), instalacionesProceso);
                }
                else{
                    guardadoTodo = false;
                    JsfUtil.addMessageError("Debe seleccionar al menos una Instalación.");
                    context.scrollTo("formTecnicasInstalaciones:pnl_instalaciones_proceso");
                }
            }

            // Guardar Técnicas
            if (descripcionProcesoPmaBean.getAplicaTecnicas()) {
                List<TecnicaProcesoPma> tecnicassProceso = new ArrayList<TecnicaProcesoPma>();

                for (CatalogoTecnica catalogo : descripcionProcesoPmaBean.getTecnicasCatalogo()) {
                    if (catalogo.isSeleccionado()) {
                        TecnicaProcesoPma objTecnica = new TecnicaProcesoPma();
                        objTecnica.setCatalogoTecnica(catalogo);
                        objTecnica.setFichaAmbientalPma(descripcionProcesoPmaBean.getFichaAmbiental());
                        tecnicassProceso.add(objTecnica);
                    }
                }
                if (!tecnicassProceso.isEmpty()) {
                	fichaAmbientalPmaFacade.guardarTecnicasProcesoHistorico(descripcionProcesoPmaBean.getFichaAmbiental().getId(), tecnicassProceso);                  
                }
                else{
                    guardadoTodo = false;
                    JsfUtil.addMessageError("Debe seleccionar al menos una Técnica.");
                    context.scrollTo("formTecnicasInstalaciones:pnl_tecnicas_proceso");
                }
            }

            //Guardar plaguicidas del proceso
            if (descripcionProcesoPmaBean.getAplicaPlaguicidas()) {
                if(!descripcionProcesoPmaBean.getFertilizantesProceso().isEmpty()) {
                	fichaAmbientalPmaFacade.guardarFertilizanteProcesoHistorico(descripcionProcesoPmaBean.getFichaAmbiental().getId(), descripcionProcesoPmaBean.getFertilizantesProceso());
                }
                else{
                    guardadoTodo = false;
                    JsfUtil.addMessageError("Debe registrar al menos un fertilizante.");
                    context.scrollTo("formFertilizantes:pnl_fertilizantes");
                }
                if(!descripcionProcesoPmaBean.getPlaguicidasProceso().isEmpty()){
                	fichaAmbientalPmaFacade.guardarPlaguicidasProcesoHistorico(descripcionProcesoPmaBean.getFichaAmbiental().getId(), descripcionProcesoPmaBean.getPlaguicidasProceso());
                }
                else{
                    guardadoTodo = false;
                    JsfUtil.addMessageError("Debe registrar al menos un plaguicida.");
                    context.scrollTo("formPlaguicidas:pnl_plaguicidas");
                }
            }
            //Guardar si aplica disposición final
            if (descripcionProcesoPmaBean.getAplicaDisposicionFinal()) {
                if (descripcionProcesoPmaBean.getIncluyeDesechosSanitarios()) {
                    if (descripcionProcesoPmaBean.getAplicaTransporteDesechosExantes()){
                        descripcionProcesoPmaBean.getDesechoSanitarioProceso().setOperacionesPrevias("");
                              descripcionProcesoPmaBean.getDesechoSanitarioProceso().setMetodoReduccion("");
                              descripcionProcesoPmaBean.getDesechoSanitarioProceso().setCapacidadAlmacenamiento(1); 
                    }else{
	                    if(descripcionProcesoPmaBean.getDesechoSanitarioProceso().getOperacionesPrevias() == null ||
	                            descripcionProcesoPmaBean.getDesechoSanitarioProceso().getMetodoReduccion() == null ||
	                            descripcionProcesoPmaBean.getDesechoSanitarioProceso().getCapacidadAlmacenamiento() == null) {
	                        guardadoTodo = false;
	                        JsfUtil.addMessageError("Debe registrar la información de desechos sanitarios.");
	                        context.scrollTo("formDsisposicionFinal:pnl_disposicion_final");
	                    }
                    }
//                    else{
//                        if(descripcionProcesoPmaBean.getDesechoSanitarioProceso().getOperacionesPrevias() != "" &&
//                                descripcionProcesoPmaBean.getDesechoSanitarioProceso().getMetodoReduccion() != "" &&
//                                descripcionProcesoPmaBean.getDesechoSanitarioProceso().getCapacidadAlmacenamiento() != 0) {
                            descripcionProcesoPmaBean.getDesechoSanitarioProceso().setFichaAmbientalPma(descripcionProcesoPmaBean.getFichaAmbiental());
                            
                            fichaAmbientalPmaFacade.guardarDesechoSanitarioProcesoHistorico(descripcionProcesoPmaBean.getDesechoSanitarioProceso());
//                        } else {
//                            guardadoTodo = false;
//                            JsfUtil.addMessageError("Debe registrar la información de desechos sanitarios.");
//                            context.scrollTo("formDsisposicionFinal:pnl_disposicion_final");
//                        }
//                    }
                } else {
                    descripcionProcesoPmaBean.getDesechoSanitarioProceso().setEstado(false);
                    descripcionProcesoPmaBean.getDesechoSanitarioProceso().setFichaAmbientalPma(descripcionProcesoPmaBean.getFichaAmbiental());
                    fichaAmbientalPmaFacade.guardarDesechoSanitarioProcesoHistorico(descripcionProcesoPmaBean.getDesechoSanitarioProceso());
                }
            }

            //Si aplica Transporte Desechos Expost guardar los vehículos
            if (descripcionProcesoPmaBean.getAplicaTransporteDesechosExpost()) {
                if(!descripcionProcesoPmaBean.getVehiculosDesechoSanitarioProceso().isEmpty()) {
                	fichaAmbientalPmaFacade.guardarVehiculosProcesoHistorico(descripcionProcesoPmaBean.getFichaAmbiental(), descripcionProcesoPmaBean.getVehiculosDesechoSanitarioProceso());
                }
                else{
                    guardadoTodo = false;
                    JsfUtil.addMessageError("Debe registrar al menos un vehículo de transporte de desecho sanitario.");
                    context.scrollTo("formTransporteDesechos:pnl_transporte_desechos");
                }
                if(!descripcionProcesoPmaBean.getDesechosPeligrosoProceso().isEmpty()) {
                	fichaAmbientalPmaFacade.guardarDesechosPeligrososProcesoHistorico(descripcionProcesoPmaBean.getFichaAmbiental().getId(), descripcionProcesoPmaBean.getDesechosPeligrosoProceso());
                }
                else{
                    guardadoTodo = false;
                    JsfUtil.addMessageError("Debe registrar al menos un desecho peligroso.");
                    context.scrollTo("form:pnl_desechos_peligrosos_expost");
                }
            }

            //Si aplica Transporte Desechos Exantes
            //if (!descripcionProcesoPmaBean.getAplicaTransporteDesechosExpost()) {
            if (descripcionProcesoPmaBean.getAplicaTransporteDesechosExantes()) {
                if (!descripcionProcesoPmaBean.getDesechosPeligrosoProceso().isEmpty()) {                	
                	fichaAmbientalPmaFacade.guardarDesechosPeligrososProcesoHistorico(descripcionProcesoPmaBean.getFichaAmbiental().getId(), descripcionProcesoPmaBean.getDesechosPeligrosoProceso());               
                }
                else{
                    guardadoTodo = false;
                    JsfUtil.addMessageError("Debe registrar al menos un desecho peligroso.");
                    context.scrollTo("form:pnl_desechos_peligrosos_exantes");
                }
            }

            //Guardar Herramientas del proceso
            if (descripcionProcesoPmaBean.getAplicaHerramientas()) {
                if (!descripcionProcesoPmaBean.getHerramientasProceso().isEmpty()){
                	fichaAmbientalPmaFacade.guardarHerramientaProcesoHistorico(descripcionProcesoPmaBean.getFichaAmbiental().getId(), descripcionProcesoPmaBean.getHerramientasProceso());
                }
                else {
                    guardadoTodo = false;
                    JsfUtil.addMessageError("Debe registrar al menos una herramienta.");
                    context.scrollTo("formHerramientas:pnl_equipos_herramientas");
                }
            }

            //Guardar Insumos del procesos
            if (descripcionProcesoPmaBean.getAplicaInsumos()) {
                if (!descripcionProcesoPmaBean.getInsumosProceso().isEmpty()){
                	fichaAmbientalPmaFacade.guardarInsumoProcesoHistorico(descripcionProcesoPmaBean.getFichaAmbiental().getId(), descripcionProcesoPmaBean.getInsumosProceso());
                }
                	else {
                    guardadoTodo = false;
                    JsfUtil.addMessageError("Debe registrar al menos un insumo.");
                    context.scrollTo("formIsumos:pnl_insumos_proceso");
                }
            }

            // Guargar Actividades del proceso
            if (!descripcionProcesoPmaBean.getActividadesProceso().isEmpty()) {
            	fichaAmbientalPmaFacade.guardarActividadesProcesoHistorico(descripcionProcesoPmaBean.getFichaAmbiental().getId(), descripcionProcesoPmaBean.getActividadesProceso());                
            } else {
                guardadoTodo = false;
                JsfUtil.addMessageError("Debe registrar al menos una actividad.");
                context.scrollTo("formActividadProceso:pnl_actividades_proceso");
            }

            if (guardadoTodo) {
                //Para especificar que los datos están completos y se pueda generar la documentación
                descripcionProcesoPmaBean.getFichaAmbiental().setValidarDescripcionProceso(true);
                fichaAmbientalPmaFacade.guardarSoloFicha(descripcionProcesoPmaBean.getFichaAmbiental());
                JsfUtil.addMessageInfo("La operación se realizó satisfactoriamente.");
            }
        }
        
        JsfUtil.redirectTo("/prevencion/categoria2/v2/fichaAmbiental/descripcionProceso.jsf");
    }
}