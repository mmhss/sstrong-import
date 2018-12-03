package org.gw.standstrong.activity;

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
public class ActivityBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ProjectRepository projectRepository;

    @Bean(name="activityItemReader")
    @StepScope
    public FlatFileItemReader<Activity> activityItemReader(@Value("#{jobParameters['FILE']}")String file) {
        FlatFileItemReader<Activity> reader = new FlatFileItemReader<Activity>();
        reader.setResource(new FileSystemResource(projectRepository.findAll().get(0).getInboundFolder()+ File.separatorChar + file));
        reader.setName("activityItemReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] { "captureDate", "androidId", "dataType", "activityType", "confidence" });
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Activity>() {
                    {
                        setTargetType(Activity.class);
                    }
                });
            }
        });

        return reader;

    }

    @Bean(name="activityItemWriter")
    @StepScope
    public JdbcBatchItemWriter<Activity> activityItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Activity>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO activity (capture_date, android_id, activity_type, confidence, mother_id) VALUES (:captureDate, :androidId, :activityType, :confidence, :motherId)")
                .dataSource(dataSource)
                .build();
    }

    @Bean(name="importActivityJob")
    public Job importActivityJob(ActivityJobCompletionNotificationListener listener, Step stepWriteActivities) {
        return jobBuilderFactory.get("importActivityJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepWriteActivities)
                .end()
                .build();
    }

    @Bean(name="stepWriteActivities")
    public Step stepWriteActivities(JdbcBatchItemWriter<Activity> activityItemWriter) {
        return stepBuilderFactory.get("stepWriteActivities")
                .<Activity, Activity> chunk(100)
                .reader(activityItemReader(null))
                .processor(activityItemProcessor())
                .writer(activityItemWriter)
                .build();
    }


    @Bean("activityItemProcessor")
    public ActivityItemProcessor activityItemProcessor() {
        return new ActivityItemProcessor();
    }
}
