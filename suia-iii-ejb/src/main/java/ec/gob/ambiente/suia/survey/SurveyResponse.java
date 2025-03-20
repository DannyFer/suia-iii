package ec.gob.ambiente.suia.survey;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "survey_response", schema = "suia_survey")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "SurveyResponse.findAll", query = "SELECT s FROM SurveyResponse s"),
		@NamedQuery(name = "SurveyResponse.findBySrvrspId", query = "SELECT s FROM SurveyResponse s WHERE s.srvrspId = :srvrspId"),
		@NamedQuery(name = "SurveyResponse.findBySrvrspProject", query = "SELECT s FROM SurveyResponse s WHERE s.srvrspProject = :srvrspProject"),
		@NamedQuery(name = "SurveyResponse.findBySrvrspUserType", query = "SELECT s FROM SurveyResponse s WHERE s.srvrspUserType = :srvrspUserType"),
		@NamedQuery(name = "SurveyResponse.findBySrvrspPersonType", query = "SELECT s FROM SurveyResponse s WHERE s.srvrspPersonType = :srvrspPersonType"),
		@NamedQuery(name = "SurveyResponse.findBySrvrspSuggestions", query = "SELECT s FROM SurveyResponse s WHERE s.srvrspSuggestions = :srvrspSuggestions"),
		@NamedQuery(name = "SurveyResponse.findBySrvrspApp", query = "SELECT s FROM SurveyResponse s WHERE s.srvrspApp = :srvrspApp"),
		@NamedQuery(name = "SurveyResponse.findBySrvrspStatus", query = "SELECT s FROM SurveyResponse s WHERE s.srvrspStatus = :srvrspStatus"),
		@NamedQuery(name = "SurveyResponse.findBySrvrspSafUserType", query = "SELECT s FROM SurveyResponse s WHERE s.srvrspSafUserType = :srvrspSafUserType"),
		@NamedQuery(name = "SurveyResponse.findBySrvrspCreateUser", query = "SELECT s FROM SurveyResponse s WHERE s.srvrspCreateUser = :srvrspCreateUser"),
		@NamedQuery(name = "SurveyResponse.findBySrvrspCreateDate", query = "SELECT s FROM SurveyResponse s WHERE s.srvrspCreateDate = :srvrspCreateDate"),
		@NamedQuery(name = "SurveyResponse.findBySrvrspUpdateUser", query = "SELECT s FROM SurveyResponse s WHERE s.srvrspUpdateUser = :srvrspUpdateUser"),
		@NamedQuery(name = "SurveyResponse.findBySrvrspUpdateDate", query = "SELECT s FROM SurveyResponse s WHERE s.srvrspUpdateDate = :srvrspUpdateDate") })
public class SurveyResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "srvrsp_id")
	private Integer srvrspId;
	@Size(max = 255)
	@Column(name = "srvrsp_project")
	private String srvrspProject;
	@Size(max = 255)
	@Column(name = "srvrsp_user_type")
	private String srvrspUserType;
	@Size(max = 255)
	@Column(name = "srvrsp_person_type")
	private String srvrspPersonType;
	@Size(max = 2147483647)
	@Column(name = "srvrsp_suggestions")
	private String srvrspSuggestions;
	@Size(max = 255)
	@Column(name = "srvrsp_app")
	private String srvrspApp;
	@Column(name = "srvrsp_status")
	private Boolean srvrspStatus;
	@Size(max = 255)
	@Column(name = "srvrsp_saf_user_type")
	private String srvrspSafUserType;
	@Size(max = 255)
	@Column(name = "srvrsp_create_user")
	private String srvrspCreateUser;
	@Column(name = "srvrsp_create_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date srvrspCreateDate;
	@Size(max = 255)
	@Column(name = "srvrsp_update_user")
	private String srvrspUpdateUser;
	@Column(name = "srvrsp_update_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date srvrspUpdateDate;
	
	public SurveyResponse() {
	}

	public SurveyResponse(Integer srvrspId) {
		this.srvrspId = srvrspId;
	}

	public Integer getSrvrspId() {
		return srvrspId;
	}

	public void setSrvrspId(Integer srvrspId) {
		this.srvrspId = srvrspId;
	}

	public String getSrvrspProject() {
		return srvrspProject;
	}

	public void setSrvrspProject(String srvrspProject) {
		this.srvrspProject = srvrspProject;
	}

	public String getSrvrspUserType() {
		return srvrspUserType;
	}

	public void setSrvrspUserType(String srvrspUserType) {
		this.srvrspUserType = srvrspUserType;
	}

	public String getSrvrspPersonType() {
		return srvrspPersonType;
	}

	public void setSrvrspPersonType(String srvrspPersonType) {
		this.srvrspPersonType = srvrspPersonType;
	}

	public String getSrvrspSuggestions() {
		return srvrspSuggestions;
	}

	public void setSrvrspSuggestions(String srvrspSuggestions) {
		this.srvrspSuggestions = srvrspSuggestions;
	}

	public String getSrvrspApp() {
		return srvrspApp;
	}

	public void setSrvrspApp(String srvrspApp) {
		this.srvrspApp = srvrspApp;
	}

	public Boolean getSrvrspStatus() {
		return srvrspStatus;
	}

	public void setSrvrspStatus(Boolean srvrspStatus) {
		this.srvrspStatus = srvrspStatus;
	}

	public String getSrvrspSafUserType() {
		return srvrspSafUserType;
	}

	public void setSrvrspSafUserType(String srvrspSafUserType) {
		this.srvrspSafUserType = srvrspSafUserType;
	}

	public String getSrvrspCreateUser() {
		return srvrspCreateUser;
	}

	public void setSrvrspCreateUser(String srvrspCreateUser) {
		this.srvrspCreateUser = srvrspCreateUser;
	}

	public Date getSrvrspCreateDate() {
		return srvrspCreateDate;
	}

	public void setSrvrspCreateDate(Date srvrspCreateDate) {
		this.srvrspCreateDate = srvrspCreateDate;
	}

	public String getSrvrspUpdateUser() {
		return srvrspUpdateUser;
	}

	public void setSrvrspUpdateUser(String srvrspUpdateUser) {
		this.srvrspUpdateUser = srvrspUpdateUser;
	}

	public Date getSrvrspUpdateDate() {
		return srvrspUpdateDate;
	}

	public void setSrvrspUpdateDate(Date srvrspUpdateDate) {
		this.srvrspUpdateDate = srvrspUpdateDate;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (srvrspId != null ? srvrspId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof SurveyResponse)) {
			return false;
		}
		SurveyResponse other = (SurveyResponse) object;
		if ((this.srvrspId == null && other.srvrspId != null)
				|| (this.srvrspId != null && !this.srvrspId
						.equals(other.srvrspId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ec.gob.ambiente.suia.survey.SurveyResponse[ srvrspId="
				+ srvrspId + " ]";
	}

}
