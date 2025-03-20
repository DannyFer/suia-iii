package ec.gob.ambiente.suia.comun.controllers;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import lombok.Getter;
import lombok.Setter;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.base.ComunBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.web.services.financial.financialws.controller.OnlinePaymentService;
import ec.gob.ambiente.suia.web.services.financial.financialws.controller.OnlinePaymentService_Service;
import ec.gob.ambiente.suia.web.services.financial.financialws.controller.PaymentInVO;
import ec.gob.ambiente.suia.web.services.financial.financialws.controller.PaymentServiceInternal;
import ec.gob.ambiente.suia.web.services.financial.financialws.controller.PaymentServiceInternal_Service;
import ec.gob.ambiente.suia.web.services.financial.financialws.controller.RespuestaVO;
import ec.gob.ambiente.suia.web.services.financial.financialws.controller.SetPaymentRegister;


@ManagedBean
@ViewScoped
public class DineroElectronicoController  extends ComunBean {

	private static final long serialVersionUID = -193849248707153861L;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	private String transaction;	
	@Getter
	@Setter
	private String ci="";
	@Getter
	@Setter
	private String phone="";
	@Getter
	@Setter
	private String user = Constantes.getServicePayUserBCE();
	@Getter
	@Setter
	private String password = Constantes.getServicePayPwdBCE();
	@Getter
	@Setter
	private String userWsOP=Constantes.getServicePayUserWsOPS();
	@Getter
	@Setter
	private String pwdWsOP=Constantes.getServicePayPwdWsOPS();
	@EJB
	private ProcesoFacade procesoFacade;
		
	 private static OMElement getVersionId(String metodo, Integer id, String amountt,String docc,String sourcee) {  
	     System.out.println("ingresa a la lospagos");   
		 OMFactory factory = null;  
	        OMNamespace ns = null;  
	        OMElement elem = null;  
	        factory = OMAbstractFactory.getOMFactory();  
	        ns = factory.createOMNamespace("http://bce.webservices.mts.inswitch.us", "MTSService"); // Colocar el namespace del WebService al que se va a conectar y nombre del servicio
	        //elem = factory.createOMElement("cashInConfirm", ns);   
	        elem = factory.createOMElement(metodo, ns);   
	        //elem = factory.createOMElement("getVersionId", ns);   //consumo del metodo de versión de SDE
	        // Parámetros de consumo al método cashInConfirm
	        OMElement dtoRequest;

	        if (id ==1){
	        	dtoRequest = factory.createOMElement("dtoRequestCobroPre", ns);
	        }else{
	        	dtoRequest = factory.createOMElement("dtoRequestCobroConfirm", ns);
	        }			       
	        OMElement amount = factory.createOMElement("amount", ns);
		amount.setText(amountt);
		System.out.println("monto"+amountt);
	        OMElement brand = factory.createOMElement("brandId", ns);
		brand.setText("1");
	        OMElement currency = factory.createOMElement("currency", ns);
		currency.setText("1");
	        OMElement doc = factory.createOMElement("document", ns);
		doc.setText(docc); // documento de cuenta destino
		System.out.println("docc"+docc);
	        OMElement language = factory.createOMElement("language", ns);
		language.setText("es");
	        OMElement source = factory.createOMElement("msisdnSource", ns);
		source.setText(sourcee); // número de cuenta origen
		System.out.println("sourcee"+sourcee);
	        OMElement target = factory.createOMElement("msisdnTarget", ns);
		target.setText(Constantes.getServicePayUserBCE()); // número de cuenta destino
		System.out.println("getServicePayUserBCE"+Constantes.getServicePayUserBCE());
	        OMElement pass = factory.createOMElement("password", ns);
		pass.setText(Constantes.getServicePayPwdBCE()); // Colocar  su clave de acceso al WS
		System.out.println("getServicePayPwdBCE"+Constantes.getServicePayPwdBCE());
	        OMElement pin = factory.createOMElement("pin", ns);
		pin.setText(Constantes.getServicePayPwdBCE());
		System.out.println("getServicePayPwdBCE"+Constantes.getServicePayPwdBCE());
	         OMElement user = factory.createOMElement("user", ns);
		user.setText(Constantes.getServicePayUserBCE()); // Colocar  su usuario de acceso al WS
		System.out.println("getServicePayUserBCE"+Constantes.getServicePayUserBCE());
	        OMElement utfi = factory.createOMElement("utfi", ns);
		utfi.setText(getUtfi()); //unico
		System.out.println("getUtfi"+getUtfi());
	        dtoRequest.addChild(amount);
		dtoRequest.addChild(brand);
		dtoRequest.addChild(currency);
		dtoRequest.addChild(doc);
		dtoRequest.addChild(language);
	        dtoRequest.addChild(source);
		dtoRequest.addChild(target);
		dtoRequest.addChild(pass);
		dtoRequest.addChild(pin);
	        dtoRequest.addChild(user);
	        dtoRequest.addChild(utfi);  	
	        elem.addChild(dtoRequest);
	        System.out.println("fin de ingresa a la lospagos" +elem);
	        return elem;  
	}  
	 

//	private static EndpointReference targetEPR = new EndpointReference("https://181.211.102.40:8443/mts_bce/services/MTSService?wsdl");
	 private static EndpointReference targetEPR = new EndpointReference(Constantes.getServiceWsdlPayWs());

