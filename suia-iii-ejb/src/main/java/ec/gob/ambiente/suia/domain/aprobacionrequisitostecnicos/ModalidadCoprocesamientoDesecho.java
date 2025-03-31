/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import javax.persistence.*;

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
@Table(name = "modality_coprocessing_waste", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "mocw_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "mocw_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "mocw_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mocw_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mocw_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mocw_status = 'TRUE'")
@NamedQueries(@NamedQuery(name = ModalidadCoprocesamientoDesecho.LISTAR_POR_ID, query = "SELECT a FROM ModalidadCoprocesamientoDesecho a WHERE a.modalidadCoprocesamiento.id = :idModalidad and a.estado = TRUE"))

public class ModalidadCoprocesamientoDesecho extends EntidadAuditable {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoDesecho.";
	public static final String LISTAR_POR_ID = PAQUETE_CLASE + "obtenerPorId";

	public ModalidadCoprocesamientoDesecho() {

	}

	public ModalidadCoprocesamientoDesecho(DesechoPeligroso desecho, ModalidadCoprocesamiento modalidad) {
		this.desecho = desecho;
		this.modalidadCoprocesamiento = modalidad;
		this.modalidadCoprocesamiento.getId();
	}

	@Id
	@Column(name = "mocw_id")
	@SequenceGenerator(name = "MODALITY_COPROCESSING_WASTE_GENERATOR", sequenceName = "seq_mocw_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_COPROCESSING_WASTE_GENERATOR")
	@Getter
	@Setter
	private Integer id;


	@Getter
	@Setter
	@JoinColumn(name = "wada_id" , referencedColumnName = "wada_id")
	@ForeignKey(name = "waste_dangerous_modality_coprocessing_waste_fk")
	@ManyToOne(fetch = FetchType.LAZY)
	private DesechoPeligroso desecho;


	/*@Getter
	@Setter
	@Column(name = "wada_id", insertable = false, updatable = false)
	private Integer idDesecho;*/

	@Getter
	@Setter
	@JoinColumn(name = "mocop_id", referencedColumnName = "mocop_id")
	@ManyToOne(fetch = FetchType.LAZY,  cascade = CascadeType.MERGE)
	@ForeignKey(name = "modality_coprocessing_waste_modality_coprocessing_fk")
	private ModalidadCoprocesamiento modalidadCoprocesamiento;

	/*@Getter
	@Setter
	@Column(name = "mocop_id", insertable = false, updatable = false)
	private Integer idModalidadCoprocesamiento;*/

	@Getter
	@Setter
	@Column(name = "mocw_yearly_capacity_process")
	private double capacidadAnualProceso = 0;

	@Column(name = "mocw_formulated_waste")
	@Getter
	@Setter
	private String desechoFormulado;

	@Column(name = "mocw_waste_used_directly")
	@Getter
	@Setter
	private String desechosEmpleadoDirectamente;

	@Column(name = "mocw_replacement_percentage_conventional_fuel")
	@Getter
	@Setter
	private double porcentajeDeSustitucionDeCombustibleConvencional = 0;

	@Transient
	@Getter
	@Setter
	private int indice;

	public boolean isRegistroCompleto() {
		return desechoFormulado != null && desechosEmpleadoDirectamente != null;
	}

}
