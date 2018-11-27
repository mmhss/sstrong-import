package org.gw.standstrong.gps;

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
public class GpsBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ProjectRepository projectRepository;

    @Bean(name="gpsItemReader")
    @StepScope
    public FlatFileItemReader<Gps> gpsItemReader(@Value("#{jobParameters['FILE']}")String file) {
        FlatFileItemReader<Gps> reader = new FlatFileItemReader<Gps>();
        reader.setResource(new FileSystemResource(projectRepository.findAll().get(0).getInboundFolder()+ File.separatorChar + file));
        reader.setName("gpsItemReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] { "captureDate", "androidId", "dataType", "latitude", "longitude", "accuracy", "altitude" });
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Gps>() {
                    {
                        setTargetType(Gps.class);
                    }
                });
            }
        });

        return reader;

    }

    @Bean(name="gpsItemWriter")
    @StepScope
    public JdbcBatchItemWriter<Gps> gpsItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Gps>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO gps (capture_date, android_id, latitude, longitude, accuracy, altitude, mother_id) VALUES (:captureDate, :androidId, :latitude, :longitude, :accuracy, :altitude, :motherId)")
                .dataSource(dataSource)
                .build();
    }

    @Bean(name="importGpsJob")
    public Job importGpsJob(GpsJobCompletionNotificationListener listener, Step stepWriteGpss) {
        return jobBuilderFactory.get("importGpsJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepWriteGpss)
                .end()
                .build();
    }

    @Bean(name="stepWriteGpss")
    public Step stepWriteGpss(JdbcBatchItemWriter<Gps> gpsItemWriter) {
        return stepBuilderFactory.get("stepWriteGpss")
                .<Gps, Gps> chunk(100)
                .reader(gpsItemReader(null))
                .processor(gpsItemProcessor())
                .writer(gpsItemWriter)
                .build();
    }


    @Bean("gpsItemProcessor")
    public GpsItemProcessor gpsItemProcessor() {
        return new GpsItemProcessor();
    }
}
