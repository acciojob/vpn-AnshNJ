package com.driver.model;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "service_provider")
public class ServiceProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    @ManyToOne
    @JoinColumn
    private Admin admin;

    @OneToMany(mappedBy = "service_provider" , cascade = CascadeType.ALL)
    private List<Country> countryList = new ArrayList<>();


    @ManyToMany(mappedBy = "service_provider" , cascade = CascadeType.ALL)
    private List<User> users;

    @OneToMany(mappedBy = "service_provider" , cascade = CascadeType.ALL)
    private List<Connection> connectionList;

    public ServiceProvider() {
    }

    public ServiceProvider(int id, String name, Admin admin, List<Country> countryList, List<User> users, List<Connection> connectionList) {
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.countryList = countryList;
        this.users = users;
        this.connectionList = connectionList;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public List<Country> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<Country> countryList) {
        this.countryList = countryList;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Connection> getConnectionList() {
        return connectionList;
    }

    public void setConnectionList(List<Connection> connectionList) {
        this.connectionList = connectionList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
