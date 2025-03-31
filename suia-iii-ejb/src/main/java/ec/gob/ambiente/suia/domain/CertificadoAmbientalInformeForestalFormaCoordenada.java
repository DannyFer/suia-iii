package ec.gob.ambiente.suia.domain;

import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the environmental_certificate_report_forest database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = CertificadoAmbientalInformeForestalFormaCoordenada.FIND_BY_REPORT, query = "SELECT c FROM CertificadoAmbientalInformeForestalFormaCoordenada c WHERE c.estado=true and c.informeForestal.id = :idInformeForestal order by 1 desc") })
@Entity
@Table(name = "environmental_certificate_report_forest_shapes", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "crfs_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "crfs_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "crfs_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "crfs_user_create")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "crfs_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "crfs_status = 'TRUE'")
public class CertificadoAmbientalInformeForestalFormaCoordenada extends EntidadAuditable {

	private static final long serialVersionUID = -3599588896716274522L;

	public static final String FIND_BY_REPORT = "ec.com.magmasoft.business.domain.CertificadoAmbientalInformeForestalFormaCoordenada.byReport";

	@Getter
	@Setter
	@Id
	@Column(name = "crfs_id")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
    @Getter
    @Setter
    @JoinColumn(name = "ecrf_id", referencedColumnName = "ecrf_id")
    @ManyToOne
    @ForeignKey(name = "crfs_ecrf_fkey")
    private CertificadoAmbientalInformeForestal informeForestal;
    
    
    @Getter
    @Setter
    @JoinColumn(name = "shty_id", referencedColumnName = "shty_id")
    @ManyToOne
    @ForeignKey(name = "shty_id")
    private TipoForma tipoForma;
    
    
	@Getter
	@Setter
	@OneToMany(mappedBy = "formasInformeForestalCA")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Coordenada> coordenadas;

	
}