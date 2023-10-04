package net.codejava;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.JsonPath;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UserApiController.class)
public class UserApiControllerTests {
	private static final String END_POINT_PATH = "/users";

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private UserService service;

	@Test
	public void testAddShouldReturn400BadRequest() throws  Exception{
		User newUser = new User().email("").firstName("").lastName("");

		String requestBody = objectMapper.writeValueAsString(newUser);

		mockMvc.perform(post(END_POINT_PATH).contentType("application/json")
				.content(requestBody))
				.andExpect(status().isBadRequest())
				.andDo(print());
	}

	@Test
	public void testAddShouldReturn201Created() throws  Exception{
		String email = "musa@gmail.com";
		User newUser = new User().email(email)
				.firstName("musa")
				.lastName("usman")
				.password("password");
		Mockito.when(service.add(newUser)).thenReturn(newUser.id(1L));

		String requestBody = objectMapper.writeValueAsString(newUser);

		 mockMvc.perform(post(END_POINT_PATH).contentType("application/json")
				.content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", is("/users/1")))
				.andExpect(jsonPath("$.email", is(email) ))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andDo(print());
	}

	@Test
	public void testGetShouldReturn404NotFound() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		Mockito.when(service.get(userId)).thenThrow(UserNotFoundException.class);

//		mockMvc.perform(get(requestURI).contentType("application/json"))
//				.andExpect(status().isNotFound())
//				.andDo(print());

		mockMvc.perform(get(requestURI))
				.andExpect(status().isNotFound())
				.andDo(print());
	}


	@Test
	public void testGetShouldReturn200Ok() throws  Exception{
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		String email = "musa@gmail.com";
		User user = new User().email(email)
				.firstName("musa")
				.lastName("usman")
				.password("password");

		Mockito.when(service.get(userId)).thenReturn(user.id(1L));

		mockMvc.perform(get(requestURI))
				//.content())
				.andExpect(content().contentType("application/json"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is(email) ))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andDo(print());
	}

	@Test
	public void testListShouldReturn204NoContent() throws Exception {

		Mockito.when(service.list()).thenReturn(new ArrayList<>());

		mockMvc.perform(get(END_POINT_PATH))
				.andExpect(status().isNoContent())
				.andDo(print());
	}

	@Test
	public void testListShouldReturn200Ok() throws Exception {

		User user1 = new User().email("musausmanjb@gmail.com")
				.firstName("musaUsman")
				.lastName("usman")
				.password("password")
				.id(1L);

		User user2 = new User().email("musausmanjb222@gmail.com")
				.firstName("musa2")
				.lastName("usman2")
				.password("password12")
				.id(2L);

		List<User> userList = List.of(user1, user2);
		Mockito.when(service.list()).thenReturn(userList);

		String email;
		mockMvc.perform(get(END_POINT_PATH))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$[0].email", is("musausmanjb@gmail.com")))
				.andExpect(jsonPath("$[0].firstName", is("musaUsman")))
				.andExpect(jsonPath("$[1].email", is("musausmanjb222@gmail.com")))
				.andDo(print());

	}

	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		User user = new User().email("musausmanjb@gmail.com").firstName("musaUsman").lastName("usman").password("password").id(userId);


		Mockito.when(service.update(user)).thenThrow(UserNotFoundException.class);

		String requestBody = objectMapper.writeValueAsString(user);

		mockMvc.perform(put(requestURI).contentType("application/json")
						.content(requestBody))
						.andExpect(status().isNotFound())
						.andDo(print());

	}


	@Test
	public void testUpdateShouldReturn400BadRequest() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		User user = new User().email("musausmanjb@gmail.com").firstName("musaUsman").lastName("usman").password("password").id(userId);

		//Mockito.when(service.update(user)).thenThrow(UserNotFoundException.class);

		String requestBody = objectMapper.writeValueAsString(user);

		mockMvc.perform(put(requestURI).contentType("application/json")
						.content(requestBody))

				.andExpect(status().isBadRequest())
				.andDo(print());

	}

	@Test
	public void testUpdateShouldReturn200Ok() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		String email = "musausmanjb@gmail.com";
		User user = new User().email("musausmanjb@gmail.com").firstName("musaUsman").lastName("usman").password("password").id(userId);

		//Mockito.when(service.update(user)).thenThrow(UserNotFoundException.class);
		String requestBody = objectMapper.writeValueAsString(user);
		Mockito.when(service.update(user)).thenReturn(user);
		mockMvc.perform(put(requestURI).contentType("application/json")
						.content(requestBody))
				.andExpect(content().contentType("application/json"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is(email) ))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andDo(print());

	}

	@Test
	public void testDeleteShouldReturn404NotFou() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		Mockito.doThrow(UserNotFoundException.class).when(service).delete(userId);

		mockMvc.perform(delete(requestURI))
				.andExpect(status().isNotFound())
				.andDo(print());
	}

	@Test
	public void testDeleteShouldReturn204NoContent() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;

		Mockito.doNothing().when(service).delete(userId);

		mockMvc.perform(delete(requestURI))
				.andExpect(status().isNoContent())
				.andDo(print());
	}



}