	public void dineroElectronico(double monto, Integer faci) throws Exception
	{			
		  try {
			  Thread.sleep(8000);	  			  
//	            System.setProperty("javax.net.ssl.keyStore", "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/security/keystore"); //--local
			  	System.setProperty("javax.net.ssl.keyStore", "/usr/java/jdk1.7.0_67/jre/lib/security/keystore"); //--preproduccion
	            System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
//	            System.setProperty("javax.net.ssl.trustStore", "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/security/cacerts"); // local
	            System.setProperty("javax.net.ssl.trustStore", "/usr/java/jdk1.7.0_67/jre/lib/security/cacerts"); //--preproduccion
	            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
	            System.setProperty("com.sun.net.ssl.dhKeyExchangeFix", "true");
	            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "false");
	            System.setProperty("sun.security.ssl.allowLegacyHelloMessages", "false");
	            System.setProperty("javax.net.debug", "ssl"); // Permite visualizar en consola los certificados de su computador            
	            SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
	            Socket s =null; 
	            	s=ssf.createSocket(Constantes.getServiceWsdlPayWsIP(),Integer.parseInt(Constantes.getServiceWsdlPayWsPuerto()));  // Cambiar por IP de servicio, el puerto es para la conexión segura de donde esta expuesto el servicio	            	
//		            Socket s=ssf.createSocket("181.211.102.40", 8443);  // Cambiar por IP de servicio, el puerto es para la conexión segura de donde esta expuesto el servicio
//	            SSLSession session = ((SSLSocket) s).getSession();
	            SSLSession session = null;
	            session = ((SSLSocket) s).getSession();
				          
	            
//	            Certificate[] cchain = session.getPeerCertificates();
//	            /*Permite conocer la información del certificado, sesion y de que ip a donde se hace la peticion opcional */
//	            for (int i = 0; i < cchain.length; i++) {
//	                  System.out.println(((X509Certificate) cchain[i]).getSubjectDN());
//	                }
//	                  System.out.println("Peer host is " + session.getPeerHost());
//	                  System.out.println("Cifrar es " + session.getCipherSuite());
//	                  System.out.println("Protocolo es " + session.getProtocol());
//	                  System.out.println("ID es " + new BigInteger(session.getId()));
//	                  System.out.println("Sesion creada en " + session.getCreationTime());
//	                  System.out.println("Sesion accedida en " + session.getLastAccessedTime());
	            
	                  
	        ServiceClient client = null;  
	        Options options = null;  
	        OMElement response = null;
	        OMElement responseconf = null;  	       
	        HttpTransportProperties.Authenticator authenticator = null;  
	        client = new ServiceClient();  
	        options = new Options();  
	        options.setAction("urn:cobroPre");   // metodo para realizar carga
	        //options.setAction("urn:getVersionId");  // metodo para ver la versión de SDE
	        Thread.sleep(8000);
	        options.setTo((targetEPR));  

	        authenticator = new HttpTransportProperties.Authenticator();  
	        // Se agrega las credenciales de la cabecera para el acceso al WS
	        authenticator.setPreemptiveAuthentication(true);
	        authenticator.setUsername(user);  
	        authenticator.setPassword(password);  	     
	        HTTPConstants org=  new HTTPConstants();
//	        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, authenticator);
//	        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, false);
//	        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.SO_TIMEOUT, new Integer(30000));
//	        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CONNECTION_TIMEOUT, new Integer(20000));
	        options.setProperty(org.AUTHENTICATE, authenticator);
	        options.setProperty(org.CHUNKED, false);
	        options.setProperty(org.SO_TIMEOUT, new Integer(30000));
	        options.setProperty(org.CONNECTION_TIMEOUT, new Integer(20000));
	        client.setOptions(options);  
	        String montototal= String.valueOf(monto);
	        
	        String cliente = JsfUtil.getLoggedUser().getPersona().getNombre();
	        String cedula = JsfUtil.getLoggedUser().getNombre();
        	String proyecto;
	        if(proyectosBean.getProyecto()!=null)
				proyecto = proyectosBean.getProyecto().getCodigo();
			else
				proyecto = bandejaTareasBean.getTarea().getProcedure();        	        
	        List<Contacto> listcontacto = new ArrayList<Contacto>();
			listcontacto=JsfUtil.getLoggedUser().getPersona().getContactos();
			String cell = "";
			for (Contacto c1 : listcontacto) {
				if(c1.getFormasContacto().getNombre().equals("*CELULAR"))
				cell=c1.getValor();			
			}	        
			System.out.println("ingresa para tomas los parametros");
	        response = client.sendReceive(getVersionId("cobroPre",1,montototal,cedula,cell));
	        String d=String.valueOf(response);
	        String[] valortot= d.split("<ax222:resultCode>");
//	        String[] valortot= d.split("<ax25:resultCode>");
	        String f=valortot[1];
	        String[] cobroPreok= f.split("</ax222:resultCode>");
//	        String[] cobroPreok= f.split("</ax25:resultCode>");
	        System.out.println("responde:::"+response);
	        if (cobroPreok[0].equals("1")){
	        	client = null;  
		        options = null;  
		        response = null;  
	        	authenticator = null;  
	 	        client = new ServiceClient();  
	 	        options = new Options();  
	 	        options.setAction("urn:cobroConfirm");   // metodo para realizar carga
	 	        //options.setAction("urn:getVersionId");  // metodo para ver la versión de SDE
	 	       Thread.sleep(8000);
	 	        options.setTo((targetEPR));  

	 	        authenticator = new HttpTransportProperties.Authenticator();  
	 	        // Se agrega las credenciales de la cabecera para el acceso al WS
	 	        authenticator.setPreemptiveAuthentication(true);
	 	        authenticator.setUsername(user);  
	 	        authenticator.setPassword(password);  	
	 	        HTTPConstants orgapache= new HTTPConstants();
//	 	        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, authenticator);
//	 	        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, false);
//	 	        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.SO_TIMEOUT, new Integer(30000));
//	 	        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CONNECTION_TIMEOUT, new Integer(20000));
	 	       options.setProperty(orgapache.AUTHENTICATE, authenticator);
	 	        options.setProperty(orgapache.CHUNKED, false);
	 	        options.setProperty(orgapache.SO_TIMEOUT, new Integer(30000));
	 	        options.setProperty(orgapache.CONNECTION_TIMEOUT, new Integer(20000));
	 	        client.setOptions(options);  	 	       
	 	        responseconf = client.sendReceive(getVersionId("cobroConfirm",2,montototal,cedula,cell));
	 	       String d3=String.valueOf(responseconf);
		        String[] valortot3= d3.split("<ax222:resultCode>");
//		        String[] valortot3= d3.split("<ax25:resultCode>");
		        String f1=valortot3[1];
		        String[] cobroConfirmok=f1.split("</ax222:resultCode>");
//		        String[] cobroConfirmok=f1.split("</ax25:resultCode>");
		        if (cobroConfirmok[0].equals("1")){
		        	ci=cedula;
					phone=cell;
		        	
					OnlinePaymentService_Service service = new OnlinePaymentService_Service();
					SetPaymentRegister parametersPayment = new SetPaymentRegister();
					String mo = String.valueOf(responseconf);
					String[] mosp = mo.split("%MSISDN_ORIGIN</ax223:key><ax223:value>");
//					String[] mosp = mo.split("%MSISDN_ORIGIN</ax25:key><ax25:value>");
					String mosps = mosp[1];
					String[] msisor = mosps.split("</ax223:value>");
//					String[] msisor = mosps.split("</ax25:value>");
					// cuenta numero
					parametersPayment.setNumeroCuenta(msisor[0]);
					// Valor Pagado
					String[] tods = mo.split("%TOTAL_DEBIT</ax223:key><ax223:value>");
//					String[] tods = mo.split("%TOTAL_DEBIT</ax25:key><ax25:value>");
					String todss = tods[1];
					String[] total_debit = todss.split("</ax223:value>");
//					String[] total_debit = todss.split("</ax25:value>");
					parametersPayment.setMonto(total_debit[0]);
					// Numero de Transaccion
					String[] trs = mo.split("%TRANSACTION</ax223:key><ax223:value>");
//					String[] trs = mo.split("%TRANSACTION</ax25:key><ax25:value>");
					String trss = trs[1];
					String[] transac = trss.split("</ax223:value>");
//					String[] transac = trss.split("</ax25:value>");
					// fecha de transaccion
					parametersPayment.setNumeroTransaccionCobis(transac[0]);
					String[] trti = mo.split("%TRANSACTION_TIMESTAMP</ax223:key><ax223:value>");
//					String[] trti = mo.split("%TRANSACTION_TIMESTAMP</ax25:key><ax25:value>");
					String trtis = trti[1];
					String[] tran_time = trtis.split("</ax223:value>");
//					String[] tran_time = trtis.split("</ax25:value>");
					// fechaHora
					parametersPayment.setFechaHora(tran_time[0]);  	                  
					
					parametersPayment.setUsuario(userWsOP);
					parametersPayment.setContrasenia(pwdWsOP);
					parametersPayment.setCodigoBanco("BCE001");
					parametersPayment.setIdCliente(ci);
					parametersPayment.setNombreCliente(cliente);
					parametersPayment
							.setNombreConvenio("Convenio BCE Dinero Electrónico");
					parametersPayment.setNumeroConvenio("BCE-0000000000");
					parametersPayment.setTipoTransaccion("Dinero Electronico");
					parametersPayment
							.setOficinaTransaccion("Dinero Electronico");

					OnlinePaymentService onlinePyment = service
							.getOnlinePaymentServicePort();
					RespuestaVO result = onlinePyment.setPaymentRegister(
							parametersPayment.getNumeroTransaccionCobis(),
							parametersPayment.getNumeroConvenio(),
							parametersPayment.getNombreConvenio(),
							parametersPayment.getNumeroCuenta(),
							parametersPayment.getFechaHora(),
							parametersPayment.getIdCliente(),
							parametersPayment.getNombreCliente(),
							parametersPayment.getMonto(),
							parametersPayment.getOficinaTransaccion(),
							parametersPayment.getUsuario(),
							parametersPayment.getContrasenia(),
							parametersPayment.getCodigoBanco(),
							parametersPayment.getTipoTransaccion());

					System.out.println("Resultado codigo: "
							+ result.getCodigoRespuesta());
					System.out.println("Resultado mensaje: "
							+ result.getMensaje());

					PaymentServiceInternal_Service servicioInt = new PaymentServiceInternal_Service();
					PaymentServiceInternal portPago = servicioInt
							.getPaymentServiceInternalPort();
					List<PaymentInVO> listRealizarPago = new ArrayList<PaymentInVO>();
					PaymentInVO pago = new PaymentInVO();
					pago.setBankEntityCode(parametersPayment.getCodigoBanco());
					pago.setTransactionNumber(parametersPayment
							.getNumeroTransaccionCobis());
					listRealizarPago.add(pago);
					portPago.realizarPago(listRealizarPago,
							Double.valueOf(parametersPayment.getMonto()),
							proyecto);
					completarTarea(faci);
		        		
		        	
		        }
		        if (cobroConfirmok[0].equals("2")){		        			        	
		        		JsfUtil.addMessageWarning("No se puede realizar la transacción, misma que fue cancelada por parte del usuario.");
		        }
	 	       System.out.println(responseconf); 	 	        
	        }
	        if (cobroPreok[0].equals("2")){
	        	String[] valorer= d.split("<ax222:resultText>");
//	        	String[] valorer= d.split("<ax25:resultText>");
		        String fer=valorer[1];
		        String[] cobroPreerror= fer.split("</ax222:resultText>");
//		        String[] cobroPreerror= fer.split("</ax25:resultText>");
		        if (cobroPreerror[0].contains("El número móvil  no se encuentra registrado en Efectivo desde mi celular")){
		        	JsfUtil.addMessageWarning("El número de celular o usuario no se encuentra registrado en el Banco Central");
		        }else{
		        	JsfUtil.addMessageWarning(cobroPreerror[0]);
		        }		        	        	
	        }
	        System.out.println("ss"+response);  
	   
	        } catch (AxisFault e)
	        {
	        	String error= String.valueOf(e);
	        	if (error.contains("Read timed out")){
	        		JsfUtil.addMessageError("No se pudo completar el proceso, tiempo agotado");
	        	}else{
	        		JsfUtil.addMessageError("En estos momentos el servicio proporcionado por el Banco Central del Ecuador está deshabilitado, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
	        	}        	
	        }
	}
	
