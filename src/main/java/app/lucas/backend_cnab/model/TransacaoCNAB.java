package app.lucas.backend_cnab.model;

import java.math.BigDecimal;

public record TransacaoCNAB(
    Integer tipo,
    String data,
    BigDecimal valor,
    Long cpf,
    String cartao,
    String hora,
    String donoLoja,
    String nomeLoja
) {
    
}
