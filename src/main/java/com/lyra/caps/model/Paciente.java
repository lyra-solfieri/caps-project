package com.lyra.caps.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lyra.caps.dto.PacienteDto;
import com.lyra.caps.model.enums.Genero;
import com.lyra.caps.model.enums.Raca;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "paciente")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String nome;

    @Email
    private String email;

    @Size(min = 3, max = 100)
    private String telefone;

    @CPF
    private String cpf;

    private String rg;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evolucao> evolucoes = new ArrayList<>();

    private Genero genero;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    @Size(min = 3, max = 100)
    private String nomeMae;
    private String profissaoMae;

    private String codIBGEMunicipio;

    @Size(min = 3, max = 100)
    private String nomePai;
    private String profissaoPai;

    @Size(min = 3, max = 100)
    private String profissao;

    private Long numeroCartaoSUS;
    private Raca raca;
    private Long numeroProntuario;
    private boolean infantojuvenil;
    private boolean emSituacaoSocioeconomica;
    private String contextoVulnerabilidadeSocioeconomica;
    private String etniaIndigena;
    private String nacionalidade;
    private String escolaridade;
    private String rendaFamiliar;
    private String auxilioOuBeneficioSocial;
    private String responsavelLegal;
    private String parentescoResponsavelLegal;
    private String telefoneResponsavelLegal;
    private String profissaoResponsavelLegal;
    private String instituicaoEnsino;
    private String religiao;
    private String unidadeBasicaSaude;
    private String agenteSaude;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Atendimento> atendimentos = new ArrayList<>();

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anamnese> anamneses = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paciente)) return false;
        return id != null && id.equals(((Paciente) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

   public static Paciente fromDto(PacienteDto dto) {
    if (dto == null) return null;

        Paciente paciente = new Paciente();
        paciente.setId(dto.getId());
        paciente.setNome(dto.getNome());
        paciente.setEmail(dto.getEmail());
        paciente.setTelefone(dto.getTelefone());
        paciente.setCpf(dto.getCpf());
        paciente.setRg(dto.getRg());
        paciente.setGenero(dto.getGenero());
        paciente.setDataNascimento(dto.getDataNascimento());
        paciente.setNomeMae(dto.getNomeMae());
        paciente.setProfissaoMae(dto.getProfissaoMae());
        paciente.setCodIBGEMunicipio(dto.getCodIBGEMunicipio());
        paciente.setNomePai(dto.getNomePai());
        paciente.setProfissaoPai(dto.getProfissaoPai());
        paciente.setProfissao(dto.getProfissao());
        paciente.setNumeroCartaoSUS(dto.getNumeroCartaoSUS());
        paciente.setRaca(dto.getRaca());
        paciente.setNumeroProntuario(dto.getNumeroProntuario());
        paciente.setInfantojuvenil(dto.isInfantojuvenil());
        paciente.setEmSituacaoSocioeconomica(dto.isEmSituacaoSocioeconomica());
        paciente.setContextoVulnerabilidadeSocioeconomica(dto.getContextoVulnerabilidadeSocioeconomica());
        paciente.setEtniaIndigena(dto.getEtniaIndigena());
        paciente.setNacionalidade(dto.getNacionalidade());
        paciente.setEscolaridade(dto.getEscolaridade());
        paciente.setRendaFamiliar(dto.getRendaFamiliar());
        paciente.setAuxilioOuBeneficioSocial(dto.getAuxilioOuBeneficioSocial());
        paciente.setResponsavelLegal(dto.getResponsavelLegal());
        paciente.setParentescoResponsavelLegal(dto.getParentescoResponsavelLegal());
        paciente.setTelefoneResponsavelLegal(dto.getTelefoneResponsavelLegal());
        paciente.setProfissaoResponsavelLegal(dto.getProfissaoResponsavelLegal());
        paciente.setInstituicaoEnsino(dto.getInstituicaoEnsino());
        paciente.setReligiao(dto.getReligiao());
        paciente.setUnidadeBasicaSaude(dto.getUnidadeBasicaSaude());
        paciente.setAgenteSaude(dto.getAgenteSaude());

        if (dto.getEndereco() != null) {
            Endereco endereco = new Endereco();
            endereco.setCep(dto.getEndereco().getCep());
            endereco.setLogradouro(dto.getEndereco().getLogradouro());
            endereco.setNumero(dto.getEndereco().getNumero());
            endereco.setComplemento(dto.getEndereco().getComplemento());
            endereco.setBairro(dto.getEndereco().getBairro());
            endereco.setCidade(dto.getEndereco().getCidade());
            endereco.setEstado(dto.getEndereco().getEstado());
            endereco.setPais(dto.getEndereco().getPais());
            paciente.setEndereco(endereco);
        }

        return paciente;
    }
    
}
