package org.gw.standstrong.calllog;

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
public class CallLogBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ProjectRepository projectRepository;

    @Bean(name="callLogItemReader")
    @StepScope
    public FlatFileItemReader<CallLog> callLogItemReader(@Value("#{jobParameters['FILE']}")String file) {
        FlatFileItemReader<CallLog> reader = new FlatFileItemReader<CallLog>();
        reader.setResource(new FileSystemResource(projectRepository.findAll().get(0).getInboundFolder()+ File.separatorChar + file));
        reader.setName("callLogItemReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] { "captureDate", "androidId", "dataType","callId", "phoneNumber", "direction", "actionDate", "duration" });
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<CallLog>() {
                    {
                        setTargetType(CallLog.class);
                    }
                });
            }
        });

        return reader;

    }

    @Bean(name="callLogItemWriter")
    @StepScope
    public JdbcBatchItemWriter<CallLog> callLogItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CallLog>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO call_log (capture_date, android_id, call_id, phone_number, direction, action_date, duration, mother_id) VALUES (:captureDate, :androidId, :callId, :phoneNumber, :direction, :actionDate, :duration, :motherId)")
                .dataSource(dataSource)
                .build();
    }

    @Bean(name="importCallLogJob")
    public Job importCallLogJob(CallLogJobCompletionNotificationListener listener, Step stepWriteCallLogs) {
        return jobBuilderFactory.get("importCallLogJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepWriteCallLogs)
                .end()
                .build();
    }

    @Bean(name="stepWriteCallLogs")
    public Step stepWriteCallLogs(JdbcBatchItemWriter<CallLog> callLogItemWriter) {
        return stepBuilderFactory.get("stepWriteCallLogs")
                .<CallLog, CallLog> chunk(100)
                .reader(callLogItemReader(null))
                .processor(callLogItemProcessor())
                .writer(callLogItemWriter)
                .build();
    }


    @Bean("callLogItemProcessor")
    public CallLogItemProcessor callLogItemProcessor() {
        return new CallLogItemProcessor();
    }
}
