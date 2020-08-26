package com.example.demojpa;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {
	@Autowired
	public ActorRepository actorRepository;
	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private Job job;
	@GetMapping("/all")
	public String getActors() {
		return actorRepository.findAll().toString();
	}
	@GetMapping(path="/add")
	public void addNewActor () throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		JobParameters params = new JobParametersBuilder()
			.addString("JobID", String.valueOf(System.currentTimeMillis()))
			.toJobParameters();
		jobLauncher.run(job, params);
	}
}
