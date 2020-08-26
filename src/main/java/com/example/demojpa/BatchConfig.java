package com.example.demojpa;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {
	@Autowired
	JobBuilderFactory jobBuilderFactory;
	@Autowired
	StepBuilderFactory stepBuilderFactory;
	@Autowired
	JobCompletionNotificationListener jobCompletionNotificationListener;
	
	private static final String QUERY_FIND_ACTORS =
		"SELECT " +
			"actor_id, " +
			"first_name, " +
			"last_name, " +
			"last_updated " +
			"FROM actor " +
			"ORDER BY first_name ASC";
	
	@Bean
	public ItemReader<Actor> itemReader(DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<Actor>()
			.name("cursorItemReader")
			.dataSource(dataSource)
			.sql(QUERY_FIND_ACTORS)
			.rowMapper(new BeanPropertyRowMapper<>(Actor.class))
			.build();
	}
	
	@Bean
	public FlatFileItemWriter<Actor> writer() {
		//Create writer instance
		FlatFileItemWriter<Actor> writer = new FlatFileItemWriter<>();
		
		//Set output file location
		writer.setResource(new FileSystemResource("./output"));
		
		//All job repetitions should "append" to same output file
		writer.setAppendAllowed(true);
		
		//Name field values sequence based on object properties
		writer.setLineAggregator(new DelimitedLineAggregator<Actor>() {
			{
				setDelimiter(",");
				setFieldExtractor(new BeanWrapperFieldExtractor<Actor>() {
					{
						setNames(new String[] { "actor_id", "first_name", "last_name", "last_updated" });
					}
				});
			}
		});
		return writer;
	}
	@Bean
	public Job importActorJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importActorJob")
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step1)
			.end()
			.build();
	}
	@Bean
	public TaskExecutor taskExecutor(){
		return new SimpleAsyncTaskExecutor("spring_batch");
	}
	@Bean
	public Step step1(FlatFileItemWriter<Actor> writer, ItemReader<Actor> itemReader) {
		return stepBuilderFactory.get("step1")
			.<Actor, Actor> chunk(10)
			.reader(itemReader)
			.writer(writer)
			.taskExecutor(taskExecutor())
			.build();
	}
	
}
