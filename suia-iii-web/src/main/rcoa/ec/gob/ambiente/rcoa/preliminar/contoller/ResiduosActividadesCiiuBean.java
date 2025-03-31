package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.NoneScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaCiiuResiduosFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaCiiuResiduos;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.desechoPeligroso.facade.DesechoPeligrosoFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FuenteDesechoPeligroso;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@NoneScoped
public class ResiduosActividadesCiiuBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private DesechoPeligrosoFacade desechoPeligrosoFacade;
	@EJB
	private ProyectoLicenciaCoaCiiuResiduosFacade proyectoLicenciaCoaCiiuResiduosFacade;
	
	@Getter
	@Setter
	private List<ProyectoLicenciaCoaCiiuResiduos> listaCiiuResiduos1 = new ArrayList<ProyectoLicenciaCoaCiiuResiduos>();
	@Getter
	@Setter
	private List<ProyectoLicenciaCoaCiiuResiduos> listaCiiuResiduos2 = new ArrayList<ProyectoLicenciaCoaCiiuResiduos>();
	@Getter
	@Setter
	private List<ProyectoLicenciaCoaCiiuResiduos> listaCiiuResiduos3 = new ArrayList<ProyectoLicenciaCoaCiiuResiduos>();
	@Getter
	@Setter
	private List<ProyectoLicenciaCoaCiiuResiduos> listaCatalogoResiduosDesechosNoPeligrosos,  listaResiduosDesechosNoPeligrosos, listaResiduosDesechosNoPeligrososEliminar;
	
	@Getter
	@Setter
	private Integer numeroActividad;

	public void buscarCatalogoResiduosActividades() {
		try {
			listaResiduosDesechosNoPeligrososEliminar = new ArrayList<>();
	
			FuenteDesechoPeligroso fuentePeligrosos = new FuenteDesechoPeligroso();
			fuentePeligrosos.setId(49);
	
			FuenteDesechoPeligroso fuenteEspeciales = new FuenteDesechoPeligroso();
			fuenteEspeciales.setId(50);
	
			List<DesechoPeligroso> listaDesechos = new ArrayList<>();
			listaDesechos.addAll(desechoPeligrosoFacade.buscarDesechosPeligrosoPorFuente(fuentePeligrosos)); 
			listaDesechos.addAll(desechoPeligrosoFacade.buscarDesechosPeligrosoPorFuente(fuenteEspeciales));
	
			listaCatalogoResiduosDesechosNoPeligrosos = new ArrayList<>();
			for(DesechoPeligroso desecho : listaDesechos) {
				ProyectoLicenciaCoaCiiuResiduos ciiuResiduo = new ProyectoLicenciaCoaCiiuResiduos();
				ciiuResiduo.setDesechoPeligroso(desecho);
	
				listaCatalogoResiduosDesechosNoPeligrosos.add(ciiuResiduo);
			}
		} catch (Exception e) {
			e.printStackTrace();
        }
	}
	
	public void buscarResiduosActividades(ProyectoLicenciaCuaCiuu actividad, Integer nroActividad){
		try {
			List<ProyectoLicenciaCoaCiiuResiduos> listaResiduosActividad = proyectoLicenciaCoaCiiuResiduosFacade.cargarResiduosPorActividad(actividad.getId());
			for(ProyectoLicenciaCoaCiiuResiduos residuo : listaResiduosActividad) {
				if(residuo.getDesechoPeligroso() == null) {
					DesechoPeligroso desecho = new DesechoPeligroso();
					desecho.setClave(residuo.getClave());
					desecho.setDescripcion(residuo.getDescripcion());
	
					residuo.setDesechoPeligroso(desecho);
				}
			}
	
			switch (nroActividad) {
			case 1:
				listaCiiuResiduos1 = listaResiduosActividad;
				break;
			case 2:
				listaCiiuResiduos2 = listaResiduosActividad;
				break;
			case 3:
				listaCiiuResiduos3 = listaResiduosActividad;
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
        }
	}

	public void agregarResiduosActividad(Integer nroActividad) {
		try {
			numeroActividad = nroActividad;
			listaResiduosDesechosNoPeligrosos = new ArrayList<>();
			for(ProyectoLicenciaCoaCiiuResiduos item : listaCatalogoResiduosDesechosNoPeligrosos) {
				ProyectoLicenciaCoaCiiuResiduos residuo = (ProyectoLicenciaCoaCiiuResiduos) SerializationUtils.clone(item);
				listaResiduosDesechosNoPeligrosos.add(residuo);
				//listaResiduosDesechosNoPeligrosos.addAll(listaCatalogoResiduosDesechosNoPeligrosos);
			}

			List<ProyectoLicenciaCoaCiiuResiduos> listaCiiuResiduos  = new ArrayList<>();
			switch (nroActividad) {
			case 1:
				listaCiiuResiduos = listaCiiuResiduos1;
				break;
			case 2:
				listaCiiuResiduos = listaCiiuResiduos2;
				break;
			case 3:
				listaCiiuResiduos = listaCiiuResiduos3;
				break;
			default:
				break;
			}
	
			if(listaCiiuResiduos.size() > 0) {
				for(ProyectoLicenciaCoaCiiuResiduos residuoSeleccionado : listaCiiuResiduos) {
					if(residuoSeleccionado.getDesechoPeligroso() != null && residuoSeleccionado.getDesechoPeligroso().getId() != null) {
						for (int i = 0; i < listaResiduosDesechosNoPeligrosos.size(); i++) {
							ProyectoLicenciaCoaCiiuResiduos residuo = listaResiduosDesechosNoPeligrosos.get(i);
							if(residuoSeleccionado.getDesechoPeligroso().getId().equals(residuo.getDesechoPeligroso().getId())) {
								residuo = residuoSeleccionado;
								residuo.setResiduoSeleccionado(true);
	
								listaResiduosDesechosNoPeligrosos.remove(i);
								listaResiduosDesechosNoPeligrosos.add(i, residuo);
								break;
							}
						}
						
					} else if(residuoSeleccionado.getDesechoPeligroso() != null && residuoSeleccionado.getDesechoPeligroso().getId() == null) {
						residuoSeleccionado.setResiduoSeleccionado(true);
						listaResiduosDesechosNoPeligrosos.add(residuoSeleccionado);
					}
				}
			}
	
			RequestContext.getCurrentInstance().execute("PF('tblAgregarResiduos').paginator.setPage(0);");
		} catch (Exception e) {
			e.printStackTrace();
        }
	}

	public void agregarOtroResiduo() {
		DesechoPeligroso desecho = new DesechoPeligroso();
		desecho.setClave("NA");

		ProyectoLicenciaCoaCiiuResiduos ciiuResiduo = new ProyectoLicenciaCoaCiiuResiduos();
		ciiuResiduo.setDesechoPeligroso(desecho);
		ciiuResiduo.setResiduoSeleccionado(true);

		listaResiduosDesechosNoPeligrosos.add(ciiuResiduo);

		RequestContext.getCurrentInstance().execute("PF('tblAgregarResiduos').paginator.setPage(PF('tblAgregarResiduos').paginator.cfg.pageCount - 1);");
	}

	public void marcarResiduo(ProyectoLicenciaCoaCiiuResiduos item) {
		try{
			if(!item.getResiduoSeleccionado()) {
				if(item.getId() != null) {
					item.setEstado(false);
					if(item.getDesechoPeligroso() != null && item.getDesechoPeligroso().getId() == null)
						item.setDesechoPeligroso(null);
					listaResiduosDesechosNoPeligrososEliminar.add(item);
				}
	
				DesechoPeligroso desecho = item.getDesechoPeligroso();
	
				for (int i = 0; i < listaResiduosDesechosNoPeligrosos.size(); i++) {
					ProyectoLicenciaCoaCiiuResiduos residuo = listaResiduosDesechosNoPeligrosos.get(i);
					if(item.getDesechoPeligroso() != null && item.getDesechoPeligroso().getId() != null) {
						if (item.getDesechoPeligroso().getId().equals(residuo.getDesechoPeligroso().getId())) {
	
							listaResiduosDesechosNoPeligrosos.remove(i);
	
							item = new ProyectoLicenciaCoaCiiuResiduos();
	
							if (desecho != null && desecho.getId() != null)
								item.setDesechoPeligroso(desecho);
	
							listaResiduosDesechosNoPeligrosos.add(i, item);
							break;
						}
					} else {
						listaResiduosDesechosNoPeligrosos.remove(item);
						break;
					}
				}
	
				RequestContext.getCurrentInstance().update("frmResiduo:tblAgregarResiduos");
			}
		} catch (Exception e) {
			e.printStackTrace();
        }
	}

	public void eliminarResiduoActividad(ProyectoLicenciaCoaCiiuResiduos item, Integer nroActividad) {
		if(item.getId() != null) {
			item.setEstado(false);
			if(item.getDesechoPeligroso() != null && item.getDesechoPeligroso().getId() == null)
					item.setDesechoPeligroso(null);
			listaResiduosDesechosNoPeligrososEliminar.add(item);
		}

		switch (nroActividad) {
		case 1:
			listaCiiuResiduos1.remove(item);
			break;
		case 2:
			listaCiiuResiduos2.remove(item);
			break;
		case 3:
			listaCiiuResiduos3.remove(item);
			break;
		default:
			break;
		}
	}

	public void aceptarResiduosActividad() {
		try {
			
			List<ProyectoLicenciaCoaCiiuResiduos> listaAux = new ArrayList<>();
			Boolean datosValidos = true;
			for(ProyectoLicenciaCoaCiiuResiduos residuo : listaResiduosDesechosNoPeligrosos) {
				if(residuo.getResiduoSeleccionado() != null && residuo.getResiduoSeleccionado()) {
					if(residuo.getDesechoPeligroso() != null && residuo.getDesechoPeligroso().getId() == null) {
						//si son otros residuos ingresados por el operador
						if(residuo.getDesechoPeligroso().getDescripcion() != null && residuo.getCantidadAnual() != null)
							listaAux.add(residuo);
						else {
							datosValidos = false;
	
							if(residuo.getDesechoPeligroso().getDescripcion() == null)
								JsfUtil.addMessageError("El nombre del residuo o desecho es requerido.");
	
							if(residuo.getCantidadAnual() != null)
								JsfUtil.addMessageError("La cantidad del residuo " + residuo.getDesechoPeligroso().getDescripcion() + " es requerida.");
						}
					} else {
						if(residuo.getCantidadAnual() != null)
							listaAux.add(residuo);
						else {
							JsfUtil.addMessageError("La cantidad del residuo " + residuo.getDesechoPeligroso().getDescripcion() + " es requerida.");
							datosValidos = false;
						}
					}
				}
			}
	
			if(!datosValidos)
				return;

			switch (numeroActividad) {
			case 1:
				listaCiiuResiduos1 = new ArrayList<>();
				listaCiiuResiduos1 = listaAux;
				RequestContext.getCurrentInstance().update("frmPreliminar:pnlGridPrincipalA1:1:tblResiduosSeleccionados1");
				RequestContext.getCurrentInstance().update("frmPreliminar:idRequiereTablaResiduo1");
				break;
			case 2:
				listaCiiuResiduos2 = new ArrayList<>();
				listaCiiuResiduos2 = listaAux;
				RequestContext.getCurrentInstance().update("frmPreliminar:pnlGridPrincipalA2:1:tblResiduosSeleccionados2");
				RequestContext.getCurrentInstance().update("frmPreliminar:idRequiereTablaResiduo2");
				break;
			case 3:
				listaCiiuResiduos3 = new ArrayList<>();
				listaCiiuResiduos3 = listaAux;
				RequestContext.getCurrentInstance().update("frmPreliminar:pnlGridPrincipalA3:1:tblResiduosSeleccionados3");
				RequestContext.getCurrentInstance().update("frmPreliminar:idRequiereTablaResiduo3");
				break;
			default:
				break;
			}
	
			listaResiduosDesechosNoPeligrosos = new ArrayList<>();
	
			JsfUtil.addCallbackParam("addResiduosActifvidad");

		} catch (Exception e) {
			e.printStackTrace();
        }
	}

	public void guardarInfoResiduosActividad( ProyectoLicenciaCuaCiuu ciiu, Integer nroActividad) {
		try {
			List<ProyectoLicenciaCoaCiiuResiduos> listaCiiuResiduos = new ArrayList<>();
			switch (nroActividad) {
			case 1:
				listaCiiuResiduos = listaCiiuResiduos1;
				break;
			case 2:
				listaCiiuResiduos = listaCiiuResiduos2;
				break;
			case 3:
				listaCiiuResiduos = listaCiiuResiduos3;
				break;
			default:
				break;
			}
			
			if (listaCiiuResiduos.size() > 0) {
				for (ProyectoLicenciaCoaCiiuResiduos residuo : listaCiiuResiduos) {
					residuo.setProyectoLicenciaCuaCiuu(ciiu);
					if (residuo.getDesechoPeligroso() != null && residuo.getDesechoPeligroso().getId() == null) {
						residuo.setClave(residuo.getDesechoPeligroso().getClave());
						residuo.setDescripcion(residuo.getDesechoPeligroso().getDescripcion());
						residuo.setDesechoPeligroso(null);
					}
	
					proyectoLicenciaCoaCiiuResiduosFacade.guardar(residuo);
				}
			}
			
			if(listaResiduosDesechosNoPeligrososEliminar.size() > 0) {
				proyectoLicenciaCoaCiiuResiduosFacade.eliminar(listaResiduosDesechosNoPeligrososEliminar);
				
				listaResiduosDesechosNoPeligrososEliminar = new ArrayList<>();
			}
			
			buscarResiduosActividades(ciiu, nroActividad);
		} catch (Exception e) {
			e.printStackTrace();
        }
	}
}
