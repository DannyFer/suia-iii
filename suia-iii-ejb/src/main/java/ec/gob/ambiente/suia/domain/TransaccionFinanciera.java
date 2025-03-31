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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * Clase entidad para almacenar las transacciones financieras
 * 
 * @author Juan Carlos Gras
 * @version 1.0
 */

@Entity
@Table(name = "financial_transaction", catalog = "", schema = "suia_iii")
@NamedQuery(name = TransaccionFinanciera.FIND_ALL, query = "SELECT t FROM TransaccionFinanciera t")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "fitr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fitr_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fitr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fitr_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fitr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fitr_status = 'TRUE'")
public class TransaccionFinanciera extends EntidadAuditable {
	private static final long serialVersionUID = 1L;
	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.TransaccionFinanciera.findAll";

	@Id
	@SequenceGenerator(name = "FINANCIAL_TRANSACCTION_GENERATOR", sequenceName = "fitr_financial_transaction_id_seq", schema = "public", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FINANCIAL_TRANSACCTION_GENERATOR")
	@Column(name = "fitr_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "fitr_transaction_number")
	@Getter
	@Setter
	private String numeroTransaccion;
	
	@Column(name = "fitr_transaction_amount")
	@Getter
	@Setter
	private Double montoTransaccion;
	
	@Column(name = "fitr_transaction_motive_id")
	@Getter
	@Setter
	private String identificadorMotivo;

	// bi-directional many-to-one association to financial_institution
	@ManyToOne
	@JoinColumn(name = "fini_id")
	@ForeignKey(name = "financial_institution_financial_transaction_fk")
	@Getter
	@Setter
	private InstitucionFinanciera institucionFinanciera;
	
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "projects_environmental_licensing_financial_transaction_fk")
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;
	
	@Transient
	@Getter
	@Setter
	private String tipoPagoProyecto;
	
	@ManyToOne
	@JoinColumn(name = "prco_id")
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoRcoa;
	
	@Getter
	@Setter
	@Column(name = "fitr_payment_type")
	private Integer tipoPago;
	
	@Getter
	@Setter
	@Column(name = "fitr_paid_value")
	private Double montoPago;

}