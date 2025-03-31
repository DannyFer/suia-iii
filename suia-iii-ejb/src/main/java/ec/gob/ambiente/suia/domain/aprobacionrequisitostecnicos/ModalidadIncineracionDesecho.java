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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import javax.persistence.Transient;

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
@Table(name = "modality_incineration_waste", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "moiw_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "moiw_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "moiw_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "moiw_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "moiw_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "moiw_status = 'TRUE'")
@NamedQueries(@NamedQuery(name = ModalidadIncineracionDesecho.LISTAR_POR_ID, query = "SELECT a FROM ModalidadIncineracionDesecho a WHERE a.idModalidadIncineracion=:idModalidad and a.estado = TRUE"))
public class ModalidadIncineracionDesecho extends EntidadAuditable {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionDesecho.";
	public static final String LISTAR_POR_ID = PAQUETE_CLASE + "obtenerPorId";

	public ModalidadIncineracionDesecho() {
	}

	public ModalidadIncineracionDesecho(DesechoPeligroso desecho, ModalidadIncineracion modalidad) {
		this.desecho = desecho;
		this.modalidadIncineracion = modalidad;
		this.idModalidadIncineracion = modalidad.getId();

	}

	@Id
	@Column(name = "moiw_id")
	@SequenceGenerator(name = "MODALITY_INCINERATION_WASTE_GENERATOR", sequenceName = "seq_moiw_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_INCINERATION_WASTE_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wada_id")
	@ForeignKey(name = "waste_dangerous_modality_incineration_waste_fk")
	@Getter
	@Setter
	private DesechoPeligroso desecho;

	@Getter
	@Setter
	@Column(name = "wada_id", insertable = false, updatable = false)
	private Integer idDesecho;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "moin_id")
	@ForeignKey(name = "modality_incineration_waste_modality_incineration_fk")
	@Getter
	@Setter
	private ModalidadIncineracion modalidadIncineracion;

	@Getter
	@Setter
	@Column(name = "moin_id", insertable = false, updatable = false)
	private Integer idModalidadIncineracion;

	@Column(name = "moiw_yearly_capacity")
	@Getter
	@Setter
	private Double capacidadAnual;

	@Transient
	@Getter
	@Setter
	private int indice;

	public boolean isRegistroCompleto() {
		return (capacidadAnual != null) && capacidadAnual != 0D;
	}

}
