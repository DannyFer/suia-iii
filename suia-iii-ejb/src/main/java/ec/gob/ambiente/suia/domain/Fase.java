package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Table(name = "phases", schema = "suia_iii")
@Entity
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "phas_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "phas_status = 'TRUE'")
public class Fase extends EntidadBase {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "phas_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "phas_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "phas_code")
	private String codigo;

	@Getter
	@Setter
	@OneToMany(mappedBy = "fase")
	private List<CatalogoCategoriaSistema> catalogoCategorias;

	@Getter
	@Setter
	@OneToMany(mappedBy = "fase")
	private List<ZonaPorFase> zonaPorFases;

	@Getter
	@Setter
	@OneToMany(mappedBy = "fase")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<EtapasProyecto> etapasProyecto;
}

