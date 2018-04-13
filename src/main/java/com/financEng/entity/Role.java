package com.financEng.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table( name = "roles" )
public class Role {

	@Id
	@GeneratedValue
	@Column(name = "ID", unique=true, nullable=false)
	private Long id;

	@Column(name = "ROLE", unique=true, nullable=false)
	private String role;

	@Column(name = "ROLE_Desc")
	private String roleDesc;

    @Column(name = "ROLE_CreationDate")
    private Date creationDate;

	@ManyToMany( mappedBy = "roles")
	private Set<User> users = new HashSet<User>();

	private Role(){}

	public Role(String role){
		this.role=role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", role='" + role + '\'' +
                ", roleDesc='" + roleDesc + '\'' +
                ", creationDate=" + creationDate +
                ", users=" + users +
                '}';
    }
}
