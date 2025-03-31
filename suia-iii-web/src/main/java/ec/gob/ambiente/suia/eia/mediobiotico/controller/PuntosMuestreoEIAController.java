package ec.gob.ambiente.suia.eia.mediobiotico.controller;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.AbundanciaRelativa;
import ec.gob.ambiente.suia.domain.enums.NivelIdentificacion;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.medioBiotico.PuntosMuestreoEiaFacade;
import ec.gob.ambiente.suia.eia.medioBiotico.facade.MetodologiaPuntoMuestreoFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.*;

/**
 * @author Oscar Campana
 */
@ManagedBean
@ViewScoped
public class PuntosMuestreoEIAController implements Serializable {

    private static final long serialVersionUID = 1572523482381028668L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(PuntosMuestreoEIAController.class);
    private static boolean requirecalcular = true;
    @Getter
    @Setter
    PuntosMuestreoEIA puntosMuestreoEIA;
    @Getter
    @Setter
    RegistroEspeciesEIA registroEspeciesEIA;
    @Getter
    @Setter
    List<PuntosMuestreoEIA> listaPuntosMuestreo;
    @Getter
    @Setter
    List<PuntosMuestreoEIA> listaPuntosMuestreoRegistro;
    @Getter
    @Setter
    List<RegistroEspeciesEIA> listaRegistroEspecies;
    @Setter
    List<ComparacionPuntosMuestreoEIA> listaComparacionesPtos;
    @Getter
    @Setter
    List<ComparacionPuntosMuestreoEIA> listaComparacionesPtosEliminadas;
    @Getter
    @Setter
    List<AnalisisDiversidadPtoMuestreoEIA> listaAnalisisDiversidad;
    @Getter
    @Setter
    List<AnalisisComparacionPtosMuestreoEIA> listaComparacionPtos;
    @Getter
    @Setter
    List<AnalisisComparacionPtosMuestreoEIA> listaComparacionPtosFiltrados;
    @Getter
    @Setter
    List<RegistroEspeciesEIA> listaMuestraAEspecies;
    @Getter
    @Setter
    List<RegistroEspeciesEIA> listaMuestraBEspecies;
    @Getter
    @Setter
    List<PuntosMuestreoEIA> listaPuntosMuestreoEliminados;
    @Getter
    @Setter
    List<RegistroEspeciesEIA> listaRegistroEspeciesEliminados;
    @Getter
    @Setter
    List<PuntosMuestreoEIA> listaAbundancia;
    TipoDocumentoSistema tipoDocumentoSistema;

    private boolean hasRegisterType;

//    @Getter
//    @Setter
    private boolean registroDirecto;

    private List<String> listaCodigosPtosMuestreo;
    @EJB
    private ValidacionSeccionesFacade validacionesSeccionesFacade;

    @EJB
    private MetodologiaPuntoMuestreoFacade metodologiaPuntoMuestreoFacade;

    private EstudioImpactoAmbiental estudioImpactoAmbiental;
    @EJB
    private PuntosMuestreoEiaFacade puntosMuestreoEIAFacade;
    @Getter
    @Setter
    private Documento documento;
    @Getter
    @Setter
    private MedioBioticoJustificacionEIA justificacionEIA;
    @Getter
    @Setter
    private MedioBioticoFormulasEIA formulasEIA;
    @Getter
    @Setter
    private String tipo;
    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
    private ValidacionSeccionesFacade validacionSeccionesFacade;
    @Setter
    @Getter
    private NivelIdentificacion[] nivelIdentificacion = NivelIdentificacion.values();
    @Setter
    @Getter
    private AbundanciaRelativa[] abundanciaRelativa = AbundanciaRelativa.values();
    @Setter
    @Getter
    private boolean isEditing;
    @Setter
    @Getter
    private boolean isEditingRegistro;
    @Getter
    private boolean existeDocumentoAdjunto;
    @Getter
    @Setter
    private boolean tieneContenido;
    @Getter
    @Setter
    private List<MetodologiaPuntoMuestreo> metodologiaPuntoMuestreos;
    @Getter
    @Setter
    private  List<TipoRegistroEspecie> listaTipoRegistroDirecto;
    @Getter
    @Setter
    private  List<TipoRegistroEspecie> listaTipoRegistroIndirecto;

    @Getter
    //@Setter
    private TipoRegistroEspecie tipoRegistroEspecie;


    /*boolean*
     * Metodo que se ejecuta automaticamente al cargar la página
     *
     * @throws CmisAlfrescoException
     * @throws ServiceExbooleanception
     */
    @PostConstruct
    private void postInit() throws CmisAlfrescoException, ServiceException {
        estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil
                .devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map params = externalContext.getRequestParameterMap();
        tipo = (String) params.get("id");

        cargarDatos();
    }

