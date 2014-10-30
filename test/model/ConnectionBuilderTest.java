package model;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.Test;

public class ConnectionBuilderTest {

	@Test
	public void testGetConnection() {
		Connection connection = ConnectionBuilder.getConnection();
		assertNotNull(connection);
	}

}
