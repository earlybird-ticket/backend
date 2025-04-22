package com.earlybird.ticket.batch.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "batch-job")
@RequiredArgsConstructor
public class BatchJobEndpoint {

    private final JobLauncher jobLauncher;
    private final Job collectOutboxJob;

    @WriteOperation
    public String triggerOutboxCollectJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("jobId", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();

            jobLauncher.run(collectOutboxJob, jobParameters);
            return "Batch Completed";
        } catch (JobExecutionException e) {
            return "Error : " + e.getMessage();
        }
    }
}
