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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * Clase entidad para almacenar las instituciones financieras
 * 
 * @author Juan Carlos Gras
 * @version 1.0
 */

@Entity
@Table(name = "financial_institution", catalog = "", schema = "public")
@NamedQueries({
		@NamedQuery(name = InstitucionFinanciera.FIND_ALL, query = "SELECT f FROM InstitucionFinanciera f"),
//		@NamedQuery(name = InstitucionFinanciera.FIND_MAIN, query = "SELECT f FROM InstitucionFinanciera f where f.institucionFinanciera.id IS NULL AND NOT f.codigoInstitucion='TEA001' ORDER BY f.id") })
		@NamedQuery(name = InstitucionFinanciera.FIND_MAIN, query = "SELECT f FROM InstitucionFinanciera f where f.institucionFinanciera.id IS NULL AND f.codigoInstitucion='BNF001' ORDER BY f.id") })
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "fini_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fini_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fini_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fini_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fini_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fini_status = 'TRUE'")
public class InstitucionFinanciera extends EntidadAuditable {
	private static final long serialVersionUID = 1L;
	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.InstitucionFinanciera.findAll";
	public static final String FIND_MAIN = "ec.com.magmasoft.business.domain.InstitucionFinanciera.findMain";

	@Id
	@SequenceGenerator(name = "FINANCIAL_INSTITUTION_GENERATOR", sequenceName = "fini_financial_institution_id_seq", schema = "public", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FINANCIAL_INSTITUTION_GENERATOR")
	@Column(name = "fini_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "fini_institution_name")
	@Getter
	@Setter
	private String nombreInstitucion;

	@Column(name = "fini_institution_code")
	@Getter
	@Setter
	private String codigoInstitucion;

	@Column(name = "fini_nut")
	@Getter
	@Setter
	private Boolean convenioNut;

	@Column(name = "fini_pay_online")
	@Getter
	@Setter
	private Boolean pagoEnLinea;

	@Getter
	@Setter
	@JoinColumn(name = "fini_parent_id", referencedColumnName = "fini_id")
	@ManyToOne(fetch = FetchType.LAZY)
	@ForeignKey(name = "fk_financial_institution_fini_idfinancial_institution_fini_id")
	private InstitucionFinanciera institucionFinanciera;

}
