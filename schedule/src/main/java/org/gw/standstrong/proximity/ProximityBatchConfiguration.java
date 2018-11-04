package org.gw.standstrong.proximity;

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
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class ProximityBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public MultiResourceItemReader<Proximity> multiResourceItemReader()
    {
        Resource[] resources = new Resource[0];

        ClassPathResource a =new ClassPathResource("input/");
        ResourceLoader rl = new ResourceLoader() {
            @Override
            public Resource getResource(String location) {
                return a;
            }

            @Override
            public ClassLoader getClassLoader() {
                return null;
            }
        };
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(rl);
        try {
            resources = resolver.getResources("*.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }


        MultiResourceItemReader<Proximity> resourceItemReader = new MultiResourceItemReader<Proximity>();
        resourceItemReader.setResources(resources);
        resourceItemReader.setDelegate(reader());
        return resourceItemReader;
    }

    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<Proximity> reader() {
        FlatFileItemReader<Proximity> reader = new FlatFileItemReader<Proximity>();
        reader.setName("proximityItemReader");
        reader.setLineMapper(new DefaultLineMapper() {
            {
                //3 columns in each row
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] { "rssi", "recordedDateTime", "motherIdentificationNumber" });
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
    public Job importProximityJob(ProximityJobCompletionNotificationListener listener, Step step1) {
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
            .reader(multiResourceItemReader())
            .processor(processor())
            .writer(writer)
            .build();
    }

}
