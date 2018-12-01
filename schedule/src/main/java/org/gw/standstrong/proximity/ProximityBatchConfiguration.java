package org.gw.standstrong.proximity;

import org.gw.standstrong.project.ProjectRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;


import javax.sql.DataSource;
import java.io.File;

@Configuration
public class ProximityBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ProjectRepository projectRepository;

    @Bean(name="proximityItemReader")
    @StepScope
    public FlatFileItemReader<Proximity> proximityItemReader(@Value("#{jobParameters['FILE']}")String file) {

        FlatFileItemReader<Proximity> reader = new FlatFileItemReader<Proximity>();
        reader.setResource(new FileSystemResource(projectRepository.findAll().get(0).getInboundFolder()+ File.separatorChar + file));
        reader.setName("proximityItemReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper() {
            {
                //3 columns in each row
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] { "captureDate", "androidId", "dataType", "event", "value"});
                    }
                });
                //Set values in Employee class
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Proximity>() {
                    {
                        setTargetType(Proximity.class);
                    }
                });
            }
        });

        return reader;

    }

    @Bean(name="proximityItemWriter")
    @StepScope
    public JdbcBatchItemWriter<Proximity> proximityItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Proximity>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO proximity (capture_Date, android_id, event, value, mother_id) VALUES (:captureDate, :androidId , :event, :value, :motherId)")
            .dataSource(dataSource)
            .build();
    }

    @Bean(name="importProximityJob")
    public Job importProximityJob(ProximityJobCompletionNotificationListener listener, Step stepWriteProximities) {
        return jobBuilderFactory.get("importProximityJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(stepWriteProximities)
            .end()
            .build();
    }

    @Bean(name="stepWriteProximities")
    public Step stepWriteProximities(JdbcBatchItemWriter<Proximity> proximityItemWriter) {
        return stepBuilderFactory.get("stepWriteProximities")
            .<Proximity, Proximity> chunk(100)
            .reader(proximityItemReader(null))
            .processor(proximityItemProcessor())
            .writer(proximityItemWriter)
            .build();
    }

    @Bean(name="proximityItemProcessor")
    public ProximityItemProcessor proximityItemProcessor() {
        return new ProximityItemProcessor();
    }


}
