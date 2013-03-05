package com.plexiti.activiti.test.fluent;

import com.plexiti.activiti.showcase.jobannouncement.model.JobAnnouncement;
import com.plexiti.activiti.showcase.jobannouncement.service.JobAnnouncementService;
import com.plexiti.activiti.test.fluent.engine.FluentBpmnProcessInstanceImpl;
import com.plexiti.helper.Entities;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import static com.plexiti.activiti.showcase.jobannouncement.process.ProcessConstants.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import static com.plexiti.activiti.test.fluent.FluentBpmnTests.*;

/**
 *
 * @author Martin Schimak <martin.schimak@plexiti.com>
 * @author Rafael Cordones <rafael.cordones@plexiti.com>
 *
 */
public class JobAnnouncementPublicationTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();
    @Rule
    public FluentBpmnTestRule bpmnFluentTestRule = new FluentBpmnTestRule(this);

	@Mock
    public JobAnnouncementService jobAnnouncementService;
	@Mock
    public JobAnnouncement jobAnnouncement;

	@Test
	@Deployment(resources = { JOBANNOUNCEMENT_PUBLICATION_PROCESS_RESOURCE })
	public void testPublishOnlyOnCompanyWebsite() {
		
		when(jobAnnouncement.getId()).thenReturn(1L);
		
		start(new FluentBpmnProcessInstanceImpl(JOBANNOUNCEMENT_PUBLICATION_PROCESS)
			.withVariable(Entities.idVariableName(JobAnnouncement.class), jobAnnouncement.getId())
			.withVariable("twitter", false)
			.withVariable("facebook", false)
		);
		
		verify(jobAnnouncementService).postToWebsite(jobAnnouncement.getId());
		verify(jobAnnouncementService, never()).postToTwitter(any(Long.class));
		verify(jobAnnouncementService, never()).postToFacebook(any(Long.class));

		assertThat(processExecution()).isFinished();

		verifyNoMoreInteractions(jobAnnouncementService);

	}

	@Test
	@Deployment(resources = { JOBANNOUNCEMENT_PUBLICATION_PROCESS_RESOURCE })
	public void testPublishAlsoOnTwitter() {
		
		start(new FluentBpmnProcessInstanceImpl(JOBANNOUNCEMENT_PUBLICATION_PROCESS)
			.withVariable(Entities.idVariableName(JobAnnouncement.class), jobAnnouncement.getId())
			.withVariable("twitter", true)
			.withVariable("facebook", false)
		);

		verify(jobAnnouncementService).postToWebsite(jobAnnouncement.getId());
		verify(jobAnnouncementService).postToTwitter(jobAnnouncement.getId());
		verify(jobAnnouncementService, never()).postToFacebook(any(Long.class));

		assertThat(processExecution()).isFinished();

		verifyNoMoreInteractions(jobAnnouncementService);
		
	}

	@Test
	@Deployment(resources = { JOBANNOUNCEMENT_PUBLICATION_PROCESS_RESOURCE })
	public void testPublishAlsoOnFacebook() {
		
		start(new FluentBpmnProcessInstanceImpl(JOBANNOUNCEMENT_PUBLICATION_PROCESS)
			.withVariable(Entities.idVariableName(JobAnnouncement.class), jobAnnouncement.getId())
			.withVariable("twitter", false)
			.withVariable("facebook", true)
		);

		verify(jobAnnouncementService).postToWebsite(jobAnnouncement.getId());
		verify(jobAnnouncementService, never()).postToTwitter(any(Long.class));
		verify(jobAnnouncementService).postToFacebook(jobAnnouncement.getId());

        assertThat(processExecution()).isFinished();

		verifyNoMoreInteractions(jobAnnouncementService);
		
	}

	@Test
	@Deployment(resources = { JOBANNOUNCEMENT_PUBLICATION_PROCESS_RESOURCE })
	public void testPublishAlsoOnFacebookAndTwitter() {
		
		start(new FluentBpmnProcessInstanceImpl(JOBANNOUNCEMENT_PUBLICATION_PROCESS)
			.withVariable(Entities.idVariableName(JobAnnouncement.class), jobAnnouncement.getId())
			.withVariable("facebook", true)
			.withVariable("twitter", true)
		);

		verify(jobAnnouncementService).postToWebsite(jobAnnouncement.getId());
		verify(jobAnnouncementService).postToTwitter(jobAnnouncement.getId());
		verify(jobAnnouncementService).postToFacebook(jobAnnouncement.getId());

		assertThat(processExecution()).isFinished();

		verifyNoMoreInteractions(jobAnnouncementService);
		
	}
}