    /**
     * Carga datos y consultas iniciales
     *
     * @throws CmisAlfrescoException
     * @throws ServiceException
     */
    private void cargarDatos() throws CmisAlfrescoException, ServiceException {

        tipoDocumentoSistema = null;

        if (this.tipo.equalsIgnoreCase("Flora")) {
            tipoDocumentoSistema = TipoDocumentoSistema.FLORA;
            hasRegisterType = false;
        } else if (this.tipo.equalsIgnoreCase("Mastofauna")) {
            tipoDocumentoSistema = TipoDocumentoSistema.MASTOFAUNA;
            hasRegisterType = true;
        } else if (this.tipo.equalsIgnoreCase("Ornitofauna")) {
            tipoDocumentoSistema = TipoDocumentoSistema.ORNITOFAUNA;
            hasRegisterType = true;
        } else if (this.tipo.equalsIgnoreCase("Herpetofauna")) {
            tipoDocumentoSistema = TipoDocumentoSistema.HERPETOFAUNA;
            hasRegisterType = true;
        } else if (this.tipo.equalsIgnoreCase("Entomofauna")) {
            tipoDocumentoSistema = TipoDocumentoSistema.ENTOMOFAUNA;
            hasRegisterType = false;
        } else if (this.tipo.equalsIgnoreCase("Ictiofauna")) {
            tipoDocumentoSistema = TipoDocumentoSistema.ICTIOFAUNA;
            hasRegisterType = true;
        } else if (this.tipo.equalsIgnoreCase("Macroinvertebrados")) {
            tipoDocumentoSistema = TipoDocumentoSistema.MACROINVERTEBRADOS;
            hasRegisterType = false;
        } else if (this.tipo.equalsIgnoreCase("Limnofauna")) {
            tipoDocumentoSistema = TipoDocumentoSistema.LIMNOFAUNA;
            hasRegisterType = false;
        }


        this.isEditing = false;
        this.existeDocumentoAdjunto = false;
        this.documento = new Documento();

        this.justificacionEIA = new MedioBioticoJustificacionEIA();
        this.formulasEIA = new MedioBioticoFormulasEIA();
        this.puntosMuestreoEIA = new PuntosMuestreoEIA();
        this.listaMuestraAEspecies = new ArrayList<RegistroEspeciesEIA>();
        this.listaMuestraBEspecies = new ArrayList<RegistroEspeciesEIA>();

        listaAbundancia = new ArrayList<PuntosMuestreoEIA>();
        listaPuntosMuestreo = new ArrayList<PuntosMuestreoEIA>();
        listaPuntosMuestreoEliminados = new ArrayList<PuntosMuestreoEIA>();

        listaRegistroEspecies = new ArrayList<RegistroEspeciesEIA>();
        listaRegistroEspeciesEliminados = new ArrayList<RegistroEspeciesEIA>();

        listaComparacionesPtosEliminadas = new ArrayList<ComparacionPuntosMuestreoEIA>();
        listaCodigosPtosMuestreo = new ArrayList<String>();

        metodologiaPuntoMuestreos = metodologiaPuntoMuestreoFacade.listarMetodologias(this.tipo.equalsIgnoreCase("Flora"));

        this.tipoRegistroEspecie = new TipoRegistroEspecie();
        this.listaTipoRegistroDirecto = new ArrayList<TipoRegistroEspecie>();
        this.listaTipoRegistroIndirecto = new ArrayList<TipoRegistroEspecie>();
        cargarAdjuntosEIA();
        cargarDatosObjetos();

        ordenarListaPtosMuestreo();
        realizarCalculos();
        compararPuntos();

        if(!this.tipo.equalsIgnoreCase("Macroinvertebrados") &&
                !this.tipo.equalsIgnoreCase("Limnofauna"))
        calcularAbundanciaRelativa();
    }

    private void cargarDatosObjetos() {
        try {
            this.listaPuntosMuestreo = puntosMuestreoEIAFacade.listarPorEIA(estudioImpactoAmbiental, tipo);
            //Cargamos las listas de tipos de registros de especies (directos e indirectos).
            if (this.tipo.equalsIgnoreCase("mastofauna") || this.tipo.equalsIgnoreCase("ornitofauna") ||
                    this.tipo.equalsIgnoreCase("herpetofauna")){
                this.listaTipoRegistroDirecto.addAll(puntosMuestreoEIAFacade.listarTiposRegistrosEspecies(this.tipo,true));
                this.listaTipoRegistroIndirecto.addAll(puntosMuestreoEIAFacade.listarTiposRegistrosEspecies(this.tipo,false));
            }
            else if(this.tipo.equalsIgnoreCase("ictiofauna")){
                this.listaTipoRegistroIndirecto.addAll(puntosMuestreoEIAFacade.listarTiposRegistrosEspecies(this.tipo,false));
            }
            listaRegistroEspecies = new ArrayList<>();
            listaComparacionesPtos = new ArrayList<>();

            for (PuntosMuestreoEIA p : this.listaPuntosMuestreo) {
                p.setCantRegEspecies(p.getEspecies().size());
                listaPuntosMuestreo.set(listaPuntosMuestreo.indexOf(p),p);
                listaRegistroEspecies.addAll(p.getEspecies());
                listaComparacionesPtos.addAll(this.puntosMuestreoEIAFacade.listarComparacionesPtosPorMuestraA(p));
                listaCodigosPtosMuestreo.add(p.getNombrePunto());
            }
            // this.listaRegistroEspecies = puntosMuestreoEIAFacade.listarEspeciesPorEIA(estudioImpactoAmbiental, tipo);
            this.formulasEIA = puntosMuestreoEIAFacade.listarFormulasPorEIA(estudioImpactoAmbiental, tipo);

            if (this.formulasEIA == null) {
                this.formulasEIA = new MedioBioticoFormulasEIA();
                this.formulasEIA.setTipoMedioBiotico(this.tipo);
                this.formulasEIA.setEia(estudioImpactoAmbiental);
            }


            if (this.listaPuntosMuestreo.size() > 0 && this.listaRegistroEspecies.size() > 0 /*&& this.documento.getId() != null && this.documento.getEstado()*/) {
                this.tieneContenido = true;
                //calcularFormulas();

            } else {
                this.tieneContenido = false;
                this.documento = new Documento();
                this.documento.setEstado(false);
                this.justificacionEIA = puntosMuestreoEIAFacade.listarJustificacionPorEIA(estudioImpactoAmbiental, tipo);
                if (this.justificacionEIA == null) {
                    this.justificacionEIA = new MedioBioticoJustificacionEIA();
                    this.justificacionEIA.setTipoMedioBiotico(this.tipo);
                    this.justificacionEIA.setEia(estudioImpactoAmbiental);
                }
            }

        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    /**
     * Metodo de guardado general
     *
     * @throws CmisAlfrescoException
     */
    public void guardar(String seccion) throws CmisAlfrescoException {
        try {
            boolean guardar = true;
            boolean omitirDocumento = false;
            if (tipo.equals("mastofauna")
                    || tipo.equals("ornitofauna")
                    || tipo.equals("herpetofauna")
                    || tipo.equals("entomofauna")
                    || tipo.equals("ictiofauna")
                    || tipo.equals("macroinvertebrados")
                    || tipo.equals("limnofauna")
                    ) {
                guardar = tieneContenido;
                omitirDocumento = true;
            }
            if ((guardar && (this.listaPuntosMuestreo.size() > 0 || this.listaRegistroEspecies.size() > 0 || this.documento.getNombre() != null))) {
                if (listaPuntosMuestreo.size() > 0 && listaRegistroEspecies.size() > 0) {
                    if (omitirDocumento || this.salvarDocumento()) {

                        puntosMuestreoEIAFacade.guardar(listaPuntosMuestreo, listaPuntosMuestreoEliminados);
                        puntosMuestreoEIAFacade.guardarEspecies(listaRegistroEspecies, listaRegistroEspeciesEliminados);
                        puntosMuestreoEIAFacade.guardarComparaciones(this.listaComparacionesPtos,this.listaComparacionesPtosEliminadas);
                        puntosMuestreoEIAFacade.guardarFormulas(formulasEIA, null);

                        cargarDatos();

                        if (this.listaPuntosMuestreo.size() > 0) {
                            this.actualizarEstadoValidacionSeccion(seccion);
                        }
                        JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
                        realizarCalculos();
                        if(!this.tipo.equalsIgnoreCase("Macroinvertebrados") &&
                                !this.tipo.equalsIgnoreCase("Limnofauna"))
                        calcularAbundanciaRelativa();

                    }
                } else {
                    validacionesSeccionesFacade
                            .guardarValidacionSeccion("EIA", seccion, estudioImpactoAmbiental.getId().toString(), false);
                    JsfUtil.addMessageError("Debe al menos registrar 1 punto de muestreo y 1 especie.");
                }
            } else {
                if (!guardar && (listaPuntosMuestreo.size() > 0 || listaRegistroEspecies.size() > 0)) {
                    for (PuntosMuestreoEIA punto : listaPuntosMuestreo) {
                        punto.setEstado(false);
                    }
                    for (RegistroEspeciesEIA registro : listaRegistroEspecies) {
                        registro.setEstado(false);
                        registro.getPuntosMuestreo().setEstado(false);
                    }
                    formulasEIA.setEstado(false);
                    puntosMuestreoEIAFacade.guardar(listaPuntosMuestreo, listaPuntosMuestreoEliminados);
                    puntosMuestreoEIAFacade.guardarEspecies(listaRegistroEspecies, listaRegistroEspeciesEliminados);
                    puntosMuestreoEIAFacade.guardarComparaciones(this.listaComparacionesPtos,this.listaComparacionesPtosEliminadas);
                    puntosMuestreoEIAFacade.guardarFormulas(formulasEIA, null);
                }
                String justificación = this.justificacionEIA.getJustificacion();
                this.justificacionEIA = puntosMuestreoEIAFacade.listarJustificacionPorEIA(estudioImpactoAmbiental, tipo);
                if (this.justificacionEIA == null) {
                    this.justificacionEIA = new MedioBioticoJustificacionEIA();
                    this.justificacionEIA.setTipoMedioBiotico(this.tipo);
                    this.justificacionEIA.setEia(estudioImpactoAmbiental);
                }
                this.justificacionEIA.setJustificacion(justificación);
                puntosMuestreoEIAFacade.guardarJustificaion(this.justificacionEIA, null);
                this.actualizarEstadoValidacionSeccion(seccion);
                JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
                this.cargarDatos();
            }

        } catch (ServiceException e) {
            LOG.error(e, e);
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + ". ");
        } catch (RuntimeException e) {
            LOG.error(e, e);
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + ". ");
        }

    }

