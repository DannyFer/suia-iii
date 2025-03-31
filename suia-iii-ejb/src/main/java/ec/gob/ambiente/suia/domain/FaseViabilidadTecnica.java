/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author christian
 */
@Table(name = "technical_viability_study_phases", schema = "suia_iii")
@Entity
@NamedQueries({
		@NamedQuery(name = FaseViabilidadTecnica.LISTAR_POR_GRUPO, query = "SELECT f FROM FaseViabilidadTecnica f WHERE f.grupo = :grupo ORDER BY f.nombre"),
		@NamedQuery(name = FaseViabilidadTecnica.LISTAR_POR_ID_PADRE, query = "SELECT f FROM FaseViabilidadTecnica f WHERE f.idPadre = :idPadre ORDER BY f.nombre") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tvsp_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvsp_status = 'TRUE'")
public class FaseViabilidadTecnica extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String DIAGNOSTICO_FACTIBILIDAD = "Diagnóstico y Factibilidad";
	public static final String GESTION_INTEGRAL = "Gestión Integral";
	public static final String LISTAR_POR_GRUPO = PAQUETE + "FaseViabilidadTecnica.listarPorGrupo";
	public static final String LISTAR_POR_ID_PADRE = PAQUETE + "FaseViabilidadTecnica.listarPorIdPadre";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "FaseViabilidadTecnica_id_generator", initialValue = 1, sequenceName = "seq_tvsp_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FaseViabilidadTecnica_id_generator")
	@Column(name = "tvsp_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "tvsp_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "tvsp_group")
	private String grupo;

	@Getter
	@Setter
	@Column(name = "tvsp_parent_id")
	private Integer idPadre;

	@OneToMany(mappedBy = "faseViabilidadTecnica")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvdm_status = 'TRUE'")
	@Getter
	@Setter
	private List<FaseViabilidadTecnicaDiagnostico> fasesViabilidadTecnicaDiagnostico;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return nombre;
	}
}
