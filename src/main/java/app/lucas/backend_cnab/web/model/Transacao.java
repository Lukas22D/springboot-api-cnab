package app.lucas.backend_cnab.web.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

public record Transacao(
    @Id Long id,
    Integer tipo,
    Date data,
    BigDecimal valor,
    Long cpf,
    String cartao,
    Time hora,
    @Column("DONO_LOJA") String donoLoja,
    @Column("NOME_LOJA") String nomeLoja
) {
    public Transacao withValor(BigDecimal valor) {
        return new Transacao(this.id(), this.tipo(), this.data(), valor, this.cpf(), this.cartao(), this.hora(), this.donoLoja(), this.nomeLoja());
    }

    public Transacao withData(String data) throws ParseException {
        var dateFormat = new SimpleDateFormat("yyyyMMdd");
        var date = dateFormat.parse(data);

        return new Transacao(
            this.id(),
            this.tipo(),
            new Date(date.getTime()),
            this.valor(),
            this.cpf(),
            this.cartao(),
            this.hora(),
            this.donoLoja(),
            this.nomeLoja()
        );
    }

    public Transacao withHora(String hora) throws ParseException {
        var timeFormat = new SimpleDateFormat("HHmmss");
        var time = timeFormat.parse(hora);

        return new Transacao(
            this.id(),
            this.tipo(),
            this.data(),
            this.valor(),
            this.cpf(),
            this.cartao(),
            new Time(time.getTime()),
            this.donoLoja(),
            this.nomeLoja()
        );
    }
}