    /**
     * Asigna la seccion valida
     *
     * @throws ServiceException
     * @throws CmisAlfrescoException
     */

    private void actualizarEstadoValidacionSeccion(String seccion) throws ServiceException, CmisAlfrescoException {
        validacionesSeccionesFacade
                .guardarValidacionSeccion("EIA", seccion, estudioImpactoAmbiental.getId().toString());

    }

    /**
     * Prepara la edicion de un punto de muestreo
     *
     * @param puntosMuestreoEIA
     */
    public void editarPuntoMuestreo(PuntosMuestreoEIA puntosMuestreoEIA) {

        this.puntosMuestreoEIA = puntosMuestreoEIA;
        if (puntosMuestreoEIA.getX2()!=null && puntosMuestreoEIA.getX2().doubleValue() == 0.0d) {
            puntosMuestreoEIA.setX2(null);
        }
        if (puntosMuestreoEIA.getX3()!=null && puntosMuestreoEIA.getX3().doubleValue() == 0.0d) {
            puntosMuestreoEIA.setX3(null);
        }
        if (puntosMuestreoEIA.getX4()!=null && puntosMuestreoEIA.getX4().doubleValue() == 0.0d) {
            puntosMuestreoEIA.setX4(null);
        }
        if (puntosMuestreoEIA.getY2()!=null && puntosMuestreoEIA.getY2().doubleValue() == 0.0d) {
            puntosMuestreoEIA.setY2(null);
        }
        if (puntosMuestreoEIA.getY2()!=null && puntosMuestreoEIA.getY3().doubleValue() == 0.0d) {
            puntosMuestreoEIA.setY3(null);
        }
        if (puntosMuestreoEIA.getY2()!=null && puntosMuestreoEIA.getY4().doubleValue() == 0.0d) {
            puntosMuestreoEIA.setY4(null);
        }

        this.isEditing = true;

    }

    /**
     * Prepara la edición de un registro de especies
     *
     * @param registroEspeciesEIA
     */
    public void editarRegistroEspecies(RegistroEspeciesEIA registroEspeciesEIA) {

        this.registroEspeciesEIA = registroEspeciesEIA;
        this.isEditingRegistro = true;
        this.tipoRegistroEspecie.setOtro(registroEspeciesEIA.getOtro());
        int pos= 0;
        boolean found = false;
        TipoRegistroEspecie tipo;
        this.registroDirecto =this.registroEspeciesEIA.getRegistroDirecto();
        if(this.registroDirecto){
            while (!found && pos<this.listaTipoRegistroDirecto.size()){
                tipo = this.listaTipoRegistroDirecto.get(pos);
                if(tipo.getOtro() == registroEspeciesEIA.getOtro()){
                    if(tipo.getOtro()){
                        tipo.setNombreOtro(registroEspeciesEIA.getTipoRegistro());
                        this.tipoRegistroEspecie = tipo;
                        found= true;
                    }
                    else{
                        if(!tipo.getOtro() && tipo.getNombre().equalsIgnoreCase(registroEspeciesEIA.getTipoRegistro())){
                            this.tipoRegistroEspecie = tipo;
                            found= true;
                        }
                        else{
                            pos++;
                        }
                    }
                }
                else{
                    pos++;
                }
            }
        }
        else{
            while (!found && pos<this.listaTipoRegistroIndirecto.size()){
                tipo = this.listaTipoRegistroIndirecto.get(pos);
                if(tipo.getOtro() == registroEspeciesEIA.getOtro()){
                    if(tipo.getOtro()){
                        tipo.setNombreOtro(registroEspeciesEIA.getTipoRegistro());
                        this.tipoRegistroEspecie = tipo;
                        found= true;
                    }
                    else{
                        if(!tipo.getOtro() && tipo.getNombre().equalsIgnoreCase(registroEspeciesEIA.getTipoRegistro())){
                            this.tipoRegistroEspecie = tipo;
                            found= true;
                        }
                        else{
                            pos++;
                        }
                    }
                }
                else{
                    pos++;
                }
            }
        }

    }

