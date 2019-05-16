package com.considlia.survey.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "user_id")
  private Long id;

  @NotEmpty
  @Email
  @Column(unique = true)
  private String email;
  //  @Column(name = "password")
  private String password;

  @NotBlank
  //  @Column(name = "username")
  private String username;

  @NotBlank
  //  @Column(name = "first_name")
  private String firstName;

  @NotBlank
  //  @Column(name = "last_name")
  private String lastName;

  @NotBlank private String role;

  // If User doesn't delete its child entity (Surveys) when User is deleted, we might need a Service
  // class for this.
  // Have not tried removing Users within Java/Hibernate, but removing them in SQL leaves the
  // surveys. (Might be redundant of me writing this but
  // I faced so many issues with this bit prior to this solution, deleting a Survey would delete the
  // User, caused by fetchtype eager,
  // SO I live this note here since this issue might come up in the future.
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
  private Set<Survey> surveys = new HashSet<>();

  /*
  Could add a private boolean if user is banned/blocked.
   */

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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Set<Survey> getSurveys() {
    return surveys;
  }

  public void setSurveys(Set<Survey> surveys) {
    this.surveys = surveys;
  }

  @Override
  public String toString() {
    return "User{" + "username='" + username + '\'' + '}';
  }
}