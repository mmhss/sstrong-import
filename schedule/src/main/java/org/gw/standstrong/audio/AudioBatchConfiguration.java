package org.gw.standstrong.audio;

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
public class AudioBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ProjectRepository projectRepository;

    @Bean(name = "audioItemReader")
    @StepScope
    public FlatFileItemReader<Audio> audioItemReader(@Value("#{jobParameters['FILE']}") String file) {
        FlatFileItemReader<Audio> reader = new FlatFileItemReader<Audio>();
        reader.setResource(new FileSystemResource(projectRepository.findAll().get(0).getInboundFolder() + File.separatorChar + file));
        reader.setName("audioItemReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[]{"captureDate", "audioType", "accuracy", "filename"});
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Audio>() {
                    {
                        setTargetType(Audio.class);
                    }
                });
            }
        });

        return reader;

    }

    @Bean(name = "audioItemWriter")
    @StepScope
    public JdbcBatchItemWriter<Audio> audioItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Audio>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO audio (capture_date, audio_type, accuracy, filename, mother_id) VALUES (:captureDate, :audioType, :accuracy, :filename, :motherId)")
                .dataSource(dataSource)
                .build();
    }

    @Bean(name = "importAudioJob")
    public Job importAudioJob(AudioJobCompletionNotificationListener listener, Step stepWriteAudios) {
        return jobBuilderFactory.get("importAudioJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepWriteAudios)
                .end()
                .build();
    }

    @Bean(name = "stepWriteAudios")
    public Step stepWriteAudios(JdbcBatchItemWriter<Audio> audioItemWriter) {
        return stepBuilderFactory.get("stepWriteAudios")
                .<Audio, Audio>chunk(100)
                .reader(audioItemReader(null))
                .processor(audioItemProcessor())
                .writer(audioItemWriter)
                .build();
    }


    @Bean("audioItemProcessor")
    public AudioItemProcessor audioItemProcessor() {
        return new AudioItemProcessor();
    }

}
