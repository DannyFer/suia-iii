package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.DefaultRequestContext;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProductoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProductoPqua;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ViewScoped
@ManagedBean
public class ValidarInicioProcesoPquaController implements Serializable{

	private static final long serialVersionUID = -9167263742346192020L;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@EJB
	private ProductoPquaFacade productoPquaFacade;
	
	@PostConstruct
	public void init(){
		
	}
	
	public void validarOperador(){
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			
			int month = cal.get(Calendar.MONTH) + 1;
			int year = cal.get(Calendar.YEAR);
			
			String sDate1 = "01/" + month + "/" + year;
			Date fechaMesIngreso = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
			
			List<ProductoPqua> listaProductosReporte = productoPquaFacade.listaProductosPorUsuarioFecha(JsfUtil.getLoggedUser().getNombre(), fechaMesIngreso);
			
			if(listaProductosReporte.size() > 0) {
				bandejaTareasBean.setTarea(null);
				bandejaTareasBean.setProcessId(0);
				
				JsfUtil.redirectTo("/pages/rcoa/plaguicidasQuimicosUsoAgricola/ingresarInformacionProducto.jsf");
			} else {
				DefaultRequestContext.getCurrentInstance().execute("PF('mensajePQUA').show();");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	
	

}
