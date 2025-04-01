package com.lyra.caps.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "_user")
public class User  {

  @Id
  @GeneratedValue
  private Integer id;

  private String email;
  private String password;
  

}
