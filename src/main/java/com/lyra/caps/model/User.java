package com.lyra.caps.model;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.lyra.caps.model.enums.Role;

import jakarta.persistence.*;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;
  private String password;
  private String nome;

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

  private String profissaoUsuario;

  public User(String email, String password, Role role) {
    this.email = email;
    this.password = password;
    this.role = role;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (this.role == Role.ADMIN) {
      return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

    } else{
      return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
  }

  @Override
  public String getUsername() {
    return this.email;
  }

}
