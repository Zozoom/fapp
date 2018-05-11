package com.financEng.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.*;

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

    @Column(name = "FNAME", nullable=false, length = 45)
    private String fName;

    @Column(name = "SNAME", nullable=false, length = 45)
    private String sName;

    @Column(name = "Email", unique=true, nullable=false, length = 45)
	private String email;

    @Column(name = "Password", nullable=false, length = 60)
	private String password;

    @Column(name = "Gender", nullable=false)
    private String userGender;

    @JsonBackReference
    @ManyToMany( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name="user_id")},
            inverseJoinColumns = {@JoinColumn(name="role_id")}
    )
    private Set<Role> roles = new HashSet<Role>();

    @Column(name = "Activation_Code")
	private String activation;

    @Column(name = "Enabled", nullable=false)
	private Boolean enabled;

    @Column(name = "Locked", nullable=false)
    private Boolean locked;

    @Column(name = "LoggedIn", nullable=false)
    private Boolean loggedIn;

    @Column(name = "CreationDate", nullable=false)
    private Date creationDate;

    @Column(name = "LastLogin")
    private Date lastLogin;

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

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    /*==================================================================================================================
     || User Methods
     ==================================================================================================================*/

    /*********************************
     * Add Roles if is not exist
     * ******************************/
    public void addRoles(String roleName) {
		if (this.roles == null || this.roles.isEmpty())
			this.roles = new HashSet<>();
		this.roles.add(new Role(roleName));
	}

    /*********************************
     * Get My Roles in String
     * ******************************/
	public String getMyRole (Set<Role> roles) {
	    String myRole = "";
        for (Role role : roles) {
            myRole = role.getRole();
        }
        return myRole;
    }

    @Override
    public String toString() {
        return "User{" +
                " id: "+id+
                " fName='" + fName +
                ", sName='" + sName +
                ", email='" + email +
                ", loggedIn='" + loggedIn +
                ", lastLogin='" + lastLogin +
                ", userGender='" + userGender +
                ", roles=" + getMyRole(roles) +
                ", enabled=" + enabled +
                ", locked=" + locked +
                ", creationDate=" + creationDate +
                ", expireDate=" + expireDate +
                '}';
    }
}