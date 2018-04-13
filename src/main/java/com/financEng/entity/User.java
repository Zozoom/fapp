package com.financEng.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

@Entity
@Table( name="users" )
public class User {

    public enum Gender {

        Unknown("Unknown"),
        Male("Male"),
        Female("Female");

        private final String genderName;

        Gender(String genderName) {
            this.genderName = genderName;
        }

        public String getGenderType() {
            return genderName;
        }

    }

	@Id
    @GeneratedValue
    @Column(name = "ID", unique=true, nullable=false)
	private Long id;

    @Column(name = "FNAME", nullable=false)
    private String fName;

    @Column(name = "SNAME", nullable=false)
    private String sName;

    @Column(name = "Email", unique=true, nullable=false)
	private String email;

    @Column(name = "Password", nullable=false)
	private String password;

    @Column(name = "Gender", nullable=false)
    private String userGender;

    @ManyToMany( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name="user_id")},
            inverseJoinColumns = {@JoinColumn(name="role_id")}
    )
    private Set<Role> roles = new HashSet<Role>();

    @Column(name = "Activation_Code", nullable=false)
	private String activation;

    @Column(name = "Enabled", nullable=false)
	private Boolean enabled;

    @Column(name = "Locked", nullable=false)
    private Boolean locked;

    @Column(name = "CreationDate", nullable=false)
    private Date creationDate;

    @Column(name = "ExpireDate", nullable=false)
    private Date expireDate;

	public User() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getActivation() {
		return activation;
	}

	public void setActivation(String activation) {
		this.activation = activation;
	}

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public void addRoles(String roleName) {
		if (this.roles == null || this.roles.isEmpty()) 
			this.roles = new HashSet<>();
		this.roles.add(new Role(roleName));
	}

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fName='" + fName + '\'' +
                ", sName='" + sName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userGender=" + userGender +
                ", roles=" + roles +
                ", activation='" + activation + '\'' +
                ", enabled=" + enabled +
                ", locked=" + locked +
                ", creationDate=" + creationDate +
                ", expireDate=" + expireDate +
                '}';
    }
}