    /**
     * Inicializacion de variables y objetos
     */
    public void inicializarPuntoMuestreo() {

        this.puntosMuestreoEIA = new PuntosMuestreoEIA();
        this.puntosMuestreoEIA.setEia(this.estudioImpactoAmbiental);
        this.puntosMuestreoEIA.setEspecies(new ArrayList<RegistroEspeciesEIA>());
        this.puntosMuestreoEIA.setTipoMedioBiotico(this.tipo);
        this.puntosMuestreoEIA.setCantRegEspecies(0);
        this.isEditing = false;
        JsfUtil.addCallbackParam("puntoMuestreo");

    }

    /**
     * Inicializacion de variables y objetos
     */
    public void inicializarRegistroEspecies() {

        this.registroEspeciesEIA = new RegistroEspeciesEIA();
        this.registroEspeciesEIA.setEia(this.estudioImpactoAmbiental);
        this.registroEspeciesEIA.setPuntosMuestreo(new PuntosMuestreoEIA());
        this.registroEspeciesEIA.setTipoMedioBiotico(this.tipo);
        this.registroEspeciesEIA.setRegistroDirecto(true);
        this.isEditingRegistro = false;
        this.tipoRegistroEspecie = new TipoRegistroEspecie();
        if(!this.tipo.equalsIgnoreCase("ictiofauna"))
            this.registroDirecto = true;
        else
            this.registroDirecto = false;

        this.registroEspeciesEIA.setOtro(false);

        JsfUtil.addCallbackParam("registroEspecies");

    }

    Comparator<PuntosMuestreoEIA> comparator = new Comparator<PuntosMuestreoEIA>() {
        @Override
        public int compare(PuntosMuestreoEIA p1, PuntosMuestreoEIA p2) {
            return p1.getNombrePunto().compareTo(p2.getNombrePunto());
        }
    };

    private void ordenarListaPtosMuestreo(){
        Collections.sort(this.listaPuntosMuestreo,this.comparator);
    }

    /**
     * Agregar un punto muestreo
     */
    public void agregarPuntoMuestreo() {

        String codigo = this.puntosMuestreoEIA.getNombrePunto();

        if(isValidCode(codigo)){
            if (!this.isEditing) {
                if(!this.listaCodigosPtosMuestreo.contains(codigo)){
                    this.listaPuntosMuestreo.add(this.puntosMuestreoEIA);

                    this.listaCodigosPtosMuestreo.add(codigo);
                    ordenarListaPtosMuestreo();
                    //Calculamos la abundancia
                    if(!this.tipo.equalsIgnoreCase("Macroinvertebrados") &&
                            !this.tipo.equalsIgnoreCase("Limnofauna"))
                        calcularAbundanciaRelativa();
                    realizarCalculos();
                    compararPuntos();
                    JsfUtil.addCallbackParam("puntoMuestreo");
                    RequestContext.getCurrentInstance().execute(
                            "PF('dlg3').hide();");
                } else {
                    JsfUtil.addMessageError("El código '" + codigo + "' ya existe. Por favor, reemplácelo.");
                }
            }
            else{
                this.listaCodigosPtosMuestreo.add(codigo);
                ordenarListaPtosMuestreo();
                //Calculamos la abundancia
                if(!this.tipo.equalsIgnoreCase("Macroinvertebrados") &&
                        !this.tipo.equalsIgnoreCase("Limnofauna"))
                    calcularAbundanciaRelativa();
                realizarCalculos();
                compararPuntos();
                JsfUtil.addCallbackParam("puntoMuestreo");
                RequestContext.getCurrentInstance().execute(
                        "PF('dlg3').hide();");
            }
        }
        else{
            JsfUtil.addMessageError("El formato del código '"+codigo+"' es incorrecto. Por favor, verifíquelo.");
        }

    }

    /**
     * Agregar registro especies
     */
    public void agregarRegistroEspecies() {

        if (!existeRegistro()){

            //Seteamos el tipo de registro de especie.
            if(this.hasRegisterType){

                if(this.tipo.equalsIgnoreCase("ictiofauna")){
                    this.registroEspeciesEIA.setRegistroDirecto(false);
                }
                if(this.tipoRegistroEspecie!=null){
                    if(this.tipoRegistroEspecie.getOtro()){
                        this.registroEspeciesEIA.setTipoRegistro(this.tipoRegistroEspecie.getNombreOtro());
                    } else {
                        this.registroEspeciesEIA.setTipoRegistro(this.tipoRegistroEspecie.getNombre());
                    }
                    this.registroEspeciesEIA.setOtro(this.tipoRegistroEspecie.getOtro());

                    if (!this.isEditingRegistro) {
                        this.listaRegistroEspecies.add(this.registroEspeciesEIA);
                        //Actualizamos el número de registros de especies del punto.
                        actualizarCantRegEspecies(this.registroEspeciesEIA, true);
                    }

                    realizarCalculos();
                    compararPuntos();
                    if(!this.tipo.equalsIgnoreCase("Macroinvertebrados") &&
                            !this.tipo.equalsIgnoreCase("Limnofauna"))
                        this.calcularAbundanciaRelativa();
                    JsfUtil.addCallbackParam("registroEspecies");

                    RequestContext.getCurrentInstance().execute(
                            "PF('dlg4').hide();");
                }
                else{
                    JsfUtil.addMessageError("El campo 'Tipo' es requerido.");
                }

            }
            else{
                if (!this.isEditingRegistro) {
                    this.listaRegistroEspecies.add(this.registroEspeciesEIA);
                    //Actualizamos el número de registros de especies del punto.
                    actualizarCantRegEspecies(this.registroEspeciesEIA, true);
                }

                realizarCalculos();
                compararPuntos();
                if(!this.tipo.equalsIgnoreCase("Macroinvertebrados") &&
                        !this.tipo.equalsIgnoreCase("Limnofauna"))
                    this.calcularAbundanciaRelativa();
                JsfUtil.addCallbackParam("registroEspecies");

                RequestContext.getCurrentInstance().execute(
                        "PF('dlg4').hide();");
            }
        }
        else{
            JsfUtil.addMessageError("Ya existe esta especie asociada al punto de muestreo '"+
                    this.registroEspeciesEIA.getPuntosMuestreo().getNombrePunto()+"' .");
        }

    }

