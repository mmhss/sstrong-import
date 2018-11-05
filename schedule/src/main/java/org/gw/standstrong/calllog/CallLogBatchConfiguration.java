package org.gw.standstrong.calllog;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class CallLogBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean("callLogMultiResourceItemReader")
    public MultiResourceItemReader<CallLog> callLogMultiResourceItemReader()
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
            resources = resolver.getResources("*.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }


        MultiResourceItemReader<CallLog> resourceItemReader = new MultiResourceItemReader<CallLog>();
        resourceItemReader.setResources(resources);
        resourceItemReader.setDelegate(callLogItemReader());
        return resourceItemReader;
    }

    // tag::readerwriterprocessor[]
    @Bean(name="callLogItemReader")
    public FlatFileItemReader<CallLog> callLogItemReader() {
        FlatFileItemReader<CallLog> reader = new FlatFileItemReader<CallLog>();
        reader.setName("callLogItemReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper() {
            {
                //3 columns in each row
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] { "captureDate", "androidId", "dataType","callId", "phoneNumber", "direction", "actionDate", "duration" });
                    }
                });
                //Set values in Employee class
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
    public JdbcBatchItemWriter<CallLog> callLogItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CallLog>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO call_log (capture_date, android_id, identification_number, phone_number, direction, action_date, duration, mother_id) VALUES (:captureDate, :androidId, :callId, :phoneNumber, :direction, :actionDate, :duration, :motherId)")
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
                .reader(callLogMultiResourceItemReader())
                .processor(callLogItemProcessor())
                .writer(callLogItemWriter)
                .build();
    }

    @Bean("callLogItemProcessor")
    public CallLogItemProcessor callLogItemProcessor() {
        return new CallLogItemProcessor();
    }


}
