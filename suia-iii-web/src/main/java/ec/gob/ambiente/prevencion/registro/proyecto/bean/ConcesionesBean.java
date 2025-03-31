package ec.gob.ambiente.prevencion.registro.proyecto.bean;

import index.Concesion_resultado;
import index.Concesiones_entrada;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.ConcesionMineraUbicacionGeografica;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ConcesionesBean implements Serializable {

	private static final long serialVersionUID = 1056800750828406979L;

	@Setter
	@ManagedProperty(value = "#{adicionarUbicaciones1Bean}")
	protected AdicionarUbicaciones1Bean adicionarUbicaciones1Bean;

	@Getter
	@Setter
	private List<ConcesionMinera> concesionesMineras;

	private ConcesionMinera concesionMinera;
	
	@Getter
	@Setter	
	private boolean habilitarConcesion;
	
	@Getter
	@Setter	
	private boolean habilitartextinput;
	
	@Getter
	@Setter	
	private boolean editarConcesionesColindantes;
	

	private boolean editarConcesionesPanel;

	@PostConstruct
	public void init() {
        concesionesMineras = new ArrayList<ConcesionMinera>();
        habilitarConcesion=true;
        habilitartextinput = false;
        editarConcesionesColindantes=false;
        editarConcesionesPanel=false;
    }

	public void validateConcesion(FacesContext context, UIComponent validate, Object value) {
		if (adicionarUbicaciones1Bean.getUbicacionesSeleccionadas().isEmpty()) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"No se han definido ubicaciones geográficas para esta concesión minera.", null));
		}
	}

	public void aceptar() {
        concesionMinera.setConcesionesUbicacionesGeograficas(new ArrayList<ConcesionMineraUbicacionGeografica>());
        Iterator<UbicacionesGeografica> iteratorUG = adicionarUbicaciones1Bean.getUbicacionesSeleccionadas().iterator();
        while (iteratorUG.hasNext()) {
            UbicacionesGeografica ubicacionesGeografica = iteratorUG.next();
            ConcesionMineraUbicacionGeografica concesionMineraUbicacionGeografica = new ConcesionMineraUbicacionGeografica();
            concesionMineraUbicacionGeografica.setConcesionMinera(concesionMinera);
            concesionMineraUbicacionGeografica.setUbicacionesGeografica(ubicacionesGeografica);
            concesionMinera.getConcesionesUbicacionesGeograficas().add(concesionMineraUbicacionGeografica);
        }
        
        if(editarConcesionesColindantes)
        {
        	if((JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.03.05")
        			|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.04.03")
        			|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.05.03")
        			|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.02.03"))){
        		JsfUtil.getBean(RegistroProyectoBean.class).setListaCoordinatesWrapper(new ArrayList<CoordinatesWrapper>());
        		
        		if(!cargarConcesionesColindantes())
        			return;
        		
        	}
        	else
        		concesionesMineras.add(concesionMinera);
            

            if(concesionesMineras.size()>4 && (JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.03.05")
                    || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.04.03")
                    || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.05.03")
                    || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.02.03"))){
                habilitarConcesion=false;                
            }
        	
        }
        else
        {
        
        	if (habilitarConcesion && !concesionesMineras.contains(concesionMinera)){

        		if((JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.03.05")
        				|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.04.03")
        				|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.05.03")
        				|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.02.03"))){
        			JsfUtil.getBean(RegistroProyectoBean.class).setListaCoordinatesWrapper(new ArrayList<CoordinatesWrapper>());

        			if(!cargarConcesionesColindantes())
            			return;
        		}
        		else
        			concesionesMineras.add(concesionMinera);


        		if(concesionesMineras.size()>4 && (JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.03.05")
        				|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.04.03")
        				|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.05.03")
        				|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.02.03"))){
        			habilitarConcesion=false;                
        		}
        	}else {

        		for (ConcesionMinera cons1 : concesionesMineras) {
        			if(cons1.getCodigo().equals(concesionMinera.getCodigo())){
        				concesionesMineras.remove(cons1);
        				concesionesMineras.add(concesionMinera);
        				break;
        			}
        		}
        	}
        }
        clear();
        JsfUtil.addCallbackParam("concesion");
    }

	public void editar(ConcesionMinera concesionMinera) {
		this.concesionMinera = concesionMinera;
		if((JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.03.05")
				|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.04.03")
				|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.05.03")
				|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.02.03"))){
			habilitartextinput = true;
			editarConcesionesPanel=true;
		}
		List<UbicacionesGeografica> ubicaciones = new ArrayList<UbicacionesGeografica>();
		for (ConcesionMineraUbicacionGeografica concesionMineraUbicacionGeografica : this.concesionMinera
				.getConcesionesUbicacionesGeograficas())
			ubicaciones.add(concesionMineraUbicacionGeografica.getUbicacionesGeografica());
		adicionarUbicaciones1Bean.setUbicacionesSeleccionadas(ubicaciones);
	}

	public void eliminar(ConcesionMinera concesionMinera) {
        concesionesMineras.remove(concesionMinera);
        if((JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.03.05")
        		|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.04.03")
        		|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.05.03")
        		|| JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.02.03"))){
        	JsfUtil.getBean(RegistroProyectoBean.class).limpiarListaCoordenadas();
        	if(concesionesMineras.size()>1)
        	{
        		for (ConcesionMinera cons1 : concesionesMineras) {
        			JsfUtil.getBean(RegistroProyectoBean.class).cargarConcesionesCoordenadas(cons1.getCodigo());
        		}
        	}
        }
        if(concesionesMineras.size()>4 && (JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.03.05")
                || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.04.03")
                || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.05.03")
                || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.02.03"))){
            habilitarConcesion=false;
        }
        else{
            habilitarConcesion=true;
        }
    }

	public void clear() {
		concesionMinera = null;
		adicionarUbicaciones1Bean.setUbicacionesSeleccionadas(new ArrayList<UbicacionesGeografica>());
		adicionarUbicaciones1Bean.resetSelections();
		habilitartextinput = false;
		editarConcesionesPanel=false;
		JsfUtil.getBean(RegistroProyectoBean.class).setHabilitartextinput(false);
	}

	public ConcesionMinera getConcesionMinera() {
		return concesionMinera == null ? concesionMinera = new ConcesionMinera() : concesionMinera;
	}
	
	public void editarConcesionesColindantes()
	{
		if((JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.03.05")
                || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.04.03")
                || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.05.03")
                || JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("21.02.02.03"))){
			if(concesionesMineras.size()>4){
	            habilitarConcesion=false;                
	        }
			else
				habilitarConcesion=true;
			
			editarConcesionesColindantes=true;
		}
		

		
	}
	public boolean cargarConcesionesColindantes()
	{
		boolean estado=true;
		if(concesionesMineras.size()==0)
		{
			concesionesMineras.add(concesionMinera);
		}
		else
		{        			
			int x =0;
			if(!editarConcesionesPanel)
			{
				for (ConcesionMinera cons : concesionesMineras) {
					if(cons.getCodigo().equals(concesionMinera.getCodigo()))
					{
						x=1;
						JsfUtil.addMessageError("Concesion ya registrada");
						//					concesionMinera= new ConcesionMinera();
						estado=false;
						break;
					}
				}
			}
			if(x==0)
			{
				if(!editarConcesionesPanel)
					concesionesMineras.add(concesionMinera);
				
				if(concesionesMineras.size()>1)
				{
					int y=1;
					Concesiones_entrada con = new Concesiones_entrada();
					SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
					Concesion_resultado[] salida = null;
					con.setU("1712876364");
					con.setConcesion1("");
					con.setConcesion2("");
					con.setConcesion3("");
					con.setConcesion4("");
					con.setConcesion5("");
					for (ConcesionMinera cons1 : concesionesMineras) {
						if(y==1)
							con.setConcesion1(JsfUtil.getBean(RegistroProyectoBean.class).cargarColindantes(cons1.getCodigo()));
						if(y==2)
							con.setConcesion2(JsfUtil.getBean(RegistroProyectoBean.class).cargarColindantes(cons1.getCodigo()));
						if(y==3)
							con.setConcesion3(JsfUtil.getBean(RegistroProyectoBean.class).cargarColindantes(cons1.getCodigo()));
						if(y==4)
							con.setConcesion4(JsfUtil.getBean(RegistroProyectoBean.class).cargarColindantes(cons1.getCodigo()));
						if(y==5)
							con.setConcesion5(JsfUtil.getBean(RegistroProyectoBean.class).cargarColindantes(cons1.getCodigo()));
						y++;
					}
					try {
						salida=ws.colindante(con);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(salida[0].getConcesion().getValor().equals("t"))
					{
//						concesionesMineras.add(concesionMinera);
						for (ConcesionMinera cons1 : concesionesMineras) {
							JsfUtil.getBean(RegistroProyectoBean.class).cargarConcesionesCoordenadas(cons1.getCodigo());
						}
					}
					else
					{
						concesionesMineras.remove(y-2);        						
						JsfUtil.addMessageError("Las concesiones mineras especificadas no son contiguas, por favor realizar de forma independiente");
						estado=false;						
					}
				}
			}
		}
		return estado;
		
	}
}