    private boolean existeRegistro() {
        boolean exist = false;
        int pos = 0;
        RegistroEspeciesEIA registroEspeciesEIA = new RegistroEspeciesEIA();
        while (!exist && pos<this.listaRegistroEspecies.size()){
            registroEspeciesEIA = this.listaRegistroEspecies.get(pos);
            if(registroEspeciesEIA!=this.registroEspeciesEIA && registroEspeciesEIA.getPuntosMuestreo().getNombrePunto().equalsIgnoreCase
                    (this.registroEspeciesEIA.getPuntosMuestreo().getNombrePunto()) &&
                    registroEspeciesEIA.getNombreCientifico().equalsIgnoreCase
                            (this.registroEspeciesEIA.getNombreCientifico()) ){
                exist = true;
            }
            else{
                pos++;
            }
        }

        return exist;
    }

    private boolean isValidCode(String code){
        boolean valid = true;

        //MA001, MA002, ...
        String typeId = code.substring(0,2);
        String typeId2Compare;
        if(code.length()!=5){
            valid = false;
        }
        else{
            if (this.tipo.equalsIgnoreCase("Macroinvertebrados")) {
                typeId2Compare = "EA";
            }
            else{
                typeId2Compare = this.tipo.substring(0,2);
            }

            if (!typeId.equalsIgnoreCase(typeId2Compare)){
                valid = false;
            }
            else{
                try{
                    if(Double.parseDouble(code.substring(2))==0)
                        valid = false;
                }catch (NumberFormatException e){
                    valid = false;
                }
            }
        }


        return valid;
    }

    private void calcularAbundanciaRelativa(){
        List<Integer> individuosPorEspecies;
        int total;
        float porcentaje;
        listaAbundancia = new ArrayList<PuntosMuestreoEIA>();

        for (PuntosMuestreoEIA ptoMuestreoEIA: this.listaPuntosMuestreo){

            total = 0;
            individuosPorEspecies = new ArrayList<Integer>();
            ptoMuestreoEIA.initAbundancia();
            for (RegistroEspeciesEIA regEsp : this.listaRegistroEspecies) {
                if(regEsp.getPuntosMuestreo().getNombrePunto().equalsIgnoreCase(ptoMuestreoEIA.getNombrePunto())){
                    individuosPorEspecies.add(regEsp.getIndividuos());
                    total+=regEsp.getIndividuos();
                }
            }
            /*
            <5% de las especies registradas=Raro
            Del  5% al 10% de las especies registrdas= Poco Común
            Del 11% al 20% de las especies registradas=Común
            >20% de las especies regiistradas=Abundantes */
            for (int individuosPorEspecie:individuosPorEspecies){
                porcentaje = (individuosPorEspecie*100)/total;
                if(porcentaje<5 )
                    ptoMuestreoEIA.setRaras(ptoMuestreoEIA.getRaras() + 1);
                else if (porcentaje>=5 && porcentaje<=10)
                    ptoMuestreoEIA.setPocoComunes(ptoMuestreoEIA.getPocoComunes() + 1);
                else if (porcentaje>10 && porcentaje<=20)
                    ptoMuestreoEIA.setComunes(ptoMuestreoEIA.getComunes() + 1);
                else//>20%
                    ptoMuestreoEIA.setAbundantes(ptoMuestreoEIA.getAbundantes() + 1);
            }
            listaAbundancia.add(ptoMuestreoEIA);
        }
    }

    /***
     * Método que realiza el cálculo de: DnR, DmR e IVI
     * y setea los valores al registro de especies en cuestión.
     */
    public void realizarCalculos() {

        //Cálculo de pi (DnR y DmR solo para flora)
        double simpson, shannon, noIndiv, sumatoriaNoIndiv, sumAB, pi, sumatoriaSumAB = sumAB = 0 ;

            for (RegistroEspeciesEIA regEsp : this.listaRegistroEspecies) {

                noIndiv = regEsp.getIndividuos();
                sumatoriaNoIndiv = noIndiv;
                if(this.tipo.equalsIgnoreCase("Flora")){
                    sumAB = regEsp.getAb();
                    sumatoriaSumAB = sumAB;
                }

                String nombrePunto = regEsp.getPuntosMuestreo().getNombrePunto();
                for (RegistroEspeciesEIA regEsp2 : this.listaRegistroEspecies) {
                    if (listaRegistroEspecies.indexOf(regEsp) != listaRegistroEspecies.indexOf(regEsp2) &&
                            regEsp2.getPuntosMuestreo().getNombrePunto().equals(nombrePunto)) {
                        sumatoriaNoIndiv += regEsp2.getIndividuos();
                        if(this.tipo.equalsIgnoreCase("Flora")){
                            sumatoriaSumAB += regEsp2.getAb();
                        }
                    }

                }
                pi = noIndiv / sumatoriaNoIndiv;
                if(this.tipo.equalsIgnoreCase("Flora")){
                    double dnr = pi * 100;
                    double dmr = (sumAB / sumatoriaSumAB) * 100;
                    //Guardamos los valores
                    regEsp.setDnr(redondear(dnr, 3)); //DnR
                    regEsp.setDmr(redondear(dmr, 3));//DmR
                    regEsp.setIvi(redondear(dnr + dmr, 3));//IVI
                }

                //Guardamos los valores
                regEsp.setPi(pi);
                regEsp.setPi2(pi * pi);
                regEsp.setLnPi(Math.log(pi));
                this.listaRegistroEspecies.set(listaRegistroEspecies.indexOf(regEsp), regEsp);
            }

        //Calculamos los valores de Shannon, Simpson, Riqueza y Abundancia
        this.listaAnalisisDiversidad = new ArrayList<AnalisisDiversidadPtoMuestreoEIA>();
        PuntosMuestreoEIA puntoMuestreo;
        AnalisisDiversidadPtoMuestreoEIA analisisDiv;
        RegistroEspeciesEIA regEsp2;
        List<String> puntoCalculados = new ArrayList<String>();
        double riqueza, abundancia;
        for (RegistroEspeciesEIA regEsp : this.listaRegistroEspecies) {
            puntoMuestreo = regEsp.getPuntosMuestreo();
            analisisDiv = new AnalisisDiversidadPtoMuestreoEIA();
            riqueza = 1;
            abundancia = regEsp.getIndividuos();

            if (!puntoCalculados.contains(puntoMuestreo.getNombrePunto())) {
                simpson = regEsp.getPi2();

                shannon = (regEsp.getPi() * regEsp.getLnPi());
                //shannon = regEsp.getLnPi() * regEsp.getLnPi();
                if(shannon<0){
                    shannon = -1*shannon;
                }
                double shannon2;
                for (int i = (this.listaRegistroEspecies.indexOf(regEsp) + 1);
                     i < this.listaRegistroEspecies.size(); i++) {
                    regEsp2 = this.listaRegistroEspecies.get(i);
                    if (regEsp2.getPuntosMuestreo().getNombrePunto().equals(puntoMuestreo.getNombrePunto())) {
                        simpson += regEsp2.getPi2();
                        shannon2 = (regEsp2.getPi() * regEsp2.getLnPi());
                        if(shannon2<0){
                            shannon2 = shannon2*-1;
                        }
                        shannon += shannon2;
                        //shannon += (-1*(regEsp2.getPi() * regEsp2.getLnPi()));
                        riqueza++;
                        abundancia += regEsp2.getIndividuos();
                    }
                }
                puntoCalculados.add(puntoMuestreo.getNombrePunto());

                analisisDiv.setPuntosMuestreoEIA(puntoMuestreo);
                analisisDiv.setSimpson(redondear(simpson,3));
                analisisDiv.setShannon(redondear(shannon,3));
                analisisDiv.setRiqueza(redondear(riqueza,3));
                analisisDiv.setAbundancia(redondear(abundancia,3));
                this.listaAnalisisDiversidad.add(analisisDiv);
            }
        }

    }

