package com.lyra.caps.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
@Table(name = "paciente")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private String cpf;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    private String genero;
    private String dataNascimento;
    private String nomeMae;
    private String nomePai;
    private String profissao;
    private String numeroCartaoSUS;
    private String raca;
    private Long numeroProntuario;
    private boolean infantojuvenil;
    private boolean emSituacaoSocioeconomica;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<Atendimento> atendimentos = new ArrayList<>();
    

}
