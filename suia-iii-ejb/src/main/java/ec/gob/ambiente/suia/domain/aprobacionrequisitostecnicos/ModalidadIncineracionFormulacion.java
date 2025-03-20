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
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 24/09/2015 $]
 *          </p>
 */
@Entity
@Table(name = "modality_incineration_formulation", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "moif_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "moif_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "moif_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "moif_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "moif_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "moif_status = 'TRUE'")
@NamedQueries(@NamedQuery(name = ModalidadIncineracionFormulacion.LISTAR_POR_ID, query = "SELECT a FROM ModalidadIncineracionFormulacion a WHERE a.idModalidadIncineracion=:idModalidad and a.estado =TRUE"))
public class ModalidadIncineracionFormulacion extends EntidadAuditable {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionFormulacion.";
	public static final String LISTAR_POR_ID = PAQUETE_CLASE + "obtenerPorId";

	@Id
	@Column(name = "moif_id")
	@SequenceGenerator(name = "MODALITY_INCINERATION_FORMULATION_GENERATOR", sequenceName = "seq_moif_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_INCINERATION_FORMULATION_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "moin_id")
	@ForeignKey(name = "modality_incineration_formulation_modality_incineration_fk")
	@Getter
	@Setter
	private ModalidadIncineracion modalidadIncineracion;

	@Getter
	@Setter
	@Column(name = "moin_id", insertable = false, updatable = false)
	private Integer idModalidadIncineracion;

	@Column(name = "moif_waste_type_formulation")
	@Getter
	@Setter
	private String tipoDesechoFormulacion;

	@Column(name = "moif_moisture_percentage_formulation")
	@Getter
	@Setter
	private Double porcentajeHumedadFormulacion;

	@Column(name = "moif_chlorine_percentage_formulation")
	@Getter
	@Setter
	private Double porcentajeCloroFormulacion;

	@Column(name = "moif_heating_power_formulation")
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
