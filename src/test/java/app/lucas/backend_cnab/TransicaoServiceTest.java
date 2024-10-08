package app.lucas.backend_cnab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import app.lucas.backend_cnab.web.model.Transacao;
import app.lucas.backend_cnab.web.repository.TransacaoRepository;
import app.lucas.backend_cnab.web.service.TransacaoService;

@SpringBootTest
public class TransicaoServiceTest {

    @InjectMocks
    private  TransacaoService transacaoService;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Test
    public void testListTotaisTransacoesPorNomeDaLoja() {
        // AAA - Arrange, Act, Assert
        // Arrange
        final String LojaA = "Loja A", LojaB = "Loja B";

        var transacao1 = new Transacao(1L, 1, new Date(System.currentTimeMillis()), BigDecimal.valueOf(100), 12345678900L,
                "12345678900", new Time(System.currentTimeMillis()), "Dono Loja A", LojaA);

        var transacao2 = new Transacao(2L, 1, new Date(System.currentTimeMillis()), BigDecimal.valueOf(50), 12345678900L,
                "12345678900", new Time(System.currentTimeMillis()), "Dono Loja B", LojaB);
        
        var transacao3 = new Transacao(3L, 1, new Date(System.currentTimeMillis()), BigDecimal.valueOf(75), 12345678900L,
                "12345678900", new Time(System.currentTimeMillis()), "Dono Loja A", LojaA);

        var mockTransacoes = List.of(transacao1, transacao2, transacao3);
        /*
         * Mockando o retorno do método findAllByOrderByNomeLojaAscIdDesc para retornar
         * Quando o método listTotaisTransacoesPorNomeDaLoja for chamado, ele irá retornar a lista mockada
         */
        when(transacaoRepository.findAllByOrderByNomeLojaAscIdDesc()).thenReturn(mockTransacoes);

        var reports = transacaoService.listTotaisTransacoesPorNomeDaLoja();

        // Assert
        // Verificar se as transações foram agrupadas corretamente por nome da loja
        assertEquals(2, reports.size());

        // Verificar se os totais das transações estão corretos, totalizando as transações por loja
        reports.forEach( report -> {
            if(report.nomeDaLoja().equals(LojaA)){
                assertEquals(2, report.transacoes().size());
                assertEquals(BigDecimal.valueOf(175), report.total());
                assertTrue(report.transacoes().contains(transacao1));
                assertTrue(report.transacoes().contains(transacao3));
            } else if(report.nomeDaLoja().equals(LojaB)){
                assertEquals(1, report.transacoes().size());
                assertEquals(BigDecimal.valueOf(50), report.total());
                assertTrue(report.transacoes().contains(transacao2));
            }
        });

    }

}
