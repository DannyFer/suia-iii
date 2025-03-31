/*
 * Copyright (c) 2014 MAGMASOFT (Innovando tecnologia)
 * Todos los derechos reservados.
 * Este software es confidencial y debe usarlo de acorde con los términos de uso.
 */

package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * Clase entidad para almacenar las instancias de procesos y tareas para las transacciones financieras
 *
 * @author Juan Carlos Gras
 * @version 1.0
 */

@Entity
@Table(name = "financial_transaction_log", catalog = "", schema = "suia_iii")
@NamedQuery(name = TransaccionFinancieraLog.FIND_ALL, query = "SELECT t FROM TransaccionFinancieraLog t")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "fitl_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "fitl_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "fitl_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fitl_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fitl_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fitl_status = 'TRUE'")
public class TransaccionFinancieraLog extends EntidadAuditable {
    private static final long serialVersionUID = 1L;
    public static final String FIND_ALL = "ec.com.magmasoft.business.domain.TransaccionFinancieraLog.findAll";

    @Id
    @SequenceGenerator(name = "FINANCIAL_TRANSACCTION_LOG_GENERATOR", sequenceName = "seq_fitl_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FINANCIAL_TRANSACCTION_LOG_GENERATOR")
    @Column(name = "fitl_id")
    @Getter
    @Setter
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "fitr_id")
    @ForeignKey(name = "finantial_transaction_log_pkey")
    @Getter
    @Setter
    private TransaccionFinanciera transaccionFinanciera;

    @Column(name = "task_id")
    @Getter
    @Setter
    private long idTarea;

    @Column(name = "task_name")
    @Getter
    @Setter
    private String nombreTarea;

    @Column(name = "processinstanceid")
    @Getter
    @Setter
    private long idInstanciaProceso;

    @Column(name = "processid")
    @Getter
    @Setter
    private String idProceso;

    @Column(name = "processname")
    @Getter
    @Setter
    private String nombreProceso;
}