	public static String getUtfi(){
        double randomico = Math.random();
        int intRandom = (int)Math.floor(randomico * 100000) ;
        Calendar calendar = Calendar.getInstance();
        String utfi = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + String.valueOf(calendar.get(Calendar.MONTH) + 1) +
                      String.valueOf(calendar.get(Calendar.YEAR)) + String.valueOf(intRandom);
        return utfi;
    }
	
	public synchronized Date subtractDatesDays(Date date, int days) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(date.getTime());
        cal.add(Calendar.DATE, -days);
        return new java.sql.Date(cal.getTimeInMillis());
    }
    public String getCurrentDate(String format) {
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat(format);
        return formateador.format(ahora);
    }

    public void enviarAprobacionPagoDineroElectronico(long processInstanceIdTask, Usuario usuario, long taskId, Map<String, Object> data)
			throws JbpmException {
		procesoFacade.aprobarTarea(usuario, taskId, processInstanceIdTask, data);
	}
    public void completarTarea(Integer facilitadores) throws IOException {
    	try {
    		Map<String, Object> data=null;
    		if (bandejaTareasBean.getTarea().getTaskName().contains("Validar pago de tasas de facilitador/es")){
    			guardarPagoDinero(facilitadores);
    			
    		}    			
			enviarAprobacionPagoDineroElectronico(bandejaTareasBean.getTarea()
					.getProcessInstanceId(), JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),data);
			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			RequestContext.getCurrentInstance().execute("PF('send').disable();");
			FacesContext.getCurrentInstance().getExternalContext().redirect("/suia-iii/bandeja/bandejaTareas.jsf?faces-redirect=true");
		} catch (JbpmException e) {
			e.printStackTrace();
		}
    }
    
