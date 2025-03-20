/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.util.Date;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author christian
 */
@NamedQueries({
		@NamedQuery(name = MatrizFactorImpacto.LISTAR_POR_MATRIZ, query = "SELECT a FROM MatrizFactorImpacto a WHERE a.idMatrizAmbientalMineria = :idMatriz AND a.estado = true"),
		@NamedQuery(name = MatrizFactorImpacto.LISTAR_POR_IMPACTO_AMBIENTAL, query = "SELECT a FROM MatrizFactorImpacto a WHERE a.idImpactoAmbientalPma = :idImpactoAmbientalPma AND a.estado = true AND a.idRegistroOriginal is null"),
		@NamedQuery(name = MatrizFactorImpacto.LISTAR_TODO_POR_IMPACTO_AMBIENTAL, query = "SELECT a FROM MatrizFactorImpacto a WHERE a.idImpactoAmbientalPma = :idImpactoAmbientalPma AND a.estado = true order by id"),
		@NamedQuery(name = MatrizFactorImpacto.LISTAR_TODO_POR_IMPACTO_MINERIA, query = "SELECT a FROM MatrizFactorImpacto a WHERE a.idMatrizAmbientalMineria = :idMatrizAmbientalMineria AND a.estado = true order by id")})
@Entity
@Table(name = "matrix_impact_factor", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "mifa_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mifa_status = 'TRUE'")
public class MatrizFactorImpacto extends EntidadBase {

	private static final long serialVersionUID = 7295700965765194767L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_MATRIZ = PAQUETE + "MatrizFactorImpacto.listarPorMatriz";
	public static final String LISTAR_POR_IMPACTO_AMBIENTAL = PAQUETE + "MatrizFactorImpacto.listarPorImpactoAmbiental";
	public static final String LISTAR_TODO_POR_IMPACTO_AMBIENTAL = PAQUETE + "MatrizFactorImpacto.listarTodoPorImpactoAmbiental";
	public static final String LISTAR_TODO_POR_IMPACTO_MINERIA = PAQUETE + "MatrizFactorImpacto.listarTodoPorImpactoMineria";

	@Id
	@Column(name = "mifa_id")
	@SequenceGenerator(name = "MATRIX_IMPACT_FACTOR_GENERATOR", sequenceName = "matrix_impact_factor_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MATRIX_IMPACT_FACTOR_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "miem_id")
	@Getter
	@Setter
	private MatrizAmbientalMineria matrizAmbientalMineria;

	@Getter
	@Setter
	@Column(name = "miem_id", insertable = false, updatable = false)
	private Integer idMatrizAmbientalMineria;

	@ManyToOne
	@JoinColumn(name = "faen_id")
	@Getter
	@Setter
	private ImpactoAmbientalPma impactoAmbientalPma;

	@Getter
	@Setter
	@Column(name = "faen_id", insertable = false, updatable = false)
	private Integer idImpactoAmbientalPma;

	@ManyToOne
	@JoinColumn(name = "fafa_id")
	@Getter
	@Setter
	private FactorPma factorPma;

	@Getter
	@Setter
	@Column(name = "fafa_id", insertable = false, updatable = false)
	private Integer idFactor;

	@ManyToOne
	@JoinColumn(name = "faim_id")
	@Getter
	@Setter
	private ImpactoPma impactoPma;

	@Getter
	@Setter
	@Column(name = "faim_id", insertable = false, updatable = false)
	private Integer idImpacto;

	@Transient
	@Getter
	@Setter
	private int indice;
	
	@Getter
	@Setter
	@Column(name = "mifa_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "mifa_historical_date")
	private Date fechaHistorico;
	
	@Getter
    @Setter
    @Transient
    private boolean nuevoEnModificacion = false;
    
    @Getter
    @Setter
    @Transient
    private boolean historialModificaciones = false;

	public MatrizFactorImpacto() {
	}

	public MatrizFactorImpacto(Integer id) {
		this.id = id;
	}

	//para validar si se han realizado cambios en el objeto para guardar historial
	public boolean equalsObject(Object obj) {
		if (obj == null)
			return false;
		MatrizFactorImpacto base = (MatrizFactorImpacto) obj;
		if (this.getId() == null && base.getId() == null)
			return super.equals(obj);
		if (this.getId() == null || base.getId() == null)
			return false;
		
		if (this.getId().equals(base.getId()) && 
				this.getFactorPma().getId().equals(base.getFactorPma().getId()) && 
				this.getImpactoPma().getId().equals(base.getImpactoPma().getId())) {
			if (this.getIdImpactoAmbientalPma() != null && this.getIdImpactoAmbientalPma().equals(base.getIdImpactoAmbientalPma()))
				return true;
			if (this.getIdMatrizAmbientalMineria() != null && this.getIdMatrizAmbientalMineria().equals(base.getIdMatrizAmbientalMineria()))
				return true;
			return false;
		} else
			return false;
	}
}
