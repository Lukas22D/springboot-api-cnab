package app.lucas.backend_cnab.job;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import app.lucas.backend_cnab.web.model.Transacao;
import app.lucas.backend_cnab.web.model.TransacaoCNAB;


@Configuration
public class BatchConfig {

    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    public BatchConfig(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
        this.transactionManager = transactionManager;
        this.jobRepository = jobRepository;
    }

    @Bean
    Job job(Step step) {
        /*
         *  JobBuilder é uma classe que ajuda a construir um Job.
         * O método start() define o primeiro Step do Job.
         * O método incrementer() define um incrementador para o Job.
         * O método build() constrói o Job.
         */
        return new JobBuilder("job", jobRepository)
            .start(step)
            .incrementer(new RunIdIncrementer())
            .build();
    }
    /*
     * O método step() cria um Step. que tem como entrada um ItemReader, um ItemProcessor e um ItemWriter.
     * ItemReader é uma interface que define um método read() que lê um item.
     * ItemProcessor é uma interface que define um método process() que processa um item<ItemQueVemDoReader> e retorna um item<ItemQueVaiParaOWriter>.
     * ItemWriter é uma interface que define um método write() que escreve um item.
     * 
     * O método chunk() define o tamanho do chunk. que recebe <T, S> onde T é o tipo do item que vem do ItemReader e S é o tipo do item que vai para o ItemWriter.
     */
    @Bean
    Step step(ItemReader<TransacaoCNAB> reader,  ItemProcessor<TransacaoCNAB, Transacao> processor, ItemWriter<Transacao> writer) {
        return new StepBuilder("step", jobRepository)
            .<TransacaoCNAB, Transacao>chunk(1000, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();    
    }
    /*
     *  StepScope é uma anotação que define que um Bean é um StepScope.
     *  Ou seja, o Bean é criado a cada Step. Conseguindo assim, passar parâmetros para o Bean.
     *  @Value é uma anotação que define que um parâmetro de um método é um valor.
     *  Que é passado para o método quando o Bean é criado.
     */

    @StepScope
    @Bean
    FlatFileItemReader<TransacaoCNAB> reader(@Value("#{jobParameters['cnabFile']}") Resource resource) {
        /*
         * FlatFileItemReaderBuilder é uma classe que ajuda a construir um FlatFileItemReader.
         * O método name() define o nome do FlatFileItemReader.
         * O método resource() define o recurso do FlatFileItemReader, que é um arquivo.
         * O método fixedLength() define que o arquivo é de tamanho fixo.
         * O método columns() define as colunas do arquivo, que são definidas por Range.
         * O método names() define os nomes das colunas.
         * O método targetType() define o tipo do item que o FlatFileItemReader lê.
         */
         return new FlatFileItemReaderBuilder<TransacaoCNAB>()
            .name("reader")
            .resource(resource)
            .fixedLength()
            .columns(
                new Range(1, 1), new Range(2, 9),
                new Range(10, 19), new Range(20, 30),
                new Range(31, 42), new Range(43, 48), 
                new Range(49, 62), new Range(63, 80)
            )
            .names("tipo", "data", "valor", "cpf", "cartao", "hora", "donoLoja", "nomeLoja")
            .targetType(TransacaoCNAB.class)
            .build();
    }

    /*
     * O método processor() retorna um ItemProcessor que processa um item TransacaoCNAB e retorna um item Transacao.
     */
    @Bean
    ItemProcessor<TransacaoCNAB, Transacao> processor() {
        return item -> {
            var transacao = new Transacao(
                null,
                item.tipo(),
                null,
                item.valor().divide(BigDecimal.valueOf(100)),
                item.cpf(),
                item.cartao(),
                null,
                item.donoLoja().trim(),
                item.nomeLoja().trim()
            )
            .withData(item.data())
            .withHora(item.hora());

            return transacao;
        };
    }
    /*
     * O método writer() retorna um JdbcBatchItemWriter que escreve um item Transacao no banco de dados.
     * O método dataSource() retorna um DataSource. Que é uma interface que define um método getConnection() que retorna uma conexão com o banco de dados.
     * O método sql() define a query que o JdbcBatchItemWriter executa. Neste caso, é uma query de inserção.
     * O método beanMapped() define que o JdbcBatchItemWriter mapeia um bean para a query.
     * O método build() constrói o JdbcBatchItemWriter.
     */

    @Bean
    JdbcBatchItemWriter<Transacao> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transacao>()
            .dataSource(dataSource)
            .sql(
                """
                        INSERT INTO transacao(tipo, data, valor, cpf, cartao, hora, dono_loja, nome_loja)
                        VALUES (:tipo, :data, :valor, :cpf, :cartao, :hora, :donoLoja, :nomeLoja)
                        """
            )
            .beanMapped()
            .build();
    }    
    /*
     *  Configuração do JobLauncher.
     * O método jobLauncherAsync() retorna um JobLauncher que executa um Job de forma assíncrona.
     * O método setJobRepository() define o JobRepository do JobLauncher.
     * O método setTaskExecutor() define o TaskExecutor do JobLauncher.
     */

    @Bean
    JobLauncher jobLauncherAsync(JobRepository jobRepository) throws Exception {
        var jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}
