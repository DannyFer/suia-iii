/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Incluir aqui la descripcion de la clase. </b>
 *
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 * <p>
 * [$Author: Javier Lucero $, $Date: 24/09/2015 $]
 * </p>
 */
@Entity
@Table(name = "modality_coprocessing_formulation", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "mocf_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "mocf_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "mocf_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mocf_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mocf_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mocf_status = 'TRUE'")
@NamedQueries(
        @NamedQuery(name = ModalidadCoprocesamientoFormulacion.LISTAR_POR_ID, query = "SELECT a FROM ModalidadCoprocesamientoFormulacion a WHERE a.idModalidadCoprocesamiento=:idModalidad and a.estado =TRUE"))
public class ModalidadCoprocesamientoFormulacion extends EntidadAuditable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoFormulacion.";
    public static final String LISTAR_POR_ID = PAQUETE_CLASE + "obtenerPorId";

    @Id
    @Column(name = "mocf_id")
    @SequenceGenerator(name = "MODALITY_COPROCESSING_FORMULATION_GENERATOR", sequenceName = "seq_mocf_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_COPROCESSING_FORMULATION_GENERATOR")
    @Getter
    @Setter
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mocop_id")
    @ForeignKey(name = "modality_coprocessing_waste_modality_coprocessing_fk")
    @Getter
    @Setter
    private ModalidadCoprocesamiento modalidadCoprocesamiento;

    @Getter
    @Setter
    @Column(name = "mocop_id", insertable = false, updatable = false)
    private Integer idModalidadCoprocesamiento;

    @Column(name = "mocf_waste_type_formulation")
    @Getter
    @Setter
    private String tipoDesechoFormulacion;

    @Column(name = "mocf_moisture_percentage_formulation")
    @Getter
    @Setter
    private Double porcentajeHumedadFormulacion;

    @Column(name = "mocf_chlorine_percentage_formulation")
    @Getter
    @Setter
    private Double porcentajeCloroFormulacion;

    @Column(name = "mocf_heating_power_formulation")
    @Getter
    @Setter
    private Double poderCalorifico;

    @Transient
    @Getter
    @Setter
    private boolean editar;
    

    @Transient
    @Getter
    @Setter
    private Integer indice;

}
