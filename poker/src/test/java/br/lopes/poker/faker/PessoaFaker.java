package br.lopes.poker.faker;

import br.lopes.poker.domain.Pessoa;

public class PessoaFaker {

    public enum EPessoa {
        DIOGO("Diogo", "diogolopes@email.com", "11995070939"), //
        EDUARDO("Eduardo", "eduardolopes@email.com", "11990000000"), //
        SONIA("Sonia", "sonialopes@email.com", "11990000001"), //
        MARCINHO("Marcinho", "marcinho@email.com", "11990000002"), //
        FILIPE("Filipe", "filipe@email.com", "11990000003"),//
        MAEDA("Maeda", "maeda@email.com", "11990000004");
        
        private final String nome;
        private final String email;
        private final String telefone;
        
        private EPessoa(final String nome, final String email, final String telefone) {
            this.nome = nome;
            this.email = email;
            this.telefone = telefone;
        }
        
        public String getNome() {
            return nome;
        }
        
    }
    
    public static Pessoa get(final EPessoa ePessoa) {
        final Pessoa pessoa = new Pessoa();
        pessoa.setNome(ePessoa.nome);
        pessoa.setEmail(ePessoa.email);
        pessoa.setTelefone(ePessoa.telefone);
        return pessoa;
    }
}
