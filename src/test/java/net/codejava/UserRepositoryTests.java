package net.codejava;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired private UserRepository repo;
	
	@Test
	public void testAddSuccess() {
		User newUser = new User().email("musa@gmail.com")
								 .firstName("usman")
								 .lastName("abba")
								 .password("musa123");
		
		User persistedUser = repo.save(newUser);
		
		assertThat(persistedUser).isNotNull();
		assertThat(persistedUser.getId()).isGreaterThan(0);
	}
}
