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

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
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
@Table(name = "modality_incineration_waste_process", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "miwp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "miwp_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "miwp_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "miwp_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "miwp_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "miwp_status = 'TRUE'")
@NamedQueries(@NamedQuery(name = ModalidadIncineracionDesechoProcesar.LISTAR_POR_ID, query = "SELECT a FROM ModalidadIncineracionDesechoProcesar a WHERE a.estado = TRUE"))
public class ModalidadIncineracionDesechoProcesar extends EntidadAuditable {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionDesechoProcesar.";
	public static final String LISTAR_POR_ID = PAQUETE_CLASE + "obtenerPorId";
	
	public ModalidadIncineracionDesechoProcesar() {
	}

	public ModalidadIncineracionDesechoProcesar(DesechoPeligroso desecho, ModalidadIncineracion modalidad) {
		this.desecho = desecho;
		this.modalidadIncineracion = modalidad;
		this.idModalidadIncineracion = modalidad.getId();

	}

	@Id
	@Column(name = "miwp_id")
	@SequenceGenerator(name = "MODALITY_INCINERATION_WASTE_PROCESS_GENERATOR", sequenceName = "seq_miwp_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_INCINERATION_WASTE_PROCESS_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wada_id")
	@ForeignKey(name = "modality_incineration_waste_process_coprocessing_waste_fk")
	@Getter
	@Setter
	private DesechoPeligroso desecho;

	@Getter
	@Setter
	@Column(name = "wada_id", insertable = false, updatable = false)
	private Integer idDesecho;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "moin_id")
	@ForeignKey(name = "modality_incineration_waste_process_modality_incineration_fk")
	@Getter
	@Setter
	private ModalidadIncineracion modalidadIncineracion;

	@Getter
	@Setter
	@Column(name = "moin_id", insertable = false, updatable = false)
	private Integer idModalidadIncineracion;

	@Column(name = "miwp_quantity_maxima_requested")
	@Getter
	@Setter
	private Double cantidadMaximaDesechosRequerido;

	@Column(name = "miwp_chemical_composition")
	@Getter
	@Setter
	private String composicionQuimica;

	@Column(name = "miwp_flammability_point")
	@Getter
	@Setter
	private Double puntoInflamabilidad;

	@Column(name = "miwp_boiling_point")
	@Getter
	@Setter
	private Double puntoEbullicion;

	@Column(name = "miwp_moisture_percentage")
	@Getter
	@Setter
	private Double porcentajeHumedad;

	@Column(name = "miwp_chlorine_percentage")
	@Getter
	@Setter
	private Double porcentajeCloro;

	@Column(name = "miwp_heavy_metal")
	@Getter
	@Setter
	private Double metalPesado;

	@Transient
	@Getter
	@Setter
	private int indice;

	public boolean isRegistroCompleto() {
		return composicionQuimica != null;
	}

}