    public static double redondear(double numero, int digitos) {
        int cifras = (int) Math.pow(10, digitos);
        return Math.rint(numero * cifras) / cifras;
    }

    public void compararPuntos() {
        this.listaComparacionPtos = new ArrayList<AnalisisComparacionPtosMuestreoEIA>();
        Map<String, ArrayList<String>> combinacionesPuntos = new HashMap<String, ArrayList<String>>();
        String key, value;
        ArrayList<String> values;
        int pos = 0;
        int size = listaPuntosMuestreo.size() - 1;
        for (PuntosMuestreoEIA puntoMuestreoA : listaPuntosMuestreo) {
            if (puntoMuestreoA.getCantRegEspecies() > 0) {
                key = puntoMuestreoA.getNombrePunto();
                if (!combinacionesPuntos.containsKey(key) && pos < size) {
                    values = new ArrayList<String>();
                    for (PuntosMuestreoEIA puntoMuestreoB : listaPuntosMuestreo) {
                        value = puntoMuestreoB.getNombrePunto();
                        if (puntoMuestreoB.getCantRegEspecies() > 0 && !key.equals(value) && !combinacionesPuntos.containsKey(value)) {
                            values.add(value);
                        }

                    }
                    combinacionesPuntos.put(key, values);
                }
            }

        }

        for (String muestraA : combinacionesPuntos.keySet()) {

            values = combinacionesPuntos.get(muestraA);

            for (String muestraB : values) {
                AnalisisComparacionPtosMuestreoEIA comp = new AnalisisComparacionPtosMuestreoEIA();
                String puntoMuestreo;
                Map<String, Integer> especiesPuntoA = new HashMap<String, Integer>();
                Map<String, Integer> especiesPuntoB = new HashMap<String, Integer>();
                int cantInd, suma = 0;
                int totalIndividuosPtoA = 0;
                int totalIndividuosPtoB = 0;
                for (RegistroEspeciesEIA regEsp : this.listaRegistroEspecies) {
                    puntoMuestreo = regEsp.getPuntosMuestreo().getNombrePunto();
                    if (muestraA.equals(puntoMuestreo)) {
                        if (!especiesPuntoA.containsKey(regEsp.getNombreCientifico())) {
                            especiesPuntoA.put(regEsp.getNombreCientifico(), regEsp.getIndividuos());
                            totalIndividuosPtoA += regEsp.getIndividuos();
                        } else {
                            cantInd = especiesPuntoA.get(regEsp.getNombreCientifico());
                            suma += (cantInd + regEsp.getIndividuos());
                            especiesPuntoA.put(regEsp.getNombreCientifico(), suma);
                            totalIndividuosPtoA += suma;
                        }
                    } else {
                        if (muestraB.equals(regEsp.getPuntosMuestreo().getNombrePunto())) {
                            if (!especiesPuntoB.containsKey(regEsp.getNombreCientifico())) {
                                especiesPuntoB.put(regEsp.getNombreCientifico(), regEsp.getIndividuos());
                                totalIndividuosPtoB += regEsp.getIndividuos();
                            } else {
                                cantInd = especiesPuntoB.get(regEsp.getNombreCientifico());
                                suma += (cantInd + regEsp.getIndividuos());
                                especiesPuntoB.put(regEsp.getNombreCientifico(), suma);
                                totalIndividuosPtoB += suma;
                            }

                        }
                    }
                }

                double jaccard, sorensen;
                int noEspeciesCompartidas = 0;
                int sumatoriaIndEspeciesCompartidas = 0;
                for (String especie : especiesPuntoA.keySet()) {

                    if (especiesPuntoB.containsKey(especie)) {
                        noEspeciesCompartidas++;
                        sumatoriaIndEspeciesCompartidas += (especiesPuntoA.get(especie) + especiesPuntoB.get(especie));
                    }
                }
                jaccard = noEspeciesCompartidas * 1.0 / (especiesPuntoA.size() + especiesPuntoB.size() - noEspeciesCompartidas);
                sorensen = sumatoriaIndEspeciesCompartidas * 1.0 / (totalIndividuosPtoA + totalIndividuosPtoB);

                comp.setPuntoMuestreoA(muestraA);
                comp.setPuntoMuestreoB(muestraB);
                comp.setJaccard(redondear(jaccard,3));
                comp.setSorensen(redondear(sorensen,3));

                if(!isAdded(comp) || this.listaComparacionesPtosEliminadas.contains(comp)){
                    comp.setAgregado(false);
                }
                else{
                    comp.setAgregado(true);
                }
                this.listaComparacionPtos.add(comp);
            }
        }
    }