//    public double Monto() {
//		double montoTotal = 1.0;
//		
//		return montoTotal;
//	}
    
    @Override
	public String executeOperationAction() {
		RequestContext.getCurrentInstance().execute("PF('sendMore').disable();PF('sendCancel').disable();");
		return super.executeOperationAction();
	}


	@Override
	public String getFunctionURL() {
		return "/comun/pagoServicios.jsf";
	}


	@Override
	public void cleanData() {
		identificadorMotivo = null;
//		transaccionFinanciera = new TransaccionFinanciera();
//		transaccionesFinancieras = new ArrayList<TransaccionFinanciera>();
		
	}

	private Boolean persist = false;
	private String identificadorMotivo;
//	
//	@EJB
//    FacilitadorFacade facilitadorFacade;
	
	@Override
	public boolean executeBusinessLogic(Object object) {	
		// TODO Auto-generated method stub
		System.out.println("aasa");				
				JsfUtil.addMessageInfo("Pago satisfactorio.");
				executeOperation(object);
				return true;
			
	}
	
	public String guardarPagoDinero(Integer facilitador) {
		if (bandejaTareasBean.getTarea().getTaskName().contains("Validar pago de tasas de facilitador/es")){
        try {
                            try {
                                List<Usuario> confirmado = new ArrayList<Usuario>();
                                List<Usuario> rechazado = new ArrayList<Usuario>();
                                String facilitadoresConfirmados = "";
                                String facilitadoresRechazados = "";
                                String separador = "";
                                Integer numeroFacilitadores = facilitador;
                                FacilitadorFacade facilitadorFacade= new FacilitadorFacade();
                                List<Usuario> facilitadores = facilitadorFacade
                                        .seleccionarFacilitadores(facilitador,                                		
                                                confirmado,
                                                rechazado,"DIRECCIÓN NACIONAL DE PREVENCIÓN DE LA CONTAMINACIÓN AMBIENTAL");
                                if (facilitadores.size() == facilitador) {
                                    String[] facilitadoresLista = new String[facilitadores.size()];
                                    String facilitadoresS = "";
                                    separador = "";
                                    Integer cont = 0;
                                    for (Usuario usuario : facilitadores) {
                                        facilitadoresLista[cont++] = usuario.getNombre();
                                        facilitadoresS += separador + usuario.getNombre();
                                        separador = ",";
                                    }
                                    facilitadoresS += facilitadoresConfirmados;
                                    Map<String, Object> params = new HashMap<String, Object>();

                                    params.put("facilitadorSRechazo", facilitadoresRechazados);
                                    params.put("listaFacilitadoresS", facilitadoresS);
                                    params.put("listaFacilitadores", facilitadoresLista);
                                    params.put("numeroFacilitadores", numeroFacilitadores);
                                    procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
                                    //Se aprueba la tarea
                                    Map<String, Object> data = new ConcurrentHashMap<String, Object>();

                                    procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),
                                            bandejaTareasBean.getTarea().getTaskId(),
                                            bandejaTareasBean.getProcessId(), data
                                    );
                                    JsfUtil.addMessageInfo("Se realizó correctamente la operación.");

                                } else {
                                    JsfUtil.addMessageError("Error al completar la tarea. No existen la cantidad de facilitadores solicitados disponibles.");
                                    return "";
                                }
                            } catch (Exception e) {
                                JsfUtil.addMessageError("Error al completar la tarea.");
                            }

                            return "";
                  
        } catch (Exception e) {
            JsfUtil.addMessageError("El pago No ha sido guardado.");
        }
	}
    
    return "";

}
	
}
