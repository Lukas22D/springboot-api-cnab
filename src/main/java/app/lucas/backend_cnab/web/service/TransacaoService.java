package app.lucas.backend_cnab.web.service;

import org.springframework.stereotype.Service;

import app.lucas.backend_cnab.web.model.TransacaoReport;
import app.lucas.backend_cnab.web.repository.TransacaoRepository;

import java.util.List;
import java.util.LinkedHashMap;
import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    public TransacaoService(TransacaoRepository transacao) {
        this.transacaoRepository = transacao;
    }	

    /*
     * 1. Listar totais de transações por nome da loja
     * 2. Cria um LinkedHashMap para manter a ordem de inserção, passando o nome da loja como chave e um objeto TransacaoReport como valor
     * 3. Itera sobre as transações, calculando o valor da transação e adicionando ao reportMap
     * 4. Retorna uma lista com os valores do reportMap
     */

    public List<TransacaoReport> listTotaisTransacoesPorNomeDaLoja(){   
        var transacoes = transacaoRepository.findAllByOrderByNomeLojaAscIdDesc();
        var reportMap = new LinkedHashMap<String, TransacaoReport>();


        transacoes.forEach(transacao -> {

            String nomeLoja = transacao.nomeLoja();
            BigDecimal valor = transacao.valor();
            /*
             * O metodo compute recebe a chave e uma função que recebe a chave e o valor atual,
             * se o valor atual for nulo, cria um novo objeto TransacaoReport com o nome da loja e o valor da transação
             * se o valor atual não for nulo, adiciona o valor da transação ao total e a transação ao array de transações
             * retorna o objeto TransacaoReport
             */
            reportMap.compute(nomeLoja, (key, existingReport) ->{
                var report = (existingReport != null) ? existingReport : new TransacaoReport(key, BigDecimal.ZERO, new ArrayList<>());
                
                return report.addTotal(valor).addTransacao(transacao.withValor(valor));
            });
        });

        return new ArrayList<>(reportMap.values());
    }
    
}