    private boolean isAdded(AnalisisComparacionPtosMuestreoEIA analisisComparacionPtos){

        boolean added = false;
        int pos = 0;
        int size = this.listaComparacionesPtos.size();
        if(!this.listaComparacionesPtos.isEmpty()){
            while(!added && pos<size){
                if(analisisComparacionPtos.getPuntoMuestreoA().equalsIgnoreCase(this.listaComparacionesPtos.get(pos).getMuestraA().getNombrePunto()) &&
                        analisisComparacionPtos.getPuntoMuestreoB().equalsIgnoreCase(this.listaComparacionesPtos.get(pos).getMuestraB().getNombrePunto())){
                    added = true;
                }
                pos++;
            }
        }
         return added;
    }


    /**
     * Elimina un punto muestreo
     *
     * @param puntoMuestreoEia punto de muestreo
     */
    public void removerPuntoMuestreo(PuntosMuestreoEIA puntoMuestreoEia) {
        this.isEditing = false;

        if (this.eliminarPuntos(puntoMuestreoEia)) {
            this.listaPuntosMuestreo.remove(puntoMuestreoEia);
            this.listaPuntosMuestreoEliminados.add(puntoMuestreoEia);

            listaCodigosPtosMuestreo.remove(puntoMuestreoEia.getNombrePunto());

            //Eliminamos todos los registros de especies asociados a este punto.
            /*for (RegistroEspeciesEIA reg : listaRegistroEspecies) {
                if (reg.getPuntosMuestreo().getNombrePunto().equalsIgnoreCase(puntoMuestreoEia.getNombrePunto())) {
                    requirecalcular = false;//Indicamos que no se debe calcular la abundancia
                    removerRegistroEspecies(reg);
                }
            }*/
            //Eliminamos los registros de análisis de diversidad asociados al punto de muestreo.
            eliminarAnalisisDivPM(puntoMuestreoEia.getNombrePunto());
            //Calculamos la abundancia
            if(!this.tipo.equalsIgnoreCase("Macroinvertebrados") &&
                    !this.tipo.equalsIgnoreCase("Limnofauna"))
            calcularAbundanciaRelativa();
            realizarCalculos();
            compararPuntos();
            requirecalcular = true;
        }
    }

    private void eliminarAnalisisDivPM(String nombrePtoMuestreo) {
        int i = 0;
        boolean found = false;
        while (!found && i < this.listaAnalisisDiversidad.size()) {

            if (this.listaAnalisisDiversidad.get(i).getPuntosMuestreoEIA().getNombrePunto().equals(nombrePtoMuestreo)) {
                found = true;
                this.listaAnalisisDiversidad.remove(i);
            } else
                i++;
        }
    }

    public boolean eliminarPuntos(PuntosMuestreoEIA puntoMuestreoEia) {
        for (RegistroEspeciesEIA reg : listaRegistroEspecies) {
            if (reg.getPuntosMuestreo().equals(puntoMuestreoEia)) {
                JsfUtil.addMessageError(JsfUtil.MESSAGE_EXISTEN_ESPECIES);
                return false;
            }
        }
        return true;
    }

    /**
     * Elimina un registro de especies
     *
     * @param registroEspeciesEIA registro de especies
     */
    public void removerRegistroEspecies(RegistroEspeciesEIA registroEspeciesEIA) {
        this.isEditing = false;
        this.listaRegistroEspecies.remove(registroEspeciesEIA);
        this.listaRegistroEspeciesEliminados.add(registroEspeciesEIA);
        //Calculamos la abundancia
        if (requirecalcular) {
            int i = 0;
            boolean found = false;
            while (!found && i < this.listaRegistroEspecies.size()) {
                if (this.listaRegistroEspecies.get(i).getPuntosMuestreo().getNombrePunto().equals(
                        registroEspeciesEIA.getPuntosMuestreo().getNombrePunto())) {
                    found = true;
                } else
                    i++;
            }
            //Si no existen otros registros de especies asociados al mismo punto de muestreo que el registro en cuestión.
            if (!found) {
                //eliminamos el registro de análisis de diversisdad asociado a dicho punto de muestreo
                eliminarAnalisisDivPM(registroEspeciesEIA.getPuntosMuestreo().getNombrePunto());
            }
            //Actualizamos la cantidad de registros de especies asociadas al punto de muestreo.
            actualizarCantRegEspecies(registroEspeciesEIA, false);
            realizarCalculos();
            compararPuntos();
            if(!this.tipo.equalsIgnoreCase("Macroinvertebrados") &&
                    !this.tipo.equalsIgnoreCase("Limnofauna"))
            calcularAbundanciaRelativa();
        }

    }

    private void actualizarCantRegEspecies(RegistroEspeciesEIA registroEspeciesEIA, boolean increment) {

        int size = this.listaPuntosMuestreo.size();
        int i = 0;
        boolean found = false;
        PuntosMuestreoEIA puntoMuestreo = new PuntosMuestreoEIA();
        while (!found && i < size) {
            puntoMuestreo = listaPuntosMuestreo.get(i);
            if (puntoMuestreo.getNombrePunto().equals(registroEspeciesEIA.getPuntosMuestreo().getNombrePunto())) {
                found = true;
                int cantRegespecies = puntoMuestreo.getCantRegEspecies();
                if (increment) {
                    puntoMuestreo.setCantRegEspecies(++cantRegespecies);
                } else {
                    puntoMuestreo.setCantRegEspecies(--cantRegespecies);
                }
                this.listaPuntosMuestreo.set(i, puntoMuestreo);
            } else {
                i++;
            }
        }
    }


    /**
     * Metodo para redireccionar
     */
    public void cancelar() {
        JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/medioBiotico/fauna.jsf");
    }

