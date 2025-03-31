package ec.gob.ambiente.suia.reportes.powerBi;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.UrlBiReportes;
import ec.gob.ambiente.suia.reportes.facade.UrlBiReportesFacade;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class UrlBiReportesController {

	@EJB
	private UrlBiReportesFacade urlBiFacade;

    @Getter
    @Setter
    private String ulrBiReporte, nombreReporte;
    
    private Integer idReporte, idTipoReporte, idArea;

    @PostConstruct
    public void init() {
    	ulrBiReporte="";
    	nombreReporte="";
    	idReporte = 3; // muestra el reporte sin filtros
    	idArea=0;
		
    	HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	if (request.getParameter("tipoR") != null){
    		idTipoReporte = Integer.valueOf(request.getParameter("tipoR"));
    		UrlBiReportes objUrl = urlBiFacade.findByIdByTipoReporte(idReporte, idTipoReporte, idArea);
        	if(objUrl != null && objUrl.getId() != null){
        		ulrBiReporte = objUrl.getUrl();
        		nombreReporte = objUrl.getDescripcion();
        	}
    	}
    }

}