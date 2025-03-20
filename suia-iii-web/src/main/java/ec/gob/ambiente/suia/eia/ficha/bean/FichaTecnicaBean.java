package ec.gob.ambiente.suia.eia.ficha.bean;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.coordenadageneral.facade.CoordenadaGeneralFacade;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.FichaTecnica;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.fichatecnica.facade.FichaTecnicaFacade;

@ManagedBean
@ViewScoped
public class FichaTecnicaBean  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3309103113578164984L;
	@EJB
	private FichaTecnicaFacade fichaTecnicaFacade;
	@EJB
	private CoordenadaGeneralFacade coordenadaGeneralFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@Getter
	@Setter
	private List<CoordenadaGeneral> coordenadas;
	@Getter
	@Setter
	private FichaTecnica fichaTecnica;
	@Getter
	@Setter
	private UploadedFile carta;
	@Getter
	@Setter
	private Documento documento;
	@Getter
	@Setter
	private List<TipoForma> tipoFormas;
	
	@Getter
	@Setter
	private TipoForma tipoForma;
	
	@Getter
	@Setter
	private CoordenadaGeneral coordenadaGeneral;
	@Getter
	@Setter
	private Integer id;
	@Getter
	@Setter
	private List<CoordenadaGeneral> coordenadasBorradas;
	@Getter
	@Setter
	private StreamedContent file;
	@Getter
	@Setter
	private boolean visibleDocumento;
	
	@Getter
	@Setter
	private DocumentosTareasProceso documentoTarea;
	@Getter
	@Setter
	private Integer eiaId;
	
//	@PostConstruct
//	public void iniciar(){
//		try{
//			
//			EstudioImpactoAmbiental eia = (EstudioImpactoAmbiental)JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
//	        setEiaId(eia.getId());
//	        if(!fichaTecnicaFacade.fichaTecnicaXEiaId(getEiaId()).isEmpty())
//	        	{
//	        	
//	        	setFichaTecnica(fichaTecnicaFacade.fichaTecnicaXEiaId(getEiaId()).get(0));
//		        getFichaTecnica().setEditar(true);
//		        setCoordenadas(coordenadaGeneralFacade.coordenadasGeneralXTablaId(getFichaTecnica().getId(), FichaTecnica.class.getSimpleName()));
//				setDocumento(documentosFacade.documentoXTablaId(getFichaTecnica().getId(), FichaTecnica.class.getSimpleName()).get(0));
//				getDocumento().setEditar(true);
//				getFichaTecnica().setNombreArchivo(getDocumento().getNombre());
//				getFichaTecnica().setEiaId(getEiaId());
//				setVisibleDocumento(true);
//	        }else{
//	        	fichaTecnica= new FichaTecnica();
//	        	fichaTecnica.setEiaId(getEiaId());
//	    		coordenadas= new ArrayList<CoordenadaGeneral>();
//	    		documento= new Documento();
//	    		setVisibleDocumento(false);
//	    		
//	        }
//	        coordenadasBorradas= new ArrayList<CoordenadaGeneral>();
//			}catch(Exception e){
//				e.getMessage();
//			}
//		
//	
//	}
	
}
