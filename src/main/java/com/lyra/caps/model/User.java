package com.lyra.caps.model;

import java.util.Calendar;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.lyra.caps.model.enums.Role;

import jakarta.persistence.*;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@Entity
@Table(name = "_user")
public class User  {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String email;
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;
  
  @Temporal(TemporalType.TIMESTAMP)
	private Calendar ultimoAcesso;

  @Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Calendar createdAt;

  @UpdateTimestamp
	private Calendar update_at;

  @ColumnDefault(value = "0")
	private Boolean bloqueado;

}
