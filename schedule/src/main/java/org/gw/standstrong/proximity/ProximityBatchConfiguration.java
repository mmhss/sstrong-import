package org.gw.standstrong.proximity;

import org.gw.standstrong.JobCompletionNotificationListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class ProximityBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<Proximity> reader() {
        return new FlatFileItemReaderBuilder<Proximity>()
            .name("proximityItemReader")
            .resource(new ClassPathResource("sample-data1.csv"))
            .delimited()
            .names(new String[]{"rssi", "recordedDateTime", "motherIdentificationNumber" })
            .fieldSetMapper(new BeanWrapperFieldSetMapper<Proximity>() {{
                setTargetType(Proximity.class);
            }})
            .build();
    }

    @Bean
    public ProximityItemProcesser processor() {
        return new ProximityItemProcesser();
    }

    @Bean
    public JdbcBatchItemWriter<Proximity> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Proximity>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO proximity (rssi, recorded_date_time, mother_id) VALUES (:rssi, :recordedDateTime, :motherId)")
            .dataSource(dataSource)
            .build();
    }

    @Bean
    public Job importProximityJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importProximityJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Proximity> writer) {
        return stepBuilderFactory.get("step1")
            .<Proximity, Proximity> chunk(10)
            .reader(reader())
            .processor(processor())
            .writer(writer)
            .build();
    }

}
