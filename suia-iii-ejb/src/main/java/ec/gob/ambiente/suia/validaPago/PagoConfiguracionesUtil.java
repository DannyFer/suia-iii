package ec.gob.ambiente.suia.validaPago;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.InventarioForestalPma;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.inventarioForestalPma.facade.InventarioForestalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.validaPago.bean.PagoConfiguracionesBean;



@Stateless
public class PagoConfiguracionesUtil {
	
	/**** pagos ****/
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;
	private Organizacion organizacion;

	private static final String IGUAL_MAS_PARTICIPACION = "EMPRESAS MIXTAS CUANDO SU CAPITAL SUSCRITO PERTENEZCA POR "
			+ "LO MENOS A LAS DOS TERCERAS PARTES A ENTIDADES DE DERECHO PÚBLICO.";

	// campo para obtener el codigo del proyecto
	public String codigoProyecto="";
	
	@EJB
    private FichaAmbientalPmaFacade fichaAmbientalFacade;
    private FichaAmbientalPma fichaAmbiental;
    @EJB
    private InventarioForestalPmaFacade inventarioForestalPmaFacade;
    private InventarioForestalPma inventarioForestal;
	
	// método para validar el pago
	public ProyectoLicenciamientoAmbiental validaPago(ProyectoLicenciamientoAmbiental proyectoP) {
		// obtención de valores desde bdd
		Float costoControlSeguimiento = 0f;
		costoControlSeguimiento = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class)
				.getPagoConfigValue("Valor por seguimiento de registro ambiental v2")); // 80.00
		
		try {
			List<FichaAmbientalPma> lista = new ArrayList<FichaAmbientalPma>();
			FichaAmbientalPma fichaAmbientalPma = new FichaAmbientalPma();
    		
			fichaAmbientalPma = fichaAmbientalPmaFacade.getFichaAmbientalPorCodigoProyecto(proyectoP.getCodigo());
			lista.add(fichaAmbientalPma);
			
			if (!lista.isEmpty()) {
				if (lista.size() > 0) {
					if (lista.get(0).getNumeroLicencia() == null) {
						organizacion = organizacionFacade.buscarPorPersona(proyectoP.getUsuario().getPersona(), proyectoP.getUsuario().getNombre());
						// validación si la persona está asociada a una
						// organización
						if (organizacion != null) {
							// pago de control y seguimiento: $80.00
							// OG:2, Gobiernos Autónomos:5, Empresas Públicas:7,
							// Empresas Mixtas: 8 Igual o más que el 70% de
							// participación del Estado
							if (organizacion.getTipoOrganizacion().getId() == 2
									|| organizacion.getTipoOrganizacion().getId() == 5
									|| organizacion.getTipoOrganizacion().getId() == 7
									|| organizacion.getTipoOrganizacion().getId() == 8) {
								
								// validación si hay participación del estado en empresa mixta
								if (organizacion.getParticipacionEstado() != null) {
									if (organizacion.getParticipacionEstado().equals(IGUAL_MAS_PARTICIPACION)) {
										if (proyectoP.getCatalogoCategoria().getTipoLicenciamiento().getId() == 2) {
											// costo quemado de $80.00
											proyectoP.getCatalogoCategoria().getTipoLicenciamiento()
												.setCosto(costoControlSeguimiento);
										}
									}
								} else {
									if (proyectoP.getCatalogoCategoria().getTipoLicenciamiento().getId() == 2) {
										// costo quemado de $80.00
										proyectoP.getCatalogoCategoria().getTipoLicenciamiento()
											.setCosto(costoControlSeguimiento);
									}
								}
							}
						}
					}
				}
			} else {
				organizacion = organizacionFacade.buscarPorPersona(proyectoLicencia.getUsuario().getPersona(), proyectoLicencia.getUsuario().getNombre());
				// validación si la persona está asociada a una organización
				if (organizacion != null) {
					// pago de control y seguimiento: $80.00
					// OG:2, Gobiernos Autónomos:5, Empresas Públicas:7,
					// Empresas Mixtas: 8 Igual o más que el 70% de
					// participación del Estado
					if (organizacion.getTipoOrganizacion().getId() == 2
							|| organizacion.getTipoOrganizacion().getId() == 5
							|| organizacion.getTipoOrganizacion().getId() == 7
							|| organizacion.getTipoOrganizacion().getId() == 8) {
						
						// validación si hay participación del estado en empresa mixta
						if (organizacion.getParticipacionEstado() != null) {
							if (organizacion.getParticipacionEstado().equals(
									IGUAL_MAS_PARTICIPACION)) {
								if (proyectoP.getCatalogoCategoria().getTipoLicenciamiento().getId() == 2) {
									// costo quemado de $80.00
									proyectoP.getCatalogoCategoria().getTipoLicenciamiento()
											.setCosto(costoControlSeguimiento);
								}
							}
						} else {
							if (proyectoP.getCatalogoCategoria().getTipoLicenciamiento().getId() == 2) {
								// costo quemado de $80.00
								proyectoP.getCatalogoCategoria().getTipoLicenciamiento()
									.setCosto(costoControlSeguimiento);
							}
						}
					}
				}
			}
			return proyectoP;

		} catch (ServiceException ex) {
			System.out.println("Error en la validación del pago");
			ex.printStackTrace();
		}
		
		return proyectoP;
	}
	
	
	// método para validar el pago en base a la organización -- CatalogoActividadesBean.java
	public Float validaPago(Usuario usuario, CatalogoCategoriaSistema catalogoP) {
		Float costoTotal = 0f;
		Float costoControlSeguimiento = 0f;
    	costoControlSeguimiento = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
				getPagoConfigValue("Valor por seguimiento de registro ambiental v2"));	//80.00
    	Float costoRegistro = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
				getPagoConfigValue("Valor por registro ambiental v2"));	//100.00
    	costoTotal = costoControlSeguimiento + costoRegistro;
		
    	try {
			organizacion = organizacionFacade.buscarPorPersona(usuario.getPersona(),usuario.getNombre()); 
			if(catalogoP != null) {
				if(organizacion != null) {
					// pago de control y seguimiento: $80.00
					// OG:2, Gobiernos Autónomos:5, Empresas Públicas:7, Empresas Mixtas: 8 Igual o más que el 70% de participación del Estado
					if(organizacion.getTipoOrganizacion().getId()==2 
							|| organizacion.getTipoOrganizacion().getId()==5
							|| organizacion.getTipoOrganizacion().getId()==7
							|| organizacion.getTipoOrganizacion().getId()==8) {
						// validación si hay participación del estado en empresa mixta
						if(organizacion.getParticipacionEstado() != null) {
							//if(organizacion.getParticipacionEstado().equals("Igual o más que el 70% de participación del Estado")) {
							if(organizacion.getParticipacionEstado().equals(IGUAL_MAS_PARTICIPACION)) {
								if(catalogoP.getCategoria().getId()==2) {
									// costo quemado de $80.00
									//catalogoP.getTipoLicenciamiento().setCosto(costoControlSeguimiento);
									costoTotal = costoControlSeguimiento;
								}
							}
						} else {
							if(catalogoP.getCategoria().getId()==2) {
								// costo quemado de $80.00
								//catalogoP.getTipoLicenciamiento().setCosto(costoControlSeguimiento);
								costoTotal = costoControlSeguimiento;
							}
						}
					}
				}
			}
			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		return costoTotal;
	}
	
    @Getter
    @Setter
    private String mensajeFinalizacion;
	
    private static final String MENOS_PARTICIPACION = "EMPRESAS MIXTAS CUANDO SU CAPITAL SUSCRITO SEA MENOR A LAS DOS TERCERAS PARTES A ENTIDADES DE DERECHO PÚBLICO.";
    
	public List<Object> validaCostoRegistroAmbiental(ProyectoLicenciamientoAmbiental proyectoLicencia, String processName) {
		mensajeFinalizacion = "";
		List<Object>objLic=new ArrayList<Object>();
		
		float valorAPagar = 0.00f;
    	
    	try {
    		organizacion = organizacionFacade.buscarPorPersona(proyectoLicencia.getUsuario().getPersona(), proyectoLicencia.getUsuario().getNombre());
        	//proyectoLicencia = proyectoLicenciaFacade.getProyectoPorId(proyectosBean.getProyecto().getId());
        	
        	Float costoEmisionRegistro = 0f;
        	Float costoControlSeguimiento = 0f;
        	
        	// validación del flujo: Registro Ambiental
        	if(processName.equals("Registro ambiental")) {
        		costoEmisionRegistro = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
        				getPagoConfigValue("Valor por registro ambiental v1"));	//100.00
        		costoControlSeguimiento = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
        				getPagoConfigValue("Valor por seguimiento de registro ambiental v1"));	//80.00
        		
        	}
        	// validación del flujo: Registro Ambiental v2
        	else if(processName.equals("Registro ambiental v2")) {
        		costoEmisionRegistro = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
        				getPagoConfigValue("Valor por registro ambiental v2"));	//100.00
        		costoControlSeguimiento = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
        				getPagoConfigValue("Valor por seguimiento de registro ambiental v2"));	//80.00
        		
        	}
        	// System.out.println("costoEmision>>>" + costoEmisionRegistro + "\ncostoControlSeguimiento>>>" + costoControlSeguimiento);
        	
        	//PAGOS PARA PERSONAS JURIDICAS
        	if(organizacion != null) {
        		// valido la categoria del proyecto
        		if(proyectoLicencia.getCatalogoCategoria().getId() != null) {
        			// No pagan las siguientes actividades:
            		// CULTIVO DE BANANO MENOR O IGUAL A 100 HECTÁREAS: 794
            		// MINERÍA ARTESANAL (METÁLICA, NO METÁLICA Y MATERIALES DE CONSTRUCCIÓN): 848
            		// MINERÍA ARTESANAL: EXPLOTACIÓN: 849
            		// MINERÍA ARTESANAL: EXPLOTACIÓN DE MATERIALES DE CONSTRUCCIÓN (ÁRIDOS Y PÉTREOS): 1457
        			if(proyectoLicencia.getCatalogoCategoria().getId()==794 
        					|| proyectoLicencia.getCatalogoCategoria().getId()==848
        					|| proyectoLicencia.getCatalogoCategoria().getId()==849 
        					|| proyectoLicencia.getCatalogoCategoria().getId()==1457) {
        				valorAPagar = 0.00f;
        				mensajeFinalizacion += "\npago por concepto de ";
        			}
        			// pago de control ($80.00)
            		// OG:2, Gobiernos Autónomos:5, Empresas Públicas:7
        			else if(organizacion.getTipoOrganizacion().getId()==2 || organizacion.getTipoOrganizacion().getId()==5 
        					|| organizacion.getTipoOrganizacion().getId()==7) {
        				valorAPagar = costoControlSeguimiento;
        				mensajeFinalizacion += "\nPago por Control y Seguimiento: ";
        			}
        			// pago de control y emisión ($180.00)
                    // ONG:1, Asociaciones:3, Comunidades:4, Empresas Privadas:6
        			else if(organizacion.getTipoOrganizacion().getId()==1 || organizacion.getTipoOrganizacion().getId()==3
        					|| organizacion.getTipoOrganizacion().getId()==4 || organizacion.getTipoOrganizacion().getId()==6){
        				valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
        				mensajeFinalizacion += "\npago por concepto de ";
        			}
        			// validación de empresas mixtas: 8
        			// Pagan: Menos del 70% de participación del Estado
        			// No pagan: Igual o más que el 70% de participación del Estado
        			else if(organizacion.getTipoOrganizacion().getId() == 8) {
        				//if(organizacion.getParticipacionEstado() == null 
        						//|| organizacion.getParticipacionEstado().equals("Menos del 70% de participación del Estado")) {
        				if(organizacion.getParticipacionEstado() == null 
        						|| organizacion.getParticipacionEstado().equals(MENOS_PARTICIPACION)) {
        					// pago de emision y control ($180.00)
        					valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
        					mensajeFinalizacion += "\npago por concepto de ";
        				//} else if(organizacion.getParticipacionEstado().equals("Igual o más que el 70% de participación del Estado")) {
        				} else if(organizacion.getParticipacionEstado().equals(IGUAL_MAS_PARTICIPACION)) {
        					// pago de control ($80.00)
        					valorAPagar = costoControlSeguimiento;
        					mensajeFinalizacion += "\nPago por Control y Seguimiento: ";
        				}
        				
        			}
        		}
        	} 
        	// PAGOS PARA PERSONAS NATURALES, EL PAGO ES LA SUMA TOTAL
        	else {
        		valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
        		mensajeFinalizacion += "\npago por concepto de ";
        	}
			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
    	
    	objLic.add(0, valorAPagar);
    	objLic.add(1, mensajeFinalizacion);
    	
    	return objLic;
    }
	
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoFacade;
	
	ProyectoLicenciamientoAmbiental proyectoLicencia;
	
	@Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineria;
	@EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;
	
	// PagoServiciosCategoriaIIV2
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@Setter
    @Getter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	public Map<String, Float> validarCostoRegistroAmbiental(float montoPagar, String projectId, String processName) {
		Map<String, Float> params = new ConcurrentHashMap<String, Float>();

    	float valorAPagar = 0.00f;
    	try {
    		
    		proyectoLicenciaCoa=proyectoLicenciaCoaFacade.buscarProyectoPorId((Integer.valueOf(projectId)));    		
    		proyectoLicencia = proyectoFacade.getProyectoPorId(Integer.valueOf(projectId));
    		
    		if(proyectoLicencia!=null)
    		organizacion = organizacionFacade.buscarPorPersona(proyectoLicencia.getUsuario().getPersona(), proyectoLicencia.getUsuario().getNombre());
    		else
    			organizacion = organizacionFacade.buscarPorPersona(proyectoLicenciaCoa.getUsuario().getPersona(), proyectoLicenciaCoa.getUsuario().getNombre());

    		if(proyectoLicencia!=null)
    		{
    			/***** cambio validación pago 20160329 *****/
    			if (proyectoLicencia.getTipoSector().getNombre().equals("Minería")) {
    				fichaAmbientalMineria = fichaAmbientalMineriaFacade.obtenerPorProyecto(proyectoLicencia);
    				if(fichaAmbientalMineria != null) {
    					inventarioForestal = inventarioForestalPmaFacade
    							.obtenerInventarioForestalPmaMineriaPorFicha(fichaAmbientalMineria.getId());
    				} else {
    					fichaAmbiental = fichaAmbientalFacade
    							.getFichaAmbientalPorCodigoProyecto(proyectoLicencia.getCodigo());
    					inventarioForestal = inventarioForestalPmaFacade
    							.obtenerInventarioForestalPmaPorFicha(fichaAmbiental.getId());
    				}

    			}
    			else {
    				fichaAmbiental = fichaAmbientalFacade
    						.getFichaAmbientalPorCodigoProyecto(proyectoLicencia.getCodigo());
    				inventarioForestal = inventarioForestalPmaFacade
    						.obtenerInventarioForestalPmaPorFicha(fichaAmbiental.getId());
    			}
    			/***** cambio validación pago 20160329 *****/
    		}
            
//        	
//        	fichaAmbiental = fichaAmbientalFacade.getFichaAmbientalPorCodigoProyecto(proyectoLicencia.getCodigo());
//        	inventarioForestal = inventarioForestalPmaFacade.obtenerInventarioForestalPmaPorFicha(fichaAmbiental.getId());
        	
        	// obtención de valores desde bdd
        	Float costoEmisionRegistro = 0f;
        	Float costoControlSeguimiento = 0f;
        	
        	// validación del flujo: Registro Ambiental
        	if(processName.equals("Registro ambiental")) {
        		costoEmisionRegistro = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
        				getPagoConfigValue("Valor por registro ambiental v1"));	//100.00
        		costoControlSeguimiento = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
        				getPagoConfigValue("Valor por seguimiento de registro ambiental v1"));	//80.00
        		
        	}
        	// validación del flujo: Registro Ambiental v2
        	else if(processName.equals("Registro ambiental v2")) {
        		costoEmisionRegistro = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
        				getPagoConfigValue("Valor por registro ambiental v2"));	//100.00
        		costoControlSeguimiento = Float.parseFloat(BeanLocator.getInstance(PagoConfiguracionesBean.class).
        				getPagoConfigValue("Valor por seguimiento de registro ambiental v2"));	//80.00
        		
        	}
        	// System.out.println("costoEmision>>>" + costoEmisionRegistro + "\ncostoControlSeguimiento>>>" + costoControlSeguimiento);
        	
        	//PAGOS PARA PERSONAS JURIDICAS
        	if(organizacion != null) {
        		// valido la categoria del proyecto
        		if(proyectoLicencia!=null)
        		{
        			if(proyectoLicencia.getCatalogoCategoria().getId() != null) {
        				// No pagan las siguientes actividades:
        				// CULTIVO DE BANANO MENOR O IGUAL A 100 HECTÁREAS: 794
        				// MINERÍA ARTESANAL (METÁLICA, NO METÁLICA Y MATERIALES DE CONSTRUCCIÓN): 848
        				// MINERÍA ARTESANAL: EXPLOTACIÓN: 849
        				// MINERÍA ARTESANAL: EXPLOTACIÓN DE MATERIALES DE CONSTRUCCIÓN (ÁRIDOS Y PÉTREOS): 1457
        				if(proyectoLicencia.getCatalogoCategoria().getId()==794 
        						|| proyectoLicencia.getCatalogoCategoria().getId()==848
        						|| proyectoLicencia.getCatalogoCategoria().getId()==849 
        						|| proyectoLicencia.getCatalogoCategoria().getId()==1457) {
        					valorAPagar = 0.00f;
        				}
        				// pago de control ($80.00)
        				// OG:2, Gobiernos Autónomos:5, Empresas Públicas:7
        				else if(organizacion.getTipoOrganizacion().getId()==2 
        						|| organizacion.getTipoOrganizacion().getId()==5 
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

        				// Validación de Remoción Vegetal
        				if(inventarioForestal!=null)
        				{
        					if(inventarioForestal.getRemocionVegetal()==true) {
        						float coberturaVegetal = 
        								inventarioForestal.getMaderaEnPie() * Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal"));
        						valorAPagar += coberturaVegetal;
        						params.put("coberturaVegetal", coberturaVegetal);
        					}
        				}
        			}
        		}
        		else
        		{
        			if(organizacion.getTipoOrganizacion().getId()==2 
    						|| organizacion.getTipoOrganizacion().getId()==5 
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
        	}
        	// PAGOS PARA PERSONAS NATURALES, EL PAGO NO SE ALTERA
        	else {
        		float coberturaVegetal =0;
        		if(fichaAmbiental!=null)
        		{
        			coberturaVegetal=inventarioForestal.getMaderaEnPie() * Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal"));
        			if(inventarioForestal.getRemocionVegetal()==true) {
        				if(proyectoLicencia.getCatalogoCategoria().getId()==794 
        						|| proyectoLicencia.getCatalogoCategoria().getId()==848
        						|| proyectoLicencia.getCatalogoCategoria().getId()==849 
        						|| proyectoLicencia.getCatalogoCategoria().getId()==1457) {
        					valorAPagar = 0.00f;
        				}else{

        					valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
        				}
        				valorAPagar += coberturaVegetal;
        				params.put("coberturaVegetal", coberturaVegetal);
        			}
        			else {
        				valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
        			}
        		}
        		else {
        			valorAPagar = costoEmisionRegistro + costoControlSeguimiento;
        		}
        	}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
    	
    	params.put("valorAPagar", valorAPagar);    	
    	//return valorAPagar;
    	return params;
    }
}