    /*
     * Crear la entidad documento y subir al alfresco
     */
    public boolean salvarDocumento() {
        try {

            if (documento != null && documento.getContenidoDocumento() != null) {

                documento.setIdTable(this.estudioImpactoAmbiental.getId());
                documento.setDescripcion(this.tipoDocumentoSistema.name());
                documento.setEstado(true);
                documentosFacade.guardarDocumentoAlfresco(this.estudioImpactoAmbiental
                                .getProyectoLicenciamientoAmbiental().getCodigo(), Constantes.CARPETA_EIA, 0L, documento,
                        this.tipoDocumentoSistema, null);
            } else {
                JsfUtil.addMessageError(JsfUtil.MESSAGE_NO_EXISTE_DOCUMENTO);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;

    }

    public StreamedContent getStreamContent() throws Exception {
        DefaultStreamedContent content = null;
        try {

            if (documento != null && documento.getNombre() != null && documento.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
                content.setName(documento.getNombre());
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

        } catch (Exception exception) {
            LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }

    public StreamedContent getStreamContentAnexo(Documento documentoDescargar) throws Exception {
        DefaultStreamedContent content = null;

        documentoDescargar = this.descargarAlfresco(documentoDescargar);

        try {
            if (documentoDescargar != null && documentoDescargar.getNombre() != null
                    && documentoDescargar.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(
                        documentoDescargar.getContenidoDocumento()));
                content.setName(documentoDescargar.getNombre());
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

        } catch (Exception exception) {
            LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }

    private void cargarAdjuntosEIA() throws CmisAlfrescoException {


        List<Documento> documentosXEIA = documentosFacade.documentoXTablaIdXIdDoc(this.estudioImpactoAmbiental.getId(),
                this.tipoDocumentoSistema.name(), this.tipoDocumentoSistema);

        if (documentosXEIA.size() > 0) {

            this.documento = documentosXEIA.get(0);
            this.descargarAlfresco(this.documento);
            this.existeDocumentoAdjunto = true;

        }
    }

    public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
        byte[] documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null)
            documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
        if (documentoContenido != null)
            documento.setContenidoDocumento(documentoContenido);
        return documento;
    }

    public void uploadListenerDocumentos(FileUploadEvent event) {
        documento = this.uploadListener(event, this.tipoDocumentoSistema.name());
        this.existeDocumentoAdjunto = true;
        // Entidad que tiene el documento adjuntado
        // getPuntoRecuperacion().setCertificadoCompatibilidadUsoSuelos(documento);
    }

    private Documento uploadListener(FileUploadEvent event,
                                     String nombreTabla) {

        String nombre = event.getFile().getFileName();
        int dot = nombre.lastIndexOf('.');
        String extension = (dot == -1) ? "" : nombre.substring(dot + 1);

        byte[] contenidoDocumento = event.getFile().getContents();
        String mime = event.getFile().getContentType();
        Documento documento = crearDocumento(contenidoDocumento, nombreTabla, extension,
                mime);
        documento.setNombre(nombre);
        return documento;
    }

    /**
     * Crea el documento
     *
     * @param contenidoDocumento arreglo de bytes
     * @param nombreTabla        Clase a la cual se va a ligar al documento
     * @param extension          extension del archivo
     * @return Objeto de tipo Documento
     */
    public Documento crearDocumento(byte[] contenidoDocumento, String nombreTabla, String extension, String mime) {
        Documento documento = new Documento();
        documento.setContenidoDocumento(contenidoDocumento);
        documento.setNombreTabla(nombreTabla);
        documento.setIdTable(0);
        documento.setExtesion("." + extension);

        documento.setMime(mime);
        return documento;
    }

    public void validateAdjunto(FacesContext context, UIComponent validate,
                                Object value) {
        StringBuilder functionJs = new StringBuilder();
        List<FacesMessage> mensajes = new ArrayList<>();
        if (this.documento.getNombre() == null) {// NO VALIDO
            FacesMessage mensajeValidacionDocumento = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "El campo 'Documento' es requerido.",
                    null);
            mensajes.add(mensajeValidacionDocumento);
            functionJs
                    .append("highlightComponent('frmDatos:subirDocumento');");
        } else {
            functionJs
                    .append("removeHighLightComponent('frmDatos:subirDocumento');");
        }
        RequestContext.getCurrentInstance().execute(functionJs.toString());
        if (!mensajes.isEmpty())
            throw new ValidatorException(mensajes);
    }

    public void removerComparacion(ComparacionPuntosMuestreoEIA comp){

        if(comp.getId()!=null){
            comp.setEstado(false);
            this.listaComparacionesPtosEliminadas.add(comp);
        }
        this.listaComparacionesPtos.remove(comp);
        boolean found = false;
        int pos = 0;
        while (!found){

            if (listaComparacionPtos.get(pos).getPuntoMuestreoA().equalsIgnoreCase(comp.getMuestraA().getNombrePunto()) &&
                    listaComparacionPtos.get(pos).getPuntoMuestreoB().equalsIgnoreCase(comp.getMuestraB().getNombrePunto())){
                found = true;
                listaComparacionPtos.get(pos).setAgregado(false);
            }
            else{pos++;}

        }
    }

    public void agregarComparacion(AnalisisComparacionPtosMuestreoEIA analisisComparacionPtos){
        ComparacionPuntosMuestreoEIA comp = new ComparacionPuntosMuestreoEIA();
        PuntosMuestreoEIA muestraA = null;
        PuntosMuestreoEIA muestraB = null;
        PuntosMuestreoEIA muestra;

        int pos = 0;
        int size = listaPuntosMuestreo.size();
        while((muestraA == null || muestraB == null) && pos<size){
            muestra = listaPuntosMuestreo.get(pos);
            if(analisisComparacionPtos.getPuntoMuestreoA().equalsIgnoreCase(muestra.getNombrePunto())){
                muestraA = muestra;
            }
            else{
                if (analisisComparacionPtos.getPuntoMuestreoB().equalsIgnoreCase(muestra.getNombrePunto())){
                    muestraB = muestra;
                }

            }
            pos++;
        }

        comp.setMuestraA(muestraA);
        comp.setMuestraB(muestraB);
        comp.setJaccard(analisisComparacionPtos.getJaccard());
        comp.setSorensen(analisisComparacionPtos.getSorensen());
        comp.setEstado(true);
        this.listaComparacionesPtos.add(comp);
        analisisComparacionPtos.setAgregado(true);

    }

    public void setRegistroDirecto(boolean registroDirecto){
        this.registroDirecto = registroDirecto;
        this.registroEspeciesEIA.setRegistroDirecto(registroDirecto);
    }


    public void setTipoRegistroEspecie(TipoRegistroEspecie tipoRegistroEspecie){
        this.tipoRegistroEspecie = tipoRegistroEspecie;
    }

    public List<ComparacionPuntosMuestreoEIA> getListaComparacionesPtos(){
        return this.listaComparacionesPtos==null? new ArrayList<ComparacionPuntosMuestreoEIA>():this.listaComparacionesPtos;
    }

    public Date getCurrentDate() {
        return new Date();
    }


}
