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
@Table(name = "modality_coprocessing_waste_process", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "mowp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "mowp_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "mowp_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mowp_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mowp_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mowp_status = 'TRUE'")
@NamedQueries(@NamedQuery(name = ModalidadCoprocesamientoDesechoProcesar.LISTAR_POR_ID, query = "SELECT a FROM ModalidadCoprocesamientoDesechoProcesar a WHERE a.estado = TRUE"))
public class ModalidadCoprocesamientoDesechoProcesar extends EntidadAuditable {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadDesechoProcesar.";
	public static final String LISTAR_POR_ID = PAQUETE_CLASE + "obtenerPorId";

	public ModalidadCoprocesamientoDesechoProcesar() {
	}

	public ModalidadCoprocesamientoDesechoProcesar(DesechoPeligroso desecho, ModalidadCoprocesamiento modalidad) {
		this.desecho = desecho;
		this.modalidadCoprocesamiento = modalidad;
		this.idModalidadCoprocesamiento = this.modalidadCoprocesamiento.getId();

	}

	@Id
	@Column(name = "mowp_id")
	@SequenceGenerator(name = "MODALITY_WASTE_PROCESS_GENERATOR", sequenceName = "seq_mowp_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_WASTE_PROCESS_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wada_id")
	@ForeignKey(name = "waste_dangerous_modality_coprocessing_waste_fk")
	@Getter
	@Setter
	private DesechoPeligroso desecho;

	@Getter
	@Setter
	@Column(name = "wada_id", insertable = false, updatable = false)
	private Integer idDesecho;

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

	@Column(name = "mowp_quantity_maxima_requested")
	@Getter
	@Setter
	private Double cantidadMaximaDesechosRequerido;

	@Column(name = "mowp_chemical_composition")
	@Getter
	@Setter
	private String composicionQuimica;

	@Column(name = "mowp_flammability_point")
	@Getter
	@Setter
	private Double puntoInflamabilidad;

	@Column(name = "mowp_boiling_point")
	@Getter
	@Setter
	private Double puntoEbullicion;

	@Column(name = "mowp_moisture_percentage")
	@Getter
	@Setter
	private Double porcentajeHumedad;

	@Column(name = "mowp_chlorine_percentage")
	@Getter
	@Setter
	private Double porcentajeCloro;

	@Column(name = "mowp_heavy_metal")
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
