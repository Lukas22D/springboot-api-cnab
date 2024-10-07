package app.lucas.backend_cnab.web.controller;

import java.util.List;

import app.lucas.backend_cnab.web.model.TransacaoReport;
import app.lucas.backend_cnab.web.service.TransacaoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("transacao")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping
    List<TransacaoReport> listAll() {
        return transacaoService.listTotaisTransacoesPorNomeDaLoja();
    }
    
}
