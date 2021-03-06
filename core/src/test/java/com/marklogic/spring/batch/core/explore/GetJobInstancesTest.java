package com.marklogic.spring.batch.core.explore;

import java.util.List;

import com.marklogic.junit.spring.AbstractSpringTest;
import org.junit.Test;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.marklogic.spring.batch.JobParametersTestUtils;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
		com.marklogic.client.spring.BasicConfig.class,
		com.marklogic.spring.batch.configuration.MarkLogicBatchConfiguration.class
})
public class GetJobInstancesTest extends AbstractSpringTest {
	
	@Autowired
	JobExplorer jobExplorer;
	
	@Autowired
	JobRepository jobRepository;
	
	private final String JOB_NAME = "testJob";
	private final String JOB_NAME_2 = JOB_NAME + "2";
	private final String JOB_NAME_3 = JOB_NAME + "3";
	
	@Test
	public void retrieveJobInstanceByIdTest() {
		JobInstance expectedJobInstance = jobRepository.createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
		JobInstance actualJobInstance = jobExplorer.getJobInstance(expectedJobInstance.getId());
		assertTrue(expectedJobInstance.equals(actualJobInstance));
	}
	
	@Test
	public void getJobInstanceCountTest() throws NoSuchJobException {
		createJobInstances();
		assertEquals(3, jobExplorer.getJobInstanceCount(JOB_NAME));
	}
	
	@Test(expected=NoSuchJobException.class)
	public void getJobInstanceCountNoJobException() throws NoSuchJobException {
		jobExplorer.getJobInstanceCount("NoJobs");
	}
	
	@Test
	public void getJobInstancesTest() {
		createJobInstances();
		List<JobInstance> jobInstances = jobExplorer.getJobInstances(JOB_NAME, 1, 2);
		assertEquals(2, jobInstances.size());		
	}
	
	@Test
	public void findJobInstancesTest() {
		createJobInstances();
		List<JobInstance> jobInstances = jobExplorer.findJobInstancesByJobName(JOB_NAME, 1, 2);
		assertEquals(2, jobInstances.size());
	}
	
	@Test
	public void getJobNamesTest() {
		createJobInstances();
		List<String> jobNames = jobExplorer.getJobNames();
		assertEquals(3, jobNames.size());
		assertTrue(jobNames.get(0).equals(JOB_NAME));
		assertTrue(jobNames.get(1).equals(JOB_NAME_2));
		assertTrue(jobNames.get(2).equals(JOB_NAME_3));
	}
	
	private void createJobInstances() {
		jobRepository.createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
		jobRepository.createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
		jobRepository.createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
		jobRepository.createJobInstance(JOB_NAME_2, JobParametersTestUtils.getJobParameters());
		jobRepository.createJobInstance(JOB_NAME_3, JobParametersTestUtils.getJobParameters());
	}

}
