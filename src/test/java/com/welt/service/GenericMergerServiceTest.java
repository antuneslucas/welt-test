package com.welt.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.welt.controller.GenericMergerController;
import com.welt.service.impl.GenericMergerServiceImpl;

public class GenericMergerServiceTest {

    private final String userUrl1 = "/users/1";
    private final String userUrl2 = "/users/2";
    private final String postsUrl = "/posts/1";

    //this executorService forces execution before future's are returned
    private final ListeningExecutorService executorService = MoreExecutors.newDirectExecutorService();
    @Mock private RestTemplate restTemplate;

    private MockMvc mockMvc;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        GenericMergerController genericMergerController = new GenericMergerController(new GenericMergerServiceImpl(executorService, restTemplate));

        mockMvc = MockMvcBuilders.standaloneSetup(genericMergerController).build();

        when(restTemplate.getForEntity(userUrl1, String.class)).thenReturn(ResponseEntity.ok("{\"userId\": 1}"));
        when(restTemplate.getForEntity(userUrl2, String.class)).thenReturn(ResponseEntity.ok("{\"userId\": 2}"));
        when(restTemplate.getForEntity(postsUrl, String.class)).thenReturn(ResponseEntity.ok("{\"userId\": 3, \"id\": 23, \"title\": \"lorem ipsum\"}"));
    }

    @Test
    public void testOneRequest() throws Exception {

        MvcResult result = getMvcResult(userUrl1);

        //{"responses":[{"content":{"userId": 1}}]}
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responses", hasSize(1)))
                .andExpect(jsonPath("$.responses[0].content", not(nullValue())))
                .andExpect(jsonPath("$.responses[0].content.userId", is(1)));
    }

    @Test
    public void testTwoMergedRequests() throws Exception {

        MvcResult result = getMvcResult(userUrl1, userUrl2);

        //{"responses":[{"content":{"userId": 1}}, {"content":{"userId": 2}}]}
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responses", hasSize(2)))
                .andExpect(jsonPath("$.responses[0].content", not(nullValue())))
                .andExpect(jsonPath("$.responses[0].content.userId", is(1)))

                .andExpect(jsonPath("$.responses[1].content", not(nullValue())))
                .andExpect(jsonPath("$.responses[1].content.userId", is(2)));
    }

    @Test
    public void testThreeMergedRequests() throws Exception {

        MvcResult result = getMvcResult(userUrl1, postsUrl, userUrl2);

        //{"responses":[{"content":{"userId": 1}}, {"content":{"userId": 3, "id": 23, "title": "lorem ipsum"}}, {"content":{"userId": 2}}]}
        mockMvc.perform(asyncDispatch(result))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.responses", hasSize(3)))
        .andExpect(jsonPath("$.responses[0].content", not(nullValue())))
        .andExpect(jsonPath("$.responses[0].content.userId", is(1)))

        .andExpect(jsonPath("$.responses[1].content", not(nullValue())))
        .andExpect(jsonPath("$.responses[1].content.userId", is(3)))
        .andExpect(jsonPath("$.responses[1].content.id", is(23)))
        .andExpect(jsonPath("$.responses[1].content.title", is("lorem ipsum")))

        .andExpect(jsonPath("$.responses[2].content", not(nullValue())))
        .andExpect(jsonPath("$.responses[2].content.userId", is(2)));
    }

    private MvcResult getMvcResult(String... urls) throws Exception {
        String urlsContent = Arrays.stream(urls)
                .map(s -> "\"" + s + "\"")  // add quotation marks at each url
                .collect(Collectors.toList())
                .toString();

        return mockMvc
            .perform(post("/api/generic-merger").content(urlsContent).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andReturn();
    }

}
