/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.springdata.rest.projections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;

import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * Integration tests to bootstrap the application.
 * 
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration("src/main/resources")
public class ApplicationIntegrationTests {

	@Autowired OrderRepository repository;
	
	@Autowired ConfigurableWebApplicationContext webappContext;

	private MockMvc mockMvc;


	@Test
	public void initializesRepositoryWithSampleData() {
		Iterable<Order> result = repository.findAll();
		assertThat(result, is(iterableWithSize(1)));
	}

	@Test
	public void queryALPS() throws Exception {
		doGet("/alps").andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	/**
	 * This is the default repository endpoint.
	 * @throws Exception
	 */
	@Test
	public void queryRepo() throws Exception {
		doGet("/orders").andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	/**
	 * This test will demonstrate that a @RepositoryRestController is able to
	 * serialize lazy-loaded projection data correctly.
	 * @throws Exception
	 */
	@Test
	public void queryRRC() throws Exception {
		doGet("/orders/rrc/collect").andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	/**
	 * This test will demonstrate that a @BasePathAwareController IS NOT able to
	 * serialize lazy-loaded projection data correctly. Instead it will fail with
	 * 2015-08-17 08:28:32.793  WARN 11452 --- [           main] .w.s.m.s.DefaultHandlerExceptionResolver : Failed to write HTTP message: org.springframework.http.converter.HttpMessageNotWritableException: Could not write content: failed to lazily initialize a collection of role: example.springdata.rest.projections.Order.items, could not initialize proxy - no Session (through reference chain: org.springframework.hateoas.Resources["_embedded"]->java.util.UnmodifiableMap["orders"]->java.util.ArrayList[0]->org.springframework.data.rest.webmvc.json.ProjectionResource["content"]->$Proxy94["items"]); nested exception is com.fasterxml.jackson.databind.JsonMappingException: failed to lazily initialize a collection of role: example.springdata.rest.projections.Order.items, could not initialize proxy - no Session (through reference chain: org.springframework.hateoas.Resources["_embedded"]->java.util.UnmodifiableMap["orders"]->java.util.ArrayList[0]->org.springframework.data.rest.webmvc.json.ProjectionResource["content"]->$Proxy94["items"])
	 * 2015-08-17 08:28:32.793  WARN 11452 --- [           main] .w.s.m.s.DefaultHandlerExceptionResolver : Handler execution resulted in exception: Could not write content: failed to lazily initialize a collection of role: example.springdata.rest.projections.Order.items, could not initialize proxy - no Session (through reference chain: org.springframework.hateoas.Resources["_embedded"]->java.util.UnmodifiableMap["orders"]->java.util.ArrayList[0]->org.springframework.data.rest.webmvc.json.ProjectionResource["content"]->$Proxy94["items"]); nested exception is com.fasterxml.jackson.databind.JsonMappingException: failed to lazily initialize a collection of role: example.springdata.rest.projections.Order.items, could not initialize proxy - no Session (through reference chain: org.springframework.hateoas.Resources["_embedded"]->java.util.UnmodifiableMap["orders"]->java.util.ArrayList[0]->org.springframework.data.rest.webmvc.json.ProjectionResource["content"]->$Proxy94["items"])
	 * @throws Exception
	 */
	@Test
	public void queryBAC() throws Exception {
		doGet("/orders/bac/collect").andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}

	private ResultActions doGet(String url) throws Exception {
		return mockMvc.perform(MockMvcRequestBuilders.get(url)).andDo(MockMvcResultHandlers.print());
	}
	
	@Before
	public void initMvcTest() throws NamingException, ServletException {
		DefaultMockMvcBuilder setup = MockMvcBuilders.webAppContextSetup(webappContext);
		mockMvc = setup.build();
	}

}
