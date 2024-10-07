package app.lucas.backend_cnab.web.repository;

import org.springframework.data.repository.CrudRepository;

import app.lucas.backend_cnab.web.model.Transacao;

import java.util.List;


public interface TransacaoRepository extends CrudRepository<Transacao, Long>{

    // select * from transacao order by nome_da_loja asc, id desc;
    List<Transacao> findAllByOrderByNomeLojaAscIdDesc();
    
}
