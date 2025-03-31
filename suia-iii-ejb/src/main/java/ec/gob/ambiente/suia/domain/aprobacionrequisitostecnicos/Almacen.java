/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import ec.gob.ambiente.suia.domain.TipoIluminacion;
import ec.gob.ambiente.suia.domain.TipoLocal;
import ec.gob.ambiente.suia.domain.TipoMaterialConstruccion;
import ec.gob.ambiente.suia.domain.TipoVentilacion;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import org.hibernate.annotations.ForeignKey;

/**
 * <b> Clase entidad para los almacenes. </b>
 *
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 * <p>
 * [$Author: Javier Lucero $, $Date: 09/06/2015 $]
 * </p>
 */
@Entity
@Table(name = "warehouse", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "waho_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "waho_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "waho_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "waho_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "waho_user_update"))})
@NamedQueries(@NamedQuery(name = Almacen.LISTAR_POR_APROBACION_REQUISITOS_TECNICOS,query = "SELECT a FROM Almacen a WHERE a.idAprobacionRequisitosTecnicos = :idAprobacionRequisitosTecnicos AND a.estado = TRUE"))
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "waho_status = 'TRUE'")
public class Almacen extends EntidadAuditable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.Almacen.";
    public static final String LISTAR_POR_APROBACION_REQUISITOS_TECNICOS = PAQUETE_CLASE + "obtenerPorIdidAprobacionRequisitosTecnicos";

    @Id
    @Column(name = "waho_id")
    @SequenceGenerator(name = "WAREHOUSE_GENERATOR", sequenceName = "seq_waho_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WAREHOUSE_GENERATOR")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @Column(name = "waho_identification")
    private String identificacion;

    @Getter
    @Setter
    @Column(name = "waho_long")
    private Double largo;

    @Getter
    @Setter
    @Column(name = "waho_width")
    private Double ancho;

    @Getter
    @Setter
    @Column(name = "waho_height")
    private Double altura;

    @Getter
    @Setter
    @Column(name = "waho_amount")
    private Double cantidad;

    @Getter
    @Setter
    @Column(name = "waho_capacity_tanks")
    private Double capacidadFosas;

    @Getter
    @Setter
    @Column(name = "fire_extinction")
    private String extincionIncendio;

    @Getter
    @Setter
    @Column(name = "waho_Security_measures")
    private String medidadSeguridad;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "loty_id")
    @ForeignKey(name = "fk_waho_id_loty_id")
    private TipoLocal tipoLocal;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "vety_id")
    @ForeignKey(name = "fk_waho_id_vety_id")
    private TipoVentilacion tipoVentilacion;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "ilty_id")
    @ForeignKey(name = "fk_waho_id_ilty_id")
    private TipoIluminacion tipoIluminacion;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "bmty_id")
    @ForeignKey(name = "fk_waho_id_bmty_id")
    private TipoMaterialConstruccion tipoMaterialConstruccion;

    @Getter
    @Setter
    @OneToMany(mappedBy = "almacen", fetch = FetchType.EAGER)
    private List<AlmacenRecepcion> almacenRecepciones;
    
    @Getter
    @Setter
    @ManyToOne()
    @JoinColumn(name = "apte_id")
    @ForeignKey(name = "fk_waho_id_id_approval_requirement_apte_id")
    private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;
    
    @Getter
    @Setter
    @Column(name = "apte_id", insertable = false, updatable = false)
    private Integer idAprobacionRequisitosTecnicos;
    
    @Getter
    @Setter
    @Transient
    private int indice;
    
    @Getter
    @Setter
    @Transient
    private boolean editar;
    
    @Getter
    @Setter
    @Transient
    private boolean modificado;

    /**
     * @param id
     */
    public Almacen(Integer id) {
        super();
        this.id = id;
    }

    public Almacen() {

    }

}
