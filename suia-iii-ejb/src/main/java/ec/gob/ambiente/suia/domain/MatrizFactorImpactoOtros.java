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
 * @author ishmael
 */
@NamedQueries({ 
	@NamedQuery(name = MatrizFactorImpactoOtros.LISTAR_POR_FICHA_ID, query = "SELECT a FROM MatrizFactorImpactoOtros a WHERE a.idFicha = :idFicha AND a.estado = true AND a.idRegistroOriginal is null ORDER BY a.idActividadProcesoPma"),
	@NamedQuery(name = MatrizFactorImpactoOtros.LISTAR_TODO_POR_FICHA_ID, query = "SELECT a FROM MatrizFactorImpactoOtros a WHERE a.idFicha = :idFicha AND a.estado = true ORDER BY a.idActividadProcesoPma, a.id")})
@Entity
@Table(name = "other_matrix_impact_factor", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "oifa_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "oifa_status = 'TRUE'")
public class MatrizFactorImpactoOtros extends EntidadBase {

	private static final long serialVersionUID = 9174980565242791583L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_FICHA_ID = PAQUETE + "MatrizFactorImpactoOtros.listarPorFichaId";
	public static final String LISTAR_TODO_POR_FICHA_ID = PAQUETE + "MatrizFactorImpactoOtros.listarTodoPorFichaId";

	@Id
	@Column(name = "oifa_id")
	@SequenceGenerator(name = "MATRIX_IMPACT_FACTOR_OTHER_GENERATOR", sequenceName = "matrix_impact_factor_other_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MATRIX_IMPACT_FACTOR_OTHER_GENERATOR")
	@Getter
	@Setter
	private Integer id;

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
	@JoinColumn(name = "fapa_id")
	@Getter
	@Setter
	private ActividadProcesoPma actividadProcesoPma;

	@Column(name = "fapa_id", insertable = false, updatable = false)
	@Getter
	@Setter
	private Integer idActividadProcesoPma;

	@ManyToOne
	@JoinColumn(name = "cafa_id")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;

	@Column(name = "cafa_id", insertable = false, updatable = false)
	@Getter
	@Setter
	private Integer idFicha;

	@Column(name = "oifa_other_impact", length = 255)
	@Getter
	@Setter
	private String impactoOtros;

	@Transient
	@Getter
	@Setter
	private int indice;

	@Transient
	@Getter
	@Setter
	private boolean editar;
	
	@Getter
	@Setter
	@Column(name = "oifa_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "oifa_historical_date")
	private Date fechaHistorico;
	
	@Getter
    @Setter
    @Transient
    private boolean nuevoEnModificacion = false;
    
    @Getter
    @Setter
    @Transient
    private boolean historialModificaciones = false;

	public MatrizFactorImpactoOtros() {
	}

	public MatrizFactorImpactoOtros(Integer id) {
		this.id = id;
	}

	//para validar si se han realizado cambios en el objeto para guardar historial
	public boolean equalsObject(Object obj) {
		if (obj == null)
			return false;
		MatrizFactorImpactoOtros base = (MatrizFactorImpactoOtros) obj;
		if (this.getId() == null && base.getId() == null)
			return super.equals(obj);
		if (this.getId() == null || base.getId() == null)
			return false;
		
		if(this.getId().equals(base.getId()) && 
				this.getFactorPma().getId().equals(base.getFactorPma().getId()) && 
				this.getActividadProcesoPma().getId().equals(base.getActividadProcesoPma().getId()) && 
				this.getImpactoOtros().equals(base.getImpactoOtros()))
			return true;
		else
			return false;
	}
	
}
