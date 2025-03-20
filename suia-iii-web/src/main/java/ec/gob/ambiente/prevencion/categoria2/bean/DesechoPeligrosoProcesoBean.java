/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.categoria2.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Setter;

import org.primefaces.event.NodeSelectEvent;

import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;

@ManagedBean
@ViewScoped
public class DesechoPeligrosoProcesoBean extends AdicionarDesechosPeligrososBean {

	private static final long serialVersionUID = -5165125547435526426L;

	@Setter
	private DesechoPeligroso desechoSeleccionado;

	public DesechoPeligroso optenerDesechoSeleccionado() {
		if (desechoSeleccionado == null)
			desechoSeleccionado = new DesechoPeligroso();
		return desechoSeleccionado;
	}

	public void onNodeSelect(NodeSelectEvent event) {
		DesechoPeligroso desecho = (DesechoPeligroso) event.getTreeNode().getData();
		if (!getDesechosSeleccionados().contains(desecho)) {
			getDesechosSeleccionados().add(desecho);
			desechoSeleccionado = desecho;
			if (completeOperationOnAdd != null)
				completeOperationOnAdd.endOperation(desecho);
		}
	}